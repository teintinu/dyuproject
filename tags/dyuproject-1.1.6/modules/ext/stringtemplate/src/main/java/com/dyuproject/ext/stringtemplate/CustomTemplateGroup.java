//========================================================================
//Copyright 2007-2009 David Yu dyuproject@gmail.com
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

package com.dyuproject.ext.stringtemplate;

import java.io.Writer;

import org.antlr.stringtemplate.AutoIndentWriter;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateErrorListener;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.StringTemplateWriter;

/**
 * @author David Yu
 * @created Jan 15, 2009
 */

public final class CustomTemplateGroup extends StringTemplateGroup
{
    
    public CustomTemplateGroup(String name, String rootDir, StringTemplateErrorListener listener)
    {
        super(name, rootDir);
        setErrorListener(listener);
    }    
    
    public StringTemplate createStringTemplate() 
    {
        return new CustomTemplate();
    }
    
    public StringTemplateWriter getStringTemplateWriter(Writer w)
    {
        return new AutoIndentWriter(w);
    }
}
