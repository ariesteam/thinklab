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

import java.util.Properties;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
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
    
    private static ISemanticObject absoluteTimeInstance; 
    private static ISemanticObject continuousTimeInstance; 

    public void load() throws ThinklabException {

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

    	timeGridConcept = Thinklab.c(TEMPORALGRID_TYPE_ID);
    	timeRecordConcept = Thinklab.c(TIMERECORD_TYPE_ID);
    	dateTimeConcept = Thinklab.c(DATETIME_TYPE_ID);
    	durationConcept = Thinklab.c(DURATION_TYPE_ID);
    	timeObservable = Thinklab.c(TIME_OBSERVABLE_ID);
    	
    	absoluteTimeInstance = null ;// km.requireInstance(ABSOLUTE_TIME_OBSERVABLE_INSTANCE);
    	continuousTimeInstance = null; // km.requireInstance(CONTINUOUS_TIME_OBSERVABLE_INSTANCE);

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
	
	public static ISemanticObject continuousTimeInstance() {
		return continuousTimeInstance;
	}

	public static ISemanticObject absoluteTimeInstance() {
		return absoluteTimeInstance;
	}

	public IConcept TimeObservable() {
		return timeObservable;
	}

	@Override
	protected String getPluginBaseName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Properties getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

}
