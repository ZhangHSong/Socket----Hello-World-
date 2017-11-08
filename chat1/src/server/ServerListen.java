package server;

import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

/**
 * 服务器的监听线程
 * 
 * @author HaiSong.Zhang
 *
 */
public class ServerListen implements Runnable {

	private ServerSocket serverSocket = null;
	private Socket socket = null;

	private ClientLinkList cll = null;

	private boolean started = false;
	private JLabel showImage;//显示图片

	private JTextArea showChatMSG = null;
	private DefaultTableModel tableModel = null;// 表格组件
	private boolean linked=true;

	/*
	 * 构造方法
	 */
	public ServerListen(ServerSocket serverSocket, ClientLinkList cll, boolean started, JTextArea showChatMSG,
			DefaultTableModel tableModel,JLabel showImage) {

		this.serverSocket = serverSocket;
		this.cll = cll;
		this.started = started;
		this.showChatMSG = showChatMSG;
		this.tableModel = tableModel;
		this.showImage=showImage;
	}

	/*
	 * 设置started的值
	 */
	public void setStarted(boolean started) {
		this.started = started;
	}

	

	/**
	 * 实现run()方法
	 */
	@Override
	public void run() {
		try {
			while (started) {
				socket = serverSocket.accept();// 接受客户端连接
				Node clientNode = new Node(socket, cll, showChatMSG,linked,showImage,tableModel);
				
				clientNode.username = (String) clientNode.input.readUTF();
				String[] str = { clientNode.username };
				tableModel.addRow(str);
				cll.addNodeToTail(clientNode);
				tableModel.fireTableDataChanged();
				
				
				showChatMSG.append(clientNode.username + "上线了" + "\n");
				
				new Thread(clientNode).start();

			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}
