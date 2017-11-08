package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * ע���¼����
 * 
 * @author HaiSong.Zhang
 *
 */
public class IndexFrame extends JFrame {

	private Font font1 = new Font("΢���ź�", Font.LAYOUT_LEFT_TO_RIGHT, 15);
	private Font font2 = new Font("΢���ź�", Font.BOLD, 12);

	private JLabel lb1 = new JLabel("�û���");
	private JTextField tf = new JTextField();
	private JLabel lb2 = new JLabel("���룺");
	private JPasswordField pf = new JPasswordField();

	private JButton btn1 = new JButton("��¼");
	private JButton btn2 = new JButton("ע��");

	Database db = new Database();

	/**
	 * �����ʼ������
	 */
	public void IFrame() {

		setTitle("��¼����");
		setBounds(450, 250, 400, 250);
		setBackground(Color.blue);

		setLayout(null);
		add(lb1);
		add(lb2);
		add(tf);
		add(pf);
		add(btn1);
		add(btn2);

		lb1.setBounds(50, 50, 40, 30);
		lb2.setBounds(50, 90, 40, 30);
		lb1.setFont(font2);
		lb2.setFont(font2);

		tf.setBounds(100, 50, 200, 30);
		tf.setFont(font1);

		pf.setBounds(100, 90, 200, 30);

		btn1.setBounds(110, 140, 70, 30);
		btn1.setFont(font2);
		btn1.setFocusPainted(false); // ȥ����ť������Χ�Ľ����
		btn1.addActionListener(new Btn1Listener());

		btn2.setBounds(210, 140, 70, 30);
		btn2.setFont(font2);
		btn2.setFocusPainted(false);
		btn2.addActionListener(new Btn2Listener());

		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {

				System.exit(0);
			}

		});
		setResizable(false);
		setVisible(true);

	}

	/**
	 * ������ʽ��֤���������Ƿ���Ϲ涨
	 * 
	 * @return
	 */
	public boolean match() {

		// ��֤�û������
		String str1 = tf.getText();
		Pattern p = Pattern.compile("\\w{6,20}");
		Matcher m1 = p.matcher(str1);

		// ��֤�����
		char[] c = pf.getPassword();
		String str2 = String.valueOf(c);
		Matcher m2 = p.matcher(str2);

		return m1.matches() && m2.matches();
	}

	/**
	 * ��¼��ť��Ӧ
	 * 
	 * @author HaiSong.Zhang
	 *
	 */
	private class Btn1Listener implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			if (match()) { // �ж��������Ƿ���ȷ
				db.selectDatabase(tf.getText());
				if (db.result) { // ������ݿ�����������û�
					// �������
					setVisible(false);
					dispose(); // ����������s
					new ClientFrame(tf.getText());
				} else
					JOptionPane.showMessageDialog(null, "�û�δע��", "��������", JOptionPane.ERROR_MESSAGE);
			} else
				JOptionPane.showMessageDialog(null, "�û�������ӦΪ6~12λ����ĸ���֡��»������", "��ʽ����", JOptionPane.ERROR_MESSAGE);
		}

	}

	/**
	 * ע�ᰴť��Ӧ
	 * 
	 * @author HaiSong.Zhang
	 *
	 */
	private class Btn2Listener implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			if (match()) { // �ж��������Ƿ���ȷ
				db.selectDatabase(tf.getText());
				if (db.result)
					JOptionPane.showMessageDialog(null, "�û���ע��", "��������", JOptionPane.ERROR_MESSAGE);
				else {
					db.insertDatabase(tf.getText(), String.valueOf(pf.getPassword()));
					tf.setText("");
					pf.setText("");
					JOptionPane.showMessageDialog(null, "��ϲ��ע��ɹ���");
				}
			} else
				JOptionPane.showMessageDialog(null, "��ʽ����", "�û�������ӦΪ6~12λ����ĸ���֡��»������", JOptionPane.ERROR_MESSAGE);
		}

	}

	/**
	 * ��Ԫ����
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new IndexFrame().IFrame();
	}
}
