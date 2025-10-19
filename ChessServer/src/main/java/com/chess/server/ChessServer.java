package com.chess.server;
import com.chess.server.dl.*;
import com.chess.common.*;
import com.nframework.server.annotations.*;
import java.util.*;
import com.google.gson.*;
import com.chess.server.board.*;

@Path("/ChessServer")
public class ChessServer
{
static private Map<String,Member> members;
static private Set<String> loggedInMembers;
static private Set<String>  playingMembers;
static private Map<String,List<Message>>inboxes;
static private Map<String,Game> games;
static private Map<String,Move> moves;
static private MemberDAO memberDAO;
static
{
memberDAO = new MemberDAO();
populateDataStructures();
}
public ChessServer()
{
}
static private void populateDataStructures()
{
List<MemberDTO> dlMembers = memberDAO.getAll();
Member member;
members = new HashMap<>();
for(MemberDTO memberDTO:dlMembers)
{
member = new Member();
member.username = memberDTO.username;
member.password = memberDTO.password;
members.put(member.username,member);
}
loggedInMembers = new HashSet<>();
playingMembers = new HashSet<>();
inboxes = new HashMap<>();
games  = new HashMap<>();
}
@Path("/authenticateMember")
public boolean isMemberAuthentic(String username,String password)
{
Member member = members.get(username);
if(member==null)
{
return false;
}
boolean b = password.equals(member.password);
if(b)
{
loggedInMembers.add(username);
}
return b;
}
@Path("/logout")
public void logout(String username)
{
loggedInMembers.remove(username);
//playingMembers.remove(username); //will deal with this scenario later on
}
@Path("/getMembers")
public List<String> getAvailableMembers(String username)
{
try
{
List<String> availableMembers = new LinkedList<>();
for(String u:loggedInMembers)
{
if(playingMembers.contains(u)==false && u.equals(username)==false) availableMembers.add(u);
}
return availableMembers;
}catch(Exception e)
{
System.out.println(e.getMessage()+"getAvailable method");
}
return null;
}
@Path("/register")
public boolean addUser(String username,String password)
{
try
{
MemberDTO memberDTO = new MemberDTO();
memberDTO.username = username;
memberDTO.password = password;
memberDAO.addMember(memberDTO);
populateDataStructures();
return true;
}catch(Exception exception)
{
return false;
}
}
@Path("/inviteUser")
public void inviteUser(String fromUsername,String toUsername)
{
try
{
Message message = new Message();
message.fromUsername = fromUsername;
message.toUsername = toUsername;
message.type = MESSAGE_TYPE.CHALLENGE;
List<Message> messages = inboxes.get(toUsername);
if(messages==null)
{
messages = new LinkedList<Message>();
inboxes.put(toUsername,messages);
}
messages.add(message);
}catch(Exception e)
{
System.out.println(e.getMessage()+"invite method");
}
}
@Path("/getMessages")
public List<Message> getMessages(String username)
{
try
{
List<Message> messages = inboxes.get(username);
if(messages!=null && messages.size()>0)
{
return messages;
}
}catch(Exception e)
{
System.out.println(e.getMessage()+"getMessage method");
}
return null;
}
@Path("/removeMessage")
public void removeMessage(String username,String tp)
{
MESSAGE_TYPE type= null;
if(tp.equals("Challenge")) type = MESSAGE_TYPE.CHALLENGE;
if(tp.equals("Accepted")) type = MESSAGE_TYPE.CHALLENGE_ACCEPTED;
if(tp.equals("Rejected")) type = MESSAGE_TYPE.CHALLENGE_REJECTED;
if(tp.equals("StartGame")) type = MESSAGE_TYPE.START_GAME;
if(tp.equals("EndGame")) type = MESSAGE_TYPE.END_GAME;
if(tp.equals("Restart")) type=MESSAGE_TYPE.RESTART;
if(tp.equals("RAccepted")) type=MESSAGE_TYPE.RESTART_ACCEPTED;
if(tp.equals("RRejected")) type=MESSAGE_TYPE.RESTART_REJECTED;
if(tp.equals("LostMatch")) type=MESSAGE_TYPE.LOST_MATCH;
if(tp.equals("Stalemate")) type=MESSAGE_TYPE.STALEMATE;
try
{
List<Message> messages = inboxes.get(username);
if(messages!=null)
{
for(Message message:messages)
{
if(message.type==type)
{
messages.remove(message);
break;
}
}
}
}catch(Exception e)
{
System.out.println(e.getMessage()+"remove method");
}
}
@Path("/setMessage")
public void setMessage(String fromUsername,String toUsername,String tp)
{
MESSAGE_TYPE type =null;
if(tp.equals("Accepted")) type=MESSAGE_TYPE.CHALLENGE_ACCEPTED;
if(tp.equals("Rejected")) type=MESSAGE_TYPE.CHALLENGE_REJECTED;
if(tp.equals("StartGame")) type=MESSAGE_TYPE.START_GAME;
if(tp.equals("EndGame")) type=MESSAGE_TYPE.END_GAME;
if(tp.equals("Restart")) type=MESSAGE_TYPE.RESTART;
if(tp.equals("RAccepted")) type=MESSAGE_TYPE.RESTART_ACCEPTED;
if(tp.equals("RRejected")) type=MESSAGE_TYPE.RESTART_REJECTED;
if(tp.equals("LostMatch")) type=MESSAGE_TYPE.LOST_MATCH;
if(tp.equals("Stalemate")) type=MESSAGE_TYPE.STALEMATE;
Message msg = new Message();
msg.fromUsername= fromUsername;
msg.toUsername = toUsername;
msg.type = type;
try
{
List<Message> messages = inboxes.get(toUsername);
if(messages==null)
{
messages = new LinkedList<Message>();
inboxes.put(msg.toUsername,messages);
}
messages.add(msg);
}catch(Exception e)
{
System.out.println(e.getMessage()+"set message");
}
}
@Path("/submitMove")
public boolean submitMove(String fromUser,String moveString)
{
Gson gson = new Gson();
Move move = gson.fromJson(moveString,Move.class);
Game game = games.get(fromUser);
String board[][] = game.board;
try
{
boolean flag = false;
String piece = board[move.fromX][move.fromY];
if(piece.contains("k.png"))
{
flag = ValidateMove.validateKing(move.fromX,move.fromX,move.toX,move.toY);
}else if(piece.contains("p.png"))
{
boolean isWhite = piece.contains("w");
boolean isCapturing = false;
if(board[move.toX][move.toY]!=null) isCapturing =  true;
flag = ValidateMove.validatePawn(move.fromX,move.fromY,move.toX,move.toY,isWhite,isCapturing);
}else
{
flag = ValidateMove.validate(piece,move.fromX,move.fromY,move.toX,move.toY);
}
if(flag)
{
String pieceString = board[move.fromX][move.fromY];
if(move.castling)
{
int x1 = move.fromX;
int y1 = move.fromY;
int x2 = move.toX;
int y2 = move.toY;
int direction = (y2>y1)?1:-1;
for(int col = y1 + direction; col != y2; col += direction)
{
String ps= board[x1][col];
if(ps!=null)
{
return false;
}
}
if(direction == 1) 
{
board[x1][y1+2] = pieceString; 
board[x1][y1+1] = move.captureString; 
}
else
{
board[x1][y1-2] = pieceString;
board[x1][y1-1] = move.captureString; 
}
board[x1][y1] = null;
board[x2][y2] = null;
}else if(move.promotion)
{
board[move.toX][move.toY] = move.promotionString;
board[move.fromX][move.fromY] = null;
}
else
{
board[move.toX][move.toY] = pieceString;
board[move.fromX][move.fromY] = null;
}
moves.put(move.toUser, move);
return true;
}
System.out.println("x1-"+move.fromX+"y1-"+move.fromY+"x2-"+move.toX+"y2-"+move.toY);
System.out.println("moving piece "+board[move.fromX][move.fromY]+"captured piece "+board[move.toX][move.toY]);
}catch(Exception e)
{
System.out.println(e.getMessage());
}
System.out.println("moving piece "+board[move.fromX][move.fromY]+"capture piece "+board[move.toX][move.toY]);
return false;
}
@Path("/getMove")
public Move getOpponentsMove(String username)
{
Move move =null;
try
{
move = moves.get(username);
if(move==null)
{
return null;
}
moves.remove(username);
}catch(Exception e)
{
e.printStackTrace();
}
return move;
}
@Path("/shareBoard")
public void shareBoard(String user1,String user2)
{
Game game = new Game();
game.user1 = user1;
game.user2 = user2;
String board[][] = new String[8][8];
board[0] = new String[]{ "br.png", "bkt.png", "bb.png", "bq.png", "bk.png", "bb.png", "bkt.png", "br.png" };
board[1] = new String[]{ "bp.png", "bp.png", "bp.png", "bp.png", "bp.png", "bp.png", "bp.png", "bp.png" };
board[6] = new String[]{ "wp.png", "wp.png", "wp.png", "wp.png", "wp.png", "wp.png", "wp.png", "wp.png" };
board[7] = new String[]{ "wr.png", "wkt.png", "wb.png", "wq.png", "wk.png", "wb.png", "wkt.png", "wr.png" };
game.board = board;
games.put(user2,game);
games.put(user1, game);
moves = new HashMap<>();
}
@Path("/getBoard")
public String[][] getBoard(String username)
{
Game game = games.get(username);
if(game==null)
{
return null;
}
return game.board;
}
}
