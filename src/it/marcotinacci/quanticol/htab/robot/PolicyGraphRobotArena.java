package it.marcotinacci.quanticol.htab.robot;

import it.marcotinacci.quanticol.htab.resp.knowledge.PolicyArena;
import it.marcotinacci.quanticol.htab.resp.knowledge.TransitionSystem;
import it.marcotinacci.quanticol.htab.scenario.TSArena5by5.Action;
import it.marcotinacci.quanticol.htab.scenario.TSArena5by5.Observation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.cmg.resp.knowledge.ActualTemplateField;
import org.cmg.resp.knowledge.FormalTemplateField;
import org.cmg.resp.knowledge.Template;
import org.cmg.resp.knowledge.Tuple;
import org.cmg.resp.topology.Self;


// TODO !!! generalizzare le azioni usando solo indici!

public class PolicyGraphRobotArena extends AbstractRobot{
	
	private PolicyArena _policy;
//	private TransitionSystem ts;
	
	public PolicyGraphRobotArena(TransitionSystem ts) {
		super("Policy Graph Robot");
//		this.ts = ts;
		System.out.println(ts);
		String pomdpPath = "/Users/marco/Workspace/pomdp-solve-5.3/";
		String fileName = "test";
		String[] pomdpCommand = {pomdpPath+"src/pomdp-solve", "-pomdp", pomdpPath+"pomdp/"+fileName+".POMDP", "-o", pomdpPath+"pomdp/"+fileName};
		String[] writeFileCommand = { "bash", "-c", "echo \""+ts.pomdpDescriptor()+"\" > "+pomdpPath+"pomdp/"+fileName+".POMDP" };	
//		System.out.println(ts.PomdpDescriptor());
		Process p;
		try {
			p = Runtime.getRuntime().exec(writeFileCommand);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
//			while ((line = reader.readLine())!= null) System.out.println(line);
			p = Runtime.getRuntime().exec(pomdpCommand);
			p.waitFor();
			reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = reader.readLine())!= null) System.out.println(line);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		_policy = new PolicyArena(pomdpPath+"pomdp/"+fileName);
	}
	
	public PolicyGraphRobotArena(TransitionSystem ts, String policyFileName){
		super("Policy Graph Robot");
		String pomdpPath = "/Users/marco/Workspace/pomdp-solve-5.3/";		
		_policy = new PolicyArena(pomdpPath+policyFileName);
//		_policy = new Policy(pomdpPath+"pomdp/"+policyFileName);
	}
	
	@Override
	protected void doRun() throws InterruptedException, IOException {
		System.out.println("doRun");
		System.out.println(_policy);
		boolean first = true;
		while(true){
			// skip observation at the first loop
			if(!first){
				// QRY ("WALLS", obs) @ self
				Tuple t = query(new Template(new ActualTemplateField("WALLS"), new FormalTemplateField(Observation.class)), Self.SELF);
				Observation obs = t.getElementAt(Observation.class,1);
				System.out.println("obs: "+obs.name());
				// policy state transition
				_policy.nextState(obs);
			}else{
				first = false;
			}
			
			// policy action
			Action act = _policy.getCurrentAction();
			// PUT ("MOVE",name,act) @ self
			put(new Tuple("MOVE",act), Self.SELF);
			System.out.println("PUT < MOVE , "+ act.name() +" >");

			// QRY ("STEP",true) @ self
			query(new Template( new ActualTemplateField( "STEP" ) , new ActualTemplateField(true) ), Self.SELF);
		}
	}

}
