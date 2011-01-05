;; -------------------------------------------------------------------------------------------
;; Core functions to enable semantic modelling using observations.
;; -------------------------------------------------------------------------------------------

(ns modelling
  (:refer-clojure :rename {count length}))
(tl/load-bindings 'org.integratedmodelling.thinklab.corescience)

; birecursive patterns be damned
(declare transform-model)

(defn j-make-model
	"Make a new instance of Model and return it. We need this because the class won't be visible when
	the macro is expanded at runtime."
	[]
	(new org.integratedmodelling.modelling.model.Model))
	
(defn j-make-scenario
	"Make a new instance of Model and return it. We need this because the class won't be visible when
	the macro is expanded at runtime."
	[]
	(new org.integratedmodelling.modelling.model.Scenario))

(defn j-make-context
	"Make a new instance of Model and return it. We need this because the class won't be visible when
	the macro is expanded at runtime."
	[]
	(new org.integratedmodelling.modelling.context.Context))

(defn j-make-agent
	"Make a new instance of Model and return it. We need this because the class won't be visible when
	the macro is expanded at runtime."
;; TODO the agent concept should select the class, using an annotation
;; or something.
	[concept]
	(new org.integratedmodelling.modelling.agents.ThinkGeolocatedAgent))
		
(defn j-make-measurement
	"Make a new instance of Model and return it."
	[]
	(new org.integratedmodelling.modelling.corescience.MeasurementModel))

(defn j-make-spank
	"Make a new instance of Model and return it."
	[]
	(new org.integratedmodelling.modelling.agents.SPANKModel))

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

(defn j-make-transform
	"Make a new instance of Model and return it."
	[concept value]
	(new org.integratedmodelling.modelling.context.FilteredTransformation concept value))

(defn register-model
	"Get the single instance of the model manager from the modelling plugin and register the passed model
	 with it."
	[model name]
	(.. org.integratedmodelling.modelling.ModellingPlugin (get) (getModelManager) (registerModel model (str *ns* "/" name))))
	
(defn register-scenario
	"Get the single instance of the model manager from the modelling plugin and register the passed model
	 with it."
	[model name]
	(.. org.integratedmodelling.modelling.ModellingPlugin (get) (getModelManager) (registerScenario model (str *ns* "/" name))))

(defn register-context
	"Get the single instance of the model manager from the modelling plugin and register the passed model
	 with it."
	[model name]
	(.. org.integratedmodelling.modelling.ModellingPlugin (get) (getModelManager) (registerContext model (str *ns* "/" name))))


(defn register-agent
	"Get the single instance of the model manager from the modelling plugin and register the passed model
	 with it."
	[model name]
	(.. org.integratedmodelling.modelling.ModellingPlugin (get) (getModelManager) (registerAgent model (str *ns* "/" name))))
		
(defn kw-pair? 
  [obj] 
  (and (seq? obj) (= (length obj) 2) (keyword? (first obj))))	

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

(defn transform-model 
  "Apply the passed clause to the passed model after transforming the
   argument according to the option. A map would be much more elegant
   but won't work in the bi-recursive pattern.  Just passes through
   anything that isn't handled - leave it to Java to validate the
   keyword."
  [model [option val]]
  (cond (= option :when)        (.applyClause model ":when"        (eval val))
        (= option :as)          (.applyClause model ":as"          (str val))
        (= option :optional)    (.applyClause model ":optional"    (eval val))
        (= option :keep)        (.applyClause model ":keep"        (map eval val))
        (= option :required)    (.applyClause model ":required"    (map eval val))
        (= option :discard)     (.applyClause model ":discard"     (map eval val))
        (= option :probability) (.applyClause model ":probability" (eval val))
        (= option :rate)        (.applyClause model ":rate"        (eval val))
        (= option :state)       (.applyClause model ":state"       (eval val))
        (= option :value)       (.applyClause model ":value"       (eval val))
        (= option :context)     (.applyClause model ":context"     (map configure-model (tl/group-with-keywords val)))
        (= option :observed)    (.applyClause model ":observed"    (map configure-model (tl/group-with-keywords val)))
        (= option :update)      (.applyClause model ":update"      (eval val))
        (= option :initialize)  (.applyClause model ":initialize"  (eval val))
        (= option :play)        (.applyClause model ":play"        (eval val))
        :otherwise              (.applyClause model (str option)   val)))

(defmacro model 
  "Return a new model for the given observable, defined using the
   given contingency structure and conditional specifications, or the
   given unconditional model if no contingency structure is supplied."
  [model-name observable & body]
  `(let [desc# (if (string? (first '~body)) (first '~body))
         contingency-model# (if (vector? (first (drop (if (nil? desc#) 0 1) '~body)))
                              (first (drop (if (nil? desc#) 0 1) '~body)))
         definition#        (drop (tl/count-not-nil (list desc# contingency-model#)) '~body)
         model#             (modelling/j-make-model)]
     (.setName model# ~model-name)
     (.setObservable  model# (if (seq? ~observable) (tl/listp ~observable) ~observable))
     (.setDescription model# desc#)
     ;; process the contingency model - should be one model with possible qualifying clauses
     ;; 	     (doseq [mdef# contingency-model#]
     ;;         	(.addContingency model# mdef# (meta contingency-model#))) 
     ;; keep the kw grouping for now
     (doseq [mdef# (tl/group-with-keywords contingency-model#)]
       (.addContingency model# (configure-model mdef#) (meta contingency-model#))) 
     ;; process the model definitions - one or more models and configuration keyword pairs
     (doseq [mdef# (tl/group-keywords definition#)] 
       (if (kw-pair? mdef#)
         (transform-model model# mdef#) 
         (.defModel model# (eval mdef#) (meta definition#))))  
     model#))
       
(defmacro scenario 
	"Return a new model for the given observable, defined using the given contingency 
	 structure and conditional specifications, or the given unconditional model if no 
	 contingency structure is supplied."
	[observable & body]
	 `(let [desc#  
	 					(if (string? (first '~body)) (first '~body))
	 				;; TODO unnecessary, remove
	 			  contingency-model# 
	        	(if (vector? (first (drop (if (nil? desc#) 0 1) '~body)))
	        		(first (drop (if (nil? desc#) 0 1) '~body)))
 	        definition# 
 	        	(drop (tl/count-not-nil (list desc# contingency-model#)) '~body)
 	        model# 
 	        	(modelling/j-make-scenario)]
 	      
 	     (.setObservable  model# (if (seq? ~observable) (tl/listp ~observable) ~observable))
 	     (.setDescription model# desc#)
 	      	     
        ; process the model definitions - one or more models or kw pairs
       (doseq [mdef# (tl/group-keywords definition#)]
          (if (kw-pair? mdef#)
            (transform-model model# mdef#) 
            (.addModel model# (eval mdef#) (meta definition#) nil)))
          
       model#))
     
       
(defmacro context 
	"Return a new context object that can be registered with a name and used to build matching contexts
   on demand."
	[& body]
	 `(let [desc#  
	 					(if (string? (first '~body)) (first '~body))
 	        definition# 
 	        	(drop (tl/count-not-nil (list desc#)) '~body)
 	        model# 
 	        	(modelling/j-make-context)]
 	      
 	     (.setDescription model# desc#)
 	      	     
        ; process the model definitions - one or more models or kw pairs
       (doseq [mdef# (tl/group-keywords definition#)] 
            (.add model# (eval mdef#) definition#))
          
       model#))
  
(defmacro tl-agent 
	"Return a new model for the given observable, defined using the given contingency 
	 structure and conditional specifications, or the given unconditional model if no 
	 contingency structure is supplied."
	[observable & body]
	 `(let [desc#  
	 					(if (string? (first '~body)) (first '~body))
	 				;; TODO unnecessary, remove
	 			  contingency-model# 
	        	(if (vector? (first (drop (if (nil? desc#) 0 1) '~body)))
	        		(first (drop (if (nil? desc#) 0 1) '~body)))
 	        definition# 
 	        	(drop (tl/count-not-nil (list desc# contingency-model#)) '~body)
 	        model# 
            ;; TODO pass the observable class so the proper agent can be created
 	        	(modelling/j-make-agent nil)]
 	      
 	     (.setObservable  model# (if (seq? ~observable) (tl/listp ~observable) ~observable))
 	     (.setDescription model# desc#)
 	      	     
        ; process the model definitions - one or more models
       (doseq [mdef# (tl/group-keywords definition#)]
          (if (kw-pair? mdef#)
            (transform-model model# mdef#)
            (.addModel model# (eval mdef#) (meta definition#))))
          
       model#))

(defmacro defmodel
	 "Define a model for the given observable, using the given contingency 
	  structure and conditional specifications, or the given unconditional model if no 
	  contingency structure is supplied."
		[model-name observable & body]
 		`(def ~model-name (modelling/register-model (eval '(modelling/model (str '~model-name) ~observable ~@body)) (str '~model-name))))
       
(defmacro defscenario
	 "Define a scenario."
		[model-name observable & body]
 		`(def ~model-name (modelling/register-scenario (eval '(modelling/scenario ~observable ~@body)) (str '~model-name))))
 
(defmacro defagent
	 "Define an agent."
		[model-name observable & body]
 		`(def ~model-name (modelling/register-agent (eval '(modelling/tl-agent ~observable ~@body)) (str '~model-name))))
  
(defmacro defcontext
	 "Define a context."
		[model-name & body]
 		`(def ~model-name (modelling/register-context (eval '(modelling/context ~@body)) (str '~model-name))))

(defn apply-scenario
  "Apply a scenario to a model, return a new model with the scenario applied."
  [scenario model]
  (.applyScenario model scenario)) 

(defn get-topology-in-location 
	"Return the spatial topology of the first observation found in the given location that
	matches the passed observable"
	[observable location] 
	(let [
			  extent  (geospace/get-topology-from-name location 256)
		    observ  (org.integratedmodelling.modelling.ObservationFactory/findObservation 
		    						(tl/conc observable) 
		    						(tl/get-session) 
		    						extent) 
		    nexten  (geospace/get-spatial-extent observ) 
			]
		nexten))

(defn get-native-topology-in-location 
	"Return the spatial topology of the first observation found in the given location that
	matches the passed observable, using the given locations envelope and the native
	resolution of the observation."
	[observable location] 
	(let [
			  shape   (geospace/get-shape-from-name location)
			  extent  (geospace/get-topology-from-shape shape 256)
		    observ  (org.integratedmodelling.modelling.ObservationFactory/findObservation 
		    						(tl/conc observable) 
		    						(tl/get-session) 
		    						extent) 
		    nexten  (geospace/get-spatial-extent observ)
		    result  (geospace/get-matching-native-grid shape nexten) 
			]
		(.getImplementation (tl/create-object result))))     

(defn get-native-topology-at-shape 
	"Return the spatial topology of the first observation found in the given location that
	matches the passed observable, using the given locations envelope and the native
	resolution of the observation."
	[observable shape] 
	(let [
			  extent  (geospace/get-topology-from-shape shape 256)
		    observ  (org.integratedmodelling.modelling.ObservationFactory/findObservation 
		    						(tl/conc observable) 
		    						(tl/get-session) 
		    						extent) 
		    nexten  (geospace/get-spatial-extent observ)
		    result  (geospace/get-matching-native-grid shape nexten) 
			]
		(.getImplementation (tl/create-object result))))     

(defn run-at-shape 
	"Build, contextualize and return the first matching observation for the passed model. Unresolved dependencies
	will be looked up in the kbox of kboxes (the KBoxManager). The next parameter should resolve to a shape in a 
	known gazetteer. The last, if provided, is the max linear resolution for the grid extent desired. If not
	provided, the resolution will be the native resolution of the first observation found for the model
	observable."
	([model-id shape]
	(let [model   (.. org.integratedmodelling.modelling.model.ModelFactory (get) (requireModel (str model-id)))
			  extent  (get-native-topology-at-shape (.getObservable model) shape)
		    kbox    (org.integratedmodelling.thinklab.kbox.KBoxManager/get)
		    qresult (.. org.integratedmodelling.modelling.model.ModelFactory (get) (run model kbox (tl/get-session) (geospace/topology-array extent)))]
		(if (> (.getTotalResultCount qresult) 0) 
				   (.getImplementation (.getObject (.getResult qresult 0 (tl/get-session)))))))
	([model-id shape resolution]
	(let [model   (.. org.integratedmodelling.modelling.model.ModelFactory (get) (requireModel (str model-id)))
			  extent  (geospace/get-topology-from-shape shape resolution)
		    kbox    (org.integratedmodelling.thinklab.kbox.KBoxManager/get)
		    qresult (.. org.integratedmodelling.modelling.model.ModelFactory (get) (run model kbox (tl/get-session) extent))]
		(if (> (.getTotalResultCount qresult) 0) 
				   (.getImplementation (.getObject (.getResult qresult 0 (tl/get-session))))))))
		    
(defn run-at-location 
	"Build, contextualize and return the first matching observation for the passed model. Unresolved dependencies
	will be looked up in the kbox of kboxes (the KBoxManager). The next parameter should resolve to a shape in a 
	known gazetteer. The last, if provided, is the max linear resolution for the grid extent desired. If not
	provided, the resolution will be the native resolution of the first observation found for the model
	observable."
	([model-id extent-id]
	(let [model   (.. org.integratedmodelling.modelling.model.ModelFactory (get) (requireModel (str model-id)))
			  extent  (get-native-topology-in-location (.getObservable model) extent-id)
		    kbox    (org.integratedmodelling.thinklab.kbox.KBoxManager/get)
		    qresult (.. org.integratedmodelling.modelling.model.ModelFactory (get) (run model kbox (tl/get-session) (geospace/topology-array extent)))]
		(if (> (.getTotalResultCount qresult) 0) 
				   (.getImplementation (.getObject (.getResult qresult 0 (tl/get-session)))))))
	([model-id extent-id resolution]
	(let [model   (.. org.integratedmodelling.modelling.model.ModelFactory (get) (requireModel (str model-id)))
			  extent  (geospace/get-topology-from-name extent-id resolution)
		    kbox    (org.integratedmodelling.thinklab.kbox.KBoxManager/get)
		    qresult (.. org.integratedmodelling.modelling.model.ModelFactory (get) (run model kbox (tl/get-session) extent))]
		(if (> (.getTotalResultCount qresult) 0) 
				   (.getImplementation (.getObject (.getResult qresult 0 (tl/get-session))))))))

(defmacro classification
	"The states of a classification model are concepts. All states must be direct children of the main observable
	of the model."
	[observable & specs]
	`(let [model# (modelling/j-make-classification)] 
 	   (.setObservable model# 
	   			(if (or (not (seq? ~observable)) (nil? (namespace (first '~observable)))) 
 	   					(if (seq? ~observable) (tl/listp ~observable) ~observable) 
 	   					(eval ~observable)))
		 (doseq [classifier# (partition 2 '~specs)]
		 	   (if  (and  (keyword? (first classifier#)) (not (= :otherwise (first classifier#)))) 
		 	   		  (transform-model model# classifier#) 
		 	   		  (.addClassifier model# (tl/unquote-if-quoted (first classifier#)) (eval (second classifier#)))))
 	   model#))

(defmacro classification-cleaner
	"The states of a classification model are concepts. All states
     must be direct children of the main observable of the model."
	[observable & specs]
    (let [interned-function-call? (comp namespace first)]
      `(let [model# (modelling/j-make-classification)]
         (.setObservable model# (if (seq? ~observable)
                                  (if (interned-function-call? '~observable)
                                    (eval ~observable)
                                    (tl/listp ~observable))
                                  ~observable))
		 (doseq [[key# val# :as classifier#] (partition 2 '~specs)]
           (if (and (keyword? key#) (not= :otherwise key#))
             (transform-model model# classifier#)
             (.addClassifier model# (tl/unquote-if-quoted key#) (eval val#))))
         model#)))

(defmacro ranking
	"Rankings describe their states as numeric values that have an ordinal relationship and optionally
	a scale. Rankings of different scales are mediated appropriately."
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
		 	   		  (transform-model model# classifier#))))
 	   model#))

(defmacro binary-coding
	 "A binary coding is a numeric model that will mediate anything non-zero to 1."
   [observable & body]
   `(ranking ~observable :binary true ~@body)) 

(defmacro numeric-coding
   "A numeric coding is like a numeric ranking but no ordinal assumption is made on the states."
   [observable & body]
   `(ranking ~observable :numeric-classification true ~@body)) 
	
(defmacro categorization
	"Categorizations have string tags as states. They hold little semantics and should only be used to
	 handle hard-to-annotate datasets."
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
		 	   		  (transform-model model# classifier#) )))
 	    model#))
	
(defmacro count
   "An enumeration is a count of individual objects, possibly distributed over an extent. It should have
    units, but these should only have the extent components in them, e.g. /km^2*year. If the enumeration is
    given no units, it's translated into an abundance ranking. For now there is a limitation in the syntax:
    enumerations with no units cannot have other metadata in the form, i.e. they can only contain the 
    observable."
  ([observable]
   `(ranking ~observable :count true))
  ([observable units & body]
   `(measurement ~observable ~units :count true ~@body))) 	
	
(defmacro probabilistic-ranking
   "Same as a numeric ranking but the states are distributions. Unimplemented."
   [observable & body]
   `(ranking ~observable :probabilistic true ~@body))

(defmacro probabilistic-measurement   
   "Same as a numeric measurement but the states are distributions. Unimplemented."
   [observable units & body]
   `(measurement ~observable ~units :probabilistic true ~@body)) 

(defmacro identification
	"Create an identification model. The observable can only be a semantic object."
	[observable & body]
	`(let [model# 
 	        	(modelling/j-make-observation)] 
 	   (.setObservable model# (if (seq? ~observable) (tl/listp ~observable) ~observable))
 	   (if (not (nil? '~body)) 
				(doseq [classifier# (partition 2 '~body)]
		 	   	(if  (keyword? (first classifier#)) 
		 	   		  (transform-model model# classifier#))))
 	   model#))

;; TODO rename this to agent or something when the seas are quiet. It can be nice to have, 
;; particularly when passing closures to create agents and define behaviors. Can build a CA
;; in 2 minutes with it.
(defmacro spank
	"Create an generalized agent model, whose final implementation is chosen based on the
   observable, just like SPAN but simpler to call and without the balls."
	[observable & body]
	`(let [model# 
 	        	(modelling/j-make-spank)] 
 	   (.setObservable model# (if (seq? ~observable) (tl/listp ~observable) ~observable))
 	   (if (not (nil? '~body)) 
				(doseq [classifier# (partition 2 '~body)]
		 	   	(if  (keyword? (first classifier#)) 
		 	   		  (transform-model model# classifier#))))
 	   model#))


(defmacro bayesian
	"Create a bayesian model. To be deleted in favor of a proper set of classifications with CPTs."
	[observable & body]
	`(let [model# 
 	        	(modelling/j-make-bayesian)] 
 	   (.setObservable model# (if (seq? ~observable) (tl/listp ~observable) ~observable))
 	   (if (not (nil? '~body)) 
				(doseq [classifier# (partition 2 '~body)]
		 	   	(if  (keyword? (first classifier#)) 
		 	   		  (transform-model model# classifier#))))
 	   model#))
 	   
(defmacro transform
  "To be used within a defcontext specification. Create a transformation object to modify states 
   during contextualization."
  [concept value & filter-objs]
  `(let [ret# (modelling/j-make-transform (tl/conc ~concept) ~value)]
     (when '~filter-objs
       (doseq [filt# '~filter-objs]
         (.addFilter ret# (eval filt#))))
     ret#))       
         
;; -------------------------------------------------------------------------------------------------------
;; inquiry, extraction etc
;; -------------------------------------------------------------------------------------------------------

(defn binary? 
	"Returns true if the datasource encodes a binary distribution that represents a yes/no situation for
	 a classified observation. If this returns true, (get-data) will return the probability of the true
	 case."
	[datasource]
	(not (nil? (.get (.getMetadata datasource) "truecase"))))

(defn probabilistic?
	"True if the given datasource is a discrete distribution. If so, uncertainty info can be
	extracted using get-uncertainty"
	[datasource]
	(instance? org.integratedmodelling.modelling.data.CategoricalDistributionDatasource datasource))
	
(defn get-uncertainty
	"Return uncertainty information from a datasource as an array of doubles."
	[datasource]
	(.get (.getMetadata datasource) "uncertainty"))
	
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
	(.get (.getMetadata datasource) "continuous_dist_breakpoints"))
	
(defn encodes-continuous-distribution? 
	"True if the given datasource is the discrete encoding of a continuous probability distribution, 
 	 meaning that get-dist-breakpoints will not throw an exception when called."
	[datasource]
	(not (nil? (.get (.getMetadata datasource) "continuous_dist_breakpoints"))))

(defn write-netcdf
	"Write out a netcdf file with all the states linked to the passed observation"
	[observation filename]
	(doto (new org.integratedmodelling.modelling.storage.NetCDFArchive)
		(.setObservation observation)
		(.write filename)))

