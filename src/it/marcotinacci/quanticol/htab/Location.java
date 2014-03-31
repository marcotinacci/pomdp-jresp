package it.marcotinacci.quanticol.htab;

import it.marcotinacci.quanticol.htab.resp.knowledge.MemorySensor;
import it.marcotinacci.quanticol.htab.scenario.TSArena5by5;
import it.marcotinacci.quanticol.htab.scenario.TSArena5by5.Observation;

import java.security.InvalidParameterException;

import org.cmg.resp.knowledge.ActualTemplateField;
import org.cmg.resp.knowledge.FormalTemplateField;
import org.cmg.resp.knowledge.Template;
import org.cmg.resp.knowledge.Tuple;

public class Location{
	protected Integer x;
	protected Integer y;
	
	protected MemorySensor wallSensor;
	
	public Location(Integer x, Integer y) {
		// FIXME name may crash with more robots, use string name as parameter
		wallSensor = new MemorySensor("walls proximity", new Template(
				new ActualTemplateField("WALLS"),
				new FormalTemplateField(Observation.class)));
		setXY(x,y);
	}
	
	/**
	 * Copy constructor
	 * @param loc
	 */
	public Location(Location loc) {
		this(loc.getX(),loc.getY());
	}
	
	public Integer getX() {
		return x;
	}

	public Integer getY() {
		return y;
	}

	public void setXY(Integer x, Integer y) {
		if(x > TSArena5by5.WIDTH-1 || x < 0 || y > TSArena5by5.HEIGHT-1 || y < 0) 
			throw new InvalidParameterException();
		this.x = x;
		this.y = y;
		// wall sensor update
		wallSensor.setTrackableValue(new Tuple(
				"WALLS",
				Arena.getWallsFromLocation(this, 0, TSArena5by5.WIDTH-1, 0, TSArena5by5.HEIGHT-1))
		);
	}

	public MemorySensor getWallSensor(){
		return wallSensor;
	}
	
	@Override
	public String toString() {
		return "<" + x + "," + y + ">";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((x == null) ? 0 : x.hashCode());
		result = prime * result + ((y == null) ? 0 : y.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Location other = (Location) obj;
		if (x == null) {
			if (other.x != null)
				return false;
		} else if (!x.equals(other.x))
			return false;
		if (y == null) {
			if (other.y != null)
				return false;
		} else if (!y.equals(other.y))
			return false;
		return true;
	}
}