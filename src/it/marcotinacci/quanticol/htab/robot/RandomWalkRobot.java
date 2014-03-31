package it.marcotinacci.quanticol.htab.robot;

import it.marcotinacci.quanticol.htab.scenario.TSArena5by5.Action;

import java.util.Random;

import org.cmg.resp.knowledge.ActualTemplateField;
import org.cmg.resp.knowledge.Template;
import org.cmg.resp.knowledge.Tuple;
import org.cmg.resp.topology.Self;

public class RandomWalkRobot extends AbstractRobot{
	
	public static void main(String[] args) {
		System.out.println("nothing to do...");
	}
	
	public RandomWalkRobot() {
		super("Random Walk Robot");
	}

	@Override
	protected void doRun() throws Exception {
		System.out.println("doRun");
		Random random = new Random();
		while(true){
			Action act = Action.values()[random.nextInt(Action.values().length)];
			// PUT ("MOVE",name,act) @ self
			put(new Tuple("MOVE",act), Self.SELF);
			System.out.println("PUT < MOVE , "+ act.name() +" >");
			query( new Template( new ActualTemplateField( "STEP" ) , new ActualTemplateField(true) ), Self.SELF);
		}
	}

}
