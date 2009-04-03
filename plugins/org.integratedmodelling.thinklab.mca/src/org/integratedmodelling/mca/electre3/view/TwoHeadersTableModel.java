package org.integratedmodelling.mca.electre3.view;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Edwin Boaz Soenaryo
 */
public class TwoHeadersTableModel extends DefaultTableModel {

    @Override
    public boolean isCellEditable(int row, int col) {
        if (row == 0 | col == 0) return false;
        else return true;
    }
    
}
