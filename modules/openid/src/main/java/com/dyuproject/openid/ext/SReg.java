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

package com.dyuproject.openid.ext;

import java.io.Serializable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mortbay.util.ajax.JSON;
import org.mortbay.util.ajax.JSON.Output;

import com.dyuproject.openid.Constants;
import com.dyuproject.openid.OpenIdUser;
import com.dyuproject.openid.UrlEncodedParameterMap;

/**
 * @author David Yu
 * @created Jan 8, 2009
 */

@SuppressWarnings("serial")
public class SReg implements Serializable, JSON.Convertible
{
    
    public static final String ATTR_NAME = "sreg";
    
    static void put(UrlEncodedParameterMap params)
    {
        params.put(Constants.OPENID_NS_SREG, Constants.Sreg.VERSION);                    
        params.put(Constants.OPENID_SREG_OPTIONAL, Constants.Sreg.OPTIONAL);        
    }
    
    static void setSreg(OpenIdUser user, SReg sreg)
    {
        user.setAttribute(ATTR_NAME, sreg);
    }
    
    public static SReg getSreg(OpenIdUser user)
    {
        return (SReg)user.getAttribute(ATTR_NAME);
    }
    
    static SReg parse(HttpServletRequest request)
    {        
        SReg sreg = new SReg();
        sreg.setNickname(request.getParameter(Constants.SREG_NICKNAME));
        sreg.setEmail(request.getParameter(Constants.SREG_EMAIL));
        sreg.setFullname(request.getParameter(Constants.SREG_FULLNAME));
        sreg.setDob(request.getParameter(Constants.SREG_DOB));
        sreg.setGender(request.getParameter(Constants.SREG_GENDER));
        sreg.setPostcode(request.getParameter(Constants.SREG_POSTCODE));
        sreg.setCountry(request.getParameter(Constants.SREG_COUNTRY));
        sreg.setLanguage(request.getParameter(Constants.SREG_LANGUAGE));
        sreg.setTimezone(request.getParameter(Constants.SREG_TIMEZONE));
        if(sreg._propertyCount==0)
            return null;
        request.setAttribute(ATTR_NAME, sreg);
        return sreg;
    }    

    private int _propertyCount = 0;
    private String _nickname;
    private String _email;
    private String _fullname;
    private String _dob;
    private String _gender;
    private String _postcode;
    private String _country;
    private String _language;
    private String _timezone;
    
    public SReg()
    {
        
    }    
    
    public int getPropertyCount()
    {
        return _propertyCount;
    }

    public String getNickname()
    {
        return _nickname;
    }

    void setNickname(String nickname)
    {
        if(nickname!=null)
        {
            if(_nickname==null)
                _propertyCount++;
            _nickname = nickname;
        }
    }

    public String getEmail()
    {
        return _email;
    }

    void setEmail(String email)
    {
        if(email!=null)
        {
            if(_email==null)
                _propertyCount++;
            _email = email;
        }
    }

    public String getFullname()
    {
        return _fullname;
    }

    void setFullname(String fullname)
    {
        if(fullname!=null)
        {
            if(_fullname==null)
                _propertyCount++;
            _fullname = fullname;
        }
    }

    public String getDob()
    {
        return _dob;
    }

    void setDob(String dob)
    {
        if(dob!=null)
        {
            if(_dob==null)
                _propertyCount++;
            _dob = dob;
        }
    }

    public String getGender()
    {
        return _gender;
    }

    void setGender(String gender)
    {
        if(gender!=null)
        {
            if(_gender==null)
                _propertyCount++;
            _gender = gender;            
        }
    }

    public String gePostcode()
    {
        return _postcode;
    }

    void setPostcode(String postcode)
    {
        if(postcode!=null)
        {
            if(_postcode==null)
                _propertyCount++;
            _postcode = postcode;            
        }
    }

    public String getCountry()
    {
        return _country;
    }

    void setCountry(String country)
    {
        if(country!=null)
        {
            if(_country==null)
                _propertyCount++;
            _country = country;            
        }
    }

    public String getLanguage()
    {
        return _language;
    }

    void setLanguage(String language)
    {
        if(language!=null)
        {
            if(_language==null)
                _propertyCount++;
            _language = language;            
        }
    }

    public String getTimezone()
    {
        return _timezone;
    }

    void setTimezone(String timezone)
    {
        if(timezone!=null)
        {
            if(_timezone==null)
                _propertyCount++;
            _timezone = timezone;            
        }
    }

    public void fromJSON(Map map)
    {
        _propertyCount = ((Number)map.get("pc")).intValue();
        _nickname = (String)map.get("n");
        _email = (String)map.get("e");
        _fullname = (String)map.get("f");
        _dob = (String)map.get("d");
        _gender = (String)map.get("g");
        _postcode = (String)map.get("p");
        _country = (String)map.get("c");
        _language = (String)map.get("l");
        _timezone = (String)map.get("t");
        
    }

    public void toJSON(Output out)
    {
        out.addClass(getClass());
        out.add("pc", _propertyCount);
        out.add("n", _nickname);
        out.add("e", _email);
        out.add("f", _fullname);
        out.add("d", _dob);
        out.add("g", _gender);
        out.add("p", _postcode);
        out.add("c", _country);
        out.add("l", _language);
        out.add("t", _timezone);        
    }

}
