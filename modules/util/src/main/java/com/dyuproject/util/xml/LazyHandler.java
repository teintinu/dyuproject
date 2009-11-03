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

package com.dyuproject.util.xml;

/**
 * A handler that can terminate the parsing anytime.
 * Useful when you already got what you wanted from the xml.
 * This saves processing time and memory.
 * Note that the implemention should be the one tracking the stack and the state.
 * 
 * @author David Yu
 * @created Sep 18, 2008
 */

public interface LazyHandler
{
    
    /**
     * Callback that gets called only once upon traversing the root xml element.
     */
    public boolean rootElement(String name, String namespace);
    
    /**
     * Callback after traversing the start of xml elements (E.g &lt;foo&gt;).
     */
    public boolean startElement(String name, String namespace);
    
    /**
     * Callback after traversing the end of xml elements (E.g &lt;/foo&gt; or 
     * /&gt;).
     */
    public boolean endElement();
    
    /**
     * Callback after traversing the attributes of an element.
     */
    public void attribute(String name, String value);
    
    /**
     * Callback after traversing the text content of an element.
     */
    public void characters(char[] data, int start, int length); 

}
