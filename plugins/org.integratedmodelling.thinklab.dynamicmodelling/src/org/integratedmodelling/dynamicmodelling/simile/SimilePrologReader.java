/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
        package org.integratedmodelling.dynamicmodelling.simile;

        import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.integratedmodelling.dynamicmodelling.model.Flow;
import org.integratedmodelling.dynamicmodelling.model.FlowEdge;
import org.integratedmodelling.dynamicmodelling.model.InfluenceEdge;
import org.integratedmodelling.dynamicmodelling.model.Model;
import org.integratedmodelling.dynamicmodelling.model.Stock;
import org.integratedmodelling.dynamicmodelling.model.Submodel;
import org.integratedmodelling.dynamicmodelling.model.SubmodelEdge;
import org.integratedmodelling.dynamicmodelling.model.Variable;

import edu.uci.ics.jung.graph.impl.SimpleDirectedSparseVertex;

        public class SimilePrologReader implements SimilePrologReaderConstants {
                // These are populated during the parsing phase and used
                // to construct the models during the build phase.
                private Hashtable<String, SimilePrologExpression> metadata = new Hashtable<String, SimilePrologExpression>();
                private Hashtable<String, Node> nodeList = new Hashtable<String, Node>();
                private Hashtable<String, Arc> arcList = new Hashtable<String, Arc>();
                private Hashtable<String, Links> linksList = new Hashtable<String, Links>();

                // These are used to maintain internal references during
                // the model build phase.
                private Hashtable<String, Stock> stockList = new Hashtable<String, Stock>();
                private Hashtable<String, Flow> flowList = new Hashtable<String, Flow>();
                private Hashtable<String, Variable> variableList = new Hashtable<String, Variable>();
                private Hashtable<String, Submodel> submodelList = new Hashtable<String, Submodel>();

                public static void main(String[] args) throws ParseException, TokenMgrError {
                        SimilePrologReader parser = new SimilePrologReader(System.in);
                        Model topLevelModel = parser.generateModel();
                        if (topLevelModel.getName() != null) {
                                System.out.println("The Simile model was recreated successfully.");
                        }
                }

                public Model generateModel() throws ParseException, TokenMgrError {
                        this.Start();
                        Model topLevelModel = new Model();
                        if (metadata.containsKey("source") && metadata.containsKey("roots") && metadata.containsKey("properties")) {
                                Vector<String> topLevelModelNodes = ((Roots) metadata.get("roots")).subnodes;
                                Hashtable<String, Object> topLevelModelProperties = ((Properties) metadata.get("properties")).properties;
                                topLevelModel = buildModelFromNodes("topLevelModel", topLevelModelNodes, topLevelModelProperties);
                                addArcsToModel();
                        } else {
                                System.out.println("File did not contain a complete Simile model specification.  Cannot build models.");
                        }
                        return topLevelModel;
                }

                private Model buildModelFromNodes(String modelNodeID, Vector<String> modelNodes, Hashtable<String, Object> modelProperties) {
                        System.out.println("Building model with nodeID " + modelNodeID + "...");

                        // Create the new Model object and set its properties
                        Model thisModel = new Model();

                        thisModel.setName((String) modelProperties.get("name"));
                        thisModel.setProgram(((Source) metadata.get("source")).program);
                        thisModel.setVersion(((Source) metadata.get("source")).version);
                        thisModel.setEdition(((Source) metadata.get("source")).edition);
                        thisModel.setDate(((Source) metadata.get("source")).date);

                        // Loop through the list of modelNodes and...
                        // 1) Add submodels to thisModel as Model objects wrapped in Submodels
                        // 2) Add compartments to thisModel as Stocks
                        // 3) Add variables/conditions to thisModel as Variables
                        // 4) Add text boxes to thisModel as Annotations
                        for (int i = 0; i < modelNodes.size(); i++) {
                                String thisNodeID = modelNodes.get(i);
                                Node thisNode = nodeList.get(thisNodeID);
                                System.out.println("Child Node " + i + ": " + thisNodeID);
                                System.out.println("Node Type: " + thisNode.nodeType);

                                if (thisNode.nodeType.equals("submodel")) {
                                        submodelList.put(thisNodeID, thisModel.addSubmodel(new Submodel(buildModelFromNodes(thisNodeID, thisNode.subnodes, thisNode.properties))));
                                } else if (thisNode.nodeType.equals("compartment")) {
                                        System.out.println("Adding stock to model");
                                        String stockName = (String) thisNode.properties.get("name");
                                        String stockState = "";
                                        String stockUnits = "";
                                        stockList.put(thisNodeID, thisModel.addStock(new Stock(stockName, stockState, stockUnits)));
                                } else if (thisNode.nodeType.equals("variable") || thisNode.nodeType.equals("condition")) {
                                        System.out.println("Adding variable to model");
                                        String varName = (String) thisNode.properties.get("name");
                                        String varValue = (String) thisNode.properties.get("value");
                                        String varUnits = (String) thisNode.properties.get("units");
                                        String varMinVal = (String) thisNode.properties.get("min_val");
                                        String varMaxVal = (String) thisNode.properties.get("max_val");
                                        String varComment = (String) thisNode.properties.get("comment");
                                        variableList.put(thisNodeID, thisModel.addVariable(new Variable(varName, varValue, varUnits, varMinVal, varMaxVal, varComment)));
                                } else if (thisNode.nodeType.equals("text")) {
                                        System.out.println("Adding annotation to model");
                                        thisModel.addAnnotation((String) thisNode.properties.get("name"));
                                } else {
                                        System.out.println("Skipping useless node " + thisNodeID + " of type " + thisNode.nodeType);
                                }
                        }

                        return thisModel;
                }

                private void addArcsToModel() {
                        // First add the flows so we don't have any weirdnesses with the influences later
                        System.out.println("Adding the flows to the model...");
                        for (Enumeration<String> keys = arcList.keys(); keys.hasMoreElements();) {
                                String thisArcID = keys.nextElement();
                                Arc thisArc = arcList.get(thisArcID);
                                System.out.println("Found " + thisArcID + " of type " + thisArc.arcType);

                                if (thisArc.arcType.equals("flow")) {
                                        System.out.println("Adding flow to model");

                                        // Find the Model it belongs in by checking which Model
                                        // its source|destination is in (if one is a cloud, it will
                                        // not exist at all, so you must check both until you find one
                                        // that is a Stock).
                                        Model thisModel = null;
                                        Stock thisSource = null;
                                        Stock thisDestination = null;

                                        if (stockList.containsKey(thisArc.sourceID)) {
                                                // This is an outflow
                                                System.out.println("Its source is a stock.");
                                                thisSource = stockList.get(thisArc.sourceID);
                                                thisModel = (Model) thisSource.getGraph();
                                        }

                                        if (stockList.containsKey(thisArc.destinationID)) {
                                                // This is an inflow
                                                System.out.println("Its destination is a stock.");
                                                thisDestination = stockList.get(thisArc.destinationID);
                                                if (thisModel == null) {
                                                        thisModel = (Model) thisDestination.getGraph();
                                                }
                                        }

                                        if (thisModel instanceof Model) {
                                                // Lookup the known properties for the Flow and add it to
                                                // the appropriate Model
                                                String flowName = (String) thisArc.properties.get("name");
                                                String flowRate = "";
                                                String flowUnits = "";

                                                System.out.println("Adding this Flow to Model " + thisModel.getName());
                                                Flow thisFlow = thisModel.addFlow(new Flow(flowName, flowRate, flowUnits));
                                                flowList.put(thisArcID, thisFlow);
                                                if (thisSource instanceof Stock) {
                                                        System.out.println("Adding FlowEdge from " + thisArc.sourceID + " to the Flow");
                                                        thisModel.addFlowEdge(new FlowEdge(thisSource, thisFlow));
                                                }
                                                if (thisDestination instanceof Stock) {
                                                        System.out.println("Adding FlowEdge from the Flow to " + thisArc.destinationID);
                                                        thisModel.addFlowEdge(new FlowEdge(thisFlow, thisDestination));
                                                }
                                        }
                                }
                        }

                        // Now that all the Flows are in the Models, you can deal with the influences.
                        // We begin in this loop by transforming function -> object arcs, such that
                        // the function becomes a property of the object.
                        System.out.println("Mapping functions to model elements...");
                        for (Enumeration<String> keys = arcList.keys(); keys.hasMoreElements();) {
                                String thisArcID = keys.nextElement();
                                Arc thisArc = arcList.get(thisArcID);
                                System.out.println("Found " + thisArcID + " of type " + thisArc.arcType);

                                if (thisArc.arcType.equals("influence")) {
                                        // In order to uniquely determine the purpose of this arc,
                                        // we need to determine the node/arc types of its source and destination.
                                        String mySourceType = "unknown";
                                        if (thisArc.sourceID.contains("node")) {
                                                mySourceType = nodeList.get(thisArc.sourceID).nodeType;
                                        } else if (thisArc.sourceID.contains("arc")) {
                                                mySourceType = arcList.get(thisArc.sourceID).arcType;
                                        } else {
                                                System.out.println("Mysterious influence arc " + thisArcID + " connects to something that is neither an arc nor a node.");
                                        }

                                        String myDestinationType = "unknown";
                                        if (thisArc.destinationID.contains("node")) {
                                                myDestinationType = nodeList.get(thisArc.destinationID).nodeType;
                                        } else if (thisArc.destinationID.contains("arc")) {
                                                myDestinationType = arcList.get(thisArc.destinationID).arcType;
                                        } else {
                                                System.out.println("Mysterious influence arc " + thisArcID + " connects to something that is neither an arc nor a node.");
                                        }

                                        System.out.println("Type: " + mySourceType + " -> " + myDestinationType);

                                        // Use the node types to determine how to respond to this arc statement
                                        if (mySourceType.equals("function")) {
                                                // Get the properties of the function node that we want to use
                                                Node myFunction = nodeList.get(thisArc.sourceID);
                                                String value = (String) myFunction.properties.get("value");
                                                String units = (String) myFunction.properties.get("units");
                                                String minVal = (String) myFunction.properties.get("min_val");
                                                String maxVal = (String) myFunction.properties.get("max_val");
                                                String comment = (String) myFunction.properties.get("comment");

                                                // We want to set these values as the state/rate/value/
                                                // units/minVal/maxVal/comment properties
                                                // of the corresponding Stock, Flow, or Variable object.
                                                // Add a note in the source object's properties that
                                                // points to the object whose function it represents.
                                                if (myDestinationType.equals("compartment")) {
                                                        Stock myStock = stockList.get(thisArc.destinationID);
                                                        myStock.setState(value);
                                                        myStock.setUnits(units);
                                                        myStock.setMinVal(minVal);
                                                        myStock.setMaxVal(maxVal);
                                                        myStock.setComment(comment);
                                                        myFunction.properties.put("function_owner", myStock);
                                                } else if (myDestinationType.equals("flow")) {
                                                        Flow myFlow = flowList.get(thisArc.destinationID);
                                                        myFlow.setRate(value);
                                                        myFlow.setUnits(units);
                                                        myFlow.setMinVal(minVal);
                                                        myFlow.setMaxVal(maxVal);
                                                        myFlow.setComment(comment);
                                                        myFunction.properties.put("function_owner", myFlow);
                                                } else if (myDestinationType.equals("variable") || myDestinationType.equals("condition")) {
                                                        Variable myVariable = variableList.get(thisArc.destinationID);
                                                        myVariable.setValue(value);
                                                        myVariable.setUnits(units);
                                                        myVariable.setMinVal(minVal);
                                                        myVariable.setMaxVal(maxVal);
                                                        myVariable.setComment(comment);
                                                        myFunction.properties.put("function_owner", myVariable);
                                                } else {
                                                        System.out.println("Mysterious function->object arc " + thisArcID + " connects to something that is not a Stock, Flow, or Variable.");
                                                }
                                        }
                                }
                        }

                        // Now you've finished with the function -> object mappings, so you can add the influence lines.
                        // Influences represent one of these model elements:
                        // 1) [function property] Function -> Stock/Flow/Variable (properties.get("complete") is not set)
                        // 2) [influence line] Variable/Stock/Flow -> Function (properties.get("complete") is set)
                        // 3) [ghost] Variable -> Variable (Real -> Ghost) (properties.get("complete") is not set) (display properties exist)
                        //    Note: Ghost variables have functions
                        //			and can have multiple influence lines into/out of them
                        //			Flag them as ghosts, don't create the edge, and collapse them
                        //			in a final pass.
                        // 4) [variable->dot link - internal exiting variable] Variable -> Variable (Real -> Dot) (properties.get("complete") is set) (display properties don't exist)
                        //    Note: the dot variable belongs to the submodel it is internal to.
                        //          Dot variables do not have functions
                        //			They should only have one input
                        //			They should be removed from the model they exist in
                        // 5) [dot->variable link - internal entering variable - dependency] Variable -> Function (variable's properties.get("complete") is not set)
                        //    Note: dependency variables should have no entering edges,
                        //			are related to the second arc in a link pair,
                        //			and belong to the model that they feed into (this is good)
                        //			The dot variable should be removed from the model it exists in
                        //			The function's variable should be flagged as a dependency in the model
                        // 6) [submodel->variable link - external variable - dependency] Submodel -> Function
                        //    Note: this dependency belongs to the function-containing model
                        //			Its variable should be flagged
                        // 7) [variable->submodel link - external variable] Variable -> Submodel
                        //    Note: Simply skip over these statements.
                        //          This is a provision rather than a dependency, but you don't
                        //          have to remove the submodel element because it doesn't exist in
                        //          any model.
                        // 8) [submodel->submodel link - variable to variable in parent model] Submodel -> Submodel
                        //    Note: This statement has no meaning in the separate models context.
                        //          Simply skip over these arcs.
                        System.out.println("Adding the influence lines, ghosts, and links...");
                        for (Enumeration<String> keys = arcList.keys(); keys.hasMoreElements();) {
                                String thisArcID = keys.nextElement();
                                Arc thisArc = arcList.get(thisArcID);
                                System.out.println("Found " + thisArcID + " of type " + thisArc.arcType);

                                if (thisArc.arcType.equals("influence")) {
                                        // In order to uniquely determine the purpose of this arc,
                                        // we need to determine the node/arc types of its source and destination.
                                        String mySourceType = "unknown";
                                        if (thisArc.sourceID.contains("node")) {
                                                mySourceType = nodeList.get(thisArc.sourceID).nodeType;
                                        } else if (thisArc.sourceID.contains("arc")) {
                                                mySourceType = arcList.get(thisArc.sourceID).arcType;
                                        } else {
                                                System.out.println("Mysterious influence arc " + thisArcID + " connects to something that is neither an arc nor a node.");
                                        }

                                        String myDestinationType = "unknown";
                                        if (thisArc.destinationID.contains("node")) {
                                                myDestinationType = nodeList.get(thisArc.destinationID).nodeType;
                                        } else if (thisArc.destinationID.contains("arc")) {
                                                myDestinationType = arcList.get(thisArc.destinationID).arcType;
                                        } else {
                                                System.out.println("Mysterious influence arc " + thisArcID + " connects to something that is neither an arc nor a node.");
                                        }

                                        System.out.println("Type: " + mySourceType + " -> " + myDestinationType);

                                        // Use the node types to determine how to respond to this arc statement
                                        if (myDestinationType.equals("function")) {
                                                System.out.println("A");
                                                SimpleDirectedSparseVertex myTargetModelElement = (SimpleDirectedSparseVertex) nodeList.get(thisArc.destinationID).properties.get("function_owner");
                                                if (mySourceType.equals("compartment")) {
                                                        System.out.println("B");
                                                        // Influence line from a Stock to another element
                                                        // Create an InfluenceEdge from the Stock to the function's object
                                                        Stock myStock = stockList.get(thisArc.sourceID);
                                                        Model thisModel = (Model) myStock.getGraph();
                                                        System.out.println("[" + (myStock.inDegree() + myStock.outDegree()) + ":" + (myTargetModelElement.inDegree() + myTargetModelElement.outDegree()) + "](" + myStock.getClass() + " => " + myTargetModelElement.getClass());
                                                        InfluenceEdge ie = new InfluenceEdge(myStock, myTargetModelElement);
                                                        try {
                                                                thisModel.addInfluenceEdge(ie);
                                                        } catch (edu.uci.ics.jung.exceptions.ConstraintViolationException e) {
                                                                if (edu.uci.ics.jung.utils.PredicateUtils.evaluateNestedPredicates(e.getViolatedConstraint(), ie).keySet().toArray()[0] instanceof edu.uci.ics.jung.graph.predicates.ParallelEdgePredicate) {
                                                                        System.out.println("Encountered a parallel edge from " + myStock.getName() + " to " + ((Flow) myTargetModelElement).getName() + ".  Skipping over this one.");
                                                                } else {
                                                                        System.out.println("UNIDENTIFIED EXCEPTION WHILE CREATING INFLUENCE EDGE!");
                                                                }
                                                        }
                                                } else if (mySourceType.equals("flow")) {
                                                        System.out.println("C");
                                                        // Influence line from a Flow to another element
                                                        // Create an InfluenceEdge from the Flow to the function's object
                                                        Flow myFlow = flowList.get(thisArc.sourceID);
                                                        Model thisModel = (Model) myFlow.getGraph();
                                                        System.out.println("[" + (myFlow.inDegree() + myFlow.outDegree()) + ":" + (myTargetModelElement.inDegree() + myTargetModelElement.outDegree()) + "](" + myFlow.getClass() + " => " + myTargetModelElement.getClass());
                                                        InfluenceEdge ie = new InfluenceEdge(myFlow, myTargetModelElement);
                                                        try {
                                                                thisModel.addInfluenceEdge(ie);
                                                        } catch (edu.uci.ics.jung.exceptions.ConstraintViolationException e) {
                                                                if (edu.uci.ics.jung.utils.PredicateUtils.evaluateNestedPredicates(e.getViolatedConstraint(), ie).keySet().toArray()[0] instanceof edu.uci.ics.jung.graph.predicates.ParallelEdgePredicate) {
                                                                        System.out.println("Encountered a parallel edge from " + myFlow.getName() + " to " + ((Flow) myTargetModelElement).getName() + ".  Skipping over this one.");
                                                                } else {
                                                                        System.out.println("UNIDENTIFIED EXCEPTION WHILE CREATING INFLUENCE EDGE!");
                                                                }
                                                        }
                                                } else if (mySourceType.equals("variable") || mySourceType.equals("condition")) {
                                                        System.out.println("D");
                                                        Node mySource = nodeList.get(thisArc.sourceID);
                                                        if (mySource.properties.containsKey("complete")) {
                                                                // Influence line from a Variable/Condition to another element
                                                                // Create an InfluenceEdge from the Variable to the function's object
                                                                Variable myVariable = variableList.get(thisArc.sourceID);
                                                                Model thisModel = (Model) myVariable.getGraph();
                                                                System.out.println("[" + (myVariable.inDegree() + myVariable.outDegree()) + ":" + (myTargetModelElement.inDegree() + myTargetModelElement.outDegree()) + "](" + myVariable.getClass() + " => " + myTargetModelElement.getClass());
                                                                InfluenceEdge ie = new InfluenceEdge(myVariable, myTargetModelElement);
                                                                try {
                                                                        thisModel.addInfluenceEdge(ie);
                                                                } catch (edu.uci.ics.jung.exceptions.ConstraintViolationException e) {
                                                                        if (edu.uci.ics.jung.utils.PredicateUtils.evaluateNestedPredicates(e.getViolatedConstraint(), ie).keySet().toArray()[0] instanceof edu.uci.ics.jung.graph.predicates.ParallelEdgePredicate) {
                                                                                System.out.println("Encountered a parallel edge from " + myVariable.getName() + " to " + ((Flow) myTargetModelElement).getName() + ".  Skipping over this one.");
                                                                        } else {
                                                                                System.out.println("UNIDENTIFIED EXCEPTION WHILE CREATING INFLUENCE EDGE!");
                                                                        }
                                                                }
                                                        } else {
                                                                // Submodel internal link from "Dot Variable" to another element
                                                                // Delete the "Dot Variable" and flag the target element as a dependency
                                                                Variable myVariable = variableList.get(thisArc.sourceID);
                                                                Model thisModel = (Model) myVariable.getGraph();
                                                                if (thisModel instanceof Model) {
                                                                        System.out.print("Removing variable " + myVariable.getName() + "...");
                                                                        thisModel.removeVariable(myVariable);
                                                                        System.out.println("done");
                                                                        thisModel.addDependency(myTargetModelElement);
                                                                }
                                                        }
                                                } else if (mySourceType.equals("submodel")) {
                                                        System.out.println("E");
                                                        // Submodel to external element link
                                                        // Add a SubmodelEdge from the Submodel to the other element in the containing Model
                                                        // Maintain a reference in the SubmodelEdge to the Submodel's internal element from which the link originated
                                                        Submodel mySubmodel = submodelList.get(thisArc.sourceID);
                                                        Model thisModel = (Model) mySubmodel.getGraph();
                                                        Links mySubmodelLinks = linksList.get(thisArc.sourceID);

                                                        for (Enumeration<String> linksKeys = mySubmodelLinks.arcPairs.keys(); linksKeys.hasMoreElements();) {
                                                                String thisSourceArcID = linksKeys.nextElement();
                                                                String thisDestinationArcID = mySubmodelLinks.arcPairs.get(thisSourceArcID);

                                                                if (thisDestinationArcID.equals(thisArcID)) {
                                                                        SimpleDirectedSparseVertex mySourceElement = new SimpleDirectedSparseVertex();
                                                                        String sourceElementID = arcList.get(thisSourceArcID).sourceID;
                                                                        if (sourceElementID.contains("node")) {
                                                                                String nodeType = nodeList.get(sourceElementID).nodeType;
                                                                                if (nodeType.equals("compartment")) {
                                                                                        mySourceElement = stockList.get(sourceElementID);
                                                                                } else if (nodeType.equals("variable") || nodeType.equals("condition")) {
                                                                                        mySourceElement = variableList.get(sourceElementID);
                                                                                } else if (nodeType.equals("submodel")) {
                                                                                        mySourceElement = submodelList.get(sourceElementID);
                                                                                } else {
                                                                                        System.out.println("Link originating element " + sourceElementID + " should be of type compartment/variable/condition/submodel but is of type " + nodeType);
                                                                                }
                                                                        } else if (sourceElementID.contains("arc")) {
                                                                                String arcType = arcList.get(sourceElementID).arcType;
                                                                                if (arcType.equals("flow")) {
                                                                                        mySourceElement = flowList.get(sourceElementID);
                                                                                } else {
                                                                                        System.out.println("Link originating element " + sourceElementID + " should be of type flow but is of type " + arcType);
                                                                                }
                                                                        }

                                                                        SubmodelEdge se = new SubmodelEdge(mySubmodel, myTargetModelElement, mySourceElement, null);
                                                                        try {
                                                                                thisModel.addSubmodelEdge(se);
                                                                        } catch (edu.uci.ics.jung.exceptions.ConstraintViolationException e) {
                                                                                if (edu.uci.ics.jung.utils.PredicateUtils.evaluateNestedPredicates(e.getViolatedConstraint(), se).keySet().toArray()[0] instanceof edu.uci.ics.jung.graph.predicates.ParallelEdgePredicate) {
                                                                                        System.out.println("Encountered a parallel edge from " + mySubmodel.getModel().getName() + " to " + ((Flow) myTargetModelElement).getName() + ".  Skipping over this one.");
                                                                                } else {
                                                                                        System.out.println("UNIDENTIFIED EXCEPTION WHILE CREATING SUBMODEL EDGE!");
                                                                                }
                                                                        }
                                                                        break;
                                                                }
                                                        }
                                                }
                                        } else if (myDestinationType.equals("submodel")) {
                                                System.out.println("F");
                                                if (mySourceType.equals("submodel")) {
                                                        // Submodel to Submodel link
                                                        // Add a SubmodelEdge between the two Submodels in the containing Model
                                                        // Maintain references in the SubmodelEdge to both Submodel's internal elements which are the endpoints of the four-arc link
                                                        Submodel mySourceSubmodel = submodelList.get(thisArc.sourceID);
                                                        Submodel myDestinationSubmodel = submodelList.get(thisArc.destinationID);
                                                        Model thisModel = (Model) mySourceSubmodel.getGraph();
                                                        Links mySourceSubmodelLinks = linksList.get(thisArc.sourceID);
                                                        Links myDestinationSubmodelLinks = linksList.get(thisArc.destinationID);

                                                        SimpleDirectedSparseVertex mySourceElement = new SimpleDirectedSparseVertex();
                                                        SimpleDirectedSparseVertex myDestinationElement = new SimpleDirectedSparseVertex();

                                                        String destinationElementID = arcList.get(myDestinationSubmodelLinks.arcPairs.get(thisArcID)).destinationID;
                                                        if (destinationElementID.contains("node") && nodeList.get(destinationElementID).nodeType.equals("function")) {
                                                                myDestinationElement = (SimpleDirectedSparseVertex) nodeList.get(destinationElementID).properties.get("function_owner");
                                                        } else {
                                                                System.out.println("Link destination element " + destinationElementID + " should be of type function but is of type " + nodeList.get(destinationElementID).nodeType);
                                                        }

                                                        for (Enumeration<String> linksKeys = mySourceSubmodelLinks.arcPairs.keys(); linksKeys.hasMoreElements();) {
                                                                String thisSourceArcID = linksKeys.nextElement();
                                                                String thisDestinationArcID = mySourceSubmodelLinks.arcPairs.get(thisSourceArcID);

                                                                if (thisDestinationArcID.equals(thisArcID)) {
                                                                        String sourceElementID = arcList.get(thisSourceArcID).sourceID;
                                                                        if (sourceElementID.contains("node")) {
                                                                                String nodeType = nodeList.get(sourceElementID).nodeType;
                                                                                if (nodeType.equals("compartment")) {
                                                                                        mySourceElement = stockList.get(sourceElementID);
                                                                                } else if (nodeType.equals("variable") || nodeType.equals("condition")) {
                                                                                        mySourceElement = variableList.get(sourceElementID);
                                                                                } else if (nodeType.equals("submodel")) {
                                                                                        mySourceElement = submodelList.get(sourceElementID);
                                                                                } else {
                                                                                        System.out.println("Link originating element " + sourceElementID + " should be of type compartment/variable/condition/submodel but is of type " + nodeType);
                                                                                }
                                                                        } else if (sourceElementID.contains("arc")) {
                                                                                String arcType = arcList.get(sourceElementID).arcType;
                                                                                if (arcType.equals("flow")) {
                                                                                        mySourceElement = flowList.get(sourceElementID);
                                                                                } else {
                                                                                        System.out.println("Link originating element " + sourceElementID + " should be of type flow but is of type " + arcType);
                                                                                }
                                                                        }

                                                                        SubmodelEdge se = new SubmodelEdge(mySourceSubmodel, myDestinationSubmodel, mySourceElement, myDestinationElement);
                                                                        try {
                                                                                thisModel.addSubmodelEdge(se);
                                                                        } catch (edu.uci.ics.jung.exceptions.ConstraintViolationException e) {
                                                                                if (edu.uci.ics.jung.utils.PredicateUtils.evaluateNestedPredicates(e.getViolatedConstraint(), se).keySet().toArray()[0] instanceof edu.uci.ics.jung.graph.predicates.ParallelEdgePredicate) {
                                                                                        System.out.println("Encountered a parallel edge from " + mySourceSubmodel.getModel().getName() + " to " + myDestinationSubmodel.getModel().getName() + ".  Skipping over this one.");
                                                                                } else {
                                                                                        System.out.println("UNIDENTIFIED EXCEPTION WHILE CREATING SUBMODEL EDGE!");
                                                                                }
                                                                        }
                                                                        break;
                                                                }
                                                        }
                                                } else if (mySourceType.equals("compartment") || mySourceType.equals("flow") || mySourceType.equals("variable") || mySourceType.equals("condition")) {
                                                        // Element to Submodel link
                                                        // Add a SubmodelEdge from the element to the Submodel in the containing Model
                                                        // Maintain a reference in the SubmodelEdge to the Submodel's internal element which is the link's ultimate destination
                                                        Submodel mySubmodel = submodelList.get(thisArc.destinationID);
                                                        Model thisModel = (Model) mySubmodel.getGraph();
                                                        Links mySubmodelLinks = linksList.get(thisArc.destinationID);

                                                        SimpleDirectedSparseVertex mySourceElement = new SimpleDirectedSparseVertex();
                                                        SimpleDirectedSparseVertex myDestinationElement = new SimpleDirectedSparseVertex();

                                                        if (mySourceType.equals("compartment")) {
                                                                mySourceElement = stockList.get(thisArc.sourceID);
                                                        } else if (mySourceType.equals("flow")) {
                                                                mySourceElement = flowList.get(thisArc.sourceID);
                                                        } else if (mySourceType.equals("variable") || mySourceType.equals("condition")) {
                                                                mySourceElement = variableList.get(thisArc.sourceID);
                                                        } else if (mySourceType.equals("submodel")) {
                                                                mySourceElement = submodelList.get(thisArc.sourceID);
                                                        } else {
                                                                System.out.println("Link source element " + thisArc.sourceID + " should be of type compartment/flow/variable/condition/submodel but is of type " + mySourceType);
                                                        }

                                                        String destinationElementID = arcList.get(mySubmodelLinks.arcPairs.get(thisArcID)).destinationID;
                                                        if (destinationElementID.contains("node") && nodeList.get(destinationElementID).nodeType.equals("function")) {
                                                                myDestinationElement = (SimpleDirectedSparseVertex) nodeList.get(destinationElementID).properties.get("function_owner");
                                                        } else {
                                                                System.out.println("Link destination element " + destinationElementID + " should be of type function but is of type " + nodeList.get(destinationElementID).nodeType);
                                                        }

                                                        SubmodelEdge se = new SubmodelEdge(mySourceElement, mySubmodel, null, myDestinationElement);
                                                        try {
                                                                thisModel.addSubmodelEdge(se);
                                                        } catch (edu.uci.ics.jung.exceptions.ConstraintViolationException e) {
                                                                if (edu.uci.ics.jung.utils.PredicateUtils.evaluateNestedPredicates(e.getViolatedConstraint(), se).keySet().toArray()[0] instanceof edu.uci.ics.jung.graph.predicates.ParallelEdgePredicate) {
                                                                        System.out.println("Encountered a parallel edge from " + ((Flow) mySourceElement).getName() + " to " + mySubmodel.getModel().getName() + ".  Skipping over this one.");
                                                                } else {
                                                                        System.out.println("UNIDENTIFIED EXCEPTION WHILE CREATING SUBMODEL EDGE!");
                                                                }
                                                        }
                                                }
                                        } else if (mySourceType.equals(myDestinationType)) {
                                                System.out.println("G");
                                                if (thisArc.properties.containsKey("complete")) {
                                                        System.out.println("G1");
                                                        // Element to "Dot Variable" internal link
                                                        // Remove the "Dot Variable" from the model containing it
                                                        Variable myDotVariable = variableList.get(thisArc.destinationID);
                                                        if (myDotVariable instanceof Variable) {
                                                                System.out.println("G1a");
                                                                Model thisModel = (Model) myDotVariable.getGraph();
                                                                if (thisModel instanceof Model) {
                                                                        System.out.print("Removing dot variable " + myDotVariable.getName() + "...");
                                                                        thisModel.removeVariable(myDotVariable);
                                                                        System.out.println("done");
                                                                }
                                                        } else {
                                                                System.out.println("G1b");
                                                                System.out.println("Unrecognizable object -> object mapping in " + thisArcID);
                                                        }
                                                } else {
                                                        System.out.println("G2");
                                                        // Ghosted object
                                                        // To be safe, we should update the ghost Object to make sure
                                                        // it has all the same information as the Object it references.
                                                        // Then we need to flag it as a ghost element in the model.
                                                        Hashtable<String, Object> ghostNote = new Hashtable<String, Object>();
                                                        if (mySourceType.equals("compartment")) {
                                                                // First making the two identical
                                                                System.out.println("G3");
                                                                Stock ghostedStock = stockList.get(thisArc.sourceID);
                                                                Stock ghostStock = stockList.get(thisArc.destinationID);
                                                                ghostStock.setState(ghostedStock.getState());
                                                                ghostStock.setUnits(ghostedStock.getUnits());
                                                                ghostStock.setMinVal(ghostedStock.getMinVal());
                                                                ghostStock.setMaxVal(ghostedStock.getMaxVal());
                                                                ghostStock.setComment(ghostedStock.getComment());
                                                                // And now the note
                                                                Model ghostsModel = (Model) ghostStock.getGraph();
                                                                ghostNote.put("ghostElement", ghostStock);
                                                                ghostNote.put("ghostedElement", ghostedStock);
                                                                ghostNote.put("ghostedModel", (Model) ghostedStock.getGraph());
                                                                ghostsModel.addGhostNote(ghostNote);
                                                        } else if (mySourceType.equals("flow")) {
                                                                System.out.println("G4");
                                                                // First making the two identical
                                                                Flow ghostedFlow = flowList.get(thisArc.sourceID);
                                                                Flow ghostFlow = flowList.get(thisArc.destinationID);
                                                                ghostFlow.setRate(ghostedFlow.getRate());
                                                                ghostFlow.setUnits(ghostedFlow.getUnits());
                                                                ghostFlow.setMinVal(ghostedFlow.getMinVal());
                                                                ghostFlow.setMaxVal(ghostedFlow.getMaxVal());
                                                                ghostFlow.setComment(ghostedFlow.getComment());
                                                                // And now the note
                                                                Model ghostsModel = (Model) ghostFlow.getGraph();
                                                                ghostNote.put("ghostElement", ghostFlow);
                                                                ghostNote.put("ghostedElement", ghostedFlow);
                                                                ghostNote.put("ghostedModel", (Model) ghostedFlow.getGraph());
                                                                ghostsModel.addGhostNote(ghostNote);
                                                        } else if (mySourceType.equals("variable") || mySourceType.equals("condition")) {
                                                                System.out.println("G5");
                                                                // First making the two identical
                                                                Variable ghostedVariable = variableList.get(thisArc.sourceID);
                                                                Variable ghostVariable = variableList.get(thisArc.destinationID);
                                                                ghostVariable.setValue(ghostedVariable.getValue());
                                                                ghostVariable.setUnits(ghostedVariable.getUnits());
                                                                ghostVariable.setMinVal(ghostedVariable.getMinVal());
                                                                ghostVariable.setMaxVal(ghostedVariable.getMaxVal());
                                                                ghostVariable.setComment(ghostedVariable.getComment());
                                                                // And now the note
                                                                Model ghostsModel = (Model) ghostVariable.getGraph();
                                                                ghostNote.put("ghostElement", ghostVariable);
                                                                ghostNote.put("ghostedElement", ghostedVariable);
                                                                ghostNote.put("ghostedModel", (Model) ghostedVariable.getGraph());
                                                                ghostsModel.addGhostNote(ghostNote);
                                                        }
                                                }
                                        } else if (mySourceType.equals("function")) {
                                                System.out.println("H");
                                                // Skip these since we handled them in the last loop
                                        } else {
                                                System.out.println("Identified new arc type combination (" + mySourceType + " -> " + myDestinationType + ") at " + thisArcID);
                                        }
                                }
                        }
                        System.out.println("Finished generating the models.");
                }

/*
 * Parser's BNF Production Rules
 *
 *   Start			->	((Expression)? EOL)* EOF
 *
 *   Expression		->	Source | Roots | Properties | Node | Arc | Links | References
 *
 *   Source			->	<SOURCE> <OPEN_PAR>
 *						<WORD> <EQUALS> <QTDSTRING> <COMMA>
 *						<WORD> <EQUALS> <NUMBER> <COMMA>
 *						<WORD> <EQUALS> <WORD> <COMMA>
 *						<WORD> <EQUALS> <QTDSTRING>
 *						<CLOSE_PAR> <DOT>
 *
 *   Roots			->	<ROOTS> <OPEN_PAR>
 *						List
 *						<CLOSE_PAR> <DOT>
 *
 *   Properties		->	<PROPERTIES> <OPEN_PAR>
 *						List
 *						<CLOSE_PAR> <DOT>
 *
 *   Node			->	<NODE> <OPEN_PAR>
 *						<WORD> <COMMA>
 *						<WORD> <COMMA>
 *						List <COMMA>
 *						List <COMMA>
 *						List
 *						<CLOSE_PAR> <DOT>
 *
 *   Arc			->	<ARC> <OPEN_PAR>
 *						<WORD> <COMMA>
 *						<WORD> <COMMA>
 *						<WORD> <COMMA>
 *						<WORD> <COMMA>
 *						List <COMMA>
 *						List
 *						<CLOSE_PAR> <DOT>
 *
 *   Links			->	<LINKS> <OPEN_PAR>
 *						<WORD> <COMMA> List
 *						<CLOSE_PAR> <DOT>
 *
 *   References		->	<REFERENCES> <OPEN_PAR>
 *						<WORD> <COMMA> List
 *						<CLOSE_PAR> <DOT>
 *
 *   List			->	<OPEN_SQR>
 *						(ListElement (<COMMA> ListElement)*)?
 *						<CLOSE_SQR>
 *
 *   ListElement	->	List
 *					  | (<DASH>)? <NUMBER>
 *					  | <WORD> (SubroutineArguments | DashValue | EqualsValue)?
					  | Formula
 *
 *   SubroutineArguments ->	<OPEN_PAR> (Formula (<COMMA> Formula)*)? <CLOSE_PAR>
 *
 *   DashValue		->	<DASH> Formula
 *
 *   EqualsValue	->	<EQUALS> Formula
 *
 *   Formula		->	Term ((<MATHSYM>|<DASH>|<RELSYM>) Term)* | <IF> Formula <THEN> Formula (<ELSEIF> Formula <THEN> Formula)* <ELSE> Formula
 *
 *   Term			->	<WORD> (SubroutineArguments)? | <NUMBER> | <QTDSTRING> | List | <DASH> Term | <OPEN_PAR> Formula <CLOSE_PAR> | <OPEN_CUR> Formula <CLOSE_CUR>
 *
 */
  final public void Start() throws ParseException {
          System.out.println("Starting to Read File");
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case EOL:
      case SOURCE:
      case ROOTS:
      case PROPERTIES:
      case NODE:
      case ARC:
      case LINKS:
      case REFERENCES:
        ;
        break;
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case SOURCE:
      case ROOTS:
      case PROPERTIES:
      case NODE:
      case ARC:
      case LINKS:
      case REFERENCES:
        Expression();
        break;
      default:
        jj_la1[1] = jj_gen;
        ;
      }
      jj_consume_token(EOL);
    }
    jj_consume_token(0);
          System.out.println("Finished Reading File");
  }

  final public void Expression() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case SOURCE:
                  System.out.println("Found Source Expression");
      Source();
      break;
    case ROOTS:
                  System.out.println("Found Roots Expression");
      Roots();
      break;
    case PROPERTIES:
                  System.out.println("Found Properties Expression");
      Properties();
      break;
    case NODE:
                  System.out.println("Found Node Expression");
      Node();
      break;
    case ARC:
                  System.out.println("Found Arc Expression");
      Arc();
      break;
    case LINKS:
                  System.out.println("Found Links Expression");
      Links();
      break;
    case REFERENCES:
                  System.out.println("Found References Expression");
      References();
      break;
    default:
      jj_la1[2] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void Source() throws ParseException {
        Token program, version, edition, date;
    jj_consume_token(SOURCE);
    jj_consume_token(OPEN_PAR);
    jj_consume_token(WORD);
    jj_consume_token(EQUALS);
    program = jj_consume_token(QTDSTRING);
    jj_consume_token(COMMA);
    jj_consume_token(WORD);
    jj_consume_token(EQUALS);
    version = jj_consume_token(NUMBER);
    jj_consume_token(COMMA);
    jj_consume_token(WORD);
    jj_consume_token(EQUALS);
    edition = jj_consume_token(WORD);
    jj_consume_token(COMMA);
    jj_consume_token(WORD);
    jj_consume_token(EQUALS);
    date = jj_consume_token(QTDSTRING);
    jj_consume_token(CLOSE_PAR);
    jj_consume_token(DOT);
                metadata.put("source",
                        new Source(
                                program.image.substring(1, program.image.length()-1),
                                version.image,
                                edition.image,
                                date.image.substring(1, date.image.length()-1)
                        )
                );
                Source s = (Source) metadata.get("source");
                System.out.println("Stored this metadata:");
                System.out.println("Program: " + s.program);
                System.out.println("Version: " + s.version);
                System.out.println("Edition: " + s.edition);
                System.out.println("Date: " + s.date);
  }

/*
 * This designates the nodes that will be present in this graph.
 * Any element not in this list will belong to a submodel of this model,
 * which we need to represent as a separate graph with the appropriate
 * links intact.
 */
  final public void Roots() throws ParseException {
        Object subnodes;
    jj_consume_token(ROOTS);
    jj_consume_token(OPEN_PAR);
    // This will always be a Vector of Strings (they are nodeIDs)
            subnodes = List();
    jj_consume_token(CLOSE_PAR);
    jj_consume_token(DOT);
                if (!(subnodes instanceof Vector)) {
                        subnodes = new Vector<String>();
                }
                metadata.put("roots", new Roots((Vector<String>) subnodes));
                Roots r = (Roots) metadata.get("roots");
                System.out.println("Number of Root Nodes: " + r.subnodes.size());
  }

  final public void Properties() throws ParseException {
        Object properties;
    jj_consume_token(PROPERTIES);
    jj_consume_token(OPEN_PAR);
    // This will always be a Hashtable of Strings to Objects
            properties = List();
    jj_consume_token(CLOSE_PAR);
    jj_consume_token(DOT);
                if (!(properties instanceof Hashtable)) {
                        properties = new Hashtable<String, Object>();
                }
                metadata.put("properties", new Properties((Hashtable<String, Object>) properties));
                Properties p = (Properties) metadata.get("properties");
                System.out.println("Name: " + (String) p.properties.get("name"));

                for (Enumeration<String> keys = p.properties.keys(); keys.hasMoreElements();) {
                        String thisProperty = keys.nextElement();
                        Object thisValue = p.properties.get(thisProperty);
                        System.out.println("Found " + thisProperty + " with value " + thisValue.toString());
                }
  }

  final public void Node() throws ParseException {
        Token nodeID, nodeType;
        Object subnodes, properties;
    jj_consume_token(NODE);
    jj_consume_token(OPEN_PAR);
    nodeID = jj_consume_token(WORD);
    jj_consume_token(COMMA);
    nodeType = jj_consume_token(WORD);
    jj_consume_token(COMMA);
    // This will always be a Vector of Strings (they are nodeIDs)
            subnodes = List();
    jj_consume_token(COMMA);
    // This will always be a Hashtable of Strings to Objects
            properties = List();
    jj_consume_token(COMMA);
    List();
    jj_consume_token(CLOSE_PAR);
    jj_consume_token(DOT);
                if (!(subnodes instanceof Vector)) {
                        subnodes = new Vector<String>();
                }
                if (!(properties instanceof Hashtable)) {
                        properties = new Hashtable<String, Object>();
                }
                nodeList.put(nodeID.image,
                        new Node(
                                nodeType.image,
                                (Vector<String>) subnodes,
                                (Hashtable<String, Object>) properties
                        )
                );
                Node n = nodeList.get(nodeID.image);
                System.out.println("Node ID: " + nodeID.image);
                System.out.println("Node Type: " + n.nodeType);
                System.out.println("Number of Subnodes: " + n.subnodes.size());
                System.out.println("Number of Properties: " + n.properties.size());
  }

  final public void Arc() throws ParseException {
        Token arcID, sourceID, destinationID, arcType;
        Object properties;
    jj_consume_token(ARC);
    jj_consume_token(OPEN_PAR);
    arcID = jj_consume_token(WORD);
    jj_consume_token(COMMA);
    sourceID = jj_consume_token(WORD);
    jj_consume_token(COMMA);
    destinationID = jj_consume_token(WORD);
    jj_consume_token(COMMA);
    arcType = jj_consume_token(WORD);
    jj_consume_token(COMMA);
    // This will always be a Hashtable of Strings to Objects
            properties = List();
    jj_consume_token(COMMA);
    List();
    jj_consume_token(CLOSE_PAR);
    jj_consume_token(DOT);
                if (!(properties instanceof Hashtable)) {
                        properties = new Hashtable<String, Object>();
                }
                arcList.put(arcID.image,
                        new Arc(
                                sourceID.image,
                                destinationID.image,
                                arcType.image,
                                (Hashtable<String, Object>) properties
                        )
                );
                Arc a = arcList.get(arcID.image);
                System.out.println("Arc ID: " + arcID.image);
                System.out.println("Source ID: " + a.sourceID);
                System.out.println("Destination ID: " + a.destinationID);
                System.out.println("Arc Type: " + a.arcType);
                System.out.println("Number of Properties: " + a.properties.size());
  }

  final public void Links() throws ParseException {
        Token submodelID;
        Object arcPairs;
    jj_consume_token(LINKS);
    jj_consume_token(OPEN_PAR);
    submodelID = jj_consume_token(WORD);
    jj_consume_token(COMMA);
    // This will always be a Hashtable of Strings to Strings
            arcPairs = List();
    jj_consume_token(CLOSE_PAR);
    jj_consume_token(DOT);
                if (!(arcPairs instanceof Hashtable)) {
                        arcPairs = new Hashtable<String, String>();
                }
                linksList.put(submodelID.image, new Links((Hashtable<String, String>) arcPairs));
                Links l = linksList.get(submodelID.image);
                System.out.println("Stored a set of Links");
                System.out.println("Submodel ID: " + submodelID.image);
                System.out.println("Number of Arc Pairs: " + l.arcPairs.size());
  }

  final public void References() throws ParseException {
        Token submodelID;
        Object references;
    jj_consume_token(REFERENCES);
    jj_consume_token(OPEN_PAR);
    submodelID = jj_consume_token(WORD);
    jj_consume_token(COMMA);
    // This will always be a Vector of Strings
            references = List();
    jj_consume_token(CLOSE_PAR);
    jj_consume_token(DOT);
                if (!(references instanceof Vector)) {
                        references = new Vector<String>();
                }
                // linksList.put(submodelID.image, new Links((Hashtable<String, String>) arcPairs));
                // Links l = linksList.get(submodelID.image);
                System.out.println("Stored a set of references");
                System.out.println("Submodel ID: " + submodelID.image);
                System.out.println("Number of references: " + ((Vector) references).size());
  }

  final public Object List() throws ParseException {
        Object nextElement;
        Object list = new Object();
    jj_consume_token(OPEN_SQR);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OPEN_PAR:
    case OPEN_SQR:
    case OPEN_CUR:
    case DASH:
    case IF:
    case QTDSTRING:
    case WORD:
    case NUMBER:
      nextElement = ListElement();
                        if (nextElement instanceof KeyValuePair) {
                                list = new Hashtable<String, Object>();
                                ((Hashtable<String, Object>) list).put(((KeyValuePair) nextElement).key, ((KeyValuePair) nextElement).value);
                        } else {
                                list = new Vector<Object>();
                                ((Vector<Object>) list).add(nextElement);
                        }
      label_2:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[3] = jj_gen;
          break label_2;
        }
        jj_consume_token(COMMA);
        nextElement = ListElement();
                                if (nextElement instanceof KeyValuePair) {
                                        ((Hashtable<String, Object>) list).put(((KeyValuePair) nextElement).key, ((KeyValuePair) nextElement).value);
                                } else {
                                        ((Vector<Object>) list).add(nextElement);
                                }
      }
      break;
    default:
      jj_la1[4] = jj_gen;
      ;
    }
    jj_consume_token(CLOSE_SQR);
                if (list instanceof Vector) {
                        ((Vector<Object>) list).trimToSize();
                }
                {if (true) return list;}
    throw new Error("Missing return statement in function");
  }

  final public Object ListElement() throws ParseException {
        Object thisElement;
        Token t;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OPEN_SQR:
      thisElement = List();
                  {if (true) return thisElement;}
      break;
    case DASH:
    case NUMBER:
                  thisElement = "";
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DASH:
        t = jj_consume_token(DASH);
                          thisElement = t.image;
        break;
      default:
        jj_la1[5] = jj_gen;
        ;
      }
      t = jj_consume_token(NUMBER);
                        thisElement = (String) thisElement + t.image;
                        {if (true) return thisElement;}
      break;
    case WORD:
      t = jj_consume_token(WORD);
                  thisElement = t.image;
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case OPEN_PAR:
      case DASH:
      case EQUALS:
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case OPEN_PAR:
          thisElement = SubroutineArguments();
                                  thisElement = t.image + (String) thisElement;
          break;
        case DASH:
          thisElement = DashValue();
                                  thisElement = new KeyValuePair(t.image, thisElement);
          break;
        case EQUALS:
          thisElement = EqualsValue();
                                  thisElement = new KeyValuePair(t.image, thisElement);
          break;
        default:
          jj_la1[6] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
        break;
      default:
        jj_la1[7] = jj_gen;
        ;
      }
                  {if (true) return thisElement;}
      break;
    case OPEN_PAR:
    case OPEN_CUR:
    case IF:
    case QTDSTRING:
      thisElement = Formula();
                  {if (true) return thisElement;}
      break;
    default:
      jj_la1[8] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public String SubroutineArguments() throws ParseException {
        String theseArgs = "";
        String moreCharacters;
    jj_consume_token(OPEN_PAR);
          theseArgs += "(";
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OPEN_PAR:
    case OPEN_SQR:
    case OPEN_CUR:
    case DASH:
    case IF:
    case QTDSTRING:
    case WORD:
    case NUMBER:
      moreCharacters = Formula();
                  theseArgs += moreCharacters;
      label_3:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[9] = jj_gen;
          break label_3;
        }
        jj_consume_token(COMMA);
                          theseArgs += ",";
        moreCharacters = Formula();
                          theseArgs += moreCharacters;
      }
      break;
    default:
      jj_la1[10] = jj_gen;
      ;
    }
    jj_consume_token(CLOSE_PAR);
          theseArgs += ")";
          {if (true) return theseArgs;}
    throw new Error("Missing return statement in function");
  }

  final public String DashValue() throws ParseException {
        String thisDashValue;
    jj_consume_token(DASH);
    thisDashValue = Formula();
          {if (true) return thisDashValue;}
    throw new Error("Missing return statement in function");
  }

  final public String EqualsValue() throws ParseException {
        String thisEqualsValue;
    jj_consume_token(EQUALS);
    thisEqualsValue = Formula();
          {if (true) return thisEqualsValue;}
    throw new Error("Missing return statement in function");
  }

  final public String Formula() throws ParseException {
        String thisFormula;
        String moreCharacters;
        Token t;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OPEN_PAR:
    case OPEN_SQR:
    case OPEN_CUR:
    case DASH:
    case QTDSTRING:
    case WORD:
    case NUMBER:
      thisFormula = Term();
      label_4:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case DASH:
        case MATHSYM:
        case RELSYM:
          ;
          break;
        default:
          jj_la1[11] = jj_gen;
          break label_4;
        }
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case MATHSYM:
          t = jj_consume_token(MATHSYM);
          break;
        case DASH:
          t = jj_consume_token(DASH);
          break;
        case RELSYM:
          t = jj_consume_token(RELSYM);
          break;
        default:
          jj_la1[12] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
        moreCharacters = Term();
                          thisFormula += t.image + moreCharacters;
      }
                  {if (true) return thisFormula;}
      break;
    case IF:
      jj_consume_token(IF);
      moreCharacters = Formula();
                  thisFormula = "if " + moreCharacters;
      jj_consume_token(THEN);
      moreCharacters = Formula();
                  thisFormula += " then " + moreCharacters;
      label_5:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case ELSEIF:
          ;
          break;
        default:
          jj_la1[13] = jj_gen;
          break label_5;
        }
        jj_consume_token(ELSEIF);
        moreCharacters = Formula();
                          thisFormula += " elseif " + moreCharacters;
        jj_consume_token(THEN);
        moreCharacters = Formula();
                          thisFormula += " then " + moreCharacters;
      }
      jj_consume_token(ELSE);
      moreCharacters = Formula();
                  thisFormula += " else " + moreCharacters;
                  {if (true) return thisFormula;}
      break;
    default:
      jj_la1[14] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public String Term() throws ParseException {
        String thisTerm;
        String moreCharacters;
        Object thisList;
        Token t;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case WORD:
      t = jj_consume_token(WORD);
                  thisTerm = t.image;
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case OPEN_PAR:
        moreCharacters = SubroutineArguments();
                          thisTerm += moreCharacters;
        break;
      default:
        jj_la1[15] = jj_gen;
        ;
      }
                  {if (true) return thisTerm;}
      break;
    case NUMBER:
      t = jj_consume_token(NUMBER);
                        thisTerm = t.image;
                        {if (true) return thisTerm;}
      break;
    case QTDSTRING:
      t = jj_consume_token(QTDSTRING);
                        thisTerm = t.image.substring(1, t.image.length()-1);
                        {if (true) return thisTerm;}
      break;
    case OPEN_SQR:
      thisList = List();
                        thisTerm = thisList.toString();
                        {if (true) return thisTerm;}
      break;
    case DASH:
      jj_consume_token(DASH);
      moreCharacters = Term();
                        thisTerm = "-" + moreCharacters;
                        {if (true) return thisTerm;}
      break;
    case OPEN_PAR:
      jj_consume_token(OPEN_PAR);
      moreCharacters = Formula();
      jj_consume_token(CLOSE_PAR);
                        thisTerm = "(" + moreCharacters + ")";
                        {if (true) return thisTerm;}
      break;
    case OPEN_CUR:
      jj_consume_token(OPEN_CUR);
      moreCharacters = Formula();
      jj_consume_token(CLOSE_CUR);
                        thisTerm = "{" + moreCharacters + "}";
                        {if (true) return thisTerm;}
      break;
    default:
      jj_la1[16] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  public SimilePrologReaderTokenManager token_source;
  SimpleCharStream jj_input_stream;
  public Token token, jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[17];
  static private int[] jj_la1_0;
  static {
      jj_la1_0();
   }
   private static void jj_la1_0() {
      jj_la1_0 = new int[] {0xfe004,0xfe000,0xfe000,0x400,0x291008a8,0x800,0x1808,0x1808,0x291008a8,0x400,0x291008a8,0x6000800,0x6000800,0x400000,0x291008a8,0x8,0x290008a8,};
   }

  public SimilePrologReader(java.io.InputStream stream) {
     this(stream, null);
  }
  public SimilePrologReader(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new SimilePrologReaderTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 17; i++) jj_la1[i] = -1;
  }

  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 17; i++) jj_la1[i] = -1;
  }

  public SimilePrologReader(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new SimilePrologReaderTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 17; i++) jj_la1[i] = -1;
  }

  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 17; i++) jj_la1[i] = -1;
  }

  public SimilePrologReader(SimilePrologReaderTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 17; i++) jj_la1[i] = -1;
  }

  public void ReInit(SimilePrologReaderTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 17; i++) jj_la1[i] = -1;
  }

  final private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  final private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.Vector jj_expentries = new java.util.Vector();
  private int[] jj_expentry;
  private int jj_kind = -1;

  public ParseException generateParseException() {
    jj_expentries.removeAllElements();
    boolean[] la1tokens = new boolean[31];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 17; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 31; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.addElement(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = (int[])jj_expentries.elementAt(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  final public void enable_tracing() {
  }

  final public void disable_tracing() {
  }

        }

        class KeyValuePair {
                public String key;
                public Object value;

                public KeyValuePair(String key, Object value) {
                        this.key = key;
                        this.value = value;
                }
        }

        abstract class SimilePrologExpression {}

        class Source extends SimilePrologExpression {
                public String program;
                public String version;
                public String edition;
                public String date;

                public Source(String program, String version, String edition, String date) {
                        this.program = program;
                        this.version = version;
                        this.edition = edition;
                        this.date = date;
                }
        }

        class Roots extends SimilePrologExpression {
                public Vector<String> subnodes;

                public Roots(Vector<String> subnodes) {
                        this.subnodes = subnodes;
                }
        }

        class Properties extends SimilePrologExpression {
                public Hashtable<String, Object> properties;

                public Properties(Hashtable<String, Object> properties) {
                        this.properties = properties;
                }
        }

        class Node extends SimilePrologExpression {
                public String nodeType;
                public Vector<String> subnodes;
                public Hashtable<String, Object> properties;

                public Node(String nodeType, Vector<String> subnodes, Hashtable<String, Object> properties) {
                        this.nodeType = nodeType;
                        this.subnodes = subnodes;
                        this.properties = properties;
                }
        }

        class Links extends SimilePrologExpression {
                public Hashtable<String, String> arcPairs;

                public Links(Hashtable<String, String> arcPairs) {
                        this.arcPairs = arcPairs;
                }
        }

        class Arc extends SimilePrologExpression {
                public String sourceID;
                public String destinationID;
                public String arcType;
                public Hashtable<String, Object> properties;

                public Arc(String sourceID, String destinationID, String arcType, Hashtable<String, Object> properties) {
                        this.sourceID = sourceID;
                        this.destinationID = destinationID;
                        this.arcType = arcType;
                        this.properties = properties;
                }
        }
