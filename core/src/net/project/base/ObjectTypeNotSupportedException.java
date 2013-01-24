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

 package net.project.base;

/**
 * <code>ObjectTypeNotSupportedException</code> is a runtime, unchecked error 
 * that should be thrown when a generic object type is passed to a method, but
 * that object type was not designed to be used in that context.
 *
 * Any time that you are considering using this error, you should double check
 * your object-orientation - it is probably wrong.
 *
 * @author Matthew Flower (11/21/2001)
 * @extends PnetRuntimeException
 * @since Gecko
 */
public class ObjectTypeNotSupportedException extends PnetRuntimeException {
    /**
     * Construct a <code>ObjectTypeNotSupportedException</code> with no
     * detail message.
     */
    public ObjectTypeNotSupportedException() {
    }

    /**
     * Construct a <code>ObjectTypeNotSupportedException</code> with a
     * specific detail message.
     *
     * @param message The detailed message
     */
    public ObjectTypeNotSupportedException(String message) {
    }
}
