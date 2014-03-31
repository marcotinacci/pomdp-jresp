package it.marcotinacci.quanticol.htab.resp.knowledge;

import it.marcotinacci.quanticol.htab.Arena;
import it.marcotinacci.quanticol.htab.Location;
import it.marcotinacci.quanticol.htab.scenario.TSArena5by5;
import it.marcotinacci.quanticol.htab.scenario.TSArena5by5.Action;
import it.marcotinacci.quanticol.htab.scenario.TSArena5by5.Observation;
import it.marcotinacci.quanticol.htab.scenario.TSArena5by5.State;
import it.marcotinacci.quanticol.htab.utils.Rational;

import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A belief state is a distribution over the state set. If nothing is known
 * at the beginning it can be assumed that the distribution is uniform.
 * @author Marco Tinacci
 *
 */

public class Belief {

	public static void main(String[] args) {
		TSArena5by5 ts = new TSArena5by5(0, TSArena5by5.HEIGHT, 0, TSArena5by5.WIDTH);
		Belief b = new Belief(ts.getStates());
		System.out.println("b(0) = " + b);
		
		b.update(Action.North, Observation.North);
		System.out.println("ACT = "+Action.North.name()+"; OBS = "+Observation.North.name());
		System.out.println("b(1) = " + b);

		b.update(Action.East, Observation.NorthEast);
		System.out.println("ACT = "+Action.East.name()+"; OBS = "+Observation.NorthEast.name());
		System.out.println("b(2) = " + b);

		b.update(Action.East, Observation.NorthEast);
		System.out.println("ACT = "+Action.East.name()+"; OBS = "+Observation.NorthEast.name());
		System.out.println("b(3) = " + b);
	}
	
	// current belief
	protected Map<TSArena5by5.State, Rational> distribution;
	// state set S
	protected List<State> states;
	// observation set O
	protected Set<Observation> observations;
	// transition function: S x A x S -> [0,1]
	protected Map<State, Map<Action, Map<State, Rational>>> transitionFunction;
	// observation function: S x A x O -> [0,1]
	protected Map<Action,Map<State,Map<Observation,Rational>>> observationFunction;

	
	public Belief(List<State> states) {
		this.states = states;
		distribution = new Hashtable<TSArena5by5.State, Rational>();

		// uniform distribution over states
		Integer card = states.size();
		for (State state : states)
			distribution.put(state, new Rational(1, card));
		
		// FIXME coupling on width and height parameters
		// instantiate the transition function
		transitionFunction = new Hashtable<State, 
				Map<Action,Map<State,Rational>>>();
		// starting location
		for (State s1 : states) {
			Map<Action, Map<State,Rational>> m1 = new Hashtable<Action, Map<State,Rational>>();
			transitionFunction.put(s1, m1);
			// action
			for (Action act : Action.values()) {
				Map<State, Rational> m2 = new Hashtable<State, Rational>();
				m1.put(act, m2);
				// arrival location
				for (State s2 : states) {
					Location loc = new Location(s1.getLocations().get(0));
					Arena.applyActionToLocation(loc, act, 0, TSArena5by5.WIDTH-1, 0, TSArena5by5.HEIGHT-1,false);
					m2.put(s2, s2.getLocations().get(0).equals(loc) ? new Rational(1,1) : new Rational(0,1));
				}
			}
		}
		
		// FIXME coupling on width and height parameters
		// instantiate the observation function
		observationFunction = new Hashtable<Action, Map<State,Map<Observation,Rational>>>();
		for (Action a : Action.values()) {
			Map<State, Map<Observation,Rational>> m1 = 
					new Hashtable<State, Map<Observation,Rational>>();
			observationFunction.put(a, m1);
			for (State s : states) { // arrival state
				Map<Observation, Rational> m2 = new Hashtable<Observation, Rational>();
				m1.put(s, m2);
				for (Observation o : Observation.values()) {
					Location loc = new Location(s.getLocations().get(0));
					//Arena.applyActionToLocation(loc, a, 0, Params.WIDTH-1, 0, Params.HEIGHT-1);
					Observation obs = Arena.getWallsFromLocation(loc, 0, TSArena5by5.WIDTH-1, 0, TSArena5by5.HEIGHT-1);
					if(o.equals(obs))
						m2.put(o, new Rational(1,1));
					else
						m2.put(o, new Rational(0,1));
				}
			}
		}
	}

	/**
	 * Belief update
	 * @param act
	 * @param obs
	 */
	public void update(Action act, Observation obs){
		// FIXME improve performance, transition and observation probabilities are repeated
		Map<State,Rational> newDist = new Hashtable<State, Rational>();
		
		// normalizing factor
		Rational norm = new Rational(0,1);
		for (State s1 : states) {
			Rational temp = new Rational(0,1);
			for (State s2 : states)
				temp.add(Rational.times(transitionProb(s2,act,s1), distribution.get(s2)));
			norm.add(Rational.times(observationProb(s1, act, obs), temp));
		}
		
		// for each arrival state compute the updated belief probability
		for (State s1 : states) { 
			// if(!prob.equals(new Rational(0,1))){
			Rational temp = new Rational(0,1);
			// for each state
			for (State s2 : states)
				temp.add(Rational.times(distribution.get(s2), transitionProb(s2,act,s1)));
			temp.mult(observationProb(s1, act, obs));
			temp.div(norm);
			// }
			newDist.put(s1, temp);
		}
		distribution = newDist;
	}
	
	private Rational observationProb(State s, Action a, Observation o) {
		return observationFunction.get(a).get(s).get(o);
	}

	private Rational transitionProb(State s1, Action act, State s2) {
		return transitionFunction.get(s1).get(act).get(s2);
	}

	public Map<State, Rational> getDistribution() {
		return Collections.unmodifiableMap(distribution);
	}

	public List<State> getStates() {
		return Collections.unmodifiableList(states);
	}
	
	@Override
	public String toString() {
		return distribution.toString();
	}

}
