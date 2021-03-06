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

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.SemanticType;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.command.CommandManager;
import org.integratedmodelling.thinklab.commandline.CommandLine;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabUnknownResourceException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.commands.IListingProvider;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IOntology;
import org.integratedmodelling.thinklab.interfaces.knowledge.IProperty;
import org.integratedmodelling.thinklab.interfaces.knowledge.IRelationship;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.kbox.KBoxManager;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.Polylist;
import org.java.plugin.registry.PluginDescriptor;

public class List implements ICommandHandler {

	enum listmode {
		DESCRIPTIVE, LIST
	};

	void listOntology(IOntology ont, ISession session)
			throws ThinklabException {

		session.getOutputStream().println("Listing ontology " + ont);

		session.getOutputStream().println("\nConcepts:");
		for (IConcept i : ont.getConcepts()) {
			session.getOutputStream().println("\t" + i.getSemanticType().toString()
					+ ":\t" + i.getLabel() + "\t" + i.getDescription());
		}
		session.getOutputStream().println("\nProperties:");
		for (IProperty i : ont.getProperties()) {
			session.getOutputStream().println("\t" + i.getSemanticType().toString()
					+ ":\t" + i.getLabel() + "\t" + i.getDescription());
		}
		session.getOutputStream().println("\nInstances:");
		for (IInstance i : ont.getInstances()) {
			session.getOutputStream().println("\t" + i.getSemanticType().toString()
					+ ":\t" + i.getLabel() + "\t" + i.getDescription());
		}

	}

	void listIndividual(IInstance c, listmode mode,
			ISession session) throws ThinklabException {

		if (mode == listmode.DESCRIPTIVE) {
			session.getOutputStream().println("Instance URI is " + c.getURI());

			session.getOutputStream().println("list representation:\n"
					+ Polylist.prettyPrint(c.toList(null)));

			session.getOutputStream().println(c.getDescription());

			if (c.getImplementation() == null) {
				session.getOutputStream().println("has no implementation");
			} else {
				session.getOutputStream().println("has implementation of class "
						+ c.getImplementation().getClass().toString());
			}

			for (IRelationship r : c.getRelationships()) {
				session.getOutputStream().println("  " + r.toString());
			}

		} else if (mode == listmode.LIST) {
			session.getOutputStream().println(Polylist.prettyPrint(c.toList(null)));
		}
	}

	void listConcept(IConcept c, listmode l, ISession session)
			throws ThinklabException {

		session.getOutputStream().println("Concept URI is " + c.getURI());

		session.getOutputStream().println(c.getDescription());

		session.getOutputStream().println("  Properties:");
		for (IProperty r : c.getAllProperties()) {
			session.getOutputStream().println("    " + r);
		}

		session.getOutputStream().println("  Direct Instances:");
		for (IInstance r : c.getInstances()) {
			session.getOutputStream().println("    " + r);
		}

		session.getOutputStream().println("  Restrictions:");
		session.getOutputStream().println(Polylist.prettyPrint(c.getRestrictions()
				.asList(), 2));

		session.getOutputStream().println("  Definition:");
		session.getOutputStream().println(Polylist.prettyPrint(c.getDefinition()
				.asList(), 2));

	}

	void listProperty(IProperty p, listmode l, ISession outputWriter) {

		outputWriter.getOutputStream().println("Property URI is " + p.getURI());
		outputWriter.getOutputStream().println(p.getDescription());

		outputWriter.getOutputStream().println("  Domain:");
		outputWriter.getOutputStream().println("    " + p.getDomain());

		outputWriter.getOutputStream().println("  Range:");
		for (IConcept c : p.getRange()) {
			outputWriter.getOutputStream().println("    " + c);
		}
	}

	void listKBox(IKBox kbox, String kbname, ISession outputWriter)
			throws ThinklabException {

		outputWriter.getOutputStream().println("Listing contents of kBox " + kbname);
		
		IQueryResult result = kbox.query(null, 0, -1);

		if (result.getResultCount() > 0) {

			for (int i = 0; i < result.getResultCount(); i++) {

				outputWriter.getOutputStream().println(
						i  +
						". " + 
						result.getResultAsList(i, null));
			}
		}

		outputWriter.getOutputStream().println("total: " + result.getResultCount());
	}

	public IValue execute(Command command, ISession session) throws ThinklabException {

		String subject = command.getArgumentAsString("subject");
		String item = null;
		
		if (!command.getArgumentAsString("item").equals("__NONE__"))
			item = command.getArgumentAsString("item");

		listmode mode = listmode.LIST;

		if (command.hasOption("lf"))
			mode = listmode.LIST;

		if (subject == null || subject.equals("__NONE__")) {

			session.getOutputStream().println("Listing session contents: \n");
			int c = 0;
			session.getOutputStream().println(c + " objects");
			return null;
		}

		/*
		 * check if we're invoking one of the installed listing providers	
		 */
		IListingProvider prov = 
			item == null ?
				CommandManager.get().getListingProvider(subject) :
				CommandManager.get().getItemListingProvider(subject);
		
		if (prov != null) {
			
			if (item == null) {
				int n = 0;
				for (Object o : prov.getListing()) {
					session.getOutputStream().println("  " + o);
					n++;
				}
				session.getOutputStream().println(n + " " + subject);
				
			} else {
				for (Object o : prov.getSpecificListing(item)) {
					session.getOutputStream().println(o);
				}
			}
			
			return null;
		}
			
				
		/*
		 * default topics
		 */
		if ("ontologies".equals(subject)) {

			for (IOntology o : KnowledgeManager.get().getKnowledgeRepository()
					.retrieveAllOntologies()) {
				session.getOutputStream().println(o.getConceptSpace() + ":\t"
						+ o.getURI());
			}

		} else if ("kboxes".equals(subject)) {

			for (String kb : KBoxManager.get().getInstalledKboxes()) {
				session.getOutputStream().println(MiscUtilities.getURLBaseName(kb)
						+ ":\t" + kb);
			}

			for (String kb : session.getLocalKBoxes()) {
				session.getOutputStream().println(kb + " (local)");
			}

		} else {

			if (subject.startsWith("#")) {

				/* looking for an instance in current session */
				IInstance obj = session.retrieveObject(subject.substring(1));

				if (obj == null) {
					session
							.getOutputStream().println("nothing known about " + subject);
				} else {
					listIndividual(obj, listmode.DESCRIPTIVE, session);
				}

			} else if (SemanticType.validate(subject)) {

				SemanticType t = new SemanticType(subject);
				IInstance inst = null;
				IConcept conc = null;
				IProperty prop = null;

				/* concept? */
				if ((conc = KnowledgeManager.get().retrieveConcept(t)) != null)
					listConcept(conc, mode, session);
				/* individual? */
				else if ((inst = KnowledgeManager.get().retrieveInstance(t)) != null)
					listIndividual(inst, mode, session);
				/* property? */
				else if ((prop = KnowledgeManager.get().retrieveProperty(t)) != null)
					listProperty(prop, mode, session);
				else
					throw new ThinklabUnknownResourceException(subject);

			} else {

				/* see if subject is an ontology */
				IOntology o = KnowledgeManager.get().getKnowledgeRepository().retrieveOntology(
						subject);
				if (o != null)
					listOntology(o, session);
				else {
					/* see if it is a kbox URL or name */
					IKBox kbox = session.retrieveKBox(subject);

					if (kbox != null) {
						listKBox(kbox, subject, session);
					} else {
						session.getOutputStream().println("nothing known about "
								+ subject);
					}
				}
			}
		}

		return null;
	}

}
