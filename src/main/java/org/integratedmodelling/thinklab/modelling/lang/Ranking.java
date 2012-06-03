package org.integratedmodelling.thinklab.modelling.lang;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IState;
import org.integratedmodelling.thinklab.api.modelling.parsing.IRankingObserverDefinition;

@Concept(NS.RANKING_OBSERVER)
public class Ranking extends Observer<Ranking> implements IRankingObserverDefinition {
	
	Number _from = null;
	Number _to = null;
	Type   _type = Type.RANKING;

	@Override
	public Type getType() {
		return _type;
	}

	@Override
	public Pair<Number, Number> getRange() {
		return new Pair<Number, Number>(_from, _to);
	}

	@Override
	public void setType(Type type) {
		_type = type;
	}

	@Override
	public void setScale(Number from, Number to) {
		_from = from;
		_to = to;
	}

	@Override
	public Ranking demote() {
		return this;
	}

	@Override
	public IState createState(ISemanticObject<?> observable, IContext context) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}


}
