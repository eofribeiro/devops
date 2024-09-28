package br.puc.devops.escola;

import java.io.Serializable;

public abstract class Estudante implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2311238404436773907L;
	
	private   int matricula;
	private   String nome;
	private   int idade;
	private   String responsavel;
	protected String nivel;
	
	public Estudante(int matricula, String nome, int idade, String responsavel) {
		this.matricula = matricula;
		this.nome = nome;
		this.idade = idade;
		this.responsavel = responsavel;
	}
	public String toString() {
		String retorno = "";
		retorno += "Matrícula: "         + this.matricula     + "\n";
		retorno += "Nome: "              + this.nome     + "\n";
		retorno += "Idade: "             + this.idade    + " anos\n";
		retorno += "Responsável: "       + this.responsavel     + "\n";
		retorno += "Nível de Ensino: "   + this.nivel  + "\n";
		retorno += "Cuidados: \n"          + cuidados()        + "\n";
		retorno += "Atividades: \n"        + atividades()        + "\n";
		retorno += "Oficinas: \n"          + oficinas()        + "\n";		
		return retorno;
	}
	
	
	public abstract String cuidados();
	public abstract String atividades();
	public abstract String oficinas();
	public int getMatricula() {
		return matricula;
	}
	public void setMatricula(int matricula) {
		this.matricula = matricula;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public int getIdade() {
		return idade;
	}
	public void setIdade(int idade) {
		this.idade = idade;
	}
	public String getResponsavel() {
		return responsavel;
	}
	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}
	
	
}
