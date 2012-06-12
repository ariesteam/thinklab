package org.integratedmodelling.thinklab.tests;

import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.annotations.Property;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.geospace.Geospace;
import org.integratedmodelling.thinklab.geospace.literals.PolygonValue;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;

public class ConceptualizeSpatialTest  {
	
	@Concept("geospace.features:AdministrativeRegion")
	public static class Place {
		
		String name;

		@Property("geospace:hasBoundingBox")
		PolygonValue boundingBox;
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Thinklab.boot();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		Thinklab.shutdown();
	}

	@Test
	public void testSpatialMembers() throws Exception {

		/*
		 * promote and demote some polygons, periods, dates etc
		 */
		Geometry g =
			new WKTReader().read("POLYGON ((-180 -60, 180.0000188 -60, 180.0000188 90.0000078, -180 90.0000078, -180 -60))");
		PolygonValue pv = new PolygonValue((Polygon)g, Geospace.get().getDefaultCRS());

		Place place = new Place();
		place.boundingBox = pv;
		place.name = "Boh";
		
		ISemanticObject<?> box = Thinklab.get().annotate(place);
		IList semantics = box.getSemantics();
		System.out.println(semantics.prettyPrint());
		Object polygon = Thinklab.get().instantiate(semantics);
		
		System.out.println(box.getSemantics().prettyPrint());
	}

	
}