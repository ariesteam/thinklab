package org.integratedmodelling.thinklab.tests;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.kbox.IKbox;
import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
import org.integratedmodelling.thinklab.query.Query;
import org.integratedmodelling.thinklab.query.operators.Operators;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class QueryTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Thinklab.boot();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		Thinklab.shutdown();
	}

	@Test
	public void queryAll() throws ThinklabException {
		
		IKbox kbox = Thinklab.get().requireKbox("thinklab");
		
		/*
		 * query everything
		 */	
		int i = 0;
		for (ISemanticObject<?> o : kbox.query(null)) {
			System.out.println((i++) + ": " + o);
		}
		
		IQuery allPeople = 
				Query.select(Thinklab.c("thinklab.test:Person"));
		
		IQuery allAdults =
				Query.select(Thinklab.c("thinklab.test:Person")).
					restrict(Thinklab.p("thinklab.test:hasAge"), Operators.compare(18, Operators.GE));
		
		/*
		 * query all persons
		 */	
		i = 0;
		for (ISemanticObject<?> o : kbox.query(allPeople)) {
			System.out.println((i++) + ": " + o);
		}
		
		for (ISemanticObject<?> o : kbox.query(allAdults)) {
			System.out.println((i++) + ": " + o);
		}
	}

}
