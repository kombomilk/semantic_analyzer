(ns semantic-analyzer.gender
  (:require [semantic-analyzer.statistics :as stat]))

(def GENDERS {:m "Мужчина"
	      :f "Женщина"})

(def PRONOUNS
     ["я" "ты" "он" "она" "мы" "вы" "они"
      "меня" "тебя" "его" "ее" "нас" "вас" "их"
      "мне" "тебе" "ему" "ей" "нам" "вам" "им"
      "мной" "тобой" "ею" "нами" "вами" "ими"
      "нем" "ней"])

(def pronounsCount (ref 0))

(defn printStatistics []
  "Выводит на консоль статистику по местоимениям"
  (println "Местоимений :"
	   @pronounsCount
	   "/"
	   (stat/getWordsCount)
	   " = "
	   (/ @pronounsCount (stat/getWordsCount) 0.01) "%"))

(defn updateValue [number]
  "Обновляет количество местоимений"
  (dosync (ref-set pronounsCount number)))

(defn addPronouns [number]
  "Увеличивает количество местоимений на заданное число"
  (updateValue (+ number @pronounsCount)))

(defn updateScore [words]
  "Обновляет количесво местоимений по данному вектору слов"
  (let [toAdd (count
	       (clojure.set/intersection (set words)
					 (set PRONOUNS)))]
    (addPronouns toAdd)))

(defn resetStatistics []
  "Обновляет статистику"
  (updateValue 0))

(def FEMALE_PRONOUN_CONSTANT 5.5)

(defn getGender []
  "Возвращает предполагаемый пол автора"
  (if (> (/ @pronounsCount (stat/getWordsCount) 0.01)
	 FEMALE_PRONOUN_CONSTANT)
    (:f GENDERS)
    (:m GENDERS)))