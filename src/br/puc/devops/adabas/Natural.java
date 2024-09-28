package br.puc.devops.adabas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Natural {

	private static final String VERSION = "Versao 3.0 - Cliente para Midlleware JASPPION - Java 11";
	private static final int RCODE_PADRAO_HIBERNATURAL = 6789;
	public static final int PADRAO_JBOSS = 2;
	public static final int PADRAO_LOCAL = 1;
	public static final int TIMEOUT_PADRAO = 5000; // timeout em milissegundos
	
	private static String ARQ_XML_PADRAO = "natural.xml";
	private static String ARQ_XML = "natural.xml";

	static {
		if (System.getProperty("jasppion.ambiente") == null) {			
			ARQ_XML = ARQ_XML_PADRAO;
		} else {
			ARQ_XML = "natural-" + System.getProperty("jasppion.ambiente") + ".xml";
		}
	}

	// Retorno
	private int paRC;
	private String paMSG;
	private String parmSaida;
	private int versao = PADRAO_JBOSS;
	private int timeout = TIMEOUT_PADRAO;

	private List<Aplicacao> aplicacoes;

	// Auxiliares
	private static Logger LOGGER = Logger.getLogger(Natural.class);

	// Function para setDefaults
	private void setDefaults(Aplicacao apl) {
		apl.setChave("");
		apl.setSenha("");
		apl.setAmbiente("D");
		apl.setServerAddr("10.15.61.10");
		apl.setServerPort("6080");
		apl.setTipoPgm("dbcon");
		this.paRC=0;
		this.paMSG="";
		this.parmSaida = "";
	}

	// Construtores
	public Natural() {

	}

	public Natural(String identificador, String parmEntrada, String chaveNatural, String senhaNatural, String nomeAplicacao) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = null;

		String usuarioDefault = "";
		String middlewareDefault = "";

		if (identificador.length() < 3) {
			this.paRC = -9999;
			this.paMSG = "PGMNAT (" + identificador + ") INVALIDO";
			return;
		}
		identificador = identificador.trim().toUpperCase();

		if (getAplicacoes() == null) {
			InputStream input = getClassLoader().getResourceAsStream(ARQ_XML);
			//if (input == null)
			//	throw new Exception("Arquivo de configuracao nao encontrado! natural.xml");
			
			if (input == null) {
				LOGGER.error("O ARQUIVO '" + ARQ_XML + "' NAO FOI ENCONTRADO. TENTANDO O ARQUIVO PADRAO");
				input = getClassLoader().getResourceAsStream(ARQ_XML_PADRAO);
				if (input == null) {
					LOGGER.error("O ARQUIVO PADRAO '" + ARQ_XML_PADRAO + "' NAO FOI ENCONTRADO");
					throw new Exception("Arquivos de configuracao nao encontrados! '" + ARQ_XML + "' e '" + ARQ_XML_PADRAO  +"'");
				}
			}
			
			doc = db.parse(input);

			Element elem = doc.getDocumentElement();
			NodeList nl = elem.getElementsByTagName("aplicacao");

			setAplicacoes(new ArrayList());

			for (int i = 0; i < nl.getLength(); ++i) {
				Element tagAplicacao = (Element) nl.item(i);
				Aplicacao apl = new Aplicacao();
				setDefaults(apl);

				apl.setNome(nomeAplicacao);

				NodeList opcoes = tagAplicacao.getElementsByTagName("opcoes");
				for (int j = 0; j < opcoes.getLength(); ++j) {
					Element tagOpcao = (Element) opcoes.item(j);
					apl.setChave(chaveNatural);
					apl.setSenha(senhaNatural);
					apl.setServerAddr(getChildTagValue(tagOpcao, "host"));
					apl.setServerPort(getChildTagValue(tagOpcao, "porta"));
					apl.setAmbiente(getChildTagValue(tagOpcao, "ambiente"));
				}

				NodeList opcoesDefault = tagAplicacao.getElementsByTagName("default");
				for (int l = 0; l < opcoesDefault.getLength(); ++l) {
					Element tagDefault = (Element) opcoesDefault.item(l);
					middlewareDefault = getChildTagValue(tagDefault, "middleware");
				}

				NodeList programas = tagAplicacao.getElementsByTagName("programa");
				for (int l = 0; l < programas.getLength(); ++l) {
					Element tagPrograma = (Element) programas.item(l);
					Programa prog = new Programa();
					prog.setNome(getTagAttribute(tagPrograma, "nome"));
					prog.setDescricao(getTagAttribute(tagPrograma, "descricao"));

					String usuario = getChildTagValue(tagPrograma, "usuario");
					if (usuario != null)
						prog.setUsuario(usuario);
					else {
						prog.setUsuario(usuarioDefault);
					}
					String middleware = getChildTagValue(tagPrograma, "middleware");
					if (middleware != null)
						prog.setMiddleware(middleware);
					else {
						prog.setMiddleware(middlewareDefault);
					}
					if (apl.getProgramas() == null)
						apl.setProgramas(new ArrayList());
					apl.getProgramas().add(prog);
				}
				getAplicacoes().add(apl);
			}

		}

		try {
			StringBuffer sb = new StringBuffer();
			sb.append("Chamada Jasppion: " + identificador + "\n");
			sb.append("> entrada: \"" + parmEntrada + "\"" + "\n");
			sb.append("> rc: " + getRC() + ", mensagem: \"" + getMSG() + "\"" + "\n");
			sb.append("> saida: \"" + getDataParameter() + "\"" + "\n");

			LOGGER.info(sb.toString());

			execute(identificador, parmEntrada);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());

			this.paRC = -9999;
			this.paMSG = e.getMessage();

			if (this.paMSG.length() > 60) {
				this.paMSG = this.paMSG.substring(this.paMSG.length() - 60);
			}
			return;
		}
	}

	public Natural(String identificador, String parmEntrada, int versao, int timeout) throws Exception {

		this.versao = versao;
		this.timeout = timeout;
		
		// Ler o natural.xml
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = null;

		String usuarioDefault = "";
		String middlewareDefault = "";

		if (identificador.length() < 3) {
			this.paRC = -9999;
			this.paMSG = "PGMNAT (" + identificador + ") INVALIDO";
			return;
		}
		identificador = identificador.trim().toUpperCase();

		// Aplicacoes nao foram lidas
		if (getAplicacoes() == null) {
			// versao 2: recurso no JBoss
			
			if(this.versao == PADRAO_JBOSS) {
				InputStream input = getClassLoader().getResourceAsStream(ARQ_XML);
				if (input == null) {
					LOGGER.error("O ARQUIVO '" + ARQ_XML + "' NAO FOI ENCONTRADO. TENTANDO O ARQUIVO PADRAO");
					input = getClassLoader().getResourceAsStream(ARQ_XML_PADRAO);
					if (input == null) {
						LOGGER.error("O ARQUIVO PADRAO '" + ARQ_XML_PADRAO + "' NAO FOI ENCONTRADO");
						throw new Exception("Arquivos de configuracao nao encontrados! '" + ARQ_XML + "' e '" + ARQ_XML_PADRAO  +"'");
					}
				}
				doc = db.parse(input);
			} else {

				// versao 1: caminho relativo do arquivo
				doc = db.parse(ARQ_XML);
			}

			Element elem = doc.getDocumentElement();
			NodeList nl = elem.getElementsByTagName("aplicacao");

			this.setAplicacoes(new ArrayList<Aplicacao>());
			// Cria a lista de Aplicacoes
			for (int i = 0; i < nl.getLength(); i++) {
				Element tagAplicacao = (Element) nl.item(i);
				Aplicacao apl = new Aplicacao();
				setDefaults(apl);
				String nome = getTagAttribute(tagAplicacao, "nome");
				apl.setNome(nome);

				// Recupera as Opes
				NodeList opcoes = tagAplicacao.getElementsByTagName("opcoes");
				for (int j = 0; j < opcoes.getLength(); j++) {
					Element tagOpcao = (Element) opcoes.item(j);
					apl.setChave(getChildTagValue(tagOpcao, "chave"));
					apl.setSenha(getChildTagValue(tagOpcao, "senha"));
					apl.setServerAddr(getChildTagValue(tagOpcao, "host"));
					apl.setServerPort(getChildTagValue(tagOpcao, "porta"));
					apl.setAmbiente(getChildTagValue(tagOpcao, "ambiente"));
				}
				// Recupera as Opes Default
				NodeList opcoesDefault = tagAplicacao.getElementsByTagName("default");
				for (int l = 0; l < opcoesDefault.getLength(); l++) {
					Element tagDefault = (Element) opcoesDefault.item(l);
					usuarioDefault = getChildTagValue(tagDefault, "usuario");
					middlewareDefault = getChildTagValue(tagDefault, "middleware");
				}
				// Recupera os Programas
				NodeList programas = tagAplicacao.getElementsByTagName("programa");
				for (int l = 0; l < programas.getLength(); l++) {
					Element tagPrograma = (Element) programas.item(l);
					Programa prog = new Programa();
					prog.setNome(getTagAttribute(tagPrograma, "nome"));
					prog.setDescricao(getTagAttribute(tagPrograma, "descricao"));
					// Verificar se foram informados parmetros complementares
					String usuario = getChildTagValue(tagPrograma, "usuario");
					if (usuario != null) {
						prog.setUsuario(usuario);
					} else {
						prog.setUsuario(usuarioDefault);
					}
					String middleware = getChildTagValue(tagPrograma, "middleware");
					if (middleware != null) {
						prog.setMiddleware(middleware);
					} else {
						prog.setMiddleware(middlewareDefault);
					}
					if (apl.getProgramas() == null)
						apl.setProgramas(new ArrayList());
					apl.getProgramas().add(prog);
				}
				getAplicacoes().add(apl);
			}
		}

		try {

			// exibir informaes de depurao
			StringBuffer sb = new StringBuffer();
			sb.append("Chamada Jaspin: " + identificador + "\n");
			sb.append("> entrada: \"" + parmEntrada + "\"" + "\n");
			sb.append("> rc: " + this.getRC() + ", mensagem: \"" + this.getMSG() + "\"" + "\n");
			sb.append("> saida: \"" + this.getDataParameter() + "\"" + "\n");

			LOGGER.info(sb.toString());

			this.execute(identificador, parmEntrada);
		} catch (Exception e) {

			LOGGER.error("ERRO NA CHAMADA AO PROGRAMA: " + identificador, e);

			this.paRC = -9999;
			this.paMSG = e.getMessage() == null? "Mensagem nula: NullPointerException" : e.getMessage();

			if (this.paMSG.length() > 60) {
				this.paMSG = this.paMSG.substring(this.paMSG.length() - 60);
			}
			return;
		}

	}

	public void execute(String PgmNat, String DadosEntrada) {
		try {
			// PgmNat
			if (PgmNat.length() < 3) {
				this.paRC = -9999;
				this.paMSG = "PGMNAT (" + PgmNat + ") INVALIDO";
				return;
			}
			if (PgmNat.length() < 8) {
				PgmNat = PgmNat + "        ".substring(0, 8 - PgmNat.length());
			}
			PgmNat = PgmNat.substring(0, 8).toUpperCase();

			// Procura o programa a ser executado
			String prefixo = PgmNat.substring(0, 3);

			Programa progExec = null;
			Aplicacao aplExec = null;

			for (Aplicacao apl : getAplicacoes()) {
				//if (prefixo.equals(apl.getNome())) {
					for (Programa programa : apl.getProgramas()) {
						if (PgmNat.equals(programa.getNome())) {
							progExec = programa;
							aplExec = apl;
							break;
						}
					}
				//}
			}

			if (progExec != null) {
				aplExec.setTipoPgm(progExec.getMiddleware().toLowerCase().trim());
				// Tipo de Programa
				if (aplExec.getTipoPgm().length() < 3) {
					aplExec.setTipoPgm("dbcon");
				}
				String Jasppion = "jaspin"; // Default DBCON
				if (aplExec.getTipoPgm().equals("dbgateway"))
					Jasppion = "jasppion";
				String Aplic = PgmNat.substring(0, 3);
				// Ambiente
				if (aplExec.getAmbiente().length() == 0) {
					aplExec.setAmbiente("D");
				}
				aplExec.setAmbiente(aplExec.getAmbiente().toUpperCase().trim());

				if ((!aplExec.getAmbiente().equals("D")) && (!aplExec.getAmbiente().equals("P"))) {
					aplExec.setAmbiente("D");
				}
				// Logon pode ter informado para Ambiente D
				if (aplExec.getAmbiente().toUpperCase().equals("P")) {
					progExec.setUsuario("PRODUCAO");
				} else {
					if (progExec.getUsuario().length() == 0) {
						progExec.setUsuario("N000" + Aplic + " "); // Default Desenvolvimento
					}
				}
				if (progExec.getUsuario().length() < 8) {
					progExec.setUsuario(progExec.getUsuario() + "        ".substring(0, 8 - progExec.getUsuario().length()));
				}
				progExec.setUsuario(progExec.getUsuario().substring(0, 8).toUpperCase());
				// Servidor Jasppion
				if (aplExec.getServerAddr().length() == 0) {
					aplExec.setServerAddr("10.15.61.10");
				}
				aplExec.setServerAddr(aplExec.getServerAddr().trim());

				// Porta do Servidor Jasppion
				if (aplExec.getServerPort().length() == 0) {
					aplExec.setServerPort("6080");
				}
				aplExec.setServerPort(aplExec.getServerPort().trim());

				// Chave
				if (aplExec.getChave().length() == 0) {
					this.paRC = -9999;
					this.paMSG = "Nao informou chave do usuario";
					return;
				}

				aplExec.setChave(aplExec.getChave().trim());
				if (aplExec.getChave().length() < 6) {
					aplExec.setChave("000000".substring(0, 6 - aplExec.getChave().length()) + aplExec.getChave());
				}
				aplExec.setChave(aplExec.getChave().substring(0, 6));

				// Senha
				if (aplExec.getSenha().length() == 0) {
					this.paRC = -9999;
					this.paMSG = "Nao informou Senha";
					return;
				}
				aplExec.setSenha(aplExec.getSenha().trim());

				if (aplExec.getSenha().length() < 8) {
					aplExec.setSenha(aplExec.getSenha() + "        ".substring(0, 8 - aplExec.getSenha().length()));
				}
				aplExec.setSenha(aplExec.getSenha().substring(0, 8).toUpperCase());

				//URL BASE
				String url = "http://" + aplExec.getServerAddr() + ":" + aplExec.getServerPort() + "/cics/cwba/" + Jasppion;
				
				//DADOS APLICAC‡ÃƒO
				String dadosAplic = Aplic + aplExec.getAmbiente() + "XXXX" + progExec.getUsuario() + PgmNat + "N" + aplExec.getChave() + " " + aplExec.getSenha();
				
				//Dados do Post: RCODE + MENSAGEM + DADOS DE ENTRADA
				String dadosPost = RCODE_PADRAO_HIBERNATURAL + "                                                            "
									+DadosEntrada; // 60 espacos

				this.paRC = RCODE_PADRAO_HIBERNATURAL; // A inicializao no pode ser zero pois zero (0) significa sucesso na execuo do middleware.
				this.paMSG = "";
				this.parmSaida = "";

				String strRet = null;

				if (isDebug()) {
					strRet = getStringParaTestes();
					this.paRC = 0;
					this.paMSG = "MainFrame nao executado";
					this.parmSaida = strRet;
					LOGGER.info("");
					LOGGER.info("||-- ATENCAO  --||  Nao esta sendo acessado o MainFrame ** Ativado MODO DEBUG ");
					LOGGER.info("");
				} else {

					//strRet = efetuaComunicacaoPost(url, dados, Jasppion);
					
					//strRet = efetuaComunicacaoGet(url, dadosAplic, dadosPost, Jasppion);				
					strRet = efetuaComunicacaoPost(url, dadosAplic, dadosPost, Jasppion);
					
					//strRet = efetuaComunicacaoGetEncoding(url, dadosAplic, dadosPost, Jasppion);//NAO DEU CERTO -- DESCOLOCOU A SAÃDA
					//strRet = efetuaComunicacaoGet(url, dados, Jasppion);
					
					// DEBUG
					if (strRet == null) {
						this.paRC = 8999;
						this.paMSG = "RETORNO DO SUBPROGRAMA ESTA NULO";
						return;						
					} else {
						
						this.parmSaida = strRet;
						// Verifica se tem pelo menos 56 bytes de retorno (paRC)
						if (strRet.length() < 56) {
							this.paRC = 8998;
							this.paMSG = "FORA DE LAYOUT PADRAO (4+60)";
							return;
						}
						// Verificar se RC Numerico
						if (!isNumeric(strRet.substring(52, 56))) {
							this.paRC = 8997;
							this.paMSG = "PA-RC NAO NUMERICO (" + strRet.substring(52, 56) + ")";
							return;
						}
						
						this.paRC = Integer.valueOf(strRet.substring(52, 56));
						this.paMSG = strRet.substring(56, 116).trim();
						
						if(RCODE_PADRAO_HIBERNATURAL == this.paRC) {
							if(this.paMSG == null || "".equals(this.paMSG)) {
								LOGGER.error("O PROGRAMA '" + PgmNat + "' RETORNOU O MESMO RCODE ENVIADO. VERIFICAR SE EXISTE RESET DO RCODE NO PROGRAMA. RCODE ORIGINAL: " + this.paRC);
								this.paRC = 0;
							}
						}
						
						if (strRet.length() > 116) {
							this.parmSaida = strRet.substring(116, strRet.length() - 15);
						}
					}
				}

			} else {
				this.paMSG = "Programa " + PgmNat + " nao encontrado no arquivo de configuracao: " + ARQ_XML == null ? ARQ_XML_PADRAO : ARQ_XML ;
			}
		} catch (Exception e) {
			this.paRC = 9999;
			this.paMSG = e.getMessage();
			if (this.paMSG.length() > 60) {
				this.paMSG = this.paMSG.substring(this.paMSG.length() - 60);
			}
			//this.parmSaida = "";
		}
		return;
	}

	/**
	 * Faz a comunicao com mainframe e devolve o resultado codificado com charset ISO-8859-1
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	private String efetuaComunicacaoV1(String url, String dadosAplic, String dadosPost, String Jasppion) throws Exception {
	// Chama o JASPPION
		URL urlConn = null;
		URLConnection conn = null;
		
		BufferedReader in = null;
		
		StringBuffer sb = new StringBuffer();
		
		Reader reader = null;

		// HttpClient client = new HttpClient();
		// PostMethod post = new PostMethod(url.toString());
		// this.paRC = client.executeMethod(post);
		String strRet = null;
		// if (this.paRC != 200) {
		// this.paMSG = "ERRO DE COMUNICACAO COM O SERVIDOR JASPPION.";
		// } else {
		// is = new InputStreamReader(post.getResponseBodyAsStream(), "ISO-8859-1");
		// in = new BufferedReader(is);
		// StringBuffer sb = new StringBuffer();
		// String line = "";
		// while ((line = in.readLine()) != null) {
		// sb.append(line).append("\r\n");
		// }
		//
		// strRet = sb.toString();
		// }
		// return strRet;

		// MODIFICADO POR evelyne.ribeiro em 24/10/2012 para resolver problemas de acentuao. Os caracteres acentuados so trocados por "?" caso no
		// seja especificado o CHARSET para a leitura do retorno da chamada ao mainframe.

		try {
			//LOGGER.error(url);
			//String urlCodificada = URLEncoder.encode(url, "ISO-8859-1");
			//LOGGER.error(urlCodificada);
			
			LOGGER.error("CHAMANDO VIA GET");			
			String urlNova = url + "?" + dadosAplic + dadosPost + "!" + Jasppion + "_fim_de_dados!";			
			LOGGER.error("URL COMPLETA = " + urlNova);
			
			urlConn = new URL(urlNova);
			conn = urlConn.openConnection();
			reader = new InputStreamReader(conn.getInputStream(), "ISO-8859-1");
			in = new BufferedReader(reader);
			String line = "";
			while ((line = in.readLine()) != null) {
				LOGGER.error("RETORNO MAINFRAME ==>");
				LOGGER.error(line);
				sb.append(line).append("\r\n");
			}

			strRet = sb.toString();
		} catch (Exception e) {
			LOGGER.error("ERRO NA COMUNICACAO COM O JASPIN : ", e);
			throw e;

		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e1) {
				LOGGER.error("Erro ao fechar reader");
			}

			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e1) {
				LOGGER.error("Erro ao fechar reader");
			}
		}

		return strRet;

	}
	
	
	private String efetuaComunicacao(String url, String dadosAplic, String dadosPost, String Jasppion) throws Exception {
	// Chama o JASPPION
		URL urlConn = null;
		URLConnection conn = null;
		BufferedReader in = null;
		StringBuffer sb = new StringBuffer();
		Reader reader = null;
		String strRet = null;
		try {
			
			LOGGER.error("CHAMANDO VIA GET");			
			String urlNova = url + "?" + dadosAplic + dadosPost + "!" + Jasppion + "_fim_de_dados!";
			
			urlNova = urlNova.replaceAll("[ ]", "%20");
			
			LOGGER.error("URL COMPLETA = " + urlNova);
			
			urlConn = new URL(urlNova);
			conn = urlConn.openConnection();
			reader = new InputStreamReader(conn.getInputStream(), "ISO-8859-1");
			in = new BufferedReader(reader);
			String line = "";
			while ((line = in.readLine()) != null) {
				LOGGER.error("RETORNO MAINFRAME ==>");
				LOGGER.error(line);
				sb.append(line).append("\r\n");
			}

			strRet = sb.toString();
		} catch (Exception e) {
			LOGGER.error("ERRO NA COMUNICACAO COM O JASPIN : ", e);
			throw e;

		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e1) {
				LOGGER.error("Erro ao fechar reader");
			}

			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e1) {
				LOGGER.error("Erro ao fechar reader");
			}
		}

		return strRet;

	}


	private String efetuaComunicacaoGet(String url, String dadosAplic, String dadosPost, String Jasppion) throws Exception {
	// Chama o JASPPION
		URL urlConn = null;
		URLConnection conn = null;
		BufferedReader in = null;
		StringBuffer sb = new StringBuffer();
		Reader reader = null;
		String strRet = null;
		try {
			
			LOGGER.info("[2.17-jdk11] - CHAMANDO VIA GET");			
			String urlNova = url + "?" + dadosAplic + dadosPost + "!" + Jasppion + "_fim_de_dados!";
			
			urlNova = urlNova.replaceAll("[ ]", "%20");
			
			LOGGER.info("[2.17-jdk11] - URL COMPLETA = " + urlNova);
			
			urlConn = new URL(urlNova);
			conn = urlConn.openConnection();
			reader = new InputStreamReader(conn.getInputStream(), "ISO-8859-1");
			in = new BufferedReader(reader);
			String line = "";
			while ((line = in.readLine()) != null) {
				LOGGER.info("[2.17-jdk11] - RETORNO MAINFRAME ==>");
				LOGGER.info(line);
				sb.append(line).append("\r\n");
			}

			strRet = sb.toString();
		} catch (Exception e) {
			LOGGER.error("ERRO NA COMUNICACAO COM O JASPIN : ", e);
			throw e;

		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e1) {
				LOGGER.error("Erro ao fechar reader");
			}

			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e1) {
				LOGGER.error("Erro ao fechar reader");
			}
		}

		return strRet;

	}
	
//	private String efetuaComunicacaoGetEncoding(String url, String dadosAplic, String dadosPost, String Jasppion) throws Exception {
//		// Chama o JASPPION
//			URL urlConn = null;
//			URLConnection conn = null;
//			BufferedReader in = null;
//			StringBuffer sb = new StringBuffer();
//			Reader reader = null;
//			String strRet = null;
//			try {
//				
//				LOGGER.info("CHAMANDO VIA GET");			
//				String urlNova = url + "?" + dadosAplic + dadosPost + "!" + Jasppion + "_fim_de_dados!";
//				//String urlNova = url + "?" + dadosAplic + URLEncoder.encode(dadosPost, "ISO-8859-1") + "!" + Jasppion + "_fim_de_dados!"; // NÃƒO DEU CERTO				
//				urlNova = urlNova.replaceAll("[ ]", "%20");
//				
//				LOGGER.info("URL COMPLETA = " + urlNova);
//				
//				urlConn = new URL(urlNova);
//				conn = urlConn.openConnection();
//				reader = new InputStreamReader(conn.getInputStream(), "ISO-8859-1");
//				in = new BufferedReader(reader);
//				String line = "";
//				while ((line = in.readLine()) != null) {
//					LOGGER.info("RETORNO MAINFRAME ==>");
//					LOGGER.info(line);
//					sb.append(line).append("\r\n");
//				}
//
//				strRet = sb.toString();
//				//strRet = URLDecoder.decode(sb.toString(), "ISO-8859-1"); // não deu certo
//				//LOGGER.info("RETORNO DECODIFICADO =" + strRet);
//			} catch (Exception e) {
//				LOGGER.error("ERRO NA COMUNICACAO COM O JASPIN : ", e);
//				throw e;
//
//			} finally {
//				try {
//					if (reader != null) {
//						reader.close();
//					}
//				} catch (IOException e1) {
//					LOGGER.error("Erro ao fechar reader");
//				}
//
//				try {
//					if (in != null) {
//						in.close();
//					}
//				} catch (IOException e1) {
//					LOGGER.error("Erro ao fechar reader");
//				}
//			}
//
//			return strRet;
//
//		}
	
	/**
	 * Faz a comunicao com mainframe e devolve o resultado codificado com charset ISO-8859-1
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	private String efetuaComunicacaoPost(String strUrl, String dadosAplic, String dadosPost, String Jasppion) throws Exception {

		String strRet = "";
		//LOGGER.error("CHAMANDO JASPIN COM POST. URL = " + strUrl + " - DADOS= " + dados);
		
		//MONTANDO A URL 
		String urlNova = strUrl + "?" + dadosAplic + dadosPost + "!" + Jasppion + "_fim_de_dados!";
		urlNova = urlNova.replaceAll("(#| )", "%20");
		
		LOGGER.info("CHAMANDO JASPIN COM POST. URL = " + urlNova);
		
		this.paRC = -9999;
		this.paMSG = "";
		this.parmSaida = "";
		HttpClient client = new HttpClient();
		PostMethod post = new PostMethod(urlNova);
		
		client.setConnectionTimeout(this.timeout);
		client.setTimeout(this.timeout);
		
		this.paRC = client.executeMethod((HttpMethod) post);
		if (this.paRC != 200) {
			this.paMSG = "ERRO DE COMUNICACAO COM O SERVIDOR JASPPION.";
		} else {
			strRet = post.getResponseBodyAsString();
		}
		
		LOGGER.info("[2.15-jdk11] - RETORNO MAINFRAME ==>" + strRet);
		
		return strRet;

	}
	
	/**
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private static ClassLoader getClassLoader() throws IllegalAccessException, InvocationTargetException {
		Method method = null;
		try {
			method = Thread.class.getMethod("getContextClassLoader", (Class[]) null);
		} catch (NoSuchMethodException e) {
			return null;
		}
		return (ClassLoader) method.invoke(Thread.currentThread(), (Object[]) null);
	}

	/**
	 * @param elem
	 *            : elemento a ser considerado
	 * @param attrName
	 *            : nome do atributo
	 * @return texto contendo valor do atributo
	 * @throws Exception
	 * @author rodrigo.hjort
	 */
	public static String getTagAttribute(Element elem, String attrName) throws Exception {
		return (elem.getAttribute(attrName));
	}

	/**
	 * este mtodo l e retorna o contedo (texto) de uma tag (elemento) filho da tag informada como parmetro. A tag filho a ser pesquisada a tag
	 * informada pelo nome (string)
	 * 
	 * @author carlos.eduardo
	 */
	public static String getChildTagValue(Element elem, String tagName) throws Exception {
		NodeList children = elem.getElementsByTagName(tagName);
		if (children == null)
			return null;
		Element child = (Element) children.item(0);
		if (child == null)
			return null;
		return child.getFirstChild().getNodeValue();
	}

	public static String getARQ_XML() {
		return ARQ_XML;
	}

	public static Logger getLog() {
		return LOGGER;
	}

	public static void setLog(Logger log) {
		Natural.LOGGER = log;
	}

	public static String getVERSION() {
		return VERSION;
	}

	public List<Aplicacao> getAplicacoes() {
		return aplicacoes;
	}

	public void setAplicacoes(List<Aplicacao> aplicacoes) {
		this.aplicacoes = aplicacoes;
	}

	public String getPaMSG() {
		return paMSG;
	}

	public void setPaMSG(String paMSG) {
		this.paMSG = paMSG;
	}

	public int getPaRC() {
		return paRC;
	}

	public void setPaRC(int paRC) {
		this.paRC = paRC;
	}

	public String getParmSaida() {
		return parmSaida;
	}

	public void setParmSaida(String parmSaida) {
		this.parmSaida = parmSaida;
	}

	// getRC
	public int getRC() {
		return (this.paRC);
	}

	// getMSG
	public String getMSG() {
		return (this.paMSG);
	}

	// getDataParameter
	public String getDataParameter() {
		return (this.parmSaida);
	}

	// getVersion
	public String getVersion() {
		return (Natural.VERSION);
	}

	/**
	 * Metodo que pode ser sobrescrito nas classes filhas, usado para identificar se o dever ser chamado o mainFrame ou simples mente ignorar a
	 * chamada.
	 * 
	 * @author geraldo
	 * @since 20/01/2007
	 * @return false - por padro no debug
	 */
	public boolean isDebug() {
		return false;
	}

	/**
	 * Metodo que pode ser sobrescrito nas classes filhas, usado para setar o valor que deveria ser retornado pelo MainFrame.
	 * 
	 * @author geraldo
	 * @since 20/01/2007
	 * @return
	 */
	public String getStringParaTestes() {
		return "";
	}

	// IsNumeric
	public static boolean isNumeric(String campo) {
		if (campo.length() == 0) {
			return false;
		}
		boolean ret = true;
		for (int ix1 = 0; ix1 < campo.length(); ix1++) {
			if ((campo.charAt(ix1) < '0') || (campo.charAt(ix1) > '9')) {
				ret = false;
			}
		}
		return ret;
	}

}
