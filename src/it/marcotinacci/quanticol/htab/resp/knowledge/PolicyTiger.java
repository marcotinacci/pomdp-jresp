package it.marcotinacci.quanticol.htab.resp.knowledge;

import it.marcotinacci.quanticol.htab.scenario.TSTiger.Action;
import it.marcotinacci.quanticol.htab.scenario.TSTiger.Observation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PolicyTiger {
	
	// policy graph
	private Map<Integer, Value> pg;
	// current state
	private Integer currentState;
	
	public PolicyTiger(String filename){
		loadPolicyFromFile(filename);
	}
	
	public void loadPolicyFromFile(String filename){
		pg = policyGraphFromFile(filename+".pg");
		currentState = startingStateFromFile(filename+".alpha");
		System.out.println("initial state: "+ currentState);
	}
	
	public void nextState(Observation o){
		 Integer s = pg.get(currentState).getStateTransition().get(o);
		 if(s == null){ 
			 System.err.println("action out of policy");
			 throw new RuntimeException(
				 "Case not covered by the policy. [s:"+currentState+", o:"+o.name()+"]");
		 }
		 currentState = s;
	}
	
	public Action getCurrentAction(){
		return pg.get(currentState).getAction();
	}
	
	public Integer getCurrentState() {
		return currentState;
	}
	
	private Integer startingStateFromFile(String filename) {
		BufferedReader br = null;
		Double maxValue = Double.MIN_VALUE;
		Integer maxIndex = 0;
		try {
			br = new BufferedReader(new FileReader(filename));
	
			for (int i=0; br.readLine() != null; i++){
				String[] strings = br.readLine().split(" ");
				Double value = 0.;
				for (String str : strings)
					value += Double.parseDouble(str);
				if(value > maxValue){
					maxValue = value;
					maxIndex = i;
				}
				br.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return maxIndex;
	}

	private Map<Integer, Value> policyGraphFromFile(String filename){
		Map<Integer, Value> map = new HashMap<Integer, Value>();
		BufferedReader br = null;
		String line = "";
		try {
			br = new BufferedReader(new FileReader(filename));
			while ((line = br.readLine()) != null) {
				// splits in two
				String[] strings = line.split("  ");
				// first part
				String[] first = strings[0].split(" ");
				// second part
				String[] second = strings[1].split(" ");
				
				Integer id = Integer.parseInt(first[0]);
//				System.out.println("id = "+id);
				Action a = Action.values()[Integer.parseInt(first[1])];
//				System.out.println("action = "+a);
				Map<Observation,Integer> m = new HashMap<Observation, Integer>();
				for (int i = 0; i < second.length; i++)
					if(!second[i].equals("-"))
						m.put(Observation.values()[i], Integer.parseInt(second[i]));
//				System.out.println("map = "+m);
//				System.out.println();
				Value v = new Value(a, m);
				map.put(id, v);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return map;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("current state: "+ currentState + "\n");
		sb.append("graph: "+ pg.toString() + "\n");
		return sb.toString();
	}
	
	private class Value {
		// FIXME container class can access to private fields
		private Action action;
		private Map<Observation, Integer> stateTransition;
		
		public Value(Action a, Map<Observation, Integer> m) {
			action = a;
			stateTransition = m;
		}
		
		public Action getAction() {
			return action;
		}
		
		public Map<Observation, Integer> getStateTransition() {
			return Collections.unmodifiableMap(stateTransition);
		}
		
		@Override
		public String toString() {
			return "(act: "+action+", transitions: "+stateTransition+")";
		}
	}
}
