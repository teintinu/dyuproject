Utils.addOnLoad(function(){
	new Module().start('content')}
);

function Services() {
	var _this = this;
	
	var _request = new RequestPool(4);
	
	this.getRequest = function() {
		return _request;
	}
	
	var UsersService = {
		get: function(handler, id) {
			_request.doGet(id ? ['users/', id, '.json'].join('') : 'users.json', null, handler);
		},
		create: function(handler) {
		
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
		var assignedTo = WidgetFactory.create('div', 'Assigned To: ' + '<a href="#' + link + '">' + todo.user.firstName + ' ' + todo.user.lastName + '</a>');
		div.addChild(title);
		div.addChild(content);
		div.addChild(assignedTo);
		_content.addChild(div);		
	}
	
	function onGet(todos) {
		for(var i=0,len=todos.length; i<len; i++)
			fill(todos[i]);
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
		var todos = WidgetFactory.create('div', '<a href="#users/' + user.id + '/todos>todos</a>');
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
	
	function construct() {
		_this.setName('users');
		_this.setToken('users');
		_this.addChild(_top);		
		_this.addChild(_content);		
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
	var _mainDiv = new FlowPanel();
	var _left = new FlowPanel();
	var _right = new FlowPanel();
	var _mid = new FlowPanel();
	
	var _mainDeck = new DeckPanel();	
	var _leftNav = new VerticalListPanel();
	var _token = null;
	var _paneMap = new IndexedMap();
	
	this.getServices = function() {
		return _services;
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
			var tokens = token.split('/');			
			if(tokens.length>1)
				token = tokens[0];				
			var pane = _paneMap.get(token);
			if(pane) {				
				if(pane.activate(tokens)) {
					_mainDeck.show(pane.getIndex());
					tokens = _token ? _token.split('/') : [];
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