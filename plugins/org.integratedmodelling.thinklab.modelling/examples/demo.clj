(defmodel demo-biomass-model 'ecology:PlantBiomass
	
	"A sample model of plant biomass showing alternative formulations according to biome"
	
	[(classification 'landuse:NLCDCoverClass [0 32] 'landuse:Terrestrial [34 :>] 'landuse:Aquatic) 
			:cluster-states true 
		  :as biome]
	
	(measurement 'ecology:Biomass "kg/m^2")
		  :context    time
			:derivative (time:Time #(* % (^ growth-rate time))) 
			:when       (is? biome 'landuse:Terrestrial)
			
	(classification (measurement 'ecology:Biomass "kg/m^2") [0 2.5] 'biomass:Low [2.5 :>] 'biomass:High)
	   :context 
	   	 (classification 
	   	 			(measurement 'biophysics:SeaLevelTemperature "C") [:< 20] 'temp:Low [20 :>] 'temp:High)
		 :probability
			 	(('biomass:Low|'temp:Low -> [0.7 0.9]) ('biomass:Low|'temp:High -> [0.1 0.3])
			 	 ('biomass:High|'temp:Low -> 0.017)    ('biomass:High|'temp:High -> 0.983))
		 :when (is? biome 'landuse:Aquatic))

(defmodel ziocan 'geophysics:Slope
  (slope 'geophysics:Elevation "deg"))
			 	 
(ontology 'carbonService
  (thinklab-core:OrdinalRangeMapping
    (CarbonSequestration :units "kg/cm2" :description "" :label ""
      (LowCarbonSequestration      :range [0 2])
      (ModerateCarbonSequestration :range [2 5])
      (HighCarbonSequestration     :range [5 9])
      (VeryHighCarbonSequestration :range [9 20]))))

(probabilistic-measurement CarbonSequestration "kg/cm2"
  "Generated from a node of network BB"
  [0 2] LowCarbonSequestration
  [2 5] ModerateCarbonSequestration
  [5 9] HighCarbonSequestration
  [9 20] VeryHighCarbonSequestration
  :context (biomass cn-ratio)
  :cpt (HighBiomass HighCNRatio : .44933 .12234 .32554,
        HighBiomass ModerateCNRatio: .44933 .12234 .32554))
  