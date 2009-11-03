//========================================================================
//Copyright 2007-2008 David Yu dyuproject@gmail.com
//------------------------------------------------------------------------
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at 
//http://www.apache.org/licenses/LICENSE-2.0
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//========================================================================

package com.dyuproject.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Util for files and streams.
 * 
 * @author David Yu
 */

public final class ResourceUtil
{
    
    static int __copyBufferSize = 4096;
    
    /**
     * Sets the buffer size to use when copying data from streams.
     */
    public static void setCopyBufferSize(int size)
    {
        __copyBufferSize = size;
    }
    
    /**
     * Gets the buffer size to use when copying data from streams.
     */
    public static int getCopyBufferSize()
    {
        return __copyBufferSize;
    }    

    /**
     * Reads the contents of the file into a byte array.
     */
    public static byte[] readBytes(File file) throws IOException 
    {
        return readBytes(file.toURI());
    }
    
    /**
     * Reads the contents of the given file into a byte array.
     */
    public static byte[] readBytes(String file) throws IOException 
    {
        return readBytes(URI.create(file));
    }
    
    /**
     * Reads the contents of the given file into a byte array.
     */
    public static byte[] readBytes(URI file) throws IOException 
    {
        return readBytes(file.toURL());
    }
    
    /**
     * Reads the contents of the given file into a byte array.
     */
    public static byte[] readBytes(URL file) throws IOException 
    {       
        return readBytes(file.openStream());
    }
    
    /**
     * Reads the contents of the given input stream into a byte array.
     */
    public static byte[] readBytes(InputStream in) throws IOException 
    {
        return getByteArrayOutputStream(in).toByteArray();
    }
    
    /**
     * Gets the ByteArrayOutputStream that was filled when reading from the 
     * given input stream {@code in}.
     */
    public static ByteArrayOutputStream getByteArrayOutputStream(InputStream in) throws IOException 
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[__copyBufferSize];
        int length = 0;
        while((length=in.read(buffer, 0, __copyBufferSize)) != -1) 
            out.write(buffer, 0, length);           
        try{in.close();}catch(Exception e){}
        return out;
    }
    
    /**
     * Copies the contents of the  input url to the output url.
     */
    public static void copy(URL in, URL out) throws IOException
    {
        copy(in.openStream(), out.openConnection().getOutputStream());
    }
    
    /**
     * Copies the contents of the  input file to the output file.
     */
    public static void copy(File in, File out) throws IOException
    {
        copy(new FileInputStream(in), new FileOutputStream(out));
    }
    
    /**
     * Copies the contents of the input url to the output file.
     */
    public static void copy(URL in, File out) throws IOException
    {
        copy(in.openStream(), new FileOutputStream(out));
    }
    
    /**
     * Copies the contents of the input stream to the output stream.
     */
    public static void copy(InputStream in, OutputStream out) throws IOException 
    {
        byte[] buffer = new byte[__copyBufferSize];
        int length = 0;
        while((length=in.read(buffer, 0, __copyBufferSize)) != -1) 
            out.write(buffer, 0, length);        
        try{out.close();}catch(Exception e){}     
    }
    
    /**
     * Copies the contents of the input stream to the output stream with the specified 
     * buffer size to use for copying.
     */
    public static void copy(InputStream in, OutputStream out, int bufferSize) throws IOException 
    {
        byte[] buffer = new byte[bufferSize];
        int length = 0;
        while((length=in.read(buffer, 0, bufferSize)) != -1) 
            out.write(buffer, 0, length);        
        try{out.close();}catch(Exception e){}     
    }
    
    static void copyDir(File dirFrom, File dirTo) throws IOException
    {
        File[] files = dirFrom.listFiles();
        if(!dirTo.exists())
            dirTo.mkdirs();
        for(int i=0; i<files.length; i++)
        {
            File f = files[i];
            if(f.isDirectory())                                            
                copyDir(f, new File(dirTo, f.getName()));                
            else                
                copy(f, new File(dirTo, f.getName()));                                    
        }
    }
    
    /**
     * Copies the given {@code file} to the given dir.
     */
    public static void copyFileToDir(File file, File dirTo) throws IOException
    {
        if(file.exists())
        {
            if(file.isDirectory())            
                copyDir(file, dirTo);            
            else
            {
                if(!dirTo.exists())
                    dirTo.mkdirs();
                copy(file, new File(dirTo, file.getName()));
            }
        }            
    }
    
    /**
     * Returns a list of file filtered by their file types/extensions; (E.g ".zip")
     */
    public static List<File> getFilesByExtension(File dir, String[] extensions)
    {
        if(!dir.isDirectory() || extensions==null)
            return Collections.emptyList();
        List<File> files = new ArrayList<File>();
        addFilesByExtension(files, dir, extensions);
        return files;
    }
    
    static void addFilesByExtension(List<File> list, File dir, String[] extensions)
    {
        File[] files = dir.listFiles();
        for(int i=0; i<files.length; i++)
        {
            File f = files[i];
            if(f.isDirectory())
                addFilesByExtension(list, f, extensions);
            else
            {
                for(String s : extensions)
                {
                    if(f.getName().endsWith(s))
                    {
                        list.add(f);
                        break;
                    }
                }
            }
        }
    }

}
