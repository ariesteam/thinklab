(ns modelling.gis
  (:refer-clojure :rename {count length}))

(defn j-make-distance-model
	[]
	(new org.integratedmodelling.modelling.gis.EuclideanDistanceModel))