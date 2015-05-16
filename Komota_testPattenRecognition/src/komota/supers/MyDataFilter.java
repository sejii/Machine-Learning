package komota.supers;

import java.util.ArrayList;

public class MyDataFilter {

	//2015/5/14
	//データのフィルタリング処理クラス。MyDataInputで生成したArrayList<double[]>の形式を前提とする。

	//標準化処理。
	public static ArrayList<double[]> normalization(ArrayList<double[]> inputlist){

		ArrayList<double[]> result = new ArrayList<double[]>();
		double[][] temptable = new double[inputlist.size()][inputlist.get(0).length];
		double mean = 0;
		double sigma = 0;


		for(int featurenum=0;featurenum<inputlist.get(0).length;featurenum++){
			//平均の計算
			for(int i=0;i<inputlist.size();i++){
				mean += inputlist.get(i)[featurenum];
			}
			mean /= inputlist.size();

			//分散の計算
			for(int i=0;i<inputlist.size();i++){
				double temp = inputlist.get(i)[featurenum] - mean;
				sigma += temp * temp;
			}
			sigma /= inputlist.size();

			//データの標準化
			for(int i=0;i<inputlist.size();i++){
				temptable[i][featurenum] = (inputlist.get(i)[featurenum] - mean) / Math.sqrt(sigma);
			}
		}
		//List形式に変換。まどろっこしい感じになってしまった
		for(int i=0;i<inputlist.size();i++){
			result.add(temptable[i]);
		}
		return result;
	}

	//無相関化処理。
	public static ArrayList<double[]> zero_correlation(ArrayList<double[]> inputlist){

		ArrayList<double[]> result = new ArrayList<double[]>();
		double[][] temptable = new double[inputlist.size()][inputlist.get(0).length];

		System.out.println("[MyDataFilter]		zero_correlation wasn't success...");


		return inputlist;
	}
}
