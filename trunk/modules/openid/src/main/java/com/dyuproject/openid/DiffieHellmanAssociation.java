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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import com.dyuproject.util.B64Code;
import com.dyuproject.util.Delim;
import com.dyuproject.util.DiffieHellman;
import com.dyuproject.util.DigestUtil;
import com.dyuproject.util.http.HttpConnector.Response;

/**
 * Association using DiffieHellman session - which allows two parties that have no prior 
 * knowledge of each other to jointly establish a shared secret key over an 
 * insecure communications channel.
 * 
 * @author David Yu
 * @created Sep 8, 2008
 */

public final class DiffieHellmanAssociation implements Association
{
    
    static final String CLIENT_PRIVATE_KEY_ATTR = "client.privateKey";
    
    private final SessionType _type;
    
    public DiffieHellmanAssociation()
    {
        this(SessionType.getDefault());
    }
    
    public DiffieHellmanAssociation(SessionType type)
    {
        _type = type;
    }
    
    /**
     * Gets the session type of this association.
     */
    public SessionType getSessionAssociationType()
    {
        return _type;
    }

    public boolean associate(OpenIdUser user, OpenIdContext context) throws Exception
    {
        if(user.isAuthenticated())
            return true;
        
        Map<String,Object> associationData = user.getAssociationData();
        if(associationData==null)
            associationData = new HashMap<String,Object>();
        else
            associationData.clear();
        
        associationData.put(Constants.OPENID_NS, Constants.DEFAULT_NS);        
        associationData.put(Constants.OPENID_MODE, Constants.Mode.ASSOCIATE);
        associationData.put(Constants.OPENID_ASSOC_TYPE, _type.getAssociationType());
        associationData.put(Constants.OPENID_SESSION_TYPE, _type.getSessionType());
        
        BigInteger[] keys = DiffieHellman.BASE_2.generateRandomKeys(Constants.DIFFIE_HELLMAN_MODULUS); 
        
        String publicKeyString = new String(B64Code.encode(keys[1].toByteArray()));
        associationData.put(Constants.OPENID_DH_CONSUMER_PUBLIC, publicKeyString);
        Response response = context.getHttpConnector().doPOST(user.getOpenIdServer(), (Map<?,?>)null,
                associationData, Constants.DEFAULT_ENCODING);
        
        associationData.put(CLIENT_PRIVATE_KEY_ATTR, keys[0].toString());
        BufferedReader br = null;        
        try
        {            
            br = new BufferedReader(new InputStreamReader(response.getInputStream(), Constants.DEFAULT_ENCODING), 1024);
            parseInputByLineSeparator(br, ':', associationData);
        }
        finally
        {
            if(br!=null)
                br.close();
            response.close();
        }
        
        user.setAssocHandle((String)associationData.get(Constants.Assoc.ASSOC_HANDLE));
        user.setAssociationData(associationData);
        return user.isAssociated();
    }

    public boolean verifyAuth(OpenIdUser user, Map<String,String> authRedirect, 
            OpenIdContext context) throws Exception
    {        
        if(user.isAuthenticated())
            return true;
        
        if(authRedirect==null || !user.isAssociated())
            return false;

        if(!Constants.Mode.ID_RES.equals(authRedirect.get(Constants.OPENID_MODE)) ||
                !user.getAssocHandle().equals(authRedirect.get(Constants.OPENID_ASSOC_HANDLE)))
        {
            //user.setAssocHandle(null);
            //user.setAssociationData(null);
            return false;
        }
        
        Map<String,Object> associationData = user.getAssociationData();

        byte[] encMacKey = B64Code.decode(associationData.get(
                Constants.Assoc.ENC_MAC_KEY).toString().toCharArray());        
        byte[] dhServerPublic = B64Code.decode(associationData.get(
                Constants.Assoc.DH_SERVER_PUBLIC).toString().toCharArray());        
        
        BigInteger privateKey = new BigInteger(associationData.get(CLIENT_PRIVATE_KEY_ATTR).toString());
        BigInteger serverPublic = new BigInteger(dhServerPublic);
        BigInteger sharedSecretKey = DiffieHellman.getSharedSecretKey(privateKey, 
                Constants.DIFFIE_HELLMAN_MODULUS, serverPublic);        
        
        byte[] secretKey = xor(sharedSecretKey, encMacKey, _type.getDigestType());
        
        String serverSig = authRedirect.get(Constants.OPENID_SIG);
        String[] signed = Delim.COMMA.split(authRedirect.get(Constants.OPENID_SIGNED));
        StringBuilder buffer = new StringBuilder();
        for(int i=0; i<signed.length; i++)
        {
            String s = signed[i];
            String v = authRedirect.get("openid."+s);
            if(v==null)
                throw new IllegalStateException("invalid signature from openid provider");
            // extension
            if(s.startsWith("ns."))
                user.addExtension(v, s.substring(3));
            buffer.append(s).append(':').append(v).append('\n');
        }        
        String generatedSig = null;
        try
        {
            byte[] b = _type.getSignature(secretKey, buffer.toString().getBytes(Constants.DEFAULT_ENCODING));
            generatedSig = new String(B64Code.encode(b));                            
        } 
        catch (Exception e)
        {            
            e.printStackTrace();
        }
        if(!serverSig.equals(generatedSig))
        {
            user.setAssocHandle(null);
            user.setAssociationData(null);
            return false;
        }
        user.setIdentity(authRedirect.get(Constants.OPENID_IDENTITY));
        return user.isAuthenticated();
    }
    
    /**
     * Digest the given {@code sharedSecretKey} into a byte array and performx xor of 
     * each single byte against the given {@code encMacKey}.
     */
    public static byte[] xor(BigInteger sharedSecretKey, byte[] encMacKey, String digestType) 
    throws Exception
    {
        return xor(DigestUtil.getPlainDigestedValue(digestType, sharedSecretKey.toByteArray()), 
                encMacKey);
    }
    
    /**
     * Performs xor of each single byte of the 2 byte arrays.
     * @throws IllegalStateException if the 2 byte arrays are not the same size.
     */
    public static byte[] xor(byte[] sharedSecret, byte[] encMacKey) throws Exception
    {        
        if(sharedSecret.length!=encMacKey.length)
            throw new IllegalStateException("shared_secret and enc_mac_key does not have the same length.");
        
        for(int i=0; i<sharedSecret.length; i++)
            sharedSecret[i] = (byte)(sharedSecret[i]^encMacKey[i]);
        
        return sharedSecret;
    }
    
    /**
     * Parses the key/value pair of each line separated by the 
     * given char {@code keyValueSeparator}.
     */
    public static void parseInputByLineSeparator(BufferedReader br, char keyValueSeparator, 
            Map<String,Object> parseMap) throws IOException
    {        
        String line = null;        
        while((line=br.readLine())!=null)
        {            
            int idx = line.indexOf(keyValueSeparator);
            if(idx>0)
                parseMap.put(line.substring(0, idx).trim(), line.substring(idx+1).trim());
        }        
    }

}
