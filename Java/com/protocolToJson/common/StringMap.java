package com.protocolToJson.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;


public class StringMap extends HashMap<String, String> {

    private static final long serialVersionUID = 1L;

    public StringMap() {
    }

    public StringMap(StringMap map) {
        this.add(map);
    }


    public String get(String key, String default_value) {
        String value = get(key);

        if (value == null || value.length() == 0 || value.equals("null"))
            value = default_value;

        return value;
    }

    public String get(String key, int index) {
        return get(_merge(key, "[", index, "]"));
    }

    public String get(String group, String key, int index) {
        return get(_merge(group, ".", key, "[", index, "]"));
    }

    public String get(String key, int index, String default_value) {
        return get(_merge(key, "[", index, "]"), default_value);
    }

    public String get(String group, String key, int index, String default_value) {
        return get(_merge(group, ".", key, "[", index, "]"), default_value);
    }

    public String get(String key, int index, String subkey, int subindex) {
        return get(_merge(key, "[", index, "].", subkey, "[", subindex, "]"));
    }

    public String get(String group, String key, int index, String subkey, int subindex) {
        return get(_merge(group, ".", key, "[", index, "].", subkey, "[", subindex, "]"));
    }

    public String get(String key, int index, String subkey, int subindex, String default_value) {
        return get(_merge(key, "[", index, "].", subkey, "[", subindex, "]"), default_value);
    }

    public String get(String group, String key, int index, String subkey, int subindex, String default_value) {
        return get(_merge(group, ".", key, "[", index, "].", subkey, "[", subindex, "]"), default_value);
    }

    public String get(String key, int index, String subkey, String default_value) {
        return get(_merge(key, "[", index, "].", subkey), default_value);
    }

    public String get(String group, String key, int index, String subkey, String default_value) {
        return get(_merge(group, ".", key, "[", index, "].", subkey), default_value);
    }


    public int getInt(String key) {
        return UtilMgr.to_int(get(key));
    }

    public int getInt(String key, int default_value) {
        String value = get(key);

        if (value == null || value.length() == 0)
            return default_value;

        return UtilMgr.to_int(value);
    }

    public int getInt(String group, String key, int index) {
        return getInt(_merge(group, ".", key, "[", index, "]"));
    }

    public int getInt(String key, int index, int default_value) {
        return getInt(_merge(key, "[", index, "]"), default_value);
    }

    public int getInt(String group, String key, int index, int default_value) {
        return getInt(_merge(group, ".", key, "[", index, "]"), default_value);
    }

    public int getInt(String key, int index, String subkey, int subindex) {
        return getInt(_merge(key, "[", index, "].", subkey, "[", subindex, "]"));
    }

    public int getInt(String group, String key, int index, String subkey, int subindex) {
        return getInt(_merge(group, ".", key, "[", index, "].", subkey, "[", subindex, "]"));
    }

    public int getInt(String key, int index, String subkey, int subindex, int default_value) {
        return getInt(_merge(key, "[", index, "].", subkey, "[", subindex, "]"), default_value);
    }

    public int getInt(String group, String key, int index, String subkey, int subindex, int default_value) {
        return getInt(_merge(group, ".", key, "[", index, "].", subkey, "[", subindex, "]"), default_value);
    }


    public long getLong(String key) {
        return UtilMgr.to_long(get(key));
    }

    public long getLong(String key, long default_value) {
        String value = get(key);

        if (value == null || value.length() == 0)
            return default_value;

        return UtilMgr.to_long(value);
    }

    public long getLong(String key, int index) {
        return getLong(_merge(key, "[", index, "]"));
    }

    public long getLong(String group, String key, int index) {
        return getLong(_merge(group, ".", key, "[", index, "]"));
    }

    public long getLong(String key, int index, long default_value) {
        return getLong(_merge(key, "[", index, "]"), default_value);
    }

    public long getLong(String group, String key, int index, long default_value) {
        return getLong(_merge(group, ".", key, "[", index, "]"), default_value);
    }

    public long getLong(String key, int index, String subkey, int subindex) {
        return getLong(_merge(key, "[", index, "].", subkey, "[", subindex, "]"));
    }

    public long getLong(String group, String key, int index, String subkey, int subindex) {
        return getLong(_merge(group, ".", key, "[", index, "].", subkey, "[", subindex, "]"));
    }

    public long getLong(String key, int index, String subkey, int subindex, long default_value) {
        return getLong(_merge(key, "[", index, "].", subkey, "[", subindex, "]"), default_value);
    }

    public long getLong(String group, String key, int index, String subkey, int subindex, long default_value) {
        return getLong(_merge(group, ".", key, "[", index, "].", subkey, "[", subindex, "]"), default_value);
    }


    public float getFloat(String key) {
        return UtilMgr.to_float(get(key));
    }

    public float getFloat(String key, float default_value) {
        String value = get(key);

        if (value == null || value.length() == 0)
            return default_value;

        return UtilMgr.to_float(value);
    }

    public float getFloat(String key, int index) {
        return getFloat(_merge(key, "[", index, "]"));
    }

    public float getFloat(String group, String key, int index) {
        return getFloat(_merge(group, ".", key, "[", index, "]"));
    }

    public float getFloat(String key, int index, float default_value) {
        return getFloat(_merge(key, "[", index, "]"), default_value);
    }

    public float getFloat(String group, String key, int index, float default_value) {
        return getFloat(_merge(group, ".", key, "[", index, "]"), default_value);
    }

    public float getFloat(String key, int index, String subkey, int subindex) {
        return getFloat(_merge(key, "[", index, "].", subkey, "[", subindex, "]"));
    }

    public float getFloat(String group, String key, int index, String subkey, int subindex) {
        return getFloat(_merge(group, ".", key, "[", index, "].", subkey, "[", subindex, "]"));
    }

    public float getFloat(String key, int index, String subkey, int subindex, float default_value) {
        return getFloat(_merge(key, "[", index, "].", subkey, "[", subindex, "]"), default_value);
    }

    public float getFloat(String group, String key, int index, String subkey, int subindex, float default_value) {
        return getFloat(_merge(group, ".", key, "[", index, "].", subkey, "[", subindex, "]"), default_value);
    }


    public String[] getList(String key) {
        String list = get(key, "").replaceAll("\r", "").replaceAll("\n", "").replaceAll("\t", "").replaceAll(" ", "");

        return UtilMgr.split(list, ",");
    }

    public String[] getList(String key, String[] def) {
        String list = get(key);

        if (list == null || list.length() == 0)
            return def;

        list = list.replaceAll("\r", "").replaceAll("\n", "").replaceAll("\t", "").replaceAll(" ", "");

        return UtilMgr.split(list, ",");
    }

    public String[] getList(String key, int index) {
        String list = get(key, index, "").replaceAll("\r", "").replaceAll("\n", "").replaceAll("\t", "").replaceAll(" ", "");

        return UtilMgr.split(list, ",");
    }

    public String[] getList(String key, int index, String[] def) {
        String list = get(key, index);

        if (list == null || list.length() == 0)
            return def;

        list = list.replaceAll("\r", "").replaceAll("\n", "").replaceAll("\t", "").replaceAll(" ", "");

        return UtilMgr.split(list, ",");
    }

    public String[] getList(String group, String key, int index) {
        String list = get(group, key, index, "").replaceAll("\r", "").replaceAll("\n", "").replaceAll("\t", "").replaceAll(" ", "");

        return UtilMgr.split(list, ",");
    }

    public String[] getList(String group, String key, int index, String[] def) {
        String list = get(group, key, index);

        if (list == null || list.length() == 0)
            return def;

        list = list.replaceAll("\r", "").replaceAll("\n", "").replaceAll("\t", "").replaceAll(" ", "");

        return UtilMgr.split(list, ",");
    }


    public String put(String key, int index, String value) {
        return put(_merge(key, "[", index, "]"), value);
    }

    public String put(String group, String key, int index, String value) {
        return put(_merge(group, ".", key, "[", index, "]"), value);
    }

    public String put(String key, int index, String subkey, int subindex, String value) {
        return put(_merge(key, "[", index, "].", subkey, "[", subindex, "]"), value);
    }

    public String put(String group, String key, int index, String subkey, int subindex, String value) {
        return put(_merge(group, ".", key, "[", index, "].", subkey, "[", subindex, "]"), value);
    }


    public int put(String key, int value) {
        return UtilMgr.to_int(put(key, Integer.toString(value)));
    }

    public int put(String key, int index, int value) {
        return put(_merge(key, "[", index, "]"), value);
    }

    public int put(String group, String key, int index, String subkey, int subindex, int value) {
        return put(_merge(group, ".", key, "[", index, "].", subkey, "[", subindex, "]"), value);
    }


    public long put(String key, long value) {
        return UtilMgr.to_long(put(key, Long.toString(value)));
    }

    public long put(String key, int index, long value) {
        return put(_merge(key, "[", index, "]"), value);
    }

    public long put(String group, String key, int index, long value) {
        return put(_merge(group, ".", key, "[", index, "]"), value);
    }

    public long put(String group, String key, int index, String subkey, int subindex, long value) {
        return put(_merge(group, ".", key, "[", index, "].", subkey, "[", subindex, "]"), value);
    }


    public float put(String key, float value) {
        return UtilMgr.to_float(put(key, Float.toString(value)));
    }

    public float put(String key, int index, float value) {
        return put(_merge(key, "[", index, "]"), value);
    }

    public float put(String group, String key, int index, float value) {
        return put(_merge(group, ".", key, "[", index, "]"), value);
    }

    public float put(String group, String key, int index, String subkey, int subindex, float value) {
        return put(_merge(group, ".", key, "[", index, "].", subkey, "[", subindex, "]"), value);
    }


    public String[] getKeys() {
        return getKeys(false);
    }

    public String[] getKeys(boolean b_sort) {
        Set<String> keySet = this.keySet();
        String[] keys = new String[keySet.size()];

        keySet.toArray(keys);
        if (b_sort)
            Arrays.sort(keys);

        return keys;
    }


    public void add(StringMap map) {
        String[] keys = map.getKeys();

        for (String key : keys)
            put(key, map.get(key));
    }

    public void add(StringMap map, String keys) {
        String[] list = UtilMgr.split(keys, ",");

        for (String key : list)
            put(key, map.get(key));
    }

    public StringMap extract(String group) {
        if (group == null || group.length() == 0)
            return new StringMap(this);

        StringMap map = new StringMap();
        String[] keys = getKeys();
        group += ".";
        int len = group.length();

        for (String key : keys) {
            if (key.startsWith(group) && key.length() > len)
                map.put(key.substring(len), get(key));
        }

        return map;
    }


    public String toString() {
        return toString(false);
    }

    public String toString(boolean b_sort) {
        StringBuffer sb = new StringBuffer();
        String[] keys = getKeys();

        if (b_sort)
            Arrays.sort(keys);

        sb.append("================================== StringMap ==\n");

        int max_len = 0;
        for (String key : keys)
            if (key.length() > max_len)
                max_len = key.length();

        for (String key : keys)
            sb.append(key).append(UtilMgr.get_filler(max_len - key.length(), " ")).append(" = [").append(get(key)).append("]\n");

        return sb.toString();
    }


    public String toHTML() {
        return toHTML(false);
    }

    public String toHTML(boolean b_sort) {
        StringBuffer sb = new StringBuffer();
        String[] keys = getKeys();

        if (b_sort)
            Arrays.sort(keys);

        sb.append("================================== StringMap ==<br />\n");

        int max_len = 0;
        for (String key : keys)
            if (key.length() > max_len)
                max_len = key.length();

        for (String key : keys)
            sb.append(key).append(UtilMgr.get_filler(max_len - key.length(), "&nbsp;")).append(" = [").append(get(key)).append("]<br />\n");

        return sb.toString();
    }


    String _merge(Object... list) {
        StringBuffer sb = new StringBuffer();

        for (Object s : list)
            sb.append(s);

        return sb.toString();
    }
}
