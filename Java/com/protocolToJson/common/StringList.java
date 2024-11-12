package com.protocolToJson.common;

import java.util.ArrayList;

public class StringList extends ArrayList<String> {

    private static final long serialVersionUID = 1L;

    public String[] toArray() {
        String[] array = new String[this.size()];
        toArray(array);

        return array;
    }

    public boolean add(int value) {
        return this.add(value + "");
    }

    public void add(int index, int value) {
        this.add(index, value + "");
    }

}
