package org.integratedmodelling.mca.electre3.model;

import java.io.Serializable;
import java.util.Hashtable;

/**
 *
 * @author Edwin Boaz Soenaryo
 */
public class Alternative implements Serializable {

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
