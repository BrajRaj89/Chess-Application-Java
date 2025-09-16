package com.chess.board;
public class Move implements java.io.Serializable
{
public byte player;
public byte piece;
public int fromX;
public int fromY;
public int toX;
public int toY;
public boolean isLastMove;
}