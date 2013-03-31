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
      "нем" "ней"
      "себя" "сам" "себе" "собой"
      "кто" "кого" "кому" "кем"
      "что" "чего" "чему" "чем"
      "такой" "такому" "такого" "таким"
      "тот" "того" "тому" "тем" "это"
      "этот" "этого" "этому" "этим"
      "такой" "такого" "такому" "таким"])

(def NEGATIVES
     ["не" "нет" "ни" "ничуть" "никак" "нельзя"])

(def pronounsCount (ref 0))
(def negativesCount (ref 0))

(defn printStatistics []
  "Prints statistics about pronouns"
  (println "Местоимений :"
	   @pronounsCount
	   "/"
	   (stat/getWordsCount)
	   " = "
	   (/ @pronounsCount (stat/getWordsCount) 0.01) "%")
  (println "Отрицательных слов :"
	   @negativesCount
	   "/"
	   (stat/getWordsCount)
	   " = "
	   (/ @negativesCount (stat/getWordsCount) 0.01) "%"))

(defn updatePronounsValue [number]
  "Updates number of pronouns"
  (dosync (ref-set pronounsCount number)))

(defn updateNegativesValue [number]
  "Updates number of negatives"
  (dosync (ref-set negativesCount number)))

(defn addPronouns [number]
  "Increases number of pronouns with a given number"
  (updatePronounsValue (+ number @pronounsCount)))

(defn addNegatives [number]
  "Increases number of negatives with a given number"
  (updateNegativesValue (+ number @negativesCount)))

(defn updateScore [words]
  "Updates data according to the given words input vector"
  (let [pronounsToAdd
	(count (clojure.set/intersection (set words)
					 (set PRONOUNS)))
	negativesToAdd
	(count (clojure.set/intersection (set words)
					 (set NEGATIVES)))]
    (addPronouns pronounsToAdd)
    (addNegatives negativesToAdd)))

(defn resetStatistics
  "Resets statistics"
  []
  (updatePronounsValue 0)
  (updateNegativesValue 0))

(def FEMALE_PRONOUN_CONSTANT 10)

(defn getGender
  "Returns suggested text author's gender"
  []
  (if (> (/
	  (+ @pronounsCount @negativesCount)
	  (stat/getWordsCount) 0.01)
	 FEMALE_PRONOUN_CONSTANT)
    (:f GENDERS)
    (:m GENDERS)))