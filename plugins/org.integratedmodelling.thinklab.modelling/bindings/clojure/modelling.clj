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
	"Make a new instance of Model and return it."
	[]
	(new org.integratedmodelling.modelling.Model))
	
;; ----------------------------------------------------------------------------------------------
;; public macros
;; ----------------------------------------------------------------------------------------------
        
(defmacro model 
	"Return a new model for the given observable, defined using the given contingency 
	 structure and conditional specifications, or the given unconditional model if no 
	 contingency structure is supplied."
	[observable & body]
	 `(let [desc#  
	 					(if (string? (first '~body)) (first '~body))
	        contingency-model# 
	        	(if (vector? (first (drop (if (nil? desc#) 0 1) '~body)))
	        		(first (drop (if (nil? desc#) 0 1) '~body)))
 	        dependency-model# 
 	        	(drop (tl/count-not-nil (list desc# contingency-model#)) '~body)
 	        model# 
 	        	(modelling/j-make-model)]
 	        	
 	     (.setObservable  model# (tl/conc ~observable))
 	     (.setDescription model# desc#)
 	     
 	     ; pass the contingency model
 	     (doseq [mdef# (tl/group-with-keywords contingency-model#)]
         	(.addContingency model# (eval (first mdef#)) (second mdef#)))         	  	
        ; pass the dependency model
       (doseq [mdef# (tl/group-with-keywords dependency-model#)]
          (.defModel model# (eval (first mdef#))(tl/map-lists (second mdef#))))
       model#))
       
(defmacro defmodel
	 "Define a model for the given observable, using the given contingency 
	  structure and conditional specifications, or the given unconditional model if no 
	  contingency structure is supplied."
		[model-name observable & body]
 		`(def ~model-name (eval '(modelling/model ~observable ~@body))))
       
(defn run 
	"Build an observation from the passed model. If the model has unresolved dependencies
	pass a kbox to resolve them. If two kboxes are passed, the first is used to resolve the
	contingencies (context) and the second is used for the dependencies. If a topology is
	passed, the context's topology will be set to it."
	[model & params]
	(.run model (tl/get-session) params))
	       
; (modelling/model 'thinklab-core:Number [] (modelling/measurement 'thinklab-core:Number "km"))
; (modelling/defmodel zio 'thinklab-core:Number [] (modelling/measurement 'thinklab-core:Number "km") :as zorro)
  