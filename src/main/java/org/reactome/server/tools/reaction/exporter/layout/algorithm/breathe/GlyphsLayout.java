package org.reactome.server.tools.reaction.exporter.layout.algorithm.breathe;

import org.reactome.server.tools.diagram.data.layout.impl.CoordinateImpl;
import org.reactome.server.tools.reaction.exporter.layout.algorithm.common.Transformer;
import org.reactome.server.tools.reaction.exporter.layout.common.EntityRole;
import org.reactome.server.tools.reaction.exporter.layout.common.Position;
import org.reactome.server.tools.reaction.exporter.layout.model.EntityGlyph;
import org.reactome.server.tools.reaction.exporter.layout.model.Glyph;
import org.reactome.server.tools.reaction.exporter.layout.model.Role;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class GlyphsLayout implements Div {

    private List<? extends Glyph> glyphs;
    private double leftPadding = 5;
    private double rightPadding = 5;
    private double topPadding = 5;
    private double bottomPadding = 5;
    private double separation = 5;
    private Position bounds;

    public GlyphsLayout(List<? extends Glyph> glyphs) {
        this.glyphs = glyphs;
    }

    public void setSeparation(double separation) {
        this.separation = separation;
    }

    public List<? extends Glyph> getGlyphs() {
        return glyphs;
    }

    @Override
    public String toString() {
        return String.format("%d glyphs", glyphs.size());
    }

    @Override
    public Position getBounds() {
        if (bounds == null) bounds = layout();
        return bounds;
    }


    @Override
    public void setHorizontalPadding(double padding) {
        leftPadding = rightPadding = padding;
    }

    @Override
    public void setVerticalPadding(double padding) {
        topPadding = bottomPadding = padding;
    }

    @Override
    public void setPadding(double padding) {
        leftPadding = rightPadding = topPadding = bottomPadding = padding;
    }

    @Override
    public void center(double x, double y) {
        final Position bounds = getBounds();
        move(x - bounds.getCenterX(), y - bounds.getCenterY());
    }

    @Override
    public void move(double dx, double dy) {
        final Position bounds = getBounds();
        final CoordinateImpl delta = new CoordinateImpl(dx, dy);
        for (final Glyph glyph : glyphs) Transformer.move(glyph, delta);
        bounds.move(dx, dy);
    }

    @Override
    public Set<EntityRole> getContainedRoles() {
        return glyphs.stream()
                .filter(EntityGlyph.class::isInstance)
                .map(EntityGlyph.class::cast)
                .flatMap(entityGlyph -> entityGlyph.getRoles().stream())
                .map(Role::getType)
                .collect(Collectors.toSet());
    }


    protected double getSeparation() {
        return separation;
    }

    protected double getBottomPadding() {
        return bottomPadding;
    }

    protected double getLeftPadding() {
        return leftPadding;
    }

    protected double getRightPadding() {
        return rightPadding;
    }

    protected double getTopPadding() {
        return topPadding;
    }

    protected void setBottomPadding(double bottomPadding) {
        this.bottomPadding = bottomPadding;
    }

    protected void setLeftPadding(double leftPadding) {
        this.leftPadding = leftPadding;
    }

    protected void setRightPadding(double rightPadding) {
        this.rightPadding = rightPadding;
    }

    protected void setTopPadding(double topPadding) {
        this.topPadding = topPadding;
    }

    abstract Position layout();

}
