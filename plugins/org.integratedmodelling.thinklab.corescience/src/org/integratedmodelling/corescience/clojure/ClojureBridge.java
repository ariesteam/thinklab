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

	/**
	 * The spec coming from Clojure is like:
	 * 
	 * ((owl:Thing "10.2 2.3")*)
	 * 
	 * @param session
	 * @param mappedIntervals
	 * @return
	 * @throws ThinklabException
	 */
	public static Polylist makeDiscretizer(Polylist mappedIntervals) throws ThinklabException {

		ArrayList<Object> list = new ArrayList<Object>();
		ArrayList<Object> plist = new ArrayList<Object>();
		
		list.add(CoreScience.get().DiscreteRankingModel());
		
		plist.add("observation:hasClassMapping");
		
		for (Object o : mappedIntervals.array()) {
			
			Polylist pl = (Polylist)o;
			
			plist.add(
				Polylist.list(
					"observation:NumericRangeToClassMapping",
					Polylist.list(
							"observation.hasInterval",
							Polylist.list("thinklab-core:Interval", pl.second().toString())),
							Polylist.list(
									"observation:hasTargetClass",
									pl.first().toString())
				));			
		}
		list.add(Polylist.PolylistFromArrayList(plist));
		
		return Polylist.PolylistFromArrayList(list);
	}
	
	public static Polylist makeUnit(String unitspecs) throws ThinklabException {

		ArrayList<Object> list = new ArrayList<Object>();
		
		list.add("measurement:Unit");
		list.add(unitspecs);
		
		return Polylist.PolylistFromArrayList(list);

	}
	
}
