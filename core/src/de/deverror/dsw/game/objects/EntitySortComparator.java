package de.deverror.dsw.game.objects;

import java.util.Comparator;

public class EntitySortComparator implements Comparator<Entity> {

    public static EntitySortComparator INSTANCE = new EntitySortComparator();

    @Override
    public int compare(Entity o1, Entity o2) {
        return ( (int) o1.getY()) - ((int) o2.getY() );
    }
}
