(ns modelling)

(tl/load-bindings 'org.integratedmodelling.thinklab.corescience)

(defn j-make-measurement
	"Make a new instance of Model and return it."
	[]
	(new org.integratedmodelling.modelling.corescience.MeasurementModel))

(defn j-make-count
	"Make a new instance of Model and return it."
	[]
	(new org.integratedmodelling.modelling.corescience.CountModel))

(defn j-make-classification
	"Make a new instance of Model and return it."
	[]
	(new org.integratedmodelling.modelling.corescience.ClassificationModel))

(defn j-make-observation
	"Make a new instance of Model and return it."
	[]
	(new org.integratedmodelling.modelling.corescience.ObservationModel))

(defn j-make-ranking
	"Make a new instance of Model and return it."
	[]
	(new org.integratedmodelling.modelling.corescience.RankingModel))
	
(defn j-make-categorization
	"Make a new instance of Model and return it."
	[]
	(new org.integratedmodelling.modelling.corescience.CategorizationModel))


(defn j-make-bayesian
	"Make a new instance of Model and return it."
	[]
	(new org.integratedmodelling.modelling.random.BayesianModel))

(defmacro classification
	""
	[observable & specs]
	`(let [model# (modelling/j-make-classification)] 
 	   (.setObservable model# 
	   			(if (or (not (seq? ~observable)) (nil? (namespace (first '~observable)))) 
 	   					(if (seq? ~observable) (tl/listp ~observable) ~observable) 
 	   					(eval ~observable)))
		 (doseq [classifier# (partition 2 '~specs)]
		 	   (.addClassifier model# (tl/unquote-if-quoted (first classifier#)) (eval (second classifier#))))
 	   model#))

(defmacro enumeration
	""
	[observable & units]
	`(let [model# 
 	        	(modelling/j-make-count)] 
 	   (.setObservable model# 
	   			(if (or (not (seq? ~observable)) (nil? (namespace (first '~observable)))) 
 	   					(if (seq? ~observable) (tl/listp ~observable) ~observable) 
 	   					(eval ~observable))) 	
 	   (if (not (nil? '~units)) (.setUnits model# (first '~units)))    
 	    model#))
	
(defmacro ranking
	""
	[observable & units]
	`(let [model# 
 	        	(modelling/j-make-ranking)] 
 	   (.setObservable model# 
	   			(if (or (not (seq? ~observable)) (nil? (namespace (first '~observable)))) 
 	   					(if (seq? ~observable) (tl/listp ~observable) ~observable) 
 	   					(eval ~observable)))
 	   (if (not (nil? '~units)) (.setUnits model# (first '~units))) 
 	   model#))
	
(defmacro categorization
	""
	[observable & categories]
	`(let [model# 
 	        	(modelling/j-make-categorization)] 
 	   (.setObservable model# 
	   			(if (or (not (seq? ~observable)) (nil? (namespace (first '~observable)))) 
 	   					(if (seq? ~observable) (tl/listp ~observable) ~observable) 
 	   					(eval ~observable)))
 	   (if (not (nil? '~categories)) (.setCategories model# (first '~categories))) 
 	   model#))
	
(defmacro measurement
	"Create a measurement model. The observable can be another measurement model or a semantic object."
	[observable units]
	`(let [model# 
 	        	(modelling/j-make-measurement)] 
 	   (.setObservable model# 
	   			(if (or (not (seq? ~observable)) (nil? (namespace (first '~observable)))) 
 	   					(if (seq? ~observable) (tl/listp ~observable) ~observable) 
 	   					(eval ~observable)))
 	    (.setUnits model# ~units) 	  
 	    model#))
	
(defmacro identification
	"Create an identification model. The observable can only be a semantic object."
	[observable]
	`(let [model# 
 	        	(modelling/j-make-observation)] 
 	   (.setObservable model# (if (seq? ~observable) (tl/listp ~observable) ~observable))
 	   model#))

(defmacro bayesian
	"Create a bayesian model. The observable can only be a semantic object. For now the only way to
	 define it is through the :import clause; bayesian network specifications are admitted but ignored."
	[observable & specs]
	`(let [model# 
 	        	(modelling/j-make-bayesian)] 
 	   (.setObservable model# (if (seq? ~observable) (tl/listp ~observable) ~observable))
 	   model#))
