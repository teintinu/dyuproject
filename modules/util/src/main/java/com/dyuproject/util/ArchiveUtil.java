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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * @author David Yu
 * @created Feb 25, 2008
 */

public abstract class ArchiveUtil
{
    
    public static final String[] SUPPORTED_FILES = new String[]{
        ".zip",
        ".jar",
        ".war"
    };
    
    private static File __tempDir;
    
    static
    {
        __tempDir = new File(System.getProperty("java.io.tmpdir"));
    }
    
    public static void setTempDir(File tempDir)
    {
        if(tempDir.isDirectory() && tempDir.exists())
            __tempDir = tempDir;
    }
    
    public static File getTempDir()
    {
        return __tempDir;
    }
    
    public static boolean isSupported(String resource)
    {
        int idx = resource.lastIndexOf('.');
        if(resource.length()==idx+4)
        {         
            for(int i=0; i<SUPPORTED_FILES.length; i++)
            {
                if(resource.endsWith(SUPPORTED_FILES[i]))
                    return true;
            }
        }
        return false;           
    }
    
    public static boolean extract(File archive, File targetDir) throws IOException
    {
        return extract(archive.toURL(), targetDir, true);
    }
    
    public static boolean extract(File archive, File targetDir, boolean deleteOnExit) throws IOException
    {
        return extract(archive.toURL(), targetDir, deleteOnExit);
    }
    
    public static boolean extract(URL archive, File targetDir) throws IOException
    {
        return extract(archive, targetDir, true);
    }
    
    public static boolean extract(URL archive, File targetDir, boolean deleteOnExit) throws IOException
    {
        String archiveStr = archive.toString();
        String jarEntry = null;        
        int idx = archiveStr.indexOf("!/");
        if(idx!=-1)
        {
            if(!archiveStr.startsWith("jar:") && archiveStr.length()==idx+2)
                return false;
            archive = new URL(archiveStr.substring(4, idx));
            jarEntry = archiveStr.substring(idx+2);            
        }
        else if(!isSupported(archiveStr))
            return false;          
           
        JarInputStream jis = new JarInputStream(archive.openConnection().getInputStream());        
        if(!targetDir.exists())
            targetDir.mkdirs(); 
        JarEntry entry = null;
        while((entry=jis.getNextJarEntry())!=null)
        {
            String entryName = entry.getName();            
            File entryFile = new File(targetDir, entryName);
            if(!entry.isDirectory())
            {
                if(jarEntry==null || entryName.startsWith(jarEntry))
                {
                    if(!entryFile.exists() || entryFile.lastModified()!=entry.getTime())
                        extractEntry(entryFile, jis, entry, deleteOnExit);                    
                }
            }
        }
        try{jis.close();}catch(Exception e){}
        return true;
    }
    
    private static void extractEntry(File entryFile, JarInputStream jis, JarEntry entry, 
            boolean deleteOnExit) throws IOException
    {            
        File parent = new File(entryFile.getParent());
        if(!parent.exists())
            parent.mkdirs();                
        ResourceUtil.copy(jis, new FileOutputStream(entryFile));     
        entryFile.setLastModified(entry.getTime());
        if(deleteOnExit)
        {
            parent.deleteOnExit();
            entryFile.deleteOnExit();
        }
    }
    
}
