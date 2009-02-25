package org.integratedmodelling.geospace.implementations.observations;

import org.integratedmodelling.corescience.implementations.observations.Observation;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;

/**
 * No methods for now, this is typically produced by a transformer and not 
 * encoded directly so we don't need to expose its semantics at the OWL level for now.
 * 
 * TODO we will need soon if this has to be contextualized...
 * 
 * @author Ferdinando
 *
 */
@InstanceImplementation(concept=Geospace.CLASSIFIED_GRID)
public class ClassifiedGrid extends Observation {

}
