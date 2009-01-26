;; -------------------------------------------------------------------------------------------
;; Core functions to enable semantic modelling using observations.
;; -------------------------------------------------------------------------------------------

(ns model)
	
(defmacro make-type 
	"Define and return a subtype of a known type by defining its conceptual model and optionally known types of
	 observables it depends upon."
	([typename cmodel-specs]
		`(. org.integratedmodelling.modelling.Model
			(deftype (tl/get-session) (tl/conc ~typename) (tl/listp '~cmodel-specs) nil)))
	([typename cmodel-specs dependent-types]
		`(. org.integratedmodelling.modelling.Model
			(deftype (tl/get-session) (tl/conc ~typename) (tl/listp '~cmodel-specs) (tl/listp '~dependent-types)))))
			