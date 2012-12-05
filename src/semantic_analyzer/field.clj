(ns semantic-analyzer.field
  (:require [clojure.set]))

(def FIELDS
     {:art "Искусство"
      :sport "Спорт"
      :politics "Политика"
      :economics "Экономика"
      :science "Наука"
      :education "Образование"
      :unknown "Неизвестно"})

(def STEMS
     {:art
      ["искусств" "художник" "художестве" "образ"]
      :sport
      ["спорт" "спортивн" "спортсме" "спортсменк"]
      :politics
      ["политик" "политическ"]
      :economics
      ["экономик" "экономичн" "экономк"]
      :science
      ["наук" "научн"]
      :education
      ["образован" "воспитан" "образовательн"]})

(def score (ref
	    {
	     :art 0
	     :sport 0
	     :politics 0
	     :economics 0
	     :science 0
	     :education 0
	     :unknown 0.5}))

(defn resetStatistics []
  (doseq [code (keys STEMS)]
    (updateField code 0)))

(defn printStatistics []
  (doseq [code (keys STEMS)]
    (println (code FIELDS) ":" (code @score))))

(defn updateField [code number]
  (dosync
   (ref-set score
	    (assoc @score code number))))

(defn updateScore [words]
  (doseq [code (keys STEMS)]
    (updateField code
		 (+ (code @score)
		    (count
		     (clojure.set/intersection (set words)
					       (set (code STEMS))))))))

(defn getField []
  ((first (apply max-key second @score))
   FIELDS))