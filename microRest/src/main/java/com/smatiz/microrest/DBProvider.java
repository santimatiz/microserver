/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smatiz.microrest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author smatiz
 */
public class DBProvider {
    private Connection con = null;  
    private boolean error=false;
    private String connectionUrl = "";
    private boolean debug = false;
    


    
    public DBProvider(String url) {    
             connectionUrl = url;  
             connect();
    }
    
    /**
     * @return the con
     */
    public Connection getCon() {
        return con;
    }

    /**
     * @param con the con to set
     */
    public void setCon(Connection con) {
        this.con = con;
    }
    
    
    
    
    String excecuteQuery(String query)  {     
     String result = "";
        try (
                Statement st  = con.createStatement();
                ResultSet rs = st.executeQuery(query);
                ) {
             if (rs.next()) result= rs.getString(1);
    }   catch (SQLException ex) {
            Logger.getLogger(DBProvider.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("ERROR_EXE_QUERY : "+query);
        }
        return result;
    }
    
    
    String getQuery(struct_page page)  {
        String sql = "";
        int final_pos = page.getPath().substring(1, page.getPath().length()).indexOf("/")+2;
        var path =page.getPath().substring(0, final_pos);
        
        System.out.println("Path :"+path);
                         
        sql = "SELECT query FROM microrest.restapi WHERE path LIKE '"+path+"%' AND action='"+page.getAction()+"'";
      
        System.out.println("Sql :"+sql);
        String result = "";
        try (
                Statement st  = con.createStatement();
                ResultSet rs = st.executeQuery(sql);
                ) {
             if (rs.next()) result= rs.getString("query");

        } catch (SQLException ex) {        
            Logger.getLogger(DBProvider.class.getName()).log(Level.SEVERE, null, ex);            
            System.out.println("ERROR_GET_QUERY");
        }
        return result;
    }
    
    

      private void connect() {
          try {  
         // Establish the connection.  
         Class.forName("org.postgresql.Driver");  
            setCon(DriverManager.getConnection(connectionUrl));            
          } catch(ClassNotFoundException | SQLException e) {
             System.out.println(" Error "+e.getMessage());
             setError(true);        
             System.out.println("ERROR_CON_QUERY");
             
       
          }         
      }
 
      public void disconect() {
        try {
            con.close();
        } catch (SQLException ex) {                       
        }
      }

    /**
     * @return the error
     */
    public boolean isError() {
        return error;
    }

    /**
     * @param error the error to set
     */
    public void setError(boolean error) {
        this.error = error;
    }

  
}
