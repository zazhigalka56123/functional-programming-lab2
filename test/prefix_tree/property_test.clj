(ns prefix-tree.property-test
  (:require [clojure.string :as str]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.clojure-test :refer [defspec]]
            [prefix-tree.core :as tree]
            [prefix-tree.generators :as tree-gen]))

;; Свойства моноида
(defspec monoid-identity 100
  (prop/for-all [words tree-gen/gen-word-list]
                (let [t (reduce tree/add tree/empty-tree words)
                      e tree/empty-tree]
                  (and (tree/tree-equal? (tree/mappend t e) t)
                       (tree/tree-equal? (tree/mappend e t) t)))))

(defspec monoid-associativity 100
  (prop/for-all [words1 tree-gen/gen-word-list
                 words2 tree-gen/gen-word-list
                 words3 tree-gen/gen-word-list]
                (let [t1 (reduce tree/add tree/empty-tree words1)
                      t2 (reduce tree/add tree/empty-tree words2)
                      t3 (reduce tree/add tree/empty-tree words3)]
                  (tree/tree-equal? (tree/mappend t1 (tree/mappend t2 t3))
                                    (tree/mappend (tree/mappend t1 t2) t3)))))

;; Добавление и удаление
(defspec add-contains 100
  (prop/for-all [t tree-gen/gen-tree
                 word tree-gen/gen-word]
                (let [t' (tree/add t word)]
                  (tree/tree-contains? t' word))))

(defspec remove-not-contains 100
  (prop/for-all [[t word] tree-gen/gen-tree-and-existing-word]
                (let [t' (tree/tree-remove t word)]
                  (not (tree/tree-contains? t' word)))))

(defspec add-idempotent 100
  (prop/for-all [t tree-gen/gen-tree
                 word tree-gen/gen-word]
                (let [t1 (tree/add t word)
                      t2 (tree/add t1 word)]
                  (tree/tree-equal? t1 t2))))

(defspec remove-non-existing 100
  (prop/for-all [t tree-gen/gen-tree
                 word tree-gen/gen-word]
                (if (tree/tree-contains? t word)
                  true
                  (tree/tree-equal? (tree/tree-remove t word) t))))

;; Преобразование в последовательность
(defspec seq-contains-all-words 100
  (prop/for-all [words tree-gen/gen-word-list]
                (let [t (reduce tree/add tree/empty-tree words)
                      seq-words (set (tree/tree->seq t))
                      original-words (set words)]
                  (= seq-words original-words))))

(defspec seq-sorted 100
  (prop/for-all [words tree-gen/gen-word-list]
                (let [t (reduce tree/add tree/empty-tree words)
                      seq-words (tree/tree->seq t)]
                  (= seq-words (sort seq-words)))))

(defspec empty-tree-empty-seq 100
  (prop/for-all [_ (gen/return nil)]
                (empty? (tree/tree->seq tree/empty-tree))))

;; Map и filter через стандартные протоколы
(defspec map-preserves-size 100
  (prop/for-all [words tree-gen/gen-word-list]
                (let [t (reduce tree/add tree/empty-tree words)
                      mapped-words (map str/upper-case t)]
                  (= (count (tree/tree->seq t))
                     (count mapped-words)))))

(defspec filter-reduces-or-preserves-size 100
  (prop/for-all [words tree-gen/gen-word-list]
                (let [t (reduce tree/add tree/empty-tree words)
                      filtered-words (filter #(> (count %) 3) t)]
                  (<= (count filtered-words)
                      (count (tree/tree->seq t))))))

(defspec filter-predicate-holds 100
  (prop/for-all [words tree-gen/gen-word-list]
                (let [pred #(> (count %) 2)
                      t (reduce tree/add tree/empty-tree words)
                      filtered-words (filter pred t)]
                  (every? pred filtered-words))))

;; Свёртки через стандартный reduce
(defspec reduce-works-correctly 100
  (prop/for-all [words tree-gen/gen-word-list]
                (let [t (reduce tree/add tree/empty-tree words)
                      f (fn [acc w] (+ acc (count w)))
                      result1 (reduce f 0 t)
                      result2 (reduce f 0 (tree/tree->seq t))]
                  (= result1 result2))))

(defspec reduce-with-reverse 100
  (prop/for-all [words tree-gen/gen-word-list]
                (let [t (reduce tree/add tree/empty-tree words)
                      result (reduce conj [] (reverse (seq t)))
                      expected (reverse (tree/tree->seq t))]
                  (= result expected))))

;; Равенство
(defspec equality-reflexive 100
  (prop/for-all [t tree-gen/gen-tree]
                (tree/tree-equal? t t)))

(defspec equality-symmetric 100
  (prop/for-all [words tree-gen/gen-word-list]
                (let [t1 (reduce tree/add tree/empty-tree words)
                      t2 (reduce tree/add tree/empty-tree words)]
                  (and (tree/tree-equal? t1 t2)
                       (tree/tree-equal? t2 t1)))))

(defspec equality-from-same-words 100
  (prop/for-all [words tree-gen/gen-word-list]
                (let [t1 (reduce tree/add tree/empty-tree words)
                      t2 (reduce tree/add tree/empty-tree (shuffle words))]
                  (tree/tree-equal? t1 t2))))

;; Merge
(defspec merge-combines-words 100
  (prop/for-all [words1 tree-gen/gen-word-list
                 words2 tree-gen/gen-word-list]
                (let [t1 (reduce tree/add tree/empty-tree words1)
                      t2 (reduce tree/add tree/empty-tree words2)
                      merged (tree/mappend t1 t2)
                      expected-words (set (concat words1 words2))]
                  (= (set (tree/tree->seq merged)) expected-words))))

(defspec merge-commutative-for-sets 100
  (prop/for-all [words1 tree-gen/gen-word-list
                 words2 tree-gen/gen-word-list]
                (let [t1 (reduce tree/add tree/empty-tree words1)
                      t2 (reduce tree/add tree/empty-tree words2)
                      m1 (tree/mappend t1 t2)
                      m2 (tree/mappend t2 t1)]
                  (tree/tree-equal? m1 m2))))

;; Counted: count согласован с количеством элементов в seq
(defspec count-equals-seq-size 100
  (prop/for-all [words tree-gen/gen-word-list]
                (let [t (reduce tree/add tree/empty-tree words)]
                  (= (count t) (count (seq t))))))

;; ILookup и Associative: get/contains? согласованы с tree-contains?
(defspec lookup-consistent-with-contains 100
  (prop/for-all [t tree-gen/gen-tree
                 word tree-gen/gen-word]
                (let [contains-result (tree/tree-contains? t word)
                      get-result (get t word)
                      contains-key-result (contains? t word)]
                  (and (= contains-result contains-key-result)
                       (if contains-result
                         (true? get-result)
                         (nil? get-result))))))

;; Associative: assoc эквивалентен add
(defspec assoc-equivalent-to-add 100
  (prop/for-all [t tree-gen/gen-tree
                 word tree-gen/gen-word]
                (let [t-assoc (assoc t word true)
                      t-add (tree/add t word)]
                  (tree/tree-equal? t-assoc t-add))))