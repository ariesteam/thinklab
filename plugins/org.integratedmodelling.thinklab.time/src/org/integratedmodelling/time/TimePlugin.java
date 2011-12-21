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

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;

public class TimePlugin extends ThinklabPlugin {

	public static final String PLUGIN_ID = "org.integratedmodelling.thinklab.time";
	
	public static final String CONTINUOUS_TIME_OBSERVABLE_INSTANCE = "time:ContinuousTimeObservableInstance";
	public static final String ABSOLUTE_TIME_OBSERVABLE_INSTANCE = "time:AbsoluteTimeObservableInstance";

	public static final String TIME_OBSERVABLE_ID = "time:TemporalObservable";
	
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
    static private IConcept timeObservable;
    
    private static IInstance absoluteTimeInstance; 
    private static IInstance continuousTimeInstance; 
    
	public static TimePlugin get() {
		return (TimePlugin) getPlugin(PLUGIN_ID);
	}
	
    public void load(KnowledgeManager km) throws ThinklabPluginException {

    	DATETIME_TYPE_ID = 
    		getProperties().getProperty("DateTimeTypeID", "time:DateTimeValue");
    	TIMERECORD_TYPE_ID = 
    		getProperties().getProperty("TemporalLocationRecordTypeID", "time:TemporalLocationRecord");
    	TEMPORALGRID_TYPE_ID = 
    		getProperties().getProperty("TemporalGridTypeID", "time:RegularTemporalGrid");
    	PERIOD_TYPE_ID = 
    		getProperties().getProperty("PeriodTypeID", "time:PeriodValue");
    	DURATION_TYPE_ID = 
    		getProperties().getProperty("DurationTypeID", "time:DurationValue");

    	STARTS_AT_PROPERTY_ID = 
    		getProperties().getProperty("StartsAtPropertyID", "time:startsAt");
    	ENDS_AT_PROPERTY_ID = 
    		getProperties().getProperty("EndsAtPropertyID", "time:endsAt");
    	STEP_SIZE_PROPERTY_ID = 
    		getProperties().getProperty("StepSizePropertyID", "time:inStepsOf");
    	
    	try {
    		
			timeGridConcept = km.requireConcept(TEMPORALGRID_TYPE_ID);
			timeRecordConcept = km.requireConcept(TIMERECORD_TYPE_ID);
			dateTimeConcept = km.requireConcept(DATETIME_TYPE_ID);
			durationConcept = km.requireConcept(DURATION_TYPE_ID);
			timeObservable = km.requireConcept(TIME_OBSERVABLE_ID);
			
			absoluteTimeInstance = km.requireInstance(ABSOLUTE_TIME_OBSERVABLE_INSTANCE);
			continuousTimeInstance = km.requireInstance(CONTINUOUS_TIME_OBSERVABLE_INSTANCE);
			
		} catch (ThinklabException e) {
			throw new ThinklabPluginException(e);
		}
    }

    public void unload() throws ThinklabPluginException {
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
	
	public static IInstance continuousTimeInstance() {
		return continuousTimeInstance;
	}

	public static IInstance absoluteTimeInstance() {
		return absoluteTimeInstance;
	}

	public IConcept TimeObservable() {
		return timeObservable;
	}

}
