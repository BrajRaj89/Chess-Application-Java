package com.chess.board;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.util.*;
import java.net.*;

public class ChessBoard extends JFrame implements ActionListener
{
private JButton[][] boardSquares = new JButton[8][8];
private Container container;
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
private JLabel message;
private JPanel capturePane;
private JPanel boardPanel;
private JPanel sidePanel;
private JButton pieceInAttack;
private Color redColor;
private ArrayList<JButton> redBtns;

public ChessBoard(String user1,String user2)
{
redBtns = new ArrayList<>();
redColor = new Color(255,0,0);
pieceInAttack=null;
messageFont = new Font("monospaced",Font.BOLD,16);
pressed1 = null;
iconMap = new HashMap<>();
setTitle("Chess Board");
setDefaultCloseOperation(EXIT_ON_CLOSE);
boardPanel = new JPanel(new GridLayout(8, 8));
initializeIconMap();
initializePieces();
initializeBoard(boardPanel);
sidePanel = new JPanel();
container = getContentPane();
container.setLayout(null);
Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
setSize(d.width, d.height);
Border border = new LineBorder(new Color(0, 86, 109), 1);
boardPanel.setBorder(border);
sidePanel.setBorder(border);
boardPanel.setBounds(2, 2, d.width - 458, d.height - 83);
sidePanel.setBounds(d.width - 457, 2, 440, d.height - 83);
JButton cancelBtn = new JButton("Exit Game");
cancelBtn.addActionListener(_->{
message = new JLabel("Are you sure? Exit Game");
message.setFont(messageFont);
message.setForeground(new Color(48,55,147));
int selected = JOptionPane.showConfirmDialog(null,message,"Select",JOptionPane.YES_NO_OPTION);
if(selected==0)
{
System.exit(0);
}
});
JButton submitBtn = new JButton("Submit Move");
submitBtn.addActionListener(_ ->{
message = new JLabel("Move submitted");
message.setFont(messageFont);
message.setForeground(new Color(48,55,147));
JOptionPane.showMessageDialog(null,message);
});
JPanel btnPanel = new JPanel();
JButton restartBtn = new JButton("Restart");
restartBtn.addActionListener(_->{
message = new JLabel("Are you sure? Restart game");
message.setFont(messageFont);
message.setForeground(new Color(48,55,147));
int selected = JOptionPane.showConfirmDialog(null,message,"Select",JOptionPane.YES_NO_OPTION);
if(selected==0)
{
// call the restart method
resetBoard();
}
});
JButton resignBtn = new JButton("Resign");
resignBtn.addActionListener(_->{
message = new JLabel("Are you sure? Resign");
message.setFont(messageFont);
message.setForeground(new Color(48,55,147));
int selected = JOptionPane.showConfirmDialog(null,message,"Select",JOptionPane.YES_NO_OPTION);
if(selected==0)
{
System.exit(0);
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
container.add(boardPanel);
container.add(sidePanel);
setVisible(true);
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
private void initializePieces()
{
board = new String[8][8];
board[0] = new String[]{ "br.png", "bkt.png", "bb.png", "bq.png", "bk.png", "bb.png", "bkt.png", "br.png" };
board[1] = new String[]{ "bp.png", "bp.png", "bp.png", "bp.png", "bp.png", "bp.png", "bp.png", "bp.png" };
board[6] = new String[]{ "wp.png", "wp.png", "wp.png", "wp.png", "wp.png", "wp.png", "wp.png", "wp.png" };
board[7] = new String[] { "wr.png", "wkt.png", "wb.png", "wq.png", "wk.png", "wb.png", "wkt.png", "wr.png" };
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
JLabel  message = new JLabel("Not valid Move");
message.setFont(messageFont);
JOptionPane.showMessageDialog(null,message);
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
message = new JLabel("Player White Wins! game ends");
message.setFont(messageFont);
JOptionPane.showMessageDialog(null,message);
return;
}
else if(capturedPiece.equals("wk.png"))
{
message = new JLabel("Player Black Wins! game ends");
message.setFont(messageFont);
JOptionPane.showMessageDialog(null,message);
return;
}
}
//find and declare check
boolean isCcheck = ChessCheckDetector.isInCheck(board,currentKing);
if(isCcheck)
{
message = new JLabel("Invalid Move");
message.setFont(messageFont);
JOptionPane.showMessageDialog(null,message);
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
message = new JLabel("Wrong king position");
message.setFont(messageFont);
JOptionPane.showMessageDialog(null,message);
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
message = new JLabel("Match ends "+player+" Wins");
message.setFont(messageFont);
JOptionPane.showMessageDialog(null,message);
boardPanel.setEnabled(false);
pressed1=null;
return;
}else if(!isOCheck && !hasLegalMove)
{
message = new JLabel("Stalemate");
message.setFont(messageFont);
JOptionPane.showMessageDialog(null,message);
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
board[x2][y2] = movingPiece;
board[x1][y1] = null;
}
public String showPromotionDialog()
{
String choices[] = {"Queen","Bishop","Rook","Knight"};
message = new JLabel("Select piece for Promotion");
Font f = new Font("monospaced",Font.BOLD,15);  
message.setFont(f);
String selected= (String)JOptionPane.showInputDialog(null,message,"Pawn Promotion",JOptionPane.QUESTION_MESSAGE,null,choices,choices[0]);
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
initializePieces();
initializeBoard(boardPanel);
}
public void submitMove(String board[][])
{
this.board = board;
initializeBoard(boardPanel);
}    
}
