/**
 * TimePlugin.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabTimePlugin.
 * 
 * ThinklabTimePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabTimePlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.thinklab.time;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;

/**
 * FIXME de-pluginize
 * @author Ferd
 *
 */
public class Time  {

	public static final String PLUGIN_ID = "org.integratedmodelling.thinklab.time";
	
	public static final String CONTINUOUS_TIME_OBSERVABLE_INSTANCE = "time:ContinuousTimeObservableInstance";
	public static final String ABSOLUTE_TIME_OBSERVABLE_INSTANCE = "time:AbsoluteTimeObservableInstance";

	public static final String TIME_OBSERVABLE_ID = "time:TemporalCoverage";
	
	static public String DATETIME_TYPE_ID;
    static public String TIMERECORD_TYPE_ID;
    static public String TEMPORALGRID_TYPE_ID;
    static public String PERIOD_TYPE_ID;
    static public String DURATION_TYPE_ID;

    public static String STARTS_AT_PROPERTY_ID;
    public static String ENDS_AT_PROPERTY_ID;
    public static String STEP_SIZE_PROPERTY_ID;
    
    static private IConcept timeGridConcept;
    static private IConcept timeRecordConcept;
    static private IConcept dateTimeConcept;
    static private IConcept durationConcept;
    static private IConcept timeDomain;
    
    private static ISemanticObject<?> absoluteTimeInstance; 
    private static ISemanticObject<?> continuousTimeInstance; 

    private static Time _this;
    
    private Time()  {

    	/*
    	 * FIXME all this is silly. Just use a global TIME object with the IDs as constants.
    	 */
    	DATETIME_TYPE_ID = 
    		Thinklab.get().getProperties().getProperty("DateTimeTypeID", "time:DateTimeValue");
    	TIMERECORD_TYPE_ID = 
    			Thinklab.get().getProperties().getProperty("TemporalLocationRecordTypeID", "time:TemporalLocationRecord");
    	TEMPORALGRID_TYPE_ID = 
    			Thinklab.get().getProperties().getProperty("TemporalGridTypeID", "time:RegularTemporalGrid");
    	PERIOD_TYPE_ID = 
    			Thinklab.get().getProperties().getProperty("PeriodTypeID", "time:PeriodValue");
    	DURATION_TYPE_ID = 
    			Thinklab.get().getProperties().getProperty("DurationTypeID", "time:DurationValue");

    	STARTS_AT_PROPERTY_ID = 
    			Thinklab.get().getProperties().getProperty("StartsAtPropertyID", "time:startsAt");
    	ENDS_AT_PROPERTY_ID = 
    			Thinklab.get().getProperties().getProperty("EndsAtPropertyID", "time:endsAt");
    	STEP_SIZE_PROPERTY_ID = 
    			Thinklab.get().getProperties().getProperty("StepSizePropertyID", "time:inStepsOf");

    	timeGridConcept = Thinklab.c(TEMPORALGRID_TYPE_ID);
    	timeRecordConcept = Thinklab.c(TIMERECORD_TYPE_ID);
    	dateTimeConcept = Thinklab.c(DATETIME_TYPE_ID);
    	durationConcept = Thinklab.c(DURATION_TYPE_ID);
    	timeDomain = Thinklab.c(TIME_OBSERVABLE_ID);
    	
    	absoluteTimeInstance = null ;// km.requireInstance(ABSOLUTE_TIME_OBSERVABLE_INSTANCE);
    	continuousTimeInstance = null; // km.requireInstance(CONTINUOUS_TIME_OBSERVABLE_INSTANCE);

    }

    public static Time get() {
    	if (_this == null)
    		_this = new Time();
    	return _this;
    }
    
    public void unload() throws ThinklabException {
        // TODO Auto-generated method stub

    }

	public static IConcept TimeGrid() {
		return timeGridConcept;
	}

	public static IConcept TimeRecord() {
		return timeRecordConcept;
	}

	public static IConcept DateTime() {
		return dateTimeConcept;
	}
	
	public static IConcept Duration() {
		return durationConcept;
	}
	
	public static ISemanticObject<?> continuousTimeInstance() {
		return continuousTimeInstance;
	}

	public static ISemanticObject<?> absoluteTimeInstance() {
		return absoluteTimeInstance;
	}

	public IConcept TimeDomain() {
		return timeDomain;
	}


}
