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
(defn removePunctuation [line]
  (filter
   #(not (empty? %))
   (clojure.string/split line
			 #"[^а-яА-Яa-zA-Z0-9]")))

(defn normalize [line]
  (let [v (removePunctuation line)]
    (map #(-> %
	      clojure.string/lower-case
	      stemmer/run)
	 v)))

(def EXTRA_MARK "%%%")

(defn- processLine [line]
  "Processes one line"
  (if-not (.startsWith line EXTRA_MARK)
    (let [words (normalize line)]
      (field/updateScore words)
      (style/updateScore words)
      (gender/updateScore words)
      (stat/updateScore words))))

(defn resetStatistics []
  (field/resetStatistics)
  (style/resetStatistics)
  (gender/resetStatistics)
  (stat/resetStatistics))

(defn parseFile [filename]
  "Parses the file line by line"
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