(ns modelling.examplesb
	(:refer-clojure)
  (:use [modelling :only (defmodel measurement dde-measurement identification 
  												classified-raster)]))

; we need these ontologies to define our observables
(tl/load "biodiversity.owl" "ecology.owl")

;; hare model
(defmodel wolf-controlled-hare-density 

	(ecology:PopulationAbundance (ecology:hasSpecies (biodiversity:SnowShoeHare)))

	"A differential equation model of hare density that is controlled by wolf predation
	 and depends on birth rate and consumption rate from the predator."

	[(classification (ecology:IGBPBiome)) :clustered discontinuous :as biome] 
	
	(count (ecology:PopulationAbundance)"n/m^2") 	
	  :change
	  	('time:ContinuousTime 
	  	 (- (* hare-birth-rate self) (* wolf-abundance wolf-consumption-rate)))	  	 	
	  :context
			((count
					(ecology:BirthRate (ecology:hasSpecies (biodiversity:SnowShoeHare)))
					"n/d") :as hare-birth-rate
			 (count
					(ecology:PopulationAbundance (ecology:hasSpecies (biodiversity:GreyWolf)))
					"n/m^2") :as wolf-abundance
			 (count 
					(ecology:PredationRate (ecology:hasSpecies (biodiversity:SnowShoeHare)))
					"n/d") 
						:as   wolf-consumption-rate 
						:when (tl/is? biome 'ecology:BorealForest)
						:parameter 0.023 
			 (count 
					(ecology:PredationRate (ecology:hasSpecies (biodiversity:SnowShoeHare)))
					"n/d") 
						:as wolf-consumption-rate 
						:parameter 0.123))

;; wolf model
(defmodel hare-dependent-wolf-density

  (ecology:PopulationAbundance (ecology:hasSpecies (biodiversity:GreyWolf)))
				
	"A differential equation model of wolf density that depends on the 
	 availability of hare prey and on a predation efficiency rate parameter. The 
	 prey model will vary according to where the wolf is."
	   
	(count (ecology:PopulationAbundance) "n/m^2") 

	  :as wolf
	  :change 
	  	('time:ContinuousTime
	  	 (* hare-abundance hare-conversion-efficiency wolf))
	  :context
			((count 
					(ecology:PredationEfficiency 
							(ecology:hasSource (biodiversity:SnowShoeHare))
							(ecology:hasTarget (biodiversity:GreyWolf)))					
					"n/d") 
				    :as hare-conversion-efficiency
						:parameter 0.0034
			 (count
					(ecology:PopulationAbundance (ecology:hasSpecies (biodiversity:SnowShoeHare)))
					"ind/m^2") 
						:as hare-abundance))

(defmodel hare-wolf-system 
		(ecology:Community (ecology:hasSpecies (biodiversity:Hare biodiversity:Wolf)))
		(identification 'ecology:Community)
		  :context 
			  (wolf-controlled-hare-density :as hare :state 10
			   hare-dependent-wolf-density  :as wolf :state 2))
			
; (run hare-wolf-system)

