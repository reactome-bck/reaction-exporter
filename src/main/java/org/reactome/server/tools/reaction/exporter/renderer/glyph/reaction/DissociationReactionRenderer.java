package org.reactome.server.tools.reaction.exporter.renderer.glyph.reaction;

import org.reactome.server.tools.reaction.exporter.layout.model.Layout;
import org.reactome.server.tools.reaction.exporter.layout.model.ReactionGlyph;
import org.reactome.server.tools.reaction.exporter.renderer.canvas.ImageCanvas;
import org.reactome.server.tools.reaction.exporter.renderer.profile.DiagramProfile;
import org.reactome.server.tools.reaction.exporter.renderer.utils.ShapeFactory;
import org.reactome.server.tools.reaction.exporter.renderer.utils.StrokeStyle;

import java.awt.*;

public class DissociationReactionRenderer extends ReactionRenderer {

    @Override
    protected Shape getShape(ReactionGlyph entity) {
        return ShapeFactory.getOval(entity.getPosition());
    }

    @Override
    protected Paint getFillColor(ReactionGlyph entity, DiagramProfile profile) {
        return profile.getReaction().getFill();
    }

    @Override
    public void draw(ReactionGlyph entity, ImageCanvas canvas, DiagramProfile profile, Layout layout) {
        super.draw(entity, canvas, profile, layout);
        canvas.getNodeBorder().add(ShapeFactory.getOval(entity.getPosition(), 3), getBorderColor(entity, profile), StrokeStyle.BORDER.getNormal());
    }
}
