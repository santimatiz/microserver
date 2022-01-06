/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smatiz.microrest;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author smatiz
 */
public final class struct_page {
    private String action = ""; //GET PUT ...
    private Map params = new HashMap(); // params
    private Map headers = new HashMap(); // params
    private String body="";
    private String path="";
    private int step_parse=0;
    
    
    
    
    public void parse(String info) {        
        struct_page page = new struct_page();
        
        if (info==null) return ;
        
        if (step_parse==4) {            
            body += info;            
        }
        
        
        // Begin of body information
        if (info.contains("Content-Length")) step_parse=4;
         
        // end of head information        
        if (info.contains("Content-Type")) step_parse=3; // Dont have head information
        
        
          //// Check head information
        if (step_parse==1) {
          
            var header = info.split(":");
            if (header.length>1)
                headers.put(header[0], header[1]);
        }
        
        if (info.length()>2) {
            if (info.trim().substring(0,3).equals("GET"))     {  action="GET"; parsePath(3, info);}
            if (info.contains("OPTIONS")) {  action="OPTIONS"; parsePath(7, info);}
            if (info.contains("POST"))    {   action="POST"; parsePath(4, info);}
            if (info.contains("PUT"))     {   action="PUT"; parsePath(3, info);}     
        }            
    }
    
    
    
    
    @Override
    public String toString() {
        String result = "";
        result = " Action :  "+this.action + "\n";
        result += " Path : "+this.getPath() + "\n";
        result += " Params : "+this.params + "\n";
        result += " Header   : "+this.headers + "\n";
        result += " Body   : "+this.body + "\n";        
        return result;
    }
    
    
    
    /**
     * 
     * @param size
     * @param info
     * @return find the path
     */
    private void parsePath(int size, String info) {
        step_parse=1;
        
        String parts[] = info.split(" ");
        int findQ = parts[1].indexOf("?");        
        if (findQ>0)
            setPath(parts[1].substring(0, findQ));        
        else
            setPath(parts[1]);
        
      String params[] = parts[1].substring(findQ+1,parts[1].length()).split("&");
      if (params.length>0) { // Son varios parametros
          for (int i=0;i<params.length;i++) {
              if (params[i].contains("=")) {
                String p[] = params[i].split("=");              
                this.params.put(p[0],p[1]);
              }
          }                   
      }
      
      
      
        
    }
    
    
    
    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * @return the params
     */
    public Map getParams() {
        return params;
    }

    /**
     * @param params the params to set
     */
    public void setParams(Map params) {
        this.params = params;
    }

    /**
     * @return the headers
     */
    public Map getHeaders() {
        return headers;
    }

    /**
     * @param headers the headers to set
     */
    public void setHeaders(Map headers) {
        this.headers = headers;
    }

    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * @param body the body to set
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }
    
    
}
