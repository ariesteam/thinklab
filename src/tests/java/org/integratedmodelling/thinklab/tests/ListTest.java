/**
 * 
 */
package org.integratedmodelling.thinklab.tests;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.integratedmodelling.list.ReferenceList;
import org.integratedmodelling.thinklab.api.lang.IReferenceList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Ferd
 *
 */
public class ListTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testReferenceList() throws Exception {
		
		HashSet<IReferenceList> set = new HashSet<IReferenceList>();
		
		IReferenceList zioPio = ReferenceList.list("zio", "pio");
		IReferenceList dioCan = zioPio.getForwardReference();
		
		set.add(dioCan);
		
		zioPio = (IReferenceList) zioPio.append(dioCan);
		IReferenceList dioCul = ReferenceList.list("dio", "cul", zioPio);
		dioCan.resolve(dioCul);
		
		assertTrue(set.contains(dioCul));
	
		
		System.out.println(zioPio);
	}

	
	@Test
	public void test() {
//		fail("Not yet implemented");
	}

}
