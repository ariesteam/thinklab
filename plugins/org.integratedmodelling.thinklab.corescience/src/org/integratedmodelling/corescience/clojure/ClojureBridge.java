package org.integratedmodelling.corescience.clojure;

import java.util.ArrayList;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.utils.Polylist;

public class ClojureBridge {

	/**
	 * The spec coming from Clojure is like:
	 * 
	 * ('owl:Thing "10.2 2.3" ...)
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
		
		Object[] objs = mappedIntervals.array();
		
		for (int i = 0; i < objs.length; i++) {

			String cnc = objs[i++].toString();
			String ivl = objs[i].toString();
			
			plist.add(
				Polylist.list(
					"observation:NumericRangeToClassMapping",
					Polylist.list(
							"observation:hasInterval",
							Polylist.list("thinklab-core:Interval", ivl)),
					Polylist.list(
							"observation:hasTargetClass",
							cnc)
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
