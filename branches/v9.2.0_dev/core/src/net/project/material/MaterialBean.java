package net.project.material;

import net.project.space.Space;

public class MaterialBean extends Material {
	
    /** Current space */
    private Space space;
    
    /**
     * Set the current space
     * @param space the current space
     */
    public void setSpace(Space space) {
        this.space = space;
    }

}
