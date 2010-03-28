package org.integratedmodelling.mca.electre3.store;

import java.io.Serializable;
import java.util.LinkedList;

import org.integratedmodelling.mca.electre3.model.Alternative;
import org.integratedmodelling.mca.electre3.model.Criterion;

/**
 *
 * @author Edwin Boaz Soenaryo
 */
public class StorageBox implements Serializable {
	
	private static final long serialVersionUID = 3117826326217824360L;
	public StorageBox() {
        alternatives = null;
        criteria = null;
    }

    public LinkedList<Alternative> getAlternatives() {
        return alternatives;
    }

    public void setAlternatives(LinkedList<Alternative> alternatives) {
        this.alternatives = alternatives;
    }

    public LinkedList<Criterion> getCriteria() {
        return criteria;
    }

    public void setCriteria(LinkedList<Criterion> criteria) {
        this.criteria = criteria;
    }
    
    private LinkedList<Alternative> alternatives;
    private LinkedList<Criterion> criteria;
    
}
