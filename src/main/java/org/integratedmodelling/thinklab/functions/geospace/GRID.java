package org.integratedmodelling.thinklab.functions.geospace;

import java.util.Map;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.interfaces.annotations.Function;

@Function(id="grid", parameterNames= 
	{ "x", "y", "shape", "crs", "resolution", "location", 
	  "center", "nwCorner", "swCorner", "neCorner", "seCorner"})
public class GRID implements IExpression {

	@Override
	public String getLanguage() {
		// TODO Auto-generated method stub
		return "TQL";
	}

	@Override
	public Object eval(Map<String, Object> parameters) throws ThinklabValidationException, ThinklabException {
		
		ShapeValue shape = null;
		
		/*
		 * resolution is either explicit (x, y) or implicit (resolution)
		 */
		
		/*
		 * shape is explicit (shape) or relative to a corner or center
		 */
		if (parameters.containsKey("shape")) {
			if (parameters.get("shape") instanceof ShapeValue) {
				shape = (ShapeValue) parameters.get("shape");
			} else {
				shape = new ShapeValue(parameters.get("shape").toString());
			}
		}
		
		/*
		 * crs defaults to "EPSG:4326"
		 */
		
		return null;
	}

}
