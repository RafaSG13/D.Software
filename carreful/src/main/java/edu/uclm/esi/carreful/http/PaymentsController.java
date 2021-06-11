package edu.uclm.esi.carreful.http;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import edu.uclm.esi.carreful.dao.CuponDao;
import edu.uclm.esi.carreful.exceptions.CarrefulException;
import edu.uclm.esi.carreful.model.Carrito;
import edu.uclm.esi.carreful.model.Corder;
import edu.uclm.esi.carreful.model.Cupon;
import edu.uclm.esi.carreful.model.OrderedProduct;
import edu.uclm.esi.carreful.model.User;
import edu.uclm.esi.carreful.tokens.Email;
import edu.uclm.esi.carreful.tokens.Token;

@RestController
@RequestMapping("payments")
public class PaymentsController extends CookiesController {
	static {
		Stripe.apiKey = "sk_test_51IdbtOE3xk4z0l3iN9AVWJ8eQSx7Ifhhk13OAeKS3TQjW2eN66yCmS3xwXV265bvot1p0ldgabUugmSLk3310dP000A4Z1Kszx";
	}
	
	@Autowired
	CuponDao cDao;
	
	@PostMapping("/solicitarPreautorizacion")
	public String solicitarPreautorizacion(HttpServletRequest request, @RequestBody Map<String, Object> info) {
		try {
			Carrito carrito=(Carrito) request.getSession().getAttribute("carrito");
			if(carrito==null) {
				carrito = new Carrito();
				request.getSession().setAttribute("carrito",carrito);
			}
			if(carrito.getProducts().isEmpty()) throw new CarrefulException(HttpStatus.FORBIDDEN,"No hay productos en el Carrito, por favor, "
					+ "incluya productos en el carrito para poder pagar");
			
			Corder pedido = new Corder();
			//pedido.setPrecioTotal(PrecioTotal(request)*100);
			//pedido.setState("Preparandose");
			//pedido.setPedido(sacarProductos(carrito.getProducts().iterator()));

			
			PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
					.setCurrency("eur")
					.setAmount((long) precioTotal(request)*100)
					.build();
			// Create a PaymentIntent with the order amount and currency
			PaymentIntent intent = PaymentIntent.create(createParams);
			JSONObject jso = new JSONObject(intent.toJson());
			
			
			
			return jso.getString("client_secret");
		}catch (StripeException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Lo sentimos, pero el  pedido minimo a cobrar debe superar los 0,50 euros.");
		} catch (CarrefulException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
		}
	}
	
	@GetMapping("/confirmarPedido")
	public String confirmarPedido(HttpServletRequest request) {
		try {
			Carrito carrito=(Carrito) request.getSession().getAttribute("carrito");
			
				
			Corder pedido = new Corder();
			pedido.setPrecioTotal(precioTotal(request)*100);
			pedido.setState("Preparandose");
			pedido.setPedido(sacarProductos(carrito.getProducts().iterator()));
			
			User user = userDao.findByEmail((String) request.getSession().getAttribute("userEmail"));
			if (user!=null) {
				Token token = new Token((String) request.getSession().getAttribute("userEmail"));
				tokenDao.save(token);
				Email smtp = new Email();
				String texto = "Su pedido es el siguiente: " + 
						"<a href='http://localhost/user/usarToken/" + token.getId() + "'>aquí</a>";
				smtp.send(user.getEmail(), "Carreful confirmacion de Pedido.", texto);
				
			
			}
			return "Compra realizada con exito\nPedido con numero: "+pedido.getId();
			
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	
	@PostMapping("/AplicarDescuento/{cupon}")
	public void AplicarDescuento(HttpServletRequest request, @PathVariable String cupon) {
		try {
			Carrito carrito = (Carrito) request.getSession().getAttribute("carrito");
			if(carrito==null) { //Si no hay carrito en la session lo crea y lo inserta.
				carrito =  new Carrito();
				request.getSession().setAttribute("carrito",carrito);
			}
			Optional<Cupon> optcupon = cDao.findById(cupon);
			if(!optcupon.isPresent()) throw new CarrefulException(HttpStatus.NOT_FOUND,"El cupon introducido no existe");
			
			else {
				Cupon cuponDescuento = optcupon.get();
				if(cuponDescuento.getRango().comprobarValidez(Calendar.getInstance().getTime())) {
					carrito.setCuponDescuento(cuponDescuento);
					request.getSession().setAttribute("carrito", carrito);
				}
				else throw new CarrefulException(HttpStatus.FORBIDDEN,"El cupon no es valido a dia de hoy");

			}
			}catch(CarrefulException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	
		
	}
	
	
	public String sacarProductos(Iterator<OrderedProduct> productos){
		String pedido = "";
		while(productos.hasNext()) {
			OrderedProduct aux = productos.next();
			pedido+="Nombre : "+aux.getName()+" Cantidad : "+aux.getAmount()+" Precio : "+aux.getPrecio()+"\n";
		}
		return pedido;
		
	}
	


	
}
