package org.integratedmodelling.corescience.clojure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.observation.measurement.MeasurementModel;
import org.integratedmodelling.corescience.observation.ranking.DiscretizedRankingModel;
import org.integratedmodelling.corescience.values.MappedInterval;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.value.ObjectReferenceValue;
import org.integratedmodelling.utils.Polylist;

public class ClojureBridge {

	public static IValue makeDiscretizer(ISession session, Polylist mappedIntervals) throws ThinklabException {
		
		ArrayList<MappedInterval> range = new ArrayList<MappedInterval>();

		for (Object o : mappedIntervals.array()) {
			range.add(new MappedInterval(o.toString()));
		}
		
		Collections.sort(range, new Comparator<MappedInterval>() {

			@Override
			public int compare(MappedInterval o1, MappedInterval o2) {
				return o1.getInterval().compare(o2.getInterval());
			}
		});
		
		IInstance ret = session.createObject(null, CoreScience.get().DiscreteRankingModel());
		ret.setImplementation(new DiscretizedRankingModel(range.toArray(new MappedInterval[range.size()])));

		return new ObjectReferenceValue(ret);
	}
	
	public static IValue makeUnit(ISession session, String unitspecs) throws ThinklabException {
				
		return new MeasurementModel(CoreScience.get().MeasurementModel(), unitspecs);
	}
	
}
