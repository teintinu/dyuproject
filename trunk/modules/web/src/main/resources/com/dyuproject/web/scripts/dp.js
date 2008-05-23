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

(function(){
var _dp_context = this;
var dp = {
	Loader: {
		getContext: function() {
			return _dp_context;
		},
		addBaseClass: function(simpleName, clazz) {
			dp[simpleName] = clazz;
		},
		addSingleton: function(simpleName, clazz) {			
			dp[simpleName] = typeof clazz=='function' ? new clazz() : clazz;
		}
	}
};

var Utils = {
    isBoolean: function(obj) {
        return typeof obj == 'boolean';
    },
    isNumber: function(obj) {
        return typeof obj == 'number';
    },
    isString: function(obj) {
        return typeof obj == 'string';
    },    
    isArray: function(obj) {
        return obj && !Utils.isString(obj) ? Utils.isNumber(obj.length) : false;
    },
    isNode: function(obj) {
        return obj && obj.nodeName;
    },
    isWidget: function(obj) {
        return obj && obj.getElement;
    },
	isDraggable: function(el) {
		return el && el.__drag;
	},
    applyStyle: function(el, style) {
        if(document.all && !window.opera)
            el.style.setAttribute('cssText', style);
        else
            el.setAttribute('style', style);
    },
    getCssStyle: function(el, str) {
        if(el.currentStyle)
            return el.currentStyle[str];
        else if(window.getComputedStyle)
            return window.getComputedStyle(el, '').getPropertyValue(str);
        return null;    
    },
	getStyleProperty: function(el, prop) {
		var p = el.style[prop];
		return p ? p : Utils.getCssStyle(el, prop);
	},
	getZIndex: function(el) {
		return el.style.zIndex ? el.style.zIndex : (document.all ? Utils.getCssStyle(el, 'zIndex') : Utils.getCssStyle(el, 'z-index'));
	},
    refreshElement: function(el) {
        while(el.hasChildNodes())
            el.removeChild(el.firstChild);
    },
    trim: function(str) {
        return str.replace(/^\s+/g, '').replace(/\s+$/g, '');
    },
    trimLineBreaks: function(str) {
        str.replace(/(\r\n|[\r\n])/g, ' ');
    },
	getParentName: function(el) {		
		var p = el.parentNode;
		if(!p)
			return null;
		var name = p.name ? p.name : p.getAttribute('name');
		return name ? name : Utils.getParentName(p);
	},
    addHandlerToEvent: function(handler, el, ev) {
        if(el.addEventListener) 
            el.addEventListener(ev.substring(2), handler, false);
        else if(el.attachEvent)       
            el.attachEvent(ev, handler);
        else 
            el[ev] = handler;    
    },
    addOnLoad: function(handler) {
        var old_onload = window.onload;
        window.onload = function() {
            if(typeof old_onload == 'function')
                old_onload();
            handler();
        }        
    },
    addOnUnload: function(handler) {
        var old_onunload=window.onunload;
        window.onunload = function() {
            if(typeof old_onunload == 'function')
                old_onunload();
            handler();
        }        
    },    
    getEventSource: function(ev) {
        if(!ev) 
            ev = window.event;
        return ev.target ? ev.target : ev.srcElement;
    },
    toggle: function(el) {
        if(!el.style.display || el.style.display!='none')           
            el.style.display = 'none';
        else
            el.style.display = 'block';
    },
    hideFirstChild: function(parent) {
        if(parent && parent.firstChild)
            parent.firstChild.style.display = 'none';
    },
    hideLastChild: function(parent) {
        if(parent && parent.lastChild)
            parent.lastChild.style.display = 'none';
    },
    getEventCoords: function(ev) {
        var x = 0;
        var y = 0;
        if(document.all) {
			x = document.documentElement.scrollLeft || document.body.scrollLeft;
			y = document.documentElement.scrollTop || document.body.scrollTop;
            x += window.event.clientX;
            y += window.event.clientY;   
        }
        else {
            x = ev.pageX;
            y = ev.pageY;
        }
        return {x:x,y:y};
    },
    getLeft: function(el) {
        var left = 0;   
        for(;;) {               
            if(!el.offsetParent) {
                left+=el.offsetLeft;
                break;
            }
            left +=  (el.offsetLeft - el.scrollLeft);   
            el = el.offsetParent;
        }
        return left;    
    },
    getTop: function(el) {
        var top = 0;    
        for(;;) {               
            if(!el.offsetParent) {
                top+=el.offsetTop;
                break;
            }
            top +=  (el.offsetTop - el.scrollTop);  
            el = el.offsetParent;
        }
        return top;    
    },
	getCoords: function(el) {
		var left = 0;
		var top = 0;
        for(;;) {               
            if(!el.offsetParent) {
				left+=el.offsetLeft;
                top+=el.offsetTop;				
                break;
            }
            left +=  (el.offsetLeft - el.scrollLeft);
			top +=  (el.offsetTop - el.scrollTop);  
            el = el.offsetParent;
        }
        return {x:left,y:top};
	},
    getCenterCoords: function() {
        var scrollLeft = document.documentElement.scrollLeft || document.body.scrollLeft;
        var scrollTop = document.documentElement.scrollTop || document.body.scrollTop;
        var left = document.body.clientWidth/2 + scrollLeft;
        var top = document.body.clientHeight/2 + scrollTop;     
        return {x:left, y:top};     
    },
    positionCenter: function(el) {
        var left = (document.body.clientWidth - el.offsetWidth) /2;
        var top = (document.body.clientHeight - el.offsetHeight)/2;
        var scrollLeft = document.documentElement.scrollLeft || document.body.scrollLeft;
        var scrollTop = document.documentElement.scrollTop || document.body.scrollTop;
        el.style.left = [scrollLeft + left, 'px'].join('');
        el.style.top = [scrollTop + top, 'px'].join('');    
    },
    newXHR: function() {
        return window.XMLHttpRequest ? new XMLHttpRequest() : new ActiveXObject('Microsoft.XMLHTTP'); 
    },
    _script_counter: 0,
    findParentByNodeName: function(el, nodeName) {
        return el.nodeName == nodeName ? el : el.parentNode ? Utils.findParentByNodeName(el.parentNode, nodeName) : null;
    },
    deleteCookie: function(name) {
        document.cookie = [name,'=','; expires=Thu, 01-Jan-70 00:00:01 GMT'].join('');   
    },
	getCookie: function(name) {		
		var c = document.cookie.split(';');
		for(var i=0; i<c.length; i++) {
			var t = Utils.trim(c[i]);			
			if(t.substring(0, name.length)==name)
				return t.substring(name.length+1, t.length);
		}
		return null;
	},
	createCookie: function(name, value, secs) {
		var expires = Utils.isNumber(secs) ? new Date((1000*secs) + new Date().getTime()).toGMTString() : '';	
		document.cookie = [name,'=',value,'; expires=', expires].join('');
	},
	validateForm: function(currentForm, feedbackEl, resolveName) {
		if(resolveName)
			return Utils.validateFormResolveName(currentForm, feedbackEl);
		for(var i=0; i<currentForm.elements.length; i++) {				
			if(currentForm.elements[i].type.toLowerCase()=='text') {			
				var formElement = currentForm.elements[i];
				formElement.value = Utils.trim(formElement.value);			
				if(formElement.value.length<1) {
					var msg = ['Missing field: ', formElement.name.substring(0,1).toUpperCase(), formElement.name.substring(1)].join('');				
					if(Utils.isNode(feedbackEl))
						feedbackEl.innerHTML = msg;
					else
						alert(msg);
					formElement.focus();					
					return false;
				}	
			}
			else if(currentForm.elements[i].type.toLowerCase()=='textarea') {			
				var ta = currentForm.elements[i];
				ta.value = Utils.trimLineBreaks(ta.value);
				ta.value = Utils.trim(ta.value);
				if(ta.value.length<1) {
					var msg = ['Missing field: ', ta.name.substring(0,1).toUpperCase(), ta.name.substring(1)].join('');
					if(Utils.isNode(feedbackEl))
						feedbackEl.innerHTML = msg;
					else
						alert(msg);
					return false;
				}		
			}		
			else if(currentForm.elements[i].nodeName.toLowerCase()=='select') {			
				var sel = currentForm.elements[i];						
				if(sel.value.length<1) {
					var msg = ['Missing value: ', sel.name.substring(0,1).toUpperCase(), sel.name.substring(1)].join('');
					if(Utils.isNode(feedbackEl))
						feedbackEl.innerHTML = msg;
					else
						alert(msg);
					return false;
				}				
			}				
			else if(currentForm.elements[i].type.toLowerCase()=='password') {
				var formPw = currentForm.elements[i];
				var firstLength = formPw.value.length;
				var secondLength = Utils.trim(formPw.value).length;
				if(firstLength==0) {
					var msg = ['Missing value: ', Utils.getParentName(formPw)].join('');
					if(Utils.isNode(feedbackEl))
						feedbackEl.innerHTML = msg;
					else
						alert(msg);
					formPw.focus();
					formPw.select();
					return false;				
				}				
				if(firstLength != secondLength) {
					var msg = 'Your password must not contain any whitespace.';
					if(Utils.isNode(feedbackEl))
						feedbackEl.innerHTML = msg;
					else
						alert(msg);
					formPw.focus();
					formPw.select();
					return false;
				}
			}
		
		}	
		return true;	
	},
	validateFormResolveName: function(currentForm, feedbackEl) {
		for(var i=0; i<currentForm.elements.length; i++) {				
			if(currentForm.elements[i].type.toLowerCase()=='text') {			
				var formElement = currentForm.elements[i];
				formElement.value = Utils.trim(formElement.value);			
				if(formElement.value.length<1) {
					var msg = ['Missing field: ', Utils.getParentName(formElement)].join('');				
					if(Utils.isNode(feedbackEl))
						feedbackEl.innerHTML = msg;
					else
						alert(msg);
					formElement.focus();					
					return false;
				}	
			}
			else if(currentForm.elements[i].type.toLowerCase()=='textarea') {			
				var ta = currentForm.elements[i];
				ta.value = Utils.trimLineBreaks(ta.value);
				ta.value = Utils.trim(ta.value);
				if(ta.value.length<1) {
					var msg = ['Missing field: ', Utils.getParentName(ta)].join('');
					if(Utils.isNode(feedbackEl))
						feedbackEl.innerHTML = msg;
					else
						alert(msg);
					return false;
				}	
			}		
			else if(currentForm.elements[i].nodeName.toLowerCase()=='select') {			
				var sel = currentForm.elements[i];						
				if(sel.value.length<1) {
					var msg = ['Missing value: ', Utils.getParentName(sel)].join('');
					if(Utils.isNode(feedbackEl))
						feedbackEl.innerHTML = msg;
					else
						alert(msg);
					return false;
				}				
			}		
			else if(currentForm.elements[i].type.toLowerCase()=='password') {
				var formPw = currentForm.elements[i];
				var firstLength = formPw.value.length;				
				var secondLength = Utils.trim(formPw.value).length;
				if(firstLength==0) {
					var msg = ['Missing value: ', Utils.getParentName(formPw)].join('');
					if(Utils.isNode(feedbackEl))
						feedbackEl.innerHTML = msg;
					else
						alert(msg);
					formPw.focus();
					formPw.select();
					return false;				
				}
				if(firstLength != secondLength) {
					var msg = 'Your password must not contain any whitespace.';
					if(Utils.isNode(feedbackEl))
						feedbackEl.innerHTML = msg;
					else
						alert(msg);
					formPw.focus();
					formPw.select();
					return false;
				}
			}
		}	
		return true;	
	}
};
dp.Loader.addSingleton('Utils', Utils);

var History = {
    handlers: new Array(),
    currentToken: window.location.hash,
    loopKey: null,
    back: function() {
        if(window.history.length)
            window.history.back();
    },
    newItem: function(token) {
        if(token!=History.currentToken)
            window.location.hash = token;         
    },
    onHistoryChanged: function(token) {
        History.currentToken = token;
        var h = History.handlers;
        for(var i in h)
            h[i](token);
    },
    getToken: function() {
        return History.currentToken;
    },
    addHandler: function(handler) {
        var idx = History.handlers.indexOf(handler);
        if(idx==-1) 
            History.handlers.push(handler);        
    },
    loop: function() {
        var token = window.location.hash.substring(1);
        if(token!=History.currentToken) {
            History.currentToken = token;
            History.onHistoryChanged(token);
        }
    },
    stop: function() {
        if(History.loopKey) {
            window.clearInterval(History.loopKey);
            History.loopKey = null;    
        }
    },
    isStarted: function() {
        return History.loopKey!=null;
    },
    start: function() {
        if(!History.loopKey)      
            History.loopKey = window.setInterval(History.loop, 250);
    }
}
//Utils.addOnLoad(History.start);
dp.Loader.addSingleton('History', History);


/* ==================================== IE/OPERA support ==================================== */

//This prototype is provided by the Mozilla foundation and
//is distributed under the MIT license.
//http://www.ibiblio.org/pub/Linux/LICENSES/mit.license

if (!Array.prototype.indexOf)
{
  Array.prototype.indexOf = function(elt /*, from*/)
  {
    var len = this.length;

    var from = Number(arguments[1]) || 0;
    from = (from < 0)
         ? Math.ceil(from)
         : Math.floor(from);
    if (from < 0)
      from += len;

    for (; from < len; from++)
    {
      if (from in this &&
          this[from] === elt)
        return from;
    }
    return -1;
  };
}

/* ==================================== OBJECTS ==================================== */

dp.Loader.addBaseClass('JSObject', function () {
    var _this = this;
    
    this.equals = function(obj) {
        return _this==obj;
    }   
    
    function construct() {}    
    construct();
});

/* TEMPLATE
function Template() {
    var _this = this;
    this.__extends = JSObject;
    this.__extends();
    
    function construct() {
    
    }
    
    construct();
}
*/

dp.Loader.addBaseClass('Indexed', function () {
    var _this = this;
    this.__extends = dp.JSObject;
    this.__extends();
    
    var _index = -1;
    
    this.setIndex = function(index) {
        _index = index;
    }
    
    this.getIndex = function() {
        return _index;
    }
    
    function construct() {}    
    construct();
});

/* ==================================== COLLECTION ==================================== */

dp.Loader.addBaseClass('AbstractCollection', function () {
    var _this = this;
    this.__extends = dp.JSObject;
    this.__extends();
    
	this.isEmpty = function() {}
	this.clear = function() {}
	this.size = function() {}
	this.get = function(obj) {}
	this.remove = function(obj) {}
    
    function construct() {}    
    construct();
});

dp.Loader.addBaseClass('ArrayList', function () {
    var _this = this;
    this.__extends = dp.AbstractCollection;
    this.__extends();
    
    var _list = new Array();
    
    this.add = function(obj) {
        if(obj) {
            var idx = _list.length;
            _list.push(obj);
            _this._a_onAdd(obj, idx, idx+1);
        }
    }
    
    this.remove = function(objOrIndex) {
        if(Utils.isNumber(objOrIndex))            
            return objOrIndex<_list.length ? _list.splice(objOrIndex, 1)[0] : null;
        else if(objOrIndex && objOrIndex.equals) {
            for(var i in _list) {
                if(_list[i].equals(objOrIndex)) {
                    var removed = _list.splice(i, 1)[0];
                    _this._a_onRemove(removed, i, _list.length);
                    return removed;
                }
            }        
        }
        return null;                   
    }
    
    this.get = function(index) {
        return index<_list.length ? _list[index] : null;
    }
    
    this.indexOf = function(obj) {
        if(obj) {
            for(var i in _list) {
                if(_list[i].equals(obj))
                    return i;
            }
        }
        return -1;
    }
	
	this.contains = function(obj) {
		return _this.indexOf(obj)!=-1;
	}
    
    this.size = function() {
        return _list.length;
    }
    
    this.clear = function() {
        //var old = _list;
        //_list = new Array();
        _list.splice(0, _list.length);
        //delete old;               
    }
    
    this.isEmpty = function() {
        return _list.length==0;
    }
    
    this.__wrappedGet = function() {
        return _list;
    }
    
    this._a_onAdd = function(obj, index, size) {}    
    this._a_onRemove = function(obj, index, size) {}
    
    function construct() {}
    construct();    
});

dp.Loader.addBaseClass('IndexedList', function (reflect) {
    var _this = this;
    this.__extends = dp.ArrayList;
    this.__extends();
    
	var _reflect = Utils.isBoolean(reflect) ? reflect : true;
	
    var super_add = this.add;    
    this.add = function(obj) {
        if(obj) {
			if(!obj.setIndex) {
				obj.setIndex = function(idx){obj.__index = idx;};
				obj.getIndex = function(){return obj.__index;};
			}
			super_add(obj);
		}
    }
    
    this._a_onAdd = function(obj, index, size) {
        obj.setIndex(index);
    }
    
    function reflectRemoval(index, size) {
        var list = _this.__wrappedGet();
        for(var i=index; i<size; i++)
            list[i].setIndex(i);
    }
    
    this._a_onRemove = function(obj, index, size) {
        if(_reflect)
			reflectRemoval(index, size);        
    }
    
    function construct() {}    
    construct();
});

dp.Loader.addBaseClass('HashMap', function () {
    var _this = this;
    this.__extends = dp.AbstractCollection;
    this.__extends();
    
    var _map = {};
    var _size = 0;
    
    this.put = function(key, value) {
        if(key && value) {
            _map[key] = value;                      
            _this._a_onPut(key, value, ++_size);       
        }
    }
    
    this.get = function(key) {
        return key ? _map[key] : null;            
    }
    
    this.remove = function(key) {
        if(key) {
            var removed = _map[key];
            _map[key] = null;            
            _this._a_onRemove(key, removed, --_size);
            return removed;
        }
        return null;
    }
    
    this.size = function() {
        return _size;
    }
    
    this.clear = function() {
        //var old = _map;
        _map = {};
        _size = 0;
        //delete old;
    }
    
    this.isEmpty = function() {
        return _size == 0;
    }
    
    this.__wrappedGet = function() {
        return _map;
    }
    
    this._a_onPut = function(key, value, size) {}
    this._a_onRemove = function(key, value, size) {}
    
    function construct() {}
    construct();    
});

dp.Loader.addBaseClass('IndexedMap', function (reflect) {
    var _this = this;
    this.__extends = dp.HashMap;
    this.__extends();
    
	var _reflect = Utils.isBoolean(reflect) ? reflect : true;
    
	var super_put = this.put;
    this.put = function(key, value) {
        if(value) {
			if(!value.setIndex) {
				value.setIndex = function(idx){value.__index = idx;};
				value.getIndex = function(){return value.__index;};
			}
			super_put(key, value);
		}
    }    
    
    this._a_onPut = function(key, value, size) {
        value.setIndex(size-1);
    }
    
    function reflectRemoval(value, size) {
        var map = _this.__wrappedGet();
        var index = value.getIndex();		
        for(var i in map) {
			var v = map[i];
			if(v && v.getIndex()>index)			
				v.setIndex(v.getIndex()-1);
        }
    }
    
    this._a_onRemove = function(key, value, size) {
        if(_reflect)
			reflectRemoval(value, size);
    }
    
    function construct() {}    
    construct();
});

/* ==================================== SERVER REQUESTS ==================================== */
dp.Loader.addBaseClass('PooledRequest', function (pl) {    
    var _this = this;
    this.__extends = dp.JSObject;
    this.__extends();
    
    var _pool = pl;
    var _request = Utils.newXHR();
    var _currentHandler = null;
    
    this.handle = function(data) {      
        var handler = _currentHandler;
        _currentHandler = null;
        _pool.push(_this);
        handler(data);
    }
    
    this.eval = function(data) {
        _this.handle(window.eval(['(',data,')'].join('')));
        //return window.eval(data);
    }
    
    function handleDefault() {
        if(_request.readyState == 4) {
            if(_request.status == 200)
                _this.eval(_request.responseText);
        }       
    }
	
	this.sendCachable = function(url, params, handler, method) {
		_currentHandler = handler;
		_request.open(method, url, true);
		_request.onreadystatechange = handleDefault;
		_request.send(null);
	}
	
    this.send = function(url, params, handler, method) {
        //var theURL = url.indexOf('?')==-1 ?[ url, '?callback=this.handle'].join('') : [url,'&callback=this.handle'].join('');
        _currentHandler = handler;
        _request.open(method, url, true);
        _request.onreadystatechange = handleDefault; 
        _request.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        _request.setRequestHeader('Content-Length', params.length);
        _request.send(params);
    }
	
	this.doGet = function(url, params, handler) {
		_this.sendCachable(url, params, handler, 'GET');
	}
	
	this.doDelete = function(url, params, handler) {
		_this.sendCachable(url, params, handler, 'DELETE');
	}
	
	this.doPost = function(url, params, handler) {
		_this.send(url, params, handler, 'POST');
	}
	
	this.doPut = function(url, params, handler) {
		_this.send(url, params, handler, 'PUT');
	}

    this.getRequest = function() {
        return _request;
    }
    
    function construct() {}    
    construct();   
});

dp.Loader.addBaseClass('RequestPool', function (size) {
    var _this = this;
    this.__extends = dp.JSObject;
    this.__extends();
    
    var _requests = new Array();
    
    this.push = function(req) {
        _requests.push(req);
    }
    
    this.addNew = function() {
        _requests.push(new dp.PooledRequest(_this));
    }
    
    this.send = function(url, params, handler) {
        _requests.shift().send(url, params, handler, 'POST');
    }
	
    this.doPut = function(url, params, handler) {     
        _requests.shift().doPut(url, params, handler);
    }

    this.doPost = function(url, params, handler) {     
        _requests.shift().doPost(url, params, handler);
    }
	
    this.doGet = function(url, params, handler) {     
        _requests.shift().doGet(url, params, handler);
    }

    this.doDelete = function(url, params, handler) {     
        _requests.shift().doDelete(url, params, handler);
    }	
    
    function construct(size) {
        var len = size ? size : 4;
        for(var i=0; i<len; i++)
            _requests.push(new dp.PooledRequest(_this));
    }    
    
    construct(size);
});

dp.Loader.addBaseClass('IFrameRequest', function () {
    var _this = this;
    this.__extends = dp.JSObject;
    this.__extends();
        
    var _iframeDiv = null;
    
    function scheduleRemove() {
        _iframeDiv.removeChild(_iframeDiv.firstChild);
    }
        
    function cleanIFrameDiv(e, handler) {
        //var textarea = document.createElement('textarea');
        var value = null;             
        if(e.currentTarget) 
            value = e.currentTarget.contentDocument.body.firstChild.innerHTML;        
        else if(e.srcElement) 
            value = e.srcElement.contentWindow.document.body.firstChild.innerHTML;             
        if(handler)  
            handler(window.eval(['(',value,')'].join('')));        
        //window.setTimeout(scheduleRemove, 40);
    }    
    
    this.send = function(url, params, handler) {
        var delim = url.indexOf('?')==-1 ? '?' : '&';
        var ifr = document.createElement('iframe');
        Utils.addHandlerToEvent(function(e){cleanIFrameDiv(e, handler)}, ifr, 'onload');
        Utils.applyStyle(ifr, 'display:none');
        ifr.src = [url, delim, params].join('');      
        _iframeDiv.appendChild(ifr);        
    }
    
    function construct() {
        _iframeDiv = document.createElement('div');
        Utils.applyStyle(_iframeDiv, 'display:none');
        document.body.appendChild(_iframeDiv);        
    }
    
    construct();
});

dp.Loader.addBaseClass('ScriptRequest', function () {
    var _this = this;
    this.__extends = dp.JSObject;
    this.__extends();
        
    var _scriptDiv = null;    
    var _name = ['scr',Utils._script_counter++].join('');
    var _handlers = new Array();    
    
    this.handle = function(msgs) {        
        var handler = _handlers.shift();
        if(handler)
            handler(msgs);          
    }
    
    this.send = function(url, params, handler) {
        var delim = url.indexOf('?')==-1 ? ['?callback=',_name,'.handle&'].join('') : ['&callback=',_name,'.handle&'].join('');
        var scr = document.createElement('script');
        // wont work with IE
        //Utils.addHandlerToEvent(update, scr, 'onload');
        Utils.applyStyle(scr, 'display:none');
        _handlers.push(handler);
        scr.src = [url, delim, params].join('');      
        _scriptDiv.appendChild(scr);   
    }
    
    function construct() {
        window[_name] = _this;
        _scriptDiv = document.createElement('div');
        Utils.applyStyle(_scriptDiv, 'display:none');
        document.body.appendChild(_scriptDiv);        
    }
    
    construct();   
});

/* ==================================== COMETD ==================================== */

dp.Loader.addBaseClass('CometdClient', function (u, obj, type) {
    var _this = this;
    this.__extends = dp.JSObject;
    this.__extends();    
    
    var _url = u.length-1 == u.lastIndexOf('/') ? u.substring(0, u.length-1) : u; 
    var _context = obj;
    var _request = null; 
    var _channels = {};
    var _connected = false;    
    var _started = false;
    var _connectCallback = null;
    var _id = null;  
    
    function handle(msgs) {
        if(msgs) {            
            if(_started)
                _request.send(_url, 'action=reconnect', handle);
            else
                _connected = false;
            if(Utils.isArray(msgs)) {
                for(var i in msgs) {
                    var m = msgs[i];
                    var cb = _channels[m.channel];                                  
                    if(cb) {
                        if(_context && Utils.isString(cb))
                            _context[cb](m);
                        else
                            cb(m);
                    }                        
                }            
            }
        }
    }
    
    function onConnect(c) {
        _id = c.id;        
        for(var i in c.channels) 
            _channels[c.channels[i]] = 'tmp';
        _connected = true;        
        if(Utils.isString(_connectCallback))
            _context[_connectCallback]();
        else if(_connectCallback)
            _connectCallback();
        _request.send(_url, 'action=reconnect', handle);        
    }
        
    this.isConnected = function() {
        return _connected;
    }
    
    this.getId = function() {
        return _id;
    }
    
    this.start = function(callback) {        
        if(!_connected) { 
            _connectCallback = callback;
            _request.send(_url, 'action=connect', onConnect);            
        }            
        _started = true;               
    }
    
    this.stop = function(callback) {
        if(_connected) {
            _connected = false;            
            _request.send(_url, 'action=disconnect', callback ? callback : function(){});
        }
        _started = false;
    }    
    
    this.publish = function(channel, data) {
        if(_connected) {
            if(channel.lastIndexOf('/')==channel.length-1)
                channel = channel.substring(0, channel.length-1); 
            var ch = _channels[channel];
            if(ch) 
                _request.send(_url + channel + '?action=publish', data, handle);                   
        }
    }
    
    this.subscribe = function(channel, callback) {
        if(_connected) {                        
            if(channel.lastIndexOf('/')==channel.length-1)
                channel = channel.substring(0, channel.length-1);        
            var old = _channels[channel];
            _channels[channel] = callback;
            if(!old)
                _request.send(_url + channel, 'action=subscribe', handle);
        }
    }
    
    this.unsubscribe = function(channel) {
        if(_connected) {                        
            if(channel.lastIndexOf('/')==channel.length-1)
                channel = channel.substring(0, channel.length-1);              
            var ch = _channels[channel];
            if(ch) {
                chanels[channel] = null;
                _request.send(_url + channel, 'action=unsubscribe', handle);
            }      
        }    
    }
    
    function construct(u, obj, type) {
        if(type) {
            if(type == 'iframe')
                _request = new dp.IFrameRequest();
            else if(type == 'script')
                _request = new dp.ScriptRequest();//only works on Firefox
            else if(type == 'xhr')
                _request = new dp.RequestPool(5);            
        }    
        if(!_request)
            _request = new dp.RequestPool(5);               
    }
    
    construct(u, obj, type);
});

/* ==================================== MOVING ELEMENTS ==================================== */

var DragUtil = {
	getInitialLeft: function(el) {
		if(el.style.left) 
			return parseInt(el.style.left);
		var left = Utils.getCssStyle(el, 'left');
		var idx = left.indexOf('px');
		return idx==-1 ? 0 : parseInt(left.substring(0, idx));
	},
	getInitialTop: function(el) {
		if(el.style.top)
			return parseInt(el.style.top);
		var top = Utils.getCssStyle(el, 'top');
		var idx = top.indexOf('px');
		return idx==-1 ? 0 : parseInt(top.substring(0, idx));
	},
	_draggables: new dp.HashMap(),
	_dropabbles: new dp.HashMap(),
	addDrag: function(el, drag) {
		DragUtil._draggables.put(el, drag);
	},
	addDragDrop: function(el, dragdrop) {
		DragUtil._droppables.put(el, dragdrop);
	},
	_front: null,
	bringToFront: function(draggable) {		
		if(Utils.isNode(draggable))
			draggable = draggable.__drag;
		if(draggable && draggable.bringToFront) {
			if(DragUtil._front)
				DragUtil._front.bringToBack();
			DragUtil._front = draggable;
			draggable.bringToFront();
		}
	}
};
dp.Loader.addSingleton('DragUtil', DragUtil);

dp.Loader.addBaseClass('Draggable', function (el, controlEl) {
    var _this = this;
    this.__extends = dp.JSObject;
    this.__extends();     
    
    var _el = null;  
    var _currentX = 0;
    var _currentY = 0;
    var _oldZIndex = null;    
    var _controlEl = null;
    var _position = null;
	var _originalCoords = null;	
	
	this.getElement = function() {
		return _el;
	}
	
	this.bringToFront = function() {
		_el.style.zIndex = 10000;
	}
	
	this.bringToBack = function() {
		_el.style.zIndex = _oldZIndex;			
	}
	
	this.reset = function() {
		if(_originalCoords) {
			if(_position!='absolute' && _position!='fixed') {
				_el.style.left = [_originalCoords.relX, 'px'].join('');
				_el.style.top = [_originalCoords.relY, 'px'].join('');
			}
			else {
				_el.style.left = [_originalCoords.initX, 'px'].join('');
				_el.style.top = [_originalCoords.initY, 'px'].join('');
			}
		}
	}
    
    function mouseMove(e) {
        if(!e) e = window.event;
        _el.style.left = [_currentX + e.clientX, 'px'].join('');
        _el.style.top = [_currentY + e.clientY, 'px'].join('');     
    }
    
    function mouseUp(e) {        
        document.onmousemove = null;
        document.onselectstart = null;
        document.onmouseup = null;          
    }
    
    function mouseDown(e) {     
        if(!e) e = window.event;
        var el = e.target ? e.target : e.srcElement;
        if(el!=_controlEl) {
			if(el==_el)
				DragUtil.bringToFront(_this);			
			return;
		}
        if(_position!='absolute' && _position!='fixed') {
			if(!_originalCoords) {				
				_originalCoords = Utils.getCoords(_el);
				_originalCoords.relX = DragUtil.getInitialLeft(_el);
				_originalCoords.relY = DragUtil.getInitialTop(_el);
				_currentX = _originalCoords.relX - e.clientX;
				_currentY = _originalCoords.relY - e.clientY;
			}
			else {
				var offsetX = _el.style.left ? parseInt(_el.style.left) : 0;
				var offsetY = _el.style.top ? parseInt(_el.style.top) : 0;
				_currentX = offsetX - e.clientX;
				_currentY = offsetY - e.clientY;         
			}
        }
        else {
			if(!_originalCoords) {				
				_originalCoords = Utils.getCoords(_el);			
				_originalCoords.initX = DragUtil.getInitialLeft(_el);
				_originalCoords.initY = DragUtil.getInitialTop(_el);				
			}
            _currentX = _el.offsetLeft - e.clientX;
            _currentY = _el.offsetTop - e.clientY;
        }        
        DragUtil.bringToFront(_this);      
        document.onmouseup = mouseUp;
        document.onmousemove = mouseMove;
        document.body.focus();
        document.onselectstart = function(){return false};
        return false;           
    }
    
    function construct(el, controlEl) {
		_el = el;		
        _el.onmousedown = mouseDown;
		_el.style.cursor = 'default';
		_oldZIndex = Utils.getZIndex(_el);
		_controlEl = controlEl;
		if(_controlEl)
			_controlEl.style.cursor = 'default';
		else
			_controlEl = _el;		
        _position = _el.style.position ? _el.style.position : Utils.getCssStyle(_el, 'position');       
        if(_position!='absolute' && _position!='fixed')
            _el.style.position = 'relative';
		_el.__drag = _this;
		DragUtil.addDrag(_el, _this);
    }
    
    construct(el, controlEl);
});

dp.Loader.addBaseClass('DraggableBounded', function (el, controlEl, container) {
    var _this = this;
    this.__extends = dp.JSObject;
    this.__extends();     
    
    var _el = null;  
    var _currentX = 0;
    var _currentY = 0;
    var _oldZIndex = null;    
    var _controlEl = null;
    var _position = null;
	var _originalCoords = null;	
	var _container = null;
	var _parentPoints = null;
	var _parentOffset = null;
	
	this.getElement = function() {
		return _el;
	}
	
	this.bringToFront = function() {
		_el.style.zIndex = 10000;
	}
	
	this.bringToBack = function() {
		_el.style.zIndex = _oldZIndex;			
	}	
	
	this.reset = function() {
		if(_originalCoords) {
			if(_position!='absolute' && _position!='fixed') {
				_el.style.left = [_originalCoords.relX, 'px'].join('');
				_el.style.top = [_originalCoords.relY, 'px'].join('');
			}
			else {
				_el.style.left = [_originalCoords.initX, 'px'].join('');
				_el.style.top = [_originalCoords.initY, 'px'].join('');
			}
		}
	}
	
    function mouseMove(e) {
        if(!e) e = window.event;		
		var x = _currentX + e.clientX + _originalCoords.x - _originalCoords.relX + _parentOffset.x;
		var y = _currentY + e.clientY + _originalCoords.y - _originalCoords.relY + _parentOffset.y;
		if(_parentPoints.xa<x+1 && _parentPoints.ya<y+1 && _parentPoints.xb>x-1+_el.offsetWidth && _parentPoints.yb>y-1+_el.offsetHeight) {
			_el.style.left = [_currentX + e.clientX, 'px'].join('');
			_el.style.top = [_currentY + e.clientY, 'px'].join('');
		}
    }	
    
    function mouseUp(e) {        
        document.onmousemove = null;
        document.onselectstart = null;
        document.onmouseup = null;        
    }
    
    function mouseDown(e) {     
        if(!e) e = window.event;
        var el = e.target ? e.target : e.srcElement;
        if(el!=_controlEl) {
			if(el==_el)
				DragUtil.bringToFront(_this);			
			return;
		}   
        if(_position!='absolute' && _position!='fixed') {
			if(!_originalCoords) {
				_parentOffset = {x:0, y:0};
				_originalCoords = Utils.getCoords(_el);
				_originalCoords.relX = DragUtil.getInitialLeft(_el);
				_originalCoords.relY = DragUtil.getInitialTop(_el);				
				if(_container==document.body) {
					_parentCoords = {x:0, y:0, relX:0, relY:0};					
					_parentPoints = {
						xa: _parentCoords.x, 
						xb: _parentCoords.x+document.body.clientWidth, 
						ya: _parentCoords.y, 
						yb: document.body.clientHeight
					};				
				}
				else {
					_parentCoords = Utils.getCoords(_container);
					_parentCoords.relX = 0;
					_parentCoords.relY = 0;
					_parentPoints = {
						xa: _parentCoords.x, 
						xb: _parentCoords.x+_container.offsetWidth, 
						ya: _parentCoords.y, 
						yb: _parentCoords.y+_container.offsetHeight
					};				
				}				
				_currentX = _originalCoords.relX - e.clientX;
				_currentY = _originalCoords.relY - e.clientY;
			}
			else {
				var offsetX = _el.style.left ? parseInt(_el.style.left) : 0;
				var offsetY = _el.style.top ? parseInt(_el.style.top) : 0;
				_currentX = offsetX - e.clientX;
				_currentY = offsetY - e.clientY;         
			}
        }
        else {
			if(!_originalCoords) {				
				_originalCoords = Utils.getCoords(_el);
				_originalCoords.initX = DragUtil.getInitialLeft(_el);
				_originalCoords.initY = DragUtil.getInitialTop(_el);
				if(_container==document.body) {					
					_parentCoords = {x:0, y:0, relX:0, relY:0};
					_parentOffset = {x:0, y:0};
					_parentPoints = {
						xa: _parentCoords.x, 
						xb: _parentCoords.x+document.body.clientWidth, 
						ya: _parentCoords.y, 
						yb: document.body.clientHeight
					};				
				}
				else {
					_parentCoords = Utils.getCoords(_container);
					_parentCoords.relX = DragUtil.getInitialLeft(_container);
					_parentCoords.relY = DragUtil.getInitialTop(_container);
					var pos = _container.style.position ? _container.style.position : Utils.getCssStyle(_container, 'position');
					if(pos=='static')
						_parentOffset = {x:0, y:0};
					else
						_parentOffset = _parentCoords;
					_parentPoints = {
						xa: _parentCoords.x, 
						xb: _parentCoords.x+_container.offsetWidth, 
						ya: _parentCoords.y, 
						yb: _parentCoords.y+_container.offsetHeight
					};				
				}				
			}
            _currentX = _el.offsetLeft - e.clientX;
            _currentY = _el.offsetTop - e.clientY;
        }        
        DragUtil.bringToFront(_this);      
        document.onmouseup = mouseUp;
        document.onmousemove = mouseMove;
        document.body.focus();
        document.onselectstart = function(){return false};
        return false;           
    }
    
    function construct(el, controlEl, container) {
		_el = el;		
        _el.onmousedown = mouseDown;
		_el.style.cursor = 'default';
		_oldZIndex = Utils.getZIndex(_el);
		_controlEl = controlEl;
		if(_controlEl)
			_controlEl.style.cursor = 'default';
		else
			_controlEl = _el;		
        _position = _el.style.position ? _el.style.position : Utils.getCssStyle(_el, 'position');       
        if(_position!='absolute' && _position!='fixed')
            _el.style.position = 'relative';
		_container = Utils.isNode(container) ? container : _el.parentNode;
		_el.__drag = _this;
		DragUtil.addDrag(_el, _this);
    }
    
    construct(el, controlEl, container);
});

dp.Loader.addBaseClass('DraggableProxy', function (el, controlEl, proxyClassName, moveOriginal, resizeProxy) {
    var _this = this;
    this.__extends = dp.JSObject;
    this.__extends();     
    
    var _el = null;  
    var _currentX = 0;
    var _currentY = 0;
    var _oldZIndex = null;    
    var _controlEl = null;
    var _position = null;
	var _originalCoords = null;
	var _moveEl = null;	
	var _resizeProxy = true;
	var _moveOriginal = true;	
	
	this.getElement = function() {
		return _el;
	}
	
	this.bringToFront = function() {
		_el.style.zIndex = 10000;
	}
	
	this.bringToBack = function() {
		_el.style.zIndex = _oldZIndex;			
	}	
	
	this.setResizeProxy = function(resizeProxy) {
		_resizeProxy = resizeProxy;
	}
	
	this.isResizeProxy = function() {
		return _resizeProxy;
	}
	
	this.setMoveOriginal = function(moveOriginal) {
		_moveOriginal = moveOriginal;
	}
	
	this.isMoveOriginal = function() {
		return _moveOriginal;
	}
	
	this.reset = function() {
		if(_originalCoords) {
			if(_position!='absolute' && _position!='fixed') {
				_el.style.left = [_originalCoords.relX, 'px'].join('');
				_el.style.top = [_originalCoords.relY, 'px'].join('');
			}
			else {
				_el.style.left = [_originalCoords.initX, 'px'].join('');
				_el.style.top = [_originalCoords.initY, 'px'].join('');
			}
		}
	}
    
    function mouseMove(e) {
        if(!e) e = window.event;
		_moveEl.style.display = '';
        _moveEl.style.left = [_currentX + e.clientX + _originalCoords.x - _originalCoords.relX, 'px'].join('');
        _moveEl.style.top = [_currentY + e.clientY + _originalCoords.y - _originalCoords.relY, 'px'].join('');   
    }
    
    function mouseUp(e) {        
        document.onmousemove = null;
        document.onselectstart = null;
        document.onmouseup = null;
		if(_moveOriginal) {
			var x = parseInt(_moveEl.style.left);
			var y = parseInt(_moveEl.style.top);
			_el.style.left = [x - _originalCoords.x + _originalCoords.relX, 'px'].join('');
			_el.style.top = [y - _originalCoords.y + _originalCoords.relY, 'px'].join('');		
		}
        _moveEl.style.zIndex = null;		
		_moveEl.style.display = 'none';
    }
    
    function mouseDown(e) {     
        if(!e) e = window.event;
        var el = e.target ? e.target : e.srcElement;
        if(el!=_controlEl) {
			if(el==_el)
				DragUtil.bringToFront(_this);			
			return;
		}
        if(_position!='absolute' && _position!='fixed') {
			if(!_originalCoords) {				
				_originalCoords = Utils.getCoords(_el);
				_originalCoords.relX = DragUtil.getInitialLeft(_el);
				_originalCoords.relY = DragUtil.getInitialTop(_el);				
				_currentX = _originalCoords.relX - e.clientX;
				_currentY = _originalCoords.relY - e.clientY;
			}
			else {
				var offsetX = _el.style.left ? parseInt(_el.style.left) : 0;
				var offsetY = _el.style.top ? parseInt(_el.style.top) : 0;			
				_currentX = offsetX - e.clientX;
				_currentY = offsetY - e.clientY;		
			}
        }
        else {
			if(!_originalCoords) {				
				_originalCoords = {x:0, y:0, relX:0, relY:0};			
				_originalCoords.initX = DragUtil.getInitialLeft(_el);
				_originalCoords.initY = DragUtil.getInitialTop(_el);				
			}
			_currentX = _el.offsetLeft - e.clientX;
			_currentY = _el.offsetTop - e.clientY;
        }
        _oldZIndex = _el.style.zIndex;
		if(_resizeProxy) {
			_moveEl.style.width = [_el.offsetWidth, 'px'].join('');
			_moveEl.style.height = [_el.offsetHeight, 'px'].join('');        
		}
		DragUtil.bringToFront(_this);
        _moveEl.style.zIndex = 10000;       
        document.onmouseup = mouseUp;
        document.onmousemove = mouseMove;
        document.body.focus();
        document.onselectstart = function(){return false};
        return false;           
    }
    
    function construct(el, controlEl, proxyClassName, moveOriginal, resizeProxy) {
		_el = el;
        _el.onmousedown = mouseDown;
		_el.style.cursor = 'default';
		_oldZIndex = Utils.getZIndex(_el);
		_controlEl = controlEl;
		if(_controlEl)
			_controlEl.style.cursor = 'default';
		else
			_controlEl = _el;		
        _position = _el.style.position ? _el.style.position : Utils.getCssStyle(_el, 'position');       
        if(_position!='absolute' && _position!='fixed')
            _el.style.position = 'relative';
		_moveEl = document.createElement('div');
		if(proxyClassName)
			_moveEl.className = proxyClassName;
		_moveEl.style.position = 'absolute';
		_moveEl.style.display = 'none';
		document.body.appendChild(_moveEl);
		if(Utils.isBoolean(resizeProxy))
			_resizeProxy = resizeProxy;
		if(Utils.isBoolean(moveOriginal))
			_moveOriginal = moveOriginal;
		_el.__drag = _this;
		DragUtil.addDrag(_el, _this);		
    }
    
    construct(el, controlEl, proxyClassName, moveOriginal, resizeProxy);
});

dp.Loader.addBaseClass('DraggableProxyBounded', function (el, controlEl, container, proxyClassName, moveOriginal, resizeProxy) {
    var _this = this;
    this.__extends = dp.JSObject;
    this.__extends();     
    
    var _el = null;  
    var _currentX = 0;
    var _currentY = 0;
    var _oldZIndex = null;    
    var _controlEl = null;
    var _position = null;
	var _originalCoords = null;
	var _moveEl = null;
	var _parentCoords = null;
	var _parentPoints = null;
	var _parentOffset = null;
	var _resizeProxy = true;
	var _moveOriginal = true;
	var _container = null;	
	
	this.getElement = function() {
		return _el;
	}
	
	this.bringToFront = function() {
		_el.style.zIndex = 10000;
	}
	
	this.bringToBack = function() {
		_el.style.zIndex = _oldZIndex;			
	}	
	
	this.setResizeProxy = function(resizeProxy) {
		_resizeProxy = resizeProxy;
	}
	
	this.isResizeProxy = function() {
		return _resizeProxy;
	}
	
	this.setMoveOriginal = function(moveOriginal) {
		_moveOriginal = moveOriginal;
	}
	
	this.isMoveOriginal = function() {
		return _moveOriginal;
	}
	
	this.setContainer = function(container) {
		if(Utils.isNode(container))
			_container = container;
	}
	
	this.getContainer = function() {
		return _container;
	}
	
	this.reset = function() {
		if(_originalCoords) {
			if(_position!='absolute' && _position!='fixed') {
				_el.style.left = [_originalCoords.relX, 'px'].join('');
				_el.style.top = [_originalCoords.relY, 'px'].join('');
			}
			else {
				_el.style.left = [_originalCoords.initX, 'px'].join('');
				_el.style.top = [_originalCoords.initY, 'px'].join('');
			}
		}
	}
    
    function mouseMove(e) {
        if(!e) e = window.event;
		_moveEl.style.display = '';
		var x = _currentX + e.clientX + _originalCoords.x - _originalCoords.relX + _parentOffset.x;
		var y = _currentY + e.clientY + _originalCoords.y - _originalCoords.relY + _parentOffset.y;
		if(_parentPoints.xa<x+1 && _parentPoints.ya<y+1 && _parentPoints.xb>x-1+_el.offsetWidth && _parentPoints.yb>y-1+_el.offsetHeight) {
			_moveEl.style.left = [x, 'px'].join('');
			_moveEl.style.top = [y, 'px'].join('');
		}
    }
    
    function mouseUp(e) {        
        document.onmousemove = null;
        document.onselectstart = null;
        document.onmouseup = null;
		if(_moveOriginal) {
			var x = parseInt(_moveEl.style.left);
			var y = parseInt(_moveEl.style.top);
			_el.style.left = [x - _originalCoords.x + _originalCoords.relX - _parentOffset.x, 'px'].join('');
			_el.style.top = [y - _originalCoords.y + _originalCoords.relY - _parentOffset.y, 'px'].join('');				
		}		
        _moveEl.style.zIndex = null;		
		_moveEl.style.display = 'none';
    }
    
    function mouseDown(e) {     
        if(!e) e = window.event;
        var el = e.target ? e.target : e.srcElement;
        if(el!=_controlEl) {
			if(el==_el)
				DragUtil.bringToFront(_this);			
			return;
		}
        if(_position!='absolute' && _position!='fixed') {
			if(!_originalCoords) {						
				_parentOffset = {x:0, y:0};
				_originalCoords = Utils.getCoords(_el);
				_originalCoords.relX = DragUtil.getInitialLeft(_el);
				_originalCoords.relY = DragUtil.getInitialTop(_el);
				if(_container==document.body) {
					_parentCoords = {x:0, y:0, relX:0, relY:0};					
					_parentPoints = {
						xa: _parentCoords.x, 
						xb: _parentCoords.x+document.body.clientWidth, 
						ya: _parentCoords.y, 
						yb: document.body.clientHeight
					};				
				}
				else {
					_parentCoords = Utils.getCoords(_container);
					_parentCoords.relX = 0;
					_parentCoords.relY = 0;
					_parentPoints = {
						xa: _parentCoords.x, 
						xb: _parentCoords.x+_container.offsetWidth, 
						ya: _parentCoords.y, 
						yb: _parentCoords.y+_container.offsetHeight
					};				
				}
				_currentX = _originalCoords.relX - e.clientX;
				_currentY = _originalCoords.relY - e.clientY;
			}
			else {
				var offsetX = _el.style.left ? parseInt(_el.style.left) : 0;
				var offsetY = _el.style.top ? parseInt(_el.style.top) : 0;			
				_currentX = offsetX - e.clientX;
				_currentY = offsetY - e.clientY;		
			}
        }
        else {
			if(!_originalCoords) {				
				_originalCoords = {x:0, y:0, relX:0, relY:0};
				_originalCoords.initX = DragUtil.getInitialLeft(_el);
				_originalCoords.initY = DragUtil.getInitialTop(_el);
				if(_container==document.body) {					
					_parentCoords = {x:0, y:0, relX:0, relY:0};
					_parentOffset = {x:0, y:0};
					_parentPoints = {
						xa: _parentCoords.x, 
						xb: _parentCoords.x+document.body.clientWidth, 
						ya: _parentCoords.y, 
						yb: document.body.clientHeight
					};				
				}
				else {
					_parentCoords = Utils.getCoords(_container);
					_parentCoords.relX = DragUtil.getInitialLeft(_container);
					_parentCoords.relY = DragUtil.getInitialTop(_container);
					var pos = _container.style.position ? _container.style.position : Utils.getCssStyle(_container, 'position');
					if(pos=='static')
						_parentOffset = {x:0, y:0};
					else
						_parentOffset = _parentCoords;
					_parentPoints = {
						xa: _parentCoords.x, 
						xb: _parentCoords.x+_container.offsetWidth, 
						ya: _parentCoords.y, 
						yb: _parentCoords.y+_container.offsetHeight
					};				
				}				
			}
			_currentX = _el.offsetLeft - e.clientX;
			_currentY = _el.offsetTop - e.clientY;
        }        
		if(_resizeProxy) {
			_moveEl.style.width = [_el.offsetWidth, 'px'].join('');
			_moveEl.style.height = [_el.offsetHeight, 'px'].join('');        
		}
		DragUtil.bringToFront(_this);
        _moveEl.style.zIndex = 10000; 
        document.onmouseup = mouseUp;
        document.onmousemove = mouseMove;
        document.body.focus();
        document.onselectstart = function(){return false};
        return false;           
    }
    
    function construct(el, controlEl, container, proxyClassName, moveOriginal, resizeProxy) {
		_el = el;
        _el.onmousedown = mouseDown;
		_el.style.cursor = 'default';
		_oldZIndex = Utils.getZIndex(_el);
		_controlEl = controlEl;
		if(_controlEl)
			_controlEl.style.cursor = 'default';
		else
			_controlEl = _el;		
        _position = _el.style.position ? _el.style.position : Utils.getCssStyle(_el, 'position');       
        if(_position!='absolute' && _position!='fixed')
            _el.style.position = 'relative';
		_moveEl = document.createElement('div');
		if(proxyClassName)
			_moveEl.className = proxyClassName;
		_moveEl.style.position = 'absolute';
		_moveEl.style.display = 'none';
		document.body.appendChild(_moveEl);
		if(Utils.isBoolean(resizeProxy))
			_resizeProxy = resizeProxy;
		if(Utils.isBoolean(moveOriginal))
			_moveOriginal = moveOriginal;
		_container = Utils.isNode(container) ? container : _el.parentNode;		
		_el.__drag = _this;
		DragUtil.addDrag(_el, _this);	
    }
    
    construct(el, controlEl, container, proxyClassName, moveOriginal, resizeProxy);
});

/* ==================================== WIDGETS ==================================== */

var WidgetFactory = {
    create: function(nodeName, innerHTML, className) {
        if(Utils.isString(nodeName))
            nodeName = document.createElement(nodeName);
        if(Utils.isNode(nodeName)) {
            if(innerHTML)
                nodeName.innerHTML = innerHTML;
            return new dp.SimpleWidget(nodeName, className);
        }                
        return null;
    },
    createInput: function(type, className) {
        var input = document.createElement('input');
        input.setAttribute('type', type);
        return WidgetFactory.create(input, null, className);
    },
    createById: function(id) {
        var node = document.getElementById(id);
        return Utils.isNode(node) ? (Utils.isWidget(node.__wrapper) ? node.__wrapper : new dp.SimpleWidget(node)) : null;
    }    
};
dp.Loader.addSingleton('WidgetFactory', WidgetFactory);

dp.Loader.addBaseClass('SimpleWidget', function (el, className) {
    var _this = this;
    this.__extends = dp.Indexed;
    this.__extends();
    
    var _element = null;
    var _parent = null;
    var _styles = new Array();
	var _attributes = null;

    this.setAttribute = function(name, value) {
		if(!_attributes)
			_attributes = new dp.HashMap();
        _attributes.put(name, value);
    }
    
    this.getAttribute = function(name) {
		if(!_attributes)
			_attributes = new dp.HashMap();
        return _attributes.get(name);
    }
    
    this.removeAttribute = function(name) {
		if(!_attributes)
			_attributes = new dp.HashMap();	
        return _attributes.remove(name);
    }

	this.getAttributeMap = function() {
		return _attributes;
	}
    
    this.isSimple = function() {
        return true;
    }
	
	this.props = {};	
    
    this.setElement = function(el) {
        if(!_element && Utils.isNode(el)) {
            _element = el;
			if(_element.__wrapper) {
				_styles = _element.__wrapper.getStyles();			
				_attributes = _element.__wrapper.getAttributeMap();
			}
            _element.__wrapper = _this;             
        }
    }
    
    this.getElement = function() {
        return _element;
    }
    
    this.setParent = function(p) {
        if(_parent) {
            _parent._a_removeChildElement(_element);            
            _this._a_onDetach();
        }   
        if(Utils.isString(p)) 
            p = document.getElementById(p);                    
        if(Utils.isNode(p)) {                                
            _parent = p.__wrapper ? p.__wrapper : new dp.SimpleWidget(p);
            _parent._a_appendChildElement(_element);
            _this._a_onAttach();
        }            
        else if(Utils.isWidget(p)) {
            _parent = p;
            _parent._a_appendChildElement(_element);
            this._a_onAttach();
        }
        else 
            _parent = null;
    }
    
    this.getParent = function() {
        return _parent;
    }
    
    this.setId = function(id) {
        _element.id = id;
    }
    
    this.getId = function() {
        return _element.id;
    }
    
    this.setName = function(name) {
        _element.name = name;
    }
    
    this.getName = function() {
        return _element.name;
    }
    
    this.setTitle = function(title) {
        _element.title = title;
    }
    
    this.getTitle = function() {
        return _element.title;
    }
    
    this.setStyle = function(className) {
        if(className) {
            if(_styles.length>0)
                _styles.splice(0, _styles.length);
            _styles.push(className);
            _element.className = className;
        }        
    }
    
    this.addStyle = function(className) {
        if(className && _styles.indexOf(className)==-1) {
            _styles.push(className);
            _element.className = _styles.length>1 ? _styles.join(' ') : _styles[0];
        }
    }
    
    this.removeStyle = function(className) {
        var idx = className ? _styles.indexOf(className) : -1;
        if(idx!=-1) {
            _styles.splice(idx, 1);
            _element.className = _styles.length>1 ? _styles.join(' ') : _styles[0];
            return true;
        }
        return false;
    }
    
    this.getStyle = function() {
        return _element.className;
    }
	
	this.getStyles = function() {
		return _styles;
	}
    
    this.setStyleProperty = function(name, value) {
        _element.style[name] = value;
    }
    
    this.getStyleProperty = function(name) {
        return _element.style[name];
    }
    
    this.setElementProperty = function(name, value) {
        _element[name] = value;
    }
    
    this.getElementProperty = function(name) {
        return _element[name];
    }
    
    this.isAttached = function() {
        return _parent!=null;
    }  
    
    this.addEventHandler = function(event, handler) {        
        Utils.addHandlerToEvent(handler, _element, event);
    }
    
    this._a_appendChildElement = function(childElement) {
        _element.appendChild(childElement);
    }
    
    this._a_removeChildElement = function(childElement) {
        _element.removeChild(childElement);
    }
    
    this.setVisible = function(visible) {
        _element.style.display = visible ? '' : 'none';
    }
    
    this.isVisible = function() {
        return _element.style.display!='none';
    }
	
	this.toggle = function() {
		_element.style.display = _element.style.display=='none' ? '' : 'none';
	}
    
    this._a_onAttach = function() {}
    this._a_onDetach = function() {}
    
    function construct(el, className) {
        if(Utils.isNode(el)) {
            _this.setElement(el);
            _this.setStyle(className);
        }
    }
    
    construct(el, className);    
});

dp.Loader.addBaseClass('AbstractWrapperWidget', function () {
    var _this = this;
    this.__extends = dp.SimpleWidget;
    this.__extends();
    
    var _child = null;
    
    this.setChild = function(child) {
        if(Utils.isWidget(child) && !_this.equals(child.getParent())) {
            _child = child;
            _child.setParent(_this);
        }
    }
    
    this.getChild = function() {
        return _child;
    }
    
    this.onlyChild = function() {
        return _child;
    }
    
    this.addChild = function(child) {
        _this.setChild(child);
    }
    
    function construct() {}    
    construct();       
});

dp.Loader.addBaseClass('AbstractContainerWidget', function () {
    var _this = this;
    this.__extends = dp.SimpleWidget;
    this.__extends(); 
    
    var _children = new dp.ArrayList();
    
    this.getChildren = function() {
        return _children;
    }
    
    this.size = function() {
        return _children.size();
    }
    
    this.getChild = function(index) {        
        return _children.get(index);     
    }
    
    this.addChild = function(child) {
        if(Utils.isWidget(child) && !_this.equals(child.getParent())) {           
            var index = _children.size();           
            child = _this._a_filterAddCandidate(child);
            if(child) {
                _children.add(child);
                child.setParent(_this);           
                _this._a_onChildAdded(child, index);
            }              
        }
    }
    
    this.removeChild = function(index) {
        var child = _children.remove(index);
        if(child) {           
            child.setParent(null);           
            _this._a_onChildRemoved(child, index);
        }               
        return child;
    }
	
	this.removeChildren = function(startIndex, endIndex) {
		var size = _children.size();
		var end = endIndex ? (endIndex>size ? size : endIndex) : size;
		var start = startIndex ? (startIndex<end ? startIndex : end) : 0;
		for(var i=0,len=end-start;i<len; i++)
			_this.removeChild(start);
	}
    
    this._a_filterAddCandidate = function(child) {
        return child;
    }
    
    this._a_onChildAdded = function(child, index) {}
    this._a_onChildRemoved = function(child, index) {}  
    
    function construct() {}    
    construct();    
});

dp.Loader.addBaseClass('CustomWidget', function () {
    var _this = this;
    this.__extends = dp.SimpleWidget;
    this.__extends();
    
    var _widget = null;
	
	this.isSimple = function() {
		return false;
	}
    
    this.setWidget = function(widget) {
        if(!_widget && Utils.isWidget(widget)) {
            _widget = widget;
            _this.setElement(_widget.getElement());
        }
    }
    
    this.getWidget = function() {
        return _widget;
    }
    
    this._a_onAttach = function() {
        _widget.setParent(_this.getParent());
    }
    
    function construct() {}    
    construct();	
});

dp.Loader.addBaseClass('SimplePanel', function (child, className) {
    var _this = this;
    this.__extends = dp.AbstractWrapperWidget;
    this.__extends();   
    
    function construct(child, className) {
        _this.setElement(document.createElement('div'));        
        _this.setStyle(className);
        _this.setChild(child);       
    }

    construct(child, className);
});

dp.Loader.addBaseClass('SimpleCellPanel', function (child) {
    var _this = this;
    this.__extends = dp.AbstractWrapperWidget;
    this.__extends();
    
    var _td = null;
    
    this._a_appendChildElement = function(childElement) {
        _td.appendChild(childElement);    
    }
    
    this._a_removeChildElement = function(childElement) {
        _td.removeChild(childElement);
    }
	
    this.setBorder = function(border) {
        _this.setElementProperty('border', border);
    }
    
    this.setSpacing = function(spacing) {
        _this.setElementProperty('cellSpacing', spacing);
    }
    
    this.setPadding = function(padding) {
        _this.setElementProperty('cellPadding', padding);
    }	
    
    function construct(child) {
        var table = document.createElement('table');
        var tbody = document.createElement('tbody');
        var tr = document.createElement('tr');
        _td = document.createElement('td');
        tr.appendChild(_td);
        tbody.appendChild(tr);
        table.appendChild(tbody);
        _this.setElement(table);
        _this.setElementProperty('cellPadding', 0);
        _this.setElementProperty('cellSpacing', 0);           
        _this.setChild(child);
    }
    
    construct(child);
});

dp.Loader.addBaseClass('CenterPanel', function (child, className) {
    var _this = this;
    this.__extends = dp.AbstractWrapperWidget;
    this.__extends();   
    
    function construct(child, className) {
        _this.setElement(document.createElement('center'));     
        _this.setStyle(className);
        _this.setChild(child);       
    }

    construct(child, className);
});

dp.Loader.addBaseClass('FlowPanel', function (className) {
    var _this = this;
    this.__extends = dp.AbstractContainerWidget;
    this.__extends();
    
    function construct(className) {
        _this.setElement(document.createElement('div'));
        _this.setStyle(className);
    }
    
    construct(className);
});

dp.Loader.addBaseClass('AbstractCellPanel', function () {
    var _this = this;
    this.__extends = dp.AbstractContainerWidget;
    this.__extends();   
    
    var _tbody = null;
    var _vAlign = {
        top: 'vertical-align: top;', 
        middle: 'vertical-align: middle;', 
        bottom: 'vertical-align: bottom;'
    };
    var _hAlign = {left: 'left', center: 'center', right: 'right'};
    
    this.getTBODY = function() {
        return _tbody;
    }
    
    this._a_filterAddCandidate = function(child) {
        var wrapper = new dp.SimpleWidget(document.createElement('td'));
        wrapper.setElementProperty('align', _hAlign['left']);
        Utils.applyStyle(wrapper.getElement(), _vAlign['top']);
        child.setParent(wrapper);
        return wrapper;
    }
    
    this._a_onChildAdded = function(child, index) {
        child.setIndex(index);
    }
    
    this.setVerticalAlign = function(child, align) {
        var al = _vAlign[align];
        if(al && Utils.isWidget(child)) {           
            var td = child.getParent();
            if(td && _this.equals(td.getParent())) 
                Utils.applyStyle(td.getElement(), al);                 
        }
    }
    
    this.setHorizontalAlign = function(child, align) {
        var al = _hAlign[align];
        if(al && Utils.isWidget(child)) {            
            var td = child.getParent();
            if(td && _this.equals(td.getParent()))
                td.setElementProperty('align', al);                
        }        
    }
    
    this.setCellWidth = function(child, width) {
        if(Utils.isWidget(child)) {
            var td = child.getParent();
            if(td && _this.equals(td.getParent()))
                td.setElementProperty('width', width);
        }        
    }
    
    this.setCellHeight = function(child, height) {
        if(Utils.isWidget(child)) {
            var td = child.getParent();
            if(td && _this.equals(td.getParent()))
                td.setElementProperty('height', height);
        }
    }
    
    this.setBorder = function(border) {
        _this.setElementProperty('border', border);
    }
    
    this.setSpacing = function(spacing) {
        _this.setElementProperty('cellSpacing', spacing);
    }
    
    this.setPadding = function(padding) {
        _this.setElementProperty('cellPadding', padding);
    }
    
    function construct() {               
        _tbody = document.createElement('tbody');
        var table = document.createElement('table'); 
        table.appendChild(_tbody);
        _this.setElement(table);       
    }
    
    construct();    
});

dp.Loader.addBaseClass('VerticalCellPanel', function () {
    var _this = this;
    this.__extends = dp.AbstractCellPanel;
    this.__extends();
    
    this._a_appendChildElement = function(childElement) {
        var tr = document.createElement('tr');
        tr.appendChild(childElement);
        _this.getTBODY().appendChild(tr);
    }
    
    this._a_removeChildElement = function(childElement) {
        var tr = childElement.parentNode;
        tr.removeChild(childElement);
        _this.getTBODY().removeChild(tr);           
    }    
    
    function construct() {
        _this.setElementProperty('cellSpacing', 0);
        _this.setElementProperty('cellPadding', 0);
    }
    
    construct();   
});

dp.Loader.addBaseClass('HorizontalCellPanel', function () {
    var _this = this;
    this.__extends = dp.AbstractCellPanel;
    this.__extends();
    
    var _tr = null;
    
    this.getTR = function() {
        return _tr;
    }
    
    this._a_appendChildElement = function(childElement) {
        _tr.appendChild(childElement);
    }
    
    this._a_removeChildElement = function(childElement) {
        _tr.removeChild(childElement);
    }
    
    function construct() {
        _this.setElementProperty('cellSpacing', 0);
        _this.setElementProperty('cellPadding', 0);
        _tr = document.createElement('tr');
        _this.getTBODY().appendChild(_tr);
    }
    
    construct();
});

dp.Loader.addBaseClass('DeckPanel', function () {
    var _this = this;
    this.__extends = dp.AbstractContainerWidget;
    this.__extends();      
    
    var _current = null;
    
    function styleChild(child, display, height) {
        child.setStyleProperty('display', display);       
        //child.setStyleProperty('height', height);        
    }
    
    this._a_onChildAdded = function(child, index) {
        styleChild(child, 'none', '100%');
    }
    
    this._a_onChildRemoved = function(child, index) {
        styleChild(child, '', '');
    }    
    
    this.show = function(index) {
		var child = _this.getChild(index);        
        if(child) {
			if(_current)
				_current.setStyleProperty('display', 'none'); 
            _current = child;
            _current.setStyleProperty('display', '');
        } 
		else if(_current) {
			_current.setStyleProperty('display', 'none'); 
			_current = null;
		}
    }
	
	this.getCurrentWidget = function() {
		return _current;
	}
     
    function construct() {       
        _this.setElement(document.createElement('div'));                          
    }
    
    construct();    
});

dp.Loader.addBaseClass('AbstractListPanel', function () {
    var _this = this;
    this.__extends = dp.AbstractContainerWidget;
    this.__extends();
    
    this._a_filterAddCandidate = function(child) {
        var li = new dp.SimpleWidget(document.createElement('li'));
        child.setParent(li);
        return li;
    }
    
    function construct() {
        _this.setElement(document.createElement('ul'));
        Utils.applyStyle(_this.getElement(), 'margin:0; padding:0; list-style:none;');
    }
    
    construct();
});

dp.Loader.addBaseClass('VerticalListPanel', function () {
    var _this = this;
    this.__extends = dp.AbstractListPanel;
    this.__extends();
    
    this._a_onChildAdded = function(child, index) {
        child.setIndex(index);
    } 

    function construct() {}    
    construct();
});

dp.Loader.addBaseClass('HorizontalListPanel', function () {
    var _this = this;
    this.__extends = dp.AbstractListPanel;
    this.__extends();    
    
    this._a_onChildAdded = function(child, index) {
        child.setIndex(index);
        child.setStyleProperty('display', 'inline');
        child.getElement().firstChild.style.display = 'inline';
    }
    
    this._a_onChildRemoved = function(child ,index) {
        child.setStyleProperty('display', '');
        child.getElement().firstChild.style.display = '';
    }    
    
    function construct() {}    
    construct();
});

/* ==================================== PRE-BUILT WIDGETS ==================================== */

dp.Loader.addBaseClass('PopupPanel', function (className) {
    var _this = this;
    this.__extends = dp.AbstractContainerWidget;
    this.__extends();
    
    this.center = function() {
        Utils.positionCenter(_this.getElement());        
    }
    
    this.setPosition = function(left, top) {
        var x = Utils.isNumber(left) ? [left,'px'].join('') : left;
        var y = Utils.isNumber(top) ? [top,'px'].join('') : top;
        _this.setStyleProperty('left', x);
        _this.setStyleProperty('top', y);      
    }
    
    this.show = function() {
        _this.setVisible(true);
    }
    
    this.isHidden = function() {
        return !_this.isVisible();
    }    
    
    this.hide = function() {
        _this.setVisible(false);
    }  
    
    function construct(className) {
        var div = document.createElement('div');        
        _this.setElement(div);
        _this.setVisible(false);
        _this.setStyle(className);
        _this.setStyleProperty('position', 'absolute');
    }
    
    construct(className);
});

dp.Loader.addBaseClass('SimpleTable', function (rows, cols, className) {
    var _this = this;
    this.__extends = dp.SimpleWidget;
    this.__extends();
    
    var _vAlign = {
        top: 'vertical-align: top;', 
        middle: 'vertical-align: middle;', 
        bottom: 'vertical-align: bottom;'
    };
    var _hAlign = {left: 'left', center: 'center', right: 'right'};    
    
    var _rows = null;
    var _cols = null;
    
    this.setWidget = function(row, col, widget) {
        if(_rows>row && _cols>col) {
            var cell = _this.getElement().rows[row].cells[col];
            Utils.refreshElement(cell);
            if(Utils.isWidget(widget))
				cell.appendChild(widget.getElement());         
        }        
    }
    
    this.getWidget = function(row, col) {
        if(_rows>row && _cols>col) {        
            var child = _this.getElement().rows[row].cells[col].firstChild;
            return child && Utils.isWidget(child.__wrapper) ? child.__wrapper : null;        
        }
        return null;
    }
    
    this.setRowStyle = function(row, style) {
        if(_rows>row)
            _this.getElement().rows[row].className = style;
    }
    
    this.setCellStyle = function(row, col, style) {
        if(_rows>row && _cols>col)
            _this.getElement().rows[row].cells[col].className = style;        
    }
    
    this.styleOddEvenRows = function(oddStyle, evenStyle, startIndex, endIndex) {        
		var end = endIndex ? (endIndex>_rows ? _rows : endIndex) : _rows;
		var start = startIndex ? (startIndex<end ? startIndex : end) : 0;		
        for(var i=0,len=end-start; i<len; i++, start++)
            _this.getElement().rows[start].className = start%2==0 ? evenStyle : oddStyle;          
    }
    
    this.addRow = function() {        
        var tr = _this.getElement().insertRow(_rows);
        for(var j=0; j<_cols; j++)
            tr.insertCell(j);        
        return _rows++;
    }
	
	this.insertRow = function(row) {
		if(row<_rows) {
			var tr = _this.getElement().insertRow(row);
			for(var j=0; j<_cols; j++)
				tr.insertCell(j);
			return _rows++;
		}
		return _rows;
	}
	
	this.removeRow = function(row) {
		if(row<_rows) {			
			_this.getElement().deleteRow(row);
			_rows--;
			return true;
		}
		return false;
	}
	
	this.removeRows = function(startIndex, endIndex) {
		var end = endIndex ? (endIndex>_rows ? _rows : endIndex) : _rows;
		var start = startIndex ? (startIndex<end ? startIndex : end) : 0;
		for(var i=0,len=end-start; i<len; i++, _rows--)
			_this.getElement().deleteRow(start);					
	}
    
    this.getTD = function(row, col) {
        return _rows>row && _cols>col ? _this.getElement().rows[row].cells[col] : null;
    }
	
	this.getTR = function(row) {
		return _rows>row ? _this.getElement().rows[row] : null;
	}
    
    this.getRowCount = function() {
        return _rows;
    }
    
    this.setBorder = function(border) {
        _this.setElementProperty('border', border);
    }
    
    this.setSpacing = function(spacing) {
        _this.setElementProperty('cellSpacing', spacing);
    }
    
    this.setPadding = function(padding) {
        _this.setElementProperty('cellPadding', padding);
    }
    
    this.setVerticalAlign = function(row, col, align) {
        var al = _vAlign[align];   
        var td = _this.getTD(row, col);
        if(al && td)
            Utils.applyStyle(td, al);
    }
    
    this.setHorizontalAlign = function(row, col, align) {
        var al = _hAlign[align];
        var td = _this.getTD(row, col);
        if(al && td)
            td['align'] = al;  
    }
    
    this.setCellWidth = function(row, col, width) {
        var td = _this.getTD(row, col);
        if(td)
            td['width'] = width;       
    }
    
    this.setCellHeight = function(row, col, height) {
        var td = _this.getTD(row, col);
        if(td)
            td['height'] = height;
    }     
    
    function construct(rows, cols, className) {
        var table = document.createElement('table');
        _rows = rows || 0;
        _cols = cols || 0;
        for(var i=0; i<_rows; i++) {
            var tr = table.insertRow(i);
            for(var j=0; j<cols; j++)
                tr.insertCell(j);
        }
        _this.setElement(table);
		_this.setStyle(className);
        _this.setElementProperty('cellSpacing', 0);
        _this.setElementProperty('cellPadding', 0);        
    }
    
    construct(rows, cols, className);
});

dp.Loader.addBaseClass('Hyperlink', function (display, token, className) {
    var _this = this;
    this.__extends = dp.SimpleWidget;
    this.__extends();
    
    function construct(display, token, className) {
        var a = document.createElement('a');
        if(Utils.isString(display))
            a.innerHTML = display;
        else if(Utils.isNode(display))
            a.appendChild(display);
        else if(Utils.isWidget(display))
            a.appendChild(display.getElement());
        a.href = ['#', token].join('');		
        _this.setElement(a);
        _this.setStyle(className);    
    }
    
    construct(display, token, className);
});

dp.Loader.addBaseClass('SelectBox', function (hasFirst) {
    var _this = this;
    this.__extends = dp.SimpleWidget;
    this.__extends();
	
	var _value = '';
	var _handlers = new Array();	
	
	var super_setElementProperty = this.setElementProperty;
	this.setElementProperty = function(name, value) {
		if(name!='value') {
			super_setElementProperty(name, value);
			return;
		}
		_this.getElement().selectedIndex = 0;
	}
	
	var super_getElementProperty = this.getElementProperty;
	this.getElementProperty = function(name) {
		return name!='value' ? super_getElementProperty(name) : _this.getValue();
	}
	
	var super_addEventHandler = this.addEventHandler;
	this.addEventHandler = function(event, handler) {
		if(event!='onchange') {
			super_addEventHandler(event, handler);
			return;
		}
		_handlers.push(handler);
	}
	
	this.getValue = function() {
		if(_this.size()>0) {
			var sel = _this.getElement();
			_value = sel.options[sel.selectedIndex].value		
		}
		else
			_value = '';
		return _value;
	}	
	
	this.size = function() {
		return _this.getElement().options.length;
	}	
	
	this.addEntry = function(value, display) {
		var option = document.createElement('option');
		option.value = value;
		option.innerHTML = display;
		_this.getElement().appendChild(option);
	}
	
	this.removeEntry = function(idx) {
		if(idx<_this.getElement().options.length) {		
			_this.getElement().remove(idx);			
			return true;
		}
		return false;
	}
	
	this.removeEntries = function(startIndex, endIndex) {
		var size = _this.size();
		var end = endIndex ? (endIndex>_rows ? size : endIndex) : size;
		var start = startIndex ? (startIndex<end ? startIndex : end) : 0;
		for(var i=0,len=end-start; i<len; i++)
			_this.getElement().remove(start);
	}
	
	function onChange(e) {
		var sel = _this.getElement();
		_value = sel.options[sel.selectedIndex].value;
		for(var i in _handlers)
			_handlers[i](e, _value);	
	}
    
    function construct(hasFirst) {
		var select = document.createElement('select');
		Utils.addHandlerToEvent(onChange, select, 'onchange');		
		_this.setElement(select);		
		if(hasFirst)		
			_this.addEntry('', '');		
    }
    
    construct(hasFirst);
});

/* ==================================== CUSTOM ==================================== */

dp.Loader.addBaseClass('AbstractPane', function () {
    var _this = this;
    this.__extends = dp.FlowPanel;
    this.__extends();    
    
    var _name = null;    
    var _parent = null;
    var _token = null;
	var _delim = '/';
	
	this.linkHidden = false;
    
    this.getPanel = function() {
        return _this;
    }
    
    this.setName = function(name) {
        _name = name;
    }
    
    this.getName = function() {
        return _name;
    }
	
	this.setDelim = function(delim) {
		_delim = delim;
	}
	
	this.getDelim = function() {
		return _delim;
	}
    
    this.setParentPane = function(parent) {
        if(parent && parent.getCompositeToken) {
            _parent = parent;
            _this._a_onParentPaneSet();
        }
    }
    
    this.getParentPane = function() {
        return _parent;
    }
	
	this.isRootPane = function() {
		return _parent==null;
	}
    
    this._a_onParentPaneSet = function(){}
    
    this.getDepth = function() {
        return _parent ? _parent.getDepth() + 1 : 0;
    }
    
    this.setToken = function(token) {
        _token = token;
    }
    
    this.getToken = function() {
        return _token;
    }
    
    this.getCompositeToken = function(childToken) {
        if(childToken)
            return _parent ? [_parent.getCompositeToken(_token), _delim, childToken].join('') : [_token, childToken].join(_delim);
        return _parent ? _parent.getCompositeToken(_token) : _token; 
    }
    
    this.activate = function(tokens) {}
    
    this.passivate = function(tokens) {}
    
    this.init = function() {}
	
	this.reset = function() {}
    
    function construct() {}    
    construct();
});

dp.Loader.addBaseClass('AbstractTreePane', function () {
    var _this = this;
    this.__extends = dp.AbstractPane;
    this.__extends();
	
	var _defaultLinkStyle = null;
	var _paneMap = new dp.IndexedMap();
	var _expanded = true;
	var _indented = true;
	
	this.setIndented = function(indented) {
		_indented = indented;
	}

	this.isIndented = function() {
		return _indented;
	}
	
	this.isExpanded = function() {
		return _expanded;
	}	
	
	function expandOrCollapse(panes, expand) {
		for(var i=0; i<panes.length; i++) {
			var p = panes[i];
			p.setVisible(expand);
			if(p.props.panes)
				expandOrCollapse(p.props.panes, expand);			
		}	
	}
	
	this.collapsePanes = function() {
		_expanded = false;
		expandOrCollapse(_this.props.panes, _expanded);
	}
	
	this.expandPanes = function() {
		_expanded = true;
		expandOrCollapse(_this.props.panes, _expanded);
	}
	
	this.toggleTree = function() {
		_expanded = !_expanded;
		expandOrCollapse(_this.props.panes, _expanded);
	}
	
	this.setDefaultLinkStyle = function(className) {
		_defaultLinkStyle = className;
	}
	
	this.getDefaultLinkStyle = function() {
		if(!_defaultLinkStyle) {
			var parent = _this.getParentPane();
			_defaultLinkStyle = parent ? parent.getDefaultLinkStyle() : null;
		}			
		return _defaultLinkStyle;
	}
	
	this.newLink = function(title, className) {
		return WidgetFactory.create('a', title, className);
	}
	
	this.newPane = function(className) {
		return new dp.FlowPanel(className);
	}
	
	this.newEntryPane = function(title, p, styles) {				
		return styles ? _this.addEntry(WidgetFactory.create('a', title, styles.k), new dp.FlowPanel(styles.v), p) : 
			_this.addEntry(title, new dp.FlowPanel(), p);
	}
	
	this.addTreePane = function(pane) {
		this.addEntry(pane.getName(), pane);
	}

	this.addEntry = function(titleOrLink, pane, p) {		
		var link = Utils.isString(titleOrLink) ? _this.newLink(titleOrLink) : titleOrLink;
		var parent = Utils.isWidget(p) ? (p.props.tree==_this ? p : _this) : _this;
		if(_indented)
			pane.setStyleProperty('margin', '2px 0 2px 10px');
		//pane.setStyleProperty('position', 'relative');
		//pane.setStyleProperty('left', '10px');
		//pane.setStyleProperty('top', '2px');
		link.addEventHandler('onclick', function(e){
			pane.toggle();
		});
		parent.addChild(new dp.SimplePanel(link));
		parent.addChild(pane);
		if(pane.getDepth) {			
			pane.setParentPane(parent);
			if(_this.equals(parent))
				_paneMap.put(pane.getToken(), pane);
		}
		link.addStyle(_this.getDefaultLinkStyle());
		pane.props.tree = _this;
		if(!parent.props.panes)
			parent.props.panes = new Array();
		parent.props.panes.push(pane);		
		return pane;
	}
	
	this.init = function() {
		if(_this.isRootPane())
			_this.collapsePanes(_this.props.panes);		
		var map = _paneMap.__wrappedGet();
		for(var i in map)
			map[i].init();		
	}
	
	this.activate = function(tokens) {
		_this.setVisible(true);
		var sub = _this.getDepth() + 1;
		if(sub==tokens.length)
			return;
		var pane = _paneMap.get(tokens[sub]);
		if(pane)
			pane.activate(tokens);	
	}
	
	this.passivate = function(tokens) {
		/*_this.setVisible(false);
		var sub = _this.getDepth() + 1;
		if(sub==tokens.length)
			return;
		var pane = _paneMap.get(tokens[sub]);
		if(pane)
			pane.passivate(tokens);
		*/
	}
	
	function construct() {
		_this.props.panes = new Array();
	}
	construct();
});

/* ============================================================================================================== */

for(var i in dp)
	window[i] = dp[i];
window.dp = dp;

}).call(function(){});