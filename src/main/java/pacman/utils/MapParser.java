package pacman.utils;

import pacman.core.Location;
import pacman.core.elements.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapParser {
    private MapParser() {
        // hide implicit constructor
    }
    
    public static MapElements parse(String filePath) {
        String map = readFile(filePath);
        Collection<MapElements.MapElement> elements = new HashSet<>();
        int row = 0;
        int column = 0;
        int maxColumn = column;
        for (int position = 0 ; position < map.length() ; position++) {
            Location location = new Location(column, row);
            String icon = map.substring(position, position + 1);
            if ("\n".equals(icon)) {
                row++;
                column = 0;
            } else {
                elements.add(new MapElements.MapElement(location, icon));
                column++;
            }
            if (column > maxColumn) {
                maxColumn = column;
            }
        }

        return new MapElements(filter(elements, Gate::isGate),
                filter(elements, Ghost::isGhost),
                filter(elements, Pacman::isPacman),
                filter(elements, Pill::isPill),
                filter(elements, PowerPill::isPowerPill),
                filter(elements, Tunnel::isTunnel),
                filter(elements, Wall::isWall),
                maxColumn, row + 1);
    }
    private static String readFile(String filePath){
        StringBuilder contentBuilder = new StringBuilder();
        if (filePath.length()>0) {
            try {
                Stream<String> stream = Files.lines(
                        Paths.get(filePath),
                        StandardCharsets.UTF_8);
                stream.forEach(line -> contentBuilder.append(line).append("\n"));
                stream.close();
            }
            catch (IOException e)
            {
                System.err.println(e);
            }
        }
        return contentBuilder.toString().trim();
    }
    private static Collection<MapElements.MapElement> filter(Collection<MapElements.MapElement> elements, Predicate<String> filterFunc) {
        return elements.stream()
                .filter(element -> filterFunc.test(element.icon))
                .collect(Collectors.toSet());
    }
}
