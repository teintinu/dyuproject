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

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateWriter;

/**
 * @author David Yu
 * @created Jan 15, 2009
 */

public final class CustomTemplate extends StringTemplate
{

    HttpServletRequest _request;
    HttpServletResponse _response;    

    void init(HttpServletRequest request, HttpServletResponse response, String newLine) 
    throws ServletException, IOException
    {
        _request = request;
        _response = response;
        StringTemplateWriter wr = group.getStringTemplateWriter(response.getWriter());
        wr.setLineWidth(StringTemplateWriter.NO_WRAP);
        try 
        {            
            write(wr);
        }
        catch (IOException io) 
        {            
            error("Got IOException writing to writer "+wr.getClass().getName());
        }
        // reset so next toString() does not wrap; normally this is a new writer
        // each time, but just in case they override the group to reuse the
        // writer.
        //wr.setLineWidth(StringTemplateWriter.NO_WRAP);
        _request = null;
        _response = null;
    }

    @SuppressWarnings("unchecked")
    public Object get(StringTemplate self, String attribute)
    {
        // System.out.println("### get("+getEnclosingInstanceStackString()+", "+attribute+")");
        // System.out.println("attributes="+(attributes!=null?attributes.keySet().toString():"none"));
        //if (self == null)
        //{
        //    return null;
        //}



        // is it here?
        Object o = _request.getAttribute(attribute);
        if(o!=null)
            return o;

        // nope, check argument context in case embedded
        Map argContext = getArgumentContext();
        if (argContext != null)
        {
            o = argContext.get(attribute);
        }

        if(o==null)
        {
            if (passThroughAttributes
                    && getFormalArgument(attribute) != null)
            {
                // if you've defined attribute as formal arg for this
                // template and it has no value, do not look up the
                // enclosing dynamic scopes. This avoids potential infinite
                // recursion.
                return null;
            }
            
            // not locally defined, check enclosingInstance if embedded
            if (enclosingInstance != null)
            {
                /*
                 * System.out.println("looking for "+getName()+"."+attribute+" in super="
                 * + enclosingInstance.getName());
                 */
                Object valueFromEnclosing = get(enclosingInstance, attribute);
                if (valueFromEnclosing == null)
                {
                    checkNullAttributeAgainstFormalArguments(self, attribute);
                }
                o = valueFromEnclosing;
            }

            // not found and no enclosing instance to look at
            else if (enclosingInstance == null)
            {
                // It might be a map in the group or supergroup...
                o = group.getMap(attribute);
            }
        }

        return o;
    }


}
