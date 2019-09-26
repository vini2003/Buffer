package buffer.utility;

public class Tuple<X, Y> {
    public X first;
    public Y second;
    
    public Tuple(X x, Y y) {
        this.first = x;
        this.second = y;
    }
}