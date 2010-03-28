package org.integratedmodelling.mca.electre3.model;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import org.integratedmodelling.mca.electre3.controller.E3Controller;
import org.integratedmodelling.mca.electre3.store.StorageBox;

/**
 *
 * @author Edwin Boaz Soenaryo
 */
public class E3Model {

    public E3Model() {
        criteria = new LinkedList<Criterion>();
        alternatives = new LinkedList<Alternative>();
    }

    public void addAlternative(String name) {
        Alternative a = new Alternative(name);
        addAlternative(a);
    }

    public void addAlternative(Alternative a) {
        alternatives.add(a);
        controller.setViewEditAlternative(true);
        controller.addViewAlternative(a);
    }

    public void addCriterion(String code) {
        Criterion c = new Criterion(code);
        addCriterion(c);
    }

    public void addCriterion(Criterion c) {
        criteria.add(c);
        controller.setViewEditCriterion(true);
        controller.addViewCriterion(c);
    }

    public void bindController(E3Controller controller) {
        this.controller = controller;
    }

    public void compute() {
        CordanceComputer corCom = new CordanceComputer();

        corCom.computeConcordance(criteria, alternatives);
        indiConcordance = corCom.getIndiConcordance();
        globalConcordance = corCom.getGlobalConcordance();

        corCom.computeDiscordance(criteria, alternatives);
        indiDiscordance = corCom.getIndiDiscordance();

        CredibilityComputer creCom = new CredibilityComputer();

        creCom.computeCredibility(criteria, alternatives, globalConcordance, indiDiscordance);
        credibility = creCom.getCredibility();
    }

    public MatrixModel getConcordances() {
        return globalConcordance;
    }

    public MatrixModel getCredibility() {
        return credibility;
    }

    public MatrixModel getIndiConcordance(Criterion c) {
        return indiConcordance.get(c);
    }

    public MatrixModel getIndiDiscordance(Criterion c) {
        return indiDiscordance.get(c);
    }

    public StorageBox getProjectData() {
        StorageBox box = new StorageBox();
        box.setAlternatives(alternatives);
        box.setCriteria(criteria);
        return box;
    }

    public void loadProject(StorageBox box) {
        LinkedList<Criterion> newCri = box.getCriteria();
        LinkedList<Alternative> newAlt = box.getAlternatives();
        criteria = new LinkedList<Criterion>();
        alternatives = new LinkedList<Alternative>();
        for (Iterator<Criterion> i = newCri.iterator(); i.hasNext();) {
            addCriterion(i.next());
        }
        for (Iterator<Alternative> i = newAlt.iterator(); i.hasNext();) {
            addAlternative(i.next());
        }
        compute();
    }
    
    private E3Controller controller;
    private MatrixModel globalConcordance;
    private Hashtable<Criterion, MatrixModel> indiConcordance;
    private Hashtable<Criterion, MatrixModel> indiDiscordance;
    private LinkedList<Criterion> criteria;
    private LinkedList<Alternative> alternatives;
    private MatrixModel credibility;
}
