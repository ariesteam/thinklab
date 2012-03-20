package org.integratedmodelling.thinklab.modelling;

import java.util.Collection;
import java.util.HashMap;

import org.integratedmodelling.thinklab.api.lang.parsing.IMetadataDefinition;
import org.integratedmodelling.thinklab.api.metadata.IMetadata;

public class Metadata implements IMetadataDefinition {

	HashMap<String, Object> data = new HashMap<String, Object>();
	
	public void put(String id, Object value) {
		data.put(id,value);
	}

	public Collection<String> getKeys() {
		return data.keySet();
	}
	
	public Object getValue(String key) {
		return data.get(key);
	}

	@Override
	public Object get(String string) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
