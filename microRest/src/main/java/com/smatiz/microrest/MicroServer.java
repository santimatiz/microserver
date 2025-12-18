/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smatiz.microrest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author smatiz
 */
public class MicroServer {
    private static final Logger logger = LoggerFactory.getLogger(MicroServer.class);
    final static String version = "0.5.0 build 20220214";
    
    // 0.3.2 Resuelve inconvenientes con SSL=false
    
    
   
   public MicroServer() {    
   }
   
   
    public static void main(String[] args) {  
        logger.info("MicroServer - REST API "+version);
        logger.info("Author : Santiago Matiz V.");
        logger.info("https://www.intelyclick.com/microserver");
        
        var url_db = "";
        if (args.length>0) {                    
            Server server = new Server(args[0]);
            server.run_server();
        } else {
            logger.error(" You must to specify the url DB connection to start the daemon");
            logger.error(" Please se the documentation of JDBC url for postgresql ");
            logger.error(" Example : jdbc:postgresql://localhost/MicroServer");
        }
        
        
    }
    
    
    
    
}
