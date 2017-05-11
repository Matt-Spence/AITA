class Either<X, Y>
{
	private final X left;
	private final Y right;

	public Either(X l, Y r)
	{
		left = l;
		right = r;
	}

	public X getLeft()
	{
		return left;
	}

	public Y getRight()
	{
		return right;
	}
}
