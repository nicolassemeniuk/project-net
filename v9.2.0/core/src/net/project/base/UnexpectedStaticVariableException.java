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
 * An illegal state exception is thrown if a series of "if" statement
 * designed to test static veriables comes up with a value that we
 * hadn't been expecting.  This is most common in a case where someone
 * has added a new static veriable.
 *
 * @author Matthew Flower
 * @version 1.0
 */
public class UnexpectedStaticVariableException extends PnetRuntimeException {
    //No implementation is necessary.  This class is derived from the PnetRuntimeException
    //to make the error more specific.
    public UnexpectedStaticVariableException(String message) {
        super(message);
    }
}
