;; -------------------------------------------------------------------------------------------
;; Core functions to enable semantic modelling using observations.
;; -------------------------------------------------------------------------------------------

(ns model)
	
	
(defmacro type 
	"Define and return a subtype of a known type by defining its conceptual model and optionally known types of
	 observables it depends upon."
	([typename cmodel-specs]
		(. org.integratedmodelling.modelling.Model
			(deftype (tl/get-session) (tl/conc typename) (tl/listp cmodel-specs) nil)))
	([typename cmodel-specs dependent-types]
		(. org.integratedmodelling.modelling.Model
			(deftype (tl/get-session) (tl/conc typename) (tl/listp cmodel-specs) (tl/listp dependent-types)))))
		
;;(defmacro defvar 
;;	"Define the subtypes that a known type should be seen as when the corresponding conditions are
;;	 true. Conditions should be expressed as constraint lists in terms of variables that have a 
;;	 specific observe statement in the same model."
;;	[base-type body]
;;	(. org.integratedmodelling.semanticmodelling.clojure.ClojureBridge 
;;		(defvar (tl/get-session) (str base-type) (tl/listp body))))
		
(defn uninterleave [coll]
  (loop [odds  []
         evens []
         rem coll]
    (if (seq rem)
      (recur (conj odds  (first rem))
             (conj evens (second rem))
             (rrest rem))
      [odds evens])))
      
(defn model
	"Specify and return a semantic model. The argument list should be a map pairing an internal 
	 symbol to a type that must be observed in order to build and run the model. The body should
	 be a list with alternating pairs of types to observe and cond statements that return the 
	 types they should be observed as if each condition is true. Conditions are thinklab constraint
	 lists, which can use the observed values in the argument list, plus the special symbol
	 :context that contains the observation context of the model building. You can use the type
	 function to build specialized types on the fly."
	 [type arguments type-conditions]
	 (let [
	 	   mod (new org.integratedmodelling.modelling.Model type)
	       [arg-types# arg-names#] (uninterleave arguments)
	       ]
	  (do 
	  	 (map (fn [id type] (doto mod (.observe (tl/conc (eval type)) (str id)))) arg-types# arg-names#)
	  	 (map (fn [id type] (doto mod (.observe (tl/conc (eval type)) (str id)))) arg-types# arg-names#))))
	 
;; testing	 
	 
	 
;;	 (doto (new org.integratedmodelling.semanticmodelling.SemanticModel)
;;		(.setDependencyTree dependencyTree)
;;		(.setExhaustive exhaustive)
;;		(.setKbox kbox)
;;		(.setRegionOfInterest regionOfInterest)
;;		(.run (tl/get-session)))