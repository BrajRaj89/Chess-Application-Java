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
static
{
populateDataStructures();
}
public ChessServer()
{
}
static private void populateDataStructures()
{
MemberDAO memberDAO = new MemberDAO();
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
if(member==null) return false;
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
public void addUser(String username,String password)
{
MemberDTO member = new MemberDTO();
member.username = username;
member.password = password;
}
@Path("/inviteUser")
public void inviteUser(String fromUsername,String toUsername)
{
try
{
System.out.println("invite User got called");
Message message = new Message();
message.fromUsername = fromUsername;
message.toUsername = toUsername;
message.type = MESSAGE_TYPE.CHALLENGE;
List<Message> messages = inboxes.get(toUsername);
if(messages==null)
{
System.out.println(message.fromUsername+","+message.toUsername);
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
System.out.println("remove message got called successfully");
MESSAGE_TYPE type= null;
if(tp.equals("Challenge")) type = MESSAGE_TYPE.CHALLENGE;
if(tp.equals("Accepted")) type = MESSAGE_TYPE.CHALLENGE_ACCEPTED;
if(tp.equals("Rejected")) type = MESSAGE_TYPE.CHALLENGE_REJECTED;
if(tp.equals("StartGame")) type = MESSAGE_TYPE.START_GAME;
if(tp.equals("EndGame")) type = MESSAGE_TYPE.END_GAME;
if(tp.equals("Restart")) type=MESSAGE_TYPE.RESTART;
if(tp.equals("RAccepted")) type=MESSAGE_TYPE.RESTART_ACCEPTED;
if(tp.equals("RRejected")) type=MESSAGE_TYPE.RESTART_REJECTED;
System.out.println(type);
System.out.println(username);
try
{
List<Message> messages = inboxes.get(username);
if(messages!=null)
{
int count =0;
for(Message message:messages)
{
System.out.println(++count);
if(message.type==type)
{
messages.remove(message);
System.out.println("message removed successfully");
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
System.out.println("set message got called");
MESSAGE_TYPE type =null;
if(tp.equals("Accepted")) type=MESSAGE_TYPE.CHALLENGE_ACCEPTED;
if(tp.equals("Rejected")) type=MESSAGE_TYPE.CHALLENGE_REJECTED;
if(tp.equals("StartGame")) type=MESSAGE_TYPE.START_GAME;
if(tp.equals("EndGame")) type=MESSAGE_TYPE.END_GAME;
if(tp.equals("Restart")) type=MESSAGE_TYPE.RESTART;
if(tp.equals("RAccepted")) type=MESSAGE_TYPE.RESTART_ACCEPTED;
if(tp.equals("RRejected")) type=MESSAGE_TYPE.RESTART_REJECTED;
Message msg = new Message();
msg.fromUsername= fromUsername;
msg.toUsername = toUsername;
msg.type = type;
try
{
List<Message> messages = inboxes.get(toUsername);
if(messages==null)
{
System.out.println(msg.fromUsername+","+msg.toUsername);
messages = new LinkedList<Message>();
inboxes.put(msg.toUsername,messages);
}
messages.add(msg);
}catch(Exception e)
{
System.out.println(e.getMessage()+"set message");
}
}
@Path("/getGameId")
public String getGameId(String username)
{
return "abc";
}
@Path("/canIplay")
public boolean canIplay(String gameId,String username)
{
return false;
}
@Path("/submitMove")
public boolean submitMove(String fromUser,String moveString)
{
Gson gson = new Gson();
Move move = gson.fromJson(moveString,Move.class);
System.out.println("submit move got called");
boolean flag = false;
Game game = games.get(fromUser);
String board[][] = game.board;
String piece = board[move.fromX][move.fromY];
System.out.println(piece);
if(piece.contains("k.png"))
{
flag = ValidateMove.validateKing(move.fromX,move.fromX,move.toX,move.toY);
}else if(piece.contains("p.png"))
{
System.out.print("is pawn");
boolean isWhite = piece.contains("w");
flag = ValidateMove.validatePawn(move.fromX,move.fromY,move.toX,move.toY,isWhite,false);
}else
{
flag = ValidateMove.validate(piece,move.fromX,move.fromY,move.toX,move.toY);
}
if(flag){
String pieceString = board[move.fromX][move.fromY];
board[move.toX][move.toY] = pieceString;
System.out.println(fromUser+" is putting move for "+move.toUser);
System.out.println("ends here");
moves.put(move.toUser, move);
return true;
}
System.out.println("ends  with false here ");
return false;
}
@Path("/getMove")
public Move getOpponentsMove(String username)
{
System.out.println("move got called from "+username);
Move move = moves.get(username);
if(move==null)
{
System.out.println("not found its move");
}
moves.remove(username);
return move;
}
@Path("/shareBoard")
public void shareBoard(String user1,String user2)
{
System.out.println("shareBoard got called");
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
System.out.println("shareBoard completes here");
}
@Path("/getBoard")
public String[][] getBoard(String username)
{
System.out.println("getBoard got called from "+username);
Game game = games.get(username);
if(game==null)
{
return null;
}
System.out.println("getBoard completes here");
return game.board;
}
}
