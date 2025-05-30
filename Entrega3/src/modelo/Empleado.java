package modelo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Empleado implements Serializable {
	private static final long serialVersionUID = 5L;

	protected String nombre;
	protected String id;
	protected String tipo;
	protected Set<String> atraccionesCapacitadas;
	protected Set<String> turnosAsignados;

	public Empleado(String nombre, String id, String tipo) {
		this.nombre = nombre;
		this.id = id;
		this.tipo = tipo;
		this.atraccionesCapacitadas = new HashSet<>();
		this.turnosAsignados = new HashSet<>();
	}

	public String getNombre() {
		return nombre;
	}

	public String getId() {
		return id;
	}

	public String getTipo() {
		return tipo;
	}
	public Set<String> getAtraccionesCapacitadas() {
	    return atraccionesCapacitadas;
	}

	public void setTipo(String nuevoTipo) {
		this.tipo = nuevoTipo;
	}

	public void agregarCapacitacion(String nombreAtraccion) {
		atraccionesCapacitadas.add(nombreAtraccion);
	}

	public boolean puedeOperar(String nombreAtraccion) {
		return atraccionesCapacitadas.contains(nombreAtraccion);
	}

	public void asignarTurno(String turno) {
		turnosAsignados.add(turno);
	}

	public Set<String> getTurnosAsignados() {
		return turnosAsignados;
	}

	public boolean estaAsignadoATurno(String turno) {
		return turnosAsignados.contains(turno);
	}

	public boolean puedeCubrirCaja() {
		return tipo.equalsIgnoreCase("cajero") || tipo.equalsIgnoreCase("cocinero");
	}

	public boolean puedeCocinar() {
		return tipo.equalsIgnoreCase("cocinero");
	}

	@Override
	public String toString() {
		return tipo + " - " + nombre + " (ID: " + id + ")";
	}
}