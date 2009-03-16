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

;(defn j-make-type
;	"Invoke static deftype at the Java side of the package"
;	( [typename cmodel-specs]
;	(. org.integratedmodelling.modelling.Model
;			(deftype (tl/get-session) (tl/conc typename) cmodel-specs nil)))
;	( [typename cmodel-specs dependencies]
;	(. org.integratedmodelling.modelling.Model
;			(deftype (tl/get-session) (tl/conc typename) cmodel-specs (tl/listp dependencies)))))

(defn j-make-model
	"Make a new instance of Model and return it"
	[]
	(new org.integratedmodelling.modelling.Model))
	

;; ----------------------------------------------------------------------------------------------
;; public macros
;; ----------------------------------------------------------------------------------------------

         
(defmacro defmodel [model-name observable & body]
	 `(let [desc#  (if (string? (first '~body)) (first '~body))
 	  	    specs# (if (nil? desc#) '~body (rest '~body))
 	   			contingency-model# (first specs#)
 	        conditional-model# (if (empty? contingency-model#) nil (rest specs#))
 	        dependency-model#  (if (nil? conditional-model#) (rest specs#))
 	        model# (model/j-make-model)]
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
       
; (model/defmodel zio 'thinklab-core:Number [] (model/measurement 'thinklab-core:Number "km"))
    