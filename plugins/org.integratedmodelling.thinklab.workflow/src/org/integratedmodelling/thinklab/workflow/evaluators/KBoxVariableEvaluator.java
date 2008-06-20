package org.integratedmodelling.thinklab.workflow.evaluators;

import java.util.Map;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.ICommandOutputReceptor;
import org.integratedmodelling.thinklab.interfaces.IInstance;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.workflow.exceptions.ThinklabWorkflowException;

import com.opensymphony.workflow.variables.ExternalVariableEvaluator;

/**
 * Sets the variable to an instance retrieved from a kbox. The expression must be a kbox URL or name with
 * the object ID after the # sign.
 * @author Ferdinando
 *
 */
public class KBoxVariableEvaluator implements ExternalVariableEvaluator {

	private ISession session;
	//private ICommandOutputReceptor outputWriter;

	public KBoxVariableEvaluator(ISession session,
			ICommandOutputReceptor outputReceptor) {
		
		this.session = session;
		//this.outputWriter = outputReceptor;
	}

	@Override
	public Object evaluate(String expression, Map<String, Object> context) {

		IInstance ret = null;
		
		try {
			ret = KnowledgeManager.get().getInstanceFromURI(expression, session);
		} catch (ThinklabException e) {
			throw new ThinklabWorkflowException(e);
		}
		
		return ret;
	
	}

	@Override
	public String getTypeinfo() {
		return "kbox";
	}

	@Override
	public String[] getRequiredInputVariableNames(String variableExpression) {
		return new String[0];
	}

}
