package org.integratedmodelling.thinklab.modelling.model.implementation;

import org.integratedmodelling.thinklab.modelling.model.DefaultAbstractModel;

/**
 * The reference implementation of the model object created by defmodel. It holds one
 * or more concrete models (e.g. measurements, rankings etc) that can represent the
 * observable, and is capable of selecting the applicable one(s) appropriately to 
 * observe the observable in a given context, generating the Observation structure
 * that can be resolved to States through contextualization.
 *  
 * @author Ferd
 *
 */
public class Model extends DefaultAbstractModel {

}
