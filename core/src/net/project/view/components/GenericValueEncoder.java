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
package net.project.view.components;

import java.util.List;

import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.ioc.services.PropertyAccess;

public class GenericValueEncoder<T> implements ValueEncoder<T> {

       private List<T> list;
       private final PropertyAccess access;
       private final String fieldName;

       public GenericValueEncoder(List<T> list, String fieldName, PropertyAccess propertyAccess) {
               this.list = list;
               this.fieldName = fieldName;
               this.access = propertyAccess;
       }

       public String toClient(T obj) {
               if (fieldName == null) {
                       return obj + "";
               } else {
                       return access.get(obj,fieldName)+"";
               }
       }

       public T toValue(String string) {
               for (T obj : list) {
                       if (fieldName == null) {
                               if ((obj + "").equals(string)) {
                                       return obj;
                               }
                       } else {
                               if (access.get(obj, fieldName).equals(string)) {
                                       return obj;
                               }
                       }
               }
               return null;
       }
}
