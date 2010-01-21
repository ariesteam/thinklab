package org.integratedmodelling.modelling;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.thinklab.PersistenceManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.storage.IPersistentObject;
import org.integratedmodelling.utils.InputSerializer;
import org.integratedmodelling.utils.MalformedListException;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.OutputSerializer;
import org.integratedmodelling.utils.Polylist;

public class ObservationCache {

	File cachePath = null;
	private File indexFile = null;
	private HashMap<String, IState> dynamicCache = new HashMap<String, IState>();
	private HashMap<String, File> persistentCache = null;
	private HashMap<String, String> classIndex = null;
	private HashMap<String, Polylist> observableIndex = null;
	
	private String getSignature(IConcept obs, IObservationContext context, String aux) {
		String sig = obs.toString();
		if (context != null)
			for (IConcept e : context.getDimensions())
				sig += "," + context.getExtent(e).getSignature();
		
		if (aux != null)
			sig += "_" + aux;
		
		return sig;
	}
	
	public ObservationCache(File scratchPath, boolean isPersistent) throws ThinklabException {
		
		if (isPersistent) {
					
			persistentCache = new HashMap<String, File>();
			classIndex  = new HashMap<String, String>();
			observableIndex = new HashMap<String, Polylist>();
			cachePath = new File(scratchPath + File.separator + "cache");
			cachePath.mkdir();
		
			/*
			 * read index file if not there
			 */
			this.indexFile  = new File(cachePath + File.separator + "cache.idx");
		
			if (indexFile.exists()) {

				InputSerializer inp = new InputSerializer(indexFile);
				
				int n = inp.readInteger();
				for (int i = 0; i < n; i++) {
					String sig = inp.readString();
					Polylist obs = null;
					try {
						obs = Polylist.parse(inp.readString());
					} catch (MalformedListException e) {
						throw new ThinklabRuntimeException(e);
					}
					String clazz = inp.readString();
					String file  = inp.readString();
					
					persistentCache.put(sig, new File(cachePath + File.separator + file));
					classIndex.put(sig, clazz);
					observableIndex.put(sig, obs);
				}
				
				inp.close();
				
			}
			
		}
	}
	
	private void updateCache() throws ThinklabIOException {

		OutputSerializer out = new OutputSerializer(this.indexFile);
		int n = persistentCache.size();
		out.writeInteger(n);
		for (String sig : persistentCache.keySet()) {
			out.writeString(sig);
			out.writeString(observableIndex.get(sig).toString());
			out.writeString(classIndex.get(sig));
			out.writeString(MiscUtilities.getFileName(persistentCache.get(sig).toString()));
		}
		out.close();
	}
	
	public Polylist getObservation(IConcept observable, IObservationContext context, String aux) throws ThinklabException {
		
		String sig = getSignature(observable, context, aux);
		IState s = dynamicCache.get(sig); 
		
		if (s != null) {
			return createObservation(observable, s);
		}
		
		if (persistentCache != null) {
			
			File f = persistentCache.get(sig);

			if (f != null) {
				ModellingPlugin.get().logger().info("reading observation from cache: " + sig);
				return createObservation(classIndex.get(sig), observableIndex.get(sig), f);
			}
		}
		
		return null;
	}

	private Polylist createObservation(String clazz, Polylist observable, File f) throws ThinklabException {
		
		IPersistentObject state = 
			PersistenceManager.get().createPersistentObject(clazz);
		
		try {
			state.deserialize(new FileInputStream(f));
		} catch (FileNotFoundException e) {
			throw new ThinklabIOException(e);
		}
		
		Polylist ret = Polylist.list(
					CoreScience.OBSERVATION,
					Polylist.list(CoreScience.HAS_OBSERVABLE, 
							observable),
					Polylist.list(CoreScience.HAS_DATASOURCE, 
							((IState)state).conceptualize()));
				
			
		return ret;
	}
	
	public Polylist createObservation(IConcept observable, IState state) throws ThinklabException {

		// FIXME it's creating a stupid observation - must check that this workss
		return Polylist.list(
				CoreScience.OBSERVATION,
				Polylist.list(CoreScience.HAS_OBSERVABLE, 
						Polylist.list(observable)),
				Polylist.list(CoreScience.HAS_DATASOURCE, 
						state.conceptualize()));
	}

	public synchronized void addObservation(IObservation obs, IObservationContext context, String aux)  {

		String sig = getSignature(obs.getObservableClass(), context, aux);
		
		if (dynamicCache.containsKey(sig))
			return;
		
		dynamicCache.put(sig, (IState) obs.getDataSource());
		
		/*
		 * TODO add signature to properties and ensure we have a file to hold
		 * the obs contents
		 */
		if (persistentCache == null || persistentCache.containsKey(sig))
			return;
		
		if ( !(obs.getDataSource() instanceof IPersistentObject)) 
			return;
		
		/*
		 * TODO add state and other necessary info to persistent cache
		 */
		File ff = null;
		 
		try {
			
			ModellingPlugin.get().logger().info("adding state to persistent cache: " + sig);
			
			ff = File.createTempFile("chs", ".obs", cachePath);
			FileOutputStream fop = new FileOutputStream(ff);
			((IPersistentObject)obs.getDataSource()).serialize(fop);
			fop.close();
			persistentCache.put(sig, ff);
			classIndex.put(sig, obs.getDataSource().getClass().getCanonicalName());
			observableIndex.put(sig, obs.getObservable().toList(null));
			updateCache();
		} catch (Exception e) {
			throw new ThinklabRuntimeException(e);
		}
	}
	
	public void resetDynamicCache() {
		dynamicCache.clear();
	}
	
}
