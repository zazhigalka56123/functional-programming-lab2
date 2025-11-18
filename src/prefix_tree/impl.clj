(ns prefix-tree.impl
  (:require [prefix-tree.api :as api]))

(declare remove-recursive tree->seq-recursive mappend tree-equal?)

(deftype PrefixTree [data]
  api/IPrefixTree

  (tree-add [_ word]
    (PrefixTree. (assoc-in data (conj (vec word) :end?) true)))

  (tree-contains? [_ word]
    (boolean (get-in data (conj (vec word) :end?))))

  (tree-remove [this word]
    (if-not (api/tree-contains? this word)
      this
      (PrefixTree. (or (remove-recursive data word) {}))))

  (tree-to-seq [_]
    (tree->seq-recursive data []))

  (tree-merge [_ other]
    (PrefixTree. (mappend data (.data other))))

  (tree-equal? [_ other]
    (tree-equal? data (.data other))) 
 
  clojure.lang.Seqable
  (seq [this]
    (seq (api/tree-to-seq this)))
  
  clojure.lang.Counted
  (count [this]
    (clojure.core/count (api/tree-to-seq this)))
  
  clojure.lang.IPersistentCollection
  (cons [this word]
    (api/tree-add this word))
  
  (empty [_]
    (PrefixTree. {}))
  
  (equiv [_ other]
    (and (instance? PrefixTree other)
         (tree-equal? data (.data other))))
  
  clojure.lang.ILookup
  (valAt [this word]
    (when (api/tree-contains? this word) true))
  
  (valAt [this word not-found]
    (if (api/tree-contains? this word) true not-found))
  
  clojure.lang.Associative
  (containsKey [this word]
    (api/tree-contains? this word))
  
  (entryAt [this word]
    (when (api/tree-contains? this word)
      (clojure.lang.MapEntry/create word true)))
  
  (assoc [this word _]
    (api/tree-add this word))
  
  clojure.lang.IFn
  (invoke [this word]
    (api/tree-contains? this word)))

(defn- remove-recursive
  "Рекурсивно удаляет слово и очищает пустые узлы"
  [node [ch & rest-word]]
  (if-not ch
    (let [new-node (dissoc node :end?)]
      (when (not-empty new-node)
        new-node))
    (let [child-node (get node ch)]
      (when child-node
        (let [new-child (remove-recursive child-node rest-word)]
          (cond
            (nil? new-child)
            (let [new-node (dissoc node ch)]
              (when (or (:end? new-node) (not-empty (dissoc new-node :end?)))
                new-node))

            (not= new-child child-node)
            (assoc node ch new-child)

            :else node))))))

(defn- tree->seq-recursive
  "Рекурсивно строит последовательность слов из дерева"
  [node prefix]
  (lazy-cat
   (if (:end? node)
     [(apply str (reverse prefix))]
     [])
   (mapcat (fn [[ch child-node]]
             (tree->seq-recursive child-node (cons ch prefix)))
           (sort-by key (dissoc node :end?)))))

(defn- mappend
  "Объединяет два префиксных дерева (моноид)"
  [tree1 tree2]
  (let [sub-trees1 (dissoc tree1 :end?)
        sub-trees2 (dissoc tree2 :end?)
        merged-trees (merge-with mappend sub-trees1 sub-trees2)
        end? (or (:end? tree1) (:end? tree2))]
    (if end?
      (assoc merged-trees :end? true)
      merged-trees)))

(defn- tree-equal?
  "Эффективно проверяет равенство двух префиксных деревьев"
  [tree1 tree2]
  (if (not= (:end? tree1) (:end? tree2))
    false
    (let [keys1 (set (keys (dissoc tree1 :end?)))
          keys2 (set (keys (dissoc tree2 :end?)))]
      (if (not= keys1 keys2)
        false
        (every? (fn [k] (tree-equal? (get tree1 k) (get tree2 k))) keys1)))))

(defn create-empty-tree
  "Создаёт пустое префиксное дерево"
  []
  (PrefixTree. {}))