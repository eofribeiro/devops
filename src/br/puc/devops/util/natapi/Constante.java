package br.puc.devops.util.natapi;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author rodrigo.hjort
 * @since 03/10/2005
 */
public class Constante extends Campo {
    
    public Constante() {
    }

    public Constante(String val) {
        this.setTipoStr("String");
        this.setValor(val != null && !val.equals("") ? val : null);
    }
    
    public void setValor(Object valor) {
        super.setValor(valor);
        String val = (String) valor;
        this.setTamanho(val != null ? val.length() : 0);
    }
    
    public String toString() {
        return new ToStringBuilder(this)
            .append("valor", getValor())
            .toString();
    }
    
}
