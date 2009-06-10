(ns modelling)

; root binding for kbox variable
(def *_kbox_* nil)

(defn j-make-kbox-handler
	[]
	(new org.integratedmodelling.modelling.data.KBoxHandler (tl/get-session)))
	
(defn j-make-object-handler
	[concept kbox]
	(new org.integratedmodelling.modelling.data.InstanceHandler (tl/get-session) concept kbox))

(defmacro object
	"Define an instance. Forward references (InstanceHandler) may also be returned, but will only 
	 be allowed within a with-kbox form."
	[concept & body]
	`(let [conc# (str ~concept)
				 inst# (j-make-object-handler conc# (eval '*_kbox_*))] 
		(doseq [prop# '~body]
			(if (string? prop#)
				(.addAnnotation inst# prop#)
				(.addProperty inst# (tl/prop (first prop#)) (eval (second prop#)))))
		(.getObject inst#)))

(defmacro kbox 
	"Define a kbox and return it."
	[id uri & body]
	 `(let [kbox#   (modelling/j-make-kbox-handler)]
			(.createKbox kbox# (str '~id) ~uri '~body)))

(defmacro with-kbox
	"The first argument must be a kbox. All other arguments must eval to knowledge (usually objects). 
	 Each argument can be followed by an arbitrary number of keyword-value pairs. 
	 Will eval all the s-expressions in body and if they represent knowledge, store them in the passed kbox. 
	 Behavior can be modified using the keywords."
	[& body]
	 `(let [body#  (tl/group-with-keywords '~body)
	 	 	    kbox#   (modelling/j-make-kbox-handler)
	 	 	    ]
	 	 	 (binding [*_kbox_* kbox#]
				 (.setKbox kbox# (eval (first (first body#))) (second (first body#)))	      	     
 		     (if (not (.isDisabled kbox#)) 
 		     		 (doseq [mdef# (rest body#)]
    	     		  (.addKnowledge kbox# (eval (first mdef#)) (second mdef#)))) 
      	 (.getKbox kbox#))))