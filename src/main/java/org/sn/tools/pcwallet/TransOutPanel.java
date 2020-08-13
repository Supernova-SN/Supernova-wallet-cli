package org.sn.tools.pcwallet;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransOutPanel extends JPanel{
	private static final Logger logger = LoggerFactory.getLogger("TransOutPanel");
	
	JPanel groupbox; 
	JButton btn_transfer;
	JButton btn_cancel;
	
	DefaultTableModel newTableModel;
	JTable table;
	String[] title = {"����","ת����ַ","���","ʱ��"};
	
	
	public TransOutPanel(JFrame parent) {
		this.setBounds(0, 0, 1000, 700);
		
		Font font = new Font("΢���ź�",Font.PLAIN,24);
		Font font20 = new Font("΢���ź�",Font.PLAIN,20);
		
		groupbox = new JPanel();
		groupbox.setLayout(null);
//		groupbox.setPreferredSize(new Dimension(780, 100));
		groupbox.setBounds(10, 10, 970, 100);
		Border border = BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLUE);
		groupbox.setBorder(BorderFactory.createTitledBorder(border,""));   //��ӱ߿�����
		this.add(groupbox);
		
		
		JLabel lbAddress = new JLabel("�˻���ַ");
		lbAddress.setBounds(30, 30, 150, 50);
		lbAddress.setFont(font);
		JLabel lbAdd = new JLabel(UI.address);
		lbAdd.setBounds(180, 30, 720, 50);
		lbAdd.setFont(font);
		JButton jbQuerry = new JButton("��ѯ");
		jbQuerry.setBounds(875, 30, 90, 50);
		jbQuerry.setFont(font);		
		
		groupbox.add(lbAddress);
		groupbox.add(lbAdd);
		groupbox.add(jbQuerry);
		
		String[][] trans = { {"","","",""}	};
		
		
		
		//�����model��ֻ�е�0������༭
		newTableModel = new DefaultTableModel(trans,title){  
            @Override  
            public boolean isCellEditable(int row,int column){  
            	if(column == 0 || column == 1){  
        	       return true;  
        	    }else{  
        	       return false;  
        	    }  
            }  
        };  
		table = new JTable(newTableModel);
		JTableHeader head = table.getTableHeader(); // �������������
        head.setPreferredSize(new Dimension(head.getWidth(), 40));// ���ñ�ͷ��С
        table.setRowHeight(40); // ���ñ���и�
        head.setFont(font);     // ���ñ������
        table.setFont(font20);
        setColumnSize(table,0,280,280,280);
        setColumnSize(table,1,280,280,280);
        setColumnSize(table,2,170,170,170);
        setColumnAlignment(table,1,SwingConstants.RIGHT);
        setColumnAlignment(table,2,SwingConstants.RIGHT);
        setColumnAlignment(table,3,SwingConstants.CENTER);
        //table.setEnabled(false);   //��ֹ�༭
        
        TableColumn txid = table.getColumnModel().getColumn(0);
        txid.setCellEditor(new MyCellEditor1());
        TableColumn address = table.getColumnModel().getColumn(1);
        address.setCellEditor(new MyCellEditor1());
		JScrollPane sp = new JScrollPane(table);
		sp.setBounds(10, 120, 970, 500);
		
		this.add(sp);
		
		
		this.setLayout(null);
		
		//��ѯת������
		jbQuerry.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {				
//				System.out.println("Easy jbQuerry Thread start...");
				logger.info("Easy jbQuerry Thread start...");
				UI.cmd =2;
				WalletThread transferThread = new WalletThread(2, UI.address, 0, 1000);//������һ�����������߳�
//				System.out.println("----0000 ");
				transferThread.start();//���������߳�
//				System.out.println("----1111 ");
				(new ThreadWaitingDialog(parent, transferThread ,"����ִ�У���ȴ�......")).start();
//				System.out.println("----2222 ");
				
			}
		});	
		
		
	}
	
	/**
	 * �����б�ĳһ�еĿ��
	 * 
	 * @param i
	 * @param width
	 */
	public static void setColumnSize(JTable table, int i, int preferedWidth, int maxWidth, int minWidth) {
		// ������ģ��
		TableColumnModel cm = table.getColumnModel();
		// �õ���i���ж���
		TableColumn column = cm.getColumn(i);
		column.setPreferredWidth(preferedWidth);
		column.setMaxWidth(maxWidth);
		column.setMinWidth(minWidth);
	}
	
	public static void setColumnAlignment(JTable table, int i, int alignment) {
		 TableColumn   column=table.getColumnModel().getColumn(i);
		 DefaultTableCellRenderer render = new DefaultTableCellRenderer();
		 render.setHorizontalAlignment(alignment); //SwingConstants.CENTER;
		 column.setCellRenderer(render); 
	}
	
	//���¼��ز�ѯ������ݣ����±������
	public void refreshTable() {
		//ͨ���������������������Դ��ʵ��Jtable�ĸ���		
		//��������
		newTableModel.setRowCount(0);
		for(int i=UI.data.size()-1; i>=0; i--) {
			Vector v = UI.data.get(i);
			newTableModel.addRow(v);	
		}		
		newTableModel.fireTableDataChanged();// ͬʱ֪ͨJTabel���ݶ������, �ػ����
		
	}
	
	

}

//���ı༭���������趨�༭״̬�������С
class MyCellEditor1 extends DefaultCellEditor{
	
	private JTextField field = new JTextField(40);
	public MyCellEditor1() {
		super(new JTextField());
	}
	
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		field.setText(value.toString());
		Font font20 = new Font("΢���ź�",Font.PLAIN,20);
		field.setFont(font20);
		return field;
		
	}
	
	public Object getCellEditorValue() {
		return field.getText();
	}
}

