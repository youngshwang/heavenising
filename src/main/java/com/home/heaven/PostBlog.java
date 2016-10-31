package com.home.heaven;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class PostBlog {



	public String sendBlogDataToTstory(String title, String description,
			String tag) {
		String rtnVal = "";
		XmlRpcClient server = new XmlRpcClient();
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		try {
			config.setServerURL(new URL(TSTORY_URL));
		} catch (Exception e) {
			e.printStackTrace();
		}

		Vector<Serializable> params = new Vector<Serializable>();
		params.add(TSTORY_API_ID);
		params.add(TSTORY_ID);
		params.add(TSTORY_KEY);

		Hashtable<String, Object> hashtable = new Hashtable<String, Object>();
		hashtable.put("title", title);
		hashtable.put("description", description);
		hashtable.put("mt_keywords", tag);

		String[] categories = new String[] { "뉴스모음" };
		hashtable.put("categories", categories);
		
		params.add(hashtable);
		params.add(Boolean.TRUE);

		try {
			rtnVal = server.execute(config, "metaWeblog.newPost", params)
					.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(rtnVal);
		return rtnVal;
	}
	
	public String sendBlogDataToNaver(String title, String description,
			String tag) {
		String rtnVal = "";
		XmlRpcClient server = new XmlRpcClient();
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		try {
			config.setServerURL(new URL(NAVER_URL));
		} catch (Exception e) {
			e.printStackTrace();
		}

		Vector<Serializable> params = new Vector<Serializable>();
		params.add(NAVER_API_ID);
		params.add(NAVER_ID);
		params.add(NAVER_KEY);

		Hashtable<String, Object> hashtable = new Hashtable<String, Object>();
		hashtable.put("title", title);
		hashtable.put("description", description);
		hashtable.put("mt_keywords", tag);

		String[] categories = new String[] { "사진뉴스기사" };
		hashtable.put("categories", categories);
		
		params.add(hashtable);
		params.add(Boolean.TRUE);

		try {
			rtnVal = server.execute(config, "metaWeblog.newPost", params)
					.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(rtnVal);
		return rtnVal;
	}

	public String sendBlogDataToEgloos(String title, String description)
			throws MalformedURLException, XmlRpcException {
		String rtnVal = "";
		XmlRpcClient server = new XmlRpcClient();
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		config.setServerURL(new java.net.URL(EGLOOS_URL));

		Vector<Serializable> params = new Vector<Serializable>();
		params.add(1);
		params.add(EGLOOS_ID);
		params.add(EGLOOS_KEY);

		Hashtable<String, Object> hashtable = new Hashtable<String, Object>();
		hashtable.put("title", title);
		hashtable.put("description", description);
		Vector<String> categories = new Vector<String>();
		categories.add("2");
		hashtable.put("categories", categories);
		params.add(hashtable);
		params.add(Boolean.TRUE);

		rtnVal = server.execute(config, "metaWeblog.newPost", params)
				.toString();

		System.out.println(rtnVal);
		return rtnVal;
	}

	public String getBlogCategoryFromEgloos() throws MalformedURLException,
			XmlRpcException {
		String rtnVal = "";
		XmlRpcClient server = new XmlRpcClient();
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		config.setServerURL(new java.net.URL(EGLOOS_URL));

		Object[] params = new Object[] { 1, EGLOOS_ID, EGLOOS_KEY };
		Object[] returnedObj = (Object[]) server.execute(config,
				"metaWeblog.getCategories", params);
		for (Object o : returnedObj) {
			rtnVal += " Type : " + o.getClass().getCanonicalName();
			System.out.println(rtnVal);
			@SuppressWarnings("unchecked")
			HashMap<String, Object> map = (HashMap<String, Object>) o;
			for (Object key : map.keySet()) {
				rtnVal += " - " + key + " : " + map.get(key) + ",";
				System.out.println(rtnVal);
			}
		}
		return rtnVal;
	}

//	public Hashtable getPost(XmlRpcClient server) throws XmlRpcException,
//			IOException {
//		Vector<String> params = new Vector<String>();
//		params.addElement(new String("1220385"));
//		params.addElement(new String("ologist"));
//		params.addElement(new String("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"));
//		Hashtable value = (Hashtable) server.execute("metaWeblog.getPost",
//				params);
//		return value;
//	}

}
