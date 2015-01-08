package com.kaancelen.charter.utils;

import java.util.Arrays;
import java.util.Collection;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

public class PrimefacesUtils {

	/**
	 * @param severity
	 * @param summary
	 * @param detail
	 */
	public static void showMessage(Severity severity, String summary, String detail){
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
	}
	
	/**
	 * @param severity
	 * @param summary
	 * @param detail
	 */
	public static void showMessage(String client, Severity severity, String summary, String detail){
		FacesContext.getCurrentInstance().addMessage(client, new FacesMessage(severity, summary, detail));
	}

	/**
	 * execute given javascript,use for show() - hide() dialog mostly
	 * @param script
	 */
	public static void executeScript(String script){
		RequestContext.getCurrentInstance().execute(script);
	}
	
	/**
	 * Get N html element id and update it with ajax request
	 * @param ids
	 */
	public static void ajaxUpdate(String...ids){
		Collection<String> idList = Arrays.asList(ids);
		RequestContext.getCurrentInstance().update(idList);
	}
	/**
	 * Check coming request
	 * @return true if ajax, false otherwise
	 */
	public static boolean isAjaxRequest(){
		return FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest();
	}	
	
	/**
	 * @param key
	 * @return url parameter with given key
	 */
	public static String input(String key){
		return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(key);
	}
}
