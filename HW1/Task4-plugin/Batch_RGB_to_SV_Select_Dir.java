import ij.*;
import ij.io.*;
import ij.plugin.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;
import java.awt.Color;
import java.io.File;

public class Batch_RGB_to_SV_Select_Dir implements PlugIn {

    public void run(String arg) {
        // Prompt user to select the input directory of the images
        String inputDir = IJ.getDirectory("Choose the Source Directory");
        if (inputDir == null) {
            IJ.showMessage("Error", "No directory selected.");
            return;
        }

        // Prompts the user to select the output directory of the images
        String chosenFolder = IJ.getDirectory("Choose the Output Directory");
        String outputDirSV = chosenFolder + "sv" + File.separator;
        File outputDir = new File(outputDirSV);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // Get a list of all image files in the selected directory
        File folder = new File(inputDir);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) {
            IJ.showMessage("Error", "No files found in the directory.");
            return;
        }

        for (File file : listOfFiles) {
            if (file.isFile() && (file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg") || file.getName().endsWith(".png"))) {
                processImage(file, outputDirSV);
            }
        }

        IJ.showMessage("Processing Complete", "All images have been processed and saved in the SV_Output directory.");
    }

    private void processImage(File file, String outputDirSV) {
        String filePath = file.getPath();
        ImagePlus imp = new Opener().openImage(filePath);
        if (imp == null) {
            IJ.log("Failed to open image: " + filePath);
            return;
        }

        ImageProcessor ip = imp.getProcessor();
        int width = ip.getWidth();
        int height = ip.getHeight();

        // Create a new image processor for the SV image
        ImageProcessor svProcessor = new ByteProcessor(width, height);

        // Process each pixel
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int[] RGB = ip.getPixel(x, y, (int[]) null);

                float[] HSB = Color.RGBtoHSB(RGB[0], RGB[1], RGB[2], null);

                // Compute SV (Saturation * Brightness)
                float S = HSB[1];
                float V = HSB[2];
                float svProduct = S * V;

                // Scale SV product to 8-bit grayscale
                int svValue = Math.round(svProduct * 255);

                svProcessor.putPixel(x, y, svValue);
            }
        }

        ImagePlus svImage = new ImagePlus(imp.getTitle() + "_SV", svProcessor);

        String svPath = outputDirSV + file.getName().replaceAll("\\..*$", "") + "_SV.png";
        new FileSaver(svImage).saveAsPng(svPath);

        svImage.close();
        imp.close();
    }
}
