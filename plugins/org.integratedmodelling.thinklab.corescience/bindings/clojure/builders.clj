;; ================================================================================================
;; simplified builders for object of common use
;; ================================================================================================

(ns corescience)

;; "private" func to call Java from compiled code - won't load classes from the macro.

(defn j-make-discretizer
	""
	[list]
	(org.integratedmodelling.corescience.clojure.ClojureBridge/makeDiscretizer 
		(tl/listp list)))
		
;; ================================================================================================
;; public functions
;; ================================================================================================

(defmacro make-discretizer
   "Builds the list definition of an instance of a discrete ranking conceptual model"
   [spec]
   `(j-make-discretizer '~spec))

(defn make-unit
   "Builds the list definition of an instance of a Measurement conceptual model from the 
   textual specification of a measurement unit."
   [unitspec]
   (org.integratedmodelling.corescience.clojure.ClojureBridge/makeUnit unitspec))