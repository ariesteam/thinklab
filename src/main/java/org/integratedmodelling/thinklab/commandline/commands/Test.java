/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.thinklab.commandline.commands;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.kbox.IKbox;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.modelling.lang.Metadata;
import org.integratedmodelling.thinklab.tests.ConceptualizeStoreTest.Person;

@ThinklabCommand(name="test",argumentNames="arg",argumentTypes="thinklab:Text", argumentDescriptions="test argument")
public class Test implements ICommandHandler {

	
	@Override
	public ISemanticObject<?> execute(Command command, ISession session)
			throws ThinklabException {
				
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
//		assertTrue(
//				clone1 instanceof Metadata && 
//				clone1.get(Metadata.DC_COVERAGE_SPATIAL) instanceof Pair<?,?> &&
//				clone1.get(Metadata.DC_COMMENT).toString().equals("Stocazzo"));
		
		/*
		 * get a neo4j (default) kbox, creating if not there.
		 */
		IKbox kbox = Thinklab.get().requireKbox("thinklab");
			
		/*
		 * store the object, just like that.
		 */
		long id = kbox.store(o);
		
		/*
		 * retrieve it back into yet another clone and see if it matches.
		 */
		ISemanticObject<?> clone2 = kbox.retrieve(id);

//		assertTrue(
//				clone2.getObject() instanceof Metadata && 
//				((Metadata)(clone2.getObject())).get(Metadata.DC_COVERAGE_SPATIAL) instanceof Pair<?,?> &&
//				((Metadata)(clone2.getObject())).get(Metadata.DC_COMMENT).toString().equals("Stocazzo"));

		/*
		 * have a look at the extracted semantics
		 */
		System.out.println(semantics.prettyPrint());
		
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
		semantics = Thinklab.get().conceptualize(dick);
		Person clone = (Person) Thinklab.get().instantiate(semantics);
		
		/*
		 * the new object in porco is a clone of dick, made by copying 
		 * his family tree.
		 */
//		assertTrue(clone instanceof Person);
//		
//		
//		// there's quite a bit to check. Just run a few tests.
//		assertTrue(clone._name.equals("dick") && clone._age == 71);
//		assertTrue(clone._parents == null);
//		assertTrue(clone._children != null && clone._children[0]._name.equals("mary"));
//		
//		Person mr = clone._children[0];
//		assertTrue(mr._parents != null && mr._parents[0]._name.equals("dick"));
//		assertTrue(mr._children != null && mr._children[0]._name.equals("pipp"));
//		assertTrue(mr._partner != null && mr._partner._name.equals("john"));
		
		/*
		 * store old dick and his clone in the "thinklab" kbox, created as necessary. Looking at
		 * the resulting database with neoclipse can be fun.
		 */
		kbox.store(dick);
		/*
		 * we should never do that - the object should be immutable, but we know it.
		 */
		clone._name = "dick's clone";
		id = kbox.store(clone);

		ISemanticObject<?> dickoid = kbox.retrieve(id);
		
		/*
		 * have a look at the referenced lists for old Dick and his clone
		 */
		System.out.println(semantics.prettyPrint());
		System.out.println(dickoid.getSemantics().prettyPrint());
		
		return null;
	}

	
	public void testKbox() throws ThinklabException {
		
	}
}
