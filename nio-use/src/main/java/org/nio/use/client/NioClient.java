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
import org.nio.use.entity.RequestObject;
import org.nio.use.entity.ResponseObject;


public class NioClient {

	private final static Logger logger = Logger.getLogger(NioClient.class.getName());
	
	public static void main(String[] args) throws Exception {
		for (int i = 0; i < 2; i++) {
			final int idx = i;
			new Thread(new MyRunnable(idx)).start();
		}
	}
	
	private static final class MyRunnable implements Runnable {
		
		private final int idx;

		private MyRunnable(int idx) {
			this.idx = idx;
		}

		public void run() {
			SocketChannel socketChannel = null;
			try {
				socketChannel = SocketChannel.open();
				SocketAddress socketAddress = new InetSocketAddress("localhost", 10000);
				socketChannel.connect(socketAddress);

				RequestObject RequestObject = new RequestObject("request_" + idx, "request_" + idx);
				logger.log(Level.INFO, RequestObject.toString());
				sendData(socketChannel, RequestObject);
				
				ResponseObject ResponseObject = receiveData(socketChannel);
				logger.log(Level.INFO, ResponseObject.toString());
			} catch (Exception ex) {
				logger.log(Level.SEVERE, null, ex);
			} finally {
				try {
					socketChannel.close();
				} catch(Exception ex) {}
			}
		}

		private void sendData(SocketChannel socketChannel, RequestObject RequestObject) throws IOException {
			byte[] bytes = SerializationUtils.serialize(RequestObject);
			ByteBuffer buffer = ByteBuffer.wrap(bytes);
			socketChannel.write(buffer);
			socketChannel.socket().shutdownOutput();
		}

		private ResponseObject receiveData(SocketChannel socketChannel) throws IOException {
			ResponseObject ResponseObject = null;
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
				ResponseObject = (ResponseObject) obj;
				socketChannel.socket().shutdownInput();
			} finally {
				try {
					baos.close();
				} catch(Exception ex) {}
			}
			return ResponseObject;
		}
	}
}
