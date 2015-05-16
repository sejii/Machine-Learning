package komota.main;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import komota.supers.MyDataFilter;
import komota.supers.MyDataInput;
import komota.supers.MyFrame;


public class IrisMain extends MyFrame{

	/**
	 * @param args
	 */

	ArrayList<double[]> sampleiris = new ArrayList<double[]>();
	//テスト用カウンター
	int countdata = 0;
	int countfeature = 0;
	int countcurrent = 0;



	//コンストラクタ
	public IrisMain(String framename){
		super(framename);
		try {
			sampleiris = MyDataFilter.normalization((ArrayList<double[]>) MyDataInput.txtToDoubleData("iris.txt"));
			this.playflag = true;
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		this.drawClear();
	}

/*
	public static void main(String[] args) throws FileNotFoundException {
		// TODO 自動生成されたメソッド・スタブ
		System.out.println("Welcome to IrisMain");

		IrisMain mainframe = new IrisMain("フレーム表示テスト");


	}
*/



	@Override
	public void run() {
		// TODO 自動生成されたメソッド・スタブ
		if(this.countfeature < 5){
			this.countfeature++;
		}
		else{
			this.countfeature = 1;
			this.countdata++;
		}
			System.out.println("[IrisMain]		sampleiris.get("+this.countdata+")["+this.countfeature+"]:"+this.sampleiris.get(countdata)[countfeature]);
			Color color = Color.WHITE;
			switch((int)this.sampleiris.get(countdata)[5]){
			case 0:
				color = Color.blue;
				break;
			case 1:
				color = Color.red;
				break;
			case 2:
				color = Color.green;
				break;
			}
			this.drawDot(this.sampleiris.get(countdata)[3] * 100 + 400,500-(this.sampleiris.get(countdata)[4]* 100), 3, color);
			this.drawDot(200,countdata * 30 + 200, 3, Color.WHITE);
			this.drawDot(countdata * 30 + 200,200, 3, Color.WHITE);
	}



}
