package com.chess.board;

class Move
{
public int  x;
public int  y;
Move(int x,int y)
{
this.x =x;
this.y =y;
}
}
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
}
return false;
}
public static boolean isStalemate(String board[][],String king)
{
String myPieces = king.equals("bk.png")?"b":"w";
if(isInCheck(board, king))
{
return false;
}
for(int i=0; i<8; i++)
{
for(int j=0; j<8; j++)
{
if(board[i][j]!=null && board[i][j].startsWith(myPieces))
{
System.out.println("piece at "+i+","+j+" is "+board[i][j]);
java.util.List<Move> moves = generatePossibleMoves(board,i,j);
if(moves==null)
{
System.out.println("No valid move");
return true;
}
for(Move move:moves)
{
System.out.println(board[i][j]+" "+move.x+","+move.y);
if(isLegalMove(board,move,king,i,j))
{
return false;
}
}
}
}
}
return true;
}
public static java.util.List<Move> generatePossibleMoves(String board[][],int i,int j)
{
java.util.List<Move> list = new java.util.ArrayList<>();
Move move =null;
String piece = board[i][j];
if(piece.equals("bp.png"))
{
int l,m;
l=m=0;
l = i;
m = j;
m++;
while(m<=7)
{
if(board[l][m]!=null)
{
move = new Move(l,m);
list.add(move);
break;
}
m++;
}
l = i;
m = j; 
l++;
m--;
if(l<=7 && m>=0)
{
if(board[l][m]!=null && board[l][m].startsWith("w"))
{
move = new Move(l,m);
list.add(move);
}
}
l = i;
m = j; 
l++;
m++;
if(l<=7 && m<=7)
{
if(board[l][m]!=null && board[l][m].startsWith("w"))
{
move = new Move(l,m);
list.add(move);
}
}
}else if(piece.equals("wp.png"))
{
int l,m;
l=m=0;
l = i;
m = j;
m++;
while(m<=7)
{
if(board[l][m]!=null)
{
move = new Move(l,m);
list.add(move);
}
m++;
}
l = i;
m = j; 
l--;
m--;
if(l>=0 && m>=0)
{
if(board[l][m]!=null && board[l][m].startsWith("b"))
{
move = new Move(l,m);
list.add(move);
}
}
l = i;
m = j; 
l--;
m++;
if(l>=0 && m<=7)
{
if(board[l][m]!=null && board[l][m].startsWith("b"))
{
move = new Move(l,m);
list.add(move);
}
}
}else if(piece.substring(1).equals("b.png"))
{
l = i;
m = j;
l++;
m++;
while (l<=7 && m<=7)
{
if(board[l][m]==null)
{
move = new Move(i, j);
list.add(move);
break;
}
if(board[l][m]!=null && !board[l][m].startsWith(piece))
{
move = new Move(i, j);
list.add(move);
break;
}
l++;
m++;
}
l = i;
m = j; 
l--;
m--;
while(l>=0 && m>=0)
{
if(board[l][m]==null)
{
move = new Move(i, j);
list.add(move);
break;
}
if(board[l][m]!=null && !board[l][m].startsWith(piece))
{
move = new Move(i, j);
list.add(move);
break;
}
l--;
m--;
}
l = i;
m = j; 
l++;
m--;
while(l<=7)
{
if(board[l][m]==null)
{
move = new Move(i, j);
list.add(move);
break;
}
if(board[l][m]!=null && !board[l][m].startsWith(piece))
{
move = new Move(i, j);
list.add(move);
break;
}
l++;
m--;
}
l = i;
m = j; 
l--;
m++;
while(l>=0) 
{
if(board[l][m]==null)
{
move = new Move(i, j);
list.add(move);
break;
}
if(board[l][m]!=null && !board[l][m].startsWith(piece))
{
move = new Move(i, j);
list.add(move);
break;
}
l--;
m++;
}
}else if(piece.substring(1).equals("k.png")) 
{
    int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
    int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};
    
    for(int d=0; d<8; d++){
        int l = i + dx[d], m = j + dy[d];
        if(l>=0 && l<=7 && m>=0 && m<=7){
            if(board[l][m]==null || !board[l][m].startsWith(piece.substring(0,1))){
                move = new Move(i, j);
                list.add(move);
            }
        }
    }
}
else if(piece.substring(1).equals("r.png")) // Rook
{
    // Up
    for(int l=i-1; l>=0; l--){
        if(board[l][j]!=null){
            if(!board[l][j].startsWith(piece.substring(0,1)))
                list.add(new Move(i, j));
            break;
        }
        list.add(new Move(i, j));
    }
    // Down
    for(int l=i+1; l<=7; l++){
        if(board[l][j]!=null){
            if(!board[l][j].startsWith(piece.substring(0,1)))
                list.add(new Move(i, j));
            break;
        }
        list.add(new Move(i, j));
    }
    // Left
    for(int m=j-1; m>=0; m--){
        if(board[i][m]!=null){
            if(!board[i][m].startsWith(piece.substring(0,1)))
                list.add(new Move(i, j));
            break;
        }
        list.add(new Move(i, j));
    }
    // Right
    for(int m=j+1; m<=7; m++){
        if(board[i][m]!=null){
            if(!board[i][m].startsWith(piece.substring(0,1)))
                list.add(new Move(i, j));
            break;
        }
        list.add(new Move(i, j));
    }
}else if(piece.substring(1).equals("q.png")) // Queen
{
    int[] dx = {-1, 1, 0, 0, -1, -1, 1, 1};
    int[] dy = {0, 0, -1, 1, -1, 1, -1, 1};
    
    for(int d=0; d<8; d++){
        int l = i + dx[d];
        int m = j + dy[d];
        while(l>=0 && l<=7 && m>=0 && m<=7){
            if(board[l][m]!=null){
                if(!board[l][m].startsWith(piece.substring(0,1)))
                    list.add(new Move(i, j));
                break;
            }
            list.add(new Move(i, j));
            l += dx[d];
            m += dy[d];
        }
    }
} else if(piece.substring(1).contains("kt.png")) // Knight
{
    int[] dx = {-2,-2,-1,-1,1,1,2,2};
    int[] dy = {-1,1,-2,2,-2,2,-1,1};
    
    for(int d=0; d<8; d++){
        int l = i + dx[d], m = j + dy[d];
        if(l>=0 && l<=7 && m>=0 && m<=7){
            if(board[l][m]==null || !board[l][m].startsWith(piece.substring(0,1))){
                move = new Move(i, j);
                list.add(move);
            }
        }
    }
}
return list;
}
public static boolean isLegalMove(String [][] board ,Move move,String king,int l,int m)
{
String boardc[][] = new String[8][8];
for(int i=0; i<8; i++)
{
for(int j=0; j<8; j++)
{
boardc[i][j] = board[i][j];
}
}
String movingPiece = boardc[l][m];
boardc[move.x][move.y] = movingPiece;
boardc[l][m] = null;
return isInCheck(boardc,king);
}
}