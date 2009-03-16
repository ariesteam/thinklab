(ns aries.models
	(use [model :only defmodel]))

(defmodel discrete-biomass 'ecology:Biomass
		"A biomass in 4 levels of discretized kg/m^2, unconditional to context"
		[] 
		(discrete-measurement
			(measurement 'ecology:Biomass "kg/m^2")
				("biomass:low 400)"
	 			 "biomass:medium [400,600)"
	 		   "biomass:high [600,800)"
	 		   "biomass:very-high [800")))
	 		   
(defmodel view-usage 'aesthetics:SensoryEnjoyment
		""
		;; if we have a contingency model, a conditional model follows
		[(classification 'lulc:LandUseClass 'lulc:anderson1) :as landuse]
		(tl/is :landuse 'anderson1:Forest) ()
		() ()
		:default ())
		
	 		   
	 		   
	 		   
	 		  

			