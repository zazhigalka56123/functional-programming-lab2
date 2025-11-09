(ns prefix-tree.unit-test
  (:require [clojure.test :refer :all]
            [prefix-tree.core :refer :all]))

(deftest add-and-contains-test
  (testing "Добавление и проверка слов"
    (let [tree1 (add empty-tree "hello")
          tree2 (add tree1 "world")]
      (is (true? (tree-contains? tree1 "hello")))
      (is (false? (tree-contains? tree1 "world")))
      (is (true? (tree-contains? tree2 "hello")))
      (is (true? (tree-contains? tree2 "world")))
      (is (false? (tree-contains? tree2 "hell")))
      (is (false? (tree-contains? empty-tree "any"))))))

(deftest remove-test
  (testing "Удаление слов из дерева"
    (let [tree (-> empty-tree
                   (add "tea")
                   (add "ten")
                   (add "in")
                   (add "inn"))
          tree-after-ten (tree-remove tree "ten")]
      (is (false? (tree-contains? tree-after-ten "ten")))
      (is (true? (tree-contains? tree-after-ten "tea")))
      (is (tree-equal? tree (tree-remove tree "non-existent")))
      (let [tree-after-in (tree-remove tree "in")]
        (is (false? (tree-contains? tree-after-in "in")))
        (is (true? (tree-contains? tree-after-in "inn"))))
      (let [tree-after-all (-> tree
                               (tree-remove "tea")
                               (tree-remove "ten")
                               (tree-remove "in")
                               (tree-remove "inn"))]
        (is (tree-equal? empty-tree tree-after-all))))))

(deftest tree-seq-test
  (testing "Преобразование дерева в последовательность"
    (let [words #{"a" "to" "tea" "ted" "ten" "i" "in" "inn"}
          tree (reduce add empty-tree words)]
      (is (= words (set (tree->seq tree))))
      (is (empty? (tree->seq empty-tree))))))

(deftest map-filter-reduce-test
  (testing "Операции map, filter и reduce"
    (let [words ["a" "i" "in" "inn" "tea" "ted" "ten" "to"]
          tree (reduce add empty-tree words)]
      (testing "map-tree"
        (let [mapped-tree (map-tree clojure.string/upper-case tree)
              expected-words #{"A" "I" "IN" "INN" "TEA" "TED" "TEN" "TO"}]
          (is (= expected-words (set (tree->seq mapped-tree))))))
      (testing "filter-tree"
        (let [filtered-tree (filter-tree #(> (count %) 2) tree)
              expected-words #{"inn" "tea" "ted" "ten"}]
          (is (= expected-words (set (tree->seq filtered-tree))))))
      (testing "reduce-left"
        (is (= 18 (reduce-left (fn [acc v] (+ acc (count v))) 0 tree))))
      (testing "reduce-right"
        (is (= "to ten ted tea inn in i a" (clojure.string/join " " (reduce-right conj [] tree))))))))

(deftest monoid-and-equality-test
  (testing "Свойства моноида и равенство"
    (let [tree1 (reduce add empty-tree ["a" "b"])
          tree2 (reduce add empty-tree ["c" "d"])
          tree3 (reduce add empty-tree ["a" "b" "c" "d"])
          merged (mappend tree1 tree2)]
      (is (tree-equal? tree3 merged))
      (is (tree-equal? tree1 (mappend tree1 empty-tree)))
      (is (tree-equal? tree1 (mappend empty-tree tree1)))
      (is (tree-equal? (add empty-tree "a") (add empty-tree "a")))
      (is (not (tree-equal? tree1 tree2))))))