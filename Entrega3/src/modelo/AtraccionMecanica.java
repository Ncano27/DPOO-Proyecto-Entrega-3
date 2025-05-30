package modelo;

public class AtraccionMecanica extends Atraccion {
	private static final long serialVersionUID = 3L;

	private double alturaMinima;
	private double alturaMaxima;
	private double pesoMinimo;
	private double pesoMaximo;
	private boolean contraindicacionCardiaca;
	private boolean contraindicacionVertigo;
	private String riesgo;

	public AtraccionMecanica(String nombre, int capacidadMaxima, int empleadosMinimos, String ubicacion,
			String exclusividad, double alturaMinima, double alturaMaxima, double pesoMinimo, double pesoMaximo,
			boolean contraindicacionCardiaca, boolean contraindicacionVertigo, String riesgo) {
		super(nombre, capacidadMaxima, empleadosMinimos, ubicacion, exclusividad);
		this.alturaMinima = alturaMinima;
		this.alturaMaxima = alturaMaxima;
		this.pesoMinimo = pesoMinimo;
		this.pesoMaximo = pesoMaximo;
		this.contraindicacionCardiaca = contraindicacionCardiaca;
		this.contraindicacionVertigo = contraindicacionVertigo;
		this.riesgo = riesgo;
	}

	public String getRiesgo() {
		return riesgo;
	}

	public double getAlturaMinima() {
		return alturaMinima;
	}

	public double getAlturaMaxima() {
		return alturaMaxima;
	}

	public double getPesoMinimo() {
		return pesoMinimo;
	}

	public double getPesoMaximo() {
		return pesoMaximo;
	}

	public boolean tieneContraindicacionCardiaca() {
		return contraindicacionCardiaca;
	}

	public boolean tieneContraindicacionVertigo() {
		return contraindicacionVertigo;
	}

	@Override
	public String getTipo() {
		return "Mecánica";
	}

	public boolean puedeIngresar(double altura, double peso, boolean tieneProblemasCardiacos, boolean tieneVertigo) {
		if (altura < this.alturaMinima || altura > this.alturaMaxima)
			return false;
		if (peso < this.pesoMinimo || peso > this.pesoMaximo)
			return false;
		if (this.contraindicacionCardiaca && tieneProblemasCardiacos)
			return false;
		if (this.contraindicacionVertigo && tieneVertigo)
			return false;
		return true;
	}
}