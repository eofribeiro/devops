package br.puc.devops.hibernatural;

/**
 * Verifica situação ADABAS para ativar a contingência para portal nacional GNRE
 */
public class FIH9NERR implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6904400034119515219L;
	
	private String dbid;
	private Integer rcode;
	private String msg;

	public String getDbid() {
		return dbid;
	}

	/**
	 * 009 - ADABAS/PROD 007 - ADABAS/TEST 005 - PARA SIMULAR ERRO RC=148
	 *
	 * @param dbid
	 */
	public void setDbid(String dbid) {
		this.dbid = dbid;
	}

	@Override
	public String toString() {
		return "FIH9NERR [dbid=" + dbid + ", rcode=" + rcode + ", msg=" + msg + "]";
	}

	public Integer getRcode() {
		return rcode;
	}

	public void setRcode(Integer rcode) {
		this.rcode = rcode;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
