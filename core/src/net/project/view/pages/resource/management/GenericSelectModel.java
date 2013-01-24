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
/**
 * 
 */
package net.project.view.pages.resource.management;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.ioc.services.PropertyAdapter;
import org.apache.tapestry5.util.AbstractSelectModel;

/** Generic selection model for a list of Objects.
 * use:
 * <pre>@Inject private PropertyAccess _access;</pre>
 * in your page to ge the {@link PropertyAccess} service.<br>
 * !Notice: you must set the created instance both as model and encoder parameter for the {@link Select} component.*/
public class GenericSelectModel<T> extends AbstractSelectModel implements ValueEncoder<T> {

    private PropertyAdapter labelFieldAdapter;
    private PropertyAdapter idFieldAdapter;
    private List<T>         list;

    public GenericSelectModel(List<T> list, Class<T> clazz, String labelField, String idField, PropertyAccess access) {
        this.list = list;
        if (idField != null)
            this.idFieldAdapter = access.getAdapter(clazz).getPropertyAdapter(idField);
        if (labelField != null)
            this.labelFieldAdapter = access.getAdapter(clazz).getPropertyAdapter(labelField);
    }

    public List<OptionGroupModel> getOptionGroups() {
        return null;
    }

    public List<OptionModel> getOptions() {
        List<OptionModel> optionModelList = new ArrayList<OptionModel>();
        if (labelFieldAdapter == null) {
            for (T obj : list) {
                optionModelList.add(new OptionModelImpl(nvl(obj), obj));
            }
        } else {
            for (T obj : list) {
                optionModelList.add(new OptionModelImpl(nvl(labelFieldAdapter.get(obj)), obj));
            }
        }
        return optionModelList;
    }

    // ValueEncoder functions
    public String toClient(T obj) {
        if (idFieldAdapter == null) {
            return obj + "";
        } else {
            return idFieldAdapter.get(obj) + "";
        }
    }

    public T toValue(String string) {
        if (idFieldAdapter == null) {
            for (T obj : list) {
                if (nvl(obj).equals(string)) return obj;
            }
        } else {
            for (T obj : list) {
                if (nvl(idFieldAdapter.get(obj)).equals(string)) return obj;
            }
        }
        return null;
    }

    private String nvl(Object o) {
        if (o == null)
            return "";
        else
            return o.toString();
    }
}
