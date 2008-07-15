/**
 * List.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of Thinklab.
 * 
 * Thinklab is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Thinklab is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @author    Ioannis N. Athanasiadis (ioannis@athanasiadis.info)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.thinklab.commandline.commands;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.SemanticType;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.commandline.CommandLine;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabUnknownResourceException;
import org.integratedmodelling.thinklab.extensions.CommandHandler;
import org.integratedmodelling.thinklab.interfaces.ICommandOutputReceptor;
import org.integratedmodelling.thinklab.interfaces.IConcept;
import org.integratedmodelling.thinklab.interfaces.IInstance;
import org.integratedmodelling.thinklab.interfaces.IKBox;
import org.integratedmodelling.thinklab.interfaces.IOntology;
import org.integratedmodelling.thinklab.interfaces.IProperty;
import org.integratedmodelling.thinklab.interfaces.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.IRelationship;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.thinklab.kbox.KBoxManager;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.Polylist;
import org.java.plugin.registry.PluginDescriptor;

public class List implements CommandHandler {

	enum listmode {
		DESCRIPTIVE, LIST
	};

	void listOntology(IOntology ont, ICommandOutputReceptor outputWriter)
			throws ThinklabException {

		outputWriter.displayOutput("Listing ontology " + ont);

		outputWriter.displayOutput("\nConcepts:");
		for (IConcept i : ont.getConcepts()) {
			outputWriter.displayOutput("\t" + i.getSemanticType().toString()
					+ ":\t" + i.getLabel() + "\t" + i.getDescription());
		}
		outputWriter.displayOutput("\nProperties:");
		for (IProperty i : ont.getProperties()) {
			outputWriter.displayOutput("\t" + i.getSemanticType().toString()
					+ ":\t" + i.getLabel() + "\t" + i.getDescription());
		}
		outputWriter.displayOutput("\nInstances:");
		for (IInstance i : ont.getInstances()) {
			outputWriter.displayOutput("\t" + i.getSemanticType().toString()
					+ ":\t" + i.getLabel() + "\t" + i.getDescription());
		}

	}

	void listIndividual(IInstance c, listmode mode,
			ICommandOutputReceptor outputWriter) throws ThinklabException {

		if (mode == listmode.DESCRIPTIVE) {
			outputWriter.displayOutput("Instance URI is " + c.getURI());

			outputWriter.displayOutput("list representation:\n"
					+ Polylist.prettyPrint(c.toList(null)));

			outputWriter.displayOutput(c.getDescription());

			if (c.getImplementation() == null) {
				outputWriter.displayOutput("has no implementation");
			} else {
				outputWriter.displayOutput("has implementation of class "
						+ c.getImplementation().getClass().toString());
			}

			for (IRelationship r : c.getRelationships()) {
				outputWriter.displayOutput("  " + r.toString());
			}

		} else if (mode == listmode.LIST) {
			outputWriter.displayOutput(Polylist.prettyPrint(c.toList(null)));
		}
	}

	void listConcept(IConcept c, listmode l, ICommandOutputReceptor outputWriter)
			throws ThinklabException {

		outputWriter.displayOutput("Concept URI is " + c.getURI());

		outputWriter.displayOutput(c.getDescription());

		outputWriter.displayOutput("  Properties:");
		for (IProperty r : c.getProperties()) {
			outputWriter.displayOutput("    " + r);
		}

		outputWriter.displayOutput("  Direct Instances:");
		for (IInstance r : c.getInstances()) {
			outputWriter.displayOutput("    " + r);
		}

		outputWriter.displayOutput("  Restrictions:");
		outputWriter.displayOutput(Polylist.prettyPrint(c.getRestrictions()
				.asList(), 2));

		outputWriter.displayOutput("  Definition:");
		outputWriter.displayOutput(Polylist.prettyPrint(c.getDefinition()
				.asList(), 2));

	}

	void listProperty(IProperty p, listmode l,
			ICommandOutputReceptor outputWriter) {

		outputWriter.displayOutput("Property URI is " + p.getURI());
		outputWriter.displayOutput(p.getDescription());

		outputWriter.displayOutput("  Domain:");
		outputWriter.displayOutput("    " + p.getDomain());

		outputWriter.displayOutput("  Range:");
		for (IConcept c : p.getRange()) {
			outputWriter.displayOutput("    " + c);
		}

	}

	void listKBox(IKBox kbox, String kbname, ICommandOutputReceptor outputWriter)
			throws ThinklabException {

		outputWriter.displayOutput("Listing contents of kBox " + kbname);


		IQueryResult result = kbox.query(null, 0, -1);

		outputWriter.displayOutput("\tID\tClass\tLabel\tDescription");

		if (result.getResultCount() > 0) {

			for (int i = 0; i < result.getResultCount(); i++) {

				outputWriter.displayOutput("\t"
						+ result.getResultField(i, IQueryResult.ID_FIELD_NAME)
						+ "\t"
						+ result.getResultField(i,
								IQueryResult.CLASS_FIELD_NAME)
						+ "\t"
						+ result.getResultField(i,
								IQueryResult.LABEL_FIELD_NAME)
						+ "\t"
						+ result.getResultField(i,
								IQueryResult.DESCRIPTION_FIELD_NAME));

			}
		}

		outputWriter.displayOutput("total: " + result.getResultCount());
	}

	public IValue execute(Command command, ICommandOutputReceptor outputWriter,
			ISession session, KnowledgeManager km) throws ThinklabException {

		String subject = command.getArgumentAsString("subject");

		listmode mode = listmode.LIST;

		if (command.hasOption("lf"))
			mode = listmode.LIST;

		if (subject == null || subject.equals("__NONE__")) {

			outputWriter.displayOutput("Listing session contents: \n");
			int c = 0;

			for (IInstance i : session.listObjects()) {

				outputWriter.displayOutput("\t" + i.getLocalName() + ": "
						+ i.getDirectType());
				c++;
			}

			outputWriter.displayOutput(c + " objects");
			return null;
		}

		if ("ontologies".equals(subject)) {

			for (IOntology o : km.getKnowledgeRepository()
					.retrieveAllOntologies()) {
				outputWriter.displayOutput(o.getConceptSpace() + ":\t"
						+ o.getURI());
			}

		} else if ("plugins".equals(subject)) {

			for (PluginDescriptor pd :
					CommandLine.get().getManager().getRegistry().getPluginDescriptors()) {

				outputWriter.displayOutput(
						pd.getId() + 
						" (" +
						pd.getVersion() + 
						")\t" +
						(CommandLine.get().getManager().isPluginActivated(pd) ?
								"activated" :
								"inactive"));
			}

		} else if ("kboxes".equals(subject)) {

			for (String kb : KBoxManager.get().getInstalledKboxes()) {
				outputWriter.displayOutput(MiscUtilities.getURLBaseName(kb)
						+ ":\t" + kb);
			}

			for (String kb : session.getLocalKBoxes()) {
				outputWriter.displayOutput(kb + " (local)");
			}

		} else {

			if (subject.startsWith("#")) {

				/* looking for an instance in current session */
				IInstance obj = session.retrieveObject(subject.substring(1));

				if (obj == null) {
					outputWriter
							.displayOutput("nothing known about " + subject);
				} else {
					listIndividual(obj, listmode.DESCRIPTIVE, outputWriter);
				}

			} else if (SemanticType.validate(subject)) {

				SemanticType t = new SemanticType(subject);
				IInstance inst = null;
				IConcept conc = null;
				IProperty prop = null;

				/* concept? */
				if ((conc = km.retrieveConcept(t)) != null)
					listConcept(conc, mode, outputWriter);
				/* individual? */
				else if ((inst = km.retrieveInstance(t)) != null)
					listIndividual(inst, mode, outputWriter);
				/* property? */
				else if ((prop = km.retrieveProperty(t)) != null)
					listProperty(prop, mode, outputWriter);
				else
					throw new ThinklabUnknownResourceException(subject);

			} else {

				/* see if subject is an ontology */
				IOntology o = km.getKnowledgeRepository().retrieveOntology(
						subject);
				if (o != null)
					listOntology(o, outputWriter);
				else {
					/* see if it is a kbox URL or name */
					IKBox kbox = session.retrieveKBox(subject);

					if (kbox != null) {
						listKBox(kbox, subject, outputWriter);
					} else {
						outputWriter.displayOutput("nothing known about "
								+ subject);
					}
				}
			}
		}

		return null;
	}

}
