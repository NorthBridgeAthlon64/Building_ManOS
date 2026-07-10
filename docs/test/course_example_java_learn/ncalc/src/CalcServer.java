import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;

public class CalcServer {
	public static final int BUFFER_SIZE = 1024;
	int port = 9090;
	Selector selector = null;
	ServerSocketChannel serverSocketChannel = null;

	private Calc calc = new CalcImpl();

	// 创建固定大小的线程池用于处理业务逻辑
	// 不建议使用Executors
	private static final ExecutorService workerPool = Executors.newFixedThreadPool(100);

	public CalcServer() {
		try {

			// 1. 打开选择器
			selector = Selector.open();

			// 2. 打开服务器通道
			serverSocketChannel = ServerSocketChannel.open();

			// 3. 配置为非阻塞模式
			serverSocketChannel.configureBlocking(false);

			// 4. 绑定端口
			serverSocketChannel.bind(new InetSocketAddress(port));

			// 5. 注册 ACCEPT 事件到选择器
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

			System.out.println("calc server is ready.");

			// 6. 主循环：处理连接请求
			while (true) {

				// 阻塞等待至少一个通道就绪
				int readyChannels = selector.select();
				if (readyChannels == 0) {
					continue;
				}

				// 获取已就绪的 SelectionKey 集合
				Set<SelectionKey> selectedKeys = selector.selectedKeys(); // 获取所有的token
				Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

				while (keyIterator.hasNext()) {
					SelectionKey key = keyIterator.next();

					// 重要：必须移除，防止重复处理
					keyIterator.remove();

					try {
						if (key.isValid()) {
							if (key.isAcceptable()) {
								handleAccept(key, selector);
							} else if (key.isReadable()) {
								// 将读任务提交给线程池处理，避免阻塞主线程
								workerPool.submit(() -> handleRead(key));
							}
						}
					} catch (Exception e) {
						// 发生异常时取消键并关闭通道
						key.cancel();
						try {
							if (key.channel() != null) {
								key.channel().close();
							}
						} catch (IOException ex) {
							ex.printStackTrace();
						}
						e.printStackTrace();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 处理新连接接受事件
	 */
	private void handleAccept(SelectionKey key, Selector selector) throws IOException {
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
		SocketChannel clientChannel = serverSocketChannel.accept();

		if (clientChannel != null) {
			// 配置客户端通道为非阻塞
			clientChannel.configureBlocking(false);

			// 注册 READ 事件，并附加一个 ByteBuffer 用于存储读取的数据
			// 注意：在实际生产中，附件对象可能需要更复杂的生命周期管理
			clientChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(BUFFER_SIZE));

			// System.out.println("new connection: " + clientChannel.getRemoteAddress());
		}
	}

	/**
	 * 处理数据读取事件 (在工作线程中执行)
	 */
	private void handleRead(SelectionKey key) {
		SocketChannel clientChannel = (SocketChannel) key.channel();
		// ByteBuffer buffer = (ByteBuffer) key.attachment();
		ByteBuffer buffer = ByteBuffer.allocate(1024);

		try {
			buffer.clear();
			// 读取数据
			int bytesRead = clientChannel.read(buffer);

			if (bytesRead > 0) {
				// System.out.println("buffer has data: " + bytesRead);
				// 切换为读模式
				buffer.flip();

				// 解析数据 (这里简单转换为字符串)
				byte[] data = new byte[buffer.remaining()];
				buffer.get(data);

				DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
				int command = dis.readInt();

				int result = 0;

				if (command == 1) {
					result = calc.add(dis.readInt(), dis.readInt());
					ByteBuffer resultBuffer = ByteBuffer.allocate(4);
					resultBuffer.put((byte) (result >> 24));
					resultBuffer.put((byte) (result >> 16));
					resultBuffer.put((byte) (result >> 8));
					resultBuffer.put((byte) result);
					resultBuffer.flip();
					clientChannel.write(resultBuffer);
				
				} else if (command == 2) {
					result = calc.subtract(dis.readInt(), dis.readInt());
					ByteBuffer resultBuffer = ByteBuffer.allocate(4);
					resultBuffer.put((byte) (result >> 24));
					resultBuffer.put((byte) (result >> 16));
					resultBuffer.put((byte) (result >> 8));
					resultBuffer.put((byte) result);
					resultBuffer.flip();
					clientChannel.write(resultBuffer);
					
				} else if (command == 0) {
					String message = dis.readUTF();
					System.out.println("recieved client message: " + message);
					message = "echo: " + message;
					byte[] ba = message.getBytes("UTF-8");
					ByteBuffer resultBuffer = ByteBuffer.allocate(ba.length);
					resultBuffer.put(ba);
					resultBuffer.flip();
					clientChannel.write(resultBuffer);
				}

				// System.out.println("收到来自 " + clientChannel.getRemoteAddress() + " 的消息: " +
				// message);

				// 模拟业务处理耗时
				Thread.sleep(100);


			} else if (bytesRead == -1) {
				// 客户端断开连接
				//System.out.println("客户端断开连接: " + clientChannel.getRemoteAddress());
				key.cancel();
				clientChannel.close();
			}
		} catch (Exception e) {
			//System.err.println("处理读取事件出错: " + e.getMessage());
			try {
				key.cancel();
				clientChannel.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {

		new CalcServer();

	}

}
