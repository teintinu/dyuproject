Utils.addOnLoad(function(){
    new Module().start('content')}
);

function Services() {
    var _this = this;
    
    var _request = new RequestPool(4);
    
    this.getRequest = function() {
        return _request;
    }
    
    function fill(buffer, param, value) {
        buffer.push([param,value].join('='));
    }    
    
    var UsersService = {
        get: function(handler, id) {
            _request.doGet(id ? ['users/', id, '.json'].join('') : 'users.json', null, handler);
        },
        create: function(handler, user) {
            var buffer = new Array();
            for(var i in user)
                fill(buffer, i, user[i]);
            _request.doPost('users.json', buffer.join('&'), handler);
        },
        update: function(handler) {
        
        },
        del: function(handler) {
        
        }   
    };

    var TodosService = {
        get: function(handler, id) {
            _request.doGet(id ? ['todos/', id, '.json'].join('') : 'todos.json', null, handler);
        },
        create: function(handler) {
        
        },
        update: function(handler) {
        
        },
        del: function(handler) {
        
        }   
    };
    
    this.getUsersService = function() {
        return UsersService;
    }
    
    this.getTodosService = function() {
        return TodosService;
    }
}

function Todos(module) {
    var _this = this;
    this.__extends = AbstractPane;
    this.__extends();
    
    var _module = module;
    var _services = _module.getServices();
    var _top = new SimplePanel(WidgetFactory.create('span', 'Todos', 'big_label'));
    var _content = new FlowPanel();
    var _last = 0;
    
    this.activate = function(tokens, params) {
        if(params && params.userId) {
            _content.removeChildren();
            _services.getRequest().doGet('users/' + params.userId + '/todos.json', null, onGet);
            _last = 0;
            return true;
        }       
        switch(tokens.length) {
            case 1:
                if(_last==1)
                    break;
                _content.removeChildren();
                _services.getTodosService().get(onGet);
                _last = 1;
                break;
            case 2:
                _content.removeChildren();
                _services.getTodosService().get(fill, tokens[1]);
                _last = 2;
                break;
            default:
                return false;
        }
        return true;
    }
    
    this.passivate = function(tokens) {
        
    }
    
    function fill(todo) {
        var div = new FlowPanel();
        Utils.applyStyle(div.getElement(), 'padding:5px 0');
        var title = WidgetFactory.create('div', 'Title: ' + todo.title);
        var content = WidgetFactory.create('div', 'Content: ' + todo.content);
        var link = 'users/' + todo.user.id;
        var assignedTo = WidgetFactory.create('div', 'Assigned to: ' + '<a href="#' + link + '">' + todo.user.firstName + ' ' + todo.user.lastName + '</a>');
        div.addChild(title);
        div.addChild(content);
        div.addChild(assignedTo);
        if(todo.completed) {
            div.addChild(WidgetFactory.create('div', 'completed'));
        }
        else{
            var btn = WidgetFactory.create('button', 'complete');
            btn.addEventHandler('onclick', function(e) {
                Utils.stopEvent(e);
                _services.getRequest().doGet('todos/complete.json?id=' + todo.id, null, function(t) {
                    if(t) {                    
                        div.removeChild(div.size()-1);
                        div.addChild(WidgetFactory.create('div', 'completed'));                    
                    }

                });
            });
            div.addChild(btn);
        }
        _content.addChild(div);     
    }
    
    function onGet(todos) {
        for(var i=0,len=todos.length; i<len; i++)
            fill(todos[i]);
    }
    
    function onComplete(todo) {
        
    }
    
    function onCreate(todo) {
    
    }
    
    function onUpdate(todo) {
    
    }
    
    function onDelete(todo) {
    
    }
    
    function construct() {
        _this.setName('todos');
        _this.setToken('todos');
        _this.addChild(_top);       
        _this.addChild(_content);
        _content.addStyle('box');        
    }

    construct();
}

function Users(module) {
    var _this = this;
    this.__extends = AbstractPane;
    this.__extends();
    
    var _module = module;
    var _services = _module.getServices();
    var _top = new SimplePanel(WidgetFactory.create('span', 'Users', 'big_label'));
    var _content = new FlowPanel();
    var last = 0;
    
    this.activate = function(tokens) {      
        switch(tokens.length) {         
            case 1:
                // cached
                if(last==1)
                    break;
                _content.removeChildren();
                _services.getUsersService().get(onGet);
                last = 1;
                break
            case 2:
                _content.removeChildren();
                _services.getUsersService().get(fill, tokens[1]);
                last = 2;
                break;
            case 3:
                _module.activate(tokens[2], {userId: tokens[1]});                       
            default:
                return false;
        }
        return true;
    }
    
    this.passivate = function(tokens) {
        
    }
    
    function fill(user) {
        var div = new FlowPanel();
        Utils.applyStyle(div.getElement(), 'padding:5px 0');
        var firstName = WidgetFactory.create('div', 'First Name: ' + user.firstName);
        var lastName = WidgetFactory.create('div', 'Last Name: ' + user.lastName);
        var email = WidgetFactory.create('div', 'Email: ' + user.email);
        var todos = WidgetFactory.create('div', '<a href="#users/' + user.id + '/todos">todos</a>');
        div.addChild(firstName);
        div.addChild(lastName);
        div.addChild(email);
        div.addChild(todos); 
        _content.addChild(div); 
    }
    
    function onGet(users) {     
        for(var i=0,len=users.length; i<len; i++)
            fill(users[i]);     
    }
    
    function onCreate(user) {
    
    }
    
    function onUpdate(user) {
    
    }
    
    function onDelete(user) {
    
    }
    
    function createWidget() {
        var status = WidgetFactory.create('div', null, 'padded');        
        var firstName = WidgetFactory.createInput('text');
        var lastName = WidgetFactory.createInput('text');
        var email = WidgetFactory.createInput('text');        
        var username = WidgetFactory.createInput('text');
        var password = WidgetFactory.createInput('password');
        var confirm = WidgetFactory.create('password');
        var create = WidgetFactory.create('button', 'Create');        
        create.addEventHandler('onclick', function(e) {
            var fn = Utils.trim(firstName.getElementProperty('value'));
            var ln = Utils.trim(lastName.getElementProperty('value'));
            var e = Utils.trim(email.getElementProperty('value'));
            var un = Utils.trim(username.getElementProperty('value'));                
            var pw = Utils.trim(password.getElementProperty('value'));
            var c = Utils.trim(confirm.getElementProperty('value'));            
            if(un && pw && c && fn && ln && e) {
                if(pw!=c) {
                    status.getElement().innerHTML = 'Password did not match.';
                    return;
                }
                _content.removeChildren();
                _services.getUsersService().create(fill, {
                    firstName: un,
                    lastName: fn,
                    email: e,
                    username: u,
                    password: pw,
                    confirmPassword: c
                });
            
            }
            else
                status.getElement().innerHTML = 'Required Parameters: First Name, Last Name, Email, Username, Password, Confirm Password.';            
        });
        var table = new SimpleTable(7, 2);
        table.setWidget(0, 0, WidgetFactory.create('span', 'First Name'));
        table.setWidget(0, 1, firstName);
        table.setWidget(1, 0, WidgetFactory.create('span', 'Last Name'));
        table.setWidget(0, 1, lastName);
        table.setWidget(2, 0, WidgetFactory.create('span', 'Email'));
        table.setWidget(0, 1, email);
        table.setWidget(3, 0, WidgetFactory.create('span', 'Username'));
        table.setWidget(0, 1, username);
        table.setWidget(4, 0, WidgetFactory.create('span', 'Password'));
        table.setWidget(0, 1, password);
        table.setWidget(5, 0, WidgetFactory.create('span', 'Confirm Password'));
        table.setWidget(5, 1, confirm);
        table.setWidget(6, 1, create);
        
        var panel = new FlowPanel('padded');
        panel.addChild(status);
        panel.addChild(table);
        return panel;
    }    
    
    function construct() {
        _this.setName('users');
        _this.setToken('users');
        _this.addChild(_top);
        _module.getPopup().addEntry('users/create', createWidget());
        var create = WidgetFactory.create('button', 'Create', 'active-label');
        create.addEventHandler('onclick', function(e) {
            _module.getPopup().show('users/create', 'Create User');
        });
        _this.addChild(create);
        _this.addChild(_content);
        _content.addStyle('box');
    }

    construct();
}

function Home(module) {
    var _this = this;
    this.__extends = AbstractPane;
    this.__extends();
    
    var _module = module;
    var _top = new SimplePanel(WidgetFactory.create('span', 'Welcome', 'big_label'));
    
    this.activate = function() {
        return true;
    }
    
    function init() {
        _this.addChild(WidgetFactory.create('div', [
            '<p> Right-click and view source.  The div#content is dynamically filled.</p>',
            '<p>Here\'s the <a href="js/todo-list.js" target="_blank">javascript source</a> that generates this page.</p>',
            //'<p><a href="#users">users</a> and <a href="#todos">todos</a> do not require login.</p>',
            '<p>Data is fetched through the REST webservice(<a href="users.json">users.json</a> and <a href="todos.json">todos.json</a>) via XHR(ajax).</p>',
            'These widgets support the <span style="font-weight:bold;color:green">Back</span> button.'
            
            
        ].join('')));
    }

    function construct() {
        _this.setName('home');
        _this.setToken('home');
        _this.addChild(_top);       
        init();
    }

    construct();
}

function Module() {
    var _this = this;
    
    var _services = new Services();
    var _popup = new Popup();
    var _mainDiv = new FlowPanel();
    var _left = new FlowPanel();
    var _right = new FlowPanel();
    var _mid = new FlowPanel();    
    
    var _mainDeck = new DeckPanel();    
    var _leftNav = new VerticalListPanel();
    var _token = null;
    var _paneMap = new IndexedMap();
    var _delim = '/';
    
    this.getServices = function() {
        return _services;
    }
    
    this.getPopup = function() {
        return _popup;
    }
    
    this.start = function(parent) {     
        _mainDiv.setParent(parent);
        History.start();        
        History.addHandler(onHistoryChanged);       
        for(var i=0,size=_mainDeck.size(); i<size; i++) {
            var pane = _mainDeck.getChild(i);
            pane.init();
            _paneMap.put(pane.getToken(), pane);
        }
        History.newItem('home');
    }
    
    function onHistoryChanged(token) {
        if(_token!=token) {
            var oldToken = token;
            var tokens = token.split(_delim);          
            if(tokens.length>1)
                token = tokens[0];              
            var pane = _paneMap.get(token);
            if(pane) {              
                if(pane.activate(tokens)) {
                    _popup.hide();
                    _mainDeck.show(pane.getIndex());
                    tokens = _token ? _token.split(_delim) : [];
                    token = tokens.length>1 ? tokens[0] : _token;
                    var old = _paneMap.get(token);      
                    if(old)
                        old.passivate(tokens);
                }
                _token = oldToken;              
            }       
        }
    }
    
    this.activate = function(token, params) {
        var pane = _paneMap.get(token);
        if(pane) {
            pane.activate(token, params);
            _mainDeck.show(pane.getIndex());
        }
    }
    
    function initLeft() {
        _left.setId('left');
        _left.addChild(_leftNav);
        _leftNav.addStyle('simple');
        for(var i=0,size=_mainDeck.size(); i<size; i++) {
            var pane = _mainDeck.getChild(i);
            _leftNav.addChild(new Hyperlink(pane.getName(), pane.getToken()));      
        }
    }
    
    function initRight() {
        _right.setId('right');
    }
    
    function initMid() {
        _mid.setId('mid');
        _mid.addChild(_mainDeck);
        Utils.applyStyle(_mainDeck.getElement(), 'margin:0 5px;padding:0 5px;padding-bottom:70px;background-color:#fff');
        _mainDeck.addChild(new Home(_this));
        _mainDeck.addChild(new Users(_this));
        _mainDeck.addChild(new Todos(_this));       
    }
    
    function construct() {
        _mainDiv.getElement().setAttribute('align', 'justify');
        _mainDiv.addChild(_left);
        _mainDiv.addChild(_mid);
        _mainDiv.addChild(_right);
        var clear = WidgetFactory.create('div');
        Utils.applyStyle(clear.getElement(), 'clear:both');
        _mainDiv.addChild(clear);       
        initMid();
        initLeft();
        initRight();
    }

    construct();
}

function Popup(token) {
    var _this = this;
    this.__extends = CustomWidget;
    this.__extends();
    
    var _shown = false;
    var _popup = new PopupPanel();
    var _deck = new DeckPanel();
    var _entryMap = new IndexedMap(false);
    var _width = 400;
    var _titleBar = null;
    var _close = null;
    
    this.show = function(token, title, coords) {        
        var widget = _entryMap.get(token);      
        var idx = Utils.isWidget(widget) ? widget.getIndex() : -1;      
        if(idx!=-1) {
            _titleBar.getElement().innerHTML = title;
            _deck.show(idx);            
            if(coords) {
                _popup.setPosition(coords.x, coords.y);         
                _popup.show();              
            }
            else {
                var pos = Utils.getCenterCoords();
                pos.x -= (_width/2);
                _popup.setPosition(pos.x, pos.y);
                _popup.show();
                _popup.setPosition(pos.x, pos.y-(_this.getElement().offsetHeight/2));
            }
            _close.getElement().focus();
            _shown = true;          
        }
        else
            alert('what?????');
    }
    
    this.hide = function() {
        if(_shown) {
            _popup.hide();
            _shown = false;
        }
    }
    
    this.addEntry = function(token, widget) {
        if(Utils.isWidget(widget)) {                
            _entryMap.put(token, widget);
            _deck.addChild(widget);
        }
    }
    
    this.removeEntries = function() {
        _entryMap.clear();
        _deck.removeChildren();     
    }
    
    this.setWidth = function(width) {
        _width = Utils.isNumber(width) ? width : parseInt(width);
        _popup.setStyleProperty('width', [_width, 'px'].join(''));
    }
    
    this.getWidth = function() {
        return _width;
    }
    
    function setupTopBar(popup) {
        _popup.addEventHandler('onkeypress', function(e) {
            if(!e) e = window.event;            
            switch(e.keyCode) {
                case 27: {
                    _popup.hide();
                    break;
                }       
            }           
        });     
        _close = WidgetFactory.create('button', 'X', 'popup-bar-btn');
        _close.addEventHandler('onclick', function() {
            _popup.hide();
        });
        
        var bar = new SimplePanel(null, 'popup-bar');
        var title = WidgetFactory.create('div', '');
        var hp = new HorizontalCellPanel();
        hp.setElementProperty('width', '100%');
        hp.addChild(title);
        var buttonPanel = new HorizontalCellPanel();        
        //buttonPanel.addChild(WidgetFactory.create('button', '_', 'popup-bar-btn'));
        buttonPanel.addChild(_close);       
        hp.addChild(buttonPanel);
        hp.setHorizontalAlign(buttonPanel, 'right');
        hp.setCellWidth(buttonPanel, '5%');
        bar.addChild(hp);
        popup.addChild(bar);
        popup.setStyle('popup');
        return title;
    }
    
    function construct() {
        _popup.setStyleProperty('width', [_width, 'px'].join(''));
        _titleBar = setupTopBar(_popup);
        //var wrapper = new SimpleCellPanel(_deck);
        //wrapper.setSpacing(5);
        //_popup.addChild(wrapper);
        _popup.addChild(new SimplePanel(_deck, 'container'));
        _this.setWidget(_popup);
        _this.setParent(document.body);
        new Draggable(_this.getElement(), _titleBar.getElement());      
    }   
    construct();
}