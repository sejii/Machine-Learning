package komota.supers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyDataInput {

	//データ入力用のスタティッククラス
	/*txtToData(テキストファイルのデータセットをリスト化)を使用する際は
	 * 1. １行目はデータの説明に使用する。(インプット時には破棄)
	 * 2. 特徴間はタグで区切る
	 */
	//2015/5/13
	//データをdoubleで読み込めるtxtToDoubleDataを作成。String型の特徴（ラベル）も整数にナンバリングされるため、注意が必要

	ArrayList<String> inputlist = new ArrayList<String>();


	//一つ（1行）のデータを引数に、データを特徴ごとに分割した配列を返すメソッド
	public static String[] dataToArray(String data){

		String[] features = data.split("	");

		return features;
	}
	//2015/5/11
	//txt形式のデータセットのファイル名が与えられたとき、「特徴ごとに配列分けされたデータのArrayList」を返すメソッド。ややこしいので注意
	public static List<String[]> txtToData(String filename) throws IOException{

		ArrayList<String[]> dataset = new ArrayList<String[]>();

		BufferedReader reader = null;

		File file = new File("dataset\\"+filename);
		reader = new BufferedReader(new FileReader(file));

		String line = reader.readLine();
		//１行目を破棄
		line = reader.readLine();
		int datanum = 0;

		while(line != null){
			dataset.add(dataToArray(line));
			line = reader.readLine();
			datanum++;
		}


		//ファイルクローズ
			reader.close();

		return dataset;
	}




	//データをdoubleで格納するメソッド。Stringで返しても使えないじゃん。
	//ラベルなどdouble以外の特徴がある場合、整数でナンバリングする。その際どの特徴量がどの数字に割り当てられたかを標準出力する
	public static List<double[]> txtToDoubleData(String filename) throws IOException{

		ArrayList<double[]> dataset = new ArrayList<double[]>();
		ArrayList<String> labelnames = new ArrayList<String>();

		BufferedReader reader = null;

		File file = new File("dataset\\"+filename);
		reader = new BufferedReader(new FileReader(file));

		String line = reader.readLine();
		//１行目を破棄
		line = reader.readLine();
		int datanum = 0;

		while(line != null){

			String[] temp = line.split("	");
			double[] tempdouble = new double[temp.length];
			for(int i=0;i<temp.length;i++){
				try{
					tempdouble[i] = Double.parseDouble(temp[i]);
				}catch(NumberFormatException e){
					//実数でない文字列の場合、対応する整数に変換する
					tempdouble[i] = -1;
					for(int j=0;j<labelnames.size();j++){
						if(temp[i].equals(labelnames.get(j)) == true){
							//既にあるラベルの場合、対応するラベル番号を格納する
							tempdouble[i] = j;
							break;
						}
					}
					if(tempdouble[i] == -1){
						//存在しないラベルの場合、新たにラベル番号を割り当てる
						labelnames.add(temp[i]);
						System.out.println("[MyDataInput]		Label \""+temp[i]+"\" is numbered "+ (labelnames.size()-1));
						tempdouble[i] = labelnames.size()-1;
					}
				}
			}
			dataset.add(tempdouble);
			line = reader.readLine();
			datanum++;
		}


		//ファイルクローズ
			reader.close();

		return dataset;
	}

}
