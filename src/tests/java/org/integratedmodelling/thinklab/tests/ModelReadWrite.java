package org.integratedmodelling.thinklab.tests;

import java.net.URL;

import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.modelling.IObservation;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ModelReadWrite {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Thinklab.boot();
		Thinklab.get().requireKbox("thinklab").clear();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		Thinklab.shutdown();
	}

	@Test
	public void test() throws Exception {

		URL test1 = ClassLoader.getSystemResource("org/integratedmodelling/thinklab/tests/tql/test1.tql");
		INamespace ns = Thinklab.get().loadFile(test1.toString(), null, null);
		
		IModel model = (IModel) ns.getModelObject("rainfall");
		IContext ctx = (IContext)ns.getModelObject("puget");
		
		IObservation result = model.observe(ctx);
		
	}

}
