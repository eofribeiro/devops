package br.puc.devops.escola;

public class EnsinoMedio extends Estudante {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1400235323282426501L;

	public EnsinoMedio(int matricula, String nome, int idade, String responsavel) {
		super(matricula, nome, idade, responsavel);
		this.nivel = "Ensino M�dio";
	}

	@Override
	public String cuidados() {
		String cuidados = "1. Ficha m�dica.\n 2. Controle do uso do celular na escola \n 3. Regras gerais da escola";
		return cuidados;
	}

	@Override
	public String atividades() {
		String atividades = "1. Reda��o para ENEM e vestibulares \n 2.Refor�o de Matem�tica \n 3. Refor�o de Portugu�s \n 4. Esportes (Basquete e Futebol)";
		return atividades;
	}

	@Override
	public String oficinas() {
		String oficinas = "1. Programa��o de computadores \n 2. Contabilidade b�sica \n 3. Marcenaria";
		return oficinas;
	}

}
