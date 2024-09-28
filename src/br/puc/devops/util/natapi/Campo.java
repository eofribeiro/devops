package br.puc.devops.util.natapi;

/**
 * @author rodrigo.hjort
 */
public class Campo extends Atributo {

    private Class tipo;
    private int decimal;
    private Object valor;
    private String mascara;
    
    public Campo() {
    }

    public Class getTipo() {
        return (tipo);
    }
    public void setTipo(Class tipo) {
        this.tipo = tipo;
    }
    
    public void setTipoStr(String nome) {
    	nome = nome.trim();
    	
        String pack = "lang";
        if (nome.equals("Date"))
            pack = "util";
        else if (nome.equals("BigDecimal"))
            pack = "math";
        
        String nomeCompleto = "java.".concat(pack).concat(".").concat(nome);
        
        tipo = null;
        try {
            tipo = Class.forName(nomeCompleto);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
        
    public Object getValor() {
        return (valor);
    }
    public void setValor(Object valor) {
        this.valor = valor;
    }
    
    public int getDecimal() {
        return (decimal);
    }
    public void setDecimal(int decimal) {
        this.decimal = decimal;
    }

    public String getMascara() {
        return (mascara);
    }
    public void setMascara(String mascara) {
        this.mascara = mascara;
    }
    
}
