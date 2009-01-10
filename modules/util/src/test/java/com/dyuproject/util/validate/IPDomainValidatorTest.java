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

package com.dyuproject.util.validate;

import junit.framework.TestCase;

/**
 * @author David Yu
 * @created Jan 10, 2009
 */

public class IPDomainValidatorTest extends TestCase
{
    
    public void test1()
    {

        
        
        assertTrue(IPDomainValidator.validate("c.org")==IPDomainValidator.PLAIN);
        assertTrue(IPDomainValidator.validate("c.ph")==IPDomainValidator.PLAIN);        
        assertTrue(IPDomainValidator.validate("123c.com")==IPDomainValidator.ALPHANUMERIC);
        assertTrue(IPDomainValidator.validate("c.museum")==IPDomainValidator.PLAIN);        
        assertTrue(IPDomainValidator.validate("10.com")==IPDomainValidator.ALPHANUMERIC);
        assertTrue(IPDomainValidator.validate("c-b.com")==IPDomainValidator.HYPHENATED);
        
        assertTrue(IPDomainValidator.validate("192.168.1.1")==IPDomainValidator.IP);
        assertTrue(IPDomainValidator.validate("255.255.255.255")==IPDomainValidator.IP);        
        
        String s = "asd1-fc.com";
        assertTrue(IPDomainValidator.validate(s, 1, s.length()-1)==IPDomainValidator.MIXED);
        assertTrue(IPDomainValidator.validate(s.toCharArray(), 1, s.length()-1)==IPDomainValidator.MIXED);
        
        String longDomain = "123456789012345678901234567890123456789012345678901234567890123.com";
        assertTrue(IPDomainValidator.validate(longDomain)==IPDomainValidator.ALPHANUMERIC);
        
        assertTrue(IPDomainValidator.validate("c.o1")==IPDomainValidator.INVALID);        
        assertTrue(IPDomainValidator.validate("c.d")==IPDomainValidator.INVALID);        
        assertTrue(IPDomainValidator.validate("c.d-g")==IPDomainValidator.INVALID);
        assertTrue(IPDomainValidator.validate("c.-dg")==IPDomainValidator.INVALID);
        assertTrue(IPDomainValidator.validate("c.dg-")==IPDomainValidator.INVALID);
        assertTrue(IPDomainValidator.validate("c.d--g")==IPDomainValidator.INVALID);
        assertTrue(IPDomainValidator.validate("c--b.org")==IPDomainValidator.INVALID);
        assertTrue(IPDomainValidator.validate("c-.org")==IPDomainValidator.INVALID);
        assertTrue(IPDomainValidator.validate("-c.org")==IPDomainValidator.INVALID);
        assertTrue(IPDomainValidator.validate("cb-.org")==IPDomainValidator.INVALID);
        assertTrue(IPDomainValidator.validate("-cb.org")==IPDomainValidator.INVALID);
        assertTrue(IPDomainValidator.validate("c_b.org")==IPDomainValidator.INVALID);        
        assertTrue(IPDomainValidator.validate("192.168.1a.1")==IPDomainValidator.INVALID);
        assertTrue(IPDomainValidator.validate("192.1-9.12.1")==IPDomainValidator.INVALID);
        assertTrue(IPDomainValidator.validate("255.255.255.255.1")==IPDomainValidator.INVALID);
        
        assertTrue(IPDomainValidator.validate(longDomain+"4")==IPDomainValidator.INVALID);
        
    }

}
