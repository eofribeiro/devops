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
     * Faz a validacao da expressão condicional da redefinição.
     * Ex: "$tipoRegistro == 1", "$campo1 < $campo3", "5 >= $campo2"
     * Sim, o código não está nada tragável... Quando tiver tempo, a gente arruma!
     * @return boolean
     * @throws Exception
     */
    public boolean isCondicaoValida() throws Exception {
        boolean valida = false;
        
        //TODO hjort: fazer um parser (tokenizing) decente para a condicional!
        
        //por enquanto, desse jeito tosco e limitado, faremos a interpretação dos termos!
        String termos[] = condicao.split(" ");
        if (termos.length != 3)
            throw new Exception("Número inválido de termos no argumento condicional! Condição: \"" + condicao + "\"");
        
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
            throw new Exception("Expressões da condição não puderam ser avaliadas! Esquerda: " + esq + ", Operador: " + ope + ", Direita: " + dir);
        }
        
        //que tal agora fazermos a comparação, hein?!
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
            throw new Exception("Operador \"" + ope + "\" não implementado!");
        
        return(valida);
    }
    
    private void lancarExcecaoTipo(Object valor) throws Exception {
        String tipo = valor.getClass().getSimpleName();
        throw new Exception("Tipo de dados \"" + tipo + "\" não implementado!");
    }
    
}
