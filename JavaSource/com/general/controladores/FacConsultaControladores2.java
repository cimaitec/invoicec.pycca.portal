package com.general.controladores;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
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

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import com.documentos.entidades.FacCabDocumento;
import com.documentos.entidades.FacDetAdicional;
import com.documentos.entidades.FacDetDocumento;
import com.documentos.entidades.FacEmpresa;
import com.general.entidades.FacEstablecimiento;
import com.general.entidades.FacGeneral;
import com.general.entidades.FacProducto;
import com.general.entidades.FacTiposDocumento;
import com.general.entidades.facDetalleDocumentoEntidad;
import com.general.servicios.FacDocumentoServicios;
import com.general.servicios.FacGeneralServicio;
import com.general.util.DetallesAdicionalesReporte;
import com.general.util.InformacionAdicional;
import com.general.controladores.FacConsultaDocumentoSeleccControladores;


@ViewScoped
@ManagedBean
@SessionScoped
public class FacConsultaControladores2 {

	@EJB
	private FacDocumentoServicios facDocumentoServicios; // variable en la cual llama la clase de servicio
	
	@EJB
	private FacGeneralServicio facGenSer;
	
	// metodod de variable del web
	private String ruc; // variable que recoge el ruc de la consulta
	private String RazonSocial; // variable que recoge la razon social de la consulta
	private String SeleccionTipo; // variable que recoge el item selecciondo del tipo de documento
	private Date FechaInicio;// dato que se encarga de recoger la fecha de inicio
	private Date FechaFinal;// dato que se encarga de recoger la fecha de final
	private String Correos;// dato de correos que se va a enviar de las facuras
	private String numDocumento;
		
	// metodos de tipo de documentos
	private FacTiposDocumento facTiposDocumento;
	private ArrayList<SelectItem> Tipo; // variable que recoge los tipos de documento
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

	//TODO contructor que se encarga de limpiar el contenido de los campos
	public void Actualizar_paguina() {
		CargarDatos();
	}

	//TODO contructor general que se encarga de cargar los datos primordiales
	public void CargarDatos(){
		try{
			 System.out.println("Prueba de Errores JZU");
			//Dialect parametrizado Default  idGeneral -> 100
			FacGeneral facGen = facGenSer.buscarDatosGeneralPrimerHijo("100");
			if (facGen !=null)
				dialect=facGen.getDescripcion();
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
			//PathReports ruta de archivos Jasper 
			pathReports = facGenSer.buscarDatosGeneralPadreHash("103");
			System.out.println("pathReports::"+pathReports.toString());
			if (pathReports ==null){
				String msg = "Configuracion General::FacGeneral::La carpeta de temporales no esta configurada::idGeneral->100";
				FacesMessage mensaje=null;
				mensaje = new FacesMessage(FacesMessage.SEVERITY_INFO,msg,null);
				FacesContext.getCurrentInstance().addMessage(null, mensaje);
			}
			
			//Path Directorio Temporal	
			facGen = facGenSer.buscarDatosGeneralPrimerHijo("109");
			if (facGen !=null)
				pathTemp=facGen.getDescripcion();
			else{
				String msg = "Configuracion General::FacGeneral::La carpeta de temporales no esta configurada::idGeneral->109";
				FacesMessage mensaje=null;
				mensaje = new FacesMessage(FacesMessage.SEVERITY_INFO,msg,null);
				FacesContext.getCurrentInstance().addMessage(null, mensaje);
			}
			
			//Path Directorio SubReports	
			facGen = facGenSer.buscarDatosGeneralPadre("112");
			if (facGen !=null)
				pathSubReport=facGen.getDescripcion();
			else{
				String msg = "Configuracion General::FacGeneral::La carpeta de SubReports no esta configurada::idGeneral->112";
				FacesMessage mensaje=null;
				mensaje = new FacesMessage(FacesMessage.SEVERITY_INFO,msg,null);
				FacesContext.getCurrentInstance().addMessage(null, mensaje);
			}
			
			
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
			setCorreos("");
			setRazonSocial("");
			setRuc("");
			setSeleccionTipo("");
			FechaFinal = null;
			FechaInicio = null;
			facTiposDocumento = new FacTiposDocumento();
			valorDetalleDocumento = new facDetalleDocumentoEntidad();
			selectedfacDetalleDocumentoEntidad = null;
			llenarCombo();
			llenarDetalleDocumento();
		}catch (Exception e) {
			FacesMessage mensaje=null;
			mensaje = new FacesMessage(FacesMessage.SEVERITY_INFO,e.getMessage(),null);
			FacesContext.getCurrentInstance().addMessage(null, mensaje);
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
			documento_detalles = BuscarDatosdeDetalleDocumento(new Object[]{ruc,
																			RazonSocial,
																			SeleccionTipo, 
																			((FechaInicio == null)? FechaInicio: (new java.text.SimpleDateFormat(ls_dateFormat).format(FechaInicio))),
																			((FechaFinal == null)? FechaFinal: (new java.text.SimpleDateFormat(ls_dateFormat).format(FechaFinal))),
																			rucEmpresa,
																			"Dialect,"+dialect,numDocumento});
			tipoDocumentos = buscarPorCodigo();
			detalleEmpresas = fac_empresas();
			if (documento_detalles!=null)
			{
			
			DetalleDocumento2 = MotrandoDocumentoFiltrados(documento_detalles,tipoDocumentos,detalleEmpresas);
		
			
			
			for (facDetalleDocumentoEntidad detalle : DetalleDocumento2) {
				DetalleDocumento.add(new facDetalleDocumentoEntidad(
																detalle.getRFCREC(),
																detalle.getNOMREC(),
																detalle.getCodDoc(),
																detalle.getTIPODOC(),
																detalle.getFOLFAC(),
																detalle.getTOTAL(),
																detalle.getFECHA(),
																detalle.getEDOFAC(),
																detalle.getPDFARC(),
																detalle.getXMLARC(),
																detalle.getEmail(),
																detalle.getDireccion(),
																detalle.getFormato(),
																detalle.getCodEstablecimiento(),
																detalle.getCodPuntoEmision(),
																detalle.getCodigoDocumento(),
																detalle.getSecuencial(), detalle.getXmlAutorizacion()));
				
				detalle_documentos_ = new FacConsultaDocumentoSeleccControladores(DetalleDocumento);
			}
		}
		}catch (Exception e) {
			FacesMessage mensaje=null;
			mensaje = new FacesMessage(FacesMessage.SEVERITY_INFO,e.getMessage(),null);
			FacesContext.getCurrentInstance().addMessage(null, mensaje);
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
	private List<FacCabDocumento> BuscarDatosdeDetalleDocumento(Object [] obj){
		List<FacCabDocumento> detalledocumento = null;
			
		try{			
			detalledocumento = facDocumentoServicios.MostrandoDetalleDocumento(obj);         
	
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
	
	//TODO contructor que se encarga de cargar el archivo seleccionado sea pdf o xml
	public void AbrirArchivo(String Archivo , String direccion){
		//sistemaOperativo.equals("Windows")
		
		try {
			//pdf();
			System.out.println("DOCUMENTO::"+direccion + Archivo);
			System.out.println("EDOFAC::"+valorDetalleDocumento.getEDOFAC());
			System.out.println("FOLFAC::"+valorDetalleDocumento.getFOLFAC());
			System.out.println("CodDoc::"+valorDetalleDocumento.getCodDoc());
			System.out.println("CodEstablecimiento::"+valorDetalleDocumento.getCodEstablecimiento());
			System.out.println("CodigoDocumento::"+valorDetalleDocumento.getCodigoDocumento());
			System.out.println("CodPuntoEmision::"+valorDetalleDocumento.getCodPuntoEmision());
			System.out.println("Secuencial::"+valorDetalleDocumento.getSecuencial());
		
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try 
		{
			//String Documento = "//192.168.32.90/DataExpress/jzurita/firmados/0992531940001010010100000603720130225.xml";
			if(Archivo.contains(".pdf") && !valorDetalleDocumento.getEDOFAC().toString().trim().equals("AUTORIZADO")){
				mensajeAlerta("Mensaje del sistema", "No se encuentra el archivo pdf porque el documento no esta AUTORIZADO", "peligro");
				return;
			}
			String Documento = direccion + Archivo;
			Documento.replace("/", "\"");
			System.out.println("DOCUMENTO::"+direccion + Archivo);
			File ficheroXLS = new File(Documento);
			FacesContext ctx = FacesContext.getCurrentInstance();
			FileInputStream fis = new FileInputStream(ficheroXLS);
			byte[] bytes = new byte[1000];
			int read = 0;

			if (!ctx.getResponseComplete()) {
			   String fileName = ficheroXLS.getName();
			   String contentType = "application/" + (Archivo.contains(".pdf") ? "pdf" : "xml");
			   HttpServletResponse response =(HttpServletResponse) ctx.getExternalContext().getResponse();
			   response.setContentType(contentType);
			   response.setHeader("Content-Disposition","attachment;filename=" + fileName);
			   ServletOutputStream out = response.getOutputStream();

			   while ((read = fis.read(bytes)) != -1) {
			        out.write(bytes, 0, read);
			   }

			   out.flush();
			   out.close();
			   ctx.responseComplete();
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		 	FacesContext context = FacesContext.getCurrentInstance();  
	        context.addMessage(null, new FacesMessage("Successful", "Hello " + "No se encuentra el archivo seleccionado"));  
		}
		/*
		if (sistemaOperativo.equals("Windows")){
			try 
			{
				//String Documento = "//192.168.32.90/DataExpress/jzurita/firmados/0992531940001010010100000603720130225.xml";
				if(Archivo.contains(".pdf") && !valorDetalleDocumento.getEDOFAC().toString().trim().equals("AUTORIZADO")){
					mensajeAlerta("Mensaje del sistema", "No se encuentra el archivo pdf porque el documento no esta AUTORIZADO", "peligro");
					return;
				}
				String Documento = direccion + Archivo;
				Documento.replace("/", "\"");
				System.out.println("DOCUMENTO::"+direccion + Archivo);
				File ficheroXLS = new File(Documento);
				FacesContext ctx = FacesContext.getCurrentInstance();
				FileInputStream fis = new FileInputStream(ficheroXLS);
				byte[] bytes = new byte[1000];
				int read = 0;
	
				if (!ctx.getResponseComplete()) {
				   String fileName = ficheroXLS.getName();
				   String contentType = "application/" + (Archivo.contains(".pdf") ? "pdf" : "xml");
				   HttpServletResponse response =(HttpServletResponse) ctx.getExternalContext().getResponse();
				   response.setContentType(contentType);
				   response.setHeader("Content-Disposition","attachment;filename=" + fileName);
				   ServletOutputStream out = response.getOutputStream();
	
				   while ((read = fis.read(bytes)) != -1) {
				        out.write(bytes, 0, read);
				   }
	
				   out.flush();
				   out.close();
				   ctx.responseComplete();
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			 	FacesContext context = FacesContext.getCurrentInstance();  
		        context.addMessage(null, new FacesMessage("Successful", "Hello " + "No se encuentra el archivo seleccionado"));  
			}
		}else{
			if (sistemaOperativo.equals("Linux")){
				try {
					pdf();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}*/
	}
	
	//TODO contructor que se encarga de autocompletar el ruc del comprador para facilitar al usuario
	public List<String> complete_RucComprador(String parametro_ruc) throws Exception{
		List<String> resultado = new ArrayList<String>();
		resultado = facDocumentoServicios.BuscarfitroEmpresaDocumentos(parametro_ruc, rucEmpresa, "2");		
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
		
	//TODO contructor que se encarga de enviar los correos
	public void EnviarCorreos(String Evento) throws Exception{
		Boolean correosnoEnviados = false;
		claseEmail = new FacEnviarMail();
		// verifica si se a seleccionado un documento del registro
		
		if(selectedfacDetalleDocumentoEntidad.length == 0){
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
			
			for(facDetalleDocumentoEntidad seleccionDocumentos: selectedfacDetalleDocumentoEntidad){
				StringBuffer body = new StringBuffer();
				body.append("Estimado(a) cliente;  <br>");
				body.append("Acaba de recibir su documento electrónico generado el " + seleccionDocumentos.getFECHA());
				body.append("<br><br>Saludos cordiales, ");
				body.append("<br>Cima IT, ");
				body.append("<br><br>Servicio proporcionado por DataExpress Internacional");
				body.append("<br><br>Tel. 593 04 2280217");
				
				String pdf = "",xml = "";
				for (int i = 0; i < selectedOptionsEmail.length; i++) {
					File ficheroXLS = new File("");
					if(selectedOptionsEmail[i].toString().trim().equals("PDF"))
					{
						pdf = empresas.getPathCompFirmados() + seleccionDocumentos.getPDFARC();
						ficheroXLS = new File(pdf);
						if(!ficheroXLS.isFile())
							pdf = "";
					}
					else
					{	
						xml = empresas.getPathCompFirmados() + seleccionDocumentos.getXMLARC();
						ficheroXLS = new File(xml);
						if(!ficheroXLS.isFile())
							xml = "";
					}
				}
				String Adjuntararchivos[];
				String NombreArchivoZepado = "C:/" + seleccionDocumentos.getFOLFAC() + ".zip";
				if(Evento.trim().equals("ZIP")){
					NombreArchivoZepado.replace("/", "\"");
					Zippear(new String[]{pdf, xml}, NombreArchivoZepado);
					Adjuntararchivos = new String[]{NombreArchivoZepado};
				}else
				{
					Adjuntararchivos = new String[]{pdf, xml};
				}
				if(xml.toString().trim() != "" || pdf.toString().trim() != "")
					claseEmail.enviar(empresas, //empresas
									  seleccionDocumentos.getEmail() + ((Correos.toString().trim().equals("")) ? Correos: "," + Correos), //toAddress
									  "",//ccAddress
									  "",//bccAddresss
									  "Documento electrónico No:" + seleccionDocumentos.getFOLFAC() + " de Cima IT",//subject
									  true, //xisHTMLFormat
									  body, //body
									  false,//debug
									  Adjuntararchivos////Adjuntararchivos
									  );
				else
					correosnoEnviados = true;
				if(Evento.trim().equals("ZIP")){
					File da = new File(NombreArchivoZepado);
					da.delete();
				}
			}
			if(correosnoEnviados)
				mensajeAlerta("Mensaje del sistema","Algunos documentos no se encuentra el archivo, y no se realizo el envio del correo", "Informacion");
			
		mensajeAlerta("Mensaje del sistema","Correos enviados", "informacion");
	}
	
	
	//TODO contructor de mensaje de alerta
	 private void mensajeAlerta(String mensajeVentana, String mensajeDetalle, String tipo) {
		 FacesContext context = FacesContext.getCurrentInstance();            
	      context.addMessage(null, new FacesMessage((tipo.toString().trim().equals("alerta") ? FacesMessage.SEVERITY_ERROR : tipo.toString().trim().equals("peligro") ? FacesMessage.SEVERITY_WARN : FacesMessage.SEVERITY_INFO),mensajeVentana, mensajeDetalle)); 
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
	
	public void pdf() throws Throwable{
		System.out.println("::PDF::");
		System.out.println("CodDoc::"+valorDetalleDocumento.getCodDoc());
		System.out.println("RucPdf::"+rucEmpresa);
		
		valorDetalleDocumento.setCodigoDocumento(valorDetalleDocumento.getCodDoc());
		System.out.println("CodEstablecimiento::"+valorDetalleDocumento.getCodEstablecimiento());
		System.out.println("CodigoDocumento::"+valorDetalleDocumento.getCodigoDocumento());
		System.out.println("CodPuntoEmision::"+valorDetalleDocumento.getCodPuntoEmision());
		System.out.println("Secuencial::"+valorDetalleDocumento.getSecuencial());
		pathReports = facGenSer.buscarDatosGeneralPadreHash("103");
		String pathReport = pathReports.get(valorDetalleDocumento.getCodigoDocumento());
		System.out.println("pathReports::"+pathReports.toString());
		System.out.println("pathReport::"+pathReport);
		
		FileInputStream is = null;
	    JRDataSource dataSource = null;
		List<FacDetDocumento> lstFactDetDocumento = new ArrayList<FacDetDocumento>();
		List<FacDetAdicional> lstFactDetAdictDocumento = new ArrayList<FacDetAdicional>();
		List<InformacionAdicional> infoAdicional = new ArrayList<InformacionAdicional>();
		List<DetallesAdicionalesReporte> detallesAdiciones = new ArrayList<DetallesAdicionalesReporte>();
		
		lstFactDetDocumento = facDocumentoServicios.buscarDetDocumentoPorFk(rucEmpresa,
																			valorDetalleDocumento.getCodEstablecimiento(),
																		 	valorDetalleDocumento.getCodPuntoEmision(), 
																	 		valorDetalleDocumento.getSecuencial(),
																			valorDetalleDocumento.getCodigoDocumento(),
																			valorDetalleDocumento.getAmbiente());
		
		lstFactDetAdictDocumento = facDocumentoServicios.buscarDetDocumentoAdicPorFk(rucEmpresa,
																					 valorDetalleDocumento.getCodEstablecimiento(),
																					 valorDetalleDocumento.getCodPuntoEmision(), 
																					 valorDetalleDocumento.getSecuencial(),
																					 valorDetalleDocumento.getCodigoDocumento(),
																					 valorDetalleDocumento.getAmbiente());
		
		for (FacDetAdicional detAdic : lstFactDetAdictDocumento) {
			InformacionAdicional infoAd = new InformacionAdicional();	        
	        infoAd.setNombre(detAdic.getNombre());
	        infoAd.setNombre(detAdic.getValor());
	        infoAdicional.add(infoAd);
		}
		
		for (FacDetDocumento detDocumento : lstFactDetDocumento) {
			FacProducto producto = new FacProducto();
            DetallesAdicionalesReporte detAd = new DetallesAdicionalesReporte();
            detAd.setCodigoPrincipal(detDocumento.getCodPrincipal());
            producto = facDocumentoServicios.buscarProductoPorId(detDocumento.getCodPrincipal().trim());
            detAd.setDetalle1(producto.getAtributo1());            
            detAd.setDetalle2(producto.getAtributo2());
            detAd.setDetalle3(producto.getAtributo3());
            detAd.setDescuento(String.valueOf(detDocumento.getDescuento()));
            detAd.setCodigoAuxiliar(detDocumento.getCodAuxiliar());
            detAd.setDescripcion(detDocumento.getDescripcion());
            detAd.setCantidad(String.valueOf(detDocumento.getCantidad()));
            detAd.setPrecioTotalSinImpuesto(String.valueOf(detDocumento.getPrecioTotalSinImpuesto()));
            detAd.setPrecioUnitario(String.valueOf(detDocumento.getPrecioUnitario()));
            detAd.setInfoAdicional(infoAdicional.isEmpty() ? null : infoAdicional);            
            detallesAdiciones.add(detAd);		
		}
		
		String name = valorDetalleDocumento.getCodigoDocumento() + valorDetalleDocumento.getCodEstablecimiento() + valorDetalleDocumento.getCodPuntoEmision() + ruc + valorDetalleDocumento.getSecuencial();
       
        JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(detallesAdiciones);  
        //String pathReport = "C:\\DataExpress\\DMIRO\\generales\\Jasper\\factura.jasper";
        pathReport = "./reportes/"+pathReports.get(valorDetalleDocumento.getCodigoDocumento());
        //String pathReport = "/reportes/factura.jasper"; 
        System.out.println("pathReportPdf::"+pathReport);
        String reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath(pathReport);  
        JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, obtenerMapaParametrosReportes(obtenerParametrosInfoTriobutaria(), obtenerInfoFactura()), beanCollectionDataSource);  
        HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
        httpServletResponse.addHeader("Content-disposition", "attachment; filename="+name+".pdf");  
        ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();  
        JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);  
        FacesContext.getCurrentInstance().responseComplete();  
    }
	public void xml(facDetalleDocumentoEntidad valor) throws Throwable{ 
		System.out.println("::PDF::");
		System.out.println("CodDoc::"+valor.getXMLARC());
		System.out.println("CodDoc::"+valor.getCodDoc());
		System.out.println("RucPdf::"+rucEmpresa);
		/*
		//valorDetalleDocumento.setCodigoDocumento(valorDetalleDocumento.getCodDoc());
		System.out.println("CodEstablecimiento::"+valorDetalleDocumento.getCodEstablecimiento());
		System.out.println("CodigoDocumento::"+valorDetalleDocumento.getCodigoDocumento());
		System.out.println("CodPuntoEmision::"+valorDetalleDocumento.getCodPuntoEmision());
		System.out.println("Secuencial::"+valorDetalleDocumento.getSecuencial());
		pathReports = facGenSer.buscarDatosGeneralPadreHash("103");
		String pathReport = pathReports.get(valorDetalleDocumento.getCodigoDocumento());
		System.out.println("pathReports::"+pathReports.toString());
		System.out.println("pathReport::"+pathReport);*/
		//String nameDocument = valorDetalleDocumento.getCodDoc()+valorDetalleDocumento.getCodEstablecimiento()+valorDetalleDocumento.getCodPuntoEmision()+valorDetalleDocumento.getSecuencial();
		//pathReport = "/reportes/"
	    String nameDocument = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/reportes/"+valor.getXMLARC());//"C:\\DataExpress\\DMIRO\\";
		//String nameDocument = pathReport+rucEmpresa+".xml";
		System.out.println("File Xml::"+nameDocument);
		String xmlString = valor.getXmlAutorizacion(); //obtengo la cadena
		System.out.println("Xml::"+xmlString);
		BufferedWriter out = new BufferedWriter(new FileWriter(nameDocument));
        out.write(xmlString);
        out.flush();
        out.close();
		String Documento = nameDocument;
		Documento.replace("/", "\"");
		
		File ficheroXLS = new File(Documento);
		FacesContext ctx = FacesContext.getCurrentInstance();
		FileInputStream fis = new FileInputStream(ficheroXLS);
		byte[] bytes = new byte[1000];
		int read = 0;

		if (!ctx.getResponseComplete()) {
		   String fileName = ficheroXLS.getName();
		   String contentType = "application/" + (nameDocument.contains(".pdf") ? "pdf" : "xml");
		   HttpServletResponse response =(HttpServletResponse) ctx.getExternalContext().getResponse();
		   response.setContentType(contentType);
		   response.setHeader("Content-Disposition","attachment;filename=" + fileName);
		   ServletOutputStream outxml = response.getOutputStream();

		   while ((read = fis.read(bytes)) != -1) {
			   outxml.write(bytes, 0, read);
		   }

		   outxml.flush();
		   outxml.close();
		   ctx.responseComplete();
		}
		/*
		String nameDocument = valorDetalleDocumento.getCodDoc()+valorDetalleDocumento.getCodEstablecimiento()+valorDetalleDocumento.getCodPuntoEmision()+valorDetalleDocumento.getSecuencial();
	    HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
        httpServletResponse.addHeader("Content-disposition", "attachment; filename="+nameDocument+".pdf");  
        ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();  
        //JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);  
        FacesContext.getCurrentInstance().responseComplete();
		
		String xmlString = valorDetalleDocumento.getXmlAutorizacion(); //obtengo la cadena
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); //un factory
		DocumentBuilder builder = factory.newDocumentBuilder(); //el documento
		//Document document = builder.parse(new InputSource(new StringReader(xmlString)));
		servletOutputStream = new InputSource(new StringReader(xmlString)).getByteStream();*/
		
	}
	private Map<String, Object> obtenerParametrosInfoTriobutaria()
	   {
	     Map param = new HashMap();
	 
	     FacCabDocumento cabDoc = new FacCabDocumento();
	     FacEmpresa empresa = new FacEmpresa();
	     FacEstablecimiento establecimiento = new FacEstablecimiento();
	     try {
	       cabDoc = facDocumentoServicios.buscarCabDocumentoPorPk(rucEmpresa,
	    		   												  valorDetalleDocumento.getCodEstablecimiento(),
	    		   												  valorDetalleDocumento.getCodPuntoEmision(), 
	    		   												  valorDetalleDocumento.getSecuencial(),
	    		   												  valorDetalleDocumento.getCodigoDocumento(),
	    		   												  valorDetalleDocumento.getAmbiente());
	       if (cabDoc != null)
	       {
	         empresa = facDocumentoServicios.listadoEmpr(rucEmpresa);
	         establecimiento = facDocumentoServicios.buscarCodEstablecimiento(rucEmpresa,valorDetalleDocumento.getCodEstablecimiento());
	         param.put("RUC", rucEmpresa);
	         param.put("CLAVE_ACC", (cabDoc.getClaveAcceso().trim().equals("")) || (cabDoc.getClaveAcceso() == null) ? "SIN CLAVE" : cabDoc.getClaveAcceso());
	         param.put("RAZON_SOCIAL", empresa.getRazonSocial());
	         param.put("NOM_COMERCIAL", empresa.getRazonComercial());
	         param.put("DIR_MATRIZ", empresa.getDireccionMatriz());
	         param.put("SUBREPORT_DIR", pathSubReport);
	         param.put("TIPO_EMISION", cabDoc.getTipoEmision().trim().equals("1") ? "NORMAL" : "CONTINGENCIA");
	         param.put("NUM_AUT", (cabDoc.getNumAutDocSustento() == null) || (cabDoc.getNumAutDocSustento().equals("")) ? null : cabDoc.getNumAutDocSustento());
	         param.put("FECHA_AUT", cabDoc.getFechaEmisionDocSustento() == null ? null : new SimpleDateFormat("dd/MM/yyyy").format(cabDoc.getFechaEmisionDocSustento()));
	         param.put("NUM_FACT", cabDoc.getId().getCodEstablecimiento() + "-" + cabDoc.getId().getCodPuntEmision() + "-" + cabDoc.getId().getSecuencial());
	         param.put("AMBIENTE", cabDoc.getId().getAmbiente().intValue() == 1 ? "PRUEBA" : "PRODUCCION");
	         param.put("DIR_SUCURSAL", establecimiento.getDireccionEstablecimiento());
	         param.put("CONT_ESPECIAL", empresa.getContribEspecial());
	         //param.put("LLEVA_CONTABILIDAD", cabDoc.getObligadoContabilidad()); 
	         //CPA
	         param.put("LLEVA_CONTABILIDAD", empresa.getObligContabilidad());
	         System.out.println("LOGO::"+empresa.getPathLogoEmpresa());
	         /*
	         if (empresa.getPathLogoEmpresa() != null){
	        	 if (empresa.getPathLogoEmpresa().length()>0)
	        	 param.put("LOGO", empresa.getPathLogoEmpresa());
	         }*/
	 
	         String file = cabDoc.getId().getAmbiente().intValue() == 1 ? "produccion.jpeg" : "pruebas.jpeg";
	         String ruta = (empresa.getPathMarcaAgua() == null) || (empresa.getPathMarcaAgua().trim().equals("")) ? "C://resources//images//" : empresa.getPathMarcaAgua();
	         /*
	         
	          System.out.println("MARCA_AGUA::" + ruta + file);
			  String marca = empresa.getMarcaAgua().trim().equals("S") ? ruta + file : "C://resources//images//produccion.jpeg";
			  f = new File(marca);
			  if (f.exists()) param.put("MARCA_AGUA", marca);
			 */
	       }
	     }
	     catch (Exception e) { e.printStackTrace(); }
	 
	     return param;
	   }
	
	private Map<String, Object> obtenerInfoFactura()
	   {
	     Map param = new HashMap();
	 
	     FacCabDocumento cabDoc = new FacCabDocumento();
	     try {
	       cabDoc = facDocumentoServicios.buscarCabDocumentoPorPk(rucEmpresa,
	    		   												  valorDetalleDocumento.getCodEstablecimiento(),
																  valorDetalleDocumento.getCodPuntoEmision(), 
																  valorDetalleDocumento.getSecuencial(),
																  valorDetalleDocumento.getCodigoDocumento(),
																  valorDetalleDocumento.getAmbiente());
	       if (cabDoc != null) {
	         param.put("RS_COMPRADOR", cabDoc.getRazonSocialComprador());
	         param.put("RUC_COMPRADOR", cabDoc.getIdentificacionComprador());
	         param.put("FECHA_EMISION", cabDoc.getFechaEmision());
	         param.put("GUIA", cabDoc.getGuiaRemision());
	         param.put("VALOR_TOTAL", Double.valueOf(cabDoc.getImporteTotal()));
	         param.put("IVA", Double.valueOf(cabDoc.getIva12()));
	         //emite.getInfEmisor().getTotalSinImpuestos()
	         param.put("IVA_0", Double.valueOf(cabDoc.getSubtotal0()));
	         param.put("IVA_12", Double.valueOf(cabDoc.getSubtotal12()));
	         param.put("ICE", Double.valueOf(cabDoc.getTotalvalorICE()));
	         param.put("NO_OBJETO_IVA", Double.valueOf(cabDoc.getSubtotalNoIva()));
	         param.put("SUBTOTAL", Double.valueOf(cabDoc.getTotalSinImpuesto()));
	         param.put("PROPINA", Double.valueOf(cabDoc.getPropina()));
	         param.put("TOTAL_DESCUENTO", Double.valueOf(cabDoc.getTotalDescuento()));
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

	public String getNumDocumento() {
		return numDocumento;
	}

	public void setNumDocumento(String numDocumento) {
		this.numDocumento = numDocumento;
	}
	
	
}
