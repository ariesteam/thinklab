package org.integratedmodelling.thinklab.modelling.lang;

import java.io.PrintStream;

import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.metadata.IMetadata;
import org.integratedmodelling.thinklab.api.modelling.IModelObject;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.modelling.parsing.IMetadataDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.IModelObjectDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.INamespaceDefinition;

@Concept(NS.MODEL_OBJECT)
public abstract class ModelObject<T> extends LanguageElement<T> implements IModelObject, IModelObjectDefinition {
	
	String     _id;
	INamespace _namespace;
	IMetadata  _metadata;
	
	/**
	 * This is called after the model object is defined. If it returns anything other than
	 * null, the metadata are merged with the object's and the object is stored in the 
	 * thinklab kbox for the namespace, so that it can be found by queries and used to
	 * resolve dependencies.
	 * 
	 * @return
	 */
	public IMetadata getStorageMetadata() {
		return null;
	}
	
	
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
	public void setId(String id) {
		_id = id;
	}


	@Override
	public String getMetadataFieldAsString(String field) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Integer getMetadataFieldAsInt(String field) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Long getMetadataFieldAsLong(String field) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Double getMetadataFieldAsDouble(String field) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Float getMetadataFieldAsFloat(String field) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Boolean getMetadataFieldAsBoolean(String field) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IConcept getMetadataFieldAsConcept(String field) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getMetadataFieldAsString(String field, String def) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int getMetadataFieldAsInt(String field, int def) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public long getMetadataFieldAsLong(String field, long def) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public double getMetadataFieldAsDouble(String field, double def) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public float getMetadataFieldAsFloat(String field, float def) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public boolean getMetadataFieldAsBoolean(String field, boolean def) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public IConcept getMetadataFieldAsConcept(String field, IConcept def) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
