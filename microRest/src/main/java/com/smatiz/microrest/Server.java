/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smatiz.microrest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import org.apache.hc.core5.ssl.SSLContexts;


/**
 *
 * @author smatiz
 * https://www.ietf.org/rfc/rfc2616.txt
 * https://developer.mozilla.org/es/docs/Web/HTTP/Overview

*/
public class Server {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private ServerSocket serverSocket;
    private SSLServerSocket ssl_serverSocket;

    private static final boolean ServerOn = true;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    Config config;

    public Server(String path) {

        config = new Config(path);
        DBProvider db = new DBProvider(config);
        if (db.isError()) {
            logger.error("There are an error with DB configuration ");
        }
        db.disconect();

        try {
            //https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/net/ServerSocket.html
            //new ServerSocket(9090, 0, InetAddress.getLoopbackAddress());

            if (config.isSsl()) {

                SSLContext c = SSLContexts
                        .custom()
                        .loadKeyMaterial(Paths.get(
                                config.getKeyStore()
                        ).toFile()
                                , config.getKeyStorePassword().toCharArray()
                                , config.getKeyStorePassword().toCharArray())
                        .loadTrustMaterial(Paths.get(config.getTrustStore()).toFile()
                                , config.getTrustStorePassword().toCharArray())
                        .build();

                SSLServerSocketFactory sf = c.getServerSocketFactory();
                ssl_serverSocket = (SSLServerSocket) sf.createServerSocket(config.getPort());

            } else {
                serverSocket = new ServerSocket(config.getPort(), 3); // Without SSL
            }
            logger.info("Listening port " + config.getPort() + " ,SSL enable " + config.isSsl());

        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        } catch (NoSuchAlgorithmException | KeyStoreException | UnrecoverableKeyException | CertificateException | KeyManagementException ex) {
            logger.error(ex.getMessage(), ex);
        }

    }

    

    
    public void run_server() {
        int id = 0;

        while (ServerOn) {
            try {               
                  if (config.isSsl()) {                  
                   
                    SSLSocket clientSocket = (SSLSocket) ssl_serverSocket.accept();
                    
                    executorService.execute(new ServiceThread(config.getConnectionUrl(), clientSocket, config));
                } else {                 
                    Socket clientSocket = serverSocket.accept();                    
                    executorService.execute(new ServiceThread(config.getConnectionUrl(), clientSocket, config));

                }

               
               
            } catch (IOException ioe) {
                logger.error(ioe.getMessage(), ioe);
            } 
        }

    }
}
