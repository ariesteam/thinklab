package org.integratedmodelling.thinklab.modelling;

import java.util.ArrayList;
import java.util.List;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.api.lang.parsing.IConditionalObserverDefinition;
import org.integratedmodelling.thinklab.api.lang.parsing.IExpressionDefinition;
import org.integratedmodelling.thinklab.api.lang.parsing.IObserverDefinition;
import org.integratedmodelling.thinklab.api.modelling.IObserver;

public class ConditionalObserver extends Observer implements IConditionalObserverDefinition {

	ArrayList<Pair<IObserver,IExpression>> _observers;
	
	public void addObserver(IExpressionDefinition expression, IObserverDefinition observer) {
		_observers.add(new Pair<IObserver, IExpression>((IObserver)observer, (IExpression)expression));
	}
	
	public List<Pair<IObserver,IExpression>> getObservers() {
		return _observers;
	}
}
