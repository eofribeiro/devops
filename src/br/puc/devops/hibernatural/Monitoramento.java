package br.puc.devops.hibernatural;

import org.apache.log4j.Logger;

import br.puc.devops.adabas.Natural;

public class Monitoramento {

	private static final Logger LOGGER = Logger.getLogger(Monitoramento.class);
	
	private static final boolean ISPRODUCAO = false;

	/**
	 * Teste do Mainframe via sub-rotina FIH9NERR
	 *
	 * @return Boolean
	 * @throws Exception
	 */
	public static boolean testarMainframe() throws Exception {
		FIH9NERRNaturalDAO dao = new FIH9NERRNaturalDAO();
		FIH9NERR fih = new FIH9NERR();
		fih.setDbid(ISPRODUCAO ? "009" : "007");
		FIH9NERR fihRetorno = dao.executar(fih, Natural.PADRAO_LOCAL);
		return fihRetorno.getRcode().equals(0);
	}
	
	
	public static void main(String[] args) {
		try {
			testarMainframe();
		} catch (Exception e) {
			//e.printStackTrace();
			LOGGER.error("ERRO TESTANDO MAINFRAME", e);
		}
	}

}
