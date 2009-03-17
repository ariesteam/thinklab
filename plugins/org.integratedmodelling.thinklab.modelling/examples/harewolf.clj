(ns modelling.examples
	(:refer-clojure)
  (:use [modelling :only (defmodel measurement dde-measurement)]))

; we need these ontologies to define our observables
(tl/load "biodiversity.owl" "ecology.owl")

;; -------------------------------------------------------------------------------
;; the (independent) hare model
;; -------------------------------------------------------------------------------
(defmodel wolf-controlled-hare-density 
				; observable concept is built on the fly from its list representation
				'(ecology:PopulationAbundance (ecology:hasSpecies (biodiversity:SnowShoeHare)))

	"A differential equation model of hare density that is controlled by wolf predation
	 and depends on birth rate and consumption rate from the predator."

	; predators and rates contingent to biome
	[(classified-raster 'ecology:Biome) :as biome] 
	
	; TODO conditional specs - should allow fine-grained choice of individual deps
	; as well as higher-level definition; set the model to nil for those biomes
	; that don't have hares.
	(dde-measurement 
			'ecology:PopulationAbundance 
			"ind/m^2" 
			'(- (* hare-birth-rate self) (* wolf-abundance wolf-consumption-rate))
			; depends on three other models, one of which can be created with defaults
			; also, being a dde, it will ensure that it's contextualized on a temporal
			; topology.
			(
				(measurement
					'(ecology:BirthRate (ecology:hasSpecies (biodiversity:SnowShoeHare)))
					"ind/d") :as hare-birth-rate
				(measurement
					'(ecology:PopulationAbundance (ecology:hasSpecies (biodiversity:GreyWolf)))
					"ind/m^2") :as wolf-abundance
				(measurement 
					'(ecology:BirthRate (ecology:hasSpecies (biodiversity:SnowShoeHare)))
					"ind/d") :as wolf-consumption-rate :initially 0.023)))
		
;; -------------------------------------------------------------------------------
;; the (independent) wolf model
;; -------------------------------------------------------------------------------
(defmodel hare-dependent-wolf-density
				'(ecology:PopulationAbundance (ecology:hasSpecies (biodiversity:GreyWolf)))
				
	"A differential equation model of wolf density that depends on the 
	 availability of hare prey and on a predation efficiency rate parameter. The 
	 prey model will vary according to where the wolf is."

	; prey species and rates contingent to biome
	[(classified-raster 'ecology:Biome) :as biome] 

	; TODO conditional specs
	(dde-measurement 
			'ecology:PopulationAbundance 
			"ind/m^2" 
			'(* hare-abundance hare-conversion-efficiency self)
			; depends on four other models
			(
				(measurement 
					'(ecology:PredationEfficiency 
							(ecology:hasSource (biodiversity:SnowShoeHare))
							(ecology:hasTarget (biodiversity:GreyWolf)))					
					"ind/d") :as hare-conversion-efficiency :initially 0.0034
				(measurement
					'(ecology:PopulationAbundance (ecology:hasSpecies (biodiversity:SnowShoeHare)))
					"ind/m^2") :as hare-abundance)))	 

;; -------------------------------------------------------------------------------
;; The hare-wolf system is an identification that depends on both hare and wolf
;; We could just use observables and it would be perfectly general, but possibly
;; non computable
;; Dependencies are resolved by the identificaton model, looking up models in the
;; same namespace; the missing parameters will be created from their :initially specs
;; -------------------------------------------------------------------------------
(defmodel hare-wolf-system 
		'(ecology:Community (ecology:hasSpecies (biodiversity:SnowShoeHare biodiversity:GreyWolf)))
		""
		[]
		(identification 
			; dependencies; whatever is not found will instantiate an appropriate parameter 
			(wolf-controlled-hare-density hare-dependent-wolf-density)))
			
; (run hare-wolf-system (kbox "kbox://ecoinformatics.uvm.edu/kbox/examples1.kbox"))

