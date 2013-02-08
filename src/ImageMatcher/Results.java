package ImageMatcher;

import java.util.ArrayList;

public class Results {
	ArrayList<String> results;
	
	public Results(){}
	
	public void addResult(String result){
		this.results.add(result);
	}
	
	public void formatResults(){
		
	}
	
	public void displayResults(){
		System.out.println(this.results.toString());
	}
	
}
