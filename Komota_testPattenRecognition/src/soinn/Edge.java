package soinn;

/**
 * SOINN ネットワークにおけるエッジ
 * @author Kazuhiro Yamasaki
 * @author Naoya Makibuchi
 * @author Daiki Kimura
 * @version 2011.10(2.3+)
 */
public class Edge
{
	/* -------- constants -------------- */
	public static final int EMPTY = -1;

	/* -------- private fields --------- */
	/** エッジの始点ノード */
	private int from;

	/** エッジの終点ノード */
	private int to;

	/** エッジの年齢 */
	private int age;

	/* -------- constructor ------------ */
	/**
	 * コンストラクタ
	 */
	public Edge()
	{
		init();
	}

	/**
	 * コンストラクタ<br>
	 * ノード間にエッジを生成する
	 * @param origin 		エッジの始点ノード
	 * @param terminate 	エッジの終点ノード
	 */
	public Edge(final int origin, final int terminate)
	{
		init();
		from 	= origin;
		to 		= terminate;
	}

	/**
	 * コピーコンストラクタ<br>
	 * 与えられたエッジと同じ情報を持つエッジを生成する
	 * @param node コピー元のエッジインスタンス
	 */
	public Edge(final Edge edge)
	{
		copy(edge);
	}

	/**
	 * コンストラクタ<br>
	 * 指定の状態で初期化
	 * @param origin 		エッジの始点ノード
	 * @param terminate 	エッジの終点ノード
	 * @param age			エッジの年齢
	 * @deprecated 動作未確認 by Makibuchi
	 */
	public Edge(final int origin, final int terminate, final int age)
	{
		init();
		from 		= origin;
		to 			= terminate;
		this.age 	= age;
	}

	/* -------- public methods --------- */
	/**
	 * ノードIDの置換をエッジの接続関係に反映する
	 * @param before 	置換前のノード ID
	 * @param after 	置換後のノード ID
	 * @return 反映されたら true、そうでなければ false
	 */
	public boolean replace(final int before, final int after)
	{
		// 置換前後で一致していたら false を返す
		if (before == after)
		{
			return false;
		}

		// 置換によって接続関係が変更されなければ false を返す
		if ((from == before) && (to == after)) {
			return false;
		}
		if ((from == after) && (to == before)) {
			return false;
		}

		// それ以外の場合は適切に反映する
		if (from == before)
		{
			from = after;
		}
		if (to == before)
		{
			to = after;
		}

		return true;
	}

	/**
	 * エッジの始点ノード getter
	 * @return エッジの始点ノード
	 */
	public int getFrom()
	{
		return from;
	}

	/**
	 * エッジの終点ノード getter
	 * @return エッジの終点ノード
	 */
	public int getTo()
	{
		return to;
	}

	/**
	 * エッジの年齢 getter
	 * @return エッジの年齢
	 */
	public int getAge()
	{
		return age;
	}

	/**
	 * エッジを加齢する
	 * @param delta 加齢幅（通常は1）
	 */
	public void addAge(final int delta)
	{
		age += delta;
	}

	/**
	 * エッジの年齢をリセットする
	 */
	public void resetAge()
	{
		age = 0;
	}

	/* -------- private methods -------- */
	/**
	 * すべてのコンストラクタに共通の設定、初期化
	 */
	private void init()
	{
		from 	= EMPTY;
		to 		= EMPTY;
		age 	= 0;
	}

	/**
	 * コピー用メソッド
	 * @param edge コピー元のエッジインスタンス
	 */
	private void copy(Edge edge)
	{
		this.from 	= edge.from;
		this.to 	= edge.to;
		this.age 	= edge.age;
	}
}
