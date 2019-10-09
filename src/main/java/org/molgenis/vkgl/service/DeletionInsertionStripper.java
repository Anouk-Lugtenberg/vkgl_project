package org.molgenis.vkgl.service;

public class DeletionInsertionStripper {
    private String REF;
    private String ALT;
    private int start;
    private int stop;

    public DeletionInsertionStripper(String REF, String ALT, int start, int stop) {
        this.REF = REF;
        this.ALT = ALT;
        this.start = start;
        this.stop = stop;
    }

    public void strip() {
        stripRightSideDeletionInsertion();
        if (ALT.length() != 0 && REF.length() != 0) {
            stripLeftSideDeletionInsertion();
        }
    }

    public String getREF() {
        return this.REF;
    }

    public String getALT() {
        return this.ALT;
    }

    public int getStart() {
        return this.start;
    }

    public int getStop() {
        return this.stop;
    }

    private void stripRightSideDeletionInsertion() {
        while (REF.substring(REF.length() - 1).equals(ALT.substring(ALT.length() - 1))) {
            REF = REF.substring(0, REF.length() - 1);
            ALT = ALT.substring(0, ALT.length() - 1);
            stop = stop - 1;
            if (ALT.length() == 0 || REF.length() == 0) {
                break;
            }
        }
    }

    private void stripLeftSideDeletionInsertion() {
        while (REF.substring(0, 1).equals(ALT.substring(0, 1))) {
            REF = REF.substring(1);
            ALT = ALT.substring(1);
            start = start + 1;
            if (ALT.length() == 0 || REF.length() == 0) {
                break;
            }
        }
    }
}
