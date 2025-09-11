package com.chess.server;
import com.chess.server.dl.*;
import com.chess.common.*;
import com.nframework.server.annotations.*;
import java.util.*;

@Path("/TMChessServer")
public class TMChessServer
{
static private Map<String,Member> members;
static private Set<String> loggedInMembers;
static private Set<String>  playingMembers;
static private Map<String,List<Message>>inboxes;
static private Map<String,Game> games;
static
{
populateDataStructures();
}
public TMChessServer()
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
@Path("canIplay")
public boolean canIplay(String gameId,String username)
{
return false;
}
@Path("submitMove")
public void submitMove(String byUsername,byte piece,int fromX,int fromY,int toX,int toY)
{

}
public Move getOpponentsMove(String username)
{

return null;
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
}
@Path("getBoard")
public String[][] getBoard(String username)
{
Game game = games.get(username);
if(game==null)
{
return null;
}
return game.board;
}
@Path("getOpponentPlayer")
public String getOpponentPlayer(String username)
{
Game game = games.get(username);
return game.user2;
}
}
