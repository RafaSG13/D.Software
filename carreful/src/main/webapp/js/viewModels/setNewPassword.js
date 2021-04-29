/**
 * @license
 * Copyright (c) 2014, 2020, Oracle and/or its affiliates.
 * Licensed under The Universal Permissive License (UPL), Version 1.0
 * as shown at https://oss.oracle.com/licenses/upl/
 * @ignore
 */
/*
 * Your dashboard ViewModel code goes here
 */
define([ 'knockout', 'appController', 'ojs/ojmodule-element-utils', 'accUtils',
		'jquery' ], function(ko, app, moduleUtils, accUtils, $) {

	function SetNewPwdViewModel() {
		var self = this;
		
		/*var searchParams= new URLSearchParams;
		var email_url= searchParams.get();*/
		self.email = ko.observable("");
		self.pwd1 = ko.observable("");
		self.pwd2 = ko.observable("");
		
		self.message = ko.observable();
		self.error = ko.observable();
				
		self.setNewPwd = function() {
			var info = {
				email : self.email(),
				pwd1 : self.pwd1(),
				pwd2 : self.pwd2(),
			};
			var data = {
					data : JSON.stringify(info),
					url : "user/setNewPwd",
					type : "put",
					contentType : 'application/json',
					success : function(response) {
						self.error("");
						self.message("Contraseña cambiada correctamente, por favor, compruebe su correo electronico");
					},
					error : function(response) {
						self.message("Error al cambiar la contraseña");
						self.error(response.responseJSON.errorMessage);
					}
			};
			$.ajax(data);    	  
		}

		// Header Config
		self.headerConfig = ko.observable({
			'view' : [],
			'viewModel' : null
		});
		moduleUtils.createView({
			'viewPath' : 'views/header.html'
		}).then(function(view) {
			self.headerConfig({
				'view' : view,
				'viewModel' : app.getHeaderModel()
			})
		})

		self.connected = function() {
			accUtils.announce('Register page loaded.');
			document.title = "Registro";
			// Implement further logic if needed
		};

		self.disconnected = function() {
			// Implement if needed
		};

		self.transitionCompleted = function() {
			// Implement if needed
		};
	}

	return SetNewPwdViewModel;
});
