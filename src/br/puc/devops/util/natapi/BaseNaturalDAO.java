package br.puc.devops.util.natapi;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import br.puc.devops.adabas.Natural;

/**
 * Classe basica para acesso ao mainframe via Natural API.
 * 
 * @author rodrigo.hjort
 * @author evelyneribeiro
 * 
 */
public class BaseNaturalDAO {

	private static final Integer ERRO_FATAL = 8000;

	protected String programa = null;

	protected String mensagem, entrada, saida, linha;
	private Integer rc;

	private String saidaDebug = null;
	private String entradaDebug = null;
	private Boolean entradaMaiusculas = null;

	private Boolean usarCache;
	private Integer tempoCache;
	private String chaveUsuarioCache;

	private static Logger log = Logger.getLogger(BaseNaturalDAO.class);

	private Entrada ent;
	private Saida sai;
	private int offset;
	private int posicao = 0;

	public static void main(String[] args) {
		for (int ii = 0; ii < args.length; ii++) {
			BaseNaturalDAO base = null;
			try {
				base = new BaseNaturalDAO(args[ii], Natural.PADRAO_LOCAL);
			} catch (Exception e) {
				e.printStackTrace();
			}
			base.gerarCodigoJava();
		}
	}

	
	public BaseNaturalDAO() {
		super();
	}

	/**
	 * Construtor da classe que especifica o programa.
	 * 
	 * @param prog
	 *            nome do programa no ADABAS (ex: "DUTHN803")
	 * @author rodrigo.hjort
	 * @throws Exception
	 */
	public BaseNaturalDAO(String prog) throws Exception {
		this.programa = prog;
		// padro no utilizar cache
		setUsarCache(false);
		lerMapeamento(Natural.PADRAO_JBOSS);
	}
	

	/**
	 * Construtor da classe que especifica o programa.
	 * 
	 * @param prog
	 *            nome do programa no ADABAS (ex: "DUTHN803")
	 * @author rodrigo.hjort
	 * @throws Exception
	 */
	public BaseNaturalDAO(String prog, int versao) throws Exception {
		this.programa = prog;
		// padro no utilizar cache
		setUsarCache(false);
		lerMapeamento(versao);
	}

	public Object executar(Object objen) throws BaseNaturalException, Exception {
		return executar(objen, Natural.PADRAO_JBOSS, Natural.TIMEOUT_PADRAO);
	}
	
	public Object executar(Object objen, int timeout) throws BaseNaturalException, Exception {
		return executar(objen, Natural.PADRAO_JBOSS, timeout);
	}
	
	/**
	 * Executa comando via Natural API e carrega os campos de cdigo de retorno, mensagem e sada (dados).
	 * 
	 * @param entrada
	 *            texto parametrizado para o programa em questo
	 * @throws BaseNaturalException
	 *             , Exception
	 * @author rodrigo.hjort
	 * @author felipevillarinho
	 * @author geraldo
	 */
	public Object executar(Object objen, int versao, int timeout) throws BaseNaturalException, Exception {
		Object obj = null;
		//log.info("");
		Natural nat = null;
		// criar parametro de entrada

		if (getEntradaDebug() == null) {
			entrada = criarEntrada(objen);
		} else {
			log.info("||-- ATENCAO  --||  Nao esta sendo montado objeto de entrada ** Ativado MODO DEBUG de ENTRADA");
			entrada = getEntradaDebug();
		}

		if (getEntradaMaiusculas() != null && getEntradaMaiusculas()) {
			entrada = entrada.toUpperCase();
		}

		boolean usouCache = false;
		if (getUsarCache() != null && getUsarCache()) {
			ChamadaCached cache = CacheSingleton.getInstance().recuperarCache(programa, entrada, getTempoCache(), getChaveUsuarioCache());
			if (cache != null) {
				this.saida = cache.getSaida();
				this.rc = cache.getRc();
				this.mensagem = cache.getMensagem();
				usouCache = true;
			}
		}

		// se no for usar cache ou se for usar e no encontrou
		if (getUsarCache() == null || !getUsarCache() || !usouCache) {

			// log.info("A ENTRADA ERA: " + entrada);
			// entrada = new String(entrada.getBytes(), "ISO-8859-1");
			// String teste = new String(entrada.getBytes(), "UTF-8");
			// log.info("TESTE UTF-8 = " + teste);
			// log.info("A ENTRADA FICOU: " + entrada);

			if (getSaidaDebug() == null) {
				// executar programa Natural
				// nat = new Natural(programa, entrada);

				nat = new Natural(programa, entrada, versao , timeout);
			} else {
				// **Criao de uma classe filha da Natural.java que ativa o modo debug
				nat = new Natural(programa, entrada, versao, timeout) {
					public boolean isDebug() {
						return true;
					};

					public String getStringParaTestes() {
						return saidaDebug;
					};
				};

			}
			this.rc = Integer.valueOf(nat.getRC());
			this.mensagem = nat.getMSG().trim();
			this.saida = nat.getDataParameter();

			// quis utilizar o cache mas no tinha uma chamada em cache
			if (getUsarCache() != null && getUsarCache()) {
				CacheSingleton.getInstance().adicionarCache(programa, entrada, this.saida, this.rc, this.mensagem, this.getChaveUsuarioCache());
			}
		}

		// exibir informaes de depurao
		StringBuffer sb = new StringBuffer();
		sb.append(StringUtils.repeat("-", 80)).append("\n");
		sb.append("Chamada API Natural: " + programa + "\n");
		sb.append("> entrada: \"" + entrada + "\"" + "\n");
		sb.append("> rc: " + this.rc + ", mensagem: \"" + this.mensagem + "\"" + "\n");
		sb.append("> saida: \"" + this.saida + "\"" + "\n");
		sb.append(StringUtils.repeat("-", 80)).append("\n");

		// tratar recebimento
		if (this.rc.intValue() == 0) {

			// exibir informacoes de depurao
			log.info(sb.toString());

		} else {

			// mostrar erros
			log.error(sb.toString());

			// lancar excecao
			// throw new BaseNaturalException(this.programa, this.rc, this.mensagem);
		}

		// criar objeto
		obj = processarLinha(saida);

		// log.info("");
		return (obj);
	}

	/**
	 * Retorna texto referente mensagem de erro.
	 * 
	 * @author rodrigo.hjort
	 */
	public String getMensagem() {
		return (mensagem);
	}

	/**
	 * Retorna o cdigo de retorno do programa.
	 * 
	 * @author rodrigo.hjort
	 */
	public Integer getRc() {
		return (rc);
	}

	/**
	 * Retorna o texto referente saida do programa.
	 * 
	 * @author rodrigo.hjort
	 */
	public String getSaida() {
		return (saida);
	}

	/**
	 * Mapea configuraes de entrada e sada do programa a partir do XML.
	 * 
	 * @author rodrigo.hjort
	 * @throws Exception
	 */
	private void lerMapeamento(int versao) throws Exception {

		// log.debug("Lendo configurao do programa Natural " + programa + "...");

		// ler os parmetros do programa especificado
		Programa prg = null;
		try {
			prg = LeituraXml.getPrograma(programa, versao);
		} catch (Exception e) {
			log.error("ERRO LENDO PROGRAMA", e);
			throw e;
		}
		ent = prg.getEntrada();
		sai = prg.getSaida();

		// cdigo antigo e bugoso
		/*
		 * LeituraXml mapa = new LeituraXml(programa);
		 * 
		 * try { mapa.executar(); } catch (Exception e) { log.error(e.getMessage()); throw e; }
		 * 
		 * ent = mapa.getEntrada(); sai = mapa.getSaida();
		 */

		if (ent == null || sai == null) {
			throw new Exception("Parmetros de entrada e ou sada nulos!");
		}
	}

	/**
	 * Cria texto para entrada na chamada do programa Natural.
	 * 
	 * @param objen
	 *            objeto do tipo DTO mapeado no XML
	 * @return String
	 * @throws Exception
	 * @author rodrigo.hjort
	 */
	public String criarEntrada(Object objen) throws Exception {
		String ret = "";
		boolean prim = true;

		log.debug(StringUtils.repeat("*", 80));
		log.debug("Criando string para envio ao Natural...");
		log.debug("+ " + objen);

		// varrer lista de campos
		ret = "";
		List atributos = ent.getAtributos();
		if (atributos != null && !atributos.isEmpty()) {
			for (Iterator it = atributos.iterator(); it.hasNext();) {
				Atributo atr = (Atributo) it.next();

				// incluir separador, se houver!
				if (!prim && ent.getSeparador() != null)
					ret += ent.getSeparador();

				ret += processarEntrada(objen, atr);

				prim = false;
			}
		}

		// se houver extremos, coloque-os!
		String ext = ent.getExtremos();
		if (ext != null)
			ret = ext.concat(ret).concat(ext);

		return (ret);
	}

	private String processarEntrada(Object objen, Atributo atr) throws Exception {
		String ret = "";

		// se for um campo...
		if (atr instanceof Campo) {

			Campo campo = (Campo) atr;
			ret += criarEntrada(objen, campo, false);

			// se for uma lista...
		} else if (atr instanceof Lista) {

			Lista lista = (Lista) atr;
			String nomeLista = lista.getNome();
			int cl = 1;
			boolean prim2 = true;

			// criar lista genericamente
			Object objl = UtilReflexao.invocaGet(objen, lista.getNome());
			if (objl != null) {
				Iterator it3 = ((List) objl).iterator();
				int ii3 = 1;
				while (it3.hasNext() && ii3 <= lista.getQtd()) {
					Object objle = it3.next();
					log.debug("+ " + nomeLista + "[" + (cl - 1) + "]: " + objle);

					List campos2 = lista.getAtributos();
					if (campos2 != null && !campos2.isEmpty()) {
						Iterator it2 = campos2.iterator();
						while (it2.hasNext()) {
							// Campo campo = (Campo) it2.next();
							if (!prim2) {
								if (lista.getSeparador() != null)
									ret += lista.getSeparador();
								else if (ent.getSeparador() != null)
									ret += ent.getSeparador();
							}
							Atributo atributo = (Atributo) it2.next();
							ret += processarEntrada(objle, atributo);
							// ret += criarEntrada(objle, campo, true);
							prim2 = false;
						}
					}

					ii3++;
					cl++;
				}
			}

			// criar itens faltantes da lista
			for (int il = cl; il <= lista.getQtd(); il++) {
				objl = UtilReflexao.criarObjeto(lista.getClasse().getName());
				log.debug("+ " + nomeLista + "[" + (il - 1) + "]: " + objl);

				List campos2 = lista.getAtributos();
				if (campos2 != null && !campos2.isEmpty()) {
					Iterator it2 = campos2.iterator();
					while (it2.hasNext()) {
						// Campo campo = (Campo) it2.next();
						if (!prim2) {
							if (lista.getSeparador() != null)
								ret += lista.getSeparador();
							else if (ent.getSeparador() != null)
								ret += ent.getSeparador();
						}
						Atributo atributo = (Atributo) it2.next();
						ret += processarEntrada(objl, atributo);
						// ret += criarEntrada(objl, campo, true);
						prim2 = false;
					}
				}
			}

			// se for um objeto...
		} else if (atr instanceof Objeto) {

			Objeto objeto = (Objeto) atr;
			String nomeObjeto = objeto.getNome();
			boolean prim2 = true;

			// criar objeto genericamente
			Object objo = UtilReflexao.invocaGet(objen, objeto.getNome());
			if (objo != null) {
				log.debug("+ " + nomeObjeto + ": " + objo);

				List campos2 = objeto.getAtributos();
				if (campos2 != null && !campos2.isEmpty()) {
					Iterator it2 = campos2.iterator();
					while (it2.hasNext()) {
						Campo campo = (Campo) it2.next();
						if (!prim2) {
							if (objeto.getSeparador() != null)
								ret += objeto.getSeparador();
							else if (ent.getSeparador() != null)
								ret += ent.getSeparador();
						}
						ret += criarEntrada(objo, campo, true);
						prim2 = false;
					}
				}
			}

		}

		return ret;
	}

	/**
	 * Cria texto referente ao campo especificado.
	 * 
	 * @param objen
	 *            objeto pai contendo o campo
	 * @param campo
	 *            objeto do campo
	 * @param lista
	 *            verdadeiro se obeto for uma lista
	 * @return String
	 * @author rodrigo.hjort
	 */
	private String criarEntrada(Object objen, Campo campo, boolean lista) {

		Object valor = campo.getValor();
		int tam = campo.getTamanho();
		if (valor == null) {
			try {
				valor = UtilReflexao.invocaGet(objen, campo.getNome());
			} catch (Exception e) {
				log.error((lista ? "  " : "") + "> ERRO: " + e.getMessage() + ". Campo: " + campo.getNome(), e);
				//e.printStackTrace();
			}
		}
		String param = "";

		// se for tamanho fixo...
		if (ent.getTratamento().isTamanhoFixo()) {

			if (campo.getTipo() == Short.class || campo.getTipo() == Integer.class || campo.getTipo() == Long.class ||
					campo.getTipo() == Byte.class || campo.getTipo() == BigInteger.class) {
				param = StringUtils.leftPad(valor != null ? valor.toString() : "", tam, "0");

			} else if (campo.getTipo() == Float.class || campo.getTipo() == Double.class || campo.getTipo() == BigDecimal.class) {
				if (valor != null) {
					double valdb = 0.0;
					if (campo.getTipo() == Float.class) {
						valdb = ((Float) valor).doubleValue();
					} else if (campo.getTipo() == Double.class) {
						valdb = ((Double) valor).doubleValue();
					} else if (campo.getTipo() == BigDecimal.class) {
						valdb = ((BigDecimal) valor).doubleValue();
					}

					if (campo.getDecimal() > 0) {
						valdb *= Math.pow(10, campo.getDecimal());
					}
					DecimalFormat df = new DecimalFormat("0");
					String valstr = df.format(valdb);
					param = StringUtils.leftPad(valstr != null ? valstr : "", tam, "0");
				} else {
					param = StringUtils.repeat("0", tam);
				}

			} else if (campo.getTipo() == Date.class) {
				param = tratarData(campo, valor, tam,ent.getTratamento().isTamanhoFixo());

			} else {
				param = StringUtils.rightPad(valor != null ? valor.toString() : "", tam, " ");
			}		

			// se tamanho do campo for maior do que o previsto...
			if (param.length() > tam) {
				String antes = param;
				param = antes.substring(0, tam);
				log.error("Valor ultrapassa o tamanho especificado para o campo ["+campo.getNome()+"]! \"" + antes + "\" -> \"" + param + "\"");
			}
		} else {
			//Usar o separador
			if (campo.getTipo() == Date.class) {
				param = "\"" + tratarData(campo, valor, tam,ent.getTratamento().isTamanhoFixo()) + "\"";
			} else {				
				if(valor == null) {
					param =  "";
				} else {
					param = valor.toString();
				}
				
				if (campo.getTipo() == String.class || campo.getTipo() == Character.class) {
					param = "\"" + param + "\"";
				}
			}			
		}
		// exibir informacoes de depuracao
		if (!(campo instanceof Constante)) {
			log.debug((lista ? "  " : "") + "- " + campo.getNome() + ": \"" + param + "\"");
		}

		return param;
	}

	private String tratarData(Campo campo, Object valor, int tam, boolean isTamanhoFixo) {
		String param = "";

		if (valor != null) {
			String dateMask = "";
			if(campo.getMascara() == null || campo.getMascara().equals("")) {
				Calendar cal = Calendar.getInstance();
				cal.setTime((Date) valor);
				dateMask = "dd/MM/yyyy";
				if (cal.get(Calendar.HOUR_OF_DAY) > 0 || cal.get(Calendar.MINUTE) > 0) {
					dateMask += " HH:mm:ss";
				}
			} else {
				dateMask = campo.getMascara();
			}
			param = new SimpleDateFormat(dateMask).format(valor);
		} else {
			if(isTamanhoFixo) {
				param = StringUtils.repeat("0", tam);
			} else {
				param = "";
			}
		}

		return param;
	}

	/**
	 * Processa linha especificada, criando respectivo objeto.
	 * 
	 * @param linha
	 *            texto a ser processado
	 * @author rodrigo.hjort
	 */
	public Object processarLinha(String linhaParametro) {
		log.debug("Processando linha recebida do Natural...");
		offset = 0;
		this.linha = linhaParametro;
		// criar classe genericamente
		Object obj = UtilReflexao.criarObjeto(sai.getClasse().getName());
		log.debug("+ " + obj);

		if(this.rc != null && this.rc > ERRO_FATAL) {
			//aconteceu um erro muito grave
			log.error("[RC > 8000] O RETORNO DO MAINFRAME NÃO SERÁ PROCESSADO. Linha = [" + this.linha + "]");
		} else {
			// varrer lista de atributos da classe
			List atributos = sai.getAtributos();
			if (atributos != null && !atributos.isEmpty()) {
				Iterator it = atributos.iterator();
				while (it.hasNext()) {

					// ler informaes do atributo
					Atributo atr = (Atributo) it.next();
					processarAtributo(obj, atr, 1, 0);
				}
			}
			log.debug(StringUtils.repeat("*", 80));
		}
		// retornar objeto criado e populado
		return (obj);
	}

	/**
	 * @return Objeto para fins de redefinio.
	 * @param atr
	 * @author rodrigo.hjort
	 * @since 05/10/2005
	 */
	@SuppressWarnings("unchecked")
	private Object processarAtributo(Object obj, Atributo atr, int nivel, int offset) {

		Object objRedef = null;
		String tab = (nivel > 1 ? StringUtils.repeat("  ", nivel - 1) : "");

		// se for campo...
		if (atr instanceof Campo) {

			Campo campo = (Campo) atr;
			lerLinha(obj, campo, nivel);

			// se for continer (lista ou objeto)...
		} else if (atr instanceof Conteiner) {

			String nomeCnt = atr.getNome();
			List ll = new ArrayList();

			// se for lista...
			if (atr instanceof Lista) {

				Lista lista = (Lista) atr;

				// verificar expresso na quantidade da lista
				if (lista.getQtdExpr() != null) {
					String expr = lista.getQtdExpr();

					// TODO hjort: fazer um parser e interpretador melhorado, capaz de entender "$campo + 1", "$campo1 * $campo2", etc...

					if (expr.startsWith("$")) {
						try {
							Object tamLista = UtilReflexao.invocaGet(obj, expr.substring(1));
							lista.setQtd(((Integer) tamLista));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

				// verificar a condio de criao dos elementos da lista
				String criacao = lista.getCriacao();

				// varrer itens da lista
				for (int il = 0; il < lista.getQtd(); il++) {

					// criar lista genericamente
					Object objl = UtilReflexao.criarObjeto(lista.getClasse().getName());
					log.debug(tab + "+ " + nomeCnt + "[" + il + "]: " + objl);
					int offset2 = lista.getInicio() + lista.getTamanhoLinha() * il;

					// processar os atributos filhos
					List atrs = lista.getAtributos();
					if (atrs != null && !atrs.isEmpty()) {
						Object objRedef2 = null;
						Iterator it2 = atrs.iterator();
						while (it2.hasNext()) {
							Atributo atr2 = (Atributo) it2.next();
							Object objRedef3 = processarAtributo(objl, atr2, nivel + 1, offset2);
							if (objRedef3 != null)
								objRedef2 = objRedef3;
						}
						boolean adicionar = true;
						if (criacao != null && !"".equals(criacao)) {
							Object objAdicionar = null;
							if (objRedef2 == null)
								objAdicionar = objl;
							else
								objAdicionar = objRedef2;
							adicionar = verificarCondicoesCriacaoObjeto(objAdicionar, criacao);
						}
						if (adicionar) {
							ll.add(objRedef2 == null ? objl : objRedef2);
						}
					}

				}

				try {
					UtilReflexao.invocaSet(obj, nomeCnt, List.class, ll);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// se for objeto...
			} else if (atr instanceof Objeto) {

				Objeto objeto = (Objeto) atr;
				Redefinicao redef = null;
				boolean validou = true;

				// se for redefinio...
				if (atr instanceof Redefinicao) {
					redef = (Redefinicao) atr;
					redef.setObjPai(obj);
					try {
						validou = redef.isCondicaoValida();
					} catch (Exception e) {
						e.printStackTrace();
						validou = false;
					}
					nomeCnt = "redefinio (" + redef.getCondicao() + ")";
				}

				// se no for redefinio, ou e foi validado...
				if (validou) {

					// criar objeto genericamente
					Object objl = UtilReflexao.criarObjeto(objeto.getClasse().getName());
					log.debug(tab + "+ " + nomeCnt + ": " + objl);
					int offset2 = objeto.getInicio();

					// processar os atributos filhos
					List atrs = objeto.getAtributos();
					if (atrs != null && !atrs.isEmpty()) {
						Object objRedef2 = null;
						Iterator it2 = atrs.iterator();
						while (it2.hasNext()) {
							Atributo atr2 = (Atributo) it2.next();
							Object objRedef3 = processarAtributo(objl, atr2, nivel + 1, offset2);
							if (objRedef3 != null)
								objRedef2 = objRedef3;
						}
						ll.add(objRedef2 == null ? objl : objRedef2);
					}

					// se no for uma redefinio...
					if (!(atr instanceof Redefinicao)) {

						try {
							UtilReflexao.invocaSet(obj, nomeCnt, objeto.getClasse(), objl);
						} catch (Exception e) {
							e.printStackTrace();
						}

						// agora, se for a desgracida...
					} else {

						// copiar os atributos, se for a opo desejada
						if (redef.getMetodo().isCopiarAtributos() && redef.getPai() != null) {
							Object atrPai = redef.getPai();
							List atrsPai = ((Conteiner) atrPai).getAtributos();

							// varrer itens do pai
							for (Object obj3 : atrsPai) {
								Atributo atr3 = (Atributo) obj3;
								if (atr3.getNome() != null) {

									// vamos pegar o valor?
									Object valAtr = null;
									try {
										valAtr = UtilReflexao.invocaGet(obj, atr3.getNome());
									} catch (Exception e1) {
										e1.printStackTrace();
									}

									// essa tosquera tem que ser feita, uma vez que no h como listar propriedades herdadas!
									if (valAtr != null) {
										try {
											UtilReflexao.invocaSet(objl, atr3.getNome(), valAtr.getClass(), valAtr);
										} catch (Exception e) {
											// e.printStackTrace();
										}
									}
								}
							}
						}

						// haha, tomar o lugar do objeto pai!
						objRedef = objl;
					}
				}
			}
		}
		return (objRedef);
	}

	/**
	 * @param linha
	 *            texto a ser processado
	 * @param obj
	 *            objeto a ser considerado
	 * @param campo
	 *            campo
	 * @param lista
	 *            indica se campo lista
	 * @param offset
	 *            deslocamento do valor no texto
	 * @author rodrigo.hjort
	 */
	private void lerLinha(Object obj, Campo campo, int nivel) {

		// se campo no for constante...
		if (!(campo instanceof Constante)) {

			String tab = (nivel > 1 ? StringUtils.repeat("  ", nivel - 1) : "");

			// copiar trecho referente ao campo
			String pedaco = "";
			try {
				// se for tamanho fixo...
				if (sai.getTratamento().isTamanhoFixo()) {

					pedaco = linha.substring(offset, offset + campo.getTamanho());
					offset += campo.getTamanho();

					// se for delimitador...
				} else if (sai.getTratamento().isDelimitador()) {
					if (sai.getDelimitador() != null && !sai.getDelimitador().equals("")) {
						pedaco = linha.split(sai.getDelimitador())[this.posicao++];
					}

				}
			} catch (StringIndexOutOfBoundsException siobe) {
				log.info(tab + "> CAMPO NAO PRESENTE NA SAIDA '"+ campo.getNome() +"' : " + siobe.getMessage());
			} catch (Exception e) {
				log.info(tab + "> ERRO NO CAMPO '"+ campo.getNome() +"' : " + e.getMessage(), e);
			}

			// log.debug("# " + campo.getNome() + " : pedaco(" + (offset + campo.getInicio() - 1) + ", " + (offset + campo.getFim()) + "): " +
			// pedaco);

			// atribuir valor ao campo genericamente
			String nomeCampo = campo.getNome();
			Class tipoCampo = campo.getTipo();
			Object valorCampo = null;
			double div = (campo.getDecimal() > 0 ? Math.pow(10, campo.getDecimal()) : 1);
			try {
				pedaco = pedaco.trim();
				if(StringUtils.isEmpty(pedaco)) {
					valorCampo = null;
				} else {
					if (tipoCampo == String.class)
						valorCampo = pedaco.trim();
					else if (tipoCampo == Byte.class)
						valorCampo = Byte.valueOf(pedaco);
					else if (tipoCampo == Short.class)
						valorCampo = Short.valueOf(pedaco.strip());
					else if (tipoCampo == Integer.class)
						valorCampo = Integer.valueOf(pedaco.strip());
					else if (tipoCampo == Long.class)
						valorCampo = Long.valueOf(pedaco.strip());
					else if (tipoCampo == Float.class)
						valorCampo = Float.parseFloat(pedaco.strip()) / div;
					else if (tipoCampo == Double.class)
						valorCampo = Double.parseDouble(pedaco.strip()) / div;
					else if (tipoCampo == BigDecimal.class) {
						int posDec = pedaco.length() - campo.getDecimal();
						String valorDec = pedaco.substring(0, posDec).concat(".").concat(pedaco.substring(posDec));
						valorCampo = new BigDecimal(valorDec);
					} else if (tipoCampo == Date.class) {
						String mask = (campo.getMascara() != null && !campo.getMascara().equals("") ? campo.getMascara() : "yyyyMMdd");
						if (!pedaco.equals(StringUtils.repeat("0", mask.length()))) {
							SimpleDateFormat sdf = new SimpleDateFormat(mask);
							sdf.setLenient(false); // forar validao da data/hora
							valorCampo = sdf.parse(pedaco);
						}
					}
				}
				
			} catch (Exception e) {
				log.error(tab + "** ERRO no campo '"+ campo.getNome() +"' : " + e.getMessage(), e);
				this.rc = 9999;
				this.mensagem = e.getMessage();
			}
			try {
				UtilReflexao.invocaSet(obj, nomeCampo, tipoCampo, valorCampo);
			} catch (Exception e) {
				log.error("PROBLEMAS AO CHAMAR SET DO CAMPO", e);
				this.rc = 9999;
				this.mensagem = e.getMessage();
			}

			// testar se valor foi atribudo
			try {
				Object val = UtilReflexao.invocaGet(obj, nomeCampo);

				// se for do tipo data/hora...
				if (val != null && val instanceof Date) {
					// val = new SimpleDateFormat("dd/MM/yyyy").format(val);
					Calendar cal = Calendar.getInstance();
					cal.setTime((Date) val);
					String dateMask = "dd/MM/yyyy";
					if (cal.get(Calendar.HOUR_OF_DAY) > 0 || cal.get(Calendar.MINUTE) > 0)
						dateMask += " HH:mm:ss";
					val = new SimpleDateFormat(dateMask).format(val);
				}

				String outstr = "- " + nomeCampo + ": \"" + val + "\"";
				log.debug(tab + outstr);
			} catch (Exception e) {
				log.error(tab + "> ERRO no campo '"+ nomeCampo +"': " + e.getMessage(), e);
				//e.printStackTrace();
			}

			// se o campo for uma constante...
		} else {

			// avanar no offset
			offset += campo.getTamanho();
		}
	}

	/**
	 * Gera cdigo em Java referente s classes de entrada e sada, a partir do XML.
	 * 
	 * @author rodrigo.hjort
	 * @since 07/10/2005
	 */
	public void gerarCodigoJava() {
		log.info(StringUtils.repeat("-", 80));

		// DTO de envio
		gerarCodigoJava(ent);

		// POJO de retorno
		gerarCodigoJava(sai);

		log.info("\n" + StringUtils.repeat("*", 80) + "\n");
	}

	private final String AUTOR = "/**\n" + " * Criada automaticamente pelo mtodo BaseNaturalDAO.gerarCodigoJava().\n" + " * @author rodrigo.hjort\n"
			+ " * @since " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + "\n" + " */\n";

	/**
	 * Uso interno.
	 * 
	 * @param arg
	 * @author rodrigo.hjort
	 * @since 07/10/2005
	 */
	private void gerarCodigoJava(Argumento arg) {

		if (arg != null) {

			// public class SITVN200 {
			log.info("\n" + AUTOR + "public class " + arg.getClasse().getSimpleName().toString() + " {\n");

			List<Conteiner> filhos = new ArrayList<Conteiner>();
			List atributos = arg.getAtributos();
			if (atributos != null && !atributos.isEmpty()) {
				for (Iterator it = atributos.iterator(); it.hasNext();) {
					Atributo atr = (Atributo) it.next();
					String nome = atr.getNome();
					String tipo = null;

					if (atr instanceof Campo) {
						if (!(atr instanceof Constante))
							tipo = ((Campo) atr).getTipo().getSimpleName().toString();
					} else if (atr instanceof Lista)
						tipo = "List<" + ((Lista) atr).getClasse().getSimpleName() + ">";
					else if (atr instanceof Objeto)
						tipo = ((Objeto) atr).getClasse().getSimpleName();

					if (atr instanceof Conteiner)
						filhos.add((Conteiner) atr);

					// private Integer tipoGuia;
					if (tipo != null)
						log.info("\tprivate " + tipo.replaceFirst("java\\.(lang|util)\\.", "") + " " + nome + ";");
				}
			}

			log.info("\n}");

			// gerar cdigo dos filhos tambm
			for (Object cnt : filhos)
				gerarCodigoJava((Conteiner) cnt);
		}
	}

	/**
	 * Uso interno.
	 * 
	 * @param arg
	 * @author rodrigo.hjort
	 * @since 07/10/2005
	 */
	private void gerarCodigoJava(Conteiner arg) {

		if (arg != null) {

			// public class SITVN200 {
			log.info("\n" + AUTOR + "public class " + arg.getClasse().getSimpleName().toString() + " {\n");

			List<Conteiner> filhos = new ArrayList<Conteiner>();
			List atributos = arg.getAtributos();
			if (atributos != null && !atributos.isEmpty()) {
				for (Iterator it = atributos.iterator(); it.hasNext();) {
					Atributo atr = (Atributo) it.next();
					String nome = atr.getNome();
					String tipo = null;

					if (atr instanceof Campo) {
						if (!(atr instanceof Constante))
							tipo = ((Campo) atr).getTipo().getSimpleName().toString();
					} else if (atr instanceof Lista)
						tipo = "List<" + ((Lista) atr).getClasse().getSimpleName() + ">";
					else if (atr instanceof Objeto)
						tipo = ((Objeto) atr).getClasse().getSimpleName();

					if (atr instanceof Conteiner)
						filhos.add((Conteiner) atr);

					// private Integer tipoGuia;
					if (tipo != null)
						log.info("\tprivate " + tipo.replaceFirst("java\\.(lang|util)\\.", "") + " " + nome + ";");
				}
			}

			log.info("\n}");

			// gerar cdigo dos filhos tambm
			for (Object cnt : filhos)
				gerarCodigoJava((Conteiner) cnt);
		}
	}

	private boolean verificarCondicoesCriacaoObjeto(Object objeto, String condicao) {

		String[] operandos = null;
		TipoCondicional condicional = null;

		if (condicao.contains(TipoCondicional.MENORIGUAL.descricao)) {
			operandos = condicao.split(TipoCondicional.MENORIGUAL.descricao);
			condicional = TipoCondicional.MENORIGUAL;
		} else if (condicao.contains(TipoCondicional.MAIORIGUAL.descricao)) {
			operandos = condicao.split(TipoCondicional.MAIORIGUAL.descricao);
			condicional = TipoCondicional.MAIORIGUAL;
		} else if (condicao.contains(TipoCondicional.IGUAL.descricao)) {
			operandos = condicao.split(TipoCondicional.IGUAL.descricao);
			condicional = TipoCondicional.IGUAL;
		} else if (condicao.contains(TipoCondicional.DIFERENTE.descricao)) {
			operandos = condicao.split(TipoCondicional.DIFERENTE.descricao);
			condicional = TipoCondicional.DIFERENTE;
		} else if (condicao.contains(TipoCondicional.MENOR.descricao)) {
			operandos = condicao.split(TipoCondicional.MENOR.descricao);
			condicional = TipoCondicional.MENOR;
		} else if (condicao.contains(TipoCondicional.MAIOR.descricao)) {
			operandos = condicao.split(TipoCondicional.MAIOR.descricao);
			condicional = TipoCondicional.MAIOR;
		}

		try {

			Object atributo = UtilReflexao.invocaGet(objeto, operandos[0].trim());
			String valor = operandos[1].trim();

			if (atributo instanceof String)
				return verificarCondicoesCriacaoObjetoTipoString(atributo, condicional, valor.replaceAll("'", ""));

			if (atributo instanceof Number)
				return verificarCondicoesCriacaoObjetoTipoNumber(atributo, condicional, valor);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;

	}

	private boolean verificarCondicoesCriacaoObjetoTipoNumber(Object atributo, TipoCondicional condicional, String valor) {

		try {
			double valorAtributoObjeto = Double.valueOf(atributo.toString());
			double valorAtributoCondicao = 0.0;
			if (valor != null && StringUtils.isNotEmpty(valor)) {
				valorAtributoCondicao = Double.valueOf(valor);
			}

			/* realiza as comparaes */
			if (TipoCondicional.DIFERENTE.is(condicional.codigo)) {
				if (valorAtributoObjeto != valorAtributoCondicao)
					return true;
			}
			if (TipoCondicional.IGUAL.is(condicional.codigo)) {
				if (valorAtributoObjeto == valorAtributoCondicao)
					return true;
			}
			if (TipoCondicional.MAIOR.is(condicional.codigo)) {
				if (valorAtributoObjeto > valorAtributoCondicao)
					return true;
			}
			if (TipoCondicional.MENOR.is(condicional.codigo)) {
				if (valorAtributoObjeto < valorAtributoCondicao)
					return true;
			}
			if (TipoCondicional.MAIORIGUAL.is(condicional.codigo)) {
				if (valorAtributoObjeto >= valorAtributoCondicao)
					return true;
			}
			if (TipoCondicional.MENORIGUAL.is(condicional.codigo)) {
				if (valorAtributoObjeto <= valorAtributoCondicao)
					return true;
			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;

	}

	private boolean verificarCondicoesCriacaoObjetoTipoString(Object atributo, TipoCondicional condicional, String valor) {

		try {

			String valorAtributoObjeto = atributo.toString();
			String valorAtributoCondicao = "";
			if (valor != null) {
				valorAtributoCondicao = valor;
			}

			/* realiza as comparaes */
			if (TipoCondicional.DIFERENTE.is(condicional.codigo)) {
				if (!valorAtributoObjeto.equals(valorAtributoCondicao))
					return true;
			}
			if (TipoCondicional.IGUAL.is(condicional.codigo)) {
				if (valorAtributoObjeto.equals(valorAtributoCondicao))
					return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;

	}

	private enum TipoCondicional {
		MENOR("<", 1), MENORIGUAL("<=", 2), MAIOR(">", 3), MAIORIGUAL(">=", 4), IGUAL("==", 5), DIFERENTE("!=", 6);

		public final String descricao;
		public final Integer codigo;

		private TipoCondicional(String descricao, Integer codigo) {
			this.descricao = descricao;
			this.codigo = codigo;
		}

		public boolean is(Integer codigo) {
			return this.codigo.equals(codigo);
		}
	}

	public void limparCacheUsuario() throws Exception {
		CacheSingleton.getInstance().limparTodoCacheUsuario(this.getChaveUsuarioCache());
	}

	public void limparCacheUsuarioPrograma(String programa) throws Exception {
		CacheSingleton.getInstance().limparTodoCacheUsuario(this.getChaveUsuarioCache());
	}

	/**
	 * Metodo que deve ser retornar sempre null quando em produo .
	 * 
	 * @author geraldo
	 * @since 20/01/2007
	 * @return
	 */
	public String getSaidaDebug() {
		return saidaDebug;
	}

	public void setSaidaDebug(String saidaDebug) {
		this.saidaDebug = saidaDebug;
	}

	public String getEntradaDebug() {
		return entradaDebug;
	}

	public void setEntradaDebug(String entradaDebug) {
		this.entradaDebug = entradaDebug;
	}

	public Integer getTempoCache() {
		return tempoCache;
	}

	public void setTempoCache(Integer tempoCache) {
		this.tempoCache = tempoCache;
	}

	public Boolean getUsarCache() {
		return usarCache;
	}

	public void setUsarCache(Boolean usarCache) {
		this.usarCache = usarCache;
	}

	public Boolean getEntradaMaiusculas() {
		return entradaMaiusculas;
	}

	public void setEntradaMaiusculas(Boolean entradaMaiusculas) {
		this.entradaMaiusculas = entradaMaiusculas;
	}

	public String getChaveUsuarioCache() {
		return chaveUsuarioCache;
	}

	public void setChaveUsuarioCache(String chaveUsuarioCache) {
		this.chaveUsuarioCache = chaveUsuarioCache;
	}

}
