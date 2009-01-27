;; -------------------------------------------------------------------------------------------
;; Core functions to enable semantic modelling using observations.
;; -------------------------------------------------------------------------------------------

(ns model)

;; ----------------------------------------------------------------------------------------------
;; Private Java binding functions, which need to actually be public because they're used in macros
;;
;; Must defer the java binding to compiled functions, or the classloader won't find the classes
;; at runtime. The classes are only visible when the bindings are loaded. 
;; ----------------------------------------------------------------------------------------------

(defn j-make-type
	"Invoke static deftype at the Java side of the package"
	( [typename cmodel-specs]
	(. org.integratedmodelling.modelling.Model
			(deftype (tl/get-session) (tl/conc typename) cmodel-specs nil)))
	( [typename cmodel-specs dependencies]
	(. org.integratedmodelling.modelling.Model
			(deftype (tl/get-session) (tl/conc typename) cmodel-specs (tl/listp dependencies)))))

(defn j-make-model
	"Make a new instance of Model and return it"
	[concept]
	(new org.integratedmodelling.modelling.Model (tl/conc concept)))
	

;; ----------------------------------------------------------------------------------------------
;; public macros
;; ----------------------------------------------------------------------------------------------
	
(defmacro make-type 
	"Define and return a subtype of a known type by defining its conceptual model and optionally known types of
	 observables it depends upon."
	([typename cmodel-specs] `(j-make-type ~typename ~cmodel-specs))
	([typename cmodel-specs dependent-types] `(j-make-type ~typename ~cmodel-specs '~dependent-types)))
						
(defmacro make-model [model-name type-bindings & rules]
  `(let [model# (j-make-model ~model-name)]
     (doseq [[id# type#] (apply zipmap (tl/uninterleave ~type-bindings))]
         (.observe model# (tl/conc type#) (str id#)))
     (doseq [rule-spec# '~rules]
         (let [type-from# (first rule-spec#)]
           (doseq [[constraint-list# type-to#] (apply zipmap (tl/uninterleave (rest rule-spec#)))]
               (if (= :default constraint-list#)
                 (.defrule model# (tl/get-session) (tl/conc (eval type-from#)) nil (tl/conc (eval type-to#)))
                 (.defrule model# (tl/get-session) (tl/conc (eval type-from#)) (tl/listp constraint-list#) (tl/conc (eval type-to#)))))))
     model#))
     
     