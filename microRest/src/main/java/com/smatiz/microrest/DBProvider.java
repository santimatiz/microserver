/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smatiz.microrest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;
import org.slf4j.LoggerFactory;

/**
 *
 * @author smatiz
 */
public class DBProvider {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DBProvider.class);
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
            sql = "SELECT value FROM microrest_config WHERE conf= ?";
        } else {
            sql = "SELECT value FROM microrest.config WHERE conf= ?";
        }
            
         
        List<Object> params = new ArrayList<>();
        params.add(q);
        return excecuteQuery(sql, params);
    }

/*        
    public struct_configuration read_configuration() {
        struct_configuration response = new struct_configuration();
        response.setTime_out(new Integer(getConfigQ("timeout")));
        response.setPort(new Integer(getConfigQ("port")));
        return response;
    }
*/
    
    
    
    
    String excecuteQuery(String query, List<Object> params)  {
        StringBuilder json = new StringBuilder();
        json.append("[");
        try (
                PreparedStatement st  = con.prepareStatement(query);
                ) {
            for (int i = 0; i < params.size(); i++) {
                st.setObject(i + 1, params.get(i));
            }
            ResultSet rs = st.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                json.append("{");
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getObject(i);
                    json.append("\"").append(columnName).append("\":\"").append(value).append("\"");
                    if (i < columnCount) {
                        json.append(",");
                    }
                }
                json.append("}");
                if (!rs.isLast()) {
                    json.append(",");
                }
            }
    }   catch (SQLException ex) {
            logger.error("ERROR_EXE_QUERY : : " + ex.getMessage(), ex);
            return null;
        }
        json.append("]");
        return json.toString();
    }

    public int executeUpdate(String query, List<Object> params) {
        int affectedRows = 0;
        try (
                PreparedStatement st = con.prepareStatement(query);
        ) {
            for (int i = 0; i < params.size(); i++) {
                st.setObject(i + 1, params.get(i));
            }
            affectedRows = st.executeUpdate();
        } catch (SQLException ex) {
            logger.error("ERROR_EXE_UPDATE : : " + ex.getMessage(), ex);
        }
        return affectedRows;
    }


    struct_response getQuery(struct_page page)  {
        struct_response response = new struct_response();
        if (page.getPath().length()==0) return response;
        String sql = "";
        
        int final_pos = page.getPath().substring(1, page.getPath().length()).indexOf("/")+2;
        var path =page.getPath().substring(0, final_pos);       
        
        logger.debug("Path :"+path);
        
        if (tipo_base_datos.compareTo(type_db.mysql) == 0 ) {
            sql = "SELECT query,path,required_token FROM microrest_restapi WHERE path = ? AND action= ?";
       } else {
           sql = "SELECT query,path,required_token FROM microrest.restapi WHERE path = ? AND action= ?";
       }
        logger.debug("Sql : " + sql);
        String result = "";
        try (
                PreparedStatement st  = con.prepareStatement(sql);
                ) {
            st.setString(1, page.getPath());
            st.setString(2, page.getAction());
            ResultSet rs = st.executeQuery();
             if (rs.next()) {
                 response.setQuery(rs.getString("query"));
                 response.setPath(rs.getString("path"));
                 if (rs.getString("required_token")!=null)
                    response.setRequire_token(rs.getString("required_token"));
                 
             }

        } catch (SQLException ex) {        
            logger.error("ERROR_GET_QUERY", ex);
        }
        return response;
    }
    

    public boolean search_token(String token) {
        String sql = "";
        boolean result = false;
        if (tipo_base_datos.compareTo(type_db.mysql) == 0) {
            sql = "SELECT microrest_check_token(?)";
        } else {
            sql = "SELECT microrest.check_token(?)";
        }
        try (PreparedStatement ps = con.prepareStatement(sql);) {
            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getString(1).contains("{\"MSG\":\"OK\"}"))
                   result = true;
                else
                    result = false;
            } else {
                result = false;
            }
            rs.close();
        } catch (SQLException ex) {
            logger.error("Error searching token", ex);
        }

        return result;
    }
    

    private void connect() {

        if (connectionUrl.contains("mysql")) {
            setTipo_base_datos(type_db.mysql);
            try {
                // Establish the connection.  
                Class.forName("com.mysql.cj.jdbc.Driver");                
                setCon(DriverManager.getConnection(connectionUrl,config.getDb_user(),config.getDb_password()));
            } catch (ClassNotFoundException | SQLException e) {
                logger.error("Error connecting to MySQL", e);
                setError(true);
            }
        }

        if (connectionUrl.contains("postgresql")) {
            setTipo_base_datos(type_db.postgres);
            try {
                // Establish the connection.  
                Class.forName("org.postgresql.Driver");
                setCon(DriverManager.getConnection(connectionUrl,config.getDb_user(),config.getDb_password()));
            } catch (ClassNotFoundException | SQLException e) {
                logger.error("Error connecting to PostgreSQL", e);
                setError(true);
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
