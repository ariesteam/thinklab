package org.integratedmodelling.mca.electre3.model;

/**
 *
 * @author Edwin Boaz Soenaryo
 */
public class AAPair implements Comparable {

    public AAPair(Alternative alt1, Alternative alt2) {
        this.alt1 = alt1;
        this.alt2 = alt2;
    }

    public int compareTo(Object o) {
        if (o instanceof AAPair) {
            AAPair aao = (AAPair) o;
            if (aao.getAlt1() == alt1 && aao.getAlt2() == alt2)
                return 0;
        }
        return -1;
    }

    public Alternative getAlt1() {
        return alt1;
    }

    public void setAlt1(Alternative alt1) {
        this.alt1 = alt1;
    }

    public Alternative getAlt2() {
        return alt2;
    }

    public void setAlt2(Alternative alt2) {
        this.alt2 = alt2;
    }
    
    @Override
    public boolean equals(Object anObject) {
        if (this.compareTo(anObject) == 0) return true;
        else return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.alt1 != null ? this.alt1.hashCode() : 0);
        hash = 97 * hash + (this.alt2 != null ? this.alt2.hashCode() : 0);
        return hash;
    }
    
    private Alternative alt1;
    private Alternative alt2;

}
