package org.integratedmodelling.modelling;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
import org.integratedmodelling.thinklab.exception.ThinklabNoKMException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.Polylist;

public class ObservationCache {

	File cachePath = null;
	private File indexFile = null;
	private HashMap<String, File> index = new HashMap<String, File>();
	
	private String getSignature(IConcept obs, IObservationContext context) {
		String sig = obs.toString();
		for (IConcept e : context.getDimensions())
			sig += "," + context.getExtent(e).getSignature();
		return sig;
	}
	
	public ObservationCache(File scratchPath) throws ThinklabException {
		cachePath = new File(scratchPath + File.separator + "cache");
		cachePath.mkdir();
		
		/*
		 * read index file if not there
		 */
		this.indexFile  = new File(cachePath + File.separator + "cache.idx");
		
		if (indexFile.exists()) {
			try {
				BufferedReader fop = new BufferedReader(
						new InputStreamReader(
								new FileInputStream(indexFile)));
				
				String line = null;
				while ((line = fop.readLine()) != null) {
					String[] zz = line.split("\\ ");
					index.put(zz[0], new File(cachePath + File.separator + zz[1]));
				}
				
			} catch (Exception e) {
				throw new ThinklabIOException(e);
			}
		}
	}
	
	public Polylist getObservation(IConcept observable, IObservationContext context) throws ThinklabException {
		
		String sig = getSignature(observable, context);
		File f = index.get(sig);
		
		/*
		 * TODO if found, reconstruct observation
		 */
		if (f != null) {
			return createObservation(f);
		}
		
		return null;
	}

	private Polylist createObservation(File f) throws ThinklabException {
		
		IConcept observableClass = null;
		IConcept observationClass = null;
		Class<?> stateClass = null;
		IState state = null;
		Polylist ret = null;
		
		try {
			
			BufferedReader fop = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(indexFile)));
			
			observableClass = 
				KnowledgeManager.get().requireConcept(fop.readLine().trim()); 
			observationClass = 
				KnowledgeManager.get().requireConcept(fop.readLine().trim()); 
			stateClass =
				Class.forName(fop.readLine().trim()); 
			
			/*
			 * create state and have it read itself from the open stream
			 * FIXME won't work, the class won't be found.
			 */
			ThinklabPlugin plug = ModellingPlugin.getPluginFor(stateClass);
			ClassLoader clsl = null;
			
			try {
				clsl = plug.swapClassloader();		
				state = (IState) stateClass.newInstance();
				state.readFromStream(fop);
				
			} catch (Exception e) {
				throw new ThinklabInternalErrorException(e);
			} finally {
				plug.resetClassLoader(clsl);
			}
			
			fop.close();

			ret = Polylist.list(
					observationClass,
					Polylist.list(CoreScience.HAS_OBSERVABLE, 
							Polylist.list(observableClass)),
					Polylist.list(CoreScience.HAS_DATASOURCE, 
							state.conceptualize()));
				

		} catch (FileNotFoundException e) {
			throw new ThinklabIOException(e);
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		} catch (ClassNotFoundException e) {
			throw new ThinklabInternalErrorException(e);
		}
		
		return ret;
	}

	public void addObservation(IObservation obs, IObservationContext context) {

		String sig = getSignature(obs.getObservableClass(), context);
		
		/*
		 * TODO add signature to properties and ensure we have a file to hold
		 * the obs contents
		 */
		if (index.containsKey(sig))
			return;
		
		/*
		 * TODO add state and other necessary info to persistent cache
		 */
		File ff = null;
		 
		try {
			ff = File.createTempFile("ch", ".obs", cachePath);
			PrintWriter writer = new PrintWriter(ff);
			writer.println(obs.getObservableClass()+"");
			writer.println(obs.getObservableClass()+"");
			writer.println(obs.getObservableClass()+"");
		} catch (IOException e) {
			throw new ThinklabRuntimeException(e);
		}
		
		
		index.put(sig, ff);
	}

}
