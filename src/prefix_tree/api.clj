(ns prefix-tree.api)

(defprotocol IPrefixTree
  
  (tree-add [this word]
    "Добавляет слово в дерево")
  
  (tree-contains? [this word]
    "Проверяет наличие слова в дереве")
  
  (tree-remove [this word]
    "Удаляет слово из дерева")
  
  (tree-to-seq [this]
    "Возвращает ленивую последовательность всех слов")
  
  (tree-map [this f]
    "Применяет функцию f к каждому слову")
  
  (tree-filter [this pred]
    "Фильтрует слова по предикату pred")
  
  (tree-reduce-left [this f] [this f init]
    "Свёртка слева направо")
  
  (tree-reduce-right [this f] [this f init]
    "Свёртка справа налево")
  
  (tree-merge [this other]
    "Объединяет два дерева (моноид)")
  
  (tree-equal? [this other]
    "Проверяет равенство двух деревьев"))

(defn empty-tree
  "Создаёт пустое префиксное дерево"
  []
  (throw (ex-info "empty-tree должна быть реализована в конкретной имплементации" {})))