package edu.uclm.esi.carreful.http;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.uclm.esi.carreful.dao.CorderDao;
import edu.uclm.esi.carreful.model.Corder;

@RestController
@RequestMapping("orders")
public class CorderController extends CookiesController {
	
	@Autowired
	private CorderDao orderDao;
	
	@GetMapping("get/{orderId}")
	public String get(@PathVariable String orderId) {
		Optional<Corder> optOrder = orderDao.findById(orderId);
		if (optOrder.isPresent())
			return optOrder.get().getState();
		return "No se encuentra el pedido";
	}
}
