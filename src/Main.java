import edu.thu.gskyline.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Main {

    private static String DATASETS_DIR = "datasets";
    private static String RESULTS_DIR = "results";
    private static Pattern DATASET_NAME_PATTEN = Pattern.compile("(.*)_(\\d+)(.*)");
    private static final int K = 4;

    private static Dataset parseDataset(File file) {
        if (!file.exists() || !file.isFile()) {
            System.err.println(file.getAbsoluteFile() + "is not a valid file");
            return null;
        }

        Matcher m = DATASET_NAME_PATTEN.matcher(file.getName());
        if (!m.find()) {
            System.err.println(file.getAbsoluteFile() + "name not valid");
            return null;
        }

        Dataset dataset = new Dataset();
        dataset.category = m.group(1);
        dataset.dimension = Integer.valueOf(m.group(2));
        dataset.points = new ArrayList<>();

        final int[] idx = {1};
        try (Stream<String> stream = Files.lines(file.toPath())) {
            stream.forEach(line -> {
                String[] fields = line.split(" ");
                if (fields.length != dataset.dimension) {
                    throw new RuntimeException("Dataset is broken, dimension mismatch");
                }
                DataPoint datapoint = new DataPoint();
                datapoint.idx = idx[0]++;
                datapoint.data = Arrays.stream(fields).mapToDouble(Double::valueOf).toArray();
                dataset.points.add(datapoint);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataset;
    }

    public static void main(String[] args) {

        GSkylineService gSkyline = new GSkylineBaseImpl();

        File dirFile = new File(DATASETS_DIR);
        if (!dirFile.exists() || (!dirFile.isDirectory())) {
            System.err.println("Cannot find datasets");
        }
        String[] files = dirFile.list();
        for (int i = 0; i < files.length; i++) {
            Dataset dataset = parseDataset(Paths.get(DATASETS_DIR, files[i]).toFile());
            if (dataset == null) {
                continue;
            }
//            if (!"test".equals(dataset.category)) {
//                continue;
//            }
            if (2 != dataset.dimension)
                continue;
            System.out.println("===Find: " + files[i]);
            DirectedSkylineGraph graph = DirectedSkylineGraph.createFrom(dataset, K);
            System.out.println("Get layers size : "+graph.layers.size());

            List<Set<DataPoint>> result = gSkyline.getGSkyline(graph, K);
            // skip if points number > 200, brute force is too slow
            if (result == null)continue;
            System.out.println("Get group size : " + result.size());
//            for (int j = 0; j < result.size(); j++) {
//                System.out.println(result.get(j));
//            }
        }
    }
}
