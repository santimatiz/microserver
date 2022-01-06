/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smatiz.microrest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author smatiz
 */
public class Config {

    Properties config = new Properties();
    InputStream configInput = null;
    
    private  static int port = 8084;
    private boolean ServerOn = true;
    private int time_out = 1000;
    private String connectionUrl = "";
    private String db_user="";
    private String db_password;

    Config(String filename) {
        try {
            configInput = new FileInputStream(filename);
            config.load(configInput);
            setConnectionUrl(config.getProperty("con_url"));
            setDb_user(config.getProperty("db_user"));
            setDb_password(config.getProperty("db_password")); 
            port = new Integer(config.getProperty("port"));
            time_out = new Integer(config.getProperty("timeout"));
            
            
            
        } catch (IOException e) {
            new Debug().out("Error loading configuration data :"+e.getMessage());
            
        }
    }

    /**
     * @return the connectionUrl
     */
    public String getConnectionUrl() {
        return connectionUrl;
    }

    /**
     * @param connectionUrl the connectionUrl to set
     */
    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    /**
     * @return the db_user
     */
    public String getDb_user() {
        return db_user;
    }

    /**
     * @param db_user the db_user to set
     */
    public void setDb_user(String db_user) {
        this.db_user = db_user;
    }

    /**
     * @return the db_password
     */
    public String getDb_password() {
        return db_password;
    }

    /**
     * @param db_password the db_password to set
     */
    public void setDb_password(String db_password) {
        this.db_password = db_password;
    }

    /**
     * @return the port
     */
    public static int getPort() {
        return port;
    }

    /**
     * @param aPort the port to set
     */
    public static void setPort(int aPort) {
        port = aPort;
    }

    /**
     * @return the ServerOn
     */
    public boolean isServerOn() {
        return ServerOn;
    }

    /**
     * @param ServerOn the ServerOn to set
     */
    public void setServerOn(boolean ServerOn) {
        this.ServerOn = ServerOn;
    }

    /**
     * @return the time_out
     */
    public int getTime_out() {
        return time_out;
    }

    /**
     * @param time_out the time_out to set
     */
    public void setTime_out(int time_out) {
        this.time_out = time_out;
    }

}
