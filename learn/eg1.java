import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

class MyModel extends AbstractTableModel
{
private Object data[][];
private String title[] = {"A","B","C"};
MyModel()
{
data = new Object[2][3];
data[0][0] = "one";
data[0][1] = new JButton("accept");
data[0][2] = new JButton("decline");
data[1][0] = "Two";
data[1][1] = new JButton("accept");
data[1][2] = new JButton("decline");
}
public int getRowCount()
{
return this.data.length;
}
public int getColumnCount()
{
return this.title.length;
}
public Object getValueAt(int row,int column)
{
return data[row][column];
}
public boolean isCellEditable(int r,int c)
{
if(c==1) return true;
return false;
}
public Class getColumnClass(int c)
{
return getValueAt(0,c).getClass();
}
public void setValueAt(Object data,int r,int c)
{
System.out.println(r+","+c+","+data);
}
}
class Whatever extends JFrame
{
private JTable table;
MyModel model;
Whatever()
{
model = new MyModel();
table = new JTable(model);
TableColumnModel tableModel = table.getColumnModel();
tableModel.getColumn(0).setPreferredWidth(200);
table.getColumn("B").setCellRenderer(new ButtonRenderer());
table.getColumn("B").setCellEditor(new ButtonCellEditor());
table.getColumn("C").setCellRenderer(new ButtonRenderer());
table.getColumn("C").setCellEditor(new ButtonCellEditor());
Container c= getContentPane();
c.setLayout(new BorderLayout());
c.add(table,BorderLayout.CENTER);
setLocation(10,10);
setSize(400,400);
setVisible(true);
}
public static void main(String gg[])
{
Whatever w = new Whatever();
}
}
class ButtonRenderer implements TableCellRenderer
{
public Component getTableCellRendererComponent(JTable table,Object value,boolean a,boolean b,int row,int column)
{
System.out.println(value.toString());
System.out.println(row+","+column);
return (JButton)value;
}
}
class ButtonCellEditor extends DefaultCellEditor
{
private JButton button;
private boolean isClicked;
private int row,col;
ButtonCellEditor()
{
super(new JCheckBox());
button = new JButton();
button.setOpaque(true);
button.addActionListener(new ActionListener()
{
public void actionPerformed(ActionEvent ev)
{
System.out.println("Great");
fireEditingStopped();
}
});
}
public Component getTableCellEditorComponent(JTable table,Object value,boolean a,int row,int column)
{
System.out.println("getTableCellEditorComponent got called");
this.row = row;
this.col = col;
button.setForeground(Color.black);
button.setBackground(UIManager.getColor("Button.background"));
button.setText("whatever");
isClicked= true;
return button;
}
public Object getCellEditorValue()
{
System.out.println("getCellEditorValue got called");
return "cool";
}
public boolean stopCellEditing()
{
isClicked = false;
return super.stopCellEditing();
}
public void fireEditingStopped()
{
//do whatever is required
super.fireEditingStopped();
}
}
