package com.smatiz.microrest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.SSLSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ServiceThread extends Thread {

        private final Logger logger = LoggerFactory.getLogger(ServiceThread.class);
        private struct_page page = new struct_page();
        long start_time = System.currentTimeMillis();

        Socket myClientSocket;
        SSLSocket SSLmyClientSocket;

        volatile boolean m_bRunThread = true;
        private String url_db = "";
        private Config config;



         ServiceThread(String url, Socket s, Config config) {
            super();
            myClientSocket = s;
            this.url_db = url;
            this.config = config;

        }


        ServiceThread(String url, SSLSocket s, Config config) {
            super();
            SSLmyClientSocket = s;
            this.url_db = url;
            this.config = config;
        }

        public void run() {
            BufferedReader in = null;
            PrintWriter out = null;

            // SSL
            InputStream is = null;
            OutputStream os = null;
            byte[] buffer = new byte[1024];
            int bytesRead = 0;

            try {


                if (config.isSsl()) {
                    logger.info("Accepted Client Address - " + SSLmyClientSocket.getInetAddress().getHostName());
                    SSLmyClientSocket.setSoTimeout(6000);
                    out = new PrintWriter(new OutputStreamWriter(SSLmyClientSocket.getOutputStream()));
                    is =  SSLmyClientSocket.getInputStream();

                } else {
                    logger.info("Accepted Client Address - " + myClientSocket.getInetAddress().getHostName());
                    myClientSocket.setSoTimeout(MAX_PRIORITY);
                    in = new BufferedReader(
                            new InputStreamReader(myClientSocket.getInputStream()));

                    out = new PrintWriter(
                            new OutputStreamWriter(myClientSocket.getOutputStream()));
                }

                while (m_bRunThread) {
                    //System.out.println("While running...");
                    long time_elapsed = System.currentTimeMillis();
                    if ((time_elapsed - start_time) > config.getTime_out()) {
                        logger.warn("Process time limit , stopping... ");
                        m_bRunThread = false;
                        this.interrupt();

                    }

                    String clientCommand = "";
                    try {

                        if (config.isSsl()) {

                            bytesRead = is.read(buffer);
                            if (bytesRead == -1) {
                                m_bRunThread = false;
                                clientCommand = "";
                            } else {
                                clientCommand = new String(buffer, 0, bytesRead);

                                if ((buffer[bytesRead - 1] == 10)
                                        && (buffer[bytesRead - 2] == 13)
                                        && (buffer[bytesRead - 3] == 10)
                                        && (buffer[bytesRead - 4] == 13)) {
                                    logger.debug(" Detect end communication !!! ");
                                    m_bRunThread = false;
                                }

                            }
                            logger.debug("Readed " + clientCommand.length() + " bytes...");
                        } else {
                            clientCommand = in.readLine();
                        }

                    } catch (Exception ex) {
                        logger.warn("Time Out, Don't detect \\n ");
                        m_bRunThread = false;

                    }

                    if (clientCommand != null) {

                        if (clientCommand.equals("")) {

                            if (page.getAction().equals("OPTIONS")) {
                                ManageOptions(out);
                            } else {
                                logger.debug("End of client request ");



                                if (!config.isSsl()) { // Reads Body information
                                    char[] cchar = new char[page.getContent_lenght()];
                                    int read = in.read(cchar, 0, page.getContent_lenght());
                                    page.setBody(new String(cchar));
                                }

                                m_bRunThread = false;

                            }
                        }


                        getPage().parse(clientCommand);
                        logger.debug("Client Says : " + clientCommand);

                    } else {// if command!=null
                        logger.warn(" Null client request ");
                        m_bRunThread = false;
                        if (in != null) {
                            in.close();
                        }
                        if (out != null) {
                            out.close();
                        }
                        this.interrupt();
                    }
                }    // While


                /***
                 * Process information of request
                 */

                try {
                    String code = "OK 200"; // Normal situation

                    String return_info = ProcessPageInformation();

                    switch (return_info) {
                        case  "404" : {
                            out.println(response("400 Bad Request", "Resorce not found!"));
                            logger.warn("404");
                            break;
                        }
                         case  "500" : {
                            out.println(response("500 Internal Server Error", "Internal Server Error")); // Alwais is OK Send response to client
                            logger.warn("500");
                            break;
                        }
                        case  "403" : {
                            out.println(response("403 Forbidden", "Forbidden")); // Alwais is OK Send response to client
                            logger.warn("403");
                            break;
                        }
                        default : {
                            out.println(response("200 OK", return_info));
                            logger.info("200");
                            break; // Alwais is OK Send response to client
                        }
                    }

                    out.flush();

                } catch (SQLException ex) {
                    logger.error("Error in process query information "+ex.getMessage(), ex);
                }

            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);

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
                     if (config.isSsl()) { SSLmyClientSocket.close(); } else { myClientSocket.close();  }
                         logger.info("End comunication ");
                         logger.debug(getPage().toString());
                } catch (IOException ex) {
                    logger.error("Error in process query information "+ex.getMessage(), ex);
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
            System.out.println("Please remove <\"Access-Control_Allow_Origin\": > of client Header ");

        }


        /**
         *
         * @return
         * @throws SQLException
         * Process Page informacion with Postgresql Database
         */
        private String ProcessPageInformation() throws SQLException {
            DBProvider con_provider = new DBProvider(config);
            String query = "";
            List<Object> params = new ArrayList<>();
            String result = "";

            if (page.getAction().equals("GET")) {
                query = QueryGenerator.generateSelectQuery(page);
                for (Map.Entry<String, String> entry : (Set<Map.Entry<String, String>>) page.getParams().entrySet()) {
                    params.add(entry.getValue());
                }
                result = con_provider.excecuteQuery(query, params);
            } else if (page.getAction().equals("POST")) {
                query = QueryGenerator.generateInsertQuery(page);
                for (Map.Entry<String, String> entry : (Set<Map.Entry<String, String>>) page.getParams().entrySet()) {
                    params.add(entry.getValue());
                }
                int affectedRows = con_provider.executeUpdate(query, params);
                result = "{\"affected_rows\":" + affectedRows + "}";
            } else if (page.getAction().equals("PUT")) {
                query = QueryGenerator.generateUpdateQuery(page);
                for (Map.Entry<String, String> entry : (Set<Map.Entry<String, String>>) page.getParams().entrySet()) {
                    params.add(entry.getValue());
                }
                int affectedRows = con_provider.executeUpdate(query, params);
                result = "{\"affected_rows\":" + affectedRows + "}";
            } else if (page.getAction().equals("DELETE")) {
                query = QueryGenerator.generateDeleteQuery(page);
                for (Map.Entry<String, String> entry : (Set<Map.Entry<String, String>>) page.getParams().entrySet()) {
                    params.add(entry.getValue());
                }
                int affectedRows = con_provider.executeUpdate(query, params);
                result = "{\"affected_rows\":" + affectedRows + "}";
            }

            logger.debug("Query : " + query);

            if (query.equals("")) {
                return "404";
            }


            if (result == null) {
                return "500";
            }
            con_provider.disconect();

            if (result.equals("")) {
                return "500";
            }
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
            return "HTTP/1.1 " + code.trim() + "\r\n"
                    + "Server:MicroServer\r\n"
                  //  +"ETag: \"51142bc1-7449-479b075b2891b\"\r\n"
                  //  +"Accept-Ranges: bytes\n"
                    +"Content-Length:" + mensaje.length() + "\r\n"
                    + "Access-Control-Allow-Origin:*\r\n"
                    + "Access-Control-Allow-Methods:GET,POST,PUT\r\n"
                    + "Access-Control-Allow-Headers:Content-Type\r\n"
                    + "Content-Type:text/html\r\n"
                    + "\r\n"

                    + mensaje.trim();
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
