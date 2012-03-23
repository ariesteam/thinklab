package org.integratedmodelling.thinklab.modelling.lang;

import java.io.PrintStream;

import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.modelling.parsing.IUnitDefinition;

@Concept(NS.UNIT_DEFINITION)
public class UnitDefinition extends LanguageElement<UnitDefinition> implements IUnitDefinition {

	String _expression;
	
	@Override
	public void setExpression(String expression) {
		_expression = expression;
	}

	@Override
	public void dump(PrintStream out) {
		
	}

	@Override
	public UnitDefinition demote() {
		return this;
	}

}
