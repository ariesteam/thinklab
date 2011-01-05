package org.integratedmodelling.modelling.context;

import java.util.Map;

import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.internal.IContextTransformation;

/**
 * Created by the "transform" form. Can apply a variety of transformations
 * to a state, optionally using a generalized context filter.
 * 
 * @author ferdinando.villa
 *
 */
public class FilteredTransformation implements IContextTransformation {

	@Override
	public Object transform(Object original, IContext context, int stateIndex,
			Map<?, ?> parameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContextTransformation newInstance() {
		// TODO Auto-generated method stub
		return null;
	}

}
