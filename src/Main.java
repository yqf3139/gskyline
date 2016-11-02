import edu.thu.gskyline.*;

import java.io.File;
import java.util.*;

public class Main {

    private static Map<String, GSkylineService> serviceMap = new HashMap<>();

    static {
        serviceMap.put("baseline", new GSkylineBaseImpl());
        serviceMap.put("pwise", new GSkylinePointWiseImpl());
        serviceMap.put("uwise", new UWise());
        serviceMap.put("uwise+", new UWisePlus());
    }

    public static void main(String[] args) {
        // testBuildGraph(args);
        // if (true)return;
        if (args.length < 3) {
            System.out.println("usage: java -jar gskyline.jar method K dataset dir or file ...");
            System.out.println("method: all baseline pwise uwise uwise+");
            System.exit(-1);
        }

        Map<String, GSkylineService> services = null;
        if ("all".equals(args[0])) {
            services = serviceMap;
        } else if (serviceMap.containsKey(args[0])) {
            services = new HashMap<>();
            services.put(args[0], serviceMap.get(args[0]));
        } else {
            System.err.println("please provide a valid method");
            System.exit(-2);
        }

        int k = 4;
        try {
            k = Integer.parseInt(args[1]);
        } catch (Exception ignored) {
        }

        System.out.println("Using K: " + k);
        for (int i = 2; i < args.length; i++) {
            runTestCase(services, k, args[i]);
        }
    }

    private static void testBuildGraph(String[] filenames) {
        System.out.printf("dataset, k, time, layersize\n");
        for (String filename : filenames) {
            for (int k : new int[]{Integer.MAX_VALUE}) {
                Dataset dataset = Dataset.parseDataset(new File(filename));
                if (dataset == null) {
                    return;
                }
                long start = System.currentTimeMillis();
                DirectedSkylineGraph graph = DirectedSkylineGraph.createFrom(dataset, k);
                long time = System.currentTimeMillis() - start;
//                System.out.printf("%s, %d, %d, %d\n", filename, k, time, graph.layers.size());
            }
        }

    }

    private static void runTestCase(Map<String, GSkylineService> services, int K, String filename) {
        Dataset dataset = Dataset.parseDataset(new File(filename));
        if (dataset == null) {
            return;
        }
        System.out.println("Filename: " + filename);

        DirectedSkylineGraph graph = null;

        for (String name : services.keySet()) {
            dataset = Dataset.parseDataset(new File(filename));
            graph = DirectedSkylineGraph.createFrom(dataset, K);

            int groupSize = 0;
            long timeElapsed = 0;
            long start = System.currentTimeMillis();
            List<Set<DataPoint>> result = services.get(name).getGSkyline(graph, K);
            timeElapsed = System.currentTimeMillis() - start;

            // skip baseline if points number > 100, brute force is too slow
            if (result != null) {
                groupSize = result.size();
            } else {
                continue;
            }
            System.out.println(String.format("> %s Group size : %d, Time: %d", name, groupSize, timeElapsed));
        }

    }

}
