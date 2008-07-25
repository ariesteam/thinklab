/**
 * Created on Mar 3, 2008 
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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.integratedmodelling.thinklab.SemanticType;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.interfaces.IConcept;
import org.integratedmodelling.thinklab.interfaces.IKnowledge;
import org.integratedmodelling.thinklab.interfaces.IProperty;
import org.integratedmodelling.thinklab.interfaces.IResource;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLProperty;
import org.semanticweb.owl.model.OWLPropertyExpression;
import org.semanticweb.owl.vocab.OWLRDFVocabulary;

/**
 * @author Ioannis N. Athanasiadis
 *
 */
public class Property extends Knowledge implements IProperty {


	/**
	 * @param p
	 */
	public Property(OWLObjectProperty p) {
		super(p,OWLType.OBJECTPROPERTY);
	}
	
	public Property(OWLDataProperty p) {
		super(p,OWLType.DATAPROPERTY);
	}

	Property(OWLPropertyExpression p) {
		super(
			( p instanceof OWLObjectProperty ? (OWLObjectProperty)p : (OWLDataProperty)p),
			( p instanceof OWLObjectProperty ? OWLType.OBJECTPROPERTY : OWLType.DATAPROPERTY));
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IProperty#getAllChildren()
	 */
	public Collection<IProperty> getAllChildren() {
		Set<IProperty> ret = new HashSet<IProperty>();
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IProperty#getAllParents()
	 */
	public Collection<IProperty> getAllParents() {
		Set<IProperty> ret = new HashSet<IProperty>();
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IProperty#getChildren()
	 */
	public Collection<IProperty> getChildren() {
		Set<IProperty> ret = new HashSet<IProperty>();
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IProperty#getDomain()
	 */
	public IConcept getDomain() {
//		((OWLProperty) entity).getDomains(ontologies)
		return null;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IProperty#getInverseProperty()
	 */
	public IProperty getInverseProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IProperty#getParent()
	 */
	public IProperty getParent()
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IProperty#getParents()
	 */
	public Collection<IProperty> getParents() {
		Set<IProperty> ret = new HashSet<IProperty>();
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IProperty#getRange()
	 */
	public Collection<IConcept> getRange() {
		Set<IConcept> ret = new HashSet<IConcept>();
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IProperty#isAbstract()
	 */
	public boolean isAbstract() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IProperty#isAnnotation()
	 */
	public boolean isAnnotation() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IProperty#isClassification()
	 */
	public boolean isClassification() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IProperty#isFunctional()
	 */
	public boolean isFunctional() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IProperty#isLiteralProperty()
	 */
	public boolean isLiteralProperty() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IProperty#isObjectProperty()
	 */
	public boolean isObjectProperty() {
		// TODO Auto-generated method stub
		return false;
	}
	
	protected boolean is(Property p){
		return false;
	}
}
