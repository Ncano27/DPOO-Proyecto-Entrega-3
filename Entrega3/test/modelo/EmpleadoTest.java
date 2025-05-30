package modelo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EmpleadoTest {

	private Empleado empleado;

	@BeforeEach
	public void setUp() {
		empleado = new Empleado("Nicolas", "201731916", "Operador");
	}

	@Test
	public void testCrearEmpleado() {
		assertEquals("Nicolas", empleado.getNombre());
		assertEquals("201731916", empleado.getId());
		assertEquals("Operador", empleado.getTipo());

	}

	@Test
	public void testCambiarTipo() {
		empleado.setTipo("Supervisor");
		assertEquals("Supervisor", empleado.getTipo(),
				"El tipo del empleado debería haberse actualizado a Supervisor.");
	}

	@Test
	public void testCapacitacionYAptitudOperar() {
		assertFalse(empleado.puedeOperar("Montaña Rusa"), "Inicialmente no debería poder operar la Montaña Rusa.");
		empleado.agregarCapacitacion("Montaña Rusa");
		assertTrue(empleado.puedeOperar("Montaña Rusa"),
				"Debería poder operar la Montaña Rusa después de la capacitación.");
		assertFalse(empleado.puedeOperar("Casa del Terror"),
				"No debería poder operar una atracción para la que no está capacitado.");
	}

	@Test
	public void testAsignacionTurnos() {
		assertFalse(empleado.estaAsignadoATurno("Lunes_Mañana"), "Inicialmente no debería estar asignado al turno.");
		empleado.asignarTurno("Lunes_Mañana");
		assertTrue(empleado.estaAsignadoATurno("Lunes_Mañana"),
				"Debería estar asignado al turno después de la asignación.");
		assertTrue(empleado.getTurnosAsignados().contains("Lunes_Mañana"));
		assertEquals(1, empleado.getTurnosAsignados().size());
	}

	@Test
	public void testHabilidadesPorTipo_Operador() {
		assertFalse(empleado.puedeCubrirCaja(), "Un Operador por defecto no debería poder cubrir caja.");
		assertFalse(empleado.puedeCocinar(), "Un Operador no debería poder cocinar.");
	}

	@Test
	public void testHabilidadesPorTipo_Cajero() {
		empleado.setTipo("Cajero");
		assertTrue(empleado.puedeCubrirCaja(), "Un Cajero debería poder cubrir caja.");
		assertFalse(empleado.puedeCocinar(), "Un Cajero no debería poder cocinar.");
	}

	@Test
	public void testHabilidadesPorTipo_Cocinero() {
		empleado.setTipo("Cocinero");
		assertTrue(empleado.puedeCubrirCaja(), "Un Cocinero debería poder cubrir caja.");
		assertTrue(empleado.puedeCocinar(), "Un Cocinero debería poder cocinar.");
	}
}