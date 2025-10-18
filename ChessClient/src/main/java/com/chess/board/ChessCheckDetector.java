package com.chess.board;
public class ChessCheckDetector
{
public static int l;
public static int m;
public static boolean hasCheck;
public static void  printCheckI()
{
System.out.println("check from "+l+","+m);
}
public static boolean check(String board[][],String king,int x1,int y1)
{
String opponentsColor = (king.equals("bk.png"))?"w":"b";
String oPawn = (opponentsColor.equals("w"))?"wp.png":"bp.png";
String oQueen =(opponentsColor.equals("w"))?"wq.png":"bq.png";
String oBishop = (opponentsColor.equals("w"))?"wb.png":"bb.png";
String oRook = (opponentsColor.equals("w"))?"wr.png":"br.png";
String oKnight = (opponentsColor.equals("w"))?"wkt.png":"bkt.png";

int x =x1; 
int y =y1;
x++;

while(x<=7)
{
if(board[x][y]!=null)
{
if(board[x][y].equals(oRook)) return true;
else if(board[x][y].equals(oQueen)) return true;
else break;
}
x++;
}
x = x1;
y = y1;
x--;
while(x>=0)
{
if(board[x][y]!=null)
{
if(board[x][y].equals(oRook)) return true;
else if(board[x][y].equals(oQueen)) return true;
else break;
}
x--;
}
x = x1;
y = y1;
y++;
while(y<=7)
{
if(board[x][y]!=null)
{
if(board[x][y].equals(oRook)) return true;
else if(board[x][y].equals(oQueen)) return true;
else break;
}
y++;
}
x = x1;
y = y1;
y--;
while(y>=0)
{
if(board[x][y]!=null)
{
if(board[x][y].equals(oRook)) return true;
else if(board[x][y].equals(oQueen)) return true;
else break;
}
y--;
}

x = x1;
y = y1;
x--;
y++;
while(y<=7 && x>=0)
{
if(board[x][y]!=null)
 {
int dx = Math.abs(x-x1); 
int dy = Math.abs(y-y1);
if(dx==1 && dy==1 && board[x][y].equals(oPawn))
{
if(oPawn.contains("w") && x1<x)
{
return true;
}else if(oPawn.contains("b") && x1>x)
{
return true;
}
}

if(board[x][y].equals(oQueen)) return true;
if(board[x][y].equals(oBishop)) return true;
else break;
}
x--;
y++;
}

x = x1;
y = y1;
x++;
y--;
while(y>=0 && x<=7)
{
if(board[x][y]!=null)
{
int dx = Math.abs(x-x1); 
int dy = Math.abs(y-y1);
if(dx==1 && dy==1 && board[x][y].equals(oPawn))
{
if(oPawn.contains("w") && x1<x)
{
return true;
}else if(oPawn.contains("b") && x1>x)
{
return true;
}
}

if(board[x][y].equals(oQueen)) return true;
if(board[x][y].equals(oBishop)) return true;
else break;
}
x++;
y--;
}

x = x1;
y = y1;
x--;
y--;
while(y>=0 && x>=0)
{
if(board[x][y]!=null)
{
int dx = Math.abs(x-x1); 
int dy = Math.abs(y-y1);
if(dx==1 && dy==1 && board[x][y].equals(oPawn))
{
if(oPawn.contains("w") && x1<x)
{
return true;
}else if(oPawn.contains("b") && x1>x)
{
return true;
}
}

if(board[x][y].equals(oQueen)) return true;
if(board[x][y].equals(oBishop)) return true;
else break;
}
x--;
y--;
}
x = x1;
y = y1;
x++;
y++;
while(x<=7 && y<=7)
{
if(board[x][y]!=null)
{
int dx = Math.abs(x-x1); 
int dy = Math.abs(y-y1);
if(dx==1 && dy==1 && board[x][y].equals(oPawn))
{
if(oPawn.contains("w") && x1<x)
{
return true;
}else if(oPawn.contains("b") && x1>x)
{
return true;
}
}
if(board[x][y].equals(oQueen))  return true;
if(board[x][y].equals(oBishop)) return true;
else break;
}
x++;
y++;
}
x = x1;
y = y1;
int knightMoves[][]= {{2,1},{1,2},{-1,2},{-1,-2},{-2,1},{1,-2},{-2,-1},{2,-1}};
for(int move[]:knightMoves)
{
int i = x+move[0];
int j = y+move[1];
if(i<=7 && j<=7 && j>=0 && i>=0)
{
if(board[i][j]!=null && board[i][j].equals(oKnight)) return true;
}
}
return false;
}

public static boolean isInCheck(String board[][],String king)
{
String opponentsColor = (king.equals("bk.png"))?"w":"b";
String oPawn = (opponentsColor.equals("w"))?"wp.png":"bp.png";
String oQueen =(opponentsColor.equals("w"))?"wq.png":"bq.png";
String oBishop = (opponentsColor.equals("w"))?"wb.png":"bb.png";
String oRook = (opponentsColor.equals("w"))?"wr.png":"br.png";
String oKnight = (opponentsColor.equals("w"))?"wkt.png":"bkt.png";
int x1,y1;
x1=0;
y1=0;
boolean flag = false;
for(int i=0; i<8; i++)
{
for(int j=0; j<8; j++)
{
if(board[i][j]!=null && board[i][j].equals(king))
{
x1 = i;
y1 = j;
flag = true;
break;
}
}
}
if(!flag)
{
return false;
}
int x =x1; 
int y =y1;
x++;

while(x<=7)
{
if(board[x][y]!=null)
{
if(board[x][y].equals(oRook))
{
l=x;
m=y;
hasCheck=true;
return true;
}
else if(board[x][y].equals(oQueen))
{
l=x;
m=y;
hasCheck=true;
return true;
}
else break;
}
x++;
}
x = x1;
y = y1;
x--;
while(x>=0)
{
if(board[x][y]!=null)
{
if(board[x][y].equals(oRook))
{
l=x;
m=y;
hasCheck=true;
return true;
}
else if(board[x][y].equals(oQueen))
{
l=x;
m=y;
hasCheck=true;
return true;
}
else break;
}
x--;
}
x = x1;
y = y1;
y++;
while(y<=7)
{
if(board[x][y]!=null)
{
if(board[x][y].equals(oRook))
{
l=x;
m=y;
hasCheck=true;
return true;
}
else if(board[x][y].equals(oQueen))
{
l=x;
m=y;
hasCheck=true;
return true;
}
else break;
}
y++;
}
x = x1;
y = y1;
y--;
while(y>=0)
{
if(board[x][y]!=null)
{
if(board[x][y].equals(oRook))
{
l=x;
m=y;
hasCheck=true;
return true;
}
else if(board[x][y].equals(oQueen))
{
l=x;
m=y;
hasCheck=true;
return true;
}
else break;
}
y--;
}
x = x1;
y = y1;
x--;
y++;
while(y<=7 && x>=0)
{
if(board[x][y]!=null)
 {
int dx = Math.abs(x-x1); 
int dy = Math.abs(y-y1);
if(dx==1 && dy==1 && board[x][y].equals(oPawn))
{
if(oPawn.contains("w") && x1<x)
{
l=x;
m=y;
hasCheck=true;
return true;
}else if(oPawn.contains("b") && x1>x)
{
l=x;
m=y;
hasCheck=true;
return true;
}
}
if(board[x][y].equals(oQueen))
{ 
l=x;
m=y;
hasCheck=true;
return true;
}
if(board[x][y].equals(oBishop))
{
l=x;
m=y;
hasCheck=true;
return true;
}
else break;
}
x--;
y++;
}

x = x1;
y = y1;
x++;
y--;
while(y>=0 && x<=7)
{
if(board[x][y]!=null)
{
int dx = Math.abs(x-x1); 
int dy = Math.abs(y-y1);
if(dx==1 && dy==1 && board[x][y].equals(oPawn))
{
if(oPawn.contains("w") && x1<x)
{
l=x;
m=y;
hasCheck=true;
return true;
}else if(oPawn.contains("b") && x1>x)
{
l=x;
m=y;
hasCheck=true;
return true;
}
}
if(board[x][y].equals(oQueen))
{
l=x;
m=y;
hasCheck=true;
return true;
}
if(board[x][y].equals(oBishop))
{
l=x;
m=y;
hasCheck=true;
return true;
}
else break;
}
x++;
y--;
}

x = x1;
y = y1;
x--;
y--;
while(y>=0 && x>=0)
{
if(board[x][y]!=null)
{
int dx = Math.abs(x-x1); 
int dy = Math.abs(y-y1);
if(dx==1 && dy==1 && board[x][y].equals(oPawn))
{
if(oPawn.contains("w") && x1<x)
{
l=x;
m=y;
hasCheck=true;
return true;
}else if(oPawn.contains("b") && x1>x)
{
l=x;
m=y;
hasCheck=true;
return true;
}
}
if(board[x][y].equals(oQueen))
{
l=x;
m=y;
hasCheck=true;
return true;
}
if(board[x][y].equals(oBishop))
{
l=x;
m=y;
hasCheck=true;
return true;
}
else break;
}
x--;
y--;
}
x = x1;
y = y1;
x++;
y++;
while(x<=7 && y<=7)
{
if(board[x][y]!=null)
{
int dx = Math.abs(x-x1); 
int dy = Math.abs(y-y1);
if(dx==1 && dy==1 && board[x][y].equals(oPawn))
{
if(oPawn.contains("w") && x1<x)
{
l=x;
m=y;
hasCheck=true;
return true;
}else if(oPawn.contains("b") && x1>x)
{
l=x;
m=y;
hasCheck=true;
return true;
}
}
if(board[x][y].equals(oQueen))
{
l=x;
m=y;
hasCheck=true;
return true;
}
if(board[x][y].equals(oBishop))
{
l=x;
m=y;
hasCheck=true;
return true;
}
else break;
}
x++;
y++;
}
x = x1;
y = y1;
int knightMoves[][]= {{2,1},{1,2},{-1,2},{-1,-2},{-2,1},{1,-2},{-2,-1},{2,-1}};
for(int move[]:knightMoves)
{
int i = x+move[0];
int j = y+move[1];
if(i<=7 && j<=7 && j>=0 && i>=0)
{
if(board[i][j]!=null && board[i][j].equals(oKnight)) 
{
l=x;
m=y;
hasCheck=true;
return true;
}
}
}
return false;
}
public static boolean hasAnyLegalMove(String board[][],String king)
{
String myColor = (king.equals("bk.png"))?"b":"w";
boolean flag=false;
int x1,y1;
x1=0;
y1=0;
for(int i=0; i<8; i++)
{
for(int j=0; j<8; j++)
{
if(board[i][j]!=null && board[i][j].equals(king))
{
x1 = i;
y1 = j;
flag = true;
break;
}
}
}
if(!flag)
{
return true;
}
boolean hasValidMove = false;
int  kingvalidMove[][] = {{1,1},{-1,-1},{-1,1},{1,-1},{-1,0},{1,0},{0,1},{0,-1}};
if(hasCheck)
{
for(int emptySquare[]:kingvalidMove)
{
int x = emptySquare[0]+x1;
int y = emptySquare[1]+y1;
if(x<=7 && x>=0 && y<=7 && y>=0)
{
if(board[x][y]!=null && board[x][y].startsWith(king.substring(0,1)))
{
continue; 
}
boolean isInCheck = ChessCheckDetector.check(board,king,x,y);
if(!isInCheck)
{
hasValidMove = true;
}
}
}
if(hasValidMove)
{
hasCheck=false;
return true;
}
boolean canCapture =false;
for(int i=0; i<=7; i++)
{
for(int j=0; j<=7; j++)
{
if(board[i][j]!=null && board[i][j].startsWith(myColor))
{
String piece = board[i][j];
if(piece.equals("wp.png"))
{
canCapture = ValidateMove.validatePawn(i,j,l,m,true,true);
}else if(piece.equals("bp.png"))
{
canCapture = ValidateMove.validatePawn(i,j,l,m,false,false);
}else
{
canCapture = ValidateMove.validate(piece,i,j,l,m);
}
if(canCapture)
{
hasCheck=false;
return true;
}
}
}
}
return false;
}
else
{
hasValidMove = false;
for(int emptySquare[]:kingvalidMove)
{
int x = emptySquare[0]+x1;
int y = emptySquare[1]+y1;
if(x<=7 && x>=0 && y<=7 && y>=0)
{
hasValidMove = ChessCheckDetector.check(board,king,x,y);
if(!hasValidMove)
{
return true;
}
}
}
// check another piece that they can move without leaving there king in check
}
return false;
}
}