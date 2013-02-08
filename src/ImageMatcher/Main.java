package ImageMatcher;

public class Main {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
			//Check the parameters and load them into the parameter object
			ParameterHandler pa = new ParameterHandler();
			try{
				//Checks the input is properly formed
				pa.checkParameters(args);

			}
			catch(Exception e){
				System.out.println("ERROR - There was an error parsing the inputs \n" + e.toString());
			}
			
			//Compare the patterns and images
			StringComparator comp = new StringComparator();
			try{
		//		comp.searchForPatterns(pa.getPattern(), pa.getSource());
			}
			catch(Exception e){
				System.out.println("ERROR - There was an error searching the source for patterns \n" + e.toString());
			}
			
			//Display Results
			
			
	}

}
