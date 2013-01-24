/* 
 * Copyright 2000-2006 Project.net Inc.
 *
 * Licensed under the Project.net Public License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://dev.project.net/licenses/PPL1.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 package net.project.schedule;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.base.finder.DateFilter;
import net.project.base.finder.DuplicateFilterIDException;
import net.project.base.finder.FinderFilterList;
import net.project.project.ProjectSpace;
import net.project.schedule.calc.TaskCalculationType;
import net.project.utils.SetMethodWatcher;

public class ScheduleTest extends TestCase {
    private static final Date LATEST_START_DATE = net.project.test.util.DateUtils.makeDate("3/5/06 4:00 AM");
	private static final Date LATEST_FINISH_DATE = net.project.test.util.DateUtils.makeDate("4/5/06 4:00 AM");;
	private static final Date EARLIEST_FINISH_DATE = net.project.test.util.DateUtils.makeDate("1/5/06 4:00 AM");;
	private static final Date EARLISET_START_DATE =net.project.test.util.DateUtils.makeDate("1/1/06 4:00 AM");;
	private static final TaskConstraintType START_CONSTRAINT = TaskConstraintType.MUST_FINISH_ON;
	private static final Date START_CONSTRAINT_DATE = net.project.test.util.DateUtils.makeDate("4/5/06 4:00 AM");
	private static final boolean SHARED = true;
	
	
	private FinderFilterList FINDER_FILTER_LIST = new FinderFilterList();
    private Date END_DATE_FILTER = net.project.test.util.DateUtils.makeDate("1/3/05 8:00 AM");
    private boolean AUTOCALCULATE_TASK_ENDPOINTS = false;
    private String DESCRIPTION = "Description";
    private String ID = "1234";
    private TimeZone TIME_ZONE = TimeZone.getTimeZone("EST");
    private String ORDER = "2";
    private int ORDER_DIRECTION = Schedule.ORDER_DESCENDING;
    private int HIERARCHY_VIEW_EXPANDED = Schedule.HIERARCHY_VIEW_EXPANDED;;
    private String FILTER_PHASE_ID = "2390874";
    private String DEFAULT_CALENDAR_ID = "49848382";
    private String NAME = "Name";
    private int MAXIMUM_ENTRIES = 838384;
    private boolean FILTER_OPEN_ITEMS_ONLY = true;
    private TaskType[] TYPE = new TaskType[] {TaskType.MILESTONE};
    private String BASELINE_ID = "12098239409209234";
    private TaskCalculationType FIXED_UNIT_EFFORT_DRIVEN = TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN;
    private ProjectSpace SPACE = new ProjectSpace("123");
    private Map ENTRY_MAP = new HashMap();
    private Date SCHEDULE_START_DATE = net.project.test.util.DateUtils.makeDate("1/4/05 8:00 AM");
    private Date SCHEDULE_END_DATE = net.project.test.util.DateUtils.makeDate("1/5/05 8:00 AM");
    private Date START_DATE_FILTER = net.project.test.util.DateUtils.makeDate("1/4/05 8:11 AM");
    private Date LAST_MODIFIED = net.project.test.util.DateUtils.makeDate("1/20/05 8:39 AM");
    private String LAST_MODIFIED_DISPLAY_NAME = "Joe Doe";
    private String LAST_MODIFIED_BY_ID = "42";
    private Date BASELINE_START = net.project.test.util.DateUtils.makeDate("12/31/04 8:00 AM");
    private Date BASELINE_END = net.project.test.util.DateUtils.makeDate("1/1/05 5:00 PM");
    private TestWorkingTimeCalendarProvider PROVIDER = new TestWorkingTimeCalendarProvider("500");
    private Date LOAD_TIME = new Date();
    private TestWorkingTimeCalendarProvider WORKING_TIME_CALENDAR_PROVIDER = new TestWorkingTimeCalendarProvider("500");


    /**
     * Constructs a test case with the given NAME.
     * 
     * @param s a <code>String</code> containing the NAME of this test.
     */
    public ScheduleTest(String s) {
        super(s);
    }

    /**
     * Direct route to unit test this object from the command line.
     * 
     * @param args a <code>String[]</code> value which contains the command line
     * options.  (These will be unused.)
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Construct a <code>TestSuite</code> containing the test that this unit 
     * test believes it is comprised of.  In most cases, it is only the current
     * class, though it can include others. 
     * 
     * @return a <code>Test</code> object which is really a TestSuite of unit
     * tests. 
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(ScheduleTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        Application.login();

        try {
            FINDER_FILTER_LIST.add(new DateFilter("ID", TaskFinder.DATE_START_COLUMN, false));
        } catch (DuplicateFilterIDException e) {
        }

        ScheduleEntry scheduleEntry = ScheduleEntryFactory.createFromType(TaskType.TASK);
        scheduleEntry.setID("23874747");
        ENTRY_MAP.put("23874747", scheduleEntry);
    }

    public void testClone() throws ClassNotFoundException {
        SetMethodWatcher watcher = new SetMethodWatcher(Class.forName("net.project.schedule.Schedule"));
        Schedule schedule = setupSchedule(watcher);

        Schedule clone = (Schedule)schedule.clone();
        checkEquals(clone);

        //First, make sure we've actually registered all the set methods that
        //are available in task.  If we haven't, it means that someone added a
        //method and didn't know to update either this unit test and/or clone
        //and/or equals
        if (!watcher.allMethodsCalled()) {
            String methodsMissed = "The following methods were unaccounted for: \n";
            for (Iterator it = watcher.getUncalledMethod().iterator(); it.hasNext();) {
                String uncalledMethodName = (String)it.next();
                methodsMissed += "  " + uncalledMethodName + "\n";
            }

            fail(methodsMissed);
        }
    }

    private void checkEquals(Schedule clone) {
        assertEquals(END_DATE_FILTER, clone.getEndDateFilter());
        assertFalse(clone.isAutocalculateTaskEndpoints());
        assertEquals(DESCRIPTION, clone.getDescription());
        assertNotNull(clone.getSpace());
        assertEquals(SPACE.getID(), clone.getSpace().getID());
        assertEquals(ORDER_DIRECTION, clone.getOrderDirection());
        assertEquals(START_DATE_FILTER, clone.getStartDateFilter());
        assertEquals(ID, clone.getID());
        assertEquals(TIME_ZONE, clone.getTimeZone());
        assertEquals(TaskFinder.TYPE_COLUMN, clone.getOrderBy());
        assertEquals(HIERARCHY_VIEW_EXPANDED, clone.getHierarchyView());
        assertEquals(SCHEDULE_START_DATE, clone.getScheduleStartDate());
        assertEquals(SCHEDULE_END_DATE, clone.getScheduleEndDate());
        assertEquals(FILTER_PHASE_ID, clone.getFilterPhaseID());
        assertEquals(DEFAULT_CALENDAR_ID, clone.getDefaultCalendarID());
        assertEquals(NAME, clone.getName());
        assertEquals(MAXIMUM_ENTRIES, clone.getMaximumEntries());
        assertEquals(FILTER_OPEN_ITEMS_ONLY, clone.getFilterOpenItemsOnly());
        assertEquals(ENTRY_MAP, clone.getEntryMap());
        assertEquals(FINDER_FILTER_LIST.size(), clone.getFinderFilterList().size());
        assertEquals(LAST_MODIFIED, clone.getLastModified());
        assertEquals(LAST_MODIFIED_DISPLAY_NAME, clone.getLastModifiedDisplayName());
        assertEquals(LAST_MODIFIED_BY_ID, clone.getLastModifiedByID());
        assertEquals(BASELINE_START, clone.getBaselineStart());
        assertEquals(BASELINE_END, clone.getBaselineEnd());
        assertEquals(BASELINE_ID, clone.getBaselineID());
        assertEquals("500", ((TestWorkingTimeCalendarProvider)clone.getWorkingTimeCalendarProvider()).getID());
        assertEquals(LOAD_TIME, clone.getLoadTime());
    }

    private Schedule setupSchedule(SetMethodWatcher watcher) {
        Schedule schedule = new Schedule();

        schedule.setFinderFilterList(FINDER_FILTER_LIST);
        watcher.methodCalled("setFinderFilterList");
        schedule.setEndDateFilter(END_DATE_FILTER);
        watcher.methodCalled("setEndDateFilter");
        watcher.skipMethod("setScheduleEndDate");
        schedule.setAutocalculateTaskEndpoints(AUTOCALCULATE_TASK_ENDPOINTS);
        watcher.methodCalled("setAutocalculateTaskEndpoints");
        schedule.setDescription(DESCRIPTION);
        watcher.methodCalled("setDescription");
        schedule.setID(ID);
        watcher.methodCalled("setID");
        schedule.setTimeZone(TIME_ZONE);
        watcher.methodCalled("setTimeZone");
        schedule.setOrder(ORDER);
        watcher.methodCalled("setOrder");
        schedule.setOrderDirection(ORDER_DIRECTION);
        watcher.methodCalled("setOrderDirection");
        schedule.setHierarchyView(HIERARCHY_VIEW_EXPANDED);
        watcher.methodCalled("setHierarchyView");
        schedule.setFilterPhaseID(FILTER_PHASE_ID);
        watcher.methodCalled("setFilterPhaseID");
        schedule.setDefaultCalendarID(DEFAULT_CALENDAR_ID);
        watcher.methodCalled("setDefaultCalendarID");
        schedule.setName(NAME);
        watcher.methodCalled("setName");
        schedule.setMaximumEntries(MAXIMUM_ENTRIES);
        watcher.methodCalled("setMaximumEntries");
        schedule.setFilterOpenItemsOnly(FILTER_OPEN_ITEMS_ONLY);
        watcher.methodCalled("setFilterOpenItemsOnly");
        schedule.setTaskType(TYPE);
        watcher.methodCalled("setTaskType");
        schedule.setBaselineID(BASELINE_ID);
        watcher.methodCalled("setBaselineID");
        schedule.setDefaultTaskCalculationType(FIXED_UNIT_EFFORT_DRIVEN);
        watcher.methodCalled("setDefaultTaskCalculationType");
        schedule.setScheduleStartDate(SCHEDULE_START_DATE);
        watcher.methodCalled("setScheduleStartDate");
        schedule.setScheduleEndDate(SCHEDULE_END_DATE);
        watcher.methodCalled("setScheduleEndDate");
        schedule.setStartDateFilter(START_DATE_FILTER);
        watcher.methodCalled("setStartDateFilter");
        watcher.skipMethod("setScheduleStartDate");  //Deprecated
        schedule.setSpace(SPACE);
        watcher.methodCalled("setSpace");
        schedule.setEntryMap(ENTRY_MAP);
        watcher.methodCalled("setEntryMap");
        schedule.setLastModified(LAST_MODIFIED);
        watcher.methodCalled("setLastModified");
        schedule.setLastModifiedDisplayName(LAST_MODIFIED_DISPLAY_NAME);
        watcher.methodCalled("setLastModifiedDisplayName");
        schedule.setLastModifiedByID(LAST_MODIFIED_BY_ID);
        watcher.methodCalled("setLastModifiedByID");
        schedule.setBaselineStart(BASELINE_START);
        watcher.methodCalled("setBaselineStart");
        schedule.setBaselineEnd(BASELINE_END);
        watcher.methodCalled("setBaselineEnd");
        schedule.setWorkingTimeCalendarProvider(PROVIDER);
        watcher.methodCalled("setWorkingTimeCalendarProvider");
        watcher.skipMethod("setWorkPercentComplete");
        watcher.skipMethod("setPhase");
        watcher.skipMethod("setStartDate");
        watcher.skipMethod("setEndDate");
        schedule.setLoadTime(LOAD_TIME);
        watcher.methodCalled("setLoadTime");
        schedule.setWorkingTimeCalendarProvider(WORKING_TIME_CALENDAR_PROVIDER);
        watcher.methodCalled("setWorkingTimeCalendarProvider");
        
        
        schedule.setLatestStartDate(LATEST_START_DATE);
        watcher.methodCalled("setLatestStartDate");
        schedule.setLatestFinishDate(LATEST_FINISH_DATE);
        watcher.methodCalled("setLatestFinishDate");
        schedule.setEarliestFinishDate(EARLIEST_FINISH_DATE);
        watcher.methodCalled("setEarliestFinishDate");
        schedule.setEarliestStartDate(EARLISET_START_DATE);
        watcher.methodCalled("setEarliestStartDate");
        schedule.setStartConstraint(START_CONSTRAINT);
        watcher.methodCalled("setStartConstraint");
        schedule.setStartConstraintDate(START_CONSTRAINT_DATE);
        watcher.methodCalled("setStartConstraintDate");
        schedule.setShared(SHARED);
        watcher.methodCalled("setShared");
        
        watcher.skipMethod("setFieldsFromSchedule");
        watcher.skipMethod("setFinderListener");
        watcher.skipMethod("setWarnings");
              
        watcher.skipMethod("setResourceCalendar");
        watcher.skipMethod("setHoursPerDay");
        watcher.skipMethod("setHoursPerWeek");
        watcher.skipMethod("setUnAssignedWorkcapture");
        watcher.skipMethod("setDaysPerMonth");
        watcher.skipMethod("setEditingWarning");
        return schedule;
    }

    /**
     * Tests both {@link Schedule#setDefaultTaskCalculationType(net.project.schedule.calc.TaskCalculationType)}
     * and {@link Schedule#getDefaultTaskCalculationType()}.
     */
    public void testDefaultTaskCalculationTypeProperty() {
        Schedule schedule;

        // Default is null
        // Value for new schedules initialized at storage time
        assertNull(new Schedule().getDefaultTaskCalculationType());

        // Test each get results in the set value
        schedule = new Schedule();
        schedule.setDefaultTaskCalculationType(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN);
        assertEquals(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN, schedule.getDefaultTaskCalculationType());

        schedule.setDefaultTaskCalculationType(TaskCalculationType.FIXED_UNIT_NON_EFFORT_DRIVEN);
        assertEquals(TaskCalculationType.FIXED_UNIT_NON_EFFORT_DRIVEN, schedule.getDefaultTaskCalculationType());

        schedule.setDefaultTaskCalculationType(TaskCalculationType.FIXED_DURATION_EFFORT_DRIVEN);
        assertEquals(TaskCalculationType.FIXED_DURATION_EFFORT_DRIVEN, schedule.getDefaultTaskCalculationType());

        schedule.setDefaultTaskCalculationType(TaskCalculationType.FIXED_DURATION_NON_EFFORT_DRIVEN);
        assertEquals(TaskCalculationType.FIXED_DURATION_NON_EFFORT_DRIVEN, schedule.getDefaultTaskCalculationType());

        schedule.setDefaultTaskCalculationType(TaskCalculationType.FIXED_WORK);
        assertEquals(TaskCalculationType.FIXED_WORK, schedule.getDefaultTaskCalculationType());
    }

    public void testSerialize() throws ClassNotFoundException {
        SetMethodWatcher watcher = new SetMethodWatcher(Class.forName("net.project.schedule.Schedule"));
        Schedule schedule = setupSchedule(watcher);
        Schedule deserialized = null;

        //Now do the serialization and restoration
        try {
            FileOutputStream fos = new FileOutputStream("test.obj");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(schedule);

            FileInputStream fis = new FileInputStream("test.obj");
            ObjectInputStream ois = new ObjectInputStream(fis);
            deserialized = (Schedule) ois.readObject();
        } catch (IOException e) {
            fail("Unable to complete schedule serialize unit test due to IOException: " + e);
        } catch (ClassNotFoundException e) {
            fail("Unable to complete schedule serialize unit test due to ClassNotFoundException" + e);
        }

        checkEquals(deserialized);
    }
}
