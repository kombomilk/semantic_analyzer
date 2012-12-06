(ns semantic-analyzer.core
  (:gen-class)
  (:require [semantic-analyzer.parser :as parser]))

(def ERROR
     (str "Ошибка при чтении файла.\n"
	  "Возможно, заданы неверные параметры."))

(defn -main
  "Main function which parses a file and prints data about it"
  [& args]
  (try 
    (parser/parseFile (first args))
  (catch Exception e
    (println ERROR))))
