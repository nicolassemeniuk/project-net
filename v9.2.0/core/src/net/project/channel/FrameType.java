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
|     $RCSfile$
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.channel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.project.base.property.PropertyProvider;

public class FrameType implements Serializable {

    /**
     * All the frame types.
     * Each key is a <code>String</code> internal ID and each
     * element is a <code>FrameType</code>.
     */
    private static final Map allFrameTypes = new HashMap();

    /** Inline frame type. */
    public static final FrameType INLINE = new FrameType("0", "inline", "prm.channel.frametype.inline.label");

    /** Scrolling frame type. */
    public static final FrameType SCROLL = new FrameType("1", "scroll", "prm.channel.frametype.scroll.label");

    /**
     * Returns the Frame Type for the specified ID.
     * @param id the ID of the frame type to get
     * @return the frame type
     * @throws IllegalArgumentException if no FrameType is found for that ID
     */
    static FrameType forID(String id) {
        FrameType frameType = (FrameType) FrameType.allFrameTypes.get(id);
        if (frameType == null) {
            throw new IllegalArgumentException("No frameType found for ID " + id);
        }
        return frameType;
    }

    /** The ID of the frame type. */
    private final String frameTypeID;

    /** The internal name of the frame type. */
    private final String internalName;

    /** The token to use for the display name. */
    private final String displayToken;

    /**
     * Creates a new FrameType.
     * @param frameTypeID the unique ID of the frame type
     * @param internalName the internal name of the frame type
     * @param displayToken the token to use for the display name
     */
    private FrameType(String frameTypeID, String internalName, String displayToken) {
        this.frameTypeID = frameTypeID;
        this.internalName = internalName;
        this.displayToken = displayToken;
        FrameType.allFrameTypes.put(frameTypeID, this);
    }

    public String getID() {
        return this.frameTypeID;
    }

    public String getDisplayName() {
        return PropertyProvider.get(this.displayToken);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FrameType)) {
            return false;
        }

        final FrameType frameType = (FrameType) o;

        if (!frameTypeID.equals(frameType.frameTypeID)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return frameTypeID.hashCode();
    }

    public String toString() {
        return this.internalName;
    }

}
