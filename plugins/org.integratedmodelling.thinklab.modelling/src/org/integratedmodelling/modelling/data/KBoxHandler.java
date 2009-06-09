package org.integratedmodelling.modelling.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import org.integratedmodelling.modelling.ModellingPlugin;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;

import clojure.lang.PersistentList;

/**
 * Interacts with a kbox on behalf of a with-kbox form.
 * 
 * @author Ferdinando Villa
 *
 */
public class KBoxHandler {

	IKBox kbox = null;
		
	public void setKbox(Object kbox, Object options) {
		
		/*
		 * input could be anything that points to a kbox
		 */
	}
	
	public void addKnowledge(Object kbox, Object options) {
		
		/*
		 * just ignore anything we don't know what to do with
		 */
	}
		
	public IKBox getKbox() {

		resolveForwardReferences();
		
		return kbox;
	}
	
	private void resolveForwardReferences() {
		// TODO Auto-generated method stub
		
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
			
			PersistentList pl = (PersistentList) options;
			Iterator<?> it = pl.iterator();
			
			while (it.hasNext()) {
				
				String key = it.next().toString();
				Object val = it.next();
				
				if (!key.startsWith(":"))
					continue;
				
				key = key.substring(1);
				if (!key.contains("."))
					key = "kbox." + key;
				
				try {
					out.write(key + "=" + val + "\n");
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

	public void declareForwardReference(String _forward, String targetId) {
		// TODO Auto-generated method stub
		
	}

	public void registerObject(String _id, IInstance _instance) {
		// TODO Auto-generated method stub
		
	}
	
}


