package modelo;

public class AtraccionCultural extends Atraccion {
	private static final long serialVersionUID = 2L;
	
	private int edadMinima;
	
	public AtraccionCultural(String nombre, int capacidadMaxima, int empleadosMinimos, String ubicacion,
			String exclusividad, int edadMinima) {
		super(nombre, capacidadMaxima, empleadosMinimos, ubicacion, exclusividad);
		this.edadMinima = edadMinima;
	}

	public int getEdadMinima() {
		return edadMinima;
	}

	@Override
	public String getTipo() {
		return "Cultural";
	}

	public boolean puedeIngresar(int edad) {
		return edad >= edadMinima;
	}
}