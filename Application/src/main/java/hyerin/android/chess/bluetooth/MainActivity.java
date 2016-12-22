package hyerin.android.chess.bluetooth;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bluetoothchat.R;

import hyerin.android.chess.common.activities.SampleActivityBase;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class MainActivity extends SampleActivityBase{
    //보드.
    protected int chessBoard[][] = new int[8][8];

    protected Boolean FLAG = FALSE;
    protected int promotion=0;
    //말 좌표 설정. cxcy는 현재, hxhy는 이동할거
    protected int cx,cy,hx,hy;

    Chess chess = new Chess();

    TextView turnView = null;
    ImageAdapter imageAdapter = null;
    int[] pos = new int[2];
    int horse = 0;
    GridView gridview;

    public void OnClick(View view){
        Button promote = (Button) view;
        switch(promote.getId()){
            case R.id.Queen:
                promotion = 1;
                break;
            case R.id.Bishop:
                promotion = 3;
                break;
            case R.id.Rook:
                promotion = 4;
                break;
            case R.id.Knight:
                promotion = 2;
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        turnView = (TextView) findViewById(R.id.Turn);
        gridview = (GridView) findViewById(R.id.GridView01);
        imageAdapter = new ImageAdapter(this);
        gridview.setAdapter(imageAdapter);
        imageAdapter.Init();
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id){
                pos = getPos(position);
                if(FLAG){
                    if(horse < 0){
                        if((int)imageAdapter.getItem(position) == 0 || (int)imageAdapter.getItem(position) > 0){
                            hx = pos[0];
                            hy = pos[1];
                        }else{
                            horse = 0;
                        }
                    } else if(horse > 0){
                        if((int)imageAdapter.getItem(position) == 0 || (int)imageAdapter.getItem(position) < 0){
                            hx = pos[0];
                            hy = pos[1];
                        }else{
                            horse = 0;
                        }
                    }
                    FLAG = FALSE;
                    chess.chessRun();
                }else{
                    if((int)imageAdapter.getItem(position) != 0) {
                        horse = (int) imageAdapter.getItem(position);
                        if(chess.BorW) {
                            if (horse < 0) {
                                cx = pos[0];
                                cy = pos[1];
                                FLAG = TRUE;
                            }else{
                                Toast.makeText(getApplicationContext(), "Now White Turn", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                                if (horse > 0) {
                                    cx = pos[0];
                                    cy = pos[1];
                                    FLAG = TRUE;
                                }else{
                                    Toast.makeText(getApplicationContext(), "Now Black Turn", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    else{
                        Toast.makeText(getApplicationContext(), "Click Horse", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        Button surrender = (Button) findViewById(R.id.Surrender);
        surrender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chess.Surrender = TRUE;
                if (chess.BorW) {
                    Toast.makeText(getApplicationContext(), "White Surrendered", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), "Black Surrendered", Toast.LENGTH_SHORT).show();
                }
                imageAdapter.Init();
            }
        });
    }

    private int[] getPos(int position){
        int temp;
        int[] tPos = new int[2];
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++) {
                temp = i + (j * 8);
                if (temp == position){
                    tPos[0] = i;
                    tPos[1] = j;
                    return tPos;
                }
            }
        }
        return null;
    }
    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        ImageView imageView;

        private ImageAdapter(Context c){
            mContext = c;
        }

        public int getCount(){
            return mThumbIds.length;
        }

        public Object getItem(int position){
            switch (mThumbIds[position]){
                case R.drawable.rook_black:
                    return 4;
                case R.drawable.knight_black:
                    return 2;
                case R.drawable.bishop_black:
                    return 3;
                case R.drawable.queen_black:
                    return 5;
                case R.drawable.king_black:
                    return 6;
                case R.drawable.pawn_black:
                    return 1;
                case R.drawable.rook_white:
                    return -4;
                case R.drawable.knight_white:
                    return -2;
                case R.drawable.bishop_white:
                    return -3;
                case R.drawable.queen_white:
                    return -5;
                case R.drawable.king_white:
                    return -6;
                case R.drawable.pawn_white:
                    return -1;
            }
            return 0;
        }

        public long getItemId(int position){
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent){
            if(convertView == null){
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(gridview.getWidth()/8, gridview.getWidth()/8));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

            }else{
                imageView = (ImageView) convertView;
            }
            imageView.setImageResource(mThumbIds[position]);
            return imageView;
        }

        private void setmThumbIds(int position, int horse){
            int tHorse = 0;
            switch (horse){
                case 1:
                    tHorse = R.drawable.pawn_black;
                    break;
                case 2:
                    tHorse = R.drawable.knight_black;
                    break;
                case 3:
                    tHorse = R.drawable.bishop_black;
                    break;
                case 4:
                    tHorse = R.drawable.rook_black;
                    break;
                case 5:
                    tHorse = R.drawable.queen_black;
                    break;
                case 6:
                    tHorse = R.drawable.king_black;
                    break;
                case -1:
                    tHorse = R.drawable.pawn_white;
                    break;
                case -2:
                    tHorse = R.drawable.knight_white;
                    break;
                case -3:
                    tHorse = R.drawable.bishop_white;
                    break;
                case -4:
                    tHorse = R.drawable.rook_white;
                    break;
                case -5:
                    tHorse = R.drawable.queen_white;
                    break;
                case -6:
                    tHorse = R.drawable.king_white;
                    break;
            }
            mThumbIds[position] = tHorse;
        }

        private void Init(){
            setmThumbIds(0, 4);
            setmThumbIds(1, 2);
            setmThumbIds(2, 3);
            setmThumbIds(3, 5);
            setmThumbIds(4, 6);
            setmThumbIds(5, 3);
            setmThumbIds(6, 2);
            setmThumbIds(7, 4);
            for(int i = 8; i <= 15; i++){
                setmThumbIds(i, 1);
            }
            for(int i = 16; i <= 47; i++){
                setmThumbIds(i, 0);
            }
            for(int i = 48; i <= 55; i++){
                setmThumbIds(i, -1);
            }
            setmThumbIds(56, -4);
            setmThumbIds(57, -2);
            setmThumbIds(58, -3);
            setmThumbIds(59, -5);
            setmThumbIds(60, -6);
            setmThumbIds(61, -3);
            setmThumbIds(62, -2);
            setmThumbIds(63, -4);
            for(int i = 0; i < 8; i++){
                for(int j = 0; j < 8; j++){
                    chessBoard[i][j] = (int)imageAdapter.getItem(i + (j*8));
                }
            }
            imageAdapter.notifyDataSetChanged();
            turnView.setText("White Turn");
            chess.BorW = TRUE;
            chess.Surrender = FALSE;
            chess.canMove = FALSE;
        }

        private Integer[] mThumbIds = {
                R.drawable.rook_black, R.drawable.knight_black,
                R.drawable.bishop_black, R.drawable.queen_black,
                R.drawable.king_black, R.drawable.bishop_black,
                R.drawable.knight_black, R.drawable.rook_black,
                R.drawable.pawn_black, R.drawable.pawn_black,
                R.drawable.pawn_black, R.drawable.pawn_black,
                R.drawable.pawn_black, R.drawable.pawn_black,
                R.drawable.pawn_black, R.drawable.pawn_black,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                R.drawable.pawn_white, R.drawable.pawn_white,
                R.drawable.pawn_white, R.drawable.pawn_white,
                R.drawable.pawn_white, R.drawable.pawn_white,
                R.drawable.pawn_white, R.drawable.pawn_white,
                R.drawable.rook_white, R.drawable.knight_white,
                R.drawable.bishop_white, R.drawable.queen_white,
                R.drawable.king_white, R.drawable.bishop_white,
                R.drawable.knight_white, R.drawable.rook_white,
        };
    }
    class Chess{
        //Turn
        boolean BorW = TRUE;
        //Surrender
        boolean Surrender = FALSE;
        boolean canMove = false;

        private void chessRun(){
            boolean KingSlainedTrigger[]={true,true};
            //get data through bluetooth
            MoveUnit();
            if(canMove) {
                if (BorW) {
                    //White Turn -> Black Turn
                    turnView.setText("Black Turn");
                    BorW = FALSE;
                } else {
                    //Black Turn -> White Turn
                    turnView.setText("White Turn");
                    BorW = TRUE;
                }
            }
            if(CheckmateB()){
                Toast.makeText(getApplicationContext(),"Check(White)",Toast.LENGTH_SHORT).show();
            }
            if(CheckmateW()){
                Toast.makeText(getApplicationContext(),"Check(Black)",Toast.LENGTH_SHORT).show();
            }
            if(Surrender) {
                if (BorW) {
                    Toast.makeText(getApplicationContext(), "White Surrendered", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), "Black Surrendered", Toast.LENGTH_SHORT).show();
                }
                imageAdapter.Init();
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
                Toast.makeText(getApplicationContext(),"White King Slained",Toast.LENGTH_SHORT).show();
            }else {
                KingSlainedTrigger[1] = true;
            }
        }

        private void MoveUnit(){
            canMove = false;
            //cx cy : Current x,y / hx hy : Heading x,y
            if(chessBoard[cx][cy]>0 || chessBoard[cx][cy]<0){//unit is there
                int type = chessBoard[cx][cy];

                //BlackPawn
                if(type == 1 && chessBoard[hx][hy]<=0){
                    if(hx == cx +1 && hy == cy+1 && chessBoard[hx][hy]<0){
                        canMove=true;
                    }
                    if(hx == cx -1 && hy == cy+1 && chessBoard[hx][hy]<0 && !canMove){
                        canMove=true;
                    }
                    if(hx == cx && hy == cy+1 && chessBoard[hx][hy]==0 && !canMove){
                        canMove=true;
                    }
                    if(cy==1 && cx == hx && hy == 3 && !canMove){
                        canMove=true;
                    }
                }
                //WhitePawn
                if(type == -1 && chessBoard[hx][hy]>=0){
                    if(hx == cx +1 && hy == cy-1 && chessBoard[hx][hy]>0){
                        canMove=true;
                    }
                    if(hx == cx -1 && hy == cy-1 && chessBoard[hx][hy]>0 && !canMove){
                        canMove=true;
                    }
                    if(hx == cx && hy == cy-1 && chessBoard[hx][hy]==0 && !canMove){
                        canMove=true;
                    }
                    if(cy==6 && cx == hx && hy == 4 && !canMove){
                        canMove=true;
                    }
                }

                //BlackKnight
                if(type == 2 && chessBoard[hx][hy]<=0){
                    if(hx == cx +2 && hy == cy+1){
                        canMove=true;
                    }
                    if(hx == cx +2 && hy == cy-1 && !canMove){
                        canMove=true;
                    }

                    if(hx == cx -2 && hy == cy+1 && !canMove){
                        canMove=true;
                    }
                    if(hx == cx -2 && hy == cy-1 && !canMove){
                        canMove=true;
                    }

                    if(hx == cx +1 && hy == cy +2 && !canMove){
                        canMove=true;
                    }
                    if(hx == cx -1 && hy == cy +2 && !canMove){
                        canMove=true;
                    }

                    if(hx == cx +1 && hy == cy -2 && !canMove){
                        canMove=true;
                    }
                    if(hx == cx -1 && hy == cy -2 && !canMove){
                        canMove=true;
                    }
                }
                //WhiteKnight
                if(type == -2 && chessBoard[hx][hy]>=0){
                    if(hx == cx +2 && hy == cy+1){
                        canMove=true;
                    }
                    if(hx == cx +2 && hy == cy-1 && !canMove){
                        canMove=true;
                    }

                    if(hx == cx -2 && hy == cy+1 && !canMove){
                        canMove=true;
                    }
                    if(hx == cx -2 && hy == cy-1 && !canMove){
                        canMove=true;
                    }

                    if(hx == cx +1 && hy == cy +2 && !canMove){
                        canMove=true;
                    }
                    if(hx == cx -1 && hy == cy +2 && !canMove){
                        canMove=true;
                    }
                    if(hx == cx +1 && hy == cy -2 && !canMove){
                        canMove=true;
                    }
                    if(hx == cx -1 && hy == cy -2 && !canMove){
                        canMove=true;
                    }
                }

                //BlackBishop
                if(type == 3 && chessBoard[hx][hy]<=0){
                    int tempX,tempY;

                    tempX=cx-1;
                    tempY=cy-1;
                    while(tempX >= 0 && tempY >= 0 && chessBoard[tempX][tempY] == 0){
                        if(tempX == hx && tempY == hy ){
                            canMove=true;
                            break;
                        }
                        tempX--;
                        tempY--;
                    }
                    if(tempX == hx && tempY == hy){
                        canMove=true;
                    }

                    tempX=cx-1;
                    tempY=cy+1;
                    while(tempX >= 0 && tempY < 8 && chessBoard[tempX][tempY] == 0 && !canMove){
                        if(tempX == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempX--;
                        tempY++;
                    }
                    if(tempX == hx && tempY == hy){
                        canMove=true;
                    }

                    tempX=cx+1;
                    tempY=cy-1;
                    while(tempX < 8 && tempY >= 0 && chessBoard[tempX][tempY] == 0 && !canMove){
                        if(tempX == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempX++;
                        tempY--;
                    }
                    if(tempX == hx && tempY == hy){
                        canMove=true;
                    }

                    tempX=cx+1;
                    tempY=cy+1;
                    while(tempX < 8 && tempY < 8 && chessBoard[tempX][tempY] == 0 && !canMove){
                        if(tempX == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempX++;
                        tempY++;
                    }
                    if(tempX == hx && tempY == hy){
                        canMove=true;
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
                    if(tempX == hx && tempY == hy){
                        canMove=true;
                    }

                    tempX=cx-1;
                    tempY=cy+1;
                    while(tempX >= 0 && tempY < 8 && chessBoard[tempX][tempY] == 0 && !canMove){
                        if(tempX == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempX--;
                        tempY++;
                    }
                    if(tempX == hx && tempY == hy){
                        canMove=true;
                    }

                    tempX=cx+1;
                    tempY=cy-1;
                    while(tempX < 8 && tempY >= 0 && chessBoard[tempX][tempY] == 0 && !canMove){
                        if(tempX == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempX++;
                        tempY--;
                    }
                    if(tempX == hx && tempY == hy){
                        canMove=true;
                    }

                    tempX=cx+1;
                    tempY=cy+1;
                    while(tempX < 8 && tempY < 8 && chessBoard[tempX][tempY] == 0 && !canMove){
                        if(tempX == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempX++;
                        tempY++;
                    }
                    if(tempX == hx && tempY == hy){
                        canMove=true;
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
                    if(tempX == hx && cy == hy){
                        canMove=true;
                    }

                    tempX=cx+1;
                    while(tempX < 8 && chessBoard[tempX][cy] == 0 && canMove == false){
                        if(tempX == hx && cy == hy){
                            canMove=true;
                            break;
                        }
                        tempX++;
                    }
                    if(tempX == hx && cy == hy){
                        canMove=true;
                    }

                    tempY=cy-1;
                    while(tempY >= 0 && chessBoard[cx][tempY] == 0 && canMove == false){
                        if(cx == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempY--;
                    }
                    if(cx == hx && tempY == hy){
                        canMove=true;
                    }

                    tempY=cy+1;
                    while(tempY < 8 && chessBoard[cx][tempY] == 0 && canMove == false){
                        if(cx == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempY++;
                    }
                    if(cx == hx && tempY == hy){
                        canMove=true;
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
                    if(tempX == hx && cy == hy){
                        canMove=true;
                    }

                    tempX=cx+1;
                    while(tempX < 8 && chessBoard[tempX][cy] == 0 && !canMove){
                        if(tempX == hx && cy == hy){
                            canMove=true;
                            break;
                        }
                        tempX++;
                    }
                    if(tempX == hx && cy == hy){
                        canMove=true;
                    }

                    tempY=cy-1;
                    while(tempY >= 0 && chessBoard[cx][tempY] == 0 && !canMove){
                        if(cx == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempY--;
                    }
                    if(cx == hx && tempY == hy){
                        canMove=true;
                    }

                    tempY=cy+1;
                    while(tempY < 8 && chessBoard[cx][tempY] == 0 && !canMove){
                        if(cx == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempY++;
                    }
                    if(cx == hx && tempY == hy){
                        canMove=true;
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
                    if(tempX == hx && cy == hy){
                        canMove=true;
                    }

                    tempX=cx+1;
                    while(tempX < 8 && chessBoard[tempX][hy] == 0 && !canMove){
                        if(tempX == hx){
                            canMove=true;
                            break;
                        }
                        tempX++;
                    }
                    if(tempX == hx){
                        canMove=true;
                    }

                    tempY=cy-1;
                    while(tempY >= 0 && chessBoard[cx][tempY] == 0 && !canMove){
                        if(cx == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempY--;
                    }
                    if(cx == hx && tempY == hy){
                        canMove=true;
                    }

                    tempY=cy+1;
                    while(tempY < 8 && chessBoard[cx][tempY] == 0 && !canMove){
                        if(cx == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempY++;
                    }
                    if(cx == hx && tempY == hy){
                        canMove=true;
                    }

                    tempX=cx-1;
                    tempY=cy-1;
                    while(tempX >= 0 && tempY >= 0 && chessBoard[tempX][tempY] == 0){
                        if(tempX == hx && tempY == hy ){
                            canMove=true;
                            break;
                        }
                        tempX--;
                        tempY--;
                    }
                    if(tempX == hx && tempY == hy){
                        canMove=true;
                    }

                    tempX=cx-1;
                    tempY=cy+1;
                    while(tempX >= 0 && tempY < 8 && chessBoard[tempX][tempY] == 0 && !canMove){
                        if(tempX == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempX--;
                        tempY++;
                    }
                    if(tempX == hx && tempY == hy){
                        canMove=true;
                    }

                    tempX=cx+1;
                    tempY=cy-1;
                    while(tempX < 8 && tempY >= 0 && chessBoard[tempX][tempY] == 0 && !canMove){
                        if(tempX == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempX++;
                        tempY--;
                    }
                    if(tempX == hx && tempY == hy){
                        canMove=true;
                    }

                    tempX=cx+1;
                    tempY=cy+1;
                    while(tempX < 8 && tempY < 8 && chessBoard[tempX][tempY] == 0 && !canMove){
                        if(tempX == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempX++;
                        tempY++;
                    }
                    if(tempX == hx && tempY == hy){
                        canMove=true;
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
                    if(tempX == hx && cy == hy){
                        canMove=true;
                    }

                    tempX=cx+1;
                    while(tempX < 8 && chessBoard[tempX][cy] == 0 && !canMove){
                        if(tempX == hx && cy == hy){
                            canMove=true;
                            break;
                        }
                        tempX++;
                    }
                    if(tempX == hx && cy == hy){
                        canMove=true;
                    }

                    tempY=cy-1;
                    while(tempY >= 0 && chessBoard[cx][tempY] == 0 && !canMove){
                        if(cx == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempY--;
                    }
                    if(cx == hx && tempY == hy){
                        canMove=true;
                    }

                    tempY=cy+1;
                    while(tempY < 8 && chessBoard[cx][tempY] == 0 && !canMove){
                        if(cx == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempY++;
                    }
                    if(cx == hx && tempY == hy){
                        canMove=true;
                    }
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
                    if(tempX == hx && tempY == hy){
                        canMove=true;
                    }

                    tempX=cx-1;
                    tempY=cy+1;
                    while(tempX >= 0 && tempY < 8 && chessBoard[tempX][tempY] == 0 && !canMove){
                        if(tempX == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempX--;
                        tempY++;
                    }
                    if(tempX == hx && tempY == hy){
                        canMove=true;
                    }

                    tempX=cx+1;
                    tempY=cy-1;
                    while(tempX < 8 && tempY >= 0 && chessBoard[tempX][tempY] == 0 && !canMove){
                        if(tempX == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempX++;
                        tempY--;
                    }
                    if(tempX == hx && tempY == hy){
                        canMove=true;
                    }

                    tempX=cx+1;
                    tempY=cy+1;
                    while(tempX < 8 && tempY < 8 && chessBoard[tempX][tempY] == 0 && !canMove){
                        if(tempX == hx && tempY == hy){
                            canMove=true;
                            break;
                        }
                        tempX++;
                        tempY++;
                    }
                    if(tempX == hx && tempY == hy){
                        canMove=true;
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
                            if(tempX == hx && tempY == hy && chessBoard[tempX][tempY]!=-6){
                                canMove=true;
                                break;
                            }
                        }
                    }
                }
                if(canMove){
                    if(chessBoard[hx][hy] != 0){
                        Toast.makeText(getApplicationContext(),"You scored something",Toast.LENGTH_SHORT).show();
                    }
                    if(type == 1 && hy == 7){
                        //promote
                        switch(promotion){
                            case 1:{
                                //Queen
                                type = 5;
                            }
                            case 2:{
                                //Knight
                                type = 2;
                            }
                            case 3:{
                                //Bishop
                                type = 3;
                            }
                            case 4:{
                                //Rook
                                type = 4;
                            }
                        }
                    }
                    if(type == -1 && hy == 0){
                        //promote
                        switch(promotion){
                            case 1:{
                                //Queen
                                type = -5;
                            }
                            case 2:{
                                //Knight
                                type = -2;
                            }
                            case 3:{
                                //Bishop
                                type = -3;
                            }
                            case 4:{
                                //Rook
                                type = -4;
                            }
                        }
                    }
                    chessBoard[hx][hy]=type;
                    chessBoard[cx][cy]=0;
                    imageAdapter.setmThumbIds(cx + (cy * 8), 0);
                    imageAdapter.setmThumbIds(hx + (hy * 8), type);
                    imageAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(getApplicationContext(),"You can't Move to There",Toast.LENGTH_SHORT).show();
                }
            }
        }

        private boolean CheckmateB(){
            boolean Checkmate=false;
            int Unit[] = {0,1,2,3,4,5,6};
            int Temp1,Temp2;
            boolean Temp;
            int TempX,TempY;
            int KingLocationX=0,KingLocationY=0;

            for(TempX=1;TempX<7;TempX++){
                Unit[TempX]=Unit[TempX]*-1;
            }

            //find King's Location
            for(TempX=0;TempX<8;TempX++){
                for(TempY=0;TempY<8;TempY++){
                    if(chessBoard[TempX][TempY]==(Unit[6]*-1)){
                        KingLocationY=TempY;
                        break;
                    }
                }
                if(TempY == 8) TempY--;
                if(chessBoard[TempX][TempY]==(Unit[6]*-1)){
                    KingLocationX=TempX;
                    break;
                }
            }
            Temp=false;
            //Rook or Queen can Attack
            //X axis
            for(TempX=KingLocationX-1;TempX>=0;TempX--){
                if(chessBoard[TempX][KingLocationY]==Unit[4] || chessBoard[TempX][KingLocationY]==Unit[5]) {
                    for(Temp1 = TempX+1;Temp1<=KingLocationX;Temp1++){
                        if(chessBoard[Temp1][KingLocationY] == 0 || chessBoard[Temp1][KingLocationY] == (Unit[6]*-1)){
                            Temp = true;
                        }else{
                            Temp = false;
                            break;
                        }
                    }
                    if(Temp){
                        Checkmate = true;
                        break;
                    }
                }
            }
            Temp=false;
            for(TempX=KingLocationX+1;TempX<8;TempX++){
                if(chessBoard[TempX][KingLocationY]==Unit[4] || chessBoard[TempX][KingLocationY]==Unit[5]) {
                    for(Temp1 = TempX-1;Temp1>=KingLocationX;Temp1--){
                        if(chessBoard[Temp1][KingLocationY] == 0 || chessBoard[Temp1][KingLocationY] == (Unit[6]*-1)){
                            Temp = true;
                        }else{
                            Temp = false;
                            break;
                        }
                    }
                    if(Temp){
                        Checkmate = true;
                        break;
                    }
                }
            }
            Temp=false;
            //Y axis
            for(TempY=KingLocationY-1;TempY>=0;TempY--){
                if(chessBoard[KingLocationX][TempY]==Unit[4] || chessBoard[KingLocationX][TempY]==Unit[5]) {
                    for(Temp1 = TempY+1;Temp1<=KingLocationY;Temp1++){
                        if(chessBoard[KingLocationX][Temp1] == 0 || chessBoard[KingLocationX][Temp1] == (Unit[6]*-1)){
                            Temp = true;
                        }else{
                            Temp = false;
                            break;
                        }
                    }
                    if(Temp){
                        Checkmate = true;
                        break;
                    }
                }
            }
            Temp=false;
            for(TempY=KingLocationY+1;TempY<8;TempY++){
                if(chessBoard[KingLocationX][TempY]==Unit[4] || chessBoard[KingLocationX][TempY]==Unit[5]) {
                    for(Temp1 = TempY-1;Temp1>=KingLocationY;Temp1--){
                        if(chessBoard[KingLocationX][Temp1] == 0 || chessBoard[KingLocationX][Temp1] == (Unit[6]*-1)){
                            Temp = true;
                        }else{
                            Temp = false;
                            break;
                        }
                    }
                    if(Temp){
                        Checkmate = true;
                        break;
                    }
                }
            }
            Temp=false;
            //Bishop or Queen can Attack
            // Diagonals
            TempX=KingLocationX;
            TempY=KingLocationY;
            while(TempX >= 0 && TempY >= 0){
                if(chessBoard[TempX][TempY] == Unit[3] || chessBoard[TempX][TempY] == Unit[5]){
                    Temp1=TempX+1;
                    Temp2=TempY+1;
                    while(Temp1 <= KingLocationX && Temp2 <= KingLocationY){
                        if (chessBoard[Temp1][Temp2] == 0 || chessBoard[Temp1][Temp2] == (Unit[6]*-1)) {
                            Temp=true;
                        } else {
                            //뭔가에 막힌거임.
                            Temp=false;
                            break;
                        }
                        Temp1++;
                        Temp2++;
                    }
                    if(Temp){
                        Checkmate = true;
                        break;
                    }
                }
                TempX--;
                TempY--;
            }


            TempX=KingLocationX;
            TempY=KingLocationY;
            while(TempX >= 0 && TempY < 8){
                if(chessBoard[TempX][TempY] == Unit[3] || chessBoard[TempX][TempY] == Unit[5]) {
                    Temp1=TempX+1;
                    Temp2=TempY-1;
                    while (Temp1 <= KingLocationX && Temp2 >= KingLocationY) {
                        if (chessBoard[Temp1][Temp2] == 0 || chessBoard[Temp1][Temp2] == (Unit[6]*-1)){
                            Temp = true;
                        } else {
                            //뭔가에 막힌거임.
                            Temp = false;
                            break;
                        }
                        Temp1++;
                        Temp2--;
                    }
                    if (Temp) {
                        Checkmate = true;
                        break;
                    }
                }
                TempX--;
                TempY++;
            }
            TempX=KingLocationX;
            TempY=KingLocationY;
            while(TempX < 8 && TempY >= 0){
                if(chessBoard[TempX][TempY] == Unit[3] || chessBoard[TempX][TempY] == Unit[5]){
                    Temp1=TempX-1;
                    Temp2=TempY+1;
                    while (Temp1 >= KingLocationX && Temp2 <= KingLocationY) {
                        if (chessBoard[Temp1][Temp2] == 0 || chessBoard[Temp1][Temp2] == (Unit[6]*-1)){
                            Temp = true;
                        } else {
                            //뭔가에 막힌거임.
                            Temp = false;
                            break;
                        }
                        Temp1--;
                        Temp2++;
                    }
                    if (Temp) {
                        Checkmate = true;
                        break;
                    }
                }
                TempX++;
                TempY--;
            }
            TempX=KingLocationX;
            TempY=KingLocationY;
            while(TempX < 8 && TempY < 8){
                if(chessBoard[TempX][TempY] == Unit[3] || chessBoard[TempX][TempY] == Unit[5]){
                    Temp1=TempX-1;
                    Temp2=TempY-1;
                    while (Temp1 >= KingLocationX && Temp2 >= KingLocationY) {
                        if (chessBoard[Temp1][Temp2] == 0 || chessBoard[Temp1][Temp2] == (Unit[6]*-1)){
                            Temp = true;
                        } else {
                            //뭔가에 막힌거임.
                            Temp = false;
                            break;
                        }
                        Temp1--;
                        Temp2--;
                    }
                    if (Temp) {
                        Checkmate = true;
                        break;
                    }
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
            //Pawn
            if(KingLocationX-1 < 8 && KingLocationX-1 >= 0 && KingLocationY-1 < 8 && KingLocationY-1>=0) {
                if(chessBoard[KingLocationX-1][KingLocationY-1] == Unit[1]) {
                    Checkmate = true;
                }
            }
            if(KingLocationX+1 < 8 && KingLocationX+1 >= 0 && KingLocationY-1 < 8 && KingLocationY-1>=0) {
                if(chessBoard[KingLocationX+1][KingLocationY-1] == Unit[1]) {
                    Checkmate = true;
                }
            }

            //King
            for(TempX=KingLocationX-1;TempX<=KingLocationX+1;TempX++){
                for(TempY=KingLocationY-1;TempY<=KingLocationY+1;TempY++){
                    if(TempX < 8 && TempX >= 0 && TempY < 8 && TempY>=0) {
                        if (chessBoard[TempX][TempY] == Unit[6]) {
                            Checkmate = true;
                        }
                    }
                }
            }
            return Checkmate;
        }
        private boolean CheckmateW(){
            boolean Checkmate=false;
            int Unit[] = {0,1,2,3,4,5,6};
            int Temp1,Temp2;
            boolean Temp;
            int TempX,TempY;
            int KingLocationX=0,KingLocationY=0;

            //find King's Location
            for(TempX=0;TempX<8;TempX++){
                for(TempY=0;TempY<8;TempY++){
                    if(chessBoard[TempX][TempY]==(-6)){
                        KingLocationY=TempY;
                        break;
                    }
                }
                if(TempY == 8) TempY--;
                if(chessBoard[TempX][TempY]==(-6)){
                    KingLocationX=TempX;
                    break;
                }
            }
            Temp=false;
            //Rook or Queen can Attack
            //X axis
            for(TempX=KingLocationX-1;TempX>=0;TempX--){
                if(chessBoard[TempX][KingLocationY]==Unit[4] || chessBoard[TempX][KingLocationY]==Unit[5]) {
                    for(Temp1 = TempX+1;Temp1<=KingLocationX;Temp1++){
                        if(chessBoard[Temp1][KingLocationY] == 0 || chessBoard[Temp1][KingLocationY] == (Unit[6]*-1)){
                            Temp = true;
                        }else{
                            Temp = false;
                            break;
                        }
                    }
                    if(Temp){
                        Checkmate = true;
                        break;
                    }
                }
            }
            Temp=false;
            for(TempX=KingLocationX+1;TempX<8;TempX++){
                if(chessBoard[TempX][KingLocationY]==Unit[4] || chessBoard[TempX][KingLocationY]==Unit[5]) {
                    for(Temp1 = TempX-1;Temp1>=KingLocationX;Temp1--){
                        if(chessBoard[Temp1][KingLocationY] == 0 || chessBoard[Temp1][KingLocationY] == (Unit[6]*-1)){
                            Temp = true;
                        }else{
                            Temp = false;
                            break;
                        }
                    }
                    if(Temp){
                        Checkmate = true;
                        break;
                    }
                }
            }
            Temp=false;
            //Y axis
            for(TempY=KingLocationY-1;TempY>=0;TempY--){
                if(chessBoard[KingLocationX][TempY]==Unit[4] || chessBoard[KingLocationX][TempY]==Unit[5]) {
                    for(Temp1 = TempY+1;Temp1<=KingLocationY;Temp1++){
                        if(chessBoard[KingLocationX][Temp1] == 0 || chessBoard[KingLocationX][Temp1] == (Unit[6]*-1)){
                            Temp = true;
                        }else{
                            Temp = false;
                            break;
                        }
                    }
                    if(Temp){
                        Checkmate = true;
                        break;
                    }
                }
            }
            Temp=false;
            for(TempY=KingLocationY+1;TempY<8;TempY++){
                if(chessBoard[KingLocationX][TempY]==Unit[4] || chessBoard[KingLocationX][TempY]==Unit[5]) {
                    for(Temp1 = TempY-1;Temp1>=KingLocationY;Temp1--){
                        if(chessBoard[KingLocationX][Temp1] == 0 || chessBoard[KingLocationX][Temp1] == (Unit[6]*-1)){
                            Temp = true;
                        }else{
                            Temp = false;
                            break;
                        }
                    }
                    if(Temp){
                        Checkmate = true;
                        break;
                    }
                }
            }
            Temp=false;
            //Bishop or Queen can Attack
            // Diagonals
            TempX=KingLocationX;
            TempY=KingLocationY;
            while(TempX >= 0 && TempY >= 0){
                if(chessBoard[TempX][TempY] == Unit[3] || chessBoard[TempX][TempY] == Unit[5]){
                    Temp1=TempX+1;
                    Temp2=TempY+1;
                    while(Temp1 <= KingLocationX && Temp2 <= KingLocationY){
                        if (chessBoard[Temp1][Temp2] == 0 || chessBoard[Temp1][Temp2] == (Unit[6]*-1)) {
                            Temp=true;
                        } else {
                            //뭔가에 막힌거임.
                            Temp=false;
                            break;
                        }
                        Temp1++;
                        Temp2++;
                    }
                    if(Temp){
                        Checkmate = true;
                        break;
                    }
                }
                TempX--;
                TempY--;
            }


            TempX=KingLocationX;
            TempY=KingLocationY;
            while(TempX >= 0 && TempY < 8){
                if(chessBoard[TempX][TempY] == Unit[3] || chessBoard[TempX][TempY] == Unit[5]) {
                    Temp1=TempX+1;
                    Temp2=TempY-1;
                    while (Temp1 <= KingLocationX && Temp2 >= KingLocationY) {
                        if (chessBoard[Temp1][Temp2] == 0 || chessBoard[Temp1][Temp2] == (Unit[6]*-1)){
                            Temp = true;
                        } else {
                            //뭔가에 막힌거임.
                            Temp = false;
                            break;
                        }
                        Temp1++;
                        Temp2--;
                    }
                    if (Temp) {
                        Checkmate = true;
                        break;
                    }
                }
                TempX--;
                TempY++;
            }
            TempX=KingLocationX;
            TempY=KingLocationY;
            while(TempX < 8 && TempY >= 0){
                if(chessBoard[TempX][TempY] == Unit[3] || chessBoard[TempX][TempY] == Unit[5]){
                    Temp1=TempX-1;
                    Temp2=TempY+1;
                    while (Temp1 >= KingLocationX && Temp2 <= KingLocationY) {
                        if (chessBoard[Temp1][Temp2] == 0 || chessBoard[Temp1][Temp2] == (Unit[6]*-1)){
                            Temp = true;
                        } else {
                            //뭔가에 막힌거임.
                            Temp = false;
                            break;
                        }
                        Temp1--;
                        Temp2++;
                    }
                    if (Temp) {
                        Checkmate = true;
                        break;
                    }
                }
                TempX++;
                TempY--;
            }
            TempX=KingLocationX;
            TempY=KingLocationY;
            while(TempX < 8 && TempY < 8){
                if(chessBoard[TempX][TempY] == Unit[3] || chessBoard[TempX][TempY] == Unit[5]){
                    Temp1=TempX-1;
                    Temp2=TempY-1;
                    while (Temp1 >= KingLocationX && Temp2 >= KingLocationY) {
                        if (chessBoard[Temp1][Temp2] == 0 || chessBoard[Temp1][Temp2] == (Unit[6]*-1)){
                            Temp = true;
                        } else {
                            //뭔가에 막힌거임.
                            Temp = false;
                            break;
                        }
                        Temp1--;
                        Temp2--;
                    }
                    if (Temp) {
                        Checkmate = true;
                        break;
                    }
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
            //Pawn
            if(KingLocationX-1 < 8 && KingLocationX-1 >= 0 && KingLocationY-1 < 8 && KingLocationY-1>=0) {
                if(chessBoard[KingLocationX-1][KingLocationY-1] == Unit[1]) {
                    Checkmate = true;
                }
            }
            if(KingLocationX+1 < 8 && KingLocationX+1 >= 0 && KingLocationY-1 < 8 && KingLocationY-1>=0) {
                if(chessBoard[KingLocationX+1][KingLocationY-1] == Unit[1]) {
                    Checkmate = true;
                }
            }

            //King
            for(TempX=KingLocationX-1;TempX<=KingLocationX+1;TempX++){
                for(TempY=KingLocationY-1;TempY<=KingLocationY+1;TempY++){
                    if(TempX < 8 && TempX >= 0 && TempY < 8 && TempY>=0) {
                        if (chessBoard[TempX][TempY] == Unit[6]) {
                            Checkmate = true;
                        }
                    }
                }
            }
            return Checkmate;
        }
    }
}