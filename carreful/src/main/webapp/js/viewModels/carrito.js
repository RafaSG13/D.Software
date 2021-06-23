
	class carritoVM{
		constructor(app){
			this.app=app;
			this.carrito = app.ko.observableArray([]);
			this.total = app.ko.observable(0);
			this.message = app.ko.observable();
			this.error = app.ko.observable();
		}
		
		
		getCarrito(){
			let self = this;
			let data = {
				url : "product/getCarrito",
				type : "get",
				contentType : 'application/json',
				success : function(response) {
					self.error("");
					self.message("Obtencion del carrito realizada");
					self.carrito(response.products);
				},
				error : function(response) {
					self.error(response.responseJSON.errorMessage);
				}
			};
			$.ajax(data);
		}
		
		calcularTotal(){
			let self = this;
			let aux;
			let data = {
				url : "payments/PrecioTotal",
				type : "get",
				contentType : 'application/json',
				success : function(response) {
					self.total(response);
					
				},
				error : function(response) {
					self.error(response.responseJSON.errorMessage);
				}
			};
			$.ajax(data);
		}
	}