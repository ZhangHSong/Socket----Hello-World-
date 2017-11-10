package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

/**
 * 客户端聊天界面
 * 
 * @author HaiSong.Zhang
 *
 */
public class ClientFrame extends JFrame {

	private JTextArea ta = new JTextArea();
	private JTextField tf = new JTextField();
	private JLabel label = new JLabel();
	private JButton btn = new JButton("发送");
	private JButton btn1 = new JButton("+");
	private JPanel panel = new JPanel();

	private boolean started = true;
	private String username = null;
	public static final int serverPort = 8888;// 服务端端口号

	// 定义表格
	private JScrollPane jScrollPane1 = null;// 滚动条面板
	private JTable heroTable = null;// 表格组件
	private ListSelectionModel listSelectionModel = null;
	// 表格头部
	private String[] tableColName = { "我的好友" };
	private String[][] tableColValue = null;// 表格的值
	private GridBagLayout gridBag = new GridBagLayout();// 布局管理器
	private GridBagConstraints gridBagCon = null;
	private Socket s = null;
	private DataInputStream input = null;
	private DataOutputStream output = null;
	private FileOutputStream fOutput = null;
	private FileInputStream fInput = null;
	private DefaultTableModel tableModel = null;
	private String obj = null;
	private Calendar c = null;
	private SimpleDateFormat f = null;
	private Font font2 = new Font("微软雅黑", 0, 15);
	private JScrollPane MSGScrollPane;// 信息显示窗口的滚动条
	private JFileChooser chooser = null;
	private JLabel showImage;// 显示图片

	/**
	 * 构造方法
	 */
	public ClientFrame(String username) {

		this.username = username;
		LanchFrame();
	}

	/**
	 * 界面初始化
	 */
	public void LanchFrame() {

		// 界面基础设置
		this.setTitle("Hello World");
		this.setBounds(450, 200, 470, 450);

		// 绝对定位
		setLayout(null);

		panel.setLayout(gridBag);
		tableColValue = new String[2][1];
		tableColValue[0][0] = "所有人";
		tableColValue[1][0] = "服务器";
		tableModel = new DefaultTableModel(tableColValue, tableColName);
		// 初始化表格中的信息
		heroTable = new JTable(tableModel) {
			public boolean isCellEditable(int row, int column) {
				return false; // 不可编辑
			}
		};
		heroTable.setFont(new Font("微软雅黑", 0, 15));
		heroTable.setRowHeight(30);
		heroTable.setPreferredScrollableViewportSize(new Dimension(120, 320));
		listSelectionModel = heroTable.getSelectionModel();
		// 设置表格选择方式:选择一行
		listSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listSelectionModel.addListSelectionListener(new SelectListener());
		jScrollPane1 = new JScrollPane(heroTable);
		jScrollPane1.setPreferredSize(new Dimension(120, 320));
		gridBagCon = new GridBagConstraints();
		gridBagCon.gridx = 0;
		gridBagCon.gridy = 1;
		gridBagCon.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(jScrollPane1, gridBagCon);
		panel.add(jScrollPane1);
		panel.setBounds(0, 0, 120, 320);

		// 当前聊天好友名
		label.setBounds(120, 0, 350, 30);

		// 对文本区域操作
		ta.setEditable(false);
		ta.setFont(font2);
		ta.setLineWrap(true);
		// 为聊天信息框添加滚动条
		MSGScrollPane = new JScrollPane(ta, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		MSGScrollPane.setPreferredSize(new Dimension(400, 200));
		MSGScrollPane.setBounds(120, 30, 350, 360);
		this.add(MSGScrollPane);
		// 在重新设置某个容器中的组件的大小以后，需要调用包含这个组件的容器的revalidate（）方法。
		// Revalidate（）方法会重新计算容器内所有的组建的大小，并且对他们进行重新布局
		MSGScrollPane.revalidate();

		showImage = new JLabel();
		showImage.setBounds(0, 310, 120, 120);
		this.add(showImage);

		// 对文本框操作
		tf.setBounds(120, 390, 240, 31);
		tf.addActionListener(new TfListener());

		// 对文件、图片进行操作
		btn1.setBounds(355, 390, 45, 30);
		btn1.setBackground(new Color(250, 250, 250));
		btn1.setFocusPainted(false); // 去掉按钮文字周围的焦点框
		btn1.addActionListener(new Btn1Listener());

		// 对按钮操作
		btn.setBounds(400, 390, 70, 30);
		btn.setBackground(new Color(250, 250, 250));
		btn.setFocusPainted(false); // 去掉按钮文字周围的焦点框
		btn.addActionListener(new BtnListener());

		add(label);
		add(panel);
		add(tf);
		add(btn1);
		add(btn);

		setResizable(false);
		setBackground(Color.white);
		setVisible(true);
		setTitle("Hello World —— " + username);

		connect();

		// 关闭程序时操作
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int flag = JOptionPane.showConfirmDialog(null, "确定要退出客户端吗?", "退出", JOptionPane.YES_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (flag == JOptionPane.YES_OPTION) {
					try {
						output.writeUTF("关闭");
						output.flush();
						s.shutdownOutput();
						s.shutdownInput();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					disconnect();
					System.exit(0);
				}
			}
		});
	}

	public void connect() {

		try {
			s = new Socket("127.0.0.1", serverPort);
			output = new DataOutputStream(s.getOutputStream());
			input = new DataInputStream(s.getInputStream());
			output.writeUTF(username);
			output.flush();
			new Thread(new ClientReceive()).start();
		} catch (ConnectException e) {
			JOptionPane.showMessageDialog(null, "警告", "无法登录,服务器未启动!", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void disconnect() {

		if (started == true)
			started = false;
		CloseResouce.closeSocket(s);
		CloseResouce.closeInput(input);
		CloseResouce.closeOutput(output);
		CloseResouce.closeFileInput(fInput);
		CloseResouce.closeFileOutput(fOutput);
	}

	private class SelectListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {

			// 取得选中的行号
			int index = heroTable.getSelectedRow();

			// 将所选用户名保存到str中
			obj = tableModel.getValueAt(index, 0).toString();
			label.setText(obj);
		}

	}

	public void action() {
		c = Calendar.getInstance();
		f = new SimpleDateFormat("HH:mm:ss");
		String str = tf.getText().trim();// 去掉文本前后的空格
		if (str.equals(""))
			JOptionPane.showMessageDialog(null, "警告", "发送内容不能为空！", JOptionPane.ERROR_MESSAGE);
		else {
			tf.setText("");
			if (obj == null) {
				JOptionPane.showMessageDialog(null, "请选择接受对象");
				return;
			}
			try {
				output.writeUTF("聊天信息");
				output.flush();
				output.writeUTF(obj);
				output.flush();
				output.writeUTF(str);
				output.flush();
				if (obj.equals("所有人")) {
					ta.append(f.format(c.getTime()) + "—" + "对所有人说:" + '\n' + str + '\n');
				} else if (obj.equals("服务器")) {
					ta.append(f.format(c.getTime()) + "—" + "对服务器说:" + '\n' + str + '\n');
				} else {
					ta.append(f.format(c.getTime()) + "(" + username + ")" + '\n' + str + '\n');
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * 文本框监听
	 * 
	 * @author HaiSong.Zhang
	 *
	 */
	private class TfListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			action();
		}

	}

	/**
	 * 附加功能按钮监听
	 * 
	 * @author HaiSong.Zhang
	 *
	 */
	private class Btn1Listener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			chooser = new JFileChooser();
			chooser.setMultiSelectionEnabled(true);
			FileNameExtensionFilter filter1 = new FileNameExtensionFilter("PNG & GIF & JPG Images", "jpg", "gif",
					"png");
			FileNameExtensionFilter filter2 = new FileNameExtensionFilter("TXT & DOC & DOCX File", "txt", "doc",
					"docx");
			FileNameExtensionFilter filter3 = new FileNameExtensionFilter("MP3 & MP4 File", "mp3", "mp4");
			chooser.addChoosableFileFilter(filter1);
			chooser.addChoosableFileFilter(filter2);
			chooser.addChoosableFileFilter(filter3);
			int returnVal = chooser.showOpenDialog(panel);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				if (obj == null) {
					JOptionPane.showMessageDialog(null, "请选择接受对象");
					return;
				}
				try {
					String objname = chooser.getSelectedFile().getName();
					String objLastName = objname.substring(objname.lastIndexOf(".") + 1);
					if (objLastName.equals("png") || objLastName.equals("jpg") || objLastName.equals("gif")) {
						output.writeUTF("图片");
						output.flush();
						output.writeUTF(obj);
						output.flush();
						output.writeUTF(objLastName);
						output.flush();
						fInput = new FileInputStream(chooser.getSelectedFile());
						byte[] sendByte = new byte[1024];
						int length;
						while ((length = fInput.read(sendByte, 0, sendByte.length)) != -1) {
							output.write(sendByte, 0, length);
							output.flush();
							if (length < sendByte.length) {
								// s.shutdownOutput();
								break;
							}
						}
						if (obj.equals("所有人")) {
							ta.append(f.format(c.getTime()) + "— 向所有人都发送了图片" + '\n');
						} else if (obj.equals("服务器")) {
							ta.append(f.format(c.getTime()) + "— 向服务器发送了图片" + '\n');
						} else {
							ta.append(f.format(c.getTime()) + "— 向" + obj + "发送了图片" + '\n');
						}
						ImageIcon image = new ImageIcon(chooser.getSelectedFile().getPath());
						image.setImage(image.getImage().getScaledInstance(120, 120, Image.SCALE_DEFAULT));
						showImage.setIcon(image);
					} else if (objLastName.equals("txt") || objLastName.equals("doc") || objLastName.equals("docx")
							|| objLastName.equals("mp3") || objLastName.equals("mp4")) {
						output.writeUTF("文件");
						output.flush();
						output.writeUTF(obj);
						output.flush();
						output.writeUTF(objLastName);
						output.flush();
						fInput = new FileInputStream(chooser.getSelectedFile());
						byte[] sendByte = new byte[1024];
						int length;
						while ((length = fInput.read(sendByte, 0, sendByte.length)) != -1) {
							output.write(sendByte, 0, length);
							output.flush();
							if (length < sendByte.length) {
								// s.shutdownOutput();
								break;
							}
						}
						if (obj.equals("所有人")) {
							ta.append(f.format(c.getTime()) + "— 向所有人都发送了文件" + '\n');
						} else if (obj.equals("服务器")) {
							ta.append(f.format(c.getTime()) + "— 向服务器发送了文件" + '\n');
						} else {
							ta.append(f.format(c.getTime()) + "— 向" + obj + "发送了文件" + '\n');
						}
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

	}

	/**
	 * 发送按钮监听
	 * 
	 * @author HaiSong.Zhang
	 *
	 */
	private class BtnListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			action();
		}

	}

	private class ClientReceive implements Runnable {

		@Override
		public void run() {
			c = Calendar.getInstance();
			f = new SimpleDateFormat("HH:mm:ss");
			String objLastName = null;
			String startName = null;
			while (started) {
				try {
					String infoType = input.readUTF().toString();// 信息类型
					if (infoType.equals("聊天信息")) {
						startName = input.readUTF().toString();
						String endName = input.readUTF().toString();
						String str = (String) input.readUTF();
						if (endName.equals("所有人")) {
							ta.append(f.format(c.getTime()) + "—" + startName + "对" + endName + " 说:" + '\n' + str
									+ '\n');
						} else {
							ta.append(f.format(c.getTime()) + "(" + startName + ")" + '\n' + str + '\n');
						}
					} else if (infoType.equals("用户列表")) {
						String userlist = input.readUTF().toString();
						String[] userlists = userlist.split("\n");
						int m = tableModel.getRowCount();
						for (int i = m - 1; i >= 0; i--) {
							tableModel.removeRow(i);
						}
						String[] str1 = { "所有人" };
						tableModel.addRow(str1);
						String[] str2 = { "服务器" };
						tableModel.addRow(str2);
						for (int i = 0; i < userlists.length; i++) {
							if (!username.equals(userlists[i])) {
								String[] str3 = { userlists[i] };
								tableModel.addRow(str3);
							}
						}
						tableModel.fireTableDataChanged();
					} else if (infoType.equals("系统消息")) {
						String str = input.readUTF().toString();
						ta.append(f.format(c.getTime()) + "— 系统消息:    " + str + '\n');

					} else if (infoType.equals("图片")) {
						objLastName = input.readUTF().toString();
						startName = input.readUTF().toString();
						int flag = JOptionPane.showConfirmDialog(null, startName + "请求向你发送图片,是否接受?", "接受",
								JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
						if (flag == JOptionPane.YES_OPTION) {
							File objImage = new File("F:\\obj." + objLastName);
							if (!objImage.exists()) {
								objImage.createNewFile();
							}
							fOutput = new FileOutputStream(objImage);
							byte[] buf = new byte[1024];
							int len;
							while ((len = input.read(buf, 0, buf.length)) != -1) {
								fOutput.write(buf, 0, len);
								fOutput.flush();
								if (len < buf.length) {
									// s.shutdownInput();
									break;
								}
							}
							ImageIcon image = new ImageIcon("F:\\obj." + objLastName);
							image.setImage(image.getImage().getScaledInstance(120, 120, Image.SCALE_DEFAULT));
							showImage.setIcon(image);
							ta.append(f.format(c.getTime()) + "—" + "已经接受了" + startName + "向你发送的图片,存储在"
									+ objImage.getPath() + "中" + '\n');
						}

					} else if (infoType.equals("文件")) {
						objLastName = input.readUTF().toString();
						startName = input.readUTF().toString();
						int flag = JOptionPane.showConfirmDialog(null, startName + "请求向你发送文件,是否接受?", "接受",
								JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
						if (flag == JOptionPane.YES_OPTION) {
							File objFile = new File("F:\\obj." + objLastName);
							if (!objFile.exists()) {
								objFile.createNewFile();
							}
							fOutput = new FileOutputStream(objFile);
							byte[] buf = new byte[1024];
							int len;
							while ((len = input.read(buf, 0, buf.length)) != -1) {
								fOutput.write(buf, 0, len);
								fOutput.flush();
								if (len < buf.length) {
									// s.shutdownInput();
									break;
								}
							}
							ta.append(f.format(c.getTime()) + "—" + "你已接受" + startName + "向你发送的文件,存储在"
									+ objFile.getPath() + "中" + '\n');
						} else {
							return;
						}
					}
				} catch (EOFException | SocketException e) {
					//e.printStackTrace();
					disconnect();
					System.exit(0);
				} catch (Exception e) {
					disconnect();
					System.exit(0);
					//e.printStackTrace();
				}
			}
		}

	}

/*	
	 * 单元测试
	 
	public static void main(String[] args) {
		new ClientFrame("test");
	}*/
}
