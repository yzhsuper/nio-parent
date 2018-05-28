package org.nio.use.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.SerializationUtils;
import org.nio.use.api.HelloService;
import org.nio.use.api.HelloServiceImpl;
import org.nio.use.entity.RequestObject;
import org.nio.use.entity.ResponseObject;


public class NioServer {

	private final static Logger logger = Logger.getLogger(NioServer.class.getName());
	
	public static void main(String[] args) {
		Selector selector = null;
		ServerSocketChannel serverSocketChannel = null;
		
		try {
			// Selector for incoming time requests
			selector = Selector.open();

			// Create a new server socket and set to non blocking mode
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			
			// Bind the server socket to the local host and port
			serverSocketChannel.socket().setReuseAddress(true);
			serverSocketChannel.socket().bind(new InetSocketAddress(10000));
			
			// Register accepts on the server socket with the selector. This
			// step tells the selector that the socket wants to be put on the
			// ready list when accept operations occur, so allowing multiplexed
			// non-blocking I/O to take place.
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
	
			// Here's where everything happens. The select method will
			// return when any operations registered above have occurred, the
			// thread has been interrupted, etc.
			while (selector.select() > 0) {
				// Someone is ready for I/O, get the ready keys
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
	
				// Walk through the ready keys collection and process date requests.
				while (it.hasNext()) {
					SelectionKey readyKey = it.next();
					it.remove();
					
					// The key indexes into the selector so you
					// can retrieve the socket that's ready for I/O
					execute((ServerSocketChannel) readyKey.channel());
				}
			}
		} catch (ClosedChannelException ex) {
			logger.log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			logger.log(Level.SEVERE, null, ex);
		} finally {
			try {
				selector.close();
			} catch(Exception ex) {}
			try {
				serverSocketChannel.close();
			} catch(Exception ex) {}
		}
	}

	private static void execute(ServerSocketChannel serverSocketChannel) throws IOException {
		SocketChannel socketChannel = null;
		try {
			socketChannel = serverSocketChannel.accept();
			RequestObject myRequestObject = receiveData(socketChannel);
			logger.log(Level.INFO, myRequestObject.toString());
			
			HelloService service = new HelloServiceImpl();
//			ResponseObject myResponseObject = new ResponseObject(
//					"response for " + myRequestObject.getName(), 
//					"response for " + myRequestObject.getValue());
//			sendData(socketChannel, service);
			byte[] bytes = SerializationUtils.serialize((Serializable) service);
			ByteBuffer buffer = ByteBuffer.wrap(bytes);
			socketChannel.write(buffer);
			
//			logger.log(Level.INFO, myResponseObject.toString());
		} finally {
			try {
				socketChannel.close();
			} catch(Exception ex) {}
		}
	}
	
	private static RequestObject receiveData(SocketChannel socketChannel) throws IOException {
		RequestObject myRequestObject = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		
		try {
			byte[] bytes;
			int size = 0;
			while ((size = socketChannel.read(buffer)) >= 0) {
				buffer.flip();
				bytes = new byte[size];
				buffer.get(bytes);
				baos.write(bytes);
				buffer.clear();
			}
			bytes = baos.toByteArray();
			Object obj = SerializationUtils.deserialize(bytes);
			myRequestObject = (RequestObject)obj;
		} finally {
			try {
				baos.close();
			} catch(Exception ex) {}
		}
		return myRequestObject;
	}

//	private static void sendData(SocketChannel socketChannel, Object<T extends Serializable> object) throws IOException {
//		byte[] bytes = SerializationUtils.serialize(object);
//		ByteBuffer buffer = ByteBuffer.wrap(bytes);
//		socketChannel.write(buffer);
//	}
}
