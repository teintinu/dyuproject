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

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import org.mortbay.util.ajax.JSON.ReaderSource;

/**
 * @author David Yu
 * @created Feb 23, 2009
 */

public class Resource
{
    
    private String _path, _type;
    private Reader _reader;    
    private ReaderSource _source = new ReaderSource(null);
    private File _file;
    
    public Resource(String path)
    {
        _path = path;
    }
    
    public Resource(String path, String type)
    {
        _path = path;
        _type = type;
    }
    
    public Resource (String path, Reader reader)
    {
        _path = path;
        resolve(reader);
    }
    
    public Resource(Reader reader)
    {
        resolve(reader);
    }
    
    public Resource(Reader reader, File file)
    {
        resolve(reader, file);        
    }
    
    public String getPath()
    {
        return _path;
    }
    
    public void setPath(String path)
    {
        _path = path;
    }
    
    public String getType()
    {
        return _type;
    }
    
    public Reader getReader()
    {
        return _reader;
    }
    
    public ReaderSource getSource()
    {
        return _source;
    }
    
    public File getFile()
    {
        return _file;
    }

    public void resolve(Reader reader)
    {
        _reader = reader;
        _source.setReader(reader);
    }
    
    public void resolve(Reader reader, File file)
    {
        _reader = reader;
        _source.setReader(reader);
        _file = file;
    }
    
    public boolean isResolved()
    {
        return _reader!=null;
    }
    
    public interface Resolver
    {
        public String getType();
        public void resolve(Resource resource, Context context) throws IOException;     
        public Resource createResource(String path) throws IOException;
    }

}
