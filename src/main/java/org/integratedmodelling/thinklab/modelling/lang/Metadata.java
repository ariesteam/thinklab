package org.integratedmodelling.thinklab.modelling.lang;

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;

import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.modelling.parsing.IMetadataDefinition;

@Concept(NS.METADATA)
public class Metadata extends LanguageElement<Metadata> implements IMetadataDefinition {

	HashMap<String, Object> _data = new HashMap<String, Object>();
	
	public void put(String id, Object value) {
		_data.put(id,value);
	}

	public Collection<String> getKeys() {
		return _data.keySet();
	}

	@Override
	public Object get(String string) {
		return _data.get(string);
	}

	@Override
	public void dump(PrintStream out) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Metadata demote() {
		return this;
	}
	
}
