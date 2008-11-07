/**
 * ThinklabCommandlineVariableEvaluator.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Apr 18, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabWorkflowPlugin.
 * 
 * ThinklabWorkflowPlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabWorkflowPlugin is distributed in the hope that it will be useful,
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
 * @date      Apr 18, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.thinklab.workflow.evaluators;

import java.util.Collection;
import java.util.Map;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.command.CommandDeclaration;
import org.integratedmodelling.thinklab.command.CommandManager;
import org.integratedmodelling.thinklab.command.CommandParser;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.ICommandOutputReceptor;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.thinklab.workflow.exceptions.ThinklabWorkflowException;

import com.opensymphony.workflow.variables.ExternalVariableEvaluator;

/**
 * Evaluates a thinklab command line, parsing arguments and options.
 * 
 * @author Ferdinando
 *
 */
public class ThinklabCommandlineVariableEvaluator implements
		ExternalVariableEvaluator {

	private ISession session;
	private ICommandOutputReceptor outputWriter;

	public ThinklabCommandlineVariableEvaluator(ISession session,
			ICommandOutputReceptor outputReceptor) {
		
		this.session = session;
		this.outputWriter = outputReceptor;
	}

	@Override
	public Object evaluate(String expression, Map<String, Object> context) {

		IValue ret = null;

		try {		
			Command command = CommandParser.parse(expression);
			ret = CommandManager.get().submitCommand(command, null, outputWriter, session);
		} catch (ThinklabException e) {
			throw new ThinklabWorkflowException(e);
		}
		
		return ret;
	}

	@Override
	public String getTypeinfo() {
		return "commandline";
	}

	@Override
	public String[] getRequiredInputVariableNames(String expression) {

		return new String[0];
	}

}
