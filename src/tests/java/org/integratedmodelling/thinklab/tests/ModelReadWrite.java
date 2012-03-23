package org.integratedmodelling.thinklab.tests;

import java.net.URL;

import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.modelling.lang.Namespace;
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
		
		System.out.println(((Namespace)ns).getSemantics().prettyPrint());
		
	}

}
