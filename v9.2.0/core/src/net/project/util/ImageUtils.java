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
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;

/**
 * Utility functions to manipulate images.
 *
 * @author Matthew Flower
 * @since 7.4
 */
public class ImageUtils {
    /**
     * Converted a BufferedImage into an array of bytes.
     *
     * @param image a <code>BufferedImage</code> that we want to convert into
     * an array of bytes.
     * @param imageFormat a <code>String</code> values that contains a string 
     * representation of the format we wish to write this image out into.  The
     * list of valid image types can be found in {@link javax.imageio.ImageIO#getReaderFormatNames}.
     */
    public static byte[] bufferedImageToByteArray(BufferedImage image, String imageFormat) throws IOException {
        ImageWriter writer = (ImageWriter)ImageIO.getImageWritersByFormatName(imageFormat).next();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MemoryCacheImageOutputStream mcios = new MemoryCacheImageOutputStream(baos);
        writer.setOutput(mcios);
        writer.write(image);
        mcios.close();
        
        return baos.toByteArray();
    }
}

