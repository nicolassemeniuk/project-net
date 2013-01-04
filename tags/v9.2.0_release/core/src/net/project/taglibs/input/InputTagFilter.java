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

 package net.project.taglibs.input;

import java.util.Map;

/**
 * An input tag filter allows you to modify attributes of an input tag by using
 * a programmatic entity.  This can ease situations where a lot of programmatic
 * decisions would need to be made in order to set an attribute.
 *
 * An example of this is the disabled flag on fields on the TaskEdit.jsp page.
 * You need to know a combination of whether you are editing a summary task,
 * whether autocalculate is turned on, and whether the task is shared from
 * another project to decide if some of the fields are going to be enabled.
 * This can make the page a bit of a mess.  This class attempts to help with that
 * problem.
 *
 * @author Matthew Flower
 * @since Version 8.2.0
 */
public interface InputTagFilter {
    public void filter(Map attributeValueMap);
}
