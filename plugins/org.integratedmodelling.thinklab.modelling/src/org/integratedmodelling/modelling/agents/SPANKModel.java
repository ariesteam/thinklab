package org.integratedmodelling.modelling.agents;

import java.util.ArrayList;
import java.util.HashMap;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.modelling.ObservationFactory;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.modelling.model.DefaultAbstractModel;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.utils.Polylist;

import clojure.lang.Compiler;

public class SPANKModel extends DefaultAbstractModel {

	protected HashMap<String, Object> parameters = new HashMap<String, Object>();
	
    @Override
    public IConcept getCompatibleObservationType(ISession session) {
        return CoreScience.Observation();
    }
    
    @Override
    public IModel getConfigurableClone() {
        
        SPANKModel ret = new SPANKModel();
        ret.copy(this);
        return ret; 
    }

    @Override
    public void applyClause(String keyword, Object argument)
            throws ThinklabException {

        if (keyword.equals(":source-threshold")   ||
            keyword.equals(":sink-threshold")     ||
            keyword.equals(":use-threshold")      ||
            keyword.equals(":trans-threshold")    ||
            keyword.equals(":source-type")        ||
            keyword.equals(":sink-type")          ||
            keyword.equals(":use-type")           ||
            keyword.equals(":benefit-type")       ||
            keyword.equals(":rv-max-states")      ||
            keyword.equals(":downscaling-factor") ||
            keyword.equals(":save-file")) {

            Object evaledArgument = null;
            try {
                evaledArgument = Compiler.eval(argument);
            } catch (Exception e) {
                evaledArgument = argument;
            }

            parameters.put(keyword, evaledArgument);

        } else {
            super.applyClause(keyword, argument);
        }
    }

    @Override
    public Polylist buildDefinition(IKBox kbox, ISession session, IContext context, int flags) throws ThinklabException {

        ArrayList<Object> arr = new ArrayList<Object>();
            
        arr.add("modeltypes:SPANKTransformer");
        arr.add(Polylist.list(
                CoreScience.HAS_OBSERVABLE,
                Polylist.list(getObservableClass())));

        Polylist ret = Polylist.PolylistFromArrayList(arr);
        ret = ObservationFactory.addReflectedField(ret, "parameters", parameters);

        return ret;
    }

    @Override
    public Polylist conceptualize() throws ThinklabException {
        return null;
    }

    @Override
    protected void validateSemantics(ISession session) throws ThinklabException {
    }

}
