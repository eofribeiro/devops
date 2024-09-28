package br.puc.devops.util.natapi;

/**
 * @author rodrigo.hjort
 * @since 27/10/2005
 */
public class BaseNaturalException extends Exception {

    private static final long serialVersionUID = 6417283959175878289L;
    
    private String programa;
    private String mensagem;
    private Integer codigo;
    
    public BaseNaturalException(String prog, Integer cod, String msg) {
        this.programa = prog;
        this.codigo = cod;
        this.mensagem = msg;
    }
    
    public String getMensagem() {
        return (mensagem);
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getPrograma() {
        return (programa);
    }

    public void setPrograma(String programa) {
        this.programa = programa;
    }
    
    public Integer getCodigo() {
        return (codigo);
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String toString() {
        return(this.codigo + ": " + this.mensagem);
    }
    
}
