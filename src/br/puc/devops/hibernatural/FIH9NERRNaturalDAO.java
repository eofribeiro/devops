package br.puc.devops.hibernatural;

import org.apache.log4j.Logger;

import br.puc.devops.adabas.Natural;
import br.puc.devops.util.natapi.BaseNaturalDAO;

public class FIH9NERRNaturalDAO extends BaseNaturalDAO {

	protected static final Logger LOGGER = Logger.getLogger(FIH9NERRNaturalDAO.class);
	
	public static final String NMPROGRAMA_VERIFICACAO_MAINFRAME = "FIH9NERR";
	private static final Integer EXCEPTION_MAINFRAME = 9999;

	public FIH9NERRNaturalDAO() throws Exception {
		super(NMPROGRAMA_VERIFICACAO_MAINFRAME, Natural.PADRAO_LOCAL);
	}

//	/**
//	 * Executar no mainframe a sub-rotina FIH9NERR
//	 *
//	 * @since 04/05/2017
//	 * @param parms : FIH
//	 * @return : FIH9NERR
//	 */
//	public FIH9NERR executar(FIH9NERR parms) {
//
//		FIH9NERR fih9nerr = null;
//		try {
//			fih9nerr = (FIH9NERR) super.executar(parms, Natural.PADRAO_LOCAL);
//			fih9nerr.setRcode(this.getRc());
//			fih9nerr.setMsg(this.getMensagem());
//		} catch (Exception e) {
//			LOGGER.error("ERRO NA GRAVACAO DOS DADOS. PARAMETROS = " + parms, e);
//			fih9nerr.setRcode(EXCEPTION_MAINFRAME);
//			fih9nerr.setMsg(e.getMessage());
//		}
//
//		return fih9nerr;
//	}
	
	/**
	 * Executar no mainframe a sub-rotina FIH9NERR
	 *
	 * @since 04/05/2017
	 * @param parms : FIH
	 * @return : FIH9NERR
	 */
	public FIH9NERR executar(FIH9NERR parms, int versao) {

		FIH9NERR fih9nerr = null;
		try {
			fih9nerr = (FIH9NERR) super.executar(parms, versao, Natural.TIMEOUT_PADRAO);
			fih9nerr.setRcode(this.getRc());
			fih9nerr.setMsg(this.getMensagem());
		} catch (Exception e) {
			LOGGER.error("ERRO NA GRAVACAO DOS DADOS. PARAMETROS = " + parms, e);
			fih9nerr.setRcode(EXCEPTION_MAINFRAME);
			fih9nerr.setMsg(e.getMessage());
		}

		return fih9nerr;
	}
}
