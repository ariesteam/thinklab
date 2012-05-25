package org.integratedmodelling.thinklab.tests;

import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.annotations.Property;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.lang.IReferenceList;
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

	@Concept("geospace.features:BiogeographicalRegion")
	public static class RawPlace {
			String name;

		@Property("geospace:hasBoundingBox")
		Polygon boundingBox;
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
		PolygonValue pv = new PolygonValue((Polygon)g);


		RawPlace rplace = new RawPlace();
		rplace.boundingBox = (Polygon)g;
		rplace.name = "BohRaw";
		
		ISemanticObject<?> rbox = Thinklab.get().annotate(rplace);
		Object rpolygon = Thinklab.get().instantiate(rbox.getSemantics());

		System.out.println(rbox.getSemantics().prettyPrint());

		
		Place place = new Place();
		place.boundingBox = pv;
		place.name = "Boh";
		
		ISemanticObject<?> box = Thinklab.get().annotate(place);
		Object polygon = Thinklab.get().instantiate(box.getSemantics());


		
		System.out.println(box.getSemantics().prettyPrint());
	}

	
	@Test
	public void testSpatialLiterals() throws Exception {
		
		/*
		 * promote and demote some polygons, periods, dates etc
		 */
		Geometry g =
			new WKTReader().read("POLYGON ((-180 -60, 180.0000188 -60, 180.0000188 90.0000078, -180 90.0000078, -180 -60))");
		
		ISemanticObject<?> box = Thinklab.get().annotate(g);
		Object polygon = Thinklab.get().instantiate(box.getSemantics());
		
		PolygonValue pv = new PolygonValue((Polygon)g);
		IReferenceList zio = Thinklab.get().conceptualize(pv);
		System.out.println(pv.getSemantics().prettyPrint());
	}
	
}
