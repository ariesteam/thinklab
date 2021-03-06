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

import java.awt.Color;
import java.text.NumberFormat;

import javax.swing.table.DefaultTableModel;

import org.integratedmodelling.mca.electre3.model.Criterion;

/**
 *
 * @author  Edwin Boaz Soenaryo
 */
public class CriterionPanel extends javax.swing.JPanel {

    /** Creates new form CriterionPanel */
    public CriterionPanel(E3View parent, Criterion c) {
        this.parent = parent;
        initComponents();
        jspTableContainer.getViewport().setBackground(Color.WHITE);
        
        setCriterionName(c.getCode());
        setDescription(c.getDescription());
        setWeightValue(c.getWeight());
        setIndifferenceValue(c.getIndifferenceAlpha(), c.getIndifferenceBeta());
        setPreferenceValue(c.getPreferenceAlpha(), c.getPreferenceBeta());
        setVetoValue(c.getVetoAlpha(), c.getVetoBeta());
        setVetoEnabledValue(c.isVetoEnabled());
        setDirectionValue(c.isAscendingPref());
        preview = new DefaultTableModel();
        tblPreview.setModel(preview);
    }
    
    public void setCriterionName(String name) {
        jlbCriterionName.setText(name);
    }
    
    public void setDescription(String description) {
        jlbDescription.setText(description);
    }
    
    public void setWeightValue(double weight) {
        NumberFormat formatter = ViewSettings.getDecimalFormat();
        jlbWeightValue.setText(formatter.format(weight));
    }
    
    public void setIndifferenceValue(double alpha, double beta) {
        String ind = getThresholdExpression(alpha, beta);
        jlbIndifferenceValue.setText(ind);
    }
    
    public void setPreferenceValue(double alpha, double beta) {
        String pre = getThresholdExpression(alpha, beta);
        jlbPreferenceValue.setText(pre);
    }
    
    public void setVetoValue(double alpha, double beta) {
        String vet = getThresholdExpression(alpha, beta);
        jlbVetoValue.setText(vet);
    }
    
    public void setVetoEnabledValue(boolean vetoEnabled) {
        jlbVetoEnabledValue.setText((vetoEnabled + "").toLowerCase());
    }
    
    public void setDirectionValue(boolean ascending) {
        if (ascending) {
            jlbDirectionValue.setText("Ascending");
        } else {
            jlbDirectionValue.setText("Descending");
        }
    }
    
    public void updatePreview(String[] header, String[][] data) {
        preview = new DefaultTableModel();
        for (int i = 0; i < header.length; i++) {
            preview.addColumn(header[i]);
        }
        for (int i = 0; i < data.length; i++) {
            preview.addRow(data[i]);
        }
        tblPreview.setModel(preview);
        tblPreview.updateUI();
    }
    
    private String getThresholdExpression(double alpha, double beta) {
        NumberFormat formatter = ViewSettings.getDecimalFormat();
        String al = formatter.format(alpha);
        String be = formatter.format(beta);
        if (alpha != 0) {
            if (beta < 0)
                return al + "g(j)" + be;
            else if (beta > 0)
                return al + "g(j)+" + be;
            else
                return al + "g(j)";
        } else 
            return be;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jlbCriterionName = new javax.swing.JLabel();
        jlbDescription = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jlbWeight = new javax.swing.JLabel();
        jlbWeightValue = new javax.swing.JLabel();
        jlbVeto = new javax.swing.JLabel();
        jlbVetoValue = new javax.swing.JLabel();
        jlbIndifference = new javax.swing.JLabel();
        jlbIndifferenceValue = new javax.swing.JLabel();
        jlbVetoEnabled = new javax.swing.JLabel();
        jlbVetoEnabledValue = new javax.swing.JLabel();
        jlbPreference = new javax.swing.JLabel();
        jlbPreferenceValue = new javax.swing.JLabel();
        jlbDirection = new javax.swing.JLabel();
        jlbDirectionValue = new javax.swing.JLabel();
        jspTableContainer = new javax.swing.JScrollPane();
        tblPreview = new javax.swing.JTable();
        jbtEdit = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));

        jlbCriterionName.setFont(new java.awt.Font("Tahoma", 1, 14));
        jlbCriterionName.setText("Criterion Name");

        jlbDescription.setText("Criterion extended description.");

        jPanel1.setBackground(new java.awt.Color(224, 236, 255));

        jlbWeight.setFont(new java.awt.Font("Tahoma", 1, 11));
        jlbWeight.setText("Criterion weight:");

        jlbWeightValue.setText("0.3");

        jlbVeto.setFont(new java.awt.Font("Tahoma", 1, 11));
        jlbVeto.setText("Veto threshold:");

        jlbVetoValue.setText("0.335g(j)-359131");

        jlbIndifference.setFont(new java.awt.Font("Tahoma", 1, 11));
        jlbIndifference.setText("Indifference threshold:");

        jlbIndifferenceValue.setText("0.471g(j)-256078");

        jlbVetoEnabled.setFont(new java.awt.Font("Tahoma", 1, 11));
        jlbVetoEnabled.setText("Veto enabled:");

        jlbVetoEnabledValue.setText("true");

        jlbPreference.setFont(new java.awt.Font("Tahoma", 1, 11));
        jlbPreference.setText("Preference threshold:");

        jlbPreferenceValue.setText("0.241g(j)-67992");

        jlbDirection.setFont(new java.awt.Font("Tahoma", 1, 11));
        jlbDirection.setText("Preference Direction:");

        jlbDirectionValue.setText("Ascending");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jlbPreference, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(145, 145, 145)
                                .addComponent(jlbPreferenceValue, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlbDirection)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlbWeight, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlbIndifference))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jlbWeightValue, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jlbVeto, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jlbIndifferenceValue, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jlbVetoEnabled, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jlbVetoValue, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                    .addComponent(jlbVetoEnabledValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlbDirectionValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlbWeight, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlbWeightValue, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlbVeto, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlbVetoValue, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlbIndifference, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlbIndifferenceValue, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlbVetoEnabled, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlbVetoEnabledValue, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlbPreference, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlbPreferenceValue, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlbDirection)
                    .addComponent(jlbDirectionValue))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jspTableContainer.setBackground(new java.awt.Color(195, 217, 255));
        jspTableContainer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(195, 217, 255), 2));
        jspTableContainer.setOpaque(false);

        tblPreview.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Alternative", "g(j)", "q(j)", "p(j)", "v(j)"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPreview.setGridColor(new java.awt.Color(195, 217, 255));
        tblPreview.setOpaque(false);
        jspTableContainer.setViewportView(tblPreview);

        jbtEdit.setText("Edit");
        jbtEdit.setOpaque(false);
        jbtEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtEditActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jspTableContainer, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 553, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jlbCriterionName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 398, Short.MAX_VALUE)
                        .addComponent(jbtEdit))
                    .addComponent(jlbDescription))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlbCriterionName)
                    .addComponent(jbtEdit))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlbDescription)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jspTableContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

private void jbtEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtEditActionPerformed
    parent.setEditCriterion(true);
}//GEN-LAST:event_jbtEditActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton jbtEdit;
    private javax.swing.JLabel jlbCriterionName;
    private javax.swing.JLabel jlbDescription;
    private javax.swing.JLabel jlbDirection;
    private javax.swing.JLabel jlbDirectionValue;
    private javax.swing.JLabel jlbIndifference;
    private javax.swing.JLabel jlbIndifferenceValue;
    private javax.swing.JLabel jlbPreference;
    private javax.swing.JLabel jlbPreferenceValue;
    private javax.swing.JLabel jlbVeto;
    private javax.swing.JLabel jlbVetoEnabled;
    private javax.swing.JLabel jlbVetoEnabledValue;
    private javax.swing.JLabel jlbVetoValue;
    private javax.swing.JLabel jlbWeight;
    private javax.swing.JLabel jlbWeightValue;
    private javax.swing.JScrollPane jspTableContainer;
    private javax.swing.JTable tblPreview;
    // End of variables declaration//GEN-END:variables

    private NumberFormat decimalFormat;
    private E3View parent;
    private DefaultTableModel preview;
}
