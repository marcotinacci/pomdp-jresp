package it.marcotinacci.quanticol.htab;

import it.marcotinacci.quanticol.htab.resp.knowledge.MemoryActuator;
import it.marcotinacci.quanticol.htab.resp.knowledge.MemorySensor;
import it.marcotinacci.quanticol.htab.robot.PolicyGraphRobotTiger;
import it.marcotinacci.quanticol.htab.scenario.TSTiger;
import it.marcotinacci.quanticol.htab.scenario.TSTiger.Action;
import it.marcotinacci.quanticol.htab.scenario.TSTiger.Observation;
import it.marcotinacci.quanticol.htab.scenario.TSTiger.State;

import java.util.Random;

import org.cmg.resp.comp.Node;
import org.cmg.resp.knowledge.AbstractSensor;
import org.cmg.resp.knowledge.ActualTemplateField;
import org.cmg.resp.knowledge.FormalTemplateField;
import org.cmg.resp.knowledge.Template;
import org.cmg.resp.knowledge.Tuple;
import org.cmg.resp.knowledge.ts.TupleSpace;

// TODO parsing from annotation on local fields to space state
public class Tiger {
	
	public static void main(String[] args) throws InterruptedException {
		Tiger tiger = new Tiger();
		Node node = new Node("Robot Node", new TupleSpace());
		PolicyGraphRobotTiger pgr = new PolicyGraphRobotTiger(new TSTiger());		
		node.addAgent(pgr);
		MemoryActuator memAct = tiger.getDoorActuator(node.getName());
		node.addActuator(memAct);
		node.addSensor(tiger.getStepSensor());
		MemorySensor memSen = tiger.getState().getTigerSensor();
		node.addSensor(memSen);
		node.start();
		int numIter = 30;
		for(int i=0; i < numIter; i++){
			tiger.move();
			System.out.println(tiger);

		}
		System.out.println("Actuator history: " + memAct.toString());
		System.out.println("Sensor history: " + memSen.getHistory());		
	}

	protected Integer width;
	protected Integer height;
	protected Random random;
	protected AbstractSensor stepSensor;
	protected Action action;
	protected State state;
	protected Double reward;
	
	public Tiger() {
		random = new Random();
		reward = 0.;
		stepSensor = new AbstractSensor( "STEP_SENSOR" , new Template( 
				new ActualTemplateField( "STEP" ) , 
				new FormalTemplateField(Boolean.class) ) ) {};
		init();
	}
	
	protected void init(){
		// random state left/right
		state = new TSTiger.State(random.nextFloat() >= 0.5);
		System.out.println("INIT: " + state);
	}
	
	public State getState() {
		return state;
	}

	public Action getAction() {
		return action;
	}
	
	public AbstractSensor getStepSensor() {
		return stepSensor;
	}

	public MemoryActuator getDoorActuator(final String n){
		return new MemoryActuator("door") {
			
			@Override
			public void trackableSend(Tuple t) {
				action = t.getElementAt(Action.class, 1);
				stepSensor.setValue( new Tuple("STEP",false ));
			}
			
			@Override
			public Template getTemplate() {
				return new Template(
						new ActualTemplateField("ACTION"),
						new FormalTemplateField(Action.class));
			}
		};
	}
	
	public synchronized void move() throws InterruptedException{
		Action act = null;
		while(act == null){
			act = action;
			action = null;
			Thread.sleep(100);
		}
		
		// execute action
		state.getTigerSensor().setTrackableValue(new Tuple(
				"TIGER", 
				random.nextFloat() <= 0.15 ?
						// wrong sensing
						(state.isTigerLeft() ? Observation.HearRight : Observation.HearLeft) :
						// right sensing
						(state.isTigerLeft() ? Observation.HearLeft : Observation.HearRight)
				));
		if(act.equals(Action.Listen)){
			reward = reward-1;
		}else if(act.equals(Action.OpenLeft) == state.isTigerLeft()){
			// bad case
//			state.getTigerSensor().setTrackableValue(new Tuple(
//					"TIGER", Observation.Lose));
			reward = reward-100;
			state.setTigerLeft(random.nextFloat() >= 0.5);
		}else{
			// good case
//			state.getTigerSensor().setTrackableValue(new Tuple(
//					"TIGER", Observation.Win));
			reward = reward+10;
			state.setTigerLeft(random.nextFloat() >= 0.5);
		}
		stepSensor.setValue(new Tuple("STEP", true));
	}

	@Override
	public String toString() {
		return "Tiger [action=" + action + ", state=" + state + ", reward="
				+ reward + "]";
	}
}
