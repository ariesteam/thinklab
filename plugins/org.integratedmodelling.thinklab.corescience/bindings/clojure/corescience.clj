;; -----------------------------------------------------------------------------------------
;; Clojure bindings for the Corescience plugin
;; VERY incomplete, stubs only
;; @author Ferdinando Villa
;; @date Nov 13, 2008
;; -----------------------------------------------------------------------------------------

(in-ns 'tl)

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
	 contextualize it so that its context reflects the union of all contexts. Return a 
	 list containing the new observation and a list of the contextualized observations."
	[o-list]
	nil)
	
(defn get-dependencies
	"Retrieve and return all the observations that the passed one depends upon."
	[observation]
	nil)
	
(defn get-contingencies
	"Retrieve and return all the observations that the passed one is contingent to."
	[observation]
	nil)
	
(defn get-extents
	"Retrieve and return all the extents that the passed observation depends on."
	[observation]
	nil)
	
(defn extensive?
	""
	[observation]
	false)
	
(defn intensive?
	"" 
	[observation]
	false)
	
(defn measurement?
	""
	[observation]
	false)
	
(defn classification?
	""
	[observation]
	false)
	
(defn identification?
	""
	[observation]
	false)
	
(defn extent?
	""
	[observation]
	false)
	
(defn count?
	""
	[observation]
	false)

(defn quantification?
	""
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
