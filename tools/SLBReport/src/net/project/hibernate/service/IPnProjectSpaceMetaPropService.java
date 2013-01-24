package net.project.hibernate.service;

import net.project.hibernate.model.PnProjectSpaceMetaProp;

import java.math.BigDecimal;
import java.util.List;


public interface IPnProjectSpaceMetaPropService {
    /**
     * @param pnProjectSpaceMetaPropId for PnProjectSpaceMetaProp we need to select from database
     * @return PnProjectSpaceMetaProp bean
     */
    public PnProjectSpaceMetaProp getProjectSpaceMetaProp(BigDecimal pnProjectSpaceMetaPropId);

    /**
     * Saves new PnProjectSpaceMetaProp
     *
     * @param pnProjectSpaceMetaProp object we want to save
     * @return primary key for saved PnProjectSpaceMetaProp
     */
    public BigDecimal saveProjectSpaceMetaProp(PnProjectSpaceMetaProp pnProjectSpaceMetaProp);

    /**
     * Deletes PnProjectSpaceMetaProp from database
     *
     * @param pnProjectSpaceMetaProp object we want to delete
     */
    public void deleteProjectSpaceMetaProp(PnProjectSpaceMetaProp pnProjectSpaceMetaProp);

    /**
     * Updates PnProjectSpaceMetaProp
     *
     * @param pnProjectSpaceMetaProp object we want to update
     */
    public void updateProjectSpaceMetaProp(PnProjectSpaceMetaProp pnProjectSpaceMetaProp);

    /**
     * Get list of all project space meta properties
     *
     * @return List of PnProjectSpaceMetaProp objects
     */
    public List<PnProjectSpaceMetaProp> getAllProjectSpaceMetaProperties();

    public PnProjectSpaceMetaProp getProjectSpaceMetaPropByName(String propertyName);
}
