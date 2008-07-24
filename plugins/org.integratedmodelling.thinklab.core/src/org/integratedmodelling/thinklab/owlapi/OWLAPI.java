package org.integratedmodelling.thinklab.owlapi;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataAllRestriction;
import org.semanticweb.owl.model.OWLDataExactCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataMaxCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataMinCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataSomeRestriction;
import org.semanticweb.owl.model.OWLDataValueRestriction;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectAllRestriction;
import org.semanticweb.owl.model.OWLObjectComplementOf;
import org.semanticweb.owl.model.OWLObjectExactCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectMaxCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectMinCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLObjectSomeRestriction;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.util.OWLDescriptionVisitorAdapter;
import org.semanticweb.owl.vocab.OWLRDFVocabulary;

/**
 * Collection of helper methods to use OWLAPI more conveniently.
 * 
 * @author Ferdinando
 *
 */
public class OWLAPI {

	/**
     * Collect all available info about restrictions.
     */
    private static class RestrictionVisitor extends OWLDescriptionVisitorAdapter {

        private boolean processInherited = true;
        private Set<OWLClass> processedClasses;
        private Set<OWLObjectPropertyExpression> restrictedProperties;
        private Set<OWLOntology> onts;

        public RestrictionVisitor(Set<OWLOntology> onts) {
            restrictedProperties = new HashSet<OWLObjectPropertyExpression>();
            processedClasses = new HashSet<OWLClass>();
            this.onts = onts;
        }

        public void setProcessInherited(boolean processInherited) {
            this.processInherited = processInherited;
        }

        public Set<OWLObjectPropertyExpression> getRestrictedProperties() {
            return restrictedProperties;
        }

        public void visit(OWLClass desc) {
        	
            if (processInherited && !processedClasses.contains(desc)) {

            	processedClasses.add(desc);
                for(OWLOntology ont : onts) {
                    for(OWLSubClassAxiom ax : ont.getSubClassAxiomsForLHS(desc)) {
                        ax.getSuperClass().accept(this);
                    }
                }
            }
        }
        
        public void reset() {
            processedClasses.clear();
            restrictedProperties.clear();
        }

        @Override
		public void visit(OWLDataAllRestriction desc) {
			// TODO Auto-generated method stub
			super.visit(desc);
		}

		@Override
		public void visit(OWLDataExactCardinalityRestriction desc) {
			// TODO Auto-generated method stub
			super.visit(desc);
		}

		@Override
		public void visit(OWLDataMaxCardinalityRestriction desc) {
			// TODO Auto-generated method stub
			super.visit(desc);
		}

		@Override
		public void visit(OWLDataMinCardinalityRestriction desc) {
			// TODO Auto-generated method stub
			super.visit(desc);
		}

		@Override
		public void visit(OWLDataSomeRestriction desc) {
			// TODO Auto-generated method stub
			super.visit(desc);
		}

		@Override
		public void visit(OWLDataValueRestriction desc) {
			// TODO Auto-generated method stub
			super.visit(desc);
		}
		
		@Override
		public void visit(OWLObjectAllRestriction desc) {
			// TODO Auto-generated method stub
			super.visit(desc);
		}

		@Override
		public void visit(OWLObjectComplementOf desc) {
			// TODO Auto-generated method stub
			super.visit(desc);
		}

		@Override
		public void visit(OWLObjectExactCardinalityRestriction desc) {
			// TODO Auto-generated method stub
			super.visit(desc);
		}

		@Override
		public void visit(OWLObjectMaxCardinalityRestriction desc) {
			// TODO Auto-generated method stub
			super.visit(desc);
		}

		@Override
		public void visit(OWLObjectMinCardinalityRestriction desc) {
			// TODO Auto-generated method stub
			super.visit(desc);
		}

		public void visit(OWLObjectSomeRestriction desc) {
			
            // This method gets called when a description (OWLDescription) is an
            // existential (someValuesFrom) restriction and it asks us to visit it
            restrictedProperties.add(desc.getProperty());
        }
    }
	
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
