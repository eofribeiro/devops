package br.puc.devops.adabas;





import java.util.List;

public class Aplicacao {
	
    // Ambiente
	private String nome;
	private String chave;
	private String senha;
	private String ambiente;	
	private String serverAddr;
	private String serverPort;
	private String tipoPgm;
	
	List<Programa> programas;

	public String getAmbiente() {
		return ambiente;
	}

	public void setAmbiente(String ambiente) {
		this.ambiente = ambiente;
	}

	public String getChave() {
		return chave;
	}

	public void setChave(String chave) {
		this.chave = chave;
	}

	public List<Programa> getProgramas() {
		return programas;
	}

	public void setProgramas(List<Programa> programas) {
		this.programas = programas;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getServerAddr() {
		return serverAddr;
	}

	public void setServerAddr(String serverAddr) {
		this.serverAddr = serverAddr;
	}

	public String getServerPort() {
		return serverPort;
	}

	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}

	public String getTipoPgm() {
		return tipoPgm;
	}

	public void setTipoPgm(String tipoPgm) {
		this.tipoPgm = tipoPgm;
	}
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	
}
