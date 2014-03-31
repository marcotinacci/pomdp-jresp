package it.marcotinacci.quanticol.htab;

import it.marcotinacci.quanticol.htab.resp.knowledge.MemoryActuator;
import it.marcotinacci.quanticol.htab.resp.knowledge.MemorySensor;
import it.marcotinacci.quanticol.htab.robot.PolicyGraphRobotArena;
import it.marcotinacci.quanticol.htab.scenario.TSArena5by5;
import it.marcotinacci.quanticol.htab.scenario.TSArena5by5.Action;
import it.marcotinacci.quanticol.htab.scenario.TSArena5by5.Observation;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.cmg.resp.comp.Node;
import org.cmg.resp.knowledge.AbstractSensor;
import org.cmg.resp.knowledge.ActualTemplateField;
import org.cmg.resp.knowledge.FormalTemplateField;
import org.cmg.resp.knowledge.Template;
import org.cmg.resp.knowledge.Tuple;
import org.cmg.resp.knowledge.ts.TupleSpace;

/**
 * 
 * @author Marco Tinacci
 *
 */
public class Arena{

	public static Location start = new Location(0,0); 
	
	public static void main(String[] args) throws InterruptedException {
		Arena arena = new Arena(TSArena5by5.WIDTH, TSArena5by5.HEIGHT);
		Node node = new Node("Robot Node", new TupleSpace());
		PolicyGraphRobotArena pgr = new PolicyGraphRobotArena(new TSArena5by5(0, TSArena5by5.HEIGHT-1, 0, TSArena5by5.WIDTH-1));		
		node.addAgent(pgr);
		MemoryActuator memAct = arena.getMovementActuator(node.getName());
		node.addActuator(memAct);
		node.addSensor(arena.getStepSensor());
		MemorySensor memSen = arena.getRobotLocations().get("Robot Node").getWallSensor();
		node.addSensor(memSen);
		node.start();
		int numIter = 10;
		for(int i=0; i < numIter; i++){
			System.out.println(arena);
			arena.move();
		}
		System.out.println("Actuator history: " + memAct.toString());
		System.out.println("Sensor history: " + memSen.getHistory());		
	}

	protected Integer width;
	protected Integer height;
	protected Hashtable<String, Location> robotLocations;
	protected Hashtable<String, Action> robotActions;
	protected Random random;
	private AbstractSensor stepSensor;
	
	public Arena(Integer w, Integer h) {
		width = w;
		height = h;
		random = new Random();
		robotLocations = new Hashtable<String, Location>();
		robotActions = new Hashtable<String, Action>();
		stepSensor = new AbstractSensor( "STEP_SENSOR" , new Template( 
				new ActualTemplateField( "STEP" ) , 
				new FormalTemplateField(Boolean.class) ) ) {};
		init();
	}
	
	protected void init(){
		// random position
		// FIXME hard coded reference 
//		robotLocations.put("Robot Node", new Location(random.nextInt(width), random.nextInt(height)));
		robotLocations.put("Robot Node", start);
		System.out.println("INIT: " + robotLocations.get("Robot Node"));
	}
	
	public Map<String, Location> getRobotLocations() {
		return Collections.unmodifiableMap(robotLocations);
	}

	public Map<String, Action> getRobotActions() {
		return Collections.unmodifiableMap(robotActions);
	}
	
	public MemoryActuator getMovementActuator(final String n){
		return new MemoryActuator("wheels") {
			
			@Override
			public void trackableSend(Tuple t) {
				robotActions.put(n, t.getElementAt(Action.class, 1));
				stepSensor.setValue( new Tuple("STEP",false ));
			}
			
			@Override
			public Template getTemplate() {
				return new Template(
						new ActualTemplateField("MOVE"),
						new FormalTemplateField(Action.class));
			}
		};
	}
	
	public AbstractSensor getStepSensor() {
		return stepSensor;
	}

	public synchronized void move() throws InterruptedException{
		for (String name : robotLocations.keySet()) {
			Location loc = robotLocations.get(name);
			Action act = null;
			while(act == null){
				act = robotActions.remove(name);
				Thread.sleep(100);
			}
			// update robot location
 			applyActionToLocation(loc,act,0,width-1,0,height-1,true);
			System.out.println(loc);
		}
//		setChanged();
//		notifyObservers();
		stepSensor.setValue( new Tuple("STEP",true ));
		
//		notifyAll();
	}

	
	public static void applyActionToLocation(Location loc, Action act, Integer minX, Integer maxX, Integer minY, Integer maxY, Boolean applyReset) {
		Random r = new Random();
		Location testLoc = new Location(loc);
		switch (act) {
			case North: if(testLoc.getY() > minY) testLoc.setXY(testLoc.getX(),testLoc.getY()-1); else testLoc.setXY(testLoc.getX(),testLoc.getY()); break;
			case East:	if(testLoc.getX() < maxX) testLoc.setXY(testLoc.getX()+1,testLoc.getY()); else testLoc.setXY(testLoc.getX(),testLoc.getY()); break;
			case South: if(testLoc.getY() < maxY) testLoc.setXY(testLoc.getX(),testLoc.getY()+1); else testLoc.setXY(testLoc.getX(),testLoc.getY()); break;
			case West:	if(testLoc.getX() > minX) testLoc.setXY(testLoc.getX()-1,testLoc.getY()); else testLoc.setXY(testLoc.getX(),testLoc.getY()); break; 
			case Stand: testLoc.setXY(testLoc.getX(),testLoc.getY()); break;
		}
		
		if(applyReset && testLoc.equals(TSArena5by5.targetLocation)){
			// random reset of position
			loc.setXY(r.nextInt(TSArena5by5.WIDTH), r.nextInt(TSArena5by5.HEIGHT));
		}else{
			switch (act) {
			case North: if(loc.getY() > minY) loc.setXY(loc.getX(),loc.getY()-1); else loc.setXY(loc.getX(),loc.getY()); break;
			case East:	if(loc.getX() < maxX) loc.setXY(loc.getX()+1,loc.getY()); else loc.setXY(loc.getX(),loc.getY()); break;
			case South: if(loc.getY() < maxY) loc.setXY(loc.getX(),loc.getY()+1); else loc.setXY(loc.getX(),loc.getY()); break;
			case West:	if(loc.getX() > minX) loc.setXY(loc.getX()-1,loc.getY()); else loc.setXY(loc.getX(),loc.getY()); break;
			// use setXY also for the stand action to trigger the wall sensor 
			case Stand: loc.setXY(loc.getX(),loc.getY()); break;
			}
		}
	}
	
	public static Observation getWallsFromLocation(Location loc, Integer minW, Integer maxW, Integer minH, Integer maxH){

		// FIXME minimize number of comparations
		if(loc.getX() == minW && loc.getY() == minH) return Observation.NorthWest;
		if(loc.getX() == maxW && loc.getY() == minH) return Observation.NorthEast;
		if(loc.getX() == minW && loc.getY() == maxH) return Observation.SouthWest;
		if(loc.getX() == maxW && loc.getY() == maxH) return Observation.SouthEast;
		if(loc.getX() == minW) return Observation.West;
		if(loc.getX() == maxW) return Observation.East;
		if(loc.getY() == minH) return Observation.North;
		if(loc.getY() == maxH) return Observation.South;
		return Observation.Free;
	}
	
	@Override
	public String toString() {
		StringBuffer strbuf = new StringBuffer();
		Iterator<Location> locs = robotLocations.values().iterator();
		Location loc = locs.next();
		Integer x = loc.getX();
		Integer y = loc.getY();
		for(int j = 0; j < height; j++){
			int i;
			for(i = 0; i < width; i++){
				if(i==x && j==y){
					strbuf.append("o "); // robot
					if(locs.hasNext()){
						loc = locs.next();
						x = loc.getX();
						y = loc.getY();
					}else{
						x = -1;
						y = -1;
					}
				}
				else
					strbuf.append(". "); // empty
			}
			if(i==x && j==y) break;
			strbuf.append("\n");
		}
		return strbuf.toString();
	}

}
