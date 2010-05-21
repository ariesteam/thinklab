package org.integratedmodelling.time;

import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;


/**
 * Build time topologies.
 * 
 * @author Ferdinando
 *
 */
public class TimeFactory {

	/**
	 * Turn a string into a time topology. If the string contains a slash, that
	 * is assumed to separate extent from resolution and the result will be a 
	 * regular time grid. Otherwise, the result will be a time record observation.
	 * 
	 * The extent part (possibly the whole string) can be any time value such 
	 * as a year , a month-year, a day-month-year or a ISO date, or a number with
	 * units expressing a duration, such as 1s, 1h, 2year or so. If it's a duration,
	 * we assume the extent starts now with the appropriate resolution.
	 * 
	 * The resolution (if any) must be a duration and it should divide the 
	 * extent evenly and into at least one time "cell". All calculations are 
	 * done in milliseconds.
	 * 
	 * @param s
	 * @return
	 */
	IInstance parseTimeTopology(String s) {
		
		String ext = null;
		String res = null;
		
		if (s.contains("/")) {
			String[] ss = s.split("/");
			ext = ss[0];
			res = ss[1];
		} else {
			ext = s;
		}
		
		
		
		return null;
	}
	
}
