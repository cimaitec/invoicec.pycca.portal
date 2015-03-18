package com.recepcion.servicios;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
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
import com.general.servicios.FacGeneralServicio;
import com.recepcion.entidades.FacDocRecepcion;
import com.recepcion.entidades.FacDocRecepcionPK;

@Stateless
public class FacRecepcionServicios {
	@PersistenceContext (unitName=ConfigPersistenceUnit.persistenceUnit)  
	private EntityManager em;
	
	@EJB
	private FacGeneralServicio facGenSer;
	
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
			System.out.println("BuscarDatosdeTipoDocumento::"+q.toString());
			return q.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error al buscar registro");
		}
	}
	
	
	
    public  List<FacDetRetencione> BuscarDatosdeRetenciones(String ruc, 
    														String codEstablecimiento, 
    														String codPuntoEmision,
    														String secuencial,
    														String codigoDocumento,
    														String ambiente) throws Exception{
		
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
			System.out.println("BuscarDatosdeRetenciones::"+q.toString());
			return q.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error al buscar registro");
		}
	}
	@SuppressWarnings("unchecked")
	public List<FacDocRecepcion> MostrandoDetalleDocumento(Object [] obj) throws Exception{
		try{
			String dialect = obj[6].toString();
			
			boolean habilitarFiltroFecha = true;
					
			String database = dialect;
			String ls_filtro = "", ls_filtro_sql = "";
			//database = "SQLServer"; ??????
			/*
			if (database.equals("PostgreSQL"))
				ls_filtro = " and E.fechaEmision Between to_date(:fechaInicio,'yyyy-MM-dd') and  to_date(:fechaFinal,'yyyy-MM-dd') ";
			if (database.equals("SQLServer"))
				ls_filtro = " and E.fechaEmision Between  DATEADD(day,1,:fechaInicio) and   DATEADD(day,1,:fechaFinal)  ";
			*/
			obj[0]= (obj[0].equals("")?null:obj[0]);
			obj[1]= (obj[1].equals("")?null:obj[1]);
			
			//query.setParameter("estado", "AT");
			
			//(E.identificacionComprador = :rucComprador or :rucComprador = :DatoVacio) 
			
			if (obj[0]!=null){
				ls_filtro_sql = ls_filtro_sql + " and E.id.rucProveedor = :rucProveedor ";
				habilitarFiltroFecha = true;
			}			
			
			if (obj[1]!=null){
				ls_filtro_sql = ls_filtro_sql + " and UPPER(E.razonSocialProv) like :razonSocialProv ";
				habilitarFiltroFecha = true;
			}
			
			if (obj[2]!=null){
				ls_filtro_sql = ls_filtro_sql + " and E.id.codDoc = :codDocumentoSelecionado ";
			}
			
			if (obj[8]!=null){
	
					if(obj[8].toString().length()<8){
						ls_filtro_sql = ls_filtro_sql + " and concat(E.id.estab,'-',E.id.ptoEmi,'-',E.id.secuencial)   like :secuencial "
										+ " or concat(E.id.estab,E.id.ptoEmi,E.id.secuencial)  like  :secuencial ";
						habilitarFiltroFecha = true;
					}else{
						ls_filtro_sql = ls_filtro_sql + " and concat(E.id.estab,'-',E.id.ptoEmi,'-',E.id.secuencial)   = :secuencial "
													  + " or concat(E.id.estab, E.id.ptoEmi, E.id.secuencial)   = :secuencial ";
						habilitarFiltroFecha = false;
					}
			}
			
			if (obj[7]!=null){
				//Busqueda por todos los estados:
				if(!obj[7].equals("A")){
					ls_filtro_sql = ls_filtro_sql + " and E.estadowf = :estado ";
				}
				else{
					ls_filtro_sql = ls_filtro_sql + " and E.estadowf IN :estado ";					
				}
			}
			
			
         if (habilitarFiltroFecha){
			
			if (obj[3]!=null){
				if (database.equals("PostgreSQL"))
					ls_filtro_sql = ls_filtro_sql + " and E.fechaEmision >= to_date(:fechaInicio,'yyyy-MM-dd') ";
				else
					ls_filtro_sql = ls_filtro_sql + " and E.fechaEmision>=DATEADD(day,1,:fechaInicio) ";
			}
			
			if (obj[4]!=null){
				if (database.equals("PostgreSQL"))
					ls_filtro_sql = ls_filtro_sql + " and E.fechaEmision <= to_date(:fechaFinal,'yyyy-MM-dd') ";
				else
					ls_filtro_sql = ls_filtro_sql + " and E.fechaEmision<=DATEADD(day,1,:fechaFinal) ";
			}
		}
			String ls_query = "SELECT E FROM FacDocRecepcion E "+
					" where E.id.rucReceptor is not null	" +		
					//" where E.id.rucReceptor = :ruc_empresa	" +	
					ls_filtro_sql +
					//" and E.estadoTransaccion in('AT','TD') " +
					" order by E.fechaEmision desc, E.id.estab desc,  E.id.ptoEmi desc, E.id.secuencial desc";
			
			
			System.out.println("ls_query::"+ls_query);
			Query query = em.createQuery(ls_query);
			System.out.println("rucComprador::"+ obj[0]);
			System.out.println("razonSocialComp::"+ obj[1]);
			System.out.println("codDocumentoSelecionado::"+ obj[2]);
			System.out.println("fechaInicio::"+ obj[3]);
			System.out.println("fechaFinal::"+ obj[4]);
			System.out.println("ruc_empresa::"+ obj[5]);
			System.out.println("dialect::"+ obj[6]);
			System.out.println("estado::"+ obj[7]);
			System.out.println("numDocumento::"+ obj[8]);
			/*
			if (obj[5].toString()!=null){
				query.setParameter("ruc_empresa", obj[5].toString());// parametro que es el ruc de la empresa logoneada
			}
			*/
			//(E.identificacionComprador = :rucComprador or :rucComprador = :DatoVacio) 
			
			if (obj[0]!=null){
				query.setParameter("rucProveedor", obj[0].toString());// parametro de identificador del comprador
			}			
			
			if (obj[1]!=null){
				query.setParameter("razonSocialProv","%"+obj[1].toString()+"%"); // parametro de la razon social
			}
			if (obj[2]!=null){
				query.setParameter("codDocumentoSelecionado",obj[2].toString()); // parametro del documento seleccionado
			}
				
				if(habilitarFiltroFecha){
				
				if (obj[3]!=null){
					query.setParameter("fechaInicio",obj[3].toString()); // parametro de la fecha de inicio
				}
				if (obj[4]!=null){
					query.setParameter("fechaFinal",obj[4].toString()); // parametro de la fecha de final
				}	
				
}
				if (obj[8]!=null){
					if (obj[8].toString().length() < 8) {
						query.setParameter("secuencial",obj[8].toString()+"%");
					}
					else{
						query.setParameter("secuencial",obj[8].toString());
					}
				}
				if (obj[7]!=null){
					//Busqueda por todos los estados:
					if(!obj[7].equals("A")){
						query.setParameter("estado",obj[7].toString());
					}
					else{
						List<FacGeneral> facGenList = new ArrayList<FacGeneral>();
						facGenList = facGenSer.buscarDatosGeneralHijo("118",true);
						ArrayList<String> listaEstados = new ArrayList<String>();
						if (facGenList.size()>0){							
							String claseEstado = "";
							claseEstado = obj[7].equals("A")?"P":"T";
							for(int i=0; i<facGenList.size(); i++){
								//Si lo seleccionado fue "En transición":
								//claseEstado = obj[7].equals("ET")?"T":"P";
								//if (facGenList.get(i).getValor().equalsIgnoreCase(claseEstado)){
									listaEstados.add(facGenList.get(i).getCodUnico());
								//}
							}							
						}
						query.setParameter("estado",listaEstados);						
				}
				
					
			
			/*
			if (database.equals("SQLServer")){
				if(obj[3]== null){
					obj[3] = "2000-01-31";								 
				}
				if(obj[4]== null){
					obj[4]= new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());	
				}
				query.setParameter("fechaInicio",obj[3].toString()); // parametro de la fecha de inicio
				if (obj[4].toString()==null){
					query.setParameter("fechaFinal","GETDATE()-1"); // parametro de la fecha de final
				}else{
					query.setParameter("fechaFinal",obj[4].toString()); // parametro de la fecha de final
				}	
			}*/
			
				}
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
	
	public List<facDetalleDocumentoEntidad> MotrandoDocumentoFiltrados(
																		List<FacDocRecepcion> documento_detalles,
																		List<FacTiposDocumento> tipoDocumentos,List<FacEmpresa> detalleEmpresas) throws Exception{
		try{
			
			
			List<facDetalleDocumentoEntidad> documentoEntidad = new ArrayList<facDetalleDocumentoEntidad>();
			facDetalleDocumentoEntidad detalleEntidad = new facDetalleDocumentoEntidad();
			
			if(documento_detalles.isEmpty()){
				return documentoEntidad;
			}
			
			List<FacGeneral> facGenList = new ArrayList<FacGeneral>();
			// Obtener todos los estados configurados:
			facGenList = facGenSer.buscarDatosGeneralHijo("117",true);
			
			HashMap<String, String> estados = new HashMap<String, String>();
			if (facGenList.size()>0){
				for (FacGeneral estado : facGenList) {					
					estados.put(estado.getCodUnico(), estado.getDescripcion());
				}
			}
			for (FacDocRecepcion detalle : documento_detalles) {/*
					for (FacTiposDocumento documento : tipoDocumentos) {
						if (detalle.getId().getCodDoc().equalsIgnoreCase(documento.getIdDocumento())) {// validando si el codigo del documento existe
							for (FacEmpresa empresa : detalleEmpresas) {
										if (detalle.getId().getRucReceptor().equalsIgnoreCase(empresa.getRuc())) {// validando si el codigo del documento existe
											String formato = Integer.parseInt(detalle.getId().getCodigoDocumento()) + detalle.getId().getRuc()+detalle.getId().getCodigoDocumento()+ detalle.getId().getCodEstablecimiento() + detalle.getId().getCodPuntEmision() + detalle.getId().getSecuencial();
											String Estado = detalle.getEstado().trim();
											detalleEntidad.setRFCREC(detalle.get);
											detalleEntidad.setNOMREC(detalle.getRazonSocialComprador());
											detalleEntidad.setCodDoc(documento.getIdDocumento());
											detalleEntidad.setTIPODOC(documento.getDescripcion());
											detalleEntidad.setFOLFAC(detalle.getId().getCodEstablecimiento() + "-" + detalle.getId().getCodPuntEmision() + "-" + detalle.getId().getSecuencial());
											if (detalle.getId().getCodigoDocumento().equals("01"))
												detalleEntidad.setTOTAL(detalle.getImporteTotal());
											if (detalle.getId().getCodigoDocumento().equals("07"))
												detalleEntidad.setTOTAL(detalle.getImporteTotal());
											if (detalle.getId().getCodigoDocumento().equals("04")){
												double total = detalle.getSubtotal12()+
															   detalle.getSubtotalNoIva()+
															   detalle.getSubtotal0()+
															   detalle.getIva12()+
															   detalle.getTotalvalorICE();
												detalleEntidad.setTOTAL(Double.valueOf(total));
											}
											detalleEntidad.setFECHA(Date.valueOf(String.valueOf(detalle.getFechaEmision())));
											
											
											detalleEntidad.setEDOFAC((Estado.trim().equals("IN") ? "INICIAL" : 
																	  Estado.trim().equals("GE") ? "GENERADO" : 
																	  Estado.trim().equals("FI") ? "FIRMADO" : 
																	  Estado.trim().equals("RE") ? "RECIBIDA" : 
																	  Estado.trim().equals("DE") ? "DEVUELTA" : 
																	  Estado.trim().equals("AT") ? "AUTORIZADO" : 
																	  Estado.trim().equals("NA") ? "NO AUTORIZADO" : 
																      Estado.trim().equals("AN") ? "NO AUTORIZADO" : 
																	  Estado.trim().equals("CT") ? "CONTINGENCIA" :
																      Estado.trim().equals("RS") ? "RECIBIDA SRI" :
																      Estado.trim().equals("RS") ? "SIN RESPUESTA" :
																      Estado.trim().equals("EX")? "ERROR XML":
																	  Estado.trim().equals("TD") ? "CONTINGENCIA RECEPCION" 
																			  : ""));
											
											detalleEntidad.setEDOFAC(estados.get(Estado.trim()));
											detalleEntidad.setPDFARC(formato + ".pdf");
											detalleEntidad.setXMLARC(formato + ".xml");
											detalleEntidad.setEmail(detalle.getEmail());
											detalleEntidad.setDireccion(empresa.getPathCompFirmados());
											detalleEntidad.setFormato(formato);
											detalleEntidad.setXmlAutorizacion(detalle.getDocuAutorizacion());
											
											detalleEntidad.setCodEstablecimiento(detalle.getId().getCodEstablecimiento());
											detalleEntidad.setCodPuntoEmision(detalle.getId().getCodPuntEmision());
											detalleEntidad.setSecuencial(detalle.getId().getSecuencial());
											detalleEntidad.setAmbiente((detalle.getId().getAmbiente().intValue()==1?"Pruebas":"Produccion"));
											
											documentoEntidad.add(detalleEntidad);
											detalleEntidad =  new facDetalleDocumentoEntidad();
											break;
										}
									}
							break;
						}
					}
			*/}
			
			return documentoEntidad;
		}catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error al buscar registro");
		}
	}
	
	public FacGeneralServicio getFacGenSer() {
		return facGenSer;
	}

	public void setFacGenSer(FacGeneralServicio facGenSer) {
		this.facGenSer = facGenSer;
	}

	@SuppressWarnings("unchecked")
	public  List<String> BuscarfitroEmpresaDocumentos(String parametro_ruc, String ruc_emppresa, String ambiente) throws Exception{
		
		try{
			Query q = em.createQuery("SELECT distinct E.identificacionComprador FROM FacCabDocumento E where E.identificacionComprador like :ruc_comprador " +
					"		and E.id.ruc = :ruc_emppresa and E.id.ambiente = :ambiente ");
			q.setParameter("ruc_comprador", "%" + parametro_ruc);
			q.setParameter("ruc_emppresa", ruc_emppresa);
			q.setParameter("ambiente", (ambiente.equals("Pruebas")?1:2));
			q.setFirstResult(0);
			q.setMaxResults(10);
			return q.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error al buscar registro");
		}
	}
	
public FacCabDocumento buscarCabDocumentoPorPk(String ruc, String codEstablecimiento, String codPuntoEmision, String secuencial, String codigoDocumento, String ambiente) throws Exception{
		
		try{
			
			Query q =  em.createQuery("SELECT E FROM FacCabDocumento E where E.id.ruc = :ruc and E.id.codEstablecimiento = :codEstablecimiento " +
					"and E.id.codPuntEmision = :codPuntoEmision and E.id.secuencial = :secuencial and E.id.codigoDocumento = :codigoDocumento " +
					"and E.id.ambiente = :ambiente") ;
			q.setParameter("ruc", ruc);
			q.setParameter("codEstablecimiento", codEstablecimiento);
			q.setParameter("codPuntoEmision", codPuntoEmision);
			q.setParameter("secuencial", secuencial);
			q.setParameter("codigoDocumento", codigoDocumento);			
			q.setParameter("ambiente",(ambiente.equals("Pruebas")?1:2));
			FacCabDocumento e = (FacCabDocumento)q.getSingleResult(); 	
			System.out.println(" ===>> 5 : Resulset "+q.getSingleResult());
			return e;
		}catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error al Buscar el registro");
		}
	}
	
	public List<FacDetDocumento> buscarDetDocumentoPorFk(String ruc, String codEstablecimiento, String codPuntoEmision, String secuencial, String codigoDocumento, String ambiente) throws Exception{
		
		try{
			System.out.println("ruc: " + ruc);
			System.out.println("codEstablecimiento: " + codEstablecimiento);
			System.out.println("codPuntoEmision: " + codPuntoEmision);
			System.out.println("secuencial: " + secuencial);
			System.out.println("codigoDocumento: " + codigoDocumento);
			System.out.println("ambiente: " + ambiente);
			List<FacDetDocumento> lstFactDetDocumento = new ArrayList<FacDetDocumento>();
			Query q =  em.createQuery("SELECT E FROM FacDetDocumento E where E.id.ruc = :param1 and E.id.codEstablecimiento = :param2 and E.id.codPuntEmision = :param3 and E.id.secuencial = :param4 and E.id.codigoDocumento = :param5 and E.id.ambiente = :param6 ") ;
			q.setParameter("param1", ruc);
			q.setParameter("param2", codEstablecimiento);
			q.setParameter("param3", codPuntoEmision);
			q.setParameter("param4", secuencial);
			q.setParameter("param5", codigoDocumento);					
			q.setParameter("param6", (ambiente.equals("Pruebas")?1:2));
			lstFactDetDocumento = q.getResultList();
			return lstFactDetDocumento;
		}catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error al Buscar el registro");
		}
	}

	public List<FacDetAdicional> buscarDetDocumentoAdicPorFk(String ruc, String codEstablecimiento, String codPuntoEmision, String secuencial, String codigoDocumento, String ambiente) throws Exception{
		
		try{
			List<FacDetAdicional> lstFactDetDocumento = new ArrayList<FacDetAdicional>();
			Query q =  em.createQuery("SELECT E FROM FacDetAdicional E where E.id.ruc = :ruc and E.id.codEstablecimiento = :codEstablecimiento and E.id.codPuntEmision = :codPuntoEmision and E.id.secuencial = :secuencial and E.id.codigoDocumento = :codigoDocumento and E.id.ambiente = :ambiente") ;
			q.setParameter("ruc", ruc);
			q.setParameter("codEstablecimiento", codEstablecimiento);
			q.setParameter("codPuntoEmision", codPuntoEmision);
			q.setParameter("secuencial", secuencial);
			q.setParameter("codigoDocumento", codigoDocumento);
			q.setParameter("ambiente", (ambiente.equals("Pruebas")?1:2));
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
			System.out.println("tu erro es ...: "+e);
			return null;
		}
		return busqueda;
	}
	
	
	
	public void actualizarEstadoDocumento(FacDocRecepcion documento,String estado,String rucEmpresa){
		try {
			try {
		    		Thread.sleep(1000);
	     		} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
	    		}
			
			FacDocRecepcionPK idpk = documento.getId();
		    Query q = em.createQuery("UPDATE FacDocRecepcion SET  estadowf= :estado " +
		    						 " WHERE id= :idDocuemnto ");
		    q.setParameter("estado", estado);
		    q.setParameter("idDocuemnto", idpk);

		    q.executeUpdate();
			    System.out.println("Se actualiza documento" );
			    
	  	} catch (Exception e) {
	  		e.printStackTrace();
	  	
		}
	}

}
