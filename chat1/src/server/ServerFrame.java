package server;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import client.CloseResouce;

/**
 * 服务器
 * 
 * @author HaiSong.Zhang
 *
 */
public class ServerFrame extends JFrame implements ActionListener, KeyListener {

	// 服务器设置
	private static final int serverPort = 8888;// 服务端端口号
	private ServerSocket serverSocket = null; // 服务器端Socket对象
	private boolean started = false;// 服务器端打开后，started = true,开始检测等待客户端的连接

	// 界面设置
	private Dimension frameSize = new Dimension(600, 500); // 界面大小
	private JButton startServerButton;// 服务器启动按钮
	private JButton stopServerButton;// 服务器关闭按钮
	private JTextArea showChatMSG;// 服务器的显示聊天信息
	private JScrollPane MSGScrollPane;// 信息显示窗口的滚动条
	private JLabel showImage;// 显示图片
	private JTextArea serverMSG;// 服务器端发送的信息
	private JPanel panel = new JPanel();
	private Font font1 = new Font("微软雅黑", Font.BOLD, 15);
	private Font font2 = new Font("微软雅黑", 0, 15);

	// 用户链表
	private ClientLinkList cll = new ClientLinkList();

	// ServerListen线程类
	private ServerListen listenThread = null;

	// 定义用户列表
	private JScrollPane jScrollPane1 = null;// 滚动条面板
	private JTable heroTable = null;// 表格组件
	private ListSelectionModel listSelectionModel = null;
	private String[][] tableColValue = null;// 表格的值
	private GridBagLayout gridBag = new GridBagLayout();// 布局管理器
	private GridBagConstraints gridBagCon = null;
	private DefaultTableModel tableModel = null;

	private String str = null;
	private Calendar c = null;
	private SimpleDateFormat f = null;

	/**
	 * 构造方法
	 */
	public ServerFrame() {

		// 调用初始化方法
		initServer();

		this.setTitle("Hello World   ————	服务器");
		this.setSize(600, 480);

		// 设置运行时窗口位置
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();// 获取屏幕尺寸对象
		this.setLocation((int) (screenSize.width - frameSize.getWidth()) / 2,
				(int) (screenSize.height - frameSize.getHeight()) / 2);

		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	/**
	 * 界面初始化方法
	 */
	public void initServer() {

		// 采用绝对定位方式
		this.setLayout(null);

		startServerButton = new JButton("启动服务");
		startServerButton.setFont(font1);
		startServerButton.setBounds(260, 300, 110, 30);
		startServerButton.setFocusPainted(false);
		this.add(startServerButton);

		stopServerButton = new JButton("停止服务");
		stopServerButton.setFont(font1);
		stopServerButton.setBounds(380, 300, 110, 30);
		stopServerButton.setFocusPainted(false);
		this.add(stopServerButton);

		showChatMSG = new JTextArea();
		showChatMSG.setFont(font2);
		showChatMSG.setEditable(false);
		showChatMSG.setLineWrap(true);
		// 为聊天信息框添加滚动条
		MSGScrollPane = new JScrollPane(showChatMSG, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		MSGScrollPane.setPreferredSize(new Dimension(400, 200));
		MSGScrollPane.setBounds(150, 10, 420, 280);
		this.add(MSGScrollPane);
		// 在重新设置某个容器中的组件的大小以后，需要调用包含这个组件的容器的revalidate（）方法。
		// Revalidate（）方法会重新计算容器内所有的组建的大小，并且对他们进行重新布局
		MSGScrollPane.revalidate();

		showImage = new JLabel();
		showImage.setBounds(170, 340, 120, 120);
		this.add(showImage);

		serverMSG = new JTextArea();
		serverMSG.setBounds(320, 350, 220, 80);
		serverMSG.setLineWrap(true);
		this.add(serverMSG);
		serverMSG.addKeyListener(this);

		panel.setLayout(gridBag);
		tableColValue = new String[1][1];
		tableColValue[0][0] = "所有人";
		// 表格头部
		String[] tableColName = { "在线用户" };
		tableModel = new DefaultTableModel(tableColValue, tableColName);
		// 初始化表格中的信息
		heroTable = new JTable(tableModel) {
			public boolean isCellEditable(int row, int column) {
				return false; // 不可编辑
			}
		};
		heroTable.setFont(font2);
		heroTable.setRowHeight(30);
		heroTable.setPreferredScrollableViewportSize(new Dimension(120, 450));
		listSelectionModel = heroTable.getSelectionModel();
		// 设置表格选择方式:选择一行
		listSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listSelectionModel.addListSelectionListener(new SelectListener());
		jScrollPane1 = new JScrollPane(heroTable);
		jScrollPane1.setPreferredSize(new Dimension(120, 420));
		gridBagCon = new GridBagConstraints();
		gridBagCon.gridx = 0;
		gridBagCon.gridy = 1;
		gridBagCon.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(jScrollPane1, gridBagCon);
		panel.add(jScrollPane1);
		panel.setBounds(0, 10, 150, 420);
		this.add(panel);

		stopServerButton.setEnabled(false);
		startServerButton.addActionListener(this);
		stopServerButton.addActionListener(this);

		// 关闭程序时操作
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int flag = JOptionPane.showConfirmDialog(null, "确定要退出服务器吗?", "退出", JOptionPane.YES_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (flag == JOptionPane.YES_OPTION) {
					stopService();
					System.exit(0);
				}
			}
		});
	}

	/**
	 * 启动服务器
	 */
	public void startService() {

		try {

			serverSocket = new ServerSocket(serverPort);
			showChatMSG.append("服务端已经启动，在" + serverPort + "端口监听..." + "\n");
			startServerButton.setEnabled(false);
			stopServerButton.setEnabled(true);
			serverMSG.setEnabled(true);

			started = true;
			listenThread = new ServerListen(serverSocket, cll, started, showChatMSG, tableModel, showImage);
			new Thread(listenThread).start();

		} catch (BindException e) {
			// 当打开一个以上服务器时
			System.out.println("端口正在使用中...");
			System.out.println("请关掉相关程序并重新运行服务器");
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭服务器
	 */
	public void stopService() {

		if (cll.getClientCount() > 0) {
			int flag = JOptionPane.showConfirmDialog(null, "客户端正在连接，确定要关闭服务吗?", "确定", JOptionPane.YES_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (flag == JOptionPane.YES_OPTION) {
				if (started == true) { // 这个if判断细节不能少，如果不加的话，假如没开启服务就点叉号退出，会报空指针异常
					listenThread.setStarted(false);
				}
				CloseResouce.closeServerSocket(serverSocket);
				startServerButton.setEnabled(true);
				stopServerButton.setEnabled(false);
				serverMSG.setEnabled(false);
				sendMsgToAllClient("服务器已关闭");
				// 当点击关闭服务时，关闭各个客户端线程的流
				for (int i = 0; i < cll.getClientCount(); i++) {
					Node node = cll.findNodeByIndex(i);
					CloseResouce.closeInput(node.input);
					CloseResouce.closeOutput(node.output);
					CloseResouce.closeFileInput(node.fInput);
					CloseResouce.closeFileOutput(node.fOutput);
					CloseResouce.closeSocket(node.socket);
					cll.delNode(node.username);
				}
				showChatMSG.append("服务端已经关闭..." + "\n");
			}
		}

	}

	/**
	 * 主方法
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new ServerFrame();
	}

	/**
	 * 动作事件监听
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		Object obj = e.getSource();
		if (obj == startServerButton) {
			startService();
		} else if (obj == stopServerButton) {
			stopService();
		}
	}

	private class SelectListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {

			// 取得选中的行号
			int index = heroTable.getSelectedRow();

			// 将所选用户名保存到str中
			str = tableModel.getValueAt(index, 0).toString();
		}

	}

	/*
	 * 向所有人发送消息
	 */
	public void sendMsgToAllClient(String msg) {
		Node node = null;
		for (int i = 0; i < cll.getClientCount(); i++) {
			node = cll.findNodeByIndex(i);
			try {
				node.output.writeUTF("系统消息");
				node.output.flush();
				node.output.writeUTF(msg);
				node.output.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * 向指定客户端发送消息
	 */
	public void sendMsgToClient(String name, String msg) {
		Node node = cll.findNodeByName(name);
		try {
			node.output.writeUTF("系统消息");
			node.output.flush();
			node.output.writeUTF(msg);
			node.output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 响应回车键发送信息
	@Override
	public void keyPressed(KeyEvent e) {
		c = Calendar.getInstance();
		f = new SimpleDateFormat("HH:mm:ss");
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			e.consume(); // 加一句这个就行
			if (str == null) {
				JOptionPane.showMessageDialog(null, "请选择接受对象");
				return;
			}
			if (str == "所有人") {
				showChatMSG.append(f.format(c.getTime()) + "— 服务器对所有人说:" + '\n' + serverMSG.getText() + '\n');
				sendMsgToAllClient(serverMSG.getText());
				serverMSG.setText("");
			} else {
				showChatMSG.append(f.format(c.getTime()) + "— 服务器对" + str + " 说:" + '\n' + serverMSG.getText() + '\n');
				sendMsgToClient(str, serverMSG.getText());
				serverMSG.setText("");
			}
		}
	}

	// 这两个方法不用

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}
}
