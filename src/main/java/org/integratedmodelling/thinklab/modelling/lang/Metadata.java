package org.integratedmodelling.thinklab.modelling.lang;

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;

import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.metadata.IMetadata;
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

	@Override
	public void merge(IMetadata md) {
		_data.putAll(((Metadata)md)._data);
	}

	@Override
	public String getString(String field) {
		Object o = _data.get(field);		
		return o != null ? o.toString() : null;
	}

	@Override
	public Integer getInt(String field) {
		Object o = _data.get(field);		
		return o != null && o instanceof Number ? ((Number)o).intValue() : null;
	}

	@Override
	public Long getLong(String field) {
		Object o = _data.get(field);		
		return o != null && o instanceof Number ? ((Number)o).longValue() : null;
	}

	@Override
	public Double getDouble(String field) {
		Object o = _data.get(field);		
		return o != null && o instanceof Number ? ((Number)o).doubleValue() : null;
	}

	@Override
	public Float getFloat(String field) {
		Object o = _data.get(field);		
		return o != null && o instanceof Number ? ((Number)o).floatValue() : null;
	}

	@Override
	public Boolean getBoolean(String field) {
		Object o = _data.get(field);		
		return o != null && o instanceof Boolean ? (Boolean)o : null;
	}

	@Override
	public IConcept getConcept(String field) {
		Object o = _data.get(field);		
		return o != null && o instanceof IConcept ? (IConcept)o : null;
	}

	@Override
	public String getString(String field, String def) {
		Object o = _data.get(field);		
		return o != null ? o.toString() : def;
	}

	@Override
	public int getInt(String field, int def) {
		Object o = _data.get(field);		
		return o != null && o instanceof Number ? ((Number)o).intValue() : def;
	}

	@Override
	public long getLong(String field, long def) {
		Object o = _data.get(field);		
		return o != null && o instanceof Number ? ((Number)o).longValue() : def;
	}

	@Override
	public double getDouble(String field, double def) {
		Object o = _data.get(field);		
		return o != null && o instanceof Number ? ((Number)o).doubleValue() : def;
	}

	@Override
	public float getFloat(String field, float def) {
		Object o = _data.get(field);		
		return o != null && o instanceof Number ? ((Number)o).floatValue() : def;
	}

	@Override
	public boolean getBoolean(String field, boolean def) {
		Object o = _data.get(field);		
		return o != null && o instanceof Boolean ? (Boolean)o : def;
	}

	@Override
	public IConcept getConcept(String field, IConcept def) {
		Object o = _data.get(field);		
		return o != null && o instanceof IConcept ? (IConcept)o : def;
	}

	@Override
	public Collection<Object> getValues() {
		return _data.values();
	}
	
}
