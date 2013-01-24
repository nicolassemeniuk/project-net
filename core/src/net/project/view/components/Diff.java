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
package net.project.view.components;

import net.project.wiki.diff.FileDiffResult;
import net.project.wiki.diff.FileLine;

import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

/*
 * Component used to show difference between two string objects
 */
public class Diff {
	
	@Parameter(required = true)
	private String leftString;
	
	@Parameter(required = true)
	private String rightString;
	
	@Parameter
	private boolean isCancelled;
	
	@Parameter
	private boolean isIgnoreLeadSpace;
	
	@Property
	private FileLine currentLeftFileLine;
	
	@Property
	private FileLine currentRightFileLine;
	
	@Property
	private FileLine[] leftFileLines;
	
	@Property
	private FileLine[] rightFileLines;
	
	@BeginRender
	void difference(){
		FileDiffResult diffResult = net.project.wiki.diff.Diff.diff(leftString, rightString, isCancelled, isIgnoreLeadSpace);
		diffResult = net.project.wiki.diff.Diff.format(diffResult);
		leftFileLines =  diffResult.getLeftFile().getLines();
		rightFileLines =  diffResult.getRightFile().getLines();
	}
}
