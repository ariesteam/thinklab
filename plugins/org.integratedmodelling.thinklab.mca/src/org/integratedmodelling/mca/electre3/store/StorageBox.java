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
package org.integratedmodelling.mca.electre3.store;

import java.io.Serializable;
import java.util.LinkedList;

import org.integratedmodelling.mca.electre3.model.Alternative;
import org.integratedmodelling.mca.electre3.model.Criterion;

/**
 *
 * @author Edwin Boaz Soenaryo
 */
public class StorageBox implements Serializable {
	
	private static final long serialVersionUID = 3117826326217824360L;
	public StorageBox() {
        alternatives = null;
        criteria = null;
    }

    public LinkedList<Alternative> getAlternatives() {
        return alternatives;
    }

    public void setAlternatives(LinkedList<Alternative> alternatives) {
        this.alternatives = alternatives;
    }

    public LinkedList<Criterion> getCriteria() {
        return criteria;
    }

    public void setCriteria(LinkedList<Criterion> criteria) {
        this.criteria = criteria;
    }
    
    private LinkedList<Alternative> alternatives;
    private LinkedList<Criterion> criteria;
    
}
