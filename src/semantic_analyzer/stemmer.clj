(ns semantic-analyzer.core
  (:use [clojure.java.io]))

;;; This file implements Porter stemming algorithm
;;; for Russian language

;; Endings
; i-suffixes

(def PERFECTIVE_GERUND_ENDINGS
     ["в" "вши" "вшись"])
(def ADJECTIVE_ENDINGS
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

;; logic goes here
