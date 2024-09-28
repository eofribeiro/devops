package br.puc.devops.util.natapi;

/**
 * @author rodrigo.hjort
 * @since 13/10/2005
 */
public class MetodoRedefinicao {
    
    public static final byte NORMAL = 1;
    public static final byte COPIAR_ATRIBUTOS = 2;
    
    private byte codigo;
    
    public MetodoRedefinicao(byte codigo) {
        this.setCodigo(codigo);
    }
    
    public MetodoRedefinicao(String nome) {
        if ("normal".equalsIgnoreCase(nome))
            this.setCodigo(NORMAL);
        else if ("copiar-atributos".equalsIgnoreCase(nome))
            this.setCodigo(COPIAR_ATRIBUTOS);
    }
    
    public byte getCodigo() {
        return (codigo);
    }
    public void setCodigo(byte codigo) {
        this.codigo = codigo;
    }
    
    public static MetodoRedefinicao Normal() {
        return(new MetodoRedefinicao(NORMAL));
    }

    public static MetodoRedefinicao CopiarAtributos() {
        return(new MetodoRedefinicao(COPIAR_ATRIBUTOS));
    }

    public boolean isNormal() {
        return(getCodigo() == NORMAL);
    }

    public boolean isCopiarAtributos() {
        return(getCodigo() == COPIAR_ATRIBUTOS);
    }
    
}
