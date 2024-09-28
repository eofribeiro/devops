package br.puc.devops.escola;

public class EnsinoMedio extends Estudante {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1400235323282426501L;

	public EnsinoMedio(int matricula, String nome, int idade, String responsavel) {
		super(matricula, nome, idade, responsavel);
		this.nivel = "Ensino Médio";
	}

	@Override
	public String cuidados() {
		String cuidados = "1. Ficha médica.\n 2. Controle do uso do celular na escola \n 3. Regras gerais da escola";
		return cuidados;
	}

	@Override
	public String atividades() {
		String atividades = "1. Redação para ENEM e vestibulares \n 2.Reforço de Matemática \n 3. Reforço de Português \n 4. Esportes (Basquete e Futebol)";
		return atividades;
	}

	@Override
	public String oficinas() {
		String oficinas = "1. Programação de computadores \n 2. Contabilidade básica \n 3. Marcenaria";
		return oficinas;
	}

}
