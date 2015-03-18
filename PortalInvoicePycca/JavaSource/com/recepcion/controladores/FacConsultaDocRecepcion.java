package com.recepcion.controladores;

import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import com.general.entidades.facDetalleDocumentoEntidad;
import com.recepcion.entidades.FacDocRecepcion;

public class FacConsultaDocRecepcion extends ListDataModel<FacDocRecepcion> implements SelectableDataModel<FacDocRecepcion> {
	  public FacConsultaDocRecepcion() {  
	    }  
	  
	    public FacConsultaDocRecepcion(List<FacDocRecepcion> data) {  
	        super(data);  
	    }  
	      
	    @SuppressWarnings("unchecked")
		@Override  
	    public FacDocRecepcion getRowData(String rowKey) {  
	        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
	          
	        List<FacDocRecepcion> docs = (List<FacDocRecepcion>) getWrappedData();  
	          
	        for(FacDocRecepcion doc : docs) {  
	            //if(car.getFOLFAC() .equals(rowKey))  
	                return doc;  
	        }  
	          
	        return null;  
	    }  
	    
	    
	    
	    public List<FacDocRecepcion> getListData() {  
	        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
	          
	        List<FacDocRecepcion> docs = (List<FacDocRecepcion>) getWrappedData();  	          	        	         
	        return docs;  
	    }  
	  
	    @Override  
	    public Object getRowKey(FacDocRecepcion doc) {  
	        return doc;  
	    }  
}
