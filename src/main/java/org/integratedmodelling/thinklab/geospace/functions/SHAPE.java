package org.integratedmodelling.thinklab.geospace.functions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.api.project.IProject;
import org.integratedmodelling.thinklab.geospace.Geospace;
import org.integratedmodelling.thinklab.geospace.coverage.CoverageFactory;
import org.integratedmodelling.thinklab.geospace.coverage.VectorCoverage;
import org.integratedmodelling.thinklab.geospace.extents.ShapeExtent;
import org.integratedmodelling.thinklab.geospace.literals.PolygonValue;
import org.integratedmodelling.thinklab.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.interfaces.annotations.Function;
import org.integratedmodelling.utils.MiscUtilities;

@Function(id="shape", parameterNames= { "wkt", "url", "service", "shape", "crs", "id", "filter" })
public class SHAPE implements IExpression {
	
	IProject   project;
	
	@Override
	public Object eval(Map<String, Object> parameters) throws ThinklabException {
		
		ShapeValue shape = null;
		
		/*
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
		if (parameters.get("shape") instanceof ShapeValue) {
			shape = (PolygonValue) parameters.get("shape");
		} else if (parameters.containsKey("wkt")) {
			shape = new PolygonValue(parameters.get("wkt").toString());
		}

		if (shape != null) {
			ret = new ShapeExtent(shape);
		} else if (parameters.containsKey("service") && parameters.containsKey("id")) {

			/*
			 * get shape(s) from WFS source - shapefile or other
			 */
			String id = parameters.get("id").toString();
			String filter = parameters.containsKey("filter") ? parameters.get("filter").toString() : null;
			Properties p = new Properties();
			if (filter != null) {
				p.setProperty(CoverageFactory.CQL_FILTER_PROPERTY, filter);
			}
			
			/*
			 * TODO other properties - also in the "url" case
			 */
			
			URL url = MiscUtilities.getURLForResource(parameters.get("service").toString());
			if (url != null) {
				VectorCoverage vc = (VectorCoverage) CoverageFactory.getCoverage(url, id, p);
				for (ShapeValue sh : vc) {
					return sh.transform(Geospace.getCRSFromID(crs));
				}
			}
			
		} else if (parameters.containsKey("url")) {
			
			URL url = null;
			
			if (this.project != null && this.project.findResource(parameters.get("url").toString()) != null) {
				try {
					url = this.project.findResource(parameters.get("url").toString()).toURI().toURL();
				} catch (MalformedURLException e) {
					throw new ThinklabIOException(e);
				}
			} else {
				url = MiscUtilities.getURLForResource(parameters.get("url").toString());
			}
			
			if (url != null) {
				VectorCoverage vc = (VectorCoverage) CoverageFactory.getCoverage(url, null, null);
				for (ShapeValue sh : vc) {
					return sh.transform(Geospace.getCRSFromID(crs));
				}
			}
		}
		
		return ret;
	}

	@Override
	public void setProjectContext(IProject project) {
		this.project = project;
	}


}
