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
package net.project.methodology.mvc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.base.property.PropertyProvider;
import net.project.database.DatabaseUtils;
import net.project.hibernate.model.PnMethodologySpace;
import net.project.hibernate.model.PnModule;
import net.project.hibernate.model.PnModuleCheckboxModel;
import net.project.hibernate.service.ServiceFactory;
import net.project.methodology.MethodologySpace;
import net.project.methodology.MethodologySpaceBean;
import net.project.project.DomainListBean;
import net.project.security.SecurityProvider;
import net.project.security.User;
import net.project.space.Space;
import net.project.space.SpaceTypes;
import net.project.util.Conversion;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class MethodologyController extends MultiActionController {

	public ModelAndView initMethodology(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		Map<String, String> model = new HashMap<String, String>();
		return new ModelAndView("/tiles_methodology_list", "model", model);
	}

	@SuppressWarnings("unchecked")
	public ModelAndView handleMain(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		Map model = new HashMap();
		try {
			String id = request.getParameter("id");
			if(id != null){
	            PnMethodologySpace methodology = ServiceFactory.getInstance().getPnMethodologySpaceService().getMethodologySpace(Integer.valueOf(id));
	            if(methodology.getIsUsed() != null && methodology.getIsUsed() == 1){
	            	request.getSession().setAttribute("templateUsed", true);
	            }else{
	            	request.getSession().setAttribute("templateUsed", false);
	            }
			}
			SecurityProvider securityProvider = (SecurityProvider) request.getSession().getAttribute("securityProvider");
			MethodologySpaceBean methodologySpace = (MethodologySpaceBean) request.getSession().getAttribute("methodologySpace");
			User user = (User) request.getSession().getAttribute("user");
			if (id != null) {
				// Security Check: Is user allowed access to requested space?

				MethodologySpace testSpace = new MethodologySpace();
				testSpace.setID(id);
				Space oldSpace = securityProvider.getSpace();
				securityProvider.setSpace(testSpace);
				if (securityProvider.isActionAllowed(null, Integer.toString(net.project.base.Module.METHODOLOGY_SPACE), net.project.security.Action.VIEW)) {
					// Passed Security Check
					methodologySpace.setID(id);
					methodologySpace.load();

				} else {
					// No access to space. Check to see if user has access to parent space.
					// Perhaps we should make this the _ONLY_ security check. However,
					// not sure if there is a scenario under which someone doesn't have access
					// to the parent space but does to the Methodology Space
					testSpace.load();
					Space parentSpace = testSpace.getParentSpace();
					securityProvider.setSpace(parentSpace);
					if (securityProvider.isActionAllowed(null, "" + net.project.base.Module.METHODOLOGY_SPACE, net.project.security.Action.VIEW)) {
						methodologySpace.setID(id);
						methodologySpace.load();
					} else {
						// Failed Security Check
						securityProvider.setSpace(oldSpace);
						throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.methodology.security.validationfailed.message"), testSpace);
					}
				}

			} else {
				if (methodologySpace.getID() != null) {
					methodologySpace.load();
				}
			}
			model.put("methodologySpace", methodologySpace);
			model.put("methodologySpaceName", methodologySpace.getName());
			model.put("methodologyBasedOnSpaceType", methodologySpace.getBasedOnSpace() != null ?
					methodologySpace.getBasedOnSpace().getSpaceType().getName() : "");
			model.put("methodologySpaceIsGlobal", Conversion.booleanToString(methodologySpace.isGlobal()));
			model.put("methodologySpaceDescription", methodologySpace.getDescription());
			model.put("methodologySpaceId", methodologySpace.getID());
			model.put("methodologySpaceParentSpaceName", methodologySpace.getParentSpaceName());
			model.put("methodologySpaceUseScenario", methodologySpace.getUseScenario());
			model.put("userCurrentSpaceId", user.getCurrentSpace().getID());
			request.setAttribute("cbModules", getCheckboxModel(methodologySpace.getModules(), methodologySpace.getBasedOnSpace()));
			request.setAttribute("id", "");
			request.setAttribute("module", Integer.toString(net.project.base.Module.METHODOLOGY_SPACE));
			request.getSession().setAttribute("methodologyModule", model);
			// Set user's current space to this Methodology
			// user.setCurrentSpace(methodologySpace);

			String forwardTo = request.getParameter("page");
			if (forwardTo != null) {
				return new ModelAndView(forwardTo, "model", model);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ModelAndView("/tiles_methodology_main", "model", model);

	}

	public ModelAndView handleEdit(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		Map<String, String> model = new HashMap<String, String>();

		try {
			User user = (User) request.getSession().getAttribute("user");
			MethodologySpaceBean methodologySpace = (MethodologySpaceBean) request.getSession().getAttribute("methodologySpace");
			DomainListBean projectDomainList = (DomainListBean) request.getSession().getAttribute("projectDomainList");

			String id = request.getParameter("id");
			methodologySpace.setID( id == null ? user.getCurrentSpace().getID() : id );
			//methodologySpace.setID(user.getCurrentSpace().getID());
			methodologySpace.load();

			model.put("userCurrentSpaceId", user.getCurrentSpace().getID());
			model.put("userId", user.getID());

			if (projectDomainList == null) {
				projectDomainList = new DomainListBean();
			}

			model.put("availableBusinessOptionList", projectDomainList.getAvailableBusinessOptionList(user.getID(), methodologySpace.getParentSpaceID(), null));

			model.put("methodologySpaceIsGlobal", Conversion.booleanToString(methodologySpace.isGlobal()));
			model.put("methodologySpaceId", methodologySpace.getID());
			model.put("methodologySpaceName", methodologySpace.getName());
			model.put("methodologySpaceUseScenario", methodologySpace.getUseScenario());
			model.put("methodologySpaceDescription", net.project.util.HTMLUtils.escape(methodologySpace.getDescription()));			
			request.setAttribute("cbModules", getCheckboxModel(methodologySpace.getModules(), methodologySpace.getBasedOnSpace()));
	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ModelAndView("/tiles_methodology_edit", "model", model);
	}

	public void handleUpdate(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		Map<String, String> model = new HashMap<String, String>();
		try {
			
			User user = (User) request.getSession().getAttribute("user");
			MethodologySpaceBean methodologySpace = (MethodologySpaceBean) request.getSession().getAttribute("methodologySpace");

			methodologySpace.setUser(user);
			if ((request.getParameter("methodologySpaceDescription") == null) || (request.getParameter("methodologySpaceDescription").equals(""))) {
				methodologySpace.setDescription("");
			} else {
				methodologySpace.setDescription(request.getParameter("methodologySpaceDescription"));
			}
			methodologySpace.setName(request.getParameter("methodologySpaceName"));
			methodologySpace.setGlobal(request.getParameter("isGlobal"));
			methodologySpace.setParentSpaceID(request.getParameter("parentSpaceID"));
			methodologySpace.setUseScenario(request.getParameter("useScenario"));
			methodologySpace.setSelectedModulesStringArray(request.getParameterValues("selectedModules"));
			methodologySpace.store();

			model.put("methodologySpaceName", methodologySpace.getName());
			model.put("methodologySpaceDescription", methodologySpace.getDescription());
			model.put("methodologySpaceId", methodologySpace.getID());
			model.put("methodologySpaceParentSpaceName", methodologySpace.getParentSpaceName());
			model.put("methodologySpaceUseScenario", methodologySpace.getUseScenario());
			model.put("userCurrentSpaceId", user.getCurrentSpace().getID());

			request.setAttribute("action", "" + net.project.security.Action.VIEW);
		
			response.sendRedirect(net.project.security.SessionManager.getJSPRootURL()+"/methodology/Main.htm?id="+methodologySpace.getID());
		} catch (Exception e) {
			e.printStackTrace();
		}
		//return new ModelAndView("/tiles_methodology_main", "model", model);
	}

	private TreeSet<PnModuleCheckboxModel> getCheckboxModel(List<PnModule> pnModules, Space basedOnSpace) {
		int[] projectmodules = new int[]{Module.SECURITY, Module.DOCUMENT, Module.PROCESS, Module.WORKFLOW, Module.DISCUSSION, Module.FORM, Module.SCHEDULE};
		int[] businessmodules = new int[]{Module.SECURITY, Module.DOCUMENT, Module.WORKFLOW, Module.DISCUSSION, Module.FORM};
		int[] imodules;
		String basedOnspaceType = basedOnSpace != null ? basedOnSpace.getType() : SpaceTypes.BUSINESS_SPACE;	// NOTE: hardcodding that if basedOnSpace is not specified(legacy cod) than set show only Business related modules
		
		if (basedOnspaceType == null || basedOnspaceType.equals(SpaceTypes.BUSINESS_SPACE)) {
			imodules = businessmodules;
		} else {
			imodules = projectmodules;
		}
		TreeSet<PnModuleCheckboxModel> cbModules = new TreeSet<PnModuleCheckboxModel>(new Comparator<PnModuleCheckboxModel>() {
		    // Comparator interface requires defining compare method.
		    public int compare(PnModuleCheckboxModel moda, PnModuleCheckboxModel modb) {
		        //... Sort in alphabetical ignoring case.
		        if (moda == null || modb == null || moda.getModule() == null || modb.getModule() == null || moda.getModule().getDescription() == null || modb.getModule().getDescription() == null) {
		            return -1;
		        } else if (moda.equals(modb)) {
		            return 1;
		        } else {
		        	return PropertyProvider.get(moda.getModule().getDescription().substring(1))
		            	.compareToIgnoreCase(PropertyProvider.get(modb.getModule().getDescription().substring(1)));
		        }
		    }
	    	
	    });
		for(int module : imodules) {
			PnModuleCheckboxModel checkboxModule = new PnModuleCheckboxModel();
			for(PnModule currModule : pnModules) {
				if(module == currModule.getModuleId().intValue()) {
					checkboxModule.setSelected(true);
					break;
				}
			}
			checkboxModule.setModule(ServiceFactory.getInstance().getPnModuleService().getModule(module));
			checkboxModule.setDisplayName(PropertyProvider.get( checkboxModule.getModule().getDescription().substring(1) ));
			cbModules.add(checkboxModule);
		}
		return cbModules;
	}
}
