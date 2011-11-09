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

import org.cometd.bayeux.Channel;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSession;
import org.cometd.java.annotation.Listener;
import org.cometd.java.annotation.Service;
import org.cometd.java.annotation.Session;
import org.cometd.java.annotation.Subscription;

/**
 * {@code ClientService}
 * <p/>
 *
 * Created on Oct 7, 2011 at 3:58:36 PM
 *
 * @author <a href="mailto:nbenothm@redhat.com">Nabil Benothman</a>
 */
@Service("clientService")
public class ClientService {

    @Session
    private ClientSession bayeuxClient;

    /**
     * Create a new instance of {@code ClientService}
     */
    public ClientService() {
        super();
    }

    @Listener(value = Channel.META_CONNECT, receiveOwnPublishes = true)
    public void metaConnect(Message message) {
        System.out.println("New message received: " + message);
    }

    @Subscription("/news/new")
    public void onMessage(Message message) {
        String title = (String) message.getDataAsMap().get("title");
        String url = (String) message.getDataAsMap().get("url");
        String desc = (String) message.getDataAsMap().get("description");
        long timestamp = (Long) message.getDataAsMap().get("timestamp");
    }
}
