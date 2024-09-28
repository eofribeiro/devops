package br.puc.devops.natapi;


//package gov.celepar.adabas; --> PACOTE ORIGINAL DA LIB NATAPI-2.0.0

import java.util.ResourceBundle;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

public class Natural {
	private static final String VERSION = "Versao - 1.0 - Cliente para Midlleware JASPPION";

	private String chave;

	private String senha;

	private String ambiente;

	private String logon;

	private String serverAddr;

	private String serverPort;

	private String tipoPgm;

	private int paRC;

	private String paMSG;

	private String parmSaida;

	private ResourceBundle rb;

	private static Logger log = Logger.getLogger(Natural.class);

	private void setDefaults() {
		this.chave = "";
		this.senha = "";
		this.logon = "";
		this.ambiente = "D";
		this.serverAddr = "10.15.61.99";
		this.serverPort = "9999";
		this.tipoPgm = "dbcon";
		this.parmSaida = "";
	}

	public static boolean isNumeric(String campo) {
		if (campo.length() == 0)
			return false;
		boolean ret = true;
		for (int ix1 = 0; ix1 < campo.length(); ix1++) {
			if (campo.charAt(ix1) < '0' || campo.charAt(ix1) > '9')
				ret = false;
		}
		return ret;
	}

	public Natural() {
		setDefaults();
	}

	public Natural(String identificador, String parmEntrada) {
		setDefaults();
		if (identificador.length() < 3) {
			this.paRC = -9999;
			this.paMSG = "PGMNAT (" + identificador + ") INVALIDO";
			return;
		}
		identificador = identificador.trim().toUpperCase();
		String prefixo = identificador.substring(0, 3);
		try {
			this.rb = ResourceBundle.getBundle(prefixo);
			this.chave = this.rb.getString(String.valueOf(prefixo) + "_Chave");
			this.senha = this.rb.getString(String.valueOf(prefixo) + "_Senha");
			this.ambiente = this.rb.getString(String.valueOf(prefixo) + "_Ambiente");
			this.serverAddr = this.rb.getString(String.valueOf(prefixo) + "_ServerAddr");
			this.serverPort = this.rb.getString(String.valueOf(prefixo) + "_ServerPort");
			String[] str1 = this.rb.getString(identificador).split(";");
			this.logon = str1[0];
			this.tipoPgm = str1[1];
			execute(identificador, parmEntrada);
		} catch (Exception e) {
			this.paRC = -9999;
			this.paMSG = e.getMessage();
			int index = this.paMSG.indexOf("PropertyResourceBundle, key");
			if (index > 0) {
				this.paMSG = "Nencontrou entrada (" + this.paMSG.substring(index + 28) + ") no arquivo " + prefixo
						+ ".properties";
			} else if (this.paMSG.indexOf("bundle for base name") > 0) {
				this.paMSG = "Nencontrou arquivo " + prefixo + ".properties";
			}
			if (this.paMSG.length() > 60)
				this.paMSG = this.paMSG.substring(this.paMSG.length() - 60);
			return;
		}
	}

	public void execute(String PgmNat, String DadosEntrada) {
		try {
			if (PgmNat.length() < 3) {
				this.paRC = -9999;
				this.paMSG = "PGMNAT (" + PgmNat + ") INVALIDO";
				return;
			}
			if (PgmNat.length() < 8)
				PgmNat = String.valueOf(PgmNat) + "        ".substring(0, 8 - PgmNat.length());
			PgmNat = PgmNat.substring(0, 8).toUpperCase();
			this.tipoPgm = this.tipoPgm.toLowerCase().trim();
			if (this.tipoPgm.length() < 3)
				this.tipoPgm = "dbcon";
			String Jasppion = "jaspin";
			if (this.tipoPgm.equals("dbgateway"))
				Jasppion = "jasppion";
			String Aplic = PgmNat.substring(0, 3);
			if (this.ambiente.length() == 0)
				this.ambiente = "D";
			this.ambiente = this.ambiente.toUpperCase().trim();
			if (!this.ambiente.equals("D") && !this.ambiente.equals("P"))
				this.ambiente = "D";
			if (this.ambiente.toUpperCase().equals("P")) {
				this.logon = "PRODUCAO";
			} else if (this.logon.length() == 0) {
				this.logon = "N000" + Aplic + " ";
			}
			if (this.logon.length() < 8)
				this.logon = String.valueOf(this.logon) + "        ".substring(0, 8 - this.logon.length());
			this.logon = this.logon.substring(0, 8).toUpperCase();
			if (this.serverAddr.length() == 0)
				this.serverAddr = "10.15.61.99";
			this.serverAddr = this.serverAddr.trim();
			if (this.serverPort.length() == 0)
				this.serverPort = "9999";
			this.serverPort = this.serverPort.trim();
			if (this.chave.length() == 0) {
				this.paRC = -9999;
				this.paMSG = "Ninformou chave do usuario";
				return;
			}
			this.chave = this.chave.trim();
			if (this.chave.length() < 6)
				this.chave = String.valueOf("000000".substring(0, 6 - this.chave.length())) + this.chave;
			this.chave = this.chave.substring(0, 6);
			if (this.senha.length() == 0) {
				this.paRC = -9999;
				this.paMSG = "Ninformou Senha";
				return;
			}
			this.senha = this.senha.trim();
			if (this.senha.length() < 8)
				this.senha = String.valueOf(this.senha) + "        ".substring(0, 8 - this.senha.length());
			this.senha = this.senha.substring(0, 8).toUpperCase();
			String url = "http://" + this.serverAddr + ":" + this.serverPort + "/cics/cwba/" + Jasppion + "?" + Aplic
					+ this.ambiente + "XXXX" + this.logon + PgmNat + "N" + this.chave + " " + this.senha + "9999"
					+ "                                                            " + DadosEntrada + "!" + Jasppion
					+ "_fim_de_dados!";
			log.debug(url);
			url = url.replaceAll("(#| )", "%20");
			this.paRC = -9999;
			this.paMSG = "";
			this.parmSaida = "";
			HttpClient client = new HttpClient();
			PostMethod post = new PostMethod(url.toString());
			this.paRC = client.executeMethod((HttpMethod) post);
			if (this.paRC != 200) {
				this.paMSG = "ERRO DE COMUNICACAO COM O SERVIDOR JASPPION.";
			} else {
				String strRet = post.getResponseBodyAsString();
				if (strRet.length() < 56) {
					this.paRC = -9999;
					this.paMSG = "FORA DE LAYOUT PADRAO(4+60)";
					return;
				}
				if (!isNumeric(strRet.substring(52, 56))) {
					this.paRC = -9999;
					this.paMSG = "PA-RC NNUMERICO (" + strRet.substring(52, 56) + ")";
					return;
				}
				this.paRC = (new Integer(strRet.substring(52, 56))).intValue();
				this.paMSG = strRet.substring(56, 116);
				if (strRet.length() > 116)
					this.parmSaida = strRet.substring(116, strRet.length() - 15);
			}
		} catch (Exception e) {
			this.paRC = -9999;
			this.paMSG = e.getMessage();
			if (this.paMSG.length() > 60)
				this.paMSG = this.paMSG.substring(this.paMSG.length() - 60);
			this.parmSaida = "";
		}
	}

	public void setAmbiente(String ambiente) {
		this.ambiente = ambiente;
	}

	public void setChave(String chave) {
		this.chave = chave;
	}

	public void setLogon(String logon) {
		this.logon = logon;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public void setServerAddr(String serverAddr) {
		this.serverAddr = serverAddr;
	}

	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}

	public void setTipoPgm(String tipoPgm) {
		this.tipoPgm = tipoPgm;
	}

	public int getRC() {
		return this.paRC;
	}

	public String getMSG() {
		return this.paMSG;
	}

	public String getDataParameter() {
		return this.parmSaida;
	}

	public String getVersion() {
		return VERSION;
	}
}
