package consola;

import modelo.Parque;
import modelo.Usuario;
import modelo.Empleado;
import modelo.Atraccion;
import modelo.AtraccionCultural;
import modelo.AtraccionMecanica;
import modelo.Tiquete;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.Set;

public class EmpleadoConsola {

    private static Parque parque;
    private static final String NOMBRE_ARCHIVO_DATOS = "datos_parque.dat";
    private static Scanner scanner;
    private static Usuario usuarioEmpleadoLogueado; 
    private static Empleado empleadoLogueado;       

    public static void main(String[] args) {
        parque = Parque.cargarDatos(NOMBRE_ARCHIVO_DATOS);
        scanner = new Scanner(System.in);
        System.out.println("=== Consola de Empleado ===");

        if (autenticarEmpleado()) {
            mostrarMenuEmpleado();
        } else {
            System.out.println("Autenticación fallida.");
        }

        parque.guardarDatos(NOMBRE_ARCHIVO_DATOS);
        System.out.println("Sesión de empleado finalizada. Datos guardados.");
        scanner.close();
    }

    private static boolean autenticarEmpleado() {
        System.out.println("--- Inicio de Sesión Empleado ---");
        for (int i = 0; i < 3; i++) {
            System.out.print("Username (ID de empleado u otro identificador de usuario): ");
            String username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();

            usuarioEmpleadoLogueado = parque.autenticarUsuario(username, password);

            if (usuarioEmpleadoLogueado != null) {
                String rolUsuario = usuarioEmpleadoLogueado.getRol();
                if (esRolDeEmpleado(rolUsuario)) {
                    empleadoLogueado = buscarEmpleadoPorId(username);
                    if (empleadoLogueado != null) {
                        System.out.println("Login exitoso. Bienvenido Empleado " + empleadoLogueado.getNombre() + " (Tipo: " + empleadoLogueado.getTipo() + ")");
                        return true;
                    } else {
                        System.out.println("Cuenta de usuario válida, pero no se encontró perfil de empleado asociado.");
                        usuarioEmpleadoLogueado = null;
                    }
                } else {
                    System.out.println("Rol de usuario ('" + rolUsuario + "') no es un rol de empleado válido para esta consola.");
                    usuarioEmpleadoLogueado = null; 
                }
            }
            System.out.println("Username o password incorrecto. Intentos restantes: " + (2 - i));
        }
        return false;
    }
    
    private static boolean esRolDeEmpleado(String rol) {
        return rol.equalsIgnoreCase("Operador") || rol.equalsIgnoreCase("Cajero") ||
               rol.equalsIgnoreCase("Cocinero") || rol.equalsIgnoreCase("Servicio General") ||
               rol.equalsIgnoreCase("Empleado"); }

    private static Empleado buscarEmpleadoPorId(String idEmpleado) {
        for (Empleado e : parque.getEmpleados()) {
            if (e.getId().equalsIgnoreCase(idEmpleado)) {
                return e;
            }
        }
        return null;
    }


    private static void mostrarMenuEmpleado() {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n--- Menú Empleado: " + empleadoLogueado.getNombre() + " (" + empleadoLogueado.getTipo() + ") ---");
            System.out.println("1. Validar Ingreso de Cliente a Atracción ()");
            System.out.println("2. Cambiar Estado de Atracción (Reportar Fuera de Servicio / Reactivar)");
            System.out.println("3. Ver Turnos Asignados()");
            System.out.println("0. Salir y Guardar");
            System.out.print("Seleccione una opción: ");
            String opcion = scanner.nextLine();

            try {
                switch (opcion) {
                    case "1":
                        validarIngresoClienteAtraccion();
                        break;
                    case "2":
                        cambiarEstadoAtraccionEmpleado();
                        break;
                    case "3": 
                    	verTurnosAsignados();
                    	break;
                    case "0":
                        continuar = false;
                        break;
                    default:
                        System.out.println("Opción no válida.");
                }
            } catch (Exception e) {
                 System.err.println("Ocurrió un error: " + e.getMessage());
            }
        }
    }
    
    private static void verTurnosAsignados() {
        System.out.println("\n--- Mis Turnos Asignados ---");
        if (empleadoLogueado == null) {
            System.out.println("Error: No hay un empleado logueado.");
            return;
        }
        Set<String> turnos = empleadoLogueado.getTurnosAsignados(); 
        if (turnos == null || turnos.isEmpty()) {
            System.out.println("No tienes turnos asignados actualmente.");
        } else {
            System.out.println("Tus turnos son:");
            for (String turno : turnos) {
                System.out.println("- " + turno);
            }
        }
    }

    private static void validarIngresoClienteAtraccion() {
        System.out.println("\n--- Validar Ingreso a Atracción ---");
        System.out.print("Email del cliente: "); String emailCliente = scanner.nextLine();
        Usuario cliente = null;
        for(Usuario u : parque.getUsuarios()){
            if(u.getEmail() != null && u.getEmail().equalsIgnoreCase(emailCliente) && u.getRol().equalsIgnoreCase("Cliente")){
                cliente = u;
                break;
            }
        }
        if (cliente == null) { System.out.println("Cliente no encontrado con ese email."); return; }

        if (cliente.getTiquetes() == null || cliente.getTiquetes().isEmpty()) {
            System.out.println("El cliente no tiene tiquetes."); return;
        }
        
        System.out.println("Tiquetes del cliente " + cliente.getUsername() + ":");
        for (int i = 0; i < cliente.getTiquetes().size(); i++) {
            System.out.println((i + 1) + ". " + cliente.getTiquetes().get(i).toString());
        }
        System.out.print("Seleccione el número del tiquete a validar: ");
        int numTiquete;
        try {
            numTiquete = scanner.nextInt(); scanner.nextLine(); // Consume newline
            if (numTiquete < 1 || numTiquete > cliente.getTiquetes().size()) {
                System.out.println("Selección de tiquete inválida."); return;
            }
        } catch (InputMismatchException e) { System.out.println("Entrada inválida."); scanner.nextLine(); return;}
        
        Tiquete tiqueteSeleccionado = cliente.getTiquetes().get(numTiquete - 1);

        System.out.print("Nombre de la atracción a la que intenta ingresar: ");
        String nombreAtr = scanner.nextLine();
        Atraccion atraccionAEntrar = null;
        for(Atraccion a : parque.getAtracciones()){
            if(a.getNombre().equalsIgnoreCase(nombreAtr)){
                atraccionAEntrar = a;
                break;
            }
        }
        if (atraccionAEntrar == null) { System.out.println("Atracción no encontrada."); return; }

        if (parque.visitantePuedeAccederAtraccion(cliente, tiqueteSeleccionado, atraccionAEntrar)) {
            System.out.println("Validación de tiquete por sistema: OK. Procediendo a chequeos de atracción...");
            boolean cumpleRequisitosEspecificos = true;
            if (atraccionAEntrar instanceof AtraccionCultural) {
                AtraccionCultural cultural = (AtraccionCultural) atraccionAEntrar;
                if (cultural.getEdadMinima() > 0) {
                    System.out.print("Edad del visitante: "); int edad = scanner.nextInt(); scanner.nextLine();
                    if (!cultural.puedeIngresar(edad)) {
                        System.out.println("ACCESO DENEGADO: No cumple requisito de edad (" + cultural.getEdadMinima() + ").");
                        cumpleRequisitosEspecificos = false;
                    }
                }
            } else if (atraccionAEntrar instanceof AtraccionMecanica) {
                AtraccionMecanica mecanica = (AtraccionMecanica) atraccionAEntrar;
                System.out.print("Altura del visitante (ej. 1.70): "); double altura = scanner.nextDouble(); scanner.nextLine();
                System.out.print("Peso del visitante (ej. 65.0): "); double peso = scanner.nextDouble(); scanner.nextLine();
                System.out.print("¿Tiene problemas cardiacos? (true/false): "); boolean pCard = scanner.nextBoolean(); scanner.nextLine();
                System.out.print("¿Sufre de vertigo? (true/false): "); boolean pVert = scanner.nextBoolean(); scanner.nextLine();
                if (!mecanica.puedeIngresar(altura, peso, pCard, pVert)) {
                    System.out.println("ACCESO DENEGADO: No cumple requisitos de seguridad de la atracción mecánica.");
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
    
    private static void cambiarEstadoAtraccionEmpleado() {
        System.out.println("\n-- Cambiar Estado de Atracción --");
        System.out.print("Nombre de la atracción a modificar: "); String nombreAtr = scanner.nextLine();
        Atraccion atraccion = null;
        for (Atraccion a : parque.getAtracciones()) {
            if (a.getNombre().equalsIgnoreCase(nombreAtr)) { atraccion = a; break; }
        }
        if (atraccion == null) { System.out.println("Atracción no encontrada."); return; }

        System.out.println("Atracción: " + atraccion.getNombre() + ", Estado: " + (atraccion.estaActiva() ? "Activa" : "Inactiva"));
        System.out.print("Nuevo estado (1: Activar, 2: Desactivar/Reportar Fuera de Servicio, 0: Cancelar): ");
        String opcion = scanner.nextLine();
        if (opcion.equals("1")) {
            atraccion.activar(); System.out.println("Atracción activada.");
        } else if (opcion.equals("2")) {
            atraccion.desactivar(); System.out.println("Atracción desactivada/reportada fuera de servicio.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }
}