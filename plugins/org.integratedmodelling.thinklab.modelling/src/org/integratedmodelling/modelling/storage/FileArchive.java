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
package org.integratedmodelling.modelling.storage;

import java.io.File;
import java.io.FileOutputStream;

import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.modelling.ModellingPlugin;
import org.integratedmodelling.modelling.interfaces.IDataset;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.storage.IPersistentObject;
import org.integratedmodelling.utils.MiscUtilities;

public class FileArchive implements IDataset {

	IContext context = null;
	
	/* 
	 * the main directory where the archives live. If not set, chosen from the 
	 * THINKLAB_ARCHIVE_DIR environmental variable, defaulting to the modelling
	 * plugin's data area + "/archive".
	 */
	private File directory;

	@Override
	public String toString() {
		return "[" + getLocation() + "]";
	}
	
	/* 
	 * the name of the file directory under the main dir. If not set it will be set
	 * from the name of the main observable and given a date suffix.
	 */
	private String location = null;
	
	public FileArchive() throws ThinklabException {
		directory = getMainDirectory();
	}
	
	public FileArchive(File directory) throws ThinklabException {
		this.directory = directory;
		this.directory.mkdirs();
	}

	public FileArchive(IContext context, File directory) throws ThinklabException {
		this.directory = directory;
		this.directory.mkdirs();
		setContext(context);
	}

	public FileArchive(IContext context) throws ThinklabException {
		this();
		setContext(context);
	}

	@Override
	public void setContext(IContext context)
			throws ThinklabException {
		
		if (this.context != null) {
			((ObservationContext)(this.context)).mergeStates((IObservationContext) context);
		} else {
			this.context = context;
		}
	}

	@Override
	public IContext getContext()
			throws ThinklabException {
		return this.context;
	}

	/**
	 * Return the name of the folder we are storing our states under.
	 * @return
	 */
	public String getLocation() {
		
		if (location == null) {
			location = 
				((IObservationContext)context).getObservation().getObservableClass().
					toString().replaceAll(":",".").toLowerCase() +
				"." + 
				MiscUtilities.getDateSuffix();
		}
		
		return location;
	}
	
	@Override
	public String persist() throws ThinklabException {
				
		/*
		 * persist context
		 */
		if (context instanceof IPersistentObject) {

			File ff = new File(getLocation() + File.separator + "context.dat");
			FileOutputStream fop;
			try {
				fop = new FileOutputStream(ff);
				if (!((IPersistentObject)context).serialize(fop)) {
					fop.close();
					ff.delete();
					new ThinklabInternalErrorException(
							"file archiver: serialization of context for " + 
							((IObservationContext)context).getObservation().getObservableClass() + 
							" failed"); 
				}
				fop.close();
			} catch (Exception e) {
				throw new ThinklabIOException(e);
			}
		} else {
			throw new ThinklabInternalErrorException("file archiver: trying to persist a non-persistent context");
		}
		
		/*
		 * persist all states
		 */
		for (IState state : context.getStates()) {
			
			File dir = getStateDirectory(state.getObservableClass());
			
			if (state instanceof IPersistentObject) {
				
				File ff = new File(dir + File.separator + "state.dat");
				FileOutputStream fop;
				try {
					fop = new FileOutputStream(ff);
					if (!((IPersistentObject)state).serialize(fop)) {
						fop.close();
						ff.delete();
						new ThinklabInternalErrorException(
								"file archiver: serialization of state of " + 
								state.getObservableClass() + 
								" failed"); 
					}
					fop.close();
				} catch (Exception e) {
					throw new ThinklabIOException(e);
				}
			} else {
				throw new ThinklabInternalErrorException(
						"file archiver: trying to persist a non-persistent state: " + 
						state.getObservableClass());
			}
		}
		
		return getLocation();
	}

	@Override
	public void restore(String location) throws ThinklabException {

		/*
		 * load contexts from location
		 */
		
		/*
		 * load all states
		 */
		
	}

	public File getStateDirectory(IConcept c) {
		
		File ret = new File(
				directory + 
				File.separator + 
				getLocation() + 
				File.separator +
				c.toString().replaceAll(":",".").toLowerCase());
		
		ret.mkdirs();
		
		return ret;
	}
	
	public String getStateRelativePath(IConcept c) {
		
		return
			getLocation() + 
			"/" +
			c.toString().replaceAll(":",".").toLowerCase();
	}
	
	public static File getDefaultDirectory() {
		
		File ret = null;
		String fenv = System.getenv("THINKLAB_ARCHIVE_DIR");
		if (fenv != null) {
			ret = new File(fenv);			
		}
	
		if (ret == null) {
			try {
				ret = 
					new File(
						ModellingPlugin.get().getScratchPath() + 
						File.separator +
						"archive");
			} catch (ThinklabException e) {
				throw new ThinklabRuntimeException(e);
			}			
		}
		ret.mkdirs();
		return ret;
	}
	
	public File getMainDirectory() {
		
		File ret = directory;
		if (ret == null) {
			ret = getDefaultDirectory();
		}
		return ret;
	}

	public File getDirectory() {
		return new File(directory + File.separator + getLocation());
	}
	
}
