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
package net.project.security;

import java.util.Collections;
import java.util.List;

public class MultiAccessResults {
    private List failedIDList;
    private boolean success;

    public MultiAccessResults(List failedIDList, boolean success) {
        this.failedIDList = failedIDList;
        this.success = success;
    }

    public boolean isSuccessful() {
        return success;
    }

    public List getFailedIDList() {
        if (failedIDList == null) {
            return Collections.EMPTY_LIST;
        } else {
            return failedIDList;
        }
    }

    void setFailedIDList(List failedIDList) {
        this.failedIDList = failedIDList;
    }
}
