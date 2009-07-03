(ns modelling)

; root binding for kbox variable
(def *_kbox_* nil)

(defn j-make-kbox-handler
	[]
	(new org.integratedmodelling.modelling.data.KBoxHandler (tl/get-session)))
	
(defn j-make-object-handler
	[concept kbox]
	(new org.integratedmodelling.modelling.data.InstanceHandler (tl/get-session) concept kbox))

(defn get-metadata-extractor
	"Return a metadata extractor whose extractMetadata method will apply a Clojure function map to an instance"
	[fnmap]
	(proxy [org.integratedmodelling.thinklab.interfaces.storage.IMetadataExtractor] []
		(extractMetadata [instance] 
			(org.integratedmodelling.modelling.data.KBoxHandler/fixMetadata 
				(tl/map-keyed-functions fnmap instance)))))

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
	"Define a kbox and return it"
	[id uri & body]
	 `(let [kbox#   (modelling/j-make-kbox-handler)]
			(.createKbox kbox# (str '~id) ~uri '~body)))

(defmacro with-kbox
	"The first argument must be a kbox. All other arguments must eval to knowledge (usually objects). 
	 Each argument can be followed by an arbitrary number of keyword-value pairs. 
	 Will eval all the s-expressions in body and if they represent knowledge, store them in the passed kbox. If a 
	 (import url) form is passed, knowledge is imported from there. 
	 Behavior can be modified using the keywords."
	[& body]
	 `(let [body#  (tl/group-with-keywords '~body)
	 			  md-extractor# (eval (:metadata-generator (tl/assoc-map (second (first body#)))))
	 	 	    kbox#   (modelling/j-make-kbox-handler)
	 	 	    ]
	 	 	 (binding [*_kbox_* kbox#]
				 (.setKbox kbox# (eval (first (first body#))) (second (first body#)))	      	     
 		     (if (not (.isDisabled kbox#)) 
 		     		 (doseq [mdef# (rest body#)]
 		     		 		(if (and (seq? (first mdef#)) (= (str (first (first mdef#))) "import"))
 		     		 			(let [url# (str (eval (second (first mdef#))))]
 		     		 				(.setWithKbox (tl/get-session) (.getKbox kbox#) (modelling/get-metadata-extractor md-extractor#))
 		     		 				(.loadObjects (tl/get-session) url#)
 		     		 				(.setWithKbox (tl/get-session) nil nil))
 			     		 		(let [object# (eval (first mdef#))]
    	     		       (.addKnowledge kbox# object# (second mdef#) (tl/map-keyed-functions md-extractor# object#)))))) 
      	 (.getKbox kbox#))))
      	 