package org.integratedmodelling.modelling.implementations.observations;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.CoreScience.PhysicalNature;
import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.modelling.data.CategoricalDistributionDatasource;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;

@InstanceImplementation(concept="modeltypes:ProbabilisticRanking")
public class ProbabilisticRanking extends ModeledClassification {

	@Override
	public IState createState(int size, IObservationContext context)
			throws ThinklabException {

		IConcept[] vmaps = new IConcept[classifiers.size()];
		for (int i = 0; i < classifiers.size(); i++)
			vmaps[i] = classifiers.get(i).getSecond();
		
		IState ret =
			new CategoricalDistributionDatasource(cSpace, size, vmaps, classifiers, (ObservationContext) context);

		ret.getMetadata().merge(this.metadata);
		
		PhysicalNature physicalNature = 
			CoreScience.getPhysicalNature(getObservableClass());		
		metadata.put(Metadata.PHYSICAL_NATURE, physicalNature);

		return ret;
	
	}

	@Override
	public void initialize(IInstance i) throws ThinklabException {
		super.initialize(i);
	}

}
