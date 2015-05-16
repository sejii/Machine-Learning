package komota.supers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

public abstract class MyFrame extends JFrame{

	//2015/5/11
	//２次元データ分布表示用のフレームクラス。クラスタは色で表現する
	//2015/5/13
	//バッファ部分を作成。簡単なメソッドのみの描画ができるようになった。
	//2015/5/15
	//描画時のGraphics2Dの取得と廃棄のタイミングを調整し、画面のちらつきをなくした。
	//依然として時々描画されなくなる現象が生じる。

	//実行フラグ。データインプットなどの前処理が終わってからrunを実行できるようにするためのフラグ
	public boolean playflag = false;

	Graphics2D g;

	//バッファストラテジー。コンストラクタでMyFrame自身のBufferStrategyを参照させる。
	public BufferStrategy buffer;

	//コンストラクタ
	public MyFrame(String framename){
		super(framename);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1000, 1000);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setVisible(true);

		//バッファの作成
		this.setIgnoreRepaint(true);
		this.createBufferStrategy(2);
		this.buffer = this.getBufferStrategy();
		this.g = (Graphics2D)MyFrame.this.buffer.getDrawGraphics();


		Timer t = new Timer();
		t.schedule(new RenderTask(), 0,8);
	}
	//タイマーを起動しない場合に使用するコンストラクタ。複数画面表示したいときに使用する。試験中
	public MyFrame(String framename,int putZERO){
		super(framename);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1000, 1000);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setVisible(true);

		//バッファの作成
		this.setIgnoreRepaint(true);
		this.createBufferStrategy(2);
		this.buffer = this.getBufferStrategy();
		this.g = (Graphics2D)MyFrame.this.buffer.getDrawGraphics();
	}

	//画面をクリアする
	public void drawClear(){
		g.setColor(Color.BLACK);
		g.fillRect(0,0,this.getWidth(),this.getHeight());
	}
	//点を描画する
	public void drawDot(double x,double y,double r,Color color){
		g.setColor(color);
		g.fillOval((int)x, (int)y, (int)r, (int)r);
	}
	//直線を描画する
	public void drawLine(double px,double py,double qx,double qy,Color color){
		g.setColor(color);
		g.drawLine((int)px, (int)py, (int)qx, (int)qy);


	}







	//タイマータスクで実行する内容。利用時に具体化する
	public abstract void run();



	class RenderTask extends TimerTask{

		@Override
		public void run() {
			// TODO 自動生成されたメソッド・スタブ
			Graphics2D g = (Graphics2D)MyFrame.this.buffer.getDrawGraphics();
			drawClear();
			//実行フラグ(playflag)がtrueのとき、各サブフレームで実装したrunが実行される
			if(playflag == true){
				MyFrame.this.run();
			}
			g.dispose();
			buffer.show();
		}
	}
}
