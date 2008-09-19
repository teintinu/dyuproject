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

import java.util.Map;
import java.util.regex.Pattern;

/**
 * RelyingParty - in other words, the "consumer", the host that redirects the user to its
 * openid provider for authentication and verification.
 * 
 * @author David Yu
 * @created Sep 8, 2008
 */

public class RelyingParty
{    
    
    private static final Pattern PREFIX = Pattern.compile("^https?://");
    
    private static RelyingParty __instance;
    
    public static RelyingParty getInstance()
    {
        if(__instance==null)
        {
            synchronized(PREFIX)
            {
                if(__instance==null)
                {                    
                    OpenIdContext context = new OpenIdContext();
                    context.setAssociation(new DiffieHellmanAssociation());
                    context.setHttpConnector(new SimpleHttpConnector());
                    context.setDiscovery(new LinkHrefDiscovery());                    
                    __instance = new RelyingParty(context);
                }
            }
        }
        return __instance;
    }
    
    
    private OpenIdContext _context;
    
    public RelyingParty()
    {
        
    }
    
    public RelyingParty(OpenIdContext context)
    {
        setOpenIdContext(context);
    }
    
    public void setOpenIdContext(OpenIdContext context)
    {
        _context = context;
    }
    
    public OpenIdUser resolveUser(String claimedId) throws Exception
    {
        if(_context==null)
            throw new IllegalStateException("OpenIdContext not set.");
        
        if(!PREFIX.matcher(claimedId).find())
            throw new IllegalArgumentException("openid.identifier type not supported.");
        
        OpenIdUser user = _context.getDiscovery().discover(claimedId, _context);                    
        if(user==null)
            throw new IllegalStateException("there was no openid.server found for \""  + claimedId + "\"");
        
        _context.getAssociation().associate(user, _context);
        return user;        
    }
    
    public boolean verifyUserAuth(OpenIdUser user, Map<String,String> authRedirect) throws Exception
    {
        if(_context==null)
            throw new IllegalStateException("OpenIdContext not set.");
        
        return _context.getAssociation().verifyAuth(user, authRedirect, _context);
    }

    public static UrlEncodedParameterMap getAuthUrlMap(OpenIdUser user, String trustRoot, String realm, 
            String returnTo)
    {
        if(!user.isAssociated())
            throw new IllegalStateException("claimed_id of user has not been verified.");
        
        UrlEncodedParameterMap map = new UrlEncodedParameterMap(user.getOpenIdServer());
        
        map.put(Constants.OPENID_NS, Constants.DEFAULT_NS);
        map.put(Constants.OPENID_MODE, Constants.Mode.CHECKID_SETUP);
        
        map.put(Constants.OPENID_TRUST_ROOT, trustRoot);
        map.put(Constants.OPENID_REALM, realm);
        map.put(Constants.OPENID_RETURN_TO, returnTo);
        
        map.put(Constants.OPENID_IDENTITY, user.getClaimedId());
        map.put(Constants.OPENID_CLAIMED_ID, user.getClaimedId());
        map.put(Constants.OPENID_ASSOC_HANDLE, user.getAssocHandle());
        
        return map;
    }
    
    public static StringBuilder getAuthUrlBuffer(OpenIdUser user, String trustRoot, String realm, 
            String returnTo)
    {
        if(!user.isAssociated())
            throw new IllegalStateException("claimed_id of user has not been verified.");    
        
        StringBuilder buffer = new StringBuilder().append(user.getOpenIdServer());        
        
        buffer.append('?').append(Constants.OPENID_NS).append('=').append(Constants.DEFAULT_NS);
        
        buffer.append('&').append(Constants.OPENID_MODE).append('=').append(
                Constants.Mode.CHECKID_SETUP);
        
        buffer.append('&').append(Constants.OPENID_TRUST_ROOT).append('=').append(
                UrlEncodedParameterMap.encode(trustRoot));
        buffer.append('&').append(Constants.OPENID_REALM).append('=').append(
                UrlEncodedParameterMap.encode(realm));
        buffer.append('&').append(Constants.OPENID_RETURN_TO).append('=').append(
                UrlEncodedParameterMap.encode(returnTo));
        
        buffer.append('&').append(Constants.OPENID_IDENTITY).append('=').append(
                UrlEncodedParameterMap.encode(user.getClaimedId()));
        buffer.append('&').append(Constants.OPENID_CLAIMED_ID).append('=').append(
                UrlEncodedParameterMap.encode(user.getClaimedId()));
        buffer.append('&').append(Constants.OPENID_ASSOC_HANDLE).append('=').append(
                UrlEncodedParameterMap.encode(user.getAssocHandle()));        
        
        return buffer;
    }
    
    public static String getAuthUrlString(OpenIdUser user, String trustRoot, String realm, 
            String returnTo)
    {
        return getAuthUrlBuffer(user, trustRoot, realm, returnTo).toString();
    }


}
