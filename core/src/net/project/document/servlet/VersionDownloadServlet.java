/* 
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
*/

 package net.project.document.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.project.document.Document;
import net.project.document.DocumentException;
import net.project.document.DocumentManagerBean;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.security.User;

import org.apache.log4j.Logger;


public class VersionDownloadServlet extends HttpServlet {

    ServletContext servletContext = null;

    /**
     * doGet is method that responds to the get method passed to a HTTP Servlet.
     *
     * @param request A <code>HttpServletRequest</code> parameter containing the 
     * variables passed to the HTTP Server.
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        DocumentManagerBean docManager = null;
        String objectID = null;
        String versionID = null;
        
        docManager = SessionManager.getDocumentManager();
        User user = SessionManager.getUser();

        // GET THE OBJECT ID FROM THE REQUEST
        objectID = request.getParameter("id");

        if (objectID != null)
            session.putValue("objectID", objectID);
        else
            objectID = (String) session.getValue("objectID");


        // GET THE VERSION  ID FROM THE REQUEST
        versionID = request.getParameter("versionid");

        if (versionID != null)
            session.putValue("versionID", versionID);
        else
            versionID = (String) session.getValue("versionID");
        
        // SET THE STATUS
        // SJMITTAL: FOR SOME UNKNOW REASON(S) WE ARE NOT SETTING THE
        // STATUS VALUE IN THE SESSION UNLIKE ABOVE
        String status = request.getParameter("status");

        Document document = new Document();

        document.setID(objectID);
        document.setUser(user);
        
        if("D".equals(status)) {
            document.setListDeleted();//need to check from deleted documents
        }

        try {
            document.load();
        } catch (PersistenceException pe) {
        	Logger.getLogger(VersionDownloadServlet.class).debug("VersionDownloadServlet.doGet() threw a persistence exception: " + pe);
        }


        document.loadVersions();

        try {
            docManager.viewVersion (document, versionID, response);
        } catch (DocumentException de) {
        	Logger.getLogger(VersionDownloadServlet.class).debug("VersionDownloadServlet.doGet() threw a document exception on download: " + de);
        } catch (PersistenceException de) {
        	Logger.getLogger(VersionDownloadServlet.class).debug("VersionDownloadServlet.doGet() threw a persistence exception on download: " + de);
        }

    } // end doGet()



    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();

        DocumentManagerBean docManager = null;

        docManager = SessionManager.getDocumentManager();
        User user = SessionManager.getUser();


        Document document = (Document) docManager.getCurrentObject();
        String versionID = (String) session.getValue("currentVersionID");

        document.loadVersions();

        document.setUser ( user );

        try {
            docManager.viewVersion (document, versionID, response);
        } catch (DocumentException de) {
        	Logger.getLogger(VersionDownloadServlet.class).debug("DocumentDownloadServlet threw a document exception on download: " + de);
        } catch (PersistenceException de) {
        	Logger.getLogger(VersionDownloadServlet.class).debug("DocumentDownloadServlet threw a persistence exception on download: " + de);
        }
    }//doPost


    public void init(ServletConfig servletConfig) throws ServletException {
        servletContext = servletConfig.getServletContext();
        //        servletContext.log("alive");
    } // init


}





