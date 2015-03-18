package com.general.entidades;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fac_punto_emision database table.
 * 
 */
@Entity
@Table(name="fac_punto_emision")
public class FacPuntoEmision implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FacPuntoEmisionPK id;

	@Column(name="\"FormaEmision\"")
	private String formaEmision;

	@Column(name="\"isActive\"")
	private String isActive;

	@Column(name="\"TipoAmbiente\"")
	private String tipoAmbiente;
	
	//*@Column(name="\"caja\""/*, insertable=true, updatable=true*/)
	//private String caja;*/
	
	@Column(name="\"Secuencial\""/*, insertable=true, updatable=true*/)
	private int secuencial;

    public FacPuntoEmision() {
    }
    
	public FacPuntoEmisionPK getId() {
		return this.id;
	}
	public void setId(FacPuntoEmisionPK id) {
		this.id = id;
	}
	
	public String getFormaEmision() {
		return this.formaEmision;
	}
	public void setFormaEmision(String formaEmision) {
		this.formaEmision = formaEmision;
	}

	public String getIsActive() {
		return this.isActive;
	}
	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public String getTipoAmbiente() {
		return this.tipoAmbiente;
	}
	public void setTipoAmbiente(String tipoAmbiente) {
		this.tipoAmbiente = tipoAmbiente;
	}
	
	/*public String getCaja() {
		return this.caja;
	}
	public void setCaja(String caja) {
		this.caja = caja;
	}*/
	
	public int getSecuencial() {
		return this.secuencial;
	}
	public void setSecuencial(int secuencial) {
		this.secuencial = secuencial;
	}

}