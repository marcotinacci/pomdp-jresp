package it.marcotinacci.quanticol.htab.robot;
import it.marcotinacci.quanticol.htab.scenario.TSArena5by5.Observation;

import org.cmg.resp.behaviour.Agent;
import org.cmg.resp.knowledge.AbstractSensor;
import org.cmg.resp.knowledge.ActualTemplateField;
import org.cmg.resp.knowledge.FormalTemplateField;
import org.cmg.resp.knowledge.Template;

/**
 * Abstract class containing all the common features of a generic robot.
 * A robot extends the agent class, it offers sensors to locate walls and
 * other robots, and actuators, wheels, to move in different directions.
 * @author Marco Tinacci
 */
public abstract class AbstractRobot extends Agent {
	
	// senses other robots
	protected AbstractSensor robotSensor;
	// senses walls or obstacles
	protected AbstractSensor wallSensor = new AbstractSensor("Wall sensor", 
			new Template(new ActualTemplateField("walls"), new FormalTemplateField(Observation.class) )) {};
	// TODO fuel sensor

	public AbstractRobot(String name) {
		super(name);
	}
}
