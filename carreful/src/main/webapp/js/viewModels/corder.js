define([ 'knockout', 'appController', 'ojs/ojmodule-element-utils', 'accUtils',
		'jquery' ], function(ko, app, moduleUtils, accUtils, $) {

	class CorderViewModel {
		constructor() {
			var self = this;
			
			self.nombre = ko.observable("");
			self.precio = ko.observable("");
			self.cantidad = ko.observable("");

	
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

		add() {
			var self = this;
			let info = {
				nombre : this.nombre(),
				precio : this.precio(),
				cantidad: this.cantidad(),

			};
			let data = {
				data : JSON.stringify(info),
				url : "corder/add",
				type : "post",
				contentType : 'application/json',
				success : function(response) {
					self.message("Pedido guardado");
					
				},
				error : function(response) {
					/*self.error(response.responseJSON.errorMessage);*/
					self.error(response.responseJSON.errorMessage);
				}
			};
			$.ajax(data);
		}
		
		/*getProductos() {
			let self = this;
			let data = {
				url : "product/getTodos",
				type : "get",
				contentType : 'application/json',
				success : function(response) {
					self.productos(response);
				},
				error : function(response) {
					self.error(response.responseJSON.errorMessage);
				}
			};
			$.ajax(data);
		}*/
		
		/*getUnProducto(id) {
			let self = this;
			let data = {
				url : "product/getUnProducto/"+ id,
				type : "get",
				contentType : 'application/json',
				success : function(response) {
					 response.categoria.id;
				},
				error : function(response) {
					self.error(response.responseJSON.errorMessage);
				}
			};
			$.ajax(data);
			
		}*/
		update() {
			var self = this;
			let info = {
				nombre : this.nombre(),
				precio : this.precio(),
				cantidad: this.cantidad(),
			};
			let data = {
				data : JSON.stringify(info),
				url : "corder/update",
				type : "post",
				contentType : 'application/json',
				success : function(response) {
					self.message("Producto guardado");
				},
				error : function(response) {
					self.error(response.responseJSON.errorMessage);
				}
			};
			$.ajax(data);
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
		
		
		register() {
			app.router.go( { path : "register" } );
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
