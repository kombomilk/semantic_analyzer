(ns semantic-analyzer.core
  (:use [clojure.java.io])
  (import [java.io FileNotFoundException]))

(def NO_FILE_MESSAGE "\nNo such file exists")

;; parsing a line
(defn removePunctuation [line]
  (filter
   #(not (empty? %))
   (clojure.string/split line
			 #"[^а-яА-Яa-zA-Z0-9]")))

(defn normalize [line]
  (let [vec (removePunctuation line)]
    (map #(clojure.string/lower-case %)
	 vec)))

(defn- processLine [line]
  "Processes one line"
  (println (normalize line)))

(defn parseFile [filename]
  "Parses the file line by line"
  (try 
    (with-open [rdr (reader filename)]
      (doseq [line (line-seq rdr)]
	(processLine line)))
    (catch FileNotFoundException e
      (println NO_FILE_MESSAGE))))