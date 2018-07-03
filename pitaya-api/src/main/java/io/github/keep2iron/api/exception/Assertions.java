package io.github.keep2iron.api.exception;

/**
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2018/07/01 22:18
 */
public class Assertions {

	public static void assertCondition(boolean condition, String errorMessage) {
		if(condition){
			throw new PitayaAssertException(errorMessage);
		}
	}
}
