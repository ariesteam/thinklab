package org.integratedmodelling.modelling;

import java.util.ArrayList;
import java.util.Collection;

import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.datastructures.IntelligentMap;
import org.integratedmodelling.thinklab.interfaces.query.IConformance;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.utils.Polylist;

/**
 * A model proxy wraps a Model so it can receive configuration through clauses.
 * 
 * Coraggio ciccio, coraggio.
 * @author Ferdinando Villa
 *
 */
public class ModelProxy extends DefaultAbstractModel {

	IModel model = null;
	
	public ModelProxy(IModel model) {
		this.model = model;
	}

	@Override
	public IConcept getCompatibleObservationType(ISession session) {
		return model.getCompatibleObservationType(session);
	}

	@Override
	public IModel getConfigurableClone() {
		return model.getConfigurableClone();
	}

	@Override
	public IConcept getObservable() {
		return model.getObservable();
	}

	@Override
	public boolean isResolved() {
		return model.isResolved();
	}

	@Override
	protected void validateMediatedModel(IModel model)
			throws ThinklabValidationException {
		// TODO
	}

	@Override
	public Polylist buildDefinition(IKBox kbox, ISession session, Collection<Topology> extents) throws ThinklabException {
		return model.buildDefinition(kbox, session, extents);
	}

	@Override
	public Polylist conceptualize() throws ThinklabException {
		return model.conceptualize();
	}

	@Override
	public ModelResult observeInternal(IKBox kbox, ISession session,
			IntelligentMap<IConformance> cp, ArrayList<Topology> extents,
			boolean acceptEmpty)
			throws ThinklabException {
		return ((DefaultAbstractModel)model).observeInternal(kbox, session, cp, extents, acceptEmpty);
	}
	
	@Override
	public String toString() {
		return model.toString() + " (proxied)";
	}

	@Override
	protected void validateSemantics(ISession session) throws ThinklabException {
		((DefaultAbstractModel)model).validateSemantics(session);
	}
	
	@Override
	public void validateConcepts(ISession session) throws ThinklabException {
		((DefaultAbstractModel)model).validateConcepts(session);
	}
}
