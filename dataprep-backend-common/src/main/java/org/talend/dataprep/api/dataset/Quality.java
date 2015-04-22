package org.talend.dataprep.api.dataset;

public class Quality {

    private int empty;

    private int invalid;

    private int valid;

    public int getEmpty() {
        return empty;
    }

    public void setEmpty(int empty) {
        this.empty = empty;
    }

    public int getInvalid() {
        return invalid;
    }

    public void setInvalid(int invalid) {
        this.invalid = invalid;
    }

    public int getValid() {
        return valid;
    }

    public void setValid(int valid) {
        this.valid = valid;
    }

    @Override
    public String toString() {
        return "Quality{" + "empty=" + empty + ", invalid=" + invalid + ", valid=" + valid + '}';
    }
}
