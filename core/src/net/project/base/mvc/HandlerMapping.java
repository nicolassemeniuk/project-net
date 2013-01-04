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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.base.mvc;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.project.base.PnetRuntimeException;
import net.project.base.compatibility.Compatibility;
import net.project.util.Validator;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class finds an appropriate "Handler" to deal with a web class.  The
 * handler is the basic workhorse in our MVC architecture that communicates with
 * the persistence tier.
 *
 * @author Matthew Flower
 * @since Version 8.0.0
 */
public class HandlerMapping {
    private static Map handlerMap = null;
    static {
        ensureMap();
    }

    public static synchronized void ensureMap() {
        try {
            if (handlerMap == null) {
                ensureMap(Compatibility.getResourceProvider().getResourceAsStream("HandlerMapping.xml"));
            }
        } catch (IOException e) {
            throw new PnetRuntimeException("Unable to load the file HandlerMapping.xml " +
                "to initialize HandlerMapping", e);
        }
    }

    public static synchronized void ensureMap(InputStream xmlStream) {
        if (handlerMap == null) {
            //Use SAX to read the document.  Put each entry into the map.
            try {
                SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
                HandlerMapBuilder mapBuilder = new HandlerMapBuilder();

                parser.parse(xmlStream, mapBuilder);

                handlerMap = mapBuilder.getHandlerMap();
            } catch (IOException e) {
                throw new PnetRuntimeException("Unable to load the file HandlerMapping.xml " +
                    "to initialize HandlerMapping", e);
            } catch (ParserConfigurationException e) {
                throw new PnetRuntimeException("Unable to parse HandlerMapping.xml " +
                    "to initialize HandlerMapping", e);
            } catch (SAXException e) {
                throw new PnetRuntimeException("Unable to parse HandlerMapping.xml " +
                    "to initialize HandlerMapping", e);
            }

            //The document probably can be gotten through ServletContext (BEA),
            //or through the file_path of apserver.txt (Bluestone).  Think about
            //the crypto.key -- that should give some hints of how to do it.
        }
    }

    /**
     * Get the appropriate handler according to the request for handlers.
     *
     * @param request a <code>HttpServletRequest</code> which should (among other
     * things) specify a handler would should be able to take care of it.
     * @return
     */
    public static Handler getHandler(HttpServletRequest request) {
        ensureMap();

        //Get the path information minus the first slash.
        String pathInfo = request.getPathInfo();
        if (Validator.isBlankOrNull(pathInfo)) {
            pathInfo = (String)request.getAttribute("javax.servlet.include.path_info");
        }
        if (Validator.isBlankOrNull(pathInfo)) {       
            pathInfo = request.getParameter("handlerName");
        }
        if (Validator.isBlankOrNull(pathInfo)) {
            pathInfo = request.getParameter("theAction");
        }

        HandlerMapEntry entry = (HandlerMapEntry)handlerMap.get(pathInfo);
        if (entry == null) {
            Logger.getLogger(HandlerMapping.class).debug("Unable to find handler mapping for " + pathInfo);
            throw new PnetRuntimeException("No HandlerMapping entry for " + pathInfo + " in HandlerMapping.xml file.");
        }
        return entry.constructHandler(request);
    }

    Map getHandlerMap() {
        return handlerMap;
    }

    void setHandlerMap(Map handlerMap) {
        HandlerMapping.handlerMap = handlerMap;
    }
}

class HandlerMapEntry {
    String url;
    String className = "";
    ViewType viewType = ViewType.NOT_SPECIFIED;

    public Handler constructHandler(HttpServletRequest request) {
        try {
            //Find the constructor for this handler that has the correct signature
            Class handlerType = Class.forName(className);
            Constructor constructor = handlerType.getConstructor(new Class[] {HttpServletRequest.class});

            Handler handler = (Handler)constructor.newInstance(new Object[] { request });
            handler.setViewType(viewType);

            return handler;
        } catch (ClassNotFoundException e) {
            throw new PnetRuntimeException("There is no class with the name " + className, e);
        } catch (NoSuchMethodException e) {
            throw new PnetRuntimeException("Unable to find constructor which has a single parameter of type 'HttpServletRequest'", e);
        } catch (InstantiationException e) {
            throw new PnetRuntimeException("Unable to instantiate a new " + className + " class.", e);
        } catch (IllegalAccessException e) {
            throw new PnetRuntimeException("Access violation.", e);
        } catch (InvocationTargetException e) {
            throw new PnetRuntimeException(e);
        }
    }
}

class HandlerMapBuilder extends DefaultHandler {
    private Map handlerMap = new HashMap();
    private HandlerMapEntry currentEntry;
    private boolean collectingClass = false;

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("Handler")) {
            currentEntry = new HandlerMapEntry();
            currentEntry.url = attributes.getValue("url");

            String viewTypeString = attributes.getValue("viewType");
            if (viewTypeString != null) {
                currentEntry.viewType = ViewType.getForID(viewTypeString);
            }


            if (Validator.isBlankOrNull(currentEntry.url)) {
                throw new PnetRuntimeException("Blank urls are not allowed in Handlers.");
            }
        } else if (qName.equalsIgnoreCase("Class") && currentEntry == null) {
            throw new PnetRuntimeException("Invalid XML Structure in HandlerMapping.xml.  Class entity" +
                "must be inside Handler entity");
        } else if (qName.equalsIgnoreCase("Class")) {
            collectingClass = true;
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("handler")) {
            handlerMap.put(currentEntry.url, currentEntry);
            currentEntry = null;
        } else if (qName.equalsIgnoreCase("class")) {
            collectingClass = false;
        }
    }

    public void characters(char ch[], int start, int length) throws SAXException {
        if (collectingClass) {
            currentEntry.className += new String(ch).substring(start, start+length);
        }
    }

    public Map getHandlerMap() {
        return handlerMap;
    }
}