package org.integratedmodelling.modelling.commands;

import java.util.ArrayList;
import java.util.HashMap;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.implementations.observations.RasterGrid;
import org.integratedmodelling.geospace.interfaces.IGazetteer;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.modelling.ObservationFactory;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.constraint.DefaultConformance;
import org.integratedmodelling.thinklab.constraint.Restriction;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.kbox.KBoxManager;
import org.integratedmodelling.utils.Polylist;

@ThinklabCommand(
		name="observe",
		description="look for observations of a concept in a context",
		argumentNames="concept",
		argumentTypes="thinklab-core:Text",
		argumentDescriptions="the concept to build a model for or the model id",
		optionalArgumentNames="context",
		optionalArgumentDefaultValues="_NONE_",
		optionalArgumentDescriptions="id of a spatial feature to define the spatial context",
		optionalArgumentTypes="thinklab-core:Text",
		optionArgumentLabels="all kboxes",
		optionLongNames="kbox",
		optionNames="k",
		optionTypes="thinklab-core:Text",
		optionDescriptions="kbox",
		returnType="observation:Observation")
public class ObserveCommand implements ICommandHandler {

	IObservationContext ctx = null;
	HashMap<IConcept, IState> states = new HashMap<IConcept, IState>();
	
	@Override
	public IValue execute(Command command, ISession session)
			throws ThinklabException {
		
		String concept = command.getArgumentAsString("concept");
		
		IKBox kbox = KBoxManager.get();
		if (command.hasOption("kbox"))
			kbox = KBoxManager.get().requireGlobalKBox(command.getOptionAsString("kbox"));
		
		
		ArrayList<Topology> extents = new ArrayList<Topology>();
		
		if (command.hasArgument("context")) {
			
			int res = 256;	
			ShapeValue roi = null;
			IQueryResult result = 
				Geospace.get().lookupFeature(
						command.getArgumentAsString("context"));
			if (result.getTotalResultCount() > 0)
				roi = (ShapeValue) result.getResultField(0, IGazetteer.SHAPE_FIELD);
				
			if (roi != null) {
				
				IInstance where = 
					session.createObject(RasterGrid.createRasterGrid(roi, res));
				extents.add((Topology) ObservationFactory.getObservation(where));
				
			} else { 
				throw new ThinklabResourceNotFoundException(
						"region name " + 
						command.getArgumentAsString("context") +
						" cannot be resolved");
			}
		}
		
		
		Constraint c = new Constraint(CoreScience.Observation());

		IInstance inst = session.createObject(Polylist.list(concept));
		c = c.restrict(new Restriction(
				CoreScience.HAS_OBSERVABLE,
				new DefaultConformance().getConstraint(inst)));

		if (extents.size() > 0) {

			ArrayList<Restriction> er = new ArrayList<Restriction>();
			for (Topology o : extents) {
				Restriction r = o.getConstraint("intersects");
				if (r != null)
					er.add(r);
			}

			if (er.size() > 0) {
				c = c.restrict(er.size() == 1 ? er.get(0) : Restriction.AND(er
						.toArray(new Restriction[er.size()])));
			}
		}

		IQueryResult r = kbox.query(c);
		
		session.getOutputStream().print(
					r.getTotalResultCount() + " possible observation(s) found");

		for (int i = 0; i < r.getTotalResultCount(); i++) {
			Polylist o = r.getResultAsList(0, null);
			session.print(i + ": " + o);
		}
			
		return null;
	}

}
