;; -----------------------------------------------------------------------------------------
;; Clojure bindings for the Corescience plugin
;; VERY incomplete, stubs only
;; @author Ferdinando Villa
;; @date Nov 13, 2008
;; -----------------------------------------------------------------------------------------

(ns tl)

(defn contextualize
	"Create the states in an observation tree. Ideally this should return new observations, but
	 that is quite expensive, so for now it will just return the same observation with the
	 added states."
	 [observation]
	 nil)

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
	(get-property-values observation "observation:dependsOn"))
	
(defn get-contingencies
	"Retrieve and return all the observations that the passed one is contingent to (the act of
     observing the passed one causes the observation of the contingent ones)."
	[observation]
	(get-property-values observation "observation:contingentTo"))
	
(defn get-extents
	"Retrieve and return all the extents that the passed observation depends on."
	[observation]
	nil)

(defn get-observable-class
     ""
     [observation]
     (.. observation (getImplementation) (getObservableClass)))

(defn get-extent
	"Retrieve and return the extents that observes the given concept (e.g. space)."
	[observation concept]
	nil)
	
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
	false)
	
(defn classification?
	"True if the passed observation is a classification."
	[observation]
	false)
	
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