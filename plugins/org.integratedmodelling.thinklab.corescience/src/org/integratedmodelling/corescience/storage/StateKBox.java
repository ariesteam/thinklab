package org.integratedmodelling.corescience.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQuery;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.interfaces.storage.IKBoxCapabilities;
import org.integratedmodelling.utils.Polylist;

/**
 * Access a result observation (with IStates for each indirect observation) as a kbox, so we can
 * chain models in the API.
 * 
 * @author Ferdinando
 *
 */
public class StateKBox implements IKBox {

	private IObservation _observation;

	public StateKBox(IObservation observation) {
		this._observation = observation;
	}
	
	@Override
	public IKBoxCapabilities getKBoxCapabilities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, IConcept> getMetadataSchema() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Polylist getObjectAsListFromID(String id,
			HashMap<String, String> refTable) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getObjectCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IInstance getObjectFromID(String id, ISession session)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IInstance getObjectFromID(String id, ISession session,
			HashMap<String, String> refTable) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Properties getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUri() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resetToEmpty() throws ThinklabException {
		// TODO Auto-generated method stub

	}

	@Override
	public String storeObject(Polylist list, String id,
			Map<String, IValue> metadata, ISession session)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String storeObject(Polylist list, String id,
			Map<String, IValue> metadata, ISession session,
			HashMap<String, String> refTable) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String storeObject(IInstance object, String id,
			Map<String, IValue> metadata, ISession session)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String storeObject(IInstance object, String id,
			Map<String, IValue> metadata, ISession session,
			HashMap<String, String> references) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IQuery parseQuery(String toEval) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IQueryResult query(IQuery q) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IQueryResult query(IQuery q, int offset, int maxResults)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IQueryResult query(IQuery q, String[] metadata, int offset,
			int maxResults) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

}
