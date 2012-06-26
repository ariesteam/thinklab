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
import org.integratedmodelling.thinklab.modelling.datasets.NetCDFDataset;
import org.integratedmodelling.thinklab.query.Queries;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ModelReadWrite {

	static INamespace _ns;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Thinklab.boot();
		URL test1 = ClassLoader.getSystemResource("org/integratedmodelling/thinklab/tests/tql/test1.tql");
		_ns = Thinklab.get().loadFile(test1.toString(), null, null);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		Thinklab.shutdown();
	}
	
	@Test
	public void testKbox() throws Exception {

		IKbox kbox = Thinklab.get().getStorageKboxForNamespace(_ns);
		
		/*
		 * find models that observe rainfall
		 */
		IQuery query =
				Queries.select(NS.MODEL).restrict(NS.HAS_OBSERVABLE, Queries.select("habitat:Rainfall"));
		
		System.out.println("Models observing rainfall:");
		for (ISemanticObject<?> o : kbox.query(query)) {
			System.out.println(o);
		}
	
		/*
		 * TODO assert something
		 */

	}

	@Test
	public void observe1() throws Exception {

		
		IModel model = (IModel) _ns.getModelObject("rainfall-global");
		IContext ctx = (IContext)_ns.getModelObject("puget");

		IObservation result = Thinklab.get().observe(model, ctx);
		
		NetCDFDataset ncds = new NetCDFDataset(result.getContext());
		ncds.write("observe1.nc");
		
		System.out.println(result);

		/*
		 * TODO assert something or compare datasets
		 */
	}

	@Test
	public void observe2() throws Exception {

		
		IModel model = (IModel) _ns.getModelObject("elevation-class");
		IContext ctx = (IContext)_ns.getModelObject("puget");

		IObservation result = Thinklab.get().observe(model, ctx);
		
		NetCDFDataset ncds = new NetCDFDataset(result.getContext());
		ncds.write("observe2.nc");
		
		System.out.println(result);

		/*
		 * TODO assert something or compare datasets
		 */
	}
	
	@Test
	public void observe3() throws Exception {

		
		IModel model = (IModel) _ns.getModelObject("identification");
		IContext ctx = (IContext)_ns.getModelObject("puget");

		IObservation result = Thinklab.get().observe(model, ctx);
		
		NetCDFDataset ncds = new NetCDFDataset(result.getContext());
		ncds.write("observe3.nc");
		
		System.out.println(result);

		/*
		 * TODO assert something or compare datasets
		 */
	}

}
