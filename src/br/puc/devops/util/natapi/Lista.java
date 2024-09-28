package br.puc.devops.util.natapi;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rodrigo.hjort
 */
public class Lista extends Atributo implements Conteiner {

    private Class classe;
    private int qtd = 0;
    private int tamanhoLinha = 0;
    private List<Atributo> atributos;
    private String separador;
    private String qtdExpr;
    private String criacao;
    
    public Lista() {
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
    
    public int getQtd() {
        return (qtd);
    }
    public void setQtd(int qtd) {
        this.qtd = qtd;
        setTamanho(getTamanhoLinha() * qtd);
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
        
        //acertar o início e fim, relativo à lista
        atr.setInicio(getTamanhoLinha() + 1);
        atr.setFim(atr.getTamanho() + getTamanhoLinha());
        
        //acertar o tamanho de uma linha - acertará também o tamanho total da lista!
        setTamanhoLinha(getTamanhoLinha() + atr.getTamanho());
        
        //quem é o seu pai?
        atr.setPai(this);
        atributos.add(atr);
    }

    public int getTamanhoLinha() {
        return (tamanhoLinha);
    }
    public void setTamanhoLinha(int tamanhoLinha) {
        this.tamanhoLinha = tamanhoLinha;
        setTamanho(tamanhoLinha * qtd);
    }

    /**
     * @param string
     * @author rodrigo.hjort
     * @since 13/10/2005
     */
    public void setQtd(String string) {
        String expr = string.trim();
        
        //se for só número mesmo...
        if (expr.matches("[0-9]+"))
            setQtd(Integer.parseInt(expr));
        
        //se for expressão, o buraco é mais embaixo...
        else {
            setQtd(0);
            setQtdExpr(expr);
        }
    }
    
    public String getQtdExpr() {
        return (qtdExpr);
    }
    
    public void setQtdExpr(String qtdExpr) {
        this.qtdExpr = qtdExpr;
    }

	public String getCriacao() {
		return criacao;
	}

	public void setCriacao(String criacao) {
		this.criacao = criacao;
	}
    
}
