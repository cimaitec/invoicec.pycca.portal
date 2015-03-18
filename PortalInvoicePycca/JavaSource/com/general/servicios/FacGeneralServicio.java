package com.general.servicios;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpSession;

import com.config.ConfigPersistenceUnit;
import com.documentos.entidades.FacEmpresa;
import com.general.entidades.FacGeneral;

@Stateless
public class FacGeneralServicio {
	@PersistenceContext(unitName = ConfigPersistenceUnit.persistenceUnit)
	private EntityManager em;

	public EntityManager getEm() {
		return em;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}
	
	@SuppressWarnings("unchecked")
	public List<FacGeneral> buscarDatosGeneralHijo(String padre, boolean todos){
		List<FacGeneral> generalHijos= null;
		try {
			Query q = null;
			if (todos){
				//Todos independiente de estado: A ser usado en busquedas.... era el query por DEFAULT!
				q = em.createQuery("select E from FacGeneral E where E.codTabla = :padre");
			}
			else {
				//Unicamente los activos: Para llenar el combo de filtros de busqueda. No es el que venia por default
				q = em.createQuery("select E from FacGeneral E where E.codTabla = :padre and isActive = 'Y' order by e.idGeneral");
			}
			
			q.setParameter("padre", padre);
			System.out.println("padre::"+padre);
			generalHijos = q.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error al buscar configuracion en fac_general..."+e);
			return null;
		}
		return generalHijos;
	}
	
	public HashMap<String, String> buscarDatosGeneralPadreHash(String codTabla, String ruc){
		HashMap<String, String> MapGeneralJasper = null;
		List<FacGeneral> generalPadreHash= null;
		try {
			
			FacesContext context = FacesContext.getCurrentInstance();
			HttpSession sesion = (HttpSession)context.getExternalContext().getSession(true);
			if(sesion.getAttribute("Ruc_Empresa") != null)
			{
				ruc = sesion.getAttribute("Ruc_Empresa").toString();
				System.out.println("Ruc obtenido en la sesión: " +ruc);
			}
			
			Query q = em.createQuery("select ES from FacGeneral ES where ES.codTabla = :codTabla and ES.valor = :ruc");
			q.setParameter("codTabla", codTabla);
			q.setParameter("ruc", ruc);
			System.out.println("codTabla::"+codTabla);
			System.out.println("ruc::"+ruc);
			generalPadreHash = q.getResultList();
			if (generalPadreHash != null){			
				if (generalPadreHash.size()>0){
					MapGeneralJasper = new HashMap<String, String>();
					for (int i=0; i<generalPadreHash.size();i++){
						MapGeneralJasper.put(generalPadreHash.get(i).getCodUnico().trim(), generalPadreHash.get(i).getDescripcion());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("tu erro es ...: "+e);
			return null;
		}
		return MapGeneralJasper;
	}
	

	@SuppressWarnings("unchecked")
	public FacGeneral buscarDatosGeneralPrimerHijo(String padre){
		FacGeneral generalHijo= null;
		List<FacGeneral> generalHijos= null;
		try {
			Query q = em.createQuery("select E from FacGeneral E where E.codTabla = :padre ");
			q.setParameter("padre", padre);
			System.out.println("padre::"+padre);
			//generalHijo = (FacGeneral) q.getSingleResult();
			generalHijos = q.getResultList();
			if (generalHijos.size()>0){
				generalHijo=generalHijos.get(0);
			}else{
				generalHijo = null;
				System.out.println("No Hay Datos codTabla ..."+padre);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("tu erro es ..."+e);
			return null;
		}
		return generalHijo;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<FacGeneral> buscarDatosGeneralHijo(String padre){
		List<FacGeneral> generalHijos= null;
		try {
			Query q = em.createQuery("select E from FacGeneral E where E.codTabla = :padre ");
			q.setParameter("padre", padre);
			System.out.println("padre::"+padre);
			generalHijos = q.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("tu erro es ..."+e);
			return null;
		}
		return generalHijos;
	}
	
	public FacGeneral buscarDatosGeneralPadre(String padre){
		FacGeneral generalPadre =null;
		try {
			Query q = em.createQuery("select ES from FacGeneral ES where ES.idGeneral = :padre ");
			q.setParameter("padre", Integer.parseInt(padre));
			System.out.println("padre::"+padre);
			generalPadre = (FacGeneral) q.getSingleResult();
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("tu erro es ...: "+e);
			return null;
		}
		return generalPadre;
	}
	
	
	public HashMap<String, String> buscarDatosGeneralPadreHash(String codTabla){
		HashMap<String, String> MapGeneralJasper = null;
		List<FacGeneral> generalPadreHash= null;
		try {
			Query q = em.createQuery("select ES from FacGeneral ES where ES.codTabla = :codTabla ");
			q.setParameter("codTabla", codTabla);
			System.out.println("codTabla::"+codTabla);
			generalPadreHash = q.getResultList();
			if (generalPadreHash != null){			
				if (generalPadreHash.size()>0){
					MapGeneralJasper = new HashMap<String, String>();
					for (int i=0; i<generalPadreHash.size();i++){
						MapGeneralJasper.put(generalPadreHash.get(i).getCodUnico().trim(), generalPadreHash.get(i).getDescripcion());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("tu erro es ...: "+e);
			return null;
		}
		return MapGeneralJasper;
	}
	
}
