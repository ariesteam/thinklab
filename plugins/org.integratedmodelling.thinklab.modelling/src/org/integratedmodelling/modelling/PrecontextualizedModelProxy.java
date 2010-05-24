package org.integratedmodelling.modelling;

import java.util.ArrayList;
import java.util.HashMap;

import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.datastructures.IntelligentMap;
import org.integratedmodelling.thinklab.interfaces.query.IConformance;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.utils.Polylist;

/**
 * Proxy to a model but produce exactly one observation that uses a given state as a 
 * datasource. Only to be used if the workflow does not
 * allow changing the context in successive recontextualizations.
 * 
 * @author Ferdinando
 *
 */
public class PrecontextualizedModelProxy extends ModelProxy {

	IState state = null;
	
	class PResult extends ModelResult {

		public PResult(IModel model, IKBox kbox, ISession session) {
			super(model, kbox, session, null);
		}

		@Override
		public Polylist getResultAsList(int n,
				HashMap<String, String> references) throws ThinklabException {

			Polylist ret = model.buildDefinition(_kbox, _session, null);
			ret = ObservationFactory.addDatasource(ret, state.conceptualize());
			return ret;
		}

		@Override
		public int getResultCount() {
			return 1;
		}

		@Override
		public int getTotalResultCount() {
			return 1;
		}
		
	}
	
	public PrecontextualizedModelProxy(IModel model, IState state) {
		super(model);
		this.state = state;
	}
	
	@Override
	public ModelResult observeInternal(IKBox kbox, ISession session,
			IntelligentMap<IConformance> cp, ArrayList<Topology> extents,
			boolean acceptEmpty)
			throws ThinklabException {
		return new PResult(this, kbox, session);
	}
	
}
