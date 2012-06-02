package org.integratedmodelling.thinklab.modelling.lang;

import java.util.ArrayList;
import java.util.List;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.collections.Triple;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.annotations.Property;
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.IObserver;
import org.integratedmodelling.thinklab.api.modelling.parsing.IConditionalObserverDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.IExpressionDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.IObserverDefinition;

@Concept(NS.CONDITIONAL_OBSERVER)
public class ConditionalObserver extends Observer<ConditionalObserver> implements IConditionalObserverDefinition {

	@Property(NS.HAS_OBSERVER)
	ArrayList<Pair<IObserver,IExpression>> _observers;
	
	public void addObserver(IExpressionDefinition expression, IObserverDefinition observer) {
		_observers.add(new Pair<IObserver, IExpression>((IObserver)observer, (IExpression)expression));
	}
	
	public List<Pair<IObserver,IExpression>> getObservers() {
		return _observers;
	}

	@Override
	public ConditionalObserver demote() {
		return this;
	}

	@Override
	public List<Triple<IModel, String, Boolean>> getDependencies() {
		ArrayList<Triple<IModel, String, Boolean>> ret = 
				new ArrayList<Triple<IModel,String,Boolean>>();

		for (Pair<IObserver, IExpression> o : _observers)
			ret.addAll(o.getFirst().getDependencies());
		
		return ret;
	}
	
	@Override
	public void initialize() throws ThinklabException {

		if (_initialized)
			return;
	
		for (Pair<IObserver, IExpression>  oo : _observers) {
			((Observer<?>)oo.getSecond()).initialize();
		}
	
		_initialized = true;
	}
}
