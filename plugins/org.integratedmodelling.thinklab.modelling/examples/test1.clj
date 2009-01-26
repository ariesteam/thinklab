;; -----------------------------------------------------------------------------------------------
;; Sample semantic model of Climate Stability
;; -----------------------------------------------------------------------------------------------
;; -----------------------------------------------------------------------------------------------

(load-bindings 'geospace)
(load-bindings 'modelling)

(let [
	dElevation (model/make-type 
				(model/make-type 'habitat:Elevation (corescience/make-unit "m"))
				(corescience/make-discretizer 
					"elevation:Low<-200)" 
					"elevation:Medium<-[200,650)" 
					"elevation:High<-[650" ))
     ]
  (model/make-model climateStabilityModel 
      [:elevation dElevation :population habitat:PopulationDensity]
	(carbonService:ClimateStability
		(and (between :elevation 12.22 21.22) (between (get-longitude (get-space :context)) 30.22 34.84)) 
		'carbonService:Discrete6CarbonSequestered
		(and (in-region (get-space :context) (get-region 'Madagascar)) (> :population 250)) 
		'carbonService:TropicalCarbonSequestered
		:default carbonService:GeneralCarbonSequestered)
	(habitat:RainfallAmount
	    (in-region (get-space :context) 'Tropics) 
          'carbonService:Discrete12RainfallPerMonth
	    :default 
	    'carbonService:GeneralRainfallPerYear)))

 