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

package com.dyuproject.ioc.factory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.mortbay.util.ajax.JSON.ReaderSource;
import org.mortbay.util.ajax.JSON.Source;

import com.dyuproject.ioc.SourceFactory;

/**
 * @author David Yu
 * @created Feb 21, 2009
 */

public abstract class AbstractSourceFactory implements SourceFactory
{
    
    public static final int DEFAULT_BUFFER_SIZE = 2048;
    
    protected int _bufferSize = DEFAULT_BUFFER_SIZE;
    protected String _encoding;
    
    public void setBufferSize(int bufferSize)
    {
        _bufferSize = bufferSize;
    }
    
    public int getBufferSize()
    {
        return _bufferSize;
    }
    
    public void setEncoding(String encoding)
    {
        _encoding = encoding;
    }
    
    public String getEncoding()
    {
        return _encoding;
    }
    
    public Source getSource(InputStream in) throws IOException
    {
        if(getEncoding()==null)
            return new ReaderSource(new BufferedReader(new InputStreamReader(in), getBufferSize()));
        
        return new ReaderSource(new BufferedReader(new InputStreamReader(in, getEncoding()), 
                getBufferSize()));
    }
    
    

}
