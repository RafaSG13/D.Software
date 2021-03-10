package edu.uclm.esi.carreful.http;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.carreful.dao.ProductDao;
import edu.uclm.esi.carreful.dao.TokenDao;
import edu.uclm.esi.carreful.dao.UserDao;
import edu.uclm.esi.carreful.model.Carrito;
import edu.uclm.esi.carreful.model.Product;
import edu.uclm.esi.carreful.model.User;
import edu.uclm.esi.carreful.tokens.Email;
import edu.uclm.esi.carreful.tokens.Token;

@RestController
@RequestMapping("user")
public class UserController extends CookiesController {
	
	@Autowired	//Son campos que se instancian automaticamente con spring
	private UserDao userDao;
	
	@Autowired
	private TokenDao tokenDao;
	
	
	@GetMapping("/recoverPwd")
	public void recoverPwd(@RequestParam("email") String email) {
		try {
			
			User user = userDao.findByEmail(email);
			if(user==null) {
				Token token= new Token(email);
				tokenDao.save(token);
				Email smtp= new Email();
				smtp.send(email,"Carreful: recuperacion de Contrasena", token.getId());
			}
			
		}catch(Exception e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
		}
	}
	
	
	@PutMapping("/register")
	public void register(@RequestBody Map<String, Object> info) {
		try {
			
			JSONObject jso = new JSONObject(info);
			String userName= jso.optString("userName");
			if(userName.length()==0) throw new Exception("Debes introducir el nombre de usuario");
			String email = jso.optString("email");
			if(email.length()==0) throw new Exception("Debes indicar un email valido");
			String pwd1 = jso.optString("pwd1");
			String pwd2 = jso.optString("pwd2");
			if(!pwd1.equals(pwd2)) throw new Exception("Las contraseñas no son iguales");
			if(pwd1.length()<8) throw new Exception("La contraseña debe contener al menos 8 caracteres");
			
			
			User user = new User();
			user.setEmail(email);
			user.setPwd(pwd1);
			user.setPicture(jso.optString("picture"));
			userDao.save(user);
			
		}catch(Exception e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
		}
		
	}
	
	@PostMapping("/login")
	public void register(HttpServletRequest request ,@RequestBody Map<String, Object> info) {
		try {
			
			JSONObject jso = new JSONObject(info);
			String email= jso.optString("email");
			if(email.length()==0) throw new Exception("Debes introducir el correo valido");
			String pwd = jso.optString("pwd");
			
			User user = userDao.findByEmailAndPwd(email,DigestUtils.sha512Hex(pwd));
			if(user==null)
				throw new Exception("Credenciales invalidas");
			
			request.getSession().setAttribute("userEmail", email);
			
		}catch(Exception e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
		}
		
	}
}
