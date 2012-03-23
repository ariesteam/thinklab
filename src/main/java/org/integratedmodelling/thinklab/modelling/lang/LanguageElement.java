package org.integratedmodelling.thinklab.modelling.lang;

import java.io.PrintStream;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.annotation.SemanticObject;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.modelling.parsing.ILanguageDefinition;

@Concept(NS.LANGUAGE_ELEMENT)
public abstract class LanguageElement<T> extends SemanticObject<T> implements ILanguageDefinition {
	
	int        _lastLineNumber = 0;
	int        _firstLineNumber = 0;
	
	public abstract void dump(PrintStream out);
	
	@Override
	public void setLineNumbers(int startLine, int endLine) {
		_firstLineNumber = startLine;
		_lastLineNumber  = endLine;
	}
	
	@Override
	public int getFirstLineNumber() {
		return _firstLineNumber;
	}
	
	@Override
	public int getLastLineNumber() {
		return _lastLineNumber;
	}
	
	public void initialize() throws ThinklabException {
		
	}
}
