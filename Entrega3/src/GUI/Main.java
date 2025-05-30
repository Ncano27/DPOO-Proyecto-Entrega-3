// Archivo: Main.java
package GUI;

import modelo.Parque;
import modelo.Usuario; // Necesario para el stream y chequeo de rol
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane; // Necesario para los diálogos gráficos

public class Main {
    private static final String NOMBRE_ARCHIVO_DATOS = "datos_parque.dat";

    public static void main(String[] args) {
        Parque parque = Parque.cargarDatos(NOMBRE_ARCHIVO_DATOS);

        // Verificar si existe un administrador. Si no, crear uno usando JOptionPane.
        boolean adminExists = false;
        if (parque.getUsuarios() != null) {
            adminExists = parque.getUsuarios().stream()
                                .anyMatch(u -> u.getRol().equalsIgnoreCase("Administrador"));
        }

        if (!adminExists) {
            JOptionPane.showMessageDialog(null, 
                "No se encontró un usuario Administrador en los datos.\nSe procederá a crear un administrador inicial.", 
                "Configuración Inicial Requerida", JOptionPane.INFORMATION_MESSAGE);
            
            String adminUser = null, adminPass = null, adminEmail = null;

            // Pedir Username
            while (adminUser == null || adminUser.trim().isEmpty()) {
                adminUser = JOptionPane.showInputDialog(null, "Ingrese Username para el nuevo Administrador:", "Crear Admin Inicial", JOptionPane.QUESTION_MESSAGE);
                if (adminUser == null) { // Usuario presionó Cancelar o cerró el diálogo
                    JOptionPane.showMessageDialog(null, "La creación del administrador es necesaria para continuar. La aplicación se cerrará.", "Error Crítico", JOptionPane.ERROR_MESSAGE);
                    System.exit(0); // Salir si se cancela la creación del primer admin
                }
                if (adminUser.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "El username no puede estar vacío.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
                }
                // Verificar si este username ya existe (aunque no debería si no hay admins y la lista de usuarios está vacía)
                else if (parque.buscarUsuarioPorUsername(adminUser.trim()) != null) {
                     JOptionPane.showMessageDialog(null, "Ese username ya existe. Intente con otro.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
                     adminUser = null; // Forzar que se pida de nuevo
                }
            }
            adminUser = adminUser.trim();

            // Pedir Password
            while (adminPass == null || adminPass.trim().isEmpty()) {
                adminPass = JOptionPane.showInputDialog(null, "Ingrese Password para el admin '" + adminUser + "':", "Crear Admin Inicial", JOptionPane.QUESTION_MESSAGE);
                if (adminPass == null) {
                    JOptionPane.showMessageDialog(null, "La creación del administrador es necesaria. La aplicación se cerrará.", "Error Crítico", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
                if (adminPass.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "El password no puede estar vacío.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
                }
            }
            adminPass = adminPass.trim();

            // Pedir Email
            while (adminEmail == null || adminEmail.trim().isEmpty() || !(adminEmail.contains("@") && adminEmail.contains("."))) {
                adminEmail = JOptionPane.showInputDialog(null, "Ingrese Email para el admin '" + adminUser + "':", "Crear Admin Inicial", JOptionPane.QUESTION_MESSAGE);
                if (adminEmail == null) {
                    JOptionPane.showMessageDialog(null, "La creación del administrador es necesaria. La aplicación se cerrará.", "Error Crítico", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
                adminEmail = adminEmail.trim();
                if (adminEmail.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "El email no puede estar vacío.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
                } else if (!(adminEmail.contains("@") && adminEmail.contains("."))) {
                    JOptionPane.showMessageDialog(null, "Formato de email inválido. Intente de nuevo.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
                } else {
                    // Verificar unicidad de email
                    boolean emailExiste = false;
                    if (parque.getUsuarios() != null) {
                        for(Usuario u : parque.getUsuarios()){
                            if(u.getEmail() != null && u.getEmail().equalsIgnoreCase(adminEmail)){
                                emailExiste = true; break;
                            }
                        }
                    }
                    if (emailExiste) {
                        JOptionPane.showMessageDialog(null, "Ese email ya está registrado. Intente con otro.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
                        adminEmail = null; // Forzar que se pida de nuevo
                    }
                }
            }

            if (parque.registrarCliente(adminUser, "Administrador", adminPass, adminEmail)) {
                JOptionPane.showMessageDialog(null, "Administrador inicial '" + adminUser + "' creado exitosamente.\nLa aplicación ahora se iniciará.", "Admin Creado", JOptionPane.INFORMATION_MESSAGE);
                parque.guardarDatos(NOMBRE_ARCHIVO_DATOS); // Guardar inmediatamente el nuevo admin
            } else {
                // Esto podría pasar si, a pesar de las validaciones, registrarCliente falla por alguna razón interna
                JOptionPane.showMessageDialog(null, "ERROR CRÍTICO: No se pudo crear el administrador inicial.\nLa aplicación se cerrará.", "Error Fatal", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }

        // Usar una variable final para la expresión lambda
        final Parque parqueFinalParaGUI = parque;
        SwingUtilities.invokeLater(() -> {
            new VentanaPrincipal(parqueFinalParaGUI).setVisible(true);
        });
    }
}