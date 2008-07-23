package org.integratedmodelling.thinklab.owlapi;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.vocab.OWLRDFVocabulary;

public class Snippets {

	public static String getLabel(OWLOntology ont, OWLEntity cls, String language) {
		return getAnnotation(ont, cls, language, OWLRDFVocabulary.RDFS_LABEL.getURI());
	}

	public static String getComment(OWLOntology ont, OWLEntity cls, String language) {
		return getAnnotation(ont, cls, language, OWLRDFVocabulary.RDFS_COMMENT.getURI());
	}
	
	public static String getAnnotation(OWLOntology ont, OWLEntity cls, String language, URI annotationURI) {
		
		String ret = null;
		
		// Get the annotations on the class that have a URI corresponding to rdfs:label
		for (OWLAnnotation annotation : cls.getAnnotations(ont, annotationURI)) {
			if (annotation.isAnnotationByConstant()) {
				OWLConstant val = annotation.getAnnotationValueAsConstant();
				if (!val.isTyped()) {
					// The value isn't a typed constant, so we can safely obtain it
					// as an OWLUntypedConstant
					if (language == null || val.asOWLUntypedConstant().hasLang(language)) {
						ret = val.getLiteral();
						break;
					}
				}
			}
		}
		return ret;
	}
	
	public static void addAnnotation(OWLOntology ont, OWLIndividual instance, URI annotationURI, String content) {
		
	}
	
	/*
	 * Return all data properties that are the object of a domain axiom for the
	 * passed class in the passed ontology. 
	 */
	public static Set<OWLDataProperty> getClassDataProperties(OWLOntology ontology,
			OWLClass c) throws OWLException {
		
		Set<OWLDataProperty> classProps = new HashSet<OWLDataProperty>();

		for (OWLDataProperty prop : ontology.getReferencedDataProperties()) {
			if (!prop.getDomains(ontology).isEmpty()) {
				for (OWLDescription od : prop.getDomains(ontology)) {
					if (od != null && od instanceof OWLClass) {
						if (((OWLClass) od).getURI().equals(c.getURI())) {
							classProps.add(prop);
						}
					}
				}
			}
		}
		return classProps;
	}

	/*
	 * Return all object properties that are the object of a domain axiom for the
	 * passed class in the passed ontology. 
	 */
	public static Set<OWLObjectProperty> getClassObjectProperties(OWLOntology ontology,
			OWLClass c) throws OWLException {
		
		Set<OWLObjectProperty> classProps = new HashSet<OWLObjectProperty>();

		for (OWLObjectProperty prop : ontology.getReferencedObjectProperties()) {
			if (!prop.getDomains(ontology).isEmpty()) {
				for (OWLDescription od : prop.getDomains(ontology)) {
					if (od != null && od instanceof OWLClass) {
						if (((OWLClass) od).getURI().equals(c.getURI())) {
							classProps.add(prop);
						}
					}
				}
			}
		}
		return classProps;
	}

}
