package br.puc.devops.util.natapi;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import br.puc.devops.adabas.Natural;

/**
 * @author rodrigo.hjort
 */
public class LeituraXml {

	//nome do arquivo de configura��es
    private static final String ARQ_XML = "natapi.xml";

    //vetor de programas configurados
    private static HashMap programas = null;

    /**
     * @param nome
     * @return
     * @throws Exception
     * @author rodrigo.hjort
     * @since 29/09/2006
     */
    public static synchronized final Programa getPrograma(String nome, int versao) throws Exception {


    	//ler todas as configuracoes uma unica vez (singleton)
    	if (programas == null) {
    		programas = new LeituraXml().lerProgramas(versao);
        }

    	//verificar se existem programas
    	if (programas == null)
        	throw new Exception("Nao existem programas nas configuracoes (" + ARQ_XML + ")!");

    	//buscar programa especificado
    	String nomePrograma = nome.toUpperCase();
    	Programa programa = (Programa) programas.get(nomePrograma);
        if (programa == null)
        	throw new Exception("Programa de nome \"" + nomePrograma + "\" nao encontrado nas configuracoes (" + ARQ_XML + ")!");

        //testar par�metros de entrada e sa�da
        if (programa.getEntrada() == null)
        	throw new Exception("Parametros de entrada nao encontrados para o programa \"" + nomePrograma + "\"!");
        if (programa.getSaida() == null)
        	throw new Exception("Parametros de saida nao encontrados para o programa \"" + nomePrograma + "\"!");

		return programa;
    }

    /**
     * @return lista de programas lidos
     * @author rodrigo.hjort
     * @since 29/09/2006
     */
    public HashMap getProgramas() {
		return programas;
	}

	/**
     * este m�todo l� e retorna o conte�do (texto) de uma tag (elemento) filho
     * da tag informada como par�metro. A tag filho a ser pesquisada � a tag
     * informada pelo nome (string)
     * @author carlos.eduardo
     */
    public static String getChildTagValue(Element elem, String tagName) throws Exception {
        NodeList children = elem.getElementsByTagName(tagName);
        if (children == null)
            return null;
        Element child = (Element) children.item(0);
        if (child == null)
            return null;
        return child.getFirstChild().getNodeValue();
    }

    /**
     * @param elem: elemento a ser considerado
     * @param attrName: nome do atributo
     * @return  texto contendo valor do atributo
     * @throws Exception
     * @author rodrigo.hjort
     */
    public static String getTagAttribute(Element elem, String attrName) throws Exception {
        return(elem.getAttribute(attrName));
    }

    /**
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private static ClassLoader getClassLoader() throws IllegalAccessException, InvocationTargetException {
        Method method = null;
        try {
            method = Thread.class.getMethod("getContextClassLoader", (Class []) null);
        } catch (NoSuchMethodException e) {
            return null;
        }
        return(ClassLoader) method.invoke(Thread.currentThread(), (Object []) null);
	}

    /**
     * Uso interno.
     * @param tag
     * @return Entrada
     * @throws Exception
     * @author rodrigo.hjort
     */
    private Entrada lerEntrada(Element tag) throws Exception {
        Entrada entrada = new Entrada();
        entrada = (Entrada) lerArgumento(entrada, tag);
        return(entrada);
    }

    /**
     * Uso interno.
     * @param tag
     * @return Saida
     * @throws Exception
     * @author rodrigo.hjort
     */
    private Saida lerSaida(Element tag) throws Exception {
        Saida saida = new Saida();
        saida = (Saida) lerArgumento(saida, tag);
        return(saida);
    }

    /**
     * Cria um objeto Argumento (Entrada ou Saida) a partir do elemento. Uso interno.
     * @return Argumento
     * @author rodrigo.hjort
     */
    private Argumento lerArgumento(Argumento param, Element tag) throws Exception {

        param.setClasse(UtilReflexao.criarClasse(getTagAttribute(tag, "classe")));
        param.setTratamento(new Tratamento(getTagAttribute(tag, "tratamento")));
        param.setSeparador(getTagAttribute(tag, "separador"));
        param.setExtremos(getTagAttribute(tag, "extremos"));
        param.setDelimitador(getTagAttribute(tag, "delimitador"));

        NodeList nlc = tag.getChildNodes();
        for (int jj = 0; jj < nlc.getLength(); jj++) {
            Node node = nlc.item(jj);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element tagItem = (Element) nlc.item(jj);

                //ler itens do n�, de modo recursivo!
                Atributo atr = lerItem(tagItem, 1);
                param.addAtributo(atr);
            }
        }

        return(param);
    }

    /**
     * @param param
     * @param tag
     * @throws Exception
     * @author rodrigo.hjort
     * @since 06/10/2005
     */
    private Atributo lerItem(Element tag, int nivel) throws Exception {
        Atributo atr = null;
        String nomeTag = tag.getTagName();

        //<campo nome="nomeNaturalidade" tipo="String" tam="30"/>
        //<campo nome="codNacionalidade" tipo="Integer" tam="1"/>
        //<campo nome="dataPrimeiraHab" tipo="Date" tam="8" mascara="ddMMyyyy"/>
        if (nomeTag.equals("campo")) {

            Campo campo = new Campo();
            int tam = Integer.parseInt("0"+getTagAttribute(tag, "tam"));
            int dec = Integer.parseInt("0" + getTagAttribute(tag, "dec"));
            String val = getTagAttribute(tag, "valor");
            campo.setNome(getTagAttribute(tag, "nome"));
            campo.setTipoStr(getTagAttribute(tag, "tipo"));
            campo.setTamanho(tam);
            campo.setDecimal(dec);
            campo.setValor(val != null && !val.equals("") ? val : null);
            campo.setMascara(getTagAttribute(tag, "mascara"));
            atr = campo;

        //<lista nome="ocorrenciasRestricao" classe="gov.pr.detran.dto.OcorrenciaRestricaoDTO" qtd="4" separador=";">
        } else if (nomeTag.equals("lista")) {

            Lista lista = new Lista();
            lista.setNome(getTagAttribute(tag, "nome"));
            lista.setClasse(UtilReflexao.criarClasse(getTagAttribute(tag, "classe")));

            Attr qtd = tag.getAttributeNode("qtd");
            lista.setQtd(qtd != null ? qtd.getValue() : null);
            //lista.setQtd(Integer.parseInt(getTagAttribute(tag, "qtd")));

            Attr criacao = tag.getAttributeNode("criacao");
            if(criacao != null )
            	lista.setCriacao(criacao.getValue());

            Attr sep = tag.getAttributeNode("separador");
            lista.setSeparador(sep != null ? sep.getValue() : null);

            //que tal fazermos uma chamada recursiva?! � n�o � fatorial...
            NodeList nlc2 = tag.getChildNodes();
            for (int kk = 0; kk < nlc2.getLength(); kk++) {
                Node node = nlc2.item(kk);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element tagItem = (Element) nlc2.item(kk);

                    //ler itens do n�, de modo recursivo!
                    Atributo atr2 = lerItem(tagItem, nivel + 1);
                    lista.addAtributo(atr2);
                }
            }
            atr = lista;

        //<objeto nome="sitvn200r1" classe="gov.pr.detran.veiculos.pojo.SITVN200R1" separador=",">
        } else if (nomeTag.equals("objeto")) {

            Objeto objeto = new Objeto();
            objeto.setNome(getTagAttribute(tag, "nome"));
            objeto.setClasse(UtilReflexao.criarClasse(getTagAttribute(tag, "classe")));
            Attr sep = tag.getAttributeNode("separador");
            objeto.setSeparador(sep != null ? sep.getValue() : null);

            //que tal fazermos uma chamada recursiva?! e n�o � fatorial...
            NodeList nlc2 = tag.getChildNodes();
            for (int kk = 0; kk < nlc2.getLength(); kk++) {
                Node node = nlc2.item(kk);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element tagItem = (Element) nlc2.item(kk);

                    //ler itens do n�, de modo recursivo!
                    Atributo atr2 = lerItem(tagItem, nivel + 1);
                    objeto.addAtributo(atr2);
                }
            }
            atr = objeto;

        //<redefinicao metodo="copiar-atributos" classe="gov.pr.detran.veiculos.pojo.SITVN601R5" condicao="$tipoRegistro == 5">
        } else if (nomeTag.equals("redefinicao")) {

            Redefinicao redef = new Redefinicao();
            redef.setClasse(UtilReflexao.criarClasse(getTagAttribute(tag, "classe")));
            redef.setMetodo(new MetodoRedefinicao(getTagAttribute(tag, "metodo")));
            redef.setCondicao(getTagAttribute(tag, "condicao"));

            //opa, outra chamada recursiva! vamos nessa...
            NodeList nlc2 = tag.getChildNodes();
            for (int kk = 0; kk < nlc2.getLength(); kk++) {
                Node node = nlc2.item(kk);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element tagItem = (Element) nlc2.item(kk);

                    //ler itens do n�, de modo recursivo!
                    Atributo atr2 = lerItem(tagItem, nivel + 1);
                    redef.addAtributo(atr2);
                }
            }
            atr = redef;

        //<fixo valor=","/>
        } else if (nomeTag.equals("fixo")) {

            String val = getTagAttribute(tag, "valor");
            atr = criarConstante(tag, val);

        //<espaco tam="3"/>
        } else if (nomeTag.equals("espaco")) {

            int tam = Integer.parseInt(getTagAttribute(tag, "tam"));
            String val = StringUtils.repeat(" ", tam);
            atr = criarConstante(tag, val);

        //<aspas/>
        } else if (nomeTag.equals("aspas")) {

            //no XML: &#034;
            atr = criarConstante(tag, "\"");

        //<virgula/>
        } else if (nomeTag.equals("virgula")) {

            atr = criarConstante(tag, ",");

        }
        return(atr);
    }

    /**
     * @param param
     * @param tagConstante
     * @param valor
     * @author rodrigo.hjort
     * @since 04/10/2005
     */
    private Constante criarConstante(Element tagConstante, String valor) {
        Constante cte = new Constante();
        String val = (valor != null ? valor : null);
        if (val == null) {
            try {
                val = getTagAttribute(tagConstante, "valor");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cte.setValor(val != null && !val.equals("") ? val : null);
        return(cte);
    }

    /**
     * @author rodrigo.hjort
     * @since 29/09/2006
     */
	@SuppressWarnings("unchecked")
	private HashMap lerProgramas(int versao) throws Exception {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = null;

        if(versao == Natural.PADRAO_JBOSS) {
        	// versao 2: recurso no JBoss
        	InputStream input = getClassLoader().getResourceAsStream(ARQ_XML);
        	if (input == null)
        		throw new Exception("Arquivo de configuracoes nao encontrado! " + ARQ_XML);
        	doc = db.parse(input);
        } else {
        	// versao 1: caminho relativo do arquivo
        	doc = db.parse(ARQ_XML);
        }

        programas = new HashMap();

        Element elem = doc.getDocumentElement();
        NodeList nl = elem.getElementsByTagName("programa");

        //<programa nome="DUTHN803">
        for (int ii = 0; ii < nl.getLength(); ii++) {
            Element tagPrograma = (Element) nl.item(ii);
            String nome = getTagAttribute(tagPrograma, "nome");

            //<entrada tratamento="tamanho-fixo">
            NodeList nle = tagPrograma.getElementsByTagName("entrada");
            Entrada entrada = null;
			if (nle != null && nle.getLength() > 0) {
                Element tagEntrada = (Element) nle.item(0);
                entrada = lerEntrada(tagEntrada);
            }

            //<saida tratamento="tamanho-fixo" classe="gov.pr.detran.pojo.DutHN803">
            NodeList nls = tagPrograma.getElementsByTagName("saida");
            Saida saida = null;
			if (nls != null && nls.getLength() > 0) {
                Element tagSaida = (Element) nls.item(0);
                saida = lerSaida(tagSaida);
            }

            //incluir programa na hash
			String nomePrograma = nome.toUpperCase();
            Programa prg = new Programa(nomePrograma);
            prg.setEntrada(entrada);
            prg.setSaida(saida);
            programas.put(nomePrograma, prg);
        }

        return programas;
    }

}
