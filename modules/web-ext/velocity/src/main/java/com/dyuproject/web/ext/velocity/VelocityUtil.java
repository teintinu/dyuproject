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

package com.dyuproject.web.ext.velocity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;

import com.dyuproject.util.ResourceUtil;

/**
 * @author David Yu
 * @created Jun 15, 2008
 */

public class VelocityUtil
{
    
    private static void addTemplateByExtension(VelocityEngine engine, Map<String,Template> map, 
            File dir, String baseDir, String extension, int dirLen) throws Exception
    {        
        for(File f : ResourceUtil.getFilesByExtension(dir, new String[]{extension}))
        {
            String str = f.toURI().toString();
            int idx = str.indexOf(baseDir);
            String relativePath = str.substring(str.indexOf(baseDir) + dirLen + 1);// +1 to not start with /
            
            // eg. map.put("/WEB-INF/velocity/helloworld/index.vm", "helloworld/index.vm");
            // wheere /WEB-INF/velocity is baseDir
            map.put(str.substring(idx), engine.getTemplate(relativePath));
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
        addTemplateByExtension(engine, mapping, dir, baseDir, extension, dirLen);
        return mapping;
    }

}
