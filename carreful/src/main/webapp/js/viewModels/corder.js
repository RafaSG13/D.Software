define([ 'knockout', 'appController', 'ojs/ojmodule-element-utils', 'accUtils',
		'jquery' ], function(ko, app, moduleUtils, accUtils, $) {

	class CorderViewModel {
		constructor() {
			var self = this;
			self.pedido_productos = ko.observableArray([]);
			self.id = ko.observable("");
			self.pedido = ko.observable("");
			self.state = ko.observable("");
			self.precio_total = ko.observable();
			self.message = ko.observable(null);
			self.error = ko.observable(null);
			
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
		}
		eliminarPedido(id) {
			let self = this;
			let data = {
				url : "corder/borrarPedido/" + id,
				type : "delete",
				contentType : 'application/json',
				success : function(response) {
					self.message("Pedido eliminado");
			
				},
				error : function(response) {
					self.error(response.responseJSON.errorMessage);
				}
			};
		
			data.success;
			$.ajax(data);
		}
		
	getPedido(){
			let self = this;
			let data = {
				url : "corder/getPedido/" + self.id(),
				type : "get",
				contentType : 'application/json',
				success : function(response) {
					self.pedido_productos(response);
				},
				error : function(response) {
					self.error(response.responseJSON.errorMessage);
				}
			};
			$.ajax(data);
		}
	
		
		connected() {
			accUtils.announce('Login page loaded.');
			document.title = "Login";

		};

		disconnected() {
			// Implement if needed
		};

		transitionCompleted() {
			// Implement if needed
		};
	}

	return CorderViewModel;
});
