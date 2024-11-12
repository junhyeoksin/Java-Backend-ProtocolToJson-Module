package com.protocoltojson.common;

import org.slf4j.LoggerFactory;


public class LogMgr {

    public static void trace(String msg) {
        LoggerFactory.getLogger(new Throwable().getStackTrace()[1].getClassName()).trace(msg);
    }

    public static void trace(String msg, Object... args) {
        LoggerFactory.getLogger(new Throwable().getStackTrace()[1].getClassName()).trace(msg, args);
    }

    public static void trace(String msg, Throwable t) {
        LoggerFactory.getLogger(new Throwable().getStackTrace()[1].getClassName()).trace(msg, t);
    }


    public static void debug(String msg) {
        LoggerFactory.getLogger(new Throwable().getStackTrace()[1].getClassName()).debug(msg);
    }

    public static void debug(String msg, Object... args) {
        LoggerFactory.getLogger(new Throwable().getStackTrace()[1].getClassName()).debug(msg, args);
    }

    public static void debug(String msg, Throwable t) {
        LoggerFactory.getLogger(new Throwable().getStackTrace()[1].getClassName()).debug(msg, t);
    }


    public static void info(String msg) {
        LoggerFactory.getLogger(new Throwable().getStackTrace()[1].getClassName()).info(msg);
    }

    public static void info(String msg, Object... args) {
        LoggerFactory.getLogger(new Throwable().getStackTrace()[1].getClassName()).info(msg, args);
    }

    public static void info(String msg, Throwable t) {
        LoggerFactory.getLogger(new Throwable().getStackTrace()[1].getClassName()).info(msg, t);
    }


    public static void warn(String msg) {
        LoggerFactory.getLogger(new Throwable().getStackTrace()[1].getClassName()).warn(msg);
    }

    public static void warn(String msg, Object... args) {
        LoggerFactory.getLogger(new Throwable().getStackTrace()[1].getClassName()).warn(msg, args);
    }

    public static void warn(String msg, Throwable t) {
        LoggerFactory.getLogger(new Throwable().getStackTrace()[1].getClassName()).warn(msg, t);
    }


    public static void error(String msg) {
        LoggerFactory.getLogger(new Throwable().getStackTrace()[1].getClassName()).error(msg);
    }

    public static void error(String msg, Object... args) {
        LoggerFactory.getLogger(new Throwable().getStackTrace()[1].getClassName()).error(msg, args);
    }

    public static void error(String msg, Throwable t) {
        LoggerFactory.getLogger(new Throwable().getStackTrace()[1].getClassName()).error(msg, t);
    }

    public static void error(Throwable t) {
        LoggerFactory.getLogger(new Throwable().getStackTrace()[1].getClassName()).debug("", t);
    }

}
