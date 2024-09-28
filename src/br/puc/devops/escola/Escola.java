package br.puc.devops.escola;

import java.awt.Dimension;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class Escola {
	
	private static final String PATH_ARQUIVO = "c:\\temp\\evelyne-escola.dados";
	
	private ArrayList<Estudante> estudantes;

	public Escola() {
		this.estudantes = new ArrayList<Estudante>();
	}
	public String[] leValores (String [] dadosIn, String prefixo){
		String [] dadosOut = new String [dadosIn.length];

		for (int i = 0; i < dadosIn.length; i++)
			dadosOut[i] = JOptionPane.showInputDialog  (prefixo + " Entre com " + dadosIn[i]+ ": ");

		return dadosOut;
	}

	public PreEscolar lePreEscolar (){

		String [] valores = new String [4];
		String [] nomeVal = {"Matrícula", "Nome", "Idade", "Responsável"};
		valores = leValores (nomeVal, "[Pré]");

		int matricula = this.retornaInteiro(valores[0], nomeVal[0]);
		String nome = (valores[1]);
		int idade = this.retornaInteiro(valores[2], nomeVal[2]);
		String responsavel = (valores[3]);
		

		PreEscolar preEscolar = new PreEscolar (matricula,nome, idade,responsavel);
		return preEscolar;
	}

	public EnsinoFundamental leEnsinoFundamental (){

		String [] valores = new String [4];
		String [] nomeVal = {"Matrícula", "Nome", "Idade", "Responsável"};
		valores = leValores (nomeVal, "[EF]");

		int matricula = this.retornaInteiro(valores[0], nomeVal[0]);
		String nome = (valores[1]);
		int idade = this.retornaInteiro(valores[2], nomeVal[2]);
		String responsavel = (valores[3]);

		EnsinoFundamental cao = new EnsinoFundamental (matricula,nome, idade,responsavel);
		return cao;
	}

	public EnsinoMedio leEnsinoMedio (){

		String [] valores = new String [4];
		String [] nomeVal = {"Matrícula", "Nome", "Idade", "Responsável"};
		valores = leValores (nomeVal, "[EM]");

		int matricula = this.retornaInteiro(valores[0], nomeVal[0]);
		String nome = (valores[1]);
		int idade = this.retornaInteiro(valores[2], nomeVal[2]);
		String responsavel = (valores[3]);

		EnsinoMedio cao = new EnsinoMedio (matricula,nome, idade,responsavel);
		return cao;
	}

	private boolean intValido(String s) {
		try {
			Integer.parseInt(s); // Metodo estatico, que tenta tranformar uma string em inteiro
			return true;
		} catch (NumberFormatException e) { // Não conseguiu tranformar em inteiro e gera erro
			return false;
		}
	}
	public int retornaInteiro(String entrada, String nomeCampo) { // retorna um valor inteiro
		int numInt;

		//Enquanto não for possível converter o valor de entrada para inteiro, permanece no loop
		while (!this.intValido(entrada)) {
			entrada = JOptionPane.showInputDialog(null, nomeCampo + " incorreta!\n\nDigite um número inteiro.");
		}
		
		numInt = Integer.parseInt(entrada);
		
		return numInt;
	}

	/**
	 * Salva os estudantes que estao na memoria junto com os estudantes que estao na base
	 * 
	 * @return
	 */
	public int salvarEstudantes (){
		ArrayList<Estudante> estudantesSalvos = recuperarEstudantes();
		estudantesSalvos.addAll(this.estudantes);
		return salvarEstudantes(estudantesSalvos);		
	}
	
	/**
	 * Antes de salvar, precisa recuperar para não sobrescrever o que j� estava no arquivo
	 * @param mamiferos
	 */
	public int salvarEstudantes (ArrayList<Estudante> lstEstudantes){
		int novos = 0;		

		ObjectOutputStream outputStream = null;
		try {
			verificarCriarArquivo();
			outputStream = new ObjectOutputStream 
					(new FileOutputStream(PATH_ARQUIVO));
			for (int i=0; i < lstEstudantes.size(); i++) {
				outputStream.writeObject(lstEstudantes.get(i));
				novos++;
			}
			
		} catch (FileNotFoundException ex) {
			JOptionPane.showMessageDialog(null,"Impossível criar arquivo!");
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {  //Close the ObjectOutputStream
			try {
				if (outputStream != null) {
					outputStream.flush();
					outputStream.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		return novos;
	}

	private void verificarCriarArquivo() throws IOException {
		File arquivo = new File(PATH_ARQUIVO);

		if (!arquivo.exists()) {
			if (!arquivo.getParentFile().exists()) {
				arquivo.getParentFile().mkdirs();
			}

			arquivo.createNewFile();
		}
		
	}
	@SuppressWarnings("finally")
	public ArrayList<Estudante> recuperarEstudantes (){
		ArrayList<Estudante> estudantesSalvos = new ArrayList<Estudante>();
		ObjectInputStream inputStream = null;

		try {
			verificarCriarArquivo();
			inputStream = new ObjectInputStream
					(new FileInputStream(PATH_ARQUIVO));
			Object obj = null;
			while ((obj = inputStream.readObject()) != null) {
				if (obj instanceof Estudante) {
					estudantesSalvos.add((Estudante) obj);
				}   
			}          
		} catch (EOFException ex) { // when EOF is reached
			System.out.println("Fim de arquivo.");
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} catch (FileNotFoundException ex) {
			JOptionPane.showMessageDialog(null,"Arquivo com estudantes NÃO existe!");
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {  //Close the ObjectInputStream
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (final IOException ex) {
				ex.printStackTrace();
			}
			return estudantesSalvos;
		}
	}

	public void menuEscola (){

		String menu = "";
		String entrada;
		int    opc1, opc2;

		do {
			menu = "Controle Grupo Escolar\n" +
					"Opções:\n" + 
					"1. Cadastrar Estudante\n" +
					"2. Exibir Estudantes não gravados\n" +
					"3. Limpar Estudantes não gravados\n" +
					"4. Gravar Estudantes\n" +
					"5. Exibir Lista de Estudantes (Gravados e não gravados)\n" +
					"6. Excluir Estudante\n" +
					"7. Limpar Base\n" +					
					"9. Sair";
			entrada = JOptionPane.showInputDialog (menu + "\n\n");
			
			if(entrada == null) { // clicou no cancelar
				opc1 = 9;
			} else {
				opc1 = this.retornaInteiro(entrada, "Opção");
			}

			switch (opc1) {
			case 1:// Entrar dados
				menu = "Cadastro de Estudante\n" +
						"Opções:\n" + 
						"1. Pré-Escola\n" +
						"2. Ensino Fundamental\n" +
						"3. Ensino Médio\n" +
						"4. Voltar\n";

				entrada = JOptionPane.showInputDialog (menu + "\n\n");
				
				if(entrada == null) {
					opc2 = 4;
				} else {
					opc2 = this.retornaInteiro(entrada, "Opção");
				}

				switch (opc2){
				case 1: 
					estudantes.add((Estudante)lePreEscolar());
					break;
				case 2: 
					estudantes.add((Estudante)leEnsinoFundamental());
					break;
				case 3: 
					estudantes.add((Estudante)leEnsinoMedio());
					break;
				case 4:
					break;				
				default: 
					JOptionPane.showMessageDialog(null,"Opção "+opc2+" não existe. Tente novamente.");
				}
				break;
			case 2: // Exibir dados
				if (estudantes.size() == 0) {
					JOptionPane.showMessageDialog(null,"Não existem estudantes não gravados.");
					break;
				}				
				exibirEstudantes(this.estudantes);
				break;
			case 3: // Limpar Dados
				if (estudantes.size() == 0) {
					JOptionPane.showMessageDialog(null,"Não existem novos estudantes cadastrados.");
					break;
				}
				estudantes.clear();
				JOptionPane.showMessageDialog(null,"Dados LIMPOS com sucesso!");
				break;
			case 4: // Grava Dados
				if (estudantes.size() == 0) {
					JOptionPane.showMessageDialog(null,"Não existem estudantes novos para salvar.");
					break;
				}
				int qtde = salvarEstudantes(); // salva os registros da mem�ria na base
				String msg = "";
				if(qtde > 0) {
					msg = "Base de alunos atualizada. Os registros em memória foram limpos. Veja a listagem completa no menu [5. Recuperar Estudantes]"; 				
				} else {
					msg = "Nenhum registro novo foi salvo";
				}
				
				JOptionPane.showMessageDialog(null,msg);
				break;
			case 5: // Recupera Dados
					ArrayList<Estudante> estudantesTmp = recuperarEstudantes();
					if (estudantesTmp.size() == 0) {
						JOptionPane.showMessageDialog(null,"Sem dados para apresentar. Não existem estudantes na base de dados.");
						break;
					} else {
						exibirEstudantes(estudantesTmp);
					}
				break;
			case 6: // Exclui um estudante
				ArrayList<Estudante> lstEstudantesBase = recuperarEstudantes();
				
				if(this.estudantes.isEmpty() && lstEstudantesBase.isEmpty()) {
					JOptionPane.showMessageDialog(null,"Sem dados para apresentar. Não existem estudantes na base de dados nem na memória.");
					break;
				}
				
				String [] valores = new String [1];
				String [] nomeVal = {"Matrícula"};
				valores = leValores (nomeVal, "");
				
				int matricula = this.retornaInteiro(valores[0], nomeVal[0]);
				int qtdeMemoria = excluirEstudante(estudantes, matricula, false);
				int qtdeBase = excluirEstudante(lstEstudantesBase, matricula, true);
				
				JOptionPane.showMessageDialog(null,"Foram excluídos ["+qtdeMemoria+"] estudantes da memória e ["+qtdeBase+"] estudantes da base da escola.");
				break;
			case 7:
				String msgExclusao = limparBase();
				JOptionPane.showMessageDialog(null, msgExclusao);
				break;
			case 9:
				JOptionPane.showMessageDialog(null,"Fim do aplicativo Grupo Escolar");
				break;
			}
		} while (opc1 != 9);
	}
	
	private String limparBase() {
		ArrayList<Estudante> estudantesSalvos = recuperarEstudantes();
		
		if(estudantesSalvos.isEmpty()) {
			return "Não existem estudantes cadastrados na base de dados";
		}
		
		if(!estudantesSalvos.isEmpty()) {
			boolean apagou = false;
			File arquivo = new File(PATH_ARQUIVO);
			if (arquivo.exists()) {
				apagou = arquivo.delete();
			}
			
			if(!apagou) {
				ObjectOutputStream outputStream = null;
				try {
					verificarCriarArquivo();
					outputStream = new ObjectOutputStream 
							(new FileOutputStream(PATH_ARQUIVO));

						outputStream.writeObject("");
					//Limpar os estudantes da mem�ria					
				} catch (FileNotFoundException ex) {
					JOptionPane.showMessageDialog(null,"Impossível apagar arquivo!");
					ex.printStackTrace();
				} catch (IOException ex) {
					ex.printStackTrace();
				} finally {  //Close the ObjectOutputStream
					try {
						if (outputStream != null) {
							outputStream.flush();
							outputStream.close();
						}
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
			
			return "Base de dados limpa com sucesso!";
		}
		
		return "";
	}
	
	public int excluirEstudante(ArrayList<Estudante> lstEstudantes, int matricula, boolean regravarBase) {
		int qtdeExcluido = 0;
		List<Estudante> lstEstRemover = new ArrayList<Estudante>();
		
		for (Estudante est : lstEstudantes) {
			if(est.getMatricula() == matricula) {
				lstEstRemover.add(est);
			}
		}
				
		if (!lstEstRemover.isEmpty()) {
			qtdeExcluido = lstEstRemover.size();
			lstEstudantes.removeAll(lstEstRemover);
		}
		
		if(regravarBase) { // se forem dados da base definitiva, regravar
			limparBase();
			salvarEstudantes(lstEstudantes);
		}
		
		return qtdeExcluido;
	}
	
	private void exibirEstudantes(ArrayList<Estudante> lstEstudantes) {
		if (lstEstudantes.size() == 0) {
			JOptionPane.showMessageDialog(null,"Não existem estudantes cadastrados.");			
		} else {
			String dados = "";
			for (int i=0; i < lstEstudantes.size(); i++)	{
				dados += lstEstudantes.get(i).toString() + "---------------\n";
			}
			//JOptionPane.showMessageDialog(null,dados);
			
			JTextArea textArea = new JTextArea(dados);
			JScrollPane scrollPane = new JScrollPane(textArea);  
			textArea.setLineWrap(true);  
			textArea.setWrapStyleWord(true); 
			scrollPane.setPreferredSize( new Dimension( 800, 500 ));
			JOptionPane.showMessageDialog(null, scrollPane, "Estudantes cadastrados e em memória", JOptionPane.INFORMATION_MESSAGE);
			
		}
	}
	public static void main (String [] args){
		Escola escola = new Escola ();
		escola.menuEscola();
	}

}
