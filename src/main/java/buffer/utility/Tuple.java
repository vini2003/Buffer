package buffer.utility;

/**
 * Utility class for Tuples.
 *
 * @param <X>
 * @param <Y>
 */
public class Tuple<X, Y> {
	public X first;
	public Y second;

	/**
	 * Default constructor, which initializes both variables.
	 *
	 * @param x
	 * @param y
	 */
	public Tuple(X x, Y y) {
		this.first = x;
		this.second = y;
	}
}