package org.integratedmodelling.thinklab.query.operators;

import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
import org.integratedmodelling.thinklab.query.Query;


/**
 * A selector to be applied to the target of a property that points to
 * a table (parsed from a hashtable, such as Thinklab metadata). Transparently
 * handles the translation to the internal representation of the table. Will
 * select those tables where a field matches the operator.
 */
public class TableSelect extends Query {

	String _field;
	IQuery _match;
	
	public TableSelect(String field, IQuery match) {
		this._field = field;
		this._match = match;
	}
	
}
