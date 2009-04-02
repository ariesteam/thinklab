(ns modelling.examplesb
	(:refer-clojure)
  (:use [modelling :only (defmodel measurement dde-measurement identification 
  												classified-raster)]))

; we need these ontologies to define our observables
(tl/load "biodiversity.owl" "ecology.owl")

;; -------------------------------------------------------------------------------
;; the (independent) hare model
;; -------------------------------------------------------------------------------
(defmodel wolf-controlled-hare-density 
		; observable instance is built on the fly from its list representation
		(ecology:PopulationAbundance (ecology:hasSpecies (biodiversity:SnowShoeHare)))

	"A differential equation model of hare density that is controlled by wolf predation
	 and depends on birth rate and consumption rate from the predator."

	; predation rates are contingent to biome in the dde, so we need to observe the biome.
	; This below will have as many states as there are distinct biomes, so we can pass
	; a high-res biome raster map and a large space extent to the (run ...) form.
	; Note: this is incomplete as it doesn't specify the mappings; in real life 
	; we'll want the result of another defmodel in here. Note that the other defmodel
	; can also be conditional.
	[(classification (ecology:Biome)) :as biome] 
	
	; conditional specs allow fine-grained choice of individual dependencies
	; --
	; only a defmodel form can specify a contingency model, but the dependency
	; structure in all model forms that can have dependencies will be able to
	; use the tags defined in the form they're in.
	(dde-measurement 
			(ecology:PopulationAbundance)
			"ind/m^2" 
			(- (* hare-birth-rate self) (* wolf-abundance wolf-consumption-rate)))
			
	:context
			
			; depends on three other models, one of which is conditional
			; also, being a dde, it will ensure that it's contextualized on a temporal
			; topology. Note that the unit determines the "temporal scaling" here, and
			; it will run on any time topology with appropriate conversions. 
			((measurement
					(ecology:BirthRate (ecology:hasSpecies (biodiversity:SnowShoeHare)))
					"ind/d") :as hare-birth-rate
			(measurement
					(ecology:PopulationAbundance (ecology:hasSpecies (biodiversity:GreyWolf)))
					"ind/m^2") :as wolf-abundance
					
			; two alternatives views of the same observable, depending on context
			(measurement 
					(ecology:PredationRate (ecology:hasSpecies (biodiversity:SnowShoeHare)))
					"ind/d") 
						:as   wolf-consumption-rate 
						:when (tl/is? biome 'ecology:BorealForest)
						; if we don't link an observation, make a parameter with this value
						:default-value 0.023
						:parameter true 
			(measurement 
					(ecology:PredationRate (ecology:hasSpecies (biodiversity:SnowShoeHare)))
					"ind/d") 
						:as wolf-consumption-rate 
						:default-value 0.123 
						:parameter true))
						 		
;; -------------------------------------------------------------------------------
;; the (independent) wolf model
;; -------------------------------------------------------------------------------
(defmodel hare-dependent-wolf-density

		(ecology:PopulationAbundance (ecology:hasSpecies (biodiversity:GreyWolf)))
				
	  "A differential equation model of wolf density that depends on the 
	   availability of hare prey and on a predation efficiency rate parameter. The 
	   prey model will vary according to where the wolf is."
	   
	(dde-measurement 
			(ecology:PopulationAbundance) 
			"ind/m^2" 
			(* hare-abundance hare-conversion-efficiency self))
	:context
			((measurement 
					(ecology:PredationEfficiency 
							(ecology:hasSource (biodiversity:SnowShoeHare))
							(ecology:hasTarget (biodiversity:GreyWolf)))					
					"ind/d") 
						:as hare-conversion-efficiency 
						:parameter 0.0034
						
			(measurement
					(ecology:PopulationAbundance (ecology:hasSpecies (biodiversity:SnowShoeHare)))
					"ind/m^2") 
						:as hare-abundance))

;; -------------------------------------------------------------------------------
;; The hare-wolf system is an identification that depends on both hare and wolf
;; We could just use observables and it would be perfectly general, but possibly
;; non computable
;; Dependencies are resolved by the identificaton model, looking up models in the
;; same namespace; the missing parameters will be created from their :initially specs
;; -------------------------------------------------------------------------------
(defmodel hare-wolf-system 
		(ecology:Community (ecology:hasSpecies (biodiversity:Hare biodiversity:Wolf)))
		(identification 
			wolf-controlled-hare-density :as hare
			hare-dependent-wolf-density :as wolf))
			
; we need a kbox (for the contingency) and a topology with at least time to run 
; ignored for now. We should deactivate the inference engine and default
; to the fallback case for all contingencies if no kbox is given.
;(run hare-wolf-system)

