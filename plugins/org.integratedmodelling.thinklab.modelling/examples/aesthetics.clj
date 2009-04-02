(ns aries.models
	(:refer-clojure)
  (:use [modelling :only (defmodel measurement classification 
  			 									discrete-random-model discrete-noisymax-model)]))

(load-bindings 'aries.core)	

(defmodel valuable-waterbodies (aestheticService:WaterBody)
		 (classification (ranking (lulc:NCLD1Numeric))
	 			(= % 23)   'aestheticService:Lake
	 			(= % 32)   'aestheticService:Ocean
	 			:otherwise 'aestheticService:NoWater))

(defmodel valuable-mountain (aestheticService:Mountain)
   "Classifies an elevation model into three levels of provision of beautiful mountains"
   (classification  (measurement (ecology:Elevation) "m")
   		[:< 2000]    'aestheticService:NoMountain 
   		[2000 2750]  'aestheticService:SmallMountain 
   		[2750 :>]    'aestheticService:LargeMountain))
   		    		 
(defmodel aesthetic-enjoyment-provision (aestheticService:SensoryEnjoyment)

 	 "Unconditional bayesian model of sensory enjoyment provision."
 	  
 	 	 (discrete-noisymax-model (aestheticService:SensoryEnjoyment)
	 	 	  ; cpt for the noisymax - all others default to uniform
 		 	  [0.4 0.3 0.2 0.1 0.2 0.3 0.3 0.2 0.0 0.0 0.0 1.0 0.7 0.1
 	 		   0.1 0.1 0.5 0.2 0.2 0.1 0.0 0.0 0.0 1.0 0.0 0.0 0.0 1.0]) 	
 	 	 :context
  	 	 ((discrete-random-model valuable-mountain) :as mountains, 
  	 	  (discrete-random-model valuable-waterbodies) :as waterbodies))

; ------------------------------------------------------------------------------------
; the following command:	 	   
;
; => (run aesthetic-enjoyment-provision (aries/get-demo-data-kbox))
;
; will lookup data in the kbox, build and run the bayesian network, reclassifying
; everything as requested.
; ------------------------------------------------------------------------------------
