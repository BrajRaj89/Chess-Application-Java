package com.chess.server.dl;

public class MemberDTO
{
public String username;
public String password;
MemberDTO()
{
this.username="";
this.password="";
}
public void setUsername(String username)
{
this.username = username;
}
public String getUsername()
{
return this.username;
}
public void setPassword(String password)
{
this.password = password;
}
public String getPassword()
{
return this.password;
}
public boolean equals(Object obj)
{
if(this==obj) return true;
if (obj == null || getClass() != obj.getClass()) return false;
MemberDTO other = (MemberDTO) obj;
return username.equals(other.username) && password.equals(other.password);
}
public int compareTo(MemberDTO other)
{
return this.username.compareTo(other.username);
}
public int hashCode()
{
return username.hashCode();
}
}