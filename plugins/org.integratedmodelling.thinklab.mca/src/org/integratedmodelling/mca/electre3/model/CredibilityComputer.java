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
public class CredibilityComputer {

    public CredibilityComputer() {
        credibility = new MatrixModel();
    }

    public void computeCredibility(LinkedList<Criterion> criteria,
            LinkedList<Alternative> alternatives, MatrixModel globalConcordance,
            Hashtable<Criterion, MatrixModel> indiDiscordance) {
        
        credibility = new MatrixModel();
        
        for (Iterator<Alternative> ia1 = alternatives.iterator(); ia1.hasNext(); ) {
            Alternative a1 = ia1.next();
            for (Iterator<Alternative> ia2 = alternatives.iterator(); ia2.hasNext(); ) {
                Alternative a2 = ia2.next();
                AAPair aa = new AAPair(a1, a2);
                double gci = globalConcordance.getValue(aa);
                double strength = 1.0d;
                for (Iterator<Criterion> ic = criteria.iterator(); ic.hasNext(); ) {
                    Criterion c = ic.next();
                    double cdi = indiDiscordance.get(c).getValue(aa);
                    if (cdi > gci) {
                        strength *= (1.0d - cdi) / (1.0d - gci);
                    }
                }
                double s = gci * strength;
                credibility.setValue(a1, a2, s);
            }
        }
    }
    
    public MatrixModel getCredibility() {
        return credibility;
    }
    
    private MatrixModel credibility;

}
