package org.integratedmodelling.modelling.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.modelling.storyline.StorylineFactory;
import org.integratedmodelling.modelling.visualization.knowledge.TypeManager;
import org.integratedmodelling.modelling.visualization.knowledge.VisualConcept;
import org.integratedmodelling.modelling.visualization.storyline.StorylineTemplate;
import org.integratedmodelling.modelling.visualization.storyline.StorylineTemplate.Page;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.utils.CamelCase;
import org.integratedmodelling.utils.xml.XML;

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
				
		StorylineTemplate template = StorylineFactory.getPresentation(concept);
		HashMap<IConcept, IState> states = new HashMap<IConcept, IState>();
		HashSet<IConcept> present = new HashSet<IConcept>();
		
		for (Page p : template.getPages()) {
			present.add(p.getConcept());
		}
		
// TODO sync with implementation, transfer to storyline command
//		for (Node node : template.getModelSpecifications()) {
//			if (node.getNodeName().equals("model")) {
//				String m = XMLDocument.getAttributeValue(node, "id");
//				String c = XMLDocument.getAttributeValue(node, "context");
//				IModel   model = ModelFactory.get().requireModel(m);
//				IContext context = ModelFactory.get().requireContext(c);
//
//				try {	
//					IQueryResult r = 
//						ModelFactory.get().run((Model) model, KBoxManager.get(), session, null, context);		
//				
//					if (r.getTotalResultCount() > 0) {
//					
//						IValue res = r.getResult(0, session);
//						IContext result = ((ContextValue)res).getObservationContext();
//					
//						for (IState s : result.getStates())
//							if (!present.contains(s.getObservableClass()))
//								states.put(s.getObservableClass(), s);
//					}
//				} catch (ThinklabException e) {
//					session.print("error running " + m + " in " + c + ": skipping");
//				}
//			}
//		}

		ArrayList<XML.XmlNode> nodes = new ArrayList<XML.XmlNode>();
		for (IConcept c : states.keySet()) {
			
			VisualConcept vc = TypeManager.get().getVisualConcept(c);
			nodes.add(
				XML.node("page",
						XML.node("concept", c.toString()),
						XML.node("name", vc.getLabel()),
						XML.node("title", vc.getLabel()),
						XML.node("see-also", XML.cdata("")),
						XML.node("credits", XML.cdata("")),
						XML.node("group", "groupname"),
						XML.node("description", XML.cdata(vc.getDescription())),
						XML.node("runninghead", vc.getLabel()),
						XML.node("plot-type", "geosurface-2d").attr("default", "true"),
						XML.node("plot-type", "geocontour-2d")
				).attr("id", CamelCase.toLowerCase(c.getLocalName(), '-')));
		}

		XML.document(XML.node("pages", nodes)).dump(session.getOutputStream());

		
		return null;
	}

}
