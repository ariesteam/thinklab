/**
 * Discretizer.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: June 01, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ARIES.
 * 
 * ARIES is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ARIES is distributed in the hope that it will be useful,
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
 * FIXME use proper strong typing; fix all the 1985-like code, casts everywhere, 
 * nested types and collections of collections of collections of collections.
 *
 * @copyright 2008 www.integratedmodelling.org
 * @author    Gary Johnson (gwjohnso@uvm.edu)
 * @date      June 01, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.corescience.discretization;

import java.util.Set;
import java.util.HashMap;
import org.integratedmodelling.riskwiz.learning.bndata.IDiscretizer;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;

public class Discretizer implements IDiscretizer {
    private HashMap<String, Projection> projections;
    
    enum RasterType { 
        BYTE,
        DOUBLE,
        STRING		
    }
    
    /**
     * 
     */
    public Discretizer() {
        this.projections = new HashMap<String, Projection>();
    }
    
    public Discretizer(HashMap<String, Projection> proj) {
        this.projections = proj;
    }
    
    public void setProjections(HashMap<String, Projection> proj) {
        this.projections = proj;
    }

    public HashMap<String, Projection> getProjections() {
        return this.projections;
    }
    
    /**
     * 
     */
    public void addVariable(String varName, Projection proj) {
        if (!this.projections.containsKey(varName)) {
            this.projections.put(varName, proj);
        }
    }
    
    public void updateVariable(String varName, Projection proj) {
        if (this.projections.containsKey(varName)) {
            this.projections.put(varName, proj);
        }
    }
    
    public void removeVariable(String varName) {
        if (this.projections.containsKey(varName)) {
            this.projections.remove(varName);
        }
    }
    
    public HashMap<String, String[]> getDomains() throws ThinklabValidationException {
        HashMap<String, String[]> domains = new HashMap<String, String[]>();
        try {
            for (String variable : this.projections.keySet()) {
                Set<Comparable> projectionRange = this.projections.get(variable).getRange();
                String[] categories = new String[projectionRange.size()];
                int i=0;
                for (Comparable elt : projectionRange)
                    categories[i++] = elt.toString();
                domains.put(variable, categories);
            }
        } catch (Exception e) {
            throw new ThinklabValidationException("Malformed discretization/projection"
                                          + " specification.  One of your"
                                          + " variables has a null projection,"
                                          + " or one of your projections has a"
                                          + " null range.");
        }
        return domains;
    }
    
    public String discretize(String varName, Comparable value) {
        return (String) this.projections.get(varName).project(value);
    }
    
    public String[] discretizeAll(String varName, Comparable[] values) {
        Projection proj = this.projections.get(varName);
        String[] discretizedValues = new String[values.length];
        for (int i=0; i<values.length; i++) {
            discretizedValues[i] = (String) proj.project(values[i]);
        }
        return discretizedValues;
    }
    
    /**
     * Pass a variable and its discretized value; get back a null if the projection
     * is a classification, and a proportion of the total range if it maps to a 
     * sortable range. E.g. if the var maps to the first of 5 intervals, this
     * will return 0.2.
     * 
     * TODO this is harder than it sounds and needs to be checked carefully. On
     * the other hand, it's only used to draw pretty pictures at this moment.
	 *
     * @param varName
     * @param value
     * @return
     */
    public Double getNormalizedOrder(String varName, Comparable value) {
    	
        Projection projection = this.projections.get(varName);
    	Double ret = null;
        
        if (projection instanceof RangeProjection) {
        	
        	RangeProjection rp = (RangeProjection) projection;
        	
        	int n = rp.getSortingOrder(value);
        	int t = rp.getNumberOfClasses();
        	
        	ret = new Double((double)(n+1)/(double)t);
        }
        
    	return ret;
    }

}
