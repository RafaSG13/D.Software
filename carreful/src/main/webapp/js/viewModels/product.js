define([ 'knockout', 'appController', 'ojs/ojmodule-element-utils', 'accUtils',
		'jquery' ], function(ko, app, moduleUtils, accUtils, $) {

	class ProductViewModel {
		constructor() {
			var self = this;
			
			self.nombre = ko.observable("");
			self.precio = ko.observable("");
			self.cantidad = ko.observable("");
			self.categoria = ko.observable();
			

			
			self.carrito = ko.observableArray([]);
			self.categorias = ko.observableArray([]);
			self.carrito = ko.observableArray([]);
			self.producto_categoria = ko.observableArray([]);
		
			
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
				categoria : this.categoria(),
				congelado : this.congelado()
			};
			let data = {
				data : JSON.stringify(info),
				url : "product/add",
				type : "post",
				contentType : 'application/json',
				success : function(response) {
					self.message("Producto guardado");
					self.getProductos_Categoria(info.categoria.id);
				},
				error : function(response) {
					/*self.error(response.responseJSON.errorMessage);*/
					self.error(response.responseJSON.errorMessage);
				}
			};
			$.ajax(data);
		}
		
		update() {
			var self = this;
			let info = {
				nombre : this.nombre(),
				precio : this.precio(),
				cantidad: this.cantidad(),
				categoria : this.categoria(),
			};
			let data = {
				data : JSON.stringify(info),
				url : "product/update",
				type : "post",
				contentType : 'application/json',
				success : function(response) {
					self.message("Producto guardado");
					self.getProductos_Categoria(info.categoria.id);
				},
				error : function(response) {
					/*self.error(response.responseJSON.errorMessage);*/
					self.error(response.responseJSON.errorMessage);
				}
			};
			$.ajax(data);
		}
		
		getCategorias() {
			let self = this;
			let data = {
				url : "product/getCategorias",
				type : "get",
				contentType : 'application/json',
				success : function(response) {
					self.categorias(response);
				},
				error : function(response) {
					self.error(response.responseJSON.errorMessage);
				}
			};
			$.ajax(data);
		}
		
		
			getProductos_Categoria(categoria) {
			
			let self = this;
			let data = {
				url : "product/getProductoCategoria/"+ categoria,
				type : "get",
				contentType : 'application/json',
				success : function(response) {
					self.producto_categoria(response);
				},
				error : function(response) {
					self.error(response.responseJSON.errorMessage);
				}
			};
			$.ajax(data);
		}
		
		eliminarProducto(id) {
			let self = this;
			let data = {
				url : "product/borrarProducto/" + id,
				type : "delete",
				contentType : 'application/json',
				success : function(response) {
					self.message("Producto eliminado");
			
				},
				error : function(response) {
					self.error(response.responseJSON.errorMessage);
				}
			};
		
			data.success;
			$.ajax(data);
		}
		
		getCarrito(){
			let self = this;
			let data = {
				url : "product/getCarrito",
				type : "get",
				contentType : 'application/json',
				success : function(response) {
					self.carrito(response.products);
				},
				error : function(response) {
					self.error(response.responseJSON.errorMessage);
				}
			};
			$.ajax(data);
		}
		
		addAlCarrito(id) {
			let self = this;
			let data = {
				url : "product/addAlCarrito/" + id,
				type : "post",
				contentType : 'application/json',
				success : function(response) {
					self.message("Producto añadido al carrito");
					self.carrito(response.products);
				},
				error : function(response) {
					self.error(response.responseJSON.errorMessage);
				}
			};
			$.ajax(data);
		}
		
		borrarDelCarrito(id) {
			let self = this;
			let data = {
				url : "product/borrarDelCarrito/" + id,
				type : "post",
				contentType : 'application/json',
				success : function(response) {
					self.message("Producto restado del carrito");
					self.carrito(response.products);
				},
				error : function(response) {
					self.error(response.responseJSON.errorMessage);
				}
			};
			$.ajax(data);
		}
		
		
			sumarCantidad(id) {
			let self = this;
			let data = {
				url : "product/sumarCantidad/" + id,
				type : "post",
				contentType : 'application/json',
				success : function(response) {
					self.message("Cantidad Actualizada");
					self.carrito(response.products);
				},
				error : function(response) {
					self.error(response.responseJSON.errorMessage);
				}
			};
			$.ajax(data);
		}
		
		register() {
			app.router.go( { path : "register" } );
		}

		connected() {
			accUtils.announce('Login page loaded.');
			document.title = "Login";
			this.getCarrito();
			this.getCategorias();
		};

		disconnected() {
			// Implement if needed
		};

		transitionCompleted() {
			// Implement if needed
		};
	}

	return ProductViewModel;
});
