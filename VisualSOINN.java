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

	//2015/5/24	ver 1.0
	//soinnのセッターとゲッターを作成した。

	//2015/5/29	ver 1.1
	//逐次入力がVisualSOINN内で行えるようにした。
	//逐次入力させたい場合は、あらかじめVisualSOINNに、setDataset(dataset)を用いてデータセットを与えたうえで、setRealtimeFlag(true)とすることでフラグを立てればOK
	//SOINNに代入するデータセットの順序をランダムにしたい場合はsetRandomDataFlag(true)でOK

	//問題点
	//・データ入力時ではなく、描画時にデータの可視性を上げる必要がある	←解決
	//・現在定数として与えている平行移動倍率や拡大倍率は実際はデータによって適切な値に設定されるべきである。	←解決


	//画面端の位置(定数)
	final int FRAMESIDE = 50;
	final int NODESIZE = 10;


	//描画時の平行移動量と倍率
	int MOVE_X;
	int MOVE_Y;
	int SCALE;


	//描画するSOINN
	SOINN soinn = null;

	//SOINNに代入するデータセット。基本的にデータ入力済みのSOINNを引数に与えることを想定しているが、入力ごとの描画が見たいときにのみ使用する。
	ArrayList<double[]> dataset = null;

	//逐次描画を行う場合のフラグ
	boolean realtimeflag = false;

	//逐次描画を行う際、データをランダムで選択する場合のフラグ
	boolean randomdataflag = false;

	Color color = Color.white;


	//バッファストラテジ
	BufferStrategy buffer;

	//入力データをそのまま描画する用のフレーム。試作中
	FormalDataFrame formaldataframe = null;

	//コンストラクタ
	//SOINN		soinn		:描画するSOINNクラス。Nodeの第1引数と第2引数の2次元で描画する。
	//String	framename	:フレーム名
	//int		move_x		:描画位置の補正の際のX方向の変化率
	//int		move_y		:描画位置の補正の際のY方向の変化率
	//int		scale		:描画域（倍率）の初期値。この値から逐次減少させていく
	public VisualSOINN(SOINN soinn , String framename , int move_x , int move_y , int scale){
		super(framename);
		this.soinn = soinn;
		this.MOVE_X = move_x;
		this.MOVE_Y = move_y;
		this.SCALE = scale;
		this.playflag = true;
	}

	//簡単版コンストラクタ
	//特別な場合を除き、こちらを使用する
	public VisualSOINN(SOINN soinn){
		this(soinn,"VisualSOINN",40,40,2000);
	}

	//入力データを描画したいときに使用するコンストラクタ。試作中
	public VisualSOINN(SOINN soinn,ArrayList<double[]> dataset){
		this(soinn);
		this.formaldataframe = new FormalDataFrame("FormalData",dataset);
	}

	//soinnのセッター、ゲッター
	public void setSOINN(SOINN soinn){
		this.soinn = soinn;
	}
	public SOINN getSOINN(){
		return this.soinn;
	}

	//逐次入力用データセットのセッターとゲッター
	public void setDataset(ArrayList<double[]> dataset){
		this.dataset = dataset;
	}
	public ArrayList<double[]> getDataset(){
		return this.dataset;
	}

	//逐次描画のフラグのセッターとゲッター
	public void setRealtimeFlag(boolean realtimeflag){
		this.realtimeflag = realtimeflag;
	}
	public boolean getRealtimeFlag(){
		return this.realtimeflag;
	}
	//データ乱択のフラグのセッターとゲッター
	public void setRandomDataFlag(boolean randomdataflag){
		this.randomdataflag = randomdataflag;
	}
	public boolean getRandomDataFlag(){
		return this.randomdataflag;
	}
	@Override
	public void run() {
		// TODO 自動生成されたメソッド・スタブ

		//逐次描画のフラグが立っている場合、datasetがnullでない場合は一つSOINNに入力して破棄
		if(this.realtimeflag == true && this.dataset != null && this.dataset.size() > 0){
			if(this.randomdataflag == true){
				int rand = (int)(Math.random() * this.dataset.size());
				double[] temp = this.dataset.get(rand);
				this.soinn.inputSignal(temp);
				this.dataset.remove(rand);
			}
			else{
				this.realtimeflag = false;
				double[] temp = this.dataset.get(0);
				this.soinn.inputSignal(temp);
				this.dataset.remove(0);
			}
		}
		else{
			this.soinn.removeUnnecessaryNode();
		}

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
