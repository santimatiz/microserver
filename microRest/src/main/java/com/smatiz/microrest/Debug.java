/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smatiz.microrest;

/**
 *
 * @author smatiz
 */
public class Debug {
    
    enum Levels {
        VERBOSE,
        INFO,        
        ERROR,
    }
    
    
    private static boolean debug = false;
    private static Levels config_level;
       
    
    public Debug(String config) {
        switch (config) {
            case "VERBOSE" : config_level = Levels.VERBOSE; break;
            case "INFO" : config_level = Levels.INFO; break;
            case "ERROR" : config_level = Levels.ERROR; break; 
        }   
    }
    
  static  void  out (String message, Levels level) {
        if (level.equals(level.ERROR)) System.err.println(message); // Siempre lo muestra
        else
        if (config_level.equals(level.VERBOSE)) {
            System.out.println("V:"+message);
        } else
            if (level.equals(Levels.INFO)) {
                System.out.println("I:"+message);
            }
        
    }

    /**
     * @return the debug
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * @param debug the debug to set
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * @return the config_level
     */
    public Levels getConfig_level() {
        return config_level;
    }

    /**
     * @param config_level the config_level to set
     */
    public void setConfig_level(Levels config_level) {
        this.config_level = config_level;
    }
    
}
