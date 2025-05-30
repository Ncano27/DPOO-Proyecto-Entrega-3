package consola;

import modelo.Parque;
import modelo.Usuario;
import modelo.Atraccion;
import modelo.AtraccionCultural;
import modelo.AtraccionMecanica;
import modelo.Tiquete;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.Arrays;

public class ClienteConsola {

	private static Parque parque;
	private static final String NOMBRE_ARCHIVO_DATOS = "datos_parque.dat";
	private static Scanner scanner;
	private static Usuario clienteLogueado;

	private static String leerStringNoVacio(String mensaje) {
		String input;
		while (true) {
			System.out.print(mensaje);
			input = scanner.nextLine().trim();
			if (!input.isEmpty()) {
				return input;
			}
			System.out.println("Este campo no puede estar vacío. Intente de nuevo.");
		}
	}

	private static String leerEmailValido(String mensaje, boolean verificarUnicidad) {
		String email;
		while (true) {
			email = leerStringNoVacio(mensaje);
			if (email.contains("@") && email.contains(".")) {
				if (!verificarUnicidad) {
					return email;
				}
				boolean emailExiste = false;
				if (parque != null && parque.getUsuarios() != null) {
					for (Usuario u : parque.getUsuarios()) {
						if (u.getEmail() != null && u.getEmail().equalsIgnoreCase(email)) {
							emailExiste = true;
							break;
						}
					}
				}
				if (!emailExiste) {
					return email;
				} else {
					System.out.println("Error: El email '" + email + "' ya está registrado. Intente con otro.");
				}
			} else {
				System.out.println("Formato de email inválido. Intente de nuevo.");
			}
		}
	}

	private static int leerIntConRango(String mensaje, int min, int max) {
		int numero;
		while (true) {
			try {
				System.out.print(mensaje);
				numero = Integer.parseInt(scanner.nextLine());
				if (numero >= min && numero <= max) {
					return numero;
				}
				System.out.println("Error: El número debe estar entre " + min + " y " + max + ". Intente de nuevo.");
			} catch (NumberFormatException e) {
				System.out.println("Error: Entrada inválida. Debe ingresar un número entero. Intente de nuevo.");
			}
		}
	}

	private static String leerOpcionValida(String mensaje, List<String> opcionesValidas, boolean caseSensitive) {
		String input;
		while (true) {
			System.out.print(mensaje + " Opciones: " + opcionesValidas + ": ");
			input = scanner.nextLine().trim();
			for (String opcionValida : opcionesValidas) {
				if (caseSensitive ? opcionValida.equals(input) : opcionValida.equalsIgnoreCase(input)) {
					return opcionValida;
				}
			}
			System.out.println("Error: Opción no válida. Por favor, elija una de la lista. Intente de nuevo.");
		}
	}

	private static LocalDate leerFechaValida(String mensaje, boolean permitirFechasPasadas) {
		LocalDate fecha;
		while (true) {
			System.out.print(mensaje + " (YYYY-MM-DD): ");
			String fechaStr = scanner.nextLine();
			try {
				fecha = LocalDate.parse(fechaStr);
				if (!permitirFechasPasadas && fecha.isBefore(LocalDate.now())) {
					System.out.println("Error: La fecha no puede ser en el pasado. Intente de nuevo.");
				} else {
					return fecha;
				}
			} catch (DateTimeParseException e) {
				System.out.println("Error: Formato de fecha incorrecto. Use YYYY-MM-DD. Intente de nuevo.");
			}
		}
	}

	private static boolean leerBoolean(String mensaje) {
		while (true) {
			System.out.print(mensaje + " (si/no o true/false): ");
			String input = scanner.nextLine().trim().toLowerCase();
			if (input.equals("true") || input.equals("si") || input.equals("s")) {
				return true;
			} else if (input.equals("false") || input.equals("no") || input.equals("n")) {
				return false;
			}
			System.out.println("Error: Entrada inválida. Responda 'true'/'false' o 'si'/'no'. Intente de nuevo.");
		}
	}

	private static double leerDoubleConRango(String mensaje, double min, double max) {
		double numero;
		while (true) {
			try {
				System.out.print(mensaje);
				numero = Double.parseDouble(scanner.nextLine());
				if (numero >= min && numero <= max) {
					return numero;
				}
				System.out.println("Error: El número debe estar entre " + min + " y " + max + ". Intente de nuevo.");
			} catch (NumberFormatException e) {
				System.out.println("Error: Entrada inválida. Debe ingresar un número. Intente de nuevo.");
			}
		}
	}

	public static void main(String[] args) {
		parque = Parque.cargarDatos(NOMBRE_ARCHIVO_DATOS);
		scanner = new Scanner(System.in);

		System.out.println("=== Bienvenido al Portal de Clientes del Parque de Atracciones ===");
		System.out.println("1. Registrarse como Nuevo Cliente");
		System.out.println("2. Iniciar Sesión");
		System.out.println("0. Salir");

		String opcionInicial = leerOpcionValida("Seleccione una opción ", Arrays.asList("1", "2", "0"), true);

		if (opcionInicial.equals("1")) {
			registrarNuevoCliente();
			System.out.println("\nAhora, por favor inicie sesión para continuar.");
			if (!autenticarCliente()) {
				finalizarSesion();
				return;
			}
		} else if (opcionInicial.equals("2")) {
			if (!autenticarCliente()) {
				finalizarSesion();
				return;
			}
		} else { // Opción "0"
			finalizarSesion();
			return;
		}

		if (clienteLogueado != null) {
			mostrarMenuCliente();
		}
		finalizarSesion();
	}

	private static void finalizarSesion() {
		if (parque != null) {
			parque.guardarDatos(NOMBRE_ARCHIVO_DATOS);
			System.out.println("\nDatos guardados. Gracias por visitar el parque.");
		}
		if (scanner != null) {
			scanner.close();
		}
	}

	private static void registrarNuevoCliente() {
		System.out.println("\n--- Registro de Nuevo Cliente ---");
		String username = leerStringNoVacio("Username (para login): ");
		if (parque.buscarUsuarioPorUsername(username) != null) {
			System.out.println("Error: El username '" + username + "' ya está en uso. Intente con otro.");
			return;
		}
		String password = leerStringNoVacio("Password: ");
		String email = leerEmailValido("Email (ej. tu@correo.com): ", true);

		if (parque.registrarCliente(username, "Cliente", password, email)) {
			System.out.println("¡Registro exitoso! Ahora puedes iniciar sesión.");
		} else {
			System.out.println("Error en el registro. Es posible que el username o email ya existan.");
		}
	}

	private static boolean autenticarCliente() {
		System.out.println("\n--- Inicio de Sesión Cliente ---");
		for (int i = 0; i < 3; i++) { // Permite 3 intentos
			String username = leerStringNoVacio("Username: ");
			String password = leerStringNoVacio("Password: ");
			clienteLogueado = parque.autenticarUsuario(username, password);
			if (clienteLogueado != null && clienteLogueado.getRol().equalsIgnoreCase("Cliente")) {
				System.out.println("\nLogin exitoso. ¡Bienvenido, " + clienteLogueado.getUsername() + "!");
				return true;
			}
			System.out.println("Username o password incorrecto, o rol no es Cliente. Intentos restantes: " + (2 - i));
		}
		return false;
	}

	private static void mostrarMenuCliente() {
		boolean continuar = true;
		while (continuar) {
			System.out.println("\n--- Menú Cliente (" + clienteLogueado.getUsername() + ") ---");
			System.out.println("1. Ver Todas las Atracciones");
			System.out.println("2. Comprar Tiquete General");
			System.out.println("3. Comprar Entrada Individual");
			System.out.println("4. Comprar Tiquete de Temporada");
			System.out.println("5. Comprar FastPass");
			System.out.println("6. Ver Mis Tiquetes");
			System.out.println("7. Intentar Ingresar a una Atracción");
			System.out.println("0. Salir y Guardar");
			System.out.print("Seleccione una opción: ");
			String opcion = scanner.nextLine();

			try {
				switch (opcion) {
				case "1":
					listarAtraccionesCliente();
					break;
				case "2":
					comprarTiqueteGeneralCliente();
					break;
				case "3":
					comprarEntradaIndividualCliente();
					break;
				case "4":
					comprarTiqueteTemporadaCliente();
					break;
				case "5":
					comprarFastPassCliente();
					break;
				case "6":
					verMisTiquetesCliente();
					break;
				case "7":
					intentarIngresarAtraccionCliente();
					break;
				case "0":
					continuar = false;
					break;
				default:
					System.out.println("Opción no válida.");
				}
			} catch (Exception e) {
				System.err.println("Ocurrió un error inesperado: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private static void listarAtraccionesCliente() {
		System.out.println("\n--- Atracciones Disponibles ---");
		List<Atraccion> atracciones = parque.getAtracciones();
		if (atracciones.isEmpty()) {
			System.out.println("Actualmente no hay atracciones para mostrar.");
			return;
		}
		for (int i = 0; i < atracciones.size(); i++) {
			Atraccion a = atracciones.get(i);
			System.out.println((i + 1) + ". " + a.getNombre() + " (Tipo: " + a.getTipo() + ", Exclusividad: "
					+ a.getExclusividad() + (a.estaActiva() ? "" : " - NO DISPONIBLE AHORA") + ")");
		}
	}

	private static void comprarTiqueteGeneralCliente() {
		System.out.println("\n--- Comprar Tiquete General ---");
		List<String> opcionesExclusividad = Arrays.asList("Basico", "Familiar", "Oro", "Diamante");
		String tipoExclusividad = leerOpcionValida("Tipo de exclusividad ", opcionesExclusividad, false);

		int cantidad = leerIntConRango("Cantidad de tiquetes: ", 1, 100);

		if (parque.comprarTiqueteGeneral(clienteLogueado.getEmail(), tipoExclusividad, cantidad)) {
			System.out.println("Compra de tiquete(s) general(es) exitosa.");
		}
	}

	private static void comprarEntradaIndividualCliente() {
		System.out.println("\n--- Comprar Entrada Individual ---");
		listarAtraccionesCliente();
		if (parque.getAtracciones().isEmpty())
			return;
		String nombreAtr = leerStringNoVacio("Nombre de la atracción para la entrada individual: ");
		int cantidad = leerIntConRango("Cantidad de tiquetes: ", 1, 10);
		if (parque.comprarEntradaIndividual(clienteLogueado.getEmail(), nombreAtr, cantidad)) {
			System.out.println("Compra de entrada(s) individual(es) exitosa.");
		}
	}

	private static void comprarTiqueteTemporadaCliente() {
		System.out.println("\n--- Comprar Tiquete de Temporada ---");
		List<String> opcionesExclusividadTemp = Arrays.asList("Familiar", "Oro", "Diamante");
		String tipoExclusividad = leerOpcionValida("Tipo de exclusividad ", opcionesExclusividadTemp, false);

		LocalDate fechaInicio = leerFechaValida("Fecha de inicio ", false);
		LocalDate fechaFin;
		while (true) {
			fechaFin = leerFechaValida("Fecha de fin ", false);
			if (!fechaFin.isBefore(fechaInicio))
				break;
			System.out.println("Error: La fecha de fin no puede ser anterior a la fecha de inicio.");
		}

		int cantidad = leerIntConRango("Cantidad de tiquetes: ", 1, 10);

		if (parque.comprarTiqueteDeTemporada(clienteLogueado.getEmail(), tipoExclusividad, fechaInicio, fechaFin,
				cantidad)) {
			System.out.println("Compra de tiquete(s) de temporada exitosa.");
		}
	}

	private static void comprarFastPassCliente() {
		System.out.println("\n--- Comprar FastPass ---");
		LocalDate diaDelFastPass = leerFechaValida("Fecha para el FastPass ", false);

		int cantidad = leerIntConRango("Cantidad de FastPasses: ", 1, 10);

		if (parque.comprarFastPass(clienteLogueado.getEmail(), diaDelFastPass, cantidad)) {
			System.out.println("Compra de FastPass(es) exitosa.");
		}
	}

	private static void verMisTiquetesCliente() {
		System.out.println("\n--- Mis Tiquetes (" + clienteLogueado.getUsername() + ") ---");
		List<Tiquete> misTiquetes = clienteLogueado.getTiquetes();
		if (misTiquetes == null || misTiquetes.isEmpty()) {
			System.out.println("No tienes tiquetes comprados.");
			return;
		}
		for (int i = 0; i < misTiquetes.size(); i++) {
			System.out.println((i + 1) + ". " + misTiquetes.get(i).toString());
		}
	}

	private static void intentarIngresarAtraccionCliente() {
		System.out.println("\n--- Intentar Ingresar a Atracción ---");
		if (clienteLogueado.getTiquetes() == null || clienteLogueado.getTiquetes().isEmpty()) {
			System.out.println("No tienes tiquetes para usar.");
			return;
		}

		System.out.println("Tus Tiquetes Disponibles:");
		verMisTiquetesCliente();
		System.out.print("Selecciona el NÚMERO del tiquete que deseas usar (o 0 para cancelar): ");
		int numTiquete = leerIntConRango("", 0, clienteLogueado.getTiquetes().size());
		if (numTiquete == 0) {
			System.out.println("Operación cancelada.");
			return;
		}
		Tiquete tiqueteSeleccionado = clienteLogueado.getTiquetes().get(numTiquete - 1);

		System.out.println("\nAtracciones Disponibles para Ingresar:");
		listarAtraccionesCliente();
		if (parque.getAtracciones().isEmpty())
			return;
		System.out.print("Selecciona el NÚMERO de la atracción a la que deseas ingresar (o 0 para cancelar): ");
		int numAtraccion = leerIntConRango("", 0, parque.getAtracciones().size());
		if (numAtraccion == 0) {
			System.out.println("Operación cancelada.");
			return;
		}
		Atraccion atraccionAEntrar = parque.getAtracciones().get(numAtraccion - 1);
		if (parque.visitantePuedeAccederAtraccion(clienteLogueado, tiqueteSeleccionado, atraccionAEntrar)) {
			System.out.println(
					"Validación de tiquete por sistema: OK. Procediendo a chequeos específicos de la atracción...");

			boolean cumpleRequisitosEspecificos = true;
			if (atraccionAEntrar instanceof AtraccionCultural) {
				AtraccionCultural cultural = (AtraccionCultural) atraccionAEntrar;
				if (cultural.getEdadMinima() > 0) {
					int edadVisitante = leerIntConRango(
							"Por favor, ingrese su edad para '" + cultural.getNombre() + "': ", 0, 120);
					if (!cultural.puedeIngresar(edadVisitante)) {
						System.out.println("ACCESO DENEGADO: No cumple el requisito de edad ("
								+ cultural.getEdadMinima() + " años).");
						cumpleRequisitosEspecificos = false;
					}
				}
			} else if (atraccionAEntrar instanceof AtraccionMecanica) {
				AtraccionMecanica mecanica = (AtraccionMecanica) atraccionAEntrar;
				System.out.println(
						"Para ingresar a '" + mecanica.getNombre() + "', necesitamos verificar algunos datos:");
				double altura = leerDoubleConRango("Su altura (metros, ej: 1.70): ", 0.5, 2.5);
				double peso = leerDoubleConRango("Su peso (kg, ej: 65.0): ", 20.0, 200.0);
				boolean pCard = leerBoolean("¿Tiene problemas cardiacos conocidos?");
				boolean pVert = leerBoolean("¿Sufre de vertigo actualmente?");

				if (!mecanica.puedeIngresar(altura, peso, pCard, pVert)) {
					System.out.println(
							"ACCESO DENEGADO: No cumple con los requisitos de seguridad de la atracción mecánica.");
					cumpleRequisitosEspecificos = false;
				}
			}

			if (cumpleRequisitosEspecificos) {
				System.out.println("¡ACCESO CONCEDIDO a " + atraccionAEntrar.getNombre() + "!");
			}
		} else {
			System.out.println("ACCESO DENEGADO por el sistema del parque (ver mensaje anterior).");
		}
	}
}