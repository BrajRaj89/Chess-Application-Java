package com.chess.server.board;

public class ValidateMove
{
//king move validation 
public static boolean validateKing(int x1,int y1,int x2,int y2)
{
int dx = Math.abs(x2-x1);
int dy = Math.abs(y2-y1);
if(dx<=1 && dy<=1 && (dx+dy!=0))
{
return true;
}
return false;
}
public static boolean validateKing(int x1,int y1,int x2,int y2,boolean kingHasMoved,boolean rookHasMoved,boolean pathClear,boolean isInCheck)
{
int dx = Math.abs(x2-x1);
int dy = Math.abs(y2-y1);
if(!kingHasMoved && !rookHasMoved && pathClear && !isInCheck && dx == 0 && dy == 2)
{
return true;
}
return false;
}
// pawn validation
public static boolean validatePawn(int x1,int y1,int x2,int y2,boolean isWhite,boolean isCapturing)
{
int direction = isWhite ? -1:1;
int startRow =  isWhite ? 6:1;
if(x2==x1+direction && y2==y1 && !isCapturing)
{
return true;
}
if(x1==startRow && x2==x1+2*direction && y2==y1 && !isCapturing)
{
return true;
}
if(x2==x1+direction && Math.abs(y2-y1)==1 && isCapturing)
{
return true;
}
return false;
}
public static boolean validate(String piece,int x1,int y1,int x2,int y2)
{
if(piece.equals("bb.png") || piece.equals("wb.png"))
{
return Math.abs(x2 - x1) == Math.abs(y2 - y1);
}
else if(piece.equals("bkt.png") || piece.equals("wkt.png"))
{
int dx = Math.abs(x2 - x1);
int dy = Math.abs(y2 - y1);
return (dx == 2 && dy == 1) || (dx == 1 && dy == 2);
}
else if(piece.equals("br.png") || piece.equals("wr.png"))
{
return (y1 == y2 || x1 == x2);
}
else if(piece.equals("bq.png") || piece.equals("wq.png"))
{
int dx = Math.abs(x2 - x1);
int dy = Math.abs(y2 - y1);
return (x1 == x2 || y1 == y2 || dx == dy);
}
return false;
}
}