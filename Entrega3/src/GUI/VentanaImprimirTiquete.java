package GUI;

import javax.swing.*;
import modelo.Parque;
import modelo.Tiquete;
import modelo.Usuario;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class VentanaImprimirTiquete extends JFrame {
	private static final long serialVersionUID = 1L;
	private Parque parque;
	private static final String NOMBRE_ARCHIVO_DATOS = "datos_parque.dat";

	private static final int QR_ANCHO_ALTO = 150;
	private static final int SIMBOLO_ANCHO_OBJETIVO = QR_ANCHO_ALTO;
	private static final int DECORATIVA_ALTO_OBJETIVO = QR_ANCHO_ALTO;

	public VentanaImprimirTiquete(Parque parque, Usuario clienteLogueado) {
		this.parque = parque;

		setTitle("Visualización de Tiquete - " + clienteLogueado.getUsername());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		List<Tiquete> misTiquetes = clienteLogueado.getTiquetes();
		if (misTiquetes == null || misTiquetes.isEmpty()) {
			JOptionPane.showMessageDialog(this, "No tienes tiquetes comprados para imprimir/visualizar.",
					"Sin Tiquetes", JOptionPane.INFORMATION_MESSAGE);
			SwingUtilities.invokeLater(() -> dispose());
			return;
		}

		String[] opcionesTiquetes = new String[misTiquetes.size()];
		for (int i = 0; i < misTiquetes.size(); i++) {
			Tiquete t = misTiquetes.get(i);
			opcionesTiquetes[i] = t.getId() + " - " + t.getTipo()
					+ (t.getAtraccionEspecifica() != null ? " (" + t.getAtraccionEspecifica() + ")" : "");
		}

		String tiqueteSeleccionadoStr = (String) JOptionPane.showInputDialog(this,
				"Seleccione el tiquete que desea visualizar/imprimir:", "Seleccionar Tiquete",
				JOptionPane.PLAIN_MESSAGE, null, opcionesTiquetes,
				opcionesTiquetes.length > 0 ? opcionesTiquetes[0] : null);

		if (tiqueteSeleccionadoStr == null) {
			SwingUtilities.invokeLater(() -> dispose());
			return;
		}

		String idTiqueteSeleccionado = tiqueteSeleccionadoStr.split(" - ")[0];
		Tiquete tiqueteAImprimir = null;
		for (Tiquete t : misTiquetes) {
			if (t.getId().equals(idTiqueteSeleccionado)) {
				tiqueteAImprimir = t;
				break;
			}
		}

		if (tiqueteAImprimir == null) {
			JOptionPane.showMessageDialog(this, "Error al seleccionar el tiquete.", "Error", JOptionPane.ERROR_MESSAGE);
			SwingUtilities.invokeLater(() -> dispose());
			return;
		}

		boolean procederConImpresion = false;
		if (tiqueteAImprimir.getNumeroDeImpresiones() == 0) {
			procederConImpresion = true;
		} else {
			int confirmacion = JOptionPane.showConfirmDialog(this,
					"Este tiquete (ID: " + tiqueteAImprimir.getId() + ") ya ha sido impreso "
							+ tiqueteAImprimir.getNumeroDeImpresiones() + " veces.\n¿Desea generarlo de nuevo?",
					"Confirmar Visualización/Reimpresión", JOptionPane.YES_NO_OPTION);
			if (confirmacion == JOptionPane.YES_OPTION) {
				procederConImpresion = true;
			}
		}

		if (!procederConImpresion) {
			SwingUtilities.invokeLater(() -> dispose());
			return;
		}

		JPanel panelTiquete = new JPanel(new BorderLayout(10, 10));
		panelTiquete.setBackground(new Color(250, 220, 190));
		panelTiquete.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));

		JLabel lblTituloParque = new JLabel("Parque de Atracciones de los Alpes", SwingConstants.CENTER);
		lblTituloParque.setFont(new Font("Serif", Font.BOLD, 22));
		lblTituloParque.setOpaque(false);
		lblTituloParque.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));
		panelTiquete.add(lblTituloParque, BorderLayout.NORTH);

		JPanel panelCentral = new JPanel(new BorderLayout(15, 5));
		panelCentral.setOpaque(false);

		JPanel panelInfoIzquierda = new JPanel();
		panelInfoIzquierda.setLayout(new BoxLayout(panelInfoIzquierda, BoxLayout.Y_AXIS));
		panelInfoIzquierda.setOpaque(false);
		panelInfoIzquierda.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 5));

		DateTimeFormatter formatterFechaExpVisual = DateTimeFormatter.ofPattern("dd-M-yyyy");
		DateTimeFormatter formatterFechaLargaPie = DateTimeFormatter.ofPattern("EEE dd-M-yyyy", new Locale("es", "ES"));
		String fechaExpedicionVisual = tiqueteAImprimir.getFechaCreacion().format(formatterFechaExpVisual);
		String fechaPieFormateada = tiqueteAImprimir.getFechaCreacion().format(formatterFechaLargaPie).toUpperCase();

		NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
		currencyFormatter.setMaximumFractionDigits(0);
		String precioFormateado = currencyFormatter.format(tiqueteAImprimir.getPrecio());

		StringBuilder infoHtml = new StringBuilder("<html><body style='font-family: Arial; font-size: 9pt;'>");
		infoHtml.append("<b>No.:</b> ").append(tiqueteAImprimir.getId()).append("<br>");
		infoHtml.append("<b>Tiquete:</b> ").append(tiqueteAImprimir.getTipo().toUpperCase()).append("<br>");
		infoHtml.append("<b>Fecha Expedición:</b> ").append(fechaExpedicionVisual).append("<br>");
		infoHtml.append("<b>Valor:</b> ").append(precioFormateado).append("<br>");
		infoHtml.append("</body></html>");

		JEditorPane infoLabel = new JEditorPane("text/html", infoHtml.toString());
		infoLabel.setEditable(false);
		infoLabel.setOpaque(false);
		infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelInfoIzquierda.add(infoLabel);
		panelInfoIzquierda.add(Box.createVerticalStrut(10));

		String tipoParaSimbolo = tiqueteAImprimir.getTipo().toLowerCase();
		String nombreArchivoSimbolo = null;
		if (tipoParaSimbolo.contains("diamante"))
			nombreArchivoSimbolo = "diamante.png";
		else if (tipoParaSimbolo.contains("oro"))
			nombreArchivoSimbolo = "oro.png";
		else if (tipoParaSimbolo.contains("familiar"))
			nombreArchivoSimbolo = "familiar.png";
		else if (tipoParaSimbolo.equals("basico"))
			nombreArchivoSimbolo = "basico.png";
		else if (tipoParaSimbolo.equals("fastpass"))
			nombreArchivoSimbolo = "fastpass.png";

		if (nombreArchivoSimbolo != null) {
			try {
				URL simboloUrl = getClass().getResource(nombreArchivoSimbolo);
				if (simboloUrl != null) {
					ImageIcon simboloIcon = new ImageIcon(simboloUrl);
					Image img = simboloIcon.getImage().getScaledInstance(SIMBOLO_ANCHO_OBJETIVO, -1,
							Image.SCALE_SMOOTH);
					JLabel lblSimbolo = new JLabel(new ImageIcon(img));
					lblSimbolo.setAlignmentX(Component.LEFT_ALIGNMENT);
					panelInfoIzquierda.add(lblSimbolo);
				} else {
					panelInfoIzquierda.add(new JLabel("[" + tiqueteAImprimir.getTipo() + "]"));
				}
			} catch (Exception e) {
				panelInfoIzquierda.add(new JLabel("[Err Símbolo]"));
			}
		} else {
			panelInfoIzquierda.add(Box
					.createRigidArea(new Dimension(0, SIMBOLO_ANCHO_OBJETIVO > 0 ? SIMBOLO_ANCHO_OBJETIVO / 2 : 20)));
		}

		panelInfoIzquierda.add(Box.createVerticalGlue());
		panelCentral.add(panelInfoIzquierda, BorderLayout.WEST);

		JPanel panelImagenDecorativa = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panelImagenDecorativa.setOpaque(false);
		panelImagenDecorativa.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		try {
			URL parqueImageUrl = getClass().getResource("foto1.png");
			if (parqueImageUrl != null) {
				ImageIcon parqueIcon = new ImageIcon(parqueImageUrl);
				Image img = parqueIcon.getImage().getScaledInstance(-1, DECORATIVA_ALTO_OBJETIVO, Image.SCALE_SMOOTH);
				JLabel lblParqueImg = new JLabel(new ImageIcon(img));
				panelImagenDecorativa.add(lblParqueImg);
			} else {
				panelImagenDecorativa.add(new JLabel("[Imagen Parque]"));
			}
		} catch (Exception e) {
			panelImagenDecorativa.add(new JLabel("[Err Img Parque]"));
		}
		panelCentral.add(panelImagenDecorativa, BorderLayout.CENTER);

		JPanel panelQR = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panelQR.setOpaque(false);
		panelQR.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 15));
		LocalDate fechaImpresionActual = LocalDate.now();
		String fechaImpresionQRFormateada = fechaImpresionActual.format(DateTimeFormatter.ISO_LOCAL_DATE);
		String datosParaQR = "Tipo: " + tiqueteAImprimir.getTipo() + "\n" + "ID: " + tiqueteAImprimir.getId() + "\n"
				+ "Impreso: " + fechaImpresionQRFormateada;
		BufferedImage qrImage = QRUtils.generarQR(datosParaQR, QR_ANCHO_ALTO, QR_ANCHO_ALTO);
		JLabel qrLabel = new JLabel();
		if (qrImage != null) {
			qrLabel.setIcon(new ImageIcon(qrImage));
		} else {
			qrLabel.setText("Error QR");
		}
		panelQR.add(qrLabel);
		panelCentral.add(panelQR, BorderLayout.EAST);

		panelTiquete.add(panelCentral, BorderLayout.CENTER);

		JLabel lblPieFecha = new JLabel(fechaPieFormateada, SwingConstants.CENTER);
		lblPieFecha.setFont(new Font("Monospaced", Font.BOLD, 16));
		lblPieFecha.setOpaque(false);
		lblPieFecha.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
		panelTiquete.add(lblPieFecha, BorderLayout.SOUTH);

		tiqueteAImprimir.registrarImpresion();
		parque.guardarDatos(NOMBRE_ARCHIVO_DATOS);
		System.out.println("Visualización/Impresión registrada para tiquete " + tiqueteAImprimir.getId()
				+ ". Contador de impresiones: " + tiqueteAImprimir.getNumeroDeImpresiones());

		add(panelTiquete);
		pack();
		setMinimumSize(new Dimension(600, 500));
		setLocationRelativeTo(this.getOwner());
	}
}