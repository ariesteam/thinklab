package org.integratedmodelling.thinklab.modelling.model;

import java.util.ArrayList;
import java.util.Collection;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.list.Polylist;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IOntology;
import org.integratedmodelling.thinklab.api.modelling.IModelObject;
import org.integratedmodelling.thinklab.api.modelling.INamespace;

/**
 * Simple object used to communicate knowledge from the clojure file that
 * define a model namespace.
 * 
 * @author ferdinando.villa
 *
 */
public class Namespace implements INamespace {

	private String conceptSpace;
	private String namespace;
	
	public static final String UNITS_ANNOTATION = "modeltypes:hasUnitDescription";
	public static final String RANGE_ANNOTATION = "modeltypes:hasRangeDescription";
	public static final String EDITABLE_ANNOTATION = "modeltypes:isEditable";
	
	private ArrayList<Polylist> body = new ArrayList<Polylist>();
	private String description;
	private IOntology ontology;

	public Namespace(String ns, String ontology) {
		this.namespace = ns;
		this.conceptSpace = ontology == null ? ns : ontology;
	}

	public void setDescription(String d) {
		this.description = d;
	}

	/*
	 * this should be capable of adding new concept hierarchies and instances
	 * defined in Clojure.
	 */
	public void defineOntology(Polylist o) {
		body.add(o);
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
	
	@Override
	public String toString() {
		return "[" + namespace + " -> " + ontology + "]";
	}

	private void createConcept(Polylist list, ArrayList<IConcept> parent) throws ThinklabException {

		ArrayList<IConcept> root = new ArrayList<IConcept>();
		Object[] objs = list.array();

		for (int i = 0; i < objs.length; i++) {

			Object o = objs[i];

				// must be a concept
			if (o instanceof Polylist) {
				createConcept((Polylist)o, root);
			} else if (o.toString().startsWith(":")) {

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
					target.addAnnotation(UNITS_ANNOTATION, arg);
				} else if (kwd.equals(":range")) {
					target.addAnnotation(RANGE_ANNOTATION, arg);
				} else if (kwd.equals(":editable")) {
					target.addAnnotation(EDITABLE_ANNOTATION, arg);
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
				ret = getOntology().createConcept(string, p);
			}
		}
		return ret;
	}

	@Override
	public IOntology getOntology()  {
		// you never know
		if (ontology == null)
			try {
				initialize();
			} catch (ThinklabException e) {
				throw new ThinklabRuntimeException(e);
			}
		return ontology;
	}

	@Override
	public long getLastModification() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Collection<IModelObject> getModelObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNamespace() {
		return this.namespace;
	}
}
