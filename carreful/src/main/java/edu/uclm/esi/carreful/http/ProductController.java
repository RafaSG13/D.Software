package edu.uclm.esi.carreful.http;

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

import edu.uclm.esi.carreful.CargaImagenes;
import edu.uclm.esi.carreful.dao.CategoriaDao;
import edu.uclm.esi.carreful.dao.ProductDao;
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
	
	@GetMapping("/buscarImagen/{nombreProducto}")
	public int buscarImagen(@PathVariable String nombreProducto) {
		try {
			return CargaImagenes.getImagenes(nombreProducto);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
		}
	}
	
	/*@PostMapping("/addAntiguo")
	public void addAntiguo(@RequestBody Map<String, Object> info) {
		try {
			JSONObject jsoProduct = new JSONObject(info);
			String idCategoria = "" + jsoProduct.optInt("categoria");
			Categoria categoria = categoriaDao.findById(idCategoria).get();
			Product product  = new Product();
			product.setNombre(jsoProduct.getString("nombre"));
			product.setPrecio(jsoProduct.getString("precio"));
			product.setCategoria(categoria);
			productDao.save(product);
		} catch(Exception e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
		}
	}*/
	
	@PostMapping("/add")
	public void add(@RequestBody Product product) {
		try {
			Optional<Product> optProduct=productDao.findByNombre(product.getNombre());
			if(optProduct.isPresent()) {
				optProduct.get().setCantidad(optProduct.get().getCantidad()+1);
				productDao.delete(optProduct.get());
				System.out.println(optProduct.get().getCantidad());
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
			throw new Exception("El producto no existe");
		} catch(Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}
	
	@GetMapping("/getUnProducto/{id}")
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
	}
	
	@PostMapping("/addAlCarrito/{id}")
	public Carrito addAlCarrito(HttpServletRequest request, @PathVariable String id) {
		Carrito carrito = (Carrito) request.getSession().getAttribute("carrito");
		if (carrito==null) {
			carrito = new Carrito();
			request.getSession().setAttribute("carrito", carrito);
		}
		Product producto = productDao.findById(id).get();
		
		try {
			if(producto.getCantidad()==0) throw new Exception("No hay stock disponible del producto");
			carrito.add(producto, 1);
			producto.setCantidad(producto.getCantidad()-1);
			productDao.delete(producto);
			productDao.save(producto);
			
		}catch(Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
		return carrito;
	}
	
	@PostMapping("/sumarCantidad/{id}")
	public Carrito sumarCantidad(HttpServletRequest request, @PathVariable String id) {
		Carrito carrito = (Carrito) request.getSession().getAttribute("carrito");
		if (carrito==null) {
			carrito = new Carrito();
			request.getSession().setAttribute("carrito", carrito);
		}
		Optional<Product> producto=productDao.findById(id);
		if(producto.isPresent()) {
			try {
				if(producto.get().getCantidad()==0) throw new Exception("No hay stock disponible del producto");
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
				throw new Exception("El producto no existe");
		} catch(Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}
	
	@DeleteMapping("/borrarProductoDeLaBD/{nombre}")
	public void borrarProductoDeLaBD(HttpServletRequest request, @PathVariable String nombre) {
		try {
			if (request.getSession().getAttribute("userEmail")==null)
				throw new Exception("No tienes permiso para borrar el producto");
			Optional<Product> optProduct = productDao.findById(nombre);
			if (optProduct.isPresent())
				productDao.deleteById(nombre);
			else
				throw new Exception("El producto no existe");
		} catch(Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}
	
	@PostMapping("/borrarDelCarrito/{id}")
	public Carrito borrarDelCarrito(HttpServletRequest request, @PathVariable String id) {
		Carrito carrito = (Carrito) request.getSession().getAttribute("carrito");
		if (carrito==null) {
			carrito = new Carrito();
			request.getSession().setAttribute("carrito", carrito);
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
