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
package org.integratedmodelling.mca.electre3.model;

import java.io.Serializable;
import java.util.Hashtable;

/**
 *
 * @author Edwin Boaz Soenaryo
 */
public class Alternative implements Serializable {

	private static final long serialVersionUID = 895818478737969333L;

	public Alternative(String name) {
        setName(name);
        setDescription("");
        performances = new Hashtable<Criterion, Double>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCriterionPerformance(Criterion criterion, double value) {
        performances.put(criterion, value);
    }

    public double getCriterionPerformance(Criterion criterion) {
        if (performances.containsKey(criterion)) {
            return performances.get(criterion);
        } else {
            return 0.0;
        }
    }

    public double getPreferenceThreshold(Criterion criterion) {
        double performanceValue = getCriterionPerformance(criterion);
        return criterion.getPreferenceThreshold(performanceValue);
    }

    public double getIndifferenceThreshold(Criterion criterion) {
        double performanceValue = getCriterionPerformance(criterion);
        return criterion.getIndifferenceThreshold(performanceValue);
    }

    public double getVetoThreshold(Criterion criterion) {
        double performanceValue = getCriterionPerformance(criterion);
        return criterion.getVetoThreshold(performanceValue);
    }

    @Override
    public String toString() {
        return name;
    }
    private String name;
    private String description;
    private Hashtable<Criterion, Double> performances;
}
