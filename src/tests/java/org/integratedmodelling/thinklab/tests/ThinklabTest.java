package org.integratedmodelling.thinklab.tests;

import junit.framework.TestCase;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.kbox.IKbox;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.metadata.Metadata;
import org.integratedmodelling.utils.StringUtils;

/**
 * The class <code>ThinklabTest</code> contains tests for the class {@link
 * <code>Thinklab</code>}
 *
 * @pattern JUnit Test Case
 *
 * @generatedBy CodePro at 3/12/12 8:06 PM
 *
 * @author Ferd
 *
 * @version $Revision$
 */
public class ThinklabTest extends TestCase {

	/*
	 * We build complicated graphs of these and try to process and store their
	 * semantics, to stress-test the handling of circular references.
	 */
	@Concept("thinklab.test:Person")
	public static class Person {
		
		String _name;
		int    _age;
		Person[] _children;
		Person[] _parents;
		Person   _partner;
		
		// needed by the instantiator. FIXME At the moment it cannot
		// be made private but I shoud fix that.
		public Person() {}
		
		public Person(String name, int age, Person[] parents, Person[] children, Person partner) {
			_name = name;
			_age = age;
			_partner = partner;
			_children = children;
			_parents = parents;
		}
		
		// god this is boring
		private String printIndented(Person p, int indent) {

			String spc = StringUtils.repeat(" ", indent);
			String ret =
					"PERSON: " + _name + " (" + _age + " yo)";
				
			if (_children != null) {
				ret += "Children:";
				for (int i = 0; i < _children.length; i++) {
					ret += "\n" + spc +_children[i]._name;
				}
			}				
			if (_parents != null) {
				ret += "Children:";
				for (int i = 0; i < _parents.length; i++) {
					ret += "\n" + spc +_parents[i]._name;
				}
			}				
			if (_partner != null) {
				ret += "Partner:";
				ret += "\n" + spc +_partner._name;
			}		
			
			return ret;
			
		}
		
		@Override
		public String toString() {
			return printIndented(this, 0);
		}
	}
		
	/**
	 * Construct new test instance
	 *
	 * @param name the test name
	 */
	public ThinklabTest(String name) {
		super(name);
	}

	/**
	 * Launch the test.
	 *
	 * @param args String[]
	 */
	public static void main(String[] args) {
		// add code to run tests here
	}

	/**
	 * Perform pre-test initialization
	 *
	 * @throws Exception
	 *
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		Thinklab.boot();
	}

	@Override
	protected void tearDown() throws Exception {
		Thinklab.shutdown();
		super.tearDown();
	}

	/**
	 * Run the conceptualize test
	 */
	public void testConceptualize() {

		// add test code here
		assertTrue(true);
	}

	public void testSimpleLiterals() throws ThinklabException {
		
		ISemanticObject quaranta = Thinklab.get().annotate(40);
		ISemanticObject stocazzo = Thinklab.get().annotate("stocazzo");
		
//		Object oquarant = Thinklab.get().instantiate(quaranta.getSemantics());
//		Object ostocazz = Thinklab.get().instantiate(stocazzo.getSemantics());
//		
//		assertTrue (oquarant.equals(40) && ostocazz.equals("stocazzo"));
	}
	
	/**
	 * Run the storeLinear test
	 * @throws Exception 
	 */
	public void testStoreAcyclic() throws Exception {

		Metadata metadata = new Metadata();
		metadata.put(Metadata.DC_COVERAGE_SPATIAL,"bestia");
		metadata.put(Metadata.DC_COMMENT, "Stocazzo");
		metadata.put(Metadata.DC_CONTRIBUTOR, "Piccione");
		
		ISemanticObject o = Thinklab.get().annotate(metadata);
		
		IList semantics = o.getSemantics();		
		
		System.out.println(semantics);
		
//		Object porco = Thinklab.get().instantiate(semantics);
//		
//		IKbox kbox = Thinklab.get().requireKbox("thinklab");
//		if (kbox != null) {
//			kbox.store(o);
//		}
//		
//		assertTrue(
//				porco instanceof Metadata && 
//				((Metadata)porco).get(Metadata.DC_COMMENT).toString().equals("Stocazzo"));
	}

	/**
	 * Run the storeAcyclic test
	 * @throws Exception 
	 */
	public void testStoreCyclic() throws Exception {
		
		Person john = new Person("john", 34, null, null, null);
		Person mary = new Person("mary", 29, null, null, john);
		john._partner = mary;
//		Person dick = new Person("dick", 71, null, new Person[]{mary}, null);
//		Person pipp = new Person("pipp", 12, new Person[]{john, mary}, null, null);
//		mary._parents = new Person[]{dick};
//		pipp._parents = new Person[]{john, mary};

		System.out.println(john);
		
		IList semantics = Thinklab.get().conceptualize(john);
		System.out.println(semantics);
//		Object porco = Thinklab.get().instantiate(semantics);
//		System.out.println(porco);
		
//		Thinklab.get().requireKbox("thinklab").store(dick);
		// add test code here
//		assertTrue(false);
	}
}

/*$CPS$ This comment was generated by CodePro. Do not edit it.
 * patternId = com.instantiations.assist.eclipse.pattern.testCasePattern
 * strategyId = com.instantiations.assist.eclipse.pattern.testCasePattern.junitTestCase
 * additionalTestNames = conceptualize, storeLinear, storeAcyclic
 * assertTrue = false
 * callTestMethod = true
 * createMain = true
 * createSetUp = true
 * createTearDown = false
 * createTestFixture = false
 * createTestStubs = false
 * methods = 
 * package = org.integratedmodelling.thinklab.tests
 * package.sourceFolder = thinklab/src/tests/java
 * superclassType = junit.framework.TestCase
 * testCase = ThinklabTest
 * testClassType = org.integratedmodelling.thinklab.Thinklab
 */