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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author Edwin Boaz Soenaryo
 */
class CordanceComputer {

    public void computeConcordance(LinkedList<Criterion> criteria,
            LinkedList<Alternative> alternatives) {

        indiConcordance = new Hashtable<Criterion, MatrixModel>();
        globalConcordance = new MatrixModel();

        computeIndiCordances(criteria, alternatives, true);
        computeGlobalConcordance(criteria, alternatives);
    }

    public void computeDiscordance(LinkedList<Criterion> criteria,
            LinkedList<Alternative> alternatives) {

        indiDiscordance = new Hashtable<Criterion, MatrixModel>();
        computeIndiCordances(criteria, alternatives, false);
    }

    public Hashtable<Criterion, MatrixModel> getIndiConcordance() {
        return indiConcordance;
    }
    
    public Hashtable<Criterion, MatrixModel> getIndiDiscordance() {
        return indiDiscordance;
    }

    public MatrixModel getGlobalConcordance() {
        return globalConcordance;
    }

    private void computeIndiCordances(LinkedList<Criterion> criteria,
            LinkedList<Alternative> alternatives, boolean concordance) {

        for (Iterator<Criterion> ic = criteria.iterator(); ic.hasNext();) {
            Criterion c = ic.next();
            boolean ascending = c.isAscendingPref();
            MatrixModel icmm = new MatrixModel();

            for (Iterator<Alternative> ia1 = alternatives.iterator(); ia1.hasNext();) {
                Alternative a1 = ia1.next();

                for (Iterator<Alternative> ia2 = alternatives.iterator(); ia2.hasNext();) {
                    Alternative a2 = ia2.next();

                    double ga1 = a1.getCriterionPerformance(c);
                    double pa1 = a1.getPreferenceThreshold(c);

                    double ga2 = a2.getCriterionPerformance(c);
                    double pa2 = a2.getPreferenceThreshold(c);

                    if (concordance) {
                        double qa1 = a1.getIndifferenceThreshold(c);
                        double qa2 = a2.getIndifferenceThreshold(c);
                        double indc = getIndiConcordance(ga1, qa1, pa1, ga2,
                                qa2, pa2, ascending);
                        icmm.setValue(a1, a2, indc);
                    } else {
                        if (!c.isVetoEnabled())
                            icmm.setValue(a1, a2, 0.0);
                        else {

                            double va1 = a1.getVetoThreshold(c);
                            double va2 = a2.getVetoThreshold(c);
                            double indd = getIndiDiscordance(ga1, va1, pa1,
                                    ga2, va2, pa2, ascending);
                            icmm.setValue(a1, a2, indd);
                        }
                    }
                }
            }
            if (concordance)
                indiConcordance.put(c, icmm);
            else
                indiDiscordance.put(c, icmm);
        }
    }

    private double getIndiConcordance(double ga1, double qa1, double pa1,
            double ga2, double qa2, double pa2, boolean ascending) {
        if (ascending) {
            if ((ga2 - ga1) <= qa1) {
                return 1;
            } else if ((ga2 - ga1) > pa1) {
                return 0;
            } else {
                return (ga1 - ga2 + pa1) / (pa1 - qa1);
            }
        } else {
            if ((ga2 - ga1) >= -qa1) {
                return 1;
            } else if ((ga2 - ga1) < -pa1) {
                return 0;
            } else {
                return (ga2 - ga1 + pa1) / (pa1 - qa1);
            }
        }
    }
    
    private double getIndiDiscordance(double ga1, double va1, double pa1,
            double ga2, double va2, double pa2, boolean ascending) {
        if (ascending) {
            if ((ga2 - ga1) <= pa1) {
                return 0;
            } else if ((ga2 - ga1) > va1) {
                return 1;
            } else {
                return (ga2 - ga1 - pa1) / (va1 - pa1);
            }
        } else {
            if ((ga2 - ga1) >= -pa1) {
                return 0;
            } else if ((ga2 - ga1) < -va1) {
                return 1;
            } else {
                return (ga1 - ga2 - pa1) / (va1 - pa1);
            }
        }
    }
    
    private void computeGlobalConcordance(LinkedList<Criterion> criteria,
            LinkedList<Alternative> alternatives) {

        for (Iterator<Alternative> ia1 = alternatives.iterator(); ia1.hasNext();) {
            Alternative a1 = ia1.next();
            for (Iterator<Alternative> ia2 = alternatives.iterator(); ia2.hasNext();) {
                Alternative a2 = ia2.next();
                AAPair aap = new AAPair(a1, a2);

                double totalC = 0.0;
                double totalWeight = 0.0;

                for (Iterator<Criterion> ic = criteria.iterator(); ic.hasNext();) {
                    Criterion c = ic.next();
                    MatrixModel icmm = indiConcordance.get(c);
                    totalC += icmm.getValue(aap) * c.getWeight();
                    totalWeight += c.getWeight();
                }
                double gci = totalC / totalWeight;
                globalConcordance.setValue(a1, a2, gci);
            }
        }
    }

    private MatrixModel globalConcordance;
    private Hashtable<Criterion, MatrixModel> indiConcordance;
    private Hashtable<Criterion, MatrixModel> indiDiscordance;
}
