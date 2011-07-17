package org.integratedmodelling.thinklab.modelling.model;

import java.util.Collection;
import java.util.Set;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.storage.IKBox;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.IScenario;
import org.integratedmodelling.thinklab.api.modelling.metadata.IMetadata;
import org.integratedmodelling.thinklab.api.modelling.observation.IContext;
import org.integratedmodelling.thinklab.api.modelling.observation.IObservationIterator;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.modelling.internal.NamespaceQualified;

public class DefaultAbstractModel extends NamespaceQualified implements IModel {

	
	// --- internal API below ------------------------------------------------
	
	
	// --- public API below --------------------------------------------------
 	
	@Override
	public Set<IConcept> getObservables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMetadata getMetadata() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IConcept getObservableClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IObservationIterator observe(IContext context, IKBox kbox,
			ISession session) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IModel train(IContext context, IKBox kbox, ISession session)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IModel applyScenario(IScenario scenario) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IModel> getDependencies() {
		// TODO Auto-generated method stub
		return null;
	}
	
	// --- private methods ---------------------------------------------------

}
