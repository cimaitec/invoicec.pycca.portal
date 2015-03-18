package com.usuario.servicios;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.config.ConfigPersistenceUnit;
import com.documentos.entidades.FacEmpresa;
import com.general.controladores.FacEncriptarcadenasControlador;
import com.general.entidades.FacCliente;
import com.usuario.entidades.FacLoginBitacora;
import com.usuario.entidades.FacRole;
import com.usuario.entidades.FacUsuario;
import com.usuario.entidades.FacUsuariosRole;



@Stateless
public class FacUsuarioServicio {
	@PersistenceContext(unitName = ConfigPersistenceUnit.persistenceUnit)
	//ConfigPersistenceUnit.persistenceUnit
	private EntityManager em;

	private List<FacUsuario> facUsuario;

	public void insertarUsuario(FacUsuario usuario) throws Exception {
		try {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			em.persist(usuario);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void EliminarDatosLogico(FacUsuario user) throws Exception {
		try {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Query q = em.createQuery("update FacUsuario set isActive = :Estado where ruc = :rucempresa and codUsuario = :codigo");
			q.setParameter("rucempresa", user.getId().getRuc());
			q.setParameter("codigo", user.getId().getCodUsuario());
			q.setParameter("Estado", "0");
			q.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void insertarUsuarioRol(FacUsuariosRole usuarioRol) throws Exception {
		try {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			em.persist(usuarioRol);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void modificarDatos(FacUsuario usuario) throws Exception {
		try {
			try {
				Thread.sleep(1000);
				
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Query q = em.createQuery("update FacUsuario set " +
					" password = :contrasena	," +
					" isActive = :estado " +
					" where id.codUsuario = :usuario and " +
					"		rucEmpresa = :ruc_usuario and" +
					"		id.ruc = :ruc_empresa  " +
					"		");
			q.setParameter("estado", usuario.getIsActive());
			q.setParameter("contrasena", usuario.getPassword());
			
			q.setParameter("usuario", usuario.getId().getCodUsuario());
			q.setParameter("ruc_usuario", usuario.getRucEmpresa());
			q.setParameter("ruc_empresa", usuario.getId().getRuc());
			q.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void modificarDatosRol(FacUsuariosRole usuarioRol) throws Exception {
		try {
			try {
				Thread.sleep(1000);
					} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Query q = em.createQuery("update FacUsuariosRole set id.codRol= :codRol , isActive = :estado where id.codUsuario = :usuario and id.ruc = :ruc_empresa");
			q.setParameter("estado", usuarioRol.getIsActive());
			q.setParameter("codRol", usuarioRol.getId().getCodRol());
			q.setParameter("usuario", usuarioRol.getId().getCodUsuario());
			q.setParameter("ruc_empresa", usuarioRol.getId().getRuc());
			q.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public FacCliente buscarRucCliente(String Ruc) throws Exception {
		FacCliente c = null;
		try {


			Query q = em.createQuery("SELECT C FROM FacCliente C where C.id.rucCliente = :ruc");
			q.setParameter("ruc", Ruc.trim());
		
			
			c = (FacCliente) q.getSingleResult();
			return  c;
		} catch (Exception ex) {
			
			ex.printStackTrace();
			throw new Exception("Error al Buscar el registro");
		}
	}

	public FacEmpresa buscarDatosPorRuc(String Ruc) throws Exception {
		FacEmpresa e = null;
		try {

			Query q = em.createQuery("SELECT E FROM FacEmpresa E where E.ruc= :ruc");
			q.setParameter("ruc", Ruc);
			
		
			e = (FacEmpresa) q.getSingleResult();
			
			
		} catch (Exception ex) {
			
			ex.printStackTrace();
			throw new Exception("Error al Buscar el registro");
		}
		
		return  e;
	}

	@SuppressWarnings("unchecked")
	public List<FacUsuario> buscarTodosLosUsuario() throws Exception {
		try {
			Query q = em.createQuery("SELECT U FROM FacUsuario U ORDER BY U.id.ruc ");
			

			return q.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error al Buscar el registro");
		}
	}

	@SuppressWarnings("unchecked")
	public FacUsuario BuscarCodUsuario(String rucUsua,String rucEmp,String tipoUsuario) throws Exception {
		FacUsuario u = new FacUsuario();
		try {
			Query q = em.createQuery("SELECT U FROM FacUsuario U where U.rucEmpresa = :rucUsuario AND U.id.ruc = :rucEmp AND U.tipoUsuario = :tipoUsua");
			q.setParameter("rucUsuario", rucUsua);
			q.setParameter("rucEmp", rucEmp);
			q.setParameter("tipoUsua", tipoUsuario.trim());
			
			List<FacUsuario> facusuario = q.getResultList();
			if(!facusuario.isEmpty()){
				u= (FacUsuario) q.getSingleResult();
				
			}else{
				u= null;
			}
		} catch (Exception e) {
			u=null;
			e.printStackTrace();
			throw new Exception("Error al Buscar");
		}
		
		return u;
		
	}
	@SuppressWarnings("unchecked")
	public List<FacEmpresa> buscarEmpresa() throws Exception {
		Query q = null;
		try {
			q = em.createQuery("SELECT E FROM FacEmpresa E where E.isActive = 'Y'");
						
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("Error al Buscar el registro");
		}
		
		return  q.getResultList();
	}
	
	public FacUsuariosRole buscarCodRol(String ruc,String codUsuario) throws Exception{
		FacUsuariosRole usuRol = null;
				try {
					Query q = em.createQuery("SELECT UR FROM FacUsuariosRole UR WHERE UR.id.ruc = :ruc AND UR.id.codUsuario = :codUsuario");
					q.setParameter("ruc", ruc);
					q.setParameter("codUsuario", codUsuario);
					usuRol = new FacUsuariosRole();
					usuRol = (FacUsuariosRole) q.getSingleResult();
				} catch (Exception e) {
					usuRol = null;
					return usuRol;
				}
		return usuRol;
	}

	public FacEmpresa ver_tipo(String tipo) throws Exception {
		try {
			Query q = em.createQuery("SELECT E FROM FacEmpresa E ");

			FacEmpresa e = (FacEmpresa) q.getSingleResult();

			return e;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error al Buscar el registro");
		}
	}

	@SuppressWarnings("unchecked")
	public List<FacUsuario> buscarTodosDatosUsuario()throws Exception{
	    try{
			Query q =  em.createQuery("SELECT U FROM FacUsuario U ") ;					
			return q.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error al Buscar el registro");
		}	
	}

	@SuppressWarnings("unchecked")
	public List<FacRole> buscarTodosRol()throws Exception{
	    try{
			Query q =  em.createQuery("SELECT R FROM FacRole R ORDER BY R.codRol ") ;
     					
			return q.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error al Buscar el registro");
		}   
	}
	
	public int generaSecuencial() {
		Query q;
		q = em.createQuery("SELECT count(*)  FROM FacUsuario");
		java.lang.Object valor = q.getSingleResult();
		int e = Integer.parseInt(valor.toString());
		e = e + 1;
		return e;
	}
	
	@SuppressWarnings("unchecked")
	public FacUsuario validarUsuario(FacUsuario usuario){
		try{
		Query q = em.createQuery("SELECT E  FROM FacUsuario E where isActive = :Estado and id.ruc = :RUCempresa and id.codUsuario = :usuario ");
		q.setParameter("Estado", "Y");
		q.setParameter("RUCempresa", usuario.getId().getRuc());
		q.setParameter("usuario", usuario.getId().getCodUsuario());
		
		List<FacUsuario> facConsultausuario = q.getResultList();
		for (FacUsuario facUsuario : facConsultausuario) {
			if(FacEncriptarcadenasControlador.decrypt(facUsuario.getPassword()).trim().equals(FacEncriptarcadenasControlador.decrypt(usuario.getPassword()).trim()))
				return facUsuario;
		}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String validarUsuarioMensaje(FacUsuario usuario){
		try{
			
			System.out.println("Funcion donde valida si usuario...");
			Query q = em.createQuery("SELECT E  FROM FacUsuario E where isActive = 'Y' and id.ruc = :RUCempresa and id.codUsuario = :usuario ");
			System.out.println("Voy a llenar parametros...");
			//q.setParameter("Estado", "Y");
			System.out.println("Voy a llenar parametros -1-...");
			System.out.println(usuario.getId());
			System.out.println(usuario.getId().getRuc());
			q.setParameter("RUCempresa", usuario.getId().getRuc());
			q.setParameter("usuario", usuario.getId().getCodUsuario());
			
			System.out.println("Funcion donde valida si usuario...");
			
			List<FacUsuario> facConsultausuario = q.getResultList();
			System.out.println("Llenó lista...");
			if (facConsultausuario.isEmpty())
				return "usuario no existe o usuario inactivo ";
			
			for (FacUsuario facUsuario : facConsultausuario) {
				if(FacEncriptarcadenasControlador.decrypt(facUsuario.getPassword()).trim().equals(FacEncriptarcadenasControlador.decrypt(usuario.getPassword()).trim()))
					
					return "ok";
				else{
					return "contraseña incorrecta";
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	

	@SuppressWarnings("unchecked")
	public List<FacCliente> buscarClientes(String Ruc,String tipoUsuario){
		Query q =  null;
		try {
			q = em.createQuery("Select C from FacCliente C Where C.id.ruc = :ruc And C.tipoCliente = :tipoUsuario");
			q.setParameter("ruc", Ruc);
			q.setParameter("tipoUsuario", tipoUsuario);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return q.getResultList();
	}
	
	// HFU
	public List<FacLoginBitacora> listaLoginBitacora(String p_ruc, String p_usuario)
	{
		Query q = null;
		List<FacLoginBitacora> listaLoginBitacora = null;
		try
		{
			/*System.out.println("Antes select");
			System.out.println("Ruc "+p_ruc);
			System.out.println("Usuario "+p_usuario);*/
			q = em.createQuery("select U from FacLoginBitacora U where U.ruc = :ruc and U.usuario = :usuario");
			q.setParameter("ruc", p_ruc);
			q.setParameter("usuario", p_usuario);
			listaLoginBitacora = q.getResultList();
			//System.out.println("Luego de ejecucion");
			return listaLoginBitacora;
		}
		catch(Exception e)
		{
			System.out.println("Error FacUsuarioServicio.listaLoginBitacora...");
			System.out.println(e);
			return listaLoginBitacora;
		}
	}
	
	public void ingresaLoginBitacora(FacLoginBitacora facLoginBitacora) throws Exception {
		try {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			em.persist(facLoginBitacora);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void actualizaLoginBitacora(FacLoginBitacora facLoginBitacora) throws Exception
	{
		try
		{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(" -- Voy a Actualizar Sesion -- ");
			System.out.println("Ruc... " + facLoginBitacora.getRuc());
			System.out.println("Usuario... " + facLoginBitacora.getUsuario());
			Query q = em.createQuery("update FacLoginBitacora " +
										"set fechaFinSesion = :fechaFinSesion " +
									  "where id.ruc = :rucempresa " +
									    "and usuario = :codigo " +
									    "and fechaFinSesion = null ");
			q.setParameter("fechaFinSesion", facLoginBitacora.getFechaFinSesion());
			q.setParameter("rucempresa", facLoginBitacora.getRuc());
			q.setParameter("codigo", facLoginBitacora.getUsuario());
			q.executeUpdate();
			System.out.println("Ejecutó...");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public int geSecuencialLoginBitacora(){
		int secuencial = 1;
		List<FacLoginBitacora> listaLoginBitacora = null;
		try {
			Query q = null;
			q = (Query) em.createQuery("select F from FacLoginBitacora F where F.id = (select max(G.id) from FacLoginBitacora G)");
			listaLoginBitacora =  q.getResultList();
			System.out.println("SecuencialLoginBitacoraMax::"+listaLoginBitacora.get(0).getId());
			Integer res = listaLoginBitacora.get(0).getId();
			if (res==null){
				secuencial = 1;
			}
			else{
				secuencial = res.intValue();
			}			
				++secuencial;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return secuencial;
	}
	
	public void cambiarContrasena(FacUsuario usuario, String p_nuevaContrasena)
	{
		try
		{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Ruc... " + usuario.getId().getRuc());
			System.out.println("Usuario... " + usuario.getId().getCodUsuario());
			Query q = em.createQuery("update FacUsuario set password = :nuevaContrasena where id.ruc = :rucempresa and id.codUsuario = :codigo");
			String nuevaContrasena = FacEncriptarcadenasControlador.encrypt(p_nuevaContrasena);
			q.setParameter("nuevaContrasena", nuevaContrasena);
			q.setParameter("rucempresa", usuario.getId().getRuc());
			q.setParameter("codigo", usuario.getId().getCodUsuario());
			q.executeUpdate();
			System.out.println("Ejecutó...");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	// HFU

	public List<FacUsuario> getFacUsuario() {
		return facUsuario;
	}

	public EntityManager getEm() {
		return em;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}

	public void setFacUsuario(List<FacUsuario> facUsuario) {
		this.facUsuario = facUsuario;
	}

}
