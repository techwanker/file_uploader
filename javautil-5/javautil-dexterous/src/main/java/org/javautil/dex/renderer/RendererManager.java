package org.javautil.dex.renderer;

import java.util.ArrayList;

import org.javautil.dex.dexterous.DexterousState;
import org.javautil.dex.renderer.interfaces.Renderer;
import org.javautil.dex.renderer.interfaces.RenderingException;

public class RendererManager {

	//private Map<String,Class<Renderer>> rendererMap = new TreeMap<String,Class<Renderer>>();
	private final ArrayList<Renderer> renderers = new ArrayList<Renderer>();

	// todo what to do if two classes with same capability are added?  take the last one?
	@SuppressWarnings("unchecked")
	public void registerRenderer(final Class<Renderer> clazz) throws InstantiationException, IllegalAccessException {
		if (clazz == null) {
			throw new IllegalArgumentException("renderer is null");
		}
		Class<Renderer> r;
		try {
			r = clazz;
		} catch
			 ( final ClassCastException cce) {
				throw new IllegalArgumentException(clazz.getName() + " does not implement Renderer");
		}
		final Renderer ri =  r.newInstance();

		renderers.add(ri);
	}


	/**
	 * todo vote for functionality
			Iterates over classes Registered with registerRenderer and creates and instance of the
			first class capable of rendering
	 * @param state
	 * @return
	 * @throws RenderingException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public Renderer getRenderer(final DexterousState state) throws RenderingException, InstantiationException, IllegalAccessException {
		if (state == null ) {
			throw new IllegalArgumentException("state is null");
		}
		Renderer r = null;
		for (final Renderer renderer : renderers) {
			if (renderer.canRender(state)) {
				r = renderer;
				break;
			}
		}
		if (r == null) {
			throw new RenderingException("cannot find a suitable renderer");
		}
		final Renderer re = r.getClass().newInstance();
		re.setState(state);
		return re;
	}
}
