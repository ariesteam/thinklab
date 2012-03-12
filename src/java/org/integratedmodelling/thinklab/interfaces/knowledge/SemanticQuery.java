package org.integratedmodelling.thinklab.interfaces.knowledge;

import java.util.List;

import org.integratedmodelling.lang.Quantifier;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;

public interface SemanticQuery extends IQuery {
	
	public List<SemanticQuery> getRestrictions();
	
	public IConcept getSubject();
	
	public Quantifier getQuantifier();

}
