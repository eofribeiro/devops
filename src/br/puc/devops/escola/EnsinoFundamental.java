package br.puc.devops.escola;

public class EnsinoFundamental extends Estudante {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3108441687995880347L;

	public EnsinoFundamental(int matricula, String nome, int idade, String responsavel) {
		super(matricula, nome, idade, responsavel);
		this.nivel = "Ensino Fundamental";
	}

	@Override
	public String cuidados() {
		String cuidados = "1. Ficha m�dica e cuidados familiares.\n 2. � necess�rio 1 monitor para cada 5 alunos";
		return cuidados;
	}

	@Override
	public String atividades() {
		String atividades = "1. Jud� \n 2.Nata��o \n 3. Rob�tica \n 4. Xadrez";
		return atividades;
	}

	@Override
	public String oficinas() {
		String oficinas = "1. Reda��o \n 2. Constru��o de brinquedos com materiais recicl�veis \n 3. Clube da Matem�tica";
		return oficinas;
	}

}
