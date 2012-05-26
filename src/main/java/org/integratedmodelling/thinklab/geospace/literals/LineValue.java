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

import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.LineString;

@Literal(
		concept="geospace:LineValue", 
		datatype="http://www.integratedmodelling.org/ks/geospace/geospace.owl#line",
		javaClass=LineValue.class)
public class LineValue extends ShapeValue {

	public LineValue(IConcept c, LineString value) {
		super(c, value);
	}

	public LineValue(IConcept c, MultiLineString value) {
		super(c, value);
	}

	public LineValue() {
		// TODO Auto-generated constructor stub
	}

	public LineValue(String s) throws ThinklabValidationException {
		super(s);
	}

	public LineValue(String s, IConcept c)
			throws ThinklabValidationException {
		super(s, c);
		// TODO Auto-generated constructor stub
	}

	public LineValue(LineString geometry) {
		super(geometry);
	}

	public LineValue(LineString geometry, CoordinateReferenceSystem crs) {
		super(geometry, crs);
	}

	public LineValue(MultiLineString geometry) {
		super(geometry);
	}

	public LineValue(MultiLineString geometry, CoordinateReferenceSystem crs) {
		super(geometry, crs);
	}

	public LineValue(double x1, double y1, double x2, double y2) {
		super(x1, y1, x2, y2);
	}

	public LineValue(String s, CoordinateReferenceSystem crs)
			throws ThinklabValidationException {
		super(s, crs);
	}

	public LineValue(ReferencedEnvelope e) {
		super(e);
	}
	
	@Override
	public IList conceptualize() throws ThinklabException {
		return PolyList.list(
				Thinklab.c("geospace:LineValue"),
				PolyList.list(Thinklab.p("geospace:hasWKB"), getWKB()),
				PolyList.list(Thinklab.p("geospace:hasCRSCode"), Geospace.getCRSIdentifier(getCRS(), true)));
	}

}
