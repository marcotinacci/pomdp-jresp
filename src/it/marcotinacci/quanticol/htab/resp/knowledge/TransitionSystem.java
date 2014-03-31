package it.marcotinacci.quanticol.htab.resp.knowledge;


// TODO abstract class to be extended with concrete scenarios

public abstract class TransitionSystem{

	public abstract String pomdpDescriptor();
}
//	public static void main(String[] args) {
//		TransitionSystem ts = new TransitionSystem(0, HEIGHT-1, 0, WIDTH-1);
//		System.out.println(ts);
//		System.out.println(ts.PomdpDescriptor());
//	}
//	
//	// arena dimension
//	public static final Integer WIDTH = 4;
//	public static final Integer HEIGHT = 4;
//	
//	// FIXME set internal references to enum of actions and observations
//	// robot's action set
////	public enum Action { North, South, East, West, Stand };
//	
//	// robot's observation set
////	public enum Observation {North, South, East, West, NorthEast, NorthWest, SouthEast, SouthWest, Free};
//	
//	// state set
//	protected List<State> states;
//	// transition relation with deterministic observations
//	protected Map<Domain, Codomain> transitions;
//	private Integer minHeight;
//	private Integer maxHeight;
//	private Integer minWidth;
//	private Integer maxWidth;
//	
//	public TransitionSystem(Integer minH, Integer maxH, Integer minW, Integer maxW) {
//		minHeight = minH;
//		maxHeight = maxH;
//		minWidth = minW;
//		maxWidth = maxW;
//		init();
//	}
//	
//	private void init() {
//		// states
//		states = new LinkedList<State>();
//		for (int i = minWidth; i <= maxWidth; i++) {
//			for (int j = minHeight; j <= maxHeight; j++) {
//				List<Location> locs = new LinkedList<Location>();
//				locs.add(new Location(i,j));
//				// System.out.println(locs);
//				states.add(new State(locs));
//			}
//		}
//		// transitions
//		transitions = new Hashtable<Domain, Codomain>();
//		// for every location
//		for (State s : states) {
//			// TODO consider nondeterminstism
//			Location loc = s.getLocations().get(0);
//			// for every action
//			for (Action act : Action.values()) {
//				Set<State> ns = new HashSet<TransitionSystem.State>();
//				Location nl = new Location(loc);
//				Arena.applyActionToLocation(nl, act, minWidth, maxWidth, minHeight, maxHeight);
//				List<Location> loclist = new LinkedList<Location>();
//				loclist.add(nl);
//				ns.add(new State(loclist));
//				List<State> stateList = new LinkedList<State>();
//				stateList.add(s);
//				transitions.put(new Domain(stateList, act), new Codomain(ns, Arena.getWallsFromLocation(nl, minWidth, maxWidth, minHeight, maxHeight)));
//			}
//		}
//	}
//
//	public Integer getMinHeight() {
//		return minHeight;
//	}
//
//	public void setMinHeight(Integer minHeight) {
//		this.minHeight = minHeight;
//	}
//
//	public Integer getMaxHeight() {
//		return maxHeight;
//	}
//
//	public void setMaxHeight(Integer maxHeight) {
//		this.maxHeight = maxHeight;
//	}
//
//	public Integer getMinWidth() {
//		return minWidth;
//	}
//
//	public void setMinWidth(Integer minWidth) {
//		this.minWidth = minWidth;
//	}
//
//	public Integer getMaxWidth() {
//		return maxWidth;
//	}
//
//	public void setMaxWidth(Integer maxWidth) {
//		this.maxWidth = maxWidth;
//	}
//
//	public List<State> getStates() {
//		return Collections.unmodifiableList(states);
//	}
//
//	public Map<Domain, Codomain> getTransitions() {
//		return Collections.unmodifiableMap(transitions);
//	}
//
//	@Override
//	public String toString() {
//		StringBuffer sb = new StringBuffer();
//		// states
//		sb.append("States: {");
//		for (State s : states) {
//			for (Location l : s.getLocations()) {
//				sb.append(l.toString()+" ");
//			}
//		}
//		sb.append("}\n");
//		// transitions
//		sb.append("Transitions: {");
//		sb.append(transitions);
//		sb.append("}\n");
//		return sb.toString();
//	}
//	
//	public String PomdpDescriptor(){
//		StringBuffer str = new StringBuffer("# Auto-generated file - Marco Tinacci\n\n");
//		
//		// parameters
//		str.append("discount: 0.75\n");
//		str.append("values: reward\n");
//		str.append("states: " + states.size() + "\n");
//
//		// action list
//		str.append("actions:");
//		for (Action a : Action.values())
//			str.append(" " + a.name());
//		str.append("\n");
//		
//		// observation list
//		str.append("observations:");
//		for (Observation o : Observation.values())
//			str.append(" " + o.name());
//		str.append("\n");
//		
//		StringBuffer obs_str = new StringBuffer();
//		// transition and observation functions
//		for (Action a : Action.values()) {
//			str.append("\nT: "+ a +"\n");
//			obs_str.append("\nO: "+ a +"\n");
//			// for each state
//			for (State s1 : states) {
//				List<State> l = new LinkedList<State>();
//				l.add(s1);
//
//				Set<State> ss = transitions.get(new Domain(l, a)).states;
//				Iterator<State> it = ss.iterator();
//				State s = it.next();
//
//				Observation o = transitions.get(new Domain(l, a)).observation;
//				
//				for (State s2 : states) 
//					str.append(s.equals(s2) ? "1.0 " : "0.0 ");
//				for (Observation obs : Observation.values())
//					obs_str.append(obs.equals(o) ? "1.0 " : "0.0 ");
//				str.append("\n");
//				obs_str.append("\n");
//			}
//		}
//		str.append(obs_str);
//
//		// reward function
//		str.append("\nR: * : * : * : NorthEast 1.0");
//		
//		return str.toString();
//	}
//	
//	/**
//	 * Inner class representing a state of the system
//	 * @author Marco Tinacci
//	 */
//	public class State{
//		// TODO state representation by reflection on annotations
//		private List<Location> locations;
//		
//		public State() {
//			locations = new LinkedList<Location>();
//		}
//		
//		public State(List<Location> locs) {
//			locations = locs;
//		}
//
//		public List<Location> getLocations() {
//			return locations;
//		}	
//		
//		@Override
//		public String toString() {
//			return locations.toString();
//		}
//
//		@Override
//		public int hashCode() {
//			final int prime = 31;
//			int result = 1;
//			result = prime * result + getOuterType().hashCode();
//			result = prime * result
//					+ ((locations == null) ? 0 : locations.hashCode());
//			return result;
//		}
//
//		@Override
//		public boolean equals(Object obj) {
//			if (this == obj)
//				return true;
//			if (obj == null)
//				return false;
//			if (getClass() != obj.getClass())
//				return false;
//			State other = (State) obj;
//			if (!getOuterType().equals(other.getOuterType()))
//				return false;
//			if (locations == null) {
//				if (other.locations != null)
//					return false;
//			} else if (!locations.equals(other.locations))
//				return false;
//			return true;
//		}
//
//		private TransitionSystem getOuterType() {
//			return TransitionSystem.this;
//		}
//	}
//	
//	public class Domain{
//		public List<State> states;
//		public Action action;
//		
//		public Domain(List<State> states, Action action) {
//			this.states = states;
//			this.action = action;
//		}
//		@Override
//		public String toString() {
//			return "("+ states.toString() + "," + action.name() +")";
//		}
//		@Override
//		public int hashCode() {
//			final int prime = 31;
//			int result = 1;
//			result = prime * result + getOuterType().hashCode();
//			result = prime * result
//					+ ((action == null) ? 0 : action.hashCode());
//			result = prime * result
//					+ ((states == null) ? 0 : states.hashCode());
//			return result;
//		}
//		@Override
//		public boolean equals(Object obj) {
//			if (this == obj)
//				return true;
//			if (obj == null)
//				return false;
//			if (getClass() != obj.getClass())
//				return false;
//			Domain other = (Domain) obj;
//			if (!getOuterType().equals(other.getOuterType()))
//				return false;
//			if (action != other.action)
//				return false;
//			if (states == null) {
//				if (other.states != null)
//					return false;
//			} else if (!states.equals(other.states))
//				return false;
//			return true;
//		}
//		private TransitionSystem getOuterType() {
//			return TransitionSystem.this;
//		}
//	}
//	
//	public class Codomain{
//		public Set<State> states;
//		public Observation observation;
//		
//		public Codomain(Set<State> states, Observation observation) {
//			this.states = states;
//			this.observation = observation;
//		}
//		@Override
//		public String toString() {
//			return "(" + states + "," + observation + ")";
//		}
//		@Override
//		public int hashCode() {
//			final int prime = 31;
//			int result = 1;
//			result = prime * result + getOuterType().hashCode();
//			result = prime * result
//					+ ((observation == null) ? 0 : observation.hashCode());
//			result = prime * result
//					+ ((states == null) ? 0 : states.hashCode());
//			return result;
//		}
//		@Override
//		public boolean equals(Object obj) {
//			if (this == obj)
//				return true;
//			if (obj == null)
//				return false;
//			if (getClass() != obj.getClass())
//				return false;
//			Codomain other = (Codomain) obj;
//			if (!getOuterType().equals(other.getOuterType()))
//				return false;
//			if (observation == null) {
//				if (other.observation != null)
//					return false;
//			} else if (!observation.equals(other.observation))
//				return false;
//			if (states == null) {
//				if (other.states != null)
//					return false;
//			} else if (!states.equals(other.states))
//				return false;
//			return true;
//		}
//		private TransitionSystem getOuterType() {
//			return TransitionSystem.this;
//		}
//		
//	}