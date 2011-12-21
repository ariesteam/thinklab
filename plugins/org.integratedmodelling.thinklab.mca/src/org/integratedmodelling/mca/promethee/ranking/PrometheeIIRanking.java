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
package org.integratedmodelling.mca.promethee.ranking;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author Edwin Boaz Soenaryo
 */
public class PrometheeIIRanking implements IRankingStrategy {

    public int[] getRanks(double[] leavingFlows, double[] enteringFlows) {
        int cnt = leavingFlows.length;
        LinkedList<SortableValue> list = new LinkedList<SortableValue>();
        int[] ranks = new int[cnt];
        
        // Calculate net flows
        double[] netFlows = new double[cnt];
        for (int i = 0; i < cnt; i++) {
            netFlows[i] = leavingFlows[i] - enteringFlows[i];
            list.add(new SortableValue(netFlows[i], i));
        }
        Collections.sort(list);
        
        int curRank = 1;
        double curValue = Double.NEGATIVE_INFINITY;
        for (Iterator<SortableValue> il = list.iterator(); il.hasNext(); ) {
            SortableValue sv = il.next();
            ranks[sv.originalPos] = curRank;
            if (curValue != sv.value)
                curRank++;
        }
        
        return ranks;
    }
 
    class SortableValue implements Comparable {
        double value;
        int originalPos;

        public SortableValue(double value, int originalPos) {
            this.value = value;
            this.originalPos = originalPos;
        }
        
        public int compareTo(Object o) {
            if (o instanceof SortableValue) {
                SortableValue so = (SortableValue) o;
                if (so.value > this.value)
                    return 1;
                else if (so.value < this.value)
                    return -1;
                else
                    return 0;
            } else {
                return 0;
            }
        }
    }
    
}
