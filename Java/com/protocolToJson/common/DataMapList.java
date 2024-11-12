package com.protocolToJson.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataMapList extends ArrayList<DataMap> {

    private static final long serialVersionUID = 1L;


    public DataMapList() {
        super();
    }

    public DataMapList(List<Map<String, Object>> list) {
        super();

        if (list == null)
            return;

        for (Map<String, Object> map : list)
            this.add(new DataMap(map));
    }


    public String toString() {
        return toString(false, 0);
    }

    public String toString(int depth) {
        return toString(false, depth);
    }

    public String toString(boolean b_sort) {
        return toString(b_sort, 0);
    }

    public String toString(boolean b_sort, int depth) {
        StringBuffer sb = new StringBuffer();
        String padding = UtilMgr.get_filler(depth * 4);

        sb.append("(").append(this.size()).append(")");

        if (this.size() == 0)
            return sb.toString();

        for (int i = 0; i < this.size(); i++) {
            DataMap dm = this.get(i);
            sb.append("\n").append(padding).append(i + 1).append(":\n").append(dm.toString(b_sort, depth + 1));
        }

        return sb.toString();
    }

}
