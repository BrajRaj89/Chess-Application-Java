package com.chess.client;
import com.nframework.client.*;
import com.chess.common.*;
import com.chess.board.*;
import com.google.gson.*;
import com.google.gson.reflect.*; 
import javax.swing.*;
import javax.swing.Timer;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.table.*;
import javax.swing.border.*;
import java.net.*;
public class ChessUI extends JFrame
{
private String username;
private AvailableUsersListModel availableUsersListModel;
private JTable availableUsersList;
private JScrollPane availableUsersListScrollPane;
private InvitationTableModel invitationTableModel;
private JTable invitationMessagesList;
private JScrollPane invitationMessagesListScrollPane;
private Timer timer,t1,t2,t3,t4;
private Container container;
private NFrameworkClient client;
private JPanel panel1;
private JPanel panel2;
private JPanel panel3;
private JLabel messageLabel;
private int w,h,cw,ch;
private Font messageFont;
private ChessBoard chessBoard;
private Gson gson;
public ChessUI()
{
this.client = new NFrameworkClient();
initComponents();
setApperance();
Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
w =700;
h = 500;
cw = d.width/2-w/2;
ch = d.height/2-h/2;
setSize(w,h);
setLocation(cw,ch);
}
private void initComponents()
{
gson = new Gson();
messageFont = new Font("monospaced",Font.BOLD,16);
panel1 = new JPanel();
panel1.setLayout(new BorderLayout());
Font headFont = new Font("Californian FB",Font.BOLD,16);
JLabel availableLabel = new JLabel("Available Users");
availableLabel.setFont(headFont);
panel1.add(availableLabel,BorderLayout.NORTH);
availableUsersListModel = new AvailableUsersListModel();
this.availableUsersList = new JTable(availableUsersListModel);
this.availableUsersList.getColumn(" ").setCellRenderer(new AvailableUsersListButtonRenderer());
this.availableUsersList.getColumn(" ").setCellEditor(new AvailableUsersListButtonCellEditor());
this.availableUsersListScrollPane = new JScrollPane(this.availableUsersList,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
panel2 = new JPanel();
panel2.setLayout(new BorderLayout());
JLabel invitationLabel  = new JLabel("Invitation Messages");
invitationLabel.setFont(headFont);
panel2.add(invitationLabel,BorderLayout.NORTH);
invitationTableModel = new InvitationTableModel();
invitationMessagesList = new JTable(invitationTableModel);
invitationMessagesList.setRowHeight(20);
invitationMessagesList.getColumn("✔").setPreferredWidth(90);
invitationMessagesList.getColumn("✖").setPreferredWidth(90);
invitationMessagesList.getColumn("✔").setCellRenderer(new DualButtonRenderer());
invitationMessagesList.getColumn("✔").setCellEditor(new DualButtonEditor());
invitationMessagesList.getColumn("✖").setCellRenderer(new DualButtonRenderer());
invitationMessagesList.getColumn("✖").setCellEditor(new DualButtonEditor());
this.invitationMessagesListScrollPane = new JScrollPane(this.invitationMessagesList,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
availableUsersListScrollPane.setPreferredSize(new Dimension(250,200));
invitationMessagesListScrollPane.setPreferredSize(new Dimension(300,200));
panel1.add(availableUsersListScrollPane);
panel2.add(invitationMessagesListScrollPane);
panel3 = new JPanel();
LayoutManager boxLayout = new BoxLayout(panel3,BoxLayout.X_AXIS);
panel3.setLayout(boxLayout);
JLabel gameLabel = new JLabel("Welcome to chess Play");
gameLabel.setFont(new Font("Monospaced",Font.BOLD,16));
panel3.setPreferredSize(new Dimension(670,110));
Border border = new LineBorder(new Color(0, 86, 109), 1, true);
panel3.setBorder(border);
JButton leaveBtn = new JButton("Leave game ➜");
leaveBtn.addActionListener(_->{
messageLabel = new JLabel("Are you sure? Exit Game");
messageLabel.setFont(messageFont);
messageLabel.setForeground(new Color(48,55,147));
int selected = JOptionPane.showConfirmDialog(null,messageLabel,"Select",JOptionPane.YES_NO_OPTION);
if(selected==0)
{
System.exit(0);
}
});
leaveBtn.setFont(new Font("Serif",Font.BOLD,15));
panel3.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); 
panel3.add(gameLabel);
panel3.add(Box.createHorizontalGlue());
panel3.add(leaveBtn);
container = getContentPane();
}
private void setApperance()
{
}
private void addListeners()
{

t4 = new javax.swing.Timer(5000,new ActionListener() {
public void actionPerformed(ActionEvent ae)
{
System.out.println("got called timer t4");
try
{
Object moveObject = client.execute("/ChessServer/getMove",username);
String moveObjString = gson.toJson(moveObject);
Move move = gson.fromJson(moveObjString,Move.class);
if(move==null)
{
messageLabel = new JLabel("No moves");
messageLabel.setFont(messageFont);
JOptionPane.showMessageDialog(null,messageLabel);
}
chessBoard.updateBoard(move);
}catch(Exception e)
{
//do nothing
}
}
});
t4.setRepeats(false);
t1 = new javax.swing.Timer(2000,new ActionListener(){
public void actionPerformed(ActionEvent ae)
{
try
{
Object result= client.execute("/ChessServer/getMessages",username);
String resultJson = gson.toJson(result,Object.class);
java.lang.reflect.Type listType = new TypeToken<java.util.List<Message>>() {}.getType();
java.util.List<Message> messages = gson.fromJson(resultJson, listType);
if(messages!=null)
{
for (Message message : messages) 
{
if(message.type==MESSAGE_TYPE.CHALLENGE)
{
invitationTableModel.addMessage(message,()->{
try 
{
client.execute("/ChessServer/removeMessage",username,"Challenge");
}catch (Exception ex) 
{
System.out.println("Exception occured "+ex.getMessage());
}
});
}else if(message.type==MESSAGE_TYPE.CHALLENGE_ACCEPTED)
{
startUI(message.fromUsername);
availableUsersListModel.refreshUsersList();
try 
{
client.execute("/ChessServer/removeMessage",username,"Accepted");
}catch(Exception ex) 
{
//do nothing
}
}else if(message.type==MESSAGE_TYPE.CHALLENGE_REJECTED)
{
JLabel messagLabel = new JLabel("Your invitation is rejected by user "+message.fromUsername);
messagLabel.setFont(new Font("Century",Font.PLAIN,14));
messagLabel.setBackground(Color.white);
messagLabel.setForeground(new Color(0,51,102)); 
JOptionPane.showMessageDialog(null,messagLabel);
availableUsersListModel.refreshUsersList();
try 
{
client.execute("/ChessServer/removeMessage",username,"Rejected");
}catch (Exception ex) 
{
//do nothing
}
}else if(message.type==MESSAGE_TYPE.START_GAME) 
{
try 
{
client.execute("/ChessServer/removeMessage",username,"StartGame");
}catch (Exception ex) 
{
//do nothing
}
startGame(username,message.fromUsername);
t4.start();
}else if(message.type==MESSAGE_TYPE.END_GAME)
{
try
{
client.execute("/ChessServer/removeMessage",username,"EndGame");
}catch(Exception e)
{
//do nothing
}
showResultUI(username,message.fromUsername,"Won");
}
}//for loop ends 
}// if ends
}catch(Exception t)
{
System.out.println("Exception occured "+t.getMessage());
}
}
});
t1.start();
timer = new javax.swing.Timer(3000,new ActionListener(){
public void actionPerformed(ActionEvent ae)
{
timer.stop();
try
{
@SuppressWarnings("unchecked")
java.util.List<String> users =(java.util.List<String>)client.execute("/ChessServer/getMembers",username);
ChessUI.this.availableUsersListModel.setUsers(users);
timer.start();
}catch(Throwable t)
{
JOptionPane.showMessageDialog(ChessUI.this,t.toString());
}
} 
});
addWindowListener(new WindowAdapter(){
public void windowClosing(WindowEvent e)
{
try
{
System.out.print("logout");
client.execute("/ChessServer/logout",username);
}catch(Throwable t)
{
JOptionPane.showMessageDialog(ChessUI.this,t.toString());
}
System.exit(0);
}
});
// now all is setup,let us starts the timer
timer.start();
}
public void showHomeUI()
{
setTitle(this.username);
hideUI();
container.setLayout(new BorderLayout());
setSize(w,h);
setLocation(cw,ch);
panel1.setBackground(Color.white);
panel2.setBackground(Color.white);
panel3.setBackground(Color.white);
container.add(panel1,BorderLayout.EAST);
container.add(panel2,BorderLayout.WEST);
container.add(panel3,BorderLayout.SOUTH);
container.revalidate();
container.repaint();
setVisible(true);
}
public void hideUI()
{
container.removeAll();
container.revalidate();
container.repaint();
container.setLayout(null);
}
public void showResultUI(String winner,String losser,String rst)
{
hideUI();
JLabel heading = new JLabel("Result");
heading.setFont(new Font("Monospaced",Font.BOLD,60));
heading.setForeground(new Color(0,51,107));
Font btnFont = new Font("Serif",Font.BOLD,20);
Font labelFont = new Font("COPPERPLATE GOTHIC LIGHT",Font.BOLD,20);
Font fieldFont = new Font("Footlight MT Light",Font.BOLD,25);
JTextField whiteStatus = new JTextField();
whiteStatus.setPreferredSize(new Dimension(300,50));
JTextField blackStatus = new JTextField();
blackStatus.setPreferredSize(new Dimension(300,50));
ImageIcon tropyIcon = new ImageIcon(getClass().getResource("/icons/tropy.png"));
 Image tropyImage = tropyIcon.getImage();
 Image newImage = tropyImage.getScaledInstance(130,130,Image.SCALE_SMOOTH);
 JLabel imageLabel = new JLabel(new ImageIcon(newImage));
JLabel whiteLabel = new JLabel("White Status");
JLabel blackLabel = new JLabel("Black Status");
heading.setBounds(250,0,300,50);
imageLabel.setBounds(220,60,260,150);
whiteLabel.setFont(labelFont);
whiteStatus.setFont(fieldFont);
whiteLabel.setBounds(120,155,200,200);
whiteStatus.setBounds(290,230,300,40);
blackLabel.setFont(labelFont);
blackStatus.setFont(fieldFont);
blackLabel.setBounds(120,195,200,200);
blackStatus.setBounds(290,274,300,40);
JTextArea message = new JTextArea();
message.setFont(fieldFont);
message.setBounds(120,320,470,40);
message.setEditable(false);
message.setText("You have "+rst+" the match");
JButton RematchBtn = new JButton("Rematch");
JButton OkBtn = new JButton("Ok");
RematchBtn.setFont(btnFont);
OkBtn.setFont(btnFont);
RematchBtn.setBounds(210,390,130,40);
OkBtn.setBounds(360,390,130,40);
whiteStatus.setEditable(false);
blackStatus.setEditable(false);
blackStatus.setText(winner+" Won");
whiteStatus.setText(losser+" Loss");
whiteStatus.setBackground(Color.white);
blackStatus.setBackground(Color.white);
OkBtn.addActionListener(_->{
showHomeUI();
});
RematchBtn.addActionListener(_->{
rematch();
});
JPanel resultPanel  = new JPanel();
resultPanel.setLayout(null);
resultPanel.add(heading);
resultPanel.add(imageLabel);
resultPanel.add(whiteLabel);
resultPanel.add(whiteStatus);
resultPanel.add(blackLabel);
resultPanel.add(blackStatus);
resultPanel.add(message);
resultPanel.add(RematchBtn);
resultPanel.add(OkBtn);
setSize(w,h);
setLocation(cw,ch);
container.setLayout(new BorderLayout());
container.add(resultPanel,BorderLayout.CENTER);
setDefaultCloseOperation(EXIT_ON_CLOSE);
setVisible(true);	
}
public void loginUI()
{
JLabel headingLogin = new JLabel("Chess Game Login");
headingLogin.setFont(new Font("Lucida Calligraphy",Font.BOLD,40));
headingLogin.setForeground(new Color(0,51,102));
headingLogin.setBounds(150,130,600,80);
Font btnFont = new Font("Serif",Font.BOLD,17);
Font labelFont = new Font("COPPERPLATE GOTHIC LIGHT",Font.BOLD,17);
Font fieldFont = new Font("Courier",Font.PLAIN,18);
ImageIcon chessImageIcon = new ImageIcon(getClass().getResource("/icons/chessboard.png"));
 Image chessImage = chessImageIcon.getImage();
 Image newImage = chessImage.getScaledInstance(130,130,Image.SCALE_SMOOTH);
 JLabel imageLabel = new JLabel(new ImageIcon(newImage));
 imageLabel.setBounds(270,10,131,131);
JTextField usernameField = new JTextField();
usernameField.setPreferredSize(new Dimension(300,45));
JTextField passwordField = new JTextField();
passwordField.setPreferredSize(new Dimension(300,45));
JLabel usernameLabel = new JLabel("Username");
JLabel passwordLabel = new JLabel("Password");
usernameLabel.setFont(labelFont);
usernameField.setFont(fieldFont);
usernameLabel.setBounds(130,160,200,180);
usernameField.setBounds(250,230,300,40);
passwordLabel.setFont(labelFont);
passwordField.setFont(fieldFont);
passwordLabel.setBounds(130,195,200,180);
passwordField.setBounds(250,274,300,40);
JButton loginBtn = new JButton("Login");
JButton createAc = new JButton("New Account");
loginBtn.setFont(btnFont);
createAc.setFont(btnFont);
loginBtn.setBounds(180,350,150,40);
createAc.setBounds(370,350,150,40);
JPanel loginPanel  = new JPanel();
loginPanel.setLayout(null);
loginPanel.add(imageLabel);
loginPanel.add(headingLogin);
loginPanel.add(usernameLabel);
loginPanel.add(usernameField);
loginPanel.add(passwordLabel);
loginPanel.add(passwordField);
loginPanel.add(loginBtn);
loginPanel.add(createAc);
loginBtn.addActionListener(_->{
String usernameVal = usernameField.getText().trim();
String passwordVal = passwordField.getText().trim();
if(usernameVal.length()==0 || passwordVal.length()==0)
{
messageLabel  = new JLabel("Empty username/password");
messageLabel.setFont(messageFont);
JOptionPane.showMessageDialog(null,messageLabel);
return;
}
boolean authentic=false;
try
{
authentic = (boolean)client.execute("/ChessServer/authenticateMember",usernameVal,passwordVal);
addListeners();
if(authentic)
{
this.username = usernameVal;
showHomeUI();
}else
{
messageLabel  = new JLabel("Invalid username/password");
messageLabel.setFont(messageFont);
JOptionPane.showMessageDialog(null,messageLabel);
}
}catch(Throwable t)
{
messageLabel = new JLabel("Server is not running Connection refused");
messageLabel.setFont(messageFont);
JOptionPane.showMessageDialog(null,messageLabel);
System.out.println(t.getMessage());
System.exit(0);
}
});
createAc.addActionListener(_->{
messageLabel = new JLabel("This feature is unavailble");
messageLabel.setFont(messageFont);
JOptionPane.showMessageDialog(null,messageLabel);
});
container.setLayout(new BorderLayout());
container.add(loginPanel,BorderLayout.CENTER);
setResizable(false);
setVisible(true);
setDefaultCloseOperation(EXIT_ON_CLOSE);
}

public void startUI(String opponent)
{
hideUI();
JPanel startPanel = new JPanel();
JPanel btnPanel = new JPanel();
JButton cancelbtn = new JButton("Cancel");
JButton startbtn = new JButton("Start");
JTextArea messageArea = new JTextArea();
messageArea.setText("Your invitation is accepted by the user "+opponent+"\nLet's Play Game!\nEnjoy the challenge!");
messageArea.setLineWrap(true);
messageArea.setWrapStyleWord(true);
messageArea.setEditable(false);
messageArea.setPreferredSize(new Dimension(480,95)); 
messageArea.setBorder(null);
Font mfont = new Font("Monospaced",Font.BOLD,16);
messageArea.setFont(mfont);
messageArea.setForeground(new Color(0, 86, 109));
Font btnFont = new Font("Serif",Font.BOLD,15);
startbtn.setFont(btnFont);
cancelbtn.setFont(btnFont);
btnPanel.add(startbtn);
btnPanel.add(cancelbtn);
startPanel.add(messageArea);
startPanel.add(btnPanel);
startPanel.setBounds(100,150,482,150);
startPanel.setBackground(Color.white);
Border border = new LineBorder(new Color(0, 86, 109), 1, true);
startPanel.setBorder(border);
container.setLayout(null);
container.add(startPanel);
setResizable(false);
startbtn.addActionListener(new ActionListener(){
public void actionPerformed(ActionEvent ae)
{
try
{
client.execute("/ChessServer/shareBoard",username,opponent);
startGame(username,opponent);
client.execute("/ChessServer/setMessage",username,opponent,"StartGame");
}catch(Exception e)
{
e.printStackTrace();
}
}
});
cancelbtn.addActionListener(new ActionListener()
{
public void actionPerformed(ActionEvent ae)
{
showHomeUI();
}
});
}
private void sendInvitation(String toUsername)
{
try
{
client.execute("/ChessServer/inviteUser",username,toUsername);
JLabel message = new JLabel("Invitation for game send to "+toUsername);
message.setFont(new Font("Century",Font.BOLD,15));
message.setBackground(Color.white);
message.setForeground(new Color(79,43,112));
JOptionPane.showMessageDialog(this,message);
}catch(Throwable t)
{
System.out.println("some error");
}
}
private void sendBackMessage(String fromUsername,String toUsername,String type)
{
try
{
client.execute("/ChessServer/setMessage",fromUsername,toUsername,type);
}catch(Exception exception)
{
System.out.println("error in sending message "+exception.getMessage());
}
}
public void startGame(String username1,String username2)
{
hideUI();
String board[][]=null;
Object boardObj=null;
try
{
boardObj = client.execute("/ChessServer/getBoard",username);
}catch(Throwable t)
{
System.out.println("getting error "+t.getMessage());
}
String resultJson = gson.toJson(boardObj,Object.class);
java.lang.reflect.Type listType = new TypeToken<String [][]>() {}.getType();
board = gson.fromJson(resultJson, listType);
chessBoard = new ChessBoard(username1, username2,board);
Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
setSize(screenSize.width,screenSize.height-41);
setLocation(0,0);
container.add(chessBoard.boardPanel);
container.add(chessBoard.sidePanel);
container.revalidate();
container.repaint();
}
public void rematch()
{

}
//inner classes starts here 
class AvailableUsersListModel extends AbstractTableModel
{
private java.util.List<String> users;
private String title[] = {"Users"," "};
private java.util.List<JButton> inviteButtons;
private boolean awaitingInvitationReply;
AvailableUsersListModel()
{
awaitingInvitationReply = false;
inviteButtons = new LinkedList<>();
users = new LinkedList<>();
}
public int getRowCount()
{
return this.users.size();
}
public int getColumnCount()
{
return this.title.length;
}
public String getColumnName(int columnIndex)
{
return title[columnIndex];
}
public Object getValueAt(int row,int column)
{
if(column==0) return this.users.get(row);
return this.inviteButtons.get(row);
}
public boolean isCellEditable(int r,int c)
{
if(c==1) return true;
return false;
}
public Class<?> getColumnClass(int c)
{
if(c==0) return String.class;
return JButton.class;
}
public void setUsers(java.util.List<String> users)
{
if(awaitingInvitationReply) return;
this.users = users;
this.inviteButtons.clear();
for(int i=0; i<this.users.size(); i++) this.inviteButtons.add(new JButton("Invite"));
fireTableDataChanged();
}
public void setValueAt(Object data,int row,int col)
{
if(col==1)
{
JButton button = inviteButtons.get(row);
String text = (String)data;
button.setText(text);
button.setEnabled(false);
if(text.equalsIgnoreCase("Invited"))
{
awaitingInvitationReply = true;
for(JButton inviteButton:inviteButtons) inviteButton.setEnabled(false);
ChessUI.this.sendInvitation(this.users.get(row));
this.fireTableDataChanged();
}
else if(text.equalsIgnoreCase("Invite"))
{
awaitingInvitationReply = false;
for(JButton inviteButton:inviteButtons) inviteButton.setEnabled(true);
this.fireTableDataChanged();
}
}
}
public void refreshUsersList()
{
this.users.clear();
this.fireTableDataChanged();
this.awaitingInvitationReply= false;
}
}
class AvailableUsersListButtonRenderer implements TableCellRenderer
{
public Component getTableCellRendererComponent(JTable table,Object value,boolean a,boolean b,int r,int c)
{
return (JButton)value;
}
}
class AvailableUsersListButtonCellEditor extends DefaultCellEditor
{
private ActionListener actionListener;
private int row,col;
AvailableUsersListButtonCellEditor()
{
super(new JCheckBox());
this.actionListener = new ActionListener()
{
public void actionPerformed(ActionEvent ae)
{
fireEditingStopped();
}
}; 
}
public Component getTableCellEditorComponent(JTable table,Object value,boolean a,int row,int column)
{
this.row = row;
this.col = column;
JButton button = (JButton)availableUsersListModel.getValueAt(this.row,this.col);
button.removeActionListener(this.actionListener);
button.addActionListener(this.actionListener);
button.setForeground(Color.black);
button.setBackground(UIManager.getColor("Button.background"));
button.setOpaque(true);
return button;
}
public Object getCellEditorValue()
{
//right now do nothing later on something to send required or call a callback 
System.out.println("Button at cell :"+this.row+","+this.col+" clicked");
return "Invited";
}
public boolean stopCellEditing()
{
return super.stopCellEditing();
}
public void fireEditingStopped()
{
//do whatever is required
super.fireEditingStopped();
}
}

// inner class

class InvitationTableModel extends AbstractTableModel
{
private java.util.List<Message> messages = new ArrayList<>();
private java.util.List<JButton> acceptButtons = new ArrayList<>();
private java.util.List<JButton> declineButtons = new ArrayList<>();
private java.util.Map<Message,javax.swing.Timer> expiryTimer = new HashMap<>();
private String columns[] = {"From","✔","✖"};
public int getRowCount()
{
return messages.size();
}
public int getColumnCount()
{
return columns.length;
}
public String getColumnName(int col)
{
return columns[col];
}
public Object getValueAt(int row,int col)
{
if(col==0) return messages.get(row).fromUsername;
if(col==1) return acceptButtons.get(row);
return declineButtons.get(row);
}
public Class<?> getColumnClass(int col)
{
if(col==0) return String.class;
return JButton.class;
}
public boolean isCellEditable(int row,int col)
{
return col==1||col==2;
}
public Message getMessageAt(int row)
{
return messages.get(row);
}
public void setValueAt(Object value,int row,int col)
{
// This method is not used in current button workflow
}
public void addMessage(Message msg,Runnable onExpire)
{
if(!messages.contains(msg))
{
messages.add(msg);
JButton btn1 = new JButton("Accept");
JButton btn2 = new JButton("Decline");
acceptButtons.add(btn1);
declineButtons.add(btn2);
fireTableRowsInserted(messages.size() - 1, messages.size() - 1);
t2 = new javax.swing.Timer(50000,new ActionListener(){
public void actionPerformed(ActionEvent ae)
{
removeMessage(msg);
}
});
onExpire.run();
t2.setRepeats(false);
t2.start();
expiryTimer.put(msg,t2);
}
}
public void removeMessage(Message msg)
{
int index = messages.indexOf(msg);
messages.remove(index);
acceptButtons.remove(index);
declineButtons.remove(index);
javax.swing.Timer t = expiryTimer.remove(msg);
if(t!=null) t.stop();
fireTableRowsDeleted(index,index);
}

}// inner class ends 

class DualButtonRenderer implements TableCellRenderer 
{
public Component getTableCellRendererComponent(JTable table,Object value,boolean a,boolean b,int r,int c)
{
return (JButton)value;
}
}
class DualButtonEditor extends DefaultCellEditor
{
private JButton button;
DualButtonEditor()
{
super(new JCheckBox());
}
public Component getTableCellEditorComponent(JTable table,Object value,boolean a,int row,int col)
{
this.button = (JButton)value;
for(ActionListener al:button.getActionListeners())
{
button.removeActionListener(al);
}
// now adding action listener
button.addActionListener(_->{
InvitationTableModel model = (InvitationTableModel)table.getModel();
String toUsername = (String)model.getValueAt(row,0);
String type =null;
if(col==1) type="Accepted";
if(col==2) type="Rejected";
sendBackMessage(username,toUsername,type);
model.removeMessage(model.getMessageAt(row));
fireEditingStopped();
});
return button;
}
public Object getCellEditorValue()
{
return button.getText();
}
public boolean stopCellEditing()
{
return super.stopCellEditing();
}
public void fireEditingStopped()
{
super.fireEditingStopped();
}
}// inner class ends 


public class ChessBoard implements ActionListener
{
private JButton[][] boardSquares = new JButton[8][8];
private JPanel capturePanel = new JPanel();
private JPanel whiteCaptured = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
private JPanel blackCaptured = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
private String[][] board;
private JButton pressed1;
private int turn = 1;
private Map<String, ImageIcon> iconMap;
private JTextField statusField;
private JTextField player1Name;
private JTextField player2Name;
private Font messageFont;
private JPanel capturePane;
public JPanel boardPanel;
public JPanel sidePanel;
private JButton pieceInAttack;
private Color redColor;
private ArrayList<JButton> redBtns;
public int x1,y1,x2,y2;
public ChessBoard(String user1,String user2,String [][]board)
{
this.board =board;
redBtns = new ArrayList<>();
redColor = new Color(255,0,0);
pieceInAttack=null;
messageFont = new Font("monospaced",Font.BOLD,16);
pressed1 = null;
iconMap = new HashMap<>();
setTitle(user1);
setDefaultCloseOperation(EXIT_ON_CLOSE);
boardPanel = new JPanel(new GridLayout(8, 8));
Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
setSize(d.width, d.height);
Border border = new LineBorder(new Color(0, 86, 109), 1);
boardPanel.setBorder(border);
boardPanel.setBounds(2, 2, d.width - 458, d.height - 83);
sidePanel = new JPanel();
sidePanel.setBounds(d.width - 457, 2, 440, d.height - 83);
sidePanel.setBorder(border);
JButton cancelBtn = new JButton("Exit Game");
cancelBtn.addActionListener(_->{
messageLabel = new JLabel("Are you sure? Exit Game");
messageLabel.setFont(messageFont);
messageLabel.setForeground(new Color(48,55,147));
int selected = JOptionPane.showConfirmDialog(null,messageLabel,"Select",JOptionPane.YES_NO_OPTION);
if(selected==0)
{
showHomeUI();
try
{
client.execute("/ChessServer/setMessage",user1,user2,"EndGame");
}catch(Exception e)
{
//do nothing
}
}
});
JButton submitBtn = new JButton("Submit Move");
submitBtn.addActionListener(_->{
try
{
boolean flag = false;
Move  move = new Move();
move.fromX = x1;
move.fromY = y1;
move.toX = x2;
move.toY = y2;
move.toUser = user2;
System.out.println("user2 is "+user2);
String moveString  = gson.toJson(move);
flag = (boolean)client.execute("/ChessServer/submitMove",username,moveString);
if(flag)
{
messageLabel = new JLabel("Move submitted");
messageLabel.setFont(messageFont);
messageLabel.setForeground(new Color(48,55,147));
JOptionPane.showMessageDialog(null,messageLabel);
}
t4.start();
}catch(Exception e)
{
System.out.println(e.getMessage());
}
t3 = new Timer(10000,new ActionListener() {
public void actionPerformed(ActionEvent ae)
{
System.out.println("got called timer t3");
try
{
Object moveObject = client.execute("/ChessServer/getMove",username);
String moveObjString = gson.toJson(moveObject);
Move move = gson.fromJson(moveObjString,Move.class);
if(move==null)
{
messageLabel = new JLabel("No moves");
messageLabel.setFont(messageFont);
JOptionPane.showMessageDialog(null,messageLabel);
}
chessBoard.updateBoard(move);
}catch(Exception e)
{
//do nothing
}
}
});
t3.start();
t3.setRepeats(false);
});
JPanel btnPanel = new JPanel();
JButton restartBtn = new JButton("Restart");
restartBtn.addActionListener(_->{
messageLabel = new JLabel("Are you sure? Restart game");
messageLabel.setFont(messageFont);
messageLabel.setForeground(new Color(48,55,147));
int selected = JOptionPane.showConfirmDialog(null,messageLabel,"Select",JOptionPane.YES_NO_OPTION);
if(selected==0)
{
resetBoard();
}
});
JButton resignBtn = new JButton("Resign");
resignBtn.addActionListener(_->{
messageLabel = new JLabel("Are you sure? Resign");
messageLabel.setFont(messageFont);
messageLabel.setForeground(new Color(48,55,147));
int selected = JOptionPane.showConfirmDialog(null,messageLabel,"Select",JOptionPane.YES_NO_OPTION);
if(selected==0)
{
showHomeUI();
try
{
client.execute("/ChessServer/setMessage",user1,user2,"EndGame");
}catch(Exception e)
{
//do nothing
}
}
});
btnPanel.add(submitBtn);
btnPanel.add(cancelBtn);
btnPanel.add(restartBtn);
btnPanel.add(resignBtn);
statusField = new JTextField("White 's turn");
statusField.setPreferredSize(new Dimension(170, 28));
statusField.setEditable(false);
statusField.setBackground(Color.white);
Font inputFont = new Font("DialogInput", Font.BOLD, 16);
statusField.setFont(inputFont);
statusField.setForeground(Color.RED);
JLabel statusLabel = new JLabel("Status");
Font labelFont = new Font("Monospaced", Font.BOLD, 16);
statusLabel.setFont(labelFont);
capturePane = setupCapturedPanel();
JPanel statusPane = new JPanel();
statusPane.add(statusLabel);
statusPane.add(statusField);
Font btnFont = new Font("Serif", Font.BOLD, 15);
submitBtn.setFont(btnFont);
cancelBtn.setFont(btnFont);
restartBtn.setFont(btnFont);
resignBtn.setFont(btnFont);
sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
JPanel player1 = new JPanel();
JPanel player2 = new JPanel();
JLabel playerL2 = new JLabel("Black Piece Player");
JLabel playerL1 = new JLabel("White Piece Player");
playerL1.setFont(labelFont);
playerL2.setFont(labelFont);
player1Name = new JTextField();
player2Name = new JTextField();
player1Name.setEditable(false);
player1Name.setBackground(Color.white);
player1Name.setPreferredSize(new Dimension(200, 24));
player1Name.setFont(inputFont);
player1Name.setText(user1);
player1.add(playerL1);
player1.add(player1Name);  
player2.add(playerL2);
player2Name.setEditable(false);
player2Name.setFont(inputFont);
player2Name.setBackground(Color.white);
player2Name.setPreferredSize(new Dimension(200, 24));
player2Name.setText(user2);
player2.add(player2Name);
sidePanel.add(player2);
sidePanel.add(capturePane);
sidePanel.add(player1 );
sidePanel.add(statusPane);
sidePanel.add(btnPanel);
initializeIconMap();
initializeBoard(boardPanel);
}
public void updateBoard(Move move)
{
int x1 = move.fromX;
int y1 = move.fromY;
int x2 = move.toX;
int y2 = move.toY;
String movePiece = board[x1][y1];
board[x2][y2] = movePiece;
JButton btn1 = boardSquares[x1][y1];
JButton btn2 = boardSquares[x2][y2];
move(btn1,btn2);
turn = (turn == 1) ? 2 : 1;
boardPanel.revalidate();
boardPanel.repaint();
}
private void initializeIconMap()
{
String[] pieces = { "bp.png", "br.png", "bkt.png", "bb.png", "bq.png", "bk.png", "wp.png", "wr.png", "wkt.png","wb.png", "wq.png", "wk.png" };
for (String piece : pieces)
{
URL url = getClass().getResource("/icons/"+piece);
iconMap.put(piece, new ImageIcon(url));
}
}
private void initializeBoard(JPanel boardPanel)
{
for(int i = 0; i < 8; i++)
{
for(int j = 0; j < 8; j++)
{
JButton piece = new JButton(); 
piece.addActionListener(this);
piece.putClientProperty("row", i);
piece.putClientProperty("col", j);
piece.setBackground((i + j)%2==0?Color.WHITE:new Color(32,32,32));
piece.setBorderPainted(false);
if (board[i][j] != null)
{
piece.setIcon(iconMap.get(board[i][j]));
}else
{
piece.setIcon(null);
}
boardSquares[i][j] = piece;
boardPanel.add(piece);
}
}
}
private JPanel setupCapturedPanel()
{
capturePanel.setLayout(new BoxLayout(capturePanel, BoxLayout.Y_AXIS));
capturePanel.setPreferredSize(new Dimension(200, 650));
JLabel whiteLabel = new JLabel("White Captures");
JLabel blackLabel = new JLabel("Black Captures");
Font font = new Font("Monospaced", Font.BOLD, 18);
whiteLabel.setFont(font);
blackLabel.setFont(font);
whiteCaptured.setBorder(BorderFactory.createLineBorder(new Color(0, 86, 109)));
whiteCaptured.setBackground(Color.WHITE);
blackCaptured.setBorder(BorderFactory.createLineBorder(new Color(0, 86, 109)));
blackCaptured.setBackground(Color.WHITE);
capturePanel.add(blackLabel);
capturePanel.add(blackCaptured);
capturePanel.add(Box.createVerticalStrut(40));
capturePanel.add(whiteLabel);
capturePanel.add(whiteCaptured);
capturePanel.setBackground(Color.WHITE);
return capturePanel;
}
public void addCapturedPiece(String imagePath, boolean capturedByWhite)
{
JLabel pieceLabel = new JLabel(iconMap.get(imagePath));
pieceLabel.setPreferredSize(new Dimension(53, 75));
if(capturedByWhite)
{
whiteCaptured.add(pieceLabel);
}
else
{
blackCaptured.add(pieceLabel);
}
capturePanel.revalidate();
capturePanel.repaint();
}
public void actionPerformed(ActionEvent ae)
{
JButton pressed = (JButton) ae.getSource();
int x = (int) pressed.getClientProperty("row");
int y = (int) pressed.getClientProperty("col");
if(pressed1 == null)
{
if(board[x][y] == null) return;
if((turn == 1 && !board[x][y].startsWith("w")) || (turn == 2 && !board[x][y].startsWith("b"))) return;
pressed1 = pressed;
}
else
{
int x1 = (int) pressed1.getClientProperty("row");
int y1 = (int) pressed1.getClientProperty("col");
if (pressed == pressed1)
{
pressed1 = null;
return;
}
int x2 = (int) pressed.getClientProperty("row");
int y2 = (int) pressed.getClientProperty("col");

String movingPiece = board[x1][y1];
String capturedPiece = board[x2][y2];
boolean isValidMove =false;
if(movingPiece==null)
{
pressed1=null;
return;
}
String currentPlayer =movingPiece;
String opponentColor = (currentPlayer.substring(0,1)).equals("w")? "b" : "w";
String opponentKing = opponentColor.equals("w") ? "wk.png" : "bk.png";
String currentKing = opponentKing.equals("wk.png")? "bk.png":"wk.png";

if(capturedPiece != null && capturedPiece.startsWith(movingPiece.substring(0, 1)))
{
//castling
String piece1 = movingPiece.substring(1);
String piece2 = capturedPiece.substring(1);
boolean isCastling = false;
if(piece1.equals("k.png") && piece2.equals("r.png")) isCastling=true;
if(ChessCheckDetector.isInCheck(board,movingPiece)) isCastling =false;
if(!isCastling)
{
pressed1 = null;
return;
}else
{
if(movingPiece.startsWith("b"))
{
if(movingPiece.equals(board[0][4])!=true)
{
pressed1=null;
return;
}
if(capturedPiece.equals(board[0][7])!=true || capturedPiece.equals(board[0][0])!=true)
{
pressed1=null;
return;
}
}else
{
if(movingPiece.equals(board[7][4])!=true)
{
pressed1=null;
return;
}
if(capturedPiece.equals(board[7][7])!=true || capturedPiece.equals(board[7][0])!=true)
{
pressed1=null;
return;
}
}
int direction = (y2>y1)?1:-1;
for(int col = y1 + direction; col != y2; col += direction)
{
String piece= board[x1][col];
if(iconMap.get(piece)!=null)
{
pressed1=null;
return;
}
}
JButton p1=null;
JButton p2=null;
if(direction == 1) 
{
pressed1.setIcon(null);
pressed.setIcon(null);
board[x1][y1+2] = movingPiece; 
board[x1][y1+1] = capturedPiece; 
p1 = boardSquares[x1][y1+2];
p2 = boardSquares[x1][y1+1];
p1.setIcon(iconMap.get(movingPiece));
p2.setIcon(iconMap.get(capturedPiece));
}
else
{
pressed1.setIcon(null);
pressed.setIcon(null);
board[x1][y1-2] = movingPiece;
board[x1][y1-1] = capturedPiece; 
p1 = boardSquares[x1][y1-2];
p2 = boardSquares[x1][y1-1];
p1.setIcon(iconMap.get(movingPiece));
p2.setIcon(iconMap.get(capturedPiece));
}
if(turn==2)
{
statusField.setText("White 's turn");
}
else 
{
statusField.setText("Black 's turn");
} 
turn = (turn == 1) ? 2 : 1;
board[x1][y1] = null;
board[x2][y2] = null;
System.out.println("Castling attempt");
pressed1 = null;
return;
}
}
if(movingPiece.substring(1).equals("k.png"))
{
isValidMove = ValidateMove.validateKing(x1,y1,x2,y2);
}else if(movingPiece.substring(1).equals("p.png"))
{
String pawn = movingPiece.substring(0,2);
boolean isCapturing = false;
if(board[x2][y2]!=null) isCapturing =  true;
boolean isWhite = pawn.equals("wp");
isValidMove = ValidateMove.validatePawn(x1,y1,x2,y2,isWhite,isCapturing);
}else
{
isValidMove = ValidateMove.validate(movingPiece,x1,y1,x2,y2);
}
if(!isValidMove)
{
pressed1 = null;
Font messageFont  = new Font("Century",Font.BOLD,15);
messageLabel = new JLabel("Not valid Move");
messageLabel.setFont(messageFont);
JOptionPane.showMessageDialog(null,messageLabel);
return;
}
ImageIcon imageIcon=null;
String imageString=null;
if(capturedPiece!=null)
{
imageIcon = (ImageIcon)pressed.getIcon();
int b= (int)pressed.getClientProperty("row");
int c= (int)pressed.getClientProperty("col");
imageString = board[b][c];
}
move(pressed1, pressed);
String selected=null;
String promotion=null;
if(movingPiece.contains("p.png"))
{
String color = (movingPiece.equals("bp.png"))?"b":"w";
if(color.equals("w"))
{
if(x2==0)
{
selected = showPromotionDialog();
if(selected==null) promotion="wq.png";
else if(selected.equals("Queen")) promotion="wq.png";
else if(selected.equals("Bishop")) promotion="wb.png";
else if(selected.equals("Rook")) promotion="wr.png";
else if(selected.equals("Knight")) promotion="wkt.png";
board[x2][y2] = promotion;
pressed.setIcon(iconMap.get(promotion));
}
}else
{
if(x2==7)
{
selected = showPromotionDialog();
if(selected==null) promotion="bq.png";
else if(selected.equals("Queen")) promotion="bq.png";
else if(selected.equals("Bishop")) promotion="bb.png";
else if(selected.equals("Rook")) promotion="br.png";
else if(selected.equals("Knight")) promotion="bkt.png";
board[x2][y2] = promotion;
pressed.setIcon(iconMap.get(promotion));
}
}
}
if(capturedPiece!=null)
{
if(capturedPiece.equals("bk.png"))
{
messageLabel = new JLabel("Player White Wins! game ends");
messageLabel.setFont(messageFont);
JOptionPane.showMessageDialog(null,messageLabel);
return;
}
else if(capturedPiece.equals("wk.png"))
{
messageLabel = new JLabel("Player Black Wins! game ends");
messageLabel.setFont(messageFont);
JOptionPane.showMessageDialog(null,messageLabel);
return;
}
}
//find and declare check
boolean isCcheck = ChessCheckDetector.isInCheck(board,currentKing);
if(isCcheck)
{
messageLabel = new JLabel("Invalid Move");
messageLabel.setFont(messageFont);
JOptionPane.showMessageDialog(null,messageLabel);
move(pressed,pressed1);
pressed.setIcon(imageIcon);
int b= (int)pressed.getClientProperty("row");
int c= (int)pressed.getClientProperty("col");
board[b][c] = imageString;
pressed1 = null;
return;
}else
{
for(JButton btn:redBtns)
{
int l = (int)btn.getClientProperty("row");
int j = (int)btn.getClientProperty("col");
btn.setBackground(((l+j)%2==0)?Color.white:new Color(32,32,32));
}
}
if(capturedPiece!=null)
{
boolean capturedByWhite = movingPiece.startsWith("w");
addCapturedPiece(capturedPiece, capturedByWhite);
}

int b1,b2;
b1=b2=0;
int w1,w2;
w1=w2=0;
boolean k1found = false;
boolean k2found = false;
for(int i=0; i<=7; i++)
{
for(int j=0; j<=7; j++)
{
if(board[i][j]!=null && board[i][j].equals("bk.png"))
{
b1=i;
b2=j;
k1found=true;
}
if(board[i][j]!=null && board[i][j].equals("wk.png"))
{
w1=i;
w2=j;
k2found=true;
}
}
}
boolean kkp =false;
if(k1found && k2found)
{
kkp = kingsTooClose(b1,b2,w1,w2);
if(kkp)
{
messageLabel = new JLabel("Wrong king position");
messageLabel.setFont(messageFont);
JOptionPane.showMessageDialog(null,messageLabel);
move(pressed,pressed1);
pressed.setIcon(imageIcon);
int b= (int)pressed.getClientProperty("row");
int c= (int)pressed.getClientProperty("col");
board[b][c] = imageString;
pressed1 = null;
return;
}
}
boolean isOCheck = ChessCheckDetector.isInCheck(board,opponentKing);
boolean hasLegalMove = ChessCheckDetector.hasAnyLegalMove(board,opponentKing);
if(isOCheck && !hasLegalMove)
{
String player = currentKing.equals("wk.png")?"White":"Black";
messageLabel = new JLabel("Match ends "+player+" Wins");
messageLabel.setFont(messageFont);
JOptionPane.showMessageDialog(null,messageLabel);
boardPanel.setEnabled(false);
pressed1=null;
return;
}else if(!isOCheck && !hasLegalMove)
{
messageLabel = new JLabel("Stalemate");
messageLabel.setFont(messageFont);
JOptionPane.showMessageDialog(null,messageLabel);
boardPanel.setEnabled(false);
}
else if(isOCheck)
{
for(int i=0; i<=7;i++)
{
for(int j=0; j<=7; j++)
{
if(board[i][j]!=null && board[i][j].equals(opponentKing))
{
pieceInAttack= boardSquares[i][j];
if(pieceInAttack!=null)
{
pieceInAttack.setOpaque(true);
pieceInAttack.setContentAreaFilled(true);
pieceInAttack.setBackground(redColor);
redBtns.add(pieceInAttack);
}
}
}
}
}
turn = (turn ==1)?2:1;
if(turn==1)
{
statusField.setText("White 's turn");
}
else 
{
statusField.setText("Black 's turn");
}
pressed1 = null;
}
}
public void move(JButton fromBtn, JButton toBtn)
{
int x1 = (int) fromBtn.getClientProperty("row");
int y1 = (int) fromBtn.getClientProperty("col");
int x2 = (int) toBtn.getClientProperty("row");
int y2 = (int) toBtn.getClientProperty("col");
String movingPiece = board[x1][y1];
toBtn.setIcon(iconMap.get(movingPiece));
fromBtn.setIcon(null);
chessBoard.x1 = x1;
chessBoard.y1 = y1;
chessBoard.x2 = x2;
chessBoard.y2 = y2;
}
public String showPromotionDialog()
{
String choices[] = {"Queen","Bishop","Rook","Knight"};
messageLabel = new JLabel("Select piece for Promotion");
Font f = new Font("monospaced",Font.BOLD,15);  
messageLabel.setFont(f);
String selected= (String)JOptionPane.showInputDialog(null,messageLabel,"Pawn Promotion",JOptionPane.QUESTION_MESSAGE,null,choices,choices[0]);
return selected;
}
public boolean kingsTooClose(int x1, int y1, int x2, int y2)
{
int dx = Math.abs(x1 - x2);
int dy = Math.abs(y1 - y2);
return (dx <= 1 && dy <= 1);
} 
public void resetBoard()
{
boardPanel.removeAll();
boardPanel.revalidate();
boardPanel.repaint();
initializeBoard(boardPanel);
}    
}

}// outer class ends                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  