package org.fastser.util.ip;

import java.util.List;

public class IpUtil {

	public static String getIpAddress(String ip) {
        try{
            return null;//IPSeeker.getInstance().getCountry(ip);
        }catch(Exception e){
            e.printStackTrace();
        }
        return "未知区域";
    }
	
	public static void main(String[] args) {

		args = new String[] { "ip", "115.25.240.255" };

		IPSeeker seeker = IPSeeker.getInstance("");

		if (args.length == 2) {
			if ("ip".equals(args[0])) {
				System.out.println(args[0] + "的所在地址是:"
						+ seeker.getAddress(args[1]));
				System.out.println(args[0] + "的所在地址是属于:"
						+ seeker.getCountry(args[1]));
			} else if ("address".equals(args[0])) {
				List a = seeker.getIPEntries(args[1]);
				System.out.println(args[0] + ":");
				for (int i = 0; i < a.size(); i++) {
					System.out.println(a.get(i).toString());
				}
			} else {
				System.out.println("usage:java Test ip/address yourIpString/yourAddressString");
			}
		} else {
			System.out.println("usage:java Test ip/address yourIpString/yourAddressString");
		}
	}
}
