package soinn;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * SOINN(Single Network Structure)
 * 
 * @author Naoya Makibuchi
 * @author Daiki Kimura
 * @version 2011.10(2.3+)
 */
public class SOINN
{
	/* -------- constants -------------- */
	public static final int 	NO_CHANGE 		= 0; 
	public static final int 	UNCLASSIFIED 	= -1;
	public static final int 	NOT_FOUND 		= -1;
	public static final double 	INFINITY 		= Double.POSITIVE_INFINITY;

	/* -------- private fields --------- */	
	/** ノード情報 */
	private ArrayList<Node> 	nodeInfo;

	/** エッジ情報　*/
	private ArrayList<Edge> 	edgeInfo;
	
	/** 次元数 */
	private int 				dimension;

	/** ノード削除基準 */
	private int 				removeNodeTime;

	/** エッジ削除基準 */
	private int 				deadAge;
	
	/** クラス数 */
	private int 				classNum;
	
	/** 既知データ数 */
	private int		 			effectiveInputNum;
	
	/* -------- constructor ------------ */	
	/**
	 * コンストラクタ
	 * @param dimension 		次元
	 * @param removeNodeTime 	ノード削除基準
	 * @param deadAge 			エッジ削除基準
	 */
	public SOINN(final int dimension, final int removeNodeTime, final int deadAge)
	{
		// 初期化
		this.dimension 			= 0;
		this.removeNodeTime 	= 0;
		this.deadAge 			= 0;
		this.classNum 			= 0;
		this.effectiveInputNum 	= 0;

		// 引数の次元が適切な値ならそれで初期化
		if (dimension > 0)
		{
			this.dimension = dimension;
			Node.setDimension(dimension);
		}
		
		// 同上、適切なら初期化
		if (removeNodeTime > 0)
		{
			this.removeNodeTime = removeNodeTime;
		}

		// 同上、適切なら初期化
		if (deadAge > 0)
		{
			this.deadAge = deadAge;
		}
		
		// インスタンスの生成
		nodeInfo = new ArrayList<Node>();
		edgeInfo = new ArrayList<Edge>();
	}
	
	/* -------- public methods --------- */
	/**
	 * シグナルを入力する
	 * @param signal シグナル
	 * @return シグナルが正常に入力されたら true、そうでなければ false
	 */
	public boolean inputSignal(final double[] signal)
	{
		// winners[0] が第1勝者
		// winners[1] が第2勝者
		int[] winners;
		
		// 入力が不正な値なら false を返す
		if (signal == null)
		{
			return false;
		}
		
		// ノード数が2個未満のときは無条件にノードを挿入する
		if (nodeInfo.size() < 2)
		{
			addNode(signal);
			return true;
		}
		
		// 第1勝者と第2勝者を探索する
		winners = findWinnerAndSecondWinner(signal);
		
		// 類似度閾値という基準を用いて第1勝者と同一のクラスタに属すか判定、
		// 異なるクラスタに属すと判定された場合、入力を重みとして持つ新たなノードを挿入する
		if (!isEffectiveInput(winners, signal))
		{
			addNode(signal);
			return true;
		}
		
		// 以下、同一のクラスタに属すと判定された場合の処理
		
		// 既知データ数をインクリメントする
		effectiveInputNum++;

		addEdge(winners[0], winners[1]); 		// 第1勝者と第2勝者の間にエッジを生成する
		resetEdgeAge(winners[0], winners[1]); 	// 第1勝者と第2勝者の間のエッジの年齢を0に戻す
		incrementEdgeAge(winners[0]); 			// 第1勝者に接続するエッジの年齢をインクリメントする
		removeDeadEdge(); 						// 年齢が閾値 deadAge を越えたエッジを削除する
		updateLearningTime(winners[0]); 		// 第1勝者の学習回数をインクリメントする
		moveNode(winners[0], signal);			// 第1勝者とその近傍ノードの重みを更新する
		
		// 入力が removeNodeTime 回与えられるごとに不要なノードを削除する
		if ((effectiveInputNum % removeNodeTime) == 0)
		{
			removeUnnecessaryNode();
			
			// 各ノードにクラスラベルを割り当てる
			//classNum = classify();
			classify();
		}

		return true;
	}
	
	/**
	 * 全ノードにクラス ID を割り当てる
	 */
	public void classify()
	{
		int nodeNum = nodeInfo.size();
		
		// 全部ノードのラベルを初期化する
		for (int i = 0; i < nodeNum; i++)
		{
			nodeInfo.get(i).setClassID(UNCLASSIFIED);
		}
		
		// クラス ID を割り当てる
		int classNum = 0;
		for (int i = 0; i < nodeNum; i++)
		{
			if (nodeInfo.get(i).getClassID() == UNCLASSIFIED)
			{
				setClassID(i, classNum);
				classNum++;
			}
		}
		
		this.classNum = classNum;
	}
	
	/**
	 * SOINN をリセットする<br>
	 * 引数はコンストラクタと同じ
	 */
	public void reset(final int dimension, final int removeNodeTime, final int deadAge)
	{
		nodeInfo.clear();
		edgeInfo.clear();
		
		if ((dimension != NO_CHANGE) && (dimension > 0))
		{
			this.dimension = dimension;
			Node.setDimension(dimension);
		}
		
		if ((removeNodeTime != NO_CHANGE) && (removeNodeTime > 0))
		{
			this.removeNodeTime = removeNodeTime;
		}
		
		if ((deadAge != NO_CHANGE) && (deadAge > 0))
		{
			this.deadAge = deadAge;
		}
		
		classNum 			= 0;
		effectiveInputNum 	= 0;
	}
	
	/**
	 * SOINN の次元数を設定する
	 * @param dimension 次元数
	 * @return 正常に設定できたら true、そうでなければ false
	 */
	public boolean setDimension(final int dimension)
	{
		if (this.dimension == dimension)
		{
			return true;
		}
		
		if (dimension <= 0)
		{
			return false;
		}
		
		reset(dimension, NO_CHANGE, NO_CHANGE);
		
		return true;
	}
	
	/**
	 * 次元数を取得する
	 * @return 次元数（dimension）
	 */
	public int getDimension()
	{
		return this.dimension;
	}
	
	/** 
	 * ノード数を取得する
	 * @param isIgnoreAcnode 孤立ノードは含めない場合は true、そうでなければ false
	 * @return ノード数
	 */
	public int getNodeNum(final boolean isIgnoreAcnode/*=false*/)
	{
		// 孤立ノードを含めない場合
		if (isIgnoreAcnode)
		{
			int count = 0;
			int nodeNum = nodeInfo.size();
			
			for (int i = 0; i < nodeNum; i++)
			{
				if (nodeInfo.get(i).getNeighborNum() > 0)
				{
					count++;
				}
			}
			return count;
		}
		// 孤立ノードを含める場合（全ノード数）
		else
		{
			return nodeInfo.size();
		}
	}
	
	/**
	 * エッジ数を取得する
	 * @return エッジ数
	 */
	public int getEdgeNum()
	{
		return edgeInfo.size();
	}
	
	/**
	 * クラス数を取得する
	 * @return クラス数
	 */
	public int getClassNum()
	{
		return classNum;
	}
	
	/**
	 * ノードを取得する
	 * @param node ノード ID
	 * @return ノードインスタンス
	 */
	public Node getNode(final int node)
	{
		if (!isExistNode(node))
		{
			return null;
		}
		
		return nodeInfo.get(node);
	}
	
	/**
	 * エッジを取得する
	 * @param edge エッジ ID
	 * @return エッジインスタンス
	 */
	public Edge getEdge(final int edge)
	{
		if (!isExistEdge(edge))
		{
			return null;
		}
		
		return edgeInfo.get(edge);
	}
	
	/**
	 * 不要なノードを削除する
	 */
	public void removeUnnecessaryNode()
	{
		/*
		int lastNode = ((int)nodeInfo.size() - 1);
		for (int i = lastNode; i >= 0; i--)
		{
			// ノード全体から近傍ノード数が1個以下のものを削除する
			if (nodeInfo.get(i).getNeighborNum() <= 1)
			{
				removeNode(i);
			}
		}
		*/
		
		// 近傍ノード数が1個以下のノードの除去フラグを true にする
		int lastNode = ((int)nodeInfo.size() - 1);
		for (int i = lastNode; i >= 0; i--)
		{
			if (nodeInfo.get(i).getNeighborNum() <= 1)
			{
				nodeInfo.get(i).setRemoveFlg(true);
			}
			else
			{
				nodeInfo.get(i).setRemoveFlg(false);
			}
		}
		
		// 除去フラグが true のノードを削除する
		for (int i = lastNode; i >= 0; i--)
		{
			if (nodeInfo.get(i).getRemoveFlg())
			{
				removeNode(i);
			}
		}
	}
	
	/**
	 * ネットワークデータをバイナリで書き出す
	 * @param dos DataOutputStream インスタンス
	 * @return 正常に書き出せたら true、そうでなければ false
	 * @deprecated 動作未確認 by Makibuchi 
	 */
	public boolean saveNetworkData(DataOutputStream dos)
	{
		try
		{
			// ネットワーク全体のプロパティ
			dos.writeInt(this.dimension);
			dos.writeInt(this.removeNodeTime);
			dos.writeInt(this.deadAge);
			dos.writeInt(this.classNum);
			dos.flush();
			
			// 各ノードのプロパティ
			int nodeNum = this.nodeInfo.size();
			dos.writeInt(Node.MAX_NEIGHBOR);
			dos.writeInt(nodeNum);
			for (int i = 0; i < nodeNum; i++)
			{
				Node objNode = this.nodeInfo.get(i);
				
				// シグナル
				for (int j = 0; j < objNode.getSignal().length; j++)
				{
					dos.writeDouble(objNode.getSignal()[j]);
				}
				
				// 近傍ノード
				for (int j = 0; j < Node.MAX_NEIGHBOR; j++)
				{
					if (j < objNode.getNeighborNum())
					{
						dos.writeInt(objNode.getNeighbor()[j]);
					}
					else
					{
						dos.writeInt(Node.EMPTY);
					}
				}
				dos.writeInt(objNode.getNeighborNum()); // 近傍ノード数
				dos.writeInt(objNode.getLearningTime());// 学習回数
				dos.writeInt(objNode.getClassID());		// クラス ID
				
			}
			dos.flush();
			
			// 各エッジのプロパティ
			dos.writeInt(this.getEdgeNum());
			for (int i = 0; i < this.getEdgeNum(); i++)
			{
				Edge edge = this.edgeInfo.get(i);
				
				dos.writeInt(edge.getFrom());
				dos.writeInt(edge.getTo());
				dos.writeInt(edge.getAge());
				
			}
			dos.flush();
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
			return false;
		}
			
		return true;
	}
	
	/**
	 * ネットワークデータを（ファイル名を指定して）バイナリで書き出す
	 * @param filename ファイル名
	 * @return 正常に書き出せたら true、そうでなければ false
	 * @deprecated 動作未確認 by Makibuchi 
	 */
	public boolean saveNetworkData(final String filename)
	{
		try
		{
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(filename));
			
			if (!saveNetworkData(dos))
			{
				return false;
			}
			
			dos.close();
		
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**
	 * ネットワークデータをバイナリから読み込む
	 * @param dis DataInputStream インスタンス
	 * @return 正常に読み込めたら true、そうでなければ false
	 * @deprecated 動作未確認 by Makibuchi 
	 */
	public boolean loadNetworkData(DataInputStream dis)
	{
		try
		{
			// データの読み込み＋インスタンスの生成
			
			// ネットワーク全体のプロパティ
			this.dimension 			= dis.readInt();
			this.removeNodeTime 	= dis.readInt();
			this.deadAge 			= dis.readInt();
			this.classNum 			= dis.readInt();
			
			// インスタンスの生成
			this.nodeInfo = new ArrayList<Node>();
			this.edgeInfo = new ArrayList<Edge>();
			
			// 各ノードのプロパティ
			Node.setDimension(this.dimension);
			dis.readInt();// 1つスキップ
			
			int nodeNum = dis.readInt();
			for (int i = 0; i < nodeNum; i++)
			{	
				Node objNode = new Node();

				for (int j = 0; j < Node.dimension; j++)
				{
					objNode.getSignal()[j] = dis.readDouble();
				}
				
				for (int j = 0; j < Node.MAX_NEIGHBOR; j++) 
				{
					objNode.getNeighbor()[j] = dis.readInt();
				}
				
				objNode.setNeighborNum(dis.readInt());
				objNode.setLearningTime(dis.readInt());
				objNode.setClassID(dis.readInt());
				
				this.nodeInfo.add(objNode);
			}
			
			// 各エッジのプロパティ
			int edgeNum = dis.readInt();
			for (int i = 0; i < edgeNum; i++)
			{

				int from = dis.readInt();
				int to = dis.readInt();
				int age = dis.readInt();
				
				this.edgeInfo.add(new Edge(from, to, age));
			}
		}
		catch (IOException ioe)
		{
			
			ioe.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**
	 * ネットワークデータを（ファイル名を指定して）バイナリから読み込む
	 * @param filename ファイル名
	 * @return 正常に読み込めたら true、そうでなければ false
	 * @deprecated 動作未確認 by Makibuchi 
	 */
	public boolean loadNetworkData(final String filename)
	{	
		try
		{	
			DataInputStream dis = new DataInputStream(new FileInputStream(filename));

			if (!loadNetworkData(dis))
			{
				return false;
			}
			
			dis.close();
			
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**
	 * パラメータを再設定する（ネットワークはリセットしない）
	 * @param removeNodeTime 	ノード削除基準
	 * @param deadAge 			エッジ削除基準
	 */
	public boolean resetParams(final int removeNodeTime, final int deadAge)
	{
		boolean flag = true;
		
		// 同上、適切なら初期化
		if (removeNodeTime > 0)
		{
			this.removeNodeTime = removeNodeTime;
		}
		else flag = false;
	
		// 同上、適切なら初期化
		if (deadAge > 0)
		{
			this.deadAge = deadAge;
		}
		else flag = false;
		
		return flag;
	}
	
	/* -------- private methods -------- */
	/**
	 * 入力シグナルとの第1勝者ノードと第2勝者ノードを探索する
	 * @param signal 入力シグナル
	 * @return 勝者ノード配列、探索に失敗した場合は null
	 */
	private int[] findWinnerAndSecondWinner(final double[] signal)
	{
		// 勝者ノード配列の初期化
		int[] winners = new int[2];
		for (int i = 0; i < winners.length; i++)
		{
			winners[i] = NOT_FOUND;
		}
		
		// ノード数が2個以下なら null で終了
		int nodeNum = nodeInfo.size();
		if (nodeNum < 2)
		{
			return null;
		}
		
		double minDist 			= INFINITY;
		double secondMinDist 	= INFINITY;
		
		// 全ノード探索
		for (int i = 0; i < nodeNum; i++)
		{
			// 入力シグナルとの距離を計算する
			double dist = distance(nodeInfo.get(i).getSignal(), signal);

			// 勝者ノードの更新
			if (minDist > dist)
			{
				// 暫定第1勝者よりも距離が近いノードであったら
				// 暫定第1勝者を更新し、以下繰下げ
				secondMinDist 	= minDist;
				minDist 		= dist;
				winners[1] 		= winners[0];
				winners[0] 		= i;
				
			}
			else if (secondMinDist > dist)
			{
				// 暫定第2勝者よりも近ければ暫定第2勝者のみ更新する
				secondMinDist = dist;
				winners[1] = i;
			}
		}
		
		return winners;
	}
	/** 
	 * 入力シグナルが第1勝者と同一のクラスタに属すか（このとき既知データとする）判定する
	 * @param winners 勝者ノード配列
	 * @param signal 入力シグナル
	 * @return 既知データなら true、そうでなければ false
	 */
	private boolean isEffectiveInput(final int[] winners, final double[] signal)
	{
		// 勝者ノード配列のチェック
		for (int i = 0; i < winners.length; i++)
		{
			if (!isExistNode(winners[i]))
			{
				return false;
			}
		}
		
		// 類似度閾値という基準により判定する
		for (int i = 0; i < winners.length; i++)
		{
			if (distance(nodeInfo.get(winners[i]).getSignal(), signal) > getSimilarityThreshold(winners[i]))
			{
				return false;
			}
		}
		
		// 第1勝者、第2勝者との距離が、いずれも類似度閾値より近ければ既知のデータと判定する
		return true;
	}
	
	/**
	 * ノードに接続しているエッジの年齢をインクリメントする
	 * @param node ノード
	 * @return インクリメントできたら true、そうでなければ false
	 */
	private boolean incrementEdgeAge(final int node)
	{
		// ノードのチェック
		if (!isExistNode(node))
		{
			return false;
		}
		
		// 近傍ノードのチェック
		if (nodeInfo.get(node).getNeighborNum() == 0)
		{
			return false;
		}
		
		// 全エッジのうち、ノードに接続しているエッジの年齢をインクリメントする
		int edgeNum = (int)edgeInfo.size();
		for (int i = 0; i < edgeNum; i++)
		{
			int f = edgeInfo.get(i).getFrom();
			int t = edgeInfo.get(i).getTo();
			
			if ((f == node) || (t == node))
			{
				edgeInfo.get(i).addAge(1);
			}
		}
		
		return true;
	}
	
	/**
	 * エッジの年齢をリセットする
	 * @param node1 エッジ端のノード1
	 * @param node2 エッジ端のノード2
	 * @return リセットできたら true、そうでなければ false
	 */
	private boolean resetEdgeAge(final int node1, final int node2)
	{
		// ノードのチェック
		if (node1 == node2)
		{
			return false;
		}
		if (!isExistNode(node1))
		{
			return false;
		}
		if (!isExistNode(node2))
		{
			return false;
		}
		
		// 年齢のリセット
		int edge = findEdge(node1, node2);
		if (edge == NOT_FOUND)
		{
			return false;
		}
		edgeInfo.get(edge).resetAge();
	
		return true;
	}

	/**
	 * 年齢が閾値 deadAge を越えたエッジを削除する
	 * @return エッジを削除したら true、そうでなければ false
	 */
	private boolean removeDeadEdge()
	{
		boolean isRemoved = false;
		int lastEdge = ((int)edgeInfo.size() - 1);
		
		// エッジ全体のうち、年齢が閾値 deadAge を越えたエッジを削除する
		for (int i = lastEdge; i >= 0; i--)
		{
			if (edgeInfo.get(i).getAge() > deadAge)
			{
				removeEdge(i);
				isRemoved = true;
			}
		}
		
		return isRemoved;
	}
	
	/**
	 * 学習回数をインクリメントする
	 * @param node ノード
	 * @return インクリメントできたら true、そうでなければ false
	 */
	private boolean updateLearningTime(final int node)
	{
		// 入力のチェック
		if (!isExistNode(node))
		{
			return false;
		}
		
		nodeInfo.get(node).addLearningTime(1);
		return true;
	}

	/**
	 * ノードとその近傍ノードの結合重みを更新する
	 * @param node ノード
	 * @param signal 入力シグナル
	 * @return シグナルを修正していれば true
	 */
	private boolean moveNode(final int node, final double[] signal)
	{	
		// 入力のチェック
		if (!isExistNode(node))
		{
			return false;
		}
		if (nodeInfo.get(node).getLearningTime() == 0)
		{
			return false;
		}
		
		// 学習率の計算
		Node objNode = nodeInfo.get(node);
		double learningRateOfNode = 1.0 / (double)objNode.getLearningTime();
		double learningRateOfNeighbor = learningRateOfNode / 100.0;
		//learningRateOfNeighbor = 0.0; // 平均位置に収束させたい場合はゼロにする
		
		// ノードの結合重みを更新する
		for (int i = 0; i < this.dimension; i++)
		{
			objNode.getSignal()[i] += learningRateOfNode * (signal[i] - objNode.getSignal()[i]);
		}
		
		// 近傍ノードの結合重みを更新する
		int neighborNum = objNode.getNeighborNum();
		for (int i = 0; i < neighborNum; i++)
		{
			int neighbor = objNode.getNeighbor()[i];
			Node objNeighbor = nodeInfo.get(neighbor);
			
			for (int j = 0; j < this.dimension; j++)
			{
				objNeighbor.getSignal()[j] += learningRateOfNeighbor * (signal[j] - objNeighbor.getSignal()[j]);
			}
		}
		
		return true;
	}

	/**
	 * シグナル間の距離を計算する
	 * @param signal1 シグナル1
	 * @param signal2 シグナル2
	 * @return シグナル1とシグナル2との距離
	 */
	private double distance(final double[] signal1, final double[] signal2)
	{
		if ((signal1 == null) || (signal2 == null))
		{
			return 0.0;
		}
		
		double sum = 0.0;
		for (int i = 0; i < dimension; i++)
		{
			sum += (signal1[i] - signal2[i]) * (signal1[i] - signal2[i]); 
		}
		
		return Math.sqrt(sum) / (double)dimension;
	}
	
	/**
	 * ノード間の距離を計算する
	 * @param node1 ノード1
	 * @param node2 ノード2
	 * @return ノード1とノード2の距離
	 */
	private double distance(final int node1, final int node2)
	{	
		// 入力のチェック
		if (node1 == node2)
		{
			return 0.0;
		}
		if (!isExistNode(node1))
		{
			return 0.0;
		}
		if (!isExistNode(node2))
		{
			return 0.0;
		}
		
		return distance(nodeInfo.get(node1).getSignal(), nodeInfo.get(node2).getSignal());	
	}
	
	/**
	 * ノードの類似度閾値を計算する
	 * @param node ノード
	 * @return ノードの類似度閾値
	 */
	private double getSimilarityThreshold(final int node)
	{
		// 入力のチェック
		if (!isExistNode(node))
		{
			return 0.0;
		}
		
		// 近傍ノードの存在をチェック
		int neighborNum = nodeInfo.get(node).getNeighborNum();
		if (neighborNum > 0)
		// 近傍ノードが存在する場合
		{
			// 近傍ノードのうち、一番遠いものまでの距離を類似度閾値とする
			double maxDist = 0.0;
			for (int i = 0; i < neighborNum; i++)
			{	
				int neighbor = nodeInfo.get(node).getNeighbor()[i];
				double dist = distance(node, neighbor);
				if (maxDist < dist)
				{
					maxDist = dist;
				}
			}
			
			return maxDist;
		}
		else
		// 近傍ノードが存在する場合
		{	
			// 全ノードのうち、一番近いものまでの距離を類似度閾値とする
			double minDist = INFINITY;
			int nodeNum = (int)nodeInfo.size();
			for (int i = 0; i < nodeNum; i++)
			{
				if (i == node)
				{
					continue;
				}
					
				double dist = distance(node, i);
				if (minDist > dist)
				{
					minDist = dist;
				}
			}
			
			return minDist;
		}
	}

	/**
	 * シグナルを重みとして持つノードを追加する
	 * @param signal シグナル
	 * @return true
	 */
	private boolean addNode(final double[] signal)
	{	
		Node newNode = new Node(signal);
		nodeInfo.add(newNode);

		return true;	
	}
	
	/**
	 * ノードを削除する
	 * @param node ノード
	 * @return ノードを削除できたら true、そうでなければ false
	 */
	private boolean removeNode(final int node)
	{
		if (!isExistNode(node))
		{
			return false;
		}
		
		// ノードに接続しているエッジを削除する
		Node objNode = nodeInfo.get(node); 
		while (objNode.getNeighborNum() > 0)
		{
			int neighbor = objNode.getNeighbor()[0];
			removeEdge(node, neighbor);
		}
		
		// 削除対象のノードの位置に最後尾のノードを挿入し、
		// リストの最後尾ノードを削除する
		int nodeNum = (int)nodeInfo.size();
		int edgeNum = (int)edgeInfo.size();
		int lastNode = ((int)nodeInfo.size() - 1);
		if (node < lastNode)
		{
			Node objLastNode = nodeInfo.get(lastNode);
			nodeInfo.set(node, objLastNode);// 削除対象のノードの位置に最後尾のノードを挿入する
			
			// インデックスが lastNode であったノードのインデックスが node に変更されているため、
			// 全ノードの近傍ノードとエッジを更新する
			for (int i = 0; i < nodeNum; i++)
			{
				nodeInfo.get(i).replaceNeighbor(lastNode, node);
			}
			for (int i = 0; i < edgeNum; i++)
			{
				edgeInfo.get(i).replace(lastNode, node);
			}	
		}
		nodeInfo.remove(lastNode);// 最後尾のノードを削除する
		
		return true;
	}

	/**
	 * ノードが存在するか調べる
	 * @param node ノード
	 * @return 存在すれば true、そうでなければ false
	 */
	private boolean isExistNode(final int node)
	{
		if (node < 0)
		{
			return false;
		}
		if (node >= (int)nodeInfo.size())
		{
			return false;
		}
		return true;
	}
	
	/**
	 * ノード間にエッジを生成する
	 * @param node1 ノード1
	 * @param node2 ノード2
	 * @return エッジを生成できたら true、そうでなければ false
	 */
	private boolean addEdge(final int node1, final int node2)
	{
		// 入力のチェック
		if (node1 == node2)
		{
			return false;
		}
		if (!isExistNode(node1))
		{
			return false;
		}
		if (!isExistNode(node2))
		{
			return false;
		}
		if (isExistEdge(node1, node2))
		{
			return false;
		}
		
		// エッジの生成
		Edge newEdge = new Edge(node1, node2);
		edgeInfo.add(newEdge);
		nodeInfo.get(node1).addNeighbor(node2);
		nodeInfo.get(node2).addNeighbor(node1);
		
		return true;
	}
	
	/**
	 * エッジを削除する
	 * @param edge エッジ
	 * @return エッジを削除できたら true、そうでなければ false
	 */
	private boolean removeEdge(final int edge)
	{
		// 入力のチェック
		if (!isExistEdge(edge))
		{
			return false;
		}
		
		int f = edgeInfo.get(edge).getFrom();
		int t = edgeInfo.get(edge).getTo();
		
		nodeInfo.get(t).deleteNeighbor(f);
		nodeInfo.get(f).deleteNeighbor(t);
		
		// 削除対象のエッジの位置に最後尾のエッジを挿入し、
		// リストの最後尾エッジを削除する
		int lastEdge = ((int)edgeInfo.size() - 1);
		if (edge < lastEdge)
		{
			Edge objLastEdge = edgeInfo.get(lastEdge);
			edgeInfo.set(edge, objLastEdge);// 削除対象のエッジの位置に最後尾のエッジを挿入する	
		}
		edgeInfo.remove(lastEdge);// 最後尾のエッジを削除する
		
		return true;
	}
	
	/**
	 * ノード間のエッジを削除する
	 * @param node1 ノード1
	 * @param node2 ノード2
	 * @return エッジを削除したら true
	 */
	private boolean removeEdge(final int node1, final int node2)
	{
		// 入力のチェック
		if (node1 == node2)
		{
			return false;
		}
		if (!isExistNode(node1))
		{
			return false;
		}
		if (!isExistNode(node2))
		{
			return false;
		}
		
		// エッジの削除
		int edge = findEdge(node1, node2);
		if (edge == NOT_FOUND)
		{
			return false;
		}
		
		return removeEdge(edge);
	}
	
	/**
	 * エッジが存在するか調べる
	 * @param edge エッジ
	 * @return エッジが存在すれば true、そうでなければ false
	 */
	private boolean isExistEdge(final int edge)
	{
		if ((edge < 0) || (edgeInfo.size() <= edge))
		{
			return false;
		}
		
		return true;
	}

	/**
	 * ノード間にエッジが存在するか調べる
	 * @param node1 ノード1
	 * @param node2 ノード2
	 * @return エッジが存在すれば true、そうでなければ false
	 */
	private boolean isExistEdge(final int node1, final int node2)
	{
		// 入力のチェック
		if (node1 == node2)
		{
			return false;
		}
		if (!isExistNode(node1))
		{
			return false;
		}
		if (!isExistNode(node2))
		{
			return false;
		}
		
		// エッジの探索
		int edgeNum = (int)edgeInfo.size();
		for (int i = 0; i < edgeNum; i++)
		{
			int f = edgeInfo.get(i).getFrom();
			int t = edgeInfo.get(i).getTo();
			if (((f == node1) && (t == node2)) ||
					((t == node1) && (f == node2)))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * ノード間のエッジを探索する
	 * @param node1 ノード1
	 * @param node2 ノード2
	 * @return エッジが存在すればその ID、存在しなければ NOT_FOUND 
	 */
	private int findEdge(final int node1, final int node2)
	{
		// 入力のチェック
		if (node1 == node2)
		{
			return NOT_FOUND;
		}
		if (!isExistNode(node1))
		{
			return NOT_FOUND;
		}
		if (!isExistNode(node2))
		{
			return NOT_FOUND;
		}

		// エッジの探索
		int edgeNum = (int)edgeInfo.size();
		for (int i = 0; i < edgeNum; i++)
		{
			int f = edgeInfo.get(i).getFrom();
			int t = edgeInfo.get(i).getTo();
			if (((f == node1) && (t == node2)) ||
					((t == node1) && (f == node2)))
			{
				return i;
			}
		}
		
		return NOT_FOUND;
	}
	
	/**
	 * ノードにクラス ID を割り当てる<br>
	 * さらにそのノードに接続しているノードにも再帰的にクラス ID を割り当てる
	 * @param node ノード
	 * @param classID クラス ID
	 * @return クラスを割り当てられたら true、そうでなければ false
	 */
	private boolean setClassID(final int node, final int classID)
	{
		// 入力のチェック
		if (!isExistNode(node))
		{
			return false;
		}
		if (nodeInfo.get(node).getClassID() != UNCLASSIFIED)
		{
			return false;
		}
		
		// ノードにクラス ID を割り当てる
		nodeInfo.get(node).setClassID(classID);
		
		// 近傍ノードにクラス ID を割り当てる
		int neighborNum = nodeInfo.get(node).getNeighborNum();
		for (int i = 0; i < neighborNum; i++)
		{
			int neighbor = nodeInfo.get(node).getNeighbor()[i];
			if (nodeInfo.get(neighbor).getClassID() == UNCLASSIFIED)
			{
				setClassID(neighbor, classID);
			}
		}
		
		return true;
	}
}
