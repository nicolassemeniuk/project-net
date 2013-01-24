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
CREATE OR REPLACE PACKAGE BODY BASE
IS

-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ---------  ------------------------------------------
-- Phil        08-May-00  Creation.


--------------------------------------------------------------------
-- GET_OBJECT_TYPE
--------------------------------------------------------------------

-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ---------  ------------------------------------------
-- Phil        08-May-00  Creation.

FUNCTION GET_OBJECT_TYPE
( IN_OBJECT_ID PN_OBJECT.OBJECT_ID%TYPE)
RETURN PN_OBJECT.OBJECT_TYPE%TYPE
AS
    V_OBJECT_TYPE PN_OBJECT.OBJECT_TYPE%TYPE;
BEGIN
    SELECT OBJECT_TYPE INTO V_OBJECT_TYPE FROM PN_OBJECT WHERE OBJECT_ID = IN_OBJECT_ID;

    RETURN V_OBJECT_TYPE;
END;

--------------------------------------------------------------------
-- GET_DOC_CONTAINER_FOR_OBJECT
--------------------------------------------------------------------

-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ---------  ------------------------------------------
-- Phil        08-May-00  Creation.

FUNCTION GET_DOC_CONTAINER_FOR_OBJECT
    (IN_OBJECT_ID PN_OBJECT.OBJECT_ID%TYPE)
RETURN PN_OBJECT.OBJECT_ID%TYPE
AS
    V_CONTAINER_ID PN_DOC_CONTAINER.DOC_CONTAINER_ID%TYPE;
BEGIN
    SELECT DOC_CONTAINER_ID INTO V_CONTAINER_ID FROM PN_DOC_CONTAINER_HAS_OBJECT
    WHERE OBJECT_ID = IN_OBJECT_ID;

    RETURN V_CONTAINER_ID;
END;

/**
 * GENERATES A DATABASE-UNIQUE OBJECT ID AND INSERTS A ROW IN PN_OBJECT.
 * DOES NOT COMMIT OR ROLLBACK.
 * I_OBJECT_TYPE THE TYPE OF OBJECT TO CREATE
 * I_CREATOR_PERSON_ID THE ID OF THE PERSON CREATING THE OBJECT
 * I_RECORD_STATUS THE RECORD STATUS OF THE OBJECT TO CREATE
 * RETURNS THE UNIQUE OBJECT ID
 * RAISES ANY EXCEPTIONS
 */
FUNCTION      CREATE_OBJECT
(
    I_OBJECT_TYPE  IN VARCHAR2,
    I_CREATOR_PERSON_ID IN NUMBER,
    I_RECORD_STATUS IN VARCHAR2
)
RETURN PN_OBJECT.OBJECT_ID%TYPE
IS

    V_OBJECT_ID PN_OBJECT.OBJECT_ID%TYPE;

    ERR_NUM NUMBER;
    ERR_MSG VARCHAR2(120);
    STORED_PROC_NAME VARCHAR2(100):= 'CREATE_OBJECT';

BEGIN
    -- get new object_id for the sequence generator
    SELECT PN_OBJECT_SEQUENCE.NEXTVAL INTO V_OBJECT_ID FROM DUAL;

    -- Create new pn_object
    INSERT INTO PN_OBJECT
        (OBJECT_ID, OBJECT_TYPE, DATE_CREATED, CREATED_BY, RECORD_STATUS)
    VALUES
        (V_OBJECT_ID, I_OBJECT_TYPE, SYSDATE, I_CREATOR_PERSON_ID, I_RECORD_STATUS);

    RETURN V_OBJECT_ID;


    -- Log exceptions
    EXCEPTION
    WHEN OTHERS THEN
        BASE.LOG_ERROR(STORED_PROC_NAME, SQLCODE, SQLERRM);
        RAISE;
END;

--------------------------------------------------------------------
-- LOG_ERROR (autonomous transaction)
-- Writes to the error log.  Truncates any varchar parameters
-- to appropriate length before inserting row.
--------------------------------------------------------------------
    PROCEDURE LOG_ERROR
        ( I_STORED_PROC_NAME    IN VARCHAR2,
          I_ERROR_NUMBER        IN NUMBER,
          I_ERROR_MESSAGE       IN VARCHAR2)
    IS
        PRAGMA AUTONOMOUS_TRANSACTION;
        V_STORED_PROC_NAME  PN_SP_ERROR_LOG.STORED_PROC_NAME%TYPE := SUBSTR(I_STORED_PROC_NAME, 1, 60);
        V_ERROR_MESSAGE     PN_SP_ERROR_LOG.ERROR_MSG%TYPE        := SUBSTR(I_ERROR_MESSAGE, 1, 240);
    BEGIN
        INSERT INTO PN_SP_ERROR_LOG
            (TIMESTAMP, STORED_PROC_NAME, ERROR_CODE, ERROR_MSG)
        VALUES
            (SYSDATE, V_STORED_PROC_NAME, I_ERROR_NUMBER, V_ERROR_MESSAGE);
        COMMIT;
    END LOG_ERROR;

   FUNCTION CLOB_LIKE
       (I_CLOB IN CLOB,
        I_SEARCH IN VARCHAR2,
        I_IS_CASE_SENSITIVE IN NUMBER)
      RETURN NUMBER
   IS

      -- PL/SQL (as opposed to SQL) can cope with varchar2s up to 32K (on 8i).
      -- This may not be the case on older versions, in which case set the
      -- following constant to 4000
      C_MAXVARCHAR2   CONSTANT NUMBER := 32767;
      L_OFFSET                 NUMBER := 1;
      L_LENGTH                 NUMBER;
      L_NEXTBITE               NUMBER;
      O_ANSWER                 NUMBER := 0;

   BEGIN

      -- Determine the length of the CLOB
      L_LENGTH := DBMS_LOB.GETLENGTH (I_CLOB);

      -- The offset between one bite and the next is less than the size of each
      -- bite. Bites need to overlap by (length of search string - 1) to allow
      -- for an occurrence of the string spanning the join.
      L_NEXTBITE := C_MAXVARCHAR2 + 1 - LENGTH (I_SEARCH);

      -- For a case insensitive search we must loop through the CLOB
      -- Look through the CLOB as a series of VARCHAR2 bites
      WHILE L_OFFSET <= L_LENGTH LOOP

          IF (I_IS_CASE_SENSITIVE > 0) THEN

              -- Perform case sensitive like
              IF DBMS_LOB.SUBSTR(I_CLOB, C_MAXVARCHAR2, L_OFFSET) LIKE I_SEARCH THEN
                  O_ANSWER := 1;
                  EXIT; -- A match has been found, so bang out of the loop
              END IF;

          ELSE

              -- Perform case insensitive like
              IF UPPER(DBMS_LOB.SUBSTR(I_CLOB, C_MAXVARCHAR2, L_OFFSET)) LIKE UPPER(I_SEARCH) THEN
                  O_ANSWER := 1;
                  EXIT; -- A match has been found, so bang out of the loop
              END IF;

          END IF;

          -- Find the start of the next bite.
          L_OFFSET := L_OFFSET + L_NEXTBITE;
      END LOOP;

      RETURN O_ANSWER;

   END;

END; -- Package Body BASE
/

