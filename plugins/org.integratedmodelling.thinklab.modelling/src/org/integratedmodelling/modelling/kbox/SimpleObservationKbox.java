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
package org.integratedmodelling.modelling.kbox;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.modelling.ObservationFactory;
import org.integratedmodelling.opal.OPALValidator;
import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.constraint.Restriction;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.datastructures.IntelligentMap;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQuery;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.interfaces.storage.IKBoxCapabilities;
import org.integratedmodelling.thinklab.kbox.ListQueryResult;
import org.integratedmodelling.thinklab.owlapi.Session;
import org.integratedmodelling.utils.Polylist;
import org.integratedmodelling.utils.instancelist.InstanceList;
import org.integratedmodelling.utils.instancelist.RelationshipList;

/**
 * A simple kbox implementation intended for a small number of observation. Will match
 * observables and extents using in-memory operations. The metadata fields "space" and
 * "time" will be used to match the corresponding extents. Initialized using a set of 
 * OPAL files explicitly loaded. NO OTHER FIELD will be matched.
 * 
 * @author Ferdinando Villa
 */
public class SimpleObservationKbox implements IKBox {

	File dataDir = null;
	Properties properties = new Properties();
	boolean _initialized = false;
	
	
	class DataHolder {
		
		class ObsHolder {
			
			IExtent spaceExtent = null;
			IExtent timeExtent = null;
			Polylist observation = null;

			public ObsHolder(Polylist observation, IExtent space, IExtent time) {
				this.observation = observation;
				this.spaceExtent = space;
				this.timeExtent = time;
			}
		}
		
		IConcept concept = null;
		ArrayList<ObsHolder> observations = new ArrayList<ObsHolder>();
		
		DataHolder(IConcept c) {
			this.concept = c;
		}
		
		void add(Polylist observation, IExtent space, IExtent time) {
			observations.add(new ObsHolder(observation, space, time));
		}
		
		Collection<Polylist> find(String spaceOp, IExtent space, String timeOp, IExtent time) {
			return null;
		}
	}
	
	class Data extends IntelligentMap<DataHolder>  {

		// add all obs, process observables and extents
		public void process(Collection<Polylist> obss) throws ThinklabException {

			// get a session and throw it away when finished
			ISession session = new Session();
			
			for (Polylist pl : obss) {
				InstanceList il = new InstanceList(pl);
				IConcept obs = il.getTargetConcept(CoreScience.HAS_OBSERVABLE);
				
				IExtent spaceExt = null;
				IExtent timeExt = null;
				
				// extract extents
				for (RelationshipList rl : il.getRelationships(CoreScience.HAS_EXTENT)) {
					
					IInstance inst = 
						session.createObject(rl.getValue().asInstanceList().asList());
					IObservation extobs = 
						ObservationFactory.getObservation(inst);
					if (extobs instanceof Topology) {
						if (extobs.getObservableClass().is(Geospace.get().SpaceObservable()))
							spaceExt = ((Topology)extobs).getExtent();
						else if (extobs.getObservableClass().is(Geospace.get().SpaceObservable()))
							timeExt = ((Topology)extobs).getExtent();
					}
				}
				
				/*
				 * add an entry for the obs in data
				 */	
				DataHolder dh = new DataHolder(obs);
				dh.add(pl, spaceExt, timeExt);
				data.put(obs, dh);
			}
		}
	}
	
	Data data = new Data();
	
	private void initialize() throws ThinklabException {

		if (!dataDir.exists() || !dataDir.isDirectory() || !dataDir.canRead())
			throw new ThinklabIOException(
					"SimpleObservationKBox:  data directory " +
					dataDir +
					" is not readable");
		/*
		 * Read in all XML files found in kbox directory
		 */
		for (String s : dataDir.list()) {
			if (s.endsWith(".xml")) {
				
				URL url;
				try {
					url = new File(dataDir + File.separator + s).toURI().toURL();
				} catch (MalformedURLException e) {
					throw new ThinklabIOException(e);
				}
				
				addOPALResource(url);
			}
		}
				
		_initialized = true;
	}
	
	public void addOPALResource(URL f) throws ThinklabException {
		
		Collection<Polylist> obss =
			new OPALValidator().validateToLists(f);

		/*
		 * TODO process list, extracting the observable concept and
		 * the space/time extents
		 */
		
		if (obss != null && obss.size() > 0)
			data.process(obss);
	}
	
	public IKBoxCapabilities getKBoxCapabilities() {
		// TODO Auto-generated method stub
		return null;
	}

	public Polylist getObjectAsListFromID(String id,
			HashMap<String, String> refTable) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public IInstance getObjectFromID(String id, ISession session)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public IInstance getObjectFromID(String id, ISession session,
			HashMap<String, String> refTable) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public String storeObject(Polylist list, String id, Map<String, IValue> metadata, ISession session)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public String storeObject(Polylist list, String id, Map<String, IValue> metadata,
			ISession session, HashMap<String, String> refTable) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public String storeObject(IInstance object, String id, Map<String, IValue> metadata, ISession session)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public String storeObject(IInstance object, String id, Map<String, IValue> metadata,
			ISession session, HashMap<String, String> references) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public IQuery parseQuery(String toEval) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * We only serve global data, so we ignore everything except the observable;
	 * plus, we only care for benefit observables, and we return predefined
	 * data for each one, loaded from an OPAL file with the same name as the
	 * benefit (- replaces : in the semantic type) and .xml as extension, in 
	 * a directory set by a plugin property.
	 */
	public IQueryResult query(IQuery q) throws ThinklabException {
		
		if (!_initialized)
			initialize();
		/*
		 * check if we're asking for something that observes a benefit
		 * if so, return all data we have for it
		 * otherwise, return null.
		 */
		Constraint c = (Constraint) q;
		
		/*
		 * extract the observable from the query and the "space" and "time" metadata
		 * fields
		 */
		Restriction rs = c.findRestriction(CoreScience.HAS_OBSERVABLE);

		IConcept observable = rs.getSubConstraint().getConcept();
		
		/* retrieve all lists representing observations with given observable */
		ArrayList<Polylist> results = new ArrayList<Polylist>();
		
//		for (Polylist pl : data) {
//
//			InstanceList il = new InstanceList(pl);
//			if (!il.getTargetConcept(CoreScience.HAS_OBSERVABLE).is(observable)) {
//				continue;
//			}
//			
//		}
		
		return new ListQueryResult(q, this, results, properties);
			
	}

	public IQueryResult query(IQuery q, int offset, int maxResults)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public IQueryResult query(IQuery q, String[] metadata, int offset,
			int maxResults) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUri() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Properties getProperties() {
		return properties;
	}

	@Override
	public long getObjectCount() {
		if (!_initialized)
			try {
				initialize();
			} catch (ThinklabException e) {
				throw new ThinklabRuntimeException(e);
			} 
		return data.size();
	}

	@Override
	public void resetToEmpty() throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, IConcept> getMetadataSchema() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

}
