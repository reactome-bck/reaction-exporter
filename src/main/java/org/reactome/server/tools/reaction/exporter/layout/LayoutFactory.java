package org.reactome.server.tools.reaction.exporter.layout;

import org.reactome.server.graph.domain.model.ReactionLikeEvent;
import org.reactome.server.graph.exception.CustomQueryException;
import org.reactome.server.graph.service.AdvancedDatabaseObjectService;
import org.reactome.server.tools.reaction.exporter.layout.algorithm.breathe.BreatheAlgorithm;
import org.reactome.server.tools.reaction.exporter.layout.model.Layout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Factory for single reaction {@link Layout}. Layout is computed with positions and dimensions already set for each
 * glyph. This factory uses a fixed layout, with an approximate O(number of entities + number of compartments) cost.
 *
 * @author Pascual Lorente (plorente@ebi.ac.uk)
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
@Component
public class LayoutFactory {


    private static final String QUERY = "" +
            "MATCH (rle:ReactionLikeEvent{stId:{stId}}) " +
            "OPTIONAL MATCH (rle)-[:normalReaction]->(nr:ReactionLikeEvent)" +

            "OPTIONAL MATCH (rle)-[i:input]->(pe:PhysicalEntity) " +
            "WHERE NOT (rle)-[:entityFunctionalStatus{physicalEntity:pe.dbId}]->() " +
            "OPTIONAL MATCH (pe)-[:hasComponent|hasMember|hasCandidate*]->(d:Drug) " +
            "WITH rle, nr, COLLECT(DISTINCT CASE pe WHEN NULL THEN NULL ELSE {physicalEntity: pe, role:{n: i.stoichiometry, type: 'input'}, drug: (pe:Drug) OR NOT d IS NULL} END) AS ps " +
            "OPTIONAL (rle)-[:normalReaction]->(:ReactionLikeEvent)-[i:input]->(pe:PhysicalEntity) " +
            "WHERE NOT (rle)-[:input]->(pe) " +
            "OPTIONAL MATCH (pe)-[:hasComponent|hasMember|hasCandidate*]->(d:Drug) " +
            "WITH rle, nr, ps + COLLECT(DISTINCT CASE pe WHEN NULL THEN NULL ELSE {physicalEntity: pe, role:{n: i.stoichiometry, type: 'input'}, drug: (pe:Drug) OR NOT d IS NULL, crossed:true} END) AS ps " +

            "OPTIONAL MATCH (rle)-[o:output]->(pe:PhysicalEntity) " +
            "WHERE NOT (rle)-[:entityFunctionalStatus{physicalEntity:pe.dbId}]->() " +
            "OPTIONAL MATCH (pe)-[:hasComponent|hasMember|hasCandidate*]->(d:Drug) " +
            "WITH rle, nr, ps + COLLECT(DISTINCT CASE pe WHEN NULL THEN NULL ELSE {physicalEntity: pe, role:{n: o.stoichiometry, type: 'output'}, drug: (pe:Drug) OR NOT d IS NULL} END) AS ps " +
            "OPTIONAL (rle)-[:normalReaction]->(:ReactionLikeEvent)-[o:output]->(pe:PhysicalEntity) " +
            "WHERE NOT (rle)-[:output]->(pe) " +
            "OPTIONAL MATCH (pe)-[:hasComponent|hasMember|hasCandidate*]->(d:Drug) " +
            "WITH rle, nr, ps + COLLECT(DISTINCT CASE pe WHEN NULL THEN NULL ELSE {physicalEntity: pe, role:{n: o.stoichiometry, type: 'output'}, drug: (pe:Drug) OR NOT d IS NULL, crossed:true} END) AS ps " +

            "OPTIONAL MATCH (rle)-[:catalystActivity|physicalEntity*]->(pe:PhysicalEntity) " +
            "WHERE NOT (rle)-[:entityFunctionalStatus{physicalEntity:pe.dbId}]->() " +
            "OPTIONAL MATCH (pe)-[:hasComponent|hasMember|hasCandidate*]->(d:Drug) " +
            "WITH rle, nr, ps + COLLECT(DISTINCT CASE pe WHEN NULL THEN NULL ELSE {physicalEntity: pe, role:{n: 1, type: 'catalyst'}, drug: (pe:Drug) OR NOT d IS NULL} END) AS ps " +
            "OPTIONAL (rle)-[:normalReaction]->(:ReactionLikeEvent)-[:catalystActivity|physicalEntity*]->(pe:PhysicalEntity) " +
            "WHERE NOT (rle)-[:catalystActivity|physicalEntity*]->(pe) " +
            "OPTIONAL MATCH (pe)-[:hasComponent|hasMember|hasCandidate*]->(d:Drug) " +
            "WITH rle, nr, ps + COLLECT(DISTINCT CASE pe WHEN NULL THEN NULL ELSE {physicalEntity: pe, role:{n: 1, type: 'catalyst'}, drug: (pe:Drug) OR NOT d IS NULL, crossed:true} END) AS ps " +

            "OPTIONAL MATCH (rle)-[:regulatedBy]->(:NegativeRegulation)-[:regulator]->(pe:PhysicalEntity) " +
            "WHERE NOT (rle)-[:entityFunctionalStatus{physicalEntity:pe.dbId}]->() " +
            "OPTIONAL MATCH (pe)-[:hasComponent|hasMember|hasCandidate*]->(d:Drug) " +
            "WITH rle, nr, ps + COLLECT(DISTINCT CASE pe WHEN NULL THEN NULL ELSE {physicalEntity: pe, role:{n: 1, type: 'negative'}, drug: (pe:Drug) OR NOT d IS NULL} END) AS ps " +
            "OPTIONAL (rle)-[:normalReaction]->(:ReactionLikeEvent)-[:regulatedBy]->(:NegativeRegulation)-[:regulator]->(pe:PhysicalEntity) " +
            "WHERE NOT (rle)-[:regulatedBy]->(:NegativeRegulation)-[:regulator]->(pe) " +
            "OPTIONAL MATCH (pe)-[:hasComponent|hasMember|hasCandidate*]->(d:Drug) " +
            "WITH rle, nr, ps + COLLECT(DISTINCT CASE pe WHEN NULL THEN NULL ELSE {physicalEntity: pe, role:{n: 1, type: 'negative'}, drug: (pe:Drug) OR NOT d IS NULL, crossed:true} END) AS ps " +

            "OPTIONAL MATCH (rle)-[:regulatedBy]->(:PositiveRegulation)-[:regulator]->(pe:PhysicalEntity) " +
            "WHERE NOT (rle)-[:entityFunctionalStatus{physicalEntity:pe.dbId}]->() " +
            "OPTIONAL MATCH (pe)-[:hasComponent|hasMember|hasCandidate*]->(d:Drug) " +
            "WITH rle, nr, ps + COLLECT(DISTINCT CASE pe WHEN NULL THEN NULL ELSE {physicalEntity: pe, role:{n: 1, type: 'positive'}, drug: (pe:Drug) OR NOT d IS NULL} END) AS ps " +
            "OPTIONAL (rle)-[:normalReaction]->(:ReactionLikeEvent)-[:regulatedBy]->(:PositiveRegulation)-[:regulator]->(pe:PhysicalEntity) " +
            "WHERE NOT (rle)-[:regulatedBy]->(:PositiveRegulation)-[:regulator]->(pe) " +
            "OPTIONAL MATCH (pe)-[:hasComponent|hasMember|hasCandidate*]->(d:Drug) " +
            "WITH rle, nr, ps + COLLECT(DISTINCT CASE pe WHEN NULL THEN NULL ELSE {physicalEntity: pe, role:{n: 1, type: 'positive'}, drug: (pe:Drug) OR NOT d IS NULL, crossed:true} END) AS ps " +

            "OPTIONAL MATCH (rle)-[s:entityFunctionalStatus]->(:EntityFunctionalStatus)-[:physicalEntity]->(pe:PhysicalEntity) " +
            "WHERE NOT s.role IS NULL " +
            "OPTIONAL MATCH (pe)-[:hasComponent|hasMember|hasCandidate*]->(d:Drug) " +
            "WITH rle, ps + COLLECT(DISTINCT CASE pe WHEN NULL THEN NULL ELSE {physicalEntity: pe, role:{n: 1, type: s.role}, drug: (pe:Drug) OR NOT d IS NULL, dashed:s.dashed} END) AS ps " +

            "RETURN rle AS reactionLikeEvent, ps AS participants";
    private AdvancedDatabaseObjectService ads;

    @Autowired
    public LayoutFactory(AdvancedDatabaseObjectService ads) {
        this.ads = ads;
    }

    /**
     * Gets the {@link Layout} of rle
     *
     * @param rle a ReactionLikeEvent
     * @return the corresponding layout of the rle
     * @throws NullPointerException if rle is null
     */
    public Layout getReactionLikeEventLayout(ReactionLikeEvent rle, Style style) {
        if (rle == null) throw new NullPointerException("rle cannot be null");
        Map<String, Object> params = new HashMap<>();
        params.put("stId", rle.getStId());
        try {
            final Layout layout = ads.getCustomQueryResult(Layout.class, QUERY, params);
            style.apply(layout);
            return layout;
        } catch (CustomQueryException e) {
            //TODO: log -> e.printStackTrace();
            e.printStackTrace();
            return null;
        }
    }

    public enum Style {
        COMPACT(layout -> new BreatheAlgorithm().compute(layout, true)),
        BREATHE(layout -> new BreatheAlgorithm().compute(layout));
        private Consumer<Layout> consumer;

        Style(Consumer<Layout> consumer) {
            this.consumer = consumer;
        }

        void apply(Layout layout) {
            consumer.accept(layout);
        }

    }

}
