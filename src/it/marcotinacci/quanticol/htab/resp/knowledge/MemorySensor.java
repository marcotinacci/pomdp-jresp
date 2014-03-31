package it.marcotinacci.quanticol.htab.resp.knowledge;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.cmg.resp.knowledge.AbstractSensor;
import org.cmg.resp.knowledge.Template;
import org.cmg.resp.knowledge.Tuple;

// TODO javadoc

/**
 * MemorySensor keeps track of every observation made through the 
 * setTrackableValue(Tuple) method.
 * @author Marco Tinacci
 *
 */
public class MemorySensor extends AbstractSensor {

	protected List<Tuple> history;
	
	public List<Tuple> getHistory() {
		return Collections.unmodifiableList(history);
	}

	public MemorySensor(String name, Template template) {
		super(name, template);
		history = new LinkedList<Tuple>();
	}
	
	public synchronized final void setTrackableValue(Tuple t){
		history.add(t);
		super.setValue(t);
	}
	
}
