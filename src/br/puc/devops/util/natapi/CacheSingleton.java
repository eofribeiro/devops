package br.puc.devops.util.natapi;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

public class CacheSingleton implements Serializable  {

	private static final long serialVersionUID = 9141476511071107949L;
	
	private static CacheSingleton instance = null;
	private static List<ChamadaCached> cache = null;
	
		
	private static Integer tempoReload = 5; // Em minutos
	private static Timer cacheColector = null;
	
	private static Logger log = Logger.getLogger(BaseNaturalDAO.class);
	
	private CacheSingleton() {
		
	}
	
	public synchronized static CacheSingleton getInstance() throws Exception {
		if (instance == null) {
			instance = new CacheSingleton();
			cache = new ArrayList<ChamadaCached>();
			cacheColector = new Timer();
			cacheColector.scheduleAtFixedRate( 
				new TimerTask() {
					public void run(){
						try {
							CacheSingleton.getInstance().cacheColector();
						} catch (Exception e) {							
							e.printStackTrace();
						}
					}					
				}
			, 1000, tempoReload);
		}
		return instance; 
	}
	
	public synchronized void limparCacheUsuario(String usuario, String programa) throws Exception {
		List<ChamadaCached> chamadasARemover = new ArrayList<ChamadaCached>();
		for (ChamadaCached chamada : cache) {
			if(programa != null && chamada.getPrograma().equals(programa)) {
				if(chamada.getUsuario() != null && chamada.getUsuario().equals(usuario)) {
					chamadasARemover.add(chamada);
				}
			} else {
				if(chamada.getUsuario() != null && chamada.getUsuario().equals(usuario)) {
					chamadasARemover.add(chamada);
				}				
			}
		}
		cache.removeAll(chamadasARemover);
	}
	
	public synchronized void limparTodoCacheUsuario(String usuario) throws Exception {
		List<ChamadaCached> chamadasARemover = new ArrayList<ChamadaCached>();
		for (ChamadaCached chamada : cache) {			
			if(chamada.getUsuario() != null && chamada.getUsuario().equals(usuario)) {
				chamadasARemover.add(chamada);
			}				
		}
		cache.removeAll(chamadasARemover);
	}
	
	public synchronized void cacheColector() throws Exception {
		List<ChamadaCached> chamadasARemover = new ArrayList<ChamadaCached>();
		for (ChamadaCached chamada : cache) {
			Calendar calChamada = Calendar.getInstance();
			calChamada.setTime(chamada.getTimestamp());
			long tempoChamada = calChamada.getTimeInMillis() + ( 5 * 60000); // chamada valida até
			long tempoAtual = Calendar.getInstance().getTimeInMillis() ;
			// Verificar as chamadas que vão ser retiradas. Data da chamada mais tempo de cache menor que data atual
			if(tempoChamada < tempoAtual){
				log.info("Hibernatural Cache: Removida do Cache chamada a " + chamada.getPrograma() + " feita em " + chamada.getTimestamp() + " por " + chamada.getUsuario());
				chamadasARemover.add(chamada);
			} 
		}
		cache.removeAll(chamadasARemover);
		
	}
	
	public synchronized void adicionarCache(String programa, String entrada, String saida, Integer rc, String mensagem, String usuario) throws Exception {
		ChamadaCached chamada = new ChamadaCached();
		chamada.setPrograma(programa);
		chamada.setEntrada(entrada);
		chamada.setTimestamp(new Date());
		chamada.setSaida(saida);
		chamada.setRc(rc);
		chamada.setMensagem(mensagem);
		chamada.setUsuario(usuario);
		log.info("Hibernatural Cache: Chamada ao Programa " + programa + " adicionada em Cache em " + chamada.getTimestamp() + " por " + chamada.getUsuario());
		cache.add(chamada);		
	}
	
	public synchronized ChamadaCached recuperarCache(String programa, String entrada, Integer segundos, String usuario) throws Exception {
		log.info("Hibernatural Cache: Procurando entrada para programa " + programa + " com validade de " + segundos + " segundos");
		for (ChamadaCached chamada : cache) {
			if(chamada.getPrograma().equals(programa)) {
				if(chamada.getEntrada().equals(entrada)) {
					if(segundos != null && segundos > 0) {
						// Comparar as datas
						Calendar cal = Calendar.getInstance();
						cal.setTime(chamada.getTimestamp());
						// Tempo de Chamada mais segundos considerados
						cal.add(Calendar.SECOND, segundos);
						Calendar calAtual = Calendar.getInstance();
						calAtual.setTime(new Date());				
						if(calAtual.before(cal)) {
							if(usuario != null && chamada.getUsuario().equals(usuario)) {
								log.info("Hibernatural Cache: Chamada ao Programa " + programa + " recuperada do Cache.");
								return chamada;
							}
						}											
					}
					if(usuario != null && chamada.getUsuario().equals(usuario)) {
						log.info("Hibernatural Cache: Chamada ao Programa " + programa + " feita por " + usuario + " recuperada do Cache.");
						return chamada;
					}					
				}
			}
		}
		// retorna null pois não vai usar Cache
		log.info("Hibernatural Cache: Chamada em Cache não encontrada. Será adicionada nova chamada");
		return null;
	}
	

}
