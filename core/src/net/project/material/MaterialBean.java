package net.project.material;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;

import net.project.persistence.IXMLPersistence;
import net.project.base.Module;
import net.project.hibernate.model.PnMaterial;
import net.project.space.Space;

public class MaterialBean extends Material implements Serializable, IXMLPersistence {
	
    /** Current space */
    private Space space;

    public MaterialBean()
    {
    	super();
    }    
    
    public MaterialBean(PnMaterial pnMaterial)
    {
    	super(pnMaterial);
    }
    
    /**
     * Set the current space
     * @param space the current space
     */
    public void setSpace(Space space) {
        this.space = space;
    }
    
    public Space getSpace(){
    	return this.space;
    }

	@Override
	public String getXML() throws SQLException {
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" ?>\n\n");
		xml.append(getXMLBody());
		return xml.toString();
	}

	@Override
	public String getXMLBody() throws SQLException
	{
		StringBuffer xml = new StringBuffer();
		
    	xml.append("<material>"); 
    	xml.append("<id>" + this.getMaterialId() + "</id>");
    	xml.append("<name>" + this.getName() + "</name>");
    	xml.append("<description>" + this.getDescription() + "</description>");
    	xml.append("<type>" + this.getMaterialTypeName() + "</type>");
    	xml.append("<cost>" + this.getCost() + "</cost>");
    	xml.append("<consumable>" + this.getConsumable() + "</consumable>");	        	
    	xml.append("<currentDate>" + String.valueOf((new Date()).getTime()) + "</currentDate>");
    	xml.append("</material>");
    	
    	return xml.toString();
	}
	
	public String getUrl()
	{
        return "/material/MaterialDetail.jsp?module=" + Module.MATERIAL + "&id=" + this.getMaterialId();		
	} 
}
