package edu.uclm.esi.carreful.http;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

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
import edu.uclm.esi.carreful.model.OrderedProduct;
import edu.uclm.esi.carreful.model.Product;

@RestController
@RequestMapping("product")
public class ProductController extends CookiesController{
	
	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private CategoriaDao categoriaDao;
	
	private final static String EL_PRODUCTO_NO_EXISTE="El producto no existe";
	private final static String CARRITO="carrito";
	
	@PostMapping("/add")
	public void add(HttpServletRequest request,@RequestBody Product product) {
		try {
			if(request.getSession().getAttribute("rol")==null || (boolean) request.getSession().getAttribute("rol")==false)
				throw new CarrefulException(HttpStatus.FORBIDDEN,"No tiene permiso para añadir un producto a la Base de Datos");
	
			Optional<Product> optProduct=productDao.findByNombre(product.getNombre());
			if(optProduct.isPresent()) {
				optProduct.get().setCantidad(optProduct.get().getCantidad()+product.getCantidad());
				optProduct.get().setNombre(product.getNombre());
				productDao.delete(optProduct.get());
				productDao.save(optProduct.get());
			}
			else
				productDao.save(product);
				
		} catch(Exception e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
		}
	}
	
	@PostMapping("/update")
	public void update(HttpServletRequest request,@RequestBody Product product) {
		try {
			if(request.getSession().getAttribute("rol")==null || (boolean) request.getSession().getAttribute("rol")==false)
				throw new CarrefulException(HttpStatus.FORBIDDEN,"No tiene permiso para modificar un producto de la Base de Datos");
	
			Optional<Product> optProduct=productDao.findByNombre(product.getNombre());
			if(optProduct.isPresent()) {
				productDao.delete(optProduct.get());
				productDao.save(product);
			}
			else
				throw new CarrefulException(HttpStatus.NOT_FOUND,"No se ha encontrado un producto con ese nombre");
				
		} catch(Exception e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
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
	
	@PostMapping("/addAlCarrito/{id}")
	public Carrito addAlCarrito(HttpServletRequest request, @PathVariable String id) {
		Carrito carrito = (Carrito) request.getSession().getAttribute(CARRITO);
		if (carrito==null) {
			carrito = new Carrito();
			request.getSession().setAttribute(CARRITO, carrito);
		}
		
		Optional<Product> producto = productDao.findById(id);
		try {
			if(producto.isPresent()) {

				Product p = producto.get();
						
				if(p.getCantidad()==0) throw new CarrefulException(HttpStatus.NOT_FOUND,"No hay stock disponible del producto");
				
				if(carrito.getOrdered(p.getNombre())!=null && carrito.getAmount(p) >= p.getCantidad()) 
					throw new CarrefulException(HttpStatus.NOT_FOUND,"No hay suficiente stock en estos momentos del producto "+ producto.get().getNombre());
				
				carrito.add(p, 1);
			}

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
	
	/*@DeleteMapping("/borrarProducto/{id}")
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
	}*/
	
	@DeleteMapping("/borrarProducto/{id}")
	public void borrarProductoDeLaBD(HttpServletRequest request, @PathVariable String id) {
		try {
			if (request.getSession().getAttribute("userEmail")==null)
				throw new CarrefulException(HttpStatus.FORBIDDEN,"No tienes permiso para borrar el producto. Inicia sesion como empleado para poder tener permisos de insercion,borrado y modificacion.");
			Optional<Product> optProduct = productDao.findById(id);
			if (optProduct.isPresent())
				productDao.deleteById(id);
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
			if(carrito.getAmount(producto)==0) {
				carrito.remove(producto);
			}

		}catch(Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
		return carrito;
	}
	
	@PostMapping("/decrementarStock")
	public void decrementarStock(HttpServletRequest request) {
		Carrito carrito = (Carrito) request.getSession().getAttribute(CARRITO);
		if (carrito==null) {
			carrito = new Carrito();
			request.getSession().setAttribute(CARRITO, carrito);
		}
		
		ArrayList<OrderedProduct> lista = new ArrayList<OrderedProduct>(carrito.getProducts());
		
		for(int i=0; i<lista.size(); i++) {
			Product product = lista.get(i).getProduct();
			product.setCantidad(product.getCantidad() - (int) carrito.getAmount(product)); //Actualizamos la cantidad de las BD.
			productDao.deleteById(product.getId());
			productDao.save(product);
		}
		
		carrito.getProducts().removeAll(carrito.getProducts());
		request.getSession().setAttribute("carrito", carrito); // el carrito se pone vacio de nuevo al decrementar el stock
		
	}
}
