package org.integratedmodelling.corescience.transformations;

import org.integratedmodelling.thinklab.interfaces.annotations.DataTransformation;
import org.integratedmodelling.thinklab.transformations.ITransformation;

@DataTransformation(id="log10plus1")
public class Log10Plus1 implements ITransformation {

	@Override
	public double transform(Object value, Object[] parameters) {
		return Math.log10((Double)value + 1.0);
	}

}
