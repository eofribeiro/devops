package br.puc.devops.util.natapi;

import java.util.List;

/**
 * @author rodrigo.hjort
 * @since 05/10/2005
 */
public interface Conteiner {

    public Class getClasse();
    public void setClasse(Class classe);
    
    public List getAtributos();
    public void setAtributos(List<Atributo> atributos);
    public void addAtributo(Atributo atr);
    
    public String getSeparador();
    public void setSeparador(String separador);
    
}
