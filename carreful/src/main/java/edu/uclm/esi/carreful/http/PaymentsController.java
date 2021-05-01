package edu.uclm.esi.carreful.http;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import edu.uclm.esi.carreful.exceptions.CarrefulException;
import edu.uclm.esi.carreful.model.Carrito;
import edu.uclm.esi.carreful.model.OrderedProduct;
import edu.uclm.esi.carreful.model.Product;
import edu.uclm.esi.carreful.tokens.Email;

@RestController
@RequestMapping("payments")
public class PaymentsController extends CookiesController {
	static {
		Stripe.apiKey = "sk_test_51IdbtOE3xk4z0l3iN9AVWJ8eQSx7Ifhhk13OAeKS3TQjW2eN66yCmS3xwXV265bvot1p0ldgabUugmSLk3310dP000A4Z1Kszx";
	}
	
	@PostMapping("/solicitarPreautorizacion")
	public String solicitarPreautorizacion(HttpServletRequest request, @RequestBody Map<String, Object> info) {
		try {
			Carrito carrito=(Carrito) request.getSession().getAttribute("carrito");
			JSONObject json_total = new JSONObject(info);
			double precio=json_total.optDouble("total");
			//Crear el pedido. coger el pedido y por cada uno declarar una variable suma que calcule el coste total. vaya producto a producto a ver si es congelado
			// si hay un congelado hacer un domicilio_express. Guardar el pedido en la base de datos.
		
			
			
			PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
					.setCurrency("eur")
					.setAmount((long) PrecioTotal(request)*100)
					.build();
			// Create a PaymentIntent with the order amount and currency
			PaymentIntent intent = PaymentIntent.create(createParams);
			JSONObject jso = new JSONObject(intent.toJson());
			
			
			
			return jso.getString("client_secret");
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@GetMapping("/confirmarPedido")
	public String confirmarPedido(HttpServletRequest request) {
		try {
			Carrito carrito=(Carrito) request.getSession().getAttribute("carrito");
			Email correo = new Email();
			correo.send((String) request.getSession().getAttribute("userEmail"),"Compra realizada","La compra de su pedido ha sido realizada correctamente");
			
			//Crear el pedido
			
			return "Compra realizada con exito";
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@GetMapping("/getCarrito")
	public Carrito getCarrito(HttpServletRequest request) {
		Carrito carrito = (Carrito) request.getSession().getAttribute("carrito");
		if (carrito==null) {
			carrito = new Carrito();
			request.getSession().setAttribute("carrito", carrito);
		}
		return carrito;
	}
	
	@GetMapping("/PrecioTotal")
	public double PrecioTotal(HttpServletRequest request) {
		double total=0;
		try {
			Carrito carrito = (Carrito) request.getSession().getAttribute("carrito");
			if (carrito==null)
				throw new CarrefulException(HttpStatus.NOT_FOUND,"No hay carrito en estos momentos");
			Iterator<OrderedProduct> iterador_productos=carrito.getProducts().iterator();
			while(iterador_productos.hasNext()) {
				OrderedProduct aux = iterador_productos.next();
				total+=aux.getAmount()*aux.getPrecio();
			}
		}catch(CarrefulException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
		return total;
	}
	
}
