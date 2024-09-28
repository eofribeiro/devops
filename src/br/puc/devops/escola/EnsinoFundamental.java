package br.puc.devops.escola;

public class EnsinoFundamental extends Estudante {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5499368834431553970L;

	public EnsinoFundamental(int matricula, String nome, int idade, String responsavel) {
		super(matricula, nome, idade, responsavel);
		this.nivel = "Ensino Fundamental";
	}

	@Override
	public String cuidados() {
		String cuidados = "1. Ficha médica e cuidados familiares.\n 2.  É‰ necessário 1 monitor para cada 5 alunos";
		return cuidados;
	}

	@Override
	public String atividades() {
		String atividades = "1. Judô \n 2.Natação \n 3. Robótica \n 4. Xadrez";
		return atividades;
	}

	@Override
	public String oficinas() {
		String oficinas = "1. Redação \n 2. Construção de brinquedos com materiais recicláveis \n 3. Clube da Matemática";
		return oficinas;
	}

}
