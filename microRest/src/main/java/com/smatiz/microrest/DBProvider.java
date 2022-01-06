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
    Config config;
    
    enum type_db {
        mysql, postgres
    }
    
    private type_db tipo_base_datos;
    


    
    public DBProvider(Config config) {    
            this.config = config;
             connectionUrl = config.getConnectionUrl();  
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
    
    String getConfigQ(String q) {
        var sql = "";

    
        
        
        if (tipo_base_datos.compareTo(type_db.mysql) == 0 ) {
            sql = "SELECT value FROM microrest_config WHERE conf='" + q + "'";
        } else {
            sql = "SELECT value FROM microrest.config WHERE conf='" + q + "'";
        }
            
         
        return excecuteQuery(sql);
    }

/*        
    public struct_configuration read_configuration() {
        struct_configuration response = new struct_configuration();
        response.setTime_out(new Integer(getConfigQ("timeout")));
        response.setPort(new Integer(getConfigQ("port")));
        return response;
    }
*/
    
    
    
    
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
    
    
    struct_response getQuery(struct_page page)  {
        
        
        
        struct_response response = new struct_response();
        if (page.getPath().length()==0) return response;
        String sql = "";
        
        int final_pos = page.getPath().substring(1, page.getPath().length()).indexOf("/")+2;
        var path =page.getPath().substring(0, final_pos);        
        System.out.println("Path :"+path);
        
        if (tipo_base_datos.compareTo(type_db.mysql) == 0 ) {
            sql = "SELECT query,path FROM microrest_restapi WHERE path = '"+page.getPath()+"' AND action='"+page.getAction()+"'";
       } else {
           sql = "SELECT query,path FROM microrest.restapi WHERE path = '"+page.getPath()+"' AND action='"+page.getAction()+"'";
       }
        System.out.println("Sql :"+sql);
        String result = "";
        try (
                Statement st  = con.createStatement();
                ResultSet rs = st.executeQuery(sql);
                ) {
             if (rs.next()) {
                 response.setQuery(rs.getString("query"));
                 response.setPath(rs.getString("path"));
             }

        } catch (SQLException ex) {        
            Logger.getLogger(DBProvider.class.getName()).log(Level.SEVERE, null, ex);            
            System.out.println("ERROR_GET_QUERY");
        }
        return response;
    }
    
    

    private void connect() {

        if (connectionUrl.contains("mysql")) {
            setTipo_base_datos(type_db.mysql);
            try {
                // Establish the connection.  
                Class.forName("com.mysql.cj.jdbc.Driver");                
                setCon(DriverManager.getConnection(connectionUrl,config.getDb_user(),config.getDb_password()));
            } catch (ClassNotFoundException | SQLException e) {
                System.out.println(" Error " + e.getMessage());
                setError(true);
                System.out.println("ERROR_CON_MYSQL");

            }
        }

        if (connectionUrl.contains("postgresql")) {
            setTipo_base_datos(type_db.postgres);
            try {
                // Establish the connection.  
                Class.forName("org.postgresql.Driver");
                setCon(DriverManager.getConnection(connectionUrl,config.getDb_user(),config.getDb_password()));
            } catch (ClassNotFoundException | SQLException e) {
                System.out.println(" Error " + e.getMessage());
                setError(true);
                System.out.println("ERROR_CON_PG");

            }
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

    /**
     * @return the tipo_base_datos
     */
    public type_db getTipo_base_datos() {
        return tipo_base_datos;
    }

    /**
     * @param tipo_base_datos the tipo_base_datos to set
     */
    public void setTipo_base_datos(type_db tipo_base_datos) {
        this.tipo_base_datos = tipo_base_datos;
    }

  
}
