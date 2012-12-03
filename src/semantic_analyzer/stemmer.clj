(ns semantic-analyzer.core
  (:use [clojure.java.io]))

;;; This file implements Porter stemming algorithm
;;; for Russian language taken from here:
;;; http://snowball.tartarus.org/algorithms/russian/stemmer.html

;; Endings
; i-suffixes

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

; d-suffixes
(def DERIVATIONAL_ENDINGS
     ["ост" "ость"])

; vowels
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
      :reflex REFLEXIVE_ENDINGS})


; helper functions
(defn hasAnyEndings? [word endings]
  (some #(.endsWith word %) endings))

(defn hasWordAnyEndings? [word]
  #(hasAnyEnding? word (% ENDINGS_MAP)))

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

(defn hasParticularEnding? [word particularEnding]
  (hasAnyEnding? word [particularEnding]))

(defn hasIEnding? [word]
  (hasParticularEnding? word "и"))

(defn hasDoubleNEnding? [word]
  (hasParticularEnding? word "нн"))

(defn hasSoftSignEnding? [word]
  (hasParticularEnding? word "ь"))

; remove functions
(comment defn removeEnding [word endings]
  )

;; stemming algorithm
(defn step1 [word]
  (if (hasPerfectiveGerundEnding? word)
    (removePerfectiveGerundEnding word)
    (else-clause)))