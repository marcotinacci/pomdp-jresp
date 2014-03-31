package it.marcotinacci.quanticol.htab.scenario;

import it.marcotinacci.quanticol.htab.resp.knowledge.MemorySensor;
import it.marcotinacci.quanticol.htab.resp.knowledge.TransitionSystem;
import it.marcotinacci.quanticol.htab.utils.Rational;

import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cmg.resp.knowledge.ActualTemplateField;
import org.cmg.resp.knowledge.FormalTemplateField;
import org.cmg.resp.knowledge.Template;

public class TSTiger extends TransitionSystem{
	
	public static void main(String[] args) {
		TSTiger ts = new TSTiger();
		System.out.println(ts);
		System.out.println(ts.pomdpDescriptor());
	}
	
	// state set
	protected List<State> states;
	// transition relation with deterministic observations
	protected Map<Domain, Codomain> transitions;

	public enum Action {OpenLeft, OpenRight, Listen};
	
	public enum Observation {HearLeft, HearRight};
	
	// transition function: S x A x S -> [0,1]
	protected Map<State, Map<Action, Map<State, Rational>>> transitionFunction;
	// observation function: S x A x O -> [0,1]
	protected Map<Action, Map<State, Map<Observation, Rational>>> observationFunction;
	
	public TSTiger() {
		init();
	}
	
	private void init() {
		// states
		states = new LinkedList<State>();
		states.add(new State(true));
		states.add(new State(false));
		
		// transitions
		transitions = new Hashtable<Domain, Codomain>();
		// observations
		
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
					if(act.equals(Action.Listen))
						actm.put(es, es.equals(ss) ? new Rational(1, 1) : new Rational(0, 1));
					else
						actm.put(es, new Rational(1,2));
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
				if(act.equals(Action.Listen)){
					esm.put(Observation.HearLeft, es.isTigerLeft() ? new Rational(85, 100) : new Rational(15, 100));
					esm.put(Observation.HearRight, es.isTigerRight() ? new Rational(85, 100) : new Rational(15, 100));
//					esm.put(Observation.Win, new Rational(0, 1));
//					esm.put(Observation.Lose, new Rational(0, 1));
				}else{
					esm.put(Observation.HearLeft, new Rational(1, 2));
					esm.put(Observation.HearRight, new Rational(1, 2));
//					esm.put(Observation.Win, 
//							act.equals(Action.OpenLeft) && es.isTigerLeft() || act.equals(Action.OpenRight) 
//							&& es.isTigerRight() ? new Rational(0, 1) : new Rational(1, 1) );
//					esm.put(Observation.Lose, 
//							act.equals(Action.OpenLeft) && es.isTigerLeft() || act.equals(Action.OpenRight) 
//							&& es.isTigerRight() ? new Rational(1, 1) : new Rational(0, 1) );
				}
			}
		}
	}

	public List<State> getStates() {
		return Collections.unmodifiableList(states);
	}

	public Map<Domain, Codomain> getTransitions() {
		return Collections.unmodifiableMap(transitions);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		// states
		sb.append("States: {");
		for (State s : states) {
			sb.append(s.toString()+", ");
		}
		sb.append("}\n");
		// transitions
		sb.append("Transitions: {");
		sb.append(transitions);
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
		
//		StringBuffer obs_str = new StringBuffer();
//		// transition and observation functions
//		for (Action a : Action.values()) {
//			str.append("\nT: "+ a +"\n");
//			obs_str.append("\nO: "+ a +"\n");
//			// for each state
//			for (State s1 : states) {
//				State ss = transitions.get(new Domain(s1, a)).state;
//				Observation o = transitions.get(new Domain(s1, a)).observation;
//				
//				for (State s2 : states) 
//					str.append(ss.equals(s2) ? "1.0 " : "0.0 ");
//				for (Observation obs : Observation.values())
//					obs_str.append(obs.equals(o) ? "1.0 " : "0.0 ");
//				str.append("\n");
//				obs_str.append("\n");
//			}
//		}
//		str.append(obs_str);

		// transitions
		for (Action act : Action.values()) {
			str.append("T: "+ act +"\n");			
			for (State ss : states) {
				for (State es : states) {
					str.append(transitionFunction.get(ss).get(act).get(es).getDouble().toString()+" ");
				}
				str.append("\n");
			}
			str.append("\n");
		}
		
		// observations
		for (Action act : Action.values()) {
			str.append("O: "+ act +"\n");
			for (State es : states) {
				for (Observation obs : Observation.values()) {
					str.append(observationFunction.get(act).get(es).get(obs).getDouble().toString() + " ");
				}
				str.append("\n");
			}
			str.append("\n");
		}
		
		// reward function
		str.append("R: * : * : * : HearLeft -1.0\n");
		str.append("R: * : * : * : HearRight -1.0\n");
		str.append("R: OpenLeft : 1 : * : * +10.0\n");
		str.append("R: OpenRight : 0 : * : * +10.0\n");
		str.append("R: OpenLeft : 0 : * : * -100.0\n");
		str.append("R: OpenRight : 1 : * : * -100.0\n");
		
		return str.toString();
	}
	
	/**
	 * Inner class representing a state of the system
	 * @author Marco Tinacci
	 */
	static public class State{
		// TODO state representation by reflection on annotations
		private Boolean _tigerLeft;
		private MemorySensor _tigerSensor;
		
		public void setTigerLeft(Boolean left){
			this._tigerLeft = left;
		}
		
		public MemorySensor getTigerSensor(){
			return _tigerSensor;
		}
		
		public State(Boolean left) {
			_tigerLeft = left;
			_tigerSensor = new MemorySensor("tiger sensor", new Template(
					new ActualTemplateField("TIGER"), new FormalTemplateField(Observation.class)));
		}

		public Boolean isTigerLeft(){
			return _tigerLeft;
		}
		
		public Boolean isTigerRight(){
			return !_tigerLeft;
		}
		
		@Override
		public String toString() {
			return _tigerLeft ? "tiger: left" : "tiger: right";
		}

		private Class<?> getOuterType() {
			return TSTiger.class;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((_tigerLeft == null) ? 0 : _tigerLeft.hashCode());
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
			if (_tigerLeft == null) {
				if (other._tigerLeft != null)
					return false;
			} else if (!_tigerLeft.equals(other._tigerLeft))
				return false;
			return true;
		}
	}
	
	static public class Domain{
		public State state;
		public Action action;
		
		public Domain(State states, Action action) {
			this.state = states;
			this.action = action;
		}
		@Override
		public String toString() {
			return "("+ state.toString() + "," + action.name() +")";
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((action == null) ? 0 : action.hashCode());
			result = prime * result
					+ ((state == null) ? 0 : state.hashCode());
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
			if (state == null) {
				if (other.state != null)
					return false;
			} else if (!state.equals(other.state))
				return false;
			return true;
		}
		private Class<?> getOuterType() {
			return TSTiger.class;
		}
	}
	
	static public class Codomain{
		public State state;
		public Observation observation;
		public Double probability;
		
		public Codomain(State state, Observation observation) {
			this.state = state;
			this.observation = observation;
			this.probability = 1.;
		}
		
		public Codomain(State state, Observation observation, Double probability) {
			this.state = state;
			this.observation = observation;
			this.probability = probability;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((observation == null) ? 0 : observation.hashCode());
			result = prime * result
					+ ((probability == null) ? 0 : probability.hashCode());
			result = prime * result + ((state == null) ? 0 : state.hashCode());
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
			if (observation != other.observation)
				return false;
			if (probability == null) {
				if (other.probability != null)
					return false;
			} else if (!probability.equals(other.probability))
				return false;
			if (state == null) {
				if (other.state != null)
					return false;
			} else if (!state.equals(other.state))
				return false;
			return true;
		}

		private Class<?> getOuterType() {
			return TSTiger.class;
		}

		@Override
		public String toString() {
			return "Codomain [state=" + state + ", observation=" + observation
					+ ", probability=" + probability + "]";
		}
	}
}
