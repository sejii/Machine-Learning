package soinn;

/**
 * SOINN ネットワークにおけるノード
 * @author Kazuhiro Yamasaki
 * @author Naoya Makibuchi
 * @author Daiki Kimura
 * @version 2011.10(2.3+)
 */
public class Node
{
	/* -------- constants -------------- */
	public static final int MAX_NEIGHBOR = 30;// 隣接ノードの最大数
	public static final int EMPTY 		= -1;
	
	/* -------- class variables -------- */
	/** 次元数 */
	public static int dimension = 0;

	/* -------- private fields --------- */
	/** 結合重み */
	private double[] 	signal;
	
	/** 近傍ノード */
	private int[] 		neighbor;
	
	/** 近傍ノード数 */
	private int 		neighborNum;
	
	/** 学習回数 */
	private int 		learningTime;
	
	/** クラス ID */
	private int 		classID;

	/**
	 * 除去フラグ<br>
	 * SOINN.removeUnnecessaryNode() で使用する
	 */
	private boolean 	removeFlg;
	
	/* -------- constructor ------------ */
	/**
	 * コンストラクタ<br>
	 * init() を呼び出す
	 */
	public Node()
	{
		init();
	}
	
	/**
	 * コンストラクタ<br>
	 * 与えられたデータベクトルを持つノードを生成する
	 * @param signal 入力シグナル
	 */
	public Node(double[] signal)
	{
		init();
		
		if (Node.dimension <= 0)
		{
			return;
		}

		if (signal == null)
		{
			return;
		}
		
		// 入力シグナルを読み込む
		for (int i = 0; i < Node.dimension; i++)
		{
			this.signal[i] = signal[i];
		}
	}
	
	/**
	 * コピーコンストラクタ<br>
	 * 与えられたノードと同じ情報を持つノードを生成する
	 * @param node コピー元のノードインスタンス
	 */
	public Node(Node node)
	{
		this.signal 	= null;
		this.neighbor 	= null;
		copy(node);
	}
	
	/* -------- public methods --------- */
	/**
	 * 指定されたノードを近傍ノードとして登録する<br>
	 * @param node ノード
	 * @return ノードが追加されたら true、そうでなければ false
	 */
	public boolean addNeighbor(final int node)
	{
		// 近傍ノードリストの要素数が最大数に達していたら false を返す
		if (neighborNum >= MAX_NEIGHBOR)
		{
			return false;
		}
		
		// 既にそのノードが近傍ノードとなっていたら false を返す
		for (int i = 0; i < neighborNum; i++)
		{
			if (neighbor[i] == node)
			{
				return false;
			}
		}
		
		// どちらでもなければそのノード ID を新たに追加し、インデックスを増やして
		neighbor[neighborNum] = node;
		neighborNum++;
		
		return true;
	}
	
	/**
	 * 指定されたノードを近傍ノードから削除する
	 * @param node ノード
	 * @return ノードが削除されたら true、そうでなければ false
	 */
	public boolean deleteNeighbor(final int node)
	{
		// 指定されたノードが近傍ノードに存在するかチェックする
		for (int i = 0; i < neighborNum; i++)
		{
			if (node == neighbor[i])
			// 存在すれば削除する
			{
				neighborNum--;
				neighbor[i] = neighbor[neighborNum];
				neighbor[neighborNum] = EMPTY;
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * ノードIDの置換を近傍ノードに反映させる
	 * @param before 	置換前のノード ID
	 * @param after 	置換後のノード ID
	 * @return 反映されたら true、そうでなければ false
	 */
	public boolean replaceNeighbor(final int before, final int after)
	{
		// 置換前後で一致していたら false を返す
		if (before == after)
		{
			return false;
		}
		
		// 近傍ノードリストから対象のノードを探索する
		for (int i = 0; i < neighborNum; i++)
		{
			if (before == neighbor[i])
			// 置換前のノード ID が見つかったら
			{
				if (isNeighbor(after))
				// 既に置換後のノード ID が近傍ノードとなっていたら
				{
					// before も after も近傍ノードであるから、
					// before ノードを削除すればよい
					neighborNum--;
					neighbor[i] = neighbor[neighborNum];
					neighbor[neighborNum] = EMPTY;
				}
				else
				{
					// before は近傍ノードだが after が近傍でないなら、
					// 単純に before に after を上書きすればよい
					neighbor[i] = after;
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 指定されたノードを近傍ノードかどうか判定する
	 * @param node ノード
	 * @return 近傍ノードであれば true、そうでなければ false
	 */
	public boolean isNeighbor(final int node)
	{
		for (int i = 0; i < neighborNum; i++)
		{
			if (neighbor[i] == node) 
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 結合重み getter
	 * @return 結合重み（ベクトル）
	 */
	public double[] getSignal()
	{
		return signal;
	}
	
	/**
	 * 近接ノード getter
	 * @return 近接ノード（リスト）
	 */
	public int[] getNeighbor()
	{
		return neighbor;
	}
	
	/**
	 * 近傍ノード数 getter
	 * @return 近接ノード数
	 */
	public int getNeighborNum()
	{
		return neighborNum;
	}
	
	/**
	 * 近傍ノード数 setter
	 * @param neighborNum 近接ノード数
	 */
	public void setNeighborNum(final int neighborNum)
	{
		this.neighborNum = neighborNum;
	}
	
	/**
	 * 学習回数 getter
	 * @return 学習回数
	 */
	public int getLearningTime()
	{
		return learningTime;
	}
	
	/**
	 * 学習回数 setter
	 * @param learnintTime 学習回数
	 */
	public void setLearningTime(final int learningTime)
	{
		this.learningTime = learningTime;
	}
	
	/**
	 * 学習回数を増やす
	 * @param delta 増加幅（通常は1）
	 */
	public void addLearningTime(final int delta)
	{
		learningTime += delta;
	}
	
	/**
	 * クラス ID getter
	 * @return クラス ID
	 */
	public int getClassID()
	{
		return classID;
	}
	
	/**
	 * クラス ID setter
	 * @param classID クラス ID
	 */
	public void setClassID(final int classID)
	{
		this.classID = classID;
	}
	
	/**
	 * 除去フラグ getter
	 * @return 除去フラグ
	 */
	public boolean getRemoveFlg()
	{
		return removeFlg;
	}
	
	/**
	 * 除去フラグ setter
	 * @param removeFlg 除去フラグ
	 */
	public void setRemoveFlg(final boolean removeFlg)
	{
		this.removeFlg = removeFlg;
	}
	
	/* -------- private methods -------- */
	/**
	 * 次元数を設定する
	 * @param argDimension 次元数
	 * @return 設定できたら true、そうでなければ false
	 */
	static protected boolean setDimension(final int argDimension)
	{
		if (argDimension <= 0)
		{
			return false;
		}
		
		Node.dimension = argDimension;
		return true;
	}
	
	/**
	 * すべてのコンストラクタに共通の処理、初期化
	 */
	private void init()
	{
		// 結合重み領域を確保
		if (Node.dimension > 0)
		{
			signal = new double[Node.dimension];
		}
	
		neighbor 		= new int [MAX_NEIGHBOR]; // 近傍ノードを格納する配列
		neighborNum 	= 0;
		learningTime 	= 0;
		classID 		= SOINN.UNCLASSIFIED;
		
		// 近傍ノードの初期化
		for (int i = 0; i < MAX_NEIGHBOR; i++)
		{
			neighbor[i] = EMPTY;
		}
	}
	
	/**
	 * 引数に与えられたノードを自分自身にコピーする<br>
	 * @param node コピー元ノードインスタンス
	 */
	private void copy(Node node)
	{
		// 既にノードに情報があるなら初期化
		if (signal != null)
		{
			signal = null;
		}
		if (neighbor != null)
		{
			neighbor = null;
		}
		
		// 中身のコピー
		signal = new double[Node.dimension];
		neighbor = new int[neighborNum];
		
		for (int i = 0; i < dimension; i++)
		{
			signal[i] = node.signal[i];
		}
		for (int i = 0; i < neighborNum; i++)
		{
			neighbor[i] = node.neighbor[i];
		}  
		
		neighborNum 	= node.neighborNum;
		learningTime 	= node.learningTime;
		classID			= node.classID;
	}
}
