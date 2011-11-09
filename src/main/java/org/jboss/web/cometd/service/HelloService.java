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

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ConfigurableServerChannel;
import org.cometd.bayeux.server.LocalSession;
import org.cometd.bayeux.server.ServerMessage;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.java.annotation.Configure;
import org.cometd.java.annotation.Listener;
import org.cometd.java.annotation.Service;
import org.cometd.java.annotation.Session;
import org.cometd.server.authorizer.GrantAuthorizer;
import org.cometd.server.filter.DataFilterMessageListener;
import org.cometd.server.filter.NoMarkupFilter;
import org.jboss.web.cometd.filter.BadWordFilter;

/**
 * {@code HelloService}
 * <p/>
 *
 * Created on Oct 7, 2011 at 3:52:35 PM
 *
 * @author <a href="mailto:nbenothm@redhat.com">Nabil Benothman</a>
 */
@Service("/hello")
public class HelloService {

    private static final Logger logger = Logger.getLogger(HelloService.class.getName());
    @Inject
    private BayeuxServer bayeux;
    @Session
    private LocalSession localSession;
    @Session
    private ServerSession serverSession;

    @PostConstruct
    void init() {
        System.out.println("intializing HelloService");
    }

    @PreDestroy
    void destroy() {
        System.out.println("Destroying HelloService");
    }
    
    @Configure("/hello/**")
    public void configure(ConfigurableServerChannel channel) {
        logger.log(Level.INFO, "Starting configuring service: {0} ", getClass().getCanonicalName());
        DataFilterMessageListener noMarkup = new DataFilterMessageListener(bayeux, new NoMarkupFilter(), new BadWordFilter());
        channel.addListener(noMarkup);
        channel.addAuthorizer(GrantAuthorizer.GRANT_ALL);
        logger.log(Level.INFO, "The service {0} is configured", getClass().getCanonicalName());
    }

    @Listener("/service/hello")
    public void news(ServerSession remote, ServerMessage.Mutable message) {
        Map<String, Object> data = message.getDataAsMap();
        String content = (String) data.get("content");
        logger.log(Level.INFO, "New message received: {0}", content);
        remote.deliver(serverSession, message.getChannel(), data, null);
        
        ClientSessionChannel channel = serverSession.getLocalSession().getChannel(message.getChannel());
        channel.publish(data);
    }
}
