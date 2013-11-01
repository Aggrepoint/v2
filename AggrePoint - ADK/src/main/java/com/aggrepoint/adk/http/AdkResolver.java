package com.aggrepoint.adk.http;

import java.beans.FeatureDescriptor;
import java.util.Hashtable;
import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.PropertyNotFoundException;
import javax.el.PropertyNotWritableException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.jsp.JspApplicationContext;
import javax.servlet.jsp.JspFactory;

import com.aggrepoint.adk.IModule;
import com.aggrepoint.adk.IModuleRequest;
import com.aggrepoint.adk.IAdkConst;
import com.aggrepoint.adk.Winlet;
import com.aggrepoint.adk.WinletModule;
import com.aggrepoint.adk.data.RetCode;
import com.aggrepoint.adk.plugin.WinletReqInfo;
import com.icebean.core.beanutil.BeanProperty;
import com.icebean.core.common.ThreadContext;

/**
 * 支持在EL中通过module访问当前模块对象，通过retcode访问响应码对象，通过adk访问taglib功能等
 * 
 * @author YJM
 */
public class AdkResolver extends javax.el.ELResolver implements IAdkConst,
		ServletContextListener {
	static Hashtable<String, BeanProperty> m_htProperties = new Hashtable<String, BeanProperty>();

	static public Object getObjectValue(Object obj, String property)
			throws Exception {
		String key = obj.getClass() + "_" + property.toString();

		// Try to get property from cache
		BeanProperty bp = m_htProperties.get(key);
		if (bp != null) {
			return bp.get(obj);
		} else {
			bp = new BeanProperty(obj.getClass(), property, true, false);
			m_htProperties.put(key, bp);
			return bp.get(obj);
		}
	}

	@Override
	public Class<?> getCommonPropertyType(ELContext arg0, Object arg1) {
		return null;
	}

	@Override
	public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext arg0,
			Object arg1) {
		return null;
	}

	@Override
	public Class<?> getType(ELContext context, Object base, Object property)
			throws NullPointerException, PropertyNotFoundException, ELException {
		return null;
	}

	@Override
	public Object getValue(ELContext context, Object base, Object property)
			throws NullPointerException, PropertyNotFoundException, ELException {
		Object val = null;

		if (base == null) {
			if (property.equals("m")) {
				val = ThreadContext.getAttribute(THREAD_ATTR_MODULE);
				if (val instanceof WinletModule)
					val = ((WinletModule) val).getWinlet();
			} else if (property.equals("retcode")) {
				val = ThreadContext.getAttribute(THREAD_ATTR_RETCODE);
			} else if (property.equals("u")) {
				IModuleRequest req = (IModuleRequest) ThreadContext
						.getAttribute(THREAD_ATTR_REQUEST);
				val = req.getUserProfile();
			} else if (property.equals("f")) {
				val = ThreadContext.getAttribute(THREAD_ATTR_REQUEST);
			} else if (property.equals("e")) {
				val = ThreadContext.getAttribute(THREAD_ATTR_EXCEPTION);
			} else if (property.equals("adk"))
				val = new AdkEl();
			else if (property.equals("rinfo"))
				val = WinletReqInfo.getInfo((IModuleRequest) ThreadContext
						.getAttribute(THREAD_ATTR_REQUEST));

			if (val != null)
				context.setPropertyResolved(true);
		} else {
			if (base instanceof IModule || base instanceof Winlet
					|| base instanceof RetCode) {
				try {
					val = getObjectValue(base, property.toString());
					context.setPropertyResolved(true);
				} catch (Exception e) {
				}
			} else if (base instanceof IModuleRequest) {
				val = ((IModuleRequest) base).getFrontRequestAttribute(property
						.toString());
				context.setPropertyResolved(true);
			} else if (base instanceof AdkEl) {
				AdkEl adkEl = (AdkEl) base;
				if (adkEl.getMethod() == null) {
					adkEl.setMethod(property.toString());
					val = adkEl;
				} else {
					val = adkEl.execute(property.toString());
					context.setPropertyResolved(true);
				}
			}
		}

		return val;
	}

	@Override
	public boolean isReadOnly(ELContext arg0, Object arg1, Object arg2)
			throws NullPointerException, PropertyNotFoundException, ELException {
		return false;
	}

	@Override
	public void setValue(ELContext arg0, Object arg1, Object arg2, Object arg3)
			throws NullPointerException, PropertyNotFoundException,
			PropertyNotWritableException, ELException {
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	@Override
	public void contextInitialized(ServletContextEvent evt) {
		ServletContext context = evt.getServletContext();
		JspApplicationContext jspContext = JspFactory.getDefaultFactory()
				.getJspApplicationContext(context);
		jspContext.addELResolver(new AdkResolver());
	}
}
