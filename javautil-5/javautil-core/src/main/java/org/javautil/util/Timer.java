package org.javautil.util;

@Deprecated
public class Timer {

	private long startTime = System.nanoTime();
	public Timer() {
		throw new UnsupportedOperationException("use org.javautil.misc.Timer");
	}
	public double getElapsedMillis() {
		long now = System.nanoTime();
		double elapsed = (now - startTime) / 1e6;
		return elapsed;
	}

	public void reset() {
		startTime = System.nanoTime();
	}

}
