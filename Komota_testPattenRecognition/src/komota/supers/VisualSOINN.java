package komota.supers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import soinn.SOINN;

public class VisualSOINN extends MyFrame{

	//ver 1.0	created by TETSUYA.K

	//2015/5/14
	//SOINNをGraphics2Dを用いてJFrame上に描画するためのクラス
	//SOINNクラスを渡すだけで描画してくれる、扱いやすいものを目指す

	//2015/5/15	ver 1.0
	//煩雑だった変数を整理した。
	//現段階でそこそこ成功と言える完成度となったので、これをver 1.0とする。

	//問題点
	//・データ入力時ではなく、描画時にデータの可視性を上げる必要がある	←解決
	//・現在定数として与えている平行移動倍率や拡大倍率は実際はデータによって適切な値に設定されるべきである。	←解決


	//画面端の位置(定数)
	final int FRAMESIDE = 50;
	final int NODESIZE = 10;


	//描画時の平行移動量と倍率
	int MOVE_X = 40;
	int MOVE_Y = 40;
	int SCALE = 2000;


	//描画するSOINN
	SOINN soinn = null;

	Color color = Color.white;


	//バッファストラテジ
	BufferStrategy buffer;

	//入力データをそのまま描画する用のフレーム。試作中
	FormalDataFrame formaldataframe = null;

	//コンストラクタ
	public VisualSOINN(SOINN soinn){
		super("VisualSOINN");
		this.soinn = soinn;
		this.playflag=true;
	}

	//入力データを描画したいときに使用するコンストラクタ
	public VisualSOINN(SOINN soinn,ArrayList<double[]> dataset){
		super("VisualSOINN");
		this.soinn = soinn;
		this.playflag=true;
		this.formaldataframe = new FormalDataFrame("FormalData",dataset);
	}

	@Override
	public void run() {
		// TODO 自動生成されたメソッド・スタブ


		try{
			soinn.classify();
		}catch(Exception e){
			System.out.println("[VisualSOINN]		Something wrong with soinn.classisy in this.run.");
		}




		//パラメータ更新用のフラグ
		boolean MOVE_X_flag = false;
		boolean MOVE_Y_flag = false;
		boolean SCALE_flag = false;


		for (int i = 0; i < soinn.getEdgeNum(); i++) {

			double[] tempedge = {(soinn.getNode(soinn.getEdge(i).getFrom()).getSignal()[0])*SCALE+MOVE_X + NODESIZE/2,
					(soinn.getNode(soinn.getEdge(i).getFrom()).getSignal()[1])*SCALE+MOVE_Y + NODESIZE/2,
					(soinn.getNode(soinn.getEdge(i).getTo()).getSignal()[0])*SCALE+MOVE_X + NODESIZE/2,
					(soinn.getNode(soinn.getEdge(i).getTo()).getSignal()[1])*SCALE+MOVE_Y + NODESIZE/2};


			drawLine(tempedge[0],tempedge[1],tempedge[2],tempedge[3], Color.gray);
		}

		for (int i = 0; i < soinn.getNodeNum(false); i++) {
			switch (soinn.getNode(i).getClassID()) {
			case 0:
				color = Color.red;
				break;
			case 1:
				color = Color.blue;
				break;
			case 2:
				color = Color.green;
				break;
			case 3:
				color = Color.orange;
				break;
			case 4:
				color = Color.cyan;
				break;
			case 5:
				color = Color.pink;
				break;
			default:
				color = Color.white;
			}

			//現在のパラメータでは画面外にはみ出してしまうデータがある場合、更新フラグを立てる
			if((soinn.getNode(i).getSignal()[0])*SCALE+MOVE_X < FRAMESIDE){
				MOVE_X_flag = true;
			}
			if((soinn.getNode(i).getSignal()[1])*SCALE+MOVE_Y < FRAMESIDE){
				MOVE_Y_flag = true;
			}
			if((soinn.getNode(i).getSignal()[0])*SCALE+MOVE_X > this.getWidth() - FRAMESIDE || (soinn.getNode(i).getSignal()[1])*SCALE+MOVE_Y > this.getHeight() - FRAMESIDE){
				SCALE_flag = true;
			}



			drawDot((soinn.getNode(i).getSignal()[0])*SCALE+MOVE_X,
					(soinn.getNode(i).getSignal()[1])*SCALE+MOVE_Y,
					NODESIZE,
					color);
		}
		//描画位置を調節するパラメータを学習する
		if(MOVE_X_flag == true){
			MOVE_X += FRAMESIDE;
		}
		if(MOVE_Y_flag == true){
			MOVE_Y += FRAMESIDE;
		}
		if(SCALE_flag == true){
			SCALE -= FRAMESIDE / 10;
			MOVE_X -= FRAMESIDE;
			MOVE_Y -= FRAMESIDE;
		}

		//入力データセットが与えられている場合、それも描画する。試作中
		if(this.formaldataframe != null){
			System.out.println("とおってるよ");
			this.g.dispose();
			this.buffer.show();
			this.g = (Graphics2D)this.formaldataframe.buffer.getDrawGraphics();
			this.formaldataframe.drawClear();
			this.formaldataframe.run();
			this.formaldataframe.buffer.show();
		}
	}
}
