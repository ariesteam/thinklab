(ns modelling.examplesb
	(:refer-clojure)
	(:use [tl :only (defconcept)])
  (:use [modelling :only (defmodel measurement dde-measurement identification 
  												classified-raster)]))

; we need these ontologies to define our observables
(tl/load "biodiversity.owl" "ecology.owl")

(defconcept 'harewolf:HarePopulationAbundance
	:subsumes ('representation:Count 'ecology:PopulationAbundance)
	:persist 'org.integratedmodelling.thinklab.modelling)

(defconcept 'harewolf:WolfPopulationAbundance
	:subsumes 'ecology:PopulationAbundance
	:persist true)
	
;; hare model
(defmodel wolf-controlled-hare-density 

	(ecology:PopulationAbundance (ecology:hasSpecies (biodiversity:SnowShoeHare)))

	"A differential equation model of hare density that is controlled by wolf predation
	 and depends on birth rate and consumption rate from the predator."

	[(classification 'ecology:IGBPBiome) :rescale (geospace:ArealSpace discontinuous) :as biome] 
	
	(enumeration 'ecology:PopulationAbundance "n/m^2")
	  :derivative
	  	(time:ContinuousTime (- (* hare-birth-rate self) (* wolf-abundance wolf-consumption-rate)))	  	 	
	  :context
			((measurement
					'(ecology:BirthRate (ecology:hasSpecies (biodiversity:SnowShoeHare)))
					"/d") :as hare-birth-rate
			 (measurement
					'(ecology:PopulationAbundance (ecology:hasSpecies (biodiversity:GreyWolf)))
					"/m^2") :as wolf-abundance
			 (measurement 
					'(ecology:PredationRate (ecology:hasSpecies (biodiversity:SnowShoeHare)))
					"/d") 
						:as   wolf-consumption-rate 
						:when (tl/is? biome 'ecology:BorealForest)
						:parameter 0.023 
			 (enumeration 
					'(ecology:PredationRate (ecology:hasSpecies (biodiversity:SnowShoeHare)))
					"/d") 
						:as wolf-consumption-rate 
						:value 0.123))

;; wolf model
(defmodel hare-dependent-wolf-density

  '(ecology:PopulationAbundance (ecology:hasSpecies (biodiversity:GreyWolf)))
				
	"A differential equation model of wolf density that depends on the 
	 availability of hare prey and on a predation efficiency rate parameter. The 
	 prey model will vary according to where the wolf is."
	   
	(enumeration 'ecology:PopulationAbundance "n/m^2") 

	  :as wolf
	  :derivative 
	  	(time:ContinuousTime (* hare-abundance hare-conversion-efficiency wolf))
	  :context
			((measurement 
					'(ecology:PredationEfficiency 
							(ecology:hasSource (biodiversity:SnowShoeHare))
							(ecology:hasTarget (biodiversity:GreyWolf)))					
					"/d") 
				    :as hare-conversion-efficiency
						:parameter 0.0034
			 (measurement
					'(ecology:PopulationAbundance (ecology:hasSpecies (biodiversity:SnowShoeHare)))
					"/m^2") 
						:as hare-abundance))

(defmodel hare-wolf-system 
	 '(ecology:Community (ecology:hasSpecies (biodiversity:Hare biodiversity:Wolf)))
		(identification 'ecology:Community)
		  :context 
			  (wolf-controlled-hare-density :as hare :value 10
			   hare-dependent-wolf-density  :as wolf :value 2))
			
; (run hare-wolf-system)

