package br.puc.devops.util.natapi;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author rodrigo.hjort
 */
public class Atributo {

    private String nome;
    private int inicio;
    private int fim;
    private int tamanho;
    private Object pai;
    
    public Atributo() {
        this.tamanho = 0;
    }

    public String getNome() {
        return (nome);
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getInicio() {
        return (inicio);
    }
    public void setInicio(int inicio) {
        this.inicio = inicio;
    }
    public int getFim() {
        return (fim);
    }
    public void setFim(int fim) {
        this.fim = fim;
    }
    public int getTamanho() {
        return tamanho;
    }
    public void setTamanho(int tamanho) {
        this.tamanho = tamanho;
    }
    
    public Object getPai() {
        return (pai);
    }
    public void setPai(Object pai) {
        this.pai = pai;
    }
    
    public String toString() {
        return new ToStringBuilder(this)
            .append("nome", getNome())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof Atributo) ) return false;
        Atributo castOther = (Atributo) other;
        return new EqualsBuilder()
            .append(this.getNome(), castOther.getNome())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getNome())
            .toHashCode();
    }
    
}
