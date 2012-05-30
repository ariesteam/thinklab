package org.integratedmodelling.thinklab.functions.geospace;

import java.util.Map;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.geospace.extents.GridExtent;
import org.integratedmodelling.thinklab.geospace.literals.PolygonValue;
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
		
		PolygonValue shape = null;
		
		/*
		 * resolution is either explicit (x [, y]) or implicit (resolution)
		 */

		/*
		 * TODO unused for now
		 * crs defaults to "EPSG:4326"
		 */
		String crs = "EPSG:4326";
		if (parameters.containsKey("crs")) {
			crs = parameters.get("crs").toString();
		}

		/*
		 * shape is explicit (shape) or relative to a corner or center
		 */
		if (parameters.containsKey("shape")) {
			if (parameters.get("shape") instanceof ShapeValue) {
				shape = (PolygonValue) parameters.get("shape");
			} else {
				shape = new PolygonValue(parameters.get("shape").toString());
			}
		}
		
		
		GridExtent ret = null;
		
		if (parameters.containsKey("resolution")) {
			if (parameters.get("resolution") instanceof Integer) {
				ret = new GridExtent(shape, (Integer)parameters.get("x"));				
			} else {
				ret = new GridExtent(shape, parameters.get("resolution").toString());
			}
		} else if (parameters.containsKey("x") && parameters.containsKey("y")) {
			ret = new GridExtent(shape, (Integer)parameters.get("x"), (Integer)parameters.get("y"));
		}
		
		return ret;
	}

}
