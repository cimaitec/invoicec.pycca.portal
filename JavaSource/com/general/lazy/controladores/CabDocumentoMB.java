package com.general.lazy.controladores;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.primefaces.event.CloseEvent;
import org.primefaces.model.LazyDataModel;

import com.documentos.entidades.FacDetAdicional;
import com.documentos.entidades.FacDetDocumento;
import com.documentos.entidades.FacEmpresa;
import com.general.controladores.FacEnviarMail;
import com.general.entidades.FacCliente;
import com.general.entidades.FacEstablecimiento;
import com.general.entidades.FacProducto;
import com.general.entidades.FacTiposDocumento;
import com.general.lazy.connection.MyTransaction;
import com.general.servicios.FacClienteServicios;
import com.general.servicios.FacDocumentoServicios;
import com.general.servicios.FacEmpresaEmisoraServicios;
import com.general.servicios.FacGeneralServicio;
import com.general.util.DetallesAdicionalesReporte;
import com.general.util.InformacionAdicional;
import com.general.lazy.wrappers.FacCabDocumento;

//import com.documentos.entidades.FacCabDocumento;


@ViewScoped
@ManagedBean
public class CabDocumentoMB implements Serializable
{
	private static final long serialVersionUID = 1L;
	private LazyDataModel<FacCabDocumento> documentos = null;
	private FacCabDocumento documento;
	private FacCabDocumento[] seleccionDocu;
	
	//Fields Filters
	private String ruc; 
	private String razonSocial;
	
	private ArrayList<SelectItem> Tipo; // variable que recoge los tipos de documento
	private String SeleccionTipo; // variable que recoge el item selecciondo del tipo de documento
	private List<FacTiposDocumento> TipoDocumento; // variable que recoge los tipos de documento
	private String numDocumento;
	private ArrayList<SelectItem> TipoEstados; // variable que recoge los tipos de estados de los documentos
	private String SeleccionTipoEstado; // variable que recoge el item selecciondo del tipo de Estado
	private Date FechaInicio;// dato que se encarga de recoger la fecha de inicio
	private Date FechaFinal;// dato que se encarga de recoger la fecha de final
	
	private List<FacCabDocumento> listAllDocumentos;
	private FacCabDocumento seleccionDocumentos;
	
	//Filters
	private HashMap<String, String> filters = new HashMap<String, String>();
	private HashMap<String, String> Criterios = new HashMap<String, String>();
	
	private String [] selectedOptionsEmail; // variable que recoge si desea enviar correo con xml o pdf 
	private FacEnviarMail claseEmail;// variable que contiene la clase de email
	private String Correos;// dato de correos que se va a enviar de las facuras
	private String rucEmpresa;
	
	private HashMap<String, String> pathReports = null;
	
	@EJB
	private FacDocumentoServicios facDocumentoServicios; // variable en la cual llama la clase de servicio
	@EJB
	private FacGeneralServicio facGenSer;
	@EJB
	private FacClienteServicios facCliSer;
	@EJB
	private FacEmpresaEmisoraServicios facServEmpresa;
	
	public void seleccionListDocumentosPdf()
	{
		if (seleccionDocu!=null){
			if (seleccionDocu.length==0){
				//System.out.println("No hay Datos Seleccionados.");
				FacesMessage mensaje=null;
				mensaje = new FacesMessage(FacesMessage.SEVERITY_INFO,"Favor Seleccione Por lo menos un Documento.",null);
				FacesContext.getCurrentInstance().addMessage(null, mensaje);
			}else{
				HashMap<String, String> tipoDocumento = null;
				HashMap<String, Integer> ambientes = null;
				
				tipoDocumento = new HashMap<String, String>();
				tipoDocumento.put("FACTURA", "01");
				tipoDocumento.put("NOTA DE CREDITO", "04");
				tipoDocumento.put("COMPROBANTE DE RETENCION", "07");
				tipoDocumento.put("GUIAS DE REMISIÓN", "06");
				
				ambientes = new HashMap<String, Integer>();
				ambientes.put("Pruebas", 1);
				ambientes.put("Produccion", 2);
				
				for(int i = 0; i< seleccionDocu.length;i++)
				{
					System.out.println("secuencial::"+seleccionDocu[i].getId().getSecuencial());
					
					seleccionDocu[i].getId().setAmbiente(ambientes.get(seleccionDocu[i].getId().getAmbiente()).toString());
					seleccionDocu[i].getId().setCodigoDocumento(tipoDocumento.get(seleccionDocu[i].getId().getCodigoDocumento()));
				}
			}
		}else{
			//System.out.println("No hay Datos Seleccionados is Null.");
			FacesMessage mensaje=null;
			mensaje = new FacesMessage(FacesMessage.SEVERITY_INFO,"Favor Seleccione Por lo menos un Documento.",null);
			FacesContext.getCurrentInstance().addMessage(null, mensaje);
		}
	}
	
	public void pdf() throws Throwable
	{
		//if(seleccionDocumentos.getId().getCodigoDocumento().equals("01") ||seleccionDocumentos.getId().getCodigoDocumento().equals("04"))
		//	generaReporteFactura();
		/*if(valorDetalleDocumento.getCodigoDocumento().equals("06"))
			generaReporteGuiaRemision();*/
		//if(seleccionDocumentos.getId().getCodigoDocumento().equals("07"))
			//generarReporteRetencion();
    }
	
/*	
	public void generaReporteFactura() throws Throwable
	{
		seleccionDocumentos.getId().setCodigoDocumento(valorDetalleDocumento.getCodDoc());
		pathReports = facGenSer.buscarDatosGeneralPadreHash("103");
		String pathReport = pathReports.get(valorDetalleDocumento.getCodigoDocumento());
		
		FileInputStream is = null;
	    JRDataSource dataSource = null;
		List<FacDetDocumento> lstFactDetDocumento = new ArrayList<FacDetDocumento>();
		List<FacDetAdicional> lstFactDetAdictDocumento = new ArrayList<FacDetAdicional>();
		List<InformacionAdicional> infoAdicional = new ArrayList<InformacionAdicional>();
		List<DetallesAdicionalesReporte> detallesAdiciones = new ArrayList<DetallesAdicionalesReporte>();
		
		lstFactDetDocumento = facDocumentoServicios.buscarDetDocumentoPorFk(rucEmpresa,
																			seleccionDocumentos.getId().getCodEstablecimiento(),
																			seleccionDocumentos.getId().getCodPuntEmision(), 
																			seleccionDocumentos.getId().getSecuencial(),
																			seleccionDocumentos.getId().getCodigoDocumento(),
																			seleccionDocumentos.getId().getAmbiente());
		
		lstFactDetAdictDocumento = facDocumentoServicios.buscarDetDocumentoAdicPorFk(rucEmpresa,
																					 seleccionDocumentos.getId().getCodEstablecimiento(),
																					 seleccionDocumentos.getId().getCodPuntEmision(), 
																					 seleccionDocumentos.getId().getSecuencial(),
																					 seleccionDocumentos.getId().getCodigoDocumento(),
																					 seleccionDocumentos.getId().getAmbiente());

		
		for (FacDetDocumento detDocumento : lstFactDetDocumento)
		{
            DetallesAdicionalesReporte detAd = new DetallesAdicionalesReporte();
            detAd.setCodigoPrincipal(detDocumento.getCodPrincipal());
            detAd.setCodigoAuxiliar(detDocumento.getCodAuxiliar());
            detAd.setDescuento(String.valueOf(detDocumento.getDescuento()));
            detAd.setDescripcion(detDocumento.getDescripcion());
            detAd.setCantidad(String.valueOf(detDocumento.getCantidad()));
            detAd.setPrecioTotalSinImpuesto(String.valueOf(detDocumento.getPrecioTotalSinImpuesto()));
            detAd.setPrecioUnitario(String.valueOf(detDocumento.getPrecioUnitario()));
            detAd.setInfoAdicional(infoAdicional.isEmpty() ? null : infoAdicional);
            detallesAdiciones.add(detAd);
		}
		
		String name = seleccionDocumentos.getId().getCodigoDocumento() + seleccionDocumentos.getId().getCodEstablecimiento() + seleccionDocumentos.getId().getCodPuntEmision() + ruc + seleccionDocumentos.getId().getSecuencial();
       
        JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(detallesAdiciones);  
        pathReport = "/reportes/"+pathReports.get(seleccionDocumentos.getId().getCodigoDocumento());        
        String reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath(pathReport);  
        JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, obtenerMapaParametrosReportes(obtenerParametrosInfoTriobutaria(), obtenerInfoFactura(lstFactDetAdictDocumento)), beanCollectionDataSource);  
        HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
        httpServletResponse.addHeader("Content-disposition", "attachment; filename="+name+".pdf");  
        ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();  
        JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
        FacesContext.getCurrentInstance().responseComplete();
	}
	
	private Map<String, Object> obtenerInfoFactura(List<FacDetAdicional> lstFactDetAdictDocumento)
	{
	     Map param = new HashMap();
	     FacCabDocumento cabDoc = new FacCabDocumento();
	     try
	     {
	    	 cabDoc = facDocumentoServicios.buscarCabDocumentoPorPk(rucEmpresa,
	    			 												seleccionDocumentos.getId().getCodEstablecimiento(),
	    			 												seleccionDocumentos.getId().getCodPuntoEmision(), 
	    			 												seleccionDocumentos.getId().getSecuencial(),
	    			 												seleccionDocumentos.getId().getCodigoDocumento(),
	    			 												seleccionDocumentos.getId().getAmbiente());
	       if (cabDoc != null) {
	         param.put("RS_COMPRADOR", cabDoc.getRazonSocialComprador());
	         param.put("RUC_COMPRADOR", cabDoc.getIdentificacionComprador());
	         param.put("FECHA_EMISION", cabDoc.getFechaEmision());
	         param.put("GUIA", cabDoc.getGuiaRemision());
	         if (seleccionDocumentos.getId().getCodigoDocumento().equals("01"))
	        	 param.put("VALOR_TOTAL", Double.valueOf(cabDoc.getImporteTotal()));
	         if (seleccionDocumentos.getId().getCodigoDocumento().equals("04")){
	        	 double total = cabDoc.getSubtotal12()+cabDoc.getSubtotalNoIva()+cabDoc.getSubtotal0()+cabDoc.getIva12();
	        	 param.put("VALOR_TOTAL", total);
	         }
	         
	         param.put("SUBTOTAL_12", (cabDoc.getSubtotal12()==0?"0.00":cabDoc.getSubtotal12()));
	         param.put("SUBTOTAL_0", (cabDoc.getSubtotal0()==0?"0.00":cabDoc.getSubtotal0()));
	         param.put("IVA", Double.valueOf(cabDoc.getIva12()));
	         //emite.getInfEmisor().getTotalSinImpuestos()
	         param.put("IVA_0", (Double.valueOf(cabDoc.getSubtotal0())==0?"0.00":Double.valueOf(cabDoc.getSubtotal0())));
	         //param.put("IVA_12", (Double.valueOf(cabDoc.getSubtotal12())==0?"0.00":Double.valueOf(cabDoc.getSubtotal12())==0));	// HFU
	         param.put("IVA_12", (Double.valueOf(cabDoc.getIva12())==0?"0.00":Double.valueOf(cabDoc.getIva12())));	// HFU
	         param.put("ICE", (Double.valueOf(cabDoc.getTotalvalorICE())==0?"0.00":Double.valueOf(cabDoc.getTotalvalorICE())));
	         param.put("NO_OBJETO_IVA", (Double.valueOf(cabDoc.getSubtotalNoIva())==0?"0.00":Double.valueOf(cabDoc.getSubtotalNoIva())));
	         param.put("SUBTOTAL", (Double.valueOf(cabDoc.getTotalSinImpuesto())==0?"0.00":Double.valueOf(cabDoc.getTotalSinImpuesto())));
	         param.put("PROPINA", (Double.valueOf(cabDoc.getPropina())==0?"0.00":Double.valueOf(cabDoc.getPropina())));
	         param.put("TOTAL_DESCUENTO", (Double.valueOf(cabDoc.getTotalDescuento())==0?"0.00":Double.valueOf(cabDoc.getTotalDescuento())));
	         
	         param.put("FECHA_EMISION_DOC_SUSTENTO", cabDoc.getFechaEmisionDocSustento());
			 param.put("NUM_DOC_MODIFICADO", cabDoc.getNumDocModificado());
			 
			 param.put("FECHA_AUTORIZACION", cabDoc.getFechaAutorizado()==null?"":cabDoc.getFechaAutorizado());
			 
			 
			 String lsTipoDocModificado ="";
				if(cabDoc.getCodDocModificado()!=null)
				{
				   if(cabDoc.getCodDocModificado().equals("01"))
					   lsTipoDocModificado = "Factura";
				   else if(cabDoc.getCodDocModificado().equals("04"))
					   lsTipoDocModificado = "Nota de Crédito";
				   else if(cabDoc.getCodDocModificado().equals("07"))
					   lsTipoDocModificado = "Comprobante Retención";
				   else if(cabDoc.getCodDocModificado().equals("06"))
					   lsTipoDocModificado = "Guía Remisión";
			    }
				param.put("TIP_DOC_MODIFICADO", lsTipoDocModificado);
	       }
	       
	       for (FacDetAdicional detAdic : lstFactDetAdictDocumento) {
		        param.put(detAdic.getNombre(), detAdic.getValor());
			}
	     } catch (Exception e) {
	       e.printStackTrace();
	     }
	     return param;
	}
	
	
	
	
*/	
	
	@PostConstruct
	public void inicializarView(){
		llenarCombo();
		buscarPorCodigo();
	}
	
	public String pdf(FacCabDocumento doc, String tipo){
		System.out.println(doc.getId().getAmbiente() + 
						   doc.getId().getRuc() + 
						   doc.getId().getCodigoDocumento()+ 
						   doc.getId().getCodEstablecimiento()+ 
						   doc.getId().getCodPuntEmision()+ 
						   doc.getId().getSecuencial());
		HashMap<String, String> pathReports = null;
		pathReports = facGenSer.buscarDatosGeneralPadreHash("103");
		String pathReport = pathReports.get(doc.getId().getCodigoDocumento());
		System.out.println("pathReports::"+pathReports.toString());
		System.out.println("pathReport::"+pathReport);
		
		FileInputStream is = null;
	    JRDataSource dataSource = null;
		List<FacDetDocumento> lstFactDetDocumento = new ArrayList<FacDetDocumento>();
		List<FacDetAdicional> lstFactDetAdictDocumento = new ArrayList<FacDetAdicional>();
		List<InformacionAdicional> infoAdicional = new ArrayList<InformacionAdicional>();
		List<DetallesAdicionalesReporte> detallesAdiciones = new ArrayList<DetallesAdicionalesReporte>();
		rucEmpresa = doc.getId().getRuc();
		try
		{
			lstFactDetDocumento = facDocumentoServicios.buscarDetDocumentoPorFk(rucEmpresa,
																				doc.getId().getCodEstablecimiento(),
																				doc.getId().getCodPuntEmision(), 
																				doc.getId().getSecuencial(),
																				doc.getId().getCodigoDocumento(),
																				doc.getId().getAmbiente());
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			lstFactDetAdictDocumento = facDocumentoServicios.buscarDetDocumentoAdicPorFk(rucEmpresa,
																						 doc.getId().getCodEstablecimiento(),
																						 doc.getId().getCodPuntEmision(), 
																						 doc.getId().getSecuencial(),
																						 doc.getId().getCodigoDocumento(),
																						 doc.getId().getAmbiente());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (FacDetDocumento detDocumento : lstFactDetDocumento) {
			FacProducto producto = new FacProducto();
            DetallesAdicionalesReporte detAd = new DetallesAdicionalesReporte();
            detAd.setCodigoPrincipal(detDocumento.getCodPrincipal());
            detAd.setCodigoPrincipal(detDocumento.getCodAuxiliar());
            detAd.setDescuento(String.valueOf(detDocumento.getDescuento()));
            detAd.setCodigoAuxiliar(detDocumento.getCodAuxiliar());
            detAd.setDescripcion(detDocumento.getDescripcion());
            detAd.setCantidad(String.valueOf(detDocumento.getCantidad()));
            detAd.setPrecioTotalSinImpuesto(String.valueOf(detDocumento.getPrecioTotalSinImpuesto()));
            detAd.setPrecioUnitario(String.valueOf(detDocumento.getPrecioUnitario()));
            detAd.setInfoAdicional(infoAdicional.isEmpty() ? null : infoAdicional);            
            detallesAdiciones.add(detAd);		
		}
		if(tipo.equals("mail")){
			String namePdf =doc.getId().getAmbiente() + 
					   doc.getId().getRuc() + 
					   doc.getId().getCodigoDocumento()+ 
					   doc.getId().getCodEstablecimiento()+ 
					   doc.getId().getCodPuntEmision()+ 
					   doc.getId().getSecuencial()+".pdf";
			File dirTemp = new File(System.getProperty("java.io.tmpdir"));
			if(!dirTemp.exists())
				dirTemp.mkdirs();
			String name = (dirTemp + dirTemp.separator+namePdf);;
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(detallesAdiciones);  
	        pathReport = "/reportes/"+pathReports.get(doc.getId().getCodigoDocumento());
	        String reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath(pathReport);  
	        JasperPrint jasperPrint = null;
			try {
				jasperPrint = JasperFillManager.fillReport(reportPath, obtenerMapaParametrosReportes(obtenerParametrosInfoTriobutaria(doc), obtenerInfoFactura(lstFactDetAdictDocumento,doc)), beanCollectionDataSource);
			} catch (JRException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        try {
				JasperExportManager.exportReportToPdfFile(jasperPrint, name);
			} catch (JRException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return name;
		}else{
			String name = doc.getId().getAmbiente() + 
					   doc.getId().getRuc() + 
					   doc.getId().getCodigoDocumento()+ 
					   doc.getId().getCodEstablecimiento()+ 
					   doc.getId().getCodPuntEmision()+ 
					   doc.getId().getSecuencial()+".pdf";
	        JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(detallesAdiciones);  
	        pathReport = "/reportes/"+pathReports.get(doc.getId().getCodigoDocumento());
	        String reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath(pathReport);  
	        JasperPrint jasperPrint = null;
			try {
				jasperPrint = JasperFillManager.fillReport(reportPath, obtenerMapaParametrosReportes(obtenerParametrosInfoTriobutaria(doc), obtenerInfoFactura(lstFactDetAdictDocumento,doc)), beanCollectionDataSource);
			} catch (JRException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
	        HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
	        httpServletResponse.addHeader("Content-disposition", "attachment; filename="+name);  
	        ServletOutputStream servletOutputStream = null;
			try {
				servletOutputStream = httpServletResponse.getOutputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
	        try {
				JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
			} catch (JRException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
	        FacesContext.getCurrentInstance().responseComplete();  
	        try {
				servletOutputStream.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return name;
		}
	}
	
	private Map<String, Object> obtenerParametrosInfoTriobutaria(FacCabDocumento doc)
	   {
	     Map param = new HashMap();
	     String pathSubReport= null;
	     com.documentos.entidades.FacCabDocumento cabDoc = new com.documentos.entidades.FacCabDocumento();
	     FacEmpresa empresa = new FacEmpresa();
	     FacEstablecimiento establecimiento = new FacEstablecimiento();
	     empresa=facServEmpresa.verificarRuc(doc.getId().getRuc());
	     try {
	       cabDoc = facDocumentoServicios.buscarCabDocumentoPorPk(doc.getId().getRuc(),
	    		   												  doc.getId().getCodEstablecimiento(),
	    		   												  doc.getId().getCodPuntEmision(), 
	    		   												  doc.getId().getSecuencial(),
	    		   												  doc.getId().getCodigoDocumento(),
	    		   												  doc.getId().getAmbiente());
	       if (cabDoc != null) {
	         empresa = facDocumentoServicios.listadoEmpr(rucEmpresa);
	         establecimiento = facDocumentoServicios.buscarCodEstablecimiento(rucEmpresa, doc.getId().getCodEstablecimiento());
	         param.put("RUC", rucEmpresa);
	         param.put("CLAVE_ACC", (cabDoc.getClaveAcceso().trim().equals("")) || (cabDoc.getClaveAcceso() == null) ? "SIN CLAVE" : cabDoc.getClaveAcceso());
	         param.put("RAZON_SOCIAL", empresa.getRazonSocial());
	         param.put("NOM_COMERCIAL", empresa.getRazonComercial());
	         param.put("DIR_MATRIZ", empresa.getDireccionMatriz());
		         if(pathSubReport==null){
		        	 pathSubReport = "";
		         }
	         param.put("SUBREPORT_DIR", pathSubReport);
	         param.put("TIPO_EMISION", cabDoc.getTipoEmision().trim().equals("1") ? "NORMAL" : "CONTINGENCIA");
	         param.put("NUM_AUT", (cabDoc.getAutorizacion() == null) || (cabDoc.getAutorizacion().equals("")) ? "" : cabDoc.getAutorizacion());
	         param.put("FECHA_AUT", cabDoc.getFechaAutorizado() == null ? "" : cabDoc.getFechaAutorizado());
	         param.put("NUM_FACT", cabDoc.getId().getCodEstablecimiento() + "-" + cabDoc.getId().getCodPuntEmision() + "-" + cabDoc.getId().getSecuencial());
	         if (cabDoc.getId().getCodigoDocumento().equals("04")){
	        	 param.put("NUM_DOC_MODIFICADO", (cabDoc.getNumDocModificado() == null) || (cabDoc.getNumDocModificado().equals("")) ? "" : cabDoc.getNumDocModificado());
	        	 param.put("FECHA_EMISION_DOC_SUSTENTO", cabDoc.getFechaEmisionDocSustento() == null ? "" : new SimpleDateFormat("dd/MM/yyyy").format(cabDoc.getFechaEmisionDocSustento()));
	         }
	         param.put("AMBIENTE", cabDoc.getId().getAmbiente().intValue() == 1 ? "PRUEBA" : "PRODUCCION");
	         param.put("DIR_SUCURSAL", establecimiento.getDireccionEstablecimiento());
	         param.put("CONT_ESPECIAL", empresa.getContribEspecial());
	         //param.put("LLEVA_CONTABILIDAD", cabDoc.getObligadoContabilidad()); 
	         //CPA
	         if (empresa.getObligContabilidad()!=null){
		         if ((empresa.getObligContabilidad().equals("S"))||(empresa.getObligContabilidad().equals("SI")))
		         param.put("LLEVA_CONTABILIDAD", "SI");
		         if ((empresa.getObligContabilidad().equals("N"))||(empresa.getObligContabilidad().equals("NO")))
			         param.put("LLEVA_CONTABILIDAD", "NO");
	         }else{
	        	 param.put("LLEVA_CONTABILIDAD", "NO");
	         }
	         
	         if (empresa.getPathLogoEmpresa() != null){
	        	 if (empresa.getPathLogoEmpresa().length()>0)
	        	 param.put("LOGO", empresa.getPathLogoEmpresa());
	         }
	       }
	     }
	     catch (Exception e) { e.printStackTrace(); }
	 
	     return param;
	   }
	
	private Map<String, Object> obtenerInfoFactura(List<FacDetAdicional> lstFactDetAdictDocumento, FacCabDocumento doc)
	{
		Map param = new HashMap();
	    com.documentos.entidades.FacCabDocumento cabDoc = new com.documentos.entidades.FacCabDocumento();
	    try
	    {
	       cabDoc = facDocumentoServicios.buscarCabDocumentoPorPk(rucEmpresa,
	    		   												  doc.getId().getCodEstablecimiento(),
	    		   												  doc.getId().getCodPuntEmision(), 
	    		   												  doc.getId().getSecuencial(),
	    		   												  doc.getId().getCodigoDocumento(),
	    		   												  doc.getId().getAmbiente());
	       if (cabDoc != null)
	       {
	         param.put("RS_COMPRADOR", cabDoc.getRazonSocialComprador());
	         param.put("RUC_COMPRADOR", cabDoc.getIdentificacionComprador());
	         param.put("FECHA_EMISION", cabDoc.getFechaEmision());
	         param.put("GUIA", cabDoc.getGuiaRemision());
	         if (doc.getId().getCodigoDocumento().equals("01"))
	         param.put("VALOR_TOTAL", Double.valueOf(cabDoc.getImporteTotal()));
	         if (doc.getId().getCodigoDocumento().equals("04")){
	        	 double total = cabDoc.getSubtotal12()+cabDoc.getSubtotalNoIva()+cabDoc.getSubtotal0()+cabDoc.getIva12();
	        	 param.put("VALOR_TOTAL", total);
	         }
			  
	         param.put("IVA", Double.valueOf(cabDoc.getIva12()));
	         //emite.getInfEmisor().getTotalSinImpuestos()
	         param.put("IVA_0", (Double.valueOf(cabDoc.getSubtotal0())==0?"0.00":Double.valueOf(cabDoc.getSubtotal0())));
	         param.put("IVA_12", (Double.valueOf(cabDoc.getSubtotal12())==0?"0.00":Double.valueOf(cabDoc.getSubtotal12())));
	         param.put("ICE", (Double.valueOf(cabDoc.getTotalvalorICE())==0?"0.00":Double.valueOf(cabDoc.getTotalvalorICE())));
	         param.put("NO_OBJETO_IVA", (Double.valueOf(cabDoc.getSubtotalNoIva())==0?"0.00":Double.valueOf(cabDoc.getSubtotalNoIva())));
	         param.put("SUBTOTAL", (Double.valueOf(cabDoc.getTotalSinImpuesto())==0?"0.00":Double.valueOf(cabDoc.getTotalSinImpuesto())));
	         param.put("PROPINA", (Double.valueOf(cabDoc.getPropina())==0?"0.00":Double.valueOf(cabDoc.getPropina())));
	         param.put("TOTAL_DESCUENTO", (Double.valueOf(cabDoc.getTotalDescuento())==0?"0.00":Double.valueOf(cabDoc.getTotalDescuento())));
	         
	         param.put("FECHA_EMISION_DOC_SUSTENTO", cabDoc.getFechaEmisionDocSustento());
			 param.put("NUM_DOC_MODIFICADO", cabDoc.getNumDocModificado());
	       }
	       
	       for (FacDetAdicional detAdic : lstFactDetAdictDocumento) {							
		        param.put(detAdic.getNombre(), detAdic.getValor());
			}
	     } catch (Exception e) {
	       e.printStackTrace();
	     }
	     return param;
	   }
	
	private Map<String, Object> obtenerMapaParametrosReportes(Map<String, Object> mapa1, Map<String, Object> mapa2)
	   {
	     mapa1.putAll(mapa2);
	     return mapa1;
	   }
	
	//TODO contructor que se encarga de llenar los combos	
	public void llenarCombo(){
			Tipo= new ArrayList<SelectItem>();
				
			try{
				TipoDocumento = buscarPorCodigo();
				for(int i = 0;i<TipoDocumento.size();i++)
					Tipo.add(new SelectItem(TipoDocumento.get(i).getIdDocumento(), TipoDocumento.get(i).getDescripcion()));
					
			}catch (Exception e) {
				FacesMessage mensaje=null;
				mensaje = new FacesMessage(FacesMessage.SEVERITY_INFO,e.getMessage(),null);
				FacesContext.getCurrentInstance().addMessage(null, mensaje);
			}
			TipoEstados= new ArrayList<SelectItem>();
			TipoEstados.add(new SelectItem("AA", "TODOS"));
			TipoEstados.add(new SelectItem("NA", "NO AUTORIZADO"));
			TipoEstados.add(new SelectItem("AT", "AUTORIZADO"));
			TipoEstados.add(new SelectItem("RS", "RECIBIDO"));
			TipoEstados.add(new SelectItem("CT", "CONTIGENCIA"));
	}

	//TODO contructor de cargar combo 
	public List<FacTiposDocumento> buscarPorCodigo(){
				
			List<FacTiposDocumento> listaFacGeneral = null;
				
			try{
				listaFacGeneral = facDocumentoServicios.BuscarDatosdeTipoDocumento();           
				
			}catch (Exception e) {
				e.printStackTrace();
				FacesMessage mensaje=null;
				mensaje = new FacesMessage(FacesMessage.SEVERITY_INFO,e.getMessage(),null);
				FacesContext.getCurrentInstance().addMessage(null, mensaje);
			}
				return  listaFacGeneral;
	}
	
	public ArrayList<SelectItem> getTipo() {
		return Tipo;
	}

	public void setTipo(ArrayList<SelectItem> tipo) {
		Tipo = tipo;
	}

	public String getSeleccionTipo() {
		return SeleccionTipo;
	}

	public void setSeleccionTipo(String seleccionTipo) {
		SeleccionTipo = seleccionTipo;
	}

	public List<FacTiposDocumento> getTipoDocumento() {
		return TipoDocumento;
	}

	public void setTipoDocumento(List<FacTiposDocumento> tipoDocumento) {
		TipoDocumento = tipoDocumento;
	}

	public FacDocumentoServicios getFacDocumentoServicios() {
		return facDocumentoServicios;
	}

	public void setFacDocumentoServicios(FacDocumentoServicios facDocumentoServicios) {
		this.facDocumentoServicios = facDocumentoServicios;
	}

	public FacCabDocumento getDocumento() {
		return documento;
	}
	
	public LazyDataModel<FacCabDocumento> getAllDocumentos() {
		//ArrayList<String, String> filtros = new ArrayList<String, String>();
		filters.clear();
		Criterios.clear();
		if (ruc !=null){
			if (ruc.length()>=10){
				filters.put("\"identificacionComprador\"", ruc);
				Criterios.put("\"identificacionComprador\"", "=");
			}
		}
		if (razonSocial !=null){
			if (razonSocial.trim().length()>0){
				filters.put("\"razonSocialComprador\"", razonSocial);
				Criterios.put("\"razonSocialComprador\"", "like");
			}
		}		
		if (SeleccionTipo !=null){
			if (SeleccionTipo.trim().length()>0){
				filters.put("\"CodigoDocumento\"", SeleccionTipo);
				Criterios.put("CodigoDocumento", "=");
			}
		}
		if (numDocumento !=null){
			if (numDocumento.trim().length()>0){
				filters.put("\"CodEstablecimiento\"||'-'||\"CodPuntEmision\"||'-'||secuencial", numDocumento);
				//Criterios.put("\"CodEstablecimiento\"||\"CodPuntEmision\"||secuencial", "=");
				Criterios.put("\"CodEstablecimiento\"||'-'||\"CodPuntEmision\"||'-'||secuencial", "equal");
			}
		}
		if (SeleccionTipoEstado !=null){
			if (SeleccionTipoEstado.trim().length()>0){
				if (!SeleccionTipoEstado.equals("AA")){
				filters.put("\"ESTADO_TRANSACCION\"", SeleccionTipoEstado);
				Criterios.put("\"ESTADO_TRANSACCION\"", "=");
				}
			}
		}
		//String ls_dateFormat = "yyyyMMMdd";
		String ls_fechas = "";
		String fecha1 = "";
		String fecha2 = "";
		String ls_dateFormat = "yyyy.MM.dd";
		if (FechaInicio !=null){
			fecha1 = "to_date('"+ new java.text.SimpleDateFormat(ls_dateFormat).format(FechaInicio) + "','yyyy.MM.dd')";
			System.out.println(fecha1);
				//filters.put("\"fechaEmision\"", new java.text.SimpleDateFormat(ls_dateFormat).format(FechaInicio));
			filters.put("\"fechaEmision\"", fecha1);
			Criterios.put("\"fechaEmision\"", ">=");
		}
		if (FechaFinal !=null){
			fecha2 = "to_date('"+ new java.text.SimpleDateFormat(ls_dateFormat).format(FechaFinal) + "','yyyy.MM.dd')";						
			System.out.println(fecha2);
			//filters.put("\"fechaEmision\"", new java.text.SimpleDateFormat(ls_dateFormat).format(FechaFinal));
			filters.put("\"fechaEmision\"", fecha2);
			Criterios.put("\"fechaEmision\"", "<=");
		}
		if ((FechaFinal !=null)&&(FechaInicio !=null)){
			ls_fechas = fecha1+ "|"+fecha2;
			filters.put("\"fechaEmision\"", ls_fechas);
			Criterios.put("\"fechaEmision\"", "between");
		}
		
		if (documentos == null) {
			documentos = new CabDocumentoLazyList(filters, Criterios);
				
		}
		return documentos;
	}
	
	public void findDocumentos()
	{
		filters.clear();
		Criterios.clear();
		if (ruc !=null){
			if (ruc.length()>=10){
				filters.put("\"identificacionComprador\"", ruc);
				//Criterios.put("\"identificacionComprador\"", "like");
				Criterios.put("\"identificacionComprador\"", "=");
			}
		}
		if (razonSocial !=null){
			if (razonSocial.trim().length()>0){
				filters.put("\"razonSocialComprador\"", razonSocial);
				Criterios.put("\"razonSocialComprador\"", "like");
			}
		}
		System.out.println("seleccion de tipo "+ SeleccionTipo);
		if (SeleccionTipo !=null){
			if (SeleccionTipo.trim().length()>0){
				filters.put("\"CodigoDocumento\"", SeleccionTipo);
				Criterios.put("\"CodigoDocumento\"", "=");
			}
		}
		System.out.println("numDocumento..."+numDocumento);
		if (numDocumento !=null){
			if (numDocumento.trim().length()>0){
				filters.put("\"CodEstablecimiento\"||'-'||\"CodPuntEmision\"||'-'||secuencial", numDocumento);
				//Criterios.put("\"CodEstablecimiento\"||\"CodPuntEmision\"||secuencial", "=");
				Criterios.put("\"CodEstablecimiento\"||'-'||\"CodPuntEmision\"||'-'||secuencial", "equal");
			}
		}
		if (SeleccionTipoEstado !=null){
			if (SeleccionTipoEstado.trim().length()>0){
				if (!SeleccionTipoEstado.equals("AA")){
					filters.put("\"ESTADO_TRANSACCION\"", SeleccionTipoEstado);
					Criterios.put("\"ESTADO_TRANSACCION\"", "=");
				}
			}
		}
		
		//String ls_dateFormat = "yyyyMMMdd";
				String ls_fechas = "";
				String fecha1 = "";
				String fecha2 = "";
				String ls_dateFormat = "yyyy.MM.dd";
				if (FechaInicio !=null){
					fecha1 = "to_date('"+ new java.text.SimpleDateFormat(ls_dateFormat).format(FechaInicio) + "','yyyy.MM.dd')";
					System.out.println(fecha1);
						//filters.put("\"fechaEmision\"", new java.text.SimpleDateFormat(ls_dateFormat).format(FechaInicio));
					filters.put("\"fechaEmision\"", fecha1);
					Criterios.put("\"fechaEmision\"", ">=");
				}
				if (FechaFinal !=null){
					fecha2 = "to_date('"+ new java.text.SimpleDateFormat(ls_dateFormat).format(FechaFinal) + "','yyyy.MM.dd')";						
					System.out.println(fecha2);
					//filters.put("\"fechaEmision\"", new java.text.SimpleDateFormat(ls_dateFormat).format(FechaFinal));
					filters.put("\"fechaEmision\"", fecha2);
					Criterios.put("\"fechaEmision\"", "<=");
				}
				if ((FechaFinal !=null)&&(FechaInicio !=null)){
					ls_fechas = fecha1+ "|"+fecha2;
					filters.put("\"fechaEmision\"", ls_fechas);
					Criterios.put("\"fechaEmision\"", "between");
				}
		
		documentos = new CabDocumentoLazyList(filters, Criterios);
	}
	
	
	//TODO contructor que se encarga de enviar los correos
	public void EnviarCorreos(String Evento, FacCabDocumento seleccionDocumentos, int index) throws Exception
	{
		Boolean correosnoEnviados = false;
		String subjectMensaje = "";
		String namePdf="";
		claseEmail = new FacEnviarMail();
		// verifica si se a seleccionado un documento del registro
		if(documentos.getRowCount() == 0){
			mensajeAlerta("Mensaje del sistema","Debe seleccionar algun documento a adjuntar", "informacion");
			return;
		}
		// validando si se a ingresado el formato re los correos
		String [] listaCorreo = Correos.split(",");
		for (int i = 0; i < listaCorreo.length; i++){
			if(!listaCorreo[i].toString().trim().equals(""))
				if(!claseEmail.validar_correo(listaCorreo[i])){
					mensajeAlerta("Mensaje del sistema","El correo :" + listaCorreo[i] + " \n no se encuentra en el formato", "peligro");
					mensajeAlerta("Mensaje del sistema","Por favor corregir el correo", "peligro");
					return;
				}
		}
		FacEmpresa empresas = facDocumentoServicios.listadoEmpr(rucEmpresa);
		if(selectedOptionsEmail.length == 0){/// verifica si a seleccionado el check del archivo pdf
			mensajeAlerta("Mensaje del sistema","Por favor debe seleccionar que tipo de archivo desea adjuntar", "Informacion");
			return;
		}
		String contenidoMensaje = facGenSer.buscarDatosGeneralPadre("114").getDescripcion();
		String nombreEmpresaMesaje =  facGenSer.buscarDatosGeneralPadre("115").getDescripcion();
		String portal =  facGenSer.buscarDatosGeneralPadre("116").getDescripcion();
		StringBuffer body = new StringBuffer();
		contenidoMensaje = contenidoMensaje.replace("|NOMEMAIL|", nombreEmpresaMesaje).toString();
		contenidoMensaje = contenidoMensaje.replace("|DOCFECHAGENERA|", seleccionDocumentos.getFechaEmision().toString()).toString();
		contenidoMensaje = contenidoMensaje.replace("|FECHA|", (seleccionDocumentos.getFechaEmision().toString()));
		contenidoMensaje = contenidoMensaje.replace("|NODOCUMENTO|", (seleccionDocumentos.getId().getCodEstablecimiento()+"-"+seleccionDocumentos.getId().getCodPuntEmision()+"-"+seleccionDocumentos.getId().getSecuencial()));	
		contenidoMensaje = contenidoMensaje.replace("|HELPDESK|", "");
		//contenidoMensaje = contenidoMensaje.replace("|CLIENTE|", seleccionDocumentos.get);
		try{
			FacCliente facCli = facCliSer.buscaUsuarioEmpresa(seleccionDocumentos.getIdentificacionComprador(), rucEmpresa);
			if (facCli!=null)
				contenidoMensaje = contenidoMensaje.replace("|CODCLIENTE|", facCli.getCodCliente());
			else
				contenidoMensaje = contenidoMensaje.replace("|CODCLIENTE|", facCli.getCodCliente());
		}catch (Exception e){
			contenidoMensaje = contenidoMensaje.replace("|CODCLIENTE|", "");
		}
		contenidoMensaje = contenidoMensaje.replace("|PORTAL|", portal);
		String ls_tipoDocumento ="";				
		if (seleccionDocumentos.getId().getCodigoDocumento().equals("01"))
			ls_tipoDocumento ="Factura";
		if (seleccionDocumentos.getId().getCodigoDocumento().equals("04"))
			ls_tipoDocumento ="Nota de Credito";
		if (seleccionDocumentos.getId().getCodigoDocumento().equals("07"))
			ls_tipoDocumento ="Comprobante de Retencion";
		if (seleccionDocumentos.getId().getCodigoDocumento().equals("06"))
			ls_tipoDocumento ="Guia de Remisión";
		contenidoMensaje = contenidoMensaje.replace("|TIPODOCUMENTO|", ls_tipoDocumento);
		body.append(contenidoMensaje);
		String pdf = "",xml = "";
		for (int i = 0; i < selectedOptionsEmail.length; i++) {
			File ficheroXLS = new File("");
			File dirTemp = new File(System.getProperty("java.io.tmpdir"));
			if(!dirTemp.exists())
				dirTemp.mkdirs();
			if(selectedOptionsEmail[i].toString().trim().equals("PDF"))
			{
				try {	namePdf=pdf(seleccionDocu[index],"mail");	} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				pdf = dirTemp + dirTemp.separator+namePdf;
				ficheroXLS = new File(pdf);
				if(!ficheroXLS.isFile())
					pdf = "";
			}
			else
			{	
				//xml = dirTemp + dirTemp.separator+ seleccionDocumentos.getXMLARC();
				//ArchivoUtils.stringToArchivo(xml, seleccionDocumentos.getXmlAutorizacion());
				System.out.println("VPI - Ruta pdf :"+xml);
				ficheroXLS = new File(xml);
				if(!ficheroXLS.isFile())
					xml = "";
			}
		}
		String Adjuntararchivos[];
		//String NombreArchivoZepado = "C:/" + seleccionDocumentos.getFOLFAC() + ".zip";
		if(Evento.trim().equals("ZIP")){
			//NombreArchivoZepado.replace("/", "\"");
			//Zippear(new String[]{pdf, xml}, NombreArchivoZepado);
			//Adjuntararchivos = new String[]{NombreArchivoZepado};
		}else
		{
			Adjuntararchivos = new String[]{pdf, xml};
		}
		if(xml.toString().trim() != "" || pdf.toString().trim() != "")
		{
			subjectMensaje = facGenSer.buscarDatosGeneralPadre("113").getDescripcion();
			subjectMensaje = subjectMensaje.replace("|NOMEMAIL|", nombreEmpresaMesaje).toString();
			//subjectMensaje = subjectMensaje.replace("|NUMDOC|", seleccionDocumentos.getFOLFAC()).toString();
			System.out.println("Content Message :"+contenidoMensaje);
			System.out.println("Subject Message :"+subjectMensaje);
			/*
			claseEmail.enviar(empresas, //empresas
							  seleccionDocumentos.getEmail() + ((Correos.toString().trim().equals("")) ? Correos: "," + Correos), //toAddress
							  "",//ccAddress
							  "",//bccAddresss
							  subjectMensaje,//subject
							  //"Documento electrónico No:" + seleccionDocumentos.getFOLFAC() + " de Cima IT",//subject
							  true, //xisHTMLFormat
							  body, //body
							  false,//debug
							  Adjuntararchivos////Adjuntararchivos
							  );*/
		
			
		}else
			correosnoEnviados = true;
		if(Evento.trim().equals("ZIP")){
			/*File da = new File(NombreArchivoZepado);
			da.delete();
			*/
		}
		//if(correosnoEnviados)
		//mensajeAlerta("Mensaje del sistema","Algunos documentos no se encuentra el archivo, y no se realizo el envio del correo", "Informacion");
				
	}
	
		//TODO Zipear archivos seleccionado
		private void Zippear(String[] pFile, String pZipFile) throws Exception
		{
			// objetos en memoria
			FileInputStream fis = null;
			FileOutputStream fos = null;
			ZipOutputStream zipos = null;
			// fichero contenedor del zip
			if(pFile[0] != "" || pFile[1] != ""){
				fos =  new FileOutputStream(pZipFile);
				// fichero comprimido
				zipos = new ZipOutputStream(fos);
			}
			// buffer
			byte[] buffer = new byte[1024];
			try {
				// fichero a comprimir
				for (int i = 0; i < pFile.length; i++) {
					if(pFile[i] != ""){
						fis = new FileInputStream(pFile[i]);
					
						ZipEntry zipEntry = new ZipEntry(new File(pFile[i]).getName());
						zipos.putNextEntry(zipEntry);
						int len = 0;
						// zippear
						while ((len = fis.read(buffer, 0, 1024)) != -1)
							zipos.write(buffer, 0, len);
						// volcar la memoria al disco
						zipos.flush();
					}
				}
			} catch (Exception e) {
				throw e;
			} finally {
				// cerramos los files
				if(zipos != null && fis != null && fos != null){
				zipos.close();
				fis.close();
				fos.close();
				}
			} // end try
		} // end Zippear
	//TODO contructor de mensaje de alerta
	 private void mensajeAlerta(String mensajeVentana, String mensajeDetalle, String tipo) {
		 FacesContext context = FacesContext.getCurrentInstance();            
	      context.addMessage(null, new FacesMessage((tipo.toString().trim().equals("alerta") ? FacesMessage.SEVERITY_ERROR : tipo.toString().trim().equals("peligro") ? FacesMessage.SEVERITY_WARN : FacesMessage.SEVERITY_INFO),mensajeVentana, mensajeDetalle)); 
     }
							
	public FacCabDocumento getPlayer() {
		if(documento == null){
			documento = new FacCabDocumento();
		}
		
		return documento;
	}

	public void setDocumento(FacCabDocumento player) {
		this.documento = player;
	}

	public LazyDataModel<FacCabDocumento> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(LazyDataModel<FacCabDocumento> documentos) {
		this.documentos = documentos;
	}

	public String getRuc() {
		return ruc;
	}

	public void setRuc(String ruc) {
		this.ruc = ruc;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public HashMap<String, String> getFilters() {
		return filters;
	}

	public void setFilters(HashMap<String, String> filters) {
		this.filters = filters;
	}

	public String getNumDocumento() {
		return numDocumento;
	}

	public void setNumDocumento(String numDocumento) {
		this.numDocumento = numDocumento;
	}

	public ArrayList<SelectItem> getTipoEstados() {
		return TipoEstados;
	}

	public void setTipoEstados(ArrayList<SelectItem> tipoEstados) {
		TipoEstados = tipoEstados;
	}

	public String getSeleccionTipoEstado() {
		return SeleccionTipoEstado;
	}

	public void setSeleccionTipoEstado(String seleccionTipoEstado) {
		SeleccionTipoEstado = seleccionTipoEstado;
	}

	public Date getFechaInicio() {
		return FechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		FechaInicio = fechaInicio;
	}

	public Date getFechaFinal() {
		return FechaFinal;
	}

	public void setFechaFinal(Date fechaFinal) {
		FechaFinal = fechaFinal;
	}

	public HashMap<String, String> getCriterios() {
		return Criterios;
	}

	public void setCriterios(HashMap<String, String> criterios) {
		Criterios = criterios;
	}

	public String[] getSelectedOptionsEmail() {
		return selectedOptionsEmail;
	}

	public void setSelectedOptionsEmail(String[] selectedOptionsEmail) {
		this.selectedOptionsEmail = selectedOptionsEmail;
	}

	public List<FacCabDocumento> getListAllDocumentos() {
		return listAllDocumentos;
	}

	public void setListAllDocumentos(List<FacCabDocumento> listAllDocumentos) {
		this.listAllDocumentos = listAllDocumentos;
	}

	public FacCabDocumento getSeleccionDocumentos() {
		return seleccionDocumentos;
	}

	public void setSeleccionDocumentos(FacCabDocumento seleccionDocumentos) {
		this.seleccionDocumentos = seleccionDocumentos;
	}

	public FacCabDocumento[] getSeleccionDocu() {
		return seleccionDocu;
	}

	public void setSeleccionDocu(FacCabDocumento[] seleccionDocu) {
		this.seleccionDocu = seleccionDocu;
	}
	
	
}