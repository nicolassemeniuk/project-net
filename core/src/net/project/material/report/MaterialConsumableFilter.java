package net.project.material.report;

import net.project.base.finder.EmptyFinderFilter;

public class MaterialConsumableFilter extends EmptyFinderFilter {
    private static String MATERIAL_CONSUMABLE_FILTER_NAME = "prm.material.report.common.filter.consumable.name";

    /**
     * Standard constructor.
     *
     * @param id a <code>String</code> that uniquely identifies this filter.
     * Generally, these are just an integer value specific to the current query
     * you are constructing, though it can be any unique value.  You can use
     * whatever value you like, as long as it is unique.
     */
    public MaterialConsumableFilter(String id) {
        super(id, MATERIAL_CONSUMABLE_FILTER_NAME);
    }

	@Override
	public String getWhereClause() {
        StringBuffer whereClause = new StringBuffer();

        if (isSelected()) {
            whereClause.append(" m.material_consumable LIKE '%true%' ");
        }

        return whereClause.toString();
	}

	@Override
	public String getFilterDescription() {
		return getName();
	}

	@Override
	protected void clearProperties() {
		//No properties to clear
	}

}
