package org.integratedmodelling.thinklab.workflow.evaluators;

import java.util.Collection;
import java.util.Map;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.command.CommandDeclaration;
import org.integratedmodelling.thinklab.command.CommandManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandOutputReceptor;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.workflow.exceptions.ThinklabWorkflowException;

import com.opensymphony.workflow.variables.ExternalVariableEvaluator;

/**
 * Evaluates a thinklab command, expecting all arguments and any options as named objects in the context. Plain
 * POD can be converted automatically to IValues so it's fine to pass numbers and strings as simple values.
 * 
 * @author Ferdinando
 *
 */
public class ThinklabCommandVariableEvaluator implements
		ExternalVariableEvaluator {

	private ISession session;

	public ThinklabCommandVariableEvaluator(ISession session) {
		
		this.session = session;
	}

	@Override
	public Object evaluate(String expression, Map<String, Object> context) {

		IValue ret = null;

		try {		
			Command command = new Command(expression, context);
			ret = CommandManager.get().submitCommand(command, session);
			
		} catch (ThinklabException e) {
			throw new ThinklabWorkflowException(e);
		}
		
		return ret;
	}

	@Override
	public String getTypeinfo() {
		return "command";
	}

	@Override
	public String[] getRequiredInputVariableNames(String expression) {

		String[] ret = new String[0];
		CommandDeclaration cdecl = CommandManager.get().getDeclarationForCommand(expression);
		
		if (ret != null)
			ret = cdecl.getAllArgumentNames();			
		
		return ret;
	}

}
