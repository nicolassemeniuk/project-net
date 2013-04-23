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
	
	public MaterialBeanList()
	{
		super();
	}

	public void setSpaceID(String spaceID)
	{
		this.spaceID = spaceID;
	}
	
	public void load()
	{
		PnMaterialList materialList = ServiceFactory.getInstance().getMaterialService().getMaterialsFromSpace(this.spaceID);
		
   		for(Iterator<PnMaterial> iterator = materialList.iterator(); iterator.hasNext();)
		{
   			MaterialBean materialBean = new MaterialBean(iterator.next()); 
   			this.add(materialBean);
		}		
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
