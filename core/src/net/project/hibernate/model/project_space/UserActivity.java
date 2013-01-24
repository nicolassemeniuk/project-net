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
package net.project.hibernate.model.project_space;


public enum UserActivity {
	
    TIME_OUT_MILLIS(10 * 1000 * 60), 
    
    INACTIVE_MILLIS( 15 * 1000 *60),
    
    ASSUME_LOG_OUT_MILLIS(30 * 1000 * 60) ;	

    
    private int fActivity;

	/**
	 * @return Returns the fActivity.
	 */
	public int getFActivity() {
		return fActivity;
	}

	/**
	 * @param activity The fActivity to set.
	 */
	public void setFActivity(int activity) {
		fActivity = activity;
	}

	private UserActivity (int activity){
		fActivity = activity;
	}
    
}
