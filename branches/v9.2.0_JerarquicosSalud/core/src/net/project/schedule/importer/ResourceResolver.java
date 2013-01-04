/* 
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
*/
package net.project.schedule.importer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.project.base.property.PropertyProvider;
import net.project.gui.html.HTMLOption;
import net.project.gui.html.IHTMLOption;
import net.project.resource.Roster;
import net.project.soa.schedule.Project.Assignments.Assignment;
import net.project.soa.schedule.Project.Resources.Resource;

import org.apache.commons.collections.MultiHashMap;

/**
 * Provides a mechanism for storing the mapping of a single person to one or
 * more resources and for presenting the options available when choosing which
 * resource's working time calendar to import for a person.
 * 
 * @author Tim Morrow
 * @since Version 7.6.3
 */
public class ResourceResolver {

	/**
	 * The value of the "Ignore" option when selecting a resource to map to a
	 * person.
	 */
	public static final String IGNORE_RESOURCE_OPTION_VALUE = "ignore";

	/**
	 * All the resources in the MSP file. Each key is a <code>Integer</code>
	 * resource UID and each value is a
	 * {@link net.project.schedule.importer.MSPResource}
	 */
	private final Map allResources;

	/**
	 * All the calendars in the MSP file.
	 */
	private final MSPCalendars allCalendars;

	/**
	 * The current space's roster required for looking person names.
	 */
	private final Roster roster;

	/**
	 * A map of resources to persons. This contains all resources that were
	 * mapped to persons. Two or more resources may be mapped to the same
	 * person; there will be more than one key pointing to the same value. Each
	 * key is an <code>Integer</code> resource UID and each value is an
	 * <code>Integer</code> person ID.
	 */
	private final Map resourceUIDMap = new HashMap();

	/**
	 * A map of persons to resources. Each person may be mapped to one or more
	 * resources. Each key is an <code>Integer</code> person ID and each value
	 * is a <code>Collection</code> of <code>Integer</code> resource UID.
	 */
	private final Map personIDMultiMap = new MultiHashMap();

	/**
	 * This represents mapped resources to persons after resolving for ambiguity
	 * and discarding resources which have no working time calendars. It is the
	 * exact set of resource calendars to import and the person who's calendar
	 * to update. There may be fewer persons in this map if a choice was made
	 * not to import a calendar for the person. A Map where each key is an
	 * <code>Integer</code> resource UID and each value is an
	 * <code>Integer</code> person ID.
	 */
	private final Map selectedResourceUIDMap = new HashMap();

	/**
	 * Creates a new ResourceResolver.
	 * 
	 * @param resources
	 *            the resources; Each key is a <code>Integer</code> resource
	 *            UID and each value is a
	 *            {@link net.project.schedule.importer.MSPResource}
	 * @param allCalendars
	 *            the working time calendars used to determine whether a given
	 *            resource has a calendar
	 * @param roster
	 *            the roster used to lookup person names
	 */
	ResourceResolver(Map resources, MSPCalendars allCalendars, Roster roster) {
		this.allResources = resources;
		this.allCalendars = allCalendars;
		this.roster = roster;
	}

	/**
	 * Add a mapping from resource to person. Each resourceUID may be mapped to
	 * more than one personID.
	 * 
	 * @param resourceUID
	 *            the UID of MSP resource
	 * @param personID
	 *            the ID of the space person to whom the resource is mapped
	 */
	public void addResourceMapping(Integer resourceUID, Integer personID) {
		this.resourceUIDMap.put(resourceUID, personID);
		this.personIDMultiMap.put(personID, resourceUID);
	}

	/**
	 * Returns the personID mapped to the resourceUID.
	 * <p>
	 * This method may return the same personID for two different resourceUIDs
	 * since more than one resourceUID could have been mapped to the same
	 * person. <br>
	 * This method is useful for assigning persons to a task since the same
	 * person can be assigned to more than one task.
	 * </p>
	 * 
	 * @param resourceUID
	 *            the UID of the resource for which to get the mapped personID
	 */
	Integer getPersonID(Integer resourceUID) {
		return (Integer) this.resourceUIDMap.get(resourceUID);
	}

	/**
	 * Resolves a mapping of a person to a resource by indicating that the
	 * specified resourceUID is the correct mapping to the personID for the
	 * purposes of importing a working time calendar.
	 * 
	 * @param personID
	 *            the personID
	 * @param resourceUID
	 *            the UID of the resource
	 */
	public void resolveMappingToResource(Integer personID, Integer resourceUID) {
		this.selectedResourceUIDMap.put(resourceUID, personID);
	}

	/**
	 * Indicates whether there is at least one resource mapped.
	 * 
	 * @return true if there is at least one resurce mapped to a person; false
	 *         otherwise
	 */
	public boolean hasResourceMapping() {
		return (this.resourceUIDMap.size() > 0);
	}

	/**
	 * Indicates whether there is at least one resource mapped for the MSP
	 * assignments specified.
	 * 
	 * @param mspAssignments
	 *            the assignments for which to look for a resource mapping; each
	 *            element is a <code>Assignment</code>
	 * @return true if at least one of the MSP Assignments is mapped to a
	 *         resource; false if none are mapped
	 */
	public boolean hasResourceMapping(Collection mspAssignments) {
		boolean isMappingFound = false;
		for (Iterator iterator = mspAssignments.iterator(); iterator.hasNext() && !isMappingFound;) {
			Assignment mspAssignment = (Assignment) iterator.next();
			isMappingFound |= (this.resourceUIDMap.containsKey(new Integer(mspAssignment.getResourceUID().intValue())));
		}

		return isMappingFound;
	}

	public void test() {
		boolean isFound = false;
		while (!isFound) {
			isFound |= true;
		}
	}

	/**
	 * Returns a resolved personID for the specified resourceUID.
	 * <p>
	 * <b>Note:</b> Not all resources will return a personID since in the case
	 * where more than one resource was mapped to a person, only one resource
	 * will have been selected to represent the person. Also, resources with no
	 * working time calendars will not be present and resources who's working
	 * time calendars were chosen to be ignored. <br>
	 * This method will never return the same personID for two different
	 * resourceUIDs. <br>
	 * This is useful for assigning persons to resource working time calendars
	 * since a person can only be assigned to a single working time calendar.
	 * </p>
	 * 
	 * @param resourceUID
	 *            the UID of the resource for which to get the unamiguous person
	 * @return the personID that this resource is mapped to
	 */
	Integer getPersonIDRepresentingResourceUID(Integer resourceUID) {
		return (Integer) this.selectedResourceUIDMap.get(resourceUID);
	}

	/**
	 * Returns the resource entries that have had at least one resource mapping
	 * to a resource with a working time calendar.
	 * 
	 * @return an unmodifiable collection where each element is
	 *         <code>ResourceResolver.Entry</code>
	 */
	public Collection getResourceEntries() {

		Collection<Entry> entries = new ArrayList<Entry>();

		for (Iterator it = this.personIDMultiMap.keySet().iterator(); it.hasNext();) {
			Integer nextPersonID = (Integer) it.next();

			// Build a collection of all the resources for the resourceUIDs
			// that have calendars
			Collection<Resource> resources = new ArrayList<Resource>();
			for (Iterator it2 = ((Collection) this.personIDMultiMap.get(nextPersonID)).iterator(); it2.hasNext();) {
				Integer nextResourceUID = (Integer) it2.next();

				if (null != this.allCalendars.get(((Resource) this.allResources.get(nextResourceUID)).getCalendarUID()
						.intValue())) {
					// Resource has a calendar
					resources.add((Resource) this.allResources.get(nextResourceUID));
				}

			}

			// If we found at least one resource with a calendar
			// We create an entry
			if (!resources.isEmpty()) {
				Entry entry = new Entry(String.valueOf(nextPersonID), roster.getPerson(String.valueOf(nextPersonID))
						.getDisplayName());
				entry.addResources(resources);
				entries.add(entry);
			}
		}

		return Collections.unmodifiableCollection(entries);
	}

	/**
	 * An Entry which maps a person to resources.
	 */
	public static class Entry {

		/** The ID of the person. */
		private final String personID;

		/** The display name of the person. */
		private final String personName;

		/**
		 * The options available to select the resource for which a calendar is
		 * to be imported for this person.
		 */
		private final Collection resources = new ArrayList();

		private final IHTMLOption ignoreOption = new HTMLOption(IGNORE_RESOURCE_OPTION_VALUE, PropertyProvider
				.get("prm.schedule.import.xml.resolveresource.ignorecalendar"));

		/**
		 * Creates a new entry for the specified person ID and display name.
		 * 
		 * @param personID
		 *            the ID of this person
		 * @param personName
		 *            the display name
		 */
		private Entry(String personID, String personName) {
			this.personID = personID;
			this.personName = personName;
		}

		/**
		 * Adds the collection of resources available for selection for this
		 * person.
		 * 
		 * @param resources
		 *            a collection where each element is a
		 *            <code>MSPResource</code>
		 */
		private void addResources(Collection resources) {
			this.resources.addAll(resources);
		}

		/**
		 * Returns the ID of this person.
		 * 
		 * @return the ID
		 */
		public String getPersonID() {
			return this.personID;
		}

		/**
		 * Returns the display name of this person.
		 * 
		 * @return the display name
		 */
		public String getPersonName() {
			return this.personName;
		}

		/**
		 * Returns acollection of resources which are mapped to the current
		 * person, thus requiring resolution. Includes an "ignore" option.
		 * 
		 * @return a collection where each element is a <code>IHTMLOption</code>
		 *         where the value is a resource UID and the display is the
		 *         resource's name
		 */
		public Collection getResourceOptions() {
			Collection resourceOptions = new ArrayList();
			resourceOptions.add(ignoreOption);
			resourceOptions.addAll(this.resources);
			return resourceOptions;
		}

		/**
		 * Returns the default selected option.
		 * <p>
		 * <li>When there is only one resource option, it is selected.
		 * <li>When there are more than one resource options the "Ignore"
		 * option is selected
		 * </p>
		 * 
		 * @return the selected option
		 */
		public IHTMLOption getDefaultSelectedOption() {
			return (this.resources.size() == 1 ? (IHTMLOption) this.resources.iterator().next() : ignoreOption);
		}
	}
}
