package org.integratedmodelling.thinklab.modelling;

import java.io.PrintStream;

import org.integratedmodelling.thinklab.api.lang.parsing.IUnitDefinition;

public class UnitDefinition extends LanguageElement implements IUnitDefinition {

	String _expression;
	
	@Override
	public void setExpression(String expression) {
		_expression = expression;
	}

	@Override
	public void dump(PrintStream out) {
		
	}

}
