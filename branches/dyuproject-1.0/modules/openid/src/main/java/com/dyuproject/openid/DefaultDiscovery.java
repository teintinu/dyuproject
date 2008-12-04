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


/**
 * Discovery initially through yadis.  If that fails, delegates to HtmlBasedDiscovery
 * 
 * @author David Yu
 * @created Sep 23, 2008
 */

public class DefaultDiscovery implements Discovery
{

    public OpenIdUser discover(String claimedId, OpenIdContext context) throws Exception
    {
        OpenIdUser user = null;
        try
        {
            user = YadisDiscovery.discover(claimedId, context.getHttpConnector().doHEAD(claimedId, 
                    context), context);            
        }
        catch(Exception e)
        {
            user = null;
        }
        return user!=null ? user : HtmlBasedDiscovery.discover(claimedId, 
                context.getHttpConnector().doGET(claimedId, context));
    }

}
