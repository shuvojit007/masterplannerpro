package com.cruxbd.master_planner_pro.utils.CustomEditText;

public class CustomPart {
    private int start;
    private int end;

    public CustomPart(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public boolean isValid() {
        return start < end;
    }
}
