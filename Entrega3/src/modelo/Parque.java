package modelo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Parque implements Serializable {
	private static final long serialVersionUID = 7L;

	private List<Atraccion> atracciones;
	private List<Empleado> empleados;
	private List<Tiquete> tiquetes;
	private List<Usuario> usuarios;

	public Parque() {
		atracciones = new ArrayList<>();
		empleados = new ArrayList<>();
		tiquetes = new ArrayList<>();
		usuarios = new ArrayList<>();
	}

	public List<Atraccion> getAtracciones() {
		return atracciones;
	}

	public List<Empleado> getEmpleados() {
		return empleados;
	}

	public List<Tiquete> getTiquetes() {
		return tiquetes;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public boolean registrarCliente(String username, String rol, String password, String email) {
		for (Usuario u : usuarios) {
			if (u.getUsername().equalsIgnoreCase(username)) {
				System.out.println("Error: Username '" + username + "' ya existe.");
				return false;
			}
			if (u.getEmail() != null && u.getEmail().equalsIgnoreCase(email)) {
				System.out.println("Error: Email '" + email + "' ya está registrado para otro usuario.");
				return false;
			}
		}
		Usuario nuevo = new Usuario(username, password, rol, email);
		usuarios.add(nuevo);
		System.out.println("Usuario '" + username + "' (Rol: " + rol + ") registrado exitosamente.");
		return true;
	}

	public Usuario autenticarUsuario(String username, String password) {
		for (Usuario u : usuarios) {
			if (u.getUsername().equalsIgnoreCase(username) && u.autenticar(password)) {
				return u;
			}
		}
		return null;
	}

	/**
	 * Busca un usuario por su username.
	 * 
	 * @param username El username a buscar.
	 * @return El Usuario si se encuentra, o null.
	 */
	public Usuario buscarUsuarioPorUsername(String username) {
		if (username == null || username.trim().isEmpty()) {
			return null;
		}
		for (Usuario u : this.usuarios) {
			if (u.getUsername().equalsIgnoreCase(username)) {
				return u;
			}
		}
		return null;
	}

	public boolean agregarEmpleado(Empleado nuevoEmpleado) {
		if (nuevoEmpleado == null || nuevoEmpleado.getId() == null) {
			System.out.println("Error: No se puede agregar un empleado nulo o sin ID.");
			return false;
		}
		for (Empleado e : this.empleados) {
			if (e.getId().equalsIgnoreCase(nuevoEmpleado.getId())) {
				System.out.println(
						"Error: Ya existe un empleado con el ID '" + nuevoEmpleado.getId() + "'. No se agregó.");
				return false;
			}
		}
		this.empleados.add(nuevoEmpleado);
		System.out.println("Empleado '" + nuevoEmpleado.getNombre() + "' (ID: " + nuevoEmpleado.getId()
				+ ") agregado al sistema del parque.");
		return true;
	}

	public Empleado buscarEmpleadoPorId(String idEmpleado) {
		for (Empleado e : this.empleados) {
			if (e.getId().equalsIgnoreCase(idEmpleado)) {
				return e;
			}
		}
		return null;
	}

	public boolean agregarAtraccionMecanica(String nombre, int capacidadMaxima, int empleadosMinimos, String ubicacion,
			String exclusividad, double alturaMinima, double alturaMaxima, double pesoMinimo, double pesoMaximo,
			boolean contraindicacionCardiaca, boolean contraindicacionVertigo, String riesgo) {
		for (Atraccion a : this.atracciones) {
			if (a.getNombre().equalsIgnoreCase(nombre)) {
				System.out.println("Error: Ya existe una atracción con el nombre '" + nombre + "'. No se agregó.");
				return false;
			}
		}
		AtraccionMecanica nuevaAtraccion = new AtraccionMecanica(nombre, capacidadMaxima, empleadosMinimos, ubicacion,
				exclusividad, alturaMinima, alturaMaxima, pesoMinimo, pesoMaximo, contraindicacionCardiaca,
				contraindicacionVertigo, riesgo);
		this.atracciones.add(nuevaAtraccion);
		System.out.println("Atracción mecánica '" + nombre + "' agregada exitosamente.");
		return true;
	}

	public boolean agregarAtraccionCultural(String nombre, int capacidadMaxima, int empleadosMinimos, String ubicacion,
			String exclusividad, int edadMinima) {
		for (Atraccion a : this.atracciones) {
			if (a.getNombre().equalsIgnoreCase(nombre)) {
				System.out.println("Error: Ya existe una atracción con el nombre '" + nombre + "'. No se agregó.");
				return false;
			}
		}
		AtraccionCultural nuevaAtraccion = new AtraccionCultural(nombre, capacidadMaxima, empleadosMinimos, ubicacion,
				exclusividad, edadMinima);
		this.atracciones.add(nuevaAtraccion);
		System.out.println("Atracción cultural '" + nombre + "' agregada exitosamente.");
		return true;
	}

	private Usuario buscarUsuarioPorEmail(String emailCliente) {
		for (Usuario u : this.usuarios) {
			if (u.getEmail() != null && u.getEmail().equalsIgnoreCase(emailCliente)) {
				return u;
			}
		}
		System.out.println("Error (interno): Cliente con email '" + emailCliente + "' no encontrado.");
		return null;
	}

	public Atraccion buscarAtraccionPorNombre(String nombreAtraccion) {
		for (Atraccion a : this.atracciones) {
			if (a.getNombre().equalsIgnoreCase(nombreAtraccion)) {
				return a;
			}
		}
		System.out.println("Error (interno): Atracción '" + nombreAtraccion + "' no encontrada.");
		return null;
	}

	public Tiquete buscarTiquetePorId(String idTiquete) {
	    if (idTiquete == null || idTiquete.trim().isEmpty()) {
	    	return null;
	    }
	    for (Tiquete t : this.tiquetes) {
	        if (t.getId().equalsIgnoreCase(idTiquete)) {
	            return t;
	        }
	    }
	    return null;
	}
	
	public boolean comprarTiqueteGeneral(String emailCliente, String tipoExclusividadDeseada, int cantidad) {
		if (cantidad <= 0) {
			System.out.println("Error: Cantidad debe ser positiva.");
			return false;
		}
		Usuario cliente = buscarUsuarioPorEmail(emailCliente);
		if (cliente == null)
			return false;

		for (int i = 0; i < cantidad; i++) {
			Tiquete nuevoTiquete = new Tiquete(tipoExclusividadDeseada, false, null, null, false, null, null);
			this.tiquetes.add(nuevoTiquete);
			cliente.agregarTiquete(nuevoTiquete);
		}
		System.out.println(cantidad + " tiquete(s) generales tipo '" + tipoExclusividadDeseada + "' comprados por "
				+ cliente.getUsername());
		return true;
	}

	public boolean comprarEntradaIndividual(String emailCliente, String nombreAtraccionAComprar, int cantidad) {
		if (cantidad <= 0) {
			System.out.println("Error: Cantidad debe ser positiva.");
			return false;
		}
		Usuario cliente = buscarUsuarioPorEmail(emailCliente);
		if (cliente == null)
			return false;
		Atraccion atraccionSeleccionada = buscarAtraccionPorNombre(nombreAtraccionAComprar);
		if (atraccionSeleccionada == null)
			return false;

		if (!atraccionSeleccionada.estaActiva()) {
			System.out.println(
					"Error: La atracción '" + nombreAtraccionAComprar + "' no está activa para venta de entradas.");
			return false;
		}

		String tipoDeTiquete = "Individual_" + atraccionSeleccionada.getExclusividad();
		for (int i = 0; i < cantidad; i++) {
			Tiquete nuevoTiquete = new Tiquete(tipoDeTiquete, false, null, null, false, null,
					atraccionSeleccionada.getNombre());
			this.tiquetes.add(nuevoTiquete);
			cliente.agregarTiquete(nuevoTiquete);
		}
		System.out.println(cantidad + " entrada(s) individual(es) para '" + nombreAtraccionAComprar + "' compradas por "
				+ cliente.getUsername());
		return true;
	}

	public boolean comprarTiqueteDeTemporada(String emailCliente, String tipoExclusividad, LocalDate fechaInicio,
			LocalDate fechaFin, int cantidad) {
		if (cantidad <= 0) {
			System.out.println("Error: Cantidad debe ser positiva.");
			return false;
		}
		Usuario cliente = buscarUsuarioPorEmail(emailCliente);
		if (cliente == null)
			return false;
		if (fechaInicio == null || fechaFin == null || fechaFin.isBefore(fechaInicio)) {
			System.out.println("Error: Fechas de temporada inválidas.");
			return false;
		}
		String tipoDeTiquete = "Temporada_" + tipoExclusividad;
		for (int i = 0; i < cantidad; i++) {
			Tiquete nuevoTiquete = new Tiquete(tipoDeTiquete, true, fechaInicio, fechaFin, false, null, null);
			this.tiquetes.add(nuevoTiquete);
			cliente.agregarTiquete(nuevoTiquete);
		}
		System.out.println(cantidad + " tiquete(s) de temporada tipo '" + tipoExclusividad + "' comprados por "
				+ cliente.getUsername());
		return true;
	}

	public boolean comprarFastPass(String emailCliente, LocalDate diaDelFastPass, int cantidad) {
		if (cantidad <= 0) {
			System.out.println("Error: Cantidad debe ser positiva.");
			return false;
		}
		Usuario cliente = buscarUsuarioPorEmail(emailCliente);
		if (cliente == null)
			return false;
		if (diaDelFastPass == null || diaDelFastPass.isBefore(LocalDate.now())) {
			System.out.println("Error: Fecha para FastPass inválida.");
			return false;
		}
		String tipoDeTiquete = "FastPass";
		for (int i = 0; i < cantidad; i++) {
			Tiquete nuevoTiqueteFastPass = new Tiquete(tipoDeTiquete, false, null, null, true, diaDelFastPass, null);
			this.tiquetes.add(nuevoTiqueteFastPass);
			cliente.agregarTiquete(nuevoTiqueteFastPass);
		}
		System.out.println(
				cantidad + " FastPass para el día " + diaDelFastPass + " comprados por " + cliente.getUsername());
		return true;
	}

	public boolean visitantePuedeAccederAtraccion(Usuario visitante, Tiquete tiquetePresentado,
			Atraccion atraccionAEntrar) {
		if (visitante == null || tiquetePresentado == null || atraccionAEntrar == null) {
			System.out.println("Error de acceso: Datos de entrada nulos.");
			return false;
		}
		if (!tiquetePresentado.estaVigenteHoy()) {
			System.out.println("Acceso denegado a '" + atraccionAEntrar.getNombre() + "': Tiquete (ID: "
					+ tiquetePresentado.getId() + ") no vigente o ya usado.");
			return false;
		}
		if (!atraccionAEntrar.estaActiva()) {
			System.out.println("Acceso denegado: La atracción '" + atraccionAEntrar.getNombre() + "' no está activa.");
			return false;
		}

		boolean accesoPermitidoPorTipoTiquete = false;
		if (tiquetePresentado.getAtraccionEspecifica() != null) {
			if (tiquetePresentado.getAtraccionEspecifica().equalsIgnoreCase(atraccionAEntrar.getNombre())) {
				if (tiquetePresentado.permiteAcceso(atraccionAEntrar.getExclusividad())) {
					accesoPermitidoPorTipoTiquete = true;
					System.out.println("Acceso (basado en tiquete) VALIDADO para '" + atraccionAEntrar.getNombre()
							+ "' con tiquete individual.");
					tiquetePresentado.marcarComoUsado();
				} else {
					System.out.println("Acceso denegado: El tipo del tiquete individual ('"
							+ tiquetePresentado.getTipo() + "') no es compatible con la exclusividad '"
							+ atraccionAEntrar.getExclusividad() + "'.");
				}
			} else {
				System.out.println("Acceso denegado: Este tiquete individual es para la atracción '"
						+ tiquetePresentado.getAtraccionEspecifica() + "', no para '" + atraccionAEntrar.getNombre()
						+ "'.");
			}
		} else {
			if (tiquetePresentado.permiteAcceso(atraccionAEntrar.getExclusividad())) {
				accesoPermitidoPorTipoTiquete = true;
				System.out.println("Acceso (basado en tiquete) VALIDADO para '" + atraccionAEntrar.getNombre()
						+ "' con tiquete general.");
				if (!tiquetePresentado.esDeTemporada() && !tiquetePresentado.estaUsado()) {
					tiquetePresentado.marcarComoUsado();
					System.out.println("Tiquete general (ID: " + tiquetePresentado.getId() + ") marcado como usado.");
				}
			} else {
				System.out.println("Acceso denegado: Tiquete tipo '" + tiquetePresentado.getTipo()
						+ "' no permite acceso a atracciones de exclusividad '" + atraccionAEntrar.getExclusividad()
						+ "'.");
			}
		}

		return accesoPermitidoPorTipoTiquete;
	}

	public boolean tieneEmpleadosDisponibles(Atraccion atraccion) {
		if (atraccion == null)
			return false;
		int empleadosNecesarios = atraccion.getEmpleadosMinimos();
		if (empleadosNecesarios == 0)
			return true;

		int operadoresDisponiblesParaEstaAtraccion = 0;
		for (Empleado e : empleados) {
			if (e.puedeOperar(atraccion.getNombre())) {
				operadoresDisponiblesParaEstaAtraccion++;
			}
		}
		return operadoresDisponiblesParaEstaAtraccion >= empleadosNecesarios;
	}

	public void guardarDatos(String nombreArchivo) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nombreArchivo))) {
			oos.writeObject(this);
			System.out.println("Datos del parque guardados en " + nombreArchivo);
		} catch (IOException e) {
			System.err.println("Error al guardar los datos del parque: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static Parque cargarDatos(String nombreArchivo) {
		Parque parqueCargado = null;
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(nombreArchivo))) {
			parqueCargado = (Parque) ois.readObject();
			System.out.println("Datos del parque cargados desde " + nombreArchivo);

			if (parqueCargado != null && parqueCargado.tiquetes != null && !parqueCargado.tiquetes.isEmpty()) {
				int maxIdNum = 0;
				for (Tiquete t : parqueCargado.tiquetes) {
					if (t.getId() != null && t.getId().startsWith("TQ-")) {
						try {
							int currentIdNum = Integer.parseInt(t.getId().substring(3));
							if (currentIdNum > maxIdNum) {
								maxIdNum = currentIdNum;
							}
						} catch (NumberFormatException | StringIndexOutOfBoundsException e) {
							System.err.println("Advertencia: Tiquete con ID '" + t.getId()
									+ "' no pudo ser procesado para el contador.");
						}
					}
				}
				Tiquete.reiniciarContadorGlobal(maxIdNum + 1);
			} else if (parqueCargado != null) {
				Tiquete.reiniciarContadorGlobal(1);
			}

		} catch (java.io.FileNotFoundException e) {
			System.err.println("Archivo de datos '" + nombreArchivo + "' no encontrado. Se creará un parque nuevo.");

		} catch (IOException e) {
			System.err.println("Error de E/S al cargar los datos del parque: " + e.getMessage());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.err.println("Error al cargar los datos: Definición de clase no encontrada. " + e.getMessage());
			e.printStackTrace();
		}

		if (parqueCargado == null) {
			parqueCargado = new Parque();
			Tiquete.reiniciarContadorGlobal(1);
		}
		return parqueCargado;
	}
}