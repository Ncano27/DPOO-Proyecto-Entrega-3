package consola;

import modelo.Parque;
import modelo.Usuario;
import modelo.Atraccion;
import modelo.Empleado;
import modelo.Tiquete; 
import java.util.List;
import java.util.Scanner;
import java.util.Arrays;

public class AdminConsola {

    private static Parque parque;
    private static final String NOMBRE_ARCHIVO_DATOS = "datos_parque.dat";
    private static Scanner scanner;
    private static Usuario adminLogueado;

    public static void main(String[] args) {
        parque = Parque.cargarDatos(NOMBRE_ARCHIVO_DATOS);
        scanner = new Scanner(System.in);
        System.out.println("=== Consola de Administrador ===");

        if (parque.getUsuarios().stream().noneMatch(u -> u.getRol().equalsIgnoreCase("Administrador"))) {
            System.out.println("No se encontró un usuario Administrador. Creando uno por defecto...");
            System.out.println("Por favor, establezca credenciales para el primer Administrador:");
            String adminUser = leerStringNoVacio("Username para Admin: ");
            String adminPass = leerStringNoVacio("Password para Admin: ");
            String adminEmail;
            while (true) { System.out.print("Email para Admin: ");
                adminEmail = scanner.nextLine().trim();
                if (adminEmail.contains("@") && adminEmail.contains(".")) {
                    break;
                }
                System.out.println("Formato de email inválido. Intente de nuevo.");
            }
            parque.registrarCliente(adminUser, "Administrador", adminPass, adminEmail);
            System.out.println("Administrador por defecto '" + adminUser + "' creado.");
        }

        if (autenticarAdmin()) {
            mostrarMenuAdmin();
        } else {
            System.out.println("Autenticación fallida. Saliendo de la aplicación.");
        }

        parque.guardarDatos(NOMBRE_ARCHIVO_DATOS);
        System.out.println("\nSesión de administrador finalizada. Datos guardados.");
        scanner.close();
    }

    private static boolean autenticarAdmin() {
        System.out.println("\n--- Inicio de Sesión Administrador ---");
        for (int i = 0; i < 3; i++) {
            System.out.print("Username: "); String username = scanner.nextLine();
            System.out.print("Password: "); String password = scanner.nextLine();
            adminLogueado = parque.autenticarUsuario(username, password);
            if (adminLogueado != null && adminLogueado.getRol().equalsIgnoreCase("Administrador")) {
                System.out.println("Login exitoso. ¡Bienvenido, " + adminLogueado.getUsername() + "!");
                return true;
            }
            System.out.println("Credenciales incorrectas o rol no es Administrador. Intentos restantes: " + (2 - i));
        }
        return false;
    }

    private static void mostrarMenuAdmin() {
        int opcion = -1; 
        do {
            System.out.println("\n--- Menú Administrador (" + adminLogueado.getUsername() + ") ---");
            System.out.println("1. Gestionar Atracciones");
            System.out.println("2. Gestionar Empleados");
            System.out.println("3. Gestionar Cuentas de Usuario");
            System.out.println("4. Ver Reportes Simples");
            System.out.println("0. Salir y Guardar");
            System.out.print("Seleccione una opción: ");
            
            String input = scanner.nextLine();
            try {
                opcion = Integer.parseInt(input);
                switch (opcion) {
                    case 1: menuGestionAtracciones(); break;
                    case 2: menuGestionEmpleados(); break;
                    case 3: menuGestionUsuarios(); break;
                    case 4: verReportesSimples(); break; 
                    case 0: System.out.println("Guardando cambios y saliendo..."); break;
                    default: System.out.println("Opción no válida. Intente de nuevo.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Por favor ingrese un número válido para la opción.");
                opcion = -1; 
            }
        } while (opcion != 0);
    }

   
    private static String leerStringNoVacio(String mensaje) {
        String input;
        while (true) {
            System.out.print(mensaje);
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) { return input; }
            System.out.println("Este campo no puede estar vacío. Intente de nuevo.");
        }
    }
    
    private static String leerEmailValido(String mensaje, boolean verificarUnicidad) {
        String email;
        while (true) {
            email = leerStringNoVacio(mensaje);
            if (email.contains("@") && email.contains(".")) {
                if (!verificarUnicidad) { return email; }
                boolean emailExiste = false;
                if (parque != null && parque.getUsuarios() != null) { 
                    for (Usuario u : parque.getUsuarios()) {
                        if (u.getEmail() != null && u.getEmail().equalsIgnoreCase(email)) {
                            emailExiste = true;
                            break;
                        }
                    }
                }
                if (!emailExiste) { return email; }
                else { System.out.println("Error: El email '" + email + "' ya está registrado. Intente con otro."); }
            } else { System.out.println("Formato de email inválido. Intente de nuevo."); }
        }
    }

    private static int leerIntConRango(String mensaje, int min, int max) {
        int numero;
        while (true) {
            try {
                System.out.print(mensaje);
                numero = Integer.parseInt(scanner.nextLine());
                if (numero >= min && numero <= max) { return numero; }
                System.out.println("Error: El número debe estar entre " + min + " y " + max + ". Intente de nuevo.");
            } catch (NumberFormatException e) {
                System.out.println("Error: Entrada inválida. Debe ingresar un número entero. Intente de nuevo.");
            }
        }
    }
    
    private static double leerDoubleConRango(String mensaje, double min, double max) {
        double numero;
        while (true) {
            try {
                System.out.print(mensaje);
                numero = Double.parseDouble(scanner.nextLine());
                if (numero >= min && numero <= max) { return numero; }
                System.out.println("Error: El número debe estar entre " + min + " y " + max + ". Intente de nuevo.");
            } catch (NumberFormatException e) {
                System.out.println("Error: Entrada inválida. Debe ingresar un número. Intente de nuevo.");
            }
        }
    }

    private static boolean leerBoolean(String mensaje) {
        while (true) {
            System.out.print(mensaje + " (si/no o true/false): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("true") || input.equals("si") || input.equals("s")) { return true; }
            else if (input.equals("false") || input.equals("no") || input.equals("n")) { return false; }
            System.out.println("Error: Entrada inválida. Responda 'true'/'false' o 'si'/'no'. Intente de nuevo.");
        }
    }
    
    private static String leerOpcionValida(String mensaje, List<String> opcionesValidas, boolean caseSensitive) {
        String input;
        while (true) {
            System.out.print(mensaje + " Opciones: " + opcionesValidas + ": ");
            input = scanner.nextLine().trim();
            for (String opcionValida : opcionesValidas) {
                if (caseSensitive) {
                    if (opcionValida.equals(input)) return input;
                } else {
                    if (opcionValida.equalsIgnoreCase(input)) return opcionValida; 
                }
            }
            System.out.println("Error: Opción no válida. Por favor, elija una de la lista. Intente de nuevo.");
        }
    }

    
    private static void menuGestionAtracciones() {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n--- Gestionar Atracciones ---");
            System.out.println("1. Agregar Atracción Cultural");
            System.out.println("2. Agregar Atracción Mecánica");
            System.out.println("3. Listar Todas las Atracciones");
            System.out.println("4. Activar/Desactivar Atracción");
            System.out.println("0. Volver al Menú Principal");
            System.out.print("Seleccione una opción: ");
            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1": agregarAtraccionCulturalConsola(); break;
                case "2": agregarAtraccionMecanicaConsola(); break;
                case "3": listarAtraccionesConsola(); break;
                case "4": activarDesactivarAtraccionConsola(); break;
                case "0": continuar = false; break;
                default: System.out.println("Opción no válida.");
            }
        }
    }

    private static void agregarAtraccionCulturalConsola() {
        System.out.println("\n-- Agregar Atracción Cultural --");
        String nombre = leerStringNoVacio("Nombre: ");
        int capMax = leerIntConRango("Capacidad Máxima: ", 1, 10000);
        int empMin = leerIntConRango("Empleados Mínimos: ", 0, 100);
        String ubicacion = leerStringNoVacio("Ubicación: ");
        String exclusividad = leerOpcionValida("Exclusividad ", Arrays.asList("Familiar", "Oro", "Diamante"), false);
        int edadMin = leerIntConRango("Edad Mínima: ", 0, 100);

        if (parque.agregarAtraccionCultural(nombre, capMax, empMin, ubicacion, exclusividad, edadMin)) {
            System.out.println("Atracción cultural '" + nombre + "' agregada.");
        }
    }
    
    private static void agregarAtraccionMecanicaConsola() {
        System.out.println("\n-- Agregar Atracción Mecánica --");
        String nombre = leerStringNoVacio("Nombre: ");
        int capMax = leerIntConRango("Capacidad Máxima: ", 1, 10000);
        int empMin = leerIntConRango("Empleados Mínimos: ", 0, 100);
        String ubicacion = leerStringNoVacio("Ubicación: ");
        String exclusividad = leerOpcionValida("Exclusividad ", Arrays.asList("Familiar", "Oro", "Diamante"), false);
        
        double altMin = leerDoubleConRango("Altura Mínima (metros, ej: 1.40): ", 0.5, 2.5);
        double altMax;
        while (true) {
            altMax = leerDoubleConRango("Altura Máxima (metros, ej: 2.00): ", altMin, 3.0);
            if (altMax >= altMin) break;
            System.out.println("Error: La altura máxima debe ser mayor o igual a la altura mínima.");
        }
        
        double pesoMin = leerDoubleConRango("Peso Mínimo (kg, ej: 40.0): ", 20.0, 150.0);
        double pesoMax;
        while(true){
            pesoMax = leerDoubleConRango("Peso Máximo (kg, ej: 110.0): ", pesoMin, 200.0);
             if (pesoMax >= pesoMin) break;
            System.out.println("Error: El peso máximo debe ser mayor o igual al peso mínimo.");
        }

        boolean pCard = leerBoolean("¿Contraindicación Cardíaca?");
        boolean pVert = leerBoolean("¿Contraindicación Vértigo?");
        String riesgo = leerOpcionValida("Riesgo ", Arrays.asList("Bajo", "Medio", "Alto"), false);

        if (parque.agregarAtraccionMecanica(nombre, capMax, empMin, ubicacion, exclusividad, altMin, altMax, pesoMin, pesoMax, pCard, pVert, riesgo)) {
            System.out.println("Atracción mecánica '" + nombre + "' agregada.");
        }
    }

    private static void listarAtraccionesConsola() {
        System.out.println("\n--- Lista de Atracciones ---");
        List<Atraccion> atracciones = parque.getAtracciones();
        if (atracciones.isEmpty()) {
            System.out.println("No hay atracciones registradas en el parque.");
            return;
        }
        for (int i = 0; i < atracciones.size(); i++) {
            System.out.println((i + 1) + ". " + atracciones.get(i).toString());
        }
    }
    
    private static void activarDesactivarAtraccionConsola() {
        System.out.println("\n-- Activar/Desactivar Atracción --");
        listarAtraccionesConsola();
        if (parque.getAtracciones().isEmpty()) return;

        System.out.print("Ingrese el NÚMERO de la atracción a modificar (o 0 para cancelar): ");
        int numAtraccion = leerIntConRango("", 0, parque.getAtracciones().size());
        
        if (numAtraccion == 0) { System.out.println("Operación cancelada."); return; }
        
        Atraccion atraccion = parque.getAtracciones().get(numAtraccion - 1);

        System.out.println("Atracción seleccionada: " + atraccion.getNombre() + ", Estado actual: " + (atraccion.estaActiva() ? "Activa" : "Inactiva"));
        String opcion = leerOpcionValida("Nuevo estado ", Arrays.asList("1", "2", "0"), true); // 1:Activar, 2:Desactivar, 0:Cancelar
        
        if (opcion.equals("1")) {
            atraccion.activar(); System.out.println("Atracción '" + atraccion.getNombre() + "' activada.");
        } else if (opcion.equals("2")) {
            atraccion.desactivar(); System.out.println("Atracción '" + atraccion.getNombre() + "' desactivada.");
        } else { // opcion "0"
            System.out.println("Operación cancelada.");
        }
    }
    
    private static void menuGestionEmpleados() {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n--- Gestionar Empleados ---");
            System.out.println("1. Agregar Nuevo Empleado");
            System.out.println("2. Listar Empleados");
            System.out.println("3. Modificar Tipo de Empleado");
            System.out.println("4. Asignar Capacitación a Empleado");
            System.out.println("0. Volver al Menú Principal");
            System.out.print("Seleccione una opción: ");
            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1": agregarEmpleadoConsola(); break;
                case "2": listarEmpleadosConsola(); break;
                case "3": modificarTipoEmpleadoConsola(); break;
                case "4": asignarCapacitacionEmpleadoConsola(); break;
                case "0": continuar = false; break;
                default: System.out.println("Opción no válida.");
            }
        }
    }

    private static void agregarEmpleadoConsola() {
        System.out.println("\n--- Agregar Nuevo Empleado ---");
        String nombre = leerStringNoVacio("Nombre completo del empleado: ");
        String id = leerStringNoVacio("ID del empleado (será su username): ");
        
        if (parque.buscarEmpleadoPorId(id) != null) {
            System.out.println("Error: Ya existe un empleado con el ID " + id);
            return;
        }
        if (parque.buscarUsuarioPorUsername(id) != null) {
            System.out.println("Error: El ID '" + id + "' ya está en uso como username de otro usuario.");
            return;
        }

        List<String> tiposEmpleadoValidos = Arrays.asList("Operador", "Cajero", "Cocinero", "Servicio General");
        String tipoEmpleado = leerOpcionValida("Tipo de empleado ", tiposEmpleadoValidos, false);

        System.out.println("\n-- Creación de Cuenta de Usuario para el Empleado --");
        String username = id; 
        String password = leerStringNoVacio("Password para " + nombre + ": ");
        String email = leerEmailValido("Email para " + nombre + " (debe ser único): ", true);
        String rolUsuario = tipoEmpleado; 
        
        if (parque.registrarCliente(username, rolUsuario, password, email)) {
            Empleado nuevoEmpleado = new Empleado(nombre, id, tipoEmpleado);
            if (parque.agregarEmpleado(nuevoEmpleado)) {
                 System.out.println("Empleado '" + nombre + "' y su cuenta de usuario creados exitosamente.");
            } else {
                System.out.println("Error inesperado: No se pudo agregar el empleado al parque después de crear el usuario.");
            }
        } else {
            System.out.println("Error al crear la cuenta de usuario (username o email podrían ya existir). El empleado NO fue agregado.");
        }
    }

    private static void listarEmpleadosConsola() {
        System.out.println("\n--- Lista de Empleados ---");
        List<Empleado> empleados = parque.getEmpleados();
        if (empleados.isEmpty()) {
            System.out.println("No hay empleados registrados.");
            return;
        }
        for (int i = 0; i < empleados.size(); i++) {
            System.out.println((i + 1) + ". " + empleados.get(i).toString());
        }
    }

    private static void modificarTipoEmpleadoConsola() {
        System.out.println("\n--- Modificar Tipo de Empleado ---");
        listarEmpleadosConsola();
        if (parque.getEmpleados().isEmpty()) return;

        System.out.print("Ingrese el NÚMERO del empleado a modificar (o 0 para cancelar): ");
        int numEmp = leerIntConRango("", 0, parque.getEmpleados().size());
        
        if (numEmp == 0) { System.out.println("Operación cancelada."); return; }

        Empleado empleadoAModificar = parque.getEmpleados().get(numEmp - 1);
        
        System.out.println("Empleado: " + empleadoAModificar.getNombre() + ", Tipo actual: " + empleadoAModificar.getTipo());
        List<String> tiposEmpleadoValidos = Arrays.asList("Operador", "Cajero", "Cocinero", "Servicio General");
        String nuevoTipo = leerOpcionValida("Nuevo tipo para el empleado ", tiposEmpleadoValidos, false);
        
        empleadoAModificar.setTipo(nuevoTipo);
        
        Usuario usuarioAsociado = parque.buscarUsuarioPorUsername(empleadoAModificar.getId());
        if (usuarioAsociado != null) {
            usuarioAsociado.setRol(nuevoTipo); 
            System.out.println("Rol del usuario asociado también actualizado a '" + nuevoTipo + "'.");
        }
        System.out.println("Tipo del empleado '" + empleadoAModificar.getNombre() + "' actualizado a '" + nuevoTipo + "'.");
    }
    
    private static void asignarCapacitacionEmpleadoConsola() {
        System.out.println("\n--- Asignar Capacitación a Empleado ---");
        listarEmpleadosConsola();
        if (parque.getEmpleados().isEmpty()) return;
        System.out.print("NÚMERO del empleado a capacitar (o 0 para cancelar): ");
        int numEmp = leerIntConRango("", 0, parque.getEmpleados().size());
        if (numEmp == 0) { System.out.println("Cancelado."); return; }
        Empleado empleado = parque.getEmpleados().get(numEmp - 1);

        listarAtraccionesConsola();
        if (parque.getAtracciones().isEmpty()) { System.out.println("No hay atracciones para asignar capacitación."); return;}
        System.out.print("NÚMERO de la atracción para la capacitación (o 0 para cancelar): ");
        int numAtr = leerIntConRango("", 0, parque.getAtracciones().size());
        if (numAtr == 0) { System.out.println("Cancelado."); return; }
        Atraccion atraccion = parque.getAtracciones().get(numAtr - 1);

        empleado.agregarCapacitacion(atraccion.getNombre());
        System.out.println("Empleado '" + empleado.getNombre() + "' capacitado para '" + atraccion.getNombre() + "'.");
    }

    private static void menuGestionUsuarios() {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n--- Gestionar Cuentas de Usuario ---");
            System.out.println("1. Listar Todos los Usuarios");
            System.out.println("2. Buscar Usuario por Username");
            System.out.println("3. Ver Tiquetes de un Usuario");
            System.out.println("4. Modificar Rol de Usuario");
            System.out.println("5. Crear Nuevo Usuario Administrador");
            System.out.println("0. Volver al Menú Principal");
            System.out.print("Seleccione una opción: ");
            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1": listarTodosLosUsuarios(); break;
                case "2": buscarUsuarioPorUsernameConsola(); break;
                case "3": verTiquetesDeUsuarioConsola(); break;
                case "4": modificarRolUsuarioConsola(); break;
                case "5": crearNuevoAdminConsola(); break;
                case "0": continuar = false; break;
                default: System.out.println("Opción no válida.");
            }
        }
    }

    private static void listarTodosLosUsuarios() {
        System.out.println("\n--- Lista de Todos los Usuarios ---");
        List<Usuario> listaUsuarios = parque.getUsuarios();
        if (listaUsuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
            return;
        }
        System.out.println(String.format("%-5s %-20s %-15s %-30s", "No.", "Username", "Rol", "Email"));
        System.out.println(String.format("%-5s %-20s %-15s %-30s", "---", "--------------------", "---------------", "------------------------------"));
        for (int i = 0; i < listaUsuarios.size(); i++) {
            Usuario u = listaUsuarios.get(i);
            System.out.println(String.format("%-5s %-20s %-15s %-30s", (i + 1) + ".", u.getUsername(), u.getRol(), u.getEmail()));
        }
    }

    private static void buscarUsuarioPorUsernameConsola() {
        System.out.println("\n--- Buscar Usuario por Username ---");
        String username = leerStringNoVacio("Username del usuario a buscar: ");
        Usuario encontrado = parque.buscarUsuarioPorUsername(username); 
        if (encontrado != null) {
            System.out.println("Usuario Encontrado: " + encontrado.toString());
            System.out.println("Tiquetes:");
            if (encontrado.getTiquetes() == null || encontrado.getTiquetes().isEmpty()) {
                System.out.println("  (No tiene tiquetes)");
            } else {
                for(Tiquete t : encontrado.getTiquetes()) {
                    System.out.println("  - " + t.toString());
                }
            }
        } else {
            System.out.println("Usuario '" + username + "' no encontrado.");
        }
    }

    private static void verTiquetesDeUsuarioConsola() {
        System.out.println("\n--- Ver Tiquetes de un Usuario ---");
        listarTodosLosUsuarios();
        if (parque.getUsuarios().isEmpty()) return;
        System.out.print("NÚMERO del usuario para ver sus tiquetes (o 0 para cancelar): ");
        int numUser = leerIntConRango("", 0, parque.getUsuarios().size());
        if (numUser == 0) { System.out.println("Cancelado."); return; }
        
        Usuario usuarioSeleccionado = parque.getUsuarios().get(numUser - 1);
        
        System.out.println("Tiquetes de " + usuarioSeleccionado.getUsername() + " (Rol: " + usuarioSeleccionado.getRol() + "):");
        List<Tiquete> tiquetesUsuario = usuarioSeleccionado.getTiquetes();
        if (tiquetesUsuario == null || tiquetesUsuario.isEmpty()) {
            System.out.println("Este usuario no tiene tiquetes.");
            return;
        }
        for (Tiquete t : tiquetesUsuario) {
            System.out.println("- " + t.toString());
        }
    }

    private static void modificarRolUsuarioConsola() {
        System.out.println("\n--- Modificar Rol de Usuario ---");
        listarTodosLosUsuarios();
        if (parque.getUsuarios().isEmpty()) return;
        System.out.print("NÚMERO del usuario a modificar (o 0 para cancelar): ");
        int numUser = leerIntConRango("", 0, parque.getUsuarios().size());
        if (numUser == 0) { System.out.println("Cancelado."); return; }

        Usuario usuarioAModificar = parque.getUsuarios().get(numUser - 1);

        System.out.println("Usuario: " + usuarioAModificar.getUsername() + ", Rol actual: " + usuarioAModificar.getRol());
        List<String> rolesValidos = Arrays.asList("Cliente", "Administrador", "Operador", "Cajero", "Cocinero", "Servicio General"); 
        String nuevoRol = leerOpcionValida("Nuevo rol para el usuario ", rolesValidos, false);
        String antiguoRol = usuarioAModificar.getRol();
        usuarioAModificar.setRol(nuevoRol); 
        System.out.println("Rol del usuario '" + usuarioAModificar.getUsername() + "' actualizado a '" + nuevoRol + "'.");
        Empleado empAsociado = parque.buscarEmpleadoPorId(usuarioAModificar.getUsername()); 
        
        if (esTipoDeEmpleado(nuevoRol)) { 
            if (empAsociado == null) { 
                Empleado nuevoEmpleado = new Empleado(usuarioAModificar.getUsername(), usuarioAModificar.getUsername(), nuevoRol); 
                parque.agregarEmpleado(nuevoEmpleado); 
                System.out.println("Perfil de empleado creado/actualizado para " + usuarioAModificar.getUsername() + " con tipo " + nuevoRol);
            } else if (!empAsociado.getTipo().equalsIgnoreCase(nuevoRol)) { 
                empAsociado.setTipo(nuevoRol);
                System.out.println("Tipo del empleado asociado también actualizado a: " + nuevoRol);
            }
        } else if (empAsociado != null && esTipoDeEmpleado(antiguoRol)) { 
        	parque.getEmpleados().remove(empAsociado);
            System.out.println("Advertencia: El usuario ya no tiene un rol de empleado, pero su perfil de empleado aún existe con tipo '" + empAsociado.getTipo() + "'. Considere la gestión de este caso.");
        }
    }

    private static void crearNuevoAdminConsola() {
        System.out.println("\n--- Crear Nuevo Usuario Administrador ---");
        String username = leerStringNoVacio("Username para nuevo Admin (debe ser único): ");
        
        if (parque.buscarUsuarioPorUsername(username) != null) {
            System.out.println("Error: El username '" + username + "' ya existe. Intente con otro.");
            return;
        }
        String password = leerStringNoVacio("Password para nuevo Admin: ");
        String email = leerEmailValido("Email para nuevo Admin (debe ser único): ", true);

        if (parque.registrarCliente(username, "Administrador", password, email)) {
            System.out.println("Nuevo usuario Administrador '" + username + "' creado exitosamente.");
            Empleado adminEmpleado = new Empleado(username, username, "Administrador"); // Asumiendo nombre=username, id=username
            parque.agregarEmpleado(adminEmpleado);
        } else {
            System.out.println("Error al crear nuevo Administrador (el username o email podrían ya existir pese a la verificación previa).");
        }
    }
    
    private static boolean esTipoDeEmpleado(String rol) {
        List<String> tiposEmpleadoValidos = Arrays.asList("Operador", "Cajero", "Cocinero", "Servicio General"); // No incluir "Administrador" aquí si no es un tipo de Empleado.
        return tiposEmpleadoValidos.stream().anyMatch(tipo -> tipo.equalsIgnoreCase(rol));
    }
    
    private static void verReportesSimples() {
        System.out.println("\n--- Reportes Simples ---");
        System.out.println("Total de atracciones: " + parque.getAtracciones().size());
        long atraccionesActivas = parque.getAtracciones().stream().filter(Atraccion::estaActiva).count();
        System.out.println("  - Activas: " + atraccionesActivas);
        System.out.println("  - Inactivas: " + (parque.getAtracciones().size() - atraccionesActivas));
        
        System.out.println("Total de usuarios registrados: " + parque.getUsuarios().size());
        parque.getUsuarios().stream()
            .map(Usuario::getRol)
            .distinct()
            .forEach(rol -> {
                long count = parque.getUsuarios().stream().filter(u -> u.getRol().equalsIgnoreCase(rol)).count();
                System.out.println("  - Usuarios con rol '" + rol + "': " + count);
            });
        
        System.out.println("Total de empleados: " + parque.getEmpleados().size());
        parque.getEmpleados().stream()
            .map(Empleado::getTipo)
            .distinct()
            .forEach(tipo -> {
                long count = parque.getEmpleados().stream().filter(e -> e.getTipo().equalsIgnoreCase(tipo)).count();
                 if(count > 0) System.out.println("  - Empleados tipo '" + tipo + "': " + count);
            });
        
        System.out.println("Total de tiquetes vendidos (histórico): " + parque.getTiquetes().size());
    }
}