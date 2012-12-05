(ns semantic-analyzer.core
  (:gen-class)
  (:require [semantic-analyzer.parser :as parser]))

(def ERROR
     (str "Ошибка при чтении файла.\n"
	  "Возможно, заданы неверные параметры."))

(defn -main
  "I don't do a whole lot."
  [& args]
  (try 
    (parser/parseFile (first args))
  (catch Exception e
    (println ERROR))))
