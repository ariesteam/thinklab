package org.integratedmodelling.modelling.storage;

import java.io.File;

import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.modelling.ModellingPlugin;
import org.integratedmodelling.modelling.interfaces.IDataset;
import org.integratedmodelling.thinklab.exception.ThinklabException;

public class FileArchive implements IDataset {

	IObservationContext context = null;
	
	/* 
	 * the main directory where the archives live. If not set, chosen from the 
	 * THINKLAB_ARCHIVE_DIR environmental variable, defaulting to the modelling
	 * plugin's data area + "/archive".
	 */
	private File directory;

	/* 
	 * the name of the file directory under the main dir. If not set it will be set
	 * from the name of the main observable and given a date suffix.
	 */
	private String location = null;
	
	public FileArchive() throws ThinklabException {
		this.directory = new File(System.getenv("THINKLAB_ARCHIVE_DIR"));
		if (this.directory == null) {
			this.directory = 
				new File(
					ModellingPlugin.get().getScratchPath() + 
					File.separator +
					"archive");			
		}
		this.directory.mkdir();
	}
	
	@Override
	public void setContext(IObservationContext context)
			throws ThinklabException {
		
		if (this.context != null) {
			((ObservationContext)(this.context)).mergeStates(context);
		} else {
			this.context = context;
		}
	}

	@Override
	public IObservationContext getContext(IObservationContext context)
			throws ThinklabException {
		return this.context;
	}

	@Override
	public void persist() throws ThinklabException {
		
		if (location == null) {
			location = 
				context.getObservation().getObservableClass().toString().replaceAll(":",".");
		}
		
		/*
		 * make dir structure
		 */
		
		/*
		 * persist context
		 */
		
		/*
		 * persist all states
		 */
		
	}

	@Override
	public void restore() throws ThinklabException {

		/*
		 * load contexts from location
		 */
		
		/*
		 * load all states
		 */
		
	}

	
	
}
