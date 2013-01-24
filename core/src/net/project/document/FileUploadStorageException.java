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

 package net.project.document;

public class FileUploadStorageException extends DocumentException {

    private final String NAME = "FileUploadStorageException";


    public FileUploadStorageException() {
        super();
        setName(this.NAME);
    }

    public FileUploadStorageException(String message) {

        super(message);

        setName(this.NAME);
        setMessage(message);

    }

    public FileUploadStorageException(String message, String severity) {

        super(message);

        setName(this.NAME);
        setMessage(message);
        setSeverity(severity);

        log();
    }

    public FileUploadStorageException(String message, Throwable cause) {
        super(message, cause);
    }

}
