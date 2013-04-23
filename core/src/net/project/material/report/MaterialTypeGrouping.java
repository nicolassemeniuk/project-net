package net.project.material.report;

import net.project.base.finder.FinderGrouping;
import net.project.base.property.PropertyProvider;
import net.project.material.MaterialBean;
import net.project.persistence.PersistenceException;

public class MaterialTypeGrouping extends FinderGrouping {
	
    private static String GROUP_BY_TYPE_TOKEN = "prm.material.report.common.grouping.bytype.name";

    /**
     * Public constructor.
     *
     * @param id a <code>String</code> value that uniquely identifies this finder
     * grouping.  This is especially important when a grouping is going to be
     * rendered to the screen or stored in a list.
     * @param isDefaultGrouping a <code>boolean</code> value that indicates if
     * this grouping should be selected as the default when it is in a list of
     * groupings.
     */
    public MaterialTypeGrouping(String id, boolean isDefaultGrouping) {
        super(id, PropertyProvider.get(GROUP_BY_TYPE_TOKEN), isDefaultGrouping);
    }

	@Override
	public Object getGroupingValue(Object currentObject) throws PersistenceException {
		return (currentObject == null ? null : ((MaterialBean)currentObject).getMaterialTypeId());
	}

	@Override
	public String getGroupName(Object currentObject) throws PersistenceException {
		return (currentObject == null ? null : ((MaterialBean)currentObject).getMaterialTypeName());
	}

}
