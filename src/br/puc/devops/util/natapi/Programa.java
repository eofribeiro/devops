package br.puc.devops.util.natapi;

/**
 * @author rodrigo.hjort
 */
public class Programa {

    private String nome;
    private Entrada entrada;
    private Saida saida;
    
    public Programa(String nome) {
        setNome(nome);
    }

    public String getNome() {
        return (nome);
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public Entrada getEntrada() {
        return (entrada);
    }
    public void setEntrada(Entrada entrada) {
        this.entrada = entrada;
    }
    public Saida getSaida() {
        return (saida);
    }
    public void setSaida(Saida saida) {
        this.saida = saida;
    }

    public String toString() {
        return getNome();
    }
    
}
