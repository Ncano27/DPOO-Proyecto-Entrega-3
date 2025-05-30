package modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.text.NumberFormat;
import java.util.Locale;

public class Tiquete implements Serializable {
	private static final long serialVersionUID = 6L;
	private static int contadorIdGlobal = 1;

	private String id;
	private String tipo;
	private boolean deTemporada;
	private LocalDate fechaInicioTemporada;
	private LocalDate fechaFinTemporada;
	private boolean usado;
	private boolean tieneFastPass;
	private LocalDate fechaEspecificaFastPass;
	private String atraccionEspecifica;
	private LocalDate fechaCreacion;
	private int numeroDeImpresiones;
	private double precio;

	public Tiquete(String tipo, boolean deTemporada, LocalDate fechaInicioTemporada, LocalDate fechaFinTemporada,
			boolean tieneFastPass, LocalDate fechaEspecificaFastPass, String atraccionEspecifica) {
		this.id = "TQ-" + contadorIdGlobal++;
		this.tipo = tipo;
		this.deTemporada = deTemporada;
		this.fechaInicioTemporada = fechaInicioTemporada;
		this.fechaFinTemporada = fechaFinTemporada;
		this.tieneFastPass = tieneFastPass;
		this.fechaEspecificaFastPass = (tieneFastPass && fechaEspecificaFastPass != null) ? fechaEspecificaFastPass
				: null;
		this.atraccionEspecifica = atraccionEspecifica;
		this.usado = false;
		this.fechaCreacion = LocalDate.now();
		this.numeroDeImpresiones = 0;
		this.precio = calcularPrecioEsteTiquete();
	}

	private double calcularPrecioEsteTiquete() {
		if (this.tipo == null)
			return 0.0;
		String tipoLower = this.tipo.toLowerCase();

		if (tipoLower.equals("basico")) {
			return 35000;
		} else if (tipoLower.equals("fastpass")) {
			return 45000;
		} else if (tipoLower.contains("familiar")) {
			return 55000;
		} else if (tipoLower.contains("oro")) {
			return 60000;
		} else if (tipoLower.contains("diamante")) {
			return 65000;
		}
		System.err.println("Advertencia: Tipo de tiquete desconocido '" + this.tipo
				+ "' para cálculo de precio. Asignando precio 0.");
		return 0.0;
	}

	public String getId() {
		return id;
	}

	public String getTipo() {
		return tipo;
	}

	public boolean esDeTemporada() {
		return deTemporada;
	}

	public LocalDate getFechaInicioTemporada() {
		return fechaInicioTemporada;
	}

	public LocalDate getFechaFinTemporada() {
		return fechaFinTemporada;
	}

	public boolean estaUsado() {
		return usado;
	}

	public boolean tieneFastPass() {
		return tieneFastPass;
	}

	public LocalDate getFechaEspecificaFastPass() {
		return fechaEspecificaFastPass;
	}

	public String getAtraccionEspecifica() {
		return atraccionEspecifica;
	}

	public LocalDate getFechaCreacion() {
		return fechaCreacion;
	}

	public int getNumeroDeImpresiones() {
		return numeroDeImpresiones;
	}

	public double getPrecio() {
		return precio;
	}

	public void marcarComoUsado() {
		this.usado = true;
	}

	public boolean estaVigenteHoy() {
		if (usado)
			return false;
		if (deTemporada) {
			if (fechaInicioTemporada == null || fechaFinTemporada == null)
				return false;
			LocalDate hoy = LocalDate.now();
			return !hoy.isBefore(fechaInicioTemporada) && !hoy.isAfter(fechaFinTemporada);
		}
		return true;
	}

	public boolean esFastPassValidoHoy() {
		if (tieneFastPass && fechaEspecificaFastPass != null) {
			return fechaEspecificaFastPass.isEqual(LocalDate.now());
		}
		return false;
	}

	public boolean permiteAcceso(String exclusividadAtraccionDeDestino) {
		if (estaUsado()) {
			return false;
		}
		if (this.atraccionEspecifica != null && this.tipo != null
				&& this.tipo.toLowerCase().startsWith("individual_")) {
			String tipoExclusividadIndividual = this.tipo.substring("individual_".length()).toLowerCase();
			switch (tipoExclusividadIndividual) {
			case "diamante":
				return true;
			case "oro":
				return !exclusividadAtraccionDeDestino.equalsIgnoreCase("Diamante");
			case "familiar":
				return exclusividadAtraccionDeDestino.equalsIgnoreCase("Familiar");
			default:
				return false;
			}
		}
		if (this.tipo == null)
			return false;
		switch (this.tipo.toLowerCase()) {
		case "diamante":
		case "temporada_diamante":
			return true;
		case "oro":
		case "temporada_oro":
			return !exclusividadAtraccionDeDestino.equalsIgnoreCase("Diamante");
		case "familiar":
		case "temporada_familiar":
			return exclusividadAtraccionDeDestino.equalsIgnoreCase("Familiar");
		case "basico":
			return false;
		default:
			return false;
		}
	}

	public void registrarImpresion() {
		this.numeroDeImpresiones++;
	}

	public static void reiniciarContadorGlobal(int siguienteIdAUsar) {
		if (siguienteIdAUsar > 0) {
			contadorIdGlobal = siguienteIdAUsar;
		} else {
			contadorIdGlobal = 1;
		}
	}

	@Override
	public String toString() {
		NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
		currencyFormatter.setMaximumFractionDigits(0);

		StringBuilder sb = new StringBuilder();
		sb.append("Tiquete [ID=").append(id).append(", Tipo=").append(tipo);
		sb.append(", Precio=").append(currencyFormatter.format(precio));
		if (deTemporada) {
			sb.append(", Temporada=").append(fechaInicioTemporada).append(" a ").append(fechaFinTemporada);
		}
		if (atraccionEspecifica != null) {
			sb.append(", Atracción Específica=").append(atraccionEspecifica);
		}
		if (tieneFastPass && fechaEspecificaFastPass != null) {
			sb.append(", FastPass para=").append(fechaEspecificaFastPass);
		}
		sb.append(", Creado=").append(fechaCreacion);
		sb.append(", Usado=").append(usado);
		sb.append(", Impresiones=").append(numeroDeImpresiones);
		sb.append("]");
		return sb.toString();
	}
}