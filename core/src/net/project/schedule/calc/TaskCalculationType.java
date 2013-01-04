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

 package net.project.schedule.calc;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.project.base.property.PropertyProvider;
import net.project.gui.html.IHTMLOption;
import net.project.util.CollectionUtils;

/**
 * Enumeration of all calculation types.
 * <p>
 * A task calculation type implies how a task should be updated based on
 * a user modification.
 * </p>
 * <p>
 * IDs are fixed; they can be used for persistence.
 * </p>
 *
 * @author Tim Morrow
 * @since Version 7.7
 */
public class TaskCalculationType implements Serializable {

    /**
     * A map where keys are <code>String</code> ids and values are
     * <code>TaskCalculationType</code>s.
     * Contains all enumeration values.
     */
    private static final Map ALL_VALUES = new HashMap();

    /**
     * Fixed Unit, effort driven.
     * Units are never changed;
     * changing duration changes work;
     * changing an assignment, adding or removing assignments changes duration.
     */
    public static final TaskCalculationType FIXED_UNIT_EFFORT_DRIVEN = new TaskCalculationType("10", FixedElement.UNIT, true);

    /**
     * Fixed Unit, non-effort driven.
     * Units are never changed;
     * changing duration changes work;
     * changing an assignment changes duration;
     * adding or removing assignments changes work.
     */
    public static final TaskCalculationType FIXED_UNIT_NON_EFFORT_DRIVEN = new TaskCalculationType("20", FixedElement.UNIT, false);

    /**
     * Fixed duration, effort driven.
     * Duration is never changed;
     * changing duration or an assignment changes work;
     * changing work or adding and removing assignments changes assignments.
     */
    public static final TaskCalculationType FIXED_DURATION_EFFORT_DRIVEN = new TaskCalculationType("30", FixedElement.DURATION, true);

    /**
     * Fixed duration, non-effort driven.
     * Duration is never changed;
     * changing duration or an assignment or adding and removing assignments changes work;
     * changing work changes assignments.
     */
    public static final TaskCalculationType FIXED_DURATION_NON_EFFORT_DRIVEN = new TaskCalculationType("40", FixedElement.DURATION, false);

    /**
     * Fixed work.
     * Work is never changed;
     * changing duration changes assignments;
     * changing work or an assignment or adding and removing assignments changes duration.
     */
    public static final TaskCalculationType FIXED_WORK = new TaskCalculationType("50", FixedElement.WORK, true);

    public static final Collection FIXED_DURATION_TYPES = CollectionUtils.createCollection(FIXED_DURATION_EFFORT_DRIVEN, FIXED_DURATION_NON_EFFORT_DRIVEN);
    public static final Collection FIXED_WORK_TYPES = CollectionUtils.createCollection(FIXED_WORK);
    public static final Collection FIXED_UNITS_TYPES = CollectionUtils.createCollection(FIXED_UNIT_EFFORT_DRIVEN, FIXED_UNIT_NON_EFFORT_DRIVEN);

    /**
     * Returns a TaskCalculationType for the specified ID.
     * @param id the id of the type to get
     * @return the type
     * @throws NullPointerException if id is null
     * @throws IllegalArgumentException if there is no type for that ID
     */
    public static TaskCalculationType forID(String id) {
        if (id == null) {
            throw new NullPointerException("id is required");
        }
        TaskCalculationType type = (TaskCalculationType) ALL_VALUES.get(id);
        if (type == null) {
            throw new IllegalArgumentException("Unknown TaskCalculationType with id " + id);
        }
        return type;
    }

    /**
     * Returns an unmodifiable list of "fixed" elements.
     * @return a list where each element is an <code>IHTMLOption</code> providing a selection of
     * <code>Fixed Unit</code>, <code>Fixed Duration</code>, <code>Fixed Work</code>
     */
    public static List getFixedElementHTMLOptions() {
        return Collections.unmodifiableList(FixedElement.getHTMLOptions());
    }

    /**
     * Returns a TaskCalculationType based on the specified fixed element and
     * whether it is effort driven or not.
     * @param fixedElement the fixed element
     * @param isNewTaskEffortDriven true if the task is effort driven; false otherwise
     * this is ignored for Fixed work
     * @return the calculation type
     */
    public static TaskCalculationType makeFromComponents(FixedElement fixedElement, boolean isNewTaskEffortDriven) {
        TaskCalculationType type;

        if (fixedElement.equals(FixedElement.UNIT)) {
            type = (isNewTaskEffortDriven ? FIXED_UNIT_EFFORT_DRIVEN : FIXED_UNIT_NON_EFFORT_DRIVEN);

        } else if (fixedElement.equals(FixedElement.DURATION)) {
            type = (isNewTaskEffortDriven ? FIXED_DURATION_EFFORT_DRIVEN : FIXED_DURATION_NON_EFFORT_DRIVEN);

        } else {
            type = FIXED_WORK;
        }

        return type;
    }

    //
    // Instance Members
    //

    /** The unique id of this value. */
    private final String id;

    /**
     * Indicates which element if fixed (unit, work, duration).
     */
    private final FixedElement fixed;

    /**
     * Indicates whether the task is effort driven or not.
     */
    private final boolean isEffortDriven;

    /**
     * Creates a new value with the specified properties.
     * @param id the unique id of this value
     * @param fixed the element that is fixed for this value
     * @param isEffortDriven true if this is an effort driven calculation type
     * @throws IllegalArgumentException if the specified id has already been added
     */
    private TaskCalculationType(String id, FixedElement fixed, boolean isEffortDriven) {
        if (ALL_VALUES.containsKey(id)) {
            throw new IllegalArgumentException("Attempted to add second enumerated value with id " + id);
        }
        this.id = id;
        this.fixed = fixed;
        this.isEffortDriven = isEffortDriven;
        ALL_VALUES.put(this.id, this);
    }

    /**
     * Returns the ID of this task calculation type.
     * @return the ID
     */
    public String getID() {
        return this.id;
    }

    /**
     * Indicates whether this task type is fixed duration or not.
     * @return true if this task type is fixed duration; false otherwise
     */
    public boolean isFixedDuration() {
        return this.fixed.equals(FixedElement.DURATION);
    }

    /**
     * Indicates whether this task type is fixed unit or not.
     * @return true if this task type is fixed unit; false otherwise
     */
    public boolean isFixedUnit() {
        return this.fixed.equals(FixedElement.UNIT);
    }

    /**
     * Indicates whether this task type is fixed work or not.
     * @return true if this task type is fixed work; false otherwise
     */
    public boolean isFixedWork() {
        return this.fixed.equals(FixedElement.WORK);
    }

    /**
     * Indicates whether this task type is effort driven or not.
     * @return true if this task type is effort driven; false otherwise
     */
    public boolean isEffortDriven() {
        return this.isEffortDriven;
    }

    /**
     * Returns the Fixed element of this task calculation type
     * as an IHTMLOption suitable for selection in a selection list.
     * @return the fixed element option
     */
    public IHTMLOption getFixedElementHTMLOption() {
        return this.fixed;
    }

    /**
     * Indicates whether the specified object is a TaskCalculationType equal to this
     * one based on id.
     * @param o the task calculation type to compare
     * @return true if their IDs are equal; false otherwise
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaskCalculationType)) {
            return false;
        }

        final TaskCalculationType taskCalculationType = (TaskCalculationType) o;

        if (!id.equals(taskCalculationType.id)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Returns a string representation of this task calculation type, suitable for debugging.
     * @return the string representation, currently consisting of the fixed element id and
     * whether effort driven or not
     */
    public String toString() {
        String result = this.fixed.toString();
        if (!this.fixed.equals(FixedElement.WORK)) {
            result += ", " + (isEffortDriven ? "effort driven" : "non-effort driven");
        }
        return result;
    }

    /**
     * Formats this task calculation type for display.
     * @return the task calculation type formatted for display
     */
    public String formatDisplay() {
        String display = fixed.getDisplayName();
        if (!this.fixed.equals(FixedElement.WORK)) {
            display += ", " + formatEffortDrivenDisplay();
        }
        return display;
    }

    /**
     * Formats the for display text indicating whether this task
     * calculation type is effort driven or not.
     * @return the display similar to "Effort Driven" or "Non-effort Driven"
     * based on property values
     * <code>prm.schedule.taskcalculationtype.effortdriven.name</code>
     * and <code>prm.schedule.taskcalculationtype.noneffortdriven.name</code>
     */
    private String formatEffortDrivenDisplay() {
        return PropertyProvider.get((isEffortDriven() ? "prm.schedule.taskcalculationtype.effortdriven.name" : "prm.schedule.taskcalculationtype.noneffortdriven.name"));
    }

    //
    // Nested top-level classes
    //

    /**
     * Provides an enumeration that indicate which element of a task
     * is fixed.
     */
    public static class FixedElement implements IHTMLOption, Serializable {

        /** Provides an ordered list of all fixed elements. */
        private static final List ALL_ELEMENTS_LIST = new LinkedList();

        /** Provides a map of id to value. */
        private static final Map ALL_ELEMENTS_MAP = new HashMap();

        /** Indicates Fixed Duration. */
        public static final FixedElement DURATION = new FixedElement("duration", "prm.schedule.taskcalculationtype.fixedduration.name");

        /** Indicates Fixed Unit. */
        public static final FixedElement UNIT = new FixedElement("unit", "prm.schedule.taskcalculationtype.fixedunit.name");

        /** Indicates Fixed Work. */
        public static final FixedElement WORK = new FixedElement("work", "prm.schedule.taskcalculationtype.fixedwork.name");

        /**
         * Returns all elements as IHTMLOptions.
         * @return a list where each element is an <code>IHTMLOption</code>
         */
        static List getHTMLOptions() {
            return ALL_ELEMENTS_LIST;
        }

        /**
         * Returns the FixedElement for the specified id.
         * @param id the id of the value to get
         * @return the value for that id
         * @throws IllegalArgumentException if no element is found for that id
         */
        public static FixedElement forID(String id) {
            if (id == null) {
                throw new NullPointerException("id is required");
            }
            FixedElement value = (FixedElement) ALL_ELEMENTS_MAP.get(id);
            if (value == null) {
                throw new IllegalArgumentException("No FixedElement value for id " + id);
            }
            return value;
        }

        //
        // Instance Members
        //

        /** The Unique ID of the Fixed Element. */
        private final String id;

        /** The display name property. */
        private final String displayNameProperty;

        /**
         * Creates a new FixedElement.
         * @param id the unique id of this fixed element
         * @param displayNameProperty the property that provides the display name of this fixed element
         * @throws IllegalArgumentException if the id is already in use
         */
        private FixedElement(String id, String displayNameProperty) {
            if (ALL_ELEMENTS_MAP.containsKey(id)) {
                throw new IllegalArgumentException("Duplicate enumerated value id " + id);
            }
            this.id = id;
            this.displayNameProperty = displayNameProperty;
            ALL_ELEMENTS_LIST.add(this);
            ALL_ELEMENTS_MAP.put(this.id, this);
        }

        public String getID() {
            return this.id;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof FixedElement)) {
                return false;
            }

            final FixedElement fixedElement = (FixedElement) o;

            if (!id.equals(fixedElement.id)) {
                return false;
            }

            return true;
        }

        public int hashCode() {
            return id.hashCode();
        }

        /**
         * Returns a string representation of this value suitable for debugging.
         * @return the string representation, currently the id
         */
        public String toString() {
            return "fixed " + this.id;
        }

        /**
         * Returns this fixed element's display name, looked-up
         * from its display name property.
         * @return the fixed element display name
         */
        String getDisplayName() {
            return PropertyProvider.get(this.displayNameProperty);
        }

        /**
         * Returns the HTML option value which is the internal id.
         * @return the internal id
         */
        public String getHtmlOptionValue() {
            return this.id;
        }

        /**
         * Returns the HTML option display value, which is the display name.
         * @return the display name
         */
        public String getHtmlOptionDisplay() {
            return getDisplayName();
        }

    }
}