package komota.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import komota.supers.MyDataInput;

public class NeuralNet {

	static final int N_INPUT = 4;
	static final int N_HIDDEN = 10;
	static final int N_OUTPUT = 3;

	double w1[][];	//入力層>隠れ層の重み
	double w2[][];	//隠れ層>出力層の重み

	double input[];
	double hidden[];
	double output[];

	double alpha;	//学習率

	public NeuralNet(){

		input  = new double[N_INPUT];
		hidden = new double[N_HIDDEN];
		output = new double[N_OUTPUT];

		//重みを[-0.1, 0.1]で初期化
		Random rnd = new Random();

		w1 = new double[N_INPUT][N_HIDDEN];
		for(int i=0; i<N_INPUT; i++){
			for(int j=0; j<N_HIDDEN; j++){
				w1[i][j] = (rnd.nextDouble()*2.0 - 1.0) * 0.1;
			}
		}

		w2 = new double[N_HIDDEN][N_OUTPUT];
		for(int i=0; i<N_HIDDEN; i++){
			for(int j=0; j<N_OUTPUT; j++){
				w2[i][j] = (rnd.nextDouble()*2.0 - 1.0) * 0.1;
			}
		}

		//学習率の初期化
		alpha = 0.2;

	}


	//メイン関数
	public static void main(String[] args){

		//アヤメデータの取得
		ArrayList<String[]> sampleiris = new ArrayList<String[]>();
		try {
			sampleiris = (ArrayList<String[]>) MyDataInput.txtToData("iris_tr.txt");
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		ArrayList<String[]> testiris = new ArrayList<String[]>();
		try {
			testiris = (ArrayList<String[]>) MyDataInput.txtToData("iris_te.txt");
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		//訓練データを作成
		double[][] input = new double[sampleiris.size()][N_INPUT];
		double[][] res = new double[sampleiris.size()][N_INPUT];

		for(int i=0;i<sampleiris.size();i++){
			for(int j=0;j<N_INPUT;j++){
				input[i][j] = Double.parseDouble(sampleiris.get(i)[j+1]);
			}
			if(sampleiris.get(i)[5].equals("setosa") == true){
				res[i][0] = 1;
				res[i][1] = 0;
				res[i][2] = 0;
			}
			else if(sampleiris.get(i)[5].equals("versicolor") == true){
				res[i][0] = 0;
				res[i][1] = 1;
				res[i][2] = 0;
			}
			else{
				res[i][0] = 0;
				res[i][1] = 0;
				res[i][2] = 1;
			}
		}
		//テストデータを作成
		double[][] testinput = new double[testiris.size()][N_INPUT];
		double[][] testres = new double[testiris.size()][N_INPUT];

		for(int i=0;i<testiris.size();i++){
			for(int j=0;j<N_INPUT;j++){
				testinput[i][j] = Double.parseDouble(testiris.get(i)[j+1]);
			}
			if(testiris.get(i)[5].equals("setosa") == true){
				testres[i][0] = 1;
				testres[i][1] = 0;
				testres[i][2] = 0;
			}
			else if(testiris.get(i)[5].equals("versicolor") == true){
				testres[i][0] = 0;
				testres[i][1] = 1;
				testres[i][2] = 0;
			}
			else{
				testres[i][0] = 0;
				testres[i][1] = 0;
				testres[i][2] = 1;
			}
		}


/*
		//訓練データ（入力）
		double input[][] = {
				{1, 1},
				{1, 0},
				{0, 1},
				{0, 0}
		};
		//訓練データ（出力）
		double res[][] = {
				{0.0},
				{1.0},
				{1.0},
				{0.0}
		};
*/
		//BPによる学習
		NeuralNet nn = new NeuralNet();
		//学習にかかったエポック数。
		int epoch = 0;
		while(true){
			System.out.println("[NeuralNet]		epoch:"+epoch++);
			//二乗誤差の総和
			double e = 0.0;

			//すべての訓練データについて、BP
			for(int i=0; i<sampleiris.size(); i++){
				nn.compute(input[i]);
				nn.backPropagation(res[i]);

				e += nn.calcError(res[i]);
			}

			//二乗誤差が十分小さくなったら、終了
			System.out.println("Error = " + e);
			if(e < 0.001){
				System.out.println("Error < 0.001");
				break;
			}
		}
		//誤差が収束した後、結果の出力のためにもう一度データを学習する
		System.out.println("[NeuralNet]		epoch:"+epoch++);
		//二乗誤差の総和
		double e = 0.0;

		//すべての訓練データについて、BP
		for(int i=0; i<sampleiris.size(); i++){
			nn.compute(input[i]);
			nn.backPropagation(res[i]);
			System.out.println("  INPUT:" + input[i][0] + "," + input[i][1] + "," + input[i][2] + "," + input[i][3] + " -> " + nn.output[0] + "(" + res[i][0] + ")" + nn.output[1] + "(" + res[i][1] + ")" + nn.output[2] + "(" + res[i][2] + ")");

			e += nn.calcError(res[i]);
		}
		System.out.println("Error = " + e);

		//学習後はテストデータを用いて汎化誤差を求める
		System.out.println("===============================================================================================================");
		for(int i=0; i<testiris.size(); i++){
			nn.compute(testinput[i]);
			System.out.println("  INPUT:" + testinput[i][0] + "," + testinput[i][1] + "," + testinput[i][2] + "," + testinput[i][3] + " -> " + nn.output[0] + "(" + testres[i][0] + ")" + nn.output[1] + "(" + testres[i][1] + ")" + nn.output[2] + "(" + testres[i][2] + ")");
			e += nn.calcError(testres[i]);
		}
		System.out.println("Generalization Error = " + e);

	}

	//NNに入力し、出力（＝Q値）を計算する
	public void compute(double in[]){

		//入力層
		for(int i=0; i<N_INPUT; i++){
			input[i] = (double)in[i];
		}

		//隠れ層の計算
		for(int i=0; i<N_HIDDEN; i++){
			hidden[i] = 0.0;
			for(int j=0; j<N_INPUT; j++){
				hidden[i] += w1[j][i] * input[j];
			}
			hidden[i] = sigmoid(hidden[i]);
		}

		//出力層（Q値）の計算
		for(int i=0; i<N_OUTPUT; i++){
			output[i] = 0.0;
			for(int j=0; j<N_HIDDEN; j++){
				output[i] += w2[j][i] * (hidden[j]);
			}
			output[i] = sigmoid(output[i]);
		}
	}

	//シグモイド関数
	public double sigmoid(double i){
		double a = 1.0 / (1.0 + Math.exp(-i));
		return a;
	}

	//誤差逆伝播法による重みの更新
	public void backPropagation(double teach[]){

		//隠れ>出力の重みを更新
		for(int i=0; i<N_OUTPUT; i++){
			for(int j=0; j<N_HIDDEN; j++){
				double delta = -alpha*( -(teach[i]-output[i])*output[i]*(1.0-output[i])*hidden[j] );
				w2[j][i] += delta;
			}
		}

		//入力>隠れの重みを更新
		for(int i=0; i<N_HIDDEN; i++){

			double sum = 0.0;
			for(int k=0; k<N_OUTPUT; k++){
				sum += w2[i][k]*(teach[k]-output[k])*output[k]*(1.0-output[k]);
			}

			for(int j=0; j<N_INPUT; j++){
				double delta = alpha*hidden[i]*(1.0-hidden[i])*input[j]*sum;
				w1[j][i] += delta;
			}
		}
	}

	//二乗誤差
	public double calcError(double teach[]){
		double e = 0.0;
		for(int i=0; i<N_OUTPUT; i++){
			e += Math.pow(teach[i]-output[i], 2.0);
		}
		e /= N_OUTPUT;
		return e;
	}

}