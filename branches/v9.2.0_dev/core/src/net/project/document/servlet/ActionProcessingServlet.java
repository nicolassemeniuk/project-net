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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.project.base.Module;
import net.project.document.DocumentManagerBean;
import net.project.security.Action;
import net.project.security.ServletSecurityProvider;
import net.project.security.SessionManager;

public class ActionProcessingServlet extends HttpServlet {
    ServletContext servletContext = null;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
        	DocumentManagerBean docManager = null;
            HttpSession session = request.getSession();

            docManager = SessionManager.getDocumentManager();
            
            String action = request.getParameter("theAction");
            session.setAttribute("currentVersionID", request.getParameter("versionID"));

            String objectType = request.getParameter("item");
            
            if (docManager == null || action == null) {
            	response.sendRedirect(SessionManager.getJSPRootURL() + "/document/Main.jsp?module=" + Module.DOCUMENT);            	
            	return;
            }
            //docManager.set if the containerID property is set, use it
            if (request.getParameter("containerID") != null && request.getParameter("containerID").trim().length()>0 )
                docManager.setCurrentContainer(request.getParameter("containerID"));
            
             // this will call LOAD on the appropriate object
            if (request.getParameter("selected") != null && request.getParameter("selected").trim().length()>0 )
                docManager.setCurrentObjectID(request.getParameter("selected"));
            
            request.setAttribute("id", docManager.getCurrentObjectID());
            request.setAttribute("module", request.getParameter("module"));
            session.setAttribute("docManager", docManager);
            // now parse the action and act accordingly!

            /*****************************************************************************************************************
             *****                             Implementing Standard Toolbar methods                                     *****
             *****************************************************************************************************************/

            if (action.equals("cancel")) {
                response.sendRedirect(docManager.getCancelPage());
            } else if (action.equals("list_Deleted")) {
                request.setAttribute("action", Integer.toString(Action.LIST_DELETED));
                request.setAttribute("id", docManager.getCurrentContainerID());

               	ServletSecurityProvider.setAndCheckValues(request);

                RequestDispatcher dispatcher = request.getRequestDispatcher("/document/ListDeleted.jsp");
                dispatcher.forward(request, response);
            }else if (action.equals("remove_Deleted")) {
                request.setAttribute("action", Integer.toString(Action.REMOVE_DELETED));
                request.setAttribute("id", docManager.getCurrentContainerID());

                ServletSecurityProvider.setAndCheckValues(request);
                
                RequestDispatcher dispatcher = request.getRequestDispatcher("/document/RemoveProcessing.jsp");
                dispatcher.forward(request, response);
            }else if (action.equals("undo_delete")) {
                request.setAttribute("action", Integer.toString(Action.UNDO_DELETED));
                request.setAttribute("id", docManager.getCurrentContainerID());

            // set and check the access of the requested resource---
                ServletSecurityProvider.setAndCheckValues(request);
                
                RequestDispatcher dispatcher = request.getRequestDispatcher("/document/RemoveProcessing.jsp");
                dispatcher.forward(request, response);
            }else if (action.equals("create")) {
                request.setAttribute("action", Integer.toString(Action.CREATE));
                request.setAttribute("id", docManager.getCurrentContainerID());

                ServletSecurityProvider.setAndCheckValues(request);
                
                RequestDispatcher dispatcher = request.getRequestDispatcher("/document/ImportObject.jsp");
                dispatcher.forward(request, response);
            } else if (action.equals("modify")) {
                request.setAttribute("action", Integer.toString(Action.MODIFY));

                ServletSecurityProvider.setAndCheckValues(request);
                
                RequestDispatcher dispatcher = request.getRequestDispatcher("/document/ObjectModifyPropertyManager.jsp");
                dispatcher.forward(request, response);
            } else if (action.equals("remove")) {
                request.setAttribute("action", Integer.toString(Action.DELETE));

                ServletSecurityProvider.setAndCheckValues(request);
                
                RequestDispatcher dispatcher = request.getRequestDispatcher("/document/RemoveProcessing.jsp");
                dispatcher.forward(request, response);
            } else if (action.equals("reset")) {
            	ServletSecurityProvider.setAndCheckValues(request);
            	RequestDispatcher dispatcher = request.getRequestDispatcher((String)docManager.getNavigator().get("TopContainer"));
                 dispatcher.forward(request, response);
            	//response.sendRedirect((String)docManager.getNavigator().get("TopContainer"));
            } else if (action.equals("properties")) {
                request.setAttribute("action", Integer.toString(Action.VIEW));

                ServletSecurityProvider.setAndCheckValues(request);
                
                RequestDispatcher dispatcher = request.getRequestDispatcher("/document/ContainerObjectPropertyManager.jsp");
                dispatcher.forward(request, response);
            } else if (action.equals("link")) {
                RequestDispatcher dispatcher = request.getRequestDispatcher("/document/doc_error.html");
                dispatcher.forward(request, response);
            } else if (action.equals("search")) {
                RequestDispatcher dispatcher = request.getRequestDispatcher("/document/doc_error.html");
                dispatcher.forward(request, response);
            } else if (action.equals("notify")) {
                RequestDispatcher dispatcher = request.getRequestDispatcher("/notification/CreateSubscription1.jsp");
                dispatcher.forward(request, response);
            } else if (action.equals("move")) {
                request.setAttribute("action", Integer.toString(Action.MODIFY));

                ServletSecurityProvider.setAndCheckValues(request);
                
                RequestDispatcher dispatcher = request.getRequestDispatcher("/document/MoveFrameset.jsp");
                dispatcher.forward(request, response);
            } else if (action.equals("help")) {
                RequestDispatcher dispatcher = request.getRequestDispatcher("/document/doc_error.html");
                dispatcher.forward(request, response);

            } else if (action.equals("security")) {
                RequestDispatcher dispatcher = request.getRequestDispatcher("/security/SecurityMain.jsp?objectType=document");
                // Does the user have modify_permission permission on the object?
                if (!SessionManager.getSecurityProvider().isActionAllowed(docManager.getCurrentObjectID(), Integer.toString(Module.DOCUMENT), Action.MODIFY_PERMISSIONS))
                    throw new net.project.security.AuthorizationFailedException("Failed security validation");

                session.setAttribute("objectID", docManager.getCurrentObjectID());
                request.setAttribute("module", Integer.toString(Module.SECURITY));
                request.setAttribute("action", Integer.toString(Action.VIEW));

                ServletSecurityProvider.setAndCheckValues(request);
                
                dispatcher.forward(request, response);
            } else if (action.equals("workflow")) {
                request.setAttribute("action", Integer.toString(Action.MODIFY));
                RequestDispatcher dispatcher = request.getRequestDispatcher("/workflow/envelope/EnvelopeWizardStart.jsp");

                ServletSecurityProvider.setAndCheckValues(request);
                
                dispatcher.forward(request, response);
            }


            /*****************************************************************************************************************
             *****                             Implementing Document Toolbar methods                                             *****
             *****************************************************************************************************************/

            else if (action.equals("check_out")) {
                request.setAttribute("action", Integer.toString(Action.MODIFY));
                RequestDispatcher dispatcher = request.getRequestDispatcher("/document/CheckOut.jsp");

                ServletSecurityProvider.setAndCheckValues(request);
                
                dispatcher.forward(request, response);
            } else if (action.equals("check_in")) {
                request.setAttribute("action", Integer.toString(Action.MODIFY));
                RequestDispatcher dispatcher = request.getRequestDispatcher("/document/CheckIn.jsp");

                ServletSecurityProvider.setAndCheckValues(request);
                
                dispatcher.forward(request, response);
            } else if (action.equals("view")) {
                RequestDispatcher dispatcher = request.getRequestDispatcher("/servlet/DownloadDocument");
                dispatcher.forward(request, response);
            } else if (action.equals("view_version")) {
                RequestDispatcher dispatcher = request.getRequestDispatcher("/servlet/DownloadVersion");
                dispatcher.forward(request, response);
            } else if (action.equals("undo_check_out")) {
                request.setAttribute("action", Integer.toString(Action.MODIFY));
                RequestDispatcher dispatcher = request.getRequestDispatcher("/document/UndoCheckOut.jsp");

                ServletSecurityProvider.setAndCheckValues(request);
               
                dispatcher.forward(request, response);
            } else if (action.equals("create_folder")) {
                request.setAttribute("action", Integer.toString(Action.CREATE));
                request.setAttribute("id", docManager.getCurrentContainerID());

                ServletSecurityProvider.setAndCheckValues(request);
                
                RequestDispatcher dispatcher = request.getRequestDispatcher("/document/CreateContainer.jsp");
                dispatcher.forward(request, response);
            }

            /*****************************************************************************************************************
             *****                                      Implementing OTHER methods                                                    *****
             *****************************************************************************************************************/

            else if (action.equals("traverse")) {
                request.setAttribute("action", Integer.toString(Action.VIEW));
                RequestDispatcher dispatcher = request.getRequestDispatcher("/document/TraverseFolderProcessing.jsp");

                ServletSecurityProvider.setAndCheckValues(request);
                
                dispatcher.forward(request, response);
            } else if (action.equals("launch_applet")) {

                RequestDispatcher dispatcher = request.getRequestDispatcher("/document/DocumentAppletLauncher.jsp");
                dispatcher.forward(request, response);
            } else if (action.equals("version")) {
                request.setAttribute("action", Integer.toString(Action.VIEW));
                RequestDispatcher dispatcher = request.getRequestDispatcher("/document/Versions.jsp");

                ServletSecurityProvider.setAndCheckValues(request);
                
                dispatcher.forward(request, response);
            } else if (action.equals("property_sheet")) {
                request.setAttribute("action", Integer.toString(Action.VIEW));
                RequestDispatcher dispatcher = request.getRequestDispatcher("/document/Properties.jsp");

                ServletSecurityProvider.setAndCheckValues(request);
                
                dispatcher.forward(request, response);
            } else if (action.equals("history")) {
                request.setAttribute("action", Integer.toString(Action.VIEW));
                RequestDispatcher dispatcher = request.getRequestDispatcher("/document/EventHistory.jsp");

                ServletSecurityProvider.setAndCheckValues(request);
                
                dispatcher.forward(request, response);
            } else if (action.equals("discussWithHistory")) {
                RequestDispatcher dispatcher = request.getRequestDispatcher("/document/Discuss.jsp?" +
                    "showHistory=1&" +
                    "module=" + Module.DISCUSSION);
            	ServletSecurityProvider.setAndCheckValues(request);

                dispatcher.forward(request, response);
	    } else if (action.equals("discussWithHistory2")) {
        		request.setAttribute("action", Integer.toString(Action.VIEW));
        		request.setAttribute("isDiscussion", "showDiscussion");
                RequestDispatcher dispatcher = request.getRequestDispatcher("/document/ContainerObjectPropertyManager.jsp");
                dispatcher.forward(request, response);
            } else if (action.equals("discuss")) {
                RequestDispatcher dispatcher = request.getRequestDispatcher("/document/Discuss.jsp?" +
                    "module=" + Module.DISCUSSION);
//              Avinash: set and check the access of the requested resource---
            	ServletSecurityProvider.setAndCheckValues(request);

                dispatcher.forward(request, response);
            } else {
                RequestDispatcher dispatcher = request.getRequestDispatcher("/document/doc_error.html");
                dispatcher.forward(request, response);
            }
        } catch (net.project.security.AuthorizationFailedException e) {
            RequestDispatcher dispatcher = request.getRequestDispatcher("/AccessDenied.jsp");
            dispatcher.forward(request, response);
        	
        }
    }//doPost


    public void init(ServletConfig servletConfig) throws ServletException {
        servletContext = servletConfig.getServletContext();
        //        servletContext.log("alive");
    } // init
}
