package net.project.material;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import net.project.hibernate.model.PnMaterial;

public class PnMaterialList extends ArrayList<PnMaterial> {
	
	public PnMaterialList()	{
		super();
	}
	
	public PnMaterialList(Collection<PnMaterial> collection){
		super(collection);
	}

	/**
	 * Converts the object to XML representation This method returns the object
	 * as XML text.
	 * 
	 * @return XML representation of this object
	 */
	public String getXML() {
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" ?>\n\n");
		xml.append(getXMLBody());
		return xml.toString();
	}
	
	/**
	 * Get an XML representation of the Space without xml tag.
	 * 
	 * @return an XML representation of this Space without the xml tag.
	 */
	public String getXMLBody() {
		Iterator<PnMaterial> iterator = this.iterator();
		StringBuffer xml = new StringBuffer();
		xml.append("<materials-list>\n");
		while (iterator.hasNext())
		{
			PnMaterial material = iterator.next();			
	        xml.append("<node>");
        	xml.append("<value>");
        	xml.append("<isowner>1</isowner>");   
        
        	xml.append("<materialspace>"); 
	        	xml.append("<materialid>" + material.getMaterialId() + "</materialid>");
	        	xml.append("<materialname>" + material.getMaterialName() + "</materialname>");
	        	xml.append("<materialdescription>" + material.getMaterialDescription() + "</materialdescription>");
	        	xml.append("<materialtype>" + material.getPnMaterialType().getMaterialTypeName() + "</materialtype>");
	        	xml.append("<materialcost>" + material.getMaterialCost() + "</materialcost>");
	        	xml.append("<currentdate>" + String.valueOf((new Date()).getTime()) + "</currentdate>");
        	xml.append("</materialspace>");         
        
        	xml.append("</value>");     
        
        	xml.append("<children>");
        	xml.append("</children>");          
        	xml.append("</node>");   			
		}
        xml.append("<maxdepth>1</maxdepth>");		
		xml.append("</materials-list>\n");
		return xml.toString();
	}
	
	/**
	 * Converts the object to XML representation This method returns the object
	 * as XML text.
	 * 
	 * @return XML representation of this object
	 */
	public String getXML2() {
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" ?>\n\n");
		xml.append(getXMLBody2());
		return xml.toString();
	}
	
	/**
	 * Get an XML representation of the Space without xml tag.
	 * 
	 * @return an XML representation of this Space without the xml tag.
	 */
	public String getXMLBody2() {
		Iterator<PnMaterial> iterator = this.iterator();
		StringBuffer xml = new StringBuffer();
		xml.append("<materials-list>\n");
		while (iterator.hasNext())
		{
			PnMaterial material = iterator.next();			

        
        	xml.append("<material>"); 
	        	xml.append("<materialid>" + material.getMaterialId() + "</materialid>");
	        	xml.append("<materialname>" + material.getMaterialName() + "</materialname>");
	        	xml.append("<materialdescription>" + material.getMaterialDescription() + "</materialdescription>");
	        	xml.append("<materialtype>" + material.getPnMaterialType().getMaterialTypeName() + "</materialtype>");
	        	xml.append("<materialcost>" + material.getMaterialCost() + "</materialcost>");	        	
        	xml.append("</material>");         
  			
		}
		xml.append("<maxdepth>1</maxdepth>");
		xml.append("</materials-list>\n");

		return xml.toString();
	}
}
