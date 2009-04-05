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
	
(defn register-model
	"Get the single instance of the model manager from the modelling plugin and register the passed model
	 with it."
	[model]
	(.. org.integratedmodelling.modelling.ModellingPlugin (get) (getModelManager) (registerModel model)))
	
;; ----------------------------------------------------------------------------------------------
;; public macros
;; ----------------------------------------------------------------------------------------------

;; transformer functions for values of specific keywords before they hit Java
(def kw-mappings
	{
		:when    #(tl/listp %) 
	})

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
 	        	
 	     (.setObservable model# (if (seq? ~observable) (tl/listp ~observable) ~observable))
 	     (.setDescription model# desc#)
 	     
 	     ; process the contingency model - as many models as we like, will build an id from all
 	     (doseq [mdef# (partition 2 (tl/group-with-keywords contingency-model#))]
         	(.addContingency model# (eval (first mdef#)) (tl/map-keywords (second mdef#) kw-mappings)))       	  	
        ; process the model definitions - one or more models, must be conditional if > 1
       (doseq [mdef# (partition 2 (tl/group-with-keywords dependency-model#))]
          (.defModel model# (eval (first mdef#))(tl/map-keywords (second mdef#) kw-mappings)))
       model#))
       
(defmacro defmodel
	 "Define a model for the given observable, using the given contingency 
	  structure and conditional specifications, or the given unconditional model if no 
	  contingency structure is supplied."
		[model-name observable & body]
 		`(def ~model-name (modelling/register-model (eval '(modelling/model ~observable ~@body)))))
       
(defn run 
	"Build an observation from the passed model. If the model has unresolved dependencies
	pass a kbox to resolve them. If two kboxes are passed, the first is used to resolve the
	contingencies (context) and the second is used for the dependencies. If a topology is
	passed, the context's topology will be set to it."
	[model & params]
	(.run model (tl/get-session) params))
	       
; (modelling/model 'thinklab-core:Number (modelling/measurement 'thinklab-core:Number "km"))
; (modelling/defmodel zio 'thinklab-core:Number [] (modelling/measurement 'thinklab-core:Number "km") :as zorro)
  