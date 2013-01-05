/**
 * 
 */
package net.project.view.pages.chargecode;

import java.util.List;

import net.project.base.Module;
import net.project.chargecode.ChargeCodeManager;
import net.project.hibernate.model.PnChargeCode;
import net.project.view.pages.base.BasePage;

import org.apache.commons.collections.CollectionUtils;
import org.apache.tapestry5.annotations.Property;
import org.slf4j.Logger;

/**
 * This class is used to manage charage codes
 * 
 * @author Ritesh S
 *
 */
public class ManageChargeCode extends BasePage {

	private static Logger log = logger;

	@Property
	private boolean isRootBusiness;
	
	@Property
	private List<PnChargeCode> chargeCodeList;
	
	@Property
	private PnChargeCode chargeCode;

    private enum ChargeCodeActions {
    	SAVE_CHARGE_CODE,EDIT_CHARGE_CODE,DELETE_CHARGE_CODE,ACTIVATE_CHARGE_CODE,DEACTIVAET_CHARGE_CODE;
		public static ChargeCodeActions get( String v ) {
            try {
                return ChargeCodeActions.valueOf( v.toUpperCase() );
            } catch( Exception ex ) { }
            return null;
         }
	}

    Object onActivate(){
		if(checkForUser() != null) {
			return checkForUser();
		}	
		isRootBusiness = (Boolean) getSessionAttribute("isRootBusiness");
		chargeCodeList = isRootBusiness ? getPnChargeCodeService().getChargeCodeByBusinessId(Integer.valueOf(getBusinessId()))  : getPnChargeCodeService().getRootBusinessChargeCodeBySubBusinessId(Integer.valueOf(getBusinessId())) ;
	    return null;
	}

    Object onActivate(String action){
    	ChargeCodeActions chargeCodeAction = ChargeCodeActions.get( action );
    	ChargeCodeManager chargeCodeManager = new ChargeCodeManager();
    	if(chargeCodeAction.equals(ChargeCodeActions.SAVE_CHARGE_CODE)){
    		return chargeCodeManager.createChargeCode(getHttpServletRequest());
    	} else if(chargeCodeAction.equals(ChargeCodeActions.EDIT_CHARGE_CODE)){
    		return chargeCodeManager.editChargeCode(getHttpServletRequest());
    	} else if(chargeCodeAction.equals(ChargeCodeActions.DELETE_CHARGE_CODE)){
    		return chargeCodeManager.deleteChargeCode(getHttpServletRequest());
    	} else if(chargeCodeAction.equals(ChargeCodeActions.ACTIVATE_CHARGE_CODE)){
    		return chargeCodeManager.deleteChargeCode(getHttpServletRequest());
    	} else if(chargeCodeAction.equals(ChargeCodeActions.DEACTIVAET_CHARGE_CODE)){
    		return chargeCodeManager.deleteChargeCode(getHttpServletRequest());
    	}
    	return null;
    }

	/**
	 * @return
	 */
	public String getBusinessId(){
		return getUser().getCurrentSpace().getID();
	}
	
	/**
	 * @return
	 */
	public String getBusinessName(){
		return getUser().getCurrentSpace().getName();
	}

	/**
	 * @return
	 */
	public int getModuleId(){
		return Module.BUSINESS_SPACE;
	}

	public int getTotalChargeCodes(){
		return CollectionUtils.isEmpty(chargeCodeList) ? 0 : chargeCodeList.size();
	}
	
	/**
	 * To get url of current business main page 
	 * @return String
	 */
	public String getBusinessPageLink(){
		return (getJSPRootURL() + "/business/Main.jsp?id=" + getUser().getCurrentSpace().getID());
	}
	
	/**
	 * To get url of current business setup page
	 * @return String 
	 */
	public String getSetupPageLink(){
		return (getJSPRootURL() + "/business/Setup.jsp?module=" + Module.BUSINESS_SPACE);
	}	
}
