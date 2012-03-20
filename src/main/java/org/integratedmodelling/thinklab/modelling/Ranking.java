package org.integratedmodelling.thinklab.modelling;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.thinklab.api.lang.parsing.IRankingObserverDefinition;

public class Ranking extends Observer implements IRankingObserverDefinition {
	
	Number _from = null;
	Number _to = null;
	Type _type = Type.RANKING;

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

}
