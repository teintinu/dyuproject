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

package com.dyuproject.util.xml;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * A simple XML parser that starts parsing right away and validates along the way. 
 * 
 * @author David Yu
 * @created Sep 17, 2008
 */

public final class XMLParser
{    
    
    private static final int STATE_EL_STARTING = 1;
    private static final int STATE_EL_STARTED = 2;
    private static final int STATE_EL_ENDING = 3;
    private static final int STATE_EL_ENDED = 4;

    private static final int STATE_EL_ATTR_NAME_START = 5;
    private static final int STATE_EL_ATTR_VALUE_START = 6;
    private static final int STATE_EL_ATTR_VALUE_END = 7;
    
    private static final int STATE_EL_TEXT = 8;
    private static final int STATE_COMMENT_STARTING = 9;
    private static final int STATE_COMMENT_DASH_START = 10;
    private static final int STATE_COMMENT_STARTED = 11;
    private static final int STATE_COMMENT_DASH_END = 12;
    private static final int STATE_COMMENT_ENDING = 13;
    private static final int STATE_IGNORE = 14;
    private static final int STATE_CDATA_STARTING = 15;
    private static final int STATE_CDATA_STARTED = 16;
    private static final int STATE_CDATA_ENDING = 17;
    private static final int STATE_CDATA_ENDED = 18;
    
    private static int __defaultBufferSize = 4096;
    
    public static void setDefaultBufferSize(int size)
    {
        __defaultBufferSize = size;
    }
    
    /**
     * Lazily parses the given {@code reader} using the default buffer size 
     * {@link #__defaultBufferSize}.  The parsing can be terminated by 
     * the {@link LazyHandler} {@code handler} at any point.
     */
    public static void parse(InputStreamReader reader, LazyHandler handler, 
            boolean includeInnerText) throws IOException
    {
        parse(reader, handler, includeInnerText, __defaultBufferSize);        
    }
    
    /**
     * Lazily parses the given {@code reader}.  The parsing can be terminated by 
     * the {@link LazyHandler} {@code handler} at any point.
     */
    public static void parse(InputStreamReader reader, LazyHandler handler, 
            boolean includeInnerText, int bufferSize) throws IOException
    {
        if(handler==null)
            throw new IllegalArgumentException("LazyHandler arg must not be null.");
        char[] cbuf = new char[bufferSize];
        int offset = 0;
        int len = 0;
        int state = 0;
        int stateBeforeComment = 0;
        int mark = -1;
        int elwsMark = -1;
        int nsMark = -1;
        String attrName = null;
        String attrValue = null;
        boolean dq = true;
        boolean searchRoot = true;
        while((len = reader.read(cbuf, offset, cbuf.length-offset))!=-1)
        {            
            for(int i=0; i<len; i++, offset++)
            {                
                char c = cbuf[offset];
                switch(c)
                {
                    case '<':
                        switch(state)
                        {
                            case STATE_COMMENT_STARTED:
                            case STATE_IGNORE:                                
                                continue;
                            case STATE_COMMENT_ENDING://handle --< comments
                                state = STATE_COMMENT_STARTED;
                                continue;
                            case STATE_CDATA_ENDING:
                            case STATE_CDATA_ENDED:
                                state = STATE_CDATA_STARTED;
                                continue;
                            case 0:
                                state = STATE_EL_STARTING;
                                mark = offset;
                                continue;
                            case STATE_EL_ENDED:
                            case STATE_EL_STARTED:
                                stateBeforeComment = state;
                                state = STATE_EL_STARTING;
                                mark = offset;
                                continue;
                            case STATE_EL_TEXT:
                                stateBeforeComment = state;
                                state = STATE_EL_STARTING;
                                if(mark!=-1 && includeInnerText)
                                {
                                    handler.characters(cbuf, mark+1, offset-mark-1);
                                }
                                mark = offset;
                                continue;
                        }
                        continue;
                    
                    case '>':
                        switch(state)
                        {
                            case STATE_IGNORE:                                
                                if(stateBeforeComment==0)
                                    state = 0;
                                continue;
                            case STATE_EL_TEXT:// uncommented text
                            case STATE_COMMENT_STARTED:
                                continue;
                            case STATE_CDATA_ENDING:
                                state = STATE_CDATA_STARTED;
                                continue;
                            case STATE_EL_ENDING:
                                state = STATE_EL_ENDED;                                
                                if(!handler.endElement())
                                    return;
                                elwsMark = -1;
                                continue;
                            case STATE_EL_ATTR_NAME_START:
                            case STATE_EL_STARTING:
                                if(elwsMark==-1)
                                {
                                    String name = null;
                                    String namespace = null;                                    
                                    if(nsMark==-1)
                                        name = new String(cbuf, mark+1, offset-mark-1).trim();
                                    else
                                    {
                                        namespace = new String(cbuf, mark+1, nsMark-mark-1).trim();
                                        name = new String(cbuf, nsMark+1, offset-mark-1).trim();
                                    }
                                    if(searchRoot)
                                    {
                                        if(!handler.rootElement(name, namespace))
                                            return;
                                        searchRoot = false;
                                    }
                                    else if(!handler.startElement(name, namespace))
                                        return;                                                                    
                                }
                                nsMark = -1;
                                elwsMark = -1;
                                state = STATE_EL_STARTED;
                                mark = -1;
                                continue;
                            case STATE_COMMENT_ENDING:
                                state = stateBeforeComment;
                                continue;
                            case STATE_CDATA_ENDED:
                                state = STATE_EL_TEXT;
                                if(mark!=-1 && includeInnerText)
                                {
                                    handler.characters(cbuf, mark+1, offset-2-mark-1);
                                }
                                mark = offset;
                                continue;
                        }
                        continue;
                        
                    case '/':
                        switch(state)
                        {
                            case STATE_COMMENT_STARTED:
                            case STATE_IGNORE:                                
                                continue;
                            case STATE_COMMENT_ENDING://handle --/ comments
                                state = STATE_COMMENT_STARTED;
                                continue;
                            case STATE_CDATA_ENDING:
                            case STATE_CDATA_ENDED:
                                state = STATE_CDATA_STARTED;
                                continue;
                            case STATE_EL_ATTR_NAME_START:
                                mark = -1;                                
                                state = STATE_EL_ENDING;
                                continue;
                                
                            case STATE_EL_STARTED:
                                state = STATE_EL_TEXT;
                                mark = offset-1;
                                continue;
                            case STATE_EL_STARTING:
                                state = STATE_EL_ENDING;
                                elwsMark = -1;
                                continue;
                                
                        }
                        continue;
                    
                    case ':':
                        switch(state)
                        {
                            case STATE_COMMENT_STARTED:
                            case STATE_IGNORE:                                
                                continue;
                            case STATE_COMMENT_ENDING://handle --: comments
                                state = STATE_COMMENT_STARTED;
                                continue;
                            case STATE_CDATA_ENDING:
                            case STATE_CDATA_ENDED:
                                state = STATE_CDATA_STARTED;
                                continue;
                            case STATE_EL_STARTING:
                                if(nsMark!=-1)
                                    throw new IOException("invalid xml.");
                                nsMark = offset;
                                continue;
                        }                        
                        continue;
                    case '?':
                        switch(state)
                        {
                            case STATE_COMMENT_STARTED:
                            case STATE_IGNORE:                                
                                continue;
                            case STATE_COMMENT_ENDING://handle --? comments
                                state = STATE_COMMENT_STARTED;
                                continue;   
                            case STATE_CDATA_ENDING:
                            case STATE_CDATA_ENDED:
                                state = STATE_CDATA_STARTED;
                                continue;
                            case STATE_EL_STARTING:
                                // uncommented text
                                if(stateBeforeComment==STATE_EL_TEXT)
                                    continue;
                                state = STATE_COMMENT_STARTING;
                                mark = -1;
                                continue;
                        }
                        continue;
                    case '!':
                        switch(state)
                        {
                            case STATE_COMMENT_STARTED:
                            case STATE_IGNORE:                                
                                continue; 
                            case STATE_COMMENT_ENDING://handle --! comments
                                state = STATE_COMMENT_STARTED;
                                continue;   
                            case STATE_CDATA_ENDING:
                            case STATE_CDATA_ENDED:
                                state = STATE_CDATA_STARTED;
                                continue;
                            case STATE_EL_STARTING:
                                state = STATE_COMMENT_STARTING;
                                mark = -1;
                                continue;
                        }
                        continue;
                    case '[':
                        switch(state)
                        {
                            case STATE_COMMENT_STARTED:
                            case STATE_IGNORE:                                
                                continue;
                            case STATE_COMMENT_ENDING://handle --[ comments
                                state = STATE_COMMENT_STARTED;
                                continue;
                            case STATE_CDATA_ENDING:
                            case STATE_CDATA_ENDED:
                                state = STATE_CDATA_STARTED;
                                continue;
                            case STATE_COMMENT_STARTING:
                                state = STATE_CDATA_STARTING;
                                if(mark!=-1 && includeInnerText)
                                {
                                    handler.characters(cbuf, mark+1, offset-2-mark-1);
                                }
                                mark = -1;
                                continue;
                            case STATE_CDATA_STARTING:
                                state = STATE_CDATA_STARTED;
                                mark = offset;
                                continue;
                        }
                        continue;
                    case ']':
                        switch(state)
                        {
                            case STATE_COMMENT_STARTED:
                            case STATE_IGNORE:                                
                                continue;
                            case STATE_COMMENT_ENDING://handle --[ comments
                                state = STATE_COMMENT_STARTED;
                                continue;
                            case STATE_CDATA_ENDED:
                                state = STATE_CDATA_STARTED;
                                continue;
                            case STATE_CDATA_STARTED:
                                state = STATE_CDATA_ENDING;
                                continue;
                            case STATE_CDATA_ENDING:
                                state = STATE_CDATA_ENDED;
                                continue;                                
                        }
                        continue;
                        
                    case '-':
                        switch(state)
                        {
                            case STATE_IGNORE:                                
                                continue;
                            case STATE_COMMENT_STARTING:
                                state = STATE_COMMENT_DASH_START;
                                continue;
                            case STATE_COMMENT_DASH_START:
                                state = STATE_COMMENT_STARTED;
                                continue;
                            case STATE_COMMENT_STARTED:
                                state = STATE_COMMENT_DASH_END;
                                continue;
                            case STATE_COMMENT_DASH_END:
                                state = STATE_COMMENT_ENDING;
                                continue;
                            case STATE_COMMENT_ENDING:// handle ---- text
                                state = STATE_COMMENT_STARTED;
                                continue;
                        }
                        continue;
                        
                    case '=':                        
                        switch(state)
                        {
                            case STATE_COMMENT_STARTED:
                            case STATE_IGNORE:                                
                                continue;
                            case STATE_COMMENT_ENDING://handle --= comments
                                state = STATE_COMMENT_STARTED;
                                continue;   
                            case STATE_CDATA_ENDING:
                            case STATE_CDATA_ENDED:
                                state = STATE_CDATA_STARTED;
                                continue;
                            case STATE_EL_ATTR_NAME_START:                                
                                state = STATE_EL_ATTR_VALUE_START;
                                attrName = new String(cbuf, mark+1, offset-mark-1).trim();                                
                                mark = -1;
                                continue;
                        }                        
                        continue;
                        
                    
                    case '\'':                        
                        switch(state)
                        {
                            case STATE_COMMENT_STARTED:
                            case STATE_IGNORE:                                
                                continue;
                            case STATE_COMMENT_ENDING://handle --' comments
                                state = STATE_COMMENT_STARTED;
                                continue;   
                            case STATE_CDATA_ENDING:
                            case STATE_CDATA_ENDED:
                                state = STATE_CDATA_STARTED;
                                continue;
                            case STATE_EL_ATTR_VALUE_START:
                                dq = false;
                                state = STATE_EL_ATTR_VALUE_END;
                                mark = offset;
                                continue;
                                
                            case STATE_EL_ATTR_VALUE_END:
                                if(dq)
                                    continue;
                                state = STATE_EL_STARTING;
                                attrValue = new String(cbuf, mark+1, offset-mark-1).trim();
                                handler.attribute(attrName, attrValue);
                                attrName = null;
                                attrValue = null;                                
                                mark = -1;
                                continue;

                        }                        
                        continue;
                    case '"':                            
                        switch(state)
                        {
                            case STATE_COMMENT_STARTED:
                            case STATE_IGNORE:                                
                                continue;
                            case STATE_COMMENT_ENDING://handle --" comments
                                state = STATE_COMMENT_STARTED;
                                continue;   
                            case STATE_CDATA_ENDING:
                            case STATE_CDATA_ENDED:
                                state = STATE_CDATA_STARTED;
                                continue;
                            case STATE_EL_ATTR_VALUE_START:
                                dq = true;
                                state = STATE_EL_ATTR_VALUE_END;
                                mark = offset;
                                continue;
                                
                            case STATE_EL_ATTR_VALUE_END:
                                if(!dq)
                                    continue;
                                state = STATE_EL_STARTING;
                                attrValue = new String(cbuf, mark+1, offset-mark-1).trim();
                                handler.attribute(attrName, attrValue);
                                attrName = null;
                                attrValue = null;                                
                                mark = -1;
                                continue;

                        }
                        continue;
                        
                    case ' ':
                    case '\t':
                    case '\r':
                    case '\n':
                        switch(state)
                        {
                            case STATE_COMMENT_STARTED:
                            case STATE_IGNORE:                                
                                continue;
                            case STATE_EL_STARTING:
                                state = STATE_EL_ATTR_NAME_START;
                                if(elwsMark==-1)
                                {                                    
                                    String name = null;
                                    String namespace = null;
                                    if(nsMark==-1)
                                        name = new String(cbuf, mark+1, offset-mark-1).trim();
                                    else
                                    {
                                        namespace = new String(cbuf, mark+1, nsMark-mark-1).trim();
                                        name = new String(cbuf, nsMark+1, offset-nsMark-1).trim();
                                    }
                                    if(searchRoot)
                                    {
                                        if(!handler.rootElement(name, namespace))
                                            return;
                                        searchRoot = false;
                                    }
                                    else if(!handler.startElement(name, namespace))
                                        return;                                
                                }
                                nsMark = -1;
                                elwsMark = offset;
                                mark = offset;                           
                                continue;
                        }
                        continue;
                        
                    default:
                        switch(state)
                        {                            
                            case STATE_COMMENT_STARTED:
                            case STATE_IGNORE:                                
                                continue;
                            case STATE_COMMENT_DASH_END:
                            case STATE_COMMENT_ENDING:
                                state = STATE_COMMENT_STARTED;
                                continue;
                            case STATE_CDATA_ENDING:
                            case STATE_CDATA_ENDED:
                                state = STATE_CDATA_STARTED;
                                continue;
                            
                            case STATE_EL_STARTED:                                
                                state = STATE_EL_TEXT;
                                if(includeInnerText)
                                    mark = offset-1;
                                continue;

                            case STATE_COMMENT_STARTING:                                
                                if(stateBeforeComment!=0)
                                    throw new IOException("invalid xml.");
                                
                                mark = -1;
                                state = STATE_IGNORE;
                                continue;
                            case STATE_COMMENT_DASH_START:
                                throw new IOException("invalid xml.");
                        }
                        continue;
                }            
            }
            
            if(mark==-1)
                offset = 0;
            else
            {
                if(state==STATE_EL_TEXT)
                {
                    if(includeInnerText)
                        handler.characters(cbuf, mark+1, offset-mark-2);                    
                    offset = 0;
                    mark = -1;
                }
                else
                {
                    offset = cbuf.length-mark;
                    System.arraycopy(cbuf, mark, cbuf, 0, offset);
                    mark = 0;   
                }             
            }
        }        
    }    

}
