;; -----------------------------------------------------------------------------------------
;; Clojure bindings for the Geospace plugin
;; VERY incomplete, stubs only
;; @author Ferdinando Villa
;; @date Nov 15, 2008
;; -----------------------------------------------------------------------------------------

(ns geospace)

(tl/load-bindings 'corescience)

(defn get-boundary
	"Returns a space areal observation with the boundary of the given observation, or the convex hull
	 of a list of observations."
	 []
	 nil)
	 
(defn make-grid
	"Returns a new grid observation from a shape and a maximum linear resolution. The grid will
	 be set to contain the shape, and the widest dimension will have the given number of cells."
	[where max-linear-resolution]
	(. org.integratedmodelling.geospace.implementations.observations.RasterGrid (createRasterGrid where max-linear-resolution)))

(defn make-grid
	"Returns a new rectangular grid observation with the specified number of cells, each cell equal
	to 1 square degree in UTM coordinates, starting at the equator on Greenwich meridian."
	[rows columns]
	nil)

(defn make-areal-location
	"Returns a new areal space observation with the given shape"
	[shape]
	nil)
	
(defn get-spatial-extent
	"Return the IObservation that defines the spatial extent for the passed observation, or nil
	if non-spatial"
	[observation]	
	(corescience/get-extent observation (.. org.integratedmodelling.geospace.Geospace (get) (SpaceObservable))))
	
(defn spatial? 
	"Returns true if the given observation is spatial, i.e. has an extent that observes space."
	[observation]
	(not (nil? (get-spatial-extent observation))))

(defn grid-extent? 
	"Returns true if the given observation has a grid extent, i.e. has an extent that observes space."
	[observation]
	(let [space (get-spatial-extent observation)]
	  (and 
	     (not (nil? space))
	     (instance? org.integratedmodelling.geospace.implementations.observations.RasterGrid space)))) 
	    
(defn grid-rows
	"Returns the number of rows in the grid extent of the passed observation. Throw an exception if
	 the observation is not distributed over a grid extent."
	 [observation]
	 (. (get-spatial-extent observation) (getRows)))

(defn grid-columns
	"Returns the number of rows in the grid extent of the passed observation. Throw an exception if
	 the observation is not distributed over a grid extent."
	 [observation]
	 (. (get-spatial-extent observation) (getColumns)))
