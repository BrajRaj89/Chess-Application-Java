package com.chess.server;
import com.nframework.server.*;

public class Main
{
public static void main(String args[])
{
try
{
NFrameworkServer nfs = new NFrameworkServer();
nfs.registerClass(ChessServer.class);
nfs.start();
}catch(Exception e)
{
e.printStackTrace();
}
}
}