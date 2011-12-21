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
package org.integratedmodelling.thinklab.workflow;

import java.net.URL;
import java.util.Properties;

import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.workflow.evaluators.IValueVariableEvaluator;
import org.integratedmodelling.thinklab.workflow.evaluators.KBoxVariableEvaluator;
import org.integratedmodelling.thinklab.workflow.evaluators.ThinklabCommandVariableEvaluator;
import org.integratedmodelling.thinklab.workflow.evaluators.ThinklabCommandlineVariableEvaluator;
import org.integratedmodelling.workflow.directors.DebugWorkflow;

import com.opensymphony.workflow.WorkflowException;

public class ThinklabDebugWorkflow extends DebugWorkflow {

	private ISession session;

	public ThinklabDebugWorkflow(String userID, String wfSource, Properties properties, ISession session) {
		
		super(userID, wfSource, properties);
		this.session = session;
		
	}
	
	@Override
	public void registerVariableEvaluators() throws WorkflowException {
		
		registerVariableEvaluator(new IValueVariableEvaluator(session));
		registerVariableEvaluator(new KBoxVariableEvaluator(session));
		registerVariableEvaluator(new ThinklabCommandVariableEvaluator(session));
		registerVariableEvaluator(new ThinklabCommandlineVariableEvaluator(session));
	}

	@Override
	protected URL getOSWorkflowConfiguration() {
		
		URL ret = null;
		try {
			ret = WorkflowPlugin.get().getOSWorkflowConfiguration();
		} catch (ThinklabIOException e) {
			// FIXME shouldn't ignore it, but it's messy
			e.printStackTrace();
		}
		return ret;
	}
	
}
