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

/**
 * @author David Yu
 */

public abstract class ResourceUtil
{
    
    public static final int BUFFER_SIZE = 16384;
    
    public static String getCurrentPath()
    {
        return new File("").getPath();
    }
    
    public static File getCurrentDirectory()
    {
        return new File("");
    }

    public static byte[] readBytes(File file) throws IOException 
    {
        return readBytes(file.toURI());
    }
    
    public static byte[] readBytes(String file) throws IOException 
    {
        return readBytes(URI.create(file));
    }
    
    public static byte[] readBytes(URI file) throws IOException 
    {
        return readBytes(file.toURL());
    }
    
    public static byte[] readBytes(URL file) throws IOException 
    {       
        return readBytes(file.openStream());
    }
    
    public static byte[] readBytes(InputStream in) throws IOException 
    {
        return getByteArrayOutputStream(in).toByteArray();
    }
    
    public static ByteArrayOutputStream getByteArrayOutputStream(InputStream in) throws IOException 
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int length = 0;
        while((length=in.read(buffer, 0, BUFFER_SIZE)) != -1) 
            out.write(buffer, 0, length);           
        try{in.close();}catch(Exception e){}
        return out;
    }
    
    public static void copy(URL in, URL out) throws IOException
    {
        copy(in.openStream(), out.openConnection().getOutputStream());
    }
    
    public static void copy(File in, File out) throws IOException
    {
        copy(new FileInputStream(in), new FileOutputStream(out));
    }
    
    public static void copy(URL in, File out) throws IOException
    {
        copy(in.openStream(), new FileOutputStream(out));
    }
    
    public static void copy(InputStream in, OutputStream out) throws IOException 
    {
        byte[] buffer = new byte[BUFFER_SIZE];
        int length = 0;
        while((length=in.read(buffer, 0, BUFFER_SIZE)) != -1) 
            out.write(buffer, 0, length);        
        try{out.close();}catch(Exception e){}     
    }
}
