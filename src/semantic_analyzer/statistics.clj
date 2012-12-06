(ns semantic-analyzer.statistics)

(def wordsCount (ref 0))

(defn printStatistics
  "Prints statistics to the console"
  []
  (println "Слов :" @wordsCount)
  (comment println "Предложений :" @sentencesCount))

(defn updateWordsCount
  "Updates words count"
  [number]
  (dosync (ref-set wordsCount number)))

(defn updateScore
  "Updates data according to the input words vector"
  [words]
  (let [toAdd (count words)]
    (updateWordsCount (+ @wordsCount toAdd))))

(defn resetStatistics
  "Resets text statistics"
  []
  (updateWordsCount 0))

(defn getWordsCount
  "Returns words number"
  []
  @wordsCount)