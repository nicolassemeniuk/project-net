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
        
        	xml.append("<material>"); 
	        	xml.append("<id>" + material.getMaterialId() + "</id>");
	        	xml.append("<name>" + material.getMaterialName() + "</name>");
	        	xml.append("<description>" + material.getMaterialDescription() + "</description>");
	        	xml.append("<type>" + material.getPnMaterialType().getMaterialTypeName() + "</type>");
	        	xml.append("<cost>" + material.getMaterialCost() + "</cost>");
	        	xml.append("<consumable>" + material.getMaterialConsumable() + "</consumable>");	        	
	        	xml.append("<currentDate>" + String.valueOf((new Date()).getTime()) + "</currentDate>");
        	xml.append("</material>");         
		}
		xml.append("</materials-list>\n");
		return xml.toString();
	}
}
