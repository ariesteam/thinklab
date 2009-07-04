package org.integratedmodelling.modelling.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.integratedmodelling.clojure.utils.OptionListIterator;
import org.integratedmodelling.modelling.ModellingPlugin;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IProperty;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.literals.Value;
import org.integratedmodelling.utils.Pair;

/**
 * Interacts with a kbox on behalf of a with-kbox form.
 * 
 * @author Ferdinando Villa
 *
 */
public class KBoxHandler {

	IKBox kbox = null;
	String kpref = "";
	ISession session = null;
	boolean _disabled = false;
	HashMap<String, Pair<IInstance, HashMap<String, IValue>>> _references = 
		new HashMap<String, Pair<IInstance, HashMap<String, IValue>>>();
	ArrayList<ReferenceRecord> _danglingRefs = new ArrayList<ReferenceRecord>(); 	
	HashSet<String> _objectsWithRefs = new HashSet<String>();
	HashMap<String,String> idToLocalName = new HashMap<String, String>();
	HashMap<String,String> localNameToId = new HashMap<String, String>();
	
	class ReferenceRecord {
		
		public IInstance target;
		public String reference;
		public IProperty property;
		
		public ReferenceRecord(IInstance t, IProperty prop, String ref) {
			target = t;
			reference = ref;
			property = prop;
		}
		
		public void resolve() throws ThinklabException {
			
			Pair<IInstance,HashMap<String, IValue>> refd = _references.get(idToLocalName.get(reference));
			if (refd == null) {
				throw new ThinklabResourceNotFoundException("kbox doesn't define forward reference " + reference);
			}
			target.addObjectRelationship(property, refd.getFirst());
		}
	}
	
	public KBoxHandler(ISession session) {
		this.session = session;
	}
	
	public void setKbox(Object kbox, Object options) throws ThinklabException {
		
		/*
		 * input could be anything that points to a kbox
		 */
		if (kbox instanceof IKBox) {
			this.kbox = (IKBox)kbox;
		}
		
		OptionListIterator opts = new OptionListIterator(options);
		while (opts.hasNext()) {
			
			Pair<String, Object> kv = opts.next();
			if (kv.getFirst().equals("id-prefix")) {

				kpref = kv.getSecond().toString();
				
			} else if (kv.getFirst().equals("storage-policy")) {
				
				String policy = kv.getSecond().toString();
				if (policy.equals(":disable-unless-empty") && this.kbox != null) {
					if (this.kbox.getObjectCount() > 0l) {
						_disabled = true;
						ModellingPlugin.get().logger().info("kbox not empty: any object definitions ignored");
					}
				} else if (policy.equals(":recreate-always") && this.kbox != null) {
					this.kbox.resetToEmpty();
				}
			} 
		}
	}
	
	
	public static HashMap<String, IValue> fixMetadata (Map<?,?> metadata) throws ThinklabException {
	
		HashMap<String, IValue> md = null;
		if (metadata != null) {
			md = new HashMap<String, IValue>();
			for (Object k : metadata.keySet()) {
				// empty metadata - ignore
				if (metadata.get(k) == null)
					continue;
				String s = k.toString();
				if (s.startsWith(":")) 
					s = s.substring(1);
				md.put(s, Value.getValueForObject(metadata.get(k)));
			}
		}
		return md;
	}
	
	public void addKnowledge(Object object, Object options, Map<?,?> metadata) throws ThinklabException {
		
		String id = null;
		
		/*
		 * just ignore anything we don't know what to do with
		 */
		IInstance instance = null;
		IConcept  concept = null;
		String iid = null;
		
		if (object instanceof IInstance) {
			instance = (IInstance)object;
			iid = instance.getLocalName();
		} else if (object instanceof IConcept) {
			concept = (IConcept)object;
		}
		
		OptionListIterator opts = new OptionListIterator(options);
		while (opts.hasNext()) {
			
			Pair<String, Object> kv = opts.next();
			if (kv.getFirst().equals("id") && instance != null) {
				id = kv.getSecond().toString();
			}
		}
		
		/*
		 * fix metadata if any
		 */
		HashMap<String, IValue> md = fixMetadata(metadata);
		
		if (id == null)
			id = iid;
		
		/*
		 * store it right away unless it has unresolved references
		 */
		if (kbox != null && instance != null) {
			this.idToLocalName.put(id, iid);
			this.localNameToId.put(iid, id);
			if (_objectsWithRefs.contains(instance.getLocalName()))
				_references.put(iid, new Pair<IInstance, HashMap<String, IValue>>(instance, md));
			else
				kbox.storeObject(instance, id, md, session);
		}
	}
		
	public IKBox getKbox() throws ThinklabException {
		resolveForwardReferences();
		return kbox;
	}
	
	private void resolveForwardReferences() throws ThinklabException {

		for (ReferenceRecord ref : _danglingRefs) {
			ref.resolve();
		}
		
		for (String iid : _references.keySet()) {
			Pair<IInstance, HashMap<String, IValue>> kkd = _references.get(iid);
			kbox.storeObject(kkd.getFirst(), localNameToId.get(iid), kkd.getSecond(), session);
		}

		
		_danglingRefs.clear();
		_references.clear();
	}

	public synchronized IKBox createKbox(Object id, Object ur, Object options) throws ThinklabException {
		
		IKBox  ret = null;
		String name = id.toString();
		String uri  = ur.toString();

		File   tmp  = null;
		Writer out = null;
		try {
			tmp = new File(ModellingPlugin.get().getScratchPath() + File.separator + name + ".kbox");
			out = new BufferedWriter(new FileWriter(tmp));
			out.write("kbox.uri=" + uri + "\n");
			
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
		
		if (options != null) {
			
			OptionListIterator it = new OptionListIterator(options);
			
			while (it.hasNext()) {
				
				Pair<String, Object> kv = it.next();
				String key = kv.getFirst();
				
				if (key.equals("metadata")) {
					
					/* set metadata into props */
					OptionListIterator ol = new OptionListIterator(kv.getSecond());
					while (ol.hasNext()) {
						Pair<String, Object> kkv = ol.next();
						try {
							out.write(IKBox.KBOX_METADATA_PROPERTY_PREFIX + kkv.getFirst() + "=" + kkv.getSecond() + "\n");
						} catch (IOException e) {
							throw new ThinklabIOException(e);
						}
					}
					
				} else if (key.equals("parameters")) {
					
					/* set metadata into props */
					OptionListIterator ol = new OptionListIterator(kv.getSecond());
					while (ol.hasNext()) {
						Pair<String, Object> kkv = ol.next();
						try {
							out.write(IKBox.KBOX_PARAMETER_PREFIX + kkv.getFirst() + "=" + kkv.getSecond() + "\n");
						} catch (IOException e) {
							throw new ThinklabIOException(e);
						}
					}
					
				} else {
				
					if (!key.contains("."))
						key = "kbox." + key;
				
					try {
						out.write(key + "=" + kv.getSecond() + "\n");
					} catch (IOException e) {
						throw new ThinklabIOException(e);
					}
				}
			}
			
			try {
				out.close();
			} catch (IOException e) {
				throw new ThinklabIOException(e);
			}
			
		}
		
		try {
			ret = KnowledgeManager.get().getKBoxManager().requireGlobalKBox(tmp.toURI().toURL().toString());
		} catch (Exception e) {
			throw new ThinklabValidationException(e);
		}

		return ret;
	}

	public void declareForwardReference(IInstance instance, IProperty property, String targetId) {
		_danglingRefs.add(new ReferenceRecord(instance, property, targetId));
		_objectsWithRefs.add(instance.getLocalName());
	}

	public void registerObject(String _id, IInstance _instance) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean isDisabled() {
		return _disabled;
	}
}


