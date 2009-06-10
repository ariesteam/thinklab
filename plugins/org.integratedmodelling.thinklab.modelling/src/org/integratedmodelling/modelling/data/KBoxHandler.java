package org.integratedmodelling.modelling.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Hashtable;

import org.integratedmodelling.clojure.utils.OptionListIterator;
import org.integratedmodelling.modelling.ModellingPlugin;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
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
	Hashtable<String, IInstance> _references = new Hashtable<String, IInstance>();
	Hashtable<String, String> _danglingRefs = new Hashtable<String, String>(); 	
	
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
				if (policy.equals("require-empty")) {
					// must be empty
				}
			} 
		}
		
	}
	
	public void addKnowledge(Object object, Object options) throws ThinklabException {
		
		String id = null;
		
		/*
		 * just ignore anything we don't know what to do with
		 */
		IInstance instance = null;
		IConcept  concept = null;
		
		if (object instanceof IInstance) {
			instance = (IInstance)object;
		} else if (object instanceof IConcept) {
			concept = (IConcept)object;
		}
		
		OptionListIterator opts = new OptionListIterator(options);
		while (opts.hasNext()) {
			
			Pair<String, Object> kv = opts.next();
			if (kv.getFirst().equals("as") && instance != null) {
				_references.put(kv.getSecond().toString(), instance);
			} else if (kv.getFirst().equals("id") && instance != null) {
				_references.put(kv.getSecond().toString(), instance);
				id = kv.getSecond().toString();
			}
		}
		
		/*
		 * store it right away unless it has unresolved references
		 */
		if (kbox != null && instance != null && _danglingRefs.get(instance.getLocalName()) == null)
			kbox.storeObject(instance, id, session);
	}
		
	public IKBox getKbox() {
		resolveForwardReferences();
		return kbox;
	}
	
	private void resolveForwardReferences() {

		for (String id : _danglingRefs.keySet()) {
			
		}
		_danglingRefs.clear();
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
				
				if (!key.contains("."))
					key = "kbox." + key;
				
				try {
					out.write(key + "=" + kv.getSecond() + "\n");
				} catch (IOException e) {
					throw new ThinklabIOException(e);
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

	public void declareForwardReference(String _forward, IProperty property, String targetId) {
		// TODO Auto-generated method stub
		
	}

	public void registerObject(String _id, IInstance _instance) {
		// TODO Auto-generated method stub
		
	}
	
}


