package org.integratedmodelling.thinklab.geospace.literals;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.list.PolyList;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.annotations.Literal;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.geospace.Geospace;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;

@Literal(
		concept="geospace:PointValue", 
		datatype="http://www.integratedmodelling.org/ks/geospace/geospace.owl#point",
		javaClass=PointValue.class)
public class PointValue extends ShapeValue {

	public PointValue(IConcept c, Point value) {
		super(c, value);
	}

	public PointValue(IConcept c, MultiPoint value) {
		super(c, value);
	}

	public PointValue() {
		// TODO Auto-generated constructor stub
	}

	public PointValue(String s) throws ThinklabValidationException {
		super(s);
	}

	public PointValue(String s, IConcept c)
			throws ThinklabValidationException {
		super(s, c);
		// TODO Auto-generated constructor stub
	}

	public PointValue(Point geometry) {
		super(geometry);
	}

	public PointValue(Geometry geometry, CoordinateReferenceSystem crs) {
		
		/*
		 * TODO check that it's a point or multipoint 
		 */
		super(geometry, crs);
	}

	public PointValue(Point geometry, CoordinateReferenceSystem crs) {
		super(geometry, crs);
	}

	public PointValue(MultiPoint geometry) {
		super(geometry);
	}

	public PointValue(MultiPoint geometry, CoordinateReferenceSystem crs) {
		super(geometry, crs);
	}

	public PointValue(double x1, double y1, double x2, double y2) {
		super(x1, y1, x2, y2);
	}

	public PointValue(String s, CoordinateReferenceSystem crs)
			throws ThinklabValidationException {
		super(s, crs);
	}

	public PointValue(ReferencedEnvelope e) {
		super(e);
	}
	
	@Override
	public IList conceptualize() throws ThinklabException {
		return PolyList.list(
				Thinklab.c("geospace:PointValue"),
				PolyList.list(Thinklab.p("geospace:hasWKB"), getWKB()),
				PolyList.list(Thinklab.p("geospace:hasCRSCode"), Geospace.getCRSIdentifier(getCRS(), true)));
	}

}
