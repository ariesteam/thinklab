(ns modelling)

(tl/load-bindings 'org.integratedmodelling.thinklab.corescience)

(defn measurement 
	"Build a model for a measurement, optionally mediating another model (which must also be
	a measurement. No dependency structure is allowed."
	[concept-or-model unitspecs]
	(new org.integratedmodelling.modelling.corescience.MeasurementModel
			concept-or-model (str unitspecs)))
			
(defn classification
  "Build a classification model of a type appropriate to the passed mappings. No
  dependency structure is allowed."
	[concept-or-model & class-specs]
	(new org.integratedmodelling.modelling.corescience.ClassificationModel
	    concept-or-model 
	    ; switch any clojure lists to thinklab lists so we can build instances from them
	    (map #(if (list? %) (tl/listp %) %) class-specs)))
	
(defn discrete-random-model
	""
	[concept-or-model & cpt-deps-body]
	nil)
	
(defn continuous-random-model
	""
	[concept-or-model & cpt-deps-body]
	nil)
	
(defn discrete-noisymax-model
	""
	[concept-or-model & cpt-deps-body]
	nil)
			
(defn computed-measurement 
  	""
		[concept unitspecs equation & deps]
		nil)
	
(defn dde-measurement 
 	""
		[concept unitspecs equation & deps]
		nil)
; 	
;(defn ranking 
;	"Build a model for a measurement, optionally mediating another model (which must also be
;	a measurement"
;	([concept-or-model & specs]
;	(new 
;		(org.integratedmodelling.modelling.corescience.MeasurementModel
;			((tl/conc concept) (str unitspecs) nil))))
;	([concept-or-model unitspecs]
;	(new
;		(org.integratedmodelling.modelling.corescience.MeasurementModel
;			((tl/conc concept) (str unitspecs) model)))))
					
(defn computed-ranking 
 	""
		[concept unitspecs equation & deps]
		nil)
	
(defn dde-ranking 
 	""
		[concept unitspecs equation & deps]
		nil)
	
 	
			
