package br.puc.devops.util.natapi;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rodrigo.hjort
 * @since 05/10/2005
 */
public class Objeto extends Atributo implements Conteiner {
    
    private Class classe;
    private List<Atributo> atributos;
    private String separador;
    
    public Objeto() {
    }
    
    public Class getClasse() {
        return (classe);
    }
    public void setClasse(Class classe) {
        this.classe = classe;
    }
    
    public String getSeparador() {
        return (separador);
    }
    public void setSeparador(String separador) {
        this.separador = separador;
    }
    
    public List getAtributos() {
        return (atributos);
    }
    public void setAtributos(List<Atributo> atributos) {
        this.atributos = atributos;
    }
    public void addAtributo(Atributo atr) {
        if (atributos == null)
            atributos = new ArrayList<Atributo>();
        
        //acertar o início e fim, relativo ao objeto
        atr.setInicio(getTamanho() + 1);
        atr.setFim(atr.getTamanho() + getTamanho());
        
        //acertar o tamanho do objeto
        setTamanho(getTamanho() + atr.getTamanho());
        
        //quem é o seu pai?
        atr.setPai(this);
        atributos.add(atr);
    }
    
}
