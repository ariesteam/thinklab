/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.modelling.corescience;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.modelling.model.DefaultAbstractModel;
import org.integratedmodelling.modelling.model.DefaultDynamicAbstractModel;
import org.integratedmodelling.modelling.model.DefaultStatefulAbstractModel;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.utils.Polylist;

public class RankingModel extends DefaultDynamicAbstractModel {

	boolean isBinary = false;
	boolean isCategorical = false;
	boolean isProbabilistic = false;
	boolean isInteger = false;
	boolean isScale = false;
	double scaleMin = -1.0;
	double scaleMax = -1.0;

	@Override
	protected void copy(DefaultStatefulAbstractModel model) {

		super.copy(model);

		isBinary = ((RankingModel) model).isBinary;
		isCategorical = ((RankingModel) model).isCategorical;
		isProbabilistic = ((RankingModel) model).isProbabilistic;
		isInteger = ((RankingModel) model).isInteger;
		isScale = ((RankingModel) model).isScale;
		scaleMin = ((RankingModel) model).scaleMin;
		scaleMax = ((RankingModel) model).scaleMax;
	}

	@Override
	public void applyClause(String keyword, Object argument)
			throws ThinklabException {

		if (keyword.equals(":binary"))
			isBinary = (Boolean) argument;
		else if (keyword.equals(":numeric-classification"))
			isCategorical = (Boolean) argument;
		else if (keyword.equals(":probabilistic"))
			isProbabilistic = (Boolean) argument;
		else if (keyword.equals(":min"))
			scaleMin = ((Number) argument).doubleValue();
		else if (keyword.equals(":max"))
			scaleMax = ((Number) argument).doubleValue();
		else if (keyword.equals(":integer"))
			isInteger = (Boolean) argument;
		else if (keyword.equals(":scale"))
			isScale = (Boolean) argument;
		else
			super.applyClause(keyword, argument);
	}

	public RankingModel(String namespace) {
		super(namespace);
		this.metadata.put(Metadata.CONTINUOUS, Boolean.TRUE);
	}

	@Override
	public String toString() {
		return ("ranking");
	}

	@Override
	public void validateMediatedModel(IModel model)
			throws ThinklabValidationException {
		// a ranking can mediate another ranking or a measurement
		super.validateMediatedModel(model);
		if (!((model instanceof MeasurementModel) || (model instanceof RankingModel))) {
			throw new ThinklabValidationException(
					"ranking models can only mediate ranking or measurements");
		}
	}

	@Override
	protected Object validateState(Object state)
			throws ThinklabValidationException {
		return state instanceof Number ? state : Double.parseDouble(state
				.toString());
	}

	@Override
	public IConcept getCompatibleObservationType(ISession session) {
		return CoreScience.Ranking();
	}

	@Override
	public IModel getConfigurableClone() {
		RankingModel ret = new RankingModel(namespace);
		ret.copy(this);
		ret.isBinary = isBinary;
		ret.isCategorical = isCategorical;
		ret.isProbabilistic = isProbabilistic;
		ret.isScale = isScale;
		ret.scaleMax = scaleMax;
		ret.scaleMin = scaleMin;
		return ret;
	}

	@Override
	public Polylist buildDefinition(IKBox kbox, ISession session,
			IContext context, int flags) throws ThinklabException {

		/*
		 * TODO choose observation class according to derivative, probability
		 * etc.
		 */
		Polylist def = Polylist
				.listNotNull(
						((dynSpecs == null && changeSpecs == null && derivativeSpecs == null) ? CoreScience.RANKING
								: "modeltypes:DynamicRanking"),
						Polylist.list(CoreScience.HAS_FORMAL_NAME, getLocalFormalName()),
						(dynSpecs != null ? Polylist.list(":code", dynSpecs)
								: null),
						(changeSpecs != null ? Polylist.list(":change",
								changeSpecs) : null),
						(derivativeSpecs != null ? Polylist.list(":derivative",
								derivativeSpecs) : null),
						((dynSpecs != null || changeSpecs != null || derivativeSpecs != null) ? Polylist
								.list(
										"modeltypes:hasExpressionLanguage",
										this.lang.equals(language.CLOJURE) ? "clojure"
												: "mvel")
								: null), (this.distribution != null ? Polylist
								.list(":distribution", this.distribution)
								: null), (this.state != null ? Polylist.list(
								CoreScience.HAS_VALUE, this.state) : null),

						/*
						 * TODO add scale attributes
						 */
						(isMediating() ? null : Polylist.list(
								CoreScience.HAS_OBSERVABLE, Polylist
										.list(getObservableClass()))));

		return addDefaultFields(addImplicitExtents(def, context));
	}

	@Override
	public Polylist conceptualize() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void validateSemantics(ISession session) throws ThinklabException {
		// TODO check that scale has min/max, that we don't have scale if
		// we are binary or categories, and that probabilistic specs and
		// states are OK and match
	}

	@Override
	protected IModel validateSubstitutionModel(IModel m) {

		IModel ret = null;

		if (/* m instanceof RankingModel*/ true) {
			// FIXME mediate to substitute ranks and scale if necessary.
			try {
				ret = (IModel) ((DefaultAbstractModel) m).clone();
			} catch (CloneNotSupportedException e) {
			}
		}

		return ret;
	}

}
