<%--
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
--%>

<%@ page
    info=""
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.base.property.PropertyProvider,
            net.project.xml.XMLFormatter,
            net.project.crossspace.ObjectListGenerator,
            net.project.base.Module,
            net.project.security.Action"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="objectListGenerator" class="net.project.crossspace.ObjectListGenerator" scope="request"/>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:import type="css" src="/styles/schedule.css" />
<template:getSpaceCSS />

<%-- Import Javascript --%>

<script language="javascript" type="text/javascript">
var theForm;

function setup() {
    theForm = self.document.forms[0];
}

function cancel() {
    history.back();
}
function submit() {
    if(validateAction())
        theForm.submit();
}

function changeCopyMoveEnabled(actionsAllowed) {
    var shareObject = document.getElementById("shareObject");
    //var shareObjectReadOnly = document.getElementById("shareObjectReadOnly");
    //var moveObject = document.getElementById("moveObject");
    var copyObject = document.getElementById("copyObject");
    theForm.moveCopyShare.selectedIndex = -1;

    shareObject.disabled = ((actionsAllowed & 1) == 0);
    //shareObjectReadOnly.disabled = ((actionsAllowed & 2) == 0);
    copyObject.disabled = ((actionsAllowed & 4) == 0);
    //moveObject.disabled = ((actionsAllowed & 8) == 0);

    var selectedValue = getSelectedValue(theForm.moveCopyShare);

    if ((shareObject.value == selectedValue && shareObject.disabled) ||
        /*(shareObjectReadOnly.value == selectedValue && shareObjectReadOnly.disabled) ||*/
        (copyObject.value == selectedValue && copyObject.disabled) /*||
        (moveObject.value == selectedValue && moveObject.disabled)*/) {
        selectFirstNotDisabled();
    }
}

function selectFirstNotDisabled() {
    var shareObject = document.getElementById("shareObject");
    var copyObject = document.getElementById("copyObject");

    if (shareObject && !shareObject.disabled) {
        shareObject.checked = true;
    } /*else if (shareObjectReadOnly && !shareObjectReadOnly.disabled) {
        shareObjectReadOnly.checked = true;
    }*/ else if (copyObject && !copyObject.disabled) {
        copyObject.checked = true;
    } /*else if (moveObject && !moveObject.disabled) {
    }*/
}

function validateAction() {
    var shareObject = document.getElementById("shareObject");
    var copyObject = document.getElementById("copyObject");
    if(shareObject.disabled && copyObject.disabled) {
    	var errorMessage = '<%=PropertyProvider.get("prm.crossspace.addexternal.allowableactions.alert")%>';
	    extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
        return false;
    }
    return true;
}

function shareChanged() {
    <%-- Disabling read-only Action using the bit mask. 1+4+8=13 --%>
    <%-- Read-only task share not supported yet. --%>
    <%-- var copyMoveEnabled = 15; --%>
	var copyMoveEnabled = 13;
	
    if (theForm.plan) {
        if (theForm.plan.length) {
            //There is more than one selection
            for (var i = 0; i < theForm.plan.length; i++) {
                if (theForm.plan[i].checked) {
                    copyMoveEnabled = copyMoveEnabled & parseInt(theForm.plan[i].getAttribute("actionsAllowed"));
                }
            }
        } else {
            if (theForm.plan.checked) {
                copyMoveEnabled = parseInt(theForm.plan.getAttribute("actionsAllowed"));
            }
        }
    }

    if (theForm.task) {
        if (theForm.task.length) {
            for (var i = 0; i < theForm.task.length; i++) {
                if (theForm.task[i].checked) {
                    copyMoveEnabled = copyMoveEnabled & parseInt(theForm.task[i].getAttribute("actionsAllowed"));
                }
            }
        } else {
            if (theForm.task.checked) {
                copyMoveEnabled = copyMoveEnabled & parseInt(theForm.task.getAttribute("actionsAllowed"));
            }
        }
    }

    changeCopyMoveEnabled(copyMoveEnabled);
}

function expand(event, rowNumber, depth) {
    if (event != "def") {
        rowNumber = this.id.substring(3, this.id.length-5);
    }
    if (depth == undefined) {
        depth = 0;
    }

    var row = document.getElementById("row"+rowNumber);
    var thisRowShown = false;

    if (row.getAttribute("visibleState") == "visible" || depth < 2) {
        thisRowShown = true;
        var children = row.getAttribute("childRows");
        var childrenShown = false;

        if (children) {
            var rowsToShow = children.split(",");

            depth++;
            for (var i = 0; i < rowsToShow.length; i++) {
                childrenShown = expand("def", rowsToShow[i], depth);
            }
        }

        row.className = "tableContent";

        var expanderImage = document.getElementById("row"+rowNumber+"Image");
        if (expanderImage != null && childrenShown == true) {
            expanderImage.onclick = collapse;
            expanderImage.src = "<%= SessionManager.getJSPRootURL() %>/images/unexpand.gif";
        }
    } else {
        thisRowShown = false;
    }

    return thisRowShown;
}

function collapse(event, rowNumber, hide) {
    if (event != "def") {
        rowNumber = this.id.substring(3, this.id.length-5);
        hide=false;
    }

    var hideThisRow = (hide);
    var row = document.getElementById("row"+rowNumber);
    var children = row.getAttribute("childRows");

    if (children) {
        var rowsToHide = children.split(",");

        for (var i = 0; i < rowsToHide.length; i++) {
            collapse("def", rowsToHide[i], true);
        }
    }

    //If we expand this row, we want to keep its expanded state just as it was
    //before
    if (row.className == "hidden") {
        row.setAttribute("visibleState", "hidden");
    } else {
        row.setAttribute("visibleState", "visible");
    }

    if (hideThisRow) {
        row.className = "hidden";
    }

    var expanderImage = document.getElementById("row"+rowNumber+"Image");
    if (expanderImage != null) {
        expanderImage.onclick = expand;
        expanderImage.src = "<%= SessionManager.getJSPRootURL() %>/images/expand.gif";
    }
}
</script>

</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" groupTitle="all.global.toolbar.standard.share">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display="Add External Link"
					jspPage='<%=SessionManager.getJSPRootURL() + "/crossspace/AddExternal.jsp" %>'
					queryString='<%="module="+Module.SCHEDULE+"&action="+Action.SHARE%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard" showAll="true">
	</tb:band>
</tb:toolbar>

<div id='content'>

<br>

<form method="post" action="<%=SessionManager.getJSPRootURL()+request.getAttribute("processing")%>">

<table border="0" width="97%">
    <tr><td><table border="0" cellpadding="0" cellspacing="0" width="100%">
        <tr class="channelHeader">
            <td width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></td>
            <td nowrap="true" class="channelHeader" colspan="4"><display:get name="prm.crossspace.addexternal.selectobjectstoinclude.label"/></td>
            <td align="right" width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></td>
        </tr>
    </table></td></tr>
    <tr>
        <td colspan="2" style="padding-left:10px">
<%
    XMLFormatter xmlFormat = new XMLFormatter();
    xmlFormat.setStylesheet("/crossspace/xsl/objectlist.xsl");
    out.print(xmlFormat.getPresentation(objectListGenerator.getXML()));
%>
        </td>
    </tr>

    <tr>
        <td>
            <table border="0" cellpadding="0" cellspacing="0" width="100%">
            <tr class="channelHeader">
                <td width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"></td>
                <td nowrap class="channelHeader" colspan="4"><display:get name="prm.crossspace.addexternal.action.label"/></td>
                <td align="right" width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"></td>
            </tr>
            </table>
        </td>
    </tr>
    <tr>
        <td class="tableContent" valign="top" style="padding-left:10px">
            <input type="radio" name="moveCopyShare" id="shareObject" value="share" checked><label for="shareObject"><display:get name="prm.crossspace.addexternal.shareobjects.label"/></label><br>
            <input type="radio" name="moveCopyShare" id="copyObject" value="copy"><label for="copyObject"><display:get name="prm.crossspace.addexternal.copyobjects.label"/></label><br>
        </td>
    </tr>
</table>

<tb:toolbar style="action" showLabels="true" width="97%" bottomFixed="true">
    <tb:band name="action">
            <tb:button type="submit"/>
            <tb:button type="cancel"/>
    </tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
