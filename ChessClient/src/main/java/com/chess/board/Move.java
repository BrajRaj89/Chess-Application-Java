package com.chess.board;
public class Move implements java.io.Serializable
{
public String toUser;
public String piece;
public int fromX;
public int fromY;
public int toX;
public int toY;
public boolean captured;
public String captureString;
public boolean capturedByWhite;
public boolean isLastMove;
}