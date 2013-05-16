(ns semantic-analyzer.core
  (:gen-class)
  (:require [semantic-analyzer.parser :as parser]))

(def ERROR
     (str "Error when reading file.\n"
	  "Perhaps, there are wrong arguments for this command."))

(defn -main
  "Main function which parses a file and prints data about it"
  [& args]
  (try 
    (parser/parseFile (first args))
  (catch Exception e
    (println ERROR))))
