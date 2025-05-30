package modelo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

public class UsuarioTest {

	private Usuario usuario;

	@BeforeEach
	public void setUp() {
		usuario = new Usuario("Laura", "secreta", "Cliente", "laura@correo.com");
	}

	@Test
	public void testCrearUsuarioEInicializacion() {
		assertEquals("Laura", usuario.getUsername());
		assertEquals("secreta", usuario.getPassword());
		assertEquals("Cliente", usuario.getRol());
		assertEquals("laura@correo.com", usuario.getEmail());
		assertNotNull(usuario.getTiquetes(), "La lista de tiquetes no debería ser null.");
		assertTrue(usuario.getTiquetes().isEmpty(), "La lista de tiquetes debería estar vacía inicialmente.");
	}

	@Test
	public void testAutenticar() {
		assertTrue(usuario.autenticar("secreta"), "Debería autenticar con la contraseña correcta.");
		assertFalse(usuario.autenticar("incorrecta"), "No debería autenticar con contraseña incorrecta.");
		assertFalse(usuario.autenticar(null), "No debería autenticar con contraseña null.");
		assertFalse(usuario.autenticar(""), "No debería autenticar con contraseña vacía.");
	}

	@Test
	public void testAgregarYTenerTiquetes() {
		assertTrue(usuario.getTiquetes().isEmpty(), "Inicialmente, el usuario no tiene tiquetes.");
		Tiquete tiquete1 = new Tiquete("Individual_Oro", false, null, null, false, null, "Rueda de la Fortuna");
		usuario.agregarTiquete(tiquete1);
		assertEquals(1, usuario.getTiquetes().size(), "El usuario debería tener 1 tiquete.");
		assertTrue(usuario.getTiquetes().contains(tiquete1),
				"La lista de tiquetes debería contener el tiquete añadido.");
		Tiquete tiquete2 = new Tiquete("Temporada_Diamante", true, LocalDate.now(), LocalDate.now().plusMonths(1),
				false, null, null);
		usuario.agregarTiquete(tiquete2);
		assertEquals(2, usuario.getTiquetes().size(), "El usuario debería tener 2 tiquetes.");
		assertTrue(usuario.getTiquetes().contains(tiquete2));
	}

	@Test
	public void testCambiarEmail() {
		String nuevoEmail = "laura.actualizada@example.org";
		usuario.setEmail(nuevoEmail);
		assertEquals(nuevoEmail, usuario.getEmail(), "El email debería haberse actualizado al nuevo valor.");
	}

}