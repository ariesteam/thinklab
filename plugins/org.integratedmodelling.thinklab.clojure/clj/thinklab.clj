;; ------------------------------------------------------------------------------------------
;; Basic Thinklab bindings for Clojure
;; 
;; @author Ferdinando Villa
;; @date 11/11/2008
;; ------------------------------------------------------------------------------------------
(ns tl
   (:import 
   		(org.integratedmodelling.clojure ClojureBridge)
   		(org.integratedmodelling.thinklab.kbox KBoxManager)
   		(org.integratedmodelling.thinklab.exception ThinklabValidationException)
    	(org.integratedmodelling.thinklab Thinklab KnowledgeManager)))

(defn get-session
	"Retrieve the current session. Throw an exception if no session was passed to the interpreter
	 in the current thread, and true is passed as an argument."
	([] (eval '*session*))
	([complain]
	(do 
		(def sess (eval '*session*))
		(if (and (nil? sess) complain) 
			(throw (new ThinklabValidationException "no session is defined")))
		sess)))

(defn plist 
	"Internal: translates a polylist into a sequence"
	[polylist]
	(. ClojureBridge (p2list polylist)))

(defn listp 
	"Internal: translates a sequence into a polylist"
	[sequence]
	(. ClojureBridge (list2p sequence)))
	
(defn require-plugin
	"Ensures that the specified thinklab plugin is loaded"
	[pname]
	(.. 
		(. Thinklab (get)) 
			(getManager) 
			(activatePlugin pname)))

(defn c
	"Returns the concept named by the passed semantic type string."
	[stype]
	(.. KnowledgeManager (get) (requireConcept stype)))
	  
(defn lit
   "Returns the literal IValue for the passed concept and string value."
   [concept textval]
   (.. KnowledgeManager 
   			(get) 
   			(validateLiteral concept textval nil)))
		
(defn get-property-values
	"Return a list of the values of a relationship (or a map of all relationships to their values
	 if the property is not specified) of an object. The values returned are stripped of semantics."
	([object]
		(. ClojureBridge (getRelationships object true)))
	([object property]
		(get (. ClojureBridge (getRelationships object true)) property)))
		
(defn get-property-value 
	"Return the value of the given property, or nil if not present. It is assumed that the
	property has one value in the object; any further values are ignored."
	[object property]
	(first (get-property-values object property)))

(defn get-implementation
	"Return the Java object that has been created as the implementation of the passed instance."
	[object]
	(. object (getImplementation)))

(defn load-objects
	"Load instances from a source into a session and return them as a sequence"
	([resource]
	 (concat (. (get-session true) (loadObjects resource))))
	([resource session]
	(concat (. session (loadObjects resource)))))
	
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
	(.. KBoxManager
		(get)
			(retrieveGlobalKBox kname)))
			
(defn query
	"Query the passed kbox with a constraint list, return a list of result objects"
	[kbox constraint]
;; TODO
	())
	
(defn serialize-object
	"Serialize the passed instance to a list"
	[inst]
	(plist (. inst (toList nil))))