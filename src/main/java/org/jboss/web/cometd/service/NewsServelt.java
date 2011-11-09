/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.web.cometd.service;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import org.eclipse.jetty.util.log.Log;
import org.jboss.web.cometd.client.ClientService;

/**
 *
 * @author Benothman
 */
@WebServlet(name = "NewsServelt", urlPatterns = {"/news"})
public class NewsServelt extends GenericServlet {

    @Override
    public void init() throws ServletException {
        super.init();
        final BayeuxServerImpl bayeux = (BayeuxServerImpl) getServletContext().getAttribute(BayeuxServer.ATTRIBUTE);

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

        ServerAnnotationProcessor processor = new ServerAnnotationProcessor(bayeux);
        processor.process(new NewsService());
        processor.process(new Monitor());
        processor.process(new ClientService());
        // processor.process(new ChatService());

        bayeux.createIfAbsent("/foo/bar/baz", new ConfigurableServerChannel.Initializer() {

            @Override
            public void configureChannel(ConfigurableServerChannel channel) {
                channel.setPersistent(true);
            }
        });

        if (bayeux.getLogger().isDebugEnabled()) {
            System.err.println(bayeux.dump());
        }
    }

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet NewsServelt</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet NewsServelt at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
             */
        } finally {
            out.close();
        }
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        throw new ServletException();
    }

    @Service("monitor")
    public static class Monitor {

        @Listener("/meta/subscribe")
        public void monitorSubscribe(ServerSession session, ServerMessage message) {
            Log.info("Monitored Subscribe from " + session + " for " + message.get(Message.SUBSCRIPTION_FIELD));
        }

        @Listener("/meta/unsubscribe")
        public void monitorUnsubscribe(ServerSession session, ServerMessage message) {
            Log.info("Monitored Unsubscribe from " + session + " for " + message.get(Message.SUBSCRIPTION_FIELD));
        }

        @Listener("/meta/*")
        public void monitorMeta(ServerSession session, ServerMessage message) {
            if (Log.isDebugEnabled()) {
                Log.debug(message.toString());
            }
        }
    }
}
