package com.chess.common;

public class Message implements java.io.Serializable
{
public String fromUsername;
public String toUsername;
public MESSAGE_TYPE type;
public boolean equals(Object object)
{
if(object instanceof Message msg)
{
return this.toUsername.equals(msg.toUsername) && this.type != null && this.type == msg.type;
}
return false;
}
public int hashCode()
{
return java.util.Objects.hash(toUsername, type);
}
}
