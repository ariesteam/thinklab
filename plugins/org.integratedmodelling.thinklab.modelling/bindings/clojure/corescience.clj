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
		 	   (if  (and  (keyword? (first classifier#)) (not (= :otherwise (first classifier#)))) 
		 	   		  (.setMetadata model# (str keyword) (eval (second classifier#))) 
		 	   		  (.addClassifier model# (tl/unquote-if-quoted (first classifier#)) (eval (second classifier#)))))
 	   model#))

(defmacro enumeration
	""
	[observable & body]
	`(let [model# 
 	        	(modelling/j-make-count)] 
 	   (.setObservable model# 
	   			(if (or (not (seq? ~observable)) (nil? (namespace (first '~observable)))) 
 	   					(if (seq? ~observable) (tl/listp ~observable) ~observable) 
 	   					(eval ~observable))) 	
 	   (if (not (nil? '~body)) 
				(doseq [classifier# (partition 2 '~body)]
		 	   	(if  (keyword? (first classifier#)) 
		 	   		  (.setMetadata model# (str keyword) (eval (second classifier#))))))
 	    model#))
	
(defmacro ranking
	""
	[observable & body]
	`(let [model# 
 	        	(modelling/j-make-ranking)] 
 	   (.setObservable model# 
	   			(if (or (not (seq? ~observable)) (nil? (namespace (first '~observable)))) 
 	   					(if (seq? ~observable) (tl/listp ~observable) ~observable) 
 	   					(eval ~observable)))
 	   (if (not (nil? '~body)) 
				(doseq [classifier# (partition 2 '~body)]
		 	   	(if  (keyword? (first classifier#)) 
		 	   		  (.setMetadata model# (str keyword) (eval (second classifier#))))))
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
	[observable units & body]
	`(let [model# 
 	        	(modelling/j-make-measurement)] 
 	   (.setObservable model# 
	   			(if (or (not (seq? ~observable)) (nil? (namespace (first '~observable)))) 
 	   					(if (seq? ~observable) (tl/listp ~observable) ~observable) 
 	   					(eval ~observable)))
 	   (.setUnits model# ~units)
 	   (if (not (nil? '~body)) 
				(doseq [classifier# (partition 2 '~body)]
		 	   	(if  (keyword? (first classifier#)) 
		 	   		  (.setMetadata model# (str keyword) (eval (second classifier#))))))
 	    model#))
	
(defmacro identification
	"Create an identification model. The observable can only be a semantic object."
	[observable & body]
	`(let [model# 
 	        	(modelling/j-make-observation)] 
 	   (.setObservable model# (if (seq? ~observable) (tl/listp ~observable) ~observable))
 	   (if (not (nil? '~body)) 
				(doseq [classifier# (partition 2 '~body)]
		 	   	(if  (keyword? (first classifier#)) 
		 	   		  (.setMetadata model# (str keyword) (eval (second classifier#))))))
 	   model#))

(defmacro bayesian
	"Create a bayesian model. The observable can only be a semantic object. For now the only way to
	 define it is through the :import clause; bayesian network specifications are admitted but ignored."
	[observable & body]
	`(let [model# 
 	        	(modelling/j-make-bayesian)] 
 	   (.setObservable model# (if (seq? ~observable) (tl/listp ~observable) ~observable))
 	   (if (not (nil? '~body)) 
				(doseq [classifier# (partition 2 '~body)]
		 	   	(if  (keyword? (first classifier#)) 
		 	   		  (.setMetadata model# (str keyword) (eval (second classifier#))))))
 	   model#))
 	   
;; -------------------------------------------------------------------------------------------------------
;; inquiry, extraction etc
;; -------------------------------------------------------------------------------------------------------

(defn binary? 
	"Returns true if the datasource encodes a binary distribution that represents a yes/no situation for
	 a classified observation. If this returns true, (get-data) will return the probability of the true
	 case."
	[datasource]
	(not (nil? (.getMetadata datasource "truecase"))))

(defn probabilistic?
	"True if the given datasource is a discrete distribution. If so, uncertainty info can be
	extracted using get-uncertainty"
	[datasource]
	(instance? org.integratedmodelling.modelling.data.CategoricalDistributionDatasource datasource))
	
(defn get-uncertainty
	"Return uncertainty information from a datasource as an array of doubles."
	[datasource]
	(.getMetadata datasource "uncertainty"))
	
(defn get-data
	"Return numbers from a datasource as an array of doubles."
	[datasource]
	(.getDataAsDoubles datasource))
	
(defn get-probabilities
	"Return probabilities for the given context state: returns an array of doubles with probabilities 
	for each state returned by get-possible-states"
	[datasource n]
	(.getProbabilities datasource n))

(defn get-possible-states
	"Returns an array of concepts that index the probabilites returned by get-probabilities"
	[datasource]
	(.getStates datasource))
	
(defn get-probability
	"Return the probability for the state represented by the passed concept at context state n"
	[datasource concept n]
	(.getProbability datasource n concept))

(defn get-dist-breakpoints
	"Return an array of doubles describing the continuous probability distribution encoded in the
	discretized categories in the passed probabilistic datasource. Throws an exception if the 
	datasource is not encoding a continuous distribution."
	[datasource]
	(.getMetadata datasource "continuous_dist_breakpoints"))
	
(defn encodes-continuous-distribution? 
	"True if the given datasource is the discrete encoding of a continuous probability distribution, 
 	 meaning that get-dist-breakpoints will not throw an exception when called."
	[datasource]
	(not (nil? (.getMetadata datasource "continuous_dist_breakpoints"))))
