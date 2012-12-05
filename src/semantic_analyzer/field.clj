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


(defn updateField [code number]
  "Обновляет значение мапа"
  (dosync
   (ref-set score
	    (assoc @score code number))))

(defn resetStatistics []
  "Сбрасывает статистику по предполагаемым областям текста"
  (doseq [code (keys STEMS)]
    (updateField code 0)))

(defn printStatistics []
  "Выводит на консоль статистику по областям"
  (doseq [code (keys STEMS)]
    (println (code FIELDS) ":" (code @score))))

(defn updateScore [words]
  "Обновляет данные согласно вектору слов очередной строки"
  (doseq [code (keys STEMS)]
    (updateField code
		 (+ (code @score)
		    (count
		     (clojure.set/intersection (set words)
					       (set (code STEMS))))))))

(defn getField []
  "Возвращает предполагаемую область текста по собранным данным"
  ((first (apply max-key second @score))
   FIELDS))