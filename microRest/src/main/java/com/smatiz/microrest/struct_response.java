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
public class struct_response {
    
    private String query = "";
    private String path="";
    private String require_token="0";
    /**
     * @return the query
     */
    public String getQuery() {
        return query;
    }

    /**
     * @param query the query to set
     */
    public void setQuery(String query) {
        this.query = query;
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

    /**
     * @return the require_token
     */
    public String getRequire_token() {
        return require_token;
    }

    /**
     * @param require_token the require_token to set
     */
    public void setRequire_token(String require_token) {
        this.require_token = require_token;
    }
  
    
}
