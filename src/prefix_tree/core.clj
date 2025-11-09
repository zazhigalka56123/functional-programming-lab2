(ns prefix-tree.core
  (:require [prefix-tree.api :as api]
            [prefix-tree.impl :as impl]))

(def empty-tree
  "Пустое префиксное дерево"
  (impl/create-empty-tree))

(defn add
  "Добавляет слово в префиксное дерево"
  [tree word]
  (api/tree-add tree word))

(defn tree-contains?
  "Проверяет наличие слова в префиксном дереве"
  [tree word]
  (api/tree-contains? tree word))

(defn tree-remove
  "Удаляет слово из префиксного дерева"
  [tree word]
  (api/tree-remove tree word))

(defn tree->seq
  "Возвращает ленивую последовательность всех слов в дереве"
  [tree]
  (api/tree-to-seq tree))

(defn map-tree
  "Применяет функцию f к каждому слову в дереве"
  [f tree]
  (api/tree-map tree f))

(defn filter-tree
  "Фильтрует слова в дереве по предикату p"
  [p tree]
  (api/tree-filter tree p))

(defn reduce-left
  "Свёртка дерева слева направо (лексикографически)"
  ([f tree]
   (api/tree-reduce-left tree f))
  ([f init tree]
   (api/tree-reduce-left tree f init)))

(defn reduce-right
  "Свёртка дерева справа налево (обратный лексикографический порядок)"
  ([f tree]
   (api/tree-reduce-right tree f))
  ([f init tree]
   (api/tree-reduce-right tree f init)))

(defn mappend
  "Объединяет два префиксных дерева (моноид)"
  [tree1 tree2]
  (api/tree-merge tree1 tree2))

(defn tree-equal?
  "Проверяет равенство двух префиксных деревьев"
  [tree1 tree2]
  (api/tree-equal? tree1 tree2))
