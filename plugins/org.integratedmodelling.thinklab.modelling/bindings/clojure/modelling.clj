;; -------------------------------------------------------------------------------------------
;; Core functions to enable semantic modelling using observations.
;; -------------------------------------------------------------------------------------------

(ns modelling)

; birecursive patterns be damned
(declare transform-model)

(defn j-make-model
	"Make a new instance of Model and return it. We need this because the class won't be visible when
	the macro is expanded at runtime."
	[]
	(new org.integratedmodelling.modelling.Model))
	
(defn register-model
	"Get the single instance of the model manager from the modelling plugin and register the passed model
	 with it."
	[model name]
	(.. org.integratedmodelling.modelling.ModellingPlugin (get) (getModelManager) (registerModel model name)))
	
(defn- get-configurable-model
	"Return a model clone that we can safely configure. Essentially a copy on write pattern, called
	 only when there are non-empty clauses in a dependent model."
	 [mod]
	 (.getConfigurableClone mod))

(defn configure-model
	"Take a pair (model (kw val kw val)) and return the model after all the clauses in the
	 kw list have been applied to it. The second parameter is a map of functions to be applied
	 to the argument of each keyword."
	 [mlist]
	 (let [confs (second mlist)
	 			 model (if (nil? confs) (eval (first mlist)) (get-configurable-model (eval (first mlist))))]
		  (doseq [kws (partition 2 confs)]
		  	(transform-model model kws))
	 		model))

(defn- transform-model 
	"Apply the passed clause to the passed model after transforming the argument according to 
	 the keyword. A map would be much more elegant but won't work in the bi-recursive pattern.
	 Just passes through anything that isn't handled - leave it to Java to validate the keyword."
	[model clause]
	(cond (= (first clause) :when)
				(.applyClause model ":when" (tl/listp (second clause)))
				(= (first clause) :as)
				(.applyClause model ":as" (str (second clause)))
				(= (first clause) :keep)
				(.applyClause model ":keep" (map eval (second clause)))
				(= (first clause) :discard)
				(.applyClause model ":discard" (map eval (second clause)))
				(= (first clause) :probability)
				(.applyClause model ":probability" (eval (second clause)))
				(= (first clause) :derivative)
				(.applyClause model ":derivative" (tl/listp (second clause)))
				(= (first clause) :context)
				(.applyClause model ":context" (map configure-model (tl/group-with-keywords (second clause))))
				:otherwise
				(.applyClause model (str (first clause)) (second clause))))

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
 	        definition# 
 	        	(drop (tl/count-not-nil (list desc# contingency-model#)) '~body)
 	        model# 
 	        	(modelling/j-make-model)]
 	        	
 	     (.setObservable  model# (if (seq? ~observable) (tl/listp ~observable) ~observable))
 	     (.setDescription model# desc#)
 	      	     
 	     ; process the contingency model - as many models as we like, will build an id from all
 	     (doseq [mdef# (tl/group-with-keywords contingency-model#)]
         	(.addContingency model# (configure-model mdef#))) 
         	    	  	
        ; process the model definitions - one or more models, must be conditional if > 1
       (doseq [mdef# (tl/group-with-keywords definition#)]
          (.defModel model# (configure-model mdef#)))
          
       model#))
       
(defmacro defmodel
	 "Define a model for the given observable, using the given contingency 
	  structure and conditional specifications, or the given unconditional model if no 
	  contingency structure is supplied."
		[model-name observable & body]
 		`(def ~model-name (modelling/register-model (eval '(modelling/model ~observable ~@body)) (str '~model-name))))
       
(defn run 
	"Build an observation from the passed model. If the model has unresolved dependencies
	pass a kbox to resolve them. If two kboxes are passed, the first is used to resolve the
	contingencies (context) and the second is used for the dependencies. If a topology is
	passed, the context's topology will be set to it."
	[model & params]
	(.run model (tl/get-session) params))
	       
; (modelling/model 'thinklab-core:Number (modelling/measurement 'thinklab-core:Number "km"))
; (modelling/defmodel zio 'thinklab-core:Number [] (modelling/measurement 'thinklab-core:Number "km") :as zorro)
  