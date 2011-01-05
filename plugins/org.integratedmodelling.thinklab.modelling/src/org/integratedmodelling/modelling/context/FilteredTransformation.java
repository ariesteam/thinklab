package org.integratedmodelling.modelling.context;

import java.util.Map;

import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.internal.IContextTransformation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

/**
 * Created by the "transform" form. Can apply a variety of transformations
 * to a state, optionally using a generalized context filter.
 * 
 * @author ferdinando.villa
 *
 */
public class FilteredTransformation implements IContextTransformation {

	private IConcept concept;
	private Object value;

	public FilteredTransformation(IConcept concept, Object value) {
		this.concept = concept;
		this.value = value;
	}
	
	@Override
	public Object transform(Object original, IContext context, int stateIndex,
			Map<?, ?> parameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContextTransformation newInstance() {
		return new FilteredTransformation(concept, value);
	}

	@Override
	public IConcept getObservableClass() {
		return this.concept;
	}

}
