package de.deverror.dsw.util;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

public class ShapeUtils {

    public static Shape tmxToBox2D(MapObject object) {
        if (object instanceof RectangleMapObject) {
            return getRectangle((RectangleMapObject)object);
        }
        else if (object instanceof PolygonMapObject) {
            return getPolygon((PolygonMapObject)object);
        } else {
            System.err.println("Unsupported shape in .tmx file");
            return null;
        }
    }

    private static PolygonShape getPolygon(PolygonMapObject polygonObject) {
        PolygonShape polygon = new PolygonShape();
        float[] vertices = polygonObject.getPolygon().getTransformedVertices();

        float[] worldVertices = new float[vertices.length];

        for (int i = 0; i < vertices.length; ++i) {
            worldVertices[i] = vertices[i]; //POSSIBLE ERROR CAUSE
        }

        polygon.set(worldVertices);
        return polygon;
    }

    private static PolygonShape getRectangle(RectangleMapObject rectangleObject) {
        float a = 1;
        Rectangle rectangle = rectangleObject.getRectangle();
        PolygonShape polygon = new PolygonShape();
        Vector2 size = new Vector2((rectangle.x + rectangle.width * 0.5f) / a,
                (rectangle.y + rectangle.height * 0.5f ) / a);
        polygon.setAsBox(rectangle.width * 0.5f / a,
                rectangle.height * 0.5f / a,
                size,
                0.0f);
        return polygon;
    }


}
