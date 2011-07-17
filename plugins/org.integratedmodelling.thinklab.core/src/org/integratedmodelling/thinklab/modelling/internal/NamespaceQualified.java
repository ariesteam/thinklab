package org.integratedmodelling.thinklab.modelling.internal;

import org.integratedmodelling.thinklab.api.INamespaceQualified;

public class NamespaceQualified implements INamespaceQualified {

	protected String _namespace;
	protected String _id;
	
	@Override
	public String getId() {
		return _id;
	}

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
