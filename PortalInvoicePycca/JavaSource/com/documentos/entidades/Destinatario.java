package com.documentos.entidades;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.documentos.controladores.DetalleGuiaRemision;

@Entity(name="fac_destinatario")
public class Destinatario
{
	@Id									private DestinatarioPK id;
	//@Column(name="\"identificacion\"")	private String identificacionDestinatario;
	@Column(name="\"razonSocial\"")		private String razonSocialDestinatario;
	@Column(name="\"direccion\"")		private String direccionDestinatario;
	@Column(name="\"motivoTraslado\"")	private String motivoTraslado;
	@Column(name="\"documentoAduanero\"")private String docAduanero;
	@Column(name="\"codEstabDestino\"") private int codEstabDestino;
	@Column(name="\"ruta\"")			private String rutaDest;
	@Column(name="\"codDocSustento\"")	private String codDocSustentoDest;
	@Column(name="\"numDocSustento\"")	private String numDocSustentoDest;
	@Column(name="\"numAutorizacionDocSustento\"")	private String numAutDocSustDest;
	@Column(name="\"fechaEmisionDocSustento\"")		private Date fechEmisionDocSustDest;
	
	private String codigoDocumento;
	private List<DetalleGuiaRemision> listDetallesGuiaRemision;

	// INI HFU
	public DestinatarioPK getId() {
		return id;
	}
	public void setId(DestinatarioPK id) {
		this.id = id;
	}
	
	public List<DetalleGuiaRemision> getListDetallesGuiaRemision() {
		return listDetallesGuiaRemision;
	}
	public void setListDetallesGuiaRemision(
			List<DetalleGuiaRemision> listDetallesGuiaRemision) {
		this.listDetallesGuiaRemision = listDetallesGuiaRemision;
	}
	
	/*public String getRuc() {
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
	}*/
	
	public String getCodigoDocumento() {
		return codigoDocumento;
	}
	public void setCodigoDocumento(String codigoDocumento) {
		this.codigoDocumento = codigoDocumento;
	}
	
	/*public List<FacDetDocumento> getListaDetallesDocumentos() {
		return listaDetallesDocumentos;
	}
	public void setListaDetallesDocumentos(
			List<FacDetDocumento> listaDetallesDocumentos) {
		this.listaDetallesDocumentos = listaDetallesDocumentos;
	}*/
	// FIN HFU
	
	
	/*public List<DetalleDocumento> getListaDetalles() {
		return listaDetalles;
	}
	public void setListaDetalles(List<DetalleDocumento> listaDetalles) {
		this.listaDetalles = listaDetalles;
	}
		
	public String getIdentificacionDestinatario() {
		return identificacionDestinatario;
	}
	public void setIdentificacionDestinatario(String identDestinatario) {
		this.identificacionDestinatario = identDestinatario;
	}*/
	
	public String getRazonSocialDestinatario() {
		return razonSocialDestinatario;
	}
	public void setRazonSocialDestinatario(String razonSocialDestinatario) {
		this.razonSocialDestinatario = razonSocialDestinatario;
	}
	public String getDireccionDestinatario() {
		return direccionDestinatario;
	}
	public void setDireccionDestinatario(String dirDestinatario) {
		this.direccionDestinatario = dirDestinatario;
	}
	public String getMotivoTraslado() {
		return motivoTraslado;
	}
	public void setMotivoTraslado(String motTraslDestinatario) {
		this.motivoTraslado = motTraslDestinatario;
	}
	public String getDocAduanero() {
		return docAduanero;
	}
	public void setDocAduanero(String docAduanero) {
		this.docAduanero = docAduanero;
	}
	public int getCodEstabDestino() {
		return codEstabDestino;
	}
	public void setCodEstabDestino(int codEstabDestino) {
		this.codEstabDestino = codEstabDestino;
	}
	public String getRutaDest() {
		return rutaDest;
	}
	public void setRutaDest(String rutaDest) {
		this.rutaDest = rutaDest;
	}
	public String getCodDocSustentoDest() {
		return codDocSustentoDest;
	}
	public void setCodDocSustentoDest(String codDocSustentoDest) {
		this.codDocSustentoDest = codDocSustentoDest;
	}
	public String getNumDocSustentoDest() {
		return numDocSustentoDest;
	}
	public void setNumDocSustentoDest(String numDocSustentoDest) {
		this.numDocSustentoDest = numDocSustentoDest;
	}
	public String getNumAutDocSustDest() {
		return numAutDocSustDest;
	}
	public void setNumAutDocSustDest(String numAutDocSustDest) {
		this.numAutDocSustDest = numAutDocSustDest;
	}
	public Date getFechEmisionDocSustDest() {
		return fechEmisionDocSustDest;
	}
	public void setFechEmisionDocSustDest(Date fechEmisionDocSustDest) {
		this.fechEmisionDocSustDest = fechEmisionDocSustDest;
	}

}
