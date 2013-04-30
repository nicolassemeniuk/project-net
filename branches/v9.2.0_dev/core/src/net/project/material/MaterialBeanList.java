package net.project.material;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import net.project.hibernate.model.PnMaterial;
import net.project.hibernate.service.ServiceFactory;

public class MaterialBeanList extends ArrayList<MaterialBean>
{
    /** Current space */
    private String spaceID;
    
    private Boolean isLoaded=false;
	
	public MaterialBeanList()
	{
		super();
	}

	public void setSpaceID(String spaceID)
	{
		this.spaceID = spaceID;
	}
	
	
	public Boolean getIsLoaded() {
		return isLoaded;
	}

	public void setIsLoaded(Boolean isLoaded) {
		this.isLoaded = isLoaded;
	}

	public void load()
	{
		PnMaterialList materialList = ServiceFactory.getInstance().getMaterialService().getMaterialsFromSpace(this.spaceID);
		
		for(PnMaterial material : materialList){
			//Only the active ones
			if(material.getRecordStatus().equals("A")){
				MaterialBean materialBean = new MaterialBean(material);
				this.add(materialBean);
			}
		}
	
   		this.isLoaded=true;
	}
	
	/**
	 * Converts the object to XML representation This method returns the object
	 * as XML text.
	 * 
	 * @return XML representation of this object
	 */
	public String getXML()
	{
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" ?>\n\n");
		xml.append("<materials-list>\n");
		
   		for(Iterator<MaterialBean> iterator = this.iterator(); iterator.hasNext(); )
		{		
			try
			{
				MaterialBean material = iterator.next();
				xml.append(material.getXMLBody());
			}
			catch(SQLException exception)
			{
		    	xml.append("</material>");				
			}
		}
		
		xml.append("</materials-list>\n");		
		return xml.toString();
	}
}
