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
public class MicroServer {
    final static String version = "0.3.1 build 20220128";
    
   
   public MicroServer() {    
   }
   
   
    public static void main(String[] args) {  
        System.out.println("MicroServer - REST API "+version);
        System.out.println("Author : Santiago Matiz V.");
        System.out.println("https://www.intelyclick.com/microserver");
        
        var url_db = "";
        if (args.length>0) {                    
            Server server = new Server(args[0]);
            server.run_server();
        } else {
            System.out.println(" You must to specify the url DB connection to start the daemon");
            System.out.println(" Please se the documentation of JDBC url for postgresql ");
            System.out.println(" Example : jdbc:postgresql://localhost/MicroServer");
        }
        
        
    }
    
    
    
    
}
