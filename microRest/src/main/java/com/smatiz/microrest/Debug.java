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
    
    
    
    private static boolean debug = false;
       
  static  void  out (String message) {
        if (debug) System.err.println(message);
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
    
}
