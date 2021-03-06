package edu.uclm.esi.carreful.http;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


import edu.uclm.esi.carreful.dao.TokenDao;
import edu.uclm.esi.carreful.tokens.Token;



public abstract class CookiesController {
	public static final  String COOKIE_NAME = "laCookie";
	public static final  String COOKIE_PATH = "/";
	
	@Autowired
	TokenDao tokenDao;
	

	protected Cookie readOrCreateCookie(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		if (cookies==null)
			return createCookie(response);
		Cookie cookie = findCookie(cookies);
		if (cookie==null)
			cookie = createCookie(response);
		return cookie;
	}

	private Cookie findCookie(Cookie[] cookies) {
		for (Cookie cookie : cookies)
			if (cookie.getName().equals(COOKIE_NAME))
				return cookie;
		return null;
	}

	private Cookie createCookie(HttpServletResponse response) {
		Cookie cookie = new Cookie(COOKIE_NAME, UUID.randomUUID().toString());
		cookie.setPath(COOKIE_PATH);
		cookie.setMaxAge(30*24*60*60);
		response.addCookie(cookie);
		return cookie;
	}
	
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
}
