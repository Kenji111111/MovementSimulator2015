
public class Utils {

	
	
	public float distance(float x1, float y1, float x2, float y2){
		
		return (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}
	
	public static int charCount(String s, char c){
		
		int counter = 0;
		
		for(char d : s.toCharArray()){
			if(c == d)
				counter++;
		}
		
		return counter;
	}
}
