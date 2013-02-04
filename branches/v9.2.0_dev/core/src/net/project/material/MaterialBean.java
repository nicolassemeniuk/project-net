package net.project.material;

import java.io.Serializable;

import net.project.space.Space;

public class MaterialBean extends Material implements Serializable {
	
    /** Current space */
    private Space space;
    
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

}
