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
package org.integratedmodelling.mca.electre3.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.integratedmodelling.mca.electre3.controller.E3Controller;
import org.integratedmodelling.mca.electre3.model.Alternative;
import org.integratedmodelling.mca.electre3.model.Criterion;
import org.integratedmodelling.mca.electre3.model.MatrixModel;
import org.integratedmodelling.mca.electre3.store.Storage;
import org.integratedmodelling.mca.electre3.store.StorageBox;

/**
 *
 * @author  Edwin Boaz Soenaryo
 */
public class E3View extends javax.swing.JFrame {

    /** Creates new form E3View */
    public E3View() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(E3View.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(E3View.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(E3View.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(E3View.class.getName()).log(Level.SEVERE, null, ex);
        }

        initComponents();
        resetState();
        jspPerformanceMatrixContainer.getViewport().setBackground(Color.WHITE);
        fileChooser = new JFileChooser();
    }

    private void resetState() {
        criteria = new DefaultListModel();
        alternatives = new DefaultListModel();
        performances = new TwoHeadersTableModel();
        editPerformanceMatrix = false;
        tblPerformanceMatrix.setEnabled(false);
        jbtSavePerformance.setVisible(false);
        cbxIndividualView.setVisible(false);
        individualMatrices = new DefaultComboBoxModel();
    }

    public void addAlternative(Alternative a) {
        alternatives.addElement(a);
        lstAlternatives.setModel(alternatives);
        lstAlternatives.setSelectedIndex(alternatives.getSize() - 1);
        setAlternative(a);
        regeneratePerformanceMatrix();
    }

    public void addCriterion(Criterion c) {
        criteria.addElement(c);
        lstCriteria.setModel(criteria);
        lstCriteria.setSelectedIndex(criteria.getSize() - 1);
        setCriterion(c);
        regeneratePerformanceMatrix();
        individualMatrices.addElement(c);
        cbxIndividualView.setModel(individualMatrices);
        cbxIndividualView.setSelectedIndex(0);
    }

    public void setCriterion(Criterion c) {
        this.activeCriterion = c;

        if (editCriterion) {
            CriterionEditPanel cep = new CriterionEditPanel(this, c);
            pnlCriteriaContainer.removeAll();
            pnlCriteriaContainer.add(cep, BorderLayout.CENTER);
            pnlCriteriaContainer.revalidate();
        } else {
            CriterionPanel cp = new CriterionPanel(this, c);
            pnlCriteriaContainer.removeAll();
            pnlCriteriaContainer.add(cp, BorderLayout.CENTER);
            pnlCriteriaContainer.revalidate();
        }
    }

    public void setEditCriterion(boolean editing) {
        if (editCriterion != editing) {
            editCriterion = editing;
            if (activeCriterion != null) {
                setCriterion(activeCriterion);
                lstCriteria.updateUI();
                regeneratePerformanceMatrix();
            }
        }
    }

    public void updateActiveAlternative(String name, String desc) {
        activeAlternative.setName(name);
        activeAlternative.setDescription(desc);
    }

    public void updateActiveCriterion(String name, String desc, double weight,
            double inda, double indb, double prea, double preb, double veta,
            double vetb, boolean vetoEnabled, boolean ascending) {
        activeCriterion.setCode(name);
        activeCriterion.setDescription(desc);
        activeCriterion.setWeight(weight);
        activeCriterion.setIndifferenceThreshold(inda, indb);
        activeCriterion.setPreferenceThreshold(prea, preb);
        activeCriterion.setVetoThreshold(veta, vetb);
        activeCriterion.setVetoEnabled(vetoEnabled);
        activeCriterion.setAscendingPref(ascending);
    }

    public boolean isEditAlternative() {
        return editAlternative;
    }

    public void setEditAlternative(boolean b) {
        if (editAlternative != b) {
            this.editAlternative = b;

            if (activeAlternative != null) {
                setAlternative(activeAlternative);
                lstAlternatives.updateUI();
                regeneratePerformanceMatrix();
            }
        }
    }

    public boolean isEditPerformanceMatrix() {
        return editPerformanceMatrix;
    }

    public void setEditPerformanceMatrix(boolean editPerformanceMatrix) {
        this.editPerformanceMatrix = editPerformanceMatrix;
    }

    private void regeneratePerformanceMatrix() {
        performances = new TwoHeadersTableModel();
        int rows = alternatives.getSize() + 1;
        int cols = criteria.getSize() + 1;
        String[][] data = new String[rows][cols];
        data[0][0] = "";
        int i = 1;
        performances.addColumn(null);
        for (Enumeration ec = criteria.elements(); ec.hasMoreElements();) {
            Criterion c = (Criterion) (ec.nextElement());
            data[0][i++] = c.getCode();
            performances.addColumn(c);
        }
        performances.addRow(data[0]);

        i = 0;
        for (Enumeration ea = alternatives.elements(); ea.hasMoreElements();) {
            Alternative a = (Alternative) (ea.nextElement());
            data[++i][0] = a.getName();
            int j = 1;
            for (Enumeration ec = criteria.elements(); ec.hasMoreElements();) {
                Criterion c = (Criterion) (ec.nextElement());
                try {
                    double perf = a.getCriterionPerformance(c);
                    data[i][j++] = ViewSettings.getDecimalFormat().format(perf);
                } catch (Exception e) {
                    data[i][j++] = "0.0";
                }
            }
            performances.addRow(data[i]);
        }
        tblPerformanceMatrix.setModel(performances);
    }

    private void setAlternative(Alternative a) {
        this.activeAlternative = a;

        if (editAlternative) {
            AlternativeEditPanel aep = new AlternativeEditPanel(this, a);
            pnlAlternativesContainer.removeAll();
            pnlAlternativesContainer.add(aep, BorderLayout.CENTER);
            pnlAlternativesContainer.revalidate();
        } else {
            AlternativePanel ap = new AlternativePanel(this, a);
            pnlAlternativesContainer.removeAll();
            pnlAlternativesContainer.add(ap, BorderLayout.CENTER);
            pnlAlternativesContainer.revalidate();
        }
    }

    public void bindController(E3Controller controller) {
        this.controller = controller;
    }

    public Object[] getDisplayedCriteria() {
        return criteria.toArray();
    }

    public Object[] getDisplayedAlternatives() {
        return alternatives.toArray();
    }

    private void setResultView(String viewName) {
        MatrixModel cm = null;
        if (viewName.equals("Global Concordance Matrix")) {
            cbxIndividualView.setVisible(false);
            cm = controller.getConcordances();
        } else if (viewName.equals("Credibility Matrix")) {
            cbxIndividualView.setVisible(false);
            cm = controller.getCredibility();
        } else if (viewName.equals("Individual Concordance Matrix") ||
                viewName.equals("Individual Discordance Matrix")) {
            cbxIndividualView.setVisible(true);
            return;
        }

        MatrixView cmp = new MatrixView(this);
        cmp.setModel(cm);
        pnlResultContainer.removeAll();
        pnlResultContainer.add(cmp, BorderLayout.CENTER);
        pnlResultContainer.updateUI();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlContainer = new javax.swing.JPanel();
        jtpMainContainer = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jbtOpenProject = new javax.swing.JButton();
        jbtSaveProject = new javax.swing.JButton();
        pnlCriteria = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstCriteria = new javax.swing.JList();
        jbtAdd = new javax.swing.JButton();
        jbtDelete = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        pnlCriteriaContainer = new javax.swing.JPanel();
        pnlAlternatives = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstAlternatives = new javax.swing.JList();
        jbtAddAlternative = new javax.swing.JButton();
        jbtDeleteAlternative = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        pnlAlternativesContainer = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jlbPerformanceMatrix = new javax.swing.JLabel();
        jspPerformanceMatrixContainer = new javax.swing.JScrollPane();
        tblPerformanceMatrix = new javax.swing.JTable();
        jbtSavePerformance = new javax.swing.JButton();
        jbtEditPerformance = new javax.swing.JButton();
        pnlMethod = new javax.swing.JPanel();
        jbtCompute = new javax.swing.JButton();
        pnlResult = new javax.swing.JPanel();
        cbxResultSelector = new javax.swing.JComboBox();
        pnlResultContainer = new javax.swing.JPanel();
        cbxIndividualView = new javax.swing.JComboBox();
        jlbTitle = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ELECTRE III Demo");

        pnlContainer.setBackground(new java.awt.Color(255, 255, 255));

        jtpMainContainer.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jbtOpenProject.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jbtOpenProject.setText("Open an existing project");
        jbtOpenProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtOpenProjectActionPerformed(evt);
            }
        });

        jbtSaveProject.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jbtSaveProject.setText("Save current project");
        jbtSaveProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtSaveProjectActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jbtSaveProject, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jbtOpenProject, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(554, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jbtOpenProject)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtSaveProject)
                .addContainerGap(338, Short.MAX_VALUE))
        );

        jtpMainContainer.addTab("   Project   ", jPanel2);

        pnlCriteria.setBackground(new java.awt.Color(255, 255, 255));

        jScrollPane1.setBorder(null);

        lstCriteria.setBackground(new java.awt.Color(224, 236, 255));
        lstCriteria.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(224, 236, 255), 5));
        lstCriteria.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstCriteriaValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(lstCriteria);

        jbtAdd.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jbtAdd.setText("Add");
        jbtAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtAddActionPerformed(evt);
            }
        });

        jbtDelete.setText("Delete");

        jPanel5.setOpaque(false);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 145, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        pnlCriteriaContainer.setBackground(new java.awt.Color(255, 255, 255));
        pnlCriteriaContainer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(195, 217, 255), 4));
        pnlCriteriaContainer.setOpaque(false);
        pnlCriteriaContainer.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout pnlCriteriaLayout = new javax.swing.GroupLayout(pnlCriteria);
        pnlCriteria.setLayout(pnlCriteriaLayout);
        pnlCriteriaLayout.setHorizontalGroup(
            pnlCriteriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCriteriaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlCriteriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlCriteriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(pnlCriteriaLayout.createSequentialGroup()
                            .addComponent(jbtAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbtDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlCriteriaContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlCriteriaLayout.setVerticalGroup(
            pnlCriteriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCriteriaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlCriteriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlCriteriaContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
                    .addGroup(pnlCriteriaLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlCriteriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jbtAdd)
                            .addComponent(jbtDelete))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jtpMainContainer.addTab("   Criteria   ", pnlCriteria);

        pnlAlternatives.setBackground(new java.awt.Color(255, 255, 255));

        jScrollPane2.setBorder(null);

        lstAlternatives.setBackground(new java.awt.Color(224, 236, 255));
        lstAlternatives.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(224, 236, 255), 5));
        lstAlternatives.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstAlternativesValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(lstAlternatives);

        jbtAddAlternative.setFont(new java.awt.Font("Tahoma", 1, 11));
        jbtAddAlternative.setText("Add");
        jbtAddAlternative.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtAddAlternativeActionPerformed(evt);
            }
        });

        jbtDeleteAlternative.setText("Delete");

        jPanel7.setOpaque(false);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 145, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        pnlAlternativesContainer.setBackground(new java.awt.Color(255, 255, 255));
        pnlAlternativesContainer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(195, 217, 255), 4));
        pnlAlternativesContainer.setOpaque(false);
        pnlAlternativesContainer.setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(195, 217, 255), 4));

        jlbPerformanceMatrix.setFont(new java.awt.Font("Tahoma", 1, 12));
        jlbPerformanceMatrix.setText("Performance Matrix");

        jspPerformanceMatrixContainer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(195, 217, 255), 2));

        tblPerformanceMatrix.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblPerformanceMatrix.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null}
            },
            new String [] {
                "Title 1"
            }
        ));
        tblPerformanceMatrix.setColumnSelectionAllowed(true);
        tblPerformanceMatrix.setGridColor(new java.awt.Color(195, 217, 255));
        tblPerformanceMatrix.setRowHeight(24);
        tblPerformanceMatrix.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblPerformanceMatrix.setTableHeader(null);
        jspPerformanceMatrixContainer.setViewportView(tblPerformanceMatrix);

        jbtSavePerformance.setText("Save");
        jbtSavePerformance.setOpaque(false);
        jbtSavePerformance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtSavePerformanceActionPerformed(evt);
            }
        });

        jbtEditPerformance.setText("Edit");
        jbtEditPerformance.setOpaque(false);
        jbtEditPerformance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtEditPerformanceActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jspPerformanceMatrixContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 544, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jlbPerformanceMatrix)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 310, Short.MAX_VALUE)
                        .addComponent(jbtEditPerformance)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtSavePerformance)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlbPerformanceMatrix)
                    .addComponent(jbtSavePerformance)
                    .addComponent(jbtEditPerformance))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jspPerformanceMatrixContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout pnlAlternativesLayout = new javax.swing.GroupLayout(pnlAlternatives);
        pnlAlternatives.setLayout(pnlAlternativesLayout);
        pnlAlternativesLayout.setHorizontalGroup(
            pnlAlternativesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAlternativesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlAlternativesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlAlternativesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(pnlAlternativesLayout.createSequentialGroup()
                            .addComponent(jbtAddAlternative, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbtDeleteAlternative, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlAlternativesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlAlternativesContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlAlternativesLayout.setVerticalGroup(
            pnlAlternativesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAlternativesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlAlternativesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlAlternativesLayout.createSequentialGroup()
                        .addComponent(pnlAlternativesContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlAlternativesLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlAlternativesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jbtAddAlternative)
                            .addComponent(jbtDeleteAlternative))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jtpMainContainer.addTab("   Alternatives   ", pnlAlternatives);

        pnlMethod.setBackground(new java.awt.Color(255, 255, 255));

        jbtCompute.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jbtCompute.setText("Compute");
        jbtCompute.setOpaque(false);
        jbtCompute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtComputeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlMethodLayout = new javax.swing.GroupLayout(pnlMethod);
        pnlMethod.setLayout(pnlMethodLayout);
        pnlMethodLayout.setHorizontalGroup(
            pnlMethodLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMethodLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jbtCompute)
                .addContainerGap(648, Short.MAX_VALUE))
        );
        pnlMethodLayout.setVerticalGroup(
            pnlMethodLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMethodLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jbtCompute)
                .addContainerGap(369, Short.MAX_VALUE))
        );

        jtpMainContainer.addTab("   Method    ", pnlMethod);

        pnlResult.setBackground(new java.awt.Color(255, 255, 255));

        cbxResultSelector.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        cbxResultSelector.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Credibility Matrix", "Global Concordance Matrix", "Individual Concordance Matrix", "Individual Discordance Matrix" }));
        cbxResultSelector.setOpaque(false);
        cbxResultSelector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxResultSelectorActionPerformed(evt);
            }
        });

        pnlResultContainer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(195, 217, 255), 4));
        pnlResultContainer.setOpaque(false);
        pnlResultContainer.setLayout(new java.awt.BorderLayout());

        cbxIndividualView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxIndividualViewActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlResultLayout = new javax.swing.GroupLayout(pnlResult);
        pnlResult.setLayout(pnlResultLayout);
        pnlResultLayout.setHorizontalGroup(
            pnlResultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlResultLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlResultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlResultContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 727, Short.MAX_VALUE)
                    .addGroup(pnlResultLayout.createSequentialGroup()
                        .addComponent(cbxResultSelector, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbxIndividualView, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnlResultLayout.setVerticalGroup(
            pnlResultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlResultLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlResultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbxIndividualView, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxResultSelector, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlResultContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
                .addContainerGap())
        );

        jtpMainContainer.addTab("   Result   ", pnlResult);

        jlbTitle.setBackground(new java.awt.Color(195, 217, 255));
        jlbTitle.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jlbTitle.setText(" ELECTRE III Demo");
        jlbTitle.setOpaque(true);

        javax.swing.GroupLayout pnlContainerLayout = new javax.swing.GroupLayout(pnlContainer);
        pnlContainer.setLayout(pnlContainerLayout);
        pnlContainerLayout.setHorizontalGroup(
            pnlContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContainerLayout.createSequentialGroup()
                .addGroup(pnlContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlContainerLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jtpMainContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 752, Short.MAX_VALUE))
                    .addGroup(pnlContainerLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jlbTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 762, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlContainerLayout.setVerticalGroup(
            pnlContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContainerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jlbTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtpMainContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jbtAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtAddActionPerformed
    controller.addModelCriterion();
}//GEN-LAST:event_jbtAddActionPerformed

private void lstCriteriaValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstCriteriaValueChanged
    if (lstCriteria.getSelectedValue() != null) {
        setCriterion((Criterion) lstCriteria.getSelectedValue());
    }
}//GEN-LAST:event_lstCriteriaValueChanged

private void lstAlternativesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstAlternativesValueChanged
    if (lstAlternatives.getSelectedValue() != null) {
        setAlternative((Alternative) lstAlternatives.getSelectedValue());
    }
}//GEN-LAST:event_lstAlternativesValueChanged

private void jbtAddAlternativeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtAddAlternativeActionPerformed
    controller.addModelAlternative();
}//GEN-LAST:event_jbtAddAlternativeActionPerformed

private void jbtEditPerformanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtEditPerformanceActionPerformed
    editPerformanceMatrix = true;
    tblPerformanceMatrix.setEnabled(true);
    jbtSavePerformance.setVisible(true);
    jbtEditPerformance.setVisible(false);
}//GEN-LAST:event_jbtEditPerformanceActionPerformed

private void jbtSavePerformanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtSavePerformanceActionPerformed
    editPerformanceMatrix = false;
    tblPerformanceMatrix.setEnabled(false);
    jbtEditPerformance.setVisible(true);
    jbtSavePerformance.setVisible(false);

    int i = 1;
    for (Enumeration ea = alternatives.elements(); ea.hasMoreElements();) {
        Alternative a = (Alternative) ea.nextElement();
        int j = 1;
        for (Enumeration ec = criteria.elements(); ec.hasMoreElements();) {
            Criterion c = (Criterion) ec.nextElement();
            String perfStr = (String) performances.getValueAt(i, j);
            try {
                double performance = Double.parseDouble(perfStr);
                a.setCriterionPerformance(c, performance);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error parsing performance data: " + perfStr, "Error", JOptionPane.ERROR_MESSAGE);
            }
            j++;
        }
        i++;
    }
}//GEN-LAST:event_jbtSavePerformanceActionPerformed

private void jbtComputeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtComputeActionPerformed
    controller.compute();
    cbxResultSelector.setSelectedIndex(0);
    setResultView((String) cbxResultSelector.getSelectedItem());
}//GEN-LAST:event_jbtComputeActionPerformed

private void jbtOpenProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtOpenProjectActionPerformed
    int returnVal = fileChooser.showOpenDialog(this);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
        Storage ds = new Storage();

        String fileName = fileChooser.getSelectedFile().getName();
        String directory = fileChooser.getCurrentDirectory().getAbsolutePath();
        String fullPath = directory + "\\" + fileName;
        File file = new File(fullPath);
        if (!file.exists() && !fileName.endsWith(".el3")) {
            file = new File(fullPath + ".el3");
            if (!file.exists()) {
                JOptionPane.showMessageDialog(this, "Cannot open the file, " +
                        "please check the filename.", "Open Project",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        StorageBox box = null;
        try {
            box = ds.load(new File(fullPath));
            resetState();
            controller.loadProject(box);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "The selected file appears " +
                    "to be corrupted.", "Open Project",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
}//GEN-LAST:event_jbtOpenProjectActionPerformed

private void jbtSaveProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtSaveProjectActionPerformed
    int returnVal = fileChooser.showSaveDialog(this);

    // Will be executed if the user clicks "Save"
    if (returnVal == JFileChooser.APPROVE_OPTION) {
        Storage ds = new Storage();

        // Get the file name and directory
        String fileName = fileChooser.getSelectedFile().getName();
        String directory = fileChooser.getCurrentDirectory().getAbsolutePath();

        StorageBox box = controller.getProjectData();

        // Need to put extension automatically - this is save method
        if (fileName.endsWith(".el3")) {
            ds.save(box, directory + "\\" + fileName);
        } else {
            ds.save(box, directory + "\\" + fileName + ".el3");
        }
    }
}//GEN-LAST:event_jbtSaveProjectActionPerformed

private void cbxResultSelectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxResultSelectorActionPerformed
    String resultView = (String) cbxResultSelector.getSelectedItem();
    setResultView(resultView);
}//GEN-LAST:event_cbxResultSelectorActionPerformed

private void cbxIndividualViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxIndividualViewActionPerformed
    Criterion c = (Criterion) cbxIndividualView.getSelectedItem();
    String resultView = (String) cbxResultSelector.getSelectedItem();
    MatrixModel cm = new MatrixModel();
    
    if (resultView.equals("Individual Concordance Matrix")) {
        cm = controller.getIndiConcordance(c);
    } else if (resultView.equals("Individual Discordance Matrix")) {
        cm = controller.getIndiDiscordance(c);
    }
    
    MatrixView cmp = new MatrixView(this);
    cmp.setModel(cm);
    pnlResultContainer.removeAll();
    pnlResultContainer.add(cmp, BorderLayout.CENTER);
    pnlResultContainer.updateUI();
}//GEN-LAST:event_cbxIndividualViewActionPerformed

   private E3Controller controller;
   private boolean editCriterion;
   private boolean editAlternative;
   private boolean editPerformanceMatrix;
   private Criterion activeCriterion;
   private Alternative activeAlternative;
   private DefaultListModel criteria;
   private DefaultListModel alternatives;
   private TwoHeadersTableModel performances;
   private JFileChooser fileChooser;
   private DefaultComboBoxModel individualMatrices;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cbxIndividualView;
    private javax.swing.JComboBox cbxResultSelector;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton jbtAdd;
    private javax.swing.JButton jbtAddAlternative;
    private javax.swing.JButton jbtCompute;
    private javax.swing.JButton jbtDelete;
    private javax.swing.JButton jbtDeleteAlternative;
    private javax.swing.JButton jbtEditPerformance;
    private javax.swing.JButton jbtOpenProject;
    private javax.swing.JButton jbtSavePerformance;
    private javax.swing.JButton jbtSaveProject;
    private javax.swing.JLabel jlbPerformanceMatrix;
    private javax.swing.JLabel jlbTitle;
    private javax.swing.JScrollPane jspPerformanceMatrixContainer;
    private javax.swing.JTabbedPane jtpMainContainer;
    private javax.swing.JList lstAlternatives;
    private javax.swing.JList lstCriteria;
    private javax.swing.JPanel pnlAlternatives;
    private javax.swing.JPanel pnlAlternativesContainer;
    private javax.swing.JPanel pnlContainer;
    private javax.swing.JPanel pnlCriteria;
    private javax.swing.JPanel pnlCriteriaContainer;
    private javax.swing.JPanel pnlMethod;
    private javax.swing.JPanel pnlResult;
    private javax.swing.JPanel pnlResultContainer;
    private javax.swing.JTable tblPerformanceMatrix;
    // End of variables declaration//GEN-END:variables

 
}
