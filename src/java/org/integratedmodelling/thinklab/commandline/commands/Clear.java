///**
// * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
// * www.integratedmodelling.org. 
//
//   This file is part of Thinklab.
//
//   Thinklab is free software: you can redistribute it and/or modify
//   it under the terms of the GNU General Public License as published
//   by the Free Software Foundation, either version 3 of the License,
//   or (at your option) any later version.
//
//   Thinklab is distributed in the hope that it will be useful, but
//   WITHOUT ANY WARRANTY; without even the implied warranty of
//   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//   General Public License for more details.
//
//   You should have received a copy of the GNU General Public License
//   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
// */
//package org.integratedmodelling.thinklab.commandline.commands;
//
//import org.integratedmodelling.exceptions.ThinklabException;
//import org.integratedmodelling.thinklab.KnowledgeManager;
//import org.integratedmodelling.thinklab.api.knowledge.ISemanticLiteral;
//import org.integratedmodelling.thinklab.api.runtime.ISession;
//import org.integratedmodelling.thinklab.command.Command;
//import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
//
///**
// * admin command to clear knowledge base (totally or selectively)
// * 
// * @author Ferdinando Villa, Ecoinformatics Collaboratory, UVM
// * 
// */
//public class Clear implements ICommandHandler {
//
//	public ISemanticLiteral execute(Command command, ISession session) throws ThinklabException {
//		// TODO we want arguments and warnings
//
//		if (command.getArgumentAsString("ontology").equals("__all__")) {
//// FIXME
//			//			if (KnowledgeManager.get().getSessionManager() instanceof SingleSessionManager) {
////				((SingleSessionManager) KnowledgeManager.get().getSessionManager()).clear();
////			}
//		} else
//			KnowledgeManager.get().clear(command.getArgumentAsString("ontology"));
//
//		return null;
//	}
//}