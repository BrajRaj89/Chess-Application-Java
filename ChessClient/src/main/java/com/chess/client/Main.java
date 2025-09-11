package com.chess.client;
import javax.swing.SwingUtilities;
public class Main
{
public static void main(String gg[])
{

SwingUtilities.invokeLater(()->{
try
{
ChessUI chessUI = new ChessUI();
chessUI.loginUI();
}catch(Throwable t)
{
System.out.println("UI Exception "+t.getMessage());
}
});

}
}