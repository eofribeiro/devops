package br.puc.devops.util.natapi;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.List;

/**
 * @author garten.nack, rodrigo.hjort
 */
public class UtilReflexao {

    public static final String REGEXP_METODOS_GET = "(get){1}[A-Z]+.*";
    public static final String REGEXP_METODOS_SET = "(set){1}[A-Z]+.*";
    public static final String REGEXP_TODOS = ".*";

    /**
     * @author  rodrigo.hjort
     * @param nome: nome da classe a ser instanciada (ex: "sere.conv.ConvEstabelecimento")
     * @return  classe correspondente ao texto
     */
    public static Class criarClasse(String nome) {
        Class classe = null;
        if(nome != null && !nome.trim().equals("") ) {
            try {
                classe = Class.forName(nome);
            } catch (ClassNotFoundException e) {
                System.err.println("> ERRO: Classe não encontrada: \"" + nome + "\"");
                e.printStackTrace();
            }
        }
        return(classe);
    }

    /**
     * @author  rodrigo.hjort
     * @param nomeClasse:   nome da classe a ser instanciada (ex: "sere.conv.ConvEstabelecimento")
     * @return  objeto correspondente à classe
     */
    public static Object criarObjeto(String nomeClasse) {
        Class classe = null;
        Object obj = null;
        try {
            classe = Class.forName(nomeClasse);
        } catch (ClassNotFoundException e) {
            System.err.println("> ERRO: Classe não encontrada: \"" + nomeClasse + "\"");
            e.printStackTrace();
        }
        if (classe != null) {
            try {
                obj = (Object) classe.newInstance();
            } catch (InstantiationException e1) {
                System.err.println("> ERRO: Classe não instanciada: \"" + nomeClasse + "\"");
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                System.err.println("> ERRO: Classe não instanciada: \"" + nomeClasse + "\"");
                e1.printStackTrace();
            }
        }
        return(obj);
    }

    /**
     * Lista os métodos get de um objeto passado como parâmetro
     * @param Object - objeto a ser verificado
     * @return List - lista de métodos da classe Method
     */
    public static List listaMetodosGet(Object o) {
        return listaMetodos(o, REGEXP_METODOS_GET);
    }

    /**
     * Lista os métodos set de um objeto passado como parâmetro
     * @param Object - objeto a ser verificado
     * @return List - lista de métodos da classe Method
     */
    public static List listaMetodosSet(Object o) {
        return listaMetodos(o, REGEXP_METODOS_SET);
    }

    /**
     * Lista os todos os métodos declarados de um objeto passado como parâmetro
     * @param Object - objeto a ser verificado
     * @return List - lista de métodos da classe Method
     */
    public static List listaMetodos(Object o) {
        return listaMetodos(o, REGEXP_TODOS);
    }

    /**
     * Devolve uma lista de métodos declarados pelo programador em um objeto
     * @param o - objeto a ser verificado
     * @param pattern - uma expressao regular dos métodos que desejam ser listados
     * @return List - lista de métodos da classe Method
     * <br>
     * Exemplo de utilização<br>
     * List l = new ArrayList();<br>
     * l = Util.listaMetodos(new Object(), ".*");  // todos os metodos da classe Object<br>
     * for (int i = 0; i < l.size(); i++)<br>
     *      System.out.println(((Method)l.get(i)).getName());<br>
     */
    public static List listaMetodos(Object o, String pattern) {
        List<Method> l = new ArrayList<Method>();

        try {
            Method[] m = o.getClass().getDeclaredMethods();

            for (int i = 0; i < m.length; i++)
                if (m[i].getName().matches(pattern))
                    l.add(m[i]);

        } catch (SecurityException s) {
            System.out.println(s);
        }

        return l;
    }

    /**
     * Lista os todos os atributos declarados de um objeto passado como parâmetro
     * @param Object - objeto a ser verificado
     * @return List - lista de atributos da classe Method
     */
    public static List listaAtributos(Object o) {
        return listaAtributos(o, REGEXP_TODOS);
    }

    /**
     * Devolve uma lista de atributos declarados pelo programador em um objeto
     * @param o - objeto a ser verificado
     * @param pattern - uma expressao regular dos atributos que desejam ser listados
     * @return List - lista de atributos da classe Field
     * <br>
     * Exemplo de utilização<br>
     * List l = new ArrayList();<br>
     * l = Util.listaAtributos(new Object(), ".*");  // todos os atributos da classe Object<br>
     * for (int i = 0; i < l.size(); i++)<br>
     *      System.out.println(((Field)l.get(i)).getName());<br>
     */
    public static List listaAtributos(Object o, String pattern) {
        List<Field> l = new ArrayList<Field>();

        try {
            Field[] f = o.getClass().getDeclaredFields();

            for (int i = 0; i < f.length; i++)
                if (f[i].getName().matches(pattern))
                    l.add(f[i]);

        } catch (SecurityException s) {
            System.out.println(s);
        }

        return l;
    }

    /**
     * Invoca um metodo getXxxxx() para o objeto e o atributo informado.
     * @param o - o objeto cujo metodo get será chamado
     * @param atributo - o nome do atributo que identifica qual getter chamar
     * @param objParams - os parametros que devem ser passados para a invocação do método - geralmente null
     * @return
     * @throws Exception
     */
    public static Object invocaGet(Object o, String atributo, Object[] objParams) throws Exception {
        String nomeGetter = "get" + primeiraLetraToUpperCase(atributo);
        try {
            /* supoe que existe apenas um unico metodo com esse nome, portanto passa null no array
             * de classes do getMethod
             */
            return o.getClass().getMethod(nomeGetter, (Class[]) null).invoke(o, objParams);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * InvocaGet Simplificado, não recebe os parâmetros de invocação de um objeto
     * @param o
     * @param atributo
     * @return
     * @throws Exception
     */
    public static Object invocaGet(Object o, String atributo) throws Exception {
        return invocaGet(o, atributo, null);
    }

    /**
     * Invoca o metodo setXxxx para o objeto e atributo informados.
     * @param o - o objeto a invocar o metodo set
     * @param atributo - o atributo cujo metodo set será invocado
     * @param objParams - um array de objetos com os parametros do setter
     * @return
     * @throws Exception
     */
    public static Object invocaSet(Object o, String atributo, Object[] objParams) throws Exception {
        String nomeSetter = "set" + primeiraLetraToUpperCase(atributo);
        try {
            /* supoe que existe apenas um unico metodo com esse nome, portanto passa null no array
             * de classes do getMethod
             */
            return o.getClass().getMethod(nomeSetter, (Class[]) null).invoke(o, objParams);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Invoca o metodo setXxxx para o objeto e atributo informados.
     * @param o - o objeto a invocar o metodo set
     * @param atributo - o atributo cujo metodo set será invocado
     * @param objParams - um array de objetos com os parametros do setter
     * @return
     * @throws Exception
     * @author rodrigo.hjort
     */
    public static Object invocaSet(Object o, String atributo, Class tipo, Object valor) throws Exception {
        String nomeSetter = "set" + primeiraLetraToUpperCase(atributo);
        try {
            /* supoe que existe apenas um unico metodo com esse nome, portanto passa null no array
             * de classes do getMethod
             */
            return o.getClass().getMethod(nomeSetter, new Class[] {tipo}).invoke(o, new Object[] {valor});
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Retorna a String informada como parâmetro modificando sua primeira letra pra letra maiúscula
     * @param string  - a String a ser modificada
     * @return String - a string modificada
     */
    public static String primeiraLetraToUpperCase(String string) {
        return string.substring(0,1).toUpperCase().concat(string.substring(1));
    }

    /**
     * Retorna um número com formatação Monetária
     * @param number
     * @return
     */
    public static String formataMoeda(double number) {
        NumberFormat formatter = new DecimalFormat("###,###,##0.00");
        //NumberFormat fn = NumberFormat.getInstance(new Locale("pt", "BR"));
        return formatter.format(number);
    }

    /**
     * Função que retorna um número formatado com '.' para separação decimal e sem vírgulas
     * @param original
     * @return
     */
    public static String formataNumero(String original) {
        original = original.replaceAll("\\.", "");
        original = original.replaceAll(",", ".");
        return original;
    }

    public static String formataByte(Long bytes) {
        long kb = converteParaKb(bytes);
        if (kb > 1000)
            return converteParaMb(new Long(kb)) + " Mb";
        else
            return kb + " Kb";
    }

    public static long converteParaKb(Long bytes) {
        return bytes.longValue() / new Long(1024).longValue();
    }

    public static long converteParaMb(Long kBytes) {
        return kBytes.longValue() / new Long(1024).longValue();
    }

}
