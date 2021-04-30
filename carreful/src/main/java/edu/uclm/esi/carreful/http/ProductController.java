package edu.uclm.esi.carreful.http;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.carreful.dao.CategoriaDao;
import edu.uclm.esi.carreful.dao.ProductDao;
import edu.uclm.esi.carreful.exceptions.CarrefulException;
import edu.uclm.esi.carreful.model.Carrito;
import edu.uclm.esi.carreful.model.Categoria;
import edu.uclm.esi.carreful.model.Product;

@RestController
@RequestMapping("product")
public class ProductController extends CookiesController {
	
	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private CategoriaDao categoriaDao;
	
	private final static String EL_PRODUCTO_NO_EXISTE="El producto no existe";
	private final static String CARRITO="carrito";
	
	@PostMapping("/add")
	public void add(HttpServletRequest request,@RequestBody Product product) {
		try {
			if(request.getSession().getAttribute("rol")==null)
				throw new CarrefulException(HttpStatus.FORBIDDEN,"No tiene permiso para añadir un producto a la Base de Datos");
			boolean rol = (boolean) request.getSession().getAttribute("rol");	
			if(rol!=true) throw new CarrefulException(HttpStatus.FORBIDDEN,"No tiene permiso para añadir un producto a la Base de Datos");
			
			Optional<Product> optProduct=productDao.findByNombre(product.getNombre());
			if(optProduct.isPresent()) {
				optProduct.get().setCantidad(optProduct.get().getCantidad()+product.getCantidad());
				productDao.delete(optProduct.get());
				productDao.save(optProduct.get());
			}
			else
				productDao.save(product);
				
		} catch(Exception e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
		}
	}
	
	@GetMapping("/getTodos")
	public List<Product> get() {
		try {
			return productDao.findAll();
		} catch(Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}
	
	@GetMapping("/getCategorias")
	public List<Categoria> getCategorias() {
		try {
			return categoriaDao.findAll();
		} catch(Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}
	

	@GetMapping("/getProductoCategoria/{categoria}")
	public List<Product> getProducto_Categoria(@PathVariable String categoria) {
		try {
			Optional<Categoria> categoriaProducto=categoriaDao.findById(categoria);
			return productDao.findByCategoria(categoriaProducto);
		} catch(Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@GetMapping("/getPrecio/{id}")
	public String getPrecio(@PathVariable String id) {
		try {
			Optional<Product> optProduct = productDao.findById(id);
			if (optProduct.isPresent())
				return ""+optProduct.get().getPrecio();
			throw new CarrefulException(HttpStatus.NOT_FOUND,EL_PRODUCTO_NO_EXISTE);
		} catch(Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}
	
	/*@GetMapping("/getUnProducto/{id}")
	public Product getUnProducto(@PathVariable String id) {
		try {
			Optional<Product> optProduct = productDao.findById(id);
			if (optProduct.isPresent()) {
				JSONObject jso= new JSONObject();
				jso.put("id", optProduct.get().getId());
				jso.put("nombre", optProduct.get().getNombre());
				jso.put("precio", optProduct.get().getPrecio());
				jso.put("categoria", optProduct.get().getCategoria());
				return optProduct.get();
			}
			throw new Exception("El producto no existe");
		} catch(Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}*/
	@GetMapping("/getCarrito")
	public Carrito getCarrito(HttpServletRequest request) {
		Carrito carrito = (Carrito) request.getSession().getAttribute(CARRITO);
		if (carrito==null) {
			carrito = new Carrito();
			request.getSession().setAttribute(CARRITO, carrito);
		}
		return carrito;
	}
	
	@PostMapping("/addAlCarrito/{id}")
	public Carrito addAlCarrito(HttpServletRequest request, @PathVariable String id) {
		Carrito carrito = (Carrito) request.getSession().getAttribute(CARRITO);
		if (carrito==null) {
			carrito = new Carrito();
			request.getSession().setAttribute(CARRITO, carrito);
		}
		
		Product producto = productDao.findById(id).get();
		
		try {
			if(producto.getCantidad()==0) throw new Exception("No hay stock disponible del producto");
			carrito.add(producto, 1);			
		}catch(Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
		return carrito;
	}
	
	@PostMapping("/sumarCantidad/{id}")
	public Carrito sumarCantidad(HttpServletRequest request, @PathVariable String id) {
		Carrito carrito = (Carrito) request.getSession().getAttribute(CARRITO);
		if (carrito==null) {
			carrito = new Carrito();
			request.getSession().setAttribute(CARRITO, carrito);
		}
		Optional<Product> producto=productDao.findById(id);
		if(producto.isPresent()) {
			try {
				if(producto.get().getCantidad()==0) throw new CarrefulException(HttpStatus.NOT_FOUND,"No hay stock disponible del producto");
				carrito.add(producto.get(), 1);
				producto.get().setCantidad(producto.get().getCantidad()-1);
				productDao.delete(producto.get());
				productDao.save(producto.get());
				
			}catch(Exception e) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
			}
		}
		

		return carrito;
	}
	
	@DeleteMapping("/borrarProducto/{id}")
	public void borrarProducto(@PathVariable String id) {
		try {
			Optional<Product> optProduct = productDao.findById(id);
			if (optProduct.isPresent())
				productDao.deleteById(id);
			else
				throw new CarrefulException(HttpStatus.NOT_FOUND,EL_PRODUCTO_NO_EXISTE);
		} catch(Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}
	
	@DeleteMapping("/borrarProductoDeLaBD/{nombre}")
	public void borrarProductoDeLaBD(HttpServletRequest request, @PathVariable String nombre) {
		try {
			if (request.getSession().getAttribute("userEmail")==null)
				throw new CarrefulException(HttpStatus.FORBIDDEN,"No tienes permiso para borrar el producto");
			Optional<Product> optProduct = productDao.findById(nombre);
			if (optProduct.isPresent())
				productDao.deleteById(nombre);
			else
				throw new CarrefulException(HttpStatus.NOT_FOUND,EL_PRODUCTO_NO_EXISTE);
		} catch(Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}
	
	@PostMapping("/borrarDelCarrito/{id}")
	public Carrito borrarDelCarrito(HttpServletRequest request, @PathVariable String id) {
		Carrito carrito = (Carrito) request.getSession().getAttribute(CARRITO);
		if (carrito==null) {
			carrito = new Carrito();
			request.getSession().setAttribute(CARRITO, carrito);
		}
		Product producto = productDao.findById(id).get();

		try {
			carrito.subtract(producto, 1);
			producto.setCantidad(producto.getCantidad()+1);
			if(carrito.getAmount(producto)==0) {
				carrito.remove(producto);
			}
			productDao.delete(producto);
			productDao.save(producto);
		}catch(Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
		return carrito;
	}
}
