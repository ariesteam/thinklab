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
import java.util.HashMap;
import java.util.Map;

import org.integratedmodelling.thinklab.SemanticType;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.interfaces.IConcept;
import org.integratedmodelling.thinklab.interfaces.IConformance;
import org.integratedmodelling.thinklab.interfaces.IInstance;
import org.integratedmodelling.thinklab.interfaces.IInstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.IKnowledge;
import org.integratedmodelling.thinklab.interfaces.IOntology;
import org.integratedmodelling.thinklab.interfaces.IProperty;
import org.integratedmodelling.thinklab.interfaces.IRelationship;
import org.integratedmodelling.thinklab.interfaces.IResource;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.thinklab.value.AlgorithmValue;
import org.integratedmodelling.utils.Polylist;
import org.semanticweb.owl.model.OWLIndividual;

/**
 * @author Ioannis N. Athanasiadis
 *
 */
public class Instance extends Knowledge implements IInstance {


	/**
	 * @param i
	 * @param ontology 
	 * @throws ThinklabResourceNotFoundException 
	 */
	public Instance(OWLIndividual i) {
		super(i,OWLType.INDIVIDUAL);
	}



	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IInstance#addClassificationRelationship(java.lang.String, org.integratedmodelling.thinklab.interfaces.IConcept)
	 */
	public void addClassificationRelationship(String p, IConcept cls)
			throws ThinklabException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IInstance#addClassificationRelationship(org.integratedmodelling.thinklab.interfaces.IProperty, org.integratedmodelling.thinklab.interfaces.IConcept)
	 */
	public void addClassificationRelationship(IProperty p, IConcept cls)
			throws ThinklabException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IInstance#addLiteralRelationship(org.integratedmodelling.thinklab.interfaces.IProperty, java.lang.Object)
	 */
	public void addLiteralRelationship(IProperty p, Object literal)
			throws ThinklabException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IInstance#addLiteralRelationship(java.lang.String, java.lang.Object)
	 */
	public void addLiteralRelationship(String p, Object literal)
			throws ThinklabException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IInstance#addObjectRelationship(org.integratedmodelling.thinklab.interfaces.IProperty, org.integratedmodelling.thinklab.interfaces.IInstance)
	 */
	public void addObjectRelationship(IProperty p, IInstance object)
			throws ThinklabException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IInstance#addObjectRelationship(java.lang.String, org.integratedmodelling.thinklab.interfaces.IInstance)
	 */
	public void addObjectRelationship(String p, IInstance instance)
			throws ThinklabException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IInstance#clone(org.integratedmodelling.thinklab.interfaces.IOntology)
	 */
	public IInstance clone(IOntology session) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IInstance#getDirectType()
	 */
	public IConcept getDirectType() {
		// cross fingers
		return new Concept(
			this.entity.asOWLIndividual().getTypes(this.getOntology()).iterator().next().asOWLClass());
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IInstance#getEquivalentInstances()
	 */
	public Collection<IInstance> getEquivalentInstances() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IInstance#getImplementation()
	 */
	public IInstanceImplementation getImplementation() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IInstance#isConformant(org.integratedmodelling.thinklab.interfaces.IInstance, org.integratedmodelling.thinklab.interfaces.IConformance)
	 */
	public boolean isConformant(IInstance otherInstance,
			IConformance conformance) throws ThinklabException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IInstance#isValidated()
	 */
	public boolean isValidated() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IInstance#toList(java.lang.String)
	 */
	public Polylist toList(String oref) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IInstance#toList(java.lang.String, java.util.HashMap)
	 */
	public Polylist toList(String oref, HashMap<String, String> refTable)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IInstance#validate()
	 */
	public void validate() throws ThinklabException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IInstance#validate(boolean)
	 */
	public void validate(boolean validateOWL) throws ThinklabException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject#get(java.lang.String)
	 */
	public IValue get(String property) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject#getNumberOfRelationships(java.lang.String)
	 */
	public int getNumberOfRelationships(String property)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject#getRelationships()
	 */
	public Collection<IRelationship> getRelationships()
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject#getRelationships(java.lang.String)
	 */
	public Collection<IRelationship> getRelationships(String property)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject#getRelationshipsTransitive(java.lang.String)
	 */
	public Collection<IRelationship> getRelationshipsTransitive(String property)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject#getType()
	 */
	public IConcept getType() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.ICommandListener#execute(org.integratedmodelling.thinklab.value.AlgorithmValue, org.integratedmodelling.thinklab.interfaces.ISession)
	 */
	public IValue execute(AlgorithmValue algorithm, ISession session)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.ICommandListener#execute(org.integratedmodelling.thinklab.value.AlgorithmValue, org.integratedmodelling.thinklab.interfaces.ISession, java.util.Map)
	 */
	public IValue execute(AlgorithmValue algorithm, ISession session,
			Map<String, IValue> arguments) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected boolean is(IConcept c){
		return false;
	}
	
	protected boolean is(IInstance i){
		return false;
	}



	@Override
	public void setImplementation(IInstanceImplementation second)
			throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

}
