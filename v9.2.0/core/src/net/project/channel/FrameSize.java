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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.project.base.property.PropertyProvider;
import net.project.gui.html.IHTMLOption;

/**
 * Provides an enumeration of FrameSize values for selecting the
 * size of a scrolling frame in which to include channel contents.
 *
 * @since Version 7.6.3
 * @author Tim Morrow
 */
public class FrameSize implements IHTMLOption, Serializable {

    //
    // Static Members
    //

    /**
     * All the frame sizes.
     * Each key is a <code>String</code> internal ID and each
     * element is a <code>FrameSize</code>.
     */
    private static final Map allFrameSizes = new HashMap();

    /** Small size, currently 250 pixels. */
    public static final FrameSize SMALL = new FrameSize("0", "small", "prm.channel.framesize.small.label", 100);

    /** Medium size, currently 500 pixels. */
    public static final FrameSize MEDIUM = new FrameSize("1", "medium", "prm.channel.framesize.medium.label", 200);

    /** Large size, currently 750 pixels. */
    public static final FrameSize LARGE = new FrameSize("2", "large", "prm.channel.framesize.large.label", 400);

    /**
     * Returns the Frame Size for the specified ID.
     * @param id the ID of the frame size to get
     * @return the frame size
     * @throws IllegalArgumentException if no FrameSize is found for that ID
     */
    static FrameSize forID(String id) {
        FrameSize frameSize = (FrameSize) FrameSize.allFrameSizes.get(id);
        if (frameSize == null) {
            throw new IllegalArgumentException("No frameSize found for ID " + id);
        }
        return frameSize;
    }

    /**
     * Returns an ordered, unmodifiable collection of all FrameSizes.
     * @return a collection where each element is a <code>FrameSize</code>
     */
    static Collection getAll() {
        List allElements = new ArrayList(allFrameSizes.values());
        Collections.sort(allElements, new IDComparator());
        return Collections.unmodifiableList(allElements);
    }

    //
    // Instance Members
    //

    /** The internal ID of the frame size. */
    private final String frameSizeID;

    /** The internal name of the frame size. */
    private final String internalName;

    /** THe token to use for the dsipaly name of the frame size. */
    private final String displayToken;

    /** The height, in pixels, of the frame. */
    private final int height;

    /**
     * Creates a new FrameSize.
     * @param frameSizeID the internal ID
     * @param internalName the internal Name
     * @param displayToken the token to use for displaying the name
     * @param height the height, in pixels
     */
    private FrameSize(String frameSizeID, String internalName, String displayToken, int height) {
        this.frameSizeID = frameSizeID;
        this.internalName = internalName;
        this.displayToken = displayToken;
        this.height = height;
        FrameSize.allFrameSizes.put(frameSizeID, this);
    }

    /**
     * Returns the internal ID of this FrameSize.
     * @return the internal ID
     */
    String getID() {
        return this.frameSizeID;
    }

    /**
     * Returns the display name of this FrameSize.
     * @return the display name
     */
    String getDisplayName() {
        return PropertyProvider.get(this.displayToken);
    }

    /**
     * Returns the height of this FrameSize, in pixels.
     * @return the height
     */
    int getHeight() {
        return this.height;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FrameSize)) {
            return false;
        }

        final FrameSize frameSize = (FrameSize) o;

        if (!frameSizeID.equals(frameSize.frameSizeID)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return frameSizeID.hashCode();
    }

    /**
     * Provides a string representation of this FrameSize
     * suitable for debugging only.
     * @return the string representation
     */
    public String toString() {
        return this.frameSizeID + " " + this.internalName + " (" + height + ")";
    }

    public String getHtmlOptionValue() {
        return getID();
    }

    public String getHtmlOptionDisplay() {
        return getDisplayName();
    }

    //
    // Nested top-level classes
    //

    /**
     * Provides a comparator of FrameSizes based on ID.
     */
    private static class IDComparator implements Comparator {

        /**
         * Compares to FrameSize objects based on their IDs.
         * @param o1 the first framesize
         * @param o2 the second framesize
         * @return the result of comparing IDs
         */
        public int compare(Object o1, Object o2) {
            if (o1 == null || o2 == null) {
                throw new NullPointerException("Missing argument to compare");
            }
            FrameSize frameSize1 = (FrameSize) o1;
            FrameSize frameSize2 = (FrameSize) o2;

            return (frameSize1.getID().compareTo(frameSize2.getID()));
        }
    }
}
