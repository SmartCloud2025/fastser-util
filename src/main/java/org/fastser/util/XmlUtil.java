/**
 * Copyright@xiaocong.tv 2013
 */
package org.fastser.util;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.xml.sax.InputSource;

import com.thoughtworks.xstream.XStream;

/**
 * @author weijun.ye
 * @version 
 * @date 2013-1-31
 */
public class XmlUtil {
	
protected static final Log log = LogFactory.getLog(XmlUtil.class);

	public static List<Map<String, Object>> getMapFromXmlFile(String filePath){
        Document doc = getDocumentFromXmlFile(filePath);
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();   
        if(doc == null)   
            return null;
        Element root = doc.getRootElement();   
        for (Iterator<?> iterator = root.elementIterator(); iterator.hasNext();) {
        	Map<String, Object> map = new HashMap<String, Object>();  
            Element e = (Element) iterator.next();   
            List<?> list = e.elements();   
            if(list != null && list.size() > 0){
                map.put(e.getName(), dom2Map(e));   
            }else{  
                map.put(e.getName(), e.getText());   
            }
            listMap.add(map);
        }   
        return listMap;
	}
    
    public static Document getDocumentFromXmlFile(String filePath) {
        SAXReader redaer = new SAXReader();
        Document document = null;
        try {
            InputStream in = XmlUtil.class.getClassLoader().getResourceAsStream(filePath);
            document = redaer.read(in);
         
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return  document;
    }
    
    public static Document getDocumentFromString(String value) {
        SAXReader redaer = new SAXReader();
        Document document = null;
        try {
            InputStream in = new ByteArrayInputStream(value.getBytes());
            document = redaer.read(in);
         
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return  document;
    }
    
    public static boolean isGzipInRequest(HttpServletRequest request) {
        String header = request.getHeader("Accept-Encoding");
        return (header != null) && (header.indexOf("gzip") >= 0);
    }
    
    public static String serialize(Object object) throws IOException {
        XStream xStream = new XStream();
        xStream.autodetectAnnotations(true);
        return xStream.toXML(object);
    }
    
    public static void writeXmlToResponse(HttpServletResponse response,String xml) throws IOException {
    	writeXmlToResponse(response,"UTF-8",xml,true,false,true,"text/xml");
    }
    
    public static void writeXmlToResponse(HttpServletResponse response, 
            String encoding, String xml, boolean addXmlHeader, boolean gzip, boolean noCache, 
            String contentType) throws IOException {
        response.setContentType(contentType + ";charset=" + encoding);
        if (noCache) {
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Expires", "0");
            response.setHeader("Pragma", "No-cache");
        }
        
        if (addXmlHeader) {
           // xml = "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>" + xml;
        }
        
        if (gzip) {
            response.addHeader("Content-Encoding", "gzip");
            GZIPOutputStream out = null;
            InputStream in = null;
            try {
                out = new GZIPOutputStream(response.getOutputStream());
                in = new ByteArrayInputStream(xml.getBytes());
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                if (in != null)
                    in.close();
                if (out != null) {
                    out.finish();
                    out.close();
                }
            }
        } else {
            response.setContentLength(xml.getBytes(encoding).length);
            PrintWriter out = response.getWriter();
            out.print(xml);
            out.flush();
            out.close();
        }
    }
    
    public static void writeXmlToResponse(HttpServletResponse response, 
            String encoding, byte[] context, boolean addXmlHeader, boolean gzip, boolean noCache, 
            String contentType) throws IOException {
        response.setContentType(contentType + ";charset=" + encoding);
        if (noCache) {
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Expires", "0");
            response.setHeader("Pragma", "No-cache");
        }
        
        /*if (addXmlHeader) {
            xml = "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>" + xml;
        }*/
        
        OutputStream out = null;
        InputStream in = null;
        try {
            out = response.getOutputStream();
            in = new ByteArrayInputStream(context);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } finally {
            if (in != null)
                in.close();
            if (out != null) {
                out.close();
            }
        }
    }


    /** 
     *  
     * @param document 
     * @return 
     */  
    public static String getStringFromDocument(Document document) { 
        String s = "";  
        try {  
            // 使用输出流来进行转化   
            ByteArrayOutputStream out = new ByteArrayOutputStream();  
            // 使用UTF-8编码   
            OutputFormat format = new OutputFormat("   ", true, "UTF-8");  
            XMLWriter writer = new XMLWriter(out, format);  
            writer.write(document);  
            s = out.toString("UTF-8");  
        } catch (Exception ex) {  
            ex.printStackTrace();  
        }  
        return s;  
    }
    
    public static Map<String, Object> getMapFromXmlString(String xml) throws DocumentException{   
    	Document doc = getDocumentFromString(xml);
        Map<String, Object> map = new HashMap<String, Object>();   
        if(doc == null)   
            return map;   
        Element root = doc.getRootElement();   
        for (Iterator<?> iterator = root.elementIterator(); iterator.hasNext();) {   
            Element e = (Element) iterator.next();   
            //System.out.println(e.getName());   
            List<?> list = e.elements();   
            if(list != null && list.size() > 0){
                map.put(e.getName(), dom2Map(e));   
            }else{  
                map.put(e.getName(), e.getText());   
            }
        }   
        return map;   
    }   
       

	public static Map<String, Object> dom2Map(Element e){   
        Map<String, Object> map = new HashMap<String, Object>();   
        List<?> outList = e.elements();   
        if(outList != null){ 
        	for (int i = 0;i < outList.size(); i++) {
        		 Element innerElement = (Element) outList.get(i);
        		 List<?> innerList = innerElement.elements();
        		 if(innerList != null && innerList.size() > 0){
                     map.put(innerElement.getName(), dom2Map(innerElement));   
                 }else{  
                     map.put(innerElement.getName(), innerElement.getText());   
                 }
        	}
        }
        return map;   
    } 
    
    
    /**
     * Converter Map<Object, Object> instance to xml string. Note: currently,
     * we aren't consider more about some collection types, such as array,list,
     *
     * @param dataMap  the data map
     *
     * @return the string
     */
    @SuppressWarnings("unchecked")
	public static String map2xml(Map<String, Object> dataMap,String rootName){
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("<").append(rootName).append(">");
       
		for (Iterator<String> iterator = dataMap.keySet().iterator(); iterator.hasNext();) {  
        	String key = iterator.next();
        	if (StringUtils.isEmpty(key)){
                continue;
            }
        	strBuilder.append("<").append(key.toString()).append(">");
        	Object value = dataMap.get(key);
        	if(value.getClass().getName().equals("java.util.HashMap")){
        		strBuilder.append(map2xml((Map<String, Object>)value));
        	}else{
                strBuilder.append(value.toString());
        	}
        	strBuilder.append("</").append(key.toString()).append(">");
        }
        strBuilder.append("</").append(rootName).append(">");
        return strBuilder.toString();
    }
    
    
    @SuppressWarnings("unchecked")
	public static String map2xml(Map<String, Object> dataMap){
        StringBuilder strBuilder = new StringBuilder();
        for (Iterator<String> iterator = dataMap.keySet().iterator(); iterator.hasNext();) {  
        	String key = iterator.next();
        	if (StringUtils.isEmpty(key)){
                continue;
            }
        	strBuilder.append("<").append(key.toString()).append(">");
        	Object value = dataMap.get(key);
        	if(value.getClass().getName().equals("java.util.HashMap")){
        		strBuilder.append(map2xml((Map<String, Object>)value));
        	}else{
                strBuilder.append(value.toString());
        	}
        	strBuilder.append("</").append(key.toString()).append(">");
        }
        return strBuilder.toString();
    }
    
    /**
     * 根据威富通的
     * @param parameters
     * @return
     */
    public static String parseXML(SortedMap<String, String> parameters) {
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        Set<?> es = parameters.entrySet();
        Iterator<?> it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            String v = (String)entry.getValue();
            if (null != v && !"".equals(v)) {
                sb.append("<" + k + ">" + parameters.get(k) + "</" + k + ">\n");
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }
    
    /**
     * 根据威富通工具类方法
     * @param xmlBytes
     * @param charset
     * @return
     * @throws Exception
     */
    public static Map<String, String> toMap(byte[] xmlBytes,String charset) throws Exception{
        SAXReader reader = new SAXReader(false);
        InputSource source = new InputSource(new ByteArrayInputStream(xmlBytes));
        source.setEncoding(charset);
        Document doc = reader.read(source);
        Map<String, String> params = toMap(doc.getRootElement());
        return params;
    }
    
    /**
     * 调用威富通工具类方法
     * @param request
     * @return
     */
    public static String parseRequst(HttpServletRequest request){
        String body = "";
        try {
            ServletInputStream inputStream = request.getInputStream(); 
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            while(true){
                String info = br.readLine();
                if(info == null){
                    break;
                }
                if(body == null || "".equals(body)){
                    body = info;
                }else{
                    body += info;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }            
        return body;
    }
    
    /**
     * 威富通工具类将map转化成xml
     */
    public static String toXml(Map<String, String> params){
        StringBuilder buf = new StringBuilder();
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        buf.append("<xml>");
        for(String key : keys){
            buf.append("<").append(key).append(">");
            buf.append("<![CDATA[").append(params.get(key)).append("]]>");
            buf.append("</").append(key).append(">\n");
        }
        buf.append("</xml>");
        return buf.toString();
    }

    /**
     * 
     * @param element
     * @return
     */
    public static Map<String, String> toMap(Element element){
        Map<String, String> rest = new HashMap<String, String>();
        List<Element> els = element.elements();
        for(Element el : els){
            rest.put(el.getName().toLowerCase(), el.getTextTrim());
        }
        return rest;
    }
    

}
