package org.integratedmodelling.modelling.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Hashtable;
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
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
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
	Hashtable<String, IInstance> _references = new Hashtable<String, IInstance>();
	ArrayList<ReferenceRecord> _danglingRefs = new ArrayList<ReferenceRecord>(); 	
	
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
			
			IInstance refd = _references.get(reference);
			if (refd == null) {
				throw new ThinklabResourceNotFoundException("kbox doesn't define forward reference " + reference);
			}
			target.addObjectRelationship(property, refd);
		}
	}
	
	public KBoxHandler(ISession session) {
		this.session = session;
	}
	
	public void setKbox(Object kbox, Object options) {
		
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
						
				}
			} 
		}
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
			if (kv.getFirst().equals("as") && instance != null) {
				iid = kv.getSecond().toString();
				_references.put(iid, instance);
			} else if (kv.getFirst().equals("id") && instance != null) {
				id = iid = kv.getSecond().toString();
				_references.put(iid, instance);
			}
		}
		
		/*
		 * store it right away unless it has unresolved references
		 */
		if (kbox != null && instance != null && _references.get(iid) == null)
			kbox.storeObject(instance, id, null, session);
	}
		
	public IKBox getKbox() throws ThinklabException {
		resolveForwardReferences();
		return kbox;
	}
	
	private void resolveForwardReferences() throws ThinklabException {

		for (String iid : _references.keySet()) {
			kbox.storeObject(_references.get(iid), iid, null, session);
		}
		for (ReferenceRecord ref : _danglingRefs) {
			ref.resolve();
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
					
					/* TODO set metadata into props */
					
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
	}

	public void registerObject(String _id, IInstance _instance) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean isDisabled() {
		return _disabled;
	}
}


