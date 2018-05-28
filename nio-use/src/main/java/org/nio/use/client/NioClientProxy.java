package org.nio.use.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.SerializationUtils;
import org.nio.use.api.HelloService;
import org.nio.use.entity.RequestObject;
import org.nio.use.entity.ResponseObject;
import org.nio.use.proxy.ProxyUtils;


public class NioClientProxy {

	private final static Logger logger = Logger.getLogger(NioClientProxy.class.getName());
	
	public static void main(String[] args) throws Exception {
		HelloService service = (HelloService) ProxyUtils.getBean();
		String s = service.hello("yangzhuo");
		System.out.println(s);
	}
	
}
