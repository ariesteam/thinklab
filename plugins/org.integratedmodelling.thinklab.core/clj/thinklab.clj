;; ------------------------------------------------------------------------------------------
;; Basic Thinklab bindings for Clojure
;; 
;; @author Ferdinando Villa
;; @date 11/11/2008
;; ------------------------------------------------------------------------------------------
(ns tl
   (:import 
   		(org.integratedmodelling.clojure  Clojure)
   		(org.integratedmodelling.thinklab.kbox KBoxManager)
   		(org.integratedmodelling.thinklab.exception ThinklabValidationException)
    	(org.integratedmodelling.thinklab Thinklab KnowledgeManager)))

(def *session* nil)

(defn load-bindings
	"Load the Clojure bindings for the passed plugin (can use a partial name if not
	 ambiguous). If the bindings need to see the classes of the plugin they're part
	 of, the latter must have been loaded with reverse-lookup=true."
	 ( [plugin-name]
	 (. (. Thinklab (resolvePlugin (str plugin-name) true)) (loadLanguageBindings "clojure")))
	 ( [pname & args] 
	 	(do
	 		(load-bindings pname)
	 		(apply load-bindings args))))

(defn get-session
	"Retrieve the current session. Throw an exception if no session was passed to the interpreter
	 in the current thread, and true is passed as an argument."
	([] (if (nil? (resolve 'tl/*session*)) nil (eval 'tl/*session*)))
	([complain]
	(do 
		(if (and (nil? (resolve 'tl/*session*)) complain) 
			(throw (new ThinklabValidationException "no session is defined")))
		(if (nil? (resolve 'tl/*session*)) nil (eval 'tl/*session*)))))

(defn get-new-session
  []
  (new org.integratedmodelling.thinklab.owlapi.Session))

(defn plist 
	"Translates a polylist into a sequence for Thinklab->Clojure bridging of datastructures"
	[polylist]
	(. Clojure (p2list polylist)))

(defn listp 
	"Translates a sequence into a polylist for Clojure->Thinklab bridging of datastructures"
	[sequence]
	(. Clojure (list2p sequence)))
	
(defn require-plugin
	"Ensures that the specified thinklab plugin is loaded, and load its bindings if any."
	[pname]
	(do (.. 
			(. Thinklab (get)) 
			  (getManager) 
			  (activatePlugin (str pname)))) 
		(load-bindings pname))
		
(defn get-plugin-resource
	"Return an URL corresponding to the resource in the classpath of a passed plugin, or nil if absent."
	[plugin-name resource-path]
	(.getResourceURL (. Thinklab (resolvePlugin (str plugin-name) true)) resource-path))

(defn conc
	"A passed concept (or nil) is returned unmodified. A list is taken to be a concept definition and passed
	to a session to create the return value. If the passed object is not a concept, its string
	value is taken to be a semantic type and the correspondent concept is returned. If no concept
	exists for that object, an exception is thrown."
	[stype]
	(if (nil? stype) 
		nil
		(if (instance? org.integratedmodelling.thinklab.interfaces.knowledge.IConcept stype)
			stype
			(if (list? stype)
				(.createConcept (tl/get-session) (tl/listp stype)) 
				(.. KnowledgeManager (get) (requireConcept (str stype)))))))

(defn find-concept
	"A passed concept is returned unmodified. If the passed object is not a concept, its string
	value is taken to be a semantic type and the correspondent concept is returned. If no concept
	exists for that object, nil is returned without complaint."
	[stype]
	(if (instance? org.integratedmodelling.thinklab.interfaces.knowledge.IConcept stype)
		stype
		(.. KnowledgeManager (get) (retrieveConcept (str stype)))))

(defn prop
	"Returns the concept named by the passed semantic type string."
	[stype]
	(.. KnowledgeManager (get) (requireProperty (str stype))))
	  
(defn lit
   "Returns the literal IValue for the passed concept and string value."
   [concept textval]
   (.. KnowledgeManager 
   			(get) 
   			(validateLiteral (tl/conc concept) textval)))
		
(defn get-property-values
	"Return a list of the values of a relationship (or a map of all relationships to their values
	 if the property is not specified) of an object. The values returned are stripped of semantics."
	([object]
		(. Clojure (getRelationships object true)))
	([object property]
		(. Clojure (getPropertyValues object property))))
		
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
	 (concat (. (get-session true) (loadObjects (str resource)))))
	([resource session]
	(concat (. session (loadObjects (str resource))))))

(defn create-object 
	"Create an instance in the current session from its list definition. Expects a polylist - TODO
	 check the argument and convert if necessary."
	 [polylist]
	 (.createObject (get-session true) polylist))
	
(defn is? 
	"Returns true if a concept or an instance is subsumed by another concept"
	[knowledge concept]
	(if (nil? knowledge) false (. knowledge (is (conc concept))))) 
	
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
	([kbox constraint]
		(.query kbox (new org.integratedmodelling.thinklab.constraint.Constraint (listp constraint))))
	([kbox constraint fields]
	  (.query kbox 
	  		(new org.integratedmodelling.thinklab.constraint.Constraint (listp constraint))
				(make-array (map str fields)))))
					
(defn serialize-object
	"Serialize the passed instance to a list"
	[inst]
	(plist (. inst (toList nil))))
	
(defn dictionary
	"Extract a dictionary of symbols from the given concept, returning all the class names that are
	 subsumed by it."
	 [concept]
;; TODO
	 ())
	 
(defn hierarchy
	 "Extract the subsumption hierarchy from the passed concept as a zip tree. All
	  concepts that are children of more than one other concept appear as many times as
	  necessary to avoid loops."
	  [concept]
;; TODO
	 ())
	 
(defn get-concept-definition
	"Return the list that defines all the passed concepts' restrictions. Usable as a
	 constraint in a query."
	 [concept]
	 (. (conc concept) (getDefinition)))
	 

;; Gary's magic to add its own source code to a fun
(defmacro with-source-code [[defsym name & body :as form]]
  `(~defsym ~(with-meta name {:source-code `'~form}) ~@body))

(defmacro get-source-code [var-name] `(:source-code (meta (var ~var-name))))

;; --------------------------------------------------------------------------------
;; command interface
;; --------------------------------------------------------------------------------

;; run a command
;(defmacro cmd 
;	"Run a thinklab command passing the arguments"
;	[command & params]
;	`())

;; create a command	
;(defmacro define-command
;	""
;	[command-name & arguments] 
;	`())


	