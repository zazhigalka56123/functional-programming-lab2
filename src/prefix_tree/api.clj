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

  (tree-merge [this other]
    "Объединяет два дерева (моноид)")

  (tree-equal? [this other]
    "Проверяет равенство двух деревьев"))

(defn empty-tree
  "Создаёт пустое префиксное дерево"
  []
  (throw (ex-info "empty-tree должна быть реализована в конкретной имплементации" {})))