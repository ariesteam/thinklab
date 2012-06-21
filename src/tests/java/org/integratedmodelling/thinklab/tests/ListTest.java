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
		
		IReferenceList refLst = ReferenceList.list("zio", "pio");
		IReferenceList dioCan = refLst.getForwardReference();
		
		set.add(dioCan);
		
		refLst = (IReferenceList) refLst.append(dioCan);
		IReferenceList dioCul = ReferenceList.list("dio", "cul", refLst);
		dioCan.resolve(dioCul);
		
		assertTrue(refLst.nth(2).equals(dioCul));
		assertTrue(refLst.nth(2).equals(dioCan));
		assertTrue(set.contains(dioCul));
	
		System.out.println(refLst.prettyPrint());
	}
		
	
	@Test
	public void testInternalize() throws Exception {

		IReferenceList zioPio1 = ReferenceList.list("zio", "pio");
		IReferenceList zioPio2 = ReferenceList.list("zio", "pio");

		IReferenceList refLst = ReferenceList.list("pit", "pot");
		IReferenceList dioCan = refLst.getForwardReference();		
		refLst = (IReferenceList) refLst.append(dioCan);
		IReferenceList dioCul = ReferenceList.list("blo", "cul", refLst);
		dioCan.resolve(dioCul);

		System.out.println("1. " + zioPio1.prettyPrint());
		System.out.println("2. " + refLst.prettyPrint());
		
		zioPio1.append(refLst);
		zioPio2.append(refLst);
		
		System.out.println("3. " + zioPio1.prettyPrint());
		System.out.println("4. " + zioPio2.prettyPrint());
	}

}
