package org.integratedmodelling.thinklab.modelling.lang;

import java.io.PrintStream;

import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.annotations.Property;
import org.integratedmodelling.thinklab.api.modelling.parsing.IUnitDefinition;

@Concept(NS.UNIT_DEFINITION)
public class UnitDefinition extends LanguageElement<UnitDefinition> implements IUnitDefinition {

	@Property(NS.HAS_EXPRESSION)
	String _expression;
	
	@Override
	public void setExpression(String expression) {
		_expression = expression;
	}

	@Override
	public void dump(PrintStream out) {
		out.append(_expression);
	}

	@Override
	public UnitDefinition demote() {
		return this;
	}

	@Override
	public String getStringExpression() {
		return _expression;
	}

}
