#!/usr/local/bin/python
# -*- coding: utf-8 -*-

import random
def near(vector, center_vectors):
    """
    vectorに対し、尤も近いラベルを返す
    :param vector:
    :param center_vectors:
    :return:
    """
    euclid_dist = lambda vector1, vector2: (sum([(vec[0]-vec[1])**2 for vec in list(zip(vector1, vector2))]))**0.5
    d = [euclid_dist(vector, center_vector) for center_vector in center_vectors]
    return d.index(min(d))


def clustering(vectors, label_count, learning_count_max=1000):
    """
    K-meansを行い、各ラベルの重心を返す
    :param vectors:
    :param label_count:
    :param learning_count_max:
    :return:
    """
    #各vectorに割り当てられたクラスタラベルを保持するvector
    label_vector = [random.randint(0, label_count-1) for i in vectors]
    #一つ前のStepで割り当てられたラベル。終了条件の判定に使用
    old_label_vector = list()
    #各クラスタの重心vector
    center_vectors = [[0 for i in range(len(vectors[0]))] for label in range(label_count)]

    for step in range(learning_count_max):
        #各クラスタの重心vectorの作成
        for vec, label in zip(vectors, label_vector):
            center_vectors[label] = [c+v for c, v in zip(center_vectors[label], vec)]
        for i, center_vector in enumerate(center_vectors):
            center_vectors[i] = [v/label_vector.count(i) for v in center_vector]
        #各ベクトルのラベルの再割当て
        for i, vec in enumerate(vectors):
            label_vector[i] = near(vec, center_vectors)
        #前Stepと比較し、ラベルの割り当てに変化が無かったら終了
        if old_label_vector == label_vector:
            break
        #ラベルのベクトルを保持
        old_label_vector = [l for l in label_vector]
    return center_vectors

#分類対象のデータのリスト。各要素はfloatのリスト
vectors = [[random.random(), random.random()] for i in range(100)]
#分類対象のデータをクラスタ数3でクラスタリング
centers = clustering(vectors, 3)

#新しいデータ
vector = [0.5, 0.5]
label = near(vector, centers)
print label
print centers