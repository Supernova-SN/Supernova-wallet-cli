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
	String[] title = {"交易","转出地址","金额","时间"};
	
	
	public TransOutPanel(JFrame parent) {
		this.setBounds(0, 0, 1000, 700);
		
		Font font = new Font("微软雅黑",Font.PLAIN,24);
		Font font20 = new Font("微软雅黑",Font.PLAIN,20);
		
		groupbox = new JPanel();
		groupbox.setLayout(null);
//		groupbox.setPreferredSize(new Dimension(780, 100));
		groupbox.setBounds(10, 10, 970, 100);
		Border border = BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLUE);
		groupbox.setBorder(BorderFactory.createTitledBorder(border,""));   //添加边框文字
		this.add(groupbox);
		
		
		JLabel lbAddress = new JLabel("账户地址");
		lbAddress.setBounds(30, 30, 150, 50);
		lbAddress.setFont(font);
		JLabel lbAdd = new JLabel(UI.address);
		lbAdd.setBounds(180, 30, 720, 50);
		lbAdd.setFont(font);
		JButton jbQuerry = new JButton("查询");
		jbQuerry.setBounds(875, 30, 90, 50);
		jbQuerry.setFont(font);		
		
		groupbox.add(lbAddress);
		groupbox.add(lbAdd);
		groupbox.add(jbQuerry);
		
		String[][] trans = { {"","","",""}	};
		
		
		
		//特殊的model，只有第0列允许编辑
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
		JTableHeader head = table.getTableHeader(); // 创建表格标题对象
        head.setPreferredSize(new Dimension(head.getWidth(), 40));// 设置表头大小
        table.setRowHeight(40); // 设置表格行高
        head.setFont(font);     // 设置表格字体
        table.setFont(font20);
        setColumnSize(table,0,280,280,280);
        setColumnSize(table,1,280,280,280);
        setColumnSize(table,2,170,170,170);
        setColumnAlignment(table,1,SwingConstants.RIGHT);
        setColumnAlignment(table,2,SwingConstants.RIGHT);
        setColumnAlignment(table,3,SwingConstants.CENTER);
        //table.setEnabled(false);   //禁止编辑
        
        TableColumn txid = table.getColumnModel().getColumn(0);
        txid.setCellEditor(new MyCellEditor1());
        TableColumn address = table.getColumnModel().getColumn(1);
        address.setCellEditor(new MyCellEditor1());
		JScrollPane sp = new JScrollPane(table);
		sp.setBounds(10, 120, 970, 500);
		
		this.add(sp);
		
		
		this.setLayout(null);
		
		//查询转出交易
		jbQuerry.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {				
//				System.out.println("Easy jbQuerry Thread start...");
				logger.info("Easy jbQuerry Thread start...");
				UI.cmd =2;
				WalletThread transferThread = new WalletThread(2, UI.address, 0, 1000);//新生成一个处理事务线程
//				System.out.println("----0000 ");
				transferThread.start();//启动事务线程
//				System.out.println("----1111 ");
				(new ThreadWaitingDialog(parent, transferThread ,"正在执行，请等待......")).start();
//				System.out.println("----2222 ");
				
			}
		});	
		
		
	}
	
	/**
	 * 设置列表某一列的宽度
	 * 
	 * @param i
	 * @param width
	 */
	public static void setColumnSize(JTable table, int i, int preferedWidth, int maxWidth, int minWidth) {
		// 表格的列模型
		TableColumnModel cm = table.getColumnModel();
		// 得到第i个列对象
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
	
	//重新加载查询后的数据，更新表格内容
	public void refreshTable() {
		//通过下面这句重新设置数据源，实现Jtable的更新		
		//倒序排列
		newTableModel.setRowCount(0);
		for(int i=UI.data.size()-1; i>=0; i--) {
			Vector v = UI.data.get(i);
			newTableModel.addRow(v);	
		}		
		newTableModel.fireTableDataChanged();// 同时通知JTabel数据对象更改, 重绘界面
		
	}
	
	

}

//表格的编辑器，用于设定编辑状态下字体大小
class MyCellEditor1 extends DefaultCellEditor{
	
	private JTextField field = new JTextField(40);
	public MyCellEditor1() {
		super(new JTextField());
	}
	
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		field.setText(value.toString());
		Font font20 = new Font("微软雅黑",Font.PLAIN,20);
		field.setFont(font20);
		return field;
		
	}
	
	public Object getCellEditorValue() {
		return field.getText();
	}
}

