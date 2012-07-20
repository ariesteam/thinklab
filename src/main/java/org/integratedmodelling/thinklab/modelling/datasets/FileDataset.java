package org.integratedmodelling.thinklab.modelling.datasets;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.integratedmodelling.common.ISerializable;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IDataset;
import org.integratedmodelling.thinklab.api.modelling.IExtent;
import org.integratedmodelling.thinklab.api.modelling.IState;
import org.integratedmodelling.utils.FolderZiper;
import org.integratedmodelling.utils.MiscUtilities;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * A dataset residing on the filesystem, with an index, pre-visualized objects and options to package it
 * in an archive file. The index is a dataindex bean, serialized using Xstream.
 * 
 * File structure after persist() is called:
 * 
 * <main dir>
 *      index.xml // use to reconstruct the index bean
 * 		thumbnails/
 * 			th_n.png*
 *      images/
 *          im_n.png*
 *      data/
 *      	d_n.xxx // any necessary data format.
 *      
 * @author Ferd
 *
 */
public class FileDataset implements IDataset {

	IContext _context;
	Index _index = new Index();
	
	int _tboxX = 40, _tboxY = 40;
	int _iboxX = 580, _iboxY = 580;
	
	public void setThumbnailBoxSize(int x, int y) {
		_tboxX = x;
		_tboxY = y;
	}
	
	public void setImageBoxSize(int x, int y) {
		_iboxX = x;
		_iboxY = y;
	}
	
	public static interface StateDescriptor {
		String getName();
		String getObservableDefinition();
	}
	
	public static class Index {
		int nStates;
		ArrayList<Object> extents = new ArrayList<Object>();
		ArrayList<Object> states = new ArrayList<Object>();
	}
	
	@Override
	public void setContext(IContext context) throws ThinklabException {
		_context = context;
	}

	@Override
	public IContext getContext() {
		return _context;
	}

	@Override
	public String persist(String location) throws ThinklabException {
		
		if (_context == null)
			return null;
		
		_index.nStates = _context.getMultiplicity();
		
		/*
		 * location must be a directory or a zip file. If zip, 
		 * record it and create a temp directory. If not, create
		 * dir if necessary and check that it's writable.
		 */
		File locDir = new File(location);
		if (location.endsWith(".zip")) {
			locDir = MiscUtilities.getPath(location);
		}
		locDir.mkdirs();
		
		/*
		 * compile index
		 */
		
		/*
		 * serialize extents
		 */
		for (IExtent e : _context.getExtents()) {
			if (e instanceof ISerializable) {
				_index.extents.add(((ISerializable)e).getSerializableBean());
			} else {
				_index.extents.add(e);
			}
		}
		
		int i = 0;
		for (final IState s : _context.getStates()) {
			
			if (s instanceof ISerializable) {
				_index.states.add(((ISerializable)s).getSerializableBean());
			} else {
				_index.states.add(s);
			}

			/*
			 * produce images
			 */
			
			
			/*
			 * produce datasets
			 */
		}
		
		/*
		 * serialize index to xml
		 */
		XStream xstream = new XStream(new StaxDriver());
		String xml = xstream.toXML(_index);
		try {
			FileUtils.writeStringToFile(new File(locDir + File.separator + "index.xml"), xml);
		} catch (IOException e1) {
			throw new ThinklabIOException(e1);
		}
		
		
		/*
		 * if location is zip, zip things up
		 */
		if (location.endsWith(".zip")) {
			FolderZiper.zipFolder(locDir.toString(), location);
		}
		
		return location;
	}

	@Override
	public void restore(String location) throws ThinklabException {
		// TODO Auto-generated method stub

	}

}
