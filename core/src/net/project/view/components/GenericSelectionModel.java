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

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.util.AbstractSelectModel;

/**
 * @author jued
 *
 * @param <T>
 */
public class GenericSelectionModel<T> extends AbstractSelectModel {

       private String labelField;

       private List<T> list;

       private final PropertyAccess adapter;

       public GenericSelectionModel(List<T> list, String labelField, PropertyAccess adapter) {
               this.labelField = labelField;
               this.list = list;
               this.adapter = adapter;
       }

       public List<OptionGroupModel> getOptionGroups() {
               return null;
       }

       public List<OptionModel> getOptions() {
               List<OptionModel> optionModelList = new ArrayList<OptionModel>();
               for (T obj : list) {
                       if (labelField == null) {
                               optionModelList.add(new OptionModelImpl(obj + "",new String[0]));
                       } else {
                               optionModelList.add(new OptionModelImpl(adapter.get(obj, labelField)
                                           +"",new String[0]));
                       }
               }
               return optionModelList;
       }
}
