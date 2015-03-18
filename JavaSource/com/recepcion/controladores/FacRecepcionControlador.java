package com.recepcion.controladores;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;





import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;

import org.primefaces.context.RequestContext;

import javax.ejb.EJB;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import com.documentos.entidades.FacCabDocumento;
import com.documentos.entidades.FacDetAdicional;
import com.documentos.entidades.FacDetDocumento;
import com.documentos.entidades.FacDetRetencione;
import com.documentos.entidades.FacEmpresa;
import com.general.controladores.FacConsultaDocumentoSeleccControladores;
import com.general.controladores.FacEnviarMail;
import com.general.entidades.FacCliente;
import com.general.entidades.FacEstablecimiento;
import com.general.entidades.FacGeneral;
import com.general.entidades.FacProducto;
import com.general.entidades.FacTiposDocumento;
import com.general.entidades.facDetalleDocumentoEntidad;
import com.general.servicios.FacClienteServicios;
import com.general.servicios.FacDocumentoServicios;
import com.general.servicios.FacEmpresaEmisoraServicios;
import com.general.servicios.FacGeneralServicio;
import com.general.util.DetallesAdicionalesReporte;
import com.general.util.InformacionAdicional;
import com.general.util.ReporteConsultaExcel;
import com.general.util.Util;
import com.recepcion.entidades.FacDocRecepcion;
import com.recepcion.servicios.FacRecepcionServicios;


@ViewScoped
@ManagedBean
@SessionScoped
public class FacRecepcionControlador {

	@EJB
	private FacDocumentoServicios facDocumentoServicios; // variable en la cual llama la clase de servicio
	
	@EJB
	private FacRecepcionServicios facRecepcionServicios; // variable en la cual llama la clase de servicio
	
	@EJB
	private FacGeneralServicio facGenSer;
	@EJB
	private FacClienteServicios facCliSer;
	@EJB
	private FacEmpresaEmisoraServicios facServEmpresa;
	//buscaUsuarioEmpresa(String Usuario, String rucEmpresa)
	// metodod de variable del web
	private String ruc; // variable que recoge el ruc de la consulta
	private String RazonSocial; // variable que recoge la razon social de la consulta
	private String SeleccionTipo; // variable que recoge el item selecciondo del tipo de documento
	private String SeleccionTipoEstado; // variable que recoge el item selecciondo del tipo de Estado
	private Date FechaInicio;// dato que se encarga de recoger la fecha de inicio
	private Date FechaFinal;// dato que se encarga de recoger la fecha de final
	private String Correos;// dato de correos que se va a enviar de las facuras
	private String numDocumento;
	
	// metodos de tipo de documentos
	private FacTiposDocumento facTiposDocumento;
	private ArrayList<SelectItem> Tipo; // variable que recoge los tipos de documento	
	private ArrayList<SelectItem> TipoEstados; // variable que recoge los tipos de estados de los documentos
	private List<FacTiposDocumento> TipoDocumento; // variable que recoge los tipos de documento
		
	// metodo del detalle de documento
	private List<facDetalleDocumentoEntidad> DetalleDocumento; // registro de consulta de documentos
	private facDetalleDocumentoEntidad [] selectedfacDetalleDocumentoEntidad; // variable que me permite poder seleccionar toda las facturas
	private facDetalleDocumentoEntidad valorDetalleDocumento;// variable que recoge el dato seleccionado
	private String rucEmpresa;
	protected String loginUsuario;
	private String [] selectedOptionsEmail; // variable que recoge si desea enviar correo con xml o pdf  
	
	private FacEnviarMail claseEmail;// variable que contiene la clase de email
	
	private FacConsultaDocumentoSeleccControladores detalle_documentos_;
	
	private String dialect=null;
	private String sistemaOperativo=null;
	private String pathTemp= null;
	private String pathSubReport= null;
	private HashMap<String, String> pathReports = null;
	private List<FacDocRecepcion> docRecepcion = null;
	private FacDocRecepcion [] selectedfacDocRecepcionEntidad;
	private FacConsultaDocRecepcion docRecepcionControlador;
	

	//TODO contructor que se encarga de limpiar el contenido de los campos
	public void Actualizar_paguina() {
		CargarDatos();
	}

	//TODO contructor general que se encarga de cargar los datos primordiales
	public void CargarDatos(){
		try{
			FacesContext context = FacesContext.getCurrentInstance();
			HttpSession sesion = (HttpSession)context.getExternalContext().getSession(true);
			if(sesion.getAttribute("Ruc_Empresa") != null)
			{
				rucEmpresa = sesion.getAttribute("Ruc_Empresa").toString();
				loginUsuario = sesion.getAttribute("id_usuario").toString();
			}
			else{
				ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
				String ctxPath = ((ServletContext) ctx.getContext()).getContextPath();
				ctx.redirect(ctxPath + "/paginas/Administrador/Cuenta/Login.jsf");
			}
				System.out.println("Ruc::"+rucEmpresa);
			
		
			//System.out.println("Prueba de Errores JZU");
			//Dialect parametrizado Default  idGeneral -> 100
			FacGeneral facGen = facGenSer.buscarDatosGeneralPrimerHijo("100");
			if (facGen !=null)
				dialect=facGen.getDescripcion().trim();
			else{
				String msg = "Configuracion General::FacGeneral::La carpeta de temporales no esta configurada::idGeneral->100";
				FacesMessage mensaje=null;				
				mensaje = new FacesMessage(FacesMessage.SEVERITY_INFO,msg,null);
				FacesContext.getCurrentInstance().addMessage(null, mensaje);
			}	
			//SistemaOperativo por Default busca el del sistema Operativo Real.
			facGen = facGenSer.buscarDatosGeneralPadre("100");
			if (facGen !=null)
				sistemaOperativo=facGen.getDescripcion();
			else{
				sistemaOperativo=System.getProperty("os.name").toUpperCase().toString();
				if ((sistemaOperativo.indexOf("WIN")>0)||(System.getProperty("os.name").toUpperCase().indexOf("MAC")>0)){
					sistemaOperativo = "Windows";
				}
				if (sistemaOperativo.indexOf("LINUX")>0){
					sistemaOperativo = "Linux";
				}	
			}
			
			
			
			//Llenar estados para el filtro de busquedas de la pag:
			List<FacGeneral> facGenList = new ArrayList<FacGeneral>();
			facGenList = facGenSer.buscarDatosGeneralHijo("118",false);
			TipoEstados= new ArrayList<SelectItem>();
			if (facGenList.size()>0){
				for(int i=0;i<facGenList.size();i++)
					//Obtener los estados PERMANENTES (valor = P)
					if (facGenList.get(i).getValor().equalsIgnoreCase("P")){
						TipoEstados.add(new SelectItem(facGenList.get(i).getCodUnico(), facGenList.get(i).getDescripcion()));
					}
									
			}else{
				TipoEstados.add(new SelectItem("", "NO HAY DATOS"));
			}
			
			
			setCorreos("");
			setRazonSocial("");
			setRuc("");
			setSeleccionTipo("");
			FechaFinal = new Date();
			FechaInicio = new Date();
			facTiposDocumento = new FacTiposDocumento();
			valorDetalleDocumento = new facDetalleDocumentoEntidad();
			selectedfacDetalleDocumentoEntidad = null;
			llenarCombo();
			//llenarDetalleDocumento();
		}catch (Exception e) {
			FacesMessage mensaje=null;
			mensaje = new FacesMessage(FacesMessage.SEVERITY_INFO,e.getMessage(),null);
			FacesContext.getCurrentInstance().addMessage(null, mensaje);
			e.printStackTrace();
		}
	}
		
	//TODO contructor que se llena el detalle de los documentos
	public void llenarDetalleDocumento(){
		try{
			String ls_dateFormat ="";
			List<facDetalleDocumentoEntidad> DetalleDocumento2; // registro de consulta de documentos
			List<FacCabDocumento> documento_detalles;//registro de detalle de documentos
			List<FacTiposDocumento> tipoDocumentos;// registro de tipos de documento
			List<FacEmpresa> detalleEmpresas; // registro de empresas
			DetalleDocumento = new ArrayList<facDetalleDocumentoEntidad>(); // inicializando DetalleDocumento
			detalle_documentos_ = new FacConsultaDocumentoSeleccControladores();
			
			if (dialect.equals("PostgreSQL"))
				ls_dateFormat = "yyyy-MM-dd";
			if (dialect.equals("SQLServer"))
				ls_dateFormat = "yyyyMMMdd";
			if (numDocumento!=null)
			if (numDocumento.trim().equals("")) numDocumento = null;
			
				
			if(FechaFinal != null && FechaInicio!= null){
				long diff = FechaFinal.getTime()-FechaInicio.getTime();
				if(diff/(24 * 60 * 60 * 1000)>=60){
					FacesContext context1 = FacesContext.getCurrentInstance();
			        context1.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Rango de fechas",  "El rango de fechas selesccionados no debe ser mayor a 60 dias") );
					return;
				}
			}else{
				FacesMessage mensaje=null;
				mensaje = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Debe seleccionar un rango de fechas","Error de filtros");
				FacesContext.getCurrentInstance().addMessage("Error de filtros", mensaje);
				return;
			}
			docRecepcion = BuscarDatosdeDetalleDocumento(new Object[]{ruc,RazonSocial,SeleccionTipo, 
																			((FechaInicio == null)? FechaInicio: (new java.text.SimpleDateFormat(ls_dateFormat).format(FechaInicio))),
																			((FechaFinal == null)? FechaFinal: (new java.text.SimpleDateFormat(ls_dateFormat).format(FechaFinal))),
																			rucEmpresa,
																			dialect,
																			SeleccionTipoEstado,
																			numDocumento});
			if (docRecepcion!=null){
					docRecepcionControlador = new FacConsultaDocRecepcion(docRecepcion);
			}
		
		}catch (Exception e) {
			FacesMessage mensaje=null;
			mensaje = new FacesMessage(FacesMessage.SEVERITY_INFO,e.getMessage(),null);
			FacesContext.getCurrentInstance().addMessage(null, mensaje);
		}
	}
	
	public void actualizarEstados(String estado) throws Exception
	{
		if (selectedfacDocRecepcionEntidad!=null){
			if (selectedfacDocRecepcionEntidad.length==0){
				//System.out.println("No hay Datos Seleccionados.");
				FacesMessage mensaje=null;
				mensaje = new FacesMessage(FacesMessage.SEVERITY_WARN,"Reprocesamiento de comprobantes","Favor seleccione por lo menos un documento.");
				FacesContext.getCurrentInstance().addMessage(null, mensaje);				
			}else{
					
				for(FacDocRecepcion seleccionDocumento  : selectedfacDocRecepcionEntidad ){
						facRecepcionServicios.actualizarEstadoDocumento(seleccionDocumento, "RP",rucEmpresa);
					}
				//CargarDatos();
				llenarDetalleDocumento();
			}
		}else{
				//System.out.println("No hay Datos Seleccionados is Null.");
				FacesMessage mensaje=null;
				mensaje = new FacesMessage(FacesMessage.SEVERITY_INFO,"Favor Seleccione Por lo menos un Documento.",null);
				FacesContext.getCurrentInstance().addMessage(null, mensaje);
	
		}
		
						
	}
	
	public void actualizarEstado(String estado,FacDocRecepcion documento) throws Exception
	{
		if (documento!=null){
						facRecepcionServicios.actualizarEstadoDocumento(documento,estado,rucEmpresa);
				llenarDetalleDocumento();
		}
		
						
	}
	
	
	//TODO mostrando filtrtados de documentos para el display del usuario
	private List<facDetalleDocumentoEntidad> MotrandoDocumentoFiltrados(List<FacCabDocumento> documento_detalles,List<FacTiposDocumento> tipoDocumentos,List<FacEmpresa> detalleEmpresas) {
		List<facDetalleDocumentoEntidad> detalledocumento = null;
			
		try{			
			detalledocumento = facDocumentoServicios.MotrandoDocumentoFiltrados(documento_detalles,tipoDocumentos,detalleEmpresas);
		}catch (Exception e) {
			FacesMessage mensaje=null;
			mensaje = new FacesMessage(FacesMessage.SEVERITY_INFO,e.getMessage(),null);
			FacesContext.getCurrentInstance().addMessage(null, mensaje);
		}
		return  detalledocumento;
	}
	
	//TODO listado de detalle de documento
	private List<FacDocRecepcion> BuscarDatosdeDetalleDocumento(Object [] obj){
		List<FacDocRecepcion> detalledocumento = null;
			
		try{			

			detalledocumento = facRecepcionServicios.MostrandoDetalleDocumento(obj);
		}catch (Exception e) {
			FacesMessage mensaje=null;
			mensaje = new FacesMessage(FacesMessage.SEVERITY_INFO,e.getMessage(),null);
			FacesContext.getCurrentInstance().addMessage(null, mensaje);
		}
		return  detalledocumento;
	}

	//TODO contructr de lista de las empresas
	public List<FacEmpresa> fac_empresas() {
		List<FacEmpresa> detalledocumento = null;
			
		try{			
			detalledocumento = facDocumentoServicios.listadoEmpresas(rucEmpresa);        
	
		}catch (Exception e) {
			FacesMessage mensaje=null;
			mensaje = new FacesMessage(FacesMessage.SEVERITY_INFO,e.getMessage(),null);
			FacesContext.getCurrentInstance().addMessage(null, mensaje);
		}
		return  detalledocumento;
	}
	
	//TODO contructor que se encarga de llenar los combox
	public void llenarCombo(){
		Tipo= new ArrayList<SelectItem>();
			
		try{
			TipoDocumento = buscarPorCodigo();
				
			if(TipoDocumento.isEmpty()){
				facTiposDocumento.setIdDocumento(null);
				facTiposDocumento.setDescripcion("NO HAY DATOS");
				facTiposDocumento.setFormatoTexto("");
				facTiposDocumento.setFormatoXML("");
				facTiposDocumento.setIsActive("Y");
				TipoDocumento.add(facTiposDocumento);
			}
			for(int i = 0;i<TipoDocumento.size();i++)
				Tipo.add(new SelectItem(TipoDocumento.get(i).getIdDocumento(), TipoDocumento.get(i).getDescripcion()));
				
		}catch (Exception e) {
			FacesMessage mensaje=null;
			mensaje = new FacesMessage(FacesMessage.SEVERITY_INFO,e.getMessage(),null);
			FacesContext.getCurrentInstance().addMessage(null, mensaje);
		}
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
	

	public List<String> complete_RucComprador(String parametro_ruc) throws Exception{
		List<String> resultado = new ArrayList<String>();
		resultado = facDocumentoServicios.BuscarfitroEmpresaDocumentos(parametro_ruc, rucEmpresa,valorDetalleDocumento.getAmbiente());		
		return resultado;
	}
	
	//TODO Zipear archivos seleccionado
	private void Zippear(String[] pFile, String pZipFile) throws Exception {
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
			
	 
	 
	public List<FacDocRecepcion> getDocRecepcion() {
		return docRecepcion;
	}

	public void setDocRecepcion(List<FacDocRecepcion> docRecepcion) {
		this.docRecepcion = docRecepcion;
	}

	public String getRuc() {
		return ruc;
	}

	public void setRuc(String ruc) {
		this.ruc = ruc;
	}

	public String getRazonSocial() {
		return RazonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		RazonSocial = razonSocial;
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

	public String getCorreos() {
		return Correos;
	}

	public void setCorreos(String correos) {
		Correos = correos;
	}

	public FacDocumentoServicios getFacDocumentoServicios() {
		return facDocumentoServicios;
	}
	
	public void setFacDocumentoServicios(FacDocumentoServicios facDocumentoServicios) {
		this.facDocumentoServicios = facDocumentoServicios;
	}

	public FacTiposDocumento getFacTiposDocumento() {
		return facTiposDocumento;
	}

	public void setFacTiposDocumento(FacTiposDocumento facTiposDocumento) {
		this.facTiposDocumento = facTiposDocumento;
	}
	
	public ArrayList<SelectItem> getTipo() {
		return Tipo;
	}

	public void setTipo(ArrayList<SelectItem> tipo) {
		Tipo = tipo;
	}

	public List<FacTiposDocumento> getTipoDocumento() {
		return TipoDocumento;
	}

	public void setTipoDocumento(List<FacTiposDocumento> tipoDocumento) {
		TipoDocumento = tipoDocumento;
	}

	public String getSeleccionTipo() {
		return SeleccionTipo;
	}

	public void setSeleccionTipo(String seleccionTipo) {
		SeleccionTipo = seleccionTipo;
	}

	public List<facDetalleDocumentoEntidad> getDetalleDocumento() {
		return DetalleDocumento;
	}

	public void setDetalleDocumento(List<facDetalleDocumentoEntidad> detalleDocumento) {
		DetalleDocumento = detalleDocumento;
	}

	public facDetalleDocumentoEntidad [] getSelectedfacDetalleDocumentoEntidad() {
		return selectedfacDetalleDocumentoEntidad;
	}

	public void setSelectedfacDetalleDocumentoEntidad(
			facDetalleDocumentoEntidad [] selectedfacDetalleDocumentoEntidad) {
		this.selectedfacDetalleDocumentoEntidad = selectedfacDetalleDocumentoEntidad;
	}

	public facDetalleDocumentoEntidad getValorDetalleDocumento() {
		return valorDetalleDocumento;
	}

	public void setValorDetalleDocumento(facDetalleDocumentoEntidad valorDetalleDocumento) {
		this.valorDetalleDocumento = valorDetalleDocumento;
	}

	public String [] getSelectedOptionsEmail() {
		return selectedOptionsEmail;
	}

	public void setSelectedOptionsEmail(String [] selectedOptionsEmail) {
		this.selectedOptionsEmail = selectedOptionsEmail;
	}
	
	public FacConsultaDocumentoSeleccControladores getDetalle_documentos_prueba() {
		return detalle_documentos_;
	}

	public void setDetalle_documentos_prueba(
			FacConsultaDocumentoSeleccControladores detalle_documentos_prueba) {
		this.detalle_documentos_ = detalle_documentos_prueba;
	}

	public String getDialect() {
		return dialect;
	}

	public void setDialect(String dialect) {
		this.dialect = dialect;
	}

	
	public void pdf(FacDocRecepcion documento) throws Throwable{ 
		File dirTemp = new File(System.getProperty("java.io.tmpdir"));
		if(!dirTemp.exists())
			dirTemp.mkdirs();
		File fileDocumento = new	File(documento.getId().getNombreArchivo().replaceAll(".xml", ".pdf"));
		File dirFile = new File(dirTemp + dirTemp.separator+fileDocumento.getName());

		if(dirFile.exists())
			dirFile.exists();
		
		Util.writeBytesToFile(dirFile, documento.getPdfImag());

		String Documento = dirFile.getAbsolutePath();
		Documento.replace("/", "\"");

		File ficheroPDF = new File(Documento);
		FacesContext ctx = FacesContext.getCurrentInstance();
		FileInputStream fis = new FileInputStream(ficheroPDF);
		byte[] bytes = new byte[1000];
		int read = 0;

		if (!ctx.getResponseComplete()) {
			String fileName = ficheroPDF.getName();
			String contentType = "application/"
					+ (ficheroPDF.getAbsolutePath().contains(".xml") ? "xml" : "pdf");
			HttpServletResponse response = (HttpServletResponse) ctx
					.getExternalContext().getResponse();
			response.setContentType(contentType);
			response.setHeader("Content-Disposition", "attachment;filename="
					+ fileName);
			ServletOutputStream outxml = response.getOutputStream();

			while ((read = fis.read(bytes)) != -1) {
				outxml.write(bytes, 0, read);
			}

			outxml.flush();
			outxml.close();
			ctx.responseComplete();
		}
    }
	
	
	public void xml(FacDocRecepcion documento) throws Throwable {
		File dirTemp = new File(System.getProperty("java.io.tmpdir"));
		if (!dirTemp.exists())
			dirTemp.mkdirs();
		File fileDocumento = new File(documento.getId().getNombreArchivo());
		try {
			File dirFile = new File(dirTemp + dirTemp.separator
					+ fileDocumento.getName());
			if (dirFile.exists())
				dirFile.exists();

		} catch (Exception e) {
			e.printStackTrace();
		}
		String nameDocument = (dirTemp + dirTemp.separator + fileDocumento
				.getName());
		System.out.println("File Xml::" + nameDocument);

		stringToArchivo(nameDocument, documento.getXmlDoc());

		String Documento = nameDocument;
		Documento.replace("/", "\"");

		File ficheroXLS = new File(Documento);
		FacesContext ctx = FacesContext.getCurrentInstance();
		FileInputStream fis = new FileInputStream(ficheroXLS);
		byte[] bytes = new byte[1000];
		int read = 0;

		if (!ctx.getResponseComplete()) {
			String fileName = ficheroXLS.getName();
			String contentType = "application/"
					+ (nameDocument.contains(".pdf") ? "pdf" : "xml");
			HttpServletResponse response = (HttpServletResponse) ctx
					.getExternalContext().getResponse();
			response.setContentType(contentType);
			response.setHeader("Content-Disposition", "attachment;filename="
					+ fileName);
			ServletOutputStream outxml = response.getOutputStream();

			while ((read = fis.read(bytes)) != -1) {
				outxml.write(bytes, 0, read);
			}

			outxml.flush();
			outxml.close();
			ctx.responseComplete();
		}
	}
	
	
	
	public static File stringToArchivo(String rutaArchivo, String contenidoArchivo)
	  {
	    FileOutputStream fos = null;
	    File archivoCreado = null;
	    try
	    {
	    	System.out.println("error::"+rutaArchivo);
	      fos = new FileOutputStream(rutaArchivo);
	      OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
	      for (int i = 0; i < contenidoArchivo.length(); i++) {
	        out.write(contenidoArchivo.charAt(i));
	      }
	      out.close();

	      archivoCreado = new File(rutaArchivo);
	    }
	    catch (Exception ex)
	    {
	      int i;
	      System.out.println(ex.getMessage());
	      return null;
	    } finally {
	      try {
	        if (fos != null)
	          fos.close();
	      }
	      catch (Exception ex) {
	    	  System.out.println(ex.getMessage());
	      }
	    }
	    return archivoCreado;
	  }
	
	
	
	
	
	private Map<String, Object> obtenerParametrosInfoTriobutaria(facDetalleDocumentoEntidad valorDetalleDocumento)
	{
	     Map param = new HashMap();
	 
	     FacCabDocumento cabDoc = new FacCabDocumento();
	     FacEmpresa empresa = new FacEmpresa();
	     FacEstablecimiento establecimiento = new FacEstablecimiento();
	     empresa=facServEmpresa.verificarRuc(rucEmpresa);
	     try {
	       cabDoc = facDocumentoServicios.buscarCabDocumentoPorPk(rucEmpresa,valorDetalleDocumento.getCodEstablecimiento(),
																		 	 valorDetalleDocumento.getCodPuntoEmision(), 
																	 		 valorDetalleDocumento.getSecuencial(),
																			 valorDetalleDocumento.getCodigoDocumento(),
																			 valorDetalleDocumento.getAmbiente());
	       if (cabDoc != null) {
	         empresa = facDocumentoServicios.listadoEmpr(rucEmpresa);
	         establecimiento = facDocumentoServicios.buscarCodEstablecimiento(rucEmpresa,valorDetalleDocumento.getCodEstablecimiento());
	         param.put("RUC", rucEmpresa);
	         param.put("CLAVE_ACC", (cabDoc.getClaveAcceso().trim().equals("")) || (cabDoc.getClaveAcceso() == null) ? "SIN CLAVE" : cabDoc.getClaveAcceso());
	         param.put("RAZON_SOCIAL", empresa.getRazonSocial());
	         param.put("NOM_COMERCIAL", empresa.getRazonComercial());
	         param.put("DIR_MATRIZ", empresa.getDireccionMatriz());
	         param.put("SUBREPORT_DIR", pathSubReport);
	         param.put("TIPO_EMISION", cabDoc.getTipoEmision().trim().equals("1") ? "NORMAL" : "CONTINGENCIA");
	         param.put("NUM_AUT", (cabDoc.getAutorizacion() == null) || (cabDoc.getAutorizacion().equals("")) ? "" : cabDoc.getAutorizacion());
	         param.put("FECHA_AUT", cabDoc.getFechaAutorizado() == null ? "" : cabDoc.getFechaAutorizado());
	         param.put("NUM_FACT", cabDoc.getId().getCodEstablecimiento() + "-" + cabDoc.getId().getCodPuntEmision() + "-" + cabDoc.getId().getSecuencial());
	         if (cabDoc.getId().getCodigoDocumento().equals("04")){
	        	 param.put("NUM_DOC_MODIFICADO", (cabDoc.getNumDocModificado() == null) || (cabDoc.getNumDocModificado().equals("")) ? "" : cabDoc.getNumDocModificado());
	        	 param.put("FECHA_EMISION_DOC_SUSTENTO", cabDoc.getFechaEmisionDocSustento() == null ? "" : cabDoc.getFechaEmisionDocSustento());
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
	         
	       //VPI se agregan campos para reporte
	         //param.put("TELEFONO_LOCAL",establecimiento.getTelefono());
	         param.put("CORREO_RETENCION", establecimiento.getCorreo());
	         
	         
	       }
	     }
	     catch (Exception e) { e.printStackTrace(); }
	 
	     return param;
	   }
	
	private Map<String, Object> obtenerInfoFactura(List<FacDetAdicional> lstFactDetAdictDocumento,facDetalleDocumentoEntidad valorDetalleDocumento)
	   {
	     Map param = new HashMap();
	 
	     FacCabDocumento cabDoc = new FacCabDocumento();
	     try {
	       cabDoc = facDocumentoServicios.buscarCabDocumentoPorPk(rucEmpresa,valorDetalleDocumento.getCodEstablecimiento(),
																 	 valorDetalleDocumento.getCodPuntoEmision(), 
															 		 valorDetalleDocumento.getSecuencial(),
																	 valorDetalleDocumento.getCodigoDocumento(),
																	 valorDetalleDocumento.getAmbiente());
	       if (cabDoc != null) {
	         param.put("RS_COMPRADOR", cabDoc.getRazonSocialComprador());
	         param.put("RUC_COMPRADOR", cabDoc.getIdentificacionComprador());
	         SimpleDateFormat dateFormat = 
	     	            new SimpleDateFormat("dd/MM/yyyy");
		         param.put("FECHA_EMISION", dateFormat.format(cabDoc.getFechaEmision()));
	         param.put("GUIA", cabDoc.getGuiaRemision());
	         
	         DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
	         otherSymbols.setDecimalSeparator('.');
	         otherSymbols.setGroupingSeparator(','); 
	         DecimalFormat df = new DecimalFormat("###,##0.00",otherSymbols);
	         if (valorDetalleDocumento.getCodigoDocumento().equals("01")){
	        	 //double total = Double.valueOf(cabDoc.getImporteTotal())+cabDoc.getSubtotal12()+cabDoc.getSubtotalNoIva()+cabDoc.getSubtotal0()+cabDoc.getIva12()+cabDoc.getTotalvalorICE();
	        	 ///VPI se comenta xq se duplica total
	        	 double total = cabDoc.getImporteTotal();
	        	 param.put("VALOR_TOTAL", df.format(total));
	         }
	        	
	         if (valorDetalleDocumento.getCodigoDocumento().equals("04")){
	        	 //double total = cabDoc.getSubtotal12()+cabDoc.getSubtotalNoIva()+cabDoc.getSubtotal0()+cabDoc.getIva12()+cabDoc.getTotalvalorICE();
	        	///VPI se comenta xq se duplica total
	        	 double total = cabDoc.getImporteTotal();
	        	 param.put("VALOR_TOTAL", df.format(total));
	         }
	         if (valorDetalleDocumento.getCodigoDocumento().equals("07")){
	        	 dateFormat = 
	        	            new SimpleDateFormat("MM/yyyy");
	        	 param.put("EJERCICIO_FISCAL",dateFormat.format(cabDoc.getFechaEmision()));
	         }
	         param.put("IVA", df.format(Double.valueOf(cabDoc.getIva12())));
	         param.put("IVA_0", df.format(Double.valueOf(cabDoc.getSubtotal0())));
	         param.put("IVA_12", df.format(Double.valueOf(cabDoc.getSubtotal12())));
	         param.put("ICE", df.format(Double.valueOf(cabDoc.getTotalvalorICE())));
	         param.put("NO_OBJETO_IVA", df.format(Double.valueOf(cabDoc.getSubtotalNoIva())));
	         param.put("SUBTOTAL_SINIMP", df.format(Double.valueOf(cabDoc.getTotalSinImpuesto()) + (cabDoc.getSubtotal0()>=0? Double.valueOf(cabDoc.getSubtotal0()):0)));
	         param.put("SUBTOTAL", df.format(Double.valueOf(cabDoc.getTotalSinImpuesto())));
	         param.put("PROPINA", df.format(Double.valueOf(cabDoc.getPropina())));
	         param.put("TOTAL_DESCUENTO", df.format(Double.valueOf(cabDoc.getTotalDescuento())));
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

	public String getSistemaOperativo() {
		return sistemaOperativo;
	}

	public void setSistemaOperativo(String sistemaOperativo) {
		this.sistemaOperativo = sistemaOperativo;
	}

	public String getPathTemp() {
		return pathTemp;
	}

	public void setPathTemp(String pathTemp) {
		this.pathTemp = pathTemp;
	}

	public String getPathSubReport() {
		return pathSubReport;
	}

	public void setPathSubReport(String pathSubReport) {
		this.pathSubReport = pathSubReport;
	}

	public HashMap<String, String> getPathReports() {
		return pathReports;
	}

	public void setPathReports(HashMap<String, String> pathReports) {
		this.pathReports = pathReports;
	}

	public String getSeleccionTipoEstado() {
		return SeleccionTipoEstado;
	}

	public void setSeleccionTipoEstado(String seleccionTipoEstado) {
		SeleccionTipoEstado = seleccionTipoEstado;
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


	public FacConsultaDocRecepcion getDocRecepcionControlador() {
		return docRecepcionControlador;
	}

	public void setDocRecepcionControlador(
			FacConsultaDocRecepcion docRecepcionControlador) {
		this.docRecepcionControlador = docRecepcionControlador;
	}

	public FacDocRecepcion[] getSelectedfacDocRecepcionEntidad() {
		return selectedfacDocRecepcionEntidad;
	}

	public void setSelectedfacDocRecepcionEntidad(
			FacDocRecepcion[] selectedfacDocRecepcionEntidad) {
		this.selectedfacDocRecepcionEntidad = selectedfacDocRecepcionEntidad;
	}
	
	



	
	
	
}
