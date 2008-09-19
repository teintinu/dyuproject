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
 * 
 * @author David Yu
 * @created Sep 18, 2008
 */

public interface LazyHandler
{
    
    public boolean rootElement(String name);
    
    public boolean startElement(String name);
    
    public boolean endElement();
    
    public void attribute(String name, String value);
    
    public void characters(char[] data, int start, int length); 

}
