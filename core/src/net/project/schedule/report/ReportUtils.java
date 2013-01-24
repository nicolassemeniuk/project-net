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
package net.project.schedule.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.project.base.finder.FinderSorter;
import net.project.report.ReportCompositeComparator;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.TaskFinder;

public class ReportUtils {

	public static void sortTasks(List detailedData, List<FinderSorter> sorterList){
	       List<Comparator> comparatorList = new ArrayList<Comparator>();
	        //List<FinderSorter> sorterList = getSorterList().getAllSorters();
	        for (FinderSorter sorter : sorterList){
	        	if (sorter.isSelected()){
	        		final int direction = sorter.isDescending() ? -1 : 1;
	        		Comparator comparator = null;
	        		if (sorter.getSelectedColumn() == TaskFinder.NAME_COLUMN) {
	        			comparator = new Comparator<ScheduleEntry>(){
	        				public int compare(ScheduleEntry o1, ScheduleEntry o2){        				  
	        					return direction * o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());        				  
	          			  	}
	          		  	};
	          	     } else if (sorter.getSelectedColumn() == TaskFinder.TYPE_COLUMN) {
	         			comparator = new Comparator<ScheduleEntry>(){
	        				public int compare(ScheduleEntry o1, ScheduleEntry o2){        				  
	        					return direction * o1.getTaskType().getID().compareTo(o2.getTaskType().getID());        				  
	          			  	}
	          		  	};                  	    	 
	          	     } else if (sorter.getSelectedColumn() == TaskFinder.DATE_START_COLUMN) {
	          			comparator = new Comparator<ScheduleEntry>(){
	         				public int compare(ScheduleEntry o1, ScheduleEntry o2){        				  
	         					return direction * o1.getStartTime().compareTo(o2.getStartTime());        				  
	           			  	}
	           		  	};                  	    	 
	          	     } else if (sorter.getSelectedColumn() == TaskFinder.DATE_FINISH_COLUMN) {
	           			comparator = new Comparator<ScheduleEntry>(){
	          				public int compare(ScheduleEntry o1, ScheduleEntry o2){        				  
	          					return direction * o1.getEndTime().compareTo(o2.getEndTime());        				  
	            			  	}
	            		  	};                  	    	 
	          	     } else if (sorter.getSelectedColumn() == TaskFinder.WORK_COMPLETE_COLUMN) {
	            			comparator = new Comparator<ScheduleEntry>(){
	           				public int compare(ScheduleEntry o1, ScheduleEntry o2){        				  
	           					return direction * o1.getWorkCompleteTQ().getAmount().compareTo(o2.getWorkCompleteTQ().getAmount());        				  
	             			  	}
	             		  	};                  	    	 
	          	     } else if (sorter.getSelectedColumn() == TaskFinder.WORK_COLUMN) {
	         			comparator = new Comparator<ScheduleEntry>(){
	        				public int compare(ScheduleEntry o1, ScheduleEntry o2){    				  
	        					return direction * o1.getWorkTQ().getAmount().compareTo(o2.getWorkTQ().getAmount());        				  
	          			  	}
	          		  	};                  	    	 
	          	     } else if (sorter.getSelectedColumn() == TaskFinder.WORK_PERCENT_COMPLETE_COLUMN) {
	          			comparator = new Comparator<ScheduleEntry>(){
	         				public int compare(ScheduleEntry o1, ScheduleEntry o2){        				  
	         					return direction * new Double(o1.getPercentComplete()).compareTo(new Double(o2.getPercentComplete()));        				  
	           			  	}
	           		  	};                  	    	 
	           	     }
	        		comparatorList.add(comparator);
	        	}
	        }
	        
		     	if (comparatorList.size() > 0){        
		     		Comparator composite = new ReportCompositeComparator(comparatorList);
		     		Collections.sort(detailedData, composite);
		     	} 		
	}
	
}
