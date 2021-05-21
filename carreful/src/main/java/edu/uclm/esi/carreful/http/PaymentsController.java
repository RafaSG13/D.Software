package edu.uclm.esi.carreful.http;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import edu.uclm.esi.carreful.auxiliares.TipoPedido;
import edu.uclm.esi.carreful.exceptions.CarrefulException;
import edu.uclm.esi.carreful.model.Carrito;
import edu.uclm.esi.carreful.model.Corder;
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
	
	@PostMapping("/solicitarPreautorizacion")
	public String solicitarPreautorizacion(HttpServletRequest request, @RequestBody Map<String, Object> info, @RequestParam String envio) {
		try {
			Carrito carrito=(Carrito) request.getSession().getAttribute("carrito");
			if(carrito==null) throw new CarrefulException(HttpStatus.NOT_FOUND,"No hay productos para pagar aun");
			
			if(hayCongelados(carrito) && envio.equals("domicilio")) 
				envio="express";
			
			Corder pedido = new Corder();
			pedido.setPrecioTotal(PrecioTotal(request)*100);
			pedido.setState("Preparandose");
			pedido.setPedido(sacarProductos(carrito.getProducts().iterator()));
			Class<?> p = Class.forName(envio);
			
			System.out.println(p.getName());
			
			pedido.setTipo(p.newInstance());
			
			
			PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
					.setCurrency("eur")
					.setAmount((long) 0)
					.build();
			// Create a PaymentIntent with the order amount and currency
			PaymentIntent intent = PaymentIntent.create(createParams);
			JSONObject jso = new JSONObject(intent.toJson());
			
			
			
			return jso.getString("client_secret");
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@GetMapping("/confirmarPedido/{envio}")
	public String confirmarPedido(HttpServletRequest request, @PathVariable String envio) {

		try {
			Carrito carrito=(Carrito) request.getSession().getAttribute("carrito");
			if(hayCongelados(carrito) && envio.equals("domicilio")) 
				envio="Express";
			
			/*Corder pedido = new Corder();
			pedido.setPrecioTotal(PrecioTotal(request)*100);
			pedido.setState("Preparandose");
			pedido.setPedido(sacarProductos(carrito.getProducts().iterator()));
			Class<?> p = Class.forName(envio);
			
			System.out.println(p.getName());
			
			pedido.setTipo(p.newInstance());
			*/
			
			User user = userDao.findByEmail((String) request.getSession().getAttribute("userEmail"));
			if (user!=null) {
				Token token = new Token((String) request.getSession().getAttribute("userEmail"));
				tokenDao.save(token);
				Email smtp = new Email();
				String texto = "Su pedido es el siguiente: " + 
						"<a href='http://localhost/user/usarToken/" + token.getId() + "'>aquí</a>";
				smtp.send(user.getEmail(), "Carreful confirmacion de Pedido.", texto);
				JSONObject j = new JSONObject();
			
			}
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
	public String sacarProductos(Iterator<OrderedProduct> productos){
		String pedido = "";
		while(productos.hasNext()) {
			OrderedProduct aux = productos.next();
			pedido+="Nombre : "+aux.getName()+" Cantidad : "+aux.getAmount()+" Precio : "+aux.getPrecio()+"\n";
		}
		return pedido;
		
	}
	
	public boolean hayCongelados(Carrito carrito) {
		Iterator<OrderedProduct> iterator_carrito = carrito.getProducts().iterator();
		boolean hayCongelado = false;
		while(iterator_carrito.hasNext() || hayCongelado==false) {
			OrderedProduct aux = iterator_carrito.next();
			if(aux.isCongelado()) 
				hayCongelado= true;
		}
		return hayCongelado;
	}

	
}
