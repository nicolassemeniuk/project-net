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

 package net.project.form;


/**
    General exceptions for the form package.
*/
public class FormException extends net.project.base.PnetException
{
    
    /**
        Constructs an FormException
    */
    public FormException()
    {
        super();
    }
    
    
    /**
        Constructs an FormException with the specified detail message
        @param message detailed message
    */
    public FormException(String message)
    {
        super(message);
    }
    
    /**
        Constructs an FormException with the specified detail message and severity.
        @param message detailed message
        @param severity ignored
        @deprecated As of 7.6.3; Use {@link #FormException(String)} instead
    */
    public FormException(String message, String severity)
    {
        super(message);
    }

    public FormException(String message, Throwable cause) {
        super(message, cause);
    }

    public FormException(Throwable cause) {
        super(cause);
    }

}



