package com.general.entidades;
import java.sql.Date;
import javax.persistence.*;

/**
 * The persistent class for the fac_general database table.
 * 
 */
@Entity
@Table(name="")
public class FacResumenValores {
	@Id
	@Column(name="\"sub12\"")
	private String sub12;

	@Column(name="\"sub0\"")
	private String sub0;

	@Column(name="\"noiva\"")
	private String noiva;

	@Column(name="\"ice\"")
	private String ice;

	@Column(name="\"iva\"")
	private String iva;
	
	@Column(name="\"total\"")
	private Double total;
	
	
	
	
	/*
	@Column(name="\"FECHA\"")
	private Date FECHA;

	@Column(name="\"codEstablecimiento\"")
	private String codEstablecimiento;
	
	@Column(name="\"codPuntoEmision\"")
	private String codPuntoEmision;
	
	@Column(name="\"codigoDocumento\"")
	private String codigoDocumento;
	
	@Column(name="\"secuencial\"")
	private String secuencial;
	
	@Column(name="ambiente")
	private String ambiente;
	*/
	
	
	public FacResumenValores(String sub12,String sub0,String noiva,String ice, String iva,Double total,
	    		String codEstablecimiento, String codPuntoEmision,
	    		String codigoDocumento, String secuencial, String xmlAutorizacion)
	{
	    	this.sub12= sub12;
	    	this.sub0=sub0;
	    	this.noiva= noiva;
	    	this.ice= ice;
	    	this.iva= iva;
	    	this.total= total;
	    	/*this.codEstablecimiento= codEstablecimiento;
	    	this.codPuntoEmision=codPuntoEmision;
	    	this.codigoDocumento= codigoDocumento;
	    	this.secuencial= secuencial;*/
	    }
	    
	    public 	FacResumenValores(){
	    	
	    }



		public String getSub12() {
			return sub12;
		}
		public void setSub12(String sub12) {
			this.sub12 = sub12;
		}

		public String getSub0() {
			return sub0;
		}
		public void setSub0(String sub0) {
			this.sub0 = sub0;
		}

		public String getNoiva() {
			return noiva;
		}
		public void setNoiva(String noiva) {
			this.noiva = noiva;
		}

		public String getIce() {
			return ice;
		}
		public void setIce(String ice) {
			this.ice = ice;
		}

		public String getIva() {
			return iva;
		}
		public void setIva(String iva) {
			this.iva = iva;
		}

		public Double getTotal() {
			return total;
		}
		public void setTotal(Double total) {
			this.total = total;
		}
	    
	    
	    
	    









		




		/*
		public Date getFECHA() {
			return FECHA;
		}
		public void setFECHA(Date fECHA) {
			FECHA = fECHA;
		}
		 
		public String getCodEstablecimiento() {
			return codEstablecimiento;
		}
		public void setCodEstablecimiento(String codEstablecimiento) {
			this.codEstablecimiento = codEstablecimiento;
		}

		public String getCodPuntoEmision() {
			return codPuntoEmision;
		}
		public void setCodPuntoEmision(String codPuntoEmision) {
			this.codPuntoEmision = codPuntoEmision;
		}

		public String getCodigoDocumento() {
			return codigoDocumento;
		}
		public void setCodigoDocumento(String codigoDocumento) {
			this.codigoDocumento = codigoDocumento;
		}

		public String getSecuencial() {
			return secuencial;
		}
		public void setSecuencial(String secuencial) {
			this.secuencial = secuencial;
		}

		public String getAmbiente() {
			return ambiente;
		}
		public void setAmbiente(String ambiente) {
			this.ambiente = ambiente;
		}
		*/
}
