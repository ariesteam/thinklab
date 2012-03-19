package org.integratedmodelling.thinklab.tests;

import junit.framework.Assert;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.kbox.IKbox;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.metadata.Metadata;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class ConceptualizeStoreTest  {
	
	/*
	 * We build complicated graphs of these and try to process and store their
	 * semantics, to stress-test the handling of circular references. Ontology
	 * "thinklab.test" contains the concept and all the properties that will allow
	 * automatic annotation of this class.
	 * 
	 * Fields that have corresponding properties in the same namespace as the
	 * concept will be automatically conceptualized. Alternatively, specific 
	 * fields can be annotated with @Property to select the ones that define
	 * the object's semantics.
	 */
	@Concept("thinklab.test:Person")
	public static class Person {
		
		public String _name;
		public int    _age;
		public Person[] _children;
		public Person[] _parents;
		public Person   _partner;
		
		public Person() {}
		
		public Person(String name, int age, Person[] parents, Person[] children, Person partner) {
			_name = name;
			_age = age;
			_partner = partner;
			_children = children;
			_parents = parents;
		}
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Thinklab.boot();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		Thinklab.shutdown();
	}

	@Test
	public void testSimpleLiterals() throws ThinklabException {
		
		ISemanticObject<?> quaranta = Thinklab.get().annotate(40);
		ISemanticObject<?> stocazzo = Thinklab.get().annotate("stocazzo");
		
		Object oquarant = Thinklab.get().instantiate(quaranta.getSemantics());
		Object ostocazz = Thinklab.get().instantiate(stocazzo.getSemantics());
		
		Assert.assertTrue(oquarant != null && ostocazz != null);
		
		Assert.assertTrue (
				oquarant.equals(40) && 
				ostocazz.equals("stocazzo"));
	}
	
	/*
	 * Create a simple object with a hashmap member and no self-references.
	 */
	@Test
	public void testStoreAcyclic() throws Exception {

		Metadata metadata = new Metadata();
		metadata.put(Metadata.DC_COVERAGE_SPATIAL, new Pair<String,String>("country","france"));
		metadata.put(Metadata.DC_COMMENT, "Stocazzo");
		metadata.put(Metadata.DC_CONTRIBUTOR, "Piccione");
		
		/*
		 * conceptualize the object to a list, then instantiate the list into a "semantic clone" of it, i.e.
		 * an object whose semantics is the same (but whether or not it's a Java-side clone depends on whether 
		 * all fields are semantically relevant in both the object and any dependent ones).
		 *  
		 * Both results are semantic objects, which can return both the semantics and the object conceptualized, in
		 * a lazy fashion.
		 */
		ISemanticObject<?> o = Thinklab.get().annotate(metadata);		
		IList semantics = o.getSemantics();		

		/*
		 * instantiate a new semantic clone and check if it matches.
		 */
		Metadata clone1 = (Metadata) Thinklab.get().instantiate(semantics);
		Assert.assertTrue(
				clone1 instanceof Metadata && 
				clone1.get(Metadata.DC_COVERAGE_SPATIAL) instanceof Pair<?,?> &&
				clone1.get(Metadata.DC_COMMENT).toString().equals("Stocazzo"));
		
		IKbox thinklabKbox = Thinklab.get().requireKbox("thinklab");

		/*
		 * store the object, just like that.
		 */
		long id = thinklabKbox.store(o);
		
		/*
		 * retrieve it back into yet another clone and see if it matches.
		 */
		ISemanticObject<?> clone2 = thinklabKbox.retrieve(id);

		Assert.assertTrue(
				clone2.demote() instanceof Metadata && 
				((Metadata)(clone2.demote())).get(Metadata.DC_COVERAGE_SPATIAL) instanceof Pair<?,?> &&
				((Metadata)(clone2.demote())).get(Metadata.DC_COMMENT).toString().equals("Stocazzo"));

		/*
		 * have a look at the extracted semantics
		 */
		System.out.println(semantics.prettyPrint());
		
	}

	/**
	 * Create, conceptualize, store and retrieve an object structure 
	 * with complex self-references.
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testStoreCyclic() throws Exception {
		
		/*
		 * create a complicated family tree with old Dick as the
		 * patriarch.
		 */
		Person john = new Person("john", 34, null, null, null);
		Person mary = new Person("mary", 29, null, null, john);
		john._partner = mary;
		Person dick = new Person("dick", 71, null, new Person[]{mary}, null);
		Person pipp = new Person("pipp", 12, new Person[]{john, mary}, null, null);
		mary._parents = new Person[]{dick};
		pipp._parents = new Person[]{john, mary};
		john._children = new Person[]{pipp};
		mary._children = new Person[]{pipp};
		mary._partner = john;
				
		/*
		 * just getting out of these two alive is quite the test.
		 */
		IList semantics = Thinklab.get().conceptualize(dick);
		Person clone = (Person) Thinklab.get().instantiate(semantics);
		
		/*
		 * the new object in porco is a clone of dick, made by copying 
		 * his family tree.
		 */
		Assert.assertTrue(clone instanceof Person);
		
		
		// there's quite a bit to check. Just run a few tests.
		Assert.assertTrue(clone._name.equals("dick") && clone._age == 71);
		Assert.assertTrue(clone._parents == null);
		Assert.assertTrue(clone._children != null && clone._children[0]._name.equals("mary"));
		
		Person mr = clone._children[0];
		Assert.assertTrue(mr._parents != null && mr._parents[0]._name.equals("dick"));
		Assert.assertTrue(mr._children != null && mr._children[0]._name.equals("pipp"));
		Assert.assertTrue(mr._partner != null && mr._partner._name.equals("john"));
		
		
		IKbox thinklabKbox = Thinklab.get().requireKbox("thinklab");
		
		/*
		 * store old dick and his clone in the "thinklab" kbox, created as necessary. Looking at
		 * the resulting database with neoclipse can be fun.
		 */
		thinklabKbox.store(dick);
		/*
		 * we should never do that - the object should be immutable, but we know it.
		 */
		clone._name = "dick's clone";
		long id = thinklabKbox.store(clone);

		ISemanticObject dickoid = thinklabKbox.retrieve(id);
		
		/*
		 * have a look at the referenced lists for old Dick and his clone
		 */
		System.out.println(semantics.prettyPrint());
		System.out.println(dickoid.getSemantics().prettyPrint());
	}
}
