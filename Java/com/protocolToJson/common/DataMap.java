package com.protocolToJson.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DataMap extends HashMap<String, Object> {

    private static final long serialVersionUID = 1L;
    private boolean b_caseInsensitive = false;


    public DataMap() {
        super();
    }

    public DataMap(boolean b_nocase) {
        super();

        this.b_caseInsensitive = b_nocase;
    }

    public DataMap(DataMap dm) {
        super();

        if (dm == null || dm.size() == 0)
            return;

        String[] keys = dm.getKeys();
        for (String key : keys)
            this.put(key, dm.get(key));
    }

    public DataMap(DataMap dm, boolean b_nocase) {
        super();
        this.b_caseInsensitive = b_nocase;

        if (dm == null || dm.size() == 0)
            return;

        String[] keys = dm.getKeys();
        for (String key : keys)
            this.put(key, dm.get(key));
    }

    public DataMap(Map<String, Object> map) {
        super();

        if (map == null || map.size() == 0)
            return;

        String[] keys = getKeys(map);
        for (String key : keys)
            this.put(key, map.get(key));
    }

    public DataMap(Map<String, Object> map, boolean b_nocase) {
        super();
        this.b_caseInsensitive = b_nocase;

        if (map == null || map.size() == 0)
            return;

        String[] keys = getKeys(map);
        for (String key : keys)
            this.put(key, map.get(key));
    }


    public Object search(String path) {
        if (path == null)
            return null;

        String[] keys = UtilMgr.split(path, ".");
        if (keys == null || keys.length == 0)
            return null;

        DataMap dm = new DataMap(this);
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] == null)
                return null;
            Object obj = dm.get(keys[i]);
            if (obj == null)
                return null;
            if ("DataMap".equals(obj.getClass().getSimpleName()))
                dm = (DataMap) obj;
        }

        return dm;
    }


    @Override
    public Object put(String key, Object value) {
        return super.put(b_caseInsensitive ? key.toUpperCase() : key, value);
    }

    public Object get(String key) {
        return super.get(b_caseInsensitive ? key.toUpperCase() : key);
    }


    public Object get(String key, Object default_value) {
        Object value = get(key);
        if (value == null)
            value = default_value;
        return value;
    }


    public String getString(String key) {
        return "" + get(key);
    }

    public String getString(String key, String default_value) {
        return "" + get(key, default_value);
    }


    public int getInt(String key) {
        return getInt(key, 0);
    }

    public int getInt(String key, int default_value) {
        Object value = get(key);
        if (value == null)
            return default_value;

        if ("Integer".equals(value.getClass().getSimpleName()))
            return (int) value;

        return UtilMgr.to_int(value + "");
    }


    public long getLong(String key) {
        return getLong(key, 0);
    }

    public long getLong(String key, long default_value) {
        Object value = get(key);
        if (value == null)
            return default_value;

        if ("Long".equals(value.getClass().getSimpleName()))
            return (long) value;

        return UtilMgr.to_long(value + "");
    }


    public DataMapList getList(String key) {
        return (DataMapList) get(key);
    }


    public DataMap add(DataMap map) {
        String[] keys = map.getKeys();

        for (String key : keys)
            put(key, map.get(key));

        return this;
    }

    public DataMap add(DataMap map, String keys) {
        String[] list = UtilMgr.split(keys, ",");

        for (String key : list)
            put(key, map.get(key));

        return this;
    }


    public StringMap toStringMap() {
        StringMap map = new StringMap();

        String[] keys = this.getKeys();
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            Object value = this.get(key);
            String cls_name = value.getClass().getSimpleName();
            if ("DataMap".equals(cls_name)) {
                DataMap dm = (DataMap) value;
                StringMap submap = dm.toStringMap();
                String[] subkeys = submap.getKeys();
                for (int j = 0; j < subkeys.length; j++)
                    map.put(key + "." + subkeys[j], submap.get(subkeys[j]));
            } else if ("DataMapList".equals(cls_name)) {
                DataMapList dml = (DataMapList) value;
                for (int j = 0; j < dml.size(); j++) {
                    DataMap dm = dml.get(j);
                    StringMap submap = dm.toStringMap();
                    String[] subkeys = submap.getKeys();
                    for (int k = 0; k < subkeys.length; k++)
                        map.put(key + "." + subkeys[k] + "[" + j + "]", submap.get(subkeys[k]));
                }
            } else
                map.put(key, value + "");
        }

        return map;
    }

    public String[] getKeys() {
        return getKeys(this, false);
    }

    public String[] getKeys(boolean b_sort) {
        return getKeys(this, b_sort);
    }


    public String toString() {
        return toString(false);
    }

    public String toString(int depth) {
        return toString(false, depth);
    }

    public String toString(boolean b_sort) {
        return toString(b_sort, 0);
    }

    public String toString(boolean b_sort, int depth) {
        StringBuffer sb = new StringBuffer();
        String[] keys = getKeys(b_sort);
        String padding = UtilMgr.get_filler(depth * 4);

        int max_len = 0;
        for (String key : keys)
            if (key.length() > max_len)
                max_len = key.length();

        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            sb.append(i > 0 ? "\n" : "").append(padding).append(key).append(UtilMgr.get_filler(max_len - key.length())).append(" =");

            Object obj = get(key);
            if (obj == null)
                sb.append(" (null)");
            else if ("DataMap".equals(obj.getClass().getSimpleName()))
                sb.append("\n").append(((DataMap) obj).toString(b_sort, depth + 1)).append(padding);
            else if ("DataMapList".equals(obj.getClass().getSimpleName()))
                sb.append(" ").append(((DataMapList) obj).toString(b_sort, depth));
            else
                sb.append(" [").append(obj).append("]");
        }

        return sb.toString();
    }


    public static String[] getKeys(Map<String, Object> map) {
        return getKeys(map, false);
    }

    public static String[] getKeys(Map<String, Object> map, boolean b_sort) {
        Set<String> keySet = map.keySet();
        String[] keys = new String[keySet.size()];

        keySet.toArray(keys);
        if (b_sort)
            Arrays.sort(keys);

        return keys;
    }


    public static String toString(Map<String, Object> map) {
        return toString(new DataMap(map));
    }

    public static String toString(Map<String, Object> map, int depth) {
        return toString(new DataMap(map), depth);
    }

    public static String toString(Map<String, Object> map, boolean b_sort) {
        return toString(new DataMap(map), b_sort);
    }

    public static String toString(Map<String, Object> map, boolean b_sort, int depth) {
        return toString(new DataMap(map), b_sort, depth);
    }


}
