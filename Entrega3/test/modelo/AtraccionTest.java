package modelo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AtraccionTest {

	private AtraccionMecanica atraccion;

	@BeforeEach
	public void setUp() {
		atraccion = new AtraccionMecanica("Montaña Rusa Extrema", 120, 8, "Zona Adrenalina", "Diamante", 1.45, 1.95,
				55.0, 115.0, true, true, "Alto");
	}

	@Test
	public void testCrearAtraccionMecanica() {
		assertEquals("Montaña Rusa Extrema", atraccion.getNombre());

		assertTrue(atraccion.estaActiva());
		assertEquals("Diamante", atraccion.getExclusividad());
		assertEquals(120, atraccion.getCapacidadMaxima());
		assertEquals("Alto", atraccion.getRiesgo());
	}

	@Test
	public void testActivarDesactivarAtraccion() {
		atraccion.desactivar();
		assertFalse(atraccion.estaActiva(), "La atracción debería estar inactiva después de desactivar.");
		atraccion.activar();
		assertTrue(atraccion.estaActiva(), "La atracción debería estar activa después de activar.");
	}

	@Test
	public void testPuedeIngresar_CumpleTodo() {
		assertTrue(atraccion.puedeIngresar(1.60, 70.0, false, false), "Debería poder ingresar si cumple todo.");
	}

	@Test
	public void testPuedeIngresar_FallaPorAlturaMinima() {
		assertFalse(atraccion.puedeIngresar(1.30, 70.0, false, false), "No debería ingresar por ser muy bajo.");
	}

	@Test
	public void testPuedeIngresar_FallaPorAlturaMaxima() {
		assertFalse(atraccion.puedeIngresar(2.00, 70.0, false, false), "No debería ingresar por ser muy alto.");
	}

	@Test
	public void testPuedeIngresar_FallaPorPesoMinimo() {
		assertFalse(atraccion.puedeIngresar(1.60, 45.0, false, false), "No debería ingresar por peso muy bajo.");
	}

	@Test
	public void testPuedeIngresar_FallaPorPesoMaximo() {
		assertFalse(atraccion.puedeIngresar(1.60, 120.0, false, false), "No debería ingresar por peso muy alto.");
	}

	@Test
	public void testPuedeIngresar_FallaPorCardiaca() {
		assertFalse(atraccion.puedeIngresar(1.60, 70.0, true, false),
				"No debería ingresar si tiene problema cardiaco y hay contraindicación.");
	}

	@Test
	public void testPuedeIngresar_FallaPorVertigo() {
		assertFalse(atraccion.puedeIngresar(1.60, 70.0, false, true),
				"No debería ingresar si tiene vertigo y hay contraindicación.");
	}

	@Test
	public void testGetTipo() {
		assertEquals("Mecánica", atraccion.getTipo(), "El tipo de la atracción mecánica debe ser 'Mecánica'.");
	}

	@Test
	public void testPuedeIngresar_CondicionMedicaPeroAtraccionNoRestringe() {
		AtraccionMecanica atraccionSinRestriccionCardiaca = new AtraccionMecanica("Paseo Tranquilo", 80, 4,
				"Zona Familiar", "Familiar", 1.00, 1.80, 30.0, 100.0, false, true, "Bajo");
		assertTrue(atraccionSinRestriccionCardiaca.puedeIngresar(1.60, 70.0, true, false),
				"Debería poder ingresar si tiene problema cardiaco PERO la atracción no lo restringe.");
	}

	@Test
	public void testPuedeIngresar_CondicionVertigoPeroAtraccionNoRestringe() {
		AtraccionMecanica atraccionSinRestriccionVertigo = new AtraccionMecanica("Mirador Giratorio", 50, 2,
				"Colina Vista", "Familiar", 0.80, 2.00, 25.0, 120.0, true, false, "Bajo");

		assertTrue(atraccionSinRestriccionVertigo.puedeIngresar(1.60, 70.0, false, true),
				"Debería poder ingresar si tiene vertigo PERO la atracción no lo restringe.");
	}

	@Test
	public void testPuedeIngresar_FallaPorCardiacaConAtraccionPorDefecto() {
		assertFalse(this.atraccion.puedeIngresar(1.60, 70.0, true, false),
				"No debería ingresar si tiene problema cardiaco y la atracción por defecto lo restringe.");
	}

}