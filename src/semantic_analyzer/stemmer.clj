(ns semantic-analyzer.core
  (:use [clojure.java.io]))

;;;; This file implements Porter stemming algorithm
;;;; for Russian language taken from here:
;;;; http://snowball.tartarus.org/algorithms/russian/stemmer.html

;; Endings
(def PERFECTIVE_GERUND_ENDINGS
     ["в" "вши" "вшись"])
(def ADJECTIVAL_ENDINGS
     ["ее"  "ие"  "ые"  "ое"  "ими" "ыми"
      "ей"  "ий"  "ый"  "ой"  "ем"  "им"
      "ым"  "ом"  "его" "ого" "ему" "ому"
      "их"  "ых"  "ую"  "юю"  "ая"  "яя"
      "ою"  "ею"])
(def PARTICIPLE_ENDINGS1
     ["ем" "нн" "вш" "ющ" "щ"])
(def PARTICIPLE_ENDINGS2
     ["ивш" "ывш" "ующ"])
(def REFLEXIVE_ENDINGS
     ["ся" "сь"])
(def VERB_ENDINGS1
     ["ла"  "на"  "ете" "йте" "ли"
      "й"   "л"   "ем"  "н"   "ло"
      "но"  "ет"  "ют"  "ны"  "ть"
      "ешь"  "нно"])
(def VERB_ENDINGS2
     ["ила"  "ыла"  "ена"  "ейте" "уйте"
      "ите"  "или"  "ыли"  "ей"   "уй"
      "ил"   "ыл"   "им"   "ым"   "ен"
      "ило"  "ыло"  "ено"  "ят"   "ует"
      "уют"  "ит"   "ыт"   "ены"  "ить"
      "ыть"  "ишь"  "ую"   "ю"])
(def NOUN_ENDINGS
     ["а"   "ев"   "ов"  "ие"  "ье" "е"   "иями"
      "ями" "ами"  "еи"  "ии"  "и"  "ией" "ей"
      "ой"  "ий"   "й"   "иям" "ям" "ием" "ем"
      "ам"  "ом"   "о"   "у"   "ах" "иях" "ях"
      "ы"   "ь"    "ию"  "ью"  "ю"  "ия"  "ья"   "я"])
(def SUPERLATIVE_ENDINGS
     ["ейш" "ейше"])

(def DERIVATIONAL_ENDINGS
     ["ост" "ость"])

(def VOWELS
     ["а" "е" "ё" "и" "о" "у" "э" "ю" "я"])

(def ENDINGS_MAP
     {:perf_gerund PERFECTIVE_GERUND_ENDINGS
      :adj ADJECTIVAL_ENDINGS
      :part1 PARTICIPLE_ENDINGS1
      :part2 PARTICIPLE_ENDINGS2
      :verb1 VERB_ENDINGS1
      :verb2 VERB_ENDINGS2
      :noun NOUN_ENDINGS
      :super SUPERLATIVE_ENDINGS
      :deriv DERIVATIONAL_ENDINGS
      :reflex REFLEXIVE_ENDINGS
      :i ["и"]
      :nn ["нн"]
      :soft ["ь"]})

;; helper functions
(defn hasAnyEndings? [word endings]
  (some #(.endsWith word %) endings))

(defn hasWordAnyEndings? [endingsCode]
  #(hasAnyEndings? % (endingsCode ENDINGS_MAP)))

(defn hasPerfectiveGerundEnding? [word]
  ((hasWordAnyEndings? :perf_gerund) word))

(defn hasReflexiveEnding? [word]
  ((hasWordAnyEndings? :reflex) word))

(defn hasAdjectivalEnding? [word]
  ((hasWordAnyEndings? :adj) word))

(defn hasVerb1Ending? [word]
  ((hasWordAnyEndings? :verb1) word))

(defn hasVerb2Ending? [word]
    ((hasWordAnyEndings? :verb2) word))

(defn hasNounEnding? [word]
    ((hasWordAnyEndings? :noun) word))

(defn hasDerivationalEnding? [word]
  ((hasWordAnyEndings? :deriv) word))

(defn hasSuperlativeEnding? [word]
  ((hasWordAnyEndings? :super) word))

(comment defn hasParticularEnding? [word particularEnding]
  (hasAnyEndings? word [particularEnding]))

(comment defn hasIEnding? [word]
  "Becase unnecessary"
  (hasParticularEnding? word "и"))

(comment defn hasDoubleNEnding? [word]
  (hasParticularEnding? word "нн"))

(comment defn hasSoftSignEnding? [word]
  (hasParticularEnding? word "ь"))


;; word regions
(def VOWELS_STRING (clojure.string/join "" VOWELS))
(def rPattern (str "[" VOWELS_STRING "]"
		   "[^" VOWELS_STRING "](.*)"))

(defn r1 [word]
  (or (last (re-find (re-pattern rPattern) word))
      ""))

(defn r2 [word] (r1 (r1 word)))


;; functions for removal
(defn findEnding [word endingsCode]
  (first (filter #(.endsWith word %)
		 (endingsCode ENDINGS_MAP))))

(defn removeEnding [word endingsCode]
  (let [ending (findEnding word endingsCode)]
    (if (and (not (nil? ending))
	     (.endsWith word ending))
      (.substring word 0 (- (count word)
			    (count ending)))
      word)))


;; stemming algorithm
(defn step1 [word]
  (if (hasPerfectiveGerundEnding? word)
    (removeEnding word :perf_gerund)
    (do 
      (removeEnding word :reflexive)
      (cond
	    (hasAdjectivalEnding? word) (removeEnding word :adj)
	    (hasVerb1Ending? word) (removeEnding word :verb1)
	    (hasVerb2Ending? word) (removeEnding word :verb2)
	    (hasNounEnding? word) (removeEnding word :noun)
	    :else word))))

(defn step2 [word]
  (removeEnding word :i))

(defn step3 [word]
  (if (hasDerivationalEnding? (r2 word))
    (removeEnding)
    word))

(defn step4 [word]
  (let [woutSuperlative (removeEnding word :super)
	woutSoft (removeEnding woutSuperlative :soft)]
    (removeEnding woutSoft :nn)))

;; main function which cuts the word
(defn run [word]
  (step4 (step3 (step2 (step1 word)))))