package dominio;

import dominio.repositorio.RepositorioProducto;
import dominio.excepcion.GarantiaExtendidaException;
import dominio.repositorio.RepositorioGarantiaExtendida;

import java.util.Calendar;
import java.util.Date;

public class Vendedor {

    public static final String EL_PRODUCTO_TIENE_GARANTIA = "El producto ya cuenta con una garantia extendida";
    private static final double PRECIO_LIMITE_GARANTIA_EXTENDIDA_10P = 500000.0;

    private RepositorioProducto repositorioProducto;
    private RepositorioGarantiaExtendida repositorioGarantia;

    public Vendedor(RepositorioProducto repositorioProducto, RepositorioGarantiaExtendida repositorioGarantia) {
        this.repositorioProducto = repositorioProducto;
        this.repositorioGarantia = repositorioGarantia;

    }

    public void generarGarantia(String codigo, String nombreCliente) {
    	
    	//Se valida si el producto ya cuenta con una garantia extendida
    	GarantiaExtendida garantiaExtendida = repositorioGarantia.obtener(codigo);
    	if(garantiaExtendida != null) {
    		throw new GarantiaExtendidaException("El producto con código: "+codigo+
    				" ya cuenta con una garantia extendida con fecha de finalización "+garantiaExtendida.getFechaFinGarantia());
    	}
    	
    	//Se valida la cantidad de vocales que tiene el codigo de producto
    	String codigoFormato = codigo.toLowerCase();
    	int cantidadVocales = 0;
    	for(int i = 0; i < codigoFormato.length(); ++i) {
    		char caracter = codigoFormato.charAt(i);
    		if(caracter == 'a' || caracter == 'e' || caracter == 'i'  || caracter == 'o' || caracter == 'u') {
                    ++cantidadVocales;
                }
    	}
    	
    	//Si es mayor a 3 genera una excepcion
    	if(cantidadVocales >= 3) {
    		throw new GarantiaExtendidaException("Este producto no cuenta con garantía extendida");
    	}
    	
    	//Se obtiene el producto por el código
    	Producto producto = repositorioProducto.obtenerPorCodigo(codigo);
    	
    	//Se valida el precio del producto para calcular el precio y la fecha de la garantia extendida
    	double precioGarantia;
    	Date fechaSolicitudGarantia = new Date();
    	Calendar calendario = Calendar.getInstance();
    	calendario.setTime(fechaSolicitudGarantia);
    	Date fechaFinalizacionGarantia;
    	int candidadLunes = 0;
    	if(producto.getPrecio() > PRECIO_LIMITE_GARANTIA_EXTENDIDA_10P) {
    		//Se calcula el precio de la garantia
    		precioGarantia = producto.getPrecio()*0.2;
    		
    		//Se le agregan la cantidad de dias de la garantia
    		for(int i = 1; i < 200; ++i) {
    			calendario.add(Calendar.DATE, 1);
    			
    			//Se valida si es lunes
    			if(calendario.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY)
    				candidadLunes++;
    		}
    		
    		//Se resta la cantidad de lunes
    		calendario.add(Calendar.DATE, -candidadLunes);
    		
    	}else {
    		//Se calcula el precio de la garantia
    		precioGarantia = producto.getPrecio()*0.1;
    		
    		//Se le agregan la cantidad de dias de la garantia
    		for(int i = 1; i < 100; ++i) {
    			calendario.add(Calendar.DATE, 1);
    			
    			//Se valida si es lunes
    			if(calendario.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY)
    				candidadLunes++;
    		}
    		
    		//Se resta la cantidad de lunes
    		calendario.add(Calendar.DATE, -candidadLunes);
    	}
    	
		//Se valida si el dia de finalizacion de la garantia finaliza un domingo
    	if(calendario.get(Calendar.DAY_OF_WEEK) == 1) {
    		//Si es domingo se agrega un dia más
    		calendario.add(Calendar.DATE, 1);
    	}
		
    	fechaFinalizacionGarantia = calendario.getTime();
    	
    	//Se crea la garantia extendida
    	garantiaExtendida = new GarantiaExtendida(producto, fechaSolicitudGarantia, fechaFinalizacionGarantia, precioGarantia, nombreCliente);

    	//Se almacena en la base datos
    	repositorioGarantia.agregar(garantiaExtendida);

    }

    public boolean tieneGarantia(String codigo) {
        return false;
    }

}
