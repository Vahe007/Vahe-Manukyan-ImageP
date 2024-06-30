import ij.*;
import ij.process.*;
import ij.plugin.filter.PlugInFilter;
import java.awt.Color;

public class RGB_to_SV_with_Color implements PlugInFilter {
    public int setup(String arg, ImagePlus imp) {
        return DOES_RGB + NO_CHANGES;
    }

    public void run(ImageProcessor ip) {
        int width = ip.getWidth();
        int height = ip.getHeight();

        // Create a new image processor for the SV image
        ImageProcessor svProcessor = new ByteProcessor(width, height);

        // Process each pixel
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Get RGB values
                int[] RGB = ip.getPixel(x, y, (int[]) null);

                // Convert RGB to HSB using Java's built-in method
                float[] HSB = Color.RGBtoHSB(RGB[0], RGB[1], RGB[2], null);

                // Compute SV (Saturation * Brightness)
                float S = HSB[1];
                float V = HSB[2];
                float svProduct = S * V;

                // Scale SV product to 8-bit grayscale
                int svValue = Math.round(svProduct * 255);

                // Set the pixel value in the new image
                svProcessor.putPixel(x, y, svValue);
            }
        }

        // Create and show the new image
        new ImagePlus("Saturation * Brightness", svProcessor).show();

    }
}
