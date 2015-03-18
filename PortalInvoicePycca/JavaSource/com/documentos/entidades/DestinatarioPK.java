package com.documentos.entidades;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class DestinatarioPK
{
	@Column(name="\"Ruc\"") private String ruc;
	@Column(name="\"CodEstablecimiento\"") private String codEstablecimiento;
	@Column(name="\"CodPuntEmision\"")	   private String codPuntEmision;
	@Column(name="secuencial")			   private String secuencial;
	@Column(name="\"identificacionDestinatario\"") private String identificacionDestinatario;
	@Column(name="\"secuencialDetalle\"")  private int secuencialDetalle;

	public String getRuc() {
		return ruc;
	}
	public void setRuc(String ruc) {
		this.ruc = ruc;
	}
	public String getCodEstablecimiento() {
		return codEstablecimiento;
	}
	public void setCodEstablecimiento(String codEstablecimiento) {
		this.codEstablecimiento = codEstablecimiento;
	}
	public String getCodPuntEmision() {
		return codPuntEmision;
	}
	public void setCodPuntEmision(String codPuntEmision) {
		this.codPuntEmision = codPuntEmision;
	}
	public String getSecuencial() {
		return secuencial;
	}
	public void setSecuencial(String secuencial) {
		this.secuencial = secuencial;
	}
	public String getIdentificacionDestinatario() {
		return identificacionDestinatario;
	}
	public void setIdentificacionDestinatario(String identificacionDestinatario) {
		this.identificacionDestinatario = identificacionDestinatario;
	}
	public int getSecuencialDetalle() {
		return secuencialDetalle;
	}
	public void setSecuencialDetalle(int secuencialDetalle) {
		this.secuencialDetalle = secuencialDetalle;
	}
	
	
	
	

}
