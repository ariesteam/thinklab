/**
 * SearchEngineKBoxCapabilities.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 21, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabSearchEnginePlugin.
 * 
 * ThinklabSearchEnginePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabSearchEnginePlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @date      Jan 21, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.searchengine.kbox;

import org.integratedmodelling.lang.Quantifier;
import org.integratedmodelling.searchengine.QueryString;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
import org.integratedmodelling.thinklab.api.knowledge.storage.IKBox;
import org.integratedmodelling.thinklab.api.lang.IList;

public class SearchEngineKBoxCapabilities implements IKBox.Capabilities {
	
	IKBox.Capabilities orig;
	
	public SearchEngineKBoxCapabilities(IKBox.Capabilities original) {
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

	public boolean supportsSchema(IList schema) {
		return orig.supportsSchema(schema);
	}
}