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
			
			
(defmacro make-model [model-name type-bindings & rules]
  `(let [model# (new org.integratedmodelling.modelling.Model (tl/conc model-name))]
     (doseq [[id# type#] (apply zipmap (tl/uninterleave ~type-bindings))]
         (.observe model# id# (tl/conc type#)))
     (doseq [rule-spec# '~rules]
         (let [type-from# (first rule-spec#)]
           (doseq [[constraint-list# type-to#] (apply zipmap (tl/uninterleave (rest rule-spec#)))]
               (if (= :default constraint-list#)
                 (.defrule model# (tl/conc type-from#) nil (tl/conc type-to#))
                 (.defrule model# (tl/conc type-from#) (tl/listp constraint-list#) (tl/conc type-to#))))))
     model#))
     
     