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
package org.integratedmodelling.thinklab.modelling.internal;

import org.integratedmodelling.thinklab.api.lang.INamespaceQualified;

public abstract class NamespaceQualified implements INamespaceQualified {

	protected String _namespace;
	protected String _id;

	@Override
	public String getNamespace() {
		return _namespace;
	}

	@Override
	public String getName() {
		return _namespace + "/" + _id;
	}
	
	public void setName(String namespace, String id) {
		this._namespace = namespace;
		this._id = id;
	}

}
