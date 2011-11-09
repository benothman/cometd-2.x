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
package org.jboss.web.cometd.service;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import javax.servlet.UnavailableException;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ConfigurableServerChannel;
import org.cometd.bayeux.server.ServerChannel;
import org.cometd.bayeux.server.ServerMessage;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.java.annotation.Listener;
import org.cometd.java.annotation.ServerAnnotationProcessor;
import org.cometd.java.annotation.Service;
import org.cometd.server.BayeuxServerImpl;
import org.cometd.server.authorizer.GrantAuthorizer;
import org.cometd.server.ext.AcknowledgedMessagesExtension;
import org.cometd.server.ext.TimesyncExtension;

/**
 * {@code BayeuxInitializer}
 * <p/>
 *
 * Created on Oct 10, 2011 at 12:28:35 PM
 *
 * @author <a href="mailto:nbenothm@redhat.com">Nabil Benothman</a>
 */
public class BayeuxInitializer extends GenericServlet {

    private static final Logger logger = Logger.getLogger(BayeuxInitializer.class.getName());
    private final List<Object> services = new ArrayList<Object>();
    private ServerAnnotationProcessor processor;

    /**
     * Create anew instance of {@code BayeuxInitializer}
     */
    public BayeuxInitializer() {
        super();
        logger.log(Level.INFO, "Create a new instance of {0} ", BayeuxInitializer.class.getName());
    }

    @Override
    public void init() throws ServletException {

        super.init();
        final BayeuxServerImpl bayeux = (BayeuxServerImpl) getServletContext().getAttribute(BayeuxServer.ATTRIBUTE);
        logger.log(Level.INFO, "Initializing the {0}", BayeuxInitializer.class.getName());

        if (bayeux == null) {
            throw new UnavailableException("No BayeuxServer!");
        }

        // Create extensions
        bayeux.addExtension(new TimesyncExtension());
        bayeux.addExtension(new AcknowledgedMessagesExtension());

        // Deny unless granted
        bayeux.createIfAbsent("/**", new ServerChannel.Initializer() {

            @Override
            public void configureChannel(ConfigurableServerChannel channel) {
                channel.addAuthorizer(GrantAuthorizer.GRANT_NONE);
            }
        });
        // Allow anybody to handshake
        bayeux.getChannel(ServerChannel.META_HANDSHAKE).addAuthorizer(GrantAuthorizer.GRANT_PUBLISH);

        processor = new ServerAnnotationProcessor(bayeux);
        Object newsService = new NewsService();
        Object monitor = new Monitor();
        HelloService helloService = new HelloService();
        processor.process(newsService);
        processor.process(monitor);
        processor.process(helloService);
        services.add(monitor);
        services.add(newsService);

        bayeux.createIfAbsent("/news", new ConfigurableServerChannel.Initializer() {

            @Override
            public void configureChannel(ConfigurableServerChannel channel) {
                channel.setPersistent(true);
            }
        });

        bayeux.createIfAbsent("/hello", new ConfigurableServerChannel.Initializer() {

            @Override
            public void configureChannel(ConfigurableServerChannel channel) {
                channel.setPersistent(true);
            }
        });

        if (bayeux.getLogger().isDebugEnabled()) {
            bayeux.getLogger().debug(bayeux.dump());
        }
    }

    @Override
    public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        throw new ServletException();
    }

    @Override
    public void destroy() {
        // Deprocess the services that have been created
        logger.log(Level.INFO, "Deprocess the services that have been created");
        for (Object service : services) {
            processor.deprocess(service);
        }
    }

    @Service("monitor")
    public static class Monitor {

        @Listener("/meta/subscribe")
        public void monitorSubscribe(ServerSession session, ServerMessage message) {
            logger.log(Level.INFO, "Monitored Subscribe from {0} for {1}", new Object[]{session, message.get(Message.SUBSCRIPTION_FIELD)});
        }

        @Listener("/meta/unsubscribe")
        public void monitorUnsubscribe(ServerSession session, ServerMessage message) {
            logger.log(Level.INFO, "Monitored Unsubscribe from {0} for {1}", new Object[]{session, message.get(Message.SUBSCRIPTION_FIELD)});
        }

        @Listener("/meta/*")
        public void monitorMeta(ServerSession session, ServerMessage message) {
            logger.log(Level.INFO, message.toString());
        }
    }
}
