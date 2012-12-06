(ns semantic-analyzer.parser
  (:use [clojure.java.io])
  (:require [semantic-analyzer.stemmer :as stemmer]
	    [semantic-analyzer.field :as field]
	    [semantic-analyzer.style :as style]
	    [semantic-analyzer.gender :as gender]
	    [semantic-analyzer.statistics :as stat])
  (import [java.io FileNotFoundException]))

(def NO_FILE_MESSAGE "\nТакого файла не существует")

;; parsing a line
(defn removePunctuation
  "Splits line by words and removes punctuation"
  [line]
  (filter
   #(not (empty? %))
   (clojure.string/split line
			 #"[^а-яА-Яa-zA-Z0-9]")))

(defn normalize
  "Returns normalized vector of stems"
  [line]
  (let [v (removePunctuation line)]
    (map #(-> %
	      clojure.string/lower-case
	      stemmer/run)
	 v)))

(def EXTRA_MARK "%%%")

(defn- processLine
  "Processes one line of text"
  [line]
  (if-not (.startsWith line EXTRA_MARK)
    (let [words (normalize line)]
      (field/updateScore words)
      (style/updateScore words)
      (gender/updateScore words)
      (stat/updateScore words))))

(defn resetStatistics
  "Resets statistics before new file processing"
  []
  (field/resetStatistics)
  (style/resetStatistics)
  (gender/resetStatistics)
  (stat/resetStatistics))

(defn parseFile
  "Parses file line by line"
  [filename]
  (resetStatistics)
  (try 
    (with-open [rdr (reader filename)]
      (doseq [line (line-seq rdr)]
	(processLine line))
      (println "Тема:" (field/getField))
      (println "Автор:" (gender/getGender))
      (println "Жанр:" (style/getStyle))
      (stat/printStatistics)
      (println)
      (field/printStatistics)
      (println)
      (style/printStatistics)
      (println)
      (gender/printStatistics))
    (catch FileNotFoundException e
      (println NO_FILE_MESSAGE))))