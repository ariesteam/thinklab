/**
 * Created on Mar 7, 2008 
 * By Ioannis N. Athanasiadis
 *
 * Copyright 2007 Dalle Molle Institute for Artificial Intelligence
 * 
 * Licensed under the GNU General Public License.
 *
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.integratedmodelling.thinklab.owlapi;

import java.net.URI;
import java.net.URISyntaxException;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.lang.SemanticType;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.api.knowledge.IKnowledge;
import org.integratedmodelling.thinklab.api.knowledge.IOntology;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.IResource;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLProperty;
import org.semanticweb.owl.vocab.OWLRDFVocabulary;

/**
 * An abstract class that implements the IKnowledge interface.
 * Practically, manages local names, semantic types, labels and descriptions.
 * 
 * @author Ioannis N. Athanasiadis
 */
public abstract class Knowledge implements IKnowledge, IResource {
	protected enum OWLType {
		CLASS, DATAPROPERTY, OBJECTPROPERTY, INDIVIDUAL
	}

	protected OWLEntity entity;
	protected URI ontoURI;
	protected String name;
	OWLType type;

	private static String DEF_LANG = "en";

	public Knowledge(OWLEntity entity, OWLType type) {
		this.entity = entity;
		this.type = type;
		this.name = entity.toString();
		ontoURI = Registry.get().getOntoURI(entity.getURI());
	}

	public OWLOntology getOWLOntology() {
		return FileKnowledgeRepository.KR.manager.getOntology(ontoURI);
	}
	
	public IOntology getOntology() {
		try {
			return FileKnowledgeRepository.KR.requireOntology(getConceptSpace());
		} catch (ThinklabResourceNotFoundException e) {
			// it can only happen in very, very weird situations.
			throw new ThinklabRuntimeException(e);
		}
	}
	
	public Ontology getThinklabOntology() {
		return (Ontology) FileKnowledgeRepository.KR.retrieveOntology(getConceptSpace());
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IResource#getURI()
	 */
	public String getURI() {
		synchronized (entity) {
			return entity.getURI().toString();
		}
	}

	public String toString() {
		return getSemanticType().toString();
	}

	/*
	 * (non-Javadoc)
	 * FV modified to store the name as it turns out OWLAPI is not thread safe (ugh). Don't
	 * know what else I'll need to do to address that.
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledge#getLocalName()
	 */
	public String getLocalName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledge#getSemanticType()
	 */
	public SemanticType getSemanticType() {
		return Registry.get().getSemanticType(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IResource#addDescription(java.lang.String)
	 */
	public void addDescription(String desc) {
		addDescription(desc, DEF_LANG);
	}

	public void addAnnotation(OWLProperty prop, String annotation) {
	
		OWLOntology ontology = getOWLOntology();
		synchronized (ontology) {
			OWLDataFactory df = FileKnowledgeRepository.df;
			OWLConstant cns = df.getOWLTypedConstant(annotation);
			OWLAnnotation<?> anno = df.getOWLConstantAnnotation(prop.getURI(), cns);
			OWLAxiom ax = df.getOWLEntityAnnotationAxiom(entity, anno);
			// Add the axiom to the ontology
			try {
				FileKnowledgeRepository.KR.manager.applyChange(new AddAxiom(
					ontology, ax));
			} catch (OWLOntologyChangeException e) {
				throw new ThinklabRuntimeException(e);
			}
		}
	}

	public void addAnnotation(String prop, String annotation) {
	
		OWLOntology ontology = getOWLOntology();
		IProperty p;
		try {
			p = KnowledgeManager.get().requireProperty(prop);
		} catch (ThinklabException e2) {
			throw new ThinklabRuntimeException(e2);
		}
		synchronized (ontology) {
			OWLDataFactory df = FileKnowledgeRepository.df;
			OWLConstant cns = df.getOWLTypedConstant(annotation);
			OWLAnnotation<?> anno;
			try {
				anno = df.getOWLConstantAnnotation(new URI(p.getURI()), cns);
			} catch (URISyntaxException e1) {
				throw new ThinklabRuntimeException(e1);
			}
			OWLAxiom ax = df.getOWLEntityAnnotationAxiom(entity, anno);
			// Add the axiom to the ontology
			try {
				FileKnowledgeRepository.KR.manager.applyChange(new AddAxiom(
					ontology, ax));
			} catch (OWLOntologyChangeException e) {
				throw new ThinklabRuntimeException(e);
			}
		}
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IResource#addDescription(java.lang.String,
	 *      java.lang.String)
	 */
	public void addDescription(String desc, String language) {
		// Here I do not which ontology to use... the one of the
		// concept or some "active" one?
		OWLOntology ontology = FileKnowledgeRepository.KR.manager
				.getOntology(ontoURI);
		synchronized (ontology) {
			OWLDataFactory df = FileKnowledgeRepository.df;
			OWLAnnotation<?> commentAnno = df.getCommentAnnotation(desc, language);
			OWLAxiom ax = df.getOWLEntityAnnotationAxiom(entity, commentAnno);
			// Add the axiom to the ontology
			try {
				FileKnowledgeRepository.KR.manager.applyChange(new AddAxiom(
					ontology, ax));
			} catch (OWLOntologyChangeException e) {
				throw new ThinklabRuntimeException(e);
			}
		}

	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IResource#addLabel(java.lang.String)
	 */
	public void addLabel(String desc) {
		addLabel(desc, DEF_LANG);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IResource#addLabel(java.lang.String,
	 *      java.lang.String)
	 */
	public void addLabel(String desc, String language) {
		// Here I do not which ontology to use... the one of the
		// concept or some "active" one?
		OWLOntology ontology = FileKnowledgeRepository.KR.manager
				.getOntology(ontoURI);
		synchronized (ontology) {
			OWLDataFactory df = FileKnowledgeRepository.df;

			OWLAnnotation<?> labelAnno = df.getOWLLabelAnnotation(desc, language);
			OWLAxiom ax = df.getOWLEntityAnnotationAxiom(entity, labelAnno);
			// Add the axiom to the ontology
			try {
				FileKnowledgeRepository.KR.manager.applyChange(new AddAxiom(
					ontology, ax));
			} catch (OWLOntologyChangeException e) {
				throw new ThinklabRuntimeException(e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IResource#getConceptSpace()
	 */
	public String getConceptSpace() {
		synchronized (this.entity) {
			return Registry.get().getConceptSpace(entity.getURI());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IResource#getDescription()
	 */
	public String getDescription() {
		String enDesc = getDescription(DEF_LANG);
		if (enDesc == null || enDesc.equals(""))
			return getDescription(null);
		else
			return enDesc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IResource#getDescription(java.lang.String)
	 */
	public String getDescription(String languageCode) {
		String ret = getAnnotation(languageCode, OWLRDFVocabulary.RDFS_COMMENT
				.getURI());
		return ret == null ? "" : ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IResource#getLabel()
	 */
	public String getLabel() {
		String enLabel = getLabel(DEF_LANG);
		if (enLabel == null || enLabel.equals(""))
			return getLabel(null);
		else
			return enLabel;
	}

	private String getAnnotation(String languageCode, URI property) {
		OWLOntology ontology = FileKnowledgeRepository.KR.manager
				.getOntology(ontoURI);
		if (ontology != null) {
			synchronized (ontology) {
				for (OWLAnnotation<?> annotation : entity.getAnnotations(ontology,
						property)) {
					if (annotation.isAnnotationByConstant()) {
						OWLConstant val = annotation.getAnnotationValueAsConstant();
						if (languageCode == null || languageCode.equals("")) {
							return val.getLiteral();
						} else {
							if (!val.isTyped())
								if (val.asOWLUntypedConstant()
										.hasLang(languageCode)) {
									return val.getLiteral();
								}	
						}
					}
				}
			}
		}

		return null;
	}
	
	@Override
	public String getAnnotation(String property) {
		IProperty p = null;
		try {
			p = KnowledgeManager.get().requireProperty(property);
		} catch (ThinklabException e) {
			throw new ThinklabRuntimeException(e);
		}
		try {
			return getAnnotation(null, new URI(p.getURI()));
		} catch (URISyntaxException e) {
			throw new ThinklabRuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IResource#getLabel(java.lang.String)
	 */
	public String getLabel(String languageCode) {
		String ret = getAnnotation(languageCode, OWLRDFVocabulary.RDFS_LABEL.getURI());
		return ret == null ? "" : ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledge#is(org.integratedmodelling.thinklab.interfaces.IKnowledge)
	 */
	public boolean is(IKnowledge k) {
		if (k == null)
			return false;
		else if (k instanceof Concept) {
			Concept right = (Concept) k;
			if (this instanceof Concept) {
				Concept left = (Concept) this;
				return left.is(right);
			} else if (this instanceof Instance) {
				Instance left = (Instance) this;
				return left.is(right);
			} else
				return false;
		} else if (k instanceof Property) {
			Property right = (Property) k;
			if (this instanceof Property) {
				Property left = (Property) this;
				return left.is(right);
			} else
				return false;
		} else if (k instanceof Instance) {
			Instance right = (Instance) k;
			if (this instanceof Instance) {
				Instance left = (Instance) this;
				return left.is(right);
			} else
				return false;
		} else
			return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledge#is(java.lang.String)
	 */
	public boolean is(String semanticType) {
		try {
			return is(new SemanticType(semanticType));
		} catch (ThinklabRuntimeException e) {
			return false;
		}

	}

	/**
	 * The is function invokes the reasoner (if present), while the equals
	 * method is not.
	 * 
	 * @param semantictype
	 * @return
	 */
	public boolean is(SemanticType st) {
		try {
			return is(Registry.get().getURI(st));
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * The is method investigates the subsumption hierarchy. If a reasoner is
	 * connected it is used.
	 * 
	 * @param uri
	 * @return
	 */
	public boolean is(URI uri) {
		
		IKnowledge k = FileKnowledgeRepository.KR.resolveURI(uri);
		return is(k);
	}

	/* 
	 * Need to override for the collections to work properly.
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode(){
			return getURI().hashCode();
	}
	
	public boolean equals(Object s){
		
	    	if (s instanceof Knowledge) {
				return ((Knowledge)s).getURI().equals(getURI());
			} else if (s instanceof String || s instanceof SemanticType) {
				return 
					this.toString().equals(s.toString()) ||
					this.getURI().toString().equals(s.toString());
			}
	    	return false;
	    }
}
