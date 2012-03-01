package org.integratedmodelling.thinklab.modelling;

import java.util.Collection;
import java.util.Set;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.lang.model.LanguageElement;
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IObservation;
import org.integratedmodelling.thinklab.api.modelling.IObserver;
import org.integratedmodelling.thinklab.api.modelling.metadata.IMetadata;

/**
 * An Observation is an observer with data. 
 * 
 * @author Ferd
 *
 */
public class ObservationImpl implements IObservation {

	@Override
	public IInstance getObservable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IObserver getObserver() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IObservation> getDependencies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IObservation contextualize(IContext context)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IInstance> getObservables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LanguageElement getLanguageElement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNamespace() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMetadata getMetadata() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContext getContext() {
		// TODO Auto-generated method stub
		return null;
	}



}
