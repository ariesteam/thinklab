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

import java.text.NumberFormat;

import org.integratedmodelling.mca.electre3.model.Alternative;

/**
 *
 * @author  Edwin Boaz Soenaryo
 */
public class AlternativePanel extends javax.swing.JPanel {

    /** Creates new form CriterionPanel */
    public AlternativePanel(E3View parent, Alternative a) {
        this.parent = parent;
        initComponents();
        
        setAlternativeName(a.getName());
        setDescription(a.getDescription());
    }
    
    public void setAlternativeName(String name) {
        jlbAlternativeName.setText(name);
    }
    
    public void setDescription(String description) {
        jlbDescription.setText(description);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jlbAlternativeName = new javax.swing.JLabel();
        jlbDescription = new javax.swing.JLabel();
        jbtEdit = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));

        jlbAlternativeName.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jlbAlternativeName.setText("Alternative Name");

        jlbDescription.setText("Alternative extended description.");

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
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jlbAlternativeName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 350, Short.MAX_VALUE)
                        .addComponent(jbtEdit))
                    .addComponent(jlbDescription))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlbAlternativeName)
                    .addComponent(jbtEdit))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlbDescription)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void jbtEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtEditActionPerformed
    parent.setEditAlternative(true);
}//GEN-LAST:event_jbtEditActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jbtEdit;
    private javax.swing.JLabel jlbAlternativeName;
    private javax.swing.JLabel jlbDescription;
    // End of variables declaration//GEN-END:variables

    private NumberFormat decimalFormat;
    private E3View parent;
}
