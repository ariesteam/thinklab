package org.integratedmodelling.utils.beans;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.utils.MiscUtilities;

/**
 * A simple "bean-like" object that can be read-written though XML and other plug-in languages. Exposes
 * properties through set/get methods and has some relatively sophisticated ways of extracting 
 * subsets of properties and objects based on attributes and fields. 
 * 
 * Can have arbitrarily nested objects and supports reading objects into given subclasses of BeanObject.
 * 
 * @author Ferdinando
 *
 */
public class BeanObject {
	
	private Properties properties = null;
	
	/*
	 * can install new readers in this one.
	 */
	static HashMap<String, Class<? extends BeanReader>> readers = 
		new HashMap<String, Class<? extends BeanReader>>();
	
	static {
		readers.put("xml", XMLBeanReader.class);
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

		ArrayList<OD> ret = new ArrayList<BeanObject.OD>();
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

		ArrayList<OD> ret = new ArrayList<BeanObject.OD>();
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

		ArrayList<OD> ret = new ArrayList<BeanObject.OD>();
		for (OD od : list) {
			if (od.id.equals(property)) {
				if (
					(attribute == null && value == null) ||
					(attribute != null && value == null && ((BeanObject)(od.value)).get(attribute) != null) ||
					(attribute != null && value != null && ((BeanObject)(od.value)).get(attribute) != null && ((BeanObject)(od.value)).get(attribute).equals(value))) {
					ret.add(od);
				}
			}
		}
		return ret;
	}

	private Collection<OD> findODsFNeg(ArrayList<OD> list, String property, String attribute, String value) {

		ArrayList<OD> ret = new ArrayList<BeanObject.OD>();
		for (OD od : list) {
			if (od.id.equals(property)) {
				if (
					(attribute != null && value == null && ((BeanObject)(od.value)).get(attribute) == null ||
					(attribute != null && value != null && (((BeanObject)(od.value)).get(attribute) == null || !((BeanObject)(od.value)).get(attribute).equals(value))))) {
					ret.add(od);
				}
			}
		}
		return ret;
	}

	class OD implements Comparable<OD> {
		
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
	
	public void addChild(String id, BeanObject value, HashMap<String,String> attributes) {
		childr.add(new OD(id, value, attributes));		
	}
	
	public static void registerReader(String extension, Class<? extends BeanReader> readerClass) {
		readers.put(extension, readerClass);
	}
	
	public static interface BeanReader {

		void read(InputStream input, BeanObject object, Map<String, Class<? extends BeanObject>> cmap)
				throws ThinklabException;
	}
	
	// these index the whole content. They're in the natural order.
	private ArrayList<OD> fields = new ArrayList<BeanObject.OD>();
	private ArrayList<OD> childr = new ArrayList<BeanObject.OD>();
	
	public String get(String property) {
		return getWith(property, null, null);
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
	
	public BeanObject getObject(String property) {
		return getObjectWith(property, null, null);
	}

	public Collection<BeanObject> getAllObjects(String property) {
		return getAllObjectsWith(property, null, null);
	}

	public Collection<BeanObject> getAllObjectsWith(String property, String attribute) {
		return getAllObjectsWith(property, attribute, null);
	}

	public Collection<BeanObject> getAllObjectsWithout(String property, String attribute) {
		return getAllObjectsWithout(property, attribute, null);
	}

	public BeanObject getObjectWith(String property, String attribute) {
		return getObjectWith(property, attribute, null);
	}

	public Map<String, BeanObject> mapObjectsWithAttribute(String property, String attribute) {
		HashMap<String, BeanObject> ret = new HashMap<String, BeanObject>();
		for (OD od : findODs(childr, property, attribute, null)) {
			ret.put(od.id, (BeanObject) od.value);
		}
		return ret;
	}

	public Map<String, BeanObject> mapObjectsWithField(String property, String attribute) {
		HashMap<String, BeanObject> ret = new HashMap<String, BeanObject>();
		for (OD od : findODsF(childr, property, attribute, null)) {
			ret.put(od.id, (BeanObject) od.value);
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

	public BeanObject getObjectWith(String property, String attribute, String value) {
		OD it = findOD(childr, property, attribute, value);
		return it == null ? null : (BeanObject)(it.value);
	}

	public Collection<BeanObject> getAllObjectsWith(String property, String attribute, String value) {
		ArrayList<BeanObject> ret = new ArrayList<BeanObject>();
		for (OD od : findODs(childr, property, attribute, value)) {
			ret.add((BeanObject) od.value);
		}
		return ret;
	}

	public Collection<BeanObject> getAllObjectsWithout(String property, String attribute, String value) {
		ArrayList<BeanObject> ret = new ArrayList<BeanObject>();
		for (OD od : findODsNeg(childr, property, attribute, value)) {
			ret.add((BeanObject) od.value);
		}
		return ret;
	}

	public Collection<BeanObject> getAllObjectsWithoutField(String property, String attribute) {
		ArrayList<BeanObject> ret = new ArrayList<BeanObject>();
		for (OD od : findODsFNeg(childr, property, attribute, null)) {
			ret.add((BeanObject) od.value);
		}
		return ret;
	}

	public Collection<BeanObject> getAllObjectsWithoutField(String property, String attribute, String value) {
		ArrayList<BeanObject> ret = new ArrayList<BeanObject>();
		for (OD od : findODsFNeg(childr, property, attribute, value)) {
			ret.add((BeanObject) od.value);
		}
		return ret;
	}

	public Collection<BeanObject> getAllObjectsWithField(String property, String attribute) {
		ArrayList<BeanObject> ret = new ArrayList<BeanObject>();
		for (OD od : findODsF(childr, property, attribute, null)) {
			ret.add((BeanObject) od.value);
		}
		return ret;
	}
	
	public BeanObject getObjectWithField(String property, String attribute) {
		Collection<BeanObject> ret = getAllObjectsWithField(property,attribute);
		return ret.size() > 0 ? ret.iterator().next() : null;
	}

	public BeanObject getObjectWithField(String property, String field, String value) {
		Collection<BeanObject> ret = getAllObjectsWithField(property, field, value);
		return ret.size() > 0 ? ret.iterator().next() : null;
	}

	public Collection<BeanObject> getAllObjectsWithField(String property, String attribute, String value) {
		ArrayList<BeanObject> ret = new ArrayList<BeanObject>();
		for (OD od : findODsF(childr, property, attribute, value)) {
			ret.add((BeanObject) od.value);
		}
		return ret;
	}

	public void read(String input, Map<String, Class<? extends BeanObject>> cmap)
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
		
		// TODO rebuild indices
	}
	
	public void write(OutputStream out) {
		
	}

}
