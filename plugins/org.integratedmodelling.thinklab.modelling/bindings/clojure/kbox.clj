(ns modelling)

(defn j-make-kbox-handler
	[]
	(new org.integratedmodelling.modelling.data.KBoxHandler))

(defmacro object
	"Define an instance. Forward references will only work if used within a with-kbox form."
	[concept & body]
	`(let [conc# (tl/conc ~concept)] 
		nil))

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
			 (.setKbox kbox# (first (first body#)) (second (first body#)))	      	     
 	     (doseq [mdef# (rest body#)]
         	(.addKnowledge kbox# (eval (first mdef#)) (second mdef#)))          	    	  	          
       (.getKbox kbox#)))