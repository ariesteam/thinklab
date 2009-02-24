package org.integratedmodelling.thinklab.interfaces.commands;

import java.util.HashMap;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

public interface IExpression {

	public abstract IValue evaluate(ISession session, HashMap<String, Object> args) throws ThinklabException;
}
