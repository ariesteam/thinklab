;; -----------------------------------------------------------------------------------------
;; Clojure bindings for the Geospace plugin
;; VERY incomplete, stubs only
;; @author Ferdinando Villa
;; @date Nov 15, 2008
;; -----------------------------------------------------------------------------------------

(ns geospace)

(tl/load-bindings 'corescience)

;; the main space observable, used to retrieve extents from observations, is bound to geospace/*space*
(def *space* (tl/conc 'geospace:SpaceObservable))

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
	(if (not (nil? observation)) 
		(corescience/get-extent observation (.. org.integratedmodelling.geospace.Geospace (get) (SpaceObservable)))))
	
(defn get-centroid 
	"Return a ShapeValue with the centroid of the spatial extent of the passed observation, or 
	 nil if no spatial extent is there."
	[observation]
	(let [extent (get-spatial-extent observation)]
		(if (instance? org.integratedmodelling.geospace.interfaces.IGeolocatedObject extent)
			  (.getCentroid extent))))

(defn get-bounding-box 
	"Return a ShapeValue with the bounding box (polygon) of the spatial extent of the passed observation, or 
	 nil if no spatial extent is there."
	[observation]
	(let [extent (get-spatial-extent observation)]
		(if (instance? org.integratedmodelling.geospace.interfaces.IGeolocatedObject extent)
				(.getBoundingBox extent)
				nil)))
		
(defn get-shape 
	"Return a ShapeValue with the overall shape (polygon) of the spatial extent of the passed observation, or 
	 nil if no spatial extent is there."
	[observation]
	(let [extent (get-spatial-extent observation)]
		(if (instance? org.integratedmodelling.geospace.interfaces.IGeolocatedObject extent)
				(.getShape extent)
				nil)))
		
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
	 
(defn build-coverage
	"Build and show a coverage from a passed spatial extent, data vector, and x/y size info"
	[extent data]
	(org.integratedmodelling.geospace.coverage.CoverageFactory/makeCoverage extent data))

(defn get-shape-from-name
		"Return a grid topology (observations of space) from a name that
		matches a location in a gazetteer. Name can be a string, a symbol or anything whose string value
		is found in the global gazetteer."
		[name] 
		(let [
			cshapes (.. org.integratedmodelling.geospace.Geospace (get) (lookupFeature (str name)))
			]
		(if (> (.size cshapes) 0) 
				(.get cshapes 0)))) 

(defn get-shape
	"Return the full extent shape from a grid." 
	[rastergrid]
	(.. rastergrid (getExtent) (getFullExtentValue))) 
	 

(defn get-matching-native-grid
		"Return a grid topology (observations of space) from a name that
		matches a location in a gazetteer. Name can be a string, a symbol or anything whose string value
		is found in the global gazetteer."
		[shape rastergrid] 
	(org.integratedmodelling.geospace.implementations.observations.RasterGrid/createRasterGrid shape rastergrid)) 

(defn topology-array
	[topology]
	(let [
		retval (make-array org.integratedmodelling.corescience.interfaces.internal.Topology 1)
		]
	(aset retval 0 topology)
	 retval)) 

(defn get-topology-from-name
		"Return a grid topology (observations of space) from a name that
		matches a location in a gazetteer. Name can be a string, a symbol or anything whose string value
		is found in the global gazetteer."
		[name resolution] 
		(let [
			cshapes (.. org.integratedmodelling.geospace.Geospace (get) (lookupFeature (str name)))
			retval (make-array org.integratedmodelling.corescience.interfaces.internal.Topology 1)]
			(if 
				(> (.size cshapes) 0) 
				(let [inst (.getImplementation 
											(tl/create-object
												(org.integratedmodelling.geospace.implementations.observations.RasterGrid/createRasterGrid
												(.get cshapes 0) resolution)))]
								(aset retval 0 inst)
								retval))))
								
(defn get-topology-from-shape
		"Return a grid topology (observations of space) from a shape and a linear resolution."
		[shape resolution] 
		(let [
			retval (make-array org.integratedmodelling.corescience.interfaces.internal.Topology 1)
			inst (.getImplementation 
							(tl/create-object
								 (org.integratedmodelling.geospace.implementations.observations.RasterGrid/createRasterGrid
										shape resolution)))]
				(topology-array inst)))