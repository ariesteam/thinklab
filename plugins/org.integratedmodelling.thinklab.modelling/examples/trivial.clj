;; will look for any observation of ecology:Biomass and return it unmediated when run

(defmodel useless-biomass 'ecology:Biomass)

;; will look for any measurement of biomass that can be mediated to kg/m2

(defmodel biomass-1 (measurement 'ecology:Biomass "kg/m^2"))

;; look! A time-dependent model!

(defmodel biomass-1 
	(measurement 'ecology:Biomass "kg/m^2")
		:context    ((measurement time:Time "d") :as time)
		:derivative (time '(* self (^  growth-rate time)))
		:initially  23.0)
		
		
		


(defmodel classified-biomass 'ecology:Biomass
	(classification (measurement 'ecology:Biomass "kg/m^2") 
			[0 2.5]  'biomass:Low 
			[2.5 :>] 'biomass:High))
			
(defmodel bayesian-biomass 'ecology:Biomass
	(classification (measurement 'ecology:Biomass "kg/m^2") 
			[0 2.5] 'biomass:Low [2.5 :>] 'biomass:High)
	   :context 
	   	 (classification 
	   	 			(measurement 'biophysics:SeaLevelTemperature "C") 
	   	 					[:< 20] 'temp:Low [20 :>] 'temp:High)
		 :probability
			 	(('b:Low|'t:Low  -> 0.773) ('b:Low|'t:High  -> 0.123)
			 	 ('b:High|'t:Low -> 0.017) ('b:High|'t:High -> 0.983)))