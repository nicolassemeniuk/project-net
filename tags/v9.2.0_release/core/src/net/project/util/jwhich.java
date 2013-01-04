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

 package net.project.util;

import java.io.File;
import java.util.Date;
import java.util.Enumeration;

public class jwhich {

    public static final char DOT = '.';
    public static final char SLASH = '/';

  public static void main(String[] args) {
          if (!(args.length==1))
          {
            System.out.println("usage:");
          }
          else
          {
            System.out.println("Searching for " + args[0] + "...\n");
        }

          System.out.println (toString (args[0]));
  }

  public static String toString (String className) {

      StringBuffer results = new StringBuffer();

      try {

          Enumeration pathList = getClasspathEnumeration (className);
      
          int counter = 0;
          while ( pathList.hasMoreElements() ) {
              counter++;
              results.append (counter + ": " + pathList.nextElement().toString());
          }
      } catch (java.io.IOException ioe) {
               // do nothing
      }

      return results.toString();
  }

  public static String getClasspathXML (String className) {

      StringBuffer xml = new StringBuffer();

      xml.append ("<ClasspathXML>");

      try {

          Enumeration pathList = getClasspathEnumeration (className);
      
          int counter = 0;
          while ( pathList.hasMoreElements() ) {
              String classPathLocation = pathList.nextElement().toString();
              if (classPathLocation.startsWith("file:/")) {
                  classPathLocation = classPathLocation.substring(6);
              }
              if (classPathLocation.startsWith("jar:file:/")) {
                  classPathLocation = classPathLocation.substring(10);
              }

              File file = new File(classPathLocation);
              String fileSize = NumberFormat.getInstance().formatNumber(file.length());
              String lastModified = DateFormat.getInstance().formatDateTime(new Date(file.lastModified()));
              counter++;

              xml.append("<ClassPathEntry>");
              xml.append("<Seq>").append(counter).append("</Seq>");
              xml.append("<Location>").append(classPathLocation).append("</Location>");
              xml.append("<FileSize>").append(fileSize).append("</FileSize>");
              xml.append("<LastModified>").append(lastModified).append("</LastModified>");
              xml.append("</ClassPathEntry>");
          }
      } catch (java.io.IOException ioe) {
               // do nothing
      }

      xml.append ("</ClasspathXML>");

      return xml.toString();
  }

  public static Enumeration getClasspathEnumeration (String className) throws java.io.IOException {

      Enumeration classPathEnumeration =  null;
      String classLookupString = (className != null) ? className.replace(DOT, SLASH) : null;

      // first append the ".class" to the end of the lookup string
      classLookupString += ".class";
      // next get an enumeration of all the locations of the specified classname in the applications classpath
      classPathEnumeration = ClassLoader.getSystemResources (classLookupString);

      return classPathEnumeration;
  }

}

