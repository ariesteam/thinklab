package org.integratedmodelling.mca.electre3.controller;

import org.integratedmodelling.mca.electre3.model.Alternative;
import org.integratedmodelling.mca.electre3.model.Criterion;
import org.integratedmodelling.mca.electre3.model.E3Model;
import org.integratedmodelling.mca.electre3.model.MatrixModel;
import org.integratedmodelling.mca.electre3.store.StorageBox;
import org.integratedmodelling.mca.electre3.view.E3View;

/**
 *
 * @author Edwin Boaz Soenaryo
 */
public class E3Controller {

    public E3Controller() {
        initMVC();
        view.setVisible(true);
        criterionCounter = 1;
        alternativeCounter = 1;
        model.addCriterion("Criterion " + criterionCounter++);
        model.addAlternative("Alternative " + alternativeCounter++);
    }

    public void addModelAlternative() {
        model.addAlternative("Alternative " + alternativeCounter++);
    }

    public void addViewAlternative(Alternative a) {
        view.addAlternative(a);
    }

    public void addViewCriterion(Criterion c) {
        view.addCriterion(c);
    }
    
    public void addModelCriterion() {
        model.addCriterion("Criterion " + criterionCounter++);
    }

    public void compute() {
        model.compute();
    }

    public MatrixModel getConcordances() {
        return model.getConcordances();
    }

    public MatrixModel getCredibility() {
        return model.getCredibility();
    }

    public MatrixModel getIndiConcordance(Criterion c) {
        return model.getIndiConcordance(c);
    }

    public MatrixModel getIndiDiscordance(Criterion c) {
        return model.getIndiDiscordance(c);
    }

    public StorageBox getProjectData() {
        return model.getProjectData();
    }

    public void loadProject(StorageBox box) {
        model.loadProject(box);
    }

    public void setViewCriterion(Criterion c) {
        view.setCriterion(c);
    }

    public void setViewEditAlternative(boolean b) {
        view.setEditAlternative(b);
    }
    
    public void setViewEditCriterion(boolean editing) {
        view.setEditCriterion(editing);
    }
    
    
    private void initMVC() {
        view = new E3View();
        view.bindController(this);
        
        model = new E3Model();
        model.bindController(this);
    }
    
    private E3View view;
    private E3Model model;
    private int criterionCounter;
    private int alternativeCounter;
    
}
