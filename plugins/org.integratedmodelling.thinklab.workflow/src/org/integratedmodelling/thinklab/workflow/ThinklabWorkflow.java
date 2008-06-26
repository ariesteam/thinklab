package org.integratedmodelling.thinklab.workflow;

import java.net.URL;
import java.util.Properties;

import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.interfaces.ICommandOutputReceptor;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.workflow.evaluators.IValueVariableEvaluator;
import org.integratedmodelling.thinklab.workflow.evaluators.KBoxVariableEvaluator;
import org.integratedmodelling.thinklab.workflow.evaluators.ThinklabCommandVariableEvaluator;
import org.integratedmodelling.thinklab.workflow.evaluators.ThinklabCommandlineVariableEvaluator;
import org.integratedmodelling.workflow.WorkflowAction;
import org.integratedmodelling.workflow.WorkflowDirector;
import org.integratedmodelling.workflow.WorkflowStep;

import com.opensymphony.workflow.WorkflowException;

public class ThinklabWorkflow extends WorkflowDirector {

	private ISession session;
	private ICommandOutputReceptor outputReceptor;

	public ThinklabWorkflow(String userID, String wfSource, Properties properties, ISession session, ICommandOutputReceptor outputReceptor) {
		
		super(userID, wfSource, properties);
		this.session = session;
		this.outputReceptor = outputReceptor;
		
	}

	@Override
	public void registerVariableEvaluators() throws WorkflowException {
		
		registerVariableEvaluator(new IValueVariableEvaluator(session, outputReceptor));
		registerVariableEvaluator(new KBoxVariableEvaluator(session, outputReceptor));
		registerVariableEvaluator(new ThinklabCommandVariableEvaluator(session, outputReceptor));
		registerVariableEvaluator(new ThinklabCommandlineVariableEvaluator(session, outputReceptor));
	}
	
	@Override
	protected URL getOSWorkflowConfiguration() {
		
		URL ret = null;
		try {
			ret = WorkflowPlugin.get().getOSWorkflowConfiguration();
		} catch (ThinklabIOException e) {
			// FIXME shouldn't ignore it, but it's messy
		}
		return ret;
	}

	@Override
	protected void onActionStarted(WorkflowAction action,
			WorkflowStep currentStep) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onIncomingTransition(WorkflowAction action,
			WorkflowStep currentStep) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onTransition(WorkflowAction action,
			WorkflowStep previousStep, WorkflowStep currentStep) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onTransitionCompleted(WorkflowAction action,
			WorkflowStep currentStep) {
		// TODO Auto-generated method stub
		
	}
}
