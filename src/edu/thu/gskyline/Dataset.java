package edu.thu.gskyline;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Dataset {

    private static Pattern DATASET_NAME_PATTEN = Pattern.compile("(.*)_(\\d+)(.*)");

    public String category;
    public int dimension;
    public List<DataPoint> points;

    public void sortBy(int dim) {
        if (dim < 0 || dim > dimension - 1) {
            return;
        }
        Collections.sort(points,
                (o1, o2) -> Double.compare(o1.data[dim], o2.data[dim]));
    }


    public static Dataset parseDataset(File file) {
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

        try (Stream<String> stream = Files.lines(file.toPath())) {
            stream.forEach(line -> {
                String[] fields = line.split(" ");
                if (fields.length != dataset.dimension) {
                    throw new RuntimeException("Dataset is broken, dimension mismatch");
                }
                DataPoint datapoint = new DataPoint();
                datapoint.data = Arrays.stream(fields).mapToDouble(Double::valueOf).toArray();
                dataset.points.add(datapoint);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataset;
    }
}
