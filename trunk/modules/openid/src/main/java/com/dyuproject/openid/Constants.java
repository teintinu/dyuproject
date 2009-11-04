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

import java.math.BigInteger;

/**
 * Constants - openid
 * 
 * @author David Yu
 * @created Sep 8, 2008
 */

public final class Constants
{
    
    public static final String DEFAULT_ENCODING = "UTF-8";
    
    public static final String DEFAULT_VERSION = "2.0";
    public static final String DEFAULT_NS = "http://specs.openid.net/auth/2.0";
    
    public static final String OPENID_NS = "openid.ns";
    
    public static final String OPENID_MODE = "openid.mode";
    
    public static final String OPENID_ASSOC_HANDLE = "openid.assoc_handle";
    
    public static final String OPENID_IDENTITY = "openid.identity";
    
    public static final String OPENID_REALM = "openid.realm";
    
    public static final String OPENID_TRUST_ROOT = "openid.trust_root";
    
    public static final String OPENID_CLAIMED_ID = "openid.claimed_id";
    
    public static final String OPENID_RETURN_TO = "openid.return_to";
    
    public static final String OPENID_SIG = "openid.sig";
    
    public static final String OPENID_NS_SREG = "openid.ns.sreg";
    
    public static final String OPENID_SREG_OPTIONAL = "openid.sreg.optional";
    
    public static final String OPENID_OP_ENDPOINT = "openid.op_endpoint";
    
    public static final String OPENID_SIGNED = "openid.signed";
    
    public static final String OPENID_RESPONSE_NONCE = "openid.response_nonce";
    
    public static final String OPENID_SESSION_TYPE = "openid.session_type";
    
    public static final String OPENID_ASSOC_TYPE = "openid.assoc_type";
    
    public static final String OPENID_DH_MODULUS = "openid.dh_modulus";
    
    public static final String OPENID_DH_GEN = "openid.dh_gen";
    
    public static final String OPENID_DH_CONSUMER_PUBLIC = "openid.dh_consumer_public";
    
    public static final String SREG_NICKNAME = "openid.sreg.nickname";
    public static final String SREG_EMAIL = "openid.sreg.email";
    public static final String SREG_FULLNAME = "openid.sreg.fullname";
    public static final String SREG_DOB = "openid.sreg.dob";
    public static final String SREG_GENDER = "openid.sreg.gender";
    public static final String SREG_POSTCODE = "openid.sreg.postcode";
    public static final String SREG_COUNTRY = "openid.sreg.country";
    public static final String SREG_LANGUAGE = "openid.sreg.language";
    public static final String SREG_TIMEZONE = "openid.sreg.timezone";
    
    
    /**
     * The possible values for openid mode.
     */
    public static class Mode
    {        
        public static final String ASSOCIATE = "associate";
        public static final String CHECKID_SETUP = "checkid_setup";
        public static final String ID_RES = "id_res";
        public static final String CANCEL = "cancel";
    }
    
    /**
     * The types of fields for the assocation request and response.
     */
    public static class Assoc
    {        
        public static final String ASSOC_TYPE = "assoc_type";
        public static final String ASSOC_HANDLE = "assoc_handle";
        public static final String SESSION_TYPE = "session_type";
        public static final String NS = "ns";
        public static final String ERROR_CODE = "error_code";        
        public static final String MAC_KEY = "mac_key";
        public static final String ENC_MAC_KEY = "enc_mac_key";
        public static final String DH_SERVER_PUBLIC = "dh_server_public";
        public static final String EXPIRES_IN = "expires_in";        
    }
    
    /**
     * Sreg constants
     */
    public static class Sreg
    {
        public static final String VERSION = "http://openid.net/extensions/sreg/1.1";
        public static final String OPTIONAL = "nickname,email,fullname,dob,gender,postcode,country,language,timezone";     

    }
    
    /**
     * The default diffie hellman modulus being used by the openid association.
     */
    public static final BigInteger DIFFIE_HELLMAN_MODULUS = new BigInteger(
            "1551728981814736974712322577637155399157248019669" +
            "154044797077953140576293785419175806512274236981" +
            "889937278161526466314385615958256881888899512721" +
            "588426754199503412587065565498035801048705376814" +
            "767265132557470407658574792912915723345106432450" +
            "947150072296210941943497839259847603755949858482" +
            "53359305585439638443");
}
