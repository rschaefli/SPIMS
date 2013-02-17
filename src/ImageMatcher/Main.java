package ImageMatcher;

import java.io.File;

    
	
	// For everything else a match was detected at the correct top left location
	
	public class Main {
		
		
		// TEST RUNS:
		// 4th comparison - flower.gif to ac1000.jpg fails
		// Might have to do with quality, not sure
	    
	        // 1 (black.jpg, bb0001.jpg took 5 seconds, correct location)
	    
	        // 3 (cliff.png, an0300.jpg took 4 seconds, correct location)
	    
	        // 4 (flower.gif, ac1000.jpg took 6 seconds, location slightly wrong)
	    
	        // 5 (nature.jpg, hh0021.jpg took 4 seconds, correct location)
		
		// 6 (rock.jpg, ar0800.jpg) took 4 seconds, correct location)
		
		// 7 (tree.jpg, aa0010.jpg) took 6 seconds, correct location)
		
		/**
		 * @param args
		 */
		public static void main(String[] args) {
			
			/*
				//Check the parameters and load them into the parameter object
				ParameterHandler pa = new ParameterHandler();
				try{
					//Checks the input is properly formed
					pa.checkParameters(args);
				}
				catch(Exception e){
					System.out.println("ERROR - There was an error parsing the inputs \n" + e.toString());
				}
				
				*/
			
				//Compare the patterns and images
				HistogramComparator comp = new HistogramComparator();
				
				try{
					File pFile = new File(args[0]);
					File sFile = new File(args[1]);
					
					if(pFile.isDirectory()){
						String[] patterns = pFile.list();
						for(String p : patterns){
							
							ImageHandler pattern = new ImageHandler(new File(pFile.getAbsolutePath() + File.separatorChar + p));
							
							if(sFile.isDirectory()){
								String[] sources = sFile.list();
									for(String s : sources){
										
										ImageHandler source = new ImageHandler(new File(sFile.getAbsolutePath() + File.separatorChar + s));
										System.out.println("Checking " + p +" against " + s);
										comp.compare(pattern, source);
										
									}
						
							}
							else{
								ImageHandler source = new ImageHandler(sFile);

								comp.compare(pattern, source);
							}
						}
					}
					else{
						ImageHandler pattern = new ImageHandler(pFile);
						ImageHandler source = new ImageHandler(sFile);
						comp.compare(pattern, source);
					}
				}
				catch(Exception e){
					System.out.println("ERROR - There was an error searching the source for patterns \n" + e.toString());
				}
				
				//Display Results
				
				
		}

	}
	

