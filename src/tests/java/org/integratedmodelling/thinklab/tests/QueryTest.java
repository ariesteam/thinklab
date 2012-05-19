package org.integratedmodelling.thinklab.tests;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.kbox.IKbox;
import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
import org.integratedmodelling.thinklab.query.Query;
import org.integratedmodelling.thinklab.query.operators.Operators;
import org.integratedmodelling.thinklab.tests.data.TestData;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class QueryTest {
	
	public static final String KBOX_NAME = "querytest";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Thinklab.boot();
		populateKbox();
	}

	private static void populateKbox() throws Exception {
		
		IKbox kbox = Thinklab.get().requireKbox(KBOX_NAME);
		
		/*
		 * add some stuff
		 */
		TestData.addFamily(kbox, "dick");
		TestData.addFamily(kbox, "burp");
		TestData.addFamily(kbox, "pork");
		TestData.addFamily(kbox, "putt");

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		IKbox kbox = Thinklab.get().requireKbox(KBOX_NAME);
		kbox.clear();
		Thinklab.shutdown();
	}

	@Test
	public void basicQueries() throws ThinklabException {
		
		IKbox kbox = Thinklab.get().requireKbox(KBOX_NAME);
		
		IQuery allPeople = 
				Query.select(Thinklab.c("thinklab.test:Person"));
		
		IQuery allAdults =
				Query.select("thinklab.test:Person").
					restrict(Thinklab.p("thinklab.test:hasAge"), 
							Operators.compare(18, Operators.GE));
		IQuery allChildren =
				Query.select("thinklab.test:Person").
					restrict(Thinklab.p("thinklab.test:hasAge"), 
							Operators.compare(18, Operators.LT));

		
		int i = 0;
		System.out.println("Everyone:");
		for (ISemanticObject<?> o : kbox.query(allPeople)) {
			System.out.println((i++) + ": " + o);
		}
		
		i=0;
		System.out.println("\nAdults:");
		for (ISemanticObject<?> o : kbox.query(allAdults)) {
			System.out.println((i++) + ": " + o);
		}
		
		i=0;
		System.out.println("\nChildren:");
		for (ISemanticObject<?> o : kbox.query(allChildren)) {
			System.out.println((i++) + ": " + o);
		}
	}

}
