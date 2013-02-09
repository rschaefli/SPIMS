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
			ImageComparator comp = new ImageComparator();
			
			try{
				//iterate through the list of Files 
				//for each file in pattern
				//for each file in source
					//comp.compare(ImageHandler(patternImage), ImageHandler(sourceImage));
			}
			catch(Exception e){
				System.out.println("ERROR - There was an error searching the source for patterns \n" + e.toString());
			}
			
			//Display Results
			
			
	}

}
