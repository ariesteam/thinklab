;; -----------------------------------------------------------------------------------------------
;; Sample semantic model of Climate Stability
;; -----------------------------------------------------------------------------------------------
;; ------------------------------------------------------------------------------------------------

(load-bindings 'geospace)
(load-bindings 'modelling)

(make-model climateStabilityModel 
    [:elevation (make-type habitat:Elevation (unit "m")) :population (tl/conc 'habitat:PopulationDensity)]
	(carbonService:ClimateStability
		((and (between :elevation 12.22 21.22) (between lon 30.22 34.84)) 
		  carbonService:Discrete6CarbonSequestered)
		((and (in-region (get-space :context) (get-region 'Madagascar)) (> :population 250)) 
		  carbonService:TropicalCarbonSequestered)
		(:default carbonService:GeneralCarbonSequestered))
	(habitat:RainfallAmount
	    ((in-region (get-space :context) 'Tropics) carbonService:Discrete12RainfallPerMonth)
	    (:default carbonService:GeneralRainfallPerYear)))

 