/**
 * ThinklabCommandFunction.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Mar 10, 2008
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
 * @date      Mar 10, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.thinklab.workflow.functions;

import java.util.HashMap;
import java.util.Map;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.command.CommandDeclaration;
import org.integratedmodelling.thinklab.command.CommandManager;
import org.integratedmodelling.thinklab.command.base.ShellCommandOutputReceptor;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.ICommandOutputReceptor;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.thinklab.value.Value;
import org.integratedmodelling.thinklab.workflow.WorkflowPlugin;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.WorkflowException;

public class ThinklabCommandFunction implements FunctionProvider {

	@Override
	public void execute(Map transientVars, Map args, PropertySet ps)
			throws WorkflowException {
		
		String aname = args.get(WorkflowPlugin.ARG_COMMAND_NAME).toString();
		IValue ret = null;
		ICommandOutputReceptor outputWriter = 
			(ICommandOutputReceptor) ps.getObject(WorkflowPlugin.ARG_COMMAND_OUTPUT_WRITER);
		ISession session = 
			(ISession) ps.getObject(WorkflowPlugin.ARG_THINKLAB_SESSION);
		
		if (outputWriter == null) {
			outputWriter = new ShellCommandOutputReceptor();
		}
		
		/* 
		 * FIXME: this should supplement the doAction() when called through WorkflowDirector, and
		 * be properly positioned at the end of the pre-functions.
		 * 
		 * if the action has the name of an installed command and we have valid values for its
		 * arguments, run the command and remember the result.
		 */
		if (CommandManager.get().hasCommand(aname)) {
			
			CommandDeclaration decl = CommandManager.get().getDeclarationForCommand(aname);

			HashMap<String, IValue> minputs = new HashMap<String, IValue>();
			HashMap<String, IValue> oinputs = new HashMap<String, IValue>();
			
			/* get arguments from args, then registers, then transients, complain if one 
			 * isn't available */
			for (String ma : decl.getMandatoryArgumentNames()) {
				
				Object oa = args.get(ma);
				if (oa == null) {
					oa = transientVars.get(ma);
				}
				if (oa == null) {
					oa = ps.getObject(ma);
				}
				
				if (oa == null) {
					throw new WorkflowException(
							"workflow: argument " + 
							ma + 
							" required by action " + 
							aname + 
							"not found in workflow");
				}
				
				try {
					minputs.put(ma, Value.getValueForObject(oa));
				} catch (ThinklabException e) {
					throw new WorkflowException(e);
				}
				
			}
			
			for (String ma : decl.getOptionalArgumentNames()) {

				Object oa = args.get(ma);
				if (oa == null) {
					oa = transientVars.get(ma);
				}
				if (oa == null) {
					oa = ps.getObject(ma);
				}

				if (oa != null) {
					try {
						oinputs.put(ma, Value.getValueForObject(oa));
					} catch (ThinklabException e) {
						throw new WorkflowException(e);
					}
				}
			}
			
			try {
				Command cmd = new Command(decl, minputs, oinputs);
				ret = CommandManager.get().submitCommand(cmd, outputWriter, session);
			} catch (ThinklabException e) {
				throw new WorkflowException(e);
			}
			
			/* set result of action into transients as actionname_result */
			if (ret != null) {
				ps.setObject(aname + "_result", ret);
			}
		}
	}

}
