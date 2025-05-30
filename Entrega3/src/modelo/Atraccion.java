package modelo;

import java.io.Serializable;

public abstract class Atraccion implements Serializable {
	private static final long serialVersionUID = 1L;
	protected String nombre;
	protected int capacidadMaxima;
	protected int empleadosMinimos;
	protected String ubicacion;
	protected String exclusividad;
	protected boolean activa;

	public Atraccion(String nombre, int capacidadMaxima, int empleadosMinimos, String ubicacion, String exclusividad) {
		this.nombre = nombre;
		this.capacidadMaxima = capacidadMaxima;
		this.empleadosMinimos = empleadosMinimos;
		this.ubicacion = ubicacion;
		this.exclusividad = exclusividad;
		this.activa = true;
	}

	public String getNombre() {
		return nombre;
	}

	public String getUbicacion() {
		return ubicacion;
	}

	public int getCapacidadMaxima() {
		return capacidadMaxima;
	}

	public String getExclusividad() {
		return exclusividad;
	}

	public boolean estaActiva() {
		return activa;
	}

	public int getEmpleadosMinimos() {
		return this.empleadosMinimos;
	}

	public void desactivar() {
		this.activa = false;
	}

	public void activar() {
		this.activa = true;
	}

	public abstract String getTipo();

	@Override
	public String toString() {
		return getTipo() + " - " + nombre + " [" + exclusividad + "] en " + ubicacion + " | Cap: " + capacidadMaxima
				+ ", Emp.Min: " + empleadosMinimos + ", Activa: " + activa;
	}
}