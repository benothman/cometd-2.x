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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import org.cometd.java.annotation.AnnotationCometdServlet;

/**
 * {@code NewsAnnotationCometdServlet}
 * <p/>
 *
 * Created on Oct 10, 2011 at 9:25:46 AM
 *
 * @author <a href="mailto:nbenothm@redhat.com">Nabil Benothman</a>
 */
/*
@WebServlet(name = "cometd", urlPatterns = {"/cometd/*"}, loadOnStartup = 1,
asyncSupported = true, initParams = {
    @WebInitParam(name = "services",
    value = "org.jboss.web.cometd.service.NewsService,org.jboss.web.cometd.service.HelloService"),
    @WebInitParam(name = "timeout", value = "30000"),
    @WebInitParam(name = "interval", value = "0"),
    @WebInitParam(name = "maxInterval", value = "10000"),
    @WebInitParam(name = "maxLazyTimeout", value = "5000"),
    @WebInitParam(name = "long-polling.multiSessionInterval", value = "2000"),
    @WebInitParam(name = "logLevel", value = "1")})
 */
public class NewsAnnotationCometdServlet extends AnnotationCometdServlet {

    /**
     * Create a new instance of {@code NewsAnnotationCometdServlet}
     */
    public NewsAnnotationCometdServlet() {
        super();
    }

    @Override
    public void init() throws ServletException {
        Logger.getLogger(NewsAnnotationCometdServlet.class.getName()).log(Level.INFO,
                "Initializing the news annotation cometd servlet");
        super.init();
    }
}
