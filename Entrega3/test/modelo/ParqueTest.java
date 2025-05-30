package modelo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;


public class ParqueTest {

	private Parque parque;

	@BeforeEach
	public void setUp() {
		parque = new Parque();
	}

	@Test
	public void testRegistroYAutenticacionUsuario() {
		boolean registroExitoso = parque.registrarCliente("MariaSol", "Cliente", "sol123", "maria.sol@example.com");
		assertTrue(registroExitoso, "Cliente MariaSol debería registrarse correctamente.");
		boolean registroDuplicado = parque.registrarCliente("MariaSol", "Cliente", "otraPass",
				"maria.otro@example.com");
		assertFalse(registroDuplicado, "No debería ser posible registrar un username ('MariaSol') que ya existe.");
		assertEquals(1, parque.getUsuarios().size(),
				"Solo debería haber un usuario registrado tras el intento de duplicado.");
		Usuario maria = parque.autenticarUsuario("MariaSol", "sol123");
		assertNotNull(maria, "MariaSol debería autenticarse correctamente.");
		if (maria != null) {
			assertEquals("maria.sol@example.com", maria.getEmail());
		}
		Usuario mariaMalPass = parque.autenticarUsuario("MariaSol", "incorrecta");
		assertNull(mariaMalPass, "Autenticación con contraseña incorrecta debería fallar.");
		Usuario noExiste = parque.autenticarUsuario("UsuarioFantasma", "pass123");
		assertNull(noExiste, "Autenticación de usuario no existente debería fallar.");
	}

	@Test
	public void testAgregarAtraccionesYDuplicados() {
		boolean agregadoCult = parque.agregarAtraccionCultural("Mundo Marino", 200, 10, "Costa", "Familiar", 0);
		assertTrue(agregadoCult, "'Mundo Marino' debería agregarse.");
		assertEquals(1, parque.getAtracciones().size());

		boolean agregadoMec = parque.agregarAtraccionMecanica("Titan Coaster", 150, 8, "Montaña", "Diamante", 1.4, 2.0,
				50, 110, true, true, "Alto");
		assertTrue(agregadoMec, "'Titan Coaster' debería agregarse.");
		assertEquals(2, parque.getAtracciones().size());

		boolean agregadoCultDup = parque.agregarAtraccionCultural("Mundo Marino", 100, 5, "Otra Costa", "Oro", 3);
		assertFalse(agregadoCultDup, "No debería agregarse atracción con nombre duplicado.");
		assertEquals(2, parque.getAtracciones().size(),
				"Número de atracciones no debe cambiar tras intento de duplicado.");
	}

	@Test
	public void testCompraEntradaIndividualExitosa() {
		parque.registrarCliente("CarlosR", "Cliente", "carlosRPass", "carlos.r@example.com");
		parque.agregarAtraccionMecanica("Vértigo Max", 40, 3, "Zona X", "Oro", 1.5, 1.9, 55, 95, false, true,
				"Extremo");

		boolean comprado = parque.comprarEntradaIndividual("carlos.r@example.com", "Vértigo Max", 1);
		assertTrue(comprado, "Compra de entrada individual para 'Vértigo Max' debería ser exitosa.");
		Usuario carlos = parque.autenticarUsuario("CarlosR", "carlosRPass");
		assertNotNull(carlos);
		assertEquals(1, carlos.getTiquetes().size());
		Tiquete t = carlos.getTiquetes().get(0);
		assertEquals("Individual_Oro", t.getTipo());
		assertEquals("Vértigo Max", t.getAtraccionEspecifica());
	}

	@Test
	public void testCompraEntradaIndividual_AtraccionNoExistente() {
		parque.registrarCliente("AnaP", "Cliente", "anaPPass", "ana.p@example.com");
		boolean comprado = parque.comprarEntradaIndividual("ana.p@example.com", "AtraccionQueNoExiste", 1);
		assertFalse(comprado, "Compra debería fallar si la atracción no existe.");
	}

	@Test
	public void testCompraTiqueteGeneral_UsuarioNoExistente() {
		parque.agregarAtraccionCultural("Show de Luces", 300, 5, "Plaza Mayor", "Familiar", 0);
		boolean comprado = parque.comprarTiqueteGeneral("nadie@example.com", "Familiar", 1);
		assertFalse(comprado, "Compra de tiquete general debería fallar si el usuario no existe.");
	}

	@Test
	public void testCompraTiqueteDeTemporada() {
		parque.registrarCliente("LauraM", "Cliente", "lauraMPass", "laura.m@example.com");
		LocalDate inicio = LocalDate.now().plusDays(1);
		LocalDate fin = LocalDate.now().plusDays(30);

		boolean comprado = parque.comprarTiqueteDeTemporada("laura.m@example.com", "Diamante", inicio, fin, 1);
		assertTrue(comprado, "Compra de tiquete de temporada debería ser exitosa.");

		Usuario laura = parque.autenticarUsuario("LauraM", "lauraMPass");
		assertNotNull(laura);
		assertEquals(1, laura.getTiquetes().size());
		Tiquete t = laura.getTiquetes().get(0);
		assertEquals("Temporada_Diamante", t.getTipo());
		assertTrue(t.esDeTemporada());
		assertEquals(inicio, t.getFechaInicioTemporada());
		assertEquals(fin, t.getFechaFinTemporada());
	}

	@Test
	public void testAcceso_TiqueteGeneralYExclusividad() {
		parque.registrarCliente("DavidB", "Cliente", "davidBPass", "david.b@example.com");
		Usuario david = parque.autenticarUsuario("DavidB", "davidBPass");
		assertNotNull(david);

		parque.agregarAtraccionCultural("Teatro de Marionetas", 100, 2, " Infantil", "Familiar", 0);
		Atraccion teatro = parque.getAtracciones().stream().filter(a -> a.getNombre().equals("Teatro de Marionetas"))
				.findFirst().get();

		parque.agregarAtraccionMecanica("Lanzadera Espacial", 30, 3, "Cosmos", "Oro", 1.4, 1.9, 50, 100, false, false,
				"Alto");
		Atraccion lanzadera = parque.getAtracciones().stream().filter(a -> a.getNombre().equals("Lanzadera Espacial"))
				.findFirst().get();

		parque.comprarTiqueteGeneral("david.b@example.com", "Familiar", 1);
		Tiquete tFamiliar = david.getTiquetes().get(0);
		assertTrue(parque.visitantePuedeAccederAtraccion(david, tFamiliar, teatro), "Familiar a Familiar OK");
		assertFalse(parque.visitantePuedeAccederAtraccion(david, tFamiliar, lanzadera), "Familiar a Oro FALLA");
		david.getTiquetes().clear();
		parque.getTiquetes().remove(tFamiliar);
		parque.comprarTiqueteGeneral("david.b@example.com", "Oro", 1);
		Tiquete tOro = david.getTiquetes().get(0);
		assertTrue(parque.visitantePuedeAccederAtraccion(david, tOro, teatro), "Oro a Familiar OK");
		assertTrue(parque.visitantePuedeAccederAtraccion(david, tOro, lanzadera), "Oro a Oro OK");
	}

	@Test
	public void testAcceso_TiqueteYaUsado() {
		parque.registrarCliente("ElenaV", "Cliente", "elenaVPass", "elena.v@example.com");
		Usuario elena = parque.autenticarUsuario("ElenaV", "elenaVPass");
		parque.agregarAtraccionCultural("Jardín Secreto", 50, 1, "Bosque", "Familiar", 0);
		Atraccion jardin = parque.getAtracciones().get(0);
		parque.comprarTiqueteGeneral("elena.v@example.com", "Familiar", 1);
		Tiquete tiqueteElena = elena.getTiquetes().get(0);
		boolean primerAcceso = parque.visitantePuedeAccederAtraccion(elena, tiqueteElena, jardin);
		assertTrue(primerAcceso, "Elena debería acceder la primera vez.");
		assertTrue(tiqueteElena.estaUsado(), "El tiquete de Elena debería estar marcado como usado.");
		boolean segundoAcceso = parque.visitantePuedeAccederAtraccion(elena, tiqueteElena, jardin);
		assertFalse(segundoAcceso, "Elena NO debería acceder la segunda vez con el mismo tiquete.");
	}
}
