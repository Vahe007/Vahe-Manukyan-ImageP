import ij.*;
import ij.process.*;
import ij.plugin.filter.PlugInFilter;
import ij.io.FileSaver;
import java.awt.Color;

public class Extract_Facial_Pixels implements PlugInFilter {
    private double hueMin, hueMax, satMin, satMax, briMin, briMax, svMin, svMax;
    private ImagePlus imp;

    // Setting up filter to process RGB images
    public int setup(String arg, ImagePlus imp) {
        this.imp = imp;
        //threshold values
        hueMin = 0.0 / 360.0;
        hueMax = 37.0 / 360.0;
        satMin = 9.0 / 255.0;
        satMax = 236.0 / 255.0;
        briMin = 0.0 / 255.0;
        briMax = 187.0 / 255.0;
        svMin = 5.0 / 255.0;
        svMax = 94.0 / 255.0;
        return DOES_RGB;
    }


    public void run(ImageProcessor ip) {
        int width = ip.getWidth();
        int height = ip.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = ip.getPixel(x, y);
                Color color = new Color(pixel);
                float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
                
                float hue = hsb[0];
                float sat = hsb[1];
                float bri = hsb[2];
                float sv = sat * bri;

                // Checking wether the currect pixes is within the threshold boundaries
                boolean isFacialPixel = (hue >= hueMin && hue <= hueMax) &&
                                        (sat >= satMin && sat <= satMax) &&
                                        (bri >= briMin && bri <= briMax) &&
                                        (sv >= svMin && sv <= svMax);

                //if the pixel falls outsite the boundary, then its changed to white
                if (!isFacialPixel) {
                    ip.putPixel(x, y, Color.WHITE.getRGB());
                }
            }
        }

        // Path to the folder, where the extracted image will be saved
        String outputPath = "/Users/vmanukyan/Desktop/HW1/face/";
        String outputFileName = outputPath + imp.getTitle();
        FileSaver fs = new FileSaver(new ImagePlus("Processed Image", ip));
        fs.saveAsPng(outputFileName);
    }

    public static void main(String[] args) {
        // This main method is just for testing purposes
        ImageJ ij = new ImageJ();
        ImagePlus image = IJ.openImage("path_to_image.jpg");
        image.show();
        IJ.runPlugIn(image, "Extract_Facial_Pixels", "");
    }
}
