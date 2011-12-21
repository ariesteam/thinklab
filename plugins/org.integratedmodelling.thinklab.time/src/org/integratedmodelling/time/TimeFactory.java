/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.time;

import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabNoKMException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.time.extents.RegularTimeGridExtent;
import org.integratedmodelling.time.extents.TemporalLocationExtent;
import org.integratedmodelling.time.literals.DurationValue;
import org.integratedmodelling.time.literals.TimeValue;
import org.integratedmodelling.utils.Pair;


/**
 * Build time topologies.
 * 
 * @author Ferdinando
 *
 */
public class TimeFactory {

	/**
	 * Turn a string into a the list representation of the corresponding
	 * time topology. If the string contains a slash, that
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
	 * @throws ThinklabNoKMException 
	 * @throws ThinklabValidationException 
	 */
	public static IExtent parseTimeTopology(String s) throws ThinklabException {
		
		String ext = null;
		String res = null;
		
		if (s.contains("/")) {
			String[] ss = s.split("/");
			ext = ss[0];
			res = ss[1];
		} else {
			ext = s;
		}
		
		DurationValue step = null;
		if (res != null)
			step = new DurationValue(res);

		TimeValue start = null;
		TimeValue end = null;
		
		if (Character.isLetter(ext.charAt(ext.length()-1))) {
			/*
			 * extent is a duration, start it now
			 */
			DurationValue duration = new DurationValue(ext);
			Pair<TimeValue, TimeValue> pd = duration.localize();
			start = pd.getFirst();
			end = pd.getSecond();
			
			if (res == null && duration.getOriginalQuantity() > 1) {
				res = (1 + duration.getOriginalUnit());
				step = new DurationValue(res);
			}
			
		} else {
			
			String exd = null;
			if (ext.contains("#")) {
				String[] zo = ext.split("#");
				ext = zo[0];
				exd = zo[1];
			}
			/*
			 * extent is a date or a range thereof, extent is one time the implied 
			 * resolution.
			 */
			start = new TimeValue(ext);
			end = exd == null ? start.getEndOfImpliedExtent() : new TimeValue(exd);
		}
		
		
		IExtent ret = null;
		if (res == null) {
			ret = new TemporalLocationExtent(start);
		} else {
			ret = 
				new RegularTimeGridExtent(
						start.getTimeData(), 
						end.getTimeData(), 
						step.getMilliseconds());
		}
		
		return ret;
	}
	
}
