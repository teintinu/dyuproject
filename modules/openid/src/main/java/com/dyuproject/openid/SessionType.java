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

package com.dyuproject.openid;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.dyuproject.util.DigestUtil;

/**
 * The type of session for an association.
 * 
 * @author David Yu
 * @created Sep 11, 2008
 */

public final class SessionType
{
    
    public static final SessionType HMAC_SHA1 = new SessionType(
            Association.SESSION_DH_SHA1, Association.ASSOC_HMAC_SHA1, DigestUtil.SHA1, "HMACSHA1");
    
    public static final SessionType HMAC_SHA256 = new SessionType(
            Association.SESSION_DH_SHA256, Association.ASSOC_HMAC_SHA256, DigestUtil.SHA256, "HMACSHA256");
    
    public static SessionType getDefault()
    {
        return HMAC_SHA1;
    }
    
    private String _sessionType;
    private String _associationType;
    private String _digestType;
    private String _algorithm;
    
    private SessionType(String sessionType, String associationType, String digestType, 
            String algorithm)
    {
        _sessionType = sessionType;
        _associationType = associationType;
        _digestType = digestType;
        _algorithm = algorithm;
    }
    
    public String getSessionType()
    {
        return _sessionType;
    }
    
    public String getAssociationType()
    {
        return _associationType;
    }
    
    public String getDigestType()
    {
        return _digestType;
    }
    
    public String getAlgorithm()
    {
        return _algorithm;
    }
    
    public byte[] getSignature(byte[] secretKey, byte[] toSign) throws NoSuchAlgorithmException, 
        InvalidKeyException
    {
        SecretKeySpec sks = new SecretKeySpec(secretKey, _algorithm);
        Mac m = Mac.getInstance(sks.getAlgorithm());
        m.init(sks);
        return m.doFinal(toSign);   
    }

}
