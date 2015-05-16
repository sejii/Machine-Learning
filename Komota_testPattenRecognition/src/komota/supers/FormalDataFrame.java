package komota.supers;

import java.awt.Color;
import java.util.ArrayList;

public class FormalDataFrame extends MyFrame{
	//描画時の平行移動量と倍率
	final int MOVE_X = 500;
	final int MOVE_Y = 500;
	final int SCALE = 200;
	final int NODESIZE = 10;

		ArrayList<double[]> dataset = new ArrayList<double[]>();

		public FormalDataFrame(String framename,ArrayList<double[]> dataset) {
			super(framename,0);
			// TODO 自動生成されたコンストラクター・スタブ
			this.playflag = true;
			this.dataset = dataset;
		}

		@Override
		public void run() {
			// TODO 自動生成されたメソッド・スタブ
			for(int i=0;i<dataset.size();i++){
				drawDot(dataset.get(i)[0]*SCALE+MOVE_X, dataset.get(i)[1]*SCALE+MOVE_Y, 10, Color.gray);
			}

		}

}
