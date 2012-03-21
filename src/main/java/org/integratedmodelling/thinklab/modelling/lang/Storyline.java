package org.integratedmodelling.thinklab.modelling.lang;

import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.lang.parsing.IStorylineDefinition;

@Concept(NS.STORYLINE)
public class Storyline extends ModelObject<Storyline> implements IStorylineDefinition {

	@Override
	public Storyline demote() {
		return this;
	}


}
