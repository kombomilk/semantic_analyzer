(ns semantic-analyzer.stemmer
  (:gen-class))

;;;; Здесь реализован алгоритм стеммера Портера для 
;;;; русского языка, взятый отсюда:
;;;; http://snowball.tartarus.org/algorithms/russian/stemmer.html

;; Окончания
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

;; вспомонательные функци
(defn hasAnyEndings?
  "Checks if the word has any ending from the given endings vector"
  [word endings]
  (some #(.endsWith word %) endings))

(defn hasWordAnyEndings?
  "Returns function which checks existence of corresponding endings
in the endings vector"
  [endingsCode]
  #(hasAnyEndings? % (endingsCode ENDINGS_MAP)))

(defn hasPerfectiveGerundEnding?
  "Checks whether a word has a perfective gerund ending"
  [word]
  ((hasWordAnyEndings? :perf_gerund) word))

(defn hasReflexiveEnding?
  "Checks whether a word has a reflexive ending"
  [word]
  ((hasWordAnyEndings? :reflex) word))

(defn hasAdjectivalEnding?
  "Checks whether a word has an adjectival ending"
  [word]
  ((hasWordAnyEndings? :adj) word))

(defn hasVerb1Ending?
  "Checks whether a word has a verb ending of the first type"
  [word]
  ((hasWordAnyEndings? :verb1) word))

(defn hasVerb2Ending?
  "Checks whether a word has a verb ending of the second type"
  [word]
  ((hasWordAnyEndings? :verb2) word))

(defn hasNounEnding?
  "Checks whether a word has a noun ending"
  [word]
  ((hasWordAnyEndings? :noun) word))

(defn hasDerivationalEnding?
  "Checks whether a word has a derivational ending"
  [word]
  ((hasWordAnyEndings? :deriv) word))

(defn hasSuperlativeEnding?
  "Checks whether a word has a superlative ending"
  [word]
  ((hasWordAnyEndings? :super) word))

(comment defn hasParticularEnding? [word particularEnding]
  (hasAnyEndings? word [particularEnding]))

(comment defn hasIEnding? [word]
  (hasParticularEnding? word "и"))

(comment defn hasDoubleNEnding? [word]
  (hasParticularEnding? word "нн"))

(comment defn hasSoftSignEnding? [word]
  (hasParticularEnding? word "ь"))

;; поиск регионов слов (r1, r2), подробнее здесь:
;; http://snowball.tartarus.org/texts/r1r2.html
(def VOWELS_STRING (clojure.string/join "" VOWELS))
(def rPattern (str "[" VOWELS_STRING "]"
		   "[^" VOWELS_STRING "](.*)"))

(defn r1
  "Returns r1 region of the word"
  [word]
  (or (last (re-find (re-pattern rPattern) word))
      ""))

(defn r2
  "Returns r2 region of the word"
  [word]
  (r1 (r1 word)))


;; функции для удаления окончаний
(defn findEnding
  "Finds a corresponding word ending in the given vector"
  [word endingsCode]
  (first (filter #(.endsWith word %)
		 (endingsCode ENDINGS_MAP))))

(defn removeEnding
  "Remove given ending in the wodr"
  [word endingsCode]
  (let [ending (findEnding word endingsCode)]
    (if (and (not (nil? ending))
	     (.endsWith word ending)
	     (> (count word) 3))
      (.substring word 0 (- (count word)
			    (count ending)))
      word)))


;; алгоритм стемминга
;; все четыре шага подробно описаны по ссылке,
;; приведенной в начале файла
(defn step1
  "The first step of the stemming algorithm"
  [word]
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

(defn step2
  "The second step of the stemming algorithm"
  [word]
  (removeEnding word :i))

(defn step3
  "The third step of the stemming algorithm"
  [word]
  (if (hasDerivationalEnding? (r2 word))
    (removeEnding word :deriv)
    word))

(defn step4
  "The fourth step of the stemming algorithm"
  [word]
  (let [woutSuperlative (removeEnding word :super)
	woutSoft (removeEnding woutSuperlative :soft)]
    (removeEnding woutSoft :nn)))

;; основная функция
(defn run
  "Main function which returns stem generated from word"
  [word]
  (-> word
      step1
      step2
      step3
      step4))