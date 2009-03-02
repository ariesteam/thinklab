;; -----------------------------------------------------------------------------------------
;; Clojure bindings for the Corescience plugin
;; VERY incomplete, stubs only
;; @author Ferdinando Villa
;; @date Nov 13, 2008
;; -----------------------------------------------------------------------------------------

(ns corescience)

(defn contextualize
	"Create the states in an observation tree. Returns the IObservation from the result of contextualization."
	 [observation]
	 (.. (org.integratedmodelling.corescience.contextualization.Compiler 
	 				(contextualize observation (tl/get-session))) (getImplementation)))

(defn harmonized-intersection 
	"Create a master observation that is contingent to all those in the passed list, and 
	 contextualize it so that its context reflects the union of all contexts. Return a 
	 list containing the new observation and a list of the contextualized observations."
	[o-list]
	nil)

(defn harmonized-union 
	"Create a master observation that is dependent on all those in the passed list, and 
	 contextualize it so that its context reflects the intersection of all contexts. Return a 
	 list containing the new observation and a list of the contextualized observations."
	[o-list]
	nil)
	
(defn get-dependencies
	"Retrieve and return all the observations that the passed one depends upon (there can be no
     observation unless the dependencies are also observed)."
	[observation]
	(tl/get-property-values observation "observation:dependsOn"))
	
(defn get-contingencies
	"Retrieve and return all the observations that the passed one is contingent to (the act of
     observing the passed one causes the observation of the contingent ones)."
	[observation]
	(tl/get-property-values observation "observation:contingentTo"))
	
(defn get-extents
	"Retrieve and return all the extents IObservations that the passed observation depends on. Note:
     this returns instance implementations (IObservation), not instances."	
	[observation]
	(.. observation (getImplementation) (getExtentDependencies)))

(defn get-observable-class
     ""
     [observation]
     (.. observation (getImplementation) (getObservableClass)))

(defn get-state
     ""
     [observation]
     (.. observation (getImplementation) (getDataSource) (getDataAsDoubles)))

(defn get-extent
	"Retrieve and return the extent that observes the given concept (e.g. space) or nil. Note:
     this returns Java implementations of instances (IObservation), not IInstances."
	[observation concept]
	(.. observation (getImplementation) (getExtent concept)))

(defn get-conceptual-model
   "Retrieve the conceptual model of the passed observation"
   [observation]
	(.. observation (getImplementation) (getConceptualModel)))

(defn get-data-source
   "Retrieve the data source of the passed observation, or nil"
   [observation]
	(.. observation (getImplementation) (getDataSource)))

(defn extensive?
	"True if the passed observation is a measurement and its observable is an extensive physical property.
     (such as mass)."
	[observation]
	false)
	
(defn intensive?
	"True if the passed observation is a measurement and its observable is an extensive physical property
     (such as temperature)."	
	[observation]
	false)
	
(defn measurement?
	"True if the passed observation is a measurement."
	[observation]
	(instance? 
        org.integratedmodelling.corescience.implementations.cmodels.MeasurementModel 
        (get-conceptual-model observation)))	
	
(defn classification?
	"True if the passed observation is a classification."
	[observation]
	(instance? 
        org.integratedmodelling.corescience.implementations.cmodels.ClassificationModel 
        (get-conceptual-model observation)))	
	
(defn identification?
	"True if the passed observation is an identification."
	[observation]
	false)
	
(defn extent?
	"True if the passed observation is an extent (can determine multiple states for observations that
     have it in its context)."
	[observation]
	false)
	
(defn count?
	"True if the passed observation is a count defined over some concrete domain."
	[observation]
	false)

(defn quantification?
	"True if the passed observation is a quantification (can have numeric states)."
	[observation]
	false)
	
(defn get-states
	"Return a map that associates each observations' name to its state array. Contextualize if
	 necessary."
	[observation] 
	())

(defn get-numeric-states
	"Return a map like get-states, but only for those observations that are measurements."
	[observation] 
	())
	
(defn get-conceptual-model 
	"Return the conceptual model for the passed observation."
	[observation]
	())
	
(defn get-datasource 
	"Return the conceptual model for the passed observation."
	[observation]
	())
	
(defn get-units
	"Returns the conceptual model for the passed observation, making sure it is a measurement."
	[observation]
	())
	
(defn get-multiplicity
	"Returns the total multiplicity of the extents."
	[observation]
	0)
	
(defn get-multiplicities
	"Returns a map of multiplicities indexed by observable class of each extent."
	[observation]
	())
	
(defn distributed-in?
	"True if the observation has an extent that is an observation of the passed concept."
	[observation concept]
	false)

(defn depends-on?
	"True if the observation depends on an observation of the passed concept."
	[observation concept]
	false)
	
(defn contingent-to?
	"True if the observation is contingent to an observation of the passed concept."
	[observation concept]
	false)

;; ================================================================================================
;; utils
;; ================================================================================================

(defn map-dependent-states 
    "Given a contextualized observation, return a map associating the states of all dependencies 
     to the concept they observe"
    [observation]
    (loop [observations (get-dependencies observation)
           state-map {}]
          (if (empty? observations)
              state-map
              (let [obs (first observations)]
                 (recur (rest observations) 
                          (assoc state-map
                             (get-observable-class obs)
                             (get-state obs)))))))
