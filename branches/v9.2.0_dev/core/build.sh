# 
# Copyright 2000-2009 Project.net Inc.
# 
# This file is part of Project.net.
# Project.net is free software: you can redistribute it and/or modify it under the terms of 
# the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
# 
# Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
# without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
# See the GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License along with Project.net.
# If not, see http://www.gnu.org/licenses/gpl-3.0.html

#! /bin/csh
# core/build.sh
#
# Build the Project.net application using ant.
# Adds Ant taskdefs to the classpath before executing the standard Ant batch script

# Clear the CLASSPATH
# The build classpath is specified in build.xml; Ant will add its own classes.
# Add external dependencies (usually optional Ant tasks) to the classpath
# set CLASSPATH="../test/unit-test/lib/junit.jar"

# Execute the build
../tools/ant/bin/ant -emacs $* -f build.xml

