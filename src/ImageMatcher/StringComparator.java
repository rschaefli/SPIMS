package ImageMatcher;

public class StringComparator extends Comparator{

	
	void compare(String pattern, String source) {
		
		char[] patternCharArray = pattern.toCharArray();
		char[] sourceCharArray = source.toCharArray();
		
		int pLength = patternCharArray.length;
		int sLength = sourceCharArray.length;
		
		for(int s = 0; s < sLength ; s++ ){
			
				if((sLength - s) < pLength){ break; }
			
				for(int p = 0; p < pLength; p++){
					if(patternCharArray[p] == sourceCharArray[s]){
						if(p == pLength - 1){
							System.out.println("The pattern "+pattern.toString()+" was found in "+source.toString());
						}
						else s++;
					}
					else break;
				}
		}
			
	}	
	
	
	
	void searchForPatterns(String patterns, String sources){
		
		String[] patternArray = patterns.split(",");
		String[] sourceArray = sources.split(",");
		
		int patternLength = patternArray.length;
		int sourceLength = sourceArray.length;
		
		for(int p = 0; p < patternLength ; p++ ){
			for(int s = 0; s < sourceLength ; s++ ){
				compare(patternArray[p],sourceArray[s]);
			}
			
		}		
		
	}
	
	
}
