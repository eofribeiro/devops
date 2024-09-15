package br.puc.devops.escola;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class EscolaExemplo {

	private static final String PATH_ARQUIVO = "c:\\temp\\grupo95-evelyne-escola-exemplo.dat";
	private ArrayList<Estudante> estudantes;

	public EscolaExemplo() {
		this.estudantes = new ArrayList<Estudante>();
	}

	public void adicionaEstudante(Estudante mani) {
		this.estudantes.add(mani);
	}

	public void listarEstudantes() {
		for (Estudante est : estudantes) {
			System.out.println(est.toString());
		}
		System.out.println("Total = " + this.estudantes.size() + " estudantes listados com sucesso!\n");
	}

	public void excluirEstudante(Estudante est) {
		if (this.estudantes.contains(est)) {
			this.estudantes.remove(est);
			System.out.println("[Estudante " + est.toString() + "exclu�do com sucesso!]\n");
		} else {
			System.out.println("Estudante inexistente!\n");
		}
	}

	public void excluirEstudantes() {
		estudantes.clear();
		System.out.println("Estudantes exclu�dos com sucesso!\n");
	}

	public void gravarEstudantes() {
		ObjectOutputStream outputStream = null;
		try {
			File arquivo = new File(PATH_ARQUIVO);

			if (!arquivo.exists()) {
				if (!arquivo.getParentFile().exists()) {
					arquivo.getParentFile().mkdirs();
				}

				arquivo.createNewFile();
			}

			outputStream = new ObjectOutputStream(new FileOutputStream(PATH_ARQUIVO));
			for (Estudante est : estudantes) {
				outputStream.writeObject(est);
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
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

	public void recuperarEstudantes() {
		ObjectInputStream inputStream = null;
		try {
			inputStream = new ObjectInputStream(new FileInputStream(PATH_ARQUIVO));
			Object obj = null;
			while ((obj = inputStream.readObject()) != null) {
				if (obj instanceof PreEscolar)
					this.estudantes.add((PreEscolar) obj);
				else if (obj instanceof EnsinoFundamental)
					this.estudantes.add((EnsinoFundamental) obj);
				else if (obj instanceof EnsinoMedio)
					this.estudantes.add((EnsinoMedio) obj);
			}
		} catch (EOFException ex) { // when EOF is reached
			System.out.println("End of file reached");
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
					System.out.println("Estudantes listados com sucesso!\n");
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		EscolaExemplo esc = new EscolaExemplo();

		PreEscolar maria = new PreEscolar(1, "Maria", 2, "Angelina");
		PreEscolar luan = new PreEscolar(2, "Luan", 4, "Rodrigo");
		EnsinoFundamental nathan = new EnsinoFundamental(3, "Nathan", 6, "Evelyne");
		EnsinoFundamental inae = new EnsinoFundamental(4, "Ina�", 7, "M�nica");
		EnsinoMedio nicolas = new EnsinoMedio(5, "Nicolas", 17, "Marcelo");
		EnsinoMedio isabela = new EnsinoMedio(6, "Isabela", 18, "S�nia");

		esc.adicionaEstudante(maria);
		esc.adicionaEstudante(luan);
		esc.adicionaEstudante(nathan);
		esc.adicionaEstudante(inae);
		esc.adicionaEstudante(nicolas);
		esc.adicionaEstudante(isabela);
		esc.listarEstudantes();
		esc.gravarEstudantes();
		esc.excluirEstudante(maria);
		esc.listarEstudantes();
		esc.excluirEstudantes();
		esc.listarEstudantes();
		esc.recuperarEstudantes();
		esc.listarEstudantes();
	}

}
