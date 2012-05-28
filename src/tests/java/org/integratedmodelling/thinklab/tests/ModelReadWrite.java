package org.integratedmodelling.thinklab.tests;

import java.net.URL;

import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.kbox.IKbox;
import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.modelling.IObservation;
import org.integratedmodelling.thinklab.query.Query;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ModelReadWrite {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Thinklab.boot();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		Thinklab.shutdown();
	}

	@Test
	public void test() throws Exception {

		URL test1 = ClassLoader.getSystemResource("org/integratedmodelling/thinklab/tests/tql/test1.tql");
		INamespace ns = Thinklab.get().loadFile(test1.toString(), null, null);
		
		IModel model = (IModel) ns.getModelObject("rainfall-global");
		IContext ctx = (IContext)ns.getModelObject("puget");

		IKbox kbox = Thinklab.get().getStorageKboxForNamespace(ns);
		
		/*
		 * find models that observe rainfall
		 */
		IQuery query =
				Query.select(NS.MODEL).restrict(NS.HAS_OBSERVABLE, Query.select("habitat:Rainfall"));
		
		System.out.println("Models observing rainfall:");
		for (ISemanticObject<?> o : kbox.query(query)) {
			System.out.println(o);
		}
		
		IObservation result = model.observe(ctx);
		
	}

}
