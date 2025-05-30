package modelo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

public class TiqueteTest {

	private Tiquete tiqueteBase;

	@BeforeEach
	public void setUp() {
		this.tiqueteBase = new Tiquete("Individual_Familiar", false, null, null, false, null, "Aventura Selvática");
	}

	@Test
	public void testCrearTiqueteEInicializacion() {
		assertNotNull(tiqueteBase.getId(), "El ID del tiquete no debería ser null.");
		assertTrue(tiqueteBase.getId().startsWith("TQ-"), "El ID debería empezar con TQ-.");
		assertEquals("Individual_Familiar", tiqueteBase.getTipo());
		assertFalse(tiqueteBase.esDeTemporada(), "No debería ser de temporada por defecto en este setup.");
		assertNull(tiqueteBase.getFechaInicioTemporada());
		assertNull(tiqueteBase.getFechaFinTemporada());
		assertFalse(tiqueteBase.tieneFastPass(), "No debería tener FastPass por defecto en este setup.");
		assertNull(tiqueteBase.getFechaEspecificaFastPass());
		assertEquals("Aventura Selvática", tiqueteBase.getAtraccionEspecifica());
		assertFalse(tiqueteBase.estaUsado(), "Un tiquete nuevo no debería estar usado.");
		assertNotNull(tiqueteBase.getFechaCreacion(), "La fecha de creación no debería ser null.");
		assertEquals(0, tiqueteBase.getNumeroDeImpresiones(), "El número de impresiones inicial debe ser 0.");
	}

	@Test
	public void testMarcarYVerificarUso() {
		assertFalse(tiqueteBase.estaUsado(), "Inicialmente, el tiquete no está usado.");
		tiqueteBase.marcarComoUsado();
		assertTrue(tiqueteBase.estaUsado(), "Después de marcarlo, el tiquete debe estar usado.");
	}

	@Test
	public void testEstaVigenteHoy_NoDeTemporadaNoUsado() {
		assertTrue(tiqueteBase.estaVigenteHoy(), "Tiquete no de temporada y no usado debe estar vigente.");
	}

	@Test
	public void testEstaVigenteHoy_NoDeTemporadaPeroUsado() {
		tiqueteBase.marcarComoUsado();
		assertFalse(tiqueteBase.estaVigenteHoy(), "Tiquete no de temporada y usado NO debe estar vigente.");
	}

	@Test
	public void testEstaVigenteHoy_DeTemporadaEnRango() {
		LocalDate hoy = LocalDate.now();
		LocalDate inicio = hoy.minusDays(5);
		LocalDate fin = hoy.plusDays(5);
		Tiquete tiqueteTemporadaVigente = new Tiquete("Temporada_Oro", true, inicio, fin, false, null, null);
		assertTrue(tiqueteTemporadaVigente.estaVigenteHoy(), "Tiquete de temporada en rango debe estar vigente.");
	}

	@Test
	public void testEstaVigenteHoy_DeTemporadaFueraDeRangoPasado() {
		LocalDate hoy = LocalDate.now();
		LocalDate inicio = hoy.minusDays(10);
		LocalDate fin = hoy.minusDays(5);
		Tiquete tiqueteTemporadaExpirado = new Tiquete("Temporada_Oro", true, inicio, fin, false, null, null);
		assertFalse(tiqueteTemporadaExpirado.estaVigenteHoy(), "Tiquete de temporada expirado NO debe estar vigente.");
	}

	@Test
	public void testEstaVigenteHoy_DeTemporadaFueraDeRangoFuturo() {
		LocalDate hoy = LocalDate.now();
		LocalDate inicio = hoy.plusDays(5);
		LocalDate fin = hoy.plusDays(10);
		Tiquete tiqueteTemporadaFuturo = new Tiquete("Temporada_Oro", true, inicio, fin, false, null, null);
		assertFalse(tiqueteTemporadaFuturo.estaVigenteHoy(), "Tiquete de temporada futuro NO debe estar vigente hoy.");
	}

	@Test
	public void testFastPassValidoHoy() {
		LocalDate hoy = LocalDate.now();
		Tiquete tiqueteConFastPassHoy = new Tiquete("FastPass", false, null, null, true, hoy, null);
		assertTrue(tiqueteConFastPassHoy.esFastPassValidoHoy(), "FastPass para hoy debería ser válido.");
	}

	@Test
	public void testFastPassNoValidoHoy_DiaDiferente() {
		LocalDate manana = LocalDate.now().plusDays(1);
		Tiquete tiqueteConFastPassManana = new Tiquete("FastPass", false, null, null, true, manana, null);
		assertFalse(tiqueteConFastPassManana.esFastPassValidoHoy(), "FastPass para mañana NO debería ser válido hoy.");
	}

	@Test
	public void testFastPass_CuandoNoTieneFastPass() {
		assertFalse(tiqueteBase.tieneFastPass());
		assertFalse(tiqueteBase.esFastPassValidoHoy(), "Tiquete sin FastPass no debería tener FastPass válido hoy.");
	}

	@Test
	public void testPermiteAcceso_LogicaExclusividad() {
		assertTrue(tiqueteBase.permiteAcceso("Familiar"), "Individual_Familiar debería acceder a Familiar.");
		assertFalse(tiqueteBase.permiteAcceso("Oro"), "Individual_Familiar NO debería acceder a Oro.");
		assertFalse(tiqueteBase.permiteAcceso("Diamante"), "Individual_Familiar NO debería acceder a Diamante.");

		Tiquete tiqueteOro = new Tiquete("Oro", false, null, null, false, null, null);
		assertTrue(tiqueteOro.permiteAcceso("Familiar"), "Oro debería acceder a Familiar.");
		assertTrue(tiqueteOro.permiteAcceso("Oro"), "Oro debería acceder a Oro.");
		assertFalse(tiqueteOro.permiteAcceso("Diamante"), "Oro NO debería acceder a Diamante.");

		Tiquete tiqueteDiamante = new Tiquete("Diamante", false, null, null, false, null, null);
		assertTrue(tiqueteDiamante.permiteAcceso("Familiar"), "Diamante debería acceder a Familiar.");
		assertTrue(tiqueteDiamante.permiteAcceso("Oro"), "Diamante debería acceder a Oro.");
		assertTrue(tiqueteDiamante.permiteAcceso("Diamante"), "Diamante debería acceder a Diamante.");

		Tiquete tiqueteBasico = new Tiquete("Basico", false, null, null, false, null, null);
		assertFalse(tiqueteBasico.permiteAcceso("Familiar"), "Basico NO debería acceder a Familiar.");

		tiqueteOro.marcarComoUsado();
		assertFalse(tiqueteOro.permiteAcceso("Familiar"), "Tiquete Oro usado NO debería acceder a Familiar.");
	}

	@Test
	public void testRegistrarImpresion() {
		assertEquals(0, tiqueteBase.getNumeroDeImpresiones(), "Inicialmente 0 impresiones.");
		tiqueteBase.registrarImpresion();
		assertEquals(1, tiqueteBase.getNumeroDeImpresiones(), "Después de una, 1 impresión.");
		tiqueteBase.registrarImpresion();
		assertEquals(2, tiqueteBase.getNumeroDeImpresiones(), "Después de dos, 2 impresiones.");
	}
}