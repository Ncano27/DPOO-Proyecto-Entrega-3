package GUI;

import modelo.Parque;
import modelo.Usuario;
import modelo.Atraccion;
import modelo.AtraccionCultural;
import modelo.AtraccionMecanica;
import modelo.Empleado;
import modelo.Tiquete;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class VentanaAdmin extends JFrame {
	private static final long serialVersionUID = 1L;
	private Parque parque;
	private Usuario adminLogueado;
	private static final String NOMBRE_ARCHIVO_DATOS = "datos_parque.dat";

	public VentanaAdmin(Parque parque, Usuario adminLogueado) {
		this.parque = parque;
		this.adminLogueado = adminLogueado;
		setTitle("Portal Administrador - " + adminLogueado.getUsername());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(600, 500);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout(10, 10));
		JLabel lblBienvenida = new JLabel("Bienvenido, Admin: " + adminLogueado.getUsername(), SwingConstants.CENTER);
		lblBienvenida.setFont(new Font("Arial", Font.BOLD, 18));
		lblBienvenida.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		add(lblBienvenida, BorderLayout.NORTH);
		JPanel panelBotones = new JPanel(new GridLayout(0, 1, 15, 15));
		panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
		JButton gestionarAtraccionesBtn = new JButton("Gestionar Atracciones");
		JButton gestionarEmpleadosBtn = new JButton("Gestionar Empleados");
		JButton gestionarUsuariosBtn = new JButton("Gestionar Cuentas de Usuario");
		JButton verTiquetesBtn = new JButton("Ver Historial de Tiquetes Vendidos");
		JButton verReportesBtn = new JButton("Ver Reportes Simples");
		gestionarAtraccionesBtn.addActionListener(e -> menuGestionAtraccionesGUI());
		gestionarEmpleadosBtn.addActionListener(e -> menuGestionEmpleadosGUI());
		gestionarUsuariosBtn.addActionListener(e -> menuGestionUsuariosGUI());
		verTiquetesBtn.addActionListener(e -> guiListarTodosLosTiquetes());
		verReportesBtn.addActionListener(e -> guiVerReportesSimples());
		panelBotones.add(gestionarAtraccionesBtn);
		panelBotones.add(gestionarEmpleadosBtn);
		panelBotones.add(gestionarUsuariosBtn);
		panelBotones.add(verTiquetesBtn);
		panelBotones.add(verReportesBtn);
		add(panelBotones, BorderLayout.CENTER);
	}

	private String guiLeerStringNoVacio(String mensajePrompt, String tituloDialogo) {
		String input;
		while (true) {
			input = JOptionPane.showInputDialog(this, mensajePrompt, tituloDialogo, JOptionPane.PLAIN_MESSAGE);
			if (input == null)
				return null;
			input = input.trim();
			if (!input.isEmpty())
				return input;
			JOptionPane.showMessageDialog(this, "Este campo no puede estar vacío.", "Error de Entrada",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private String guiLeerOpcionDeLista(String mensajePrompt, String tituloDialogo, String[] opciones) {
		if (opciones == null || opciones.length == 0) {
			JOptionPane.showMessageDialog(this, "No hay opciones disponibles para seleccionar.", "Error",
					JOptionPane.WARNING_MESSAGE);
			return null;
		}
		return (String) JOptionPane.showInputDialog(this, mensajePrompt, tituloDialogo, JOptionPane.PLAIN_MESSAGE, null,
				opciones, opciones[0]);
	}

	private int guiLeerIntConRango(String mensajePrompt, String tituloDialogo, int min, int max) {
		int numero;
		while (true) {
			String input = JOptionPane.showInputDialog(this, mensajePrompt, tituloDialogo, JOptionPane.PLAIN_MESSAGE);
			if (input == null)
				return Integer.MIN_VALUE;
			try {
				numero = Integer.parseInt(input.trim());
				if (numero >= min && numero <= max)
					return numero;
				JOptionPane.showMessageDialog(this, "El número debe estar entre " + min + " y " + max + ".",
						"Error de Entrada", JOptionPane.ERROR_MESSAGE);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(this, "Entrada inválida. Debe ingresar un número entero.",
						"Error de Formato", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private double guiLeerDoubleConRango(String mensajePrompt, String tituloDialogo, double min, double max) {
		double numero;
		while (true) {
			String input = JOptionPane.showInputDialog(this, mensajePrompt, tituloDialogo, JOptionPane.PLAIN_MESSAGE);
			if (input == null)
				return Double.NaN;
			try {
				numero = Double.parseDouble(input.trim());
				if (numero >= min && numero <= max)
					return numero;
				JOptionPane.showMessageDialog(this, "El número debe estar entre " + min + " y " + max + ".",
						"Error de Entrada", JOptionPane.ERROR_MESSAGE);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(this, "Entrada inválida. Debe ingresar un número.", "Error de Formato",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private boolean guiLeerBoolean(String mensajePrompt, String tituloDialogo) {
		Object[] options = { "Sí", "No" };
		int n = JOptionPane.showOptionDialog(this, mensajePrompt, tituloDialogo, JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		return n == JOptionPane.YES_OPTION;
	}

	private void menuGestionAtraccionesGUI() {
		String[] opciones = { "Agregar Atracción Cultural", "Agregar Atracción Mecánica", "Listar Atracciones",
				"Activar/Desactivar Atracción", "Cancelar" };
		int seleccion = JOptionPane.showOptionDialog(this, "Seleccione una acción para Atracciones:",
				"Gestionar Atracciones", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opciones,
				opciones[0]);

		switch (seleccion) {
		case 0:
			guiAgregarAtraccionCultural();
			break;
		case 1:
			guiAgregarAtraccionMecanica();
			break;
		case 2:
			guiListarAtracciones();
			break;
		case 3:
			guiActivarDesactivarAtraccion();
			break;
		default:
			break;
		}
	}

	private void guiListarTodosLosTiquetes() {
		List<Tiquete> tiquetes = parque.getTiquetes();
		if (tiquetes == null || tiquetes.isEmpty()) {
			JOptionPane.showMessageDialog(this, "No hay tiquetes vendidos registrados en el sistema.",
					"Historial de Tiquetes Vendidos", JOptionPane.INFORMATION_MESSAGE);
		} else {
			StringBuilder sb = new StringBuilder(
					"<html><body><h2>Historial de Todos los Tiquetes Vendidos (" + tiquetes.size() + ")</h2><hr>");
			for (int i = 0; i < tiquetes.size(); i++) {
				Tiquete t = tiquetes.get(i);
				sb.append("<b>").append(i + 1).append(". ID: ").append(t.getId()).append("</b> (Tipo: ")
						.append(t.getTipo()).append(")<br>");
				sb.append("Creado: ").append(t.getFechaCreacion());
				sb.append(" | Usado: ").append(t.estaUsado() ? "Sí" : "No");
				if (t.esDeTemporada()) {
					sb.append(" | Temporada: ").append(t.getFechaInicioTemporada()).append(" a ")
							.append(t.getFechaFinTemporada());
				}
				if (t.getAtraccionEspecifica() != null) {
					sb.append(" | Para Atracción: ").append(t.getAtraccionEspecifica());
				}
				if (t.tieneFastPass() && t.getFechaEspecificaFastPass() != null) {
					sb.append(" | FastPass para el día: ").append(t.getFechaEspecificaFastPass());
				}
				sb.append(" | Impresiones: ").append(t.getNumeroDeImpresiones());
				sb.append("<br><hr>");
			}
			sb.append("</body></html>");
			JEditorPane editorPane = new JEditorPane("text/html", sb.toString());
			editorPane.setEditable(false);
			JScrollPane scrollPane = new JScrollPane(editorPane);
			scrollPane.setPreferredSize(new Dimension(550, 400));
			JOptionPane.showMessageDialog(this, scrollPane, "Historial de Tiquetes Vendidos",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void guiListarAtracciones() {
		List<Atraccion> atracciones = parque.getAtracciones();
		if (atracciones.isEmpty()) {
			JOptionPane.showMessageDialog(this, "No hay atracciones registradas.", "Lista de Atracciones",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		StringBuilder sb = new StringBuilder("<html><body><h2>Lista de Atracciones</h2><hr>");
		for (int i = 0; i < atracciones.size(); i++) {
			Atraccion a = atracciones.get(i);
			sb.append("<b>").append(i + 1).append(". ").append(a.getNombre()).append("</b> (").append(a.getTipo())
					.append(")<br>");
			sb.append("Exclusividad: ").append(a.getExclusividad());
			sb.append(" | Ubicación: ").append(a.getUbicacion());
			sb.append(" | Capacidad: ").append(a.getCapacidadMaxima());
			sb.append(" | Emp. Mín: ").append(a.getEmpleadosMinimos());
			sb.append(" | Estado: ")
					.append(a.estaActiva() ? "<font color='green'>Activa</font>" : "<font color='red'>Inactiva</font>")
					.append("<br><hr>");
		}
		sb.append("</body></html>");
		JEditorPane editorPane = new JEditorPane("text/html", sb.toString());
		editorPane.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(editorPane);
		scrollPane.setPreferredSize(new Dimension(550, 400));
		JOptionPane.showMessageDialog(this, scrollPane, "Lista de Atracciones", JOptionPane.INFORMATION_MESSAGE);
	}

	private void guiAgregarAtraccionCultural() {
		JTextField nombreField = new JTextField();
		JTextField capMaxField = new JTextField();
		JTextField empMinField = new JTextField();
		JTextField ubicacionField = new JTextField();
		JComboBox<String> exclusividadCombo = new JComboBox<>(new String[] { "Familiar", "Oro", "Diamante" });
		JTextField edadMinField = new JTextField();

		JPanel panelForm = new JPanel(new GridLayout(0, 2, 10, 5));
		panelForm.add(new JLabel("Nombre:"));
		panelForm.add(nombreField);
		panelForm.add(new JLabel("Capacidad Máxima:"));
		panelForm.add(capMaxField);
		panelForm.add(new JLabel("Empleados Mínimos:"));
		panelForm.add(empMinField);
		panelForm.add(new JLabel("Ubicación:"));
		panelForm.add(ubicacionField);
		panelForm.add(new JLabel("Exclusividad:"));
		panelForm.add(exclusividadCombo);
		panelForm.add(new JLabel("Edad Mínima:"));
		panelForm.add(edadMinField);

		int result = JOptionPane.showConfirmDialog(this, panelForm, "Agregar Atracción Cultural",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result == JOptionPane.OK_OPTION) {
			try {
				String nombre = nombreField.getText().trim();
				if (nombre.isEmpty())
					throw new IllegalArgumentException("Nombre no puede ser vacío.");
				int capMax = Integer.parseInt(capMaxField.getText());
				if (capMax <= 0)
					throw new IllegalArgumentException("Capacidad debe ser positiva.");
				int empMin = Integer.parseInt(empMinField.getText());
				if (empMin < 0)
					throw new IllegalArgumentException("Empleados no puede ser negativo.");
				String ubicacion = ubicacionField.getText().trim();
				if (ubicacion.isEmpty())
					throw new IllegalArgumentException("Ubicación no puede estar vacía.");
				String exclusividad = (String) exclusividadCombo.getSelectedItem();
				int edadMin = Integer.parseInt(edadMinField.getText());
				if (edadMin < 0)
					throw new IllegalArgumentException("Edad mínima no puede ser negativa.");

				if (parque.agregarAtraccionCultural(nombre, capMax, empMin, ubicacion, exclusividad, edadMin)) {
					JOptionPane.showMessageDialog(this, "Atracción cultural '" + nombre + "' agregada.", "Éxito",
							JOptionPane.INFORMATION_MESSAGE);
					parque.guardarDatos(NOMBRE_ARCHIVO_DATOS);
				} else {
					JOptionPane.showMessageDialog(this, "Error al agregar (posiblemente nombre duplicado).", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(this, "Error: Capacidad, Empleados y Edad deben ser números.",
						"Error de Formato", JOptionPane.ERROR_MESSAGE);
			} catch (IllegalArgumentException ex) {
				JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Validación", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void guiAgregarAtraccionMecanica() {
		JTextField nombreField = new JTextField();
		JTextField capMaxField = new JTextField();
		JTextField empMinField = new JTextField();
		JTextField ubicacionField = new JTextField();
		JComboBox<String> exclusividadCombo = new JComboBox<>(new String[] { "Familiar", "Oro", "Diamante" });
		JTextField altMinField = new JTextField("1.0");
		JTextField altMaxField = new JTextField("2.2");
		JTextField pesoMinField = new JTextField("30");
		JTextField pesoMaxField = new JTextField("120");
		JCheckBox pCardCheck = new JCheckBox("Contraindicación Cardíaca");
		JCheckBox pVertCheck = new JCheckBox("Contraindicación Vértigo");
		JComboBox<String> riesgoCombo = new JComboBox<>(new String[] { "Bajo", "Medio", "Alto" });

		JPanel panelForm = new JPanel(new GridLayout(0, 2, 10, 5));
		panelForm.add(new JLabel("Nombre:"));
		panelForm.add(nombreField);
		panelForm.add(new JLabel("Capacidad Máx.:"));
		panelForm.add(capMaxField);
		panelForm.add(new JLabel("Emp. Mínimos:"));
		panelForm.add(empMinField);
		panelForm.add(new JLabel("Ubicación:"));
		panelForm.add(ubicacionField);
		panelForm.add(new JLabel("Exclusividad:"));
		panelForm.add(exclusividadCombo);
		panelForm.add(new JLabel("Altura Mín. (m):"));
		panelForm.add(altMinField);
		panelForm.add(new JLabel("Altura Máx. (m):"));
		panelForm.add(altMaxField);
		panelForm.add(new JLabel("Peso Mín. (kg):"));
		panelForm.add(pesoMinField);
		panelForm.add(new JLabel("Peso Máx. (kg):"));
		panelForm.add(pesoMaxField);
		panelForm.add(pCardCheck);
		panelForm.add(new JLabel(""));
		panelForm.add(pVertCheck);
		panelForm.add(new JLabel(""));
		panelForm.add(new JLabel("Riesgo:"));
		panelForm.add(riesgoCombo);

		int result = JOptionPane.showConfirmDialog(this, panelForm, "Agregar Atracción Mecánica",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result == JOptionPane.OK_OPTION) {
			try {
				String nombre = nombreField.getText().trim();
				if (nombre.isEmpty())
					throw new IllegalArgumentException("Nombre no puede ser vacío.");
				int capMax = Integer.parseInt(capMaxField.getText());
				int empMin = Integer.parseInt(empMinField.getText());
				String ubicacion = ubicacionField.getText().trim();
				String exclusividad = (String) exclusividadCombo.getSelectedItem();
				double altMin = Double.parseDouble(altMinField.getText());
				double altMax = Double.parseDouble(altMaxField.getText());
				if (altMax < altMin)
					throw new IllegalArgumentException("Altura máxima debe ser >= altura mínima.");
				double pesoMin = Double.parseDouble(pesoMinField.getText());
				double pesoMax = Double.parseDouble(pesoMaxField.getText());
				if (pesoMax < pesoMin)
					throw new IllegalArgumentException("Peso máximo debe ser >= peso mínimo.");
				boolean pCard = pCardCheck.isSelected();
				boolean pVert = pVertCheck.isSelected();
				String riesgo = (String) riesgoCombo.getSelectedItem();

				if (parque.agregarAtraccionMecanica(nombre, capMax, empMin, ubicacion, exclusividad, altMin, altMax,
						pesoMin, pesoMax, pCard, pVert, riesgo)) {
					JOptionPane.showMessageDialog(this, "Atracción mecánica '" + nombre + "' agregada.", "Éxito",
							JOptionPane.INFORMATION_MESSAGE);
					parque.guardarDatos(NOMBRE_ARCHIVO_DATOS);
				} else {
					JOptionPane.showMessageDialog(this, "Error al agregar (posiblemente nombre duplicado).", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(this, "Error: Campos numéricos inválidos.", "Error de Formato",
						JOptionPane.ERROR_MESSAGE);
			} catch (IllegalArgumentException ex) {
				JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Validación", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void guiActivarDesactivarAtraccion() {
		List<Atraccion> atracciones = parque.getAtracciones();
		if (atracciones.isEmpty()) {
			JOptionPane.showMessageDialog(this, "No hay atracciones para modificar.");
			return;
		}
		String[] nombresAtracciones = atracciones.stream()
				.map(a -> a.getNombre() + (a.estaActiva() ? " (Activa)" : " (Inactiva)")).toArray(String[]::new);
		String seleccionConEstado = guiLeerOpcionDeLista("Seleccione atracción a activar/desactivar:",
				"Activar/Desactivar Atracción", nombresAtracciones);
		if (seleccionConEstado == null)
			return;

		String nombreRealAtraccion = seleccionConEstado.split(" \\(")[0];
		Atraccion atraccionAModificar = parque.buscarAtraccionPorNombre(nombreRealAtraccion);

		if (atraccionAModificar != null) {
			String[] opcionesCambio = { "Activar", "Desactivar", "Cancelar" };
			int accionIndex = JOptionPane.showOptionDialog(this,
					"Atracción: " + atraccionAModificar.getNombre() + "\nEstado actual: "
							+ (atraccionAModificar.estaActiva() ? "Activa" : "Inactiva") + "\nSeleccione nueva acción:",
					"Cambiar Estado", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opcionesCambio,
					opcionesCambio[0]);

			if (accionIndex == 0) {
				atraccionAModificar.activar();
				JOptionPane.showMessageDialog(this, "Atracción activada.");
				parque.guardarDatos(NOMBRE_ARCHIVO_DATOS);
			} else if (accionIndex == 1) {
				atraccionAModificar.desactivar();
				JOptionPane.showMessageDialog(this, "Atracción desactivada.");
				parque.guardarDatos(NOMBRE_ARCHIVO_DATOS);
			}
		}
	}

	private void menuGestionEmpleadosGUI() {
		String[] opciones = { "Agregar Empleado", "Listar Empleados", "Modificar Tipo Empleado", "Asignar Capacitación",
				"Asignar Turno", "Cancelar" };
		int seleccion = JOptionPane.showOptionDialog(this, "Seleccione una acción para Empleados:",
				"Gestionar Empleados", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opciones,
				opciones[0]);

		switch (seleccion) {
		case 0:
			guiAgregarEmpleado();
			break;
		case 1:
			guiListarEmpleados();
			break;
		case 2:
			guiModificarTipoEmpleado();
			break;
		case 3:
			guiAsignarCapacitacion();
			break;
		case 4:
			guiAsignarTurno();
			break;
		default:
			break;
		}
	}

	private void guiListarEmpleados() {
		List<Empleado> empleados = parque.getEmpleados();
		if (empleados.isEmpty()) {
			JOptionPane.showMessageDialog(this, "No hay empleados registrados.");
			return;
		}
		StringBuilder sb = new StringBuilder("<html><body><h2>Empleados del Parque</h2><hr>");
		for (int i = 0; i < empleados.size(); i++) {
			Empleado e = empleados.get(i);
			sb.append("<b>").append(i + 1).append(". ").append(e.getNombre()).append("</b> (ID: ").append(e.getId())
					.append(")<br>");
			sb.append("Tipo: ").append(e.getTipo()).append("<br>");
			sb.append("Capacitaciones: ")
					.append(e.getAtraccionesCapacitadas().isEmpty() ? "Ninguna" : e.getAtraccionesCapacitadas())
					.append("<br>");
			sb.append("Turnos: ").append(e.getTurnosAsignados().isEmpty() ? "Ninguno" : e.getTurnosAsignados())
					.append("<br><hr>");
		}
		sb.append("</body></html>");
		JEditorPane editorPane = new JEditorPane("text/html", sb.toString());
		editorPane.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(editorPane);
		scrollPane.setPreferredSize(new Dimension(450, 350));
		JOptionPane.showMessageDialog(this, scrollPane, "Lista de Empleados", JOptionPane.INFORMATION_MESSAGE);
	}

	private void guiAgregarEmpleado() {
		JTextField nombreField = new JTextField();
		JTextField idField = new JTextField();
		String[] tiposEmpleado = { "Operador", "Cajero", "Cocinero", "Servicio General", "Administrador" };
		JComboBox<String> tipoEmpleadoCombo = new JComboBox<>(tiposEmpleado);
		JTextField emailField = new JTextField();
		JPasswordField passwordField = new JPasswordField();

		JPanel panelForm = new JPanel(new GridLayout(0, 2, 10, 5));
		panelForm.add(new JLabel("Nombre Empleado:"));
		panelForm.add(nombreField);
		panelForm.add(new JLabel("ID Empleado (será Username):"));
		panelForm.add(idField);
		panelForm.add(new JLabel("Tipo Empleado:"));
		panelForm.add(tipoEmpleadoCombo);
		panelForm.add(new JLabel("Email (para Usuario):"));
		panelForm.add(emailField);
		panelForm.add(new JLabel("Password (para Usuario):"));
		panelForm.add(passwordField);

		int result = JOptionPane.showConfirmDialog(this, panelForm, "Agregar Nuevo Empleado y Usuario",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result == JOptionPane.OK_OPTION) {
			try {
				String nombre = nombreField.getText().trim();
				String id = idField.getText().trim();
				String tipoEmpleado = (String) tipoEmpleadoCombo.getSelectedItem();
				String email = emailField.getText().trim();
				String password = new String(passwordField.getPassword());

				if (nombre.isEmpty() || id.isEmpty() || email.isEmpty() || password.isEmpty())
					throw new IllegalArgumentException("Todos los campos son obligatorios.");
				if (!email.contains("@") || !email.contains("."))
					throw new IllegalArgumentException("Formato de email inválido.");
				if (parque.buscarEmpleadoPorId(id) != null)
					throw new IllegalArgumentException("Ya existe un empleado con el ID: " + id);
				if (parque.buscarUsuarioPorUsername(id) != null)
					throw new IllegalArgumentException("El ID '" + id + "' ya está en uso como username.");
				boolean emailYaExiste = false;
				for (Usuario u : parque.getUsuarios()) {
					if (u.getEmail().equalsIgnoreCase(email)) {
						emailYaExiste = true;
						break;
					}
				}
				if (emailYaExiste) {
					throw new IllegalArgumentException("El email '" + email + "' ya está registrado.");
				}

				String rolUsuario = tipoEmpleado;
				if (parque.registrarCliente(id, rolUsuario, password, email)) {
					Empleado nuevoEmpleado = new Empleado(nombre, id, tipoEmpleado);
					if (parque.agregarEmpleado(nuevoEmpleado)) {
						JOptionPane.showMessageDialog(this, "Empleado '" + nombre + "' y su cuenta de usuario creados.",
								"Éxito", JOptionPane.INFORMATION_MESSAGE);
						parque.guardarDatos(NOMBRE_ARCHIVO_DATOS);
					} else {
						JOptionPane.showMessageDialog(this,
								"Se creó el usuario, pero falló agregar el perfil de empleado.", "Error Inesperado",
								JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(this, "Falló la creación de la cuenta de usuario para el empleado.",
							"Error Usuario", JOptionPane.ERROR_MESSAGE);
				}
			} catch (IllegalArgumentException ex) {
				JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Validación", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void guiModificarTipoEmpleado() {
		List<Empleado> empleados = parque.getEmpleados();
		if (empleados.isEmpty()) {
			JOptionPane.showMessageDialog(this, "No hay empleados para modificar.");
			return;
		}
		String[] idEmpleados = empleados.stream().map(emp -> emp.getId() + " - " + emp.getNombre())
				.toArray(String[]::new);
		String seleccion = guiLeerOpcionDeLista("Seleccione empleado a modificar:", "Modificar Tipo Empleado",
				idEmpleados);
		if (seleccion == null)
			return;

		String idEmpleadoSeleccionado = seleccion.split(" - ")[0];
		Empleado empleadoAModificar = parque.buscarEmpleadoPorId(idEmpleadoSeleccionado);
		if (empleadoAModificar == null) {
			JOptionPane.showMessageDialog(this, "Error: Empleado no encontrado internamente.");
			return;
		}

		String[] tiposValidos = { "Operador", "Cajero", "Cocinero", "Servicio General", "Administrador" };
		JComboBox<String> tipoCombo = new JComboBox<>(tiposValidos);
		tipoCombo.setSelectedItem(empleadoAModificar.getTipo());

		JPanel panelMod = new JPanel(new GridLayout(0, 1, 5, 5));
		panelMod.add(new JLabel(
				"Empleado: " + empleadoAModificar.getNombre() + " (ID: " + empleadoAModificar.getId() + ")"));
		panelMod.add(new JLabel("Tipo Actual: " + empleadoAModificar.getTipo()));
		panelMod.add(new JLabel("Nuevo Tipo:"));
		panelMod.add(tipoCombo);

		int result = JOptionPane.showConfirmDialog(this, panelMod, "Confirmar Nuevo Tipo", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);
		if (result == JOptionPane.OK_OPTION) {
			String nuevoTipo = (String) tipoCombo.getSelectedItem();
			String antiguoTipo = empleadoAModificar.getTipo();
			empleadoAModificar.setTipo(nuevoTipo);

			Usuario usuarioAsociado = parque.buscarUsuarioPorUsername(empleadoAModificar.getId());
			if (usuarioAsociado != null) {
				usuarioAsociado.setRol(nuevoTipo);
				System.out.println("Rol del usuario '" + usuarioAsociado.getUsername() + "' también actualizado a '"
						+ nuevoTipo + "'.");
			} else {
				System.out.println("Advertencia: No se encontró cuenta de usuario para el empleado ID: "
						+ empleadoAModificar.getId() + " para actualizar su rol.");
			}
			JOptionPane.showMessageDialog(this,
					"Tipo del empleado '" + empleadoAModificar.getNombre() + "' actualizado a '" + nuevoTipo + "'.");
			parque.guardarDatos(NOMBRE_ARCHIVO_DATOS);
		}
	}

	private void guiAsignarCapacitacion() {
		if (parque.getEmpleados().isEmpty()) {
			JOptionPane.showMessageDialog(this, "No hay empleados registrados para asignar capacitación.", "Error",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (parque.getAtracciones().isEmpty()) {
			JOptionPane.showMessageDialog(this, "No hay atracciones disponibles para capacitar.", "Error",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		String[] empleadosArray = parque.getEmpleados().stream()
				.map(e -> e.getId() + " - " + e.getNombre() + " (" + e.getTipo() + ")").toArray(String[]::new);
		String empleadoSeleccionadoStr = guiLeerOpcionDeLista("Seleccione el empleado a capacitar:",
				"Asignar Capacitación", empleadosArray);
		if (empleadoSeleccionadoStr == null)
			return;

		String idEmpleado = empleadoSeleccionadoStr.split(" - ")[0];
		Empleado empleado = parque.buscarEmpleadoPorId(idEmpleado);
		if (empleado == null) {
			JOptionPane.showMessageDialog(this, "Empleado no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		String[] atraccionesArray = parque.getAtracciones().stream().map(Atraccion::getNombre).toArray(String[]::new);
		String atraccionSeleccionadaNombre = guiLeerOpcionDeLista("Seleccione la atracción para la capacitación:",
				"Asignar Capacitación", atraccionesArray);
		if (atraccionSeleccionadaNombre == null)
			return;

		if (empleado.getAtraccionesCapacitadas() != null
				&& empleado.getAtraccionesCapacitadas().contains(atraccionSeleccionadaNombre)) {
			JOptionPane.showMessageDialog(this, "El empleado " + empleado.getNombre() + " ya está capacitado para "
					+ atraccionSeleccionadaNombre + ".", "Información", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		empleado.agregarCapacitacion(atraccionSeleccionadaNombre);
		parque.guardarDatos(NOMBRE_ARCHIVO_DATOS);
		JOptionPane.showMessageDialog(this,
				"Empleado '" + empleado.getNombre() + "' ahora capacitado para '" + atraccionSeleccionadaNombre + "'.",
				"Capacitación Asignada", JOptionPane.INFORMATION_MESSAGE);
	}

	private void guiAsignarTurno() {
		if (parque.getEmpleados().isEmpty()) {
			JOptionPane.showMessageDialog(this, "No hay empleados registrados para asignar turnos.", "Error",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		String[] empleadosArray = parque.getEmpleados().stream()
				.map(e -> e.getId() + " - " + e.getNombre() + " (" + e.getTipo() + ")").toArray(String[]::new);
		String empleadoSeleccionadoStr = guiLeerOpcionDeLista("Seleccione el empleado para asignar turno:",
				"Asignar Turno", empleadosArray);
		if (empleadoSeleccionadoStr == null)
			return;

		String idEmpleado = empleadoSeleccionadoStr.split(" - ")[0];
		Empleado empleado = parque.buscarEmpleadoPorId(idEmpleado);
		if (empleado == null) {
			JOptionPane.showMessageDialog(this, "Empleado no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		String descripcionTurno = guiLeerStringNoVacio(
				"Descripción del turno (ej. 'Lunes Mañana - Montaña Cobra', 'Martes Tarde - Taquilla Principal'):",
				"Asignar Turno");
		if (descripcionTurno == null) {
			return;
		}
		empleado.asignarTurno(descripcionTurno);
		parque.guardarDatos(NOMBRE_ARCHIVO_DATOS);
		JOptionPane.showMessageDialog(this, "Turno '" + descripcionTurno + "' asignado a " + empleado.getNombre() + ".",
				"Turno Asignado", JOptionPane.INFORMATION_MESSAGE);
	}

	private void menuGestionUsuariosGUI() {
		String[] opciones = { "Listar Todos los Usuarios", "Buscar Usuario por Username", "Ver Tiquetes de un Usuario",
				"Modificar Rol de Usuario", "Crear Nuevo Usuario Administrador", "Cancelar" };
		int seleccion = JOptionPane.showOptionDialog(this, "Seleccione una acción para Cuentas de Usuario:",
				"Gestionar Cuentas de Usuario", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opciones,
				opciones[0]);

		switch (seleccion) {
		case 0:
			guiListarTodosLosUsuarios();
			break;
		case 1:
			guiBuscarUsuarioPorUsername();
			break;
		case 2:
			guiVerTiquetesDeUsuario();
			break;
		case 3:
			guiModificarRolUsuario();
			break;
		case 4:
			guiCrearNuevoAdmin();
			break;
		default:
			break;
		}
	}

	private void guiListarTodosLosUsuarios() {
		List<Usuario> usuarios = parque.getUsuarios();
		if (usuarios.isEmpty()) {
			JOptionPane.showMessageDialog(this, "No hay usuarios registrados.");
			return;
		}
		StringBuilder sb = new StringBuilder("<html><body><h2>Usuarios del Sistema</h2><hr>");
		for (int i = 0; i < usuarios.size(); i++) {
			Usuario u = usuarios.get(i);
			sb.append("<b>").append(i + 1).append(". ").append(u.getUsername()).append("</b> (Rol: ").append(u.getRol())
					.append(")<br>");
			sb.append("Email: ").append(u.getEmail()).append("<br><hr>");
		}
		sb.append("</body></html>");
		JEditorPane editorPane = new JEditorPane("text/html", sb.toString());
		editorPane.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(editorPane);
		scrollPane.setPreferredSize(new Dimension(450, 350));
		JOptionPane.showMessageDialog(this, scrollPane, "Lista de Usuarios", JOptionPane.INFORMATION_MESSAGE);
	}

	private void guiBuscarUsuarioPorUsername() {
		String username = guiLeerStringNoVacio("Username del usuario a buscar:", "Buscar Usuario");
		if (username == null)
			return;
		Usuario u = parque.buscarUsuarioPorUsername(username);
		if (u != null) {
			StringBuilder sb = new StringBuilder("<html><body><b>Usuario Encontrado:</b><br><hr>");
			sb.append("Username: ").append(u.getUsername()).append("<br>");
			sb.append("Rol: ").append(u.getRol()).append("<br>");
			sb.append("Email: ").append(u.getEmail()).append("<br><hr>");
			sb.append("<b>Tiquetes:</b><br>");
			if (u.getTiquetes() == null || u.getTiquetes().isEmpty()) {
				sb.append(" (No tiene tiquetes)<br>");
			} else {
				for (Tiquete t : u.getTiquetes()) {
					sb.append(" - ").append(t.getId()).append(" (").append(t.getTipo()).append(")<br>");
				}
			}
			sb.append("</body></html>");
			JEditorPane editorPane = new JEditorPane("text/html", sb.toString());
			editorPane.setEditable(false);
			JScrollPane scrollPane = new JScrollPane(editorPane);
			scrollPane.setPreferredSize(new Dimension(400, 250));
			JOptionPane.showMessageDialog(this, scrollPane, "Detalles del Usuario", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this, "Usuario '" + username + "' no encontrado.", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void guiVerTiquetesDeUsuario() {
		List<Usuario> usuarios = parque.getUsuarios();
		if (usuarios.isEmpty()) {
			JOptionPane.showMessageDialog(this, "No hay usuarios para seleccionar.");
			return;
		}
		String[] nombresUsuarios = usuarios.stream().map(u -> u.getUsername() + " (" + u.getRol() + ")")
				.toArray(String[]::new);
		String seleccionUsuario = guiLeerOpcionDeLista("Seleccione usuario para ver sus tiquetes:", "Ver Tiquetes",
				nombresUsuarios);
		if (seleccionUsuario == null)
			return;

		String usernameSeleccionado = seleccionUsuario.split(" \\(")[0];
		Usuario u = parque.buscarUsuarioPorUsername(usernameSeleccionado);

		if (u != null) {
			List<Tiquete> tiquetes = u.getTiquetes();
			if (tiquetes == null || tiquetes.isEmpty()) {
				JOptionPane.showMessageDialog(this, "El usuario " + u.getUsername() + " no tiene tiquetes.",
						"Tiquetes de Usuario", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			StringBuilder sb = new StringBuilder("<html><body><b>Tiquetes de " + u.getUsername() + ":</b><hr>");
			for (Tiquete t : tiquetes) {
				sb.append(t.toString().replace("\n", "<br>").replace("Tiquete [", "[").replace("]", "]"))
						.append("<br><hr>");
			}
			sb.append("</body></html>");
			JEditorPane editorPane = new JEditorPane("text/html", sb.toString());
			editorPane.setEditable(false);
			JScrollPane scrollPane = new JScrollPane(editorPane);
			scrollPane.setPreferredSize(new Dimension(500, 350));
			JOptionPane.showMessageDialog(this, scrollPane, "Tiquetes de " + u.getUsername(),
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this, "Usuario no encontrado internamente.", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void guiModificarRolUsuario() {
		if (parque.getUsuarios().isEmpty()) {
			JOptionPane.showMessageDialog(this, "No hay usuarios para modificar.");
			return;
		}
		String[] usuariosArray = parque.getUsuarios().stream().map(u -> u.getUsername() + " (Rol: " + u.getRol() + ")")
				.toArray(String[]::new);
		String seleccionUsuario = guiLeerOpcionDeLista("Seleccione usuario a modificar:", "Modificar Rol Usuario",
				usuariosArray);
		if (seleccionUsuario == null)
			return;

		String usernameAModificar = seleccionUsuario.split(" \\(")[0];
		Usuario usuarioAModificar = parque.buscarUsuarioPorUsername(usernameAModificar);

		if (usuarioAModificar == null) {
			JOptionPane.showMessageDialog(this, "Usuario no encontrado.");
			return;
		}

		List<String> rolesValidosList = Arrays.asList("Cliente", "Administrador", "Operador", "Cajero", "Cocinero",
				"Servicio General");
		String[] rolesValidosArray = rolesValidosList.toArray(new String[0]);

		JComboBox<String> rolCombo = new JComboBox<>(rolesValidosArray);
		rolCombo.setSelectedItem(usuarioAModificar.getRol());

		JPanel panelModRol = new JPanel(new GridLayout(0, 1, 5, 5));
		panelModRol.add(new JLabel("Usuario: " + usuarioAModificar.getUsername()));
		panelModRol.add(new JLabel("Rol Actual: " + usuarioAModificar.getRol()));
		panelModRol.add(new JLabel("Seleccione Nuevo Rol:"));
		panelModRol.add(rolCombo);

		int result = JOptionPane.showConfirmDialog(this, panelModRol, "Confirmar Nuevo Rol",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			String nuevoRol = (String) rolCombo.getSelectedItem();
			String antiguoRol = usuarioAModificar.getRol();

			if (nuevoRol.equals(antiguoRol)) {
				JOptionPane.showMessageDialog(this,
						"El rol seleccionado es el mismo que el actual. No se realizaron cambios.", "Información",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			usuarioAModificar.setRol(nuevoRol);
			boolean cambioRealizado = true;
			Empleado empAsociado = parque.buscarEmpleadoPorId(usuarioAModificar.getUsername());

			if (esTipoDeEmpleado(nuevoRol)) {
				if (empAsociado == null) {
					Empleado nuevoEmpleado = new Empleado(usuarioAModificar.getUsername(),
							usuarioAModificar.getUsername(), nuevoRol);
					if (!parque.agregarEmpleado(nuevoEmpleado)) {
						usuarioAModificar.setRol(antiguoRol);
						cambioRealizado = false;
						JOptionPane.showMessageDialog(this,
								"Error creando perfil de empleado para el nuevo rol. El rol del usuario no fue cambiado.",
								"Error", JOptionPane.ERROR_MESSAGE);
					} else {
						System.out.println("Perfil de empleado creado para " + usuarioAModificar.getUsername()
								+ " con tipo " + nuevoRol);
					}
				} else if (!empAsociado.getTipo().equalsIgnoreCase(nuevoRol)) {
					empAsociado.setTipo(nuevoRol);
					System.out.println("Tipo del empleado asociado (" + empAsociado.getNombre()
							+ ") también actualizado a: " + nuevoRol);
				}
			} else if (empAsociado != null && esTipoDeEmpleado(antiguoRol)) {
				System.out.println("Advertencia: El usuario '" + usuarioAModificar.getUsername()
						+ "' ya no tiene un rol de empleado. Su perfil de empleado (Tipo: " + empAsociado.getTipo()
						+ ") aún existe. "
						+ "Considere si necesita gestionarlo manualmente (ej. eliminar el objeto Empleado).");
			}

			if (cambioRealizado) {
				JOptionPane.showMessageDialog(this,
						"Rol del usuario '" + usuarioAModificar.getUsername() + "' actualizado a '" + nuevoRol + "'.");
				parque.guardarDatos(NOMBRE_ARCHIVO_DATOS);
			}
		}
	}

	private void guiCrearNuevoAdmin() {
		JTextField usernameField = new JTextField();
		JPasswordField passwordField = new JPasswordField();
		JTextField emailField = new JTextField();

		JPanel panelForm = new JPanel(new GridLayout(0, 2, 10, 5));
		panelForm.add(new JLabel("Username Admin:"));
		panelForm.add(usernameField);
		panelForm.add(new JLabel("Password:"));
		panelForm.add(passwordField);
		panelForm.add(new JLabel("Email:"));
		panelForm.add(emailField);

		int result = JOptionPane.showConfirmDialog(this, panelForm, "Crear Nuevo Administrador",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result == JOptionPane.OK_OPTION) {
			try {
				String username = usernameField.getText().trim();
				String password = new String(passwordField.getPassword());
				String email = emailField.getText().trim();

				if (username.isEmpty() || password.isEmpty() || email.isEmpty())
					throw new IllegalArgumentException("Todos los campos son obligatorios.");
				if (!email.contains("@") || !email.contains("."))
					throw new IllegalArgumentException("Formato de email inválido.");
				if (parque.buscarUsuarioPorUsername(username) != null)
					throw new IllegalArgumentException("El username '" + username + "' ya existe.");
				boolean emailYaExiste = false;
				for (Usuario u : parque.getUsuarios()) {
					if (u.getEmail().equalsIgnoreCase(email)) {
						emailYaExiste = true;
						break;
					}
				}
				if (emailYaExiste) {
					throw new IllegalArgumentException("El email '" + email + "' ya está registrado.");
				}

				if (parque.registrarCliente(username, "Administrador", password, email)) {
					JOptionPane.showMessageDialog(this, "Nuevo Admin '" + username + "' creado.", "Éxito",
							JOptionPane.INFORMATION_MESSAGE);
					parque.guardarDatos(NOMBRE_ARCHIVO_DATOS);
				} else {
					JOptionPane.showMessageDialog(this, "Error al crear Admin.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			} catch (IllegalArgumentException ex) {
				JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Validación", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private static boolean esTipoDeEmpleado(String rol) {
		List<String> tiposEmpleadoValidos = Arrays.asList("Operador", "Cajero", "Cocinero", "Servicio General");
		if (rol == null)
			return false;
		return tiposEmpleadoValidos.stream().anyMatch(tipo -> tipo.equalsIgnoreCase(rol));
	}

	private void guiVerReportesSimples() {
		List<Atraccion> atracciones = parque.getAtracciones();
		List<Usuario> usuarios = parque.getUsuarios();
		List<Empleado> empleados = parque.getEmpleados();
		List<Tiquete> tiquetes = parque.getTiquetes();
		long atraccionesActivas = atracciones.stream().filter(Atraccion::estaActiva).count();

		StringBuilder sb = new StringBuilder("<html><body><h2>Reportes Simples del Parque</h2><hr>");
		sb.append("<b>Total de Atracciones:</b> ").append(atracciones.size()).append("<br>");
		sb.append("  - Activas: ").append(atraccionesActivas).append("<br>");
		sb.append("  - Inactivas: ").append(atracciones.size() - atraccionesActivas).append("<br><hr>");
		sb.append("<b>Total de Usuarios Registrados:</b> ").append(usuarios.size()).append("<br>");
		usuarios.stream().map(Usuario::getRol).distinct().forEach(rol -> {
			long count = usuarios.stream().filter(u -> u.getRol().equalsIgnoreCase(rol)).count();
			if (count > 0)
				sb.append("  - Usuarios con rol '").append(rol).append("': ").append(count).append("<br>");
		});
		sb.append("<hr>");
		sb.append("<b>Total de Empleados:</b> ").append(empleados.size()).append("<br>");
		empleados.stream().map(Empleado::getTipo).distinct().forEach(tipo -> {
			long count = empleados.stream().filter(e -> e.getTipo().equalsIgnoreCase(tipo)).count();
			if (count > 0)
				sb.append("  - Empleados tipo '").append(tipo).append("': ").append(count).append("<br>");
		});
		sb.append("<hr>");
		sb.append("<b>Total de Tiquetes Vendidos (histórico):</b> ").append(tiquetes.size()).append("<br>");
		sb.append("</body></html>");

		JEditorPane editorPane = new JEditorPane("text/html", sb.toString());
		editorPane.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(editorPane);
		scrollPane.setPreferredSize(new Dimension(450, 350));
		JOptionPane.showMessageDialog(this, scrollPane, "Reportes Simples", JOptionPane.INFORMATION_MESSAGE);
	}
}