(ns aries.models.test
	(:refer-clojure)
  (:use [modelling :only (defmodel measurement classification)]))
	 
(defmodel valuable-mountain 'aestheticService:Mountain
   "Classifies an elevation model into three levels of provision of beautiful mountains"
   [] ; unconditional for now, later it may reflect variable preferences
   (classification 
   		(measurement  'geophysics:Elevation "m")
   		"2000)"       'aestheticService:NoMountain 
   		"[2000-2750)" 'aestheticService:SmallMountain 
   		"[2750"       'aestheticService:LargeMountain ))
   		    		 
(defmodel aesthetic-enjoyment-provision 'aestheticService:SensoryEnjoyment
 	 "Unconditional bayesian model of sensory enjoyment provision."
 	 []
 	 (discrete-noisymax-model 
 	 		'aestheticService:SensoryEnjoyment
 	 		; dependencies
 	 	  ((discrete-random-model valuable-mountain)
 	 	   (discrete-random-model 'aestheticService:WaterBody))
 	 	  ; cpt - others are uniform
 	 	  (0.4 0.3 0.2 0.1 0.2 0.3 0.3 0.2 0.0 0.0 0.0 1.0 0.7 0.1 
 	 	   0.1 0.1 0.5 0.2 0.2 0.1 0.0 0.0 0.0 1.0 0.0 0.0 0.0 1.0)))
 	 	   
; (run aesthetic-enjoyment-provision (aries/get-demo-data-kbox))