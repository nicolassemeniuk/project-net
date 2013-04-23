package net.project.material.report;

import net.project.base.finder.EmptyFinderFilter;


public class MaterialNotConsumableFilter extends EmptyFinderFilter {
	
    private static String MATERIAL_NO_CONSUMABLE_FILTER_NAME = "prm.material.report.common.filter.notconsumable.name";

    /**
     * Standard constructor.
     *
     * @param id a <code>String</code> that uniquely identifies this filter.
     * Generally, these are just an integer value specific to the current query
     * you are constructing, though it can be any unique value.  You can use
     * whatever value you like, as long as it is unique.
     */
    public MaterialNotConsumableFilter(String id) {
        super(id, MATERIAL_NO_CONSUMABLE_FILTER_NAME);
    }

	@Override
	public String getWhereClause() {
        StringBuffer whereClause = new StringBuffer();

        if (isSelected()) {
            whereClause.append(" m.material_consumable LIKE '%false%' ");
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
