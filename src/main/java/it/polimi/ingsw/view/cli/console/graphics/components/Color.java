package it.polimi.ingsw.view.cli.console.graphics.components;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Color {

    public static final String WHITE_BG = "[48;5;231m";
    public static final String BLACK_FG = "[38;2;0;0;0m";

    protected final String backgroundDark;
    protected final String backgroundLight;
    protected final String foregroundDark;
    protected final String foregroundLight;

    protected final String light;
    protected final String dark;

    /**
     * Default constructor
     * <p>
     * Loads a given input's color scheme
     *
     * @param in the properties file
     */
    public Color(InputStream in) {
        Properties properties = new Properties();
        try {
            properties.loadFromXML(in);
        } catch (IOException e) {
        }
        backgroundDark = "\033" + properties.getProperty("bgColorDark", WHITE_BG);
        backgroundLight = "\033" + properties.getProperty("bgColorLight", WHITE_BG);
        foregroundDark = "\033" + properties.getProperty("fgColorDark", BLACK_FG);
        foregroundLight = "\033" + properties.getProperty("fgColorDark", BLACK_FG);

        light = backgroundDark + foregroundLight;
        dark = backgroundLight + foregroundDark;
    }

    /**
     * <i>light</i> getter
     *
     * @return the object's light color scheme
     */
    public String getLight() {
        return light;
    }

    /**
     * <i>dark</i> getter
     *
     * @return the object's dark color scheme
     */
    public String getDark() {
        return dark;
    }

    /**
     * <i>backgroundDark</i> getter
     *
     * @return the object's dark background color
     */
    public String getBackgroundDark() {
        return backgroundDark;
    }

    /**
     * <i>backgroundLight</i> getter
     *
     * @return the object's light background color
     */
    public String getBackgroundLight() {
        return backgroundLight;
    }

    /**
     * <i>foregroundDark</i> getter
     *
     * @return the object's dark foreground color
     */
    public String getForegroundDark() {
        return foregroundDark;
    }

    /**
     * <i>foregroundLight</i> getter
     *
     * @return the object's light foreground color
     */
    public String getForegroundLight() {
        return foregroundLight;
    }
}
