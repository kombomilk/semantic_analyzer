(ns semantic-analyzer.gender)

(def GENDERS {:m "Мужчина"
	      :f "Женщина"})

(defn getGender []
  (:m GENDERS))