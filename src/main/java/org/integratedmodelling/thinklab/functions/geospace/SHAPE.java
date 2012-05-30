package org.integratedmodelling.thinklab.functions.geospace;

import java.util.Map;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.geospace.extents.ShapeExtent;
import org.integratedmodelling.thinklab.geospace.literals.PolygonValue;
import org.integratedmodelling.thinklab.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.interfaces.annotations.Function;

@Function(id="shape", parameterNames= { "wkt", "wfs", "shape" })
public class SHAPE implements IExpression {

	@Override
	public String getLanguage() {
		// TODO Auto-generated method stub
		return "TQL";
	}

	@Override
	public Object eval(Map<String, Object> parameters) throws ThinklabException {
		
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

		ShapeExtent ret = null;

		/*
		 * shape is explicit
		 */
		if (parameters.containsKey("shape")) {
			if (parameters.get("shape") instanceof ShapeValue) {
				shape = (PolygonValue) parameters.get("shape");
			} else if (parameters.containsKey("wkt")) {
				shape = new PolygonValue(parameters.get("shape").toString());
			}

			ret = new ShapeExtent(shape);
	
		} else if (parameters.containsKey("wfs")) {

			/*
			 * TODO get shape(s) from a WFS server
			 */
		}
		
		return ret;
	}


}
