package br.puc.devops.evelyne;

public class PreEscolar extends Estudante {


	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4068243008589910233L;

	public PreEscolar(int matricula, String nome, int idade, String responsavel) {
		super(matricula, nome, idade, responsavel);
		this.nivel = "Ensino Pr�-Escolar";
	}

	@Override
	public String cuidados() {
		String cuidados = "1. Ficha m�dica para verificar restri��es de sa�de.\n 2. Ficha de alimenta��o e cuidados familiares.\n 3. Em caso de febre acima de 38,5�, acionar o respons�vel";
		
		if(this.getIdade() < 4) {
			cuidados += "\n 4. Trocas de fraldas no m�ximo de 3 em 3 horas";			
		}
		
		return cuidados;
	}

	@Override
	public String atividades() {
		String atividades = "1. Musicaliza��o \n 2.Brinquedos sensoriais \n 3. Hora da hist�ria \n 4. Descobrindo os alimentos";
		return atividades;
	}

	@Override
	public String oficinas() {
		String oficinas = "1. Imitar os sons dos animais \n 2. Brincando na areia";
		
		if(this.getIdade() > 2) {
			oficinas += "\n 3. Pintura a dedo \n 4. Esculturas com massinha de modelar \n 5. Pequenos cientistas";			
		}
		
		return oficinas;
	}
	
	
}
