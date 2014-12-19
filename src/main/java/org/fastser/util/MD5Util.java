/**
 * Copyright@xiaocong.tv 2012
 */
package org.fastser.util;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * MD5
 * @author weijun.ye
 * @version 
 * @date 2012-4-26
 */
public class MD5Util {
    
    private static Log log = LogFactory.getLog(MD5Util.class);
    
    private static final char hexDigits[] = 
            { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    
    private MD5Util() {
        //ignore
    }
    
    public static String getMD5(String source) throws Exception {
        
        if (null == source) return source;
        
        String s = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(source.getBytes("UTF-8"));
            byte tmp[] = md.digest();
            char str[] = new char[16 * 2];
            int k = 0;
            for (int i = 0; i < 16; i++) {
                byte byte0 = tmp[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            s = new String(str);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new Exception(e.getMessage(), e);
        }
        
        return s;
    }
    
    public static String getFileMD5(File file) {
		if (!file.isFile()) {
			return null;
		}
		MessageDigest digest = null;
		FileInputStream in = null;
		byte buffer[] = new byte[1024];
		int len;
		try {
			digest = MessageDigest.getInstance("MD5");
			in = new FileInputStream(file);
			while ((len = in.read(buffer, 0, 1024)) != -1) {
				digest.update(buffer, 0, len);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		BigInteger bigInt = new BigInteger(1, digest.digest());
		return bigInt.toString(16);
	}
    
    public static String sign(String content, String key) throws Exception{
    	String signData = content + "&" + key;
    	return getMD5(signData);
    }
    
    public static boolean checkSign(String content,String sign, String key) throws Exception{
    	String signData = sign(content, key);
    	return signData.equals(sign.trim().toLowerCase());
    }
      
    
}