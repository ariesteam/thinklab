package org.integratedmodelling.thinklab.tests.data;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.knowledge.kbox.IKbox;

public class TestData {

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
		
		@Override
		public String toString() {
			return "[" + _name + " age: " + _age + "]";
		}
	}
	
	public static void addFamily(IKbox kbox, String patriarchName) throws ThinklabException {
		
		/*
		 * create a complicated family tree with old Dick as the
		 * patriarch.
		 */
		TestData.Person john = 
				new TestData.Person(patriarchName + "sDaughtersHusband", 34, null, null, null);
		TestData.Person mary = 
				new TestData.Person(patriarchName + "sDaughter", 29, null, null, john);
		
		john._partner = mary;
		TestData.Person dick = 
				new TestData.Person(patriarchName, 71, null, new TestData.Person[]{mary}, null);
		TestData.Person pipp = 
				new TestData.Person(patriarchName + "sNiece", 12, new TestData.Person[]{john, mary}, null, null);
		mary._parents = new TestData.Person[]{dick};
		pipp._parents = new TestData.Person[]{john, mary};
		john._children = new TestData.Person[]{pipp};
		mary._children = new TestData.Person[]{pipp};
		mary._partner = john;
				
		kbox.store(dick);
	}

}
