var M = {
	plugin : function(pluginname) {
		if (pluginname == 'location') {
			return M.plugin_location;
		}
	},
	sys : {
		exit : function() {
			M.page.html("login.jsp");
		},
		call : function(phoneno) {

		},
		sms : function(obj) {
			alert(obj.content);
		}
	},
	apps : {
		open : function(url, param) {
			window.open(url);
		},
		browser : function(url, param) {
			window.open(url);
		},
	},
	net : {
		http : {
			send : function(jvar) {
				$.ajax({
					url : 'ajax.do',
					type : jvar.method,
					data : JSON.stringify(jvar.data),
					processData : false,
					contentType : "application/json; charset=utf-8",
					success : function(data) {
						jvar.onSuccess(data, {});
					},
					error : function(httpReq, status, exception) {
						jvar.onError({}, {});
					}
				});

				// alert( JSON.stringify(jvar.data) );
				// jvar.onSuccess({result:'FAIL', resultMessage:'none'},{});
			},
			upload : function(url, obj) {
				obj.finish(0, {}, {}, {});
			}
		}
	},

	page : {
		back : function() {
			history.back();
		},
		html : function(url) {
			window.location.href = url;
		},
		move : function(url) {
			window.location.replace(url);
		}
	},
	tool : {
		log : function() {

		}
	},
	onReady : function(afunc) {
		afunc();
	},
	onBack : function(afunc) {
		afunc();
	},
	onRestore : function(afunc) {
		afunc();
	},
	onKey : function(afunc) {
		afunc();
	},

};