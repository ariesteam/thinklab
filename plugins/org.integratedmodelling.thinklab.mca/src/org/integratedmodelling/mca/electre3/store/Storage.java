/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.integratedmodelling.mca.electre3.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author Edwin Boaz Soenaryo
 */
public class Storage {

    public Storage() {
    }

    public void save(StorageBox storageBox, String fileName) {
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(storageBox);
            oos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public StorageBox load(File f) {
        try {
            if (f.exists()) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
                Object obj = ois.readObject();
                ois.close();
                return (StorageBox) obj;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
