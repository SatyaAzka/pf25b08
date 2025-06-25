import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

public enum Seed {
    CROSS("image/Heart", "image/Heart.png"),
    NOUGHT("image/Flowey", "image/Flowey.png"),
    NO_SEED(" ", null),
    Background2P("image/2PBackground", "image/2PBackground.gif"),
    BackgroundEasy("image/EasyBG", "image/EasyBG.png"),
    BackgroundHard("image/HardBG", "image/HardBG.gif");

    private String displayName;
    private Image img = null;

    private Seed(String name, String imageFilename) {
        this.displayName = name;

        if (imageFilename != null) {
            URL imgURL = getClass().getClassLoader().getResource(imageFilename);
            ImageIcon icon = null;
            if (imgURL != null) {
                icon = new ImageIcon(imgURL);
                // debugging
            } else {
                System.err.println("Couldn't find file " + imageFilename);
            }
            img = icon.getImage();
        }
    }

    public String getDisplayName() {
        return displayName;
    }
    public Image getImage() {
        return img;
    }
}