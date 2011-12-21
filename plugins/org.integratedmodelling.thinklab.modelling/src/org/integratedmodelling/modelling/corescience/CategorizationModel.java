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
package org.integratedmodelling.modelling.corescience;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.modelling.model.DefaultDynamicAbstractModel;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.utils.Polylist;

public class CategorizationModel extends DefaultDynamicAbstractModel {

	public CategorizationModel(String namespace) {
		super(namespace);
		// TODO Auto-generated constructor stub
	}


	@Override
	public String toString() {
		return ("categorization");
	}

	
	public void setCategories(Object categories) {
		System.out.println("categories: " + categories);
		// TODO do something
	}
	
	@Override
	public void validateMediatedModel(IModel model)
			throws ThinklabValidationException {
		super.validateMediatedModel(model);
		// a ranking can mediate another ranking or a measurement
		if (! (model instanceof CategorizationModel)) {
			throw new ThinklabValidationException("categorization models can only mediate other categorizations");
		}
	}

	@Override
	protected Object validateState(Object state)
			throws ThinklabValidationException {
		return state instanceof String ? state : state.toString();
	}

	@Override
	public IConcept getCompatibleObservationType(ISession session) {
		return CoreScience.Categorization();
	}

	@Override
	public IModel getConfigurableClone() {
		CategorizationModel ret = new CategorizationModel(namespace);
		ret.copy(this);
		// TODO copy categories when we store them
		return ret;
	}

	@Override
	public Polylist buildDefinition(IKBox kbox, ISession session, IContext context, int flags) throws ThinklabException {

		Polylist def = Polylist.listNotNull(
				CoreScience.CATEGORIZATION,
				Polylist.list(CoreScience.HAS_FORMAL_NAME, getLocalFormalName()),				
				/*
				 * TODO add scale attributes, possibly units
				 */
				(isMediating() ? 
						null :
						Polylist.list(
								CoreScience.HAS_OBSERVABLE,
								Polylist.list(getObservableClass()))));
		
		return addDefaultFields(def);
	}

	@Override
	public Polylist conceptualize() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected void validateSemantics(ISession session) throws ThinklabException {
		// nothing to do	
	}

}
