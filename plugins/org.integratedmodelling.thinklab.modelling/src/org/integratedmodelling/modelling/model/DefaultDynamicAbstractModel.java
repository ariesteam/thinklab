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
package org.integratedmodelling.modelling.model;


import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.modelling.ObservationFactory;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.time.TimePlugin;
import org.integratedmodelling.utils.Polylist;

import clojure.lang.IFn;

/**
 * Just adds handling of state and derivative clauses followed by executable code.
 * 
 * @author Ferdinando Villa
 *
 */
public abstract class DefaultDynamicAbstractModel extends DefaultStatefulAbstractModel {

	public DefaultDynamicAbstractModel(String namespace) {
		super(namespace);
		// TODO Auto-generated constructor stub
	}

	protected Object dynSpecs = null;
	public enum language {
		CLOJURE,
		MVEL
	};
	
	protected language lang = null;
	protected Object changeSpecs = null;
	protected Object derivativeSpecs = null;

	private void setLanguage(Object arg) throws ThinklabValidationException {
		
		language l = null;
		if (arg instanceof IFn) {
			l = language.CLOJURE;
		} else if (arg instanceof String) {
			l = language.MVEL;
		} else
			throw new ThinklabValidationException("invalid expression in model: " + arg);
		
		if (this.lang != null && this.lang != l) {
			throw new ThinklabValidationException("cannot mix expression languages in model specification");			
		}
		
		this.lang = l;
	}
	
	@Override
	public void applyClause(String keyword, Object argument) throws ThinklabException {
		
		if (keyword.equals(":state") && (argument instanceof IFn)) {
			this.dynSpecs = argument;
			setLanguage(argument);
		} else if (keyword.equals(":rate")) {
			this.derivativeSpecs = argument;
			setLanguage(argument);
		} else if (keyword.equals(":probability")) {
			
			// TODO accept bayesian node form
			
		} else if (keyword.equals(":update")) {
			this.changeSpecs = argument;
			setLanguage(argument);
		} else super.applyClause(keyword, argument);
	}
	
	@Override
	public boolean isResolved() {
		return super.isResolved() || this.dynSpecs != null;
	}

	@Override
	protected void copy(DefaultStatefulAbstractModel model) {
		super.copy(model);
		lang = ((DefaultDynamicAbstractModel)model).lang;
		dynSpecs = ((DefaultDynamicAbstractModel)model).dynSpecs;
	}

	protected Polylist addImplicitExtents(Polylist list, IContext context) throws ThinklabException {
							
		if (context == null)
			return list;
		
		/*
		 * adopt them all unless there is a value statement; if time, adopt it
		 * anyway if we have change statements.		
		 */		
		for (IExtent t : context.getExtents()) {		
			if (state == null) {
				list = ObservationFactory.addExtent(list, t.conceptualize());				
			} else if (t.getObservableClass().is(TimePlugin.get().TimeObservable()) &&
					(changeSpecs != null || derivativeSpecs != null)) {
						list = ObservationFactory.addExtent(list, t.conceptualize());
			}
		}
		
		return list;
	}

	@Override
	public void validateMediatedModel(IModel model) throws ThinklabValidationException {
		super.validateMediatedModel(model);
	}
	
}