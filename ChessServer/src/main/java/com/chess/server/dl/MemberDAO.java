package com.chess.server.dl;
import java.util.*;
public class MemberDAO
{
public List<MemberDTO> getAll()
{
List<MemberDTO> members = new LinkedList<>();
MemberDTO m = new MemberDTO();
m.username = "Amit";
m.password = "amit";
members.add(m);
m = new MemberDTO();
m.username = "Bobby";
m.password = "bobby";
members.add(m);
m = new MemberDTO();
m.username = "Chetan";
m.password = "chetan";
members.add(m);
m = new MemberDTO();
m.username = "Rohit";
m.password ="rohit";
members.add(m);
return members;
}
}