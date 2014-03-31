package it.marcotinacci.quanticol.htab.scenario;

import it.marcotinacci.quanticol.htab.Arena;
import it.marcotinacci.quanticol.htab.Location;
import it.marcotinacci.quanticol.htab.resp.knowledge.TransitionSystem;
import it.marcotinacci.quanticol.htab.utils.Rational;

import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

// TODO abstract class to be extended with concrete scenarios

public class TSArena5by5 extends TransitionSystem{
	
	public static void main(String[] args) {
		TSArena5by5 ts = new TSArena5by5(0, HEIGHT-1, 0, WIDTH-1);
		System.out.println(ts);
		System.out.println(ts.pomdpDescriptor());
	}
	
	// =======================================
	// ============ PARAMETERS ===============
	// =======================================

	// arena dimension
	public static final Integer WIDTH = 3;
	public static final Integer HEIGHT = 3;
	// target
	public static final Location targetLocation = new Location(1,1);
	// robot's action set
	public enum Action { North, South, East, West, Stand };
	// robot's observation set
	public enum Observation {North, South, East, West, NorthEast, NorthWest, SouthEast, SouthWest, Free};
	
	// state set
	protected List<State> states;
	// transition function: S x A x S -> [0,1]
	protected Map<State, Map<Action, Map<State, Rational>>> transitionFunction;
	// observation function: S x A x O -> [0,1]
	protected Map<Action, Map<State, Map<Observation, Rational>>> observationFunction;
	
	// transition relation with deterministic observations
	private Integer minHeight;
	private Integer maxHeight;
	private Integer minWidth;
	private Integer maxWidth;

	public TSArena5by5(Integer minH, Integer maxH, Integer minW, Integer maxW) {
		minHeight = minH;
		maxHeight = maxH;
		minWidth = minW;
		maxWidth = maxW;
		init();
	}
	
	private void init() {
		// states
		states = new LinkedList<State>();
		for (int i = minWidth; i <= maxWidth; i++) {
			for (int j = minHeight; j <= maxHeight; j++) {
				List<Location> locs = new LinkedList<Location>();
				locs.add(new Location(i,j));
				// System.out.println(locs);
				states.add(new State(locs));
			}
		}
		
		// instantiate the transition function
		transitionFunction = new Hashtable<State, Map<Action,Map<State,Rational>>>();
		// start states
		for (State ss : states) {
			Map<Action, Map<State,Rational>> ssm = new Hashtable<Action, Map<State,Rational>>();
			transitionFunction.put(ss, ssm);
			// actions
			for (Action act : Action.values()) {
				Map<State, Rational> actm = new Hashtable<State, Rational>();
				ssm.put(act, actm);
				// end states
				for (State es : states) {
					// TODO refactoring, this condition can be extracted as a parameter
					Location loc = new Location(ss.locations.get(0));
					Arena.applyActionToLocation(loc, act, minWidth, maxWidth, minHeight, maxHeight,false);
					if(loc.equals(targetLocation))
						actm.put(es, new Rational(1, HEIGHT*WIDTH));
					else
						actm.put(es, loc.equals(es.locations.get(0)) ? new Rational(1, 1) : new Rational(0, 1));
				}
			}
		}
		
		// instantiate the observation function
		observationFunction = new Hashtable<Action, Map<State,Map<Observation,Rational>>>();
		// actions
		for (Action act : Action.values()) {
			Map<State, Map<Observation,Rational>> actm = new Hashtable<State, Map<Observation,Rational>>();			
			observationFunction.put(act, actm);
			// end states
			for (State es : states) {
				Map<Observation, Rational> esm = new Hashtable<Observation, Rational>();
				actm.put(es, esm);
				// observations
				for (Observation obs : Observation.values()) {
					// TODO refactoring, this condition can be extracted as a parameter
					esm.put(obs, obs.equals(Arena.getWallsFromLocation(es.locations.get(0), minWidth, maxWidth, minHeight, maxHeight)) ? 
							new Rational(1, 1) : new Rational(0, 1));
				}
			}
		}
	}

	public Integer getMinHeight() {
		return minHeight;
	}

	public void setMinHeight(Integer minHeight) {
		this.minHeight = minHeight;
	}

	public Integer getMaxHeight() {
		return maxHeight;
	}

	public void setMaxHeight(Integer maxHeight) {
		this.maxHeight = maxHeight;
	}

	public Integer getMinWidth() {
		return minWidth;
	}

	public void setMinWidth(Integer minWidth) {
		this.minWidth = minWidth;
	}

	public Integer getMaxWidth() {
		return maxWidth;
	}

	public void setMaxWidth(Integer maxWidth) {
		this.maxWidth = maxWidth;
	}

	public List<State> getStates() {
		return Collections.unmodifiableList(states);
	}

	public Map<State, Map<Action, Map<State, Rational>>> getTransitionFunction() {
		return Collections.unmodifiableMap(transitionFunction);
	}

	public void setTransitionFunction(
			Map<State, Map<Action, Map<State, Rational>>> transitionFunction) {
		this.transitionFunction = transitionFunction;
	}

	public Map<Action, Map<State, Map<Observation, Rational>>> getObservationFunction() {
		return Collections.unmodifiableMap(observationFunction);
	}

	public void setObservationFunction(
			Map<Action, Map<State, Map<Observation, Rational>>> observationFunction) {
		this.observationFunction = observationFunction;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		// states
		sb.append("States: {");
		for (State s : states) {
			for (Location l : s.getLocations()) {
				sb.append(l.toString()+" ");
			}
		}
		sb.append("}\n");
		// transitions
		sb.append("Transitions: {");
		sb.append(transitionFunction);
		sb.append("}\n");
		
		// observations
		sb.append("Observations: {");
		sb.append(observationFunction);
		sb.append("}\n");		
		
		return sb.toString();
	}
	
	@Override
	public String pomdpDescriptor(){
		StringBuffer str = new StringBuffer("# Auto-generated file - Marco Tinacci\n\n");
		
		// parameters
		str.append("discount: 0.75\n");
		str.append("values: reward\n");
		str.append("states: " + states.size() + "\n");

		// action list
		str.append("actions:");
		for (Action a : Action.values())
			str.append(" " + a.name());
		str.append("\n");
		
		// observation list
		str.append("observations:");
		for (Observation o : Observation.values())
			str.append(" " + o.name());
		str.append("\n");

		// transition function
		for (Action act : Action.values()) {
			str.append("\nT: "+ act +"\n");
			for (State ss : states) {
				for (State es : states) {
					str.append(transitionFunction.get(ss).get(act).get(es).getDouble().toString()+" ");
				}
				str.append("\n");
			}
		}
		
		// observation function
		for (Action act : Action.values()) {
			str.append("\nO: "+ act +"\n");
			for (State es : states) {
				for (Observation obs : Observation.values()) {
					str.append(observationFunction.get(act).get(es).get(obs).getDouble().toString()+" ");
				}
				str.append("\n");
			}
		}

		for (State s : states) {
			if(s.locations.get(0).equals(targetLocation)){
				// reward function
				str.append("\nR: * : * : " + states.indexOf(s) + " : * 1.0");
			}
		}
		
		return str.toString();
	}
	
	/**
	 * Inner class representing a state of the system
	 * @author Marco Tinacci
	 */
	public class State{
		// TODO state representation by reflection on annotations
		private List<Location> locations;
		
		public State() {
			locations = new LinkedList<Location>();
		}
		
		public State(List<Location> locs) {
			locations = locs;
		}

		public List<Location> getLocations() {
			return locations;
		}	
		
		@Override
		public String toString() {
			return locations.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((locations == null) ? 0 : locations.hashCode());
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
			State other = (State) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (locations == null) {
				if (other.locations != null)
					return false;
			} else if (!locations.equals(other.locations))
				return false;
			return true;
		}

		private TSArena5by5 getOuterType() {
			return TSArena5by5.this;
		}
	}
	
	public class Domain{
		public List<State> states;
		public Action action;
		
		public Domain(List<State> states, Action action) {
			this.states = states;
			this.action = action;
		}
		@Override
		public String toString() {
			return "("+ states.toString() + "," + action.name() +")";
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((action == null) ? 0 : action.hashCode());
			result = prime * result
					+ ((states == null) ? 0 : states.hashCode());
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
			Domain other = (Domain) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (action != other.action)
				return false;
			if (states == null) {
				if (other.states != null)
					return false;
			} else if (!states.equals(other.states))
				return false;
			return true;
		}
		private TSArena5by5 getOuterType() {
			return TSArena5by5.this;
		}
	}
	
	public class Codomain{
		public Set<State> states;
		public Observation observation;
		
		public Codomain(Set<State> states, Observation observation) {
			this.states = states;
			this.observation = observation;
		}
		@Override
		public String toString() {
			return "(" + states + "," + observation + ")";
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((observation == null) ? 0 : observation.hashCode());
			result = prime * result
					+ ((states == null) ? 0 : states.hashCode());
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
			Codomain other = (Codomain) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (observation == null) {
				if (other.observation != null)
					return false;
			} else if (!observation.equals(other.observation))
				return false;
			if (states == null) {
				if (other.states != null)
					return false;
			} else if (!states.equals(other.states))
				return false;
			return true;
		}
		private TSArena5by5 getOuterType() {
			return TSArena5by5.this;
		}
		
	}

}