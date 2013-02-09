/**
 * 
 */
package ImageMatcher;

/**
 * @author ChrisA
 *
 */
public interface Comparator {

		//abstract void searchForPatterns(String patterns, String source);
		public void compare(ImageHandler pattern, ImageHandler source);

}