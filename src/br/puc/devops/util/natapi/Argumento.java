package br.puc.devops.util.natapi;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author rodrigo.hjort
 *
 */
public class Argumento implements Conteiner {

    private Class classe;
    private Tratamento tratamento;
    private String delimitador = null;
    private String separador = null;
    private String extremos = null;

    private List<Atributo> atributos;
    private int offset = 0;

    public Argumento() {
    }

    public Class getClasse() {
        return (classe);
    }
    public void setClasse(Class classe) {
        this.classe = classe;
    }

    public Tratamento getTratamento() {
        return (tratamento);
    }
    public void setTratamento(Tratamento tratamento) {
        this.tratamento = tratamento;
    }

    public String getSeparador() {
        return (separador);
    }
    public void setSeparador(String separador) {
        this.separador = (separador != null && !separador.equals("") ? separador : null);
    }

    public String getExtremos() {
        return (extremos);
    }
    public void setExtremos(String extremos) {
        this.extremos = (extremos != null && !extremos.equals("") ? extremos : null);
    }

    public int getOffset() {
        return(offset);
    }
    public void setOffset(int offset) {
        this.offset = offset;
    }

    public List<Atributo> getAtributos() {
        return (atributos);
    }
    public void setAtributos(List<Atributo> atributos) {
        this.atributos = atributos;
    }
    public void addAtributo(Atributo atr) {
        if (atributos == null)
            atributos = new ArrayList<Atributo>();

        atr.setInicio(this.offset + 1);
        atr.setFim(atr.getTamanho() + this.offset);

        this.offset += atr.getTamanho();

        //quem é o seu pai?
        atr.setPai(this);
        atributos.add(atr);
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("classe", getClasse().getName())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof Argumento) ) return false;
        Argumento castOther = (Argumento) other;
        return new EqualsBuilder()
            .append(this.getClasse(), castOther.getClasse())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getClasse())
            .toHashCode();
    }

    public String getDelimitador() {
        return delimitador;
    }

    public void setDelimitador(String delimitador) {
        this.delimitador = delimitador;
    }

}
