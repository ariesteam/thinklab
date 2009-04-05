(ns modelling)
	
(defn j-make-random
	""
	[rtype]
	(new org.integratedmodelling.modelling.random.RandomModel rtype))
	
(defmacro noisymax-classification
  ""
	[observable & body]
	'(let [model# 
 	        	(modelling/j-make-random 2)]
 	  model#))
 	  
(defmacro random-classification
	""
	[observable & body]
	'(let [model# 
 	        	(modelling/j-make-random 0)]
 	  model#))
 	  
(defmacro random-function
	""
	[observable function & body]
	'(let [model# 
 	        	(modelling/j-make-random 1)]
	  model#))