package net.project.material;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import net.project.hibernate.model.PnMaterial;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.IXMLPersistence;

public class MaterialBeanList extends ArrayList<MaterialBean> implements IXMLPersistence
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
				MaterialBean materialBean = new MaterialBean(material);
				this.add(materialBean);
		}
	
   		this.isLoaded=true;
	}
	
//	public void load(String searchKey)
//	{
//		PnMaterialList materialList = ServiceFactory.getInstance().getMaterialService().getMaterialsFromSpace(this.spaceID, name, materialTypeId, consumable, minCost, maxCost);
//		
//		for(PnMaterial material : materialList){
//				MaterialBean materialBean = new MaterialBean(material);
//				this.add(materialBean);
//		}
//	
//   		this.isLoaded=true;		
//	}
//	
	public void load(String name, String materialTypeId, String consumable, String minCost, String maxCost)
	{
		PnMaterialList materialList = ServiceFactory.getInstance().getMaterialService().getMaterialsFromSpace(this.spaceID, name, materialTypeId, consumable, minCost, maxCost);
		
		for(PnMaterial material : materialList){
				MaterialBean materialBean = new MaterialBean(material);
				this.add(materialBean);
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
		xml.append(this.getXMLBody());
		return xml.toString();
	}

	@Override
	public String getXMLBody()
	{
		StringBuffer xml = new StringBuffer();
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
