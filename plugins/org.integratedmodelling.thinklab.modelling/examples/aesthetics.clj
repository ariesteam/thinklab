(ns aries.models
	(:refer-clojure)
  (:use [modelling :only (defmodel measurement classification 
  			 									discrete-random-model discrete-noisymax-model)]))
	 	 	
(load-bindings 'aries.core)	

(defmodel valuable-mountain 'aestheticService:Mountain
   "Classifies an elevation model into three levels of provision of beautiful mountains"
   (classification 
   		(measurement  'geophysics:Elevation "m")
   		"2000)"       'aestheticService:NoMountain 
   		"[2000-2750)" 'aestheticService:SmallMountain 
   		"[2750"       'aestheticService:LargeMountain ))
   		    		 
(defmodel aesthetic-enjoyment-provision 'aestheticService:SensoryEnjoyment
 	 "Unconditional bayesian model of sensory enjoyment provision."
 	 (discrete-noisymax-model 'aestheticService:SensoryEnjoyment
 	 
 	 	  ; cpt for the noisymax - all others default to uniform
 	 	  [0.4 0.3 0.2 0.1 0.2 0.3 0.3 0.2 0.0 0.0 0.0 1.0 0.7 0.1
 	 	   0.1 0.1 0.5 0.2 0.2 0.1 0.0 0.0 0.0 1.0 0.0 0.0 0.0 1.0]

 	 		; dependencies
 	 	  (discrete-random-model valuable-mountain) 
 	 	 	(discrete-random-model 
 	 	   		(classification 'aestheticService:WaterBody
 	 	   			0 'aestheticService:NoWater
 	 	   			1 'aestheticService:Lake
 	 	   			2 'aestheticService:Ocean))))

; ------------------------------------------------------------------------------------
; the following command:	 	   
;
; => (run aesthetic-enjoyment-provision (aries/get-demo-data-kbox))
;
; will lookup data in the kbox, build and run the bayesian network, reclassifying
; everything as requested.
; ------------------------------------------------------------------------------------
