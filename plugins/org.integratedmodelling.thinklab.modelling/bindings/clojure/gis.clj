(ns modelling.gis
  (:refer-clojure :rename {count length}))

(defn j-make-distance-model
	[]
	(new org.integratedmodelling.modelling.gis.EuclideanDistanceModel (str *ns*)))

(defn j-make-slope-model
	[]
	(new org.integratedmodelling.modelling.gis.SlopeModel (str *ns*)))

(defmacro slope
	"Rankings describe their states as numeric values that have an ordinal relationship and optionally
	a scale. Rankings of different scales are mediated appropriately."
	[observable & body]
	`(let [model# 
 	        	(modelling.gis/j-make-slope-model)] 
 	   (.setObservable model# (modelling/process-observable '~observable))
 	   (if (not (nil? '~body)) 
				(doseq [classifier# (partition 2 '~body)]
		 	   	(if  (keyword? (first classifier#)) 
		 	   		  (modelling/transform-model model# classifier#))))
 	   model#))