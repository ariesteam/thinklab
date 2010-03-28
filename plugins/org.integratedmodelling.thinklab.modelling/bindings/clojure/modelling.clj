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
	
(defn j-make-scenario
	"Make a new instance of Model and return it. We need this because the class won't be visible when
	the macro is expanded at runtime."
	[]
	(new org.integratedmodelling.modelling.Scenario))

(defn j-make-agent
	"Make a new instance of Model and return it. We need this because the class won't be visible when
	the macro is expanded at runtime."
	[]
	(new org.integratedmodelling.modelling.agents.ThinkAgent))
		
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

(defn register-agent
	"Get the single instance of the model manager from the modelling plugin and register the passed model
	 with it."
	[model name]
	(.. org.integratedmodelling.modelling.ModellingPlugin (get) (getModelManager) (registerAgent model (str *ns* "/" name))))
		
(defn kw-pair? 
  [obj] 
  (and (seq? obj) (= (count obj) 2) (keyword? (first obj))))	

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
	"Apply the passed clause to the passed model after transforming the argument according to 
	 the keyword. A map would be much more elegant but won't work in the bi-recursive pattern.
	 Just passes through anything that isn't handled - leave it to Java to validate the keyword."
	[model clause]
	(cond (= (first clause) :when)
				(.applyClause model ":when" (tl/listp (second clause)))
				(= (first clause) :as)
				(.applyClause model ":as" (str (second clause)))
				(= (first clause) :optional)
				(.applyClause model ":optional" (eval (second clause)))
				(= (first clause) :optional)
				(.applyClause model ":required" (eval (second clause)))
				(= (first clause) :keep)
				(.applyClause model ":keep" (map eval (second clause)))
				(= (first clause) :discard)
				(.applyClause model ":discard" (map eval (second clause)))
				(= (first clause) :probability)
				(.applyClause model ":probability" (eval (second clause)))
				(= (first clause) :derivative)
				(.applyClause model ":derivative" (tl/listp (second clause)))
				(= (first clause) :state)
				(.applyClause model ":state" (eval (second clause)))
				(= (first clause) :context)
				(.applyClause model ":context" (map configure-model (tl/group-with-keywords (second clause))))
				(= (first clause) :observed)
				(.applyClause model ":observed" (map configure-model (tl/group-with-keywords (second clause))))
				(= (first clause) :random-walk)
				(.applyClause model ":random-walk" (eval (second clause)))
				(= (first clause) :movement)
				(.applyClause model ":movement" (eval (second clause)))
				(= (first clause) :death)
				(.applyClause model ":death" (eval (second clause)))
				(= (first clause) :update)
				(.applyClause model ":update" (eval (second clause)))
				(= (first clause) :random-move)
				(.applyClause model ":random-move" (eval (second clause)))
				(= (first clause) :metabolism)
				(.applyClause model ":metabolism" (eval (second clause)))
				(= (first clause) :initialize)
				(.applyClause model ":initialize" (eval (second clause)))
				(= (first clause) :play)
				(.applyClause model ":play" (eval (second clause)))
				:otherwise
				(.applyClause model (str (first clause)) (second clause))))

(defmacro model 
	"Return a new model for the given observable, defined using the given contingency 
	 structure and conditional specifications, or the given unconditional model if no 
	 contingency structure is supplied."
	[model-name observable & body]
	 `(let [desc#  
	 					(if (string? (first '~body)) (first '~body))
	        contingency-model# 
	        	(if (vector? (first (drop (if (nil? desc#) 0 1) '~body)))
	        		(first (drop (if (nil? desc#) 0 1) '~body)))
 	        definition# 
 	        	(drop (tl/count-not-nil (list desc# contingency-model#)) '~body)
 	        model# 
 	        	(modelling/j-make-model)]
 	       
 	     (.setName model# ~model-name)
 	     (.setObservable  model# (if (seq? ~observable) (tl/listp ~observable) ~observable))
 	     (.setDescription model# desc#)

 	     ; process the contingency model - as many models as we like, will build an id from all
 	     (doseq [mdef# contingency-model#]
         	(.addContingency model# mdef# (meta contingency-model#))) 
        
       
        ; process the model definitions - one or more models and configuration keyword pairs
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
 	        	(modelling/j-make-agent)]
 	      
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
	(let [model   (.. org.integratedmodelling.modelling.ModelFactory (get) (requireModel (str model-id)))
			  extent  (get-native-topology-at-shape (.getObservable model) shape)
		    kbox    (org.integratedmodelling.thinklab.kbox.KBoxManager/get)
		    qresult (.. org.integratedmodelling.modelling.ModelFactory (get) (run model kbox (tl/get-session) (geospace/topology-array extent)))]
		(if (> (.getTotalResultCount qresult) 0) 
				   (.getImplementation (.getObject (.getResult qresult 0 (tl/get-session)))))))
	([model-id shape resolution]
	(let [model   (.. org.integratedmodelling.modelling.ModelFactory (get) (requireModel (str model-id)))
			  extent  (geospace/get-topology-from-shape shape resolution)
		    kbox    (org.integratedmodelling.thinklab.kbox.KBoxManager/get)
		    qresult (.. org.integratedmodelling.modelling.ModelFactory (get) (run model kbox (tl/get-session) extent))]
		(if (> (.getTotalResultCount qresult) 0) 
				   (.getImplementation (.getObject (.getResult qresult 0 (tl/get-session))))))))
		    
(defn run-at-location 
	"Build, contextualize and return the first matching observation for the passed model. Unresolved dependencies
	will be looked up in the kbox of kboxes (the KBoxManager). The next parameter should resolve to a shape in a 
	known gazetteer. The last, if provided, is the max linear resolution for the grid extent desired. If not
	provided, the resolution will be the native resolution of the first observation found for the model
	observable."
	([model-id extent-id]
	(let [model   (.. org.integratedmodelling.modelling.ModelFactory (get) (requireModel (str model-id)))
			  extent  (get-native-topology-in-location (.getObservable model) extent-id)
		    kbox    (org.integratedmodelling.thinklab.kbox.KBoxManager/get)
		    qresult (.. org.integratedmodelling.modelling.ModelFactory (get) (run model kbox (tl/get-session) (geospace/topology-array extent)))]
		(if (> (.getTotalResultCount qresult) 0) 
				   (.getImplementation (.getObject (.getResult qresult 0 (tl/get-session)))))))
	([model-id extent-id resolution]
	(let [model   (.. org.integratedmodelling.modelling.ModelFactory (get) (requireModel (str model-id)))
			  extent  (geospace/get-topology-from-name extent-id resolution)
		    kbox    (org.integratedmodelling.thinklab.kbox.KBoxManager/get)
		    qresult (.. org.integratedmodelling.modelling.ModelFactory (get) (run model kbox (tl/get-session) extent))]
		(if (> (.getTotalResultCount qresult) 0) 
				   (.getImplementation (.getObject (.getResult qresult 0 (tl/get-session))))))))
		    