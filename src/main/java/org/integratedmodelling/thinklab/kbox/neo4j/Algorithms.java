package org.integratedmodelling.thinklab.kbox.neo4j;

import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;

public class Algorithms {

	public static Evaluator semanticClosure(final IConcept concept) {
		
		return new Evaluator() {
			
			@Override
			public Evaluation evaluate(Path arg0) {
				Node node = arg0.startNode();
				IConcept c = 
					Thinklab.c(node.getProperty(NeoKBox.TYPE_PROPERTY).toString());
				if (c.getSemanticClosure().contains(concept)) {
					return Evaluation.INCLUDE_AND_CONTINUE;
				}
				return Evaluation.EXCLUDE_AND_CONTINUE;
			}
		};
	}
	
	
}
