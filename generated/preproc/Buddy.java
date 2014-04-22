
import java.util.ArrayList;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.RevoluteJoint;


public class Buddy {

	ArrayList<Body> parts = new ArrayList<Body>();
	ArrayList<RevoluteJoint> joints = new ArrayList<RevoluteJoint>();
	BuddyInstructions instructions;
	
	String dna = "";
	
	int currentTick = 0;
	int currentStage = 0;
	
	int distanceTravelled = 0;
	
	public void addPart(Body b){
		parts.add(b);
	}
	
	public void addJoint(RevoluteJoint j, float torque){
		j.enableMotor(true);
		j.setMaxMotorTorque(torque);
		joints.add(j);
	}
	
	public Body getHead(){
		return parts.get(0);
	}
	
	public int getStage(){
		currentTick++;
		currentTick = (int) (currentTick % instructions.timings.get(currentStage));
		if(currentTick == 0)
			currentStage++;
		currentStage = currentStage % instructions.totalStages;
		
		return currentStage;
	}
	
	public float getDistanceTravelled(float startX){
		
		return parts.get(0).getPosition().x - startX;
	}
	
	public String toString(){
		return dna + "/" + instructions.toString();
	}
	
	
	
}
