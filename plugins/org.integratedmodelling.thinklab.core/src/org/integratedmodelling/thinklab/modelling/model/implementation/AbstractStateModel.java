package org.integratedmodelling.thinklab.modelling.model.implementation;

import java.util.ArrayList;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.query.IConformance;
import org.integratedmodelling.thinklab.api.knowledge.query.IRestriction;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.modelling.ModelTypes;
import org.integratedmodelling.thinklab.api.modelling.observation.IContext;
import org.integratedmodelling.thinklab.api.modelling.observation.IExtent;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.constraint.DefaultConformance;
import org.integratedmodelling.thinklab.constraint.Restriction;
import org.integratedmodelling.thinklab.implementations.operators.Operator;
import org.integratedmodelling.thinklab.interfaces.knowledge.datastructures.IntelligentMap;
import org.integratedmodelling.thinklab.modelling.internal.StateModel;

public abstract class AbstractStateModel extends DefaultAbstractModel implements StateModel {

	public AbstractStateModel(INamespace ns) {
		super(ns);
	}

	public boolean isResolved() {
		return _mediated.size() > 0 || (_stateExpressions.size() > 0 && _dependencies.size() == 0);
	}
	
	/**
	 * Generate a query that will select the requested observation type and
	 * restrict the observable to the specifications we got for this model. Use
	 * passed conformance table to define the observable constraint. Optionally
	 * add in an extent restriction.
	 * 
	 * @param extentRestriction
	 * @param conformancePolicies
	 * @param session
	 * @param context
	 *            .getTopologies()
	 * @return
	 * @throws ThinklabException
	 */
	public Constraint generateObservableQuery(
			IntelligentMap<IConformance> conformancePolicies, ISession session,
			IContext context) throws ThinklabException {

		Constraint c = new Constraint(this.getCompatibleObservationType());

		IConformance conf = conformancePolicies == null ? new DefaultConformance()
				: conformancePolicies.get(_observable.getDirectType());

		c = c.restrict(new Restriction(
							ModelTypes.P_HAS_OBSERVABLE.getProperty(Thinklab.get()), 
							conf.getQuery(_observable)));

		if (context.getExtents().size() > 0) {

			ArrayList<IRestriction> er = new ArrayList<IRestriction>();
			for (IExtent o : context.getExtents()) {
				IRestriction r = o.getConstraint(Operator.INTERSECTS);
				if (r != null)
					er.add(r);
			}

			if (er.size() > 0) {
				c = c.restrict(er.size() == 1 ? er.get(0) : Restriction.AND(er
						.toArray(new Restriction[er.size()])));
			}
		}

		
		return c;
	}
	
}
