package server;

import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

/**
 * �������ļ����߳�
 * 
 * @author HaiSong.Zhang
 *
 */
public class ServerListen implements Runnable {

	private ServerSocket serverSocket = null;
	private Socket socket = null;

	private ClientLinkList cll = null;

	private boolean started = false;
	private JLabel showImage;//��ʾͼƬ

	private JTextArea showChatMSG = null;
	private DefaultTableModel tableModel = null;// ������
	private boolean linked=true;

	/*
	 * ���췽��
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
	 * ����started��ֵ
	 */
	public void setStarted(boolean started) {
		this.started = started;
	}

	

	/**
	 * ʵ��run()����
	 */
	@Override
	public void run() {
		try {
			while (started) {
				socket = serverSocket.accept();// ���ܿͻ�������
				Node clientNode = new Node(socket, cll, showChatMSG,linked,showImage,tableModel);
				
				clientNode.username = (String) clientNode.input.readUTF();
				String[] str = { clientNode.username };
				tableModel.addRow(str);
				cll.addNodeToTail(clientNode);
				tableModel.fireTableDataChanged();
				
				
				showChatMSG.append(clientNode.username + "������" + "\n");
				
				new Thread(clientNode).start();

			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}
