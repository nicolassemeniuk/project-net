package net.project.hibernate.service.impl;

import net.project.hibernate.dao.IPnProjectSpaceMetaValueDAO;
import net.project.hibernate.model.PnProjectSpaceMetaValue;
import net.project.hibernate.model.PnProjectSpaceMetaValuePK;
import net.project.hibernate.service.IPnProjectSpaceMetaValueService;

import java.math.BigDecimal;
import java.util.List;


public class PnProjectSpaceMetaValueServiceImpl implements IPnProjectSpaceMetaValueService {
    private IPnProjectSpaceMetaValueDAO pnProjectSpaceMetaValueDAO;

    public void setPnProjectSpaceMetaValueDAO(IPnProjectSpaceMetaValueDAO pnProjectSpaceMetaValueDAO) {
        this.pnProjectSpaceMetaValueDAO = pnProjectSpaceMetaValueDAO;
    }

    public PnProjectSpaceMetaValue getProjectSpaceMetaValue(PnProjectSpaceMetaValuePK pnProjectSpaceMetaValuePK) {
        return pnProjectSpaceMetaValueDAO.findByPimaryKey(pnProjectSpaceMetaValuePK);
    }

    public PnProjectSpaceMetaValuePK saveProjectSpaceMetaValue(PnProjectSpaceMetaValue pnProjectSpaceMetaValue) {
        return pnProjectSpaceMetaValueDAO.create(pnProjectSpaceMetaValue);
    }

    public void deleteProjectSpaceMetaValue(PnProjectSpaceMetaValue pnProjectSpaceMetaValue) {
        pnProjectSpaceMetaValueDAO.delete(pnProjectSpaceMetaValue);
    }

    public void updateProjectSpaceMetaValue(PnProjectSpaceMetaValue pnProjectSpaceMetaValue) {
        pnProjectSpaceMetaValueDAO.update(pnProjectSpaceMetaValue);
    }

    public List<PnProjectSpaceMetaValue> getMetaValuesByProjectId(final BigDecimal projectId) {
        return pnProjectSpaceMetaValueDAO.getMetaValuesByProjectId(projectId);
    }
}
