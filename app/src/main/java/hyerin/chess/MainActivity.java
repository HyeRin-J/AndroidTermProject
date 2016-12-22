<<<<<<< HEAD
package kr.ac.koreatech.choyeunghyeun.test;

import android.os.Handler;
=======
package hyerin.chess;

import android.content.Intent;
>>>>>>> ddd49bffc25fba22b3ffff6567f4f79d4eb4dc73
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
<<<<<<< HEAD
import android.widget.Toast;

import hyerin.chess.R;

public class MainActivity extends AppCompatActivity {
    //보드.
    protected int chessBoard[][] = new int[8][8];

    //말 좌표 설정. cxcy는 현재, hxhy는 이동할거
    protected int cx,cy,hx,hy;

    //누가 누름? - 이동관련
    protected boolean WhoPressed=false;

    //누가 항복 누름?
    protected boolean BlackSurrender=false;
    protected boolean WhiteSurrender=false;

    //현재 쓰레드 동작중?
    protected boolean Running = false;

    //체스 쓰레드
    protected chessThread chess;
    //핸들러
    protected Handler cHandler = new Handler();
        
=======

public class MainActivity extends AppCompatActivity {

>>>>>>> ddd49bffc25fba22b3ffff6567f4f79d4eb4dc73
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
<<<<<<< HEAD
        chess = new chessThread();
    }
    
=======
    }

>>>>>>> ddd49bffc25fba22b3ffff6567f4f79d4eb4dc73
    public void Onclick(View view){
        Button button = (Button)view;
        switch(button.getId()){
            case R.id.Bluetooth:
                break;
            case R.id.Chess_Activity:
<<<<<<< HEAD
                if(Running){

                }else {
                    chess.chessRun();
                }
                break;
        }
    }

    class chessThread extends Thread{
        //Turn
        private boolean BorW;
        //Surrender
        private boolean Surrender = false;
        
        public void chessRun(){
            ChessInit();
            boolean KingSlainedTrigger[]={true,true};

            while(Surrender == false){
                //get data through bluetooth

                if(WhoPressed == BorW){
                    MoveUnit();
                    if(Checkmate()){
                        if(BorW){
                            Toast.makeText(getApplicationContext(),"Checkmate(Warn - Black)",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(),"Checkmate(Warn - Black)",Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(BorW){
                        //White Turn
                        BorW = false;
                      }else{
                          //Black Turn
                          BorW = true;
                      }

                      if(BlackSurrender){
                          Surrender=true;
                          Toast.makeText(getApplicationContext(),"Black Surrendered",Toast.LENGTH_SHORT).show();
                      }
                      if(WhiteSurrender){
                          Surrender=true;
                          Toast.makeText(getApplicationContext(),"White Surrendered",Toast.LENGTH_SHORT).show();
                      }

                      for(int tempX = 0;tempX < 8;tempX++){
                          for(int tempY = 0;tempY < 8;tempY++){
                              //BlackKing
                              if(chessBoard[tempX][tempY] == 6){
                                  KingSlainedTrigger[0]=false;
                              }
                              //WhiteKing
                              if(chessBoard[tempX][tempY] == -6){
                                  KingSlainedTrigger[1]=false;
                              }
                          }
                      }
                    if(KingSlainedTrigger[0]){
                      Surrender=true;
                      Toast.makeText(getApplicationContext(),"Black King Slained",Toast.LENGTH_SHORT).show();
                  }else{
                      KingSlainedTrigger[0]=true;
                  }
  
                  if(KingSlainedTrigger[1]){
                      Surrender=true;
                      Toast.makeText(getApplicationContext(),"Black King Slained",Toast.LENGTH_SHORT).show();
                  }else{
                      KingSlainedTrigger[1]=true;
                  }
                    cHandler.post(new Runnable(){
                        @Override
                        public void run(){
                            //핸들러 만들어놨다.
                        }
                    });
                }
            }

        }
        protected void ChessInit(){
            int Temp_i,Temp_j;

            //Black's turn or White's turn. false=White
            BorW=false;

            //Empty board init
            for(Temp_i = 2;Temp_i<5;Temp_i++){
                for(Temp_j = 0;Temp_j<8;Temp_j++){
                    chessBoard[Temp_i][Temp_j]=0;
                }
            }
            //Pawn init
            for(Temp_j = 0;Temp_j<8;Temp_j++){
                //Black Team
                chessBoard[1][Temp_j]=1;
                //White Team
                chessBoard[6][Temp_j]=-1;
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

        protected void MoveUnit(){
            //cx cy : Current x,y / hx hy : Heading x,y
            if(chessBoard[cx][cy]>0 || chessBoard[cx][cy]<0){//unit is there
                int type = chessBoard[cx][cy];
                boolean canMove = false;

                //BlackPawn
                if(type == 1 && chessBoard[hx][hy]<=0){
                    if(hx == cx +1 && hy == cy+1){
                        canMove=true;
                    }
                    if(hx == cx -1 && hy == cy+1 && canMove == false){
                        canMove=true;
                    }
                    if(cy==1 && cx == hx && hy == 3 && canMove == false){
                        canMove=true;
                    }
                }
                //WhitePawn
                if(type == -1 && chessBoard[hx][hy]>=0){
                    if(hx == cx +1 && hy == cy-1){
                        canMove=true;
                    }
                    if(hx == cx -1 && hy == cy-1 && canMove == false){
                        canMove=true;
                    }
                    if(cy==6 && cx == hx && hy == 4 && canMove == false){
                        canMove=true;
                    }
                }

                //BlackKnight
                if(type == 2 && chessBoard[hx][hy]<=0){
                    if(hx == cx +2 && hy == cy+1){
                        canMove=true;
                    }
                    if(hx == cx +2 && hy == cy-1 && canMove == false){
                        canMove=true;
                    }

                    if(hx == cx -2 && hy == cy+1 && canMove == false){
                        canMove=true;
                    }
                    if(hx == cx -2 && hy == cy-1 && canMove == false){
                        canMove=true;
                    }

                    if(hx == cx +1 && hy == cy +2 && canMove == false){
                        canMove=true;
                    }
                    if(hx == cx -1 && hy == cy +2 && canMove == false){
                        canMove=true;
                    }

                    if(hx == cx +1 && hy == cy -2 && canMove == false){
                        canMove=true;
                    }
                    if(hx == cx -1 && hy == cy -2 && canMove == false){
                        canMove=true;
                    }
                }
                //WhiteKnight
                if(type == -2 && chessBoard[hx][hy]>=0){
                    if(hx == cx +2 && hy == cy+1){
                        canMove=true;
                    }
                    if(hx == cx +2 && hy == cy-1 && canMove == false){
                        canMove=true;
                    }

                    if(hx == cx -2 && hy == cy+1 && canMove == false){
                        canMove=true;
                    }
                    if(hx == cx -2 && hy == cy-1 && canMove == false){
                        canMove=true;
                    }

                    if(hx == cx +1 && hy == cy +2 && canMove == false){
                        canMove=true;
                    }
                    if(hx == cx -1 && hy == cy +2 && canMove == false){
                        canMove=true;
                    }
                    if(hx == cx +1 && hy == cy -2 && canMove == false){
                        canMove=true;
                    }
                    if(hx == cx -1 && hy == cy -2 && canMove == false){
                        canMove=true;
                    }
                }

                //BlackBishop
                if(type == 3 && chessBoard[hx][hy]<=0){
                    int tempX,tempY;
                    tempX=cx-1;
                    tempY=cy-1;
                    while(tempX >= 0 && tempY >= 0 && chessBoard[tempX][tempY] == 0){
                        if(tempX == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempX--;
                        tempY--;
                    }
                    tempX=cx-1;
                    tempY=cy+1;
                    while(tempX >= 0 && tempY < 8 && chessBoard[tempX][tempY] == 0 && canMove == false){
                        if(tempX == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempX--;
                        tempY++;
                    }
                    tempX=cx+1;
                    tempY=cy-1;
                    while(tempX < 8 && tempY >= 0 && chessBoard[tempX][tempY] == 0 && canMove == false){
                        if(tempX == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempX++;
                        tempY--;
                    }

                    tempX=cx+1;
                    tempY=cy+1;
                    while(tempX < 8 && tempY < 8 && chessBoard[tempX][tempY] == 0 && canMove == false){
                        if(tempX == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempX++;
                        tempY++;
                    }
                }

                //WhiteBishop
                if(type == -3 && chessBoard[hx][hy]>=0){
                    int tempX,tempY;
                    tempX=cx-1;
                    tempY=cy-1;
                    while(tempX >= 0 && tempY >= 0 && chessBoard[tempX][tempY] == 0){
                        if(tempX == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempX--;
                        tempY--;
                    }
                    tempX=cx-1;
                    tempY=cy+1;
                    while(tempX >= 0 && tempY < 8 && chessBoard[tempX][tempY] == 0 && canMove == false){
                        if(tempX == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempX--;
                        tempY++;
                    }
                    tempX=cx+1;
                    tempY=cy-1;
                    while(tempX < 8 && tempY >= 0 && chessBoard[tempX][tempY] == 0 && canMove == false){
                        if(tempX == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempX++;
                        tempY--;
                    }

                    tempX=cx+1;
                    tempY=cy+1;
                    while(tempX < 8 && tempY < 8 && chessBoard[tempX][tempY] == 0 && canMove == false){
                        if(tempX == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempX++;
                        tempY++;
                    }
                }

                //BlackRook
                if(type == 4 && chessBoard[hx][hy]<=0){
                    int tempX,tempY;
                    tempX=cx-1;
                    while(tempX >= 0 && chessBoard[tempX][cy] == 0){
                        if(tempX == hx && cy == hy){
                            canMove=true;
                            break;
                        }
                        tempX--;
                    }
                    tempX=cx+1;
                    while(tempX < 8 && chessBoard[tempX][cy] == 0 && canMove == false){
                        if(tempX == hx && cy == hy){
                            canMove=true;
                            break;
                        }
                        tempX++;
                    }
                    tempY=cy-1;
                    while(tempY >= 0 && chessBoard[cx][tempY] == 0 && canMove == false){
                        if(cx == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempY--;
                    }

                    tempY=cy+1;
                    while(tempY < 8 && chessBoard[cx][tempY] == 0 && canMove == false){
                        if(cx == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempY++;
                    }
                }
                //WhiteRook
                if(type == -4 && chessBoard[hx][hy]>=0){
                    int tempX,tempY;
                    tempX=cx-1;
                    while(tempX >= 0 && chessBoard[tempX][cy] == 0){
                        if(tempX == hx && cy == hy){
                            canMove=true;
                            break;
                        }
                        tempX--;
                    }
                    tempX=cx+1;
                    while(tempX < 8 && chessBoard[tempX][cy] == 0 && canMove == false){
                        if(tempX == hx && cy == hy){
                            canMove=true;
                            break;
                        }
                        tempX++;
                    }
                    tempY=cy-1;
                    while(tempY >= 0 && chessBoard[cx][tempY] == 0 && canMove == false){
                        if(cx == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempY--;
                    }

                    tempY=cy+1;
                    while(tempY < 8 && chessBoard[cx][tempY] == 0 && canMove == false){
                        if(cx == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempY++;
                    }
                }


                //BlackQueen
                if(type == 5 && chessBoard[hx][hy]<=0){
                    int tempX,tempY;
                    tempX=cx-1;
                    while(tempX >= 0 && chessBoard[tempX][cy] == 0){
                        if(tempX == hx && cy == hy){
                            canMove=true;
                            break;
                        }
                        tempX--;
                    }
                    tempX=cx+1;
                    while(tempX < 8 && chessBoard[tempX][cy] == 0 && canMove == false){
                        if(tempX == hx && cy == hy){
                            canMove=true;
                            break;
                        }
                        tempX++;
                    }
                    tempY=cy-1;
                    while(tempY >= 0 && chessBoard[cx][tempY] == 0 && canMove == false){
                        if(cx == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempY--;
                    }

                    tempY=cy+1;
                    while(tempY < 8 && chessBoard[cx][tempY] == 0 && canMove == false){
                        if(cx == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempY++;
                    }
                    tempX=cx-1;
                    while(tempX >= 0 && chessBoard[tempX][cy] == 0 && canMove == false){
                        if(tempX == hx && cy == hy){
                            canMove=true;
                            break;
                        }
                        tempX--;
                    }
                    tempX=cx+1;
                    while(tempX < 8 && chessBoard[tempX][cy] == 0 && canMove == false){
                        if(tempX == hx && cy == hy){
                            canMove=true;
                            break;
                        }
                        tempX++;
                    }
                    tempY=cy-1;
                    while(tempY >= 0 && chessBoard[cx][tempY] == 0 && canMove == false){
                        if(cx == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempY--;
                    }

                    tempY=cy+1;
                    while(tempY < 8 && chessBoard[cx][tempY] == 0 && canMove == false){
                        if(cx == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempY++;
                    }
                }
                //WhiteQueen
                if(type == -5 && chessBoard[hx][hy]>=0){
                    int tempX,tempY;
                    tempX=cx-1;
                    while(tempX >= 0 && chessBoard[tempX][cy] == 0){
                        if(tempX == hx && cy == hy){
                            canMove=true;
                            break;
                        }
                        tempX--;
                    }
                    tempX=cx+1;
                    while(tempX < 8 && chessBoard[tempX][cy] == 0 && canMove == false){
                        if(tempX == hx && cy == hy){
                            canMove=true;
                            break;
                        }
                        tempX++;
                    }
                    tempY=cy-1;
                    while(tempY >= 0 && chessBoard[cx][tempY] == 0 && canMove == false){
                        if(cx == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempY--;
                    }

                    tempY=cy+1;
                    while(tempY < 8 && chessBoard[cx][tempY] == 0 && canMove == false){
                        if(cx == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempY++;
                    }
                    tempX=cx-1;
                    while(tempX >= 0 && chessBoard[tempX][cy] == 0 && canMove == false){
                        if(tempX == hx && cy == hy){
                            canMove=true;
                            break;
                        }
                        tempX--;
                    }
                    tempX=cx+1;
                    while(tempX < 8 && chessBoard[tempX][cy] == 0 && canMove == false){
                        if(tempX == hx && cy == hy){
                            canMove=true;
                            break;
                        }
                        tempX++;
                    }
                    tempY=cy-1;
                    while(tempY >= 0 && chessBoard[cx][tempY] == 0 && canMove == false){
                        if(cx == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempY--;
                    }

                    tempY=cy+1;
                    while(tempY < 8 && chessBoard[cx][tempY] == 0 && canMove == false){
                        if(cx == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempY++;
                    }
                }

                //BlackKing
                if(type == 6 && chessBoard[hx][hy]<=0) {
                    int tempX,tempY;
                    for(tempX=cx-1;tempX<=cx+1;tempX++){
                        for(tempY=cy-1;tempY<=cy+1;tempY++){
                            if(tempX == hx && tempY == hy && chessBoard[tempX][tempY]!=6){
                                canMove=true;
                                break;
                            }
                        }
                    }
                }
                //WhiteKing
                if(type == -6 && chessBoard[hx][hy]>=0) {
                    int tempX,tempY;
                    for(tempX=cx-1;tempX<=cx+1;tempX++){
                        for(tempY=cy-1;tempY<=cy+1;tempY++){
                            if(tempX == hx && tempY == hy && chessBoard[tempX][tempY]!=6){
                                canMove=true;
                                break;
                            }
                        }
                    }
                }

                if(canMove == true){
                    if(chessBoard[hx][hy] != 0){
                        Toast.makeText(getApplicationContext(),"You scored something",Toast.LENGTH_SHORT).show();
                    }
                    chessBoard[hx][hy]=type;
                    chessBoard[cx][cy]=0;
                }else{
                    Toast.makeText(getApplicationContext(),"You can't Move to There",Toast.LENGTH_SHORT).show();
                }
            }
        }

        protected boolean Checkmate(){
            boolean Checkmate=false;
            int Unit[] = {0,1,2,3,4,5,6};
            int TempX,TempY;
            int KingLocationX=0,KingLocationY=0;

            if(BorW){
                //Black
                for(TempX=1;TempX<7;TempX++){
                    Unit[TempX]=Unit[TempX]*-1;
                }
            }//Else White

            //find King's Location
            for(TempX=0;TempX<7;TempX++){
                for(TempY=0;TempY<7;TempY++){
                    if(chessBoard[TempX][TempY]==(Unit[6]*-1)){
                        KingLocationY=TempY;
                        break;
                    }
                }
                if(chessBoard[TempX][TempY]==(Unit[6]*-1)){
                    KingLocationY=TempX;
                    break;
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
                TempX=KingLocationX-1;
                TempY=KingLocationY-1;
                while(TempX >= 0 && TempY >= 0){
                    if(chessBoard[TempX][TempY] == Unit[3] || chessBoard[TempX][TempY] == Unit[5] || chessBoard[TempX][TempY] == Unit[6] || chessBoard[TempX][TempY] == Unit[1]){
                        Checkmate=true;
                        break;
                    }
                    TempX--;
                    TempY--;
                }
                TempX=KingLocationX-1;
                TempY=KingLocationY+1;
                while(TempX >= 0 && TempY < 8){
                    if(chessBoard[TempX][TempY] == Unit[3] || chessBoard[TempX][TempY] == Unit[5] || chessBoard[TempX][TempY] == Unit[6] || chessBoard[TempX][TempY] == Unit[1]){
                        Checkmate=true;
                        break;
                    }
                    TempX--;
                    TempY++;
                }
                TempX=KingLocationX+1;
                TempY=KingLocationY-1;
                while(TempX < 8 && TempY >= 0){
                    if(chessBoard[TempX][TempY] == Unit[3] || chessBoard[TempX][TempY] == Unit[5] || chessBoard[TempX][TempY] == Unit[6] || chessBoard[TempX][TempY] == Unit[1]){
                        Checkmate=true;
                        break;
                    }
                    TempX++;
                    TempY--;
                }
                TempX=KingLocationX+1;
                TempY=KingLocationY+1;
                while(TempX < 8 && TempY < 8){
                    if(chessBoard[TempX][TempY] == Unit[3] || chessBoard[TempX][TempY] == Unit[5] || chessBoard[TempX][TempY] == Unit[6] || chessBoard[TempX][TempY] == Unit[1]){
                        Checkmate=true;
                        break;
                    }
                    TempX++;
                    TempY++;
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
                Intent intent = new Intent(this, ChessActivity.class);
                startActivity(intent);
                break;
        }
    }
>>>>>>> ddd49bffc25fba22b3ffff6567f4f79d4eb4dc73
}
