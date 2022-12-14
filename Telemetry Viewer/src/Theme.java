import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.jogamp.opengl.GL2ES3;


/**
 * All GUI-related colors, element spacing, and fonts are managed by this class.
 * Colors are specified in float[4]{r,g,b,a} format.
 * Element spacing is specified in true pixels (pre-multiplied by the display scaling factor.)
 */
public class Theme {

	// general swing and other settings
	public static Color  jpanelColor                      = new JPanel().getBackground();
	public static int    padding                          = Integer.parseInt(System.getProperty("java.version").split("\\.")[0]) >= 9 ? 5 : (int) (5 * ChartsController.getDisplayScalingFactor());
	public static String removeSymbol                     = "\uD83D\uDDD9";
	public static Color  defaultDatasetColor              = Color.RED;
	public static long   defaultChartDurationMilliseconds = 10_000;
	public static Cursor defaultCursor                    = new Cursor(Cursor.DEFAULT_CURSOR);
	public static Cursor clickableCursor                  = new Cursor(Cursor.HAND_CURSOR);
	public static Cursor upDownCursor                     = new Cursor(Cursor.N_RESIZE_CURSOR);
	public static Cursor leftRigthCursor                  = new Cursor(Cursor.E_RESIZE_CURSOR);
	public static Border narrowButtonBorder;
	static {
		JToggleButton temp = new JToggleButton("_");
		Insets insets = temp.getBorder().getBorderInsets(temp);
		narrowButtonBorder = new EmptyBorder(insets.top, Integer.max(insets.top, insets.bottom), insets.bottom, Integer.max(insets.top, insets.bottom));
	}
	
	// general openGL
	public static float  lineWidth = 1.0f;
	public static float  pointWidth = 3.0f;
	public static long   animationMilliseconds = 300;
	public static double animationMillisecondsDouble = 300.0;
	
	// charts region
	public static float[] tileColor               = new float[] {0.8f, 0.8f, 0.8f, 1.0f};
	public static float[] tileShadowColor         = new float[] {0.7f, 0.7f, 0.7f, 1.0f};
	public static float[] tileSelectedColor       = new float[] {0.5f, 0.5f, 0.5f, 1.0f};
	public static float   tilePadding             = 5.0f;
	public static float   tileShadowOffset        = tilePadding / 2;
	public static float[] neutralColor            = new float[] {jpanelColor.getRed() / 255.0f, jpanelColor.getGreen() / 255.0f, jpanelColor.getBlue() / 255.0f, 1.0f};
	public static float[] transparentNeutralColor = new float[] {neutralColor[0], neutralColor[1], neutralColor[2], 0.7f};
	
	// plot region
	public static float[] plotOutlineColor        = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
	public static float[] plotBackgroundColor     = neutralColor;
	public static float[] divisionLinesColor      = new float[] {0.7f, 0.7f, 0.7f, 1.0f};
	public static float[] divisionLinesFadedColor = new float[] {0.7f, 0.7f, 0.7f, 0.0f};
	
	// tooltips and markers in the plot region
	public static float[] tooltipBackgroundColor  = new float[] {1, 1, 1, 1};
	public static float[] tooltipBorderColor      = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
	public static float[] markerBorderColor       = new float[] {0.6f, 0.6f, 0.6f, 1.0f};
	public static float[] tooltipVerticalBarColor = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
	public static float   tooltipTextPadding      = 5.0f;
	
	// tick marks surrounding the plot region
	public static float[] tickLinesColor   = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
	public static float   tickLength       = 6.0f;
	public static float   tickTextPadding  = 3.0f;
	
	// legend
	public static float[] legendBackgroundColor = tileShadowColor;
	public static float   legendTextPadding     = 5.0f;
	public static float   legendNamesPadding    = 25.0f;
	
	// fonts
	public static Font smallFont  = new Font("Geneva", Font.PLAIN, 12);
	public static Font mediumFont = new Font("Geneva", Font.BOLD,  14);
	public static Font largeFont  = new Font("Geneva", Font.BOLD,  18);
	
	/**
	 * This method must be called when the OpenGL context is initialized,
	 * and any time the display scaling factor changes.
	 * 
	 * @param gl                      The OpenGL context.
	 * @param displayScalingFactor    The display scaling factor.
	 */
	public static void initialize(GL2ES3 gl, float displayScalingFactor) {
			
		lineWidth  = 1.0f * displayScalingFactor;
		pointWidth = 3.0f * displayScalingFactor;
		
		tilePadding      = 5.0f * displayScalingFactor;
		tileShadowOffset = tilePadding / 2;
		
		tooltipTextPadding = 5.0f * displayScalingFactor;
		
		tickLength      = 6.0f * displayScalingFactor;
		tickTextPadding = 3.0f * displayScalingFactor;
		
		legendTextPadding  = 5.0f * displayScalingFactor;
		legendNamesPadding = 25.0f * displayScalingFactor;
		
		smallFont  = new Font("Geneva", Font.PLAIN, (int) (12.0 * displayScalingFactor));
		mediumFont = new Font("Geneva", Font.BOLD,  (int) (14.0 * displayScalingFactor));
		largeFont  = new Font("Geneva", Font.BOLD,  (int) (18.0 * displayScalingFactor));
		OpenGL.updateFontTextures(gl);
		
	}

}
