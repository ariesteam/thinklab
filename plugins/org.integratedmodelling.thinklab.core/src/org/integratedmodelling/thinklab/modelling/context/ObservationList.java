package org.integratedmodelling.thinklab.modelling.context;

import java.util.Iterator;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.observation.IContext;
import org.integratedmodelling.thinklab.api.modelling.observation.IObservation;
import org.integratedmodelling.thinklab.api.modelling.observation.IObservationList;

public class ObservationList implements IObservationList {

	IContext context;
	IModel   model;
	
	
	@Override
	public IContext resolve(int index) throws ThinklabException {
		// TODO Auto-generated method stub
		
		
		/*
		 * 
		 */
		
		return null;
	}

	@Override
	public Iterator<IObservation> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IObservation get(int index) {
		// TODO Auto-generated method stub
		return null;
	}

}
