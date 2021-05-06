package edu.uclm.esi.carreful.http;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.carreful.dao.TokenDao;
import edu.uclm.esi.carreful.dao.UserDao;
import edu.uclm.esi.carreful.exceptions.CarrefulException;
import edu.uclm.esi.carreful.exceptions.CarrefulLoginException;
import edu.uclm.esi.carreful.model.User;
import edu.uclm.esi.carreful.tokens.Email;
import edu.uclm.esi.carreful.tokens.Token;

@RestController
@RequestMapping("user")
public class UserController extends CookiesController {
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	TokenDao tokenDao;	
	
	@GetMapping("usarToken/{tokenId}")
	public void usarToken(HttpServletResponse response, @PathVariable String tokenId) throws IOException {
		Optional<Token> optToken = tokenDao.findById(tokenId);
		if (optToken.isPresent()) {
			Token token = optToken.get();
			if (token.isUsed())
				response.sendError(409, "El token ya se utilizó");
			else {
				response.sendRedirect("http://localhost?ojr=setNewPassword&token="+tokenId+"&email="+token.getEmail());
			}
		} else {
			response.sendError(404, "El token no existe");
		}
	}
	
	@PostMapping("/login")
	public void login(HttpServletRequest request, @RequestBody Map<String, Object> info) {
		try {
			JSONObject jso = new JSONObject(info);
			String email = jso.optString("email");
			if (email.length()==0)
				throw new CarrefulException(HttpStatus.FORBIDDEN, "Por favor, escribe tu correo");
			String pwd= jso.optString("pwd");
			User user = userDao.findByEmailAndPwd(email, DigestUtils.sha512Hex(pwd));
			if (user==null)
				throw new CarrefulException(HttpStatus.UNAUTHORIZED, "Credenciales Invalidas");
			request.getSession().setAttribute("userEmail", email);
			request.getSession().setAttribute("rol", user.getRol());
		} catch (CarrefulException e) {
			throw new ResponseStatusException(e.getStatus(), e.getMessage());
		}
	}
	
	@PutMapping("/register")
	public void register(@RequestBody Map<String, Object> info) {
		try {
			JSONObject jso = new JSONObject(info);
			String email = jso.optString("email");
			if (email.length()==0)
				throw new CarrefulLoginException();
			String pwd1 = jso.optString("pwd1");
			String pwd2 = jso.optString("pwd2");
			if (!pwd1.equals(pwd2))
				throw new CarrefulException(HttpStatus.NOT_ACCEPTABLE,"La contraseña no coincide con su confirmación");
			if (pwd1.length()<8)
				throw new CarrefulException(HttpStatus.NOT_ACCEPTABLE,"La contraseña tiene que tener al menos 8 caracteres");
			
			User user = new User();
			
			String rol = jso.optString("rol");
			if(rol.equalsIgnoreCase("empleado"))
				user.setRol(true);
			else
				user.setRol(false);
			
			user.setEmail(email);
			user.setPwd(pwd1);
			user.setPicture(jso.optString("picture"));
			userDao.save(user);
			Email correConfirmacion = new Email();
			String contenido="Su registro se ha realizado con éxito.\nSus credenciales son:\nUsuario: "+email+"\nContraseña: "+pwd1;
			correConfirmacion.send(user.getEmail(), "Registro completado en Carreful", contenido);
		} catch (CarrefulException e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
		}
	}
	
	@PutMapping("/setNewPwd")
	public void setNewPwd(@RequestBody Map<String, Object> info) {
		try {
			JSONObject jso = new JSONObject(info);
			String email = jso.optString("email");
			if (email.length()==0)
				throw new CarrefulLoginException();
			Optional<User> usuario = userDao.findById(email);
			if(usuario.isPresent()) {
				String pwd1 = jso.optString("pwd1");
				String pwd2 = jso.optString("pwd2");
				if (!pwd1.equals(pwd2))
					throw new CarrefulException(HttpStatus.NOT_ACCEPTABLE,"La contraseña no coincide con su confirmación");
				if (pwd1.length()<8)
					throw new CarrefulException(HttpStatus.NOT_ACCEPTABLE,"La contraseña tiene que tener al menos 8 caracteres");
				usuario.get().setPwd(pwd1);
				userDao.deleteById(email);
				userDao.save(usuario.get());
				
				Email correo= new Email();
				
				correo.send(usuario.get().getEmail(), "Cambio de Contraseña", "Se ha realizado el cambio de contraseña.\n La nueva contraseña es: "+pwd1);
			}
		}catch(CarrefulException e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
		}
		
	}
	
	@GetMapping("/recoverPwd")
	public void recoverPwd(@RequestParam String email) {
		try {
			User user = userDao.findByEmail(email);
			if (user!=null) {
				Token token = new Token(email);
				tokenDao.save(token);
				Email smtp = new Email();
				String texto = "Para recuperar tu contraseña, pulsa aquí: " + 
					"<a href='http://localhost/user/usarToken/" + token.getId() + "'>aquí</a>";
				smtp.send(user.getEmail(), "Carreful: recuperación de contraseña", texto);
			}
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
		}
	}
}
