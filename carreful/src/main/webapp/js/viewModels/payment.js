define([ 'knockout', 'appController', 'ojs/ojmodule-element-utils', 'accUtils',
		'jquery' ], function(ko, app, moduleUtils, accUtils, $) {

	class PaymentViewModel {
		constructor() {
			var self = this;
			
			self.stripe = Stripe('pk_test_51IdbtOE3xk4z0l3iOwpaJ3Rp0n58pBWBVBVxrba7Vslzdk28K2SCTtqYgk16LXkXthMQ5kZQQPaTkMr34BLL6BlJ00AKbD4VQZ');
			
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

		connected() {
			accUtils.announce('Login page loaded.');
			document.title = "Pago";
			this.solicitarPreautorizacion();			
		};
		
		solicitarPreautorizacion() {
			let self = this;
			// The items the customer wants to buy
			let purchase = {
			  items: [{ id: "xl-tshirt" }]
			};
			
			let data = {
				data : JSON.stringify(purchase),
				url : "payments/solicitarPreautorizacion",
				type : "post",
				contentType : 'application/json',
				success : function(response) {
					self.clientSecret = response;
					self.rellenarFormulario();
				},
				error : function(response) {
					self.error(response.responseJSON.errorMessage);
				}
			};
			$.ajax(data);
		}
		
		rellenarFormulario() {
			let self = this;
			var elements = self.stripe.elements();
		    var style = {
		      base: {
		        color: "#32325d",
		        fontFamily: 'Arial, sans-serif',
		        fontSmoothing: "antialiased",
		        fontSize: "16px",
		        "::placeholder": {
		          color: "#32325d"
		        }
		      },
		      invalid: {
		        fontFamily: 'Arial, sans-serif',
		        color: "#fa755a",
		        iconColor: "#fa755a"
		      }
		    };
			
		    var card = elements.create("card", { style: style });
		    // Stripe injects an iframe into the DOM
		    card.mount("#card-element");
		    card.on("change", function (event) {
		      // Disable the Pay button if there are no card details in the Element
		      document.querySelector("button").disabled = event.empty;
		      document.querySelector("#card-error").textContent = event.error ? event.error.message : "";
		    });
		    
		    var form = document.getElementById("payment-form");
		    form.addEventListener("submit", function(event) {
		      event.preventDefault();
		      // Complete payment when the submit button is clicked
		      self.payWithCard(card);
		    });
		}
		
		payWithCard(card){
			let self = this;
			self.stripe.confirmCardPayment(self.clientSecret, {
				payment_method: {
					card: card
			    } 
			}).then(function(result) {
				if (result.error) {
					// Show error to your customer (e.g., insufficient funds)
					self.error(result.error.message);
				} else {
					// The payment has been processed!
					if (result.paymentIntent.status === 'succeeded') {
						alert("Pago exitoso");
					}
				}
			});			
		}

		disconnected() {
			// Implement if needed
		};

		transitionCompleted() {
			// Implement if needed
		};
	}

	return PaymentViewModel;
});
