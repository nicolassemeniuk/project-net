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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.persistence;

import net.project.base.PnetRuntimeException;

/**
 * This class is designed to carry a PersistenceException across a method
 * boundary where the method signature cannot be redefined.
 *
 * For example, if an exception occurs in an implementation of
 * <code>java.util.Comparator</code>, you cannot throw a
 * <code>PersistenceException</code> from that location.  You know what code is
 * calling the comparator in the first place, but you need a way to get that
 * PersistenceException in that calling method.  This exception is a subclass
 * of <code>RuntimeException</code> so it has the ability to act as a Throwable
 * that does not need to be explicitly defined.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class PersistenceExceptionPropagator extends PnetRuntimeException {
    private PersistenceException originalException;

    /**
     * Standard constructor.  Note that this exception constructor does not
     * accept a message.  That is because this is not a real exception.  It is
     * only being used to propagate a message across a single method boundary.
     * This class should not be used to propagate an exception through many
     * method calls.
     *
     * @param original a <code>PersistenceException</code> that we are
     * propagating across code boundaries.
     */
    public PersistenceExceptionPropagator(PersistenceException originalException) {
        this.originalException = originalException;
    }

    /**
     * Get the original PersistenceException that we are propagating across
     * method boundaries.
     *
     * @return a <code>PersistenceException</code> that we are propagating
     * across method boundaries.
     */
    public PersistenceException getOriginalException() {
        return originalException;
    }
}
