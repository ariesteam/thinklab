package org.integratedmodelling.thinklab.modelling.model.implementation;

import org.integratedmodelling.thinklab.modelling.model.DefaultAbstractModel;

/**
 * The reference implementation of the model object created by defmodel. It holds one
 * or more concrete models (e.g. measurements, rankings etc) that can represent the
 * observable, and is capable of selecting the applicable one(s) appropriately to 
 * observe the observable in a given context, generating the Observation structure
 * that can be resolved to States through contextualization.
 *  
 * Compared to any other model, it supports:
 * 
 * <ol>
 * <li>a context model that is computed before the others and drives the choice of
 *     contingent models;</li>
 * <li>multiple models (contingencies) to be selected on the basis of the context
 * 	   model if present, or on previous models not being defined in each specific
 * 	   context state;</li>
 * </ol>
 * 	
 * @author Ferd
 *
 */
public class Model extends DefaultAbstractModel {

}
