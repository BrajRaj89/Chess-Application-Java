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
private String username2;
private AvailableUsersListModel availableUsersListModel;
private JTable availableUsersList;
private JScrollPane availableUsersListScrollPane;
private InvitationTableModel invitationTableModel;
private JTable invitationMessagesList;
private JScrollPane invitationMessagesListScrollPane;
private Timer timer,t1,t2,t3;
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
private boolean isSecond;
private boolean capturedByWhite;
private Object boardObject;
private String board[][];
java.lang.reflect.Type listType;
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
setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
addWindowListener(new WindowAdapter(){
public void windowClosing(WindowEvent e)
{
try
{
messageLabel = new JLabel("Are you sure? Exit Game");
messageLabel.setFont(messageFont);
messageLabel.setForeground(new Color(48,55,147));
int selected = JOptionPane.showConfirmDialog(null,messageLabel,"Select",JOptionPane.YES_NO_OPTION);
if(selected==0)
{
String wdw = getTitle();
if(wdw.equals("ChessBoard"))
{
try
{
isSecond=false;
showHomeUI();
client.execute("/ChessServer/setMessage",username,username2,"EndGame");
}catch(Exception ee)
{ 
System.out.println("printing the error message--------------------------"+ee.getMessage());
}
}else
{
client.execute("/ChessServer/logout",username);
System.exit(0);
}
}
}catch(Throwable t)
{
JOptionPane.showMessageDialog(ChessUI.this,t.toString());
}
}
});
}
private void initComponents()
{
isSecond = false;
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
t3 = new javax.swing.Timer(500,new ActionListener() {
public void actionPerformed(ActionEvent ae)
{
try
{
Object moveObject = client.execute("/ChessServer/getMove",username);
String moveObjString = gson.toJson(moveObject);
Move move = gson.fromJson(moveObjString,Move.class);
if(move!=null)
{
chessBoard.updateBoard(move);
if(move.captured)
{
chessBoard.addCapturedPiece(move.captureString, move.capturedByWhite);
}
t3.stop();
chessBoard.submitBtn.setEnabled(true);
}
}catch(Exception e)
{
//do nothing
}
}
});
t1 = new javax.swing.Timer(2000,new ActionListener(){
public void actionPerformed(ActionEvent ae)
{
try
{
Object result= client.execute("/ChessServer/getMessages",username);
String resultJson = gson.toJson(result,Object.class);
 listType = new TypeToken<java.util.List<Message>>() {}.getType();
java.util.List<Message> messages = gson.fromJson(resultJson, listType);
if(messages!=null)
{
for (Message message : messages) 
{
if(message.type==MESSAGE_TYPE.CHALLENGE)
{
try 
{
client.execute("/ChessServer/removeMessage",username,"Challenge");
}catch (Exception ex) 
{
System.out.println("Exception occured "+ex.getMessage());
}
invitationTableModel.addMessage(message);
}else if(message.type==MESSAGE_TYPE.CHALLENGE_ACCEPTED)
{
try 
{
client.execute("/ChessServer/removeMessage",username,"Accepted");
}catch(Exception ex) 
{
//do nothing
}
startUI(message.fromUsername);
availableUsersListModel.refreshUsersList();
}else if(message.type==MESSAGE_TYPE.CHALLENGE_REJECTED)
{
try 
{
client.execute("/ChessServer/removeMessage",username,"Rejected");
}catch (Exception ex) 
{
//do nothing
}
JLabel messagLabel = new JLabel("Your invitation is rejected by user "+message.fromUsername);
messagLabel.setFont(new Font("Century",Font.PLAIN,14));
messagLabel.setBackground(Color.white);
messagLabel.setForeground(new Color(0,51,102)); 
JOptionPane.showMessageDialog(null,messagLabel);
availableUsersListModel.refreshUsersList();
}else if(message.type==MESSAGE_TYPE.START_GAME) 
{
try 
{
client.execute("/ChessServer/removeMessage",username,"StartGame");
}catch (Exception ex) 
{
//do nothing
}
isSecond = true;
startGame(username,message.fromUsername);
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
}else if(message.type==MESSAGE_TYPE.RESTART)
{
messageLabel = new JLabel(message.fromUsername+" Requested to Restart");
messageLabel.setFont(messageFont);
int selected = JOptionPane.showConfirmDialog(null, messageLabel,"Agree",JOptionPane.OK_CANCEL_OPTION);
if(selected==0)
{
try
{
client.execute("/ChessServer/removeMessage",username,"Restart");
client.execute("/ChessServer/shareBoard",username,username2);
client.execute("/ChessServer/setMessage",username,message.fromUsername,"RAccepted");
boardObject  = client.execute("/ChessServer/getBoard",username);
resultJson = gson.toJson(boardObject,Object.class);
listType = new TypeToken<String [][]>() {}.getType();
board = gson.fromJson(resultJson, listType);
chessBoard.board = board;
}catch(Exception e)
{
//do nothing    
}
chessBoard.resetBoard();
}else
{
try
{
client.execute("/ChessServer/removeMessage",username,"Restart");
client.execute("/ChessServer/setMessage",username,message.fromUsername,"RRejected");
}catch(Exception e)
{
}
}
}else if(message.type==MESSAGE_TYPE.RESTART_ACCEPTED)
{
try
{
client.execute("/ChessServer/removeMessage",username,"RAccepted");
boardObject  = client.execute("/ChessServer/getBoard",username);
resultJson = gson.toJson(boardObject,Object.class);
listType = new TypeToken<String [][]>() {}.getType();
board = gson.fromJson(resultJson, listType);
chessBoard.board = board;
}catch(Exception e)
{
//do something
}
chessBoard.resetBoard();
}else if(message.type==MESSAGE_TYPE.RESTART_REJECTED)
{
try
{
client.execute("/ChessServer/removeMessage",username,"RRejected");
}catch(Exception e)
{
//do something
}
messageLabel = new JLabel("Request Rejected for Restart");
messageLabel.setFont(messageFont);
JOptionPane.showMessageDialog(null,messageLabel);
}else if(message.type==MESSAGE_TYPE.LOST_MATCH)
{
showResultUI(message.fromUsername,username,"Loss");
}else if(message.type==MESSAGE_TYPE.STALEMATE)
{
messageLabel = new JLabel("Stalemate");
messageLabel.setFont(messageFont);
JOptionPane.showMessageDialog(null,messageLabel);
showHomeUI();
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
java.util.List<String> members =(java.util.List<String>)client.execute("/ChessServer/getMembers",username);
ChessUI.this.availableUsersListModel.setUsers(members);
timer.start();
}catch(Throwable t)
{
JOptionPane.showMessageDialog(ChessUI.this,t.toString());
}
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
isSecond=false;
hideUI();
setTitle(username);
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
JButton OkBtn = new JButton("Ok");
OkBtn.setFont(btnFont);
OkBtn.setBounds(290,390,130,40);
whiteStatus.setEditable(false);
blackStatus.setEditable(false);
blackStatus.setText(winner+" Won");
whiteStatus.setText(losser+" Loss");
whiteStatus.setBackground(Color.white);
blackStatus.setBackground(Color.white);
OkBtn.addActionListener(_->{
showHomeUI();
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
resultPanel.add(OkBtn);
setSize(w,h);
setLocation(cw,ch);
container.setLayout(new BorderLayout());
container.add(resultPanel,BorderLayout.CENTER);
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
}

public void startUI(String opponent)
{
hideUI();
JPanel startPanel = new JPanel();
JPanel btnPanel = new JPanel();
JButton cancelbtn = new JButton("Cancel");
JButton startbtn = new JButton("Start");
JTextArea messageArea = new JTextArea();
messageArea.setText("Your invitation is accepted by user "+opponent+"\nLet's Play Game!\nEnjoy the challenge!");
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
this.username2 = username2;
t3.start();
hideUI();
String board[][]=null;
try
{
boardObject = client.execute("/ChessServer/getBoard",username);
}catch(Throwable t)
{
System.out.println("getting error "+t.getMessage());
}
String resultJson = gson.toJson(boardObject,Object.class);
java.lang.reflect.Type listType = new TypeToken<String [][]>() {}.getType();
board = gson.fromJson(resultJson, listType);
chessBoard = new ChessBoard(username1,username2,board);
if(isSecond)
{
chessBoard.initializeBoard2(chessBoard.boardPanel);
}else
{
chessBoard.initializeBoard(chessBoard.boardPanel);
}
Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
setSize(screenSize.width,screenSize.height-41);
setLocation(0,0);
container.add(chessBoard.boardPanel);
container.add(chessBoard.sidePanel);
container.revalidate();
container.repaint();
}
//inner classes starts here 
class AvailableUsersListModel extends AbstractTableModel
{
private java.util.List<String> members;
private String title[] = {"Users"," "};
private java.util.List<JButton> inviteButtons;
private boolean awaitingInvitationReply;
AvailableUsersListModel()
{
awaitingInvitationReply = false;
inviteButtons = new LinkedList<>();
members = new LinkedList<>();
}
public int getRowCount()
{
return this.members.size();
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
if(column==0) return this.members.get(row);
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
public void setUsers(java.util.List<String> members)
{
if(awaitingInvitationReply) return;
this.members = members;
this.inviteButtons.clear();
for(int i=0; i<this.members.size(); i++) this.inviteButtons.add(new JButton("Invite"));
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
ChessUI.this.sendInvitation(this.members.get(row));
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
this.members.clear();
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
public void addMessage(Message msg)
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
sendBackMessage(username,msg.fromUsername,"Rejected");
}
});
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
public String[][] board;
public JButton submitBtn;
public Move currentMove;
private boolean isPromotion;
private String promotionString;
private String captureString;
public ChessBoard(String user1,String user2,String [][]board)
{
this.board =board;
redBtns = new ArrayList<>(); 
redColor = new Color(255,0,0);
pieceInAttack=null;
messageFont = new Font("monospaced",Font.BOLD,16);
pressed1 = null;
iconMap = new HashMap<>();
setTitle("ChessBoard");
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
submitBtn = new JButton("Submit Move");
submitBtn.addActionListener(_->{
try
{
boolean flag = false;
if(currentMove==null)
{
messageLabel = new JLabel("No move yet");
messageLabel.setFont(messageFont);
JOptionPane.showMessageDialog(null,messageLabel);
return;
}
String moveString  = gson.toJson(currentMove);
flag = (boolean)client.execute("/ChessServer/submitMove",username,moveString);
if(flag)
{
messageLabel = new JLabel("Move submitted");
messageLabel.setFont(messageFont);
messageLabel.setForeground(new Color(48,55,147));
JOptionPane.showMessageDialog(null,messageLabel);
turn = (turn == 1) ? 2 : 1;
if(turn==1)
{
statusField.setText("White 's turn");
}
else 
{
statusField.setText("Black 's turn");
}
}else
{
JOptionPane.showMessageDialog(null,"Move not submitted ");
System.out.println("x1 "+currentMove.fromX+"y1 "+currentMove.fromY+"x2 "+currentMove.toX+"y2 "+currentMove.toY);
}
submitBtn.setEnabled(false);
t3.start();
currentMove = null;
}catch(Exception e)
{
System.out.println(e.getMessage());
}
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
try
{
client.execute("/ChessServer/setMessage",user1,user2,"Restart");
}catch(Exception e)
{
System.out.println("some Problem in restart");
}
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
isSecond=false;
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
statusField.addActionListener(_->{
statusField.requestFocusInWindow();
});
statusField.setPreferredSize(new Dimension(290, 33));
statusField.setEditable(false);
statusField.setBackground(Color.white);
Font inputFont = new Font("DialogInput", Font.PLAIN, 17);
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
player1.add(playerL1);
player1.add(player1Name);  
player2.add(playerL2);
player2Name.setEditable(false);
player2Name.setFont(inputFont);
player2Name.setBackground(Color.white);
player2Name.setPreferredSize(new Dimension(200, 24));
player2.add(player2Name);
if(isSecond)
{
player1Name.setText(user2);
player2Name.setText(user1);
sidePanel.add(player1);
sidePanel.add(capturePane);
sidePanel.add(player2);
}else
{ 
player1Name.setText(user1);
player2Name.setText(user2);
sidePanel.add(player2);
sidePanel.add(capturePane);
sidePanel.add(player1 );
}
sidePanel.add(statusPane);
sidePanel.add(btnPanel);
initializeIconMap();
}
public void updateBoard(Move move)
{
try
{
int x1 = move.fromX;
int y1 = move.fromY;
int x2 = move.toX;
int y2 = move.toY;
String movString = board[x1][y1];
String capString = board[x2][y2];
JButton btn1 = boardSquares[x1][y1];
JButton btn2 = boardSquares[x2][y2];
if(move.castling)
{
doCastling(btn1, btn2, movString, capString,x1,y1,x2,y2);
}else
{
move(btn1,btn2);
}
if(move.promotion)
{
board[x2][y2] = move.promotionString;
btn2.setIcon(iconMap.get(move.promotionString));
}else
{
evaluateMove(btn1,btn2,movString,capString,x2,y2);
}
board[x1][y1]=null;
chessBoard.enableBoard(); 
turn = (turn == 1) ? 2 : 1;
if(turn==1)
{
statusField.setText("White 's turn");
}
else 
{
statusField.setText("Black 's turn");
}
boardPanel.revalidate();
boardPanel.repaint();
}catch(Exception e)
{
JOptionPane.showMessageDialog(null, e.getMessage());
}
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
for(int i = 0; i <8; i++)
{
for(int j = 0; j <8; j++)
{
JButton piece = new JButton(); 
piece.addActionListener(this);
piece.putClientProperty("row", i);
piece.putClientProperty("col", j);
piece.setBackground((i + j)%2==0?Color.WHITE:new Color(32,32,32));
piece.setBorderPainted(false);
piece.setFocusPainted(false);
if(board[i][j] != null)
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
private void initializeBoard2(JPanel boardPanel)
{
for(int i = 7; i >=0; i--)
{
for(int j = 7; j >=0; j--)
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
if(isSecond)
{
capturePanel.add(whiteLabel);
capturePanel.add(whiteCaptured);
capturePanel.add(Box.createVerticalStrut(40));
capturePanel.add(blackLabel);
capturePanel.add(blackCaptured);
}
else
{
capturePanel.add(blackLabel);
capturePanel.add(blackCaptured);
capturePanel.add(Box.createVerticalStrut(40));
capturePanel.add(whiteLabel);
capturePanel.add(whiteCaptured);
}
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
try
{
JButton pressed = (JButton) ae.getSource();
int x = (int) pressed.getClientProperty("row");
int y = (int) pressed.getClientProperty("col");
if(pressed1 == null)
{
if(board[x][y] == null) return;
if((turn == 1 && !board[x][y].startsWith("w")) || (turn == 2 && !board[x][y].startsWith("b"))) 
{
JOptionPane.showMessageDialog(null,"Not your turn ");
return;
}
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
doCastling(pressed1,pressed,movingPiece,capturedPiece,x1,y1,x2,y2);
Move move = new Move();
move.fromX = x1;
move.fromY = y1;
move.toX = x2;
move.toY = y2;
move.toUser = username2;
move.castling = true;
move.captured=true;
move.captureString =capturedPiece;
move.promotion=false;
this.currentMove = move;
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
move(pressed1, pressed);
boolean hasCompleted = evaluateMove(pressed1, pressed,movingPiece,capturedPiece,x2,y2);
if(!hasCompleted)
{
pressed1=null;
return;
}
board[x1][y1]=null;
chessBoard.disableBoard();
chessBoard.statusField.setText("Board Disabled Submit Move");
Move move = new Move();
move.fromX = x1;
move.fromY = y1;
move.toX = x2;
move.toY = y2;
move.toUser = username2;
if(capturedPiece!=null)
{
capturedByWhite = movingPiece.startsWith("w");
addCapturedPiece(capturedPiece, capturedByWhite);
move.captured  = true;
move.captureString = capturedPiece;
move.capturedByWhite = capturedByWhite;
}else
{
move.captured = false;
}
if(isPromotion)
{
move.promotion = true;
move.promotionString = promotionString;
isPromotion=false;
}else
{
move.promotion=false;
}
this.currentMove = move;
pressed1 = null;
}
}catch(Exception e)
{
e.printStackTrace();
}
}
public void doCastling(JButton pressed1,JButton pressed,String movingPiece,String capturedPiece,int x1,int y1,int x2,int y2)
{
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
board[x1][y1] = null;
board[x2][y2] = null;
}
public boolean evaluateMove(JButton pressed1,JButton pressed,String movingString,String capturedString,int x,int y)
{
String currentPlayer =movingString;
String opponentColor = (currentPlayer.substring(0,1)).equals("w")? "b" : "w";
String opponentKing = opponentColor.equals("w") ? "wk.png" : "bk.png";
String currentKing = opponentKing.equals("wk.png")? "bk.png":"wk.png";
String selected=null;
String promotion=null;
if(movingString.contains("p.png"))
{
String color = (movingString.equals("bp.png"))?"b":"w";
if(color.equals("w"))
{
if(x==0)
{
selected = showPromotionDialog();
if(selected==null) promotion="wq.png";
else if(selected.equals("Queen")) promotion="wq.png";
else if(selected.equals("Bishop")) promotion="wb.png";
else if(selected.equals("Rook")) promotion="wr.png";
else if(selected.equals("Knight")) promotion="wkt.png";
board[x][y] = promotion;
isPromotion=true;
promotionString = promotion;
pressed.setIcon(iconMap.get(promotion));
}
}else
{
if(x==7)
{
selected = showPromotionDialog();
if(selected==null) promotion="bq.png";
else if(selected.equals("Queen")) promotion="bq.png";
else if(selected.equals("Bishop")) promotion="bb.png";
else if(selected.equals("Rook")) promotion="br.png";
else if(selected.equals("Knight")) promotion="bkt.png";
board[x][y] = promotion;
isPromotion=true;
promotionString = promotion;
pressed.setIcon(iconMap.get(promotion));
}
}
}
boolean isCcheck = ChessCheckDetector.isInCheck(board,currentKing);
if(isCcheck)
{
messageLabel = new JLabel("Invalid Move");
messageLabel.setFont(messageFont);
JOptionPane.showMessageDialog(null,messageLabel);
//done done
move(pressed,pressed1);
pressed.setIcon(iconMap.get(capturedString));
int b= (int)pressed.getClientProperty("row");
int c= (int)pressed.getClientProperty("col");
board[b][c] = capturedString;
pressed1 = null;
return false;
}else
{
for(JButton btn:redBtns)
{
int l = (int)btn.getClientProperty("row");
int j = (int)btn.getClientProperty("col");
btn.setBackground(((l+j)%2==0)?Color.white:new Color(32,32,32));
}
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
pressed.setIcon(iconMap.get(capturedString));
int b= (int)pressed.getClientProperty("row");
int c= (int)pressed.getClientProperty("col");
board[b][c] = capturedString;
pressed1 = null;
return false;
}
}
boolean isOCheck = ChessCheckDetector.isInCheck(board,opponentKing);
boolean hasLegalMove = ChessCheckDetector.hasAnyLegalMove(board,opponentKing);
if(isOCheck && !hasLegalMove)
{
showResultUI(username,username2,"Won");
try
{
client.execute("/ChessServer/setMessage",username,username2,"LostMatch");
}catch(Exception e)
{
//do nothing 
}
pressed1=null;
return false;
}else if(!isOCheck && !hasLegalMove)
{
messageLabel = new JLabel("Stalemate");
messageLabel.setFont(messageFont);
try
{
client.execute("/ChessServer/setMessage",username,username2,"Stalemate");
}catch(Exception e)
{
//do nothing
}
JOptionPane.showMessageDialog(null,messageLabel);
showHomeUI();
}
else if(isOCheck)
{
JOptionPane.showMessageDialog(null,"King in check "+currentKing);
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
return true;
}
public void disableBoard()
{
for(int i=0; i<8; i++)
{
for(int j=0; j<8; j++)
{
boardSquares[i][j].setEnabled(false);
boardSquares[i][j].setDisabledIcon(boardSquares[i][j].getIcon());
}
}
}
public void enableBoard()
{
for(int i=0; i<8; i++)
{
for(int j=0; j<8; j++)
{
JButton btn = boardSquares[i][j];
btn.setEnabled(true);
btn.setBorderPainted(false);
btn.setFocusPainted(false);
}
}
}
public void move(JButton fromBtn, JButton toBtn)
{
int x1 = (int) fromBtn.getClientProperty("row");
int y1 = (int) fromBtn.getClientProperty("col");
int x2 = (int) toBtn.getClientProperty("row");
int y2 = (int) toBtn.getClientProperty("col");
String movingPiece = board[x1][y1];
board[x2][y2] = movingPiece;
toBtn.setIcon(iconMap.get(movingPiece));
fromBtn.setIcon(null);
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
if(isSecond)
{
initializeBoard2(boardPanel);
}else
{
initializeBoard(boardPanel);
}
resetCapturedBoard();
}
public void resetCapturedBoard()
{
whiteCaptured.removeAll();
blackCaptured.removeAll();
}    
}
}// outer class ends                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  