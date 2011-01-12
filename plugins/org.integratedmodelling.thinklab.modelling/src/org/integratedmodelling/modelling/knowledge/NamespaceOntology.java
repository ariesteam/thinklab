package org.integratedmodelling.modelling.knowledge;

import java.util.ArrayList;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IOntology;
import org.integratedmodelling.utils.Polylist;

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
			ontology.createConcept(o);
		}
	}
	
	public IOntology getOntology() throws ThinklabException {
		// you never know
		if (ontology == null)
			initialize();
		return ontology;
	}
}
