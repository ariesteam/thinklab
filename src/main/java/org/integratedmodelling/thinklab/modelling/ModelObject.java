package org.integratedmodelling.thinklab.modelling;

import java.io.PrintStream;

import org.integratedmodelling.thinklab.api.lang.parsing.IMetadataDefinition;
import org.integratedmodelling.thinklab.api.lang.parsing.IModelObjectDefinition;
import org.integratedmodelling.thinklab.api.lang.parsing.INamespaceDefinition;
import org.integratedmodelling.thinklab.api.metadata.IMetadata;
import org.integratedmodelling.thinklab.api.modelling.IModelObject;
import org.integratedmodelling.thinklab.api.modelling.INamespace;

public class ModelObject extends LanguageElement implements IModelObject, IModelObjectDefinition {
	
	String     _id;
	int        _lastLineNumber = 0;
	int        _firstLineNumber = 0;
	INamespace _namespace;
	IMetadata  _metadata;
	
	@Override
	public void dump(PrintStream out) {
	}
	
	@Override
	public String getName() {
		return getNamespace().getId() + "/" + _id;
	}
	
	@Override
	public IMetadata getMetadata() {
		return _metadata;
	}
	
	@Override
	public void setNamespace(INamespaceDefinition namespace) {
		_namespace = (INamespace) namespace;
	}
	
	@Override
	public void setLineNumbers(int startLine, int endLine) {
		_firstLineNumber = startLine;
		_lastLineNumber  = endLine;
	}
	
	@Override
	public void setMetadata(IMetadataDefinition metadata) {
		_metadata = (IMetadata) metadata;
	}
	
	@Override
	public INamespace getNamespace() {
		return _namespace;
	}

	@Override
	public String getId() {
		return _id;
	}
	
	@Override
	public int getFirstLineNumber() {
		return _firstLineNumber;
	}
	
	@Override
	public int getLastLineNumber() {
		return _lastLineNumber;
	}

	@Override
	public void setId(String id) {
		_id = id;
	}

}
