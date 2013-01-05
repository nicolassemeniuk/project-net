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

 package net.project.taglibs.template;

/**
  * Template tag page parameter. Encapsulates name, content as whether content
  * is to be directly inserted or indirectly included.
  */
public class PageParameter {
    private String name = null;
    private String content = null;
    private boolean isDirect = false; 

    public PageParameter(String name, String content, boolean isDirect) {
        setName(name);
        setContent(content);
        setDirect(isDirect);
    } 

    public void setName(String name) {
        this.name = name;
    }
    public void setContent(String content) {
        this.content = content;
    } 
    public void setDirect(boolean isDirect) {
        this.isDirect = isDirect;
    } 

    public String getName() {
        return this.name;
    }
    public String getContent() {
        return content;
    } 
    public boolean isDirect() {
        return isDirect;
    } 

} 
