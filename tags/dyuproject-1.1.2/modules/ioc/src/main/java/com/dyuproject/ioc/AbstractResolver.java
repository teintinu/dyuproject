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

package com.dyuproject.ioc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.dyuproject.ioc.Resource.Resolver;


/**
 * @author David Yu
 * @created Feb 23, 2009
 */

public abstract class AbstractResolver implements Resolver
{
    
    public static final int DEFAULT_BUFFER_SIZE = Integer.getInteger("resolver.default_buffer_size", 
            4096).intValue();
    
    public static String generateTypeFromClass(Class<?> clazz)
    {
        String sn = clazz.getSimpleName();
        return sn.substring(0, sn.lastIndexOf(Resolver.class.getSimpleName())).toLowerCase();
    }

    protected int _bufferSize = DEFAULT_BUFFER_SIZE;
    protected String _encoding;
    
    public int getBufferSize()
    {
        return _bufferSize;
    }
    
    public void setBufferSize(int bufferSize)
    {
        _bufferSize = bufferSize;
    }
    
    public String getEncoding()
    {
        return _encoding;
    }
    
    public void setEncoding(String encoding)
    {
        _encoding = encoding;
    }

    protected Reader newReader(InputStream in) throws IOException
    { 
        return getEncoding()==null ? new BufferedReader(new InputStreamReader(in), getBufferSize()) : 
            new BufferedReader(new InputStreamReader(in, getEncoding()), getBufferSize());
    }

}
