/**
 * 
 */
package org.integratedmodelling.thinklab.tests;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.integratedmodelling.list.ReferenceList;
import org.integratedmodelling.thinklab.api.lang.IReferenceList;
import org.junit.Test;

/**
 * @author Ferd
 *
 */
public class ListTest {

	@Test
	public void testReferenceList() throws Exception {
		
		HashSet<IReferenceList> set = new HashSet<IReferenceList>();
		
		IReferenceList zioPio = ReferenceList.list("zio", "pio");
		IReferenceList dioCan = zioPio.getForwardReference();
		
		set.add(dioCan);
		
		zioPio = (IReferenceList) zioPio.append(dioCan);
		IReferenceList dioCul = ReferenceList.list("dio", "cul", zioPio);
		dioCan.resolve(dioCul);
		
		assertTrue(zioPio.nth(2).equals(dioCul));
		assertTrue(zioPio.nth(2).equals(dioCan));
		assertTrue(set.contains(dioCul));
	
		System.out.println(zioPio.prettyPrint());
	}

}
