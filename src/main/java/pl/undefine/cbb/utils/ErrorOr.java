package pl.undefine.cbb.utils;

public class ErrorOr<T>
{
    Error error;
    T value;

    public ErrorOr(Error error)
    {
        this.error = error;
        this.value = null;
    }

    public ErrorOr(T value)
    {
        this.error = null;
        this.value = value;
    }

    public boolean is_error()
    {
        return error != null;
    }

    public T get_value()
    {
        assert !is_error();
        return value;
    }

    public Error get_error()
    {
        assert is_error();
        return error;
    }

    public <T> ErrorOr<T> rethrow()
    {
        assert is_error();
        return new ErrorOr<>(error);
    }
}
