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
 * An arbitrary resource where you can obtain a {@link Reader} to read data.
 * 
 * @author David Yu
 * @created Feb 23, 2009
 */

public class Resource
{
    
    private String _path, _type;
    private Reader _reader;    
    private final ReaderSource _source = new ReaderSource(null);
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
    
    public Resource(String path, String type, Reader reader)
    {
        this(path, type);
        resolve(reader);
    }
    
    public Resource(Reader reader)
    {
        resolve(reader);
    }
    
    /**
     * Gets the path.
     */
    public String getPath()
    {
        return _path;
    }
    
    /**
     * Sets the path.
     */
    public void setPath(String path)
    {
        _path = path;
    }
    
    /**
     * Gets the type.
     */
    public String getType()
    {
        return _type;
    }
    
    /**
     * Gets the reader
     */
    public Reader getReader()
    {
        return _reader;
    }
    
    /**
     * Gets the reader source that can be used for parsing json.
     */
    public ReaderSource getSource()
    {
        return _source;
    }    
    
    /**
     * Gets the file if any.
     */
    public File getFile()
    {
        return _file;
    }

    /**
     * Resolves the resource via setting the {@code reader}.
     */
    public void resolve(Reader reader)
    {
        _reader = reader;
        _source.setReader(reader);
    }
    
    /**
     * Resolves the resource via setting the {@code reader}and {@code type}.
     */
    public void resolve(Reader reader, String type)
    {
        resolve(reader);
        _type = type;
    }
    
    /**
     * Resolves the resource via setting the {@code reader}, {@code type} and {@code file}.
     */
    public void resolve(Reader reader, String type, File file)
    {
        resolve(reader);
        _type = type;
        _file = file;
    }
    
    /**
     * Checks if this resource is already resolved.
     */
    public boolean isResolved()
    {
        return _reader!=null;
    }
    
    /**
     * A {@link Resource} resolver that basically allows it to be read 
     * via {@link Resource#getReader()}.
     */
    public interface Resolver
    {
        /**
         * Gets the type (id) of resolver
         */
        public String getType();
        
        /**
         * Resolves a resource by setting the {@link Reader} property of the {@link Resource}.
         * That is done via {@link Resource#resolve(Reader)}.
         */
        public void resolve(Resource resource, Context context) throws IOException;
        
        /**
         * Creates a resource from a given path string.
         */
        public Resource createResource(String path) throws IOException;
        
    }

}
