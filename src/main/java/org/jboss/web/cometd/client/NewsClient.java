/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.web.cometd.client;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.cometd.client.BayeuxClient;
import org.cometd.client.transport.ClientTransport;
import org.cometd.client.transport.LongPollingTransport;
import org.eclipse.jetty.client.HttpClient;

/**
 * {@code NewsClient}
 * <p/>
 *
 * Created on Oct 12, 2011 at 12:23:11 PM
 *
 * @author <a href="mailto:nbenothm@redhat.com">Nabil Benothman</a>
 */
public class NewsClient implements Runnable {

    private static final String DEFAULT_URL = "http://localhost:8080/cometd/cometd";
    private static final String SERVICE_NAME = "/news/new";
    private BayeuxClient client;
    private HttpClient httpClient;
    private String url;
    private boolean running = true;

    /**
     * Create a new instance of {@code NewsClient}
     */
    public NewsClient() {
        this(DEFAULT_URL);
    }

    /**
     * Create a new instance of {@code NewsClient}
     * @param url 
     */
    public NewsClient(String url) {
        this.url = url;
    }

    /**
     * 
     * @param args
     * @throws Exception 
     */
    public static void main(String args[]) throws Exception {

        /*
        if (args.length < 1) {
        System.err.println("Usage: java " + NewsClient.class.getName() + " [URL] n");
        System.err.println("\tURL: the service URL, default: " + DEFAULT_URL);
        System.err.println("\tn: the number of clients");
        }
        
        String url = null;
        int n = -1;
        
        if (args.length > 1) {
        URL sURL = new URL(args[0]);
        url = sURL.toString();
        n = Integer.parseInt(args[1]);
        } else {
        n = Integer.parseInt(args[0]);
        url = DEFAULT_URL;
        }
         */
        int n = 5;
        String url = DEFAULT_URL;
        for (int i = 0; i < n; i++) {
            NewsClient nc = new NewsClient(url);
            nc.init();
            nc.handshake();
            Thread t = new Thread(nc);
            t.start();
            t.join();
        }
    }

    @Override
    public void run() {
        Map<String, Object> data = new HashMap<String, Object>();
        client.waitFor(1000, BayeuxClient.State.CONNECTED);
        int counter = 0;
        while (running) {
            try {
                data.put("title", "JBoss hot news");
                data.put("url", "http://www.jboss.org");
                data.put("timestamp", System.currentTimeMillis());
                data.put("description", "JBoss community news");
                publish(data);
                Thread.sleep(500);
                if((++counter) > 5) {
                    running = false;
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(NewsClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * 
     */
    protected void init() throws Exception {
        // Create (and eventually setup) Jetty's HttpClient
        httpClient = new HttpClient();
        // Here setup Jetty's HttpClient, for example:
        // httpClient.setMaxConnectionsPerAddress(2);
        httpClient.start();
        // Prepare the transport
        Map<String, Object> options = new HashMap<String, Object>();
        ClientTransport transport = LongPollingTransport.create(options);
        // Initialize the BayeuxClient
        client = new BayeuxClient(url, transport);
    }

    /**
     * 
     */
    public void handshake() {
        client.handshake();
        client.getChannel(SERVICE_NAME).subscribe(new ClientSessionChannel.MessageListener() {

            @Override
            public void onMessage(ClientSessionChannel csc, Message msg) {
                Map<String, Object> data = msg.getDataAsMap();
                System.out.println("New message received(" + msg.getId() + "): [Title: "
                        + data.get("title") + ", URL: " + data.get("url") + ", Description: "
                        + data.get("description") + ",Timestamp: " + data.get("timestamp") + "]");
            }
        });
    }

    /**
     * 
     * @param data 
     */
    public void publish(Map<String, Object> data) {
        this.client.getChannel(SERVICE_NAME).publish(data);
    }
}
