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
import java.nio.charset.Charset;

import com.dyuproject.ioc.Resource.Resolver;


/**
 * @author David Yu
 * @created Feb 23, 2009
 */

public abstract class AbstractResolver implements Resolver
{
    
    public static final Charset DEFAULT_ENCODING = Charset.forName("UTF-8");
    public static final int DEFAULT_BUFFER_SIZE = Integer.getInteger("resolver.default_buffer_size", 
            4096).intValue();
    
    public static String generateTypeFromClass(Class<?> clazz)
    {
        String sn = clazz.getSimpleName();
        return sn.substring(0, sn.lastIndexOf(Resolver.class.getSimpleName())).toLowerCase();
    }

    protected final int _bufferSize;
    protected final Charset _encoding;
    
    public AbstractResolver()
    {
        this(DEFAULT_ENCODING, DEFAULT_BUFFER_SIZE);
    }
    
    public AbstractResolver(Charset encoding, int bufferSize)
    {
        _encoding = encoding;
        _bufferSize = bufferSize;
    }
    
    public final int getBufferSize()
    {
        return _bufferSize;
    }
    
    public final Charset getEncoding()
    {
        return _encoding;
    }

    protected Reader newReader(InputStream in) throws IOException
    { 
        return getEncoding()==null ? new BufferedReader(new InputStreamReader(in), _bufferSize) : 
            new BufferedReader(new InputStreamReader(in, _encoding), _bufferSize);
    }

}
