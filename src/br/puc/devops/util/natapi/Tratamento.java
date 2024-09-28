package br.puc.devops.util.natapi;

/**
 * @author rodrigo.hjort
 */
public class Tratamento {

    public static final byte TAMANHO_FIXO = 1;
    public static final byte DELIMITADOR = 2;
    
    private byte codigo;
    
    public Tratamento(byte codigo) {
        this.setCodigo(codigo);
    }
    
    public Tratamento(String nome) {
        if ("delimitador".equalsIgnoreCase(nome))
            this.setCodigo(DELIMITADOR);
        else if ("tamanho-fixo".equalsIgnoreCase(nome))
            this.setCodigo(TAMANHO_FIXO);
    }
    
    public byte getCodigo() {
        return (codigo);
    }
    public void setCodigo(byte codigo) {
        this.codigo = codigo;
    }
    
    public static Tratamento TamanhoFixo() {
        return(new Tratamento(TAMANHO_FIXO));
    }

    public static Tratamento Delimitador() {
        return(new Tratamento(DELIMITADOR));
    }

    public boolean isTamanhoFixo() {
        return(getCodigo() == TAMANHO_FIXO);
    }

    public boolean isDelimitador() {
        return(getCodigo() == DELIMITADOR);
    }
    
}
