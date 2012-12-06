(ns semantic-analyzer.gender
  (:gen-class)
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
  "Prints statistics about pronouns"
  (println "Местоимений :"
	   @pronounsCount
	   "/"
	   (stat/getWordsCount)
	   " = "
	   (/ @pronounsCount (stat/getWordsCount) 0.01) "%"))

(defn updateValue [number]
  "Updates number of pronouns"
  (dosync (ref-set pronounsCount number)))

(defn addPronouns [number]
  "Increases number of pronouns with a given number"
  (updateValue (+ number @pronounsCount)))

(defn updateScore [words]
  "Updates data according to the given words input vector"
  (let [toAdd (count
	       (clojure.set/intersection (set words)
					 (set PRONOUNS)))]
    (addPronouns toAdd)))

(defn resetStatistics
  "Resets statistics"
  []
  (updateValue 0))

(def FEMALE_PRONOUN_CONSTANT 5.5)

(defn getGender
  "Returns suggested text author's gender"
  []
  (if (> (/ @pronounsCount (stat/getWordsCount) 0.01)
	 FEMALE_PRONOUN_CONSTANT)
    (:f GENDERS)
    (:m GENDERS)))