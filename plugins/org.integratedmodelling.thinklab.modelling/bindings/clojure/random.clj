(ns modelling)
	
(defn j-make-random
	""
	[rtype]
	(new org.integratedmodelling.modelling.random.RandomModel rtype))
	
(defmacro discrete-noisymax-model
  ""
	[observable & cpt]
	'(let [model# 
 	        	(modelling/j-make-random 2)]
 	  model#))
 	  
(defmacro discrete-random-model
	""
	[observable & cpt]
	'(let [model# 
 	        	(modelling/j-make-random 0)]
 	  model#))
 	  
(defmacro discrete-continuous-model
	""
	[observable function & cpt]
	'(let [model# 
 	        	(modelling/j-make-random 1)]
	  model#))