package org.integratedmodelling.geospace.coverage;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.list.Polylist;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.runtime.ISession;

public class InstanceCoverageLoader extends CoverageHandler {

	ArrayList<IInstance> instances = null;
	
	public InstanceCoverageLoader(URL url, Properties properties)
			throws ThinklabException {
		super(url, properties);
	}

	public Collection<IInstance> loadObservations(ISession session) throws ThinklabException {

		instances = new ArrayList<IInstance>();
		
		process();
		
		for (Polylist list : olist) {
			System.out.println(Polylist.prettyPrint(list));
			instances.add(session.createObject(list));
		}
		
		return instances;
	}

}
