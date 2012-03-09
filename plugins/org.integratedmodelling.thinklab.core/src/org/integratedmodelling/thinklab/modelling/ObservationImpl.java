package org.integratedmodelling.thinklab.modelling;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.lang.model.LanguageElement;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
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
@Concept(NS.OBSERVATION)
public class ObservationImpl implements IObservation {

	@Override
	public ISemanticObject getObservable() {
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
	public Set<ISemanticObject> getObservables() {
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

	@Override
	public IProperty getContextProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IObservation> getContextObjects() {
		// TODO Auto-generated method stub
		return null;
	}



}
