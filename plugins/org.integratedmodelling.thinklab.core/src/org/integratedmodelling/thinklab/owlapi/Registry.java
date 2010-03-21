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
import java.util.Hashtable;

import org.integratedmodelling.thinklab.SemanticType;
import org.integratedmodelling.thinklab.exception.ThinklabMalformedSemanticTypeException;
import org.integratedmodelling.utils.MiscUtilities;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;

/**
 * A helper class of  FileKnowledgeRepository that manages namespaces, conceptspaces, 
 * prefixes and SemacticTypes for ThinkLab.
 * 
 * @author Ioannis N. Athanasiadis
 *
 */
public class Registry {
	private static Registry registry = null;
	
	private Hashtable<URI, String> uri2cs = new Hashtable<URI, String>();
	private Hashtable<String, URI> cs2uri = new Hashtable<String, URI>();
	private int counter =1;

	private Registry(){
		registry = this;
	}
	
	protected static Registry get(){
		if (registry == null)
			   return new Registry();
			return registry;
	}
	
	protected String registerURI(String cs,URI logicalURI){
		if(uri2cs.containsKey(logicalURI)){
			return uri2cs.get(logicalURI);
		} else if (cs2uri.containsKey(cs)){
			cs = "cs"+counter;
			counter++;
			cs2uri.put(cs, logicalURI);
			uri2cs.put(logicalURI, cs);
			return cs;
		} else{
			cs2uri.put(cs, logicalURI);
			uri2cs.put(logicalURI, cs);
			return cs;
		}
	}
	
	protected void updateRegistry(OWLOntologyManager model, OWLOntology ontology){
		
	}

	protected boolean containsConceptSpace(String cs){
		return cs2uri.containsKey(cs);
	}
	
	protected SemanticType getSemanticType(URI uri){
		String cs = uri2cs.get(getOntoURI(uri));
		String label = uri.getFragment();
		return new SemanticType(cs,label);
	}
	
	protected SemanticType getSemanticType(OWLEntity thing){
		return getSemanticType(thing.getURI());
	}

	
	protected URI getOntoURI(URI uri){
		return URI.create(uri.getScheme() +":"+ uri.getSchemeSpecificPart());
	}
	/**
	 * @param s
	 */
	protected void removeConceptSpace(String cs) {
		URI uri = cs2uri.get(cs);
		cs2uri.remove(cs);
		uri2cs.remove(uri);
	}

	/**
	 * @param cs
	 * @return
	 */
	protected URI getURI(String conceptSpace) {
		return cs2uri.get(conceptSpace);
	}
	
	protected URI getURI(SemanticType st) throws ThinklabMalformedSemanticTypeException{
		if(cs2uri.containsKey(st.getConceptSpace())){
			return URI.create(getURI(st.getConceptSpace()).toString()+"#"+st.getLocalName());
		} else throw new ThinklabMalformedSemanticTypeException("Malformed Semantic Type" + st);
	}
	
	protected String getConceptSpace(URI uri){
		
		return uri2cs.get(MiscUtilities.removeFragment(uri));
	}
}
