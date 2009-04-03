(ns modelling)

(tl/load-bindings 'org.integratedmodelling.thinklab.corescience)

(defn j-make-measurement
	"Make a new instance of Model and return it."
	[]
	(new org.integratedmodelling.modelling.corescience.MeasurementModel))

(defn j-make-count
	"Make a new instance of Model and return it."
	[]
	(new org.integratedmodelling.modelling.corescience.CountModel))

(defn j-make-classification
	"Make a new instance of Model and return it."
	[]
	(new org.integratedmodelling.modelling.corescience.ClassificationModel))

(defn j-make-observation
	"Make a new instance of Model and return it."
	[]
	(new org.integratedmodelling.modelling.corescience.ObservationModel))

(defn j-make-ranking
	"Make a new instance of Model and return it."
	[]
	(new org.integratedmodelling.modelling.corescience.RankingModel))

(defmacro classification
	""
	[observable & specs]
	'(let [model# 
 	        	(modelling/j-make-classification)] 
 ;	    ()    	
 	  model#))

(defmacro enumeration
	""
	[observable & specs]
	'(let [model# 
 	        	(modelling/j-make-count)] 
 ;	    ()  	
 	  model#))
	
(defmacro ranking
	""
	[observable & specs]
	'(let [model# 
 	        	(modelling/j-make-ranking)] 
 ;	    () 	
 	  model#))
	
(defmacro measurement
	""
	[observable & specs]
	'(let [model# 
 	        	(modelling/j-make-measurement)] 
 ;	    ()
 	  model#))
	
(defmacro identification
	""
	[observable & specs]
	'(let [model# 
 	        	(modelling/j-make-observation)] 
 ;	    ()
 	  model#))

;(defmacro discrete-noisymax-model
;	""
;	[observable & specs]
;	'(let [model# 
; 	        	(modelling/j-make-noisymax)]
; 	    ()
; 	  model#))
 	  
;(defmacro discrete-random-model
;	""
;	[observable & specs]
;	'(let [model# 
; 	        	(modelling/j-make-random)]
; 	    ()
; 	  model#))
 	  
;(defmacro discrete-continuous-model
;	""
;	[observable & specs]
;	'(let [model# 
; 	        	(modelling/j-make-random-continuous)]
; 	    ()
; 	  model#))
