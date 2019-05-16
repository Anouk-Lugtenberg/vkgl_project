package org.molgenis.vkgl.model;

public class StartAndStopPosition {

    private final int start;
    private final int stop;

    public StartAndStopPosition(int start, int stop) {
        this.start = start;
        this.stop = stop;
    }

    public int getStart() {
        return start;
    }

    public int getStop() {
        return stop;
    }
}
