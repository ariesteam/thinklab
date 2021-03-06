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
package org.integratedmodelling.persistence.shell;

import org.integratedmodelling.persistence.factory.PersistentStorageFactory;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.SemanticType;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IOntology;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.literals.TextValue;

/**
 * A Thinklab shell command to generate Entity Beans and Hibernate mappings
 * 
 * @author Ioannis N. Athanasiadis
 * @since Feb 5, 2007
 * @version 0.2
 */
public class Generate implements ICommandHandler {

	public IValue execute(Command command, ISession session) {
		SemanticType s1;
		IConcept concept;
		if (command.getArgumentAsString("c1") == null)
			return new TextValue(
					"Cant generate from your arguments. \n USE: \n generate valid:SemanticType");
		try {
			if (command.getArgumentAsString("c1").contains(":")) {
				s1 = new SemanticType(command.getArgumentAsString("c1"));
				concept = KnowledgeManager.get().requireConcept(s1);
				PersistentStorageFactory.createPersistentStorage(concept);
			} else {
				IOntology onto = KnowledgeManager.get()
						.getKnowledgeRepository().requireOntology(
								command.getArgumentAsString("c1"));
				PersistentStorageFactory.createPersistentStorage(onto);
			}
		} catch (ThinklabException e) {
			return new TextValue(
					"Cant generate from your arguments. \n USE: \n generate valid:SemanticType"
							+ e.getStackTrace());
		}
		return new TextValue("Generated HJBeans and HBMaps for "
				+ command.getArgumentAsString("c1"));
	}

}
