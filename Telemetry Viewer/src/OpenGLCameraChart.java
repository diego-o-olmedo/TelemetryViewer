import com.jogamp.opengl.GL2ES3;
import com.jogamp.opengl.GL3;

/**
 * Displays images from a camera.
 * 
 * User settings:
 *     Camera to use.
 *     Rotation and mirroring.
 *     A label can be displayed.
 */
public class OpenGLCameraChart extends PositionedChart {
	
	ConnectionCamera camera = null;
	long previousFrameTimestamp = 0;
	
	// image region on screen
	int[] texHandle;
	boolean mirrorX;
	boolean mirrorY;
	boolean rotateClockwise;
	float xDisplayLeft;
	float xDisplayRight;
	float yDisplayTop;
	float yDisplayBottom;
	float displayWidth;
	float displayHeight;
	
	// label
	boolean showLabel;
	float labelWidth;
	float xLabelLeft;
	float xLabelRight;
	float yLabelBaseline;
	float yLabelTop;
	
	// control widgets
	WidgetCamera cameraWidget;
	WidgetCheckbox mirrorXwidget;
	WidgetCheckbox mirrorYwidget;
	WidgetCheckbox rotatewidget;
	WidgetCheckbox labelWidget;
	
	@Override public String toString() {
		
		return "Camera";
		
	}
	
	public OpenGLCameraChart(int x1, int y1, int x2, int y2) {
		
		super(x1, y1, x2, y2);
		
		cameraWidget = new WidgetCamera(cameraName -> {
			for(ConnectionCamera c : ConnectionsController.cameraConnections)
				if(c.name.equals(cameraName))
					camera = c;
		});
		
		mirrorXwidget = new WidgetCheckbox("Mirror X-Axis \u2194",
		                                   false,
		                                   mirror -> mirrorX = mirror);
		
		mirrorYwidget = new WidgetCheckbox("Mirror Y-Axis \u2195",
		                                   false,
		                                   mirror -> mirrorY = mirror);
		
		rotatewidget = new WidgetCheckbox("Rotate Clockwise \u21B7",
		                                   false,
		                                   rotate -> rotateClockwise = rotate);
		
		labelWidget = new WidgetCheckbox("Show Label",
		                                 true,
		                                 newShowLabel -> showLabel = newShowLabel);
		
		widgets = new Widget[5];
		widgets[0] = cameraWidget;
		widgets[1] = mirrorXwidget;
		widgets[2] = mirrorYwidget;
		widgets[3] = rotatewidget;
		widgets[4] = labelWidget;
		
	}

	@Override public EventHandler drawChart(GL2ES3 gl, float[] chartMatrix, int width, int height, long nowTimestamp, int lastSampleNumber, double zoomLevel, int mouseX, int mouseY) {

		// get the image
		ConnectionCamera.GLframe f = null;
		if(camera == null)
			f = new ConnectionCamera.GLframe(null, true, 1, 1, "[select a camera]", 0);
		else if(OpenGLChartsView.instance.isLiveView() && !ConnectionsController.importing)
			f = camera.getLiveImage();
		else {
			long lastTimestamp = OpenGLChartsView.instance.isLiveView() ? ConnectionsController.getLastTimestamp() : OpenGLChartsView.instance.pausedTimestamp;
			f = camera.getImageAtOrBeforeTimestamp(lastTimestamp);
		}
		
		// calculate x and y positions of everything
		xDisplayLeft = Theme.tilePadding;
		xDisplayRight = width - Theme.tilePadding;
		displayWidth = xDisplayRight - xDisplayLeft;
		yDisplayBottom = Theme.tilePadding;
		yDisplayTop = height - Theme.tilePadding;
		displayHeight = yDisplayTop - yDisplayBottom;

		if(showLabel) {
			labelWidth = OpenGL.largeTextWidth(gl, f.label);
			yLabelBaseline = Theme.tilePadding;
			yLabelTop = yLabelBaseline + OpenGL.largeTextHeight;
			xLabelLeft = (width / 2f) - (labelWidth / 2f);
			xLabelRight = xLabelLeft + labelWidth;
		
			yDisplayBottom = yLabelTop + Theme.tickTextPadding + Theme.tilePadding;
			displayHeight = yDisplayTop - yDisplayBottom;
		}
		
		// maintain the image aspect ratio, so it doesn't stretch
		float desiredAspectRatio = rotateClockwise ? (float) f.height / (float) f.width : (float) f.width / (float) f.height;
		float currentAspectRatio = displayWidth / displayHeight;
		if(currentAspectRatio != desiredAspectRatio) {
			if(desiredAspectRatio > currentAspectRatio) {
				// need to make image shorter
				float desiredHeight = displayWidth / desiredAspectRatio;
				float delta = displayHeight - desiredHeight;
				yDisplayTop    -= delta / 2;
				yDisplayBottom += delta / 2;
				displayHeight = yDisplayTop - yDisplayBottom;
			} else {
				// need to make image narrower
				float desiredWidth = displayHeight * desiredAspectRatio;
				float delta = displayWidth - desiredWidth;
				xDisplayLeft  += delta / 2;
				xDisplayRight -= delta / 2;
				displayWidth = xDisplayRight - xDisplayLeft;
			}
		}
		
		// draw the image
		if(texHandle == null) {
			texHandle = new int[1];
			OpenGL.createTexture(gl, texHandle, f.width, f.height, f.isBgr ? GL3.GL_BGR : GL3.GL_RGB, GL3.GL_UNSIGNED_BYTE, true);
			OpenGL.writeTexture (gl, texHandle, f.width, f.height, f.isBgr ? GL3.GL_BGR : GL3.GL_RGB, GL3.GL_UNSIGNED_BYTE, f.buffer);
			previousFrameTimestamp = f.timestamp;
		} else if(f.timestamp != previousFrameTimestamp) {
			// only replace the texture if a new image is available
			OpenGL.writeTexture(gl, texHandle, f.width, f.height, f.isBgr ? GL3.GL_BGR : GL3.GL_RGB, GL3.GL_UNSIGNED_BYTE, f.buffer);
			previousFrameTimestamp = f.timestamp;
		}
		
		     if(!mirrorX && !mirrorY) OpenGL.drawTexturedBox(gl, texHandle, false, xDisplayLeft,  yDisplayTop,     displayWidth, -displayHeight, 0, rotateClockwise);
		else if( mirrorX && !mirrorY) OpenGL.drawTexturedBox(gl, texHandle, false, xDisplayRight, yDisplayTop,    -displayWidth, -displayHeight, 0, rotateClockwise);
		else if(!mirrorX &&  mirrorY) OpenGL.drawTexturedBox(gl, texHandle, false, xDisplayLeft,  yDisplayBottom,  displayWidth,  displayHeight, 0, rotateClockwise);
		else if( mirrorX &&  mirrorY) OpenGL.drawTexturedBox(gl, texHandle, false, xDisplayRight, yDisplayBottom, -displayWidth,  displayHeight, 0, rotateClockwise);
		
		// draw the label, on top of a background quad, if there is room
		if(showLabel && labelWidth < width - Theme.tilePadding * 2) {
			OpenGL.drawQuad2D(gl, Theme.tileShadowColor, xLabelLeft - Theme.tickTextPadding, yLabelBaseline - Theme.tickTextPadding, xLabelRight + Theme.tickTextPadding, yLabelTop + Theme.tickTextPadding);
			OpenGL.drawLargeText(gl, f.label, (int) xLabelLeft, (int) yLabelBaseline, 0);
		}
		
		return null;
		
	}
	
	@Override public void disposeGpu(GL2ES3 gl) {
		
		super.disposeGpu(gl);
		if(texHandle != null)
			gl.glDeleteTextures(1, texHandle, 0);
		texHandle = null;
		
	}

}
