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

package com.dyuproject.web.ws.rest.generator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;

/**
 * @author David Yu
 * @created Mar 17, 2008
 */

public abstract class VelocityUtil
{

    static void addTemplateByExtension(VelocityEngine engine, Map<String,Template> map, File dir, String baseDir, String extension, 
            String index, String id ,int dirLen) throws Exception
    {
        File[] files = dir.listFiles();
        for(int i=0; i<files.length; i++)
        {
            File f = files[i];            
            String str = f.toURI().toString();
            String key = str.substring(str.indexOf(baseDir) + dirLen + 1);// +1 to not start with /
            if(f.isDirectory())
            {
                // avoid .svn files and such
                if(f.getName().charAt(0)!='.')
                {
                    // index.${format}
                    if(new File(f, index).exists())
                    {
                        String indexPath = key.concat("/").concat(index);
                        Template indexTemplate = engine.getTemplate(indexPath);
                        map.put(key.substring(0, key.length()-1), indexTemplate);                                
                        map.put(indexPath, indexTemplate);
                    }
                    // id.${format}
                    if(new File(f, id).exists())
                    {
                        map.put(key.concat(id), 
                                engine.getTemplate(key.concat("/").concat(id)));                        
                    }
                    addTemplateByExtension(engine, map, f, baseDir, extension, index, id, dirLen); 
                }
            }
            else 
            {
                String name = f.getName();
                if(name.hashCode()!=index.hashCode() && name.hashCode()!=id.hashCode())
                    map.put(key, engine.getTemplate(key));     
            }
        }
    }
    
    public static Map<String,Template> getTemplateMapping(VelocityEngine engine, File dir, 
            String baseDir, String extension) throws Exception
    {
        if(extension.charAt(0)!='.')
            extension = ".".concat(extension);
        if(baseDir.charAt(0)!='/')
            baseDir = "/".concat(baseDir);
        int dirLen = baseDir.charAt(baseDir.length()-1)=='/' ? baseDir.length()-1 :
            baseDir.length();
        Map<String,Template> mapping = new HashMap<String,Template>();
        addTemplateByExtension(engine, mapping, dir, baseDir, extension, "index".concat(extension), 
                "id".concat(extension), dirLen);
        return mapping;
    }
    
}
