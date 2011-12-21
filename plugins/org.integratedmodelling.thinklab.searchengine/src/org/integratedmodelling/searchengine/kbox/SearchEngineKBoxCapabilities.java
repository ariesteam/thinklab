/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.searchengine.kbox;

import org.integratedmodelling.searchengine.QueryString;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.query.IQuery;
import org.integratedmodelling.thinklab.interfaces.storage.IKBoxCapabilities;
import org.integratedmodelling.utils.Polylist;
import org.integratedmodelling.utils.Quantifier;

public class SearchEngineKBoxCapabilities implements IKBoxCapabilities {
	
	IKBoxCapabilities orig;
	
	public SearchEngineKBoxCapabilities(IKBoxCapabilities original) {
		orig = original;
	}

	public boolean canQuery() {
		return true;
	}

	public boolean canQueryAll() {
		return orig.canQueryAll();
	}

	public boolean canRetrieveAsList() {
		return orig.canRetrieveAsList();
	}

	public boolean canRetrieveInstance() {
		return orig.canRetrieveInstance();
	}

	public boolean canStoreInstance() {
		return orig.canStoreInstance();
	}

	public boolean canStoreList() {
		return orig.canStoreList();
	}

	public boolean canUseReferencesWhileRetrieving() {
		return orig.canUseReferencesWhileRetrieving();
	}

	public boolean canUseReferencesWhileStoring() {
		return orig.canUseReferencesWhileStoring();
	}

	public boolean honorsQuantifier(Quantifier q) {
		return orig.honorsQuantifier(q);
	}

	public boolean supportsOperator(IConcept target, IConcept operand,
			String op) {
		return orig.supportsOperator(target, operand, op);
	}

	public boolean supportsQuery(Class<IQuery> query) {

		if (query.equals(QueryString.class))
			return true;
		return orig.supportsQuery(query);
	}

	public boolean supportsSchema(Polylist schema) {
		return orig.supportsSchema(schema);
	}
}