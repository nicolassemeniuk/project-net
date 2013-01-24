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

 /*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.datatransform.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses a CSV file.
 * The CSV file is assumed to use commas as data separators.  Data values may be
 * surrounded by double-quotes.  This is necessary if the data value contains
 * a comma itself. Actual double-quote data values are represented by a pair
 * of double-quote characters.
 *
 * @author Deepak
 * @since emu
 */
public class CSV implements Serializable {

    //
    // Static members
    //

    /**
     * This character is inserted in place of double-quote characters when
     * preparing a line.  It is assumed not to occur in any real value.
     */
    private static final char REPLACED_DOUBLE_QUOTE = '\u0001';

    /**
     * This character is inserted in place of embedded comma characters in
     * values.  It is assumed not to occur in any real value.
     */
    private static final char REPLACED_COMMA = '\u0002';

    /**
     * Splits a line into an array assuming elements are delimited by a comma.
     * All commas are assumed to be delimiters.  No processing is performed
     * on the data values.
     * @param str the comma delimited values to split
     * @return an array where each element is one of the delimited values
     */
    private static String[] split(String str) {

        List elementList = new ArrayList();

        // Add each element to the list
        int start = 0;
        int stop;
        while ((stop = str.indexOf(',', start)) > -1) {
            elementList.add(str.substring(start, stop));
            start = stop + 1;
        }

        // Now add the remaining element after the last comma
        elementList.add(str.substring(start));

        return (String[]) elementList.toArray(new String[]{});
    }

    //
    // Instance members
    //

    /**
     * Maintains the Rows from the CSV File.
     */
    private final CSVRows csvRows = new CSVRows();

    /**
     * Maintains the Columns from the CSV File.
     */
    private final CSVColumns csvCols = new CSVColumns();

    /**
     * Provides a collection of every data value.
     */
    private final CSVCellCollection csvCellCollection = new CSVCellCollection();

    /**
     * Provides a collection of erroneous data values.
     */
    private final CSVErrorCellCollection csvErrorCellCollection = new CSVErrorCellCollection();

    /**
     * Provides an incremental row count.
     */
    private long rowNumber = 0;

    /**
     * Provides an incremental ID value assigned to each CSV data value.
     */
    private long csvDataValueID = 0;

    /**
     * Provides the character encoding that should be used to read the CSV
     * file.
     */
    private String encoding = null;

    /**
     * Creates a new CSV parser.
     */
    public CSV() {
        // Do nothing
    }

    /**
     * Returns the parsed CSV columns
     * @return the column data
     * @see #parse
     */
    public CSVColumns getCSVColumns() {
        return this.csvCols;
    }

    /**
     * Returns the parsed CSV as rows.
     * @return the row data
     * @see #parse
     */
    public CSVRows getCSVRows() {
        return this.csvRows;
    }


    /**
     * Returns all the CSV data parsed from the CSV file.
     * @return the CSV data
     * @see #parse
     */
    public CSVCellCollection getCSVCellCollection() {
        return this.csvCellCollection;
    }

    /**
     * Returns erroneous CSV data.
     * Note: This returns a mutable value; it is not populated by {@link #parse}.
     * @return the CSV data
     * @see CSVCellValidator#validate
     */
    public CSVErrorCellCollection getCSVErrorCellCollection() {
        return this.csvErrorCellCollection;
    }

    /**
     * Reads & parses the CVS file.
     * @param file to be parsed
     * @throws CSVException
     */
    public void parse(File file) throws CSVException {

        this.csvCellCollection.getCSVCells().clear();
        this.rowNumber = 0;
        this.csvDataValueID = 0;

        this.csvRows.clear();
        this.csvCols.clear();
        this.csvErrorCellCollection.getCSVErrorCells().clear();

        BufferedReader reader = null;

        try {
            // Create a buffered reader to read the file
            // Based on the specified encoding
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));

            // Now read all lines
            while (decodeLine(reader) != -1) {
                // Nothing to do; decodeLine does all the work for each line
            }


        } catch (UnsupportedEncodingException uee) {
            // Specified character encoding is not supported in this runtime
            throw new CSVException("Character encoding " + encoding + " not supported: " + uee, uee);

        } catch (FileNotFoundException fnfe) {
            // The file to be parsed could not be opened for reading
            throw new CSVException("File " + file + " not found: " + fnfe, fnfe);

        } catch (IOException e) {
            // Some error reading the file
            throw new CSVException("Error reading CSV file: " + e, e);

        } finally {

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // Do nothing; if this was the only problem, then the
                    // CSV file will have been read
                    // If there was some other problem, we'll let it propagate
                }
            }

        }

    }

    /**
     * Decodes a comma delimited line.
     * @param reader the reader to read from
     * @return 0 if a line was read, -1 if the end of the reader has been reached
     * @throws IOException if there is a problem reading
     */
    private int decodeLine(Reader reader) throws IOException {

        // Prepare the line by replacing real double-quote values and embedded
        // commas with special characters
        String line = prep(reader);

        if (line == null) {
            // This signifies we have reached the end of the file
            return -1;
        }

        // Convert the line to an array
        // The number of elements in the array will be equal to the number of
        // comma-separated values in the line
        // Elements will have had double-quote and commas replaced by special
        // symbols
        String str[] = split(line);

        if (rowNumber == 1) {
            // The first row is assumed to be the columns

            // Each element is assumed to be the column name
            for (int i = 0; i < str.length; i++) {
                CSVColumn col = new CSVColumn(
                        String.valueOf(i),
                        str[i].replace(REPLACED_DOUBLE_QUOTE, '"').replace(REPLACED_COMMA, ','),
                        csvCellCollection,
                        new CSVColumnNumber(String.valueOf(i)));

                csvCols.add(col);
            }

        } else {
            // A non-column row

            // Actual row numbers begin at zero
            CSVRowNumber csvRowNum = new CSVRowNumber(String.valueOf(rowNumber - 1));
            CSVRow csvRow = new CSVRow(String.valueOf(rowNumber - 1), csvCellCollection, csvRowNum);
            csvRows.add(csvRow);

            // Each element is a data value
            // If there are more elements in this row than columns then additional
            // elements are ignored
            for (int i = 0; i < str.length; i++) {

                // Grab the corresponding column for this data element
                CSVColumn currentColumn = csvCols.getCSVColumnForID(String.valueOf(i));

                if (currentColumn != null) {
                    // We have a column definition for this element

                    // We create a data value; note that if any column contains
                    // two more more data values that are textually identical
                    // then we get an existing CSVDataValue, otherwise a new
                    // one is created
                    // This is used to produce the distinct values in a column
                    // when mapping data values
                    CSVDataValue dataValue = currentColumn.getCSVDataValue(
                            str[i].replace(REPLACED_DOUBLE_QUOTE, '"').replace(REPLACED_COMMA, ','),
                            String.valueOf(csvDataValueID++));

                    // Add a new cell based on the current column, row number and
                    // data values
                    csvCellCollection.add(new CSVCell(currentColumn.getCSVColumnNumber(), csvRowNum, dataValue));
                }

            }

        }

        // Return something to indicate that we're not done
        return 0;
    }

    /**
     * Prepare a CSV line for splitting along commas.
     * Any commas found within a value (i.e. a comma that is not a separator)
     * are replaced with {@link #REPLACED_COMMA}.
     * Any double-quotes found within a value (i.e. paired double-quotes that
     * are not there to escape values) are replaced with {@link #REPLACED_DOUBLE_QUOTE}.
     * It assumed that those characters will be replaced before utilizing the
     * actual value.
     * @param reader the reader to read characters from
     * @return the next line with escape characters removed and embedded double-quote
     * and comma characters replaced
     * @throws IOException if there is a problem reading from the reader
     */
    private String prep(Reader reader) throws IOException {

        // Current character under examination.
        char currentChar;

        // Next character (look-ahead).
        char nextChar;

        // State - false if we're outside a field, true if we're within one.
        boolean inner = false;

        // The line under constructions
        StringBuffer currentLine = new StringBuffer();

        // Grab a character
        currentChar = (char) reader.read();
        if (currentChar == (char) -1) {
            // We reached the end
            nextChar = (char) -1;
        } else {
            // Look ahead to the next character
            nextChar = (char) reader.read();
        }

        while (nextChar != -1) {

            switch (currentChar) {
                // Ignore newlines and EOFs. Return what we have.
                case '\n':
                case '\r':
                    rowNumber++;
                case (char) -1:
                    if (currentLine.length() == 0) {
                        // We reached the end of the file and we did not
                        // read any line
                        return null;

                    } else {
                        // We reached the end of the file but we have a line
                        return currentLine.toString();

                    }

                // Ignore quotes, but note that they start a string field.
                case '"':
                    inner = true;
                    break;

                // Accumulate separator chars and anything else in the line
                case ',':
                default:
                    currentLine.append(currentChar);
                    break;
            }

            // We're parsing a delimited value
            // Iterate over characters until we find another single double-quote
            // If we find two double-quotes, then it is replaced with the special
            // double-quote character
            // Embedded commas are replaced with the special comma character
            // Newlines and all other characters are appended to the result
            while (inner == true) {
                currentChar = nextChar;
                nextChar = (char) reader.read();

                switch (currentChar) {
                    case '"':
                        // If there are two, we keep it (encoded).  Else we're outa here.
                        if (nextChar != '"') {
                            inner = false;
                            break;
                        } else {
                            currentLine.append(REPLACED_DOUBLE_QUOTE);
                            // get a char, 'cause we ate the nc.
                            nextChar = (char) reader.read();
                            break;
                        }

                    case ',':
                        // Encode embedded commas so they aren't valid sparators.
                        currentLine.append(REPLACED_COMMA);
                        break;

                    case (char) -1:
                        // eof's at this point are unexpected and unwelcome.
                        inner = false;
                        break;

                    case '\n':
                        // Save it; does not signify EOL while inside a quoted
                        // value
                    case '\r':
                        // Save it; does not signify EOL while inside a quoted
                        // value
                    default:
                        // just a char, save it.
                        currentLine.append(currentChar);
                        break;

                }
            }

            // This bit at then keeps things sane for UNIX & DOS/Windows text files.
            currentChar = nextChar;

            if (currentChar == '\n') {
                nextChar = '\n';
                currentChar = '\n';

            } else if (currentChar == (char) -1) {
                // We reached the end of the file
                nextChar = (char) -1;

            } else {
                // More to read
                nextChar = (char) reader.read();
            }
        }

        if (currentLine.length() == 0) {
            // We didn't read anyting
            return null;
        }

        return currentLine.toString();
    }


    /**
     * Sets the CharacterEncoding.  The canonical name of the character encoding
     * specified here is the same name as can be found in
     * {@link java.lang.String#String(byte[], java.lang.String)}.  Any character
     * encoding specified must be supported both by the version of java that is
     * being used, and by the database.
     *
     * @param encoding a <code>String</code> value containing the canonical name
     * for the character encoding that the CSV file is encoded in.
     */
    public void setCharacterEncoding(String encoding) {
        this.encoding = encoding;
    }
}
