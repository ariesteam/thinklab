package org.integratedmodelling.corescience.transformations;

import org.integratedmodelling.thinklab.interfaces.annotations.DataTransformation;
import org.integratedmodelling.thinklab.transformations.ITransformation;

@DataTransformation(id="log10")
public class Log10 implements ITransformation {

	@Override
	public double transform(Object value, Object[] parameters) {
		return Math.log10((Double)value);
	}

}
