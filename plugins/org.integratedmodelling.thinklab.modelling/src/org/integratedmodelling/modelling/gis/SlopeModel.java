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
package org.integratedmodelling.modelling.gis;

import java.util.ArrayList;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.modelling.ObservationFactory;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.modelling.model.DefaultAbstractModel;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.utils.Polylist;

public class SlopeModel extends DefaultAbstractModel {

	Integer method = 0;
	Integer units = 0;
	
	public SlopeModel(String namespace) {
		super(namespace);
	}

    @Override
    public void applyClause(String keyword, Object argument)
            throws ThinklabException {

    	String smethod = "";
    	String sunits = "degrees";
    	
        if (keyword.equals(":method")) {
        	
        	smethod = argument.toString();
        
        } else if (keyword.equals(":units")) {
        
        	sunits = argument.toString();
        
        } else {
            super.applyClause(keyword, argument);
        }
    }

	@Override
	public IConcept getCompatibleObservationType(ISession session) {
        return CoreScience.Observation();
	}

	@Override
	public IModel getConfigurableClone() {
        SlopeModel ret = new SlopeModel(namespace);
        ret.copy(this);
        ret.method = this.method;
        ret.units = this.units;
        return ret; 
	}

	@Override
	public Polylist buildDefinition(IKBox kbox, ISession session,
			IContext context, int flags) throws ThinklabException {
		
        ArrayList<Object> arr = new ArrayList<Object>();
        
        arr.add("modeltypes:SlopeAlgorithm");
        
        Polylist ret = Polylist.PolylistFromArrayList(arr);
        ret = ObservationFactory.addReflectedField(ret, "method", method);
        ret = ObservationFactory.addReflectedField(ret, "units", units);

        return addDefaultFields(ret);

	}

	@Override
	public Polylist conceptualize() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void validateSemantics(ISession session) throws ThinklabException {
		// TODO Auto-generated method stub

	}

}
