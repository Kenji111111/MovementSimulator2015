import java.util.ArrayList;


public class BuddyInstructions {

	ArrayList<ArrayList<Float>> speeds = new ArrayList<ArrayList<Float>>();
	ArrayList<Float> timings = new ArrayList<Float>();
	
	int totalTime = 0;
	int totalStages = 0;
	
	
	public BuddyInstructions(ArrayList<ArrayList<Float>> spede, ArrayList<Float> time){
		speeds = spede;
		timings = time;
		
		for(float f : timings)
			totalTime += f;
		
		totalStages = speeds.size();
	}
	
	// This method creates a string useful for seeing what data is contained and for the Buddy's toString method. 
	public String toString(){
		String s = "";
		for(int i = 0; i < timings.size(); i ++){
			s+= '[';
			for(float f : speeds.get(i)){
				s += f + ",";
			}
			s = s.substring(0, s.length() - 1);
			s += "|" + timings.get(i) + "]";
		}
		return s;
	}
}
