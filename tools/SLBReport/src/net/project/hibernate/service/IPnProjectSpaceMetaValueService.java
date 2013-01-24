package net.project.hibernate.service;

import net.project.hibernate.model.PnProjectSpaceMetaValue;
import net.project.hibernate.model.PnProjectSpaceMetaValuePK;

import java.math.BigDecimal;
import java.util.List;


public interface IPnProjectSpaceMetaValueService {
    /**
     * @param pnProjectSpaceMetaValuePK for PnProjectSpaceMetaValue we need to select from database
     * @return PnProjectSpaceMetaValue bean
     */
    public PnProjectSpaceMetaValue getProjectSpaceMetaValue(PnProjectSpaceMetaValuePK pnProjectSpaceMetaValuePK);

    /**
     * Saves new PnProjectSpaceMetaValue
     *
     * @param pnProjectSpaceMetaValue object we want to save
     * @return primary key for saved PnProjectSpaceMetaValue
     */
    public PnProjectSpaceMetaValuePK saveProjectSpaceMetaValue(PnProjectSpaceMetaValue pnProjectSpaceMetaValue);

    /**
     * Deletes PnProjectSpaceMetaValue from database
     *
     * @param pnProjectSpaceMetaValue object we want to delete
     */
    public void deleteProjectSpaceMetaValue(PnProjectSpaceMetaValue pnProjectSpaceMetaValue);

    /**
     * Updates PnProjectSpaceMetaValue
     *
     * @param pnProjectSpaceMetaValue object we want to update
     */
    public void updateProjectSpaceMetaValue(PnProjectSpaceMetaValue pnProjectSpaceMetaValue);

    public List<PnProjectSpaceMetaValue> getMetaValuesByProjectId(final BigDecimal projectId);
}
