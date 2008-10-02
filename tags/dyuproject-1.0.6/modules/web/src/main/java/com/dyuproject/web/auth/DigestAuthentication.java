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

import java.io.IOException;
import java.security.MessageDigest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.util.QuotedStringTokenizer;
import org.mortbay.util.TypeUtil;

import com.dyuproject.util.B64Code;

/**
 * Web authorization using MD5 digest.
 * 
 * @author David Yu
 * @created Jun 28, 2008
 */

public class DigestAuthentication extends Authentication
{
    
    public static final String TYPE = "Digest";
    
    public DigestAuthentication(CredentialSource credentialSource)
    {
        super(credentialSource);
    }
    
    public String getType()
    {
        return TYPE;
    }
    
    public boolean authenticate(String realm, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
        String authorization = request.getHeader(AUTHORIZATION);
        if(authorization==null)
        {
            sendChallenge(realm, request, response);
            return false;
        }
        QuotedStringTokenizer tokenizer = new QuotedStringTokenizer(authorization, "=, ", true,
                false);
        String last = null, name = null, username = null, password = null, rlm = null;
        String nonce = null, nc = null, cnonce = null, qop = null, uri = null, rsp = null;
        while (tokenizer.hasMoreTokens())
        {
            String tok = tokenizer.nextToken();
            char c=(tok.length()==1)?tok.charAt(0):'\0';

            switch (c)
            {
              case '=':
                  name=last;
                  last=tok;
                  break;
              case ',':
                  name=null;
              case ' ':
                  break;

              default:
                  last=tok;
                  if (name!=null)
                  {
                      if (USERNAME.equalsIgnoreCase(name))
                      {
                          username=tok;
                          password = getCredentialSource().getPassword(realm, username, request);
                          if(password==null)
                          {
                              sendChallenge(realm, request, response);
                              return false;
                          }
                      }
                      else if (REALM.equalsIgnoreCase(name))
                          rlm=tok;
                      else if ("nonce".equalsIgnoreCase(name))
                          nonce=tok;
                      else if ("nc".equalsIgnoreCase(name))
                          nc=tok;
                      else if ("cnonce".equalsIgnoreCase(name))
                          cnonce=tok;
                      else if ("qop".equalsIgnoreCase(name))
                          qop=tok;
                      else if ("uri".equalsIgnoreCase(name))
                          uri=tok;
                      else if ("response".equalsIgnoreCase(name))
                          rsp=tok;
                      break;
                  }
            }
        }        
        if(check(username, password, rlm, nonce, nc, cnonce, qop, uri, rsp, request))
        {
            getCredentialSource().onAuthenticated(realm, username, password, request, response);
            return true;
        }
        sendChallenge(realm, request, response);
        return false;
    }
    
    protected boolean check(String username, String password, String realm, String nonce, 
            String nc, String cnonce, String qop, String uri, String response, 
            HttpServletRequest request)
    {        
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            
            // calc A1 digest
            md.update(username.getBytes());
            md.update((byte)':');
            md.update(realm.getBytes());
            md.update((byte)':');
            md.update(password.getBytes());
            byte[] ha1 = md.digest();
            // calc A2 digest
            md.reset();
            md.update(request.getMethod().getBytes());
            md.update((byte)':');
            md.update(uri.getBytes());
            byte[] ha2=md.digest();
            
            md.update(TypeUtil.toString(ha1,16).getBytes());
            md.update((byte)':');
            md.update(nonce.getBytes());
            md.update((byte)':');
            md.update(nc.getBytes());
            md.update((byte)':');
            md.update(cnonce.getBytes());
            md.update((byte)':');
            md.update(qop.getBytes());
            md.update((byte)':');
            md.update(TypeUtil.toString(ha2,16).getBytes());
            byte[] digest=md.digest();
            
            // check digest
            return response.equals(encode(digest));
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }        
    }
    
    protected void sendChallenge(String realm, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
        String domain=request.getContextPath();
        StringBuilder buffer = new StringBuilder().append("Digest realm=\"").append(realm);
        buffer.append("\", domain=\"").append(domain!=null && domain.length()>0 ? domain : '/');
        buffer.append("\", nonce=\"").append(newNonce(request));
        buffer.append("\", algorithm=MD5, qop=\"auth\"");

        response.setHeader(WWW_AUTHENTICATE, buffer.toString());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
    
    protected String newNonce(HttpServletRequest request)
    {        
        return B64Code.encode(new StringBuilder().append(request.getRemoteAddr()).append(':')
                .append(System.currentTimeMillis()).toString());
    }
    
    private static String encode(byte[] data)
    {
        StringBuilder buffer = new StringBuilder();
        for (int i=0; i<data.length; i++) 
        {
            buffer.append(Integer.toHexString((data[i] & 0xf0) >>> 4));
            buffer.append(Integer.toHexString(data[i] & 0x0f));
        }
        return buffer.toString();
    }

}
