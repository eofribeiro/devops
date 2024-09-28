package br.puc.devops.test;

import org.junit.Test;

import br.puc.devops.escola.EscolaExemplo;
import br.puc.devops.petstore.PetStore1;
import br.puc.devops.util.natapi.Util;
import junit.framework.TestCase;

public class TestarSistema extends TestCase {

	/**
	 * Testa se o aplicativo de escola está funcionando
	 */
	@Test
	public void testeEscola() {

		EscolaExemplo escola = null;

		try {
			escola = new EscolaExemplo();
			escola.main(null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertNotNull(escola);
	}

	/**
	 * Testa se o aplicativo de Petstore está funcionando
	 */
	@Test
	public void testePetstore() {
		PetStore1 pet = null;

		try {
			pet = new PetStore1();
			pet.listarAnimais();
		} catch (Exception e) {
			e.printStackTrace();
			pet = null;
		}

		assertNotNull(pet);
	}

	/**
	 * Testa se a rotina de validação de CPF está funcionando
	 */

	@Test
	public void testeValidaCpf() {
		boolean resultado = false;

		try {
			String cpfOk = "029.304.009-51";
			resultado = Util.validarCPF(cpfOk);
		} catch (Exception e) {
			e.printStackTrace();
			resultado = false;
		}

		assertTrue(resultado);

	}

	/**
	 * Testa se a rotina de validação de CPF inválido está funcionando
	 */
	@Test
	public void testeErroCpf() {
		boolean resultado = false;

		try {
			Long cpfOk = 123456788l;
			resultado = Util.validarCPF(cpfOk);
		} catch (Exception e) {
			e.printStackTrace();
			resultado = false;
		}

		assertFalse(resultado);
	}

	/**
	 * Testa se a rotina de validação de CNPJ inválido está funcionando
	 */
	@Test
	public void testeErroCnpj() {
		boolean resultado = false;

		try {
			Long cnpjNOk = 123456788l;
			resultado = Util.validarCNPJ(cnpjNOk);
		} catch (Exception e) {
			e.printStackTrace();
			resultado = false;
		}

		assertFalse(resultado);
	}

	/**
	 * Testa se a rotina de validação de CNPJ está funcionando
	 */
	@Test
	public void testeValidaCnpj() {
		boolean resultado = false;

		try {
			String cnpjOk = "76.545.011/0003-80";
			resultado = Util.validarCNPJ(cnpjOk);
		} catch (Exception e) {
			e.printStackTrace();
			resultado = false;
		}

		assertTrue(resultado);
	}

}
