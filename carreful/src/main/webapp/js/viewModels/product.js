define([ 'knockout', 'appController', 'ojs/ojmodule-element-utils', 'accUtils',
		'jquery' ], function(ko, app, moduleUtils, accUtils, $) {

	class ProductViewModel {
		constructor() {
			var self = this;
			
			self.nombre = ko.observable("Detergente");
			self.precio = ko.observable("9,50 €");
			
			self.productos = ko.observableArray([]);
			self.carrito = ko.observableArray([]);
			
			self.message = ko.observable();
			self.error = ko.observable();
			
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
				precio : this.precio()
			};
			let data = {
				data : JSON.stringify(info),
				url : "product/add",
				type : "post",
				contentType : 'application/json',
				success : function(response) {
					self.message("Producto guardado");
					self.getProductos();
				},
				error : function(response) {
					self.error(response.responseJSON.errorMessage);
				}
			};
			$.ajax(data);
		}
		getProductos(){
			let self = this;
			let data = {
				url : "product/getTodos",
				type : "get",
				contentType : 'application/json',
				success : function(response) {
					self.productos(response);
					/*for (let i=0; i<response.length; i++) {	Porque hemos puesto en cada boton la llamada a $parent.eliminarProducrto(...)
						let objetito = {
								nombre : response[i].nombre,
								precio : response[i].precio,
								eliminar : function(){
									self.eliminarProducto(response[i].nombre);
								}
						};
						self.productos.push(objetito);
					}*/
					
				},
				error : function(response) {
					self.error(response.responseJSON.errorMessage);
				}
			};
			$.ajax(data);
		}
		
		eliminarProducto(nombre){
			let self = this;
			let data = {
					url : "product/borrarProducto/"+nombre,
					type : "delete",
					contentType : 'application/json',
					success : function(response) {
						self.message("Producto eliminado");
						self.getProductos();
					},
					error : function(response) {
						self.error(response.responseJSON.errorMessage);
					}
				};
				$.ajax(data);
		}
		
		addAlCarrito(nombre){
			let self = this;
			let data = {
					url : "product/addAlCarrito/" + nombre,
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
		
		getFoto(index){
			var self= this;
			let data ={
				url: "product/getFoto/"
			$.ajax(data);
		}
		
		}
		
		register() {
			app.router.go( { path : "register" } );
		}

		connected() {
			accUtils.announce('Login page loaded.');
			document.title = "Login";
			
			this.getProductos();
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
