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
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.kbox.IKbox;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.metadata.Metadata;
import org.integratedmodelling.thinklab.tests.ThinklabConceptualizeStoreTest.Person;
import org.integratedmodelling.utils.StringUtils;

@ThinklabCommand(name="test",argumentNames="arg",argumentTypes="thinklab:Text", argumentDescriptions="test argument")
public class Test implements ICommandHandler {

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
	
	@Override
	public ISemanticObject execute(Command command, ISession session)
			throws ThinklabException {
				
		Person john = new Person("john", 34, null, null, null);
		Person mary = new Person("mary", 29, null, null, john);
		john._partner = mary;
		Person dick = new Person("dick", 71, null, new Person[]{mary}, null);
		Person pipp = new Person("pipp", 12, new Person[]{john, mary}, null, null);
		mary._parents = new Person[]{dick};
		pipp._parents = new Person[]{john, mary};
		john._children = new Person[]{pipp};
		mary._children = new Person[]{pipp};

		System.out.println(dick);
		
		IList semantics = Thinklab.get().conceptualize(dick);
		System.out.println(semantics);
//		Object porco = Thinklab.get().instantiate(semantics);
//		System.out.println(porco);
		
//		Thinklab.get().requireKbox("thinklab").store(dick);
		
//		Metadata metadata = new Metadata();
//		metadata.put(Metadata.DC_COVERAGE_SPATIAL, new Pair<String,String>("cazzo","bestia"));
//		metadata.put(Metadata.DC_COMMENT, "Stocazzo");
//		metadata.put(Metadata.DC_CONTRIBUTOR, "Piccione");
//		
//		ISemanticObject o = Thinklab.get().annotate(metadata);
//		
//		IList semantics = o.getSemantics();		
//		Object porco = Thinklab.get().instantiate(semantics);
//		
//		session.print(semantics.prettyPrint());
//		session.print("\n" + porco);
//		
//		IKbox kbox = Thinklab.get().requireKbox("thinklab");
//		if (kbox != null) {
//			kbox.store(o);
//		}
//		
//		ISemanticObject quaranta = Thinklab.get().annotate(40);
//		session.print(quaranta.getSemantics().prettyPrint());
//		
//
//		return o;
		
		return null;
	}

	
	public void testKbox() throws ThinklabException {
		
	}
}
