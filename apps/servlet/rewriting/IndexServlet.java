package org.joe_e.servlet.mail;

import java.io.IOException;

import org.w3c.dom.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joe_e.servlet.JoeEServlet;
import org.joe_e.servlet.response.ServletResponseWrapper;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import org.joe_e.servlet.AbstractCookieView;
import org.joe_e.servlet.AbstractSessionView;
/**
 * @author akshay
 *
 */
public class IndexServlet extends JoeEServlet {
    
    public SessionView session;
    public CookieView cookies;
    
    public class SessionView extends AbstractSessionView {
        private HttpSession session;
        public SessionView(HttpSession ses) {
            super(ses);
            session = ses;
        }
        public String getusername() {
            return (String) session.getAttribute("__joe-e__username");
        }
        public String gettoken() {
            return (String) session.getAttribute("__joe-e__token");
        }
    }

    public class CookieView extends AbstractCookieView {
        public CookieView(Cookie[] c) {
            super(c);
        }
        public String gettestCookie() {
            for (Cookie c : cookies) {
                if (c.getName().equals("__joe-e__testCookie")) {
                    return c.getValue();
                }
            }
            return null;
        }
        public void settestCookie(String arg) {
            boolean done = false;
            for (Cookie c : cookies) {
                if (c.getName().equals("__joe-e__testCookie")) {
                    c.setValue(arg);
                    done = true;
                }
            }
            if (!done) {
                cookies.add(new Cookie("__joe-e__testCookie", arg));
            }
        }

    }
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (session.getUsername() != null) {
            res.sendRedirect("/servlet/inbox");
        }
        res.addHeader("Content-type", "text/html");
        Document doc = ((ServletResponseWrapper)res).getDocument();
        Element body = HtmlWriter.printHeader(doc);

        Element tmp = doc.createElement("p");
        body.appendChild(tmp);
        tmp.appendChild(doc.createTextNode("Welcome to Joe-E mail"));
        tmp = doc.createElement("a");
        tmp.setAttribute("href","/servlet/login");
        tmp.appendChild(doc.createTextNode("Log In"));
        body.appendChild(tmp);
        body.appendChild(doc.createElement("br"));

        tmp = doc.createElement("a");
        tmp.setAttribute("href","/servlet/create");
        tmp.appendChild(doc.createTextNode("Create an Account"));
        body.appendChild(tmp);
        body.appendChild(doc.createElement("br"));
        
        tmp = doc.createElement("a");
        tmp.setAttribute("href","/servlet/");
        tmp.appendChild(doc.createTextNode("Stay here"));
        body.appendChild(tmp);
        body.appendChild(doc.createElement("br"));
        tmp = doc.createElement("script");
        tmp.appendChild(doc.createTextNode("alert('foo');"));
        body.appendChild(tmp);

        if (cookies.getTestCookie() != null) {
            body.appendChild(doc.createTextNode(cookies.getTestCookie()));
        }
        body.appendChild(doc.createElement("br"));

        body.appendChild(doc.createTextNode("token: " + session.getToken()));
        body.appendChild(doc.createElement("br"));

        if (cookies.getTestCookie() == null) {
            cookies.setTestCookie("1");
        } else {
            cookies.setTestCookie("" + (Integer.parseInt(cookies.getTestCookie())+1));
        }
    }
    
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        this.doGet(req, res);
    }
}
