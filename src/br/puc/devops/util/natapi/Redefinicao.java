package br.puc.devops.util.natapi;

/**
 * @author rodrigo.hjort
 * @since 13/10/2005
 */
public class Redefinicao extends Objeto {
    
    private MetodoRedefinicao metodo;
    private String condicao;
    private Object objPai;
    
    public String getCondicao() {
        return (condicao);
    }
    public void setCondicao(String condicao) {
        this.condicao = condicao;
    }
    
    public MetodoRedefinicao getMetodo() {
        return (metodo);
    }
    public void setMetodo(MetodoRedefinicao metodo) {
        this.metodo = metodo;
    }
    
    public Object getObjPai() {
        return (objPai);
    }
    public void setObjPai(Object objPai) {
        this.objPai = objPai;
    }
    
    /**
     * Faz a validacao da express�o condicional da redefini��o.
     * Ex: "$tipoRegistro == 1", "$campo1 < $campo3", "5 >= $campo2"
     * Sim, o c�digo n�o est� nada trag�vel... Quando tiver tempo, a gente arruma!
     * @return boolean
     * @throws Exception
     */
    public boolean isCondicaoValida() throws Exception {
        boolean valida = false;
        
        //TODO hjort: fazer um parser (tokenizing) decente para a condicional!
        
        //por enquanto, desse jeito tosco e limitado, faremos a interpreta��o dos termos!
        String termos[] = condicao.split(" ");
        if (termos.length != 3)
            throw new Exception("N�mero inv�lido de termos no argumento condicional! Condi��o: \"" + condicao + "\"");
        
        String esq = termos[0], ope = termos[1], dir = termos[2];
        Object valEsq = null, valDir = null;
        
        //pegar valor da esquerda
        if (esq.startsWith("$")) {
            try {
                valEsq = UtilReflexao.invocaGet(objPai, esq.substring(1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        //pegar valor da direita
        if (dir.startsWith("$")) {
            try {
                valDir = UtilReflexao.invocaGet(objPai, dir.substring(1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        //preencher os valores constantes
        if (valEsq != null) {
            if (valEsq instanceof Byte)
                valDir = new Byte(dir);
            else if (valEsq instanceof Integer)
                valDir = new Integer(dir);
            else if (valEsq instanceof String)
                valDir = new String(dir);
            else
                lancarExcecaoTipo(valDir);
        } else if (valDir != null) {
            if (valDir instanceof Byte)
                valEsq = new Byte(dir);
            if (valDir instanceof Integer)
                valEsq = new Integer(dir);
            else if (valDir instanceof String)
                valEsq = new String(dir);
            else
                lancarExcecaoTipo(valEsq);
        } else {
            throw new Exception("Express�es da condi��o n�o puderam ser avaliadas! Esquerda: " + esq + ", Operador: " + ope + ", Direita: " + dir);
        }
        
        //que tal agora fazermos a compara��o, hein?!
        int comp = 0;
        if (valDir instanceof Byte)
            comp = ((Byte) valEsq).compareTo((Byte) valDir);
        else if (valDir instanceof Integer)
            comp = ((Integer) valEsq).compareTo((Integer) valDir);
        else if (valDir instanceof String)
            comp = ((String) valEsq).compareTo((String) valDir);
        
        //operador igual...
        if (ope.equals("=") || ope.equals("=="))
            valida = (comp == 0);
        //operador diferente...
        else if (ope.equals("!=") || ope.equals("<>"))
            valida = (comp != 0);
        //operador maior que...
        else if (ope.equals(">"))
            valida = (comp > 0);
        //operador maior ou igual a...
        else if (ope.equals(">="))
            valida = (comp >= 0);
        //operador menor que...
        else if (ope.equals("<"))
            valida = (comp < 0);
        //operador menor ou igual a...
        else if (ope.equals("<="))
            valida = (comp <= 0);
        else
            throw new Exception("Operador \"" + ope + "\" n�o implementado!");
        
        return(valida);
    }
    
    private void lancarExcecaoTipo(Object valor) throws Exception {
        String tipo = valor.getClass().getSimpleName();
        throw new Exception("Tipo de dados \"" + tipo + "\" n�o implementado!");
    }
    
}
