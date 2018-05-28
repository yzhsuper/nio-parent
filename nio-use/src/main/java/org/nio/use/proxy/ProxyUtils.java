package org.nio.use.proxy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.apache.commons.lang3.SerializationUtils;
import org.nio.use.api.HelloService;
import org.nio.use.entity.RequestObject;
import org.nio.use.entity.ResponseObject;

/**
 * Hello world!
 *
 */
public class ProxyUtils {
	public static HelloService getBean() {
		SocketChannel socketChannel = null;
		try {
			socketChannel = SocketChannel.open();
			SocketAddress socketAddress = new InetSocketAddress("localhost", 10000);
			socketChannel.connect(socketAddress);

			RequestObject RequestObject = new RequestObject("request_", "request_");
			sendData(socketChannel, RequestObject);

			HelloService service = receiveData(socketChannel);
			return service;
		} catch (Exception ex) {
		} finally {
			try {
				socketChannel.close();
			} catch (Exception ex) {
			}
		}
		return null;
	}
	
	public static void sendData(SocketChannel socketChannel, RequestObject RequestObject) throws IOException {
		byte[] bytes = SerializationUtils.serialize(RequestObject);
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		socketChannel.write(buffer);
		socketChannel.socket().shutdownOutput();
	}

	public static HelloService receiveData(SocketChannel socketChannel) throws IOException {
		HelloService helloService = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try {
			ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
			byte[] bytes;
			int count = 0;
			while ((count = socketChannel.read(buffer)) >= 0) {
				buffer.flip();
				bytes = new byte[count];
				buffer.get(bytes);
				baos.write(bytes);
				buffer.clear();
			}
			bytes = baos.toByteArray();
			Object obj = SerializationUtils.deserialize(bytes);
			helloService = (HelloService) obj;
			socketChannel.socket().shutdownInput();
		} finally {
			try {
				baos.close();
			} catch(Exception ex) {}
		}
		return helloService;
	}
}
