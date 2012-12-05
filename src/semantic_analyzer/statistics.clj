(ns semantic-analyzer.statistics)

(def wordsCount (ref 0))
(comment def sentencesCount (ref 0))

(defn printStatistics []
  (println "Слов :" @wordsCount)
  (comment println "Предложений :" @sentencesCount))

(defn updateWordsCount [number]
  (dosync (ref-set wordsCount number)))

(comment defn updateSentencesCount [number]
  (dosync (ref-set sentencesCount number)))

(defn updateScore [words]
  (let [toAdd (count words)]
    (updateWordsCount (+ @wordsCount toAdd))))

(defn resetStatistics []
  (updateWordsCount 0))

(defn getWordsCount []
  @wordsCount)