package net.project.view.pages;

import java.text.MessageFormat;

import net.project.base.property.PropertyProvider;
import net.project.util.StringUtils;
import net.project.view.pages.base.BasePage;

import org.apache.tapestry5.annotations.Property;

public class ErrorPage extends BasePage{
	
	@Property
	private String errorMessage;
	
	void onActivate(String objectType, String message){
		if(StringUtils.isNotEmpty(objectType)){
			if(objectType.contains("_")){
				objectType = objectType.replace("_", " ");
			} 
			errorMessage = MessageFormat.format(PropertyProvider.get(message.equals("hidden") ? "prm.project.errorpage.hiddenmessage" : "prm.project.errorpage.alreadydeletedmessage"), StringUtils.capitalize(objectType));
		}
	}
}
