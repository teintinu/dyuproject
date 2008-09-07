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

package com.dyuproject.web.auth;

import javax.servlet.http.HttpServletRequest;

import com.dyuproject.util.B64Code;
import com.dyuproject.util.Delim;
import com.dyuproject.util.DigestUtil;

/**
 * @author David Yu
 * @created Jul 1, 2008
 */

public class SmartDigestAuthentication extends DigestAuthentication
{
    
    private String _secretKey;
    private int _maxAge;
    
    public SmartDigestAuthentication(CredentialSource credentialSource, String secretKey)
    {
        this(credentialSource, secretKey, -1);
    }

    public SmartDigestAuthentication(CredentialSource credentialSource, String secretKey, 
            int maxAgeSeconds)
    {
        super(credentialSource);
        setSecretKey(secretKey);
        setMaxAge(maxAgeSeconds);
    }
    
    public void setSecretKey(String secretKey)
    {
        _secretKey = secretKey;
    }
    
    public void setMaxAge(int maxAge)
    {
        _maxAge = maxAge;
    }
    
    protected String newNonce(HttpServletRequest request)
    {
        String remoteAddr = request.getRemoteAddr();
        String ts = String.valueOf(System.currentTimeMillis());
        String sig = DigestUtil.digestMD5(remoteAddr + ts + _secretKey);
        
        StringBuilder buffer = new StringBuilder();
        buffer.append(remoteAddr).append(',').append(ts).append(',').append(sig);
        return B64Code.encode(buffer.toString());
    }
    
    protected boolean check(String username, String password, String realm, String nonce, 
            String nc, String cnonce, String qop, String uri, String response, 
            HttpServletRequest request)
    {
        String[] tokens = Delim.COMMA.split(B64Code.decode(nonce));        
        if(tokens.length!=3 || !tokens[0].equals(request.getRemoteAddr()))
            return false;
        
        // check if nonce was not altered
        String sig = DigestUtil.digestMD5(tokens[0] + tokens[1] + _secretKey);        
        if(!sig.equals(tokens[2]))
            return false;
        
        // does not expire
        if(_maxAge<1)
            return super.check(username, password, realm, nonce, nc, cnonce, qop, uri, response, 
                    request);
        
        // check if it already expired
        long max = Long.parseLong(tokens[1]) + (1000*_maxAge);            
        return max > System.currentTimeMillis() && super.check(username, password, realm, nonce, 
                nc, cnonce, qop, uri, response, request);        
    }

}
