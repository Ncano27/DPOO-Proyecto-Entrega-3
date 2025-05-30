package GUI;

import modelo.Parque;
import modelo.Usuario;
import modelo.Empleado;
import modelo.Atraccion;
import modelo.AtraccionCultural;
import modelo.AtraccionMecanica;
import modelo.Tiquete;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.Arrays;

public class VentanaEmpleado extends JFrame {
	private static final long serialVersionUID = 1L;
	private Parque parque;
	private Usuario usuarioEmpleado;
	private Empleado empleadoLogueado;
	private static final String NOMBRE_ARCHIVO_DATOS = "datos_parque.dat";

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

	private int guiLeerIntConRango(String mensajePrompt, String tituloDialogo, int min, int max) {
		int numero;
		while (true) {
			String input = JOptionPane.showInputDialog(this, mensajePrompt, tituloDialogo, JOptionPane.PLAIN_MESSAGE);
			if (input == null)
				return -1;
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

	private String guiLeerOpcionDeLista(String mensajePrompt, String tituloDialogo, String[] opciones) {
		if (opciones == null || opciones.length == 0) {
			JOptionPane.showMessageDialog(this, "No hay opciones disponibles para seleccionar.", "Error",
					JOptionPane.WARNING_MESSAGE);
			return null;
		}
		return (String) JOptionPane.showInputDialog(this, mensajePrompt, tituloDialogo, JOptionPane.PLAIN_MESSAGE, null,
				opciones, opciones[0]);
	}

	public VentanaEmpleado(Parque parque, Usuario usuarioEmpleado, Empleado empleadoLogueado) {
		this.parque = parque;
		this.usuarioEmpleado = usuarioEmpleado;
		this.empleadoLogueado = empleadoLogueado;

		setTitle("Portal Empleado - " + empleadoLogueado.getNombre() + " (" + empleadoLogueado.getTipo() + ")");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(500, 400);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout(10, 10));

		JLabel lblBienvenida = new JLabel(
				"Empleado: " + empleadoLogueado.getNombre() + " | Tipo: " + empleadoLogueado.getTipo(),
				SwingConstants.CENTER);
		lblBienvenida.setFont(new Font("Arial", Font.BOLD, 16));
		add(lblBienvenida, BorderLayout.NORTH);

		JPanel panelBotones = new JPanel(new GridLayout(0, 1, 10, 10));
		panelBotones.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

		JButton validarIngresoBtn = new JButton("Validar Ingreso de Cliente a Atracción");
		JButton cambiarEstadoAtraccionBtn = new JButton("Cambiar Estado de Atracción");
		JButton verMisDatosBtn = new JButton("Ver Mis Turnos y Capacitaciones");
		validarIngresoBtn.addActionListener(e -> guiValidarIngresoClienteAtraccion());
		cambiarEstadoAtraccionBtn.addActionListener(e -> guiCambiarEstadoAtraccionEmpleado());
		verMisDatosBtn.addActionListener(e -> guiVerMisDatosEmpleado());

		panelBotones.add(validarIngresoBtn);
		panelBotones.add(cambiarEstadoAtraccionBtn);
		panelBotones.add(verMisDatosBtn);
		add(panelBotones, BorderLayout.CENTER);
	}

	private void guiValidarIngresoClienteAtraccion() {
		String emailCliente = guiLeerStringNoVacio("Email del cliente a validar:", "Validar Ingreso de Cliente");
		if (emailCliente == null)
			return;

		Usuario cliente = null;
		for (Usuario u : parque.getUsuarios()) {
			if (u.getEmail() != null && u.getEmail().equalsIgnoreCase(emailCliente)
					&& u.getRol().equalsIgnoreCase("Cliente")) {
				cliente = u;
				break;
			}
		}
		if (cliente == null) {
			JOptionPane.showMessageDialog(this, "Cliente no encontrado con email: " + emailCliente, "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (cliente.getTiquetes() == null || cliente.getTiquetes().isEmpty()) {
			JOptionPane.showMessageDialog(this, "El cliente " + cliente.getUsername() + " no tiene tiquetes.", "Error",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		List<Tiquete> tiquetesCliente = cliente.getTiquetes();
		String[] opcionesTiquetes = new String[tiquetesCliente.size()];
		for (int i = 0; i < tiquetesCliente.size(); i++) {
			opcionesTiquetes[i] = (i + 1) + ". " + tiquetesCliente.get(i).getId() + " ("
					+ tiquetesCliente.get(i).getTipo() + ")";
		}
		String tiqueteSeleccionadoStr = guiLeerOpcionDeLista(
				"Seleccione el tiquete del cliente (" + cliente.getUsername() + "):", "Seleccionar Tiquete",
				opcionesTiquetes);
		if (tiqueteSeleccionadoStr == null)
			return;
		int indiceTiquete = Integer.parseInt(tiqueteSeleccionadoStr.split("\\.")[0]) - 1;
		Tiquete tiqueteAUsar = tiquetesCliente.get(indiceTiquete);

		List<Atraccion> atraccionesActivas = parque.getAtracciones().stream().filter(Atraccion::estaActiva).toList();
		if (atraccionesActivas.isEmpty()) {
			JOptionPane.showMessageDialog(this, "No hay atracciones activas en el parque.");
			return;
		}
		String[] nombresAtracciones = atraccionesActivas.stream().map(Atraccion::getNombre).toArray(String[]::new);
		String nombreAtrSeleccionada = guiLeerOpcionDeLista(
				"Seleccione la atracción a la que el cliente desea ingresar:", "Seleccionar Atracción",
				nombresAtracciones);
		if (nombreAtrSeleccionada == null)
			return;

		Atraccion atraccionAEntrar = parque.buscarAtraccionPorNombre(nombreAtrSeleccionada);
		if (atraccionAEntrar == null) {
			JOptionPane.showMessageDialog(this, "Atracción no válida.");
			return;
		}

		if (parque.visitantePuedeAccederAtraccion(cliente, tiqueteAUsar, atraccionAEntrar)) {
			JOptionPane.showMessageDialog(this,
					"Validación de tiquete por sistema: OK. Procediendo a chequeos específicos de la atracción...",
					"Paso 1 Exitoso", JOptionPane.INFORMATION_MESSAGE);

			boolean cumpleRequisitosEspecificos = true;
			if (atraccionAEntrar instanceof AtraccionCultural) {
				AtraccionCultural cultural = (AtraccionCultural) atraccionAEntrar;
				if (cultural.getEdadMinima() > 0) {
					int edadVisitante = guiLeerIntConRango("Edad del visitante para '" + cultural.getNombre() + "': ",
							"Verificar Edad", 0, 120);
					if (edadVisitante == -1)
						return;
					if (!cultural.puedeIngresar(edadVisitante)) {
						JOptionPane.showMessageDialog(
								this, "ACCESO DENEGADO: Cliente no cumple requisito de edad ("
										+ cultural.getEdadMinima() + " años).",
								"Acceso Denegado", JOptionPane.ERROR_MESSAGE);
						cumpleRequisitosEspecificos = false;
					}
				}
			} else if (atraccionAEntrar instanceof AtraccionMecanica) {
				AtraccionMecanica mecanica = (AtraccionMecanica) atraccionAEntrar;
				JPanel panelMecanica = new JPanel(new GridLayout(0, 2, 5, 5));
				JTextField alturaField = new JTextField();
				JTextField pesoField = new JTextField();
				JCheckBox cardiacoCheck = new JCheckBox("¿Visitante tiene problemas cardiacos relevantes?");
				JCheckBox vertigoCheck = new JCheckBox("¿Visitante sufre de vertigo actualmente?");
				panelMecanica.add(new JLabel("Altura del visitante (metros, ej: 1.70):"));
				panelMecanica.add(alturaField);
				panelMecanica.add(new JLabel("Peso del visitante (kg, ej: 65.0):"));
				panelMecanica.add(pesoField);
				panelMecanica.add(cardiacoCheck);
				panelMecanica.add(new JLabel(""));
				panelMecanica.add(vertigoCheck);
				panelMecanica.add(new JLabel(""));
				int resultMecanica = JOptionPane.showConfirmDialog(this, panelMecanica,
						"Requisitos para " + mecanica.getNombre(), JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE);

				if (resultMecanica == JOptionPane.OK_OPTION) {
					try {
						double altura = Double.parseDouble(alturaField.getText());
						double peso = Double.parseDouble(pesoField.getText());
						boolean tieneProblemasCardiacos = cardiacoCheck.isSelected();
						boolean tieneVertigo = vertigoCheck.isSelected();
						if (!mecanica.puedeIngresar(altura, peso, tieneProblemasCardiacos, tieneVertigo)) {
							JOptionPane.showMessageDialog(this,
									"ACCESO DENEGADO: Cliente no cumple requisitos de seguridad de la atracción.",
									"Acceso Denegado", JOptionPane.ERROR_MESSAGE);
							cumpleRequisitosEspecificos = false;
						}
					} catch (NumberFormatException ex) {
						JOptionPane.showMessageDialog(this, "Altura o peso deben ser números válidos.",
								"Error de Formato", JOptionPane.ERROR_MESSAGE);
						cumpleRequisitosEspecificos = false;
					}
				} else {
					cumpleRequisitosEspecificos = false;
				}
			}

			if (cumpleRequisitosEspecificos) {
				JOptionPane
						.showMessageDialog(this,
								"¡ACCESO CONCEDIDO a " + atraccionAEntrar.getNombre() + " para el cliente "
										+ cliente.getUsername() + "!",
								"Acceso Concedido", JOptionPane.INFORMATION_MESSAGE);
				parque.guardarDatos(NOMBRE_ARCHIVO_DATOS);
			}
		}
	}

	private void guiCambiarEstadoAtraccionEmpleado() {
		List<Atraccion> atracciones = parque.getAtracciones();
		if (atracciones.isEmpty()) {
			JOptionPane.showMessageDialog(this, "No hay atracciones para modificar.");
			return;
		}
		String[] nombresAtracciones = atracciones.stream()
				.map(a -> a.getNombre() + (a.estaActiva() ? " (Activa)" : " (Inactiva)")).toArray(String[]::new);

		String seleccionConEstado = guiLeerOpcionDeLista("Seleccione atracción:", "Activar/Desactivar Atracción",
				nombresAtracciones);
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
				JOptionPane.showMessageDialog(this, "Atracción '" + atraccionAModificar.getNombre() + "' activada.");
			} else if (accionIndex == 1) {
				atraccionAModificar.desactivar();
				JOptionPane.showMessageDialog(this,
						"Atracción '" + atraccionAModificar.getNombre() + "' desactivada/reportada fuera de servicio.");
			}
			if (accionIndex == 0 || accionIndex == 1) {
				parque.guardarDatos(NOMBRE_ARCHIVO_DATOS);
			}
		}
	}

	private void guiVerMisDatosEmpleado() {
		StringBuilder sb = new StringBuilder("<html><body>");
		sb.append("<h2>Datos del Empleado: ").append(empleadoLogueado.getNombre()).append("</h2>");
		sb.append("<b>ID (Username):</b> ").append(empleadoLogueado.getId()).append("<br>");
		sb.append("<b>Tipo/Rol:</b> ").append(empleadoLogueado.getTipo()).append("<br>");
		sb.append("<b>Email:</b> ").append(usuarioEmpleado.getEmail()).append("<br><hr>");

		sb.append("<b>Capacitaciones:</b><br>");
		Set<String> capacitaciones = empleadoLogueado.getAtraccionesCapacitadas();
		if (capacitaciones == null || capacitaciones.isEmpty()) {
			sb.append("Ninguna capacitación asignada.<br>");
		} else {
			for (String cap : capacitaciones) {
				sb.append("- ").append(cap).append("<br>");
			}
		}
		sb.append("<hr>");

		sb.append("<b>Turnos Asignados:</b><br>");
		Set<String> turnos = empleadoLogueado.getTurnosAsignados();
		if (turnos == null || turnos.isEmpty()) {
			sb.append("Ningún turno asignado.<br>");
		} else {
			for (String turno : turnos) {
				sb.append("- ").append(turno).append("<br>");
			}
		}
		sb.append("</body></html>");

		JEditorPane editorPane = new JEditorPane("text/html", sb.toString());
		editorPane.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(editorPane);
		scrollPane.setPreferredSize(new Dimension(450, 350));
		JOptionPane.showMessageDialog(this, scrollPane, "Mis Datos - " + empleadoLogueado.getNombre(),
				JOptionPane.INFORMATION_MESSAGE);
	}
}