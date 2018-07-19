package org.nio.use.client;

import org.nio.use.api.HelloService;
import org.nio.use.proxy.ProxyUtils;


public class NioClientProxy {

	public static void main(String[] args) throws Exception {
		HelloService service = (HelloService) ProxyUtils.getBean();
		String s = service.hello("yangzhuo");
		System.out.println(s);
	}
	
}
