package edu.uclm.esi.carreful.http;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
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

import edu.uclm.esi.carreful.Patrones.CuponMultiple;
import edu.uclm.esi.carreful.Patrones.CuponUnUso;
import edu.uclm.esi.carreful.Patrones.CuponUnUsuario;
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

	@PostMapping("/solicitarPreautorizacion")
	public String solicitarPreautorizacion(HttpServletRequest request, @RequestBody Map<String, Object> info) {
		try {
			Carrito carrito = (Carrito) request.getSession().getAttribute("carrito");
			if (carrito == null) {
				carrito = new Carrito();
				request.getSession().setAttribute("carrito", carrito);
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

	@GetMapping("/confirmarPedido")
	public String confirmarPedido(HttpServletRequest request) {
		try {
			Carrito carrito=(Carrito) request.getSession().getAttribute("carrito");
			User user = userDao.findByEmail((String) request.getSession().getAttribute("userEmail"));

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

			Cupon cupon = carrito.getCuponDescuento();
			Optional<CuponUnUso> optcuponUnUso = cuponUnUsoDao.findById(cupon.getCodigo());
			Optional<CuponMultiple> optcuponMultiple = cuponMultipleDao.findById(cupon.getCodigo());
			Optional<CuponUnUsuario> optcuponUnUsuario = cuponUnUsuarioDao.findById(cupon.getCodigo());

			if (optcuponUnUso.isPresent()) {
				optcuponUnUso.get().usarCupon("");
				cuponUnUsoDao.deleteById(optcuponUnUso.get().getCodigo());
				cuponUnUsoDao.save(optcuponUnUso.get());
			}

			if (optcuponUnUsuario.isPresent()) {
				optcuponUnUsuario.get().usarCupon((String) request.getSession().getAttribute("userEmail"));
				cuponUnUsuarioDao.deleteById(optcuponUnUsuario.get().getCodigo());
				cuponUnUsuarioDao.save(optcuponUnUsuario.get());
			}

			if (optcuponMultiple.isPresent()) {
				optcuponMultiple.get().usarCupon((String) request.getSession().getAttribute("userEmail"));
				cuponMultipleDao.deleteById(optcuponMultiple.get().getCodigo());
				cuponMultipleDao.save(optcuponMultiple.get());
			}

			return "Compra realizada con exito\nPedido con numero: " + pedido.getId();

		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@PostMapping("/AplicarDescuento")
	public void AplicarDescuento(HttpServletRequest request, @RequestBody Map<String, Object> info) {
		try {
			JSONObject json = new JSONObject(info);
			String codigoCupon = json.optString("cupon");
			String emailAlternativo = json.optString("email");
			Carrito carrito = (Carrito) request.getSession().getAttribute("carrito");

			if(carrito==null) { //Si no hay carrito en la session lo crea y lo inserta.
				carrito =  new Carrito();
				request.getSession().setAttribute("carrito",carrito);
			}
			String email = (String) request.getSession().getAttribute("userEmail");

			Optional<CuponUnUso> optcuponUnUso = cuponUnUsoDao.findById(codigoCupon);
			Optional<CuponMultiple> optcuponMultiple = cuponMultipleDao.findById(codigoCupon);
			Optional<CuponUnUsuario> optcuponUnUsuario = cuponUnUsuarioDao.findById(codigoCupon);

			if (!optcuponUnUso.isPresent() && !optcuponMultiple.isPresent() && !optcuponUnUsuario.isPresent())
				throw new CarrefulException(HttpStatus.NOT_FOUND, "El cupon introducido no existe");

			if (optcuponUnUso.isPresent() && !optcuponUnUso.get().isUsado())
				carrito = introducirCuponEnCarrito(request, optcuponUnUso.get());

			if (emailAlternativo.equals("") && email == null) {
				throw new CarrefulException(HttpStatus.FORBIDDEN,
						"Para utilizar este tipo de cupon necesitas introducir un email en el campo email alternativo o bien iniciar sesion");
			} else {
				if (!emailAlternativo.equals("") && email != null)
					email = emailAlternativo;
				
				if (!(email.contains("@") && (email.contains(".com") || email.contains(".es"))))
					throw new CarrefulException(HttpStatus.FORBIDDEN, "El formato del correo introducido no es valido");

				if (optcuponUnUsuario.isPresent() && !optcuponUnUsuario.get().contieneUsuario(email))
					carrito = introducirCuponEnCarrito(request, optcuponUnUsuario.get());

				if (optcuponMultiple.isPresent())
					carrito = introducirCuponEnCarrito(request, optcuponMultiple.get());
			}
			request.getSession().setAttribute("carrito", carrito);

		} catch (CarrefulException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}catch(NullPointerException nullexception) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}

	}

	@GetMapping("/PrecioTotal")
	public double precioTotal(HttpServletRequest request) {
		double total = 0;
		Carrito carrito = (Carrito) request.getSession().getAttribute("carrito");
		if (carrito == null) {
			carrito = new Carrito();
			request.getSession().setAttribute("carrito", carrito);
		}

		Iterator<OrderedProduct> iterador_productos = carrito.getProducts().iterator();
		while (iterador_productos.hasNext()) {
			OrderedProduct aux = iterador_productos.next();
			total += aux.getAmount() * aux.getPrecio();
		}
		if (carrito.getCuponDescuento() != null) {
			if (carrito.getCuponDescuento().getTipoDescuento().equalsIgnoreCase("porcentual")) {
				total = total - (total * carrito.getCuponDescuento().getDescuento() / 100);
			}

			else if (carrito.getCuponDescuento().getTipoDescuento().equalsIgnoreCase("fijo"))
				total = total - carrito.getCuponDescuento().getDescuento();
		}
		if (total < 0) // si el descuento hace que el precio sea negativo, entonces lo cambiamos a que
						// sea como minimo GRATIS
			total = 0;
		return total;

	}

	public String sacarProductos(Iterator<OrderedProduct> productos) {
		String pedido = "";
		while (productos.hasNext()) {
			OrderedProduct aux = productos.next();
			pedido += aux.getName() + "," + aux.getAmount() + "," + aux.getPrecio() + "\n";
		}
		return pedido;
	}

	private Carrito introducirCuponEnCarrito(HttpServletRequest request, Cupon cupon) throws CarrefulException {
		Carrito carrito = (Carrito) request.getSession().getAttribute("carrito");

		if (cupon.getRango().comprobarValidez(Calendar.getInstance().getTime())) {
			carrito.setCuponDescuento(cupon);
			request.getSession().setAttribute("carrito", carrito);

		} else
			throw new CarrefulException(HttpStatus.FORBIDDEN, "El cupon no es valido a dia de hoy");
		return carrito;
	}
}
