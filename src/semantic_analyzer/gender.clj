(ns semantic-analyzer.gender
  (:gen-class)
  (:require [semantic-analyzer.statistics :as stat]))

(def GENDERS {:m "Male"
	      :f "Female"})

(def PRONOUNS
     ["я" "ты" "он" "она" "мы" "вы" "они"
      "мен" "теб" "его" "ее" "нас" "вас" "их"
      "мне" "ему" "ей" "нам" "вам" "им"
      "мн" "тоб" "ею" "н" "в" "ими"
      "нем" "ней" "себ" "сам" "соб"
      "кто" "к" "кем" "что" "ч" "чем"
      "так" "т" "тот" "тому" "тем" "это"
      "этот" "эт"])

(def NEGATIVES
     ["не" "нет" "ни" "нич" "ник" "ничу" "ничт" "никак" "нельз"])

(def INTRODUCTORY
     ["разумеетс" "правд" "несомнен" "конеч" "естествен" "действител"
      "безуслов" "бесспор" "пожалу" "видим" "очевид" "вероят" "наверн"
      "возмож" "скаж" "например" "напрот" "наконец" "помнитс" "согласитес"
      "пожалуйст"])

(def pronounsCount (ref 0))
(def negativesCount (ref 0))
(def introductoryCount (ref 0))

(defn formatNumber [number]
  (format "%.2f" number))

(defn printStatistics []
  "Prints statistics about pronouns"
  (println "Pronouns :"
	   @pronounsCount
	   "/"
	   (stat/getWordsCount)
	   " = "
	   (formatNumber (/ @pronounsCount (stat/getWordsCount) 0.01)) "%")
  (println "Negative words :"
	   @negativesCount
	   "/"
	   (stat/getWordsCount)
	   " = "
	   (formatNumber (/ @negativesCount (stat/getWordsCount) 0.01)) "%")
  (println "Introductory words : "
	   @introductoryCount
	   "/"
	   (stat/getWordsCount)
	   " = "
	   (formatNumber (/ @introductoryCount (stat/getWordsCount) 0.01)) "%"))

(defn updatePronounsValue [number]
  "Updates number of pronouns"
  (dosync (ref-set pronounsCount number)))

(defn updateNegativesValue [number]
  "Updates number of negatives"
  (dosync (ref-set negativesCount number)))

(defn updateIntroductoryValue [number]
  "Updates number of introductory words"
  (dosync (ref-set introductoryCount number)))

(defn addPronouns [number]
  "Increases number of pronouns with a given number"
  (updatePronounsValue (+ number @pronounsCount)))

(defn addNegatives [number]
  "Increases number of negatives with a given number"
  (updateNegativesValue (+ number @negativesCount)))

(defn addIntroductory [number]
  "Increases number of introductory words with a given number"
  (updateIntroductoryValue (+ number @introductoryCount)))

(defn updateScore [words]
  "Updates data according to the given words input vector"
  (let [pronounsToAdd
	(count (clojure.set/intersection (set words)
					 (set PRONOUNS)))
	negativesToAdd
	(count (clojure.set/intersection (set words)
					 (set NEGATIVES)))
	introductoryToAdd
	(count (clojure.set/intersection (set words)
					 (set INTRODUCTORY)))]
    (addPronouns pronounsToAdd)
    (addNegatives negativesToAdd)
    (addIntroductory introductoryToAdd)))

(defn resetStatistics
  "Resets statistics"
  []
  (updatePronounsValue 0)
  (updateNegativesValue 0)
  (updateIntroductoryValue 0))

(def FEMALE_CONSTANT 10)

(defn getGender
  "Returns suggested text author's gender"
  []
  (if (> (/
	  (+ @pronounsCount @negativesCount @introductoryCount)
	  (stat/getWordsCount) 0.01)
	 FEMALE_CONSTANT)
    (:f GENDERS)
    (:m GENDERS)))