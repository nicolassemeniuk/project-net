/**
 * 
 */
package net.project.view.pages.chargecode;

import org.apache.tapestry5.annotations.Property;

import net.project.util.StringUtils;
import net.project.view.pages.base.BasePage;

/**
 * This class is used to save charge codes
 *  
 * @author Ritesh S
 *
 */
public class SaveChargeCode extends BasePage {
	
	@Property
	private String codeId;
	
	@Property
	private String codeNo;
	
	@Property
	private String codeName;

	@Property
	private String codeDesc;
	
	void onActivate(){
		String action = getRequestParameter("action");
		if(StringUtils.equals(action, "edit_charge_code")){
			codeId = getRequestParameter("codeId");
			codeNo = getRequestParameter("codeNo");
			codeName = getRequestParameter("codeName");
			codeDesc = getRequestParameter("codeDesc");
		}
	}
	
}
