package net.project.material;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.project.hibernate.model.PnMaterialType;

public class PnMaterialTypeList extends ArrayList<PnMaterialType> {

	public PnMaterialTypeList()
	{
		super();
	}
	
	public PnMaterialTypeList(Collection<PnMaterialType> collection)
	{
		super(collection);
	}

	public String getXML() {
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" ?>\n\n");
		xml.append(getXMLBody());
		return xml.toString();
	}
	
	public String getXMLBody() {
		Iterator<PnMaterialType> iterator = this.iterator();
		StringBuffer xml = new StringBuffer();
		xml.append("<material-types-list>\n");
		while (iterator.hasNext())
		{
			PnMaterialType materialType = iterator.next();			
	        xml.append("<node>");
        	xml.append("<value>");
        	xml.append("<isowner>1</isowner>");   
        
        	xml.append("<materialtype>"); 
	        	xml.append("<materialtypeid>" + materialType.getMaterialTypeId() + "</materialid>");
	        	xml.append("<materialname>" + materialType.getMaterialTypeName() + "</materialname>");    	
        	xml.append("</materialtype>");         
        
        	xml.append("</value>");     
        
        	xml.append("<children>");
        	xml.append("</children>");          
        	xml.append("</node>");   			
		}
        xml.append("<maxdepth>1</maxdepth>");		
		xml.append("</material-types-list>\n");
		return xml.toString();
	}
}
