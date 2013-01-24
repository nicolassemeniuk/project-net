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
function toggleShareBoxEnabled() {
    var enabled = (getSelectedValue(document.forms[0].permissionType) == 1);
    var shareTable = document.getElementById("shareTable");
    shareTable.disabled = !enabled;
    shareTable.setAttribute("disabled", !enabled);

    var childInputs = shareTable.getElementsByTagName("input");
    for (var i = 0; i < childInputs.length; i++) {
        childInputs[i].disabled = !enabled;
        childInputs[i].readonly = !enabled;
    }
}
function findSharingSpaces() {
    openwin_dialog('addspace', '<%=SessionManager.getJSPRootURL()%>/crossspace/FindSpaces.jsp?module=<%=Module.SCHEDULE%>', 300, 400, 1);
}
function findSharingUsers() {
    openwin_dialog('adduser', '<%=SessionManager.getJSPRootURL()%>/crossspace/FindUsers.jsp?module=<%=Module.SCHEDULE%>', 300, 400, 1);
}
function addSharingSpace(spaceid, spacename) {
    var spaceTable = document.getElementById("spaceTable");

    //If the "No spaces.." message is there, remove it
    var nospacesrow = document.getElementById("nospaces");
    if (nospacesrow)
        spaceTable.removeChild(nospacesrow);

    //Check to make sure this space hasn't already been added
    if (document.getElementById("space"+spaceid))
        return;

    var spaceRow = document.createElement("tr");
    spaceRow.id = "space"+spaceid;

    var nameTD = document.createElement("td");
    nameTD.className = "tableContent";
    nameTD.appendChild(document.createTextNode(spacename));
    spaceRow.appendChild(nameTD);

    var actionTD = document.createElement("td");
    var aObj = document.createElement("a");
    aObj.href="javascript:removeSpace('"+spaceid+"')";
    var removeImg = document.createElement("img");
    removeImg.src = "/images/icons/toolbar-gen-remove_on.gif";
    removeImg.border = "0";
    aObj.appendChild(removeImg);
    actionTD.appendChild(aObj);

    //Add an hidden form input to let the processing page know to about this space
    var hiddenInput = document.createElement("input");
    hiddenInput.type = "hidden";
    hiddenInput.name = "shareSpaceID";
    hiddenInput.value = spaceid;
    actionTD.appendChild(hiddenInput);
    spaceRow.appendChild(actionTD);
    spaceTable.appendChild(spaceRow);
}
function addSharingUser(userid, display) {
    var userTable = document.getElementById("userTable");

    //If the "No users have been added" message is there, remove it.
    var nousersrow = document.getElementById("nousers");
    if (nousersrow)
        userTable.removeChild(nousersrow);

    //If this user has been added, don't add them again, bail out
    if (document.getElementById("user"+userid))
        return;

    var userRow = document.createElement("tr");
    userRow.id = "user"+userid;

    var displayTD = document.createElement("td");
    displayTD.className = "tableContent";
    displayTD.appendChild(document.createTextNode(display));
    userRow.appendChild(displayTD);

    var actionTD = document.createElement("td");
    var aObj = document.createElement("a");
    aObj.href="javascript:removeUser('"+userid+"')";
    var removeImg = document.createElement("img");
    removeImg.src = "/images/icons/toolbar-gen-remove_on.gif";
    removeImg.border = "0";
    aObj.appendChild(removeImg);
    actionTD.appendChild(aObj);

    //Add an hidden form input to let the processing page know to about this user
    var hiddenInput = document.createElement("input");
    hiddenInput.type = "hidden";
    hiddenInput.name = "shareUserID";
    hiddenInput.value = userid;
    actionTD.appendChild(hiddenInput);
    userRow.appendChild(actionTD);
    userTable.appendChild(userRow);
}
function removeSpace(spaceID) {
    document.getElementById("spaceTable").removeChild(document.getElementById("space"+spaceID));
}
function removeUser(userID) {
    document.getElementById("userTable").removeChild(document.getElementById("user"+userID));
}
