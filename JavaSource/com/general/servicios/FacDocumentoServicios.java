package com.general.servicios;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.config.ConfigPersistenceUnit;
import com.documentos.entidades.FacCabDocumento;
import com.documentos.entidades.FacDetAdicional;
import com.documentos.entidades.FacDetDocumento;
import com.documentos.entidades.FacDetRetencione;
import com.documentos.entidades.FacEmpresa;
import com.general.entidades.*;

@Stateless
public class FacDocumentoServicios {
	@PersistenceContext (unitName=ConfigPersistenceUnit.persistenceUnit)  
	private EntityManager em;
	
	public EntityManager getEm() {
		return em;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}

	
	@SuppressWarnings("unchecked")
	public  List<FacTiposDocumento> BuscarDatosdeTipoDocumento() throws Exception{
		
		try{
			Query q = em.createQuery("SELECT E FROM FacTiposDocumento E where E.isActive = :Activado");
			q.setParameter("Activado", "Y");
			return q.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error al buscar registro");
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<FacCabDocumento> MostrandoDetalleDocumento(Object [] obj) throws Exception
	{
		try{
			String dialect = obj[6].toString();			
			//String datos[] = dialect.split(dialect);			
			String database = dialect;
			String ls_filtro = "";
			
			ls_filtro = " and E.fechaEmision Between :fechaInicio and :fechaFinal ";
			if(obj[7] != null)
				if(obj[7].toString().length()>0)
					//ls_filtro = ls_filtro + " and E.id.codEstablecimiento + E.id.codPuntEmision + E.id.secuencial = :numDocumento ";
					ls_filtro = ls_filtro + " and (concat(E.id.codEstablecimiento, E.id.codPuntEmision, E.id.secuencial) = :numDocumento "
							+ " or concat(E.id.codEstablecimiento, '-', E.id.codPuntEmision, '-', E.id.secuencial) = :numDocumento) ";
			
			String ls_query = "SELECT E FROM FacCabDocumento E "+
					" where E.id.ruc = :ruc_empresa and	(E.identificacionComprador = :rucComprador or :rucComprador = :DatoVacio) " +					
					" and E.razonSocialComprador like :razonSocialComp  " +
					" and E.id.codigoDocumento = :codDocumentoSelecionado " +
					ls_filtro +
					//" and E.estadoTransaccion = 'AT' " +
					//" and E.id.codEstablecimiento + E.id.codPuntEmision + E.id.secuencial = :numDocumento" +
					" order by E.id.ruc desc, E.fechaEmision desc ";
			Query query = em.createQuery(ls_query);
			//query.setParameter("estado", "AT");
						
			if(obj[7] != null)
				query.setParameter("numDocumento", obj[7].toString());
			
			if (obj[5].toString()==null){
				query.setParameter("ruc_empresa", "");// parametro que es el ruc de la empresa logoneada
			}else{
				query.setParameter("ruc_empresa", obj[5].toString());// parametro que es el ruc de la empresa logoneada
			}
			if (obj[0].toString()==null){
				query.setParameter("rucComprador", "");// parametro de identificador del comprador
			}else{
				query.setParameter("rucComprador", obj[0].toString());// parametro de identificador del comprador
			}			
			query.setParameter("DatoVacio", "");
			
			if (obj[1].toString()==null){
				query.setParameter("razonSocialComp","%"); // parametro de la razon social
			}else{
				query.setParameter("razonSocialComp","%"+obj[1].toString()+"%"); // parametro de la razon social
			}
			
			query.setParameter("codDocumentoSelecionado",obj[2].toString()); // parametro del documento seleccionado
			if (database.equals("PostgreSQL")){
				if(obj[3]== null){
					obj[3] = "2000-01-31";
				}
				if(obj[4]== null){
					obj[4]= new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
				}
				query.setParameter("fechaInicio",obj[3].toString()); // parametro de la fecha de inicio
				if (obj[4].toString()==null){
					query.setParameter("fechaFinal","now()"); // parametro de la fecha de final
				}else{
					query.setParameter("fechaFinal",obj[4].toString()); // parametro de la fecha de final
				}
			}
			if (database.equals("SQLServer")){
				if(obj[3]== null){
					obj[3] = "2000-01-31";								 
				}
				if(obj[4]== null){
					obj[4]= new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());	
				}
				query.setParameter("fechaInicio",obj[3].toString()); // parametro de la fecha de inicio
				if (obj[4].toString()==null){
					query.setParameter("fechaFinal","now()"); // parametro de la fecha de final
				}else{
					query.setParameter("fechaFinal",obj[4].toString()); // parametro de la fecha de final
				}	
			}
			query.setParameter("fechaInicio", Date.valueOf(obj[3].toString()));
			query.setParameter("fechaFinal", Date.valueOf(obj[4].toString()));
			return query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error al buscar registro");
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<FacEmpresa> listadoEmpresas(String Ruc_empresa) throws Exception{
		try{
			String stringQuery = "select E from FacEmpresa E where E.ruc = :ruc_empresa and E.isActive = :Estado";
			Query query = em.createQuery(stringQuery);
			query.setParameter("ruc_empresa", Ruc_empresa);
			query.setParameter("Estado", "Y");
			return query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error al buscar registro");
		}
	}
	
	public FacEmpresa listadoEmpr(String Ruc_empresa) throws Exception{
		try{
			String stringQuery = "select E from FacEmpresa E where E.ruc = :ruc_empresa and E.isActive = :Estado";
			Query query = em.createQuery(stringQuery);
			query.setParameter("ruc_empresa", Ruc_empresa);
			query.setParameter("Estado", "Y");
			FacEmpresa e = (FacEmpresa) query.getSingleResult(); 
			return e;
		}catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error al buscar registro");
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<FacEmpresa> listadoTodasEmpresas() throws Exception{
		try{
			String stringQuery = "select E from FacEmpresa E where E.isActive = :Estado";
			Query query = em.createQuery(stringQuery);
			query.setParameter("Estado", "Y");
			return query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error al buscar registro");
		}
	}
	
	public List<facDetalleDocumentoEntidad> MotrandoDocumentoFiltrados(List<FacCabDocumento> documento_detalles,
																	   List<FacTiposDocumento> tipoDocumentos,List<FacEmpresa> detalleEmpresas) throws Exception{
		try{
			List<facDetalleDocumentoEntidad> documentoEntidad = new ArrayList<facDetalleDocumentoEntidad>();
			facDetalleDocumentoEntidad detalleEntidad = new facDetalleDocumentoEntidad();
			
			if(documento_detalles.isEmpty()){
				return documentoEntidad;
			}
			
			for (FacCabDocumento detalle : documento_detalles) {
					for (FacTiposDocumento documento : tipoDocumentos) {
						if (detalle.getId().getCodigoDocumento().equalsIgnoreCase(documento.getIdDocumento())) {// validando si el codigo del documento existe
							for (FacEmpresa empresa : detalleEmpresas) {
										if (detalle.getId().getRuc().equalsIgnoreCase(empresa.getRuc())) {// validando si el codigo del documento existe
											String formato = Integer.parseInt(detalle.getId().getCodigoDocumento()) + detalle.getId().getRuc()+detalle.getId().getCodigoDocumento()+ detalle.getId().getCodEstablecimiento() + detalle.getId().getCodPuntEmision() + detalle.getId().getSecuencial();
											String Estado = detalle.getEstadoTransaccion().trim();
											detalleEntidad.setRFCREC(detalle.getIdentificacionComprador());
											detalleEntidad.setNOMREC(detalle.getRazonSocialComprador());
											detalleEntidad.setCodDoc(documento.getIdDocumento());
											detalleEntidad.setTIPODOC(documento.getDescripcion());
											
											detalleEntidad.setFOLFAC(detalle.getId().getCodEstablecimiento() + "-" + detalle.getId().getCodPuntEmision() + "-" + detalle.getId().getSecuencial());
											detalleEntidad.setTOTAL(detalle.getImporteTotal());
											detalleEntidad.setFECHA(Date.valueOf(String.valueOf(detalle.getFechaEmision())));
											detalleEntidad.setEDOFAC((Estado.trim().equals("IN") ? "INICIAL" :
																	  Estado.trim().equals("GE") ? "GENERADO" :
																	  Estado.trim().equals("FI") ? "FIRMADO" : 
																	  Estado.trim().equals("RE") ? "RECIBIDA" : 
																	  Estado.trim().equals("DE") ? "DEVUELTA" : 
																	  Estado.trim().equals("AT") ? "AUTORIZADO" : 
																	  Estado.trim().equals("NA") ? "NO AUTORIZADO" : 
																	  Estado.trim().equals("CT") ? "CONTINGENCIA" : ""));
											detalleEntidad.setPDFARC(formato + ".pdf");
											detalleEntidad.setXMLARC(formato + ".xml");
											detalleEntidad.setEmail(detalle.getEmail());
											detalleEntidad.setDireccion(empresa.getPathCompFirmados());
											detalleEntidad.setFormato(formato);
											detalleEntidad.setXmlAutorizacion(detalle.getDocuAutorizacion());
											
											detalleEntidad.setCodEstablecimiento(detalle.getId().getCodEstablecimiento());
											detalleEntidad.setCodigoDocumento(detalle.getId().getCodigoDocumento());
											detalleEntidad.setCodPuntoEmision(detalle.getId().getCodPuntEmision());
											detalleEntidad.setSecuencial(detalle.getId().getSecuencial());
											detalleEntidad.setAmbiente((detalle.getId().getAmbiente()==1?"Pruebas":"Produccion"));
											 	
											documentoEntidad.add(detalleEntidad);
											detalleEntidad =  new facDetalleDocumentoEntidad();
											break;
										}
									}
							break;
						}
					}
			}
			
			return documentoEntidad;
		}catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error al buscar registro");
		}
	}
	
	@SuppressWarnings("unchecked")
	public  List<String> BuscarfitroEmpresaDocumentos(String parametro_ruc, String ruc_emppresa, String ambiente) throws Exception{
		
		try{
			Query q = em.createQuery("SELECT distinct E.identificacionComprador FROM FacCabDocumento E where E.identificacionComprador like :ruc_comprador " +
									 "   and E.id.ruc = :ruc_emppresa "
									 + " and E.id.ambiente = :ambiente");
			q.setParameter("ruc_comprador", "%" + parametro_ruc);
			q.setParameter("ruc_emppresa", ruc_emppresa);
			q.setParameter("ambiente", ambiente);
			q.setFirstResult(0);
			q.setMaxResults(10);
			return q.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error al buscar registro");
		}
	}
	
public FacCabDocumento buscarCabDocumentoPorPk(String ruc, 
											   String codEstablecimiento,
											   String codPuntoEmision,
											   String secuencial,
											   String codigoDocumento,
											   String ambiente) throws Exception
{		
		try{
			Query q =  em.createQuery("SELECT E FROM FacCabDocumento E "
								+ "where E.id.ruc = :ruc and E.id.codEstablecimiento = :codEstablecimiento "
								+ " and E.id.codPuntEmision = :codPuntoEmision and E.id.secuencial = :secuencial "
								+ " and E.id.codigoDocumento = :codigoDocumento "
								+ " and E.id.ambiente = :ambiente") ;
			q.setParameter("ruc", ruc);
			q.setParameter("codEstablecimiento", codEstablecimiento);
			q.setParameter("codPuntoEmision", codPuntoEmision);
			q.setParameter("secuencial", secuencial);
			q.setParameter("codigoDocumento", codigoDocumento);
			q.setParameter("ambiente", ambiente.equals("Produccion")?2:1);
			FacCabDocumento e = (FacCabDocumento)q.getSingleResult(); 					
			return e;
		}catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error al Buscar el registro");
		}
	}
	
	public List<FacDetDocumento> buscarDetDocumentoPorFk(String ruc,
														 String codEstablecimiento,
														 String codPuntoEmision,
														 String secuencial,
														 String codigoDocumento,
														 String ambiente) throws Exception
	{
		try{
			List<FacDetDocumento> lstFactDetDocumento = new ArrayList<FacDetDocumento>();
			Query q =  em.createQuery("SELECT E FROM FacDetDocumento E "
									+ "where E.id.ruc = :param1 "
									+ " and E.id.codEstablecimiento = :param2 "
									+ " and E.id.codPuntEmision = :param3 "
									+ " and E.id.secuencial = :param4 "
									+ " and E.id.codigoDocumento = :param5 "
									+ " and E.id.ambiente = :param6 ");
			
			q.setParameter("param1", ruc);
			q.setParameter("param2", codEstablecimiento);
			q.setParameter("param3", codPuntoEmision);
			q.setParameter("param4", secuencial);
			q.setParameter("param5", codigoDocumento);
			q.setParameter("param6", ambiente.equals("Produccion")?2:1);
			lstFactDetDocumento = q.getResultList();
			return lstFactDetDocumento;
		}catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error al Buscar el registro");
		}
	}

	public List<FacDetAdicional> buscarDetDocumentoAdicPorFk(String ruc,
															 String codEstablecimiento,
															 String codPuntoEmision,
															 String secuencial,
															 String codigoDocumento,
															 String ambiente) throws Exception
	{
		try{
			List<FacDetAdicional> lstFactDetDocumento = new ArrayList<FacDetAdicional>();
			Query q =  em.createQuery("SELECT E FROM FacDetAdicional E "
						+ "where E.id.ruc = :ruc and E.id.codEstablecimiento = :codEstablecimiento "
					+ "and E.id.codPuntEmision = :codPuntoEmision and E.id.secuencial = :secuencial "
					+ "and E.id.codigoDocumento = :codigoDocumento "
					+ "and E.id.ambiente = :ambiente") ;
			q.setParameter("ruc", ruc);
			q.setParameter("codEstablecimiento", codEstablecimiento);
			q.setParameter("codPuntoEmision", codPuntoEmision);
			q.setParameter("secuencial", secuencial);
			q.setParameter("codigoDocumento", codigoDocumento);
			q.setParameter("ambiente", ambiente.equals("Produccion")?2:1);		
			lstFactDetDocumento = q.getResultList();
			return lstFactDetDocumento;
		}catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error al Buscar el registro");
		}
	}
	
	public FacProducto buscarProductoPorId(String codPrincipal) throws Exception{
		
		try{
			
			Query q =  em.createQuery("SELECT E FROM FacProducto E where E.codPrincipal = 4");//:codPrincipal") ;
			//q.setParameter("codPrincipal", Integer.getInteger(codPrincipal));		
			FacProducto e = (FacProducto)q.getSingleResult(); 					
			return e;
		}catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error al Buscar el registro");
		}
	}
	
	public FacEstablecimiento buscarCodEstablecimiento(String ruc, String codEstablecimiento){
		FacEstablecimiento busqueda =null;
		try {
			Query q = em.createQuery("select ES from FacEstablecimiento ES where ES.id.ruc= :Ruc AND ES.id.codEstablecimiento = :codEstablecimiento");
			q.setParameter("Ruc", ruc);
			q.setParameter("codEstablecimiento", codEstablecimiento);
			busqueda = (FacEstablecimiento) q.getSingleResult();
			
		} catch (Exception e) {
			System.out.println("tu error es ...: "+e);
			return null;
		}
		return busqueda;
	}
	
	public  List<FacDetRetencione> BuscarDatosdeRetenciones(String ruc, 
															String codEstablecimiento, 
															String codPuntoEmision,
															String secuencial,
															String codigoDocumento,
															String ambiente) throws Exception
	{
		try{
			Query q = em.createQuery("SELECT E FROM FacDetRetencione E where E.id.ruc = :ruc " +
					" and E.id.codEstablecimiento = :establecimiento " +
					" and E.id.codPuntEmision = :puntoemision " +
					" and E.id.secuencial = :secuencial " +
					" and E.id.codigoDocumento = :codigoDocumento " +
					" and E.id.ambiente = :ambiente ");
			q.setParameter("ruc", ruc);
			q.setParameter("establecimiento", codEstablecimiento);
			q.setParameter("puntoemision", codPuntoEmision);
			q.setParameter("secuencial", secuencial);
			q.setParameter("codigoDocumento", codigoDocumento);
			q.setParameter("ambiente",(ambiente.equals("Pruebas")?1:2));
			return q.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error al buscar registro");
		}
	}
	
	
	// INI HFU
	public List<FacDetRetencione> buscarDetRetencionPorFk(String ruc, String codEstablecimiento, String codPuntoEmision, String secuencial, String codigoDocumento, String ambiente) throws Exception
	{
		try{
			List<FacDetRetencione> lstFactDetDocumento = new ArrayList<FacDetRetencione>();
			Query q =  em.createQuery("SELECT E FROM FacDetRetencione E where E.id.ruc = :param1 and E.id.codEstablecimiento = :param2 and E.id.codPuntEmision = :param3 and E.id.secuencial = :param4 and E.id.codigoDocumento = :param5 and E.id.ambiente = :param6") ;
			q.setParameter("param1", ruc);
			q.setParameter("param2", codEstablecimiento);
			q.setParameter("param3", codPuntoEmision);
			q.setParameter("param4", secuencial);
			q.setParameter("param5", codigoDocumento);
			q.setParameter("param6", ambiente.equals("Produccion")?2:1);
			lstFactDetDocumento = q.getResultList();
			return lstFactDetDocumento;
		}catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error al Buscar el registro");
		}
	}
	// FIN HFU
	
}
