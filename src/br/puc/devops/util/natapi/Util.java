package br.puc.devops.util.natapi;



import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
import javax.swing.text.MaskFormatter;

import org.apache.commons.lang3.StringUtils;



public final class Util {

	private Util() {
		// ...
	}
	
	public static boolean validarCPF(String numero) {
		String cpf = somenteNumeros(numero);
		Long lngCpf = null;
		try {
			lngCpf = Long.valueOf(cpf);

		} catch (NumberFormatException nfe) {
			return false;
		}
		return validarCPF(lngCpf);
	}
	
	public static boolean validarCPF(Long numero) {
		if(numero == null || numero.equals(Long.valueOf(0)) || numero.toString().length() > 11) {
			return false;
		}

		String CPF = numero.toString();

		if(CPF.equals("00000000000") || 
		   CPF.equals("11111111111") || 
		   CPF.equals("22222222222") || 
		   CPF.equals("33333333333") || 
		   CPF.equals("44444444444") || 
		   CPF.equals("55555555555") || 
		   CPF.equals("66666666666") || 
		   CPF.equals("77777777777") || 
		   CPF.equals("88888888888") || 
		   CPF.equals("99999999999")) {
			return false;
		}

		int nDig = 0;
		int d1 = 0;
		int d2 = 0;
		int digito1 = 0;
		int digito2 = 0;
		int resto = 0;
		String digVerif = "";
		StringBuilder digResul = new StringBuilder();
		String cpf = String.valueOf(numero);

		while (cpf.length() < 11) {
			cpf = "0" + cpf;
		}

		for (int i = 1; i < cpf.length() - 1; i++) {
			nDig = Integer.parseInt(cpf.substring(i - 1, i));
			d1 += (11 - i) * nDig; // Calculo do digito: Multiplicar ultima
			// casa por 2, penultima
			d2 += (12 - i) * nDig; // casa por 3, antepenultima casa por 4
			// e assim por diante
		}

		resto = (d1 % 11); // Se o resto for 0 ou 1 digito = 0, senao
		// digito = 11 menos o resto
		digito1 = (resto < 2) ? 0 : (11 - resto);
		d2 += 2 * digito1;
		resto = (d2 % 11);
		digito2 = (resto < 2) ? 0 : (11 - resto);
		digVerif = cpf.substring((cpf.length() - 2), cpf.length());
		digResul.append(digito1).append(digito2);

		return Integer.parseInt(digVerif) == Integer.parseInt(digResul.toString());
	}

	public static boolean validarCNPJ(String numero) {
		String cnpj = somenteNumeros(numero);
		Long lngCnpj = null;

		try {
			lngCnpj = Long.valueOf(cnpj);

		} catch (NumberFormatException nfe) {
			return false;
		}

		return validarCNPJ(lngCnpj);
	}

	public static boolean validarCNPJ(Long numero) {
		if (numero == null || numero.equals(Long.valueOf(0))) {
			return false;
		}

		int soma = 0;
		int dig = 0;
		String cnpj = String.valueOf(numero);
		StringBuilder cnpjResul = new StringBuilder();
		long cnpjCalc = 0L;

		while (cnpj.length() < 14) {
			cnpj = "0" + cnpj;
		}

		cnpjResul.append(cnpj.substring(0, 12));
		char[] nDig = cnpjResul.toString().toCharArray();

		// Primeira parte
		for (int i = 0; i < 4; i++) {
			if ((nDig[i] - 48 >= 0) && (nDig[i] - 48 <= 9)) {
				soma += (nDig[i] - 48) * (6 - (i + 1));
			}
		}

		for (int i = 0; i < 8; i++) {
			if ((nDig[i + 4] - 48 >= 0) && (nDig[i + 4] - 48 <= 9)) {
				soma += (nDig[i + 4] - 48) * (10 - (i + 1));
			}
		}

		dig = 11 - (soma % 11);
		cnpjResul.append(((dig == 10) || (dig == 11)) ? "0" : String.valueOf(dig));
		nDig = cnpjResul.toString().toCharArray();

		// Segunda parte
		soma = 0;

		for (int i = 0; i < 5; i++) {
			if ((nDig[i] - 48 >= 0) && (nDig[i] - 48 <= 9)) {
				soma += (nDig[i] - 48) * (7 - (i + 1));
			}
		}

		for (int i = 0; i < 8; i++) {
			if ((nDig[i + 5] - 48 >= 0) && (nDig[i + 5] - 48 <= 9)) {
				soma += (nDig[i + 5] - 48) * (10 - (i + 1));
			}
		}

		dig = 11 - (soma % 11);

		cnpjResul.append(((dig == 10) || (dig == 11)) ? "0" : String.valueOf(dig));

		cnpjCalc = Long.parseLong(cnpjResul.toString());

		return numero.longValue() == cnpjCalc;
	}

	public static String completarComZero(String valor, int tamanho, int lado) {
		String num = "";
		if (StringUtils.isNotBlank(valor)) {
			if (lado == 1) {
				num = StringUtils.leftPad(valor, tamanho, "0");
			} else if (lado == 2) {
				num = StringUtils.rightPad(valor, tamanho, "0");
			}
		}
		return num;
	}

	public static String completarCpfComZero(Long cpf) {
		return Util.completarComZero(cpf.toString(), 11, 1);
	}
	
	public static String completarCpfComZero(String cpf) {
		String num = "";
		if (StringUtils.isNotBlank(cpf)) {
		    num = Util.completarComZero(cpf.trim(), 11, 1);
		}
		return num;
	}

	public static String completarCNPJComZero(Long CNPJ) {
		return Util.completarComZero(CNPJ.toString(), 14, 1);
	}
	
	public static String completarCNPJComZero(String CNPJ) {
		String num = "";
		if (StringUtils.isNotBlank(CNPJ)) {
		    num = Util.completarComZero(CNPJ.trim(), 14, 1);
		}
		return num;
	}

	public static String formatarCPF(String numero) {

		if (StringUtils.isBlank(numero)) {
			return "000.000.000-00";
		}
		if (numero.contains(".") && numero.contains("-")) {
			return numero;
		}

		String zeros = "00000000000";
		int tamanho = numero.length();
		String auxiliar = numero;

		if (tamanho < 11) {
			auxiliar = zeros.substring(0, 11 - tamanho) + numero;
		}

		StringBuffer cpf = new StringBuffer();
		cpf.append(auxiliar.substring(0, 3));
		cpf.append(".");
		cpf.append(auxiliar.substring(3, 6));
		cpf.append(".");
		cpf.append(auxiliar.substring(6, 9));
		cpf.append("-");
		cpf.append(auxiliar.substring(9));

		return cpf.toString();
	}
	
	public static String formatarCPF(Long numero) {
		return formatarCPF(((numero != null) ? numero.toString() : "0"));
	}

	public static String formatarCEP(String cep) {
		cep = StringUtils.leftPad(cep, 8, "0");
		return mascaraGenerica("#####-###", cep);
	}

	public static String formatarCNPJ(String cnpj) {
		cnpj = StringUtils.leftPad(cnpj, 14, "0");
		return mascaraGenerica("##.###.###/####-##", cnpj);
	}

	public static String formatarCNPJ(Long cnpj) {
		String cnpjFmt = "";
		if((cnpj != null) && (validarCNPJ(cnpj))) {
			cnpjFmt = formatarCNPJ(cnpj.toString());
		}
		return cnpjFmt;
	}

	public static String formatarCpfCnpj(Long cpfcnpj) {
		String cpfCnpjFmt = null;
		if(cpfcnpj != null) {
			if(validarCNPJ(cpfcnpj)) {
				cpfCnpjFmt = formatarCNPJ(cpfcnpj);
			}
			else if(validarCPF(cpfcnpj)) {
				cpfCnpjFmt = formatarCPF(cpfcnpj.toString());
			}
		}
		return cpfCnpjFmt;
	}
	
	public static String formatarCpfCnpj(String cpfcnpj) {
		String cpfCnpjFmt = "";
		if(StringUtils.isNotBlank(cpfcnpj)) {
			if(validarCNPJ(cpfcnpj)) {
				cpfCnpjFmt = formatarCNPJ(cpfcnpj);
			}
			else if(validarCPF(cpfcnpj)) {
				cpfCnpjFmt = formatarCPF(cpfcnpj);
			}
		}
		return cpfCnpjFmt;
	}

	private static String mascaraGenerica(String mascara, Object texto) {
		MaskFormatter mask;
		try {
			mask = new MaskFormatter(mascara);
			mask.setValueContainsLiteralCharacters(false);
			return mask.valueToString(texto);

		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean validarChaveAcessoNFeNFCe(String chaveAcesso) {
		if (chaveAcesso == null || chaveAcesso.length() != 44) {
			return false;
		}

		String dv = chaveAcesso.substring(43);
		chaveAcesso = chaveAcesso.substring(0, 43);
		int total = 0;
		int peso = 2;

		for (int i = 0; i < chaveAcesso.length(); i++) {
			total += (chaveAcesso.charAt((chaveAcesso.length() - 1) - i) - '0') * peso;
			peso++;

			if (peso == 10) {
				peso = 2;
			}
		}

		int resto = total % 11;

		return String.valueOf((resto == 0 || resto == 1) ? 0 : (11 - resto)).equals(dv);
	}

	/**
	 * Remove caracteres especiais e substitui caracteres acentuados por caracteres não acentuados IMPORTANTE: a alteração da implementação desse método requer a alteração do método de mesmo nome no arquivo javascript util.js
	 * 
	 * @param texto
	 * @return
	 */
	public static String replaceSpecialChars(String texto) {
		String retorno = texto;
		retorno = retorno.replaceAll("[\\xE0-\\xE6]", "a");
		retorno = retorno.replaceAll("[\\xC0-\\xC6]", "A");
		retorno = retorno.replaceAll("[\\xE8-\\xEB]", "e");
		retorno = retorno.replaceAll("[\\xC8-\\xCB]", "E");
		retorno = retorno.replaceAll("[\\xEC-\\xEF]", "i");
		retorno = retorno.replaceAll("[\\xCC-\\xCF]", "I");
		retorno = retorno.replaceAll("[\\xF2-\\xF6]", "o");
		retorno = retorno.replaceAll("[\\xD2-\\xD6]", "O");
		retorno = retorno.replaceAll("[\\xF9-\\xFC]", "u");
		retorno = retorno.replaceAll("[\\xD9-\\xDC]", "U");
		retorno = retorno.replaceAll("\\xE7", "c");
		retorno = retorno.replaceAll("\\xC7", "C");
		retorno = retorno.replaceAll("\\xF1", "n");
		retorno = retorno.replaceAll("\\xD1", "N");
		retorno = retorno.replaceAll("[^\\w\\s]", "");
		retorno = retorno.toUpperCase();
		retorno = retorno.trim();
		return retorno;
	}

	/**
	 * Remove caracteres especiais e substitui caracteres acentuados por caracteres não acentuados IMPORTANTE: a alteração da implementação desse método requer a alteração do método de mesmo nome no arquivo javascript util.js
	 * 
	 * @param texto
	 * @return
	 */
	public static String substituirCaracterAcentuado(String texto) {
		String retorno = texto;
		retorno = retorno.replaceAll("[\\xE0-\\xE6]", "a");
		retorno = retorno.replaceAll("[\\xC0-\\xC6]", "A");
		retorno = retorno.replaceAll("[\\xE8-\\xEB]", "e");
		retorno = retorno.replaceAll("[\\xC8-\\xCB]", "E");
		retorno = retorno.replaceAll("[\\xEC-\\xEF]", "i");
		retorno = retorno.replaceAll("[\\xCC-\\xCF]", "I");
		retorno = retorno.replaceAll("[\\xF2-\\xF6]", "o");
		retorno = retorno.replaceAll("[\\xD2-\\xD6]", "O");
		retorno = retorno.replaceAll("[\\xF9-\\xFC]", "u");
		retorno = retorno.replaceAll("[\\xD9-\\xDC]", "U");
		retorno = retorno.replaceAll("\\xE7", "c");
		retorno = retorno.replaceAll("\\xC7", "C");
		retorno = retorno.replaceAll("\\xF1", "n");
		retorno = retorno.replaceAll("\\xD1", "N");
		retorno = retorno.toUpperCase();
		retorno = retorno.trim();
		return retorno;
	}

	public static String somenteNumeros(String valor) {
		if (StringUtils.isNotBlank(valor))
			return valor.replaceAll("[^\\d]", "");

		return "";
	}
	
	public static String filtroCoringaNrControle(String valor) {
		return (StringUtils.isNotBlank(valor))
			? valor.replaceAll("[^\\d\\%]", "")
			: "";
	}

	// public static boolean foneticaSemelhante(String a, String b) {
	// return Soundex.gerarValor(a).equals(Soundex.gerarValor(b));
	// }

//	public static void gerarMensagemJson(HttpServletResponse response, String mensagem, boolean success) {
//		try {
//			response.setContentType("application/json");
//			response.getWriter().write(gerarMensagemJson(mensagem, success));
//		} catch (IOException e1) {
//		}
//	}

	public static String gerarMensagemJson(String mensagem, boolean success) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"success\":").append(success).append(",");
		sb.append("\"msg\":\"").append(mensagem).append("\"");
		sb.append("}");
		return sb.toString();
	}

//	public static void gerarRetornoJson(HttpServletResponse response, String mensagem, String obj, boolean success) {
//		try {
//			StringBuilder sb = new StringBuilder();
//			sb.append("{");
//			sb.append("\"success\":").append(success).append(",");
//			sb.append("\"msg\":\"").append(mensagem).append("\",");
//			sb.append("\"obj\":").append(obj);
//			sb.append("}");
//
//			response.setContentType("application/json");
//			response.getWriter().write(sb.toString());
//		} catch (IOException e1) {
//		}
//	}

	public static String formatarTelefone(String numFone) {
		if (numFone.length() > 8)
			return mascaraGenerica("#####-####", numFone);

		return mascaraGenerica("####-####", numFone);
	}

	public static String formatarTelefone(Long numFone) {
		return formatarTelefone(numFone.toString());
	}

//	public static void writerResponse(HttpServletResponse response, String content) throws ServletException, IOException {
//		response.setContentType("application/json");
//		response.getWriter().write(content);
//	}

	// /**
	// * Verifica se o ambiente é de produção.<br>
	// * @author marcio.correa
	// * @since 16/12/2015
	// * @return TRUE: produção | FALSE: homologação
	// * @throws IOException
	// */
	// public static boolean isAmbienteProducao() throws IOException {
	// if (Constante.AMBIENTE_PRODUCAO) {
	// return true;
	// } else {
	// return false;
	// }
	// }

	// /**
	// * Verifica qual escopo está a aplicação para validação no OAUth2
	// * referente ao serviço Minutrade.<br>
	// * <br>
	// * @author marcio.correa
	// * @since 16/10/2015
	// * @return String[] : scope
	// * @throws IOException
	// */
	// public static String[] getScope() throws IOException {
	// if (isAmbienteProducao()) {
	// return new String[] { "nfpr:creditos" };
	// } else {
	// return new String[] { "h:nfpr:creditos" };
	// }
	// }

	// /**
	// * Verifica o ambiente e retorna a configuração correta para o RPRPlugin.<br>
	// * <br>
	// * @author marcio.correa
	// * @since 16/12/2015
	// * @return RPRPluginConfig
	// * @throws IOException
	// */
	// public static RPRPluginConfig getRPRPluginConfig() throws IOException {
	// return new RPRPluginConfig("jrprcli.properties");
	// }

	
//	/**
//	 * Define o objeto do usuario logado na sessao
//	 * 
//	 * @author lgmcjr@celepar.pr.gov.br
//	 * @since 29/12/2020
//	 */
//	public static void setInfoUsuarioLogado(HttpServletRequest request, UserInfoDTO userInfoDTO) {
//		request.getSession().setAttribute(OAuth2AuthenticationFilter.SESSION_ATTRIBUTE_USER_INFO, userInfoDTO);
//	}

	/**
	 * Retorna um numero com formatacao Monetaria com o simbolo da moeda, inserindo sinal negativo quando menor que zero.
	 * 
	 * @author ddominoni
	 * @since 25/04/2012
	 * @param number
	 * @return number
	 * @throws Exception
	 */
	public static String formataMoeda(double number, String simbolo) {
		StringBuffer sb = new StringBuffer();

		if (number < 0) {
			number *= -1.0d;// transforma em positivo, pois o sinal sera adicionado antes do simbolo da moeda
			sb.append("- ");
		}
		if (simbolo != null) {
			sb.append(simbolo.toUpperCase()).append(" ");
		}
		NumberFormat formatter = new DecimalFormat("###,###,###,##0.00");

		return sb.append(formatter.format(number)).toString();
	}
	
	public static String formataMoedaReal(Double number) {
		return formataMoeda(((number != null) ? number : 0), "R$");
	}

	public static String limparEspacosDuplosETruncar(String string, int tamanho) {
		if (string != null)
			return StringUtils.abbreviate(string.replaceAll("\\s+", " "), tamanho);

		return null;
	}

	/**
	 * String to Date
	 * 
	 * @author lgmcjr@celepar.pr.gov.br
	 * @since 30/01/2023
	 */
	public static Date strToDate(String dataFmt, String padrao) {
		Date data = null;
		if(StringUtils.isNotBlank(dataFmt) && StringUtils.isNotBlank(padrao)) {
			try {
				data = new SimpleDateFormat(padrao).parse(dataFmt);
			} catch (ParseException e) {
				data = null;
			}
		}
		return data;
	}
	
	public static boolean ehAlfanumerico(String valor) {
		if (StringUtils.isNotBlank(valor)) {
			return StringUtils.isAlphanumeric(valor.trim());
		}
		return false;
	}

	/**
	 * Formata uma data de acordo com o padrao especificado.
	 * 
	 * <pre>
	 * Exemplos de Padrao:
	 * "HH:mm"          = 14:30
	 * "HH:mm:ss"        = 14:30:35
	 * "dd/MM/yyyy"       = 10/04/2008
	 * "dd/MM/yyyy HH:mm"    = 10/04/2008 14:30
	 * "dd 'de' MMMM 'de' yyyy" = 07 de Abril de 2008
	 * "yyyy-MM-dd HH:mm:ss.SSS" = 2008-04-07 18:16:43.991
	 * </pre>
	 * 
	 * @author Digam
	 * @since 05/04/2008
	 * @param Date
	 * @return String
	 */
	public static String formatarData(Date date, String padrao) {

		String data = "";
		if (date != null && StringUtils.isNotBlank(padrao)) {
			data = new SimpleDateFormat(padrao).format(date);
		}

		return data;

	}

	/**
	 * Obtem o primeiro dia do mês da data informada.<br>
	 * Ex.: 01/12/1999 (dd/MM/yyyy) <br>
	 * 
	 * @author marcio.correa
	 * @since 11/07/2016
	 * @param data
	 * @return
	 */
	public static String obterPrimeiroDiaDoMes(Date data) {
		if (data == null)
			return null;

		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return Util.formatarData(cal.getTime(), "dd/MM/yyyy");
	}

	public static Double parseValorMonetario(String valor) {
		if (StringUtils.isNotBlank(valor)) {
			return Double.valueOf(valor.replaceAll("\\.", "") // Retira todos os pontos(.)
					.replaceAll(",", ".") // Troca vírgula por ponto(.)
					.replaceAll("[^\\d\\.]", "")); // Retira tudo que não seja dígito e ponto(.));
		}

		return Double.valueOf("0");
	}
	
	/**
	 * BigDecimal to Integer
	 * 
	 * @author lgmcjr@celepar.pr.gov.br
	 * @since 13/08/2020
	 */
	public static Integer bigDecToInt(BigDecimal valor, Integer padrao) {
		Integer res = null;
		try {
			res = ((valor != null) ? valor.intValue() : padrao);
		} catch (Exception e) {
			res = padrao;
		}
		return res;
	}
	
	/**
	 * BigDecimal to Long
	 * 
	 * @author lgmcjr@celepar.pr.gov.br
	 * @since 13/08/2020
	 */
	public static Long bigDecToLong(BigDecimal valor, Long padrao) {
		Long res = null;
		try {
			res = ((valor != null) ? valor.longValue() : padrao);
		} catch (Exception e) {
			res = padrao;
		}
		return res;
	}
	
	/**
	 * BigDecimal to Double
	 * 
	 * @author lgmcjr@celepar.pr.gov.br
	 * @since 13/08/2020
	 */
	public static Double bigDecToDouble(BigDecimal valor, Double padrao) {
		Double res = null;
		try {
			res = ((valor != null) ? valor.doubleValue() : padrao);
		} catch (Exception e) {
			res = padrao;
		}
		return res;
	}
	
	/**
	 * BigDecimal to BigInteger
	 * 
	 * @author lgmcjr@celepar.pr.gov.br
	 * @since 19/08/2020
	 */
	public static BigInteger bigDecToBigInt(BigDecimal valor, BigInteger padrao) {
		BigInteger res = null;
		try {
			res = ((valor != null) ? valor.toBigInteger() : padrao);
		} catch (Exception e) {
			res = padrao;
		}
		return res;
	}
	
	/**
	 * BigInteger to Long
	 * 
	 * @author lgmcjr@celepar.pr.gov.br
	 * @since 07/10/2022
	 */
	public static Long bigIntToLong(BigInteger valor, Long padrao) {
		Long res = null;
		try {
			res = ((valor != null) ? valor.longValue() : padrao);
		} catch (Exception e) {
			res = padrao;
		}
		return res;
	}
	
	/**
	 * BigInteger to Integer
	 * 
	 * @author lgmcjr@celepar.pr.gov.br
	 * @since 07/10/2022
	 */
	public static Integer bigIntToInt(BigInteger valor, Integer padrao) {
		Integer res = null;
		try {
			res = ((valor != null) ? valor.intValue() : padrao);
		} catch (Exception e) {
			res = padrao;
		}
		return res;
	}
	
	/**
	 * String to Integer
	 * 
	 * @author lgmcjr@celepar.pr.gov.br
	 * @since 05/03/2021
	 */
	public static Integer stringToInt(String valor, Integer padrao) {
		Integer res = null;
		try {
			res = ((valor != null) ? Integer.valueOf(valor) : padrao);
		} catch (Exception e) {
			res = padrao;
		}
		return res;
	}
	
	/**
	 * String to Long
	 * 
	 * @author lgmcjr@celepar.pr.gov.br
	 * @since 05/03/2021
	 */
	public static Long stringToLong(String valor, Long padrao) {
		Long res = null;
		try {
			res = ((valor != null) ? Long.valueOf(valor) : padrao);
		} catch (Exception e) {
			res = padrao;
		}
		return res;
	}
	
	/**
	 * Character to String
	 * 
	 * @author lgmcjr@celepar.pr.gov.br
	 * @since 05/01/2021
	 */
	public static String charToString(Character valor, String padrao) {
		String res = null;
		try {
			res = ((valor != null) ? String.valueOf(valor) : padrao);
		} catch (Exception e) {
			res = padrao;
		}
		return res;
	}
	
	/**
	 * Formata o numero do processo 
	 * 
	 * @author lgmcjr@celepar.pr.gov.br
	 * @since 19/08/2020
	 */
	public static String formatarNrProcesso(BigInteger nrProcesso) {
		String nrProcFmt = "";
		if(nrProcesso != null) {
			String num = completarComZero(nrProcesso.toString(), 20, 1);
			nrProcFmt = nrProcFmt
				.concat(num.substring(0, 7))
				.concat("-")
				.concat(num.substring(7, 9))
				.concat(".")
				.concat(num.substring(9, 13))
				.concat(".")
				.concat(num.substring(13, 14))
				.concat(".")
				.concat(num.substring(14, 16))
				.concat(".")
				.concat(num.substring(16, 20));
		}
		return nrProcFmt;
	}
	
	/**
	 * <p>Checar se o Número do Processo é válido</p>
	 * 
	 * @author avpferreira@celepar.pr.gov.br
	 * @since 23/09/2020
	 */	
	public static boolean validarNrProcesso(BigInteger nrProcesso) {		  
		if (numeroIsNullOrZero(nrProcesso)) {
			return false;
		}
		if (nrProcesso.toString().length() > 20) {
			return false;
		}

		String nrProcessoStr      = Util.mascaraGenerica("#######.##.####.#######", Util.completarComZero(nrProcesso.toString(), 20, 1));
        long nrSequencial         = Long.parseLong(nrProcessoStr.split("\\.")[0]);
        long dv                   = Long.parseLong(nrProcessoStr.split("\\.")[1]);
		long anoAjuizamento       = Long.parseLong(nrProcessoStr.split("\\.")[2]);
		long orgaoTribunalUnidade = Long.parseLong(nrProcessoStr.split("\\.")[3]);
		long orgao                = Long.parseLong(nrProcessoStr.substring(16, 17));		
		long calculoDv            = (98 - ((((((nrSequencial * 10000) + anoAjuizamento) % 97) * 1000000000) + (orgaoTribunalUnidade * 100)) % 97));
		
		if (anoAjuizamento <= 1900 || anoAjuizamento > ano(new Date()) || orgao == 0) {
			return false;
		}
		
		return (dv == calculoDv);
	}
	
	/**
	 * <p>Checar se Número do Processo é inválido</p>
	 * 	
	 * @author avpferreira@celepar.pr.gov.br
	 * @since 23/09/2020
	 */		
	public static boolean numeroProcessoInvalido(BigInteger nrProcesso) {
		return !validarNrProcesso(nrProcesso);
	}
	
	/**
	 * <p>Checar se Número é null ou 0</p>
	 * 
	 * @author avpferreira@celepar.pr.gov.br
	 * @since 23/09/2020
	 */		
	public static boolean numeroIsNullOrZero(Number nr) {
		return (nr == null || nr.longValue()==0);
	}	
	
	/**
	 * <p>Checar se Número é diferente de null ou 0</p>
	 * 	
	 * @author avpferreira@celepar.pr.gov.br
	 * @since 23/09/2020
	 */		
	public static boolean numeroIsNotEmpty(BigInteger nrProcesso) {
		return !numeroIsNullOrZero(nrProcesso);
	}
	
	/**
	 * <p>Retorna o ano de uma data</p>
	 * 	
	 * @author avpferreira@celepar.pr.gov.br
	 * @since 23/09/2020
	 */			
	public static Integer ano(Date data) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		return cal.get(Calendar.YEAR);
	}
	/**
	 * <p>Retorna dia da semana de uma data</p>
	 * 	
	 * @author avpferreira@celepar.pr.gov.br
	 * @since 08/09/2020
	 */				
	public static Integer diaDaSemana(Date data) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		return cal.get(Calendar.DAY_OF_WEEK);		
	}
	
	/**
	 * <p>Retorna dia do mês de uma data</p>
	 * 	
	 * @author avpferreira@celepar.pr.gov.br
	 * @since 08/09/2020
	 */				
	public static Integer diaDoMes(Date data) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		return cal.get(Calendar.DAY_OF_MONTH);		
	}
	
	
	public static final Date formatData(String pattern, String data) {
		Date dt = null;
		try {
			if(StringUtils.isNotBlank(pattern) && StringUtils.isNotBlank(data)) {
				SimpleDateFormat format = new SimpleDateFormat(pattern);
				dt = new Date(format.parse( data ).getTime());
			}
		} catch (Exception e) {
			// .. 
		}
		return dt;
	}
	
	/**
	 * Retorna o ano atual
	 * 
	 * @author lgmcjr@celepar.pr.gov.br
	 * @since 09/10/2020
	 */
	public static Integer anoAtual() {
		Calendar cal = GregorianCalendar.getInstance();
		return cal.get(Calendar.YEAR);
	}
	
	/**
	 * <p>Retorna true se algum parametro da lista estiver em branco</p>
	 * 
	 * @author avpferreira@celepar.pr.gov.br
	 * @since  26/10/2020
	 */
	public static boolean isBlank(Object... values) {
		for(Object obj : values) {
			if(obj==null) {
			    return true;
			}
			if(obj instanceof String && StringUtils.isBlank(((String) obj))) {
			    return true;
			}
		}
		return false;
	}
	
	public static boolean isNotBlank(Object... values) {
		return !isBlank(values); 
	}
	
	/**
	 * Retorna o IP do servidor
	 * 
	 * @author lgmcjr@celepar.pr.gov.br
	 * @since 11/01/2021
	 */
	public static String getServerIp() {
		String srvIp = "0.0.0.0";
		try {
			InetAddress ia = InetAddress.getLocalHost();
			srvIp = ia.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return srvIp;
	}
	
	/**
	 * Retorna a ultima data do mes atual
	 * 
	 * @author lgmcjr@celepar.pr.gov.br
	 * @since 11/01/2021
	 */
	public static Date getUltimaDataMesAtual() {
		Calendar  calendar  = Calendar.getInstance();
		Integer   mesAtual  = calendar.get(Calendar.MONTH) + 1;
		LocalDate localDate = LocalDate.now().withMonth(mesAtual).with(TemporalAdjusters.lastDayOfMonth());
		return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}
	
	/**
	 * Retorna a ultima data do proximo mes
	 * 
	 * @author lgmcjr@celepar.pr.gov.br
	 * @since 11/01/2021
	 */
	public static Date getUltimaDataProxMes() {
		Calendar calendar = Calendar.getInstance();
		Integer  proxMes  = (calendar.get(Calendar.MONTH) + 1) + 1;
		if(proxMes > 12) {
			proxMes -= 12;
		}
		LocalDate localDate = LocalDate.now().withMonth(proxMes).with(TemporalAdjusters.lastDayOfMonth());
		return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}
	
	/**
	 * Retorna a data atual no formato DDMMAAAA
	 * 
	 * @author lgmcjr@celepar.pr.gov.br
	 * @since 11/01/2021
	 */
	public static String getDataAtualDDMMAAAA() {
		return formatarData(new Date(), "ddMMyyyy");
	}
	
	/**
	 * Retorna a ultima data do mes atual no formato DDMMAAAA
	 * 
	 * @author lgmcjr@celepar.pr.gov.br
	 * @since 11/01/2021
	 */
	public static String getUltimaDataMesAtualDDMMAAAA() {
		return formatarData(getUltimaDataMesAtual(), "ddMMyyyy");
	}
	
	/**
	 * Retorna a ultima data do proximo mes no formato DDMMAAAA
	 * 
	 * @author lgmcjr@celepar.pr.gov.br
	 * @since 11/01/2021
	 */
	public static String getUltimaDataProxMesDDMMAAAA() {
		return formatarData(getUltimaDataProxMes(), "ddMMyyyy");
	}
	
//	/**
//	 * Retorna o idioma / pais
//	 * 
//	 * @author lgmcjr@celepar.pr.gov.br
//	 * @since 17/03/2021
//	 */
//	public static Locale getLocale(HttpServletRequest request) {
//		return (request != null) ? request.getLocale() : null;
//	}
//	
//	/**
//	 * Retorna o idioma / pais
//	 * 
//	 * @author lgmcjr@celepar.pr.gov.br
//	 * @since 17/03/2021
//	 */
//	public static String getLocaleString(HttpServletRequest request) {
//		String padrao  = "pt_BR";
//		String lngPais = padrao;
//		Locale locale  = getLocale(request);
//		if(locale != null) {
//			lngPais = locale.getLanguage().concat("_").concat(locale.getCountry());
//		}
//		return lngPais;
//	}
	
	public static boolean isNotNull(Object obj) {
		return obj != null;
	}

	public static boolean isNull(Object obj) {
		return obj == null;
	}

	public static String removerAcentos(String str) {
		String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		return pattern.matcher(nfdNormalizedString).replaceAll("");
	}
	
//	public static boolean validarCadIcmsPR(String valor) {
//        try {
//            if (!Util.numericoDiferenteZeros(valor)) {
//                return true;
//            }
//            if (valor.length() < 10) {
//            	valor = StringUtils.leftPad(valor, 10, "0");
//            }
//            if (verificaPosicoes(valor)) {
//                return true;
//            }
//            boolean verificaPrimeiroDigito = Modulo11Validator.validar(valor.substring(0, 8), 2, 7) == Integer.parseInt(valor.substring(8).substring(0, 1));
//            boolean verificaSegundoDigito = Modulo11Validator.validar(valor.substring(0, 9), 2, 7) == Integer.parseInt(valor.substring(9).substring(0, 1));
//            return !verificaPrimeiroDigito || !verificaSegundoDigito;
//        } catch (NumberFormatException ex) {
//            return false;
//        }
//    }
	
//	public static boolean validarCadIcmsPR(Long valor) {
//		try {
//			String num = String.format("%010d", valor);
//
//			MaskFormatter mf = new MaskFormatter("########-##");
//			mf.setValueContainsLiteralCharacters(false);
//			String documento = mf.valueToString(num);
//
//			return validarCadIcmsPR(documento);
//		} catch (ParseException e) {
//			return false;
//		}
//	}

	private static boolean verificaPosicoes(String valor) {
        int aux = Integer.parseInt(valor.substring(0, 3), 10);
        return !((aux >= 99 && aux <= 139) || (aux >= 201 && aux <= 222) || (aux >= 301 && aux <= 346) ||
                (aux >= 401 && aux <= 467) || (aux >= 501 && aux <= 539) || (aux >= 601 && aux <= 659) ||
                (aux >= 701 && aux <= 746) || (aux >= 801 && aux <= 853) || (aux >= 901 && aux <= 910));
    }
	
	/**
	 * Verifica se a string só contém numeros de 1 a 9
	 * @param valor
	 * @return
	 */
	public static boolean numericoDiferenteZeros(String valor){
		Pattern containsNumber = Pattern.compile("^[0-9]*$");
		Pattern containsOnlyZero = Pattern.compile("^[0]*$");
		Matcher matcherNumber = containsNumber.matcher(valor);
		Matcher matcherZero = containsOnlyZero.matcher(valor);
		return matcherNumber.find() && !matcherZero.find();
	}
	
//	/**
//	 * Retorna o endereco IP pelo request 
//	 *
//	 * @author lgmcjr@celepar.pr.gov.br
//	 * @since 14/06/2024
//	 */
//	public static String getEnederecoIp(HttpServletRequest request) {
//		return (request != null) ? request.getRemoteAddr() : null;
//	}
	
	/**
	 * Retorna a consulta com os parametros 
	 *
	 * @author lgmcjr@celepar.pr.gov.br
	 * @since 20/09/2024
	 */
	public static String getConsultaComParams(String consulta, Map<String, Object> params) {
		String con = StringUtils.defaultIfBlank(consulta, "");
		if((StringUtils.isNotBlank(con)) && (params != null) && (!params.isEmpty())) {
			con = con.replaceAll(" +", " ");
			for(Map.Entry<String, Object> param : params.entrySet()) {
				con = StringUtils.replace(
					con, ":".concat(param.getKey()), 
					getValorParamConsultaFmt(param.getValue()));
			}
		}
		return con;
	}
	
	/**
	 * Retorna o parametro da consulta formatado de acordo com o tipo 
	 *
	 * @author lgmcjr@celepar.pr.gov.br
	 * @since 20/09/2024
	 */
	@SuppressWarnings("rawtypes")
	private static String getValorParamConsultaFmt(Object val) {
		String fmt = "";
		if(val == null) {
			fmt = "NULL";
		} else if(val instanceof Date) {
			fmt = "'".concat((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format((Date)val)).concat("'");
		} else if(val instanceof Number) {
			fmt = ((Number)val).toString();
		} else if(val instanceof Collection) {
			Object[] col = ((Collection)val).toArray();
			if((col != null) && (col.length > 0)) {
				List<String> lstFmt = new ArrayList<>();
				for(int i = 0; i < col.length; i++) {
					lstFmt.add(getValorParamConsultaFmt(col[i]));
				}
				fmt = String.join(",", lstFmt);
			}
		} else {
			fmt = "'".concat(val.toString()).concat("'");
		}
		return fmt;
	}
}