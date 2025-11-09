(ns prefix-tree.generators
  (:require [clojure.test.check.generators :as gen]
            [prefix-tree.core :as tree]))

(def gen-word
  "Генератор случайных слов (строк из букв)"
  (gen/fmap (fn [chars] (apply str chars))
            (gen/vector (gen/elements (map char (range 97 123))) 1 10)))

(def gen-word-list
  "Генератор списка слов"
  (gen/vector gen-word 0 20))

(def gen-tree
  "Генератор префиксного дерева"
  (gen/fmap (fn [words]
              (reduce tree/add tree/empty-tree words))
            gen-word-list))

(def gen-non-empty-tree
  "Генератор непустого префиксного дерева"
  (gen/fmap (fn [words]
              (reduce tree/add tree/empty-tree words))
            (gen/not-empty gen-word-list)))

(def gen-tree-and-word
  "Генератор пары [дерево, слово], где слово может быть в дереве или нет"
  (gen/bind gen-tree
            (fn [t]
              (gen/tuple (gen/return t) gen-word))))

(def gen-tree-and-existing-word
  "Генератор пары [дерево, слово], где слово гарантированно есть в дереве"
  (gen/bind gen-non-empty-tree
            (fn [t]
              (let [words (tree/tree->seq t)]
                (gen/tuple (gen/return t)
                           (gen/elements words))))))