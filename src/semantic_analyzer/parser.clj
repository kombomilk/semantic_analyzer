(ns semantic-analyzer.core
  (:use [clojure.java.io])
  (import [java.io FileNotFoundException]))

(def NO_FILE_MESSAGE "No such file exists")

(defn- processLine [line]
  "Processes one line"
  (println line))

(defn parseFile [filename]
  "Parses the file line by line"
  (try 
    (with-open [rdr (reader filename)]
      (doseq [line (line-seq rdr)]
	(processLine line)))
    (catch FileNotFoundException e
      (println NO_FILE_MESSAGE))))

