package server;

/**
 * ����
 * 
 * @author HaiSong.Zhang
 *
 */
public class ClientLinkList {

	private Node root;
	private Node pointer;
	/** ������ */
	private int clientCount = 0;

	/** ���췽�� */
	public ClientLinkList() {
		root = new Node();
		root.next = null;
		pointer = null;
		clientCount = 0;
	}

	/**
	 * β�巨
	 * 
	 * @param newNode
	 *            ��Ҫ��ӵĽڵ�
	 */
	public void addNodeToTail(Node newNode) {

		pointer = root;
		while (pointer.next != null) {
			pointer = pointer.next;
		}
		pointer.next = newNode;
		newNode.next = null;
		clientCount++;
	}

	/**
	 * �����û������ҽڵ�
	 * 
	 * @param name
	 *            �û���
	 * @return ���ҵ��Ľڵ�
	 */
	public Node findNodeByName(String name) {
		if (clientCount == 0)
			return null;
		pointer = root;
		while (pointer.next != null) {
			pointer = pointer.next;
			if (pointer.getUsername().equalsIgnoreCase(name)) {
				return pointer;
			}
		}
		return null;
	}

	/**
	 * ����������ѯ�ڵ�
	 * 
	 * @param index
	 * @return
	 */
	public Node findNodeByIndex(int index) {
		if (index < 0 || clientCount == 0) {
			return null;
		}
		pointer = root;
		int k = 0;
		while (k <= index) {
			if (pointer.next == null) {
				return null;
			} else {
				pointer = pointer.next;
			}
			k++;
		}
		return pointer;
	}

	/**
	 * ɾ���ڵ�
	 * 
	 * @param name
	 */
	public void delNode(String name) {

		Node node = findNodeByName(name);
		pointer = root;
		while (pointer.next != null) {
			if (pointer.next == node) {
				pointer.next = node.next;
				clientCount--;
				break;
			}
			pointer = pointer.next;
		}
	}

	// �������߿ͻ�������
	public int getClientCount() {
		return clientCount;

	}

	public String[] toArray(int i) {
		String[] str = { findNodeByIndex(i).username };
		return str;
	}

}
