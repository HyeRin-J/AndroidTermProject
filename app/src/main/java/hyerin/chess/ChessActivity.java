package hyerin.chess;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

<<<<<<< HEAD
public class ChessActivity extends AppCompatActivity{
  protected int chessBoard[8][8];
  protected boolean BorW;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chess);
  }
  
  /*
  protected int[][] CheckPosition(){
    //check
    return chessBoard;
  }
  */
  
  public void chessInit(){
    int Temp_i,Temp_j;
    
    //Black's turn or White's turn. false=White
    BorW=false;
    
    //Empty board init
    for(Temp_i = 2;i<5;i++){
      for(Temp_j = 0;j<8;j++){
        chessBoard[i][j]=0;
      }
    }
    //Pawn init
    for(Temp_j = 0;j<8;j++){
      //Blue Team
      chessBoard[1][j]=1;
      //Red Team
      chessBoard[6][j]=-1;
    }
    
    //Knight init
    chessBoard[0][1]=2;
    chessBoard[0][6]=2;
    chessBoard[7][1]=-2;
    chessBoard[7][6]=-2;
    
    //Bishop init
    chessBoard[0][2]=3;
    chessBoard[0][5]=3;
    chessBoard[7][2]=-3;
    chessBoard[7][5]=-3;
    
    //Rook init
    chessBoard[0][0]=4;
    chessBoard[0][7]=4;
    chessBoard[7][0]=-4;
    chessBoard[7][7]=-4;
    
    //Queen init
    chessBoard[0][4]=5;
    chessBoard[7][4]=-5;
    
    //King init
    chessBoard[0][3]=6;
    chessBoard[7][3]=-6;
  }
  
  public void MoveUnit(int type, int Currentlocation[int cx][int cy], int Headinglocation[int hx][int hy]){
    if(Currentlocation[cx][cy]==type)){//unit is there
      Currentlocation[cx][cy]=0;
      if(Headinglocation[hx][hy] != 0){//scored
        print("u killed "+ Headinglocation[hx][hy]+"\n");
      }
      Headinglocation[hx][hy]=type;
    }
  }
  
  protected boolean CheckCheckmate(){
    boolean Checkmate=false;
    int Unit[7]={0,1,2,3,4,5,6};
    int TempX,TempY;
    int KingLocationX,KingLocationY;
    
    if(BorW){//Black
      for(TempX=1;TempX<7;TempX++){
        Unit[TempX]=Unit[TempX]*-1;
      }
    }//Else White
    
    for(TempX=0;TempX<7;TempX++){
      for(TempY=0;TempY<7;TempY++){
        if(chessboard[TempX][TempY]==(Unit[6]]*-1)){
          KingLocationY=TempY;
          Break;
        }
      }
      if(chessBoard[TempX][TempY]==(Unit[6]]*-1)){
          KingLocationY=TempX;
        Break;
      }
    }
    
    //Rock or Queen or King can Attack
      //X axis
      for(TempX=0;TempX<7;TempX++){
        if(chessBoard[TempX][KingLocationY]==Unit[6] || chessBoard[TempX][KingLocationY]==Unit[4] || chessBoard[TempX][KingLocationY]==Unit[5]){
          Checkmate=true;
          break;
        }
      }
      //Y axis
      for(TempY=0;TempY<7;TempY++){
        if(chessBoard[TempX][KingLocationY]==Unit[6] || chessBoard[KingLocationX][TempY]==Unit[4] || chessBoard[KingLocationX][TempY]==Unit[5]){
          Checkmate=true;
          break;
        }
      }
    
    //Bishop or Pawn or Queen or King can Attack
      // Diagonals
      for(TempX=KingLocationX;TempX>=0;TempX--){
        for(TempY=KingLocationY;TempY>=0;TempY--){
          if(chessBoard[TempX][TempY] == Unit[3] || chessBoard[TempX][TempY] == Unit[1] || chessBoard[TempX][TempY] == Unit[5] || chessBoard[TempX][TempY] == Unit[6]){
            Checkmate=true;
            break;
          }
        }
        if(chessBoard[TempX][TempY] == Unit[3] || chessBoard[TempX][TempY] == Unit[1] || chessBoard[TempX][TempY] == Unit[5] || chessBoard[TempX][TempY] == Unit[6]){
          Checkmate=true;
          break;
        }
      }
      for(TempX=KingLocationX;TempX<8;TempX++){
        for(TempY=KingLocationY;TempY<8;TempY--){
          if(chessBoard[TempX][TempY] == Unit[3] || chessBoard[TempX][TempY] == Unit[1] || chessBoard[TempX][TempY] == Unit[5] || chessBoard[TempX][TempY] == Unit[6]){
            Checkmate=true;
            break;
          }
        }
        if(chessBoard[TempX][TempY] == Unit[3] || chessBoard[TempX][TempY] == Unit[1] || chessBoard[TempX][TempY] == Unit[5] || chessBoard[TempX][TempY] == Unit[6]){
          Checkmate=true;
          break;
        }
      }
    
    //Knight check
      //first Quadrant
      if((KingLocationX+1 <8 && KingLocationX+1>=0)&& (KingLocationY+2 <8 && KingLocationY+2>=0)){
        if(chessBoard[KingLocationX+1][KingLocationY+2] == Unit[2]){
            Checkmate=true;
        }
      }
      if((KingLocationX+2 <8 && KingLocationX+2>=0)&& (KingLocationY+1 <8 && KingLocationY+1>=0)){
        if(chessBoard[KingLocationX+2][KingLocationY+1] == Unit[2]){
            Checkmate=true;
        }
      }
      
      //second Quadrant
      if((KingLocationX-1 <8 && KingLocationX-1>=0)&& (KingLocationY+2 <8 && KingLocationY+2>=0)){
        if(chessBoard[KingLocationX-1][KingLocationY+2] == Unit[2]){
            Checkmate=true;
        }
      }
      if((KingLocationX-2 <8 && KingLocationX-2>=0)&& (KingLocationY+1 <8 && KingLocationY+1>=0)){
        if(chessBoard[KingLocationX-2][KingLocationY+1] == Unit[2]){
            Checkmate=true;
        }
      }
      
      //third Quadrant
      if((KingLocationX-1 <8 && KingLocationX-1>=0)&& (KingLocationY-2 <8 && KingLocationY-2>=0)){
        if(chessBoard[KingLocationX-1][KingLocationY-2] == Unit[2]){
            Checkmate=true;
        }
      }
      if((KingLocationX-2 <8 && KingLocationX-2>=0)&& (KingLocationY-1 <8 && KingLocationY-1>=0)){
        if(chessBoard[KingLocationX-2][KingLocationY-1] == Unit[2]){
            Checkmate=true;
        }
      }
      
      //fourth Quadrant
      if((KingLocationX+1 <8 && KingLocationX+1>=0)&& (KingLocationY-2 <8 && KingLocationY-2>=0)){
        if(chessBoard[KingLocationX+1][KingLocationY-2] == Unit[2]){
            Checkmate=true;
        }
      }
      if((KingLocationX+2 <8 && KingLocationX+2>=0)&& (KingLocationY-1 <8 && KingLocationY-1>=0)){
        if(chessBoard[KingLocationX+2][KingLocationY-1] == Unit[2]){
            Checkmate=true;
        }
      }
    
    return Checkmate;
  }
}  
=======
/**
 * Created by 혜린 on 2016-12-16.
 */

public class ChessActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess);
    }
}
>>>>>>> ddd49bffc25fba22b3ffff6567f4f79d4eb4dc73
