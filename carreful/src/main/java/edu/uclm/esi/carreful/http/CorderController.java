package edu.uclm.esi.carreful.http;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.carreful.dao.CorderDao;
import edu.uclm.esi.carreful.exceptions.CarrefulException;
import edu.uclm.esi.carreful.model.Carrito;
import edu.uclm.esi.carreful.model.Categoria;
import edu.uclm.esi.carreful.model.Corder;
import edu.uclm.esi.carreful.model.Product;

@RestController
@RequestMapping("corder")
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
	
	@GetMapping("/getPedido/{id}")
	public Corder getPedido(HttpServletRequest request,@PathVariable String id) {
		Corder order;
		try {
			Optional<Corder> optpedido=orderDao.findById(id);
			if(!optpedido.isPresent()) {
				throw new CarrefulException(HttpStatus.NOT_FOUND, "El pedido no ha sido encontrado");
			}
			else {
				order = optpedido.get();
				
			}
			return order;
		} catch(Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}
	
	@GetMapping("/getListaProductos/{id}")
	public List<Product> getListaProductos(HttpServletRequest request,@PathVariable String id){
		try {
			Optional<Corder> optpedido=orderDao.findById(id);
			
			if(!optpedido.isPresent()) 
				throw new CarrefulException(HttpStatus.NOT_FOUND, "El pedido no ha sido encontrado");
			
			List<Product> p = new ArrayList<Product>();
			StringTokenizer st = new StringTokenizer(optpedido.get().getPedido(),"\n");
			while(st.hasMoreElements()) {
				Product producto = new Product(); 
				String token[] = st.nextToken().split(",");
				producto.setNombre(token[0]);
				System.out.println(token[1]);
				producto.setCantidad(Integer.parseInt(st.nextToken(token[1])));
				producto.setPrecio(Double.parseDouble(st.nextToken(token[2])));
				p.add(producto);
			}
				return p;
			
		}catch(CarrefulException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,e.getMessage());
		}
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
}
