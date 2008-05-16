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

package com.dyuproject.web.ws.error;

import com.dyuproject.util.format.FormatConverter.Builder;
import com.dyuproject.web.ws.WebServiceResponse;

/**
 * @author David Yu
 * @created Mar 25, 2008
 */

public abstract class HttpError implements WebServiceResponse
{
    
    public static final HttpError _203 = new HttpError(){
        public int getStatusCode()
        {            
            return 203;
        }     
    };
    
    public static final HttpError _400 = new HttpError(){
        public int getStatusCode()
        {            
            return 400;
        }        
    };
    
    public static final HttpError _401 = new HttpError(){
        public int getStatusCode()
        {            
            return 401;
        }        
    };
    
    public static final HttpError _403 = new HttpError(){
        public int getStatusCode()
        {            
            return 403;
        }        
    };
    
    public static final HttpError _404 = new HttpError(){
        public int getStatusCode()
        {            
            return 404;
        }        
    };    
    
    public static final HttpError _500 = new HttpError(){
        public int getStatusCode()
        {            
            return 500;
        }        
    };    
    
    public static final HttpError _501 = new HttpError(){
        public int getStatusCode()
        {            
            return 501;
        }        
    };
    
    public static final HttpError _301 = new HttpError(){
        public int getStatusCode()
        {            
            return 301;
        }        
    };
    
    public static final HttpError _302 = new HttpError(){
        public int getStatusCode()
        {            
            return 302;
        }        
    };
    
    public static final HttpError _303 = new HttpError(){
        public int getStatusCode()
        {            
            return 303;
        }        
    };
    
    
    private HttpError()
    {
        
    }
    

    public void convert(Builder builder, String format)
    {
        // TODO Auto-generated method stub        
    }   
    
    public abstract int getStatusCode();
    
    
    

}
