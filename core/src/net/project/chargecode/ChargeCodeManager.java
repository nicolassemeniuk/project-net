/**
 * 
 */
package net.project.chargecode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.project.base.property.PropertyProvider;
import net.project.gui.html.HTMLOption;
import net.project.gui.html.HTMLOptionList;
import net.project.hibernate.model.PnChargeCode;
import net.project.util.StringUtils;
import net.project.view.pages.base.BasePage;

import org.apache.commons.collections.CollectionUtils;
import org.apache.tapestry5.util.TextStreamResponse;

/**
 * This class is used to perform operations on charge codes
 * acts as helper for managing charge codes.
 * 
 * @author Ritesh S
 *
 */
public class ChargeCodeManager extends BasePage {
	
	List<PnChargeCode> chargeCodeList;
	
	/**
	 * To create charge code
	 * @param request
	 * @return charge codeId
	 */
	public Object createChargeCode(HttpServletRequest request){
		PnChargeCode pnChargeCode = new PnChargeCode(); 
		pnChargeCode.setBusinessId(Integer.parseInt(request.getParameter("bussinessId").toString()));
		pnChargeCode.setCodeName(request.getParameter("codeName").toString());
		pnChargeCode.setCodeNumber(request.getParameter("codeNo").toString());
		pnChargeCode.setCodeDesc(request.getParameter("description").toString());
		pnChargeCode.setRecordStatus("A");
		int codeId = getPnChargeCodeService().save(pnChargeCode);
		return new TextStreamResponse("text/plain", Integer.toString(codeId));
	}

	/**
	 * To edit existing charge codes
	 * @param request
	 * @return
	 */
	public Object editChargeCode(HttpServletRequest request){
		PnChargeCode pnChargeCode = new PnChargeCode(); 
		pnChargeCode.setCodeId(Integer.parseInt(request.getParameter("codeId").toString()));
		pnChargeCode.setBusinessId(Integer.parseInt(request.getParameter("bussinessId").toString()));
		pnChargeCode.setCodeName(request.getParameter("codeName").toString());
		pnChargeCode.setCodeNumber(request.getParameter("codeNo").toString());
		pnChargeCode.setCodeDesc(request.getParameter("description").toString());
		pnChargeCode.setRecordStatus("A");
		getPnChargeCodeService().update(pnChargeCode);
		return new TextStreamResponse("text/plain", "updated");
	}
	
	/**
	 * To delete existing charge code
	 * @param codeId
	 * @return
	 */
	public Object deleteChargeCode(HttpServletRequest request){
		PnChargeCode pnChargeCode = new PnChargeCode(); 
		pnChargeCode.setCodeId(Integer.parseInt(request.getParameter("codeId").toString()));
		pnChargeCode.setBusinessId(Integer.parseInt(request.getParameter("bussinessId").toString()));
		pnChargeCode.setCodeName(request.getParameter("codeName").toString());
		pnChargeCode.setCodeNumber(request.getParameter("codeNo").toString());
		pnChargeCode.setCodeDesc(request.getParameter("description").toString());
		pnChargeCode.setRecordStatus("D");
		getPnChargeCodeService().update(pnChargeCode);
		return new TextStreamResponse("text/plain", "deleted");
	}

	/**
	 * To activate existing charge code which are deactivated
	 * @param codeId
	 * @return
	 */
	public Object activateChargeCode(HttpServletRequest request){
		PnChargeCode pnChargeCode = new PnChargeCode(); 
		pnChargeCode.setCodeId(Integer.parseInt(request.getParameter("codeId").toString()));
		pnChargeCode.setBusinessId(Integer.parseInt(request.getParameter("bussinessId").toString()));
		pnChargeCode.setCodeName(request.getParameter("codeName").toString());
		pnChargeCode.setCodeNumber(request.getParameter("codeNo").toString());
		pnChargeCode.setCodeDesc(request.getParameter("description").toString());
		pnChargeCode.setRecordStatus("A");
		getPnChargeCodeService().update(pnChargeCode);
		return new TextStreamResponse("text/plain", "deleted");
	}

	/**
	 * To deactivate existing active charge code
	 * @param codeId
	 * @return
	 */
	public Object deActivateChargeCode(HttpServletRequest request){
		PnChargeCode pnChargeCode = new PnChargeCode(); 
		pnChargeCode.setCodeId(Integer.parseInt(request.getParameter("codeId").toString()));
		pnChargeCode.setBusinessId(Integer.parseInt(request.getParameter("bussinessId").toString()));
		pnChargeCode.setCodeName(request.getParameter("codeName").toString());
		pnChargeCode.setCodeNumber(request.getParameter("codeNo").toString());
		pnChargeCode.setCodeDesc(request.getParameter("description").toString());
		pnChargeCode.setRecordStatus("D");
		getPnChargeCodeService().update(pnChargeCode);
		return new TextStreamResponse("text/plain", "deleted");
	}

	/**
	 * @return
	 */
	public List<PnChargeCode> getChargeCodeList() {
		return chargeCodeList;
	}

	/**
	 * @param chargeCodeList
	 */
	public void setChargeCodeList(List<PnChargeCode> chargeCodeList) {
		this.chargeCodeList = chargeCodeList;
	}
	
	/**
	 * Method to get html select list of charge codes.
	 * If selected value in not null then
	 * the option whose value is equals to selectedValue is displyed selected.
	 * @param selectedValue
	 * @return String
	 */
	public String getChargeCodeHtml(String selectedValue){
		String chargeCodeHtmlOptions = " <option value=\"\">"+PropertyProvider.get("prm.business.chargecode.dropdownlist.defaultoption.label")+"</option>";
		if(StringUtils.isEmpty(selectedValue))
			chargeCodeHtmlOptions += HTMLOptionList.makeHtmlOptionList(getChargeCodeHtmlOptionCollection());
		else
			chargeCodeHtmlOptions += HTMLOptionList.makeHtmlOptionList(getChargeCodeHtmlOptionCollection(), selectedValue);
		return chargeCodeHtmlOptions;
	}
	
	/**
	 * Method to get collection on HtmlOption that can be used to generate html select list of charge codes.
	 * @return Collection
	 */
	public Collection getChargeCodeHtmlOptionCollection(){
		List chargeCodeOptions = new ArrayList();

        // if there are charge codes in the list
        if (chargeCodeList != null) {
        	for (PnChargeCode chargeCode :  chargeCodeList){
        		chargeCodeOptions.add(new HTMLOption(chargeCode.getCodeId(),chargeCode.getCodeName()));
            }
        }
        return chargeCodeOptions;
	}
	
	public boolean isChargeCodeAvailable(){
		return CollectionUtils.isNotEmpty(chargeCodeList);
	}
}
