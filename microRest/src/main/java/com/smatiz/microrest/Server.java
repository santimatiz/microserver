/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smatiz.microrest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author smatiz
 * https://www.ietf.org/rfc/rfc2616.txt
 * https://developer.mozilla.org/es/docs/Web/HTTP/Overview

*/
public class Server {

    private ServerSocket serverSocket;    
    private static final boolean ServerOn= true;
    List<ServiceThread> lhilos = new ArrayList();
    Config config;

    public Server(String path) {
        
        config = new Config(path);
        DBProvider db = new DBProvider(config);
        if (db.isError()) {
            System.out.println("There are an error with DB configuration ");
        }
        db.disconect();

        try {
            //https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/net/ServerSocket.html
            //new ServerSocket(9090, 0, InetAddress.getLoopbackAddress());
            serverSocket = new ServerSocket(config.getPort(),3);
            System.out.println("Listening port " + config.getPort());
        } catch (IOException ex) {
            Logger.getLogger(MicroServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    

    
    public void run_server() {
        while (ServerOn) {
            try {
                Socket clientSocket = serverSocket.accept();
                ServiceThread cliThread = new ServiceThread(config.getConnectionUrl(), clientSocket);

                lhilos.add(cliThread);
                cliThread.start();
                for (int i = 0; i < lhilos.size(); i++) {
                    var h = lhilos.get(i);
                    if (h.m_bRunThread == false) {
                        h.stop();
                        lhilos.remove(h);
                    }
                    long stop_time = System.currentTimeMillis();
                    if ((stop_time - h.start_time) > config.getTime_out()) {
                        h.stop();
                        lhilos.remove(h);
                    }

                }
                System.out.println("Thread  :" + lhilos.size());

            } catch (IOException ioe) {
                System.out.println("Exception found on accept. Ignoring. Stack Trace :");
            } 
        }

    }

    class ServiceThread extends Thread {

        private struct_page page = new struct_page();
        long start_time = System.currentTimeMillis();
        Socket myClientSocket;
        boolean m_bRunThread = true;
        private String url_db = "";

        /*
        public ServiceThread(String url) {
            super();
            this.url_db = url;

        }
        */

        ServiceThread(String url, Socket s) {
            super();
            myClientSocket = s;
            this.url_db = url;
        }

        public void run() {
            BufferedReader in = null;
            PrintWriter out = null;
            System.out.println(
                    "Accepted Client Address - " + myClientSocket.getInetAddress().getHostName());
            try {
                 myClientSocket.setSoTimeout(MAX_PRIORITY);

                in = new BufferedReader(
                        new InputStreamReader(myClientSocket.getInputStream()));

                out = new PrintWriter(
                        new OutputStreamWriter(myClientSocket.getOutputStream()));

                while (m_bRunThread) {
                    //System.out.println("While running...");
                    long time_elapsed = System.currentTimeMillis();
                    if ((time_elapsed - start_time) > config.getTime_out()) {
                        System.out.println("Process time limit , stopping...");
                        m_bRunThread = false;
                        this.stop();

                    }

                    String clientCommand = "";
                    try {                         
                        clientCommand = in.readLine();
                        

                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                        System.out.println("Time Out, Don't detect \\n ");
                        m_bRunThread = false;

                    }

                    if (clientCommand != null) {
                        
                        if (clientCommand.equals("")) { 
                            
                            if (page.getAction().equals("OPTIONS")) {
                                ManageOptions(out);
                            } else {
                                System.out.println("End of client request");
                                m_bRunThread = false;
                            }
                        }
                       
                        
                        getPage().parse(clientCommand);
                        System.out.println("Client Says :" + clientCommand);
                       
                    } // if command!=null
                } // While

                
                /***
                 * Process information of request
                 */
                
                try {
                    String code = "OK 200"; // Normal situation
                   
                    
                    String return_info = ProcessPageInformation();
                   

                                      
                    switch (return_info) {
                        case  "404" : {
                            out.println(response("400 Bad Request", "Resorce not found!")); // Alwais is OK Send response to client
                            System.out.println("404");
                            break;
                        } 
                         case  "500" : {
                            out.println(response("500 Internal Server Error", "Internal Server Error")); // Alwais is OK Send response to client
                            System.out.println("500");
                            break;
                        } 
                        default : {
                            out.println(response("200 OK", return_info));                            
                            System.out.println("200");
                            break; // Alwais is OK Send response to client
                        }
                    }
                    
                    out.flush();

                } catch (SQLException ex) {
                    System.out.println("Error in process query information ");
                    Logger.getLogger(ServiceThread.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (IOException ex) {
                Logger.getLogger(MicroServer.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    // out.println(response()); // Send response to client
                    // out.flush();
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                    myClientSocket.close();
                    System.out.println("end communication");
                    System.out.println(getPage());
                } catch (IOException ex) {
                    Logger.getLogger(MicroServer.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Error in process query information ");
                }
            }
        }

        
        /**
         * This is the OPTIONS Stage
         */
        private void ManageOptions(PrintWriter out) {
            String return_info = "HTTP/1.1 200 OK\n" +
                   // "Date: Mon, 01 Dec 2008 01:15:39 GMT\n" +
                    "Server: MicroServer\r\n" +
                    "Access-Control-Allow-Origin: http://localhost:54035\r\n" +
                    "Access-Control-Allow-Methods: POST, GET, OPTIONS\r\n" +
                    //"Access-Control-Allow-Headers: X-PINGOTHER\n" +
                    "Access-Control-Max-Age: 1728000\r\n" +
                    "Vary: Accept-Encoding, Origin\r\n" +
                    "Content-Encoding: gzip\r\n" +
                    "Content-Length: 0\r\n" +
                    "Keep-Alive: timeout=2, max=100\r\n" +
                    "Connection: Keep-Alive\r\n" +
                    "Content-Type: text/plain\r\n";           
            out.println(response("200 OK", return_info));  
            out.flush();
            System.out.println("ATENTION !!!  : In this moment we are not compatible with OPTIONS  ");
            System.out.println("PLease remove <\"Access-Control_Allow_Origin\": > of client Header ");
            
        }
        
        
        /**
         * 
         * @return
         * @throws SQLException 
         * Process Page informacion with Postgresql Database
         */
        private String ProcessPageInformation() throws SQLException {
            // Saca el action y el path de la base de datos para obtener el query
            DBProvider con_provider = new DBProvider(config);
            
            
            //TODO Debe reemplazar las variables con los valores
            struct_response response = con_provider.getQuery(page);
            
            
            //TODO : debe tomar el path configurado y sacar las variables del path que llego...
            // PENDIENTE
            
            
           
           // Replace variables Headers and Body with values information
           Set<Map.Entry<String,String>> lparams = page.getParams().entrySet();
           for (Map.Entry<String, String> p : lparams) {
               response.setQuery(response.getQuery().replaceAll(p.getKey(), p.getValue()));
           }
         
           // Headers
           Set<Map.Entry<String,String>> lheaders = page.getHeaders().entrySet();
           for (Map.Entry<String, String> h : lheaders) {
               response.setQuery(response.getQuery().replaceAll(h.getKey(), h.getValue()));
           }
         
           response.setQuery(response.getQuery().replaceAll("body", "'" + page.getBody() +"'")); // Body           
           response.setQuery(response.getQuery().replaceAll("%27", "'")); // Pone las comillas simples
           
           new Debug().out("Query : " + response.getQuery());
           System.out.println("Query : " + response.getQuery());
            
            if (response.getQuery().equals("")) { return "404";}
            String result = con_provider.excecuteQuery(response.getQuery());
            
            if (result.equals("")) { return "500";}
            con_provider.disconect();
            
            return result;
        }

        /**
         *
         * @param code : 200 OK
         * @param msq
         * @return return "HTTP/1.1 200 OK\r\n" + "Server: MicroServer\r\n";
         * HttpHeaders headers = new HttpHeaders();
    headers.add("Access-Control-Allow-Origin", "*");
    headers.add("Access-Control-Allow-Methods", "GET, OPTIONS, POST");
    headers.add("Access-Control-Allow-Headers", "Content-Type");
return new ResponseEntity(list.toString(), headers, HttpStatus.OK);
         */
        public String response(String code, String msg) {
            String mensaje = msg.trim().replaceAll("\n", "").replaceAll("\r", "");
            return "HTTP/1.1 " + code + "\r\n"
                    + "Server: MicroServer\r\n"
                  //  +"ETag: \"51142bc1-7449-479b075b2891b\"\r\n" 
                  //  +"Accept-Ranges: bytes\n" 
                    +"Content-Length: " + mensaje.length() + "\r\n"
                    + "Access-Control-Allow-Origin : * \r\n"
                    + "Access-Control-Allow-Methods : GET, POST, PUT \r\n"
                    + "Access-Control-Allow-Headers: Content-Type\r\n"
                    + "Content-Type: text/html\r\n"
                    + "\r\n"
                    + mensaje;
        }

        /**
         * @return the page
         */
        public struct_page getPage() {
            return page;
        }

        /**
         * @param page the page to set
         */
        public void setPage(struct_page page) {
            this.page = page;
        }

    }
}
