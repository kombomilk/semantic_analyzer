(ns semantic-analyzer.statistics)

(def wordsCount (ref 0))
(comment def sentencesCount (ref 0))

(defn printStatistics []
  "Выводит на консоль статистику"
  (println "Слов :" @wordsCount)
  (comment println "Предложений :" @sentencesCount))

(defn updateWordsCount [number]
  "Обновляет количество слов"
  (dosync (ref-set wordsCount number)))

(comment defn updateSentencesCount [number]
  (dosync (ref-set sentencesCount number)))

(defn updateScore [words]
  "Обновляет данные по вектору слов очередной строки"
  (let [toAdd (count words)]
    (updateWordsCount (+ @wordsCount toAdd))))

(defn resetStatistics []
  "Обновляет статистику текста"
  (updateWordsCount 0))

(defn getWordsCount []
  "Возвращает количество слов"
  @wordsCount)