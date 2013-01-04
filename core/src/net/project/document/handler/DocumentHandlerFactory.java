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

 /*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
|
+----------------------------------------------------------------------*/
package net.project.document.handler;

import java.sql.SQLException;

import net.project.database.DBBean;
import net.project.document.Document;

import org.apache.log4j.Logger;

/**
 * DocumentHandlerFactory is responsible for instanciating finding the proper
 * class to handle a task related to a document.  For example, if you were 
 * trying to view a document, you would call:
 * <code>
 *     DocumentHandlerFactory.getHandler(doc, DocumentHandlerFactory.DEFAULT_HANDLER);
 * </code>
 *
 * @author Matthew Flower
 * @since Gecko Update 2 (ProductionLink)
 */
public class DocumentHandlerFactory {
    public static String DEFAULT_HANDLER = "view";
    private static String DEFAULT_HANDLER_CLASS = "net.project.document.handler.DefaultDocumentHandler";

    /**
     * Get a document handler that corresponds to the document and the handler
     * type that you specify.
     *
     * @param document a <code>Document</code> value that you wish to handle.  We
     * will grab the mime type from this document to help us find the right class
     * to handle the factory.
     * @param handlerType a <code>String</code> value that specifies if we want to
     * VIEW, EDIT, DEFAULT, etc this class.  There are constants defined in this
     * class that should be passed to this method.
     */
    public static DocumentHandler getHandler(Document document, String handlerType) {
        DBBean db = new DBBean();
        String handlerClass;
        DocumentHandler handlerInstance = null;

        //See if a specific class has been registered to deal with this kind of class
        try {
            //Check to see if this handler exists
            db.prepareStatement("select action_handler "+
                                "from pn_doc_handler dh, pn_doc_format df "+
                                "where "+
                                "  action = ? and dh.doc_format_id = df.doc_format_id  "+
                                "  and df.mime_type = ? "+
                                "order by is_default desc ");
            db.pstmt.setString(1, handlerType);
            db.pstmt.setString(2, document.getMimeType());
            db.executePrepared();

            if (db.result.next()) {
                handlerClass = db.result.getString("action_handler");
            } else {
                //We couldn't find a handler, go for the default
                db.prepareStatement("select dh.action_handler "+
                                    "from pn_doc_handler dh, pn_doc_format df " +
                                    "where " +
                                    "  dh.doc_format_id = df.doc_format_id " +
                                    "  and df.mime_type = ?");
                db.pstmt.setString(1, document.getMimeType());
                db.executePrepared();
    
                if (db.result.next()) {
                    //We found a default handler for this class.  Use that
                    //class to handle the file access.
                    handlerClass = db.result.getString("action_handler");
                } else {
                    //We couldn't find a handler in the database.  Just use the
                    //default.
                    handlerClass = DEFAULT_HANDLER_CLASS;
                }
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(DocumentHandlerFactory.class).debug(""+sqle);
            handlerClass = DEFAULT_HANDLER_CLASS;
        } finally { 
            db.release();
        }

        //Return an instance of the Document Handler Class
        try {
            handlerInstance = (DocumentHandler)Class.forName(handlerClass).newInstance();
        } catch (ClassNotFoundException cnfe) {
        	Logger.getLogger(DocumentHandlerFactory.class).error("A class not found exception was thrown "+
                                       "while trying to instantiate " + handlerClass + ".  "+
                                       cnfe);
        } catch (InstantiationException ie) {
        	Logger.getLogger(DocumentHandlerFactory.class).error("An unexpected instantiation exception was thrown "+
                                       "while trying to instantiate " + handlerClass + ".  "+
                                       ie);
        } catch (IllegalAccessException iae) {
        	Logger.getLogger(DocumentHandlerFactory.class).error("An unexpected illegal access exception was thrown "+
                                       "while trying to instantiate " + handlerClass + ".  "+
                                       iae);
        }
    
        return handlerInstance;
    }
}
