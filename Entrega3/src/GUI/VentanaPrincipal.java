package GUI;

import javax.swing.*;
import modelo.Parque;
import modelo.Usuario;
import modelo.Empleado;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.util.Arrays;
import java.util.List;

public class VentanaPrincipal extends JFrame {
	private static final long serialVersionUID = 1L;
	private Parque parque;
	private static final String NOMBRE_ARCHIVO_DATOS = "datos_parque.dat";

	class BackgroundPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private Image backgroundImage;
		private Color backgroundColor = new Color(255, 204, 153);

		public BackgroundPanel(String nombreImagenFondo) {
			if (nombreImagenFondo != null) {
				InputStream inputStream = null;
				try {
					inputStream = getClass().getResourceAsStream(nombreImagenFondo);
					if (inputStream != null) {
						backgroundImage = ImageIO.read(inputStream);

					} else {
						System.err.println("Error: Imagen de fondo general '" + nombreImagenFondo
								+ "' no encontrada en el paquete GUI.");
					}
				} catch (Exception e) {
					System.err.println("Excepción al cargar imagen de fondo general: " + e.getMessage());
				} finally {
					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (java.io.IOException e) {
						}
					}
				}
			}
			setLayout(new BorderLayout());
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (backgroundImage != null) {
				g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
			} else if (backgroundColor != null) {
				g.setColor(backgroundColor);
				g.fillRect(0, 0, getWidth(), getHeight());
			} else {
				g.setColor(Color.LIGHT_GRAY);
				g.fillRect(0, 0, getWidth(), getHeight());
			}
		}
	}

	public VentanaPrincipal(Parque parque) {
		this.parque = parque;
		setTitle("Parque de Atracciones de los Alpes");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (VentanaPrincipal.this.parque != null) {
					VentanaPrincipal.this.parque.guardarDatos(NOMBRE_ARCHIVO_DATOS);
					System.out.println("Datos guardados al cerrar la aplicación.");
				}
				System.exit(0);
			}
		});

		BackgroundPanel panelDeFondo = new BackgroundPanel(null);
		setContentPane(panelDeFondo);

		JPanel panelContenidoPrincipal = new JPanel();
		panelContenidoPrincipal.setLayout(new BoxLayout(panelContenidoPrincipal, BoxLayout.Y_AXIS));
		panelContenidoPrincipal.setOpaque(false);

		JLabel lblTituloGrande = new JLabel("Bienvenido al Parque de Atracciones de los Alpes", SwingConstants.CENTER);
		lblTituloGrande.setFont(new Font("Serif", Font.BOLD, 32));
		lblTituloGrande.setForeground(new Color(80, 80, 80));
		lblTituloGrande.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblTituloGrande.setBorder(BorderFactory.createEmptyBorder(25, 20, 15, 20));
		panelContenidoPrincipal.add(lblTituloGrande);

		URL imageUrlIntermedia = getClass().getResource("foto1.png");
		JLabel lblImagenIntermedia = new JLabel();
		if (imageUrlIntermedia != null) {
			ImageIcon icono = new ImageIcon(imageUrlIntermedia);
			lblImagenIntermedia.setIcon(icono);
		} else {
			lblImagenIntermedia.setText(" [ Imagen 'foto1.png' no encontrada en paquete GUI ] ");
			lblImagenIntermedia.setForeground(Color.RED);
		}
		lblImagenIntermedia.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblImagenIntermedia.setBorder(BorderFactory.createEmptyBorder(15, 0, 20, 0));
		panelContenidoPrincipal.add(lblImagenIntermedia);

		JPanel panelBotones = new JPanel();
		panelBotones.setLayout(new GridLayout(3, 1, 15, 15));
		panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 120, 30, 120));
		panelBotones.setOpaque(false);

		JButton clienteBtn = new JButton("Portal Cliente");
		JButton empleadoBtn = new JButton("Portal Empleado");
		JButton adminBtn = new JButton("Portal Administrador");

		Font botonFont = new Font("Arial", Font.BOLD, 16);

		for (JButton btn : Arrays.asList(clienteBtn, empleadoBtn, adminBtn)) {
			btn.setFont(botonFont);
		}

		clienteBtn.addActionListener(e -> {
			String[] opciones = { "Iniciar Sesión", "Registrarse como Nuevo Cliente", "Cancelar" };
			int seleccion = JOptionPane.showOptionDialog(this, "Bienvenido al Portal Cliente", "Portal Cliente",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
			if (seleccion == 0) {
				guiLoginCliente();
			} else if (seleccion == 1) {
				guiRegistrarNuevoCliente();
			}
		});
		empleadoBtn.addActionListener(e -> { /* ... tu lógica de login empleado ... */
			String usernameEmp = JOptionPane.showInputDialog(this, "Ingrese su Username (ID de Empleado):",
					"Login Empleado", JOptionPane.PLAIN_MESSAGE);
			if (usernameEmp == null || usernameEmp.trim().isEmpty())
				return;
			String passwordEmp = JOptionPane.showInputDialog(this, "Ingrese su Password:", "Login Empleado",
					JOptionPane.PLAIN_MESSAGE);
			if (passwordEmp == null)
				return;
			Usuario usuarioAutenticado = parque.autenticarUsuario(usernameEmp.trim(), passwordEmp);
			if (usuarioAutenticado != null && esRolDeEmpleado(usuarioAutenticado.getRol())) {
				Empleado empleadoObjeto = parque.buscarEmpleadoPorId(usuarioAutenticado.getUsername());
				if (empleadoObjeto != null) {
					new VentanaEmpleado(parque, usuarioAutenticado, empleadoObjeto).setVisible(true);
				} else {
					JOptionPane.showMessageDialog(this, "Perfil de empleado no encontrado.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(this, "Autenticación de empleado fallida o rol incorrecto.",
						"Error de Login", JOptionPane.ERROR_MESSAGE);
			}
		});
		adminBtn.addActionListener(e -> { /* ... tu lógica de login admin ... */
			String usernameAdmin = JOptionPane.showInputDialog(this, "Username Administrador:", "Login Admin",
					JOptionPane.PLAIN_MESSAGE);
			if (usernameAdmin == null || usernameAdmin.trim().isEmpty())
				return;
			String passwordAdmin = JOptionPane.showInputDialog(this, "Password Administrador:", "Login Admin",
					JOptionPane.PLAIN_MESSAGE);
			if (passwordAdmin == null)
				return;
			Usuario adminAutenticado = parque.autenticarUsuario(usernameAdmin.trim(), passwordAdmin);
			if (adminAutenticado != null && adminAutenticado.getRol().equalsIgnoreCase("Administrador")) {
				new VentanaAdmin(parque, adminAutenticado).setVisible(true);
			} else {
				JOptionPane.showMessageDialog(this, "Autenticación de administrador fallida o rol incorrecto.",
						"Error de Login", JOptionPane.ERROR_MESSAGE);
			}
		});

		panelBotones.add(clienteBtn);
		panelBotones.add(empleadoBtn);
		panelBotones.add(adminBtn);

		panelContenidoPrincipal.add(panelBotones);
		panelDeFondo.add(panelContenidoPrincipal, BorderLayout.CENTER);

		pack();
		setMinimumSize(new Dimension(700, 650));
		setLocationRelativeTo(null);
	}

	private void guiLoginCliente() {
		String username = JOptionPane.showInputDialog(this, "Ingrese su username de cliente:", "Login Cliente",
				JOptionPane.PLAIN_MESSAGE);
		if (username == null || username.trim().isEmpty())
			return;
		String password = JOptionPane.showInputDialog(this, "Ingrese su password:", "Login Cliente",
				JOptionPane.PLAIN_MESSAGE);
		if (password == null)
			return;
		Usuario usuarioAutenticado = parque.autenticarUsuario(username.trim(), password);
		if (usuarioAutenticado != null && usuarioAutenticado.getRol().equalsIgnoreCase("Cliente")) {
			new VentanaCliente(parque, usuarioAutenticado).setVisible(true);
		} else {
			JOptionPane.showMessageDialog(this, "Autenticación fallida o rol incorrecto.", "Error de Login",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void guiRegistrarNuevoCliente() {
		JTextField usernameField = new JTextField();
		JPasswordField passwordField = new JPasswordField();
		JTextField emailField = new JTextField();
		JPanel panelRegistro = new JPanel(new GridLayout(0, 2, 5, 5));
		panelRegistro.add(new JLabel("Username (para login):"));
		panelRegistro.add(usernameField);
		panelRegistro.add(new JLabel("Password:"));
		panelRegistro.add(passwordField);
		panelRegistro.add(new JLabel("Email:"));
		panelRegistro.add(emailField);
		int result = JOptionPane.showConfirmDialog(this, panelRegistro, "Registro de Nuevo Cliente",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result == JOptionPane.OK_OPTION) {
			String username = usernameField.getText().trim();
			String password = new String(passwordField.getPassword());
			String email = emailField.getText().trim();
			if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (!email.contains("@") || !email.contains(".")) {
				JOptionPane.showMessageDialog(this, "Formato de email inválido.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (parque.buscarUsuarioPorUsername(username) != null) {
				JOptionPane.showMessageDialog(this, "El username ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			boolean emailExiste = false;
			for (Usuario u : parque.getUsuarios()) {
				if (u.getEmail() != null && u.getEmail().equalsIgnoreCase(email)) {
					emailExiste = true;
					break;
				}
			}
			if (emailExiste) {
				JOptionPane.showMessageDialog(this, "El email ya está registrado.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (parque.registrarCliente(username, "Cliente", password, email)) {
				JOptionPane.showMessageDialog(this, "¡Registro exitoso! Ahora puede iniciar sesión.",
						"Registro Completado", JOptionPane.INFORMATION_MESSAGE);
				parque.guardarDatos(NOMBRE_ARCHIVO_DATOS);
			} else {
				JOptionPane.showMessageDialog(this, "Error en el registro.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private boolean esRolDeEmpleado(String rol) {
		if (rol == null)
			return false;
		return rol.equalsIgnoreCase("Operador") || rol.equalsIgnoreCase("Cajero") || rol.equalsIgnoreCase("Cocinero")
				|| rol.equalsIgnoreCase("Servicio General") || rol.equalsIgnoreCase("Empleado");
	}

}