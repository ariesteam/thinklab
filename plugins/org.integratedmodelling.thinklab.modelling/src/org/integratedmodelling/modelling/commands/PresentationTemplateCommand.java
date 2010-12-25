package org.integratedmodelling.modelling.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.corescience.listeners.IContextualizationListener;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.implementations.observations.RasterGrid;
import org.integratedmodelling.geospace.interfaces.IGazetteer;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.modelling.Context;
import org.integratedmodelling.modelling.DefaultAbstractModel;
import org.integratedmodelling.modelling.Model;
import org.integratedmodelling.modelling.ModelFactory;
import org.integratedmodelling.modelling.ObservationFactory;
import org.integratedmodelling.modelling.Scenario;
import org.integratedmodelling.modelling.interfaces.IDataset;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.modelling.interfaces.IVisualization;
import org.integratedmodelling.modelling.literals.ContextValue;
import org.integratedmodelling.modelling.storage.FileArchive;
import org.integratedmodelling.modelling.storage.NetCDFArchive;
import org.integratedmodelling.modelling.visualization.FileVisualization;
import org.integratedmodelling.modelling.visualization.ObservationListing;
import org.integratedmodelling.modelling.visualization.knowledge.TypeManager;
import org.integratedmodelling.modelling.visualization.knowledge.VisualConcept;
import org.integratedmodelling.modelling.visualization.presentation.PresentationFactory;
import org.integratedmodelling.modelling.visualization.presentation.PresentationTemplate;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.kbox.KBoxManager;
import org.integratedmodelling.time.TimeFactory;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.Polylist;
import org.integratedmodelling.utils.xml.XML;
import org.integratedmodelling.utils.xml.XMLDocument;
import org.w3c.dom.Node;

@ThinklabCommand(
		name="ptemplate",
		description="read models from a presentation, run them all and build a template for the union of their states",
		argumentNames="template",
		argumentTypes="thinklab-core:Text",
		argumentDescriptions="the template concept to read")
public class PresentationTemplateCommand implements ICommandHandler {

	IObservationContext ctx = null;
	HashMap<IConcept, IState> states = new HashMap<IConcept, IState>();
	
	@Override
	public IValue execute(Command command, ISession session)
			throws ThinklabException {
		
		IConcept concept = 
			KnowledgeManager.get().requireConcept(command.getArgumentAsString("template"));
				
		PresentationTemplate template = PresentationFactory.getPresentation(concept);
		HashMap<IConcept, IState> states = new HashMap<IConcept, IState>();
		
		for (Node node : template.getCustomNodes()) {
			if (node.getNodeName().equals("model")) {
				String m = XMLDocument.getAttributeValue(node, "id");
				String c = XMLDocument.getAttributeValue(node, "context");
				IModel   model = ModelFactory.get().requireModel(m);
				IContext context = ModelFactory.get().requireContext(c);

				try {	
					IQueryResult r = 
						ModelFactory.get().run((Model) model, KBoxManager.get(), session, null, context);		
				
					if (r.getTotalResultCount() > 0) {
					
						IValue res = r.getResult(0, session);
						IContext result = ((ContextValue)res).getObservationContext();
					
						for (IState s : result.getStates())
							states.put(s.getObservableClass(), s);
					}
				} catch (ThinklabException e) {
					session.print("error running " + m + " in " + c + ": skipping");
				}
			}
		}
		
		int i = 0;
		for (IConcept c : states.keySet()) {
			
			VisualConcept vc = TypeManager.get().getVisualConcept(c);
			XML.document(
				XML.node("page",
						XML.node("concept", c.toString()),
						XML.node("name", vc.getLabel()),
						XML.node("group", ""),
						XML.node("description", vc.getDescription()),
						XML.node("runninghead", vc.getDescription()),
						XML.node("sequence", i + ""),
						XML.node("plot-type", "geosurface-2d").attr("default", "true"),
						XML.node("plot-type", "geocontour-2d")
			)).dump(session.getOutputStream());
			i++;
		}
			
		return null;
	}

}
