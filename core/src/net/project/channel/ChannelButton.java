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

 package net.project.channel;

/**
 * ChannelButton
 * Data structure for buttons displayable by a channel
 *
 * @author AdamKlatzkin    03/00
 */
public class ChannelButton implements java.io.Serializable {
    public String m_image = null;
    public String m_url = null;
    public String m_target = null;
    public String m_title = null;

    /**
     * Constructor
     *
     * @param image url to button's image (should be a 18x15 image)
     * @param link  url to button's link
     */
    public ChannelButton(String image, String link) {
        m_image = image;
        m_url = link;
    }

    public ChannelButton(String image, String link, String title) {
        m_image = image;
        m_url = link;
        m_title = title;
    }
}

