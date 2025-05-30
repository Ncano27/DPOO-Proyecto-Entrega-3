package GUI;

import modelo.Parque;
import modelo.Usuario;
import modelo.Atraccion;
import modelo.AtraccionCultural;
import modelo.AtraccionMecanica;
import modelo.Tiquete;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class VentanaCliente extends JFrame {
	private static final long serialVersionUID = 1L;
	private Parque parque;
	private Usuario clienteLogueado;
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

	private String guiLeerOpcionDeLista(String mensajePrompt, String tituloDialogo, String[] opciones) {
		if (opciones == null || opciones.length == 0) {
			JOptionPane.showMessageDialog(this, "No hay opciones disponibles.", "Error", JOptionPane.WARNING_MESSAGE);
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

	private LocalDate guiLeerFechaValida(String mensajePrompt, String tituloDialogo, boolean permitirFechasPasadas) {
		LocalDate fecha;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		while (true) {
			String input = JOptionPane.showInputDialog(this, mensajePrompt + " (Formato: YYYY-MM-DD)", tituloDialogo,
					JOptionPane.PLAIN_MESSAGE);
			if (input == null)
				return null;
			try {
				fecha = LocalDate.parse(input.trim(), formatter);
				if (permitirFechasPasadas || !fecha.isBefore(LocalDate.now())) {
					return fecha;
				}
				JOptionPane.showMessageDialog(this, "La fecha no puede ser en el pasado.", "Error de Fecha",
						JOptionPane.ERROR_MESSAGE);
			} catch (DateTimeParseException e) {
				JOptionPane.showMessageDialog(this, "Formato de fecha incorrecto. Use YYYY-MM-DD.", "Error de Formato",
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

	public VentanaCliente(Parque parque, Usuario clienteLogueado) {
		this.parque = parque;
		this.clienteLogueado = clienteLogueado;

		setTitle("Portal Cliente - " + clienteLogueado.getUsername());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(500, 450);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout(10, 10));

		JLabel lblBienvenida = new JLabel("Cliente: " + clienteLogueado.getUsername(), SwingConstants.CENTER);
		lblBienvenida.setFont(new Font("Arial", Font.BOLD, 16));
		lblBienvenida.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		add(lblBienvenida, BorderLayout.NORTH);

		JPanel panelBotones = new JPanel(new GridLayout(0, 1, 10, 10));
		panelBotones.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

		JButton verAtraccionesBtn = new JButton("Ver Todas las Atracciones");
		JButton comprarTiqueteBtn = new JButton("Comprar Tiquete");
		JButton verMisTiquetesBtn = new JButton("Ver Mis Tiquetes");
		JButton ingresarAtraccionBtn = new JButton("Intentar Ingresar a Atracción");
		JButton imprimirTiqueteBtn = new JButton("Imprimir Tiquete (Visualizar)");

		verAtraccionesBtn.addActionListener(e -> guiListarAtraccionesCliente());
		comprarTiqueteBtn.addActionListener(e -> guiComprarTiqueteCliente());
		verMisTiquetesBtn.addActionListener(e -> guiVerMisTiquetesCliente());
		ingresarAtraccionBtn.addActionListener(e -> guiIntentarIngresarAtraccionCliente());
		imprimirTiqueteBtn.addActionListener(e -> {
			if (this.clienteLogueado == null) {
				JOptionPane.showMessageDialog(this, "Error: No hay cliente logueado.", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			new VentanaImprimirTiquete(parque, this.clienteLogueado).setVisible(true);
		});

		panelBotones.add(verAtraccionesBtn);
		panelBotones.add(comprarTiqueteBtn);
		panelBotones.add(verMisTiquetesBtn);
		panelBotones.add(ingresarAtraccionBtn);
		panelBotones.add(imprimirTiqueteBtn);
		add(panelBotones, BorderLayout.CENTER);
	}

	private void guiListarAtraccionesCliente() {
		List<Atraccion> atracciones = parque.getAtracciones();
		if (atracciones.isEmpty()) {
			JOptionPane.showMessageDialog(this, "No hay atracciones disponibles.", "Atracciones",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		StringBuilder sb = new StringBuilder("<html><body><h2>Atracciones Disponibles:</h2><hr>");
		for (Atraccion a : atracciones) {
			sb.append("<b>").append(a.getNombre()).append("</b> (").append(a.getTipo()).append(")<br>");
			sb.append("Exclusividad: ").append(a.getExclusividad());
			sb.append(" | Ubicación: ").append(a.getUbicacion());
			sb.append(" | ")
					.append(a.estaActiva() ? "<font color='green'>Activa</font>" : "<font color='red'>Inactiva</font>");
			sb.append("<br><hr>");
		}
		sb.append("</body></html>");
		JEditorPane editorPane = new JEditorPane("text/html", sb.toString());
		editorPane.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(editorPane);
		scrollPane.setPreferredSize(new Dimension(500, 350));
		JOptionPane.showMessageDialog(this, scrollPane, "Lista de Atracciones", JOptionPane.INFORMATION_MESSAGE);
	}

	private void guiComprarTiqueteCliente() {
		String[] tiposDeCompra = { "Tiquete General", "Entrada Individual", "Tiquete de Temporada", "FastPass",
				"Cancelar" };
		int seleccionIndice = JOptionPane.showOptionDialog(this,
				"¿Qué tipo de tiquete desea comprar, " + clienteLogueado.getUsername() + "?", "Comprar Tiquete",
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, tiposDeCompra, tiposDeCompra[0]);

		if (seleccionIndice == JOptionPane.CLOSED_OPTION || seleccionIndice == 4) {
			return;
		}

		String tipoCompraSeleccionado = tiposDeCompra[seleccionIndice];
		String emailCliente = clienteLogueado.getEmail();
		int cantidad = guiLeerIntConRango("Cantidad de tiquetes (1-5):", "Cantidad", 1, 5);
		if (cantidad == -1) {
			JOptionPane.showMessageDialog(this, "Compra cancelada.");
			return;
		}

		boolean compraExitosa = false;
		String mensajeExito = cantidad + " tiquete(s) '" + tipoCompraSeleccionado + "' comprado(s) exitosamente.";

		switch (tipoCompraSeleccionado) {
		case "Tiquete General":
			String[] opcionesExclusividad = { "Basico", "Familiar", "Oro", "Diamante" };
			String tipoExclusividad = guiLeerOpcionDeLista("Seleccione tipo de exclusividad:", "Tiquete General",
					opcionesExclusividad);
			if (tipoExclusividad != null) {
				compraExitosa = parque.comprarTiqueteGeneral(emailCliente, tipoExclusividad, cantidad);
			}
			break;
		case "Entrada Individual":
			List<Atraccion> atrActivas = parque.getAtracciones().stream().filter(Atraccion::estaActiva).toList();
			if (atrActivas.isEmpty()) {
				JOptionPane.showMessageDialog(this, "No hay atracciones activas para comprar entrada individual.");
				return;
			}
			String[] nombresAtracciones = atrActivas.stream().map(Atraccion::getNombre).toArray(String[]::new);
			String nombreAtr = guiLeerOpcionDeLista("Seleccione la atracción:", "Entrada Individual",
					nombresAtracciones);
			if (nombreAtr != null) {
				compraExitosa = parque.comprarEntradaIndividual(emailCliente, nombreAtr, cantidad);
			}
			break;
		case "Tiquete de Temporada":
			String[] opExTemp = { "Familiar", "Oro", "Diamante" };
			String tipoExclusividadTemp = guiLeerOpcionDeLista("Exclusividad para temporada:", "Tiquete Temporada",
					opExTemp);
			if (tipoExclusividadTemp == null)
				break;

			LocalDate fechaInicio = guiLeerFechaValida("Fecha de inicio ", "Fecha Inicio Temporada", false);
			if (fechaInicio == null)
				break;
			LocalDate fechaFin;
			while (true) {
				fechaFin = guiLeerFechaValida("Fecha de fin ", "Fecha Fin Temporada", false);
				if (fechaFin == null) {
					compraExitosa = false;
					break;
				}
				if (!fechaFin.isBefore(fechaInicio))
					break;
				JOptionPane.showMessageDialog(this, "Fecha de fin debe ser posterior o igual a la de inicio.",
						"Error Fechas", JOptionPane.ERROR_MESSAGE);
			}
			if (fechaFin == null && !compraExitosa)
				break;

			compraExitosa = parque.comprarTiqueteDeTemporada(emailCliente, tipoExclusividadTemp, fechaInicio, fechaFin,
					cantidad);
			break;
		case "FastPass":
			LocalDate diaDelFastPass = guiLeerFechaValida("Fecha para el FastPass ", "Comprar FastPass", false);
			if (diaDelFastPass != null) {
				compraExitosa = parque.comprarFastPass(emailCliente, diaDelFastPass, cantidad);
			}
			break;
		}

		if (compraExitosa) {
			JOptionPane.showMessageDialog(this, mensajeExito, "Compra Exitosa", JOptionPane.INFORMATION_MESSAGE);
			parque.guardarDatos(NOMBRE_ARCHIVO_DATOS);
		} else if (seleccionIndice < tiposDeCompra.length - 1) {
			JOptionPane.showMessageDialog(this, "La compra no pudo ser completada.", "Error en Compra",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void guiVerMisTiquetesCliente() {
		List<Tiquete> misTiquetes = clienteLogueado.getTiquetes();
		if (misTiquetes == null || misTiquetes.isEmpty()) {
			JOptionPane.showMessageDialog(this, "No tienes tiquetes comprados.", "Mis Tiquetes",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		StringBuilder sb = new StringBuilder("<html><body><h2>Mis Tiquetes (" + misTiquetes.size() + ")</h2><hr>");
		NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
		currency.setMaximumFractionDigits(0);

		for (int i = 0; i < misTiquetes.size(); i++) {
			Tiquete t = misTiquetes.get(i);
			sb.append("<b>").append(i + 1).append(". ID: ").append(t.getId()).append("</b> (Tipo: ").append(t.getTipo())
					.append(")<br>");
			sb.append("Precio: ").append(currency.format(t.getPrecio()));
			sb.append(" | Creado: ").append(t.getFechaCreacion().format(DateTimeFormatter.ISO_DATE));
			sb.append(" | Usado: ").append(t.estaUsado() ? "Sí" : "No");
			sb.append(" | Impresiones: ").append(t.getNumeroDeImpresiones()).append("<br>");
			if (t.esDeTemporada())
				sb.append("Temporada: ").append(t.getFechaInicioTemporada()).append(" a ")
						.append(t.getFechaFinTemporada()).append("<br>");
			if (t.getAtraccionEspecifica() != null)
				sb.append("Para: ").append(t.getAtraccionEspecifica()).append("<br>");
			if (t.tieneFastPass())
				sb.append("FastPass para: ").append(t.getFechaEspecificaFastPass()).append("<br>");
			sb.append("<hr>");
		}
		sb.append("</body></html>");
		JEditorPane editorPane = new JEditorPane("text/html", sb.toString());
		editorPane.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(editorPane);
		scrollPane.setPreferredSize(new Dimension(550, 400));
		JOptionPane.showMessageDialog(this, scrollPane, "Mis Tiquetes", JOptionPane.INFORMATION_MESSAGE);
	}

	private void guiIntentarIngresarAtraccionCliente() {
		if (clienteLogueado.getTiquetes() == null || clienteLogueado.getTiquetes().isEmpty()) {
			JOptionPane.showMessageDialog(this, "No tienes tiquetes para usar.", "Error", JOptionPane.WARNING_MESSAGE);
			return;
		}

		List<Tiquete> misTiquetes = clienteLogueado.getTiquetes();
		String[] opcionesTiquetes = new String[misTiquetes.size()];
		for (int i = 0; i < misTiquetes.size(); i++) {
			opcionesTiquetes[i] = (i + 1) + ". " + misTiquetes.get(i).getId() + " (" + misTiquetes.get(i).getTipo()
					+ ")";
		}
		String tiqueteSeleccionadoStr = guiLeerOpcionDeLista("Selecciona el tiquete que deseas usar:",
				"Seleccionar Tiquete", opcionesTiquetes);
		if (tiqueteSeleccionadoStr == null)
			return;
		int indiceTiquete = Integer.parseInt(tiqueteSeleccionadoStr.split("\\.")[0]) - 1;
		Tiquete tiqueteAUsar = misTiquetes.get(indiceTiquete);

		List<Atraccion> atraccionesActivas = parque.getAtracciones().stream().filter(Atraccion::estaActiva).toList();
		if (atraccionesActivas.isEmpty()) {
			JOptionPane.showMessageDialog(this, "No hay atracciones activas en el parque.");
			return;
		}
		String[] nombresAtracciones = atraccionesActivas.stream().map(Atraccion::getNombre).toArray(String[]::new);
		String nombreAtrSeleccionada = guiLeerOpcionDeLista("Selecciona la atracción a la que deseas ingresar:",
				"Seleccionar Atracción", nombresAtracciones);
		if (nombreAtrSeleccionada == null)
			return;

		Atraccion atraccionAEntrar = parque.buscarAtraccionPorNombre(nombreAtrSeleccionada);
		if (atraccionAEntrar == null) {
			JOptionPane.showMessageDialog(this, "Atracción no válida.");
			return;
		}

		if (parque.visitantePuedeAccederAtraccion(clienteLogueado, tiqueteAUsar, atraccionAEntrar)) {
			JOptionPane.showMessageDialog(this,
					"Validación de tiquete por sistema: OK. Procediendo a chequeos específicos...", "Paso 1 Exitoso",
					JOptionPane.INFORMATION_MESSAGE);

			boolean cumpleRequisitosEspecificos = true;
			if (atraccionAEntrar instanceof AtraccionCultural) {
				AtraccionCultural cultural = (AtraccionCultural) atraccionAEntrar;
				if (cultural.getEdadMinima() > 0) {
					int edadVisitante = guiLeerIntConRango(
							"Por favor, ingrese su edad para '" + cultural.getNombre() + "': ", "Verificación de Edad",
							0, 120);
					if (edadVisitante == -1)
						return;
					if (!cultural.puedeIngresar(edadVisitante)) {
						JOptionPane.showMessageDialog(this, "ACCESO DENEGADO: No cumple el requisito de edad ("
								+ cultural.getEdadMinima() + " años).", "Acceso Denegado", JOptionPane.ERROR_MESSAGE);
						cumpleRequisitosEspecificos = false;
					}
				}
			} else if (atraccionAEntrar instanceof AtraccionMecanica) {
				AtraccionMecanica mecanica = (AtraccionMecanica) atraccionAEntrar;
				JPanel panelMecanica = new JPanel(new GridLayout(0, 2, 5, 5));
				JTextField alturaField = new JTextField("1.60");
				JTextField pesoField = new JTextField("60");
				JCheckBox cardiacoCheck = new JCheckBox("Declaro NO tener problemas cardiacos relevantes.");
				JCheckBox vertigoCheck = new JCheckBox("Declaro NO sufrir de vertigo incapacitante.");
				cardiacoCheck.setSelected(true);
				vertigoCheck.setSelected(true);
				panelMecanica.add(new JLabel("Su altura (metros, ej: 1.70):"));
				panelMecanica.add(alturaField);
				panelMecanica.add(new JLabel("Su peso (kg, ej: 65.0):"));
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
						boolean tieneProblemasCardiacos = !cardiacoCheck.isSelected();
						boolean tieneVertigo = !vertigoCheck.isSelected();
						if (!mecanica.puedeIngresar(altura, peso, tieneProblemasCardiacos, tieneVertigo)) {
							JOptionPane.showMessageDialog(this,
									"ACCESO DENEGADO: No cumple requisitos de seguridad de la atracción.",
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
				JOptionPane.showMessageDialog(this, "¡ACCESO CONCEDIDO a " + atraccionAEntrar.getNombre() + "!",
						"Acceso Concedido", JOptionPane.INFORMATION_MESSAGE);
				parque.guardarDatos(NOMBRE_ARCHIVO_DATOS);
			}
		}
	}
}