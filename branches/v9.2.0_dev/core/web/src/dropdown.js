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
/*
* Variable JSPRootURL should be declared before including this JS file,
* for getting context. This change is for making project application context neutral. 
*/
function toggleFilterExpansion(imageID, openDivID, closedDivID, showClosedWhenOpen) {
    var filterExpandImage = document.getElementById(imageID);
    var openDiv = document.getElementById(openDivID);
    var closedDiv = document.getElementById(closedDivID);

    if (filterExpandImage.src.indexOf('unexpand.gif')>-1) {
        filterExpandImage.src = JSPRootURL+'/images/expand.gif';
        closedDiv.className = 'visible';
        openDiv.className = 'hidden';
    } else {
        filterExpandImage.src = JSPRootURL+'/images/unexpand.gif';
        closedDiv.className = (showClosedWhenOpen ? 'visible' : 'hidden');
        openDiv.className = 'visible';
    }
}

