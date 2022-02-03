/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smatiz.microrest;

import com.smatiz.microrest.Debug.Levels;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author smatiz
 */
public final class Config {

    Properties config = new Properties();
    InputStream configInput = null;
    
    private  static int port = 8084;
    private boolean ServerOn = true;
    private int time_out = 1000;
    private String connectionUrl = "";
    private String db_user="";
    private String db_password;
    private boolean ssl=false;
    private String TrustStore="/programas/certs/e-compra.co.p12";
    private String TrustStorePassword="Sonycdr74";
    private String KeyStorePassword="Sonycdr74";
    private String KeyStore="/programas/certs/e-compra.co.keystore";
    private String log = "";
    
    
    Config(String filename) {
        try {
            configInput = new FileInputStream(filename);
            config.load(configInput);
            setConnectionUrl(config.getProperty("con_url"));
            setDb_user(config.getProperty("db_user"));
            setDb_password(config.getProperty("db_password"));
            port = new Integer(config.getProperty("port"));
            time_out = new Integer(config.getProperty("timeout"));

            ssl = config.getProperty("ssl").trim().equals("true");
            if (config.getProperty("ssl")==null) Debug.out(" You must configure attribute <ssl> in config file (truu|false)", Levels.ERROR);
            if (ssl) {
                TrustStore = config.getProperty("TrustStore").trim();
                TrustStorePassword = config.getProperty("TrustStorePassword").trim();
                KeyStorePassword = config.getProperty("KeyStorePassword").trim();
                KeyStore = config.getProperty("KeyStore").trim();
            }
            if (config.getProperty("log") == null)
                Debug.out(" You must configure attribute <log> in config file (VERBOSE|INFO|ERROR)", Levels.ERROR);
            else {
                log = config.getProperty("log").trim();
                if (!log.equals("VERBOSE") || !log.equals("INFO") || !log.equals("ERROR")) {
                    Debug.out(" You must configure attribute <log> in config file (VERBOSE|INFO|ERROR)", Levels.ERROR);
                }
            }
             
           

        } catch (IOException e) {
            Debug.out("Error loading configuration data :" + e.getMessage(), Levels.ERROR);
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

    /**
     * @return the ssl
     */
    public boolean isSsl() {
        return ssl;
    }

    /**
     * @param ssl the ssl to set
     */
    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    /**
     * @return the TrustStore
     */
    public String getTrustStore() {
        return TrustStore;
    }

    /**
     * @param TrustStore the TrustStore to set
     */
    public void setTrustStore(String TrustStore) {
        this.TrustStore = TrustStore;
    }

    /**
     * @return the TrustStorePassword
     */
    public String getTrustStorePassword() {
        return TrustStorePassword;
    }

    /**
     * @param TrustStorePassword the TrustStorePassword to set
     */
    public void setTrustStorePassword(String TrustStorePassword) {
        this.TrustStorePassword = TrustStorePassword;
    }

    /**
     * @return the KeyStorePassword
     */
    public String getKeyStorePassword() {
        return KeyStorePassword;
    }

    /**
     * @param KeyStorePassword the KeyStorePassword to set
     */
    public void setKeyStorePassword(String KeyStorePassword) {
        this.KeyStorePassword = KeyStorePassword;
    }

    /**
     * @return the KeyStore
     */
    public String getKeyStore() {
        return KeyStore;
    }

    /**
     * @param KeyStore the KeyStore to set
     */
    public void setKeyStore(String KeyStore) {
        this.KeyStore = KeyStore;
    }

    /**
     * @return the log
     */
    public String getLog() {
        return log;
    }

    /**
     * @param log the log to set
     */
    public void setLog(String log) {
        this.log = log;
    }

}
