/**
 * Created on Feb 29, 2008 
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
import java.net.URL;
import java.util.Collection;
import java.util.Set;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.IConcept;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository;
import org.integratedmodelling.thinklab.interfaces.IOntology;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;

/**
 * @author Ioannis N. Athanasiadis
 * 
 */
public class FileKnowledgeRepositoryTest {
	public static void main(String... args) throws ThinklabException, Exception {

		KnowledgeManager km = new KnowledgeManager(
				new FileKnowledgeRepository());
		km.initialize();
		URL pizzaURL = new URL("http://www.co-ode.org/ontologies/pizza/2007/02/12/pizza.owl");
		URI pizzaURI = URI.create("http://www.co-ode.org/ontologies/pizza/pizza.owl");
		km.getKnowledgeRepository().importOntology(pizzaURL,"pizza", false);

		OWLOntologyManager manager = FileKnowledgeRepository.KR.manager;
		OWLOntology ontology = manager.getOntology(pizzaURI);
		Set<OWLClass> clss = ontology.getReferencedClasses();
		for (OWLClass cls : clss) {
			// System.out.println();
			for (OWLObjectProperty op : ontology
					.getReferencedObjectProperties()) {
				Set<OWLDescription> rang = (op.getDomains(manager
						.getOntologies()));
				if (rang.contains(cls))
					System.out.println(cls + " == " + op + " ==> "
							+ op.getRanges(manager.getOntologies()));
			}
			for (OWLDataProperty op : ontology.getReferencedDataProperties()) {
				Set<OWLDescription> rang = (op.getDomains(manager
						.getOntologies()));
				if (rang.contains(cls))
					System.out.println(cls + "--" + op + "--> "
							+ op.getRanges(manager.getOntologies()));
			}

		}
		// The URL is not needed any more
		km.getKnowledgeRepository().connectReasoner(null);
		km.getKnowledgeRepository().classifyTaxonomy();
		km.getKnowledgeRepository().classifyTaxonomy();
		km.getKnowledgeRepository().classifyTaxonomy();

		
		{
		Concept food = (Concept) km.requireConcept("pizza:VegetarianPizza");
		Collection<IConcept> allparents = food.getAllParents();
		System.out.println(allparents);
		Collection<IConcept> parents = food.getParents();
		System.out.println(parents);
		}
		{
		Concept text = (Concept) km.requireConcept("thinklab-core:Text");
		Collection<IConcept> allparents = text.getAllParents();
		System.out.println(allparents);
		Collection<IConcept> parents = text.getParents();
		System.out.println(parents);
		Concept st = (Concept) km.requireConcept("thinklab-core:StorageType");
		System.out.println(text.is(st));
		}

	}

	public static void print(IKnowledgeRepository kr) {
		Collection<IOntology> ontos = kr.retrieveAllOntologies();
		for (IOntology onto : ontos) {
			System.out.println("* Ontology *" + onto.getURI());
			for (IConcept c : onto.getConcepts())
				System.out.println(" Concept " + c.getURI());
		}
	}

}
