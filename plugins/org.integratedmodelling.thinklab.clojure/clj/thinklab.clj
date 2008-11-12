;; ------------------------------------------------------------------------------------------
;; Clojure extension for Thinklab access
;; Very basic for now
;; @author Ferdinando Villa
;; @date 11/11/2008
;; ------------------------------------------------------------------------------------------
(ns tl)

(defn require-plugin
	"Ensures that the specified plugin is loaded"
	[pname]
	(.. 
		(. org.integratedmodelling.thinklab.Thinklab (get)) 
			(getManager) 
			(activatePlugin pname)))

(defn c
	"Returns the concept named by the passed semantic type."
	[stype]
	(.. org.integratedmodelling.thinklab.KnowledgeManager (get) (requireConcept stype)))
	  
(defn lit
   "Returns the literal IValue for the passed concept and string value."
   [concept textval]
   (.. org.integratedmodelling.thinklab.KnowledgeManager 
   			(get) 
   			(validateLiteral concept textval nil)))

(defn load-objects
	"Load instances from a source into a session and return them as a sequence"
	[resource session]
	(concat (. session (loadObjects resource))))
	
(defn is 
	"Returns true if a concept or an instance is subsumed by another concept"
	[knowledge conc]
	(. knowledge (is conc))) 
	
(defn get-type 
	"Returns the type of the passed object"
	[object]
	(. object (getDirectType)))
	
(defn kbox 
	"Returns a named kbox"
	[kname]
	(.. org.integratedmodelling.thinklab.kbox.KBoxManager
		(get)
			(retrieveGlobalKBox kname)))