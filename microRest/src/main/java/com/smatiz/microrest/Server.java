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
 * https://www.tutorialspoint.com/javaexamples/net_multisoc.htm
 * https://www.bigbinary.com/blog/generating-json-using-postgresql-json-function
 * https://jdbc.postgresql.org/documentation/81/use.html
 * https://jdbc.postgresql.org/documentation/81/connect.html
 * 
*/
public class Server {

    private ServerSocket serverSocket;
    private final static int port = 8084;
    boolean ServerOn = true;
    private String url_db = "";
    private final int time_out = 4000;

    List<ServiceThread> lhilos = new ArrayList();

    public Server(String url) {
        this.url_db = url;
        DBProvider db = new DBProvider(url_db);
        if (db.isError()) {
            System.out.println("There are an error with DB configuration ");
        }
        db.disconect();

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Listening port " + port);
        } catch (IOException ex) {
            Logger.getLogger(MicroServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void run_server() {
        while (ServerOn) {
            try {
                Socket clientSocket = serverSocket.accept();
                ServiceThread cliThread = new ServiceThread(url_db, clientSocket);

                lhilos.add(cliThread);
                cliThread.start();
                Thread.sleep(100);

                for (int i = 0; i < lhilos.size(); i++) {
                    var h = lhilos.get(i);
                    if (h.m_bRunThread == false) {
                        h.stop();
                        lhilos.remove(h);
                    }
                    long stop_time = System.currentTimeMillis();
                    if ((stop_time - h.start_time) > time_out) {
                        h.stop();
                        lhilos.remove(h);
                    }

                }
                System.out.println("Thread  :" + lhilos.size());

            } catch (IOException ioe) {
                System.out.println("Exception found on accept. Ignoring. Stack Trace :");
            } catch (InterruptedException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    class ServiceThread extends Thread {

        private struct_page page = new struct_page();
        long start_time = System.currentTimeMillis();
        Socket myClientSocket;
        boolean m_bRunThread = true;
        private String url_db = "";

        public ServiceThread(String url) {
            super();
            this.url_db = url;

        }

        ServiceThread(String url, Socket s) {
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
                    if ((time_elapsed - start_time) > time_out) {
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
                        getPage().parse(clientCommand);
                        System.out.println("Client Says :" + clientCommand);
                        if (clientCommand.equals("}")) {
                            //m_bRunThread=false;

                        }
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
                            break;
                        } 
                         case  "500" : {
                            out.println(response("500 Internal Server Error", "Internal Server Error")); // Alwais is OK Send response to client
                            break;
                        } 
                        default : {
                            out.println(response("200 OK", return_info));                            
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
                    System.out.println("...Stopped");
                    System.out.println(getPage());
                } catch (IOException ex) {
                    Logger.getLogger(MicroServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        /**
         * 
         * @return
         * @throws SQLException 
         * Process Page informacion with Postgresql Database
         */
        private String ProcessPageInformation() throws SQLException {
            // Saca el action y el path de la base de datos para obtener el query
            DBProvider con_provider = new DBProvider(url_db);
            
            
            //TODO Debe reemplazar las variables con los valores
            String query = con_provider.getQuery(page);
           
           // Replace variables Headers and Body with values information
           Set<Map.Entry<String,String>> lparams = page.getParams().entrySet();
           for (Map.Entry<String, String> p : lparams) {
               query = query.replaceAll(p.getKey(), p.getValue());
           }
         
           // Headers
           Set<Map.Entry<String,String>> lheaders = page.getHeaders().entrySet();
           for (Map.Entry<String, String> h : lheaders) {
               query = query.replaceAll(h.getKey(), h.getValue());
           }
         
           query = query.replaceAll("body", "'" + page.getBody() +"'"); // Body           
           query = query.replaceAll("%27", "'"); // Pone las comillas simples
           
           System.out.println("Query : " + query);
            
            if (query.equals("")) { return "404";}
            String result = con_provider.excecuteQuery(query);
            
            if (result.equals("")) { return "500";}
            con_provider.disconect();
            
            return result;
        }

        /**
         *
         * @param code : 200 OK
         * @param msq
         * @return return "HTTP/1.1 200 OK\r\n" + "Server: MicroServer\r\n";
         */
        public String response(String code, String msg) {
            String mensaje = msg.trim().replaceAll("\n", "").replaceAll("\r", "");
            return "HTTP/1.1 " + code + "\r\n"
                    + "Server: MicroServer\r\n"
                  //  +"ETag: \"51142bc1-7449-479b075b2891b\"\r\n" 
                  //  +"Accept-Ranges: bytes\n" 
                    +"Content-Length: " + mensaje.length() + "\r\n"
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
