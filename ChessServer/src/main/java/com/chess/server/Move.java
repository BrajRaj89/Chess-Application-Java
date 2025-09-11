package com.chess.server;
public class Move implements java.io.Serializable
{
public byte player;
public byte piece;
public byte fromX;
public byte fromY;
public byte toX;
public byte toY;
public boolean isLastMove;
}