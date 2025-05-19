package cs113.trolley;

import javafx.scene.paint.Color;

import java.util.*;

// ********** Graph Construction ********** //
class TrolleyGraph {
    private List<TrolleyStation> stations;
    private List<TrolleyRoute> routes;

    public TrolleyGraph() {
        stations = new ArrayList<>();
        routes = new ArrayList<>();
    }

    // Add a new station (node) to the graph
    public void addStation(String name, int x, int y) {
        // todo: Implement this method to add a new station
        // Make sure to check if a station with the same name already exists
        if(getStationByName(name) == null) {
            TrolleyStation station = new TrolleyStation(name, x, y);
            stations.add(station);
        }
    }

    // Get a station by its name
    public TrolleyStation getStationByName(String name) {
        // todo: Implement this method to find a station by name
        Iterator<TrolleyStation> iterator = stations.iterator();
        while(iterator.hasNext())
        {
            TrolleyStation temp = iterator.next();
            if(temp.getName().equals(name))
            {
                return temp;
            }
        }
        return null;
    }

    // Get all station names
    public Set<String> getStationNames() {
        Set<String> names = new HashSet<>();
        for (TrolleyStation station : stations) {
            names.add(station.getName());
        }
        return names;
    }

    public int getStationIndex(String name)
    {
        for(int i = 0; i < stations.size(); i++)
        {
            if(stations.get(i).getName().equals(name))
            {
                return i;
            }
        }
        return -1;
    }

    // Add a new route (edge) between two stations
    public void addRoute(String fromStation, String toStation, int weight, Color color) {
        // todo: Implement this method to add a new route
        // Make sure both stations exist before adding the route
        if(getStationByName(fromStation) == null || getStationByName(toStation) == null)
        {
            return;
        }
        TrolleyRoute route = new TrolleyRoute(fromStation, toStation, weight, color);
        routes.add(route);
    }

    // Get all stations
    public List<TrolleyStation> getStations() {
        return stations;
    }

    // Get all routes
    public List<TrolleyRoute> getRoutes() {
        return routes;
    }

    // ********** Adjacency Lists ********** //
    public List<String> getAdjacentStations(String stationName) {
        // todo: Implement this method to find all stations connected to the given station
        List<String> adjacent = new ArrayList<>();
        Iterator<TrolleyRoute> iterator = routes.iterator();
        while(iterator.hasNext())
        {
            TrolleyRoute temp = iterator.next();
            if(temp.getToStation().equals(stationName))
            {
                adjacent.add(temp.getFromStation());
            }
            if(temp.getFromStation().equals(stationName))
            {
                adjacent.add(temp.getToStation());
            }
        }
        return adjacent;
    }

    // Get the weight of a route between two stations
    public int getRouteWeight(String fromStation, String toStation) {
        // todo: Calculate the route weight between stations
        Iterator<TrolleyRoute> iterator = routes.iterator();
        while(iterator.hasNext())
        {
            TrolleyRoute temp = iterator.next();
            if(temp.getFromStation().equals(fromStation) && temp.getToStation().equals(toStation))
            {
                return temp.getWeight();
            }
            if(temp.getFromStation().equals(toStation) && temp.getToStation().equals(fromStation))
            {
                return temp.getWeight();
            }
        }
        return -1; // No direct route
    }

    // ********** Breadth First Search (BFS) ********** //
    public List<String> breadthFirstSearch(String startStation, String endStation) {
        // todo: Implement a BFS (see readme)
        Map<String, String> parentMap = new HashMap<>();
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.add(startStation);
        visited.add(startStation);

        while(!queue.isEmpty())
        {
            String current = queue.poll();
            if(current.equals(endStation))
            {
                return reconstructPath(parentMap, startStation, endStation);
            }
            List<String> neighbors = getAdjacentStations(current);
            for(String neighbor : neighbors)
            {
                if(!visited.contains(neighbor))
                {
                    queue.add(neighbor);
                    visited.add(neighbor);
                    parentMap.put(neighbor, current);
                }
            }
        }

        return null; // No path found
    }

    // ********** Depth First Search (DFS) ********** //
    public List<String> depthFirstSearch(String startStation, String endStation) {
        // todo: Implement a DFS (see readme)
        Map<String, String> parentMap = new HashMap<>();
        Set<String> visited = new HashSet<>();
        if(dfsHelper(parentMap, visited, startStation, endStation))
        {
            return reconstructPath(parentMap, startStation, endStation);
        }
        return null; // No path found
    }

    public boolean dfsHelper(Map<String, String> parentMap, Set<String> visited, String current, String end)
    {
        if(current.equals(end))
        {
            return true;
        }
        visited.add(current);
        List<String> neighbors = getAdjacentStations(current);
        for(String neighbor : neighbors)
        {
            if(!visited.contains(neighbor))
            {
                parentMap.put(neighbor, current);
                if(dfsHelper(parentMap, visited, neighbor, end))
                {
                    return true;
                }
            }
        }
        return false;
    }

    // ********** Dijkstra's Algorithm ********** //
    public List<String> dijkstra(String startStation, String endStation) {
        // todo: Implement Dijkstra's Algorithm
        Map<String, String> parentMap = new HashMap<>();
        int[] distances = new int[stations.size()];
        Arrays.fill(distances, Integer.MAX_VALUE);
        int index = getStationIndex(startStation);
        distances[index] = 0;
        PriorityQueue<DNode> pq = new PriorityQueue<>();
        pq.add(new DNode(startStation, 0));
        while(!pq.isEmpty())
        {
            DNode node = pq.poll();
            if(node.name.equals(endStation))
            {
                return reconstructPath(parentMap, startStation, endStation);
            }
            int current_index = getStationIndex(node.name);
            int current_distance = distances[current_index];
            List<String> neighbors = getAdjacentStations(node.name);
            for(String neighbor : neighbors)
            {
                int neighbor_index = getStationIndex(neighbor);
                int neighbor_distance = distances[neighbor_index];
                int route_weight = getRouteWeight(node.name, neighbor);
                if(neighbor_distance > current_distance + route_weight)
                {
                    distances[neighbor_index] = current_distance + route_weight;
                    parentMap.put(neighbor, node.name);
                    pq.add(new DNode(neighbor, distances[neighbor_index]));
                }
            }
        }
        return null; // No path found
    }

    class DNode implements Comparable<DNode>
    {
        String name;
        int distance;
        DNode(String name, int distance)
        {
            this.name = name;
            this.distance = distance;
        }

        @Override
        public int compareTo(DNode o) {
            return distance - o.distance;
        }
    }
    // Helper method to reconstruct the path from start to end using the parent map
    private List<String> reconstructPath(Map<String, String> parentMap, String start, String end) {
        List<String> path = new ArrayList<>();
        String current = end;

        while (current != null) {
            path.add(0, current);
            current = parentMap.get(current);

            if (current != null && current.equals(start)) {
                path.add(0, start);
                break;
            }
        }

        return path.size() > 1 ? path : null;
    }
}