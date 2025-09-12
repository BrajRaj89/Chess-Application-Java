package com.chess.server;
import com.nframework.server.*;

public class Main
{
public static void main(String args[])
{
NFrameworkServer nfs = new NFrameworkServer();
nfs.registerClass(ChessServer.class);
nfs.start();
}
}