package org.reactome.server.tools.reaction.exporter.renderer.glyph.entity;

import org.reactome.server.tools.reaction.exporter.layout.model.CompartmentGlyph;
import org.reactome.server.tools.reaction.exporter.layout.model.Layout;
import org.reactome.server.tools.reaction.exporter.renderer.canvas.ImageCanvas;
import org.reactome.server.tools.reaction.exporter.renderer.glyph.Renderer;
import org.reactome.server.tools.reaction.exporter.renderer.profile.DiagramProfile;
import org.reactome.server.tools.reaction.exporter.renderer.utils.ShapeFactory;
import org.reactome.server.tools.reaction.exporter.renderer.utils.StrokeStyle;

import java.awt.*;

public class CompartmentRenderer implements Renderer<CompartmentGlyph> {
    @Override
    public void draw(CompartmentGlyph compartment, ImageCanvas canvas, DiagramProfile profile, Layout layout) {
        final Shape rect = ShapeFactory.getRoundedRectangle(compartment.getPosition());
        canvas.getCompartmentFill().add(rect, profile.getCompartment().getFill());
        canvas.getCompartmentBorder().add(rect, profile.getCompartment().getStroke(), StrokeStyle.BORDER.getNormal());
        canvas.getCompartmentText().add(compartment.getName(), compartment.getLabelPosition(), profile.getCompartment().getText());
    }
}
