;; -------------------------------------------------------------------------------------------
;; Core functions to enable semantic modelling using observations.
;; -------------------------------------------------------------------------------------------

(ns modelling)

;; ----------------------------------------------------------------------------------------------
;; Private Java binding functions, which need to actually be public because they're used in macros
;;
;; Must defer the java binding to compiled functions, or the classloader won't find the classes
;; at runtime. The classes are only visible when the bindings are loaded. 
;; ----------------------------------------------------------------------------------------------

(defn j-make-model
	"Make a new instance of Model and return it"
	[]
	(new org.integratedmodelling.modelling.Model))
	
;; ----------------------------------------------------------------------------------------------
;; public macros
;; ----------------------------------------------------------------------------------------------
        
(defmacro model 
	"Return a new model for the given observable, defined using the given contingency 
	 structure and conditional specifications, or the given unconditional model if an 
	 empty contingency structure is supplied."
	[observable & body]
	 `(let [desc#  (if (string? (first '~body)) (first '~body))
 	  	    specs# (if (nil? desc#) '~body (rest '~body))
 	   			contingency-model# (first specs#)
 	        conditional-model# (if (empty? contingency-model#) nil (rest specs#))
 	        dependency-model#  (if (nil? conditional-model#) (rest specs#))
 	        model# (modelling/j-make-model)]
 	     (.setObservable model# (tl/conc ~observable))
 	     (if (not (empty? contingency-model#))
 	         (doseq [[id# contingent-model#] (apply zipmap (tl/uninterleave contingency-model#))]
         	  	(.addContingency model# (str id#) (eval contingent-model#))))
       (if (not (nil? conditional-model#))
        	 (doseq [[constraint-list# type-to#] (apply zipmap (tl/uninterleave conditional-model#))]
               (if (= :default constraint-list#)
                 (.defrule model# nil (tl/conc (eval type-to#)))
                 (.defrule model# (tl/listp constraint-list#) (tl/conc (eval type-to#))))))
       (if (not (nil? dependency-model#))
           (.setModel model# (eval (first dependency-model#))))
       model#))
       
(defmacro defmodel 
	 "Define a model for the given observable, using the given contingency 
	 structure and conditional specifications, or the given unconditional model if an 
	 empty contingency structure is supplied."
		[model-name observable & body]
 		`(def ~model-name (eval '(modelling/model ~observable ~@body))))
       
(defn run 
	"Build an observation from the passed model and the given kbox;
	return an observation of its contextualized state."
	[model kbox]
	(.run model kbox (tl/get-session)))
	       
; (modelling/model 'thinklab-core:Number [] (modelling/measurement 'thinklab-core:Number "km"))
; (modelling/defmodel zio 'thinklab-core:Number [] (modelling/measurement 'thinklab-core:Number "km"))
  