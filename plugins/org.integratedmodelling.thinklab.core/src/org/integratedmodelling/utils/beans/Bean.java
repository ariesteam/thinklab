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
package org.integratedmodelling.utils.beans;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.literals.BooleanValue;
import org.integratedmodelling.utils.MiscUtilities;

/**
 * A simple "bean-like" object that can be read-written though XML and other plug-in languages. Exposes
 * properties through set/get methods and has some relatively sophisticated ways of extracting 
 * subsets of properties and objects based on attributes and fields. 
 * 
 * Node, property and attribute extraction is at the moment not particularly efficient as
 * it uses no index hash. So limit the "get" operations to outside of loops and cache
 * results if possible. Can be sped up to a great extent at the cost of a couple more hours
 * of boredom (and some memory).
 * 
 * Can have arbitrarily nested objects and supports reading objects into given subclasses of BeanObject.
 * 
 * One of those extremely boring classes to write that can be used everywhere to 
 * simplify life.
 * 
 * @author Ferdinando
 *
 */
public class Bean implements Cloneable {
	
	private Properties properties = null;
	
	/*
	 * can install new readers in this one.
	 */
	static HashMap<String, Class<? extends BeanReader>> readers = 
		new HashMap<String, Class<? extends BeanReader>>();
	
	static {
		readers.put("xml", XMLBeanReader.class);
	}
	
	public Collection<OD> getFields() {
		return fields;
	}

	public Collection<OD> getChildren() {
		return childr;
	}

	@Override
	public Object clone() {
		
		Bean it = null;
		try {
			it = this.getClass().newInstance();
		} catch (Exception e) {
			throw new ThinklabRuntimeException(e);
		}
		
		for (OD od : fields) {
			it.addField(od.id, (String)od.value, cloneAttributes(od));			
		}
		for (OD od : childr) {
			it.addChild(od.id, (Bean)(((Bean)(od.value)).clone()), cloneAttributes(od));			
		}
		
		return it;
	}
	
	protected HashMap<String, String> cloneAttributes(OD od) {
		HashMap<String, String> ret = null;
		if (od.attributes != null) {
			ret = new HashMap<String, String>();
			for (String k : od.attributes.keySet())
				ret.put(k, od.attributes.get(k));
		}
		return ret;
	}

	private OD findOD(ArrayList<OD> list, String property, String attribute, String value) {

		OD it = null;
		for (OD od : list) {
			if (od.id.equals(property)) {
				if (
					(attribute == null && value == null) ||
					(attribute != null && value == null && od.attributes != null && od.attributes.containsKey(attribute)) ||
					(attribute != null && value != null && od.attributes != null && od.attributes.containsKey(attribute) && od.attributes.get(attribute).equals(value))) {
					it = od;
					break;
				}
			}
		}
		return it;
	}

	private Collection<OD> findODs(ArrayList<OD> list, String property, String attribute, String value) {

		ArrayList<OD> ret = new ArrayList<Bean.OD>();
		for (OD od : list) {
			if (od.id.equals(property)) {
				if (
					(attribute == null && value == null) ||
					(attribute != null && value == null && od.attributes != null && od.attributes.containsKey(attribute)) ||
					(attribute != null && value != null && od.attributes != null && od.attributes.containsKey(attribute) && od.attributes.get(attribute).equals(value))) {
					ret.add(od);
				}
			}
		}
		return ret;
	}
	
	private Collection<OD> findODsNeg(ArrayList<OD> list, String property, String attribute, String value) {

		ArrayList<OD> ret = new ArrayList<Bean.OD>();
		for (OD od : list) {
			if (od.id.equals(property)) {
				if (
					(attribute != null && value == null && (od.attributes == null || !od.attributes.containsKey(attribute))) ||
					(attribute != null && value != null && (od.attributes == null || !od.attributes.containsKey(attribute) || !od.attributes.get(attribute).equals(value)))) {
					ret.add(od);
				}
			}
		}
		return ret;
	}

	private Collection<OD> findODsF(ArrayList<OD> list, String property, String attribute, String value) {

		ArrayList<OD> ret = new ArrayList<Bean.OD>();
		for (OD od : list) {
			if (od.id.equals(property)) {
				if (
					(attribute == null && value == null) ||
					(attribute != null && value == null && ((Bean)(od.value)).get(attribute) != null) ||
					(attribute != null && value != null && ((Bean)(od.value)).get(attribute) != null && ((Bean)(od.value)).get(attribute).equals(value))) {
					ret.add(od);
				}
			}
		}
		return ret;
	}

	private Collection<OD> findODsFNeg(ArrayList<OD> list, String property, String attribute, String value) {

		ArrayList<OD> ret = new ArrayList<Bean.OD>();
		for (OD od : list) {
			if (od.id.equals(property)) {
				if (
					(attribute != null && value == null && ((Bean)(od.value)).get(attribute) == null ||
					(attribute != null && value != null && (((Bean)(od.value)).get(attribute) == null || !((Bean)(od.value)).get(attribute).equals(value))))) {
					ret.add(od);
				}
			}
		}
		return ret;
	}

	static class OD implements Comparable<OD> {
		
		public String id;
		public Object value;
		public HashMap<String, String> attributes;
		
		public OD(String id, Object value, HashMap<String, String> attributes) {
			this.id = id;
			this.value = value;
			this.attributes = attributes;
		}

		@Override
		public int compareTo(OD o) {
			return id.compareTo(o.id);
		}
	}
	
	public void addField(String id, String value, HashMap<String,String> attributes) {
		fields.add(new OD(id, value, attributes));
	}
	
	public void addChild(String id, Bean value, HashMap<String,String> attributes) {
		childr.add(new OD(id, value, attributes));		
	}
	
	// remove all occurrences of named field or child bean
	public void remove(String id) {

		ArrayList<Integer> todo = new ArrayList<Integer>();
		int i = 0;
		for (OD od : fields) {
			if (od.id.equals(id))
				todo.add(i);
			i++;
		}
		for (int n : todo)
			fields.remove(n);

		todo.clear();
		i = 0;
		for (OD od : childr) {
			if (od.id.equals(id))
				todo.add(i);
			i++;
		}
		for (int n : todo)
			childr.remove(n);

	}
	
	public static void registerReader(String extension, Class<? extends BeanReader> readerClass) {
		readers.put(extension, readerClass);
	}
	
	public static interface BeanReader {

		void read(InputStream input, Bean object, Map<String, Class<? extends Bean>> cmap)
				throws ThinklabException;
		
		void write(OutputStream output, Bean object, Map<Class<? extends Bean>, String> cmap) throws ThinklabException;
	}
	
	// these index the whole content. They're in the natural order.
	private ArrayList<OD> fields = new ArrayList<Bean.OD>();
	private ArrayList<OD> childr = new ArrayList<Bean.OD>();
	
	public String get(String property) {
		return getWith(property, null, null);
	}

	public boolean getBoolean(String property) {
		String p = get(property);
		if (p == null)
			return false;
		return BooleanValue.parseBoolean(p);
	}

	public Integer getInteger(String property) {
		String p = get(property);
		if (p == null)
			return null;
		return Integer.parseInt(p);
	}

	public Double getDouble(String property) {
		String p = get(property);
		if (p == null)
			return null;
		return Double.parseDouble(p);
	}

	// expose all fields without attributes as properties
	public Properties getProperties() {
		if (this.properties == null) {
			this.properties = new Properties();
			for (OD od : this.fields) {
				if (od.attributes == null) {
					this.properties.setProperty(od.id, (String) od.value);
				}
			}
		}
		return this.properties;
	}
	
	public Collection<String> getAll(String property) {
		return getAllWith(property, null, null);
	}
	
	public String getWith(String property, String attribute) {
		return getWith(property, attribute, null);
	}

	public Collection<String> getAllWithout(String property, String attribute) {
		return getAllWithout(property, attribute, null);
	}

	public String getWith(String property, String attribute, String value) {
		OD it = findOD(fields, property, attribute, value);
		return it == null ? null : (String)(it.value);
	}

	public Collection<String> getAllWith(String property, String attribute, String value) {
		ArrayList<String> ret = new ArrayList<String>();
		for (OD od : findODs(fields, property, attribute, value)) {
			ret.add((String) od.value);
		}
		return ret;
	}

	public Collection<String> getAllWithout(String property, String attribute, String value) {
		ArrayList<String> ret = new ArrayList<String>();
		for (OD od : findODsNeg(fields, property, attribute, value)) {
			ret.add((String) od.value);
		}
		return ret;
	}

	public String getAttribute(String id, String attribute) {
		OD od = findOD(fields, id, null, null);
		if (od == null) 
			od = findOD(childr, id, null, null);
		if (od != null && od.attributes != null)
			return od.attributes.get(attribute);
		return null;
	}
	
	public HashMap<String, String> getAttributes(String id, String attribute, String value) {
		OD od = findOD(fields, id, attribute, value);
		if (od == null) 
			od = findOD(childr, id, attribute, value);
		if (od != null)
			return od.attributes;
		return null;
	}
	
	public Bean getObject(String property) {
		return getObjectWith(property, null, null);
	}

	public Collection<Bean> getAllObjects(String property) {
		return getAllObjectsWith(property, null, null);
	}

	public Collection<Bean> getAllObjectsWith(String property, String attribute) {
		return getAllObjectsWith(property, attribute, null);
	}

	public Collection<Bean> getAllObjectsWithout(String property, String attribute) {
		return getAllObjectsWithout(property, attribute, null);
	}

	public Bean getObjectWith(String property, String attribute) {
		return getObjectWith(property, attribute, null);
	}

	public Map<String, Bean> mapObjectsWithAttribute(String property, String attribute) {
		HashMap<String, Bean> ret = new HashMap<String, Bean>();
		for (OD od : findODs(childr, property, attribute, null)) {
			ret.put(od.id, (Bean) od.value);
		}
		return ret;
	}

	public Map<String, Bean> mapObjectsWithField(String property, String attribute) {
		HashMap<String, Bean> ret = new HashMap<String, Bean>();
		for (OD od : findODsF(childr, property, attribute, null)) {
			ret.put(od.id, (Bean) od.value);
		}
		return ret;	
	}

	public Map<String, String> mapWithAttribute(String property, String attribute) {
		HashMap<String, String> ret = new HashMap<String, String>();
		for (OD od : findODs(fields, property, attribute, null)) {
			ret.put(od.id, (String) od.value);
		}
		return ret;	
	}

	public Bean getObjectWith(String property, String attribute, String value) {
		OD it = findOD(childr, property, attribute, value);
		return it == null ? null : (Bean)(it.value);
	}

	public Collection<Bean> getAllObjectsWith(String property, String attribute, String value) {
		ArrayList<Bean> ret = new ArrayList<Bean>();
		for (OD od : findODs(childr, property, attribute, value)) {
			ret.add((Bean) od.value);
		}
		return ret;
	}

	public Collection<Bean> getAllObjectsWithout(String property, String attribute, String value) {
		ArrayList<Bean> ret = new ArrayList<Bean>();
		for (OD od : findODsNeg(childr, property, attribute, value)) {
			ret.add((Bean) od.value);
		}
		return ret;
	}

	public Collection<Bean> getAllObjectsWithoutField(String property, String attribute) {
		ArrayList<Bean> ret = new ArrayList<Bean>();
		for (OD od : findODsFNeg(childr, property, attribute, null)) {
			ret.add((Bean) od.value);
		}
		return ret;
	}

	public Collection<Bean> getAllObjectsWithoutField(String property, String attribute, String value) {
		ArrayList<Bean> ret = new ArrayList<Bean>();
		for (OD od : findODsFNeg(childr, property, attribute, value)) {
			ret.add((Bean) od.value);
		}
		return ret;
	}

	public Collection<Bean> getAllObjectsWithField(String property, String attribute) {
		ArrayList<Bean> ret = new ArrayList<Bean>();
		for (OD od : findODsF(childr, property, attribute, null)) {
			ret.add((Bean) od.value);
		}
		return ret;
	}
	
	public Bean getObjectWithField(String property, String attribute) {
		Collection<Bean> ret = getAllObjectsWithField(property,attribute);
		return ret.size() > 0 ? ret.iterator().next() : null;
	}

	public Bean getObjectWithField(String property, String field, String value) {
		Collection<Bean> ret = getAllObjectsWithField(property, field, value);
		return ret.size() > 0 ? ret.iterator().next() : null;
	}

	public Collection<Bean> getAllObjectsWithField(String property, String attribute, String value) {
		ArrayList<Bean> ret = new ArrayList<Bean>();
		for (OD od : findODsF(childr, property, attribute, value)) {
			ret.add((Bean) od.value);
		}
		return ret;
	}

	public void read(String input, Map<String, Class<? extends Bean>> cmap)
		throws ThinklabException {
	
		String ext = MiscUtilities.getFileExtension(input);
		Class<? extends BeanReader> readerc = readers.get(ext);
		if (readerc == null) {
			throw new ThinklabValidationException(
					"don't know how to read objects from file with extension " + ext);
		}
		
		BeanReader reader = null;
		try {
			reader = readerc.newInstance();
		} catch (Exception e) {
			throw new ThinklabInternalErrorException(e);
		}
		
		InputStream inp = MiscUtilities.getInputStreamForResource(input);
		reader.read(inp, this, cmap);
		
		try {
			inp.close();
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}		
		
		// TODO rebuild indices
	}
	
	public void write(String output, Map<Class<? extends Bean>, String> cmap) throws ThinklabException {

		String ext = MiscUtilities.getFileExtension(output);
		Class<? extends BeanReader> readerc = readers.get(ext);
		if (readerc == null) {
			throw new ThinklabValidationException(
					"don't know how to write objects to file with extension " + ext);
		}
		
		BeanReader reader = null;
		try {
			reader = readerc.newInstance();
		} catch (Exception e) {
			throw new ThinklabInternalErrorException(e);
		}

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(output);
		} catch (FileNotFoundException e) {
			throw new ThinklabIOException(e);
		}
		reader.write(out, this, cmap);
		try {
			out.close();
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}		
	}

}
