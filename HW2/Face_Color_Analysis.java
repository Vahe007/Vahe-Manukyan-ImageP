import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import java.io.File;
import java.io.FilenameFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeSet;

public class Face_Color_Analysis implements PlugIn {
    @Override
    public void run(String arg) {
        TreeSet<Integer> uniqueColors = collectFaceColors();

        int[] count = new int[256];
        int[] min = new int[256];
        int[] max = new int[256];
        double[] mean = new double[256];
        double[] mean2 = new double[256];

        processColors(uniqueColors, count, min, max, mean, mean2);

        outputResults(count, min, max, mean, mean2);
    }

    private TreeSet<Integer> collectFaceColors() {
        TreeSet<Integer> colors = new TreeSet<Integer>();
        File folder = new File("/Users/vmanukyan/Desktop/HW2/face");
        File[] files = folder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".png");
            }
        });

        if (files != null) {
            for (File file : files) {
                ImagePlus imp = IJ.openImage(file.getAbsolutePath());
                if (imp != null) {
                    ImageProcessor ip = imp.getProcessor();
                    
                    for (int y = 0; y < ip.getHeight(); y++) {
                        for (int x = 0; x < ip.getWidth(); x++) {
                            int pixel = ip.get(x, y);
                            if (pixel != 0) {
                                colors.add(pixel & 0xFFFFFF);
                            }
                        }
                    }
                    imp.close();
                }
            }
        }
        return colors;
    }

    private void processColors(TreeSet<Integer> colors, int[] count, int[] min, int[] max, double[] mean, double[] mean2) {

        for (int i = 0; i < 256; i++) {
            min[i] = 255;
            max[i] = 0;
        }

        for (Integer color : colors) {
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;
            double rb = (r + b) / 2.0;

            count[g]++;
            min[g] = Math.min(min[g], (int)rb);
            max[g] = Math.max(max[g], (int)rb);
            mean[g] += rb;
            mean2[g] += rb * rb;
        }


        for (int i = 0; i < 256; i++) {
            if (count[i] > 0) {
                mean[i] /= count[i];
                mean2[i] /= count[i];
            }
        }
    }

    private void outputResults(int[] count, int[] min, int[] max, double[] mean, double[] mean2) {
        StringBuilder sb = new StringBuilder();
        sb.append("G\tcount\tmin\tmax\tmean\tmean2\n");
        
        for (int g = 0; g < 256; g++) {
            sb.append(String.format("%d\t%d\t%d\t%d\t%.2f\t%.2f\n",
                    g, count[g], min[g], max[g], mean[g], mean2[g]));
        }
        
        IJ.log(sb.toString());
        
        
        saveResultsToFile(sb.toString());
    }

    private void saveResultsToFile(String results) {
        try {
            String filePath = "/Users/vmanukyan/Desktop/HW2/task3_result";
            FileWriter writer = new FileWriter(filePath);
            writer.write(results);
            writer.close();
            IJ.log("Results saved to: " + filePath);
        } catch (IOException e) {
            IJ.error("Error saving results to file: " + e.getMessage());
        }
    }
}
