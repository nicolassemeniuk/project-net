package net.project.hibernate.service.impl;

import net.project.hibernate.dao.IPnProjectSpaceMetaPropDAO;
import net.project.hibernate.model.PnProjectSpaceMetaProp;
import net.project.hibernate.service.IPnProjectSpaceMetaPropService;

import java.math.BigDecimal;
import java.util.List;


public class PnProjectSpaceMetaPropServiceImpl implements IPnProjectSpaceMetaPropService {
    private IPnProjectSpaceMetaPropDAO pnProjectSpaceMetaPropDAO;

    public void setPnProjectSpaceMetaPropDAO(IPnProjectSpaceMetaPropDAO pnProjectSpaceMetaPropDAO) {
        this.pnProjectSpaceMetaPropDAO = pnProjectSpaceMetaPropDAO;
    }

    public PnProjectSpaceMetaProp getProjectSpaceMetaProp(BigDecimal pnProjectSpaceMetaPropId) {
        return pnProjectSpaceMetaPropDAO.findByPimaryKey(pnProjectSpaceMetaPropId);
    }

    public BigDecimal saveProjectSpaceMetaProp(PnProjectSpaceMetaProp pnProjectSpaceMetaProp) {
        return pnProjectSpaceMetaPropDAO.create(pnProjectSpaceMetaProp);
    }

    public void deleteProjectSpaceMetaProp(PnProjectSpaceMetaProp pnProjectSpaceMetaProp) {
        pnProjectSpaceMetaPropDAO.delete(pnProjectSpaceMetaProp);
    }

    public void updateProjectSpaceMetaProp(PnProjectSpaceMetaProp pnProjectSpaceMetaProp) {
        pnProjectSpaceMetaPropDAO.update(pnProjectSpaceMetaProp);
    }

    public List<PnProjectSpaceMetaProp> getAllProjectSpaceMetaProperties() {
        return pnProjectSpaceMetaPropDAO.findAll();
    }

    public PnProjectSpaceMetaProp getProjectSpaceMetaPropByName(String propertyName) {
        return pnProjectSpaceMetaPropDAO.getProjectSpaceMetaPropByName(propertyName);
    }
}
