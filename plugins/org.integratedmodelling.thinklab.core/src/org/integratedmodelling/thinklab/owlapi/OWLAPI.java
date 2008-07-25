package org.integratedmodelling.thinklab.owlapi;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.IConcept;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataAllRestriction;
import org.semanticweb.owl.model.OWLDataExactCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataMaxCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataMinCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLDataSomeRestriction;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDataValueRestriction;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectAllRestriction;
import org.semanticweb.owl.model.OWLObjectExactCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectMaxCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectMinCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLObjectSomeRestriction;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLProperty;
import org.semanticweb.owl.model.OWLRestriction;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.util.OWLDescriptionVisitorAdapter;
import org.semanticweb.owl.vocab.OWLRDFVocabulary;
import org.semanticweb.owl.vocab.XSDVocabulary;

/**
 * Collection of helper methods to use OWLAPI more conveniently.
 * 
 * @author Ferdinando
 *
 */
public class OWLAPI {
	

	/**
     * Collect all available info about restrictions and the properties they restrict.
     */
    private static class RestrictionVisitor extends OWLDescriptionVisitorAdapter {

        private boolean processInherited = true;
        private Set<OWLClass> processedClasses;
        private Set<OWLObjectPropertyExpression> restrictedProperties;
        private Set<OWLOntology> onts;
        protected Set<OWLRestriction> restrictions;
        
        public RestrictionVisitor(Set<OWLOntology> onts) {
            restrictedProperties = new HashSet<OWLObjectPropertyExpression>();
            processedClasses = new HashSet<OWLClass>();
            restrictions = new HashSet<OWLRestriction>();
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
            restrictions.clear();
        }

        @Override
		public void visit(OWLDataAllRestriction desc) {
			super.visit(desc);
			restrictions.add(desc);
		}

		@Override
		public void visit(OWLDataExactCardinalityRestriction desc) {
			super.visit(desc);
			restrictions.add(desc);
		}

		@Override
		public void visit(OWLDataMaxCardinalityRestriction desc) {
			super.visit(desc);
			restrictions.add(desc);
		}

		@Override
		public void visit(OWLDataMinCardinalityRestriction desc) {
			super.visit(desc);
			restrictions.add(desc);
		}

		@Override
		public void visit(OWLDataSomeRestriction desc) {
			super.visit(desc);
			restrictions.add(desc);
		}

		@Override
		public void visit(OWLDataValueRestriction desc) {
			super.visit(desc);
			restrictions.add(desc);
		}
		
		@Override
		public void visit(OWLObjectAllRestriction desc) {
			super.visit(desc);
			restrictions.add(desc);
		}

		@Override
		public void visit(OWLObjectExactCardinalityRestriction desc) {
			super.visit(desc);
			restrictions.add(desc);
		}

		@Override
		public void visit(OWLObjectMaxCardinalityRestriction desc) {
			super.visit(desc);
			restrictions.add(desc);
		}

		@Override
		public void visit(OWLObjectMinCardinalityRestriction desc) {
			super.visit(desc);
			restrictions.add(desc);
		}

		public void visit(OWLObjectSomeRestriction desc) {
			/*
			 * remember the property that was restricted 
			 */
            restrictedProperties.add(desc.getProperty());
			restrictions.add(desc);
        }
    }
	
    public static Collection<OWLRestriction> getRestrictions(Concept clazz, boolean checkModel) {
    	
    	Set<OWLOntology> target = 
    		checkModel ? 
    				FileKnowledgeRepository.get().manager.getOntologies() :
    				Collections.singleton(clazz.getOntology());
    	
    	 RestrictionVisitor restrictionVisitor = new RestrictionVisitor(target);

         for(OWLSubClassAxiom ax : clazz.getOntology().getSubClassAxiomsForLHS(clazz.entity.asOWLClass())) {
 
        	 OWLDescription superCls = ax.getSuperClass();
             superCls.accept(restrictionVisitor);
         }
         
         return restrictionVisitor.restrictions;
    }
    
	public static String getLabel(Knowledge cls, String language) {
		return getAnnotation(cls, language, OWLRDFVocabulary.RDFS_LABEL.getURI());
	}

	public static String getComment(Knowledge cls, String language) {
		return getAnnotation(cls, language, OWLRDFVocabulary.RDFS_COMMENT.getURI());
	}
	
	public static String getAnnotation(Knowledge k, String language, URI annotationURI) {
		
		String ret = null;
		
		// Get the annotations on the class that have a URI corresponding to rdfs:label
		for (OWLAnnotation annotation : k.entity.getAnnotations(k.getOntology(), annotationURI)) {
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
	
	public static void setOWLObjectPropertyValue(OWLOntology ont, OWLIndividual ind, OWLProperty prop, OWLIndividual value) {
  
		OWLObjectPropertyAssertionAxiom assertion = 
			FileKnowledgeRepository.df.getOWLObjectPropertyAssertionAxiom(
					ind, 
					prop.asOWLObjectProperty(), 
					value);
		
        AddAxiom addAxiomChange = new AddAxiom(ont, assertion);
        try {
			FileKnowledgeRepository.get().manager.applyChange(addAxiomChange);
		} catch (OWLOntologyChangeException e) {
			throw new ThinklabRuntimeException(e);
		}

	}

	public static void setOWLDataPropertyValue(OWLOntology ont, OWLIndividual ind, OWLProperty prop, OWLConstant value) {
		
		OWLDataPropertyAssertionAxiom assertion = 
			FileKnowledgeRepository.df.getOWLDataPropertyAssertionAxiom(
					ind, prop.asOWLDataProperty(), value);

		AddAxiom addAxiomChange = new AddAxiom(ont, assertion);
        try {
			FileKnowledgeRepository.get().manager.applyChange(addAxiomChange);
		} catch (OWLOntologyChangeException e) {
			throw new ThinklabRuntimeException(e);
		}
		
	}

	public static OWLConstant getOWLConstant(Object obj) {
		
		OWLConstant ret = null;
		
		if (obj instanceof Boolean)
			ret = FileKnowledgeRepository.df.getOWLTypedConstant((Boolean)obj);
		else if (obj instanceof Double)
			ret = FileKnowledgeRepository.df.getOWLTypedConstant((Double)obj);
		else if (obj instanceof Float)
			ret = FileKnowledgeRepository.df.getOWLTypedConstant((Float)obj);
		else if (obj instanceof Integer)
			ret = FileKnowledgeRepository.df.getOWLTypedConstant((Integer)obj);
		else if (obj instanceof String)
			ret = FileKnowledgeRepository.df.getOWLTypedConstant((String)obj);
		else 
			ret = FileKnowledgeRepository.df.getOWLUntypedConstant(obj.toString());

		return ret;
	}


}
