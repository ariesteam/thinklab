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
