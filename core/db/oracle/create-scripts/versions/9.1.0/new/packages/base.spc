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
CREATE OR REPLACE PACKAGE BASE IS

-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ---------  ------------------------------------------
-- Phil        08-May-00  Creation.
-- Tim         18-Sep-00  Added Database Error Codes (mirror Java ones)
-- Tim         02-Oct-00  Added LOG_ERROR as an autonomous transaction
-- THIS is the base package which contains generally accessible functionality
--

    -- Error codes
    --3456789012345678901234567890
    OPERATION_SUCCESSFUL              CONSTANT NUMBER  := 0;
    UPDATE_RECORD_OUT_OF_SYNC         CONSTANT NUMBER  := 1000;
    UPDATE_RECORD_LOCKED              CONSTANT NUMBER  := 1010;
    PLSQL_EXCEPTION                   CONSTANT NUMBER  := 2000;
    DOC_UNIQUE_NAME_CONSTRAINT        CONSTANT NUMBER  := 5001;
    UNABLE_TO_EXTEND_INDEX            CONSTANT NUMBER  := -1654;
    -- End of error codes

    -- Record Status values
    ACTIVE_RECORD_STATUS            CONSTANT CHAR := 'A';
    DELETED_RECORD_STATUS           CONSTANT CHAR := 'D';
    PENDING_RECORD_STATUS           CONSTANT CHAR := 'P';

  FUNCTION GET_OBJECT_TYPE
     ( IN_OBJECT_ID PN_OBJECT.OBJECT_ID%TYPE)
     RETURN PN_OBJECT.OBJECT_TYPE%TYPE;

  FUNCTION GET_DOC_CONTAINER_FOR_OBJECT
    ( IN_OBJECT_ID PN_OBJECT.OBJECT_ID%TYPE)
    RETURN PN_OBJECT.OBJECT_ID%TYPE;

  FUNCTION  CREATE_OBJECT
(
    I_OBJECT_TYPE  IN VARCHAR2,
    I_CREATOR_PERSON_ID IN NUMBER,
    I_RECORD_STATUS IN VARCHAR2
) RETURN PN_OBJECT.OBJECT_ID%TYPE;

    /*
        THIS PROCEDURE WRITES AN ERORR TO PN_SP_ERROR_LOG
        IT IS AN AUTONOMOUS TRANSACTION WHICH MEANS IT CAN COMMIT
        AND ROLLBACK WITHOUT AFFECTING THE CALLING PROCEDURE AT ALL.
     */
    PROCEDURE LOG_ERROR
        ( I_STORED_PROC_NAME    IN VARCHAR2,
          I_ERROR_NUMBER        IN NUMBER,
          I_ERROR_MESSAGE       IN VARCHAR2);

   /*
    * INDICATES WHETHER THE SPECIFIED CLOB CONTAINS THE SPECIFIED SEARCH
    * STRING.
    * THIS WORKS ON ORACLE 8I BY PARSING THE CLOB IN CHUNKS.
    * @PARAM I_CLOB THE CLOB LOCATER TO SEARCH IN
    * @PARAM I_SEARCH THE STRING TO SEARCH FOR
    * @PARAM I_IS_CASE_SENSITIVE (OPTIONAL) 0 MEANS PERFORM A CASE INSENSITIVE
    * SEARCH.  THIS IS THE DEFAULT.  1 MEANS PERFORM A CASE SENSITIVE SEARCH
    * @RETURN 1 IF THE SEARCH STRING WAS FOUND; 0 OTHERWISE
    */
   FUNCTION CLOB_LIKE
       (I_CLOB IN CLOB,
        I_SEARCH IN VARCHAR2,
        I_IS_CASE_SENSITIVE IN NUMBER)
      RETURN NUMBER;

END; -- Package Specification BASE
/

