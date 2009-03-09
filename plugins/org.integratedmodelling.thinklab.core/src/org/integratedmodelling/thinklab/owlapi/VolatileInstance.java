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

import java.util.Collections;
import java.util.Properties;

import org.integratedmodelling.thinklab.SemanticType;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstanceImplementation;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.util.OWLEntityRemover;

/**
 * A volatile instance retracts all related axioms from the corresponding ontology
 * when it is garbage collected. It also stores its implementation locally and
 * does not save it anywhere. It should be the default implementation for
 * those created in sessions, although for now it is not used.
 * 
 * @author Ferdinando Villa
 */
public class VolatileInstance extends  Instance {

	IInstanceImplementation implementation = null;
	
	public VolatileInstance(OWLIndividual i, Properties properties) {
		super(i, properties);
	}

	@Override
	protected void finalize() throws Throwable {
		
		System.out.println("Retracting " + getLocalName());
		
		/*
		 * retract all axioms from only our own ontology - which
		 * should be fine for sessions
		 */
        OWLEntityRemover remover = 
        	new OWLEntityRemover(
        			FileKnowledgeRepository.get().manager, 
        			Collections.singleton(getOntology()));
        entity.accept(remover);
        
        try {
        	FileKnowledgeRepository.get().manager.applyChanges(remover.getChanges());
        } catch (OWLOntologyChangeException e) {
        	throw new ThinklabRuntimeException(e);
        }

        super.finalize();
	}
	
	


}
