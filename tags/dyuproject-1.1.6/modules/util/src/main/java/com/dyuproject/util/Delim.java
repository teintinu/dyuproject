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

import java.util.regex.Pattern;

/**
 * Common delimiters (used to split strings into arrays) 
 * that are pre-compiled to increase efficiency.  
 * 
 * @author David Yu
 */

public final class Delim
{
    
    public static final Pattern COMMA = Pattern.compile(",");
    public static final Pattern SLASH = Pattern.compile("/");
    public static final Pattern COLON = Pattern.compile(":");
    public static final Pattern SEMI_COLON = Pattern.compile(";");
    public static final Pattern AMPER = Pattern.compile("&");
    public static final Pattern EQUALS = Pattern.compile("=");
    
}
