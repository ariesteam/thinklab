package org.integratedmodelling.modelling.knowledge;

import java.util.ArrayList;

import org.integratedmodelling.modelling.ModellingPlugin;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IOntology;
import org.integratedmodelling.utils.Polylist;

import clojure.lang.Keyword;

/**
 * Simple object used to communicate knowledge from the clojure file that
 * define a model namespace.
 * 
 * @author ferdinando.villa
 *
 */
public class NamespaceOntology {

	private String conceptSpace;
	private ArrayList<Polylist> body = new ArrayList<Polylist>();
	private String description;
	private IOntology ontology;
	
	public NamespaceOntology(String c) {
		this.conceptSpace = c;
	}
	
	public void setDescription(String d) {
		this.description = d;
	}
	
	/*
	 * this should be capable of adding new concept hierarchies and instances
	 * defined in Clojure.
	 */
	public void add(Object o, Object clauses) {
		body.add((Polylist)o);
	}

	public void initialize() throws ThinklabException {

		ontology =
			KnowledgeManager.get().getKnowledgeRepository().
				retrieveOntology(conceptSpace);
		
		if (ontology == null) {

			ontology = KnowledgeManager.get().getKnowledgeRepository().
				createTemporaryOntology(conceptSpace);
				
			if (description != null)
				ontology.addDescription(description);				
		}
		
		/*
		 * add any concept definition we have in the form.
		 */
		for (Polylist o : body) {
			createConcept(o, null);
		}
	}
	
	private void createConcept(Polylist list, ArrayList<IConcept> parent) throws ThinklabException {

		ArrayList<IConcept> root = new ArrayList<IConcept>();
		Object[] objs = list.array();
		
		for (int i = 0; i < objs.length; i++) {
			
			Object o = objs[i];
			
				// must be a concept
			if (o instanceof Polylist) {
				createConcept((Polylist)o, root);
			} else if (o instanceof Keyword) {
				
				/*
				 * adding annotation properties only if one concepts is being defined and
				 * it's in the same ontology.
				 */
				if (root.size() > 1)
					throw new ThinklabValidationException(
							"cannot add annotation " + 
							o + 
							" to more than one concept");
				
				if (root.size() < 1 || !root.get(0).getConceptSpace().equals(getOntology().getConceptSpace())) {
					throw new ThinklabValidationException(
							"cannot add annotation " + 
							o + 
							" to concept " + 
							(root.size() < 1 ? "null" : root.get(0).toString()));					
				}
				
				IConcept target = root.get(0);
				
				Object ag0 = objs[++i];
				String arg = ag0 == null ? "nil" : ag0.toString();
				String kwd = o.toString();
				
				if (kwd.equals(":label")) {
					target.addLabel(arg);
				} else if (kwd.equals(":description")) {
					target.addDescription(arg);
				} else if (kwd.equals(":units")) {
					target.addAnnotation(ModellingPlugin.UNITS_ANNOTATION, arg);
				} else if (kwd.equals(":range")) {
					target.addAnnotation(ModellingPlugin.RANGE_ANNOTATION, arg);
				} else if (kwd.equals(":editable")) {
					target.addAnnotation(ModellingPlugin.EDITABLE_ANNOTATION, arg);
				} 
				
			}  else {
				root.add(annotateConcept(o.toString(), parent));
			}
		}
		
	}

	private IConcept annotateConcept(String string, ArrayList<IConcept> parent) throws ThinklabException {
		
		IConcept ret = null;
		if (string.contains(":")) {
			ret = KnowledgeManager.getConcept(string); 
		} else {
			ret = getOntology().getConcept(string);
			if (ret == null) {
				IConcept[] p = null;
				if (parent != null && parent.size() > 0)
					p = parent.toArray(new IConcept[parent.size()]);
				ret = getOntology().createConcept(string, p, false);
			}
		}
		return ret;
	}

	public IOntology getOntology() throws ThinklabException {
		// you never know
		if (ontology == null)
			initialize();
		return ontology;
	}
}
