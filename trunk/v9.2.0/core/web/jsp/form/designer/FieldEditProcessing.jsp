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
    contentType="text/html; charset=UTF-8"
    info="FieldEditProcessing.jsp  Omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="java.util.ArrayList,
            java.util.Iterator,
            net.project.base.property.PropertyProvider,
			net.project.security.*,
            net.project.form.*,
			net.project.form.property.IPropertySheet,
            net.project.form.property.CalculationFieldPropertySheet,
            java.util.List,
            net.project.base.PnetException"
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<jsp:useBean id="formDesigner" class="net.project.form.FormDesigner" scope="session" />
<jsp:useBean id="formFieldDesigner" class="net.project.form.FormFieldDesigner" scope="session" />
<jsp:useBean id="domainValue" class="net.project.form.FieldDomainValue" scope="session" />

<jsp:useBean id="formElementList" class="net.project.form.FormElementList" scope="page" />

<html>
<head>
<META http-equiv="expires" content="0"> 
<title><%=PropertyProvider.get("prm.form.designer.fieldeditprocessingpage.title")%></title>
</head>

<script>
	// Is letter key
	function isNumberKey(evt) {
	     var charCode = (evt.which) ? evt.which : evt.keyCode;
	     if (charCode > 31 && (charCode < 48 || charCode > 57)) {
	        return false;
	     } else {
	        return true;
	     }
	  }
</script>

<%!
		FieldDomain domain = null;
		FieldDomainValue domainValue = null;
		List domainValues = null;
%>

<%
	// Get the IPropertySheet from session
	IPropertySheet fieldPropertySheet = (IPropertySheet) pageContext.getAttribute("fieldPropertySheet", PageContext.SESSION_SCOPE);

    //Get the action from the request parameters (we were looking it up frequently before)
    String theAction = ( request.getParameter("theAction") != null ?
                         request.getParameter("theAction") :
                         "");

    if (theAction.equals("promote")) {
        // Make the domain value appear higher in the list
        domain = formFieldDesigner.getDomain();
		
        String domainValueIDToPromote = request.getParameter("fieldDomainValueID");

        for (Iterator it = domain.getValues().listIterator(); it.hasNext();) {
            domainValue = (FieldDomainValue)it.next();

            if (domainValueIDToPromote.equals(domainValue.getID())) {
                domainValue.promote();
            }
        }

        //Reloading will force all domain values to have the proper sequence
        domain.load();

        pageContext.forward("FieldEdit.jsp");

    } else if (theAction.equals("demote")) {
        // Make the domain value appear lower in the list
        domain = formFieldDesigner.getDomain();

        String domainValueIDToDemote = request.getParameter("fieldDomainValueID");

        for (Iterator it = domain.getValues().listIterator(); it.hasNext();) {
            domainValue = (FieldDomainValue)it.next();

            if (domainValueIDToDemote.equals(domainValue.getID()))
                domainValue.demote();
        }

        //Reloading will force all domain values to have the proper sequence
        domain.load();

        pageContext.forward("FieldEdit.jsp");

    } else if (theAction.equals("alphabetizeDomainValues")) {
        domain = formFieldDesigner.getDomain();
        domain.alphabetizeDomainValues();

        //Reloading will force all domain values to have the proper sequence
        domain.load();

        //Redirect back to the edit page
        pageContext.forward("FieldEdit.jsp");

    } else if (theAction.equals("change")) {
    	// User CHANGED the field type... redraw.
		formFieldDesigner.setElement(formElementList.getElementForDisplayClass(request.getParameter("ElementID")));
		fieldPropertySheet = formFieldDesigner.getPropertySheet();
		fieldPropertySheet.setSpace(formDesigner.getSpace());
		fieldPropertySheet.setUser(user);
		fieldPropertySheet.load();

		// Old attribute must be removed if we are to replace it with one of a possibly
		// different class
		pageContext.removeAttribute("fieldPropertySheet");
		pageContext.setAttribute("fieldPropertySheet", fieldPropertySheet, PageContext.SESSION_SCOPE);

		pageContext.forward("FieldEdit.jsp?fieldID=");

    } else {
	    // All other actions require a submit
        String additionalParameters = "";

		if(!("remove").equals(request.getParameter("opPair"))){
			// Process property sheet values and store
			fieldPropertySheet.processHttpPost(request);
			fieldPropertySheet.store();
		}

		if (formFieldDesigner.isNewField()) {
			// Add newly designed field to form
			formDesigner.addField(formFieldDesigner);
			formFieldDesigner.setIsNewField(false);

		} else {
			// Update existing field
			formDesigner.updateField(formFieldDesigner);

		}

		// store this field's domain info.
		if ((domain = formFieldDesigner.getDomain()) != null) {
			//domain.store();
			
			// store the user's domain value default selection.
			if ((domainValues = domain.getValues()) != null && domainValues.size() > 0)	{
				for (int i=0; i<domainValues.size(); i++) {					
					domainValue = (FieldDomainValue)domainValues.get(i);
					if ((request.getParameter("DefaultValue") != null) && 
						(request.getParameter("DefaultValue").equals(domainValue.getID())))
					{
						domainValue.setIsDefault(true);
						//domainValue.store();
					} else{
						domainValue.setIsDefault(false);
						//domainValue.store();						
					}
					 
				}
			}
			domain.store();
		}

		if (theAction.equals("create")) {
    		// Domain value ADD button
			// If this is the first domain field, create new domain.
			if (formFieldDesigner.getDomain() == null) {
				domain = new FieldDomain();
				domain.setForm(formDesigner);
				domain.setField(formFieldDesigner);
				domain.setName(formFieldDesigner.getFieldLabel() + " domain");
				domain.setType("user_domain");
				domain.store();
				formFieldDesigner.setDomain(domain);

			} else {
				domain = formFieldDesigner.getDomain();	
			}
			
			// Create the new domain value and store.
			domainValue = new FieldDomainValue();
			domainValue.clear();
			if ((request.getParameter("DomainValueName") != null) && !(request.getParameter("DomainValueName").equals(""))) {
				domainValue.setName(request.getParameter("DomainValueName"));
				domainValue.setDomain(domain);
				domainValue.store();
				domain.addValue(domainValue);
			}
					
			pageContext.forward("FieldEdit.jsp?fieldID=");

		} else if (theAction.equals("remove")) {
    		// Toolbar: REMOVE (remove the domain value).
            // Delete the domain value from the database
            if (request.getParameter("selected") != null && !request.getParameter("selected").equals("")){
				domainValue.setID(request.getParameter("selected"));
				domainValue.remove();

        	    //Reload the domain list to get rid of this value
            	domain.load();
            }
        } else if (theAction.equals("addOpPair")) {
            additionalParameters = "opPair=add";

        } else if (theAction.equals("removeOpPair")) {
            additionalParameters = "opPair=remove&orderID=" + request.getParameter("selected");

		} else if (theAction.equals("addOperand")) {
            additionalParameters = "addOperand=true";

		} else if (theAction.equals("addOperator")) {
            additionalParameters = "addOperator=true";

        } else if (theAction.equals("submit")) {
            // Back to fields list
			pageContext.forward("FieldsManager.jsp");

        } else {
            throw new PnetException("Unknown action " + theAction);
        }

        // Perform the appropriate forwarding action for all non-submit actions
        if (!theAction.equals("submit")) {
			if (formFieldDesigner.getID() != null) {
				pageContext.forward("FieldEdit.jsp?fieldID=" + formFieldDesigner.getID() + "&" + additionalParameters);
			} else {
				pageContext.forward("FieldEdit.jsp" + "?" + additionalParameters);
			}
		}

	}

%>
