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

 package net.project.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import net.project.base.PnetException;

import org.apache.tapestry5.upload.services.UploadedFile;
import org.springframework.web.multipart.MultipartFile;

/**
 * Provides utility methods used when manipulating files and paths.
 *
 * @author Tim Morrow
 * @since 02/2002
 */
public class FileUtils {
    /**
     * This field contains the maximum size in bytes that can be copied at a 
     * time using FileChannel.transferTo().  This limit is imposed by Microsoft
     * Windows.  It has not been verified that this limit (or any limit) exists
     * on any other operating systems.
     *
     * Here are the magic numbers we've found thus far:
     *
     * Windows XP: (64 * 1024 * 1024) - (32 * 1024)
     * Windows 2000 Server: We've found conflicting numbers here.
     * On a customer site, 32Mb worked but not on our server
     *
     * We've chosen 16Mb.
     */
    private static final int COPY_CHUNK_SIZE = (16 * 1024 * 1024);

    /**
     * Ensures that the specified path's last non-space character is a "slash".
     * <p>
     * The result is determined as follows:
     * <li>When <code>path</code> is <code>null</code>, result is <code>null</code>
     * <li>When <code>path</code> is the empty string (after trimming), result is the empty string
     * <li>When the last non-whitespace character of <code>path</code> is one of <code>\</code>, <code>/</code> or
     * <code>{@link java.io.File#separatorChar}</code>, result is the <code>path</code> with
     * trailing whitespace characters removed
     * <li>Otherwise, result is <code>path + {@link java.io.File#separatorChar}</code>; no trimming occurs
     * in this case
     * <br>
     * Examples:
     * <li>path = <code>null</code>, result is <code>null</code>
     * <li>path = <code>""</code>, result is <code>""</code>
     * <li>path = <code>" "</code>, result is <code>""</code>
     * <li>path = <code>"/ "</code>, result is <code>"/"</code>
     * <li>path = <code>"c:\temp\"</code>, result = <code>"c:\temp\"</code>
     * <li>path = <code>"c:\temp/"</code>, result = <code>"c:\temp/"</code>
     * <li>path = <code>"c:\temp"</code>, result depends on platform, but likely <code>"c:\temp\"</code> on
     * Windows or <code>"c:\temp/"</code> on Unix
     * <li>path = <code>"temp"</code>,  result depends on platform, but likely <code>"temp\"</code> on
     * Windows or <code>"temp/"</code> on Unix
     * </p>
     * @param path the path to check for a trailing slash
     * @return the resultant path, including a trailing slash
     */
    public static String ensureTrailingSlash(String path) {

        String result = null;

        if (path != null) {

            if (path.trim().length() == 0) {
                result = "";

            } else {

                String tempPath = path;
                tempPath = tempPath.replace('\\', File.separatorChar);
                tempPath = tempPath.replace('/', File.separatorChar);

                int lastSeparatorPos = tempPath.lastIndexOf(File.separatorChar);
                if (lastSeparatorPos >= 0) {
                    // We found the last file separator

                    if (lastSeparatorPos == (path.length() - 1)) {
                        // Already at end
                        // Return unmodified path
                        result = path;

                    } else {
                        // Separator not at end

                        if (path.substring(lastSeparatorPos + 1).trim().length() == 0) {
                            // All spaces after last file separator
                            // Means it is really at the end
                            // We drop the trailing whitespace
                            // No need to add a separator
                            result = path.substring(0, lastSeparatorPos + 1);

                        } else {
                            // there are non-whitespace characters after the last
                            // separator
                            // This means it is not a trailing separator
                            // Add one
                            result = path + File.separator;
                        }

                    }


                } else {
                    // There is no file separator at all
                    result = path + File.separator;

                }

            }

        }

        return result;
    }

    /**
     * Returns the file extension of the filename in the specified string.
     * The file extension is defined as the text following the last period (".")
     * to the end of the string (excluding the period itself).
     * If the string has no extension or is <code>null</code>, the empty string
     * is returned.  If the period is the last character then the empty string
     * is returned
     *
     * @param s a <code>String</code> value containing a filename in which we
     * are going to look for the file extension.
     * @return the file extension (excluding ".") or the empty string
     */
    public static String getFileExt(String s) {

        String extension = null;

        if (s == null) {
            extension = "";

        } else {

            String name = new File(s).getName();
            int startPos = name.lastIndexOf(".") + 1;

            if (startPos > 0 && startPos < name.length()) {
                extension = name.substring(startPos);
            } else {
                extension = "";
            }
        }

        return extension;
    }

    /**
     * Copies a physical File on disk.
     * @param sourceFilePath the path to the source file; this file must exist
     * and be readable
     * @param targetFilePath the path to the target file; this file must not
     * already exist (but all directories in the path <b>must</b> exist
     * @throws NullPointerException if source file or target file path are null
     * @throws IllegalArgumentException if source file or target file path
     * are empty
     * @throws IOException if the source file cannot be read or the target
     * file exists or some other problem occurs copying
     */
    public static void copy(String sourceFilePath, String targetFilePath)
            throws IOException {

        if (sourceFilePath == null || targetFilePath == null) {
            throw new NullPointerException("Source file and target file are required");
        }

        if (sourceFilePath.trim().length() == 0 || targetFilePath.trim().length() == 0) {
            throw new IllegalArgumentException("Source file and target file are required");
        }

        copy(new File(sourceFilePath), new File(targetFilePath));

    }

    /**
     * Copies a physical File on disk.
     * @param sourceFile the abstract file representing the file to copy;
     * this file must exist and be readable
     * @param targetFile the abstract file representing the new file to create;
     * this file must not already exist
     * @throws IOException if the source file cannot be read or the target
     * file exists or some other problem occurs copying
     */
    public static void copy(File sourceFile, File targetFile) throws IOException {

    	//if we are running kernel 2.6+ with channels error
    	boolean linux26 = false;
    	
        if (sourceFile == null || targetFile == null) {
            throw new NullPointerException("Source file and target file are required");
        }

        // Check that source file is readable
        if (!sourceFile.canRead()) {
            throw new IOException("Unable to read file: " + sourceFile.getAbsolutePath());
        }

        // Check that target file does not exist
        if (targetFile.exists()) {
            throw new IOException("Error copying file, target file already exists: " + targetFile.getAbsolutePath());
        }

        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {

            fis = new FileInputStream(sourceFile);
            fos = new FileOutputStream(targetFile);
            FileChannel fcin = fis.getChannel();
            FileChannel fcout = fos.getChannel();

            // Do the file copy
            //
            // Normally, you'd expect to just be able to do:
            //   fcin.transferTo(0, fcin.size(), fcout);
            // to transfer any file.  This will only work with files up to
            // up to 64Mb-32kb in size.  After this size it would throw an
            // error after allocating the size necessary.
            int maxCount = COPY_CHUNK_SIZE;
            long size = fcin.size();
            long position = 0;
            while (position < size) {
                position += fcin.transferTo(position, maxCount, fcout);
            }


            fcin.close();
            fcout.close();

        } catch (IOException ioe) {
        	//this could be kernel 2.6 problem
        	linux26 = true;
            //If we had a problem copying the file, delete the target file,
            //it is remarkably unlikely that it is correct.
            if (targetFile != null && targetFile.exists()) {
                targetFile.delete();
            }

            //throw ioe;
        } finally {
            // Attempt to clean up any open inputstreams
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ioe) {
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ioe) {
                }
            }
        }
        
        if (linux26) {
        	//alternative file copy
        	copyLinux(sourceFile, targetFile);
        }

    }
    /**
     * Alternative file copy code
     * for linux kernel 2.6+
     * 
     */
    public static void copyLinux(File sourceFile, File targetFile) throws IOException {
    	FileInputStream fis = null;
    	FileOutputStream fos = null;
    	try {
    		fis = new FileInputStream(sourceFile);
        	fos = new FileOutputStream(targetFile);
    		byte[] buf = new byte[1024];
            int len;
            while ((len = fis.read(buf)) > 0) {
                fos.write(buf, 0, len);
            }
    	} finally {    	
    		if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ioe) {
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ioe) {
                }
            }
    	}
    }

    /**
     * Moves a physical File on disk.
     * Performs this operation by copying the source file then deleting it.
     * The delete is performed only if the target file can be read after copying.
     * This method is more reliable than <code>{@link File#renameTo}</code>
     * since that method may not work across file systems on some operating systems.
     * @param sourceFile the abstract file representing the file to move;
     * this file must exist and be readable
     * @param targetFile the abstract file representing the new file to move to;
     * this file must not already exist
     * @throws IOException if the source file cannot be read or the target
     * file exists or some other problem occurs moving
     */
    public static void move(File sourceFile, File targetFile) throws IOException {

        copy(sourceFile, targetFile);
        if (targetFile.canRead()) {
            sourceFile.delete();
        }

    }

    /**
     * Resolves a file name from a path while determining the file separator
     * character from the path itself.
     * <p>
     * Using <code>new File(path).getName()</code> behaves correctly while
     * the path uses appropriate File separator characters for the current platform.
     * However, on Unix, the code <code>new File("c:\temp\file.txt").getName()</code>
     * will return the value <code>"c:\temp\file.txt"</code> instead of <code>"file.txt"</code>
     * because on Unix, <code>"\"</code> is a valid filename character. <br>
     * The goal of this method is to allow a filename to be extracted from a path
     * regardless of whether the path uses Windows- or Unix-style separator
     * characters. It does this by assuming the File separator is specified by
     * the first slash character in the path.
     * </p>
     * <p>
     * <b>Note:</b> If the path ends with a separator character, it is dropped
     * and the path treated as if the separator character was never there.
     * </p>
     * @param path the path from which to extract the file name
     * @return the file name; if there are no slashes in the path, the original
     * path value is returned
     * @throws NullPointerException if the path is null
     */
    public static String resolveNameFromPath(String path) {

        if (path == null) {
            throw new NullPointerException("path is required");
        }

        String resolvedName;

        int firstBackslashPosition = path.indexOf("\\");
        int firstForwardslashPostition = path.indexOf("/");

        if (firstBackslashPosition < 0 && firstForwardslashPostition < 0) {
            // There is no backslash or forwardslash
            // We return the original path
            resolvedName = path;

        } else {
            // There is a backslash or a forwardslash

            char separatorChar;

            if ((firstBackslashPosition < 0) || (firstForwardslashPostition >= 0 && firstForwardslashPostition < firstBackslashPosition)) {
                // No backslash or forwardslash before backslash
                separatorChar = '/';

            } else {
                // Must be a backslash that is before forward slash
                separatorChar = '\\';
            }

            if (path.lastIndexOf(separatorChar) == (path.length() - 1)) {
                // Path ends with a separator character
                // Strip it
                path = path.substring(0, path.length() - 1);
            }

            // The name is the text starting at the last separator, to the end
            // Note: The path may now be empty if it contained only a trailing separator
            // Substring will then be 0,0 which results in empty string
            resolvedName = path.substring(path.lastIndexOf(separatorChar)+1, path.length());
        }

        return resolvedName;
    }

    public static String commitUploadedFileToFileSystem(MultipartFile file) throws PnetException {
        /*
         Transfer the file to a location where it can be read -- this is
         expecially important on linux machines where the OS can't read the
         file and needs to transfer it to a place where it can be read.
        */
        try {
            String extension = getFileExt(file.getOriginalFilename());
            File newFileLocation = File.createTempFile("tmp", extension);
            file.transferTo(newFileLocation);
            return newFileLocation.getPath();
        } catch (Exception e) {
            throw new PnetException(e);
        }
    }

    /**
     * Modified version of above method for use with Tapestry 5 file uplading.
     * 
     * @param file
     * @return
     * @throws PnetException
     */
    public static String commitUploadedFileToFileSystem(UploadedFile file) throws PnetException {
        /*
         Transfer the file to a location where it can be read -- this is
         expecially important on linux machines where the OS can't read the
         file and needs to transfer it to a place where it can be read.
        */
        try {
            String extension = getFileExt(file.getFileName());
            File newFileLocation = File.createTempFile("tmp", extension);
            file.write(newFileLocation);
            return newFileLocation.getPath();
        } catch (Exception e) {
            throw new PnetException(e);
        }
    }
    
    /**
     * Modified version of above method for use with person image uploading.
     * 
     * @param file
     * @return
     * @throws PnetException
     */
    public static String commitUploadedFileToFileSystem(File file) throws PnetException {
        /*
         Transfer the file to a location where it can be read -- this is
         expecially important on linux machines where the OS can't read the
         file and needs to transfer it to a place where it can be read.
        */
    	FileInputStream fileInputStream = null;
    	FileOutputStream fileOutputStream = null;
        try {
            String extension = getFileExt(file.getName());
            File newFileLocation = File.createTempFile("tmp", extension);
            fileInputStream = new FileInputStream(file);
            fileOutputStream = new FileOutputStream(newFileLocation);
            byte[] buf = new byte[1024];            
            int len;         
	        while ((len = fileInputStream.read(buf)) > 0){
	        	fileOutputStream.write(buf, 0, len);
	        }
            return newFileLocation.getPath();
        } catch (Exception e) {
            throw new PnetException(e);
        } finally {
        	try {
				fileInputStream.close();
				fileOutputStream.close();
			} catch (IOException e) {
				throw new PnetException(e);
			}        	
        }
    }
}
