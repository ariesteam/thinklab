///**
// * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
// * www.integratedmodelling.org. 
//
//   This file is part of Thinklab.
//
//   Thinklab is free software: you can redistribute it and/or modify
//   it under the terms of the GNU General Public License as published
//   by the Free Software Foundation, either version 3 of the License,
//   or (at your option) any later version.
//
//   Thinklab is distributed in the hope that it will be useful, but
//   WITHOUT ANY WARRANTY; without even the implied warranty of
//   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//   General Public License for more details.
//
//   You should have received a copy of the GNU General Public License
//   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
// */
//package org.integratedmodelling.thinklab.modelling.model.implementation;
//
//import java.util.ArrayList;
//
//import org.integratedmodelling.exceptions.ThinklabException;
//import org.integratedmodelling.thinklab.api.knowledge.query.IConformance;
//import org.integratedmodelling.thinklab.api.knowledge.query.IRestriction;
//import org.integratedmodelling.thinklab.api.modelling.IContext;
//import org.integratedmodelling.thinklab.api.modelling.IExtent;
//import org.integratedmodelling.thinklab.api.modelling.INamespace;
//import org.integratedmodelling.thinklab.api.modelling.IObservation;
//import org.integratedmodelling.thinklab.api.runtime.ISession;
//import org.integratedmodelling.thinklab.constraint.Constraint;
//import org.integratedmodelling.thinklab.constraint.Restriction;
//import org.integratedmodelling.thinklab.implementations.operators.Operator;
//
//public abstract class AbstractStateModel extends DefaultAbstractModel  {
//
//	public AbstractStateModel(INamespace ns) {
//		super(ns);
//	}
//
//	public boolean isResolved() {
//		return _mediated.size() > 0 || (_stateExpressions.size() > 0 && _dependencies.size() == 0);
//	}
//	
//	/**
//	 * Generate a query that will select the requested observation type and
//	 * restrict the observable to the specifications we got for this model. Use
//	 * passed conformance table to define the observable constraint. Optionally
//	 * add in an extent restriction.
//	 * 
//	 * @param extentRestriction
//	 * @param conformancePolicies
//	 * @param session
//	 * @param context
//	 *            .getTopologies()
//	 * @return
//	 * @throws ThinklabException
//	 */
//	public Constraint generateObservableQuery(IConformance conformance, ISession session,
//			IContext context) throws ThinklabException {
//
//		Constraint c = new Constraint(this.getCompatibleObservationType());
////		c = c.restrict(new Restriction(
////							ModelTypes.P_HAS_OBSERVABLE.getProperty(Thinklab.get()), 
////							conformance.getQuery(_observable)));
//
//		if (context.getExtents().size() > 0) {
//
//			ArrayList<IRestriction> er = new ArrayList<IRestriction>();
//			for (IExtent o : context.getExtents()) {
//				IRestriction r = o.getConstraint(Operator.INTERSECTS);
//				if (r != null)
//					er.add(r);
//			}
//
//			if (er.size() > 0) {
//				c = c.restrict(er.size() == 1 ? er.get(0) : Restriction.AND(er
//						.toArray(new Restriction[er.size()])));
//			}
//		}
//
//		
//		return c;
//	}
//	
//
//	private String getCompatibleObservationType() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	protected int addMediated(IObservation o) {
//
//		return 0;
//	}
//	
//	
//}
