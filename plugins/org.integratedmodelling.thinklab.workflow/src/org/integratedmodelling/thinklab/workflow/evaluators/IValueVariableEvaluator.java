package org.integratedmodelling.thinklab.workflow.evaluators;

import java.util.Map;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.workflow.exceptions.ThinklabWorkflowException;

import com.opensymphony.workflow.variables.ExternalVariableEvaluator;

/**
 * Sets the variable to a new IValue created from a string. The string expression should contain a semantic type, a space, and
 * the literal specification of the IValue.
 * 
 * @author Ferdinando
 *
 */
public class IValueVariableEvaluator implements ExternalVariableEvaluator {

	public IValueVariableEvaluator(ISession session) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object evaluate(String expression, Map<String, Object> context) {
		
		/*
		 * separate the expression into type and literal, separated by whitespace
		 */
		expression = expression.trim();
		int split = expression.indexOf(' ');
		if (split < 0)
			throw new ThinklabWorkflowException("ivalue expressions must have a semantic type and a literal separated by space");
		
		String type = expression.substring(0, split).trim();
		String lite = expression.substring(split+1).trim();
		
		IConcept c = null;
		IValue val = null;
		
		try {
			c = KnowledgeManager.get().requireConcept(type);
		} catch (ThinklabException e) {
			throw new ThinklabWorkflowException("ivalue expression: concept " + type + " is unknown");
		}
		
		try {		
			val = KnowledgeManager.get().validateLiteral(c, lite, null);
		} catch (ThinklabException e) {
			throw new ThinklabWorkflowException("ivalue expression: literal " + lite + " does not validate as a " + type);
		}
		
		return val;
	}

	@Override
	public String getTypeinfo() {
		return "ivalue";
	}

	@Override
	public String[] getRequiredInputVariableNames(String variableExpression) {
		return new String[0];
	}

}
