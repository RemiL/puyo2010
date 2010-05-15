package graphique;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;

public class ZoneDeJeu extends GLCanvas implements GLEventListener
{
	private static final long serialVersionUID = 2421808737959876690L;
	
	public ZoneDeJeu(GLCapabilities capabilities, int width, int height)
	{
		super(capabilities);
		setSize(width, height);
		addGLEventListener(this);
	}

	@Override
	public void display(GLAutoDrawable arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void init(GLAutoDrawable arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4)
	{
		// TODO Auto-generated method stub

	}
}
