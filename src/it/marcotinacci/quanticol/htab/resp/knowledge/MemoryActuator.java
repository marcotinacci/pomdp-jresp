package it.marcotinacci.quanticol.htab.resp.knowledge;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.cmg.resp.knowledge.AbstractActuator;
import org.cmg.resp.knowledge.Tuple;

// TODO javadoc

public abstract class MemoryActuator extends AbstractActuator {

	@Override
	public String toString() {
		return history.toString();
	}

	protected List<Tuple> history;
	
	public List<Tuple> getHistory() {
		return Collections.unmodifiableList(history);
	}

	public MemoryActuator(String name) {
		super(name);
		// FIXME concurrent modification exception with LinkedList implementation
		history = new Vector<Tuple>();
	} 
	
	public abstract void trackableSend(Tuple t);
	
	@Override
	public void send(Tuple t) {
		trackableSend(t);
		history.add(t);
	}
}
