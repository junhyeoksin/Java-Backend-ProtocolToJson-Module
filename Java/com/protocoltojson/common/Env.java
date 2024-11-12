package com.protocoltojson.common;

import java.io.File;

import com.protocoltojson.crypto.SEED;


public class Env {

    public static boolean b_production = UtilMgr.getHostName().startsWith("test");

    // 환경변수
    protected static StringMap m_env = null;

    private static boolean env_loaded = false;

    protected static boolean debug = false;


    public Env() {
        init();
    }


    public synchronized static void init() {
        loadEnv();
    }

    public static void reset() {
        env_loaded = false;

        init();
    }

    public static void setDebug(boolean _debug) {
        debug = _debug;
    }


    // 공통 환경설정
    protected static void loadEnv() {

        if (env_loaded)
            return;

        m_env = new StringMap();

        String ProtocolToJsonConfig_home = System.getProperty("ProtocolToJsonConfig.home");

        String env_path = ProtocolToJsonConfig_home + "/Web/WEB-INF/config/" + System.getProperty("ProtocolToJsonConfig.config", "ProtocolToJsonConfig.xml");
        if (!new File(env_path).exists()) {
            env_path = ProtocolToJsonConfig_home + "/config/" + System.getProperty("ProtocolToJsonConfig.config", "ProtocolToJsonConfig.xml");
            if (!new File(env_path).exists())
                return;
        }

        m_env = UtilMgr.loadXML(env_path);

        String host_env_path = ProtocolToJsonConfig_home + "/Web/WEB-INF/config/ProtocolToJsonConfig_" + UtilMgr.getHostName() + ".xml";
        if (new File(host_env_path).exists()) {
            host_env_path = ProtocolToJsonConfig_home + "/config/ProtocolToJsonConfig_" + UtilMgr.getHostName() + ".xml";
            if (!new File(host_env_path).exists())
                return;
            m_env.add(UtilMgr.loadXML(host_env_path));
        }

        env_loaded = true;

        m_env.put("system.encoding", System.getProperty("file.encoding", "EUC-KR"));

        if (debug)
            LogMgr.debug(m_env.toString(true));
    }


    public static String getRaw(String name) {
        return getRaw(name, "");
    }

    public static String getRaw(String name, String default_value) {

        init();

        return m_env.get(name.toLowerCase(), default_value);
    }


    public static String get(String name) {
        return get(name, "");
    }

    public static String get(String name, String default_value) {

        init();

        if (m_env == null)
            return null;

        String value = m_env.get(name.toLowerCase(), default_value);

        if (value == null)
            return null;

        // 다른 환경변수 참조
        int start = value.indexOf("${");

        while (start != -1) {
            int end = value.indexOf("}", start + 2);

            if (end != -1) {
                String sub = value.substring(start + 2, end);

                if (sub.length() > 0) {
                    if (start == 0)
                        value = get(sub) + value.substring(end + 1);
                    else
                        value = value.substring(0, start) + get(sub) + value.substring(end + 1);
                }
            }

            start = value.indexOf("${");
        }


        start = value.indexOf("@{");

        while (start != -1) {
            int end = value.indexOf("}", start + 2);

            if (end != -1) {
                String sub = value.substring(start + 2, end);

                if (sub.length() > 0) {
                    if (start == 0)
                        value = System.getProperty(sub) + value.substring(end + 1);
                    else
                        value = value.substring(0, start) + System.getProperty(sub) + value.substring(end + 1);
                }
            }

            start = value.indexOf("@{");
        }


        start = value.indexOf("#{");

        while (start != -1) {
            int end = value.indexOf("}", start + 2);

            if (end != -1) {
                String sub = value.substring(start + 2, end);

                if (sub.length() > 0) {
                    if (start == 0)
                        value = SEED.Decrypt(sub, UtilMgr.getHostID()) + value.substring(end + 1);
                    else
                        value = value.substring(0, start) + SEED.Decrypt(sub, UtilMgr.getHostID()) + value.substring(end + 1);
                }
            }

            start = value.indexOf("#{");
        }


        return value;
    }

    public static int getInt(String name, int default_value) {

        String value = get(name, default_value + "");

        if (value == null || value.length() == 0)
            return 0;

        return UtilMgr.to_int(value);
    }

    public static int getInt(String name) {

        String value = get(name);

        if (value == null || value.length() == 0)
            return 0;

        return UtilMgr.to_int(value);
    }

    public static String[] getCodes(String name) {
        String codes = get(name);

        codes = UtilMgr.replace(codes, "\r", "");
        codes = UtilMgr.replace(codes, "\n", "");
        codes = UtilMgr.replace(codes, "\t", "");
        codes = UtilMgr.replace(codes, " ", "");

        return UtilMgr.split(codes, ",");
    }

    public static String[] getList(String name) {
        String list = get(name);

        list = UtilMgr.replace(list, "\r", "");
        list = UtilMgr.replace(list, "\n", "");
        list = UtilMgr.replace(list, "\t", "");
        list = UtilMgr.replace(list, " ", "");

        return UtilMgr.split(list, ",");
    }

    public static StringMap getMap() {
        init();

        return m_env;
    }


    public static String _get(String profile, String name, String default_value) {
        if (profile != null && profile.length() > 0)
            profile += ".";
        else
            profile = "";

        return get(profile + name, default_value);
    }

    public static String _get(String profile, String name) {
        return _get(profile, name, null);
    }

    public static int _getInt(String profile, String name, int default_value) {
        return UtilMgr.to_int(_get(profile, name, default_value + ""));
    }

    public static int _getInt(String profile, String name) {
        return _getInt(profile, name, 0);
    }

    public static String[] _getList(String profile, String name) {
        if (profile != null && profile.length() > 0)
            profile += ".";
        else
            profile = "";

        return getList(profile + name);
    }

}
