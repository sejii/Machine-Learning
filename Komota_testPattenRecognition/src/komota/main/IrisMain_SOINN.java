package komota.main;

import java.io.IOException;
import java.util.ArrayList;

import komota.supers.MyDataFilter;
import komota.supers.MyDataInput;
import komota.supers.VisualSOINN;
import soinn.SOINN;

public class IrisMain_SOINN {

	/**
	 * @param args
	 */

	//テスト用カウンター
	int countdata = 0;
	int countfeature = 0;
	int countcurrent = 0;



	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ
		ArrayList<double[]> sampleiris = new ArrayList<double[]>();

		Gauss gauss = new Gauss();

		try {
			sampleiris = MyDataFilter.normalization((ArrayList<double[]>) MyDataInput.txtToDoubleData("iris.txt"));
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		//アヤメのデータではなくガウス分布で実験するため、データをそっくりそのままガウスにしている。アヤメデータでやる際は下の1行をコメントアウト。
		sampleiris = MyDataFilter.normalization(gauss.gauss);
//		sampleiris = gauss.gauss;

		SOINN soinn = new SOINN(2,100,50);





		/* ************************************************************************************************************* */
		//データを与えきってからVisualSOINNを生成する
/*
		for(int i=0;i<sampleiris.size();i++){
			double[] tempdata = {sampleiris.get(i)[0],sampleiris.get(i)[1]};
			soinn.inputSignal(tempdata);
		}
		soinn.removeUnnecessaryNode();
		VisualSOINN frame = new VisualSOINN(soinn);
*/
		/* ************************************************************************************************************* */

		/* ************************************************************************************************************* */
		//VisualSOINNを生成してからデータを与える。（順序通り）
/*
		VisualSOINN frame = new VisualSOINN(soinn);

		for(int i=0;i<sampleiris.size();i++){
			try {
				Thread.sleep(16);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			double[] tempdata = {sampleiris.get(i)[0],sampleiris.get(i)[1]};
			soinn.inputSignal(tempdata);
		}
		soinn.removeUnnecessaryNode();
*/


		/* ************************************************************************************************************* */

		/* ************************************************************************************************************* */
		//VisualSOINNを生成してからデータを与える。（ランダム）

 		VisualSOINN frame = new VisualSOINN(soinn);
		int countinputeddata = 0;
		int[] inputeddata = new int[sampleiris.size()];
		for(int i=0;i<inputeddata.length;i++){
			inputeddata[i] = 0;
		}
		while(countinputeddata < sampleiris.size()){

			try {
				Thread.sleep(8);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			int random = (int)(Math.random() * sampleiris.size());
			while(true){
				if(inputeddata[random] != -1){
					double[] tempdata = {sampleiris.get(random)[0],sampleiris.get(random)[1]};
					soinn.inputSignal(tempdata);
					inputeddata[random] = -1;
					countinputeddata++;
					break;
				}
				random++;
				if(random >= inputeddata.length)break;
			}
		}
		soinn.removeUnnecessaryNode();


	}


}
