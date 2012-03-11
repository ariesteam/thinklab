///**
// * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
// * www.integratedmodelling.org. 
//
//   This file is part of Thinklab.
//
//   Thinklab is free software: you can redistribute it and/or modify
//   it under the terms of the GNU General Public License as published
//   by the Free Software Foundation, either version 3 of the License,
//   or (at your option) any later version.
//
//   Thinklab is distributed in the hope that it will be useful, but
//   WITHOUT ANY WARRANTY; without even the implied warranty of
//   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//   General Public License for more details.
//
//   You should have received a copy of the GNU General Public License
//   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
// */
//package org.integratedmodelling.thinklab.kbox;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.MalformedURLException;
//import java.net.URI;
//import java.net.URL;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Properties;
//
//import org.integratedmodelling.exceptions.ThinklabException;
//import org.integratedmodelling.exceptions.ThinklabIOException;
//import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
//import org.integratedmodelling.exceptions.ThinklabRuntimeException;
//import org.integratedmodelling.exceptions.ThinklabStorageException;
//import org.integratedmodelling.exceptions.ThinklabValidationException;
//import org.integratedmodelling.list.PolyList;
//import org.integratedmodelling.thinklab.KnowledgeManager;
//import org.integratedmodelling.thinklab.Thinklab;
//import org.integratedmodelling.thinklab.api.knowledge.IConcept;
//import org.integratedmodelling.thinklab.api.knowledge.IInstance;
//import org.integratedmodelling.thinklab.api.knowledge.IValue;
//import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
//import org.integratedmodelling.thinklab.api.knowledge.query.IQueryResult;
//import org.integratedmodelling.thinklab.api.knowledge.storage.IKBox;
//import org.integratedmodelling.thinklab.api.lang.IList;
//import org.integratedmodelling.thinklab.api.runtime.ISession;
//import org.integratedmodelling.thinklab.constraint.Constraint;
//import org.integratedmodelling.thinklab.extensions.KBoxHandler;
//import org.integratedmodelling.thinklab.interfaces.storage.IKnowledgeImporter;
//import org.integratedmodelling.utils.MiscUtilities;
//
///**
// * A singleton that manages KBoxes. One of these is created by and accessed through
// * the Knowledge manager. All the KBox functions are not here yet, but will be
// * moved here with time.
// * 
// * The KboxManager is also a kbox, which will dispatch a query to all installed kboxes.
// * 
// * @author Ferdinando Villa
// *
// */
//public class KBoxManager implements IKBox {
//
//	private static final String METADATA_KBOX_PROPERTY = "kbox.metadata.schema";
//	
//	static KBoxManager _this = null;
//	
//	/**
//	 * Get the only instance of the kbox manager.
//	 * 
//	 */
//	public static KBoxManager get() {
//		if (_this == null) {
//			_this = new KBoxManager();
//		}
//		return _this;
//	}
//	
//	/*
//	 * knowledge importer classes harvested by plugins
//	 */
//	HashMap<String, Class<?>> importers = new HashMap<String, Class<?>>();
//	
//    /*
//     * a registry of plugins that handle KBox creation.
//     */
//    HashMap<String, KBoxHandler> kboxPlugins = new HashMap<String, KBoxHandler>();
//    
//    /*
//     * A registry of installed KBoxes, indexed by their URL.
//     */
//    HashMap<String, IKBox> kBoxes = new HashMap<String, IKBox>();
//	
//    @Override
//    public String toString() {
//    	return "[global kbox: " + kBoxes.size() + " sources online]";
//    }
//    
//	/**
//	 * Properties starting with this prefix declare a metadata field in
//	 * their name (after the prefix) and its semantic type. Plugins can
//	 * add as many metadata fields as they want, which kboxes can 
//	 * declare and set into each object's metadata fields. 
//	 */
//	public final String KBOX_METADATA_PREFIX = "kbox.metadata.type";
//
//	HashMap<String, IConcept> metadataTypes = new HashMap<String, IConcept>();
//	
//	public KBoxManager() {
//		
//		/* add the default metadata fields */
//		metadataTypes.put(IQueryResult.ID_FIELD_NAME, KnowledgeManager.Text());
//		metadataTypes.put(IQueryResult.LABEL_FIELD_NAME, KnowledgeManager.Text());
//		metadataTypes.put(IQueryResult.DESCRIPTION_FIELD_NAME, KnowledgeManager.Text());
//		metadataTypes.put(IQueryResult.CLASS_FIELD_NAME, KnowledgeManager.Text());
//	}
//	
//	public void installKbox(String uri, IKBox kbox) {
//		kBoxes.put(uri, kbox);
//	}
//	
//	public IKnowledgeImporter getKnowledgeImporter(String format) throws ThinklabException {
//		
//		IKnowledgeImporter ret = null;
//		Class<?> cls = importers.get(format);
//		if (cls != null)
//			try {
//				ret = (IKnowledgeImporter) cls.newInstance();
//			} catch (Exception e) {
//				throw new ThinklabValidationException(e);
//			}
//		return ret;
//	}
//	
//	/*
//	 * Called by the plugin registry
//	 */
//	public void defineMetadataTypes(Properties properties) throws ThinklabException {
//		
//		for (Object p : properties.keySet()) {
//			
//			if (p.toString().startsWith(KBOX_METADATA_PREFIX)) {
//				String cid = properties.getProperty(p.toString());
//				
//				String[] ss = p.toString().split("\\.");
//				String metadataName = ss[ss.length - 1];
//				
//				IConcept cc = KnowledgeManager.get().requireConcept(cid);
//				metadataTypes.put(metadataName, cc);
//			}
//		}
//	}
//	
//	public void validateSchema(IList schema) {
//		
//	}
//	
//	public IList getDefaultSchema() {
//		return PolyList.list(
//				IQueryResult.ID_FIELD_NAME, 
//				IQueryResult.ID_FIELD_NAME, 
//				IQueryResult.LABEL_FIELD_NAME, 
//				IQueryResult.DESCRIPTION_FIELD_NAME);
//	}
//	
//	public HashMap<String, IValue> createResult(IList schema, IList results) throws ThinklabException {
//		
//		HashMap<String, IValue> ret = new HashMap<String, IValue>();
//
//		Object[] oo = schema.array();
//		Object[] ov = results.array();
//		
//		for (int i = 0; i < oo.length; i++) {
//			if (ov[i] != null) {
//				if (ov[i] instanceof IValue)
//					ret.put(oo[i].toString(), (IValue)ov[i]);
//				else {
//					
//					String sv = ov[i].toString();
//					IConcept c = metadataTypes.get(oo[i].toString());
//					
//					if (c == null) 
//						throw new ThinklabValidationException(
//								"kbox: metadata field " + 
//								oo[i] + 
//								" has not been defined by any plugins");
//					
//					IValue val = KnowledgeManager.get().validateLiteral(c, sv);
//					
//					ret.put(oo[i].toString(), val);
//				}
//			}
//		}
//		
//		return ret;
//	}
//
//	public IList parseSchema(Properties properties) throws ThinklabValidationException {
//		
//		IList ret = getDefaultSchema();
//		
//		if (properties != null) {
//
//			String pmd = properties.getProperty(METADATA_KBOX_PROPERTY);
//			if (pmd != null) {
//				String[] ss = pmd.split(",");
//				for (String mdf : ss) {
//					/* validate */
//					if (metadataTypes.get(mdf) == null) 
//						throw new ThinklabValidationException(
//								"kbox: metadata field " + 
//								mdf + 
//								" has not been defined by any plugins");
//					
//					ret = ret.cons(mdf);
//				}
//			}
//		}
//		
//		return ret;
//	}
//	
//	
//	/**
//	 * Register a plugin to handle a particular KBox protocol. Called upon initialization by
//	 * KBoxPlugins.
//	 * @param protocol
//	 * @param plugin
//	 */
//	public void registerKBoxProtocol(String protocol, KBoxHandler plugin) {
//		kboxPlugins.put(protocol, plugin);
//	}
//	
//	/* (non-Javadoc)
//	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#retrieveGlobalKBox(java.lang.String)
//	 */
//	public IKBox retrieveGlobalKBox(String kboxURI) throws ThinklabException {
//	    
//		/* get the KBox URL */
//		int dot = kboxURI.indexOf("#");
//		if (dot >= 0)
//			kboxURI = kboxURI.substring(0, dot);
//		
//		/* see if we have it already */
//		IKBox ret = null;
//		if (kboxURI.contains(":")) {
//			ret = kBoxes.get(kboxURI);
//		} else {
//			String uri = null;
//			/* see if we're using an unambiguous kbox name */
//			for (String kb : kBoxes.keySet()) {
//				if (MiscUtilities.getNameFromURL(kb).equals(kboxURI)) {
//					if (uri == null) {
//						uri = kb;
//					} else {
//						throw new ThinklabValidationException(
//								"identifier " + kboxURI + " specifies more than one kbox");
//					}
//				}
//			}
//			if (uri != null)
//				ret = kBoxes.get(uri);
//		}
//		
//		if (ret == null && kboxURI.contains(":")) {
//			
//			String protocol;
//			
//			try {
//				protocol = new URI(kboxURI).getScheme();
//
//				if (protocol.equals("kbox") || 
//						((protocol.equals("file") || protocol.equals("http") || protocol.equals("https")) &&
//								kboxURI.endsWith(".kbox"))) {
//					
//					ret = retrieveGenericKBox(kboxURI);
//
//				} else {
//
//					KBoxHandler plu = kboxPlugins.get(protocol);
//					if (plu == null)
//						return null;
//					ret = plu.createKBoxFromURL(new URI(kboxURI));					
//				}
//
//				kBoxes.put(kboxURI.toString(), ret);
//
//			} catch (Exception e) {
//				throw new ThinklabStorageException(e);
//			}
//		}
//		
//		return ret;
//	}
//
//	/**
//	 * Retrieve a KBox identified by generic protocol "kbox", which requires the URL to point
//	 * to a metadata (properties) document. This document is looked for first in the filesystem
//	 * by changing "kbox" to "file"; if such a file does not exist, "http" is tried. The document
//	 * must contain at least the "protocol" and "uri" properties. Any other property is considered
//	 * a parameter. All are passed to the kbox initialize() function, after the kbox is initialized
//	 * with the empty constructor.
//	 * 
//	 * TODO move to KBoxManager
//	 * 
//	 * @param kboxURI
//	 * @return
//	 */
//	private IKBox retrieveGenericKBox(String kboxURI) throws ThinklabException {
//	
//		IKBox ret = null;
//		URL sourceURL = null;
//		InputStream input = null;
//		
//		try {
//		
//			/* see if we have a metadata document in the corresponding file: url */
//			sourceURL = new URL("file" + kboxURI.substring(4));
//			
//			try {
//				input = sourceURL.openStream();
//			} catch (IOException e) {
//				input = null;
//			}
//			
//			if (input == null) {
//
//				/* try http: */
//				sourceURL =  new URL("http" + kboxURI.substring(4));
//
//				try {
//					input = sourceURL.openStream();
//				} catch (IOException e) {
//					input = null;
//				}				
//			}
//		
//		} catch (MalformedURLException e) {
//		}
//		
//		if (input == null) {
//			throw new ThinklabValidationException("url " + kboxURI + " does not point to a valid metadata document");
//		}
//
//		
//		/* we have a metadata document; extract protocol, url, and all parameters */
//		Properties properties = new Properties();
//		
//		try {
//			properties.load(input);
//		} catch (IOException e) {
//			throw new ThinklabIOException(e);
//		}
//		
//		String protocol = properties.getProperty(IKBox.KBOX_PROTOCOL_PROPERTY);
//		String dataUri = properties.getProperty(IKBox.KBOX_URI_PROPERTY);
//		String ontologies = properties.getProperty(IKBox.KBOX_ONTOLOGIES_PROPERTY);
//		String wrapperCls = properties.getProperty(IKBox.KBOX_WRAPPER_PROPERTY);
//		
//		Thinklab.get().logger().info("opening kbox " + kboxURI + " with data uri " + dataUri);
//		
//		if (protocol == null || protocol.equals(""))
//			throw new ThinklabValidationException("kbox metadata for " + kboxURI + " don't specify a protocol");
//
//		/* load plugin for protocol; create kbox */
//		KBoxHandler plu = kboxPlugins.get(protocol);
//		if (plu == null)
//			throw new ThinklabValidationException("kbox protocol " 
//					+ protocol + " referenced in " +
//					kboxURI + " is undefined");
//		
//		/* 
//		 * see if kbox requires ontologies that are not loaded and import them as necessary. 
//		 * FIXME these should probably be considered temporary and loaded in the current session, not
//		 * imported. 
//		 */
//		if (ontologies != null && !ontologies.trim().equals("")) {
//			String[] onts = ontologies.split(",");
//			for (String ourl : onts) {
//				try {
//					KnowledgeManager.get().getKnowledgeRepository().refreshOntology(new URL(ourl), null, false);
//				} catch (MalformedURLException e) {
//					throw new ThinklabIOException(e);
//				}
//			}
//		}
//		
//		ret = plu.createKBox(kboxURI, protocol, dataUri, properties);
//		
//		if (ret != null && wrapperCls != null) {
//			
//			try {
//				Class<?> cls = Class.forName(wrapperCls);
//				KBoxWrapper wrapper = (KBoxWrapper) cls.newInstance();
//				if (wrapper != null) {
//					wrapper.initialize(ret);
//					ret = wrapper;
//				}
//			} catch (Exception e) {
//				throw new ThinklabIOException("kbox wrapper error: " + e.getMessage());
//			}
//		}
//		
//		return ret;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#requireGlobalKBox(java.lang.String)
//	 */
//	public IKBox requireGlobalKBox(String kboxURI) throws ThinklabException {
//		
//		IKBox ret = retrieveGlobalKBox(kboxURI);
//		if (ret == null)
//			throw new ThinklabResourceNotFoundException("URI " + kboxURI + " does not identify a valid kbox");
//		return ret;
//	}
//	
//	/* (non-Javadoc)
//	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#getInstalledKboxes()
//	 */
//	public Collection<String> getInstalledKboxes() {
//		return kBoxes.keySet();
//	}
//
//	@Override
//	public Capabilities getCapabilities() {
//		throw new ThinklabRuntimeException("global kbox cannot provide capabilities");
//	}
//
//	@Override
//	public Map<String, IConcept> getMetadataSchema() throws ThinklabException {
//		throw new ThinklabStorageException("global kbox has no metadata schema");
//	}
//
//	@Override
//	public IList getObjectAsListFromID(String id,
//			HashMap<String, String> refTable) throws ThinklabException {
//		
//		IList ret = null;
//		for (IKBox k : kBoxes.values()) {
//			ret = k.getObjectAsListFromID(id, refTable);
//			if (ret != null)
//				break;
//		}
//		return ret;
//	}
//
//	@Override
//	public long getObjectCount() {
//		int tot = 0;
//		for (IKBox k : kBoxes.values()) {
//			tot += k.getObjectCount();
//		}
//		return tot;
//	}
//
//	@Override
//	public IInstance getObjectFromID(String id, ISession session)
//			throws ThinklabException {
//		IInstance ret = null;
//		for (IKBox k : kBoxes.values()) {
//			ret = k.getObjectFromID(id, session);
//			if (ret != null)
//				break;
//		}
//		return ret;
//	}
//
//	@Override
//	public IInstance getObjectFromID(String id, ISession session,
//			HashMap<String, String> refTable) throws ThinklabException {
//		
//		IInstance ret = null;
//		for (IKBox k : kBoxes.values()) {
//			ret = k.getObjectFromID(id, session, refTable);
//			if (ret != null)
//				break;
//		}
//		return ret;
//	}
//
//	@Override
//	public Properties getProperties() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String getUri() {
//		// TODO Auto-generated method stub
//		return "kbox://global";
//	}
//
//	@Override
//	public void resetToEmpty() throws ThinklabException {
//		throw new ThinklabStorageException("global kbox is read only");
//	}
//
//	@Override
//	public String storeObject(IList list, String id,
//			Map<String, IValue> metadata, ISession session)
//			throws ThinklabException {
//		throw new ThinklabStorageException("global kbox is read only");
//	}
//
//	@Override
//	public String storeObject(IList list, String id,
//			Map<String, IValue> metadata, ISession session,
//			HashMap<String, String> refTable) throws ThinklabException {
//		throw new ThinklabStorageException("global kbox is read only");
//	}
//
//	@Override
//	public String storeObject(IInstance object, String id,
//			Map<String, IValue> metadata, ISession session)
//			throws ThinklabException {
//		throw new ThinklabStorageException("global kbox is read only");
//	}
//
//	@Override
//	public String storeObject(IInstance object, String id,
//			Map<String, IValue> metadata, ISession session,
//			HashMap<String, String> references) throws ThinklabException {
//		throw new ThinklabStorageException("global kbox is read only");
//	}
//
//	@Override
//	public IQuery parseQuery(String toEval) throws ThinklabException {
//		return Constraint.parseConstraint(toEval);
//	}
//
//	@Override
//	public IQueryResult query(IQuery q) throws ThinklabException {
//
//		MultipleQueryResult ret = new MultipleQueryResult(q, -1, 0);
//
//		for (IKBox k : kBoxes.values()) {
//			if (!ret.add(k.query(q))) 
//				break;
//		}
//		
//		return ret;
//	}
//
//	@Override
//	public IQueryResult query(IQuery q, int offset, int maxResults)
//			throws ThinklabException {
//		
//		MultipleQueryResult ret = new MultipleQueryResult(q, maxResults, offset);
//
//		for (IKBox k : kBoxes.values()) {
//			if (!ret.add(k.query(q))) 
//				break;
//		}
//		
//		return ret;
//	}
//
//	@Override
//	public IQueryResult query(IQuery q, String[] metadata, int offset,
//			int maxResults) throws ThinklabException {
//		
//		MultipleQueryResult ret = new MultipleQueryResult(q, maxResults, offset);
//
//		for (IKBox k : kBoxes.values()) {
//			if (!ret.add(k.query(q, metadata, -1, 0))) 
//				break;
//		}
//		
//		return ret;
//	}
//
//	public void registerImporterClass(String fmt, Class<?> cls) {
//		importers.put(fmt, cls);
//	}
//	
//}
