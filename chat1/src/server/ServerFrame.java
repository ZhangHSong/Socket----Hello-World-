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
 * ������
 * 
 * @author HaiSong.Zhang
 *
 */
public class ServerFrame extends JFrame implements ActionListener, KeyListener {

	// ����������
	private static final int serverPort = 8888;// ����˶˿ں�
	private ServerSocket serverSocket = null; // ��������Socket����
	private boolean started = false;// �������˴򿪺�started = true,��ʼ���ȴ��ͻ��˵�����

	// ��������
	private Dimension frameSize = new Dimension(600, 500); // �����С
	private JButton startServerButton;// ������������ť
	private JButton stopServerButton;// �������رհ�ť
	private JTextArea showChatMSG;// ����������ʾ������Ϣ
	private JScrollPane MSGScrollPane;// ��Ϣ��ʾ���ڵĹ�����
	private JLabel showImage;// ��ʾͼƬ
	private JTextArea serverMSG;// �������˷��͵���Ϣ
	private JPanel panel = new JPanel();
	private Font font1 = new Font("΢���ź�", Font.BOLD, 15);
	private Font font2 = new Font("΢���ź�", 0, 15);

	// �û�����
	private ClientLinkList cll = new ClientLinkList();

	// ServerListen�߳���
	private ServerListen listenThread = null;

	// �����û��б�
	private JScrollPane jScrollPane1 = null;// ���������
	private JTable heroTable = null;// ������
	private ListSelectionModel listSelectionModel = null;
	private String[][] tableColValue = null;// ����ֵ
	private GridBagLayout gridBag = new GridBagLayout();// ���ֹ�����
	private GridBagConstraints gridBagCon = null;
	private DefaultTableModel tableModel = null;

	private String str = null;
	private Calendar c = null;
	private SimpleDateFormat f = null;

	/**
	 * ���췽��
	 */
	public ServerFrame() {

		// ���ó�ʼ������
		initServer();

		this.setTitle("Hello World   ��������	������");
		this.setSize(600, 480);

		// ��������ʱ����λ��
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();// ��ȡ��Ļ�ߴ����
		this.setLocation((int) (screenSize.width - frameSize.getWidth()) / 2,
				(int) (screenSize.height - frameSize.getHeight()) / 2);

		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	/**
	 * �����ʼ������
	 */
	public void initServer() {

		// ���þ��Զ�λ��ʽ
		this.setLayout(null);

		startServerButton = new JButton("��������");
		startServerButton.setFont(font1);
		startServerButton.setBounds(260, 300, 110, 30);
		startServerButton.setFocusPainted(false);
		this.add(startServerButton);

		stopServerButton = new JButton("ֹͣ����");
		stopServerButton.setFont(font1);
		stopServerButton.setBounds(380, 300, 110, 30);
		stopServerButton.setFocusPainted(false);
		this.add(stopServerButton);

		showChatMSG = new JTextArea();
		showChatMSG.setFont(font2);
		showChatMSG.setEditable(false);
		showChatMSG.setLineWrap(true);
		// Ϊ������Ϣ����ӹ�����
		MSGScrollPane = new JScrollPane(showChatMSG, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		MSGScrollPane.setPreferredSize(new Dimension(400, 200));
		MSGScrollPane.setBounds(150, 10, 420, 280);
		this.add(MSGScrollPane);
		// ����������ĳ�������е�����Ĵ�С�Ժ���Ҫ���ð�����������������revalidate����������
		// Revalidate�������������¼������������е��齨�Ĵ�С�����Ҷ����ǽ������²���
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
		tableColValue[0][0] = "������";
		// ���ͷ��
		String[] tableColName = { "�����û�" };
		tableModel = new DefaultTableModel(tableColValue, tableColName);
		// ��ʼ������е���Ϣ
		heroTable = new JTable(tableModel) {
			public boolean isCellEditable(int row, int column) {
				return false; // ���ɱ༭
			}
		};
		heroTable.setFont(font2);
		heroTable.setRowHeight(30);
		heroTable.setPreferredScrollableViewportSize(new Dimension(120, 450));
		listSelectionModel = heroTable.getSelectionModel();
		// ���ñ��ѡ��ʽ:ѡ��һ��
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

		// �رճ���ʱ����
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int flag = JOptionPane.showConfirmDialog(null, "ȷ��Ҫ�˳���������?", "�˳�", JOptionPane.YES_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (flag == JOptionPane.YES_OPTION) {
					stopService();
					System.exit(0);
				}
			}
		});
	}

	/**
	 * ����������
	 */
	public void startService() {

		try {

			serverSocket = new ServerSocket(serverPort);
			showChatMSG.append("������Ѿ���������" + serverPort + "�˿ڼ���..." + "\n");
			startServerButton.setEnabled(false);
			stopServerButton.setEnabled(true);
			serverMSG.setEnabled(true);

			started = true;
			listenThread = new ServerListen(serverSocket, cll, started, showChatMSG, tableModel, showImage);
			new Thread(listenThread).start();

		} catch (BindException e) {
			// ����һ�����Ϸ�����ʱ
			System.out.println("�˿�����ʹ����...");
			System.out.println("��ص���س����������з�����");
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * �رշ�����
	 */
	public void stopService() {

		if (cll.getClientCount() > 0) {
			int flag = JOptionPane.showConfirmDialog(null, "�ͻ����������ӣ�ȷ��Ҫ�رշ�����?", "ȷ��", JOptionPane.YES_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (flag == JOptionPane.YES_OPTION) {
				if (started == true) { // ���if�ж�ϸ�ڲ����٣�������ӵĻ�������û��������͵����˳����ᱨ��ָ���쳣
					listenThread.setStarted(false);
				}
				CloseResouce.closeServerSocket(serverSocket);
				startServerButton.setEnabled(true);
				stopServerButton.setEnabled(false);
				serverMSG.setEnabled(false);
				sendMsgToAllClient("�������ѹر�");
				// ������رշ���ʱ���رո����ͻ����̵߳���
				for (int i = 0; i < cll.getClientCount(); i++) {
					Node node = cll.findNodeByIndex(i);
					CloseResouce.closeInput(node.input);
					CloseResouce.closeOutput(node.output);
					CloseResouce.closeFileInput(node.fInput);
					CloseResouce.closeFileOutput(node.fOutput);
					CloseResouce.closeSocket(node.socket);
					cll.delNode(node.username);
				}
				showChatMSG.append("������Ѿ��ر�..." + "\n");
			}
		}

	}

	/**
	 * ������
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new ServerFrame();
	}

	/**
	 * �����¼�����
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

			// ȡ��ѡ�е��к�
			int index = heroTable.getSelectedRow();

			// ����ѡ�û������浽str��
			str = tableModel.getValueAt(index, 0).toString();
		}

	}

	/*
	 * �������˷�����Ϣ
	 */
	public void sendMsgToAllClient(String msg) {
		Node node = null;
		for (int i = 0; i < cll.getClientCount(); i++) {
			node = cll.findNodeByIndex(i);
			try {
				node.output.writeUTF("ϵͳ��Ϣ");
				node.output.flush();
				node.output.writeUTF(msg);
				node.output.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * ��ָ���ͻ��˷�����Ϣ
	 */
	public void sendMsgToClient(String name, String msg) {
		Node node = cll.findNodeByName(name);
		try {
			node.output.writeUTF("ϵͳ��Ϣ");
			node.output.flush();
			node.output.writeUTF(msg);
			node.output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ��Ӧ�س���������Ϣ
	@Override
	public void keyPressed(KeyEvent e) {
		c = Calendar.getInstance();
		f = new SimpleDateFormat("HH:mm:ss");
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			e.consume(); // ��һ���������
			if (str == null) {
				JOptionPane.showMessageDialog(null, "��ѡ����ܶ���");
				return;
			}
			if (str == "������") {
				showChatMSG.append(f.format(c.getTime()) + "�� ��������������˵:" + '\n' + serverMSG.getText() + '\n');
				sendMsgToAllClient(serverMSG.getText());
				serverMSG.setText("");
			} else {
				showChatMSG.append(f.format(c.getTime()) + "�� ��������" + str + " ˵:" + '\n' + serverMSG.getText() + '\n');
				sendMsgToClient(str, serverMSG.getText());
				serverMSG.setText("");
			}
		}
	}

	// ��������������

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}
}
