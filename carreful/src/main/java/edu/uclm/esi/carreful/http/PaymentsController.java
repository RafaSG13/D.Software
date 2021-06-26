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

import edu.uclm.esi.carreful.dao.CorderDao;
import edu.uclm.esi.carreful.dao.CuponMultipleDao;
import edu.uclm.esi.carreful.dao.CuponUnUsoDao;
import edu.uclm.esi.carreful.dao.CuponUnUsuarioDao;
import edu.uclm.esi.carreful.dao.UserDao;
import edu.uclm.esi.carreful.exceptions.CarrefulException;
import edu.uclm.esi.carreful.model.Carrito;
import edu.uclm.esi.carreful.model.Corder;
import edu.uclm.esi.carreful.model.Cupon;
import edu.uclm.esi.carreful.model.OrderedProduct;
import edu.uclm.esi.carreful.model.User;
import edu.uclm.esi.carreful.patrones.CuponMultiple;
import edu.uclm.esi.carreful.patrones.CuponUnUso;
import edu.uclm.esi.carreful.patrones.CuponUnUsuario;
import edu.uclm.esi.carreful.tokens.Email;
import edu.uclm.esi.carreful.tokens.Token;

@RestController
@RequestMapping("payments")
public class PaymentsController extends CookiesController {
	static {
		Stripe.apiKey = "sk_test_51IdbtOE3xk4z0l3iN9AVWJ8eQSx7Ifhhk13OAeKS3TQjW2eN66yCmS3xwXV265bvot1p0ldgabUugmSLk3310dP000A4Z1Kszx";
	}

	@Autowired
	CuponUnUsoDao cuponUnUsoDao;

	@Autowired
	CuponUnUsuarioDao cuponUnUsuarioDao;

	@Autowired
	CuponMultipleDao cuponMultipleDao;

	@Autowired
	CorderDao corderDao;

	@Autowired
	UserDao userDao;
	
	private String cadenaUserEmail = "userEmail";
	private String cadenaCarrito = "carrito";

	@PostMapping("/solicitarPreautorizacion")
	public String solicitarPreautorizacion(HttpServletRequest request, @RequestBody Map<String, Object> info) {
		try {
			Carrito carrito = (Carrito) request.getSession().getAttribute(cadenaCarrito);
			if (carrito == null) {
				carrito = new Carrito();
				request.getSession().setAttribute(cadenaCarrito, carrito);
			}
			if (carrito.getProducts().isEmpty())
				throw new CarrefulException(HttpStatus.FORBIDDEN, "No hay productos en el Carrito, por favor, "
						+ "incluya productos en el carrito para poder pagar");

			PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder().setCurrency("eur")
					.setAmount((long) precioTotal(request) * 100).build();
			// Create a PaymentIntent with the order amount and currency
			PaymentIntent intent = PaymentIntent.create(createParams);
			JSONObject jso = new JSONObject(intent.toJson());

			return jso.getString("client_secret");
		} catch (StripeException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Lo sentimos, pero el  pedido minimo a cobrar debe superar los 0,50 euros.");
		} catch (CarrefulException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/confirmarPedido/{email}")
	public String confirmarPedido(HttpServletRequest request, @PathVariable String email) {
		try {
		
			if(email.equals("vacio"))
				email="";
			String emailSession="";
			Carrito carrito=(Carrito) request.getSession().getAttribute(cadenaCarrito);
			User user= null;
			if(request.getSession().getAttribute("userEmail") != null) {
				user = userDao.findByEmail((String) request.getSession().getAttribute("userEmail"));
				emailSession = (String) request.getSession().getAttribute("userEmail");
			}			
			Corder pedido = new Corder();
			pedido.setPrecioTotal(precioTotal(request));
			pedido.setState("Preparandose");
			pedido.setPedido(sacarProductos(carrito.getProducts().iterator()));

			corderDao.save(pedido);
			
			if (user != null) {
				Token token = new Token((String) request.getSession().getAttribute("userEmail"));
				tokenDao.save(token);
				Email smtp = new Email();
				String texto = "Su pedido es el siguiente: " + "<a href='http://localhost/user/usarToken/"
						+ token.getId() + "'>aquí</a>";
				smtp.send(user.getEmail(), "Carreful confirmacion de Pedido.", texto);
			}
			String emailUsado="";
			emailUsado=emailSessionOemailAlternativo(emailSession, email);
			

			Cupon cupon = carrito.getCuponDescuento();
			Optional<CuponUnUso> optcuponUnUso = cuponUnUsoDao.findById(cupon.getCodigo());
			Optional<CuponMultiple> optcuponMultiple = cuponMultipleDao.findById(cupon.getCodigo());
			Optional<CuponUnUsuario> optcuponUnUsuario = cuponUnUsuarioDao.findById(cupon.getCodigo());

			if (optcuponUnUso.isPresent()) {
				optcuponUnUso.get().usarCupon(emailUsado);
				cuponUnUsoDao.deleteById(optcuponUnUso.get().getCodigo());
				cuponUnUsoDao.save(optcuponUnUso.get());
			}

			if (optcuponUnUsuario.isPresent()) {
				optcuponUnUsuario.get().usarCupon(emailUsado);
				cuponUnUsuarioDao.deleteById(optcuponUnUsuario.get().getCodigo());
				cuponUnUsuarioDao.save(optcuponUnUsuario.get());
			}

			if (optcuponMultiple.isPresent()) {
				optcuponMultiple.get().usarCupon(emailUsado);
				cuponMultipleDao.deleteById(optcuponMultiple.get().getCodigo());
				cuponMultipleDao.save(optcuponMultiple.get());
			}

			return "Compra realizada con exito\nPedido con numero: " + pedido.getId();

		} catch (CarrefulException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@PostMapping("/AplicarDescuento")
	public void aplicarDescuento(HttpServletRequest request, @RequestBody Map<String, Object> info) {
		try {
			JSONObject json = new JSONObject(info);
			String codigoCupon = json.optString("cupon");
			String emailAlternativo = json.optString("email");
			String emailSession ="";
			
			if(request.getSession().getAttribute(cadenaUserEmail)!=null)
				emailSession = (String) request.getSession().getAttribute(cadenaUserEmail);

			
			Carrito carrito = (Carrito) request.getSession().getAttribute(cadenaCarrito);
			if(carrito==null) { //Si no hay carrito en la session lo crea y lo inserta.
				carrito =  new Carrito();
				request.getSession().setAttribute(cadenaCarrito,carrito);
			}
			
			Optional<CuponUnUso> optcuponUnUso = cuponUnUsoDao.findById(codigoCupon);
			Optional<CuponMultiple> optcuponMultiple = cuponMultipleDao.findById(codigoCupon);
			Optional<CuponUnUsuario> optcuponUnUsuario = cuponUnUsuarioDao.findById(codigoCupon);

			if (!optcuponUnUso.isPresent() && !optcuponMultiple.isPresent() && !optcuponUnUsuario.isPresent())
				throw new CarrefulException(HttpStatus.NOT_FOUND, "El cupon introducido no existe");

			if (optcuponUnUso.isPresent()) {
				if(optcuponUnUso.get().isUsado()) throw new CarrefulException(HttpStatus.FORBIDDEN,"El cupon ya ha sido utilizado");
				carrito = introducirCuponEnCarrito(request, optcuponUnUso.get());
			}
				

				String emailUsado=emailSessionOemailAlternativo(emailSession, emailAlternativo);
		
				if (optcuponUnUsuario.isPresent() && !optcuponUnUsuario.get().contieneUsuario(emailUsado))
					carrito = introducirCuponEnCarrito(request, optcuponUnUsuario.get());

				if (optcuponMultiple.isPresent())
					carrito = introducirCuponEnCarrito(request, optcuponMultiple.get());
			
			request.getSession().setAttribute(cadenaCarrito, carrito);

		} catch (CarrefulException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}

	}

	@GetMapping("/PrecioTotal")
	public double precioTotal(HttpServletRequest request) {
		double total = 0;
		Carrito carrito = (Carrito) request.getSession().getAttribute(cadenaCarrito);
		if (carrito == null) {
			carrito = new Carrito();
			request.getSession().setAttribute(cadenaCarrito, carrito);
		}

		Iterator<OrderedProduct> iteradorProductos = carrito.getProducts().iterator();
		while (iteradorProductos.hasNext()) {
			OrderedProduct aux = iteradorProductos.next();
			total += aux.getAmount() * aux.getPrecio();
		}
		if (carrito.getCuponDescuento() != null) {
			if (carrito.getCuponDescuento().getTipoDescuento().equalsIgnoreCase("porcentual")) {
				total = total - (total * carrito.getCuponDescuento().getDescuento() / 100);
			}

			else if (carrito.getCuponDescuento().getTipoDescuento().equalsIgnoreCase("fijo")) {
				total = total - carrito.getCuponDescuento().getDescuento();
			}
		}
		if (total < 0) // si el descuento hace que el precio sea negativo, entonces lo cambiamos a que
						// sea como minimo GRATIS
			total = 0;
		return total;

	}

	public String sacarProductos(Iterator<OrderedProduct> productos) {
		StringBuilder pedido = new StringBuilder();
		while (productos.hasNext()) {
			OrderedProduct aux = productos.next();
			pedido.append(""+aux.getName() + "," + aux.getAmount() + "," + aux.getPrecio() + "\n");
		}
	
		return 	pedido.toString();
	}

	private Carrito introducirCuponEnCarrito(HttpServletRequest request, Cupon cupon) throws CarrefulException {
		Carrito carrito = (Carrito) request.getSession().getAttribute(cadenaCarrito);

		if (cupon.getRango().comprobarValidez(Calendar.getInstance().getTime())) {
			carrito.setCuponDescuento(cupon);
			request.getSession().setAttribute(cadenaCarrito, carrito);

		} else
			throw new CarrefulException(HttpStatus.FORBIDDEN, "El cupon no es valido a dia de hoy");
		return carrito;
	}
	
	private String emailSessionOemailAlternativo(String emailSession, String emailAlternativo) throws CarrefulException {
		String emailUsado;
		
		if(emailAlternativo.equals("") && emailSession.equals("")) throw new CarrefulException(HttpStatus.FORBIDDEN,
				"Para utilizar esta funcionalidad debe introducir un email. Introduzca un correo alternativo o inicie session.");

		if (!emailAlternativo.equals(""))
			emailUsado = emailAlternativo;
		else
			emailUsado= emailSession;
		
		if (!(emailUsado.contains("@") && (emailUsado.contains(".com") || emailUsado.contains(".es"))))
			throw new CarrefulException(HttpStatus.FORBIDDEN, "El formato del correo introducido no es valido");
		
		return emailUsado;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
